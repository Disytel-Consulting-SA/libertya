package org.openXpertya.print.fiscal.comm;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.msg.MsgRepository;
import org.openXpertya.print.fiscal.util.ArrayUtils;
import org.openXpertya.util.CLogger;

/**
 * Interfaz de comunicación mediante un Spooler conectado por TCP.
 * <br>
 * NOTAS Modificaciones Ader Javier: <br>
 * <ul>
 * <li>Version original 10.03: Se tenia 5 timeous de 200 mls para considerar que la respuesta
 * del spooler habia terminado. Esto da un overhead de 1 segundo por comando enviado independientemente
 * del cuanto tarde realmente la ejecución comando y la recepción de la respuesta desde el Spooler. En
 * general esa espera es innecesaria, ya que la respuesta (o un DC2) llega rapidamente.
 * <br>
 * <li>Optimización I (nov 2010): Se modifico el bucle de espera para que en caso de baber recibido una secuencia
 * de bytes de longitud minima 9 (todas las respuestas del Spooler, salvo DC2, y DC4 tiene al menos
 * 9 bytes) se pase a una espera de 2 timeouts de 200mls (ver variable readTimeOutCountAfterFirstRealByte).
 * Con esto se logra una mejora de 600mls, sin sacrificar visiblemente la correctitud: es muy poco probable, pero puede
 * darse el caso de la respuesta completa llegue en dos partes, y la segunda a mas de 400mls que la primera
 * y por lo tanto se "corte antes". Es poco probable las respuestas son pequeñas (menos de 512 bytes siempre;
 * en genral menores a 30 bytes) y el spooler hasar "flushea" el buffer de escritura cada vez
 * que emite una respuesta; bajo estas condiciones,para que la respuesta llegue en partes, 
 * la capa TCP o IP deberia fragmentar la respuesta y ninguna capa de TPC/IP con minimo sentido común 
 * hace esta fragmentacion. 
 * Basicamente, una vez que llega el primer byte de un respuesta (sin contar a DC2) es porque 
 * llego completa desde la red, el proximo timeout de lectura casi necesariamente marca el final 
 * real de la respuesta (de todas maneras se espera que esto ocurra 2 veces).
 * <br> De cualquier manera, esta optimizacion estuvo ampliamente testeado, incluso bajo producción
 * durante varios meses.
 * <br>
 * Junto con esta optimización se ha hecho otra modificación que hace que la implementacion actual 
 * sea incluso más conservativa que la anterior: por el primer byte de un respuesta real (no DC2) 
 * se espera  dos rondas de 5 timeouts de 200mls (esto es, 2 segundos, frente a 1 segundo en la version 
 * anterior). Esto es para tener en cuenta posibles roundtrips de red largos o un red con mucha carga (ver
 * variable retriesWaitFirstByte). Esta mejora (variando el valor de la variable retriesWaitFirstByte)
 * permiría lograr accesos realmente remotos o lentos, de manera confiable. 
 * <br>
 * A manera de conclusión: siempre tratando de manera especial los casos de recepcion de DC2 
 * ("espere, impresor procesando comando") se espera de más si el primer byte tarda en arribar;
 * pero una vez que arriba se espera menos. En la práctica, el primer byte arriba rapido, y una
 * vez que llego, es porque llego la respuesta completa; esto es, salvo ciertos comandos que
 * son lentos a nivel de controlador fiscal (cierre X por ej) basicamente todos los respuestas
 * se esperan 400 mls (vs 1 segundo anteior).<br>
 * <b>NOTA Adicional </b>: ademas de esta modificación, se agrego el "flush" sobre el socket
 * despues escribir el comando; esto para evitar que Java retarde la emision real del comando
 * y que potencialmente el bucle de espera de respuesta posterior falle (simplemente, se va 
 * a esperar por el simple hecho de que la respuesta no llego al spooler, porque Java retardo
 * su emisión....). 
 * De todas maneras esta no es la solución completamente real a este escenario, porque el caching
 * de la emisión se puede dar por culpa de la stack TCP/IP del SO (hay formas de obligar
 * a que esto tampoco ocurra, y realmente asegurarse que se ha trasmitido el comando
 * a nivel de TCP, pero es un hack medio dudoso ya que afecta a todo el entorno de Java, no solo
 * a esta conexión en particular; este "evitar" de caching puede ralentar otras partes
 * del sistema, en particular el acceso a la base de datos...).
 * 
 * 
 * 
 * <br>
 * <li>Optimizacion II (feb 2011): Saleo completo de Timeouts para la respuesta de ciertos comandos.
 * <br>
 * La razón de esta optimización es que las respuestas de muchos comandos son necesariamente
 * de longitud fija EN TODOS los modelos; una vez leida esta longitud en bytes, es seguro suponer 
 * que la respuesta llego completamente. Bajo estas circunstancias, se corta el bucle de lectura 
 * incluso sin esperar por timeouts (esto es, incluso se evitan los 400mls que logra la optimizacion I).
 * <br>
 * Actualmente solo se trata el caso de PrintLineItem, el cual es el más usado en la impresión 
 * de facturas y NC(una vez por cada item de la misma). Hay otros comandos con respuesta fija,
 * que serian facilmente chequeables de la misma manera (por ej PrintFiscalText, LastItemDiscount,
 * SetCustomerData), pero bajo la versió 10.03 estos se usan poco (no son una función de la cantidad
 * de items) o directente no se usan (OBS: bajo 10.09, LastItemDiscount creo que se usa, y hay
 * pontencialmente uno por item de factura; si este es el caso, debería hacerse lo mismo
 * que con PrintLineItem; creo que ademas se utilizan documentos no fiscales, cuyas lineas
 * se manejan usando PrintNonFiscalText, el cual como los anteriores, tiene respueta fija).
 * <br>
 * Ver SpoolerManagerResponse.
 * </ul>
 * TODO: manejar respuesta DC4 (en la actualidad no es muy necesario... ningún modelo nuevo
 * muestra este aviso de "tapa abierta" simplemente porque no tienen tapa; ademas esto
 * es responsabilidad del usuario y antes esta situcaion Liberyta simplemente aborta
 * porque no puede interpretar la respuesta).
 * 
 *
 * 
 * @author Franco Bonafine
 * @date 04/02/2008
 */
