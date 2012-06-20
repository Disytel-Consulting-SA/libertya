package org.openXpertya.plugin.install;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class PluginSQLBuilder extends PluginDocumentBuilder {

	// Variables de clase
	
	/** Columnas de referencias a alter column */
	
	protected static List<Integer> alterColumns;
	
	/** Tipos de datos libertya - integer */
	
	protected static List<Integer> integers;
	
	/** Tipos de datos libertya - numeric */
	
	protected static List<Integer> numerics;
	
	/** Tipos de datos libertya - texts */
	
	protected static List<Integer> texts;
	
	/** Tipos de datos libertya - timestamp */
	
	protected static List<Integer> timestamps;
	
	// Variables de instancia
	
	/** Archivo SQL */
	
	private File sqlFile;
	
	/** Writer del documento */
	
	private PrintWriter writer;
	
	/** Tablas nuevas agregadas */
	
	private List<Integer> newTables;
	
	/** AD_ChangeLog_ID inicial */
	
	private Integer changeLogIDFrom = null;  
	
	/** AD_ChangeLog_ID fin */
	
	private Integer changeLogIDTo = null;
	
	/** Usuario registrado en registros del changelog */
	
	private Integer userID = null;
	
	
	// Inicialización estática
	
	static{
		// Enteros
		integers = new ArrayList<Integer>();
		integers.add(11);
		integers.add(13);
		integers.add(18);
		integers.add(19);
		integers.add(21);
		integers.add(25);
		integers.add(26);
		integers.add(27);
		integers.add(30);
		integers.add(31);
		integers.add(33);
		integers.add(35);
		// Numéricos
		numerics = new ArrayList<Integer>();
		numerics.add(12);
		numerics.add(22);
		numerics.add(29);
		numerics.add(37);
		// Textos grandes
		texts = new ArrayList<Integer>();
		texts.add(10);
		texts.add(14);
		texts.add(34);
		texts.add(36);
		// Timestamps
		timestamps = new ArrayList<Integer>();
		timestamps.add(16);
		timestamps.add(24);
		// Columnas a tener en cuenta en alter column
		alterColumns = new ArrayList<Integer>();
		alterColumns.add(226);
		alterColumns.add(124);
		alterColumns.add(118);
	}
	
	// Constructores
	
	public PluginSQLBuilder(String path, String fileName, Integer componentVersionID, String trxName) {
		super(path, fileName, componentVersionID, trxName);
		setNewTables(new ArrayList<Integer>());
	}
	
	public PluginSQLBuilder(String path, String fileName, Integer componentVersionID, Integer changeLogIDFrom, Integer changeLogIDTo, Integer userID, String trxName) {
		this(path, fileName, componentVersionID, trxName);
		setChangeLogIDFrom(changeLogIDFrom);
		setChangeLogIDTo(changeLogIDTo);
		setUserID(userID);
	}

	
	// Métodos heredados

	@Override
	protected void createDocument() throws Exception {
		try {
			setSqlFile(new File(getDestinyFilePath()));
		    FileWriter fw = new FileWriter(getSqlFile());
		    BufferedWriter bw = new BufferedWriter(fw);
		    setWriter(new PrintWriter(bw));
		}
		catch(IOException ioex) {
			ioex.printStackTrace();
		}	
	}


	@Override
	protected void fillDocument() throws Exception {
		// Crear nuevas tablas
		makeNewTables();
		// Crear columnas nuevas
		makeNewColumns();
		// Crear alters de las tablas modificadas 
		makeAltersTable();
	}


	@Override
	protected void saveDocument() throws Exception {
		// Cierro el archivo
		getWriter().close();
	}

	
	/**
	 * Crear nuevas tablas a partir del changelog
	 */
	protected void makeNewTables(){
		StringBuffer sql = new StringBuffer("SELECT t.ad_table_id, tablename " +
					 						"FROM (SELECT DISTINCT ad_componentobjectuid as uid " +
					 								"FROM ad_changelog as cl " +
					 								"WHERE cl.ad_componentversion_id = ? " +
					 									"AND operationtype = 'I' " +
					 									"AND cl.ad_table_id = 100 " +
					 									"AND cl.IsActive = 'Y' ");
		
		if(getChangeLogIDFrom() != null){
			sql.append("AND (ad_changelog_id >= ?) ");
		}
		if(getChangeLogIDTo() != null){
			sql.append("AND (ad_changelog_id <= ?) ");
		}
		if(getUserID() != null){
			sql.append("AND (createdby = ?) ");
		}
		sql.append(") as ut " +
					 "INNER JOIN ad_table as t ON t.ad_componentobjectuid = ut.uid");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = DB.prepareStatement(sql.toString(), trxName);
			int i=1;
			ps.setInt(i++, getComponentVersion().getID());
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
			getWriter().println(SQLMaker.getHeader(Msg.getMsg(Env.getCtx(), "TablesAndViews")));
			while (rs.next()) {
				// WRITE DEFINITION TABLE
				getWriter().println(SQLMaker.getDefinition(rs.getString("tablename"), trxName));
				getWriter().println();
				getNewTables().add(rs.getInt("ad_table_id"));
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
	}
	
	/**
	 * Crear nuevas columnas de tablas existentes
	 */
	protected void makeNewColumns(){
		StringBuffer sql = new StringBuffer("SELECT tablename, c.ad_column_id, columnname " +
				 "FROM (SELECT DISTINCT ad_componentobjectuid as uid " +
				 		"FROM ad_changelog as cl " +
				 		"WHERE cl.ad_componentversion_id = ? " +
				 				"AND operationtype = 'I' " +
				 				"AND cl.ad_table_id = 101 " +
				 				"AND cl.IsActive = 'Y' "); 
		if(getChangeLogIDFrom() != null){
			sql.append("AND (ad_changelog_id >= ?) ");
		}
		if(getChangeLogIDTo() != null){
			sql.append("AND (ad_changelog_id <= ?) ");
		}
		if(getUserID() != null){
			sql.append("AND (createdby = ?) ");
		}
		sql.append(") as ut " +
				 "INNER JOIN ad_column as c ON c.ad_componentobjectuid = ut.uid ");
		// Si hay nuevas tablas, las filtro
		if(getNewTables().size() > 0){
			sql.append("INNER JOIN (SELECT * " +
				"FROM ad_table " +
				"WHERE ad_table_id NOT IN "+toCol(getNewTables())+") as t ON t.ad_table_id = c.ad_table_id ");
		}
		else{
			sql.append("INNER JOIN ad_table as t ON t.ad_table_id = c.ad_table_id ");
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = DB.prepareStatement(sql.toString(), trxName);
			int i = 1;
			ps.setInt(i++, getComponentVersion().getID());
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
			getWriter().println(SQLMaker.getHeader(Msg.getMsg(Env.getCtx(), "NewColumns")));
			while (rs.next()) {
				// WRITE DEFINITION COLUMN
				getWriter().println(SQLMaker.isView(rs.getString("tablename"), trxName)?
										SQLMaker.getViewDefinition(rs.getString("tablename"), trxName):
										SQLMaker.getAddColumn(rs.getString("tablename"), rs.getString("columnname"), trxName));
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
	}
	
	/**
	 * Crear alters de columnas existentes sobre columnas
	 * de tipo de dato, longitud y not null
	 */
	protected void makeAltersTable(){
		StringBuffer sql = new StringBuffer("SELECT tablename, c.ad_column_id, columnname, id, ut.uid, oldvalue, newvalue " +
					 "FROM (SELECT DISTINCT ad_componentobjectuid as uid " +
					 		"FROM ad_changelog as cl " +
					 		"WHERE cl.ad_componentversion_id = ? " +
					 				"AND operationtype = 'M' " +
					 				"AND cl.ad_table_id = 101 " +
					 				"AND cl.IsActive = 'Y' ");
		if(getChangeLogIDFrom() != null){
			sql.append("AND (ad_changelog_id >= ?) ");
		}
		if(getChangeLogIDTo() != null){
			sql.append("AND (ad_changelog_id <= ?) ");
		}
		if(getUserID() != null){
			sql.append("AND (createdby = ?) ");
		}
		sql.append("AND ad_column_id IN "+toCol(alterColumns)+") as ut " +
					 "INNER JOIN ad_column as c ON c.ad_componentobjectuid = ut.uid ");
		// Si hay nuevas tablas, las filtro
		if(getNewTables().size() > 0){
			sql.append("INNER JOIN (SELECT * " +
				"FROM ad_table " +
				"WHERE ad_table_id NOT IN "+toCol(getNewTables())+") as t ON t.ad_table_id = c.ad_table_id ");
		}
		else{
			sql.append("INNER JOIN ad_table as t ON t.ad_table_id = c.ad_table_id ");
		}					 
		sql.append("INNER JOIN (SELECT ad_column_id as id, ad_componentobjectuid as uid, oldvalue, newvalue " +
					 			 "FROM ad_changelog " +
					 			 "WHERE ad_componentversion_id = ? " +
					 			 		"AND operationtype = 'M' " +
					 			 		"AND ad_table_id = 101 ");
		if(getChangeLogIDFrom() != null){
			sql.append("AND (ad_changelog_id >= ?) ");
		}
		if(getChangeLogIDTo() != null){
			sql.append("AND (ad_changelog_id <= ?) ");
		}
		if(getUserID() != null){
			sql.append("AND (createdby = ?) ");
		}
		sql.append("AND ad_column_id IN "+toCol(alterColumns)+") as col ON col.uid = ut.uid ");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = DB.prepareStatement(sql.toString(), trxName);
			int i = 1;
			ps.setInt(i++, getComponentVersion().getID());
			if(getChangeLogIDFrom() != null){
				ps.setInt(i++, getChangeLogIDFrom());
			}
			if(getChangeLogIDTo() != null){
				ps.setInt(i++, getChangeLogIDTo());
			}
			if(getUserID() != null){
				ps.setInt(i++, getUserID());
			}
			ps.setInt(i++, getComponentVersion().getID());
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
			getWriter().println(SQLMaker.getHeader(Msg.getMsg(Env.getCtx(), "AltersTable")));
			while (rs.next()) {
				if(SQLMaker.isView(rs.getString("tablename"), trxName)){
					getWriter().println(SQLMaker.getViewDefinition(rs.getString("tablename"), trxName));
				}
				else{
					// Si es isMandatory, me fijo not nulls
					if(rs.getInt("id") == 124){
						getWriter().println(SQLMaker.getColumnNotNull(rs.getString("tablename"), rs.getString("columnname"), trxName));
					}
					// Si no, verifico validaciones de tipo de datos 
					// específicamente si es necesario modificar el tipo de la base
					else if(rs.getInt("id") == 226 && changeBDDataType(rs.getString("oldvalue"),rs.getString("newvalue"))){
						getWriter().println(SQLMaker.getAlterType(rs.getString("tablename"), rs.getString("columnname"), trxName));
					}
					else if(rs.getInt("id") == 118){
						getWriter().println(SQLMaker.getAlterType(rs.getString("tablename"), rs.getString("columnname"), trxName));
					}
				}
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
	}
	
	/**
	 * @param col colección del tipo [elem1,...,elemN]
	 * @return colección del tipo (elem1,...,elemN)
	 */
	public static String toCol(Collection<?> col){
		return col.toString().replace("[", "(").replace("]", ")");
	} 
	
	/**
	 * @param oldValue old value
	 * @param newValue new value
	 * @return true si debo cambiar el tipo de dato de la base
	 */
	private static boolean changeBDDataType(String oldValue, String newValue){
		boolean change = true;
		if(oldValue.equalsIgnoreCase("NULL") || newValue.equalsIgnoreCase("NULL")){
			change = false;
		}
		else{
			// Tipos de datos a entero
			Integer dataTypeOld = Integer.parseInt(oldValue);
			Integer dataTypeNew = Integer.parseInt(newValue);
			// Si son enteros no cambio el tipo de dato
			if(integers.contains(dataTypeOld) && integers.contains(dataTypeNew)){
				change = false;
			}
			else if(numerics.contains(dataTypeOld) && numerics.contains(dataTypeNew)){
				change = false;
			}
			else if(texts.contains(dataTypeOld) && texts.contains(dataTypeNew)){
				change = false;
			}
			else if(timestamps.contains(dataTypeOld) && timestamps.contains(dataTypeNew)){
				change = false;
			}
		}
		return change;
	}
		
	// Getters y Setters

	protected void setSqlFile(File sqlFile) {
		this.sqlFile = sqlFile;
	}


	protected File getSqlFile() {
		return sqlFile;
	}


	protected void setWriter(PrintWriter writer) {
		this.writer = writer;
	}


	protected PrintWriter getWriter() {
		return writer;
	}


	protected void setNewTables(List<Integer> newTables) {
		this.newTables = newTables;
	}


	protected List<Integer> getNewTables() {
		return newTables;
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
