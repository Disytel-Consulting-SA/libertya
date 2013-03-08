package org.openXpertya.replication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.DB;

public class ReplicationCache {

	
	/**
	 * Cache de consultas frecuentemente accedidas durante
	 * el proceso de replicación de datos, ya sea dentro
	 * del proceso de generación como del de parseo y copia
	 */
	
	/* Cache de tablas.  Map: ID-Nombre */
	public static HashMap<Integer, String> tablesData = null;
	/* Cache de tablas.  Map: Nombre(lower)-ID */
	public static HashMap<String, Integer> tablesIDs = null;
	/* Referencias: identificador de referencia y tabla a la cual apunta  */
	public static HashMap<Integer, Integer> referencesData = null;
	/* InformationSchema: tablas con columna retrieveuid */
	public static HashSet<String> tablesWithRetrieveUID = null;
	/* InformationSchema: tablas con columna AD_ComponentObjectUID */
	public static HashSet<String> tablesWithComponentObjectUID = null;
	/* Relacion entre la posicion del una organizacion en el replicationArrayPos y el AD_Org_ID */
	public static HashMap<String, Integer> map_RepArrayPos_OrgID = null;
	/* Columnas.  Información gral. de las columnass */
	public static HashMap<Integer, Object[]> columnsData = null;
	/* Tablas: nombre de tabla y columnas clave */
	public static HashMap<String, String> keyColumns = null;
	/* Dado un repArrayPos, obtener el AD_Org_ID asociado */
	public static HashMap<Integer, Integer> map_RepArrayPos_OrgID_inv = null;
	/* Transaccion utilizada.  Para instalacion de plugins sera la especificada en PluginUtils. */
	public static String trxName = PluginUtils.getPluginInstallerTrxName();
	/* IDs de las columnas de cada tabla (el key del HashMap incluye NOMBRETABLA_NOMBRECOLUMNA */
	public static HashMap<String, Integer> columnIDs = null;
	/* Dependiendo si el framework de componentes/replicacion es usado en la instalacion de un plugin
	 * o en el uso de replicación, este valor deberá cambiar acordemente.  Para replicacion debera ser falso */
	public static boolean shouldReloadCache = true;
	/* En caso de mapear a otro componente, se almacenarán los mapeos de UIDs originales a los nuevos UIDs */
	public static HashMap<String, String> mappedUIDs = new HashMap<String, String>();
	
	
	static
	{
		loadCacheData();
	}
	
