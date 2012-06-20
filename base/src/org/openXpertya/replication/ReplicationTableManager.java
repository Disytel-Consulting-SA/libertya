package org.openXpertya.replication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Vector;

import org.openXpertya.model.M_Column;
import org.openXpertya.process.CreateReplicationTriggerProcess;
import org.openXpertya.util.DB;

public class ReplicationTableManager {

	/** Modificador de nulls */
	protected static final String NULL_Y = "null=\"Y\"";
	
	/** Tablas que contienen la columna reparray */
	protected static Vector<String> tablesForReplication = null;
	protected static String recordsForReplicationQuery = null;
	
	/** trxName */
	protected String trxName = null;
	
	/** El valor de retorno con el rowset de registros a replicar.  
	 * 	Aalmacenará un set de columnas correspondiente a un registro a replicar 
	 *  	<columns><column="C_BPartner_ID" value="1000185"><column="AD_Client_ID" value="1010016"></columns> */
	protected StringBuffer recordToReplicate = null;
	
	/** Main PreparedStatement y ResultSet */
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	/** Datos y metadatos de recuperacion de un registro a replicar */
	PreparedStatement recordPS = null;
	ResultSet recordRS = null;
	String tableName = null;
	String repArray = null;
	String retrieveUID = null;
	Integer tableID = null;
	boolean isDeletionAction = false;
	
	
	public ReplicationTableManager(String trxName)
	{
		this.trxName = trxName;
	}
	
	/**
	 *	Ejecuta la consulta principal
	 */
	public void evaluateChanges() throws Exception
	{
		// Recuperar todos los registros a replicar
		pstmt = DB.prepareStatement(getRecordsForReplicationQuery().toString(), trxName, true);
		rs = pstmt.executeQuery();	
	}
	
	
	/**
	 * Retorn de a un registro, los registros que deben ser replicados 
	 * (estado 1 o 3 del reparray).  
	 * Retorna true si hay un nuevo registro a replicar o false en caso contrario
	 */
	protected boolean getNextChange() throws Exception
	{
		// garantizar que rs no sea null
		if (rs == null)
			evaluateChanges();
		
		recordToReplicate = new StringBuffer();
		// Recuperar un registro a replicar y generar la estructura basica que
		// es utilizada como input de entrada para el ChangeLogGroupListReplication
		if (rs.next())
		{
			// Por cada registro, se genera en memoria la información de todas las columnas del registro
			// Esto luego servirá como entrada para ReplicationBuilder y ChangelogGroupListReplication.  
			recordPS = DB.prepareStatement(getPSForRecord(rs), trxName, true);
			recordRS = recordPS.executeQuery();
			if (!recordRS.next())
				return true;	// devuelve true porque respeta el rs.next(), aunque no exista el retrieveUID (igualmente recordToReplicate tendra longitud = 0)
			
			ResultSetMetaData recordMetaData = recordRS.getMetaData();
			int colCount = recordMetaData.getColumnCount();
			String columnName;
			Integer columnId;
			String columnValue;
			String nullValue = "";
			for (int i = 1; i <= colCount; i++ )
			{
				// se omiten columnas especiales
				columnName = recordMetaData.getColumnName(i).toLowerCase();
				if (CreateReplicationTriggerProcess.COLUMN_RETRIEVEUID.equalsIgnoreCase(columnName) ||
					CreateReplicationTriggerProcess.COLUMN_REPARRAY.equalsIgnoreCase(columnName))
					continue;
				
				// Si es null, setear de manera acorde
				if (recordRS.getObject(i) == null) {
					columnValue = "";
					nullValue = NULL_Y;
				}
				else
				{
					columnValue = recordRS.getObject(i).toString();
					nullValue = "";
				}

				// obtener el ID de la columna, si devuelve -1, entonces omitir esta columna, ya que 
				// se encuentra fuera del conjunto de columnas pertenecientes al diccionario de datos.
				columnId = ReplicationCache.columnIDs.get(rs.getString("tablename").toLowerCase()+"_"+recordMetaData.getColumnName(i).toLowerCase());
				if (null == columnId || -1 == columnId)
					continue;

				// Armar la parte del XML
				recordToReplicate.append("<column id=\"").append(columnId).append("\" value=\"")
									.append(columnValue).append("\" ").append(nullValue).append("/>");
			}
			// Si la tabla es de eliminacion, tomar la referencia a la tabla del registro a eliminar, dentro del contenido de recordRS
			tableName = (ReplicationConstants.DELETIONS_TABLE.equalsIgnoreCase(rs.getString("tablename"))) ?
							ReplicationCache.tablesData.get(recordRS.getInt("ad_table_id")) : 
							rs.getString("tablename");
			repArray = rs.getString("repArray");
			retrieveUID = rs.getString("retrieveUID");
			tableID = ReplicationCache.tablesIDs.get(tableName.toLowerCase());
			isDeletionAction = (ReplicationConstants.DELETIONS_TABLE.equalsIgnoreCase(rs.getString("tablename")));
			return true;
		}
		return false;
		
	}
	
