package org.openXpertya.replication;

/**
 * Redefinición de ChangelogXMLBuilder para la generación 
 * de los archivos XML definitivos para su replicación, 
 * a partir de la información en AD_Changelog_Replication 
 * 
 * @author fcristina
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.plugin.install.ChangeLogGroup;
import org.openXpertya.plugin.install.ChangeLogXMLBuilder;
import org.openXpertya.util.DB;


public class ReplicationBuilder extends ChangeLogXMLBuilder {

	public static final String UID_REFERENCE_PREFIX = "UID=";
	
	public static final int MAX_LOG_RECORDS = 5000;
	
	/** Posicion del replicationArray a exportar para replicación */
	protected int m_replicationArrayPos = -1;
	protected int m_AD_Org_Target_ID = -1;
	
	/** Primer entrada en el changelog a buscar*/
	protected int initial_changelog_replication_id = -1;
	
	/** Ultima entrada en el changelog a buscar*/
	protected int final_changelog_replication_id = -1;
	
	/** XML a enviar al destinatario */
	protected StringBuffer m_replicationXMLData = null; 
	
	/** Encargado de recuperar las entradas a replicar */
	ChangeLogGroupListReplication groupList = null;
	
	
	/** Datos actuales de todas las tablas en base de datos */
	private static HashMap<Integer, String> tablesData = null;
	private static HashMap<Integer, Integer> referencesData = null;
	private static HashSet<String> tablesWithRetrieveUID = null;
	private static HashMap<String, Integer> map_RepArrayPos_OrgID = null;
	
	
	/**
	 * Carga inicial de todas las entradas relacionadas con las columnas
	 * De esta manera evitamos realizar consultas SQL durante 
	 * cada invocación al constructor ChangeLogElement 
	 * @return
	 */
	public static void loadCacheData(String trxName) throws Exception
	{
		/* Tablas: identificador y nombre */
		if (tablesData == null)	{
			tablesData = new HashMap<Integer, String>();
			String sql = new String( " SELECT ad_table_id, tablename FROM ad_table ");
			PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
				tablesData.put(rs.getInt(1), rs.getString(2));
			rs.close();
			rs =null;
			pstmt = null;
		}
		
		if (referencesData == null)	{
			/* Referencias: identificador de referencia y tabla a la cual apunta  */
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
		
		/* InformationSchema: tablas con columna retrieveuid */
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

		/* Relacion entre la posicion del una organizacion en el replicationArrayPos y el AD_Org_ID */
		if (map_RepArrayPos_OrgID == null)	{
			map_RepArrayPos_OrgID = new HashMap<String, Integer>();
			String sql = new String (	" SELECT COALESCE(o.retrieveuid::varchar, rh.ad_org_id::varchar) as org, rh.replicationarraypos " +
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

		
	}

	/** 
	 * Libera los recursos utilizados
	 */
	public static void freeCacheData()
	{
		tablesData = null;
		referencesData = null;
		tablesWithRetrieveUID = null;
		map_RepArrayPos_OrgID = null;
		System.gc();
	}
	
	
	/**
	 * Constructor especifico para el Builder de Replicacion
	 * @param replicationArrayPos Indica la posicion dentro de replicationArray
	 * 							  Se filtrará el AD_Changelog_Replication, según este criterio (utilizado para particionar el changelog en partes a fin de comprimir cada una de estas)
	 */
	public ReplicationBuilder(int replicationArrayPos, int initialChangelogReplicationID, int finalChangelogReplicationID, String trxName) {
		/* No es necesario especificar archivo destino o componentVersion */
		super(replicationArrayPos, trxName);
		m_replicationArrayPos = replicationArrayPos;
		initial_changelog_replication_id = initialChangelogReplicationID;
		final_changelog_replication_id = finalChangelogReplicationID;
	}

	/** Overrides varios */
	protected Integer getTableSchemaID() {		return null;	}
	protected void initIgnoreColumns(){}
	protected void initComponentVersion(Integer componentVersionID){}
	

	/** En este caso no se guarda el documento en archivo,
	 *  sino que se almacena el String resultante a fin de 
	 *  enviarlo al host correspondiente para su replicación
	 */
	protected void saveDocument() throws Exception{
		// YA SE CUENTA CON EL STRINGBUFFER RESULTANTE
//        StringWriter stw = new StringWriter();
//        Transformer serializer = TransformerFactory.newInstance().newTransformer();
//        serializer.transform(new DOMSource(getDoc()), new StreamResult(stw));
//        m_replicationXMLData = stw.toString(); 
	}
	
	
	
	
	/**
	 * Redefinicion específica para replicación
	 * 
	 * Dado el gran volumen de elementos a generar, 
	 * en lugar de generar un DOM-XML para luego 
	 * convertirlo a String, se opta directamente 
	 * por generar el String mediante concatenación
	 */
	@Override
	protected void fillDocument() throws Exception {
						
		/* Variables para la generacion del groupList */
		m_replicationXMLData = new StringBuffer("");
		groupList = new ChangeLogGroupListReplication();
		String newValue;
		boolean isTableReference;
		boolean useRetrieveUID = false;
		String retrieveUIDValue = "";
		String tableName = "";
		boolean tableHasRetrieveUID;
		int tableID = -1;
		
		/* Recuperar la OrgID de destino para la posicion dada */
		m_AD_Org_Target_ID = MReplicationHost.getReplicationOrgForPosition(m_replicationArrayPos, trxName);
		
		/* Cargar el listado de grupos (cada tupla de AD_Changelog_Replication es un groupList) */
		groupList.fillList(m_replicationArrayPos, initial_changelog_replication_id, final_changelog_replication_id, trxName);

		/* Contenido inicial del XML */
		m_replicationXMLData.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><changelog>");
		
		/* Por cada grupo... */
		int i = 0;
		for (ChangeLogGroup group : groupList.getGroups()) {
			// Creo el nodo elemento del grupo			
			m_replicationXMLData.append("<changegroup");
			
			// Creo, seteo el valor de los nodos atributos y asocio con el nodo padre
			m_replicationXMLData.append(" tableName=\"").append(group.getTableName()).append("\"");
			m_replicationXMLData.append(" uid=\"").append(group.getAd_componentObjectUID()).append("\"");  // <- el uid es usado para el recordUID (retrieveUID)
			m_replicationXMLData.append(" operation=\"").append(group.getOperation()).append("\"");
			
			m_replicationXMLData.append(">");
			
			// Si es eliminación va un tag vacío, 
			// sino se deben crear tags para cada columna  
			if(!group.getOperation().equals(MChangeLog.OPERATIONTYPE_Deletion)){
				// Itero por los elementos del grupo
				for (ChangeLogElement element : group.getElements()) {
					// Creo el nodo elemento para la columna
					m_replicationXMLData.append("<column");
					m_replicationXMLData.append(" name=\"").append(element.getColumnName()).append("\"");
					m_replicationXMLData.append(" type=\"").append(element.getAD_Reference_ID()).append("\"");
					
					// Si es de tipo referencia a una tabla o de tipo Entero con _ID, 
					// coloco los atributos específicos
					isTableReference = isTableReference(element);
					useRetrieveUID = false;
					retrieveUIDValue = "";
					if(isTableReference && element.getNewValue() != null && element.getNewValue().toString().length() > 0 
							&& !getIgnoresReferenceColumns().contains(element.getColumnName())){
						// Determinar el nombre de la tabla de referencia
						tableName = element.getColumnName();
						if(element.getColumnName().toUpperCase().endsWith("_ID")){
							tableName = element.getColumnName().substring(0,element.getColumnName().lastIndexOf("_"));
						}
						// Si tiene una referencia seteada en la columna, entonces busco ahí la tabla
						if(element.getAD_Reference_Value_ID() != 0){
							tableID = referencesData.get(element.getAD_Reference_Value_ID()); // DB.getSQLValue(null, "SELECT ad_table_id FROM ad_ref_table WHERE ad_reference_id = ?", element.getAD_Reference_Value_ID());
							tableName = tablesData.get(tableID);  // DB.getSQLValueString(null, "SELECT tablename FROM ad_table WHERE ad_table_id = ? LIMIT 1", tableID);
						}
						
						/**
						 *  Si la referencia posee un retrieveUID, entonces se deberá utilizar éste en lugar del ID local
						 */
						// La tabla referenciada tiene campo retrieveUID?
						tableHasRetrieveUID = tablesWithRetrieveUID.contains(tableName.toLowerCase()); // (1 == DB.getSQLValue(null, "SELECT count(1) FROM information_schema.columns WHERE table_name = '" + tableName.toLowerCase() + "' AND column_name = 'retrieveuid'"));
						if (tableHasRetrieveUID)
						{
							// El registro tiene seteado el retrieveUID? Si no lo tiene, es una refTable tradicional
							retrieveUIDValue = DB.getSQLValueString(trxName, " SELECT retrieveUID FROM " + tableName + " WHERE " + tableName + "_ID = " + element.getNewValue() + " AND 1 = ?", 1 );
							if (retrieveUIDValue != null && retrieveUIDValue.length() > 0)
								useRetrieveUID = true;
						}
						// incorporar la referencia a la tabla correspondiente
						m_replicationXMLData.append(" refTable=\"").append(tableName).append("\"");
					}
					
					// cierre de tag inicial de la columna
					m_replicationXMLData.append(">");
					
					// Textos del nodo, old y new values
				    newValue = useRetrieveUID ? (UID_REFERENCE_PREFIX+retrieveUIDValue) : String.valueOf(element.getNewValue());
					if(element.getBinaryValue() != null){
						/** TODO: VER QUE HACER ACA CON LOS BINARIOS EN REPLICACIÓN! */
					}
					// En el AD_Org_ID en realidad no se envia el AD_Org_ID sino el host asociado (replicationArrayPos) cargado, 
					// dado que este es el único valor en común.  Para AD_Org_ID = 0, pasamos directamente ese valor sin mapear 
					if ("AD_Org_ID".equalsIgnoreCase(element.getColumnName()) && !"UID=o0_0".equals(newValue))
					{
						Integer orgPos = map_RepArrayPos_OrgID.get(newValue.replace("UID=", ""));
						if (orgPos == null)
							throw new Exception ("No hay mapeo posible para la organización " + newValue + " en la tabla de hosts de replicación ");
						newValue = orgPos.toString();
					}
					// Agrego los nodos texto al nodo newValue
					m_replicationXMLData.append("<newValue>").append(newValue).append("</newValue>");
					// Cierre de columna
					m_replicationXMLData.append("</column>");
				}
			}

			// Cierre del changegroup
			m_replicationXMLData.append("</changegroup>");
			
			// Limpiar memoria cada cierto intervalo de iteraciones
			if (i++ % 1000 == 0)
				System.gc();
		}

		// Cierre del changelog
		m_replicationXMLData.append("</changelog>");
		
		/* Si no hay entradas, entonces vaciar el StringBuffer */
		if (i==0) 
			m_replicationXMLData = null;
	}

	public String getM_replicationXMLData() {
		return m_replicationXMLData.toString();
	}
	
	public int getM_replicationXMLDataLength() {
		return m_replicationXMLData.length();
	}
	
	public void emptyM_replicationXMLData() {
		m_replicationXMLData.delete(0, m_replicationXMLData.length());
		m_replicationXMLData = null;
		groupList.getGroups().clear();
		
	}
	
	public boolean hasReplicationData() {
		return m_replicationXMLData != null && m_replicationXMLData.length()>0;
	}

	public ChangeLogGroupListReplication getGroupList() {
		return groupList;
	}
	
	public byte[] getCompressedXML() throws Exception
	{
		return ReplicationUtils.compressString(m_replicationXMLData.toString());
	}

	public void setInitial_changelog_replication_id(
			int initialChangelogReplicationId) {
		initial_changelog_replication_id = initialChangelogReplicationId;
	}

}
