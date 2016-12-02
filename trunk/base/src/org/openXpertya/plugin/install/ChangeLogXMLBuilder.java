package org.openXpertya.plugin.install;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MComponent;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.w3c.tools.codec.Base64Encoder;

public abstract class ChangeLogXMLBuilder extends PluginXMLBuilder {

	// Variables de instancia
	
	/** Versión del componente */
	
	private MComponentVersion componentVersion;
	
	/** Componente */
	
	private MComponent component;
	
	/** Columnas de referencia ignoradas */
	
	private List<String> ignoresReferenceColumns;
	
	/** AD_ChangeLog_ID inicial */
	
	private Integer changeLogIDFrom = null;  
	
	/** AD_ChangeLog_ID fin */
	
	private Integer changeLogIDTo = null;
	
	/** Usuario registrado en registros del changelog */
	
	private Integer userID = null;

	/** First Changelog (primer changelog) */
	
	protected int firstChangelogID = -1;
	
	/** Last Changelog (ultimo changelog) */
	
	protected int lastChangelogID = -1;
	
	/** Checkear si la los metadatos en el changelog coincide los metadatos */
	protected boolean validateChangelogConsistency = false;
	
	/** Deshabilitar las entradas del changelog inconsistentes con los metadatos */
	protected boolean disableInconsistentChangelog = false;
	

	
	// Constructores
	public ChangeLogXMLBuilder(String trxName) {
		super(trxName);
		/* No hay columnas a ignorar */
		List<String> ignoreColumns = new ArrayList<String>();
		setIgnoresReferenceColumns(ignoreColumns);
	}
	
	
	public ChangeLogXMLBuilder(String path, String fileName, Integer componentVersionID, String trxName) {
		super(path, fileName, componentVersionID, trxName);
		initComponentVersion(componentVersionID);
		initIgnoreColumns();
	}

	
	public ChangeLogXMLBuilder(String path, String fileName, Integer componentVersionID, Integer changeLogIDFrom, Integer changeLogIDTo, Integer userID, String trxName, boolean validateChangelogConsistency, boolean disableInconsistentChangelog) {
		this(path, fileName, componentVersionID, trxName);
		setChangeLogIDFrom(changeLogIDFrom);
		setChangeLogIDTo(changeLogIDTo);
		setUserID(userID);
		setValidateChangelogConsistency(validateChangelogConsistency);
		setDisableInconsistentChangelog(disableInconsistentChangelog);
	}
	
	
	// Inicialización
	
	private void initComponentVersion(Integer componentVersionID){
		setComponentVersion(new MComponentVersion(Env.getCtx(),componentVersionID,trxName));
		setComponent(new MComponent(Env.getCtx(),getComponentVersion().getAD_Component_ID(), trxName));
	}
	
	private void initIgnoreColumns(){
		List<String> ignoreColumns = new ArrayList<String>();
		ignoreColumns.add("AD_Client_ID");
		ignoreColumns.add("AD_Org_ID");
		ignoreColumns.add("AD_User_ID");
		ignoreColumns.add("CreatedBy");
		ignoreColumns.add("UpdatedBy");
//		ignoreColumns.add("AD_Reference_ID");
		setIgnoresReferenceColumns(ignoreColumns);
	}
	
	@Override
	protected String getRootNodeName() {
		return "changelog";
	}
	
