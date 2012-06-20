package org.openXpertya.replication;

import javax.jms.ConnectionFactory;
import javax.naming.Context;

import org.openXpertya.db.CConnection;
import org.openXpertya.model.MReplicationHost;

public class ReplicationConnection extends CConnection {

	public static final String CONN_FACTORY_JNDI_NAME = "ConnectionFactory";
		
	protected int m_orgID = -1;
	protected String m_apps_host = "";
	protected int m_apps_port = -1;
	protected Exception exception = null; 
	protected Context m_ic = null;
	/**
	 * Conexion para replicación
	 * @param orgID sucursal destino
	 * @param trxName transacción a utilizar
	 */
	public ReplicationConnection(int orgID, String trxName)
	{
		super();
		m_orgID = orgID;
		
		/* Recuperar host y port para esta conexión según AD_HostReplication */
		m_apps_host = MReplicationHost.getHostForOrg(orgID, trxName);
		m_apps_port = MReplicationHost.getPortForOrg(orgID, trxName);
	}
		
    
    /**
     * Redefinición según AD_HostReplication
     */
    public String getAppsHost() {
        return m_apps_host;
    }
    
    /**
     * Redefinición según AD_HostReplication
     */
    public int getAppsPort() {
        return m_apps_port;
    }
 
    /**
     * Redefinición según AD_HostReplication
     */
    public boolean isRMIoverHTTP() {
        return false;  // TODO: Ampliar metadatos para permitir esta feature
    }


	public Exception getException() {
		return exception;
	}		
	
	
	public Context getContext()
	{
		if (m_ic == null)
			m_ic = getInitialContext(false);
		
		return m_ic;
	}
	
	/**
	 * Obtengo el servidor a partir del host y puerto parámetro
	 * @param m_apps_host host 
	 * @param m_apps_port puerto
	 * @return connection factory
	 * @throws Exception
	 */
	public ConnectionFactory getConnectionFactory() throws Exception{
		Context context = getContext();
		ConnectionFactory connFactory = null;
   		if (context != null) {
			connFactory = (ConnectionFactory) context
					.lookup(CONN_FACTORY_JNDI_NAME);    		
    	}
   		return connFactory;
	}
	

}
