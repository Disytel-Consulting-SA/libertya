package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;



public class MTableReplication extends X_AD_TableReplication {

	/** Cache de replicationArray según AD_TableReplication */
	protected static HashMap<Integer, String> replicationArrayByTable = null;
	
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
	 * Dadas una tabla y una sucursal en especial, 
	 * retorna el replicationArray correspondiente
	 */
	public static String getReplicationArray(int tableID, String trxName)
	{
		if (replicationArrayByTable == null)
			loadReplicationArrayByTable(trxName);

		return replicationArrayByTable.get(tableID);
	}
	
	/**
	 * Carga inicial en variable replicationArrayByTable usada para cache
	 */
	protected static void loadReplicationArrayByTable(String trxName)
	{
		replicationArrayByTable = new HashMap<Integer, String>();
		try
		{
			// Obtener la nomina completa por tabla. SE SUPONE QUE LAS ENTRADAS EXISTENTES SON DEL HOST ACTUAL (SUCURSAL)
			PreparedStatement pstmt = DB.prepareStatement( " SELECT AD_Table_ID, replicationArray FROM AD_TableReplication ", trxName);
			ResultSet rs = pstmt.executeQuery();
		
			while (rs.next())
				replicationArrayByTable.put(rs.getInt(1), rs.getString(2));
		}
		catch (Exception e)
		{
			s_log.log(Level.SEVERE, " Error al recuperar replicationArray");
		}
	}

}
