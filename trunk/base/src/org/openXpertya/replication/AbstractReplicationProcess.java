package org.openXpertya.replication;

import java.util.HashMap;
import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;

import org.openXpertya.model.MProcess;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_AD_ReplicationError;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

public abstract class AbstractReplicationProcess extends SvrProcess {

	/** Constantes internas */
	private final String JMS_CONSUMER_TYPE = "C";
	private final String JMS_PRODUCER_TYPE = "P";

	/** ID de esta organización */
	int thisOrgID = -1; 
	/** Posicion de esta organización en el array de organización */
	int thisOrgPos = -1; 
	/** ID de la compañía configurada para replicación */
	int thisInstanceClient = -1;
	
	/** Miembros JMS */
	private Session producerSession = null;
	private Connection producerConnection = null;
	private MessageProducer producer = null;
	private Session consumerSession = null;
	private Connection consumerConnection = null;	
	private MessageConsumer consumer = null;
	
	/** Transaccion interna para replicacion */
	protected String rep_trxName = null; 
	
	/** Log de replicacion */
	protected static CLogger log = CLogger.getCLogger(AbstractReplicationProcess.class);
	
	/** Discriminacion entre proceso origen y destino */
	public final static String SOURCE_PROCESS = "[SOURCE] ";
	public final static String TARGET_PROCESS = "[TARGET] ";
	
	/**
	 * Redefinicion del trxName.  No debe utilizarse transacciones (excepto partes especiales en donde 
	 * se utiliza rep_trxName), dado que las mismas pueden llegar a bloquear por completo la base de datos,
	 * lo cual es contraproducente para la performance de los puntos de venta (TPV).
	 */
	protected String get_TrxName() {
		return null;
	}
	
	@Override
	protected void prepare() {
		saveLog(Level.INFO, false, "Iniciando proceso", null);
		/* Setear el ID y Pos de esta organizacion (ignora la Org del login, utiliza la conf. de thisHost), asi como su cia. */
		thisOrgID  = DB.getSQLValue(get_TrxName(), " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
		thisOrgPos = MReplicationHost.getReplicationPositionForOrg(thisOrgID, get_TrxName());
		thisInstanceClient = DB.getSQLValue(get_TrxName(), " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y'" );
		/* Setear variables del contexto, para los posibles casos en que las mismas sean utilizdas */
        Env.setContext(Env.getCtx(), "#AD_Client_ID", thisInstanceClient);
        Env.setContext(Env.getCtx(), "#AD_Org_ID", thisOrgID);
		/* Redefinicion de valor por defecto.  Las estructuras de tablas, columnas, indices, etc. no cambian en replicacion */
		ReplicationCache.shouldReloadCache = false;
		processParameters();
	}
	
	protected void processParameters()
	{
		// tiempo para time out (1, 2, 10, 100, etc). 
		Integer timeOutNro = null;
		// tiempo para time out (minute, hour, day).
		String timeOutType = null;
		ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
	        String name = para[ i ].getParameterName();
	        if( para[ i ].getParameter() == null ) ;
	        // Source: Cantidad de timeout
	        else if( name.equalsIgnoreCase( "TimeOutNro" ))
	        	timeOutNro = para[ i ].getParameterAsInt();
	        // Source: Unidad de timeout	        
	        else if( name.equalsIgnoreCase( "TimeOutType" ))
	            timeOutType = (String)para[ i ].getParameter();
	        // Source: Numero de registros a actualizar por lote su repArray al enviar (cambiar a waiting ack). La modificacion invalida cache del gestor de tablas.
	        else if( name.equalsIgnoreCase( "SourceAckQueryesPerGroup" ))
	        {
	        	int tempo = para[ i ].getParameterAsInt();
	        	if (tempo != ReplicationConstants.REPLICATION_SOURCE_QUERIES_PER_GROUP) {
	        		ReplicationConstants.REPLICATION_SOURCE_QUERIES_PER_GROUP = tempo;
	        		ReplicationTableManager.invalidateCache();
	        	}
	        }
	        // Target: Numero de mensajes por transaccion
	        else if( name.equalsIgnoreCase( "TargetMessagesPerTrx" ))
	        	ReplicationConstants.REPLICATION_TARGET_MESSAGES_PER_TRX = para[ i ].getParameterAsInt();
	        // Source: Numero maximo de registros a enviar. Modificacion NO invalida cache del gestor de tablas	        
	        else if( name.equalsIgnoreCase( "SourceMaxRecords" ))
	        	ReplicationConstants.REPLICATION_SOURCE_MAX_RECORDS = para[ i ].getParameterAsInt();
	        // Si el check LimitRecords esta desactivado, entonces setear a 0 el parametro TargetMaxRecords
	        else if( name.equalsIgnoreCase( "LimitRecords" ))
	        {
	        	if ("N".equals((String)para[ i ].getParameter()))
	        		ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS = 0;
	        }
	        // Target: Numero maximo de registros a procesar	        
	        else if( name.equalsIgnoreCase( "TargetMaxRecords" ))
	        	ReplicationConstants.REPLICATION_TARGET_MAX_RECORDS = para[ i ].getParameterAsInt();
	        // Target: Replicar solo desde un host
	        else if( name.equalsIgnoreCase( "ReplicateFromHost" ))
	        	ReplicationConstants.REPLICATION_TARGET_REPLICATE_FROM_HOST = para[ i ].getParameterAsInt();
	        // Source: Reenvio completo de registros dentro de un periodo dado. La modificacion invalida cache del gestor de tablas
	        else if( name.equalsIgnoreCase( "ResendAllRecords" ))
	        {
	        	boolean tempo = "Y".equals((String)para[ i ].getParameter());
	        	if (tempo != ReplicationConstants.RESEND_ALL_RECORDS) {
	        		ReplicationConstants.RESEND_ALL_RECORDS = tempo;
	        		ReplicationTableManager.invalidateCache();
	        	}
	        }
        }
            
        // Setear valor de TimeOut. Conjuncion entre ambos datos.  La modificacion invalida la cache del gestor de tablas
        if (timeOutNro != null && timeOutNro != 0 && timeOutType != null && timeOutType.length() > 0)
        {
        	String tempo = "" + timeOutNro + " " + timeOutType;
        	if (!tempo.equals(ReplicationConstants.ACK_TIME_OUT)) {
        		ReplicationConstants.ACK_TIME_OUT = tempo;
        		ReplicationTableManager.invalidateCache();
        	}
        }
	}

	
	