public class SpoolerTCPComm extends AbstractFiscalComm {

	/** Puerto TCP donde se encuentra escuchando el Spooler */
	private int tcpPort;
	/** HOST en donde se encuentra el Spooler */
	private String host;
	/** Socket de conexión al Spooler */
	private Socket spoolerSocket;
	/** Cantidad de timeOuts a esperar para leer un byte del stream */
	private int readTimeOutCount = 5;
		
	/**
	 * @param host Host donde se encuentra el Spooler.
	 * @param tcpPort Puerto TCP donde se encuentra abierto el Spooler.
	 */
	public SpoolerTCPComm(String host, int tcpPort) {
		super();
		this.tcpPort = tcpPort;
		this.host = host;
	}

	public void connect() throws IOException {
		try {
			Socket soc = new Socket(getHost(), getTcpPort());
			soc.setSoTimeout(200);
			setSpoolerSocket(soc);
			setInputStream(soc.getInputStream());
			setOutputStream(soc.getOutputStream());
			setConnected(true);
		} catch (UnknownHostException e) {
			setConnected(false);
			throw new IOException(MsgRepository.get("UnknownHostError") + " (" + getHost() + ")");
		} catch (ConnectException e) {
			throw new ConnectException(MsgRepository.get("SpoolerConnectError") + " (Host: " + getHost() + ":" + getTcpPort() + ").");
		}
	}
	
	public void close() throws IOException {
		super.close();
		if(isConnected()) {
			getSpoolerSocket().close();
		}
	}

