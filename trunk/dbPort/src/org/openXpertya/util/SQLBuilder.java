package org.openXpertya.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLBuilder {

	/** Lista de los nombres de las columnas */
	private List<String> columnNames;

	/**
	 * Asociación entre los nombres de las columnas y los valores a concatenar
	 * dentro la consulta
	 */
	private Map<String, String> columnNamesValues;

	/** ID de la tabla */
	private Integer tableID;
	
	/** Nombre de la tabla */
	private String tableName;

	public SQLBuilder() {
		setColumnNames(new ArrayList<String>());
		setColumnNamesValues(new HashMap<String, String>());
	}
	
	public SQLBuilder(Integer tableID, String tableName) {
		this();
		setTableID(tableID);
		setTableName(tableName);
		initColumnNames();
	}

	/**
	 * Inicializa los nombres de las columnas en base a la tabla configurada
	 */
	public void initColumnNames(){
		String sql = "SELECT columnname FROM ad_column WHERE ad_table_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql);
			ps.setInt(1, getTableID());
			rs = ps.executeQuery();
			while (rs.next()) {
				getColumnNames().add(rs.getString("columnname"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(rs != null){
					rs.close();
					rs = null;
				}
			} catch(Exception e2){
				e2.printStackTrace();
			}
		}
	}
	
	/**
	 * Agrega un nombre de columna y un valor para esa columna
	 * 
	 * @param columnName
	 *            nombre de columna
	 * @param columnValue
	 *            valor de la columna 
	 */
	public void addColumnNameValue(String columnName, String columnValue){
		getColumnNamesValues().put(columnName.toUpperCase(), columnValue);
	}
	
	/**
	 * @return el sql insert into correspondiente a los nombres de columna y sus
	 *         respectivos valores
	 */
	public String makeSQLInsert() {
		StringBuilder sql = new StringBuilder(" INSERT INTO ");
		StringBuilder columns = new StringBuilder(Env.PARENTHESIS_OPEN);
		StringBuilder values = new StringBuilder(Env.PARENTHESIS_OPEN);
		sql.append(getTableName()).append(Env.BLANK);
		for (String columnName : getColumnNames()) {
			columns.append(columnName).append(Env.COMMA);
			values.append(getColumnNamesValues().get(columnName.toUpperCase())).append(
					Env.COMMA);
		}
		sql.append(columns.substring(0, columns.length() - 1)).append(
				Env.PARENTHESIS_CLOSE);
		sql.append(" VALUES ");
		sql.append(values.substring(0, values.length() - 1)).append(
				Env.PARENTHESIS_CLOSE);
		return sql.toString();
	}

	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNamesValues(Map<String, String> columnNamesValues) {
		this.columnNamesValues = columnNamesValues;
	}

	public Map<String, String> getColumnNamesValues() {
		return columnNamesValues;
	}

	public void setTableID(Integer tableID) {
		this.tableID = tableID;
	}

	public Integer getTableID() {
		return tableID;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}
}
