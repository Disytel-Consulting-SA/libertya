package org.openXpertya.plugin.install;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.DB;

public class ChangeLogGroupList {

	// Variables de instancia
	
	/** Lista de grupos de change log */
	
	protected List<ChangeLogGroup> groups;
	
	/** Identificador de Versión de Componente */
	
	private Integer componentVersionID;
	
	/** Esquema de tablas, opcional */

	private Integer tableSchemaID;
	
	/** AD_ChangeLog_ID inicial */
	
	private Integer changeLogIDFrom = null;  
	
	/** AD_ChangeLog_ID fin */
	
	private Integer changeLogIDTo = null;
	
	/** Usuario registrado en registros del changelog */
	
	private Integer userID = null;
	
	
	// Constructores
	
	
	public ChangeLogGroupList(){
		setGroups(new ArrayList<ChangeLogGroup>());
		setComponentVersionID(null);
		setTableSchemaID(null);
	}
	
	
	public ChangeLogGroupList(Integer ad_componentVersion_id, Integer ad_tableSchema_id){
		this();
		setComponentVersionID(ad_componentVersion_id);
		setTableSchemaID(ad_tableSchema_id);
	}
	
	
	public ChangeLogGroupList(Integer ad_componentVersion_id, Integer ad_tableSchema_id, Integer changeLogIDFrom, Integer changeLogIDTo, Integer userID){
		this(ad_componentVersion_id, ad_tableSchema_id);
		setChangeLogIDFrom(changeLogIDFrom);
		setChangeLogIDTo(changeLogIDTo);
		setUserID(userID);
	}
	
	// Varios
	
	public void fillList(String trxName) throws Exception{

		/* Recargar la caché si asi corresponde */
		ReplicationCache.reloadCacheData();
		
		boolean existTableSchema = getTableSchemaID() != null;
		if(getComponentVersionID() == null){
			throw new Exception("Error de Log: No existe AD_ComponentVersion_ID para determinar la informacion a obtener.");
		}
		StringBuffer sql = new StringBuffer(" SELECT log.ad_changelog_id,log.ad_componentObjectuid as uid,log.ad_componentversion_id,	log.ad_table_id,	log.ad_client_id,	log.ad_org_id,	log.created,	log.createdby,	log.updated,	log.updatedby,	log.ad_session_id,	log.record_id,	log.ad_column_id,	log.oldvalue,	log.newvalue,	log.operationtype,	log.binaryvalue,	log.trxname,	log.changeloggroup_id, t.tableName FROM ad_changelog as log ");
		sql.append(" INNER JOIN AD_Table t ON (log.ad_table_id = t.ad_table_id)");
		if(existTableSchema){
			sql.append(" INNER JOIN ad_tableschemaline AS tsl ON (log.ad_table_id = tsl.ad_table_id) ");
			sql.append(" INNER JOIN ad_tableschema AS ts ON (ts.ad_tableschema_id = tsl.ad_tableschema_id) ");
		}
		sql.append(" WHERE log.ad_componentversion_id = ? AND log.changeloggroup_id is not null ");
		if(existTableSchema){
			sql.append(" AND (ts.ad_tableschema_id = ?) ");
		}
		// Solo tomamos las tuplas activas (util para hacer exportaciones
		// parciales desactivando tuplas ya exportadas por ejemplo).
		sql.append(" AND log.IsActive='Y' ");
		// Agregar filtro ppor changelog id y user id (util para hacer exportaciones
		// parciales desactivando tuplas ya exportadas por ejemplo).
		if(getChangeLogIDFrom() != null){
			sql.append("AND (ad_changelog_id >= ?) ");
		}
		if(getChangeLogIDTo() != null){
			sql.append("AND (ad_changelog_id <= ?) ");
		}
		if(getUserID() != null){
			sql.append("AND (log.createdby = ?) ");
		}
		// 
		sql.append(" ORDER BY changeloggroup_id, ad_changelog_id asc ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		ChangeLogGroup group = null;
		ChangeLogElement element = null;
		Integer ad_table_id = null;
		Integer changeLogGroupID = null;
		String operationType = null;
		String ad_componentObjectUID = null;
		String tableName = null;
		try{
			ps = DB.prepareStatement(sql.toString(), trxName);
			int i = 1;
			ps.setInt(i++, getComponentVersionID());
			if(existTableSchema){
				ps.setInt(i++, getTableSchemaID());
			}
			if(getChangeLogIDFrom() != null){
				ps.setInt(i++, getChangeLogIDFrom());
			}
			if(getChangeLogIDTo() != null){
				ps.setInt(i++, getChangeLogIDTo());
			}
			if(getUserID() != null){
				ps.setInt(i++, getUserID());
			}
			rs = ps.executeQuery();
			while(rs.next()){
				// Obtener un grupo de changeLog nuevo
				// o seguir con el que venía antes
				if((group == null) || !equalValue(changeLogGroupID, rs.getInt("changeloggroup_id"))){
					// Asigno los valores que identifican una transacción
					changeLogGroupID = rs.getInt("changeloggroup_id");
					ad_table_id = rs.getInt("ad_table_id");
					operationType = rs.getString("operationtype");
					ad_componentObjectUID = rs.getString("uid");
					tableName = rs.getString("tableName");
					// Creo el grupo
					group = new ChangeLogGroup(ad_table_id, ad_componentObjectUID, operationType, tableName);
					group.setAd_componentObjectUID(ad_componentObjectUID);
					group.setOperation(operationType);
					getGroups().add(group);
				}
				// Crear el elemento y agregarlo al grupo
				element = new ChangeLogElement(rs.getInt("ad_column_id"), rs.getString("oldvalue"), rs.getString("newvalue"), rs.getBytes("binaryvalue"), rs.getInt("ad_changelog_id"));
				group.addElement(element);
			}
		} catch(Exception e){
			e.printStackTrace();
			// TODO: Modificar el mensaje de error pasado
			throw e;
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				e.printStackTrace();
				// TODO: Modificar el mensaje de error pasado
				throw e;
			}
		}
	}
	
	/**
	 * @param oldValue valor anterior 
	 * @param value nuevo valor
	 * @return true si los valores son iguales, else cc
	 */
	protected boolean equalValue(Integer oldValue, Integer value){
		return ((oldValue == null) && (value == null)) 
		|| ((oldValue != null) && (value != null) && (oldValue.intValue() == value.intValue()));
	}
	
	/**
	 * @param oldValue valor anterior
	 * @param value nuevo valor
	 * @return true si los valores son iguales, else cc
	 */
	private boolean equalValue(String oldValue, String value){
		return ((oldValue == null) && (value == null)) 
		|| ((oldValue != null) && (value != null) && (oldValue.equalsIgnoreCase(value)));
	}
	
	// Getters y Setters
	
	private void setGroups(List<ChangeLogGroup> groups) {
		this.groups = groups;
	}

	public List<ChangeLogGroup> getGroups() {
		return groups;
	}

	public void setComponentVersionID(Integer ad_componentVersion_id) {
		this.componentVersionID = ad_componentVersion_id;
	}

	public Integer getComponentVersionID() {
		return componentVersionID;
	}


	public void setTableSchemaID(Integer tableSchemaID) {
		this.tableSchemaID = tableSchemaID;
	}


	public Integer getTableSchemaID() {
		return tableSchemaID;
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
}
