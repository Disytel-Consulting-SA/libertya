package org.openXpertya.cc;

import java.util.Properties;
import java.util.logging.Level;

import javax.jms.ConnectionFactory;
import javax.naming.Context;

import org.openXpertya.db.CConnection;
import org.openXpertya.model.MCentralConfiguration;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class CurrentAccountConnection extends CConnection {

	// Constantes
	
	public static final String CONN_FACTORY_JNDI_NAME = "ConnectionFactory";
	
	// Variables de clase
	
	/** Configuración de la conexión a la central */
	
	private static MCentralConfiguration centralConfig;
	
	// Variables de instancia
	
	/** Contexto */
	
	private Properties ctx;	
	
	/** Transacción */
	
	private String trxName;
	
	/** Host actual */
	
	protected String m_apps_host;
	
	/** Puerto actual */
	
	protected int m_apps_port;
	
	/** Exception en caso de que haya algún error */
	
	protected Exception exception = null; 

	// Métodos de clase

	/**
	 * Crea una configuración de central a partir de la compañía del contexto
	 * 
	 * @param ctx
	 *            contexto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return configuración de central si es que existe alguna
	 */
	public static MCentralConfiguration createCentralConfig(Properties ctx, String trxName){
		return (MCentralConfiguration) PO.findFirst(ctx, MCentralConfiguration.Table_Name,
				"ad_client_id = ?", new Object[] { Env.getAD_Client_ID(ctx) },
				null, trxName);
	}

	/**
	 * Obtengo la configuración de la central almacenada. Si no existe alguna,
	 * se crea una a partir de la compañía del contexto parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return configuración de la central de la compañía actual
	 */
	public static MCentralConfiguration getCentralConfig(Properties ctx, String trxName){
		if(centralConfig == null){
			centralConfig = createCentralConfig(ctx,trxName);
		}
		return centralConfig;
	}
	
	/**
	 * Limpia la configuración para buscarla nuevamente
	 */
	public static void clearConfig(){
		centralConfig = null;
	}
	
	
	// Constructores 
	
	public CurrentAccountConnection(Properties ctx, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
	}
	
	
	/**
	 * Obtengo el servidor a partir del host y puerto parámetro
	 * @param m_apps_host host 
	 * @param m_apps_port puerto
	 * @return connection factory
	 * @throws Exception
	 */
	public ConnectionFactory getConnectionFactory(String m_apps_host, int m_apps_port) throws Exception{
		Context context = getContext(m_apps_host, m_apps_port);
		ConnectionFactory connFactory = null;
   		if (context != null) {
			connFactory = (ConnectionFactory) context
					.lookup(CONN_FACTORY_JNDI_NAME);    		
    	}
   		return connFactory;
	}

	/**
	 * Connection factory desde el contexto parámetro
	 * 
	 * @param context
	 *            contexto parámetro
	 * @return connection factory
	 * @throws Exception
	 *             en caso de error
	 */
	protected ConnectionFactory getConnectionFactory(Context context) throws Exception{
		ConnectionFactory connFactory = null;
   		if (context != null) {
			connFactory = (ConnectionFactory) context
					.lookup(CONN_FACTORY_JNDI_NAME);    		
    	}
   		return connFactory;
	}

	/**
	 * Contexto de conexión
	 * 
	 * @param m_apps_host
	 *            host
	 * @param m_apps_port
	 *            puerto
	 * @return contexto de conexión
	 * @throws Exception
	 *             en caso de error
	 */
	public Context getContext(String m_apps_host, int m_apps_port) throws Exception{
		// Existe host o port configurado?
    	if (m_apps_host.length()==0 || m_apps_port == -1){
			throw new Exception("No connection configuration for host "
					+ m_apps_host + " and port " + m_apps_port);
    	}
    	// Actualizo mis variables de conexión locales
    	this.m_apps_port = m_apps_port;
    	this.m_apps_host = m_apps_host; 
    	// Recuperar el contexto
 		return getInitialContext(false);
	}
	
	/**
	 * Obtener el contexto jndi para la conexión a la central
	 * @return contexto jndi
	 * @throws Exception
	 */
	public Context getContext() throws Exception{
    	MReplicationHost replicationHost  = null;
		MCentralConfiguration config = null;
		ConnectionFactory factory = null;
		Context context = null;
		try {
			// Primero intento con el host configurado en replicación, sino
			// tengo conexión pruebo con el alternativo
			config = getCentralConfig(getCtx(), getTrxName());
			if(config == null){
				String msg = Msg.getMsg(getCtx(), "NotExistCCACConfForClient");
				log.log(Level.SEVERE, msg);
	    		exception = new Exception(msg);
			}
			else{
				// 1) Host de Replicación
				// Obtengo el host de replicación configurado
				replicationHost = new MReplicationHost(getCtx(),
						config.getAD_ReplicationHost_ID(), getTrxName());
				context = getContext(replicationHost.getHostName(),
						replicationHost.getHostPort());
				factory = getConnectionFactory(context);
				// Si no hay server, entonces busco la conexión alternativa
				if(factory == null){
					// 2) Host Alternativo, si existe
					if(!Util.isEmpty(config.getAlternativeCentralAddress())){
						context = getContext(config.getAlternativeCentralAddress(),
								replicationHost.getHostPort());
						factory = getConnectionFactory(context);
					}
				}
				// Si sigue no habiendo server entonces mensaje de error
				if(factory == null){
					String msg = Msg.getMsg(ctx, "NoConnectionToCentral");
		    		log.log(Level.SEVERE, msg);
					exception = new Exception(msg);
					context = null;
				}
			}
    	} catch (Exception ex) {
    		try{
	    		// Si no hay conexión, entonces verifico la dirección alternativa
	   			// 2) Host Alternativo, si existe
				if(!Util.isEmpty(config.getAlternativeCentralAddress())){
					context = getContext(config.getAlternativeCentralAddress(),
							replicationHost.getHostPort());
					factory = getConnectionFactory(context);
				}
				// Si no hay server, entonces me quedo con la excepción anterior
				if(factory == null){
		    		log.log(Level.SEVERE, ex.getMessage(), ex);
					exception = ex;
					context = null;
				}
    		} catch (Exception ex2) {
        		log.log(Level.SEVERE, ex2.getMessage(), ex2);
        		exception = ex2;
        		context = null;
            }
        }
 		return context;
	}
	
	/**
     * Redefinición según la configuración de la central
     */
    public String getAppsHost() {
        return m_apps_host;
    }
       
    public int getAppsPort() {
    	return m_apps_port;
    }

	public Exception getException() {
		return exception;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}
}