	@Override
	protected void fillDocument() throws Exception {
		// TODO: Filtrar client = 0 para metadatos
		ChangeLogGroupList groupList = new ChangeLogGroupList(getComponentVersion().getID(),getTableSchemaID(),getChangeLogIDFrom(),getChangeLogIDTo(),getUserID());
		groupList.fillList(trxName);
		Element groupNode, columnNode, oldValue, newValue;
		Text oldValueTextNode, newValueTextNode;
		boolean isTableReference;
		List<String> elementsUID;
		for (ChangeLogGroup group : groupList.getGroups()) {
			// Creo el nodo elemento del grupo			
			groupNode = createElement("changegroup");
			// Creo, seteo el valor de los nodos atributos y asocio con el nodo padre
			setAttribute("tableName", group.getTableName(), groupNode);
			setAttribute("uid", group.getAd_componentObjectUID(), groupNode);
			setAttribute("operation", group.getOperation(), groupNode);
			setAttribute("changelogGroupID", ""+group.getChangelogGroupID(), groupNode);			
			// Si es eliminación va un tag vacío, 
			// sino se deben crear tags para cada columna  
			if(!group.getOperation().equals(MChangeLog.OPERATIONTYPE_Deletion)){
				// Validar si efectivamente existe el registro.  Pueden darse casos en donde el changelog queda desfazado con respecto
				// a la actividad en metadatos.  Por ejemplo si se eliminó un AD_Tab, por definición de tabla, se realiza el CASCADE
				// de los AD_Fields relacionados al tab. Sin embargo, al changelog nunca es incorporada dicha información.  En caso
				// de que estemos en una inserción/modificación y el registro ya no existe, no tiene sentido alguno exportar esta información
				if (validateChangelogConsistency && 1 != DB.getSQLValue(null, "SELECT count(1) FROM " + group.getTableName() + " WHERE AD_ComponentObjectUID = '" + group.getAd_componentObjectUID() + "'")) {
					System.err.println("WARNING: Registro " + group.getAd_componentObjectUID() + " de tabla " + group.getTableName() + " no existe.  Se omite changelogGroupID " + group.getChangelogGroupID() + " en la exportacion.");
					if (disableInconsistentChangelog && 0 < DB.executeUpdate("UPDATE AD_Changelog SET isActive = 'N' WHERE changelogGroup_ID = " + group.getChangelogGroupID(), trxName)) {
						System.err.println("         El changelogGroupID " + group.getChangelogGroupID() + " ha sido deshabilitado de la bitacora debido a inconsistencias con los metadatos.");
					}
					continue;
				}
				// Itero por los elementos del grupo
				for (ChangeLogElement element : group.getElements()) {
					// Creo el nodo elemento para la columna
					columnNode = createElement("column");
					// Creo los nodos elemento con el oldValue y el newValue
					oldValue = createElement("oldValue");
					newValue = createElement("newValue");
					// Creo, seteo el valor de los nodos atributos y asocio con el nodo padre
					setAttribute("name", element.getColumnName(), columnNode);
					// Tipo de columna
					setAttribute("type", String.valueOf(element.getAD_Reference_ID()), columnNode);
					// Si es de tipo referencia a una tabla o de tipo Entero con _ID, 
					// coloco los atributos específicos
					isTableReference = isTableReference(element);
					if(isTableReference 
							&& !getIgnoresReferenceColumns().contains(element.getColumnName())){
						// Determinar el nombre de la tabla de referencia
						String uidReference = null;
						String tableName = element.getColumnName();
						String columnName = element.getColumnName();
						if(element.getColumnName().toUpperCase().endsWith("_ID")){
							tableName = element.getColumnName().substring(0,element.getColumnName().lastIndexOf("_"));
						}
						// Si tiene una referencia seteada la columna, 
						// entonces busco ahí la tabla
						if(element.getAD_Reference_Value_ID() != 0){
							String sql = "SELECT ad_table_id, ad_key FROM ad_ref_table WHERE ad_reference_id = ?";
							PreparedStatement ps = null;
							ResultSet rs = null;
							int tableID = 0, key = 0;
							try{
								ps = DB.prepareStatement(sql, trxName);
								ps.setInt(1, element.getAD_Reference_Value_ID());
								rs = ps.executeQuery();
								if(rs.next()){
									tableID = rs.getInt("ad_table_id");
									key = rs.getInt("ad_key");
								}
							} catch(Exception e){
								e.printStackTrace();
							} finally{
								try{
									if(ps != null)ps.close();
									if(rs != null)rs.close();
								} catch(Exception e){
									e.printStackTrace();
								}
							}
							sql = "SELECT tablename FROM ad_table WHERE ad_table_id = ? LIMIT 1";
							tableName = DB.getSQLValueString(trxName, sql, tableID);
							columnName = M_Column.getColumnName(Env.getCtx(), key);
						}
						// Si no existe el campo ad_componentobjectuid en esa tabla no referencio a nada
						if(existsColumn(tableName, "ad_componentObjectUID")){
							// Nombre de tabla de referencia en el XML
							setAttribute("refTable", tableName, columnNode);
							if(element.getNewValue() != null){
								// Obtener el uid correspondiente para el newValue
								String sql = "SELECT ad_componentobjectuid FROM "+tableName+" WHERE "+columnName+" = ?";
								PreparedStatement ps = null;
								ResultSet rs = null;
								Object value;
								Integer intValue;
								String strValue = String.valueOf(element.getNewValue());
								try{
									intValue = Integer.parseInt(strValue);
									value = intValue;
								} catch(NumberFormatException cce){
									value = strValue;
								}
								try{
									ps = DB.prepareStatement(sql, trxName);
									ps.setObject(1, value);
									rs = ps.executeQuery();
									if(rs.next()){
										uidReference = rs.getString("ad_componentobjectuid");
									}
								} catch(Exception e){
									e.printStackTrace();
								} finally{
									try{
										if(ps != null)ps.close();
										if(rs != null)rs.close();
									} catch(Exception e){
										e.printStackTrace();
									}
								}
								// uid reference 
								if(Util.isEmpty(uidReference)){
									elementsUID = new ArrayList<String>();
									elementsUID.add(getComponent().getPrefix());
									elementsUID.add(tableName);
									elementsUID.add(String.valueOf(element.getNewValue()));
									uidReference = PO.makeUID(elementsUID);
								}
								setAttribute("refUID", uidReference, newValue);
							}
						}
					}
					// Textos del nodo, old y new values
					oldValueTextNode = createTextNode(String.valueOf(element.getOldValue()));
				    newValueTextNode = createTextNode(String.valueOf(element.getNewValue()));
					if(element.getBinaryValue() != null){
						setAttribute("algorithm", "base64", columnNode);
					    System.out.println(element.getBinaryValue());
					    Base64Encoder encoder = new Base64Encoder(element.toString());
					    oldValueTextNode = createTextNode("null");
					    newValueTextNode = createTextNode(encoder.processString());
	//				    Base64Decoder decoder = new Base64Decoder(result);
	//				    String resultDecode = decoder.processString();
	//				    Base64Encoder encoder2 = new Base64Encoder(resultDecode);
	//				    String result2 = encoder2.processString();
	//				    System.out.println(result2);
	//				    byte[] binaryBytes = binary.getBytes("UTF8");
	//				    System.out.println(binaryBytes);
	//				    
	//				    String binaryNew = new String(binaryBytes,"UTF8");
	//				    System.out.println(binaryNew);
					}
					// Agrego los nodos texto a los nodos new y old
					addNode(oldValueTextNode, oldValue);
					addNode(newValueTextNode, newValue);
					// Agrego los nodos old y new al nodo de la columna
					addNode(oldValue, columnNode);
					addNode(newValue, columnNode);
					// Agrego el nodo columna al grupo
					addNode(columnNode, groupNode);
				}
			}
			// Agrego el nodo de grupo dentro del nodo root
			addNode(groupNode, getRootNode());
		}
		int groupListSize = groupList.getGroups().size();
		// Guardar primer changelog exportado
		if(groupListSize > 0){
			ChangeLogGroup firstGroup = groupList.getGroups().get(0);
			int elementSize = firstGroup.getElements().size();
			if(elementSize > 0) {
				ChangeLogElement firstElement = firstGroup.getElements().get(0); 
				firstChangelogID = firstElement.getAD_Changelog_ID();
			}
		}
		// Guardar ultimo changelog exportado
		if(groupListSize > 0){
			ChangeLogGroup lastGroup = groupList.getGroups().get(groupListSize - 1);
			int elementSize = lastGroup.getElements().size();
			if(elementSize > 0){
				ChangeLogElement lastElement = lastGroup.getElements().get(elementSize - 1); 
				lastChangelogID = lastElement.getAD_Changelog_ID();
			}
		}
	}
	