	/**
	 * Carga los valores en la cache
	 */
	public static void loadCacheData()
	{
		try
		{
			trxName = PluginUtils.getPluginInstallerTrxName();
			
			if (tablesData == null)	{
				tablesData = new HashMap<Integer, String>();
				tablesIDs = new HashMap<String, Integer>();
				String sql = new String( " SELECT ad_table_id, tablename FROM ad_table ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
				{
					tablesData.put(rs.getInt(1), rs.getString(2));
					tablesIDs.put(rs.getString(2).toLowerCase(), rs.getInt(1));
				}
				rs.close();
				rs =null;
				pstmt = null;
			}
			
			if (referencesData == null)	{
				referencesData = new HashMap<Integer, Integer>();
				String sql = new String( " SELECT ad_reference_id, ad_table_id FROM ad_ref_table  ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					referencesData.put(rs.getInt(1), rs.getInt(2));
				rs.close();
				rs =null;
				pstmt = null;
			}
			
			if (tablesWithRetrieveUID == null)	{
				tablesWithRetrieveUID = new HashSet<String>();
				String sql = new String (	" SELECT lower(t.table_name) " +
											" FROM information_schema.tables t " +
											" INNER JOIN information_schema.columns c ON t.table_name = c.table_name " +
											" WHERE t.table_schema = 'libertya' " + 
											" AND c.column_name = 'retrieveuid' ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					tablesWithRetrieveUID.add(rs.getString(1));
				rs.close();
				rs =null;
				pstmt = null;
			}
	
			if (tablesWithComponentObjectUID == null)	{
				tablesWithComponentObjectUID = new HashSet<String>();
				String sql = new String (	" SELECT lower(t.table_name) " +
											" FROM information_schema.tables t " +
											" INNER JOIN information_schema.columns c ON t.table_name = c.table_name " +
											" WHERE t.table_schema = 'libertya' " + 
											" AND c.column_name = 'ad_componentobjectuid' ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					tablesWithComponentObjectUID.add(rs.getString(1));
				rs.close();
				rs =null;
				pstmt = null;
			}			
			
			if (map_RepArrayPos_OrgID == null)	{
				map_RepArrayPos_OrgID = new HashMap<String, Integer>();
				String sql = new String (	" SELECT COALESCE(rh.ad_org_id::varchar) as org, rh.replicationarraypos " +
											" FROM AD_ReplicationHost rh " +
											" INNER JOIN ad_org o ON rh.ad_org_id  = o.ad_org_id " +
											" WHERE rh.AD_Client_ID = " +
											" (SELECT AD_CLIENT_ID FROM AD_ReplicationHost WHERE thisHost = 'Y') " );
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					map_RepArrayPos_OrgID.put(rs.getString(1), rs.getInt(2));
				rs.close();
				rs =null;
				pstmt = null;
			}
	
			if (columnsData == null) {
				columnsData = new HashMap<Integer, Object[]>();
				String sql = new String( " SELECT columnname, AD_Reference_ID, AD_Reference_Value_ID, isKey, ad_column_id FROM AD_Column ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
				{
					Object[] data =  {rs.getString(1), rs.getInt(2), rs.getInt(3), ("Y".equals(rs.getString(4))) };
					columnsData.put(rs.getInt(5), data);
				}
				rs.close();
				rs =null;
				pstmt = null;
			}
			
			if (keyColumns == null) {
				keyColumns = new HashMap<String, String>();
				String sql = new String( " SELECT lower(t.tablename), lower(c.columnname) " +
										 " FROM ad_table t " +
										 " INNER join ad_column c ON t.ad_table_id = c.ad_table_id " +
										 " WHERE c.iskey = 'Y'");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					keyColumns.put(rs.getString(1), rs.getString(2));
			}
			
			if (map_RepArrayPos_OrgID_inv == null)	{
				map_RepArrayPos_OrgID_inv = new HashMap<Integer, Integer>();
				String sql = new String (	" SELECT replicationarraypos, ad_org_id FROM AD_ReplicationHost WHERE AD_Client_ID = " +
											" (SELECT AD_CLIENT_ID FROM AD_ReplicationHost WHERE thisHost = 'Y') " );
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					map_RepArrayPos_OrgID_inv.put(rs.getInt(1), rs.getInt(2));
				rs.close();
				rs = null;
				pstmt = null;
			}
			
			if (columnIDs == null)
			{
				columnIDs = new HashMap<String,Integer>();
				String sql = new String ( " SELECT lower(t.tablename) || '_' || lower(c.columnname), ad_column_ID " +
		        						  " FROM AD_Column c, AD_Table t " +
		        						  " WHERE t.AD_Table_ID = c.AD_Table_ID ");
				PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())
					columnIDs.put(rs.getString(1) , rs.getInt(2));
				rs.close();
				rs = null;
				pstmt = null;

			}
					
		}
		catch (Exception e)
		{
			System.out.println("SEVERE: ReplicationCache exception. " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/** 
	 * Libera los recursos utilizados
	 */
	public static void freeCacheData()
	{
		tablesData = null;
		tablesIDs = null;
		referencesData = null;
		tablesWithRetrieveUID = null;
		tablesWithComponentObjectUID = null;
		map_RepArrayPos_OrgID = null;
		columnsData = null;
		keyColumns = null;
		map_RepArrayPos_OrgID_inv = null;
		System.gc();
	}
	
	/** 
	 * Refresca la cache
	 */
	public static void reloadCacheData()
	{
		if (shouldReloadCache)
		{	
			freeCacheData();
			loadCacheData();
		}
	}
	
}
