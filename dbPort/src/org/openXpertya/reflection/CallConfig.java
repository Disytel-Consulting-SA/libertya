package org.openXpertya.reflection;

import java.util.Properties;

/**
 * Clase con datos utilizables. 
 * Esta clase contiene la configuración de una llamada
 * 
 * @author Matías Cap
 *
 */

public abstract class CallConfig {

	// Variables de instancia	
	
	/**
	 * Clave de búsqueda del timer configurado para esta llamada en la tabla
	 * AD_ReflectionTimer. En el caso que sea null, se debe ingresar el timeout
	 * manualmente en la variable timeout. Por defecto esta variable se
	 * inicializa en 0 ,lo que significa que no se crea timer, el 0 significa
	 * sin timer.
	 */
	private String timerValue;
	/**
	 * Id de la tabla AD_ReflectionTimer relacionado con esta llamada 
	 */
	private Integer AD_ReflectionTimer_ID;
	/**
	 * Timeout de espera en ms de la llamada bloqueante, cuando pasa este tiempo
	 * definido se corta la llamada
	 */
	private long timeout;
	/** Contexto */
	private Properties ctx;
	/** Nombre de transacción local */
	private String trxName;
	
	// Constructores
	
	public CallConfig(){
		setTimeout(0);
	}
	
	/**
	 * Crea una llamada para la configuración en particular
	 * @return llamada específica para esta configuración
	 */
	public abstract Call createCall();
	
	// Getters y Setters
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTimerValue(String timerValue) {
		this.timerValue = timerValue;
	}

	public String getTimerValue() {
		return timerValue;
	}

	public void setAD_ReflectionTimer_ID(Integer aD_ReflectionTimer_ID) {
		AD_ReflectionTimer_ID = aD_ReflectionTimer_ID;
	}

	public Integer getAD_ReflectionTimer_ID() {
		return AD_ReflectionTimer_ID;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}
	
}
