package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;



public class MTableReplication extends X_AD_TableReplication {

	/** Cache de replicationArray según AD_TableReplication por ID de tabla */
	protected static HashMap<Integer, String> replicationArrayByTable = null;
	/** Cache de replicationArray según AD_TableReplication por nombre de tabla (en minuscula) */
	protected static HashMap<String, String> replicationArrayByTableName = null;
	
	/** Logger */
    private static CLogger	s_log	= CLogger.getCLogger(MChangeLog.class);

	
	public MTableReplication(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MTableReplication(Properties ctx, int AD_TableReplication_ID,
			String trxName) {
		super(ctx, AD_TableReplication_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Dado el ID de una tabla retorna el replicationArray correspondiente
	 */
	public static String getReplicationArray(int tableID, String trxName)
	{
		if (replicationArrayByTable == null)
			loadReplicationArrayByTable(trxName);

		return replicationArrayByTable.get(tableID);
	}
	
	/**
	 * Dado el nombre de una tabla retorna el replicationArray correspondiente
	 */
	public static String getReplicationArray(String tableName, String trxName)
	{
		if (replicationArrayByTableName == null)
			loadReplicationArrayByTable(trxName);

		return replicationArrayByTableName.get(tableName.toLowerCase());
	}
	
	/**
	 * Carga inicial en variable replicationArrayByTable usada para cache
	 */
	protected static void loadReplicationArrayByTable(String trxName)
	{
		replicationArrayByTable = new HashMap<Integer, String>();
		replicationArrayByTableName = new HashMap<String, String>();
		try
		{
			// Obtener la nomina completa por tabla
			PreparedStatement pstmt = DB.prepareStatement( " SELECT tr.AD_Table_ID, tr.replicationArray, lower(t.tablename) FROM AD_TableReplication tr INNER JOIN AD_Table t ON t.AD_Table_ID = tr.AD_Table_ID ", trxName);
			ResultSet rs = pstmt.executeQuery();
		
			while (rs.next()) {
				replicationArrayByTable.put(rs.getInt(1), rs.getString(2));
				replicationArrayByTableName.put(rs.getString(3), rs.getString(2));
			}
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, " Error al recuperar replicationArray");
		}
	}

}