	/**
	 * @param tableName nombre de tabla
	 * @param columnName nombre de columna
	 * @return true si existe la columna con el nombre parámetro en la tabla parámetro
	 */
	protected boolean existsColumn(String tableName, String columnName){
		String sql = "SELECT COALESCE(count(*),0) AS exist " +
					 "FROM ad_table as t " +
					 "INNER JOIN ad_column as c on (t.ad_table_id = c.ad_table_id) " +
					 "WHERE upper(tablename) = upper(?) AND upper(columnname) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		int exist = 0;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			ps.setString(2, columnName);
			rs = ps.executeQuery();
			if(rs.next()){
				exist = rs.getInt("exist");
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return exist > 0;
	}
	
	
	/*
	 * -------------------------------------------------------------------
	 * 						Métodos abstractos
	 * -------------------------------------------------------------------
	 */
	
	/**
	 * Obtener el esquema de tablas de las subclases
	 */
	protected abstract Integer getTableSchemaID();

	/*
	 * -------------------------------------------------------------------
	 */
	
	// Getters y Setters

	private void setIgnoresReferenceColumns(List<String> ignoresReferenceColumns) {
		this.ignoresReferenceColumns = ignoresReferenceColumns;
	}

	protected List<String> getIgnoresReferenceColumns() {
		return ignoresReferenceColumns;
	}


	protected void setComponentVersion(MComponentVersion componentVersion) {
		this.componentVersion = componentVersion;
	}


	protected MComponentVersion getComponentVersion() {
		return componentVersion;
	}


	protected void setComponent(MComponent component) {
		this.component = component;
	}


	protected MComponent getComponent() {
		return component;
	}
	
	protected void setChangeLogIDFrom(Integer changeLogIDFrom) {
		this.changeLogIDFrom = changeLogIDFrom;
	}


	protected Integer getChangeLogIDFrom() {
		return changeLogIDFrom;
	}


	protected void setChangeLogIDTo(Integer changeLogIDTo) {
		this.changeLogIDTo = changeLogIDTo;
	}


	protected Integer getChangeLogIDTo() {
		return changeLogIDTo;
	}


	protected void setUserID(Integer userID) {
		this.userID = userID;
	}


	protected Integer getUserID() {
		return userID;
	}


	public int getLastChangelogID() {
		return lastChangelogID;
	}


	public void setLastChangelogID(int lastChangelogID) {
		this.lastChangelogID = lastChangelogID;
	}


	public boolean isValidateChangelogConsistency() {
		return validateChangelogConsistency;
	}


	public void setValidateChangelogConsistency(boolean validateChangelogConsistency) {
		this.validateChangelogConsistency = validateChangelogConsistency;
	}


	public boolean isDisableInconsistentChangelog() {
		return disableInconsistentChangelog;
	}


	public void setDisableInconsistentChangelog(boolean disableInconsistentChangelog) {
		this.disableInconsistentChangelog = disableInconsistentChangelog;
	}


	public int getFirstChangelogID() {
		return firstChangelogID;
	}


	public void setFirstChangelogID(int firstChangelogID) {
		this.firstChangelogID = firstChangelogID;
	}
}
