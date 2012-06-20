package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.model.X_AD_ReplicationHost;
import org.openXpertya.util.DB;

public class MReplicationHost extends X_AD_ReplicationHost {

	public MReplicationHost(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MReplicationHost(Properties ctx, int AD_ReplicationHost_ID, String trxName) {
		super(ctx, AD_ReplicationHost_ID, trxName);
		// TODO Auto-generated constructor stub
	}


	/**
	 * Para una organización dada, retorna la posicion de la 
	 * misma dentro del array de replicación (replicationArray)
	 */
	public static int getReplicationPositionForOrg(int orgID, String trxName)
	{
		return DB.getSQLValue(trxName, " SELECT replicationarraypos FROM AD_ReplicationHost WHERE AD_Org_ID = ?", orgID);	
	}
	
	/**
	 * Para una posicion dada en el array de replicación, 
	 * retorna el AD_Org_ID correspondiente
	 */
	public static int getReplicationOrgForPosition(int pos, String trxName)
	{
		return DB.getSQLValue(trxName, " SELECT AD_Org_ID FROM AD_ReplicationHost WHERE replicationarraypos = ?", pos);	
	}
	
	/**
	 * Para una organización dada, retorna el host configurado 
	 */
	public static String getHostForOrg(int orgID, String trxName)
	{
		return DB.getSQLValueString(trxName, " SELECT hostname FROM AD_ReplicationHost WHERE AD_Org_ID = ?", orgID);	
	}
	
	/**
	 * Para una organización dada, retorna el port configurado 
	 */
	public static int getPortForOrg(int orgID, String trxName)
	{
		return DB.getSQLValue(trxName, " SELECT hostport FROM AD_ReplicationHost WHERE AD_Org_ID = ?", orgID);	
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// El numero de host es valido?
		if (getReplicationArrayPos() <= 0)
		{
    		log.saveError( "Error", "El valor del número de organización debe ser mayor a cero");
    		return false;
			
		}
		
		// Ya hay una entrada con la marca thishost?
		int cant = DB.getSQLValue(get_TrxName(), " SELECT COUNT(1) FROM AD_ReplicationHost WHERE AD_ReplicationHost_ID <> " + getAD_ReplicationHost_ID() + " AND thishost = 'Y' AND AD_Client_ID = ?" , getAD_Client_ID());
		if (isThisHost() && cant > 0)
		{
    		log.saveError( "Error", "Ya existe otro regisgtro con la marca de Este Host.  Debe destildar primeramente dicha marca. ");
    		return false;
		}
		
		return true;
		
	}
	
}
