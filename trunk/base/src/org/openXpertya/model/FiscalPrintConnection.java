package org.openXpertya.model;

import java.util.logging.Level;

import javax.naming.InitialContext;

import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.FiscalPrint;
import org.openXpertya.interfaces.FiscalPrintHome;

public class FiscalPrintConnection extends CConnection{

	protected int m_orgID = -1;
	protected String m_apps_host = "";
	protected int m_apps_port = -1;
	protected Exception exception = null; 
	
	/**
	 * Conexion para replicación
	 * @param orgID sucursal destino
	 * @param trxName transacción a utilizar
	 */
	public FiscalPrintConnection(int orgID, String trxName)
	{
		super();
		m_orgID = orgID;
		
		/* Recuperar host y port para esta conexión según AD_HostReplication */
		m_apps_host = MReplicationHost.getHostForOrg(orgID, trxName);
		m_apps_port = MReplicationHost.getPortForOrg(orgID, trxName);
	}

	/**
	 * @return el servidor de impresión fiscal remota
	 */
    public FiscalPrint getFiscalPrintConnection() 
    {
    	/* Estan correctamente cargados los datos? */
    	if (m_apps_host.length()==0 || m_apps_port == -1)
    		return null;

    	/* Recuperar el server */
    	FiscalPrint m_server = null; 
    	try 
    	{
    		InitialContext	ic	= getInitialContext(false);
    		if (ic != null) 
    		{
    			FiscalPrintHome	fiscalPrintHome	= (FiscalPrintHome) ic.lookup(FiscalPrintHome.JNDI_NAME);
    			if (fiscalPrintHome != null) 
                        m_server = fiscalPrintHome.create();
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
	
}
