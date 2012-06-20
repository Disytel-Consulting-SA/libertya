package org.openXpertya.replication;

/**
 * Cliente RMI de replicación.  Se encarga de interactuar
 * via RMI con el servidor de Replication, pasándole
 * los datos a replicar, iterando dicha lógica por cada sucursal
 */



import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.jms.Session;
import javax.naming.Context;

import org.openXpertya.interfaces.Replication;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_ReplicationError;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.reflection.CallConfig;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.reflection.ClientCall;
import org.openXpertya.reflection.JMSCallConfig;
import org.openXpertya.reflection.RMICallConfig;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;


public class ReplicationClientProcess extends SvrProcess {

	
	/** Flag de replicacion sincrónica */
	boolean sync_replication = true;

	/** Todas las replicaciones dan OK */
	boolean all_orgs_ok = true;
	
	/** ID de esta organización */
	int thisOrgID = -1; 
	
	/** Posicion de esta organización en el array de organización */
	int thisOrgPos = -1; 
		
	/** Si esta variable está seteada, significa que en este momento deberá replicarse unicamente hacia esta sucursal 
	 *  Esto está pensado para los casos de replicateNow, en los cuales hay que priorizar el dato para una org. especifica */
	int p_forceTargetOrgID = -1;
	
	/** ID inicial del changelog a replicar (sujeto a valores del replicationArray) - tabla AD_Changelog_Replication */
	int p_Changelog_Initial_ID = -1; 
	
	/** ID final del changelog a replicar (sujeto a valores del replicationArray) - tabla AD_Changelog_Replication */
	int p_Changelog_Final_ID = -1;

	/** Realizar la replicación sincrónica de manera forzada (salteando replicaciones pendientes en host destino)  */
	String p_Force_Replication = "N";
	
	/** Constante para entrada en el contexto: verificación del origen de la petición de replicación */
	public static final String VALIDATION_PROPERTY = "#Replication_Access_Key";
	
	/** Si Nombre de host es OFFLINE_HOST, entonces guarda a archivo en lugar de enviar la información a replicar */
	public static final String OFFLINE_REPLICATION_VALUE = "OFFLINE_HOST";
	
	/** JNDI Name del bean y destino de los mensajes JMS */
	private static final String EVTQUEUE_JNDI_NAME = "queue/EventQueue";
	private static final String ACKQUEUE_JNDI_NAME = "queue/AckQueue";
	
	protected Context m_ic = null;
	