	/**
	 *	Query para obtener las columnas a replicar (o la tabla donde hay que eliminar el registro) 
	 */
	protected String getPSForRecord(ResultSet rs) throws Exception
	{
		String queryTail = " FROM " + rs.getString("tablename") + " WHERE retrieveUID = '" + rs.getString("retrieveUID")+ "'";
		// Si estoy tomando un registro de la tabla de eliminaciones, entonces solo obtener la columna que apunta a la tabla donde hay que eliminar el registro
		if (ReplicationConstants.DELETIONS_TABLE.equalsIgnoreCase(rs.getString("tablename")))
			return " SELECT ad_table_id " + queryTail;
		return " SELECT * " + queryTail;

	}
	
	/**
	 * Retorna la nomina de tablas que estan marcadas para replicación
	 */
	protected Vector<String> getTablesForReplication() throws Exception
	{
		if (tablesForReplication == null)
		{
			tablesForReplication = new Vector<String>();

			String query = " select table_name from information_schema.columns " + 
						   " where  lower(column_name) = 'reparray' " +
						   " and table_schema = 'libertya' ";
			
			PreparedStatement pstmt = DB.prepareStatement(query, trxName, true);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				tablesForReplication.add(rs.getString("table_name"));
		}
		
		return tablesForReplication;
	}
	
	/**
	 * Retorna el query principal que permite obtener los registros a replicar
	 */
	protected String getRecordsForReplicationQuery() throws Exception
	{
		if (recordsForReplicationQuery == null)
		{
			// Obtengo las tuplas a replicar (Inserciones o Modificaciones)
			StringBuffer query = new StringBuffer(" SELECT * FROM ( ");
			for (String aTable : getTablesForReplication())
			{
				query.append(" SELECT '").append(aTable).append("' as tablename, retrieveUID, reparray, created ");
				query.append(" FROM ").append(aTable);
				query.append(" WHERE ( reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_INSERT +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION 		+"%' ");
				query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY1 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY2 	+"%' ");
				query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY3 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY4 	+"%' ");
				query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY5 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY6 	+"%' ");
				query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY7 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY8 	+"%' ");
				query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY9 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY10 +"%' ");
				// incluir registros por timeout sin ack.  si no recibo ack luego de un tiempo, reenviarlos (solo en caso de estar definido)
				if (ReplicationConstants.ACK_TIME_OUT != null )
					query.append(" 	OR ((reparray ilike '%"+ReplicationConstants.REPARRAY_ACK_WAITING+"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_ACK +"%') ")
						 .append("		AND NOW() - " + CreateReplicationTriggerProcess.COLUMN_DATELASTSENT +  "  > '" + ReplicationConstants.ACK_TIME_OUT + "') 	");
				query.append(" 		) ");				
				query.append(" AND AD_Client_ID = (SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thishost = 'Y') ");
				query.append(" UNION ");
			}
			// Eliminaciones
			query.append(" SELECT '"+ReplicationConstants.DELETIONS_TABLE+"' as tablename, retrieveUID, reparray, created FROM " + ReplicationConstants.DELETIONS_TABLE);
			query.append(" WHERE ( reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_INSERT +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_MODIFICATION 		+"%' ");
			query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY1 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY2 	+"%' ");
			query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY3 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY4 	+"%' ");
			query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY5 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY6 	+"%' ");
			query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY7 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY8 	+"%' ");
			query.append(" 		OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY9 +"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_RETRY10 +"%' ");
			// incluir registros por timeout sin ack.  si no recibo ack luego de un tiempo, reenviarlos (solo en caso de estar definido)
			if (ReplicationConstants.ACK_TIME_OUT != null )
				query.append(" 	OR ((reparray ilike '%"+ReplicationConstants.REPARRAY_ACK_WAITING+"%' OR reparray ilike '%"+ReplicationConstants.REPARRAY_REPLICATE_AFTER_ACK +"%') ")
				 	 .append("		AND NOW() - " + CreateReplicationTriggerProcess.COLUMN_DATELASTSENT + " > '" + ReplicationConstants.ACK_TIME_OUT + "') ");
			query.append(" 		) ");			
			query.append(" AND AD_Client_ID = (SELECT AD_Client_ID FROM AD_ReplicationHost WHERE thishost = 'Y') ");			
			query.append(" UNION ");
			// Finalizacion del query
			query.append(" SELECT NULL, NULL, NULL, NULL ");							// por el ultimo union...
			query.append(" ) AS foo WHERE tablename IS NOT NULL ORDER BY CREATED ");	// (se ignora en este where)
			
			recordsForReplicationQuery = query.toString();
		}
		return recordsForReplicationQuery;
	}
	
	/**
	 * Liberar memoria
	 */
	public void finalize() throws Exception
	{
		pstmt.close();
		rs.close();
		pstmt = null;
		rs = null;
	}

	public String getColumnValuesForReplication()
	{
		return recordToReplicate.toString();
	}
	
	public String getCurrentRecordTableName()
	{
		return tableName;
	}
	
	public String getCurrentRecordRetrieveUID()
	{
		return retrieveUID;		
	}
	
	public String getCurrentRecordRepArray()
	{
		return repArray;
	}
	
	public int getCurrentRecordTableID()
	{
		return tableID;	
	}

	public boolean isDeletionAction() {
		return isDeletionAction;
	}
	

}