	/**
	 * Metodo que inicia la conexion JMS de tipo productor.
	 * @param queueName es el nombre de la cola
	 * @param orgID es el AD_Org_ID de la organizacion a la cual se desea conectar
	 * @throws Exception en caso de error
	 */
	protected void startJMSProducer(String queueName, int orgID) throws Exception
	{
		startJMS(JMS_PRODUCER_TYPE, queueName, orgID, null);
	}

	/**
	 * Metodo que inicia la conexion JMS de tipo consumidor
	 * @param queueName es el nombre de la cola
	 * @param orgID es el AD_Org_ID de la organizacion a la cual se desea conectar 
	 * @param selector filtro de mensajes (null si no desea filtrarse mensajes)
	 * @throws Exception en caso de error
	 */
	protected void startJMSConsumer(String queueName, int orgID, String selector) throws Exception
	{
		startJMS(JMS_CONSUMER_TYPE, queueName, orgID, selector);
	}
	
	/**
	 * Realiza la configuración de conexión (productor o consumidor) 
	 * en función de los parametros recibidos 
	 * @param type CONSUMER o PRODUCER
	 * @param queueName es el nombre de la cola
	 * @param orgID es el AD_Org_ID de la organizacion a la cual se desea conectar 
	 * @param selector filtro de mensajes (null si no desea filtrarse mensajes)
	 * @throws Exception en caso de error
	 */
	private void startJMS(String type, String queueName, int orgID, String consumerSelector) throws Exception
	{
		// Configuración general (conexión, sesión, etc.)
		ReplicationConnection aconn = new ReplicationConnection(orgID, get_TrxName());
        ConnectionFactory connectionFactory = aconn.getConnectionFactory();
        Context jndiContext = aconn.getContext();
        Destination dest = (Destination) jndiContext.lookup(queueName);
        
        // Si debe configurar el productor...
        if (JMS_PRODUCER_TYPE.equals(type))
        {
        	producerConnection = connectionFactory.createConnection();
        	producerSession = producerConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        	producer = producerSession.createProducer(dest);
        }

        // Si debe configurar el consumidor...        
        if (JMS_CONSUMER_TYPE.equals(type))
        {
        	consumerConnection = connectionFactory.createConnection();
        	consumerSession = consumerConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        	consumer = consumerSession.createConsumer(dest, consumerSelector);
        	// Recuperar mensajes no "acknowledgeados"
        	consumerSession.recover();
        }
	}
	
	
	/**
	 * Almacena el error correspondiente segun sea el caso de invocación
	 * @param aLevel nivel de log (o null si no desea imprimir en consola)
	 * @param persistError true si desea persistir el error en la tabla de log
	 * @param logMessage mensaje a presentar
	 * @param targetOrgPosOrID posicion de la Org en el repArray (si es < a 1000000) , o bien su ID (si es > a 1000000).
	 */
	protected void saveLog(Level aLevel, boolean persistError, String logMessage, Integer targetOrgPosOrID)
	{
		// Los AD_Org_IDs superan el valor 1000000, mientras que las posiciones en el repArray inician en 1
		Integer targetOrgID = targetOrgPosOrID == null || targetOrgPosOrID >= 1000000 ? 
								targetOrgPosOrID : ReplicationCache.map_RepArrayPos_OrgID_inv.get(targetOrgPosOrID);
		
		// mostrar en consola (si a Level no es null)
		if (aLevel != null)
			log.log(aLevel, getProcessName() + logMessage);
		
		// persistir en log de BBDD
		if (persistError)
		{
			// Instanciar entrada en la tabla de log
			X_AD_ReplicationError aLog = new X_AD_ReplicationError(getCtx(), 0, get_TrxName());
			aLog.setORG_Target_ID(targetOrgID == null ? 0 : targetOrgID);
			aLog.setReplication_Error(Env.getDateTime("yyyy/MM/dd-HH:mm:ss.SSS") + " - " + getProcessName() + logMessage);
			aLog.setClientOrg(getAD_Client_ID(), thisOrgID);
			// Estos campos se setean por retro compatibilidad con versiones anteriores
			aLog.setReplication_Type(X_AD_ReplicationError.REPLICATION_TYPE_Asynchronous);
			aLog.setInitialChangelog_ID(-1);
			aLog.setFinalChangelog_ID(-1);
			// Persistir en BBDD
			aLog.save();
		}
	}


