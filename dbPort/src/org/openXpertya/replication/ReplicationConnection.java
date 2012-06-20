package org.openXpertya.replication;

import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Replication;
import org.openXpertya.interfaces.ReplicationHome;
import org.openXpertya.model.MReplicationHost;

public class ReplicationConnection extends CConnection {

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
	 * Recupera el servidor de Replicación específico 
	 * para la sucursal especificada en el constructor
	 * @return
	 */
    public Replication getReplication() 
    {
    	/* Estan correctamente cargados los datos? */
    	if (m_apps_host.length()==0 || m_apps_port == -1)
    		return null;

    	/* Recuperar el server */
    	Replication m_server = null; 
    	try 
    	{
    		InitialContext	ic	= getInitialContext(false);
    		if (ic != null) 
    		{
    	    	m_ic = ic;
    			ReplicationHome	replicationHome	= (ReplicationHome) ic.lookup(ReplicationHome.JNDI_NAME);
    			if (replicationHome != null) 
                        m_server = replicationHome.create();
    		}
    	} 
    	catch (Exception ex) 
    	{
    		log.log(Level.SEVERE, "", ex);
    		exception = ex;
        }
        return m_server;
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
}