	public synchronized void execute(FiscalPacket request, FiscalPacket response) throws IOException {
		if(request == response) throw new IllegalArgumentException();
		if(request == null) throw new NullPointerException(MsgRepository.get("NullRequestError"));
		if(response == null) throw new NullPointerException(MsgRepository.get("NullResponseError"));
		// Se valida el estado de la conexión con el spooler.
		validateConnection();
		
		// Se obtiene la representación en bytes del comando y se escribe
		// sobre stream de salida.
		byte[] cmdBytes = request.encodeBytes();
		getOutputStream().write(cmdBytes);
		//Ader Javier: 10 oct 2010 se debe poner el flush luego de
		//escribir; si no puede que despues fallen los timeouts de lectura
		getOutputStream().flush();
	
		debug("REQ: " + request.toString());
		
		// Se obtiene la respuesta de la impresora a partir del stream de entrada
		// del socket y se decodifican los bytes para crear el paquete de respuesta.
		byte[] resBytes = new byte[512];
		resBytes = getResponse(request);
		response.decode(request.getCommandCode(), resBytes);
		
		debug("RES: " + response.toString());
	}
	//Ader Javier, feb 2011; se agrego el parametro request para que se puedan aplicar
	//optimizaciones de salteo completos de timeouts para ciertos comandos y ante ciertas
	//respuesta.
	private byte[] getResponse(FiscalPacket request) throws IOException {
		// Se obtienen los bytes a partir del stream de entrada.
		// NOTA: El WSpooler tiene una particularidad muy desagradable 
		// de realizar respuestas de comandos sin indicar el fin de la 
		// respuesta. Por lo tanto no existe ningún caracter de control 
		// para determinar el fin de una respuesta, ni tampoco es posible 
		// determinar la cantidad de bytes que componen a la misma, por 
		// lo tanto, se optó por implementar un contador de timeOuts para  
		// determinar este fin.
		byte[] rspBytes = new byte[0];
		byte b;
		// boolean end = false; SE USA while(true) y breaks
		int timeOutCount = 0;
		int DC2Counter = 0;
		boolean lastReadWasDC2 = false;
		
		// TODO: que sea parametrizable y documetado
		// si expiran todos los timeous y aun no se recibio ni un solo byte
		// se reintenta "volver a esperar" esta cantidad de veces
		int retriesWaitFirstByte = 1;
		// TODO: que sea parametrizable y documentado
		// Depues de una succion de DC2DC2DC2...DC2 puede que expiren todos los timeouts
		// en la espera del primer byte de la respueta o del primer byte de otro DC2...
		// Bajo esta circunstancia se vuelve a esperar una vez la siguiente cantidad de
		// veces
		int retriesWaitFirstRealByteAfterTimeoutInDC2s = 5; //serian 
		boolean alwaysWaitFirstRealByteAfterDC2 = false; //si esto es true SIEMPRE
		// se espera luego de haber consumido un almeno un DC2, y el contadora anterior no tiene
		// efecto 
		int readTimeOutCountAfterFirstRealByte = 2; //despues de que ya se lleyo parte
		// de la respuesta real, cuanto tiempo hay que esperar? Cuantos timeouts pueden expirar 
		// sin haber recibido nuevamente nada? (por defecto 2 de 200mls; lo cual da 400mls). 
		// Esto es para acelerar las cosas; una vez que uno recibe parte de la repuseta real (esto es NO "DC2"),
		// es muy poco probable que el timeout "corte" la respuesta. Esta optimizacion
		// solo se usa cuando la repusta actual tieen una longitud mayor o igaul a 9 (el spooler
		// retonrna en respuesta reales al menos 9 bytes (dos conjuntos de 4 bytes de status
		// y un byte seperardor)
		
		// El proceso de lectura termina cuando... documentar: acutalmente no es tan simple
		// como simplemente terminar hasta que cuando todos los timeoust expiran...
		while(true) {
			try {
				int res = getInputStream().read();
				if (res == -1) {
					// endOfStream! No necesariamene un error aunque es muy probable...
					debug("WARNING: sokcet cerrado a la espera de respuesta. Long. Resp. parcial: " +
							rspBytes.length);
					break;
				}
				b = (byte)res;
				rspBytes = ArrayUtils.append(rspBytes, b);
				timeOutCount = 0;
				// Ader Javier : 10 oct 2010 , manejo de DC2
				if (startWithDC2(rspBytes)) {   
					//DC2 : se debe consumir y setear lastReadWasDC2
					rspBytes = ArrayUtils.removePrefix(rspBytes, 3);
					DC2Counter++;
					debug("DC2 : " + DC2Counter);
					lastReadWasDC2 = true;
					continue;
				// Optimizacion II: solo se agrego este "else if"	
				} else if (isResponseCompleted(request,rspBytes)) { 
					debug("Dejando de esperar respuesta por optimización II: Salteo completo de timeots");
					break; // Se sale pero no porque por que haya ocurrido un timeout
				}
				// Si se llega a aca se tiene en rspBytes secuncioa "D", "DC", 
				// o otra secuencia de mas de un byte la cual no se puede saber por isResponseCompleted
				// que represente una respuesta completa; se tiene que volver para leer los siguientes
				// bytes o para que ImputStream.read() corte por timeout de 200 mls (si esto
				// ultimo ocurre la logica sigue en el catch)
				lastReadWasDC2 = false;
				continue;
				
			} catch(SocketTimeoutException e) {
				
				timeOutCount++;
				// No se recibio ni un solo byte de la respueta, ni siquiera un DC2 
				if (rspBytes.length == 0 && !lastReadWasDC2) {
					// la unica forma de que esto pase es que se haya enviado la solicitud
					// y no se haya respondido ni un solo byte
					if (timeOutCount < getReadTimeOutCount())
						continue; //caso comun a la espera del primer byte
					// else; expiraron todos los timeouts y no se recibio ni un solo byte!
					if (retriesWaitFirstByte >0) {
						// se hace todo una ronda nueva
						retriesWaitFirstByte--;
						debug("Reitentando al espera del primer byte");
						timeOutCount =0;
						continue;
					} else {
						// NECESARIAMENTE UN ERROR!
						debug("ERROR: expiraron todos los timeouts y no se recibio ni un solo byte de respeusta");
						break; //se sale y rspBytes necesariamente vacio....
					}
				} else if (rspBytes.length == 0 ) {
					// En este punto lastReadWasDC2 = true; esto es se han leido
					// una sucesión de DC2DC2DC2...DC2 y se expiro el timeout
					if (timeOutCount < getReadTimeOutCount()) {
						continue; // Se sigue esperando
					}
					// Si se llega aca todos los timeouts expiraron pero lo ulmito
					// que se recibio fue un DC2; conceptualmente uno debería esperar
					// idefinidamente bajo esta situacion; igual por las dudas
					// se pone esta condicion 
					if (retriesWaitFirstRealByteAfterTimeoutInDC2s > 0 
							|| alwaysWaitFirstRealByteAfterDC2) {
				
						retriesWaitFirstRealByteAfterTimeoutInDC2s--;
						debug("WARNING: volviendo a esperar primer byte real de repuesta luego de DC2's");
						timeOutCount =0;
						continue;
					}
					// Si se llega aca: se recibieron muchos DC2's pero se espero
					// mas retriesWaitFirstRealByteAfterTimeoutInDC2s tandas de timeouts...
					debug("ERROR: cancelando espera de repuesta en DC2; demasiada espera");
					break;
				}
				
				// OK Si se llega a ca rspBytes.length > 0; y casi necesariamete contiene
				// toda la repuesta. Es poco probable que "se corte la repuesta" si 
				// se hace en este punto una optimizacion....
				if (rspBytes.length >= 9 
						&& timeOutCount >= readTimeOutCountAfterFirstRealByte) {
					// OTPIMIZACION I en tiempo de procesamiento. En realidad este es el caso
					// común. ESTE ES EL PUNTO DE SALIDA MAS COMUN para todos los comandos
					// no manejados por la Optimizacion II
					debug("Dejando de esperar respueta por optimización");
					break;
				}
				
				if (timeOutCount >= getReadTimeOutCount()) {
					debug("WARNING: Dejando de esperar repuesta antes de recibir al menos 9 bytes");
					break;
				} // else {continue}
				
				// end = timeOutCount == getReadTimeOutCount();
				debug("Timeout! = " + timeOutCount + " partial. length:" + rspBytes.length +
						" CT(mls):" + System.currentTimeMillis());
			}

		}
		
		// Este metodo no tenia sentido y hacia una conversion 
		// a string muy dudusa. Los bytes DC2 se eleminan en el bucle de lectura; SIEMPRE (ver 
		// codido del Spooler Hasar) en sucesión antes de que la respuesta.
		// rspBytes = cleanResponseBytes(rspBytes);
		return rspBytes;
	}
	
