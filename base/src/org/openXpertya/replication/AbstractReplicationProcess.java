package org.openXpertya.replication;

import java.math.BigDecimal;
import java.util.logging.Level;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;

import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_AD_ReplicationError;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

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
	        else if( name.equalsIgnoreCase( "TimeOutNro" ))
	        	timeOutNro = para[ i ].getParameterAsInt();
	        else if( name.equalsIgnoreCase( "TimeOutType" ))
	            timeOutType = (String)para[ i ].getParameter();
	        else if( name.equalsIgnoreCase( "SourceAckQueryesPerGroup" ))
	        	ReplicationConstants.REPLICATION_SOURCE_QUERIES_PER_GROUP = para[ i ].getParameterAsInt();	        
	        else if( name.equalsIgnoreCase( "TargetMessagesPerTrx" ))
	        	ReplicationConstants.REPLICATION_TARGET_MESSAGES_PER_TRX = para[ i ].getParameterAsInt();
        }
            
        // Setear valor de TimeOut. Conjuncion entre ambos datos
        if (timeOutNro != null && timeOutNro != 0 && timeOutType != null && timeOutType.length() > 0)
        	ReplicationConstants.ACK_TIME_OUT = "" + timeOutNro + " " + timeOutType;
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
	
}
