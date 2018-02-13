package org.openXpertya.replication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashSet;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ReplicationTableManager {

	/** Modificador de nulls */
	protected static final String NULL_Y = "null=\"Y\"";
	
	/** Columna binaria */
	protected static final String BINARY_Y = "binary=\"Y\"";
	
	
	/** Modificador especial en sentencia avanzada de filtrado de tablas para incluir tablas de eliminaciones */
	public static final String DELETIONS_SQL_MODIFIER = "WITH DELETIONS";
	
	/** Tablas que contienen la columna reparray */
	protected static Vector<String> tablesForReplication = null;
	protected static String recordsForReplicationQuery = null;
	/** filtrar replicacion de una tabla especifica */
	public static String filterTable = null;
	/** filtro avanzado de tablas de replicación */
	public static String advancedFilterTable = null;
	/** filtrar replicacion de un registro especifico */
	public static String filterRecord = null;
	/** filtro avanzado de registros a replicar */
	public static String advancedFilterRecord = null;
	/** filtrar replicacion solo hacia un host */
	public static HashSet<Integer> filterHost = null;
	/** omitir registros con antiguedad menor al tiempo especificado (segundos) */
	public static Integer delayRecords = null;
	
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
		// Recuperar todos los registros a replicar (inlcuir el limite indicado por parametro si el mismo es mayor a cero)
		// Esta parte queda fuera del query cacheado debido a que en distintas ejecuciones el parametro puede variar
		String limitRecords = ReplicationConstants.REPLICATION_SOURCE_MAX_RECORDS > 0 ? " LIMIT " + ReplicationConstants.REPLICATION_SOURCE_MAX_RECORDS : "";
		pstmt = DB.prepareStatement(getRecordsForReplicationQuery() + limitRecords, trxName, true);
		rs = pstmt.executeQuery();	
	}
	
	
	/**
	 * Retorna de a un registro, los registros que deben ser replicados 
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
			String binary = "";
			for (int i = 1; i <= colCount; i++ )
			{
				binary = "";
				// se omiten columnas especiales
				columnName = recordMetaData.getColumnName(i).toLowerCase();
				if (ReplicationConstants.COLUMN_RETRIEVEUID.equalsIgnoreCase(columnName) ||
					ReplicationConstants.COLUMN_REPARRAY.equalsIgnoreCase(columnName))
					continue;
				
				// Si es null, setear de manera acorde
				if (recordRS.getObject(i) == null) {
					columnValue = "";
					nullValue = NULL_Y;
				}
				else
				{
					// Si la columna es de tipo binaria, a la información a replicar se le aplica un encoding Base64
					// a fin de que los datos sean incluidos en el XML correctamente.  Luego se hace un decode en el destino
					// Notar que estos datos viajaran en el newValue del ChangeLogElement, no en el BinaryValue
					if (recordMetaData.getColumnType(i) == java.sql.Types.BINARY) {
						columnValue = Base64.encodeBase64String((byte[])recordRS.getObject(i));
						binary = BINARY_Y;
					} else {
						columnValue = recordRS.getObject(i).toString()
																.replaceAll("&",  "&#x26;amp;")								
																.replaceAll("<",  "&#x26;#x3C;")
																.replaceAll(">",  "&#x26;#x3E;")
																.replaceAll("\"", "&#x26;#x22;")
																.replaceAll("'",  "&#x26;#x27;");
					}
					nullValue = "";
				}

				// obtener el ID de la columna, si devuelve -1, entonces omitir esta columna, ya que 
				// se encuentra fuera del conjunto de columnas pertenecientes al diccionario de datos.
				columnId = ReplicationCache.columnIDs.get(rs.getString("tablename").toLowerCase()+"_"+recordMetaData.getColumnName(i).toLowerCase());
				if (null == columnId || -1 == columnId)
					continue;

				// Armar la parte del XML
				recordToReplicate.append("<column id=\"").append(columnId).append("\" value=\"")
									.append(columnValue).append("\" ").append(nullValue).append(" ").append(binary).append("/>");
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
			return " SELECT ad_table_id, ad_org_id " + queryTail;
		return " SELECT * " + queryTail;

	}
	
	/**
	 * Retorna la nomina de tablas que estan marcadas para replicación
	 */
	public static Vector<String> getTablesForReplication(String trxName) throws Exception
	{
		if (tablesForReplication == null)
		{
			tablesForReplication = new Vector<String>();
			boolean includeDeletions = false;
			
			// Debe incluirse la tabla de eliminaciones?
			if (advancedFilterTable==null && (filterTable==null || ReplicationConstants.DELETIONS_TABLE.equalsIgnoreCase(filterTable))) {
				includeDeletions = true;
			} else if (advancedFilterTable!=null && advancedFilterTable.toLowerCase().contains(DELETIONS_SQL_MODIFIER.toLowerCase())) {
				includeDeletions = true;
				advancedFilterTable = advancedFilterTable.toLowerCase().replace(DELETIONS_SQL_MODIFIER.toLowerCase(), "");
			}
			
			// Recuperar las tablas que: 1) Tienen incorporado el reparray como una columna  Y  2) Son tablas configuradas para replicacion
			//							 incluyendo ademas la tabla de eliminaciones
			String query =  " SELECT table_name " + 
							" FROM information_schema.columns " + 
							" WHERE lower(column_name) = 'reparray' " + 
							" AND lower(table_name) in ( " +
							" 	SELECT lower(tablename) " +
							" 	FROM ad_tablereplication tr " +
							" 	INNER JOIN ad_table t ON tr.ad_table_id = t.ad_table_id " +
							" 	WHERE replicationarray SIMILAR TO ('%" + ReplicationConstants.REPLICATION_CONFIGURATION_SEND + "%|%" + ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE + "%') " +
							" 	AND tr.AD_Client_ID = " + Env.getContext(Env.getCtx(), "#AD_Client_ID") +
							// Incluir eventual filtro por nombre de tabla
							(filterTable!=null&&filterTable.length()>0?"  AND LOWER(t.tablename) = '"+filterTable.toLowerCase()+"'" : "") +
							// Incluir eventual filtro de tabla avanzado
							(advancedFilterTable!=null&&advancedFilterTable.length()>0?("  AND ("+advancedFilterTable.toLowerCase()+")"): "") +
							" ) ";
			if (includeDeletions)
				query += " UNION SELECT '" + ReplicationConstants.DELETIONS_TABLE + "' AS table_name ";
			
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
			// Obtengo las tuplas a replicar. Todos los registros con marcas de replicacion (Inserciones, Modificaciones o Eliminaciones).  
			StringBuffer query = new StringBuffer(" SELECT * FROM ( ");
			for (String aTable : getTablesForReplication(trxName))
			{
				query.append(" SELECT '").append(aTable).append("' as tablename, retrieveUID, reparray, created ");
				query.append(" FROM ").append(aTable);
				query.append(" WHERE ( ");
				query.append(" 		   ").append(ReplicationConstants.COLUMN_INCLUDEINREPLICATION).append(" = 'Y' ");
				// SOLO JMS: incluir registros por timeout sin ack.  si no recibo ack luego de un tiempo, reenviarlos (solo en caso de estar definido el parametro)
				if (ReplicationConstants.ACK_TIME_OUT != null )
				{
					// reenviar TODOS los registros dentro del período especificado? incluso los ya confirmados?
					if (ReplicationConstants.RESEND_ALL_RECORDS)
						query.append(" 	OR ( (reparray similar to ('").append(getReplicationStates(false)).append("') ) ");
					else
						query.append(" 	OR ( (reparray similar to ('%").append(ReplicationConstants.REPARRAY_ACK_WAITING).append("%|%").append(ReplicationConstants.REPARRAY_REPLICATE_AFTER_ACK).append("%') ) ");
					// limitar al periodo especificado (registros cuya fecha de envioJMS supere el ACK_TIME_OUT indicado)
					query.append("		AND NOW() - " + ReplicationConstants.COLUMN_DATELASTSENT +  "  > '" + ReplicationConstants.ACK_TIME_OUT + "') 	");
				}
				query.append(" 		) ");				
				query.append(" AND (AD_Client_ID = 0 OR AD_Client_ID = " + Env.getContext(Env.getCtx(), "#AD_Client_ID") + ")" );
				// Incluir eventual filtro por nombre de registro
				if (filterRecord!=null && filterRecord.length()>0)
					query.append(" AND retrieveUID = '" + filterRecord + "'");
				if (advancedFilterRecord!=null && advancedFilterRecord.length()>0) 
					query.append(" AND (").append(advancedFilterRecord).append(")");
				// Contemplar los registros con cierta antiguedad segun el argumento recibido (segundos). LAS TABLAS INVOLUCRADAS DEBEN CONTENER ESTE CAMPO!
				if (delayRecords != null) {
					query.append(" AND age(NOW(), created) > '" + delayRecords + " seconds' ");
				}
				query.append(" UNION ALL ");
			}
			// Finalizacion del query
			query.append(" SELECT NULL::varchar as tablename, NULL::varchar as retrieveuid, NULL::varchar as reparray, NULL::timestamp as created "); // por el ultimo union...
			query.append(" ) AS foo WHERE tablename IS NOT NULL ORDER BY CREATED ");																  // (se ignora en este where)
			
			recordsForReplicationQuery = query.toString();
		}
		return recordsForReplicationQuery;
	}
	
	/** Si hay cambios en parametros, las caches y filtros deberán invalidarse */
	public static void invalidateCache()
	{
		tablesForReplication = null;
		recordsForReplicationQuery = null;
		filterTable = null;
		filterRecord = null;
		filterHost = null;
		delayRecords = null;
	}
	
	/**
	 * Carga como parte del query los posibles estados basicos para replicación
	 * Ejemplo: (1|3|A|B...| )
	 * @param standardRepStates si es true devuelve los estados tradicinoales de replicacion
	 * 							(ignorando los de replicado, por timeout y los de fin de reintentos)
	 * 							si es false devuelve los estados no tradicionals de replicación
	 * 							(los de replicado, por timeout y los de fin de reintentos) 
	 */
	StringBuffer standardRepStateList = null;
	StringBuffer extrasRepStatesList = null;
	protected StringBuffer getReplicationStates(boolean standardRepStates)
	{
		/* Caso por estados tradicionales */
		if (standardRepStates) {
			// Usar cache
			if (standardRepStateList == null) {
				standardRepStateList = new StringBuffer();
				for (Character aRepState : ReplicationConstants.replicateStates)
					standardRepStateList.append("%").append(aRepState).append("%|");
			}
			return standardRepStateList;
		}

		/* Caso por estados extras  */
		// Usar cache
		if (extrasRepStatesList == null) {
			extrasRepStatesList = new StringBuffer();
			for (Character aRepState : ReplicationConstants.timeOutStates)
				extrasRepStatesList.append("%").append(aRepState).append("%|");
			extrasRepStatesList.append("%").append(ReplicationConstants.REPARRAY_REPLICATED).append("%|");
			extrasRepStatesList.append("%").append(ReplicationConstants.REPARRAY_NO_RETRY).append("%|");
			extrasRepStatesList.append("%").append(ReplicationConstants.REPARRAY_REPLICATE_NO_RETRY).append("%|");
		}
		return extrasRepStatesList;
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
