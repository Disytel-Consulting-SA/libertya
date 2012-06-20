package org.openXpertya.reflection;

import java.util.Timer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Esta clase es la encargada de gestionar la llamada al método en un thread
 * diferente y el timeout de la misma. Existen métodos con la metodología
 * monitores para sincronizar el evento de resultado de la llamada con el evento
 * timeout.
 * 
 * @author Matías Cap
 * 
 */

public class ClientCall {

	// Variables de instancia
	
	/** Configuración de llamada */
	
	private CallConfig config;
	
	/** Timer para el timeout de la llamada del cliente */
	
	private Timer timer;

	/** Lock para proteger la condicion de concurrencia */
	
	private ReentrantLock lock;
	
	/**
	 * Condicion por la cual espera por dos tipos de respuesta: Timeout o
	 * resultado de llamada del método
	 */
	
	private Condition response;
	
	/** Resultado de la operación de llamada al método */
	
	private CallResult result;
	
	/** Llamada al método */
	
	private Call call;
	
	/** Log */
	
	protected CLogger log = CLogger.getCLogger("ClientCall"); 
	
	// Constructores
	
	public ClientCall(CallConfig config){
		setConfig(config);
		initConcurrentComponents();
	}
	
	/**
	 * Inicializa las variables a utilizar para la concurrencia
	 */
	private void initConcurrentComponents(){
		// Instanciar el lock
		lock = new ReentrantLock(true);
		// Instanciar la condicion
		response = lock.newCondition();
		// Crear la llamada
		setCall(getConfig().createCall());
		getCall().setClient(this);
		// Obtener el timer en base a la clave de búsqueda o clave del timer
		// configurado para esta llamada.
		timerManagememt();
	}

	/**
	 * Creo el timer. El orden de prioridad de timeout a elegir es:
	 * <ol>
	 * <li>El valor del timer a partir del AD_ReflectionTimer_ID de la config.</li>
	 * <li>El valor del timer a partir del value de la config.</li>
	 * <li>El valor de la variable timeout de la config.</li>
	 * </ol>
	 * Si ninguno de los primeros presenta valores, significa que está
	 * configurado en la varible timeout que por defecto se inicializa en 0 y
	 * significa que no debemos crear un timer para esta llamada.
	 */
	private void timerManagememt(){
		// 2 
		findTimer(getConfig().getTimerValue());
		// 1
		findTimer(getConfig().getAD_ReflectionTimer_ID());
		// Seteo el timer, si el timeout es 0, no debe haber timer
		setTimer(getConfig().getTimeout() != 0 ? new Timer():null);
	}

	/**
	 * Obtener el timer para el value parámetro y setear el timeout de la
	 * config. Si el parámetro value es null o vacío entonces no se hace nada.
	 * 
	 * @param value value del registro de AD_ReplicationTimer 
	 */
	protected void findTimer(String value){
		// Si es null o vacío retorno
		if(Util.isEmpty(value)){
			return;
		}
		// Obtengo el timeout en segundos del timer correspondiente
		Integer timeoutSecs = DB
				.getSQLValue(
						getConfig().getTrxName(),
						"SELECT timeout FROM ad_reflectiontimer WHERE (ad_client_id = ?) AND (upper(trim(value)) = upper(trim(?))) AND (isactive = 'Y')",
						Env.getAD_Client_ID(getConfig().getCtx()), value);
		// Seteo el timeout de la config
		setConfigTimeout(timeoutSecs);
	}

	/**
	 * Obtengo el timer a partir del id del registro de AD_ReplicationTimer
	 * parámetro. Si el parámetro es null o 0, entonces no se hace nada.
	 * 
	 * @param AD_ReplicationTimer_ID
	 *            id del registro correspondiente de la tabla
	 *            AD_ReplicationTimer
	 */
	protected void findTimer(Integer AD_ReplicationTimer_ID){
		// Si es null o vacío retorno
		if(Util.isEmpty(AD_ReplicationTimer_ID,true)){
			return;
		}
		// Obtengo el timeout en segundos del timer correspondiente
		Integer timeoutSecs = DB
				.getSQLValue(
						getConfig().getTrxName(),
						"SELECT timeout FROM ad_reflectiontimer WHERE (ad_reflectiontimer_id = ?) AND (isactive = 'Y')",
						AD_ReplicationTimer_ID);
		// Seteo el timeout de la config
		setConfigTimeout(timeoutSecs);
	}

	/**
	 * Seteo el timeout en milisegundos de la config a partir de los segundos
	 * parámetro
	 * 
	 * @param timeoutInSecs
	 *            timeout en segundos
	 */
	public void setConfigTimeout(Integer timeoutInSecs){
		// Si no es null el timeout en sgs parámetro, entonces seteo y
		// multiplico por 1000 para que sean milisegundos
		if(!Util.isEmpty(timeoutInSecs,false)){
			getConfig().setTimeout(timeoutInSecs*1000);
		}
	}
	
	/**
	 * Realización de llamada al método. Inicia el timer y realiza la llamada al
	 * método en un thread para poder cancelarlo cuando ocurra un timeout.
	 * 
	 * @return resultado de la operación, error u resultado del método
	 */
	public CallResult call(){
		lock.lock();
		// Si hay timer entonces lo inicio
		if(getTimer() != null){
			// Instancio el timer task
			CallTimeoutTask task = new CallTimeoutTask(this);
			// Arrancar el timer
			getTimer().schedule(task, getConfig().getTimeout());
		}
		// Realizo la llamada al método, ejecutando el thread
		getCall().start();
		// Quedo en espera de aviso
		try{
			response.await();
			setTimer(null);
		} catch(InterruptedException ie){
			log.severe(Msg.getMsg(getConfig().getCtx(), "InterruptedClass"));
		} finally{
			lock.unlock();	
		}
		return getResult();
	}

	/**
	 * Método de retorno de timeout del timer. Cancela el thread de llamada al
	 * método y desbloquea la condicion para que se siga ejecutando.
	 * 
	 * @param result
	 *            resutado de la operación, TIMEOUT
	 */
	public void timeout(CallResult result){
		lock.lock();
		// Si hay alguien es espera de la condicion realizo las acciones
		// correspondientes
		if(lock.hasWaiters(response)){
			// Me guardo el resultado
			setResult(result);
			// Interrumpo el thread de la llamada
			getCall().interrupt();
			// Desbloqueo la condicion
			response.signal();
		}
		lock.unlock();
	}

	/**
	 * Método de retorno de la llamada al método. Cancela el timer y desbloquea
	 * la condicion para que se siga ejecutando.
	 * 
	 * @param result
	 *            si la ejecución tuvo error, entonces se retorna el mensaje de
	 *            error. Caso contrario si no hubo error, dentro del resultado
	 *            se encuentra el objeto que retorna el método invocado
	 */
	public void callReturn(CallResult result){
		lock.lock();
		// Si hay alguien es espera de la condicion realizo las acciones
		// correspondientes
		if(lock.hasWaiters(response)){
			// Me guardo el resultado
			setResult(result);
			// Interrumpo el thread de la llamada
			if(getTimer() != null){
				getTimer().cancel();
			}
			// Desbloqueo la condicion
			response.signal();
		}
		lock.unlock();
	}
	
	// Getters y Setters

	public void setConfig(CallConfig config) {
		this.config = config;
	}

	public CallConfig getConfig() {
		return config;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setResult(CallResult result) {
		this.result = result;
	}

	public CallResult getResult() {
		return result;
	}

	public void setCall(Call call) {
		this.call = call;
	}

	public Call getCall() {
		return call;
	}
	
}