	private static final byte byteD = 0x44;
	private static final byte byteC = 0x43;
	private static final byte byte2 = 0x32;
	private static final byte byte4 = 0x34; //NO usado actulemtne; serviria para chequear respuesta "DC4" (cajon abierto)
	
	// Ader Javier: 10 Oct 2010 : esta es la forma correcta de 
	// chequear los bytes DC2
	private boolean startWithDC2(byte[] res)
	{
		if (res.length < 3)
			return false;
		if (res[0]== byteD && res[1] == byteC && res[2]==byte2)
			return true;
		return false;
	}
	
	
	private boolean isResponseCompleted(FiscalPacket request, byte[] rspBytes)
	{
		// se delega a SpoolerManagerResponse
		return 	SpoolerManagerResponse.getDefInst().isResponseCompleted(request, rspBytes);
		//return false; descomentar esta linea, y comaentar la anterior para comparar
		//la performance con y sin Optimizacion II.
	}
	
	
	//cambiar acordemente; este es solo para debugguear
	protected static boolean debugInStdOut = false;
	protected static boolean debugInLogger = true;
	protected static CLogger log = CLogger.getCLogger(SpoolerTCPComm.class);
	
	private static void debug(String text) {
		String textDebug = "==> DEBUG = " + text;
		if (debugInStdOut)
			System.out.println(textDebug);
		if (debugInLogger)
			log.log(Level.INFO,textDebug);
	}
	
	
	/**
	 * @return Returns the host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host The host to set.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return Returns the tcpPort.
	 */
	public int getTcpPort() {
		return tcpPort;
	}

	/**
	 * @param tcpPort The tcpPort to set.
	 */
	public void setTcpPort(int tcpPort) {
		this.tcpPort = tcpPort;
	}

	/**
	 * @return Returns the spoolerSocket.
	 */
	protected Socket getSpoolerSocket() {
		return spoolerSocket;
	}

	/**
	 * @param spoolerSocket The spoolerSocket to set.
	 */
	protected void setSpoolerSocket(Socket spoolerSocket) {
		this.spoolerSocket = spoolerSocket;
	}
	



	/**
	 * @return Returns the readTimeOutCount.
	 */
	public int getReadTimeOutCount() {
		return readTimeOutCount;
	}

	/**
	 * @param readTimeOutCount The readTimeOutCount to set.
	 */
	public void setReadTimeOutCount(int readTimeOutCount) {
		this.readTimeOutCount = readTimeOutCount;
	}
}
