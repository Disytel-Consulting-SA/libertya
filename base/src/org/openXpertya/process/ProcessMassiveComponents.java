package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MComponent;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class ProcessMassiveComponents extends SvrProcess {

	// Variables de instancia
	
	/** Versión de Componente */
	
	private MComponentVersion componentVersion;
	
	/** Sólo actualizar UID */
	
	private boolean justUID;
	
	/** Tabla opcional */
	
	private Integer tableID;
	
	/** Componente */
	
	private MComponent component;
	
	/** Tablas excluídas */
	
	private List<String> excludedTables;
	
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
        // deteminar la tabla base que será actualizada 
        for( int i = 0; i < para.length; i++ ) {
            String name = para[i].getParameterName();
            if( name.equals( "AD_ComponentVersion_ID" )) {
            	setComponentVersion(new MComponentVersion(getCtx(), para[i].getParameterAsInt(), get_TrxName()));
            }
            else if( name.equals( "JustUID" )) {
            	setJustUID(((String)para[i].getParameter()).equalsIgnoreCase("Y"));
            }
            else if( name.equals( "AD_Table_ID" )) {
            	setTableID(para[i].getParameterAsInt());
            }
            else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		initialize();
		// Obtengo todas las tablas que contienen el campo AD_ComponentVersion_ID
		StringBuffer sql = new StringBuffer("SELECT distinct t.ad_table_id, t.tablename " +
		 			 						"FROM ad_table as t " +
		 			 						"INNER JOIN ad_column as c ON (t.ad_table_id = c.ad_table_id) " +
		 			 						"WHERE t.tablename NOT IN "+elementsToCol(getExcludedTables()));
		// Si es solamente para campos uid
		if(isJustUID()){
			sql.append(" AND (c.columnname ilike 'AD_ComponentObjectUID')");
		}
		else{
			sql.append(" AND (c.columnname ilike 'AD_ComponentVersion_ID')");
		}
		// Si es solo una tabla
		if(!Util.isEmpty(getTableID(), true)){
			sql.append(" AND t.ad_table_id = ").append(getTableID());
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sqlTable;
		StringBuffer uid = new StringBuffer();
		M_Table table;
		List<String> keyColumns;
		try{
			getTrx(get_TrxName()).start();
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			rs = ps.executeQuery();
			// Actualizar AD_ComponentVersion_ID y AD_ComponentObjectUID
			while(rs.next()){
				table = new M_Table(getCtx(), rs.getInt("ad_table_id"), get_TrxName()); 
				uid = new StringBuffer("'"+getComponent().getPrefix() + PO.SEPARATORUID + table.getTableName()+"'");
				keyColumns = table.getKeyColumns();
				// Iterar por las columnas clave
				for (int i = 0; i < keyColumns.size(); i++) {
					uid.append("||'").append(PO.SEPARATORUID).append("'||").append(keyColumns.get(i));
				}
				sqlTable = getSqlUpdateForTable(table.getTableName(),uid.toString());
				DB.executeUpdate(sqlTable, get_TrxName());
			}
			getTrx(get_TrxName()).commit();
		} catch(Exception e){
			getTrx(get_TrxName()).rollback();
			log.severe(e.getMessage());
			throw new Exception(e.getMessage());
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				log.severe(e.getMessage());
				throw new Exception(e.getMessage());
			}
		}
		return "Actualizacion de tablas finalizada correctamente";
	}
	
	
	private void initialize(){
		setComponent(new MComponent(getCtx(), getComponentVersion().getAD_Component_ID(), get_TrxName()));
		initExcludedTables();
	}
	
	private void initExcludedTables(){
		List<String> tables = new ArrayList<String>();
		tables.add("'AD_ChangeLog'");
		tables.add("'AD_Component'");
		tables.add("'AD_ComponentVersion'");
		setExcludedTables(tables);
	}	
	
	
	private String getSqlUpdateForTable(String tablename, String uid){
		StringBuffer update = new StringBuffer(" UPDATE " + tablename + " SET ad_componentobjectuid = " + uid);
		if(!isJustUID()){
			update.append(" , ad_componentversion_id = "+getComponentVersion().getID());
			update.append(" WHERE ad_componentversion_id is null and ad_componentobjectuid is null ");
		}
		else{
			update.append(" WHERE ad_componentobjectuid is null ");
		}
		return update.toString();
	}
	
	/**
	 * Transforma una colección en un conjunto por el estilo: (1,2,3). 
	 * Esto sirve para, por ejemplo, colocar dentro de una query todos los id de una colección.
	 * @param elements elementos dentro de la colección 
	 * @return una cadena de caracteres que representa los elementos en una colección: (1,2,3)
	 */
	protected String elementsToCol(List<String> elements){
		return elements.toString().replace('[', '(').replace(']', ')');
	}
	
	// Getters y Setters
	
	private void setComponentVersion(MComponentVersion componentVersion) {
		this.componentVersion = componentVersion;
	}

	private MComponentVersion getComponentVersion() {
		return componentVersion;
	}

	private void setComponent(MComponent component) {
		this.component = component;
	}

	private MComponent getComponent() {
		return component;
	}

	private void setExcludedTables(List<String> excludedTables) {
		this.excludedTables = excludedTables;
	}

	private List<String> getExcludedTables() {
		return excludedTables;
	}

	private void setJustUID(boolean justUID) {
		this.justUID = justUID;
	}

	private boolean isJustUID() {
		return justUID;
	}

	private Integer getTableID() {
		return tableID;
	}

	private void setTableID(Integer tableID) {
		this.tableID = tableID;
	}
}