	@Override
	protected void prepare() {
		
		/* Setear el ID y Pos de esta organizacion (ignora la Org del login, utiliza la conf. de thisHost) */
		thisOrgID  = DB.getSQLValue(get_TrxName(), "SELECT AD_Org_ID FROM AD_ReplicationHost WHERE thisHost = 'Y' AND AD_Client_ID = " + getAD_Client_ID()); 
		thisOrgPos = MReplicationHost.getReplicationPositionForOrg(thisOrgID, get_TrxName()); 
		
		/* Log en tabla de errores de replicacion - inicio de replicación */
		String info = "INICIO DE REPLICACIÓN";
		saveLog(false, Level.INFO, info, info, null, -1, -1, getReplicationType());

		/* Parametros? */
        ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( name.equals( "AD_Org_ID" ))
            	p_forceTargetOrgID = para[ i ].getParameterAsInt();
            else if (name.equals( "Changelog_Initial_ID" ))
            	p_Changelog_Initial_ID = para[i].getParameterAsInt();
            else if (name.equals( "Changelog_Final_ID" ))
            	p_Changelog_Final_ID = para[i].getParameterAsInt();
            else if (name.equals( "Force_Replication" ))
            	p_Force_Replication = (String)para[i].getParameter();
            		
        }

	}
	
	
	@Override
	protected String doIt() throws Exception {
		
		/* Configuración correcta en AD_ReplicationHost? */
		if (thisOrgID == -1)
			throw new Exception (" Sin marca de host.  Debe realizar la configuración correspondiente en la ventana Hosts de Replicación ");
		
		/** Cargar en memoria todas las referencias e información de tablas */
        loadCacheData();
		
        
        /*
         * POR AQUI DEBEREMOS RECIBIR DE LA COLA DE ACKS PARA DECREMENTAR LOS VALORES DE REPARRAY SEGUN CORRESPONDA
         */
        
        
        
		/* Recuperar todas las organizaciones a fin de replicar la informacion para cada una 
		   Contemplando la posibilidad de replicar en este momento unicamente a una sucursal destino */		
		int[] orgs = {p_forceTargetOrgID}; 
		if (p_forceTargetOrgID == -1) 
			orgs = PO.getAllIDs("AD_Org", " isActive = 'Y' AND AD_Client_ID = " + getAD_Client_ID() + " AND AD_Org_ID != " + thisOrgID, get_TrxName());
		
		/* Builder que genenera el XML a partir del changelog */
		ReplicationBuilder builder = null;
		int initialID = -1;
		int finalID = -1;

		/* Iterar por todas las sucursales */
		for (int i=0; i<orgs.length; i++)
		{
			try
			{
				/* Obtener el XML a enviar al host destino, en caso de error continuar con siguiente host */
				int replicationArrayPos = MReplicationHost.getReplicationPositionForOrg(orgs[i], get_TrxName());
				if (replicationArrayPos == -1)
					continue;

				/* Setear la accessKey para validación en el host remoto */
				Env.setContext(getCtx(), VALIDATION_PROPERTY, DB.getSQLValueString(get_TrxName(), getAccessKeySQLQuery(true), replicationArrayPos)); 
				
				/**
				 * Recorrer el changelog para la sucursal paginando de a
				 * un numero de registros, comprimirlos e incorporarlos
				 * al vector que contiene todos los stripes comprimidos,
				 * el cual será enviado al host remoto para su replicación
				 */
				initialID = -1;
				finalID = -1;
				Vector<byte[]> completeCompressedXMLContent = new Vector<byte[]>();
				boolean hasReplicationData = true;
				int iteration = 0;
				
				/* El builder a utilizar procesará la información del changelog
				 * En la primera iteración, buscará el menor changelogReplicationID (por esto se le pasa -1) 
				 * En las siguientes iteraciones, deberá buscar a partir del siguiente registro luego del changelog_final_id 
				 */
				builder = new ReplicationBuilder(replicationArrayPos, p_Changelog_Initial_ID, p_Changelog_Final_ID, get_TrxName());
				while (hasReplicationData)
				{
					/* Generar el XML */
					builder.generateDocument();
					hasReplicationData = builder.hasReplicationData();

					/* Hay informacion a replicar? */
					if (hasReplicationData)
					{
						/* Si es la primera iteración, quedarme con el id inicial */
						if (iteration++ == 0)
							initialID = builder.getGroupList().getM_changelog_initial_id();
						/* Quedarme siempre con el ultimo changelog */
						finalID = builder.getGroupList().getM_changelog_final_id();
	
						/* Comprimir el string a fin de reducir los tiempos de transmision entre hosts e incorporarlo al Vector */
						byte[] compressedXML = builder.getCompressedXML();
						completeCompressedXMLContent.add(compressedXML);
	
						/* Limpiar memoria */
						builder.emptyM_replicationXMLData();
						builder.setInitial_changelog_replication_id(finalID+1);
						compressedXML = null;
						System.gc();
					}
				}
				
				/* Recuperar el hostname para determinar si es una replicación online o no */
				String hostName = MReplicationHost.getHostForOrg(orgs[i], get_TrxName());
				boolean offlineOrg = OFFLINE_REPLICATION_VALUE.equalsIgnoreCase(hostName);
				
				/* Realizar el envio correspondiente */
				transferContent(completeCompressedXMLContent, orgs, i, initialID, finalID, replicationArrayPos, offlineOrg);

			}
			catch (RemoteException e)
			{
				String error = "Error Remoto. ";
				saveLog(true, Level.SEVERE, error, error + e.getMessage(), orgs[i], initialID, finalID, getReplicationType());
			}
			catch (Exception e)
			{
				String error = "Error Local. ";
				saveLog(true, Level.SEVERE, error, error + e.getMessage(), orgs[i], initialID, finalID, getReplicationType());
			}
			finally
			{
				/* Limpiar el contexto */
				Env.setContext(getCtx(), VALIDATION_PROPERTY, (String)null);
				
				/* Help garbage collector */
				builder = null;
				System.gc();
			}
		}

		/* Log en tabla de info/errores de replicacion - fin de replicación */
		String info = "FIN DE REPLICACIÓN";
		saveLog(false, Level.INFO, info, info, null, -1, -1, getReplicationType());
		
		/* Informar acordemenete (aunque esto es relativo, ya que este proceso sera una tarea programada) */
		freeCacheData();
		return "FINALIZADO " + (all_orgs_ok?"":"Revise la nomina de errores en replicación");
		
	}
	
	/**
	 * Realiza el envio de los datos de replicación
	 * @param completeCompressedXMLContent contenido
	 * @param orgs organizaciones
	 * @param i organizacion actual
	 * @param initialID changelog inicial
	 * @param finalID changelog final
	 * @param replicationArrayPos posicion en el array de replicacion
	 * @param offlineOrg indica si debe almacenarse en archivo en lugar de ser enviado
	 * @throws Exception
	 */
	protected void transferContent(Vector<byte[]> completeCompressedXMLContent, int[] orgs, int i, int initialID, int finalID, int replicationArrayPos, boolean offlineOrg) throws Exception, RemoteException
	{
		CallResult aCallResult = null; 
		
		/* Si no hay nada para replicar, salteamos la org */
		if (completeCompressedXMLContent.size() == 0)
			return;
		
		/* Si es una sucursl offline, generar archivos con la informacion (uno por cada posicion del bytearray) */
		if (offlineOrg)
		{
			MOrg thisOrg = new MOrg(getCtx(), thisOrgID, get_TrxName());
			MOrg anOrg = new MOrg(getCtx(), orgs[i], get_TrxName());
			String baseFileName = "ReplicationInfo_" + Env.getDateTime("yyyyMMdd-HHmmss")
									+ "_" + thisOrgPos + ("(") + thisOrg.getName().replace("(", "").replace(")", "") + ")a" 
									+ replicationArrayPos + "(" + anOrg.getName().replace("(", "").replace(")", "") + ")_" 
									+ initialID + "a" + finalID;
			int size = completeCompressedXMLContent.size();
			for (int k = 0; k < size; k++)
			{
				String fileName = baseFileName + ".rep" + (k+1) + "_" + size;
				DataOutputStream os = new DataOutputStream(new FileOutputStream(fileName));
				os.write(completeCompressedXMLContent.get(k));
				os.close();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(baseFileName + ".md5"));
			out.write(DB.getSQLValueString(get_TrxName(), getAccessKeySQLQuery(true), replicationArrayPos));
			out.close();
		}
		else
		{
			/* Instanciar la conexión correspondiente y verificar que sea correcta*/
			ReplicationConnection conn = new ReplicationConnection(orgs[i], get_TrxName()); 
			Replication replication = conn.getReplication();
			m_ic = conn.getContext();
			if (replication==null)
			{
				String error = "Error Local. Imposible conectar a host. ";
				saveLog(true, Level.SEVERE, error, error + conn.getException().getMessage(), orgs[i], initialID, finalID, getReplicationType());
				return;
			}
			// Modified by Matías Cap - Disytel
			// ---------------------------------------------------------
			// Usar las nuevas llamadas a métodos genéricos con timeouts
			
			// Armo el objeto a enviar en el mensaje
			Map<String, Object> objectToSend = new HashMap<String, Object>();
			objectToSend.put("ctx", getCtx());
			objectToSend.put("completeCompressedXML", completeCompressedXMLContent);
			objectToSend.put("sync", sync_replication);
			objectToSend.put("arrayPos", thisOrgPos);
			objectToSend.put("initialChangelogID", initialID);
			objectToSend.put("finalChangelogID", finalID);
			objectToSend.put("force", "Y".equals(p_Force_Replication));
			// Armo el mensaje a enviar, su tipo y objeto
			Map<String, List<Object>> message = new HashMap<String, List<Object>>();
			List<Object> params = new ArrayList<Object>();
			params.add(objectToSend);
			message.put(JMSCallConfig.MSG_TYPE_OBJECT, params);
			// Realizo la llamada remota
			aCallResult = makeCall(getCtx(), JMSCallConfig.DESTINATION_TYPE_QUEUE,
									"generalReplication", Session.AUTO_ACKNOWLEDGE,
									EVTQUEUE_JNDI_NAME, message, true, true,
									null, null, get_TrxName());	
			}
		
		/* Todo bien? Decrementar los indices de replicationArray en la posicion de la organización 
		 * Comitteamos inmediatamente ya que en el host remoto los cambios ya han sido realizados 
		 * (en el caso de replicación offline, se toma la correcta exportación a archivo */
		if(offlineOrg || !aCallResult.isError()){
			decrementReplicationArray(replicationArrayPos, initialID, finalID);
			Trx.getTrx(get_TrxName()).commit();
		}
		else{
			// Hubo un problema en una replicación del lado del server 
			String error = "Error Remoto durante replicación. ";
			saveLog(true, Level.SEVERE, error, error + aCallResult.getMsg(), orgs[i], initialID, initialID, getReplicationType());
		}
		aCallResult = null;
		// ---------------------------------------------------------
	}
	
	/**
	 * Almacena el error correspondiente segun sea el caso de invocación
	 * @param logMessage
	 */
	protected void saveLog(boolean setError, Level aLevel, String logMessage, String replicationError, Integer orgID, int changelogInitPos, int changelogFinalPos, String replicationType)
	{
		System.out.println("Replication " + (orgID==null?"":"("+orgID+")") + ":" + logMessage + "-" + replicationError);
		
		if (setError)
			all_orgs_ok = false;

		X_AD_ReplicationError aLog = new X_AD_ReplicationError(getCtx(), 0, get_TrxName());
		aLog.setORG_Target_ID(orgID==null?0:orgID);
		aLog.setInitialChangelog_ID(changelogInitPos);
		aLog.setFinalChangelog_ID(changelogFinalPos);
		aLog.setReplication_Type(replicationType);
		aLog.setReplication_Error(Env.getDateTime("yyyy/MM/dd-HH:mm:ss.SSS") + " - " + replicationError);
		aLog.setClientOrg(getAD_Client_ID(), thisOrgID);
		aLog.save();
		
		log.log(aLevel, logMessage );
	}
	
	
	/**
	 * Retorna el tipo de replicación en función del boolean correspondiente 
	 */
	protected String getReplicationType()
	{
		return sync_replication?X_AD_ReplicationError.REPLICATION_TYPE_Synchronous:X_AD_ReplicationError.REPLICATION_TYPE_Asynchronous;
	}
	
	
	/**
	 * Decrementa a 0 el valor en la columna replicationArray para todas 
	 * las entradas en la bitacora de replicación en la posicion pos
	 * @param pos posicion a decrementar en el array
	 */
	protected void decrementReplicationArray(int pos, int initialID, int finalID)
	{
		DB.executeUpdate(" UPDATE ad_changelog_replication " +
						 " SET replicationarray = OVERLAY(replicationarray placing '0' FROM "+(pos)+" for 1) " +
						 " WHERE substring(replicationarray, "+(pos)+", 1) = '1' " +
						 " AND ad_changelog_replication_id between " + initialID + " AND " + finalID + 
						 " AND AD_Client_ID = " + getAD_Client_ID(), get_TrxName());
	}
	    

    /**
     * Invocaciones a carga de tablas referenciales varias en memoria
     * a fin de reducir los tiempos de acceso a base de datos
     * @throws Exception
     */
    protected void loadCacheData() throws Exception
    {
    	ReplicationBuilder.loadCacheData(get_TrxName());
		ChangeLogElement.loadColumnData(get_TrxName());
    }
    
    /**
     * Liberación de memoria de tablas referenciales
     */
    protected void freeCacheData()
    {
    	ReplicationBuilder.freeCacheData();
    	ChangeLogElement.freeColumnData();
    }
    
    
    /**
     * Genera el query para obtener el key definitivo entre las sucursales origen 
     * y destino a fin de validar la correctitud de la petición de replicación
     * @param host: si es true arma la consulta para el host origen, si es false la arma para el host destino
     * @return el md5 resultante de concatenar ambos hostAccessKey
     */
    public static String getAccessKeySQLQuery(boolean host)
    {
    	String sourceWhere = host ? " thisHost = 'Y' " : " replicationArrayPos = ? ";
    	String targetWhere = host ? " replicationArrayPos = ? " : " thisHost = 'Y' ";
    	return 
			" SELECT md5( " + 
			"			(SELECT hostAccessKey as source FROM AD_REPLICATIONHOST WHERE " + sourceWhere + ") || " +
			"	  	    (SELECT hostAccessKey as target FROM AD_REPLICATIONHOST WHERE " + targetWhere + ")    " +
			"	  	 )    ";
    }

    
    /**
     * Invocación al proceso de replicación cliente desde terminal
     * Alternativa de uso del proceso fuera de la aplicación
     * @param args
     */
    public static void main(String args[])
    {
    	// Ejemplo de uso:
    	// java -classpath lib/OXP.jar:lib/OXPLib.jar:lib/OXPXLib.jar org.openXpertya.replication.ReplicationClientProcess -host1010053 -initial1049382 -final1058178 -force
    	// -host 		es el host destino al cual deberá replicar, indicándose el AD_Org_ID correspondiente.  
    	//				si no es especifica ninguno, replicará a todos los hosts según el replicationArray
    	// -initial 	es el changelogID inicial que se quiere replicar
    	// -final 		es el changelogID inicial que se quiere replicar
    	// -force		fuerza la replicación (este parámetro no requiere valor alguno).
    	// ------------ NO DEBEN DEJARSE ESPACIOS ENTRE EL PARAMETRO Y EL VALOR DEL PARAMETRO! ---------------
    	
    	/* Constantes */
    	final String PARAMETER_HOST 		= "-host";
    	final String PARAMETER_INITIAL_CL 	= "-initial"; 
    	final String PARAMETER_FINAL_CL 	= "-final"; 
    	final String PARAMETER_FORCE 		= "-force"; 
    	
    	// Parsear los parametros
    	HashMap<String, Object> params = new HashMap<String, Object>();
    	for (String arg : args)
    	{
    		if (arg.toLowerCase().startsWith(PARAMETER_HOST))
    			params.put("AD_Org_ID", arg.substring(PARAMETER_HOST.length()));
    		else if (arg.toLowerCase().startsWith(PARAMETER_INITIAL_CL))
    			params.put("Changelog_Initial_ID", arg.substring(PARAMETER_INITIAL_CL.length()));
    		else if (arg.toLowerCase().startsWith(PARAMETER_FINAL_CL))
    			params.put("Changelog_Final_ID", arg.substring(PARAMETER_FINAL_CL.length()));
    		else if (arg.toLowerCase().startsWith(PARAMETER_FORCE))
    			params.put("Force_Replication", "Y");
    	}
    		
    	String oxpHomeDir = System.getenv("OXP_HOME"); 
    	if (oxpHomeDir == null)
    	{
    		System.out.println("Error: OXP_HOME environment variable not set");
    		return;
    	}

    	// Cargar el entorno basico
    	System.setProperty("OXP_HOME", oxpHomeDir);
    	if (!org.openXpertya.OpenXpertya.startupEnvironment( false ))
    	{
    		System.err.println("Error al iniciar la configuración de replicacion");
    		return;
    	}
        Env.setContext(Env.getCtx(), "#AD_Client_ID", DB.getSQLValue(null, " SELECT AD_Client_ID FROM AD_REPLICATIONHOST WHERE thisHost = 'Y' "));
        Env.setContext(Env.getCtx(), "#AD_Org_ID", DB.getSQLValue(null, " SELECT AD_Org_ID FROM AD_REPLICATIONHOST WHERE thisHost = 'Y' "));
        
        // Iniciar la transacción
		String m_trxName = Trx.createTrxName();
		Trx.getTrx(m_trxName).start();
		
		// Recuperar el proceso de replicación cliente
		int processId = DB.getSQLValue(m_trxName, " SELECT AD_PROCESS_ID FROM AD_PROCESS WHERE AD_COMPONENTOBJECTUID = 'CORE-AD_Process-1010218' ");
		ProcessInfo pi = MProcess.execute(Env.getCtx(), processId, params, m_trxName);

		// En caso de error, presentar en consola
		if (pi.isError())
			System.err.println("Error en replicacion: " + pi.getSummary());
    }
    
    
    


    
	/**
	 * Armo la configuración de la llamada remota y realizo dicha llamada,
	 * retornando el resultado de ella.
	 * 
	 * @param ctx
	 *            contexto
	 * @param methodName
	 *            nombre del método remoto
	 * @param parameterTypes
	 *            tipo de los parámetros
	 * @param parameterValues
	 *            valores de los parámetros
	 * @param timerValue
	 *            clave de búsqueda del timer
	 * @param timeout
	 *            valor del timeout, generalmente null cuando poseemos una clave
	 *            de timer
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return resultado de la llamada
	 */
	protected CallResult makeCall(Properties ctx, String destinationType,
			String msgType, int ackType, String destinationJNDIName,
			Map<String, List<Object>> messages, boolean createReplyDestination,
			boolean sessionTransacted, String timerValue, Long timeout,
			String trxName) {
		// Obtener el servidor y mandar los datos para la carga remota
		CallConfig config = null;
		CallResult result = null;		
		try {
			// Armar la configuración
			config = makeCallConfig(ctx, destinationType, msgType, ackType,
					destinationJNDIName, messages, createReplyDestination,
					sessionTransacted, timerValue, timeout, trxName);
			// Realizar la llamada remota
			// Creo y realizo la llamada remota
			result = call(config);
		} catch (Exception e) {
			String msg = Util.isEmpty(e.getMessage()) ? e.getCause()
					.getMessage() : e.getMessage();
			if(msg == null){
				msg = Msg.getMsg(ctx, "NoConnectionToCentral");
			}
			result.setMsg(msg,true);
		}
		return result;		
	}
	
	/**
	 * Crear la configuración para los llamados remotos
	 * 
	 * @param ctx
	 *            contexto
	 * @param methodName
	 *            nombre del método remoto
	 * @param parameterTypes
	 *            tipos de datos de los parámetros del método remoto
	 * @param parameterValues
	 *            valores de los parámetros del método remoto
	 * @param timerValue
	 *            value del timer
	 * @param timeout
	 *            timeout en milisegundos
	 * @param trxName
	 *            nombre de la transacción
	 * @return configuración de la llamada remota
	 * @throws Exception
	 */
	protected CallConfig makeCallConfig(Properties ctx, String destinationType,
			String msgType, Integer ackType, String destinationJNDIName,
			Map<String, List<Object>> messages, boolean createReplyDestination,
			boolean sessionTransacted, String timerValue, Long timeout,
			String trxName) throws Exception {
		// Obtengo la conexión
		Context jndiContext = m_ic; // getJNDIContext(ctx, trxName, true);
		JMSCallConfig callConfig = new JMSCallConfig();
		callConfig.setJndiContext(jndiContext);
		callConfig.setCtx(ctx);
		callConfig.setTrxName(trxName);
		// Si el value del timer está vacío, verifico el timeout parámetro
		if(Util.isEmpty(timerValue)){
			// Si el timeout parámetro es null, entonces dejo como está ya que
			// se inicializa con un timeout de 0 segundos
			if(timeout != null){
				callConfig.setTimeout(timeout);
			}
		}
		else{
			callConfig.setTimerValue(timerValue);
		}
		callConfig.setDestinationType(null); 	// no quiero respuesta
		callConfig.setMsgType(msgType);
		if(!Util.isEmpty(ackType, false)){
			callConfig.setAckType(ackType);
		}
		callConfig.setDestinationJNDIName(destinationJNDIName);
		callConfig.setMessages(messages);
		callConfig.setCreateReplyDestination(createReplyDestination);
		callConfig.setSessionTransacted(sessionTransacted);
		return callConfig;
	}
    
    
	/**
	 * Realizar la llamada remota con una configuración parámetro
	 * 
	 * @param callConfig
	 *            configuración de llamada
	 * @return resultado de la llamada remota
	 * @throws Exception 
	 */
	protected CallResult call(CallConfig callConfig) throws Exception{
		// Obtener el servidor y mandar los datos para la carga remota
		ClientCall clientCall = null;
		CallResult result = null;
		try {
			// Realizar la llamada remota
			// Creo y realizo la llamada remota
			clientCall = new ClientCall(callConfig);
			result = clientCall.call();
		} catch (Exception e) {
			String msg = Util.isEmpty(e.getMessage()) ? e.getCause()
					.getMessage() : e.getMessage();
			if(msg == null){
				msg = Msg.getMsg(callConfig.getCtx(), "NoConnectionToCentral");
			}
			result.setMsg(msg,true);
		}
		return result;
	}
	
	
	/**
	 * Obtengo el contexto jndi para la conexión a la central
	 * 
	 * @param ctx
	 *            contexto de la aplicación
	 * @param trxName
	 *            nombre de la transacción
	 * @return contexto jndi para la conexión a la central, null en caso de error
	 */
//	protected Context getJNDIContext(Properties ctx, String trxName, boolean throwIfNull) throws Exception{
//		ReplicationConnection conn = new ReplicationConnection(ctx, trxName);
//		Context context = conn.getContext();
//		if(context == null && throwIfNull){
//			throw new Exception(Msg.getMsg(ctx, "NoConnectionToCentral"));
//		}
//		return context;
//	}
    
    
   


}