	public Session getProducerSession() {
		return producerSession;
	}


	public Connection getProducerConnection() {
		return producerConnection;
	}


	public MessageProducer getProducer() {
		return producer;
	}


	public Session getConsumerSession() {
		return consumerSession;
	}


	public Connection getConsumerConnection() {
		return consumerConnection;
	}


	public MessageConsumer getConsumer() {
		return consumer;
	}

	protected int getAD_Client_ID() {
		return thisInstanceClient;
	}
	
	/**
	 * Cierra la conexión de tipo consumidor
	 */
	protected void closeConsumerConnection()
	{
		if (getConsumerConnection() != null)
			try
			{	
				getConsumerConnection().close();
			}
			catch (Exception e) {}
	}
	
	/**
	 * Cierra la conexión de tipo productor
	 */
	protected void closeProducerConnection()
	{
		if (getProducerConnection() != null)
			try
			{	
				getProducerConnection().close();
			}
			catch (Exception e) {}
	}
	
	
	protected abstract String getProcessName();

	
	public static void main(String args[])
	{
		// UIDs de proceso origen y destino
		final String sourceUID = "CORE-AD_Process-1010246";
		final String targetUID = "CORE-AD_Process-1010247";
						
		/* Posibles parametros desde consola */
		final String PARAM_PROCESS = 				"-p";
		final String PARAM_LIMIT = 					"-l";
		final String PARAM_QUERY_GROUP = 			"-q";
		final String PARAM_SOURCE_TIMEOUT_NUMBER = 	"-tn";
		final String PARAM_SOURCE_TIMEOUT_TYPE = 	"-tt";
		final String PARAM_SOURCE_ALL_RECORDS = 	"-ta";
		final String PARAM_TARGET_FROM_HOST = 		"-h";
		// Invocar a proceso Source o Target?
		String processType = null;
		
		// Parsear los parametros y validar
	  	HashMap<String, Object> params = new HashMap<String, Object>();
	  	
	  	// Parametro P obligatorio para ambos casos
	  	for (String arg : args)
	  		if (arg.toLowerCase().startsWith(PARAM_PROCESS))
	  			processType = arg.substring(PARAM_PROCESS.length());
	  	if (!"Source".equals(processType) && !"Target".equals(processType))
	  		showHelp("ERROR: No se especifico el parametro " + PARAM_PROCESS); 		
	  	
	  	// Parametros L y Q optativos para ambos casos, TN y TT optativos solo para origen, H optativos solo para destino
	  	for (String arg : args)
	  	{
	  		if (arg.toLowerCase().startsWith(PARAM_LIMIT))
	  			params.put(processType+"MaxRecords", arg.substring(PARAM_LIMIT.length()));
	  		else if ("Source".equals(processType))
	  		{
		  		if (arg.toLowerCase().startsWith(PARAM_QUERY_GROUP))
		  			params.put("SourceAckQueryesPerGroup", arg.substring(PARAM_QUERY_GROUP.length()));
		  		else if (arg.toLowerCase().startsWith(PARAM_SOURCE_TIMEOUT_NUMBER))
		  			params.put("TimeOutNro", arg.substring(PARAM_SOURCE_TIMEOUT_NUMBER.length()));
		  		else if (arg.toLowerCase().startsWith(PARAM_SOURCE_TIMEOUT_TYPE))
		  			params.put("TimeOutType", arg.substring(PARAM_SOURCE_TIMEOUT_TYPE.length()));
		  		else if (arg.toLowerCase().startsWith(PARAM_SOURCE_ALL_RECORDS))
		  			params.put("ResendAllRecords", arg.substring(PARAM_SOURCE_ALL_RECORDS.length()));
	  		}
	  		else if ("Target".equals(processType))
	  		{
	  			if (arg.toLowerCase().startsWith(PARAM_QUERY_GROUP))
		  			params.put("TargetMessagesPerTrx", arg.substring(PARAM_QUERY_GROUP.length()));
	  			else if (arg.toLowerCase().startsWith(PARAM_TARGET_FROM_HOST))
		  			params.put("ReplicateFromHost", arg.substring(PARAM_TARGET_FROM_HOST.length()));	  			
	  		}
	  	}

	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null)
	  		showHelp("ERROR: La variable de entorno OXP_HOME no está seteada ");
	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!org.openXpertya.OpenXpertya.startupEnvironment( false ))
	  		showHelp("ERROR: Error al iniciar la configuracion de replicacion ");

	  	// Configuracion 
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' "));
	      
	  	if (Env.getContext(Env.getCtx(), "#AD_Client_ID") == null || Env.getContext(Env.getCtx(), "#AD_Client_ID") == null)
	  		showHelp("ERROR: Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación. ");

	  	// Iniciar la transacción
		String m_trxName = Trx.createTrxName();
		Trx.getTrx(m_trxName).start();
		
		
		// Recuperar el proceso de replicación cliente
		int processId = DB.getSQLValue(m_trxName, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE AD_COMPONENTOBJECTUID = '" + 
													("Source".equals(processType)?sourceUID:targetUID) + "' ");
		ProcessInfo pi = MProcess.execute(Env.getCtx(), processId, params, m_trxName);

		// En caso de error, presentar en consola
		if (pi.isError())
			System.err.println("Error en replicacion: " + pi.getSummary());
			
	}
	
	
	private static final void showHelp(String message)
	{
		String help = " [[ " + message + " ]] " + 
				"\n" + 	
				" ------------ FRAMEWORK DE REPLICACION. MODO DE INSTANCIACION DE LOS PROCESOS ORIGEN Y DESTINO. --------------- " +
				" Ejemplos de uso de proceso origen (caso tipico de uso y parametros completos): \n" +
				" java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.replication.AbstractReplicationProcess -pSource \n" +
				" java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.replication.AbstractReplicationProcess -pSource -q1000 -tn2 -ttDAYS -taY -l5000 \n" +						
				" donde \n" +
				" -p    es el proceso a ejecutar.  En este caso se debera definir Source (Origen). Parametro obligatorio. \n" +
				" -q    es la cantidad de registros a actualizar a waitingAck de manera agrupada (por performance). Si no se especifica, el valor por defecto es 100. \n" +
				" -tn 	en reenvio de registros por timeout sin ack, indica la cantidad (ver tt). Si no se especifican, no se reenviarán registros en espera de ack. \n" +
				" -tt 	en reenvio de registros por timeout sin ack, indica la unidad   (ver tn). Opciones: [SECONDS, MINUTES, HOURS, DAYS]  \n" +
				" -ta 	además del reenvio de registros por timeout sin ack, se reenvian todos los registros dentro del periodo especificado (ver tn). Opciones: [Y, N] \n" +				
				" -l    limita la cantidad de registros a enviar.  Si no se especifica enviará todos los registros marcados para replicación. \n" +
				" \n" +
				" Ejemplo de uso de proceso destino (caso tipico de uso y parametros completos): \n" +
				" java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.replication.AbstractReplicationProcess -pTarget \n" +						
				" java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.replication.AbstractReplicationProcess -pTarget -h1 -q1000 -l5000  \n" +
				" donde \n" +
				" -p    es el proceso a ejecutar.  En este caso se debera definir Target (Destino). Parametro obligatorio. \n" +
				" -q    es la cantidad de registros a procesar en una misma transacción (por performance). Si no se especifica, el valor por defecto es 100. \n" +						
				" -h    es el host origen del cual se desea replicar (limitado solo a éste). Si no se especifica, replicará de todos los hosts según la configuración \n" +
				" -l    limita la cantidad de registros a procesar por host origen.  Si no se especifica procesará todos los registros. \n" +
				" donde \n" +
				" ------------ IMPORTANTE: NO DEBEN DEJARSE ESPACIOS ENTRE EL PARAMETRO Y EL VALOR DEL PARAMETRO! --------------- ";
  		System.out.println(help);
  		System.exit(1);		
	}
	
}
