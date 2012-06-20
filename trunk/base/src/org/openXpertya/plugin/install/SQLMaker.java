package org.openXpertya.plugin.install;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class SQLMaker {

	// Constantes
	
	protected static final String BLANK = " ";
	protected static final String NL = "\n";
	protected static final String COMMA = ",";
	
	// Variables de Clase
	
	protected static List<String> characterDataTypes;
	protected static List<String> numericDataTypes;
	
	// Inicialización estática
	
	static{
		characterDataTypes = new ArrayList<String>();
		characterDataTypes.add("character");
		characterDataTypes.add("character varying");
		characterDataTypes.add("char");
		characterDataTypes.add("varchar");
		numericDataTypes = new ArrayList<String>();
		numericDataTypes.add("numeric");
		numericDataTypes.add("decimal");
	}
	

	/**
	 * Obtengo la definición del nombre de tabla parámetro.
	 * Puede ser una view o una tabla.
	 * @param tableName nombre de tabla
	 * @param trxName nombre de transacción
	 */
	public static String getDefinition(String tableName, String trxName){
		String definition = null;
		// Si es una vista creo la definición de la view
		if(isView(tableName, trxName)){
			definition = getViewDefinition(tableName, trxName);
		}
		// Sino creo la definición de la tabla
		else{
			definition = getTableDefinition(tableName, trxName);
		}
		return definition;
	}
	
	
	/**
	 * @param tableName 
	 * @param trxName
	 * @return definition de la tabla parámetro si es que existe
	 */
	public static String getTableDefinition(String tableName, String trxName){
		String constraints = null;
		String columns = null;
		StringBuffer definition = new StringBuffer("CREATE TABLE").append(BLANK);
		definition.append(tableName);
		definition.append("(");
		definition.append(NL);
		columns = getColumnsDefinition(tableName, trxName);
		if(columns != null){
			definition.append(columns);
		}
		constraints = getConstraintsDefinition(tableName, trxName);
		if(constraints != null){
			definition.append(",");
			definition.append(constraints);
		}
		definition.append(")");
		definition.append(";");
		return definition.toString();
	}
	
	/**
	 * String representando la definición de las columnas 
	 * de la tabla en la base de datos.  
	 * La última columna va sin coma.
	 * @param tableName nombre de la tabla
	 * @param trxName 
	 * @return
	 */
	public static String getColumnsDefinition(String tableName, String trxName){
		String sql = "SELECT * " +
					 "FROM information_schema.columns " +
					 "WHERE upper(table_name) = upper(?) " +
					 "ORDER BY ordinal_position";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer columns = new StringBuffer();
		String strColumns = null; 
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				columns.append(NL);
				// NOMBRE DE COLUMNA
				columns.append(rs.getString("column_name")).append(BLANK);
				// TIPO DE DATO
				columns.append(getDataType(rs.getString("data_type"), rs.getInt("character_maximum_length"), rs.getInt("numeric_precision"), rs.getInt("numeric_scale"))).append(BLANK);
				// NOT NULL
				if(rs.getString("is_nullable").equalsIgnoreCase("NO")){
					columns.append("NOT NULL").append(BLANK);
				}
				// DEFAULT
				if(!Util.isEmpty(rs.getString("column_default"))){
					columns.append("DEFAULT").append(BLANK);
					columns.append(rs.getString("column_default")).append(BLANK);
				}
				columns.append(COMMA);
			}
			// Si no es vacío, saco la última coma 
			if(!Util.isEmpty(columns.toString())){
				strColumns = columns.deleteCharAt(columns.length()-1).toString();
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
		return strColumns;
	}
	
	/**
	 * String representando el tipo de dato en ddl de tabla.
	 * Por ejemplo si es numeric con precision 3 y escala 2, 
	 * el string quedará numeric(3,2).
	 * @param dataType tipo de dato
	 * @param characterLength longitud de los caracteres
	 * @param numeric_precision precision numérica
	 * @param numeric_scale escala numérica
	 * @return
	 */
	public static String getDataType(String dataType, 
										Integer characterLength,
										Integer numeric_precision,
										Integer numeric_scale){
		StringBuffer dataTypeBuilded = new StringBuffer(dataType);
		// Si es de tipo caracter, va: character(n)
		if(characterDataTypes.contains(dataType)){
			if(characterLength != null && characterLength > 0){
				dataTypeBuilded.append("(");
				dataTypeBuilded.append(String.valueOf(characterLength));
				dataTypeBuilded.append(")");
			}
		}
		// Si es de tipo numérico, va: numeric(p,s) 
		// en caso de scala = 0 va: numeric(p)
		else if(numericDataTypes.contains(dataType)){
			if(numeric_precision != null && numeric_precision > 0){
				dataTypeBuilded.append("(");
				dataTypeBuilded.append(String.valueOf(numeric_precision));
				if(numeric_scale != null && numeric_scale > 0){
					dataTypeBuilded.append(COMMA);
					dataTypeBuilded.append(String.valueOf(numeric_scale));
				}
				dataTypeBuilded.append(")");
			}
		}
		return dataTypeBuilded.toString();
	}
	
	/**
	 * @param tableName
	 * @param trxName
	 * @return definición de las constraints de la tabla parámetro 
	 */
	public static String getConstraintsDefinition(String tableName, String trxName){
		StringBuffer constraints = new StringBuffer();
		// Constraints de la tabla parámetro
		String sql = "SELECT * " +
					 "FROM information_schema.table_constraints " +
					 "WHERE upper(table_name) = upper(?) AND constraint_type <> 'CHECK' " +
					 "ORDER BY constraint_type DESC, constraint_name ASC";
		PreparedStatement ps = null;
		ResultSet rs = null;
		String constraint_name = null;
		String constraints_columns = null;
		String constraints_references = null;
		String strConstraints = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				constraints.append(NL);
				// Nombre de la constraint
				constraint_name = rs.getString("constraint_name");
				constraints.append("CONSTRAINT").append(BLANK);
				constraints.append(constraint_name).append(BLANK);
				// Tipo de constraint
				constraints.append(rs.getString("constraint_type")).append(BLANK);
				// Columnas del constraint
				constraints_columns = getConstraintsTableColumns(tableName, constraint_name,trxName);
				if(!Util.isEmpty(constraints_columns)){
					constraints.append(constraints_columns).append(BLANK);
				}
				constraints_references = getConstraintsReferences(constraint_name,trxName);
				if(!Util.isEmpty(constraints_references)){
					constraints.append(constraints_references).append(BLANK);
				}
				constraints.append(COMMA);
			}
			// Si no es vacío, saco la última coma 
			if(!Util.isEmpty(constraints.toString())){
				strConstraints = constraints.deleteCharAt(constraints.length()-1).toString();
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
		return strConstraints;
	}
	
	/**
	 * @param tableName
	 * @param constraintName
	 * @param trxName
	 * @return columnas de la tabla parámetro que están involucrados
	 * en la constraint parámetro
	 */
	public static String getConstraintsTableColumns(String tableName, String constraintName, String trxName){
		String sql = "SELECT * " +
					 "FROM information_schema.key_column_usage " +
					 "WHERE upper(constraint_name) = upper(?) " +
					 		"AND upper(table_name) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer colConstraints = new StringBuffer();
		String strConstraints = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, constraintName);
			ps.setString(2, tableName);
			rs = ps.executeQuery();
			while (rs.next()) {
				// Nombre de la columna
				colConstraints.append(rs.getString("column_name")).append(COMMA);
			}
			if(!Util.isEmpty(colConstraints.toString())){
				strConstraints = "("+colConstraints.deleteCharAt(colConstraints.length()-1).toString()+")";
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
		return strConstraints;
	}
	
	/**
	 * @param constraintName
	 * @param trxName
	 * @return referencias de la constraint parámetro
	 */
	public static String getConstraintsReferences(String constraintName, String trxName){
		String sql = "SELECT * " +
					 "FROM information_schema.referential_constraints " +
					 "WHERE upper(constraint_name) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer constraintsReferences = new StringBuffer();
		String strConstraintsReferences = null;
		String uniqueConstraint = null;
		String tableNameUConstraint = null;
		String columnsUConstraint = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, constraintName);
			rs = ps.executeQuery();
			if (rs.next()) {
				// Nombre de la unique constraint
				uniqueConstraint = rs.getString("unique_constraint_name");
				constraintsReferences.append("REFERENCES").append(BLANK);
				tableNameUConstraint = getTableName(uniqueConstraint, trxName);
				if(!Util.isEmpty(tableNameUConstraint)){
					constraintsReferences.append(tableNameUConstraint).append(BLANK);
					columnsUConstraint = getConstraintsTableColumns(tableNameUConstraint,uniqueConstraint, trxName);
					if(!Util.isEmpty(columnsUConstraint)){
						constraintsReferences.append(columnsUConstraint).append(BLANK);
					}
				}
				constraintsReferences.append("MATCH").append(BLANK);
				constraintsReferences.append(getMatch(rs.getString("match_option"))).append(BLANK);
				constraintsReferences.append("ON UPDATE").append(BLANK);
				constraintsReferences.append(rs.getString("update_rule")).append(BLANK);
				constraintsReferences.append("ON DELETE").append(BLANK);
				constraintsReferences.append(rs.getString("delete_rule")).append(BLANK);
				constraintsReferences.append(COMMA);
			}
			if(!Util.isEmpty(constraintsReferences.toString())){
				strConstraintsReferences = constraintsReferences.deleteCharAt(constraintsReferences.length()-1).toString();
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
		return strConstraintsReferences;
	}

	/**
	 * @param constraintName
	 * @param trxName
	 * @return nombre de la tabla de la constraint parámetro
	 */
	public static String getTableName(String constraintName, String trxName){
		String sql = "SELECT DISTINCT table_name " +
					 "FROM information_schema.table_constraints " +
					 "WHERE upper(constraint_name) = upper(?) " +
					 "LIMIT 1";
		return DB.getSQLValueString(trxName, sql, constraintName);
	}
	
	/**
	 * @param matchOption
	 * @return opción de match
	 */
	private static String getMatch(String matchOption){
		String match = matchOption;
		if(match.equalsIgnoreCase("NONE")){
			match = "SIMPLE";
		}
		return match;
	}
	
	/**
	 * @param tableName
	 * @param columnName
	 * @param trxName
	 * @return add columna de la tabla parámetro
	 */
	public static String getAddColumn(String tableName, String columnName, String trxName){
		String sql = "SELECT * " +
		 			 "FROM information_schema.columns " +
		 			 "WHERE upper(table_name) = upper(?) AND upper(column_name) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer alterTable = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			ps.setString(2, columnName);
			rs = ps.executeQuery();
			if (rs.next()) {
				alterTable = new StringBuffer("ALTER TABLE").append(BLANK);
				alterTable.append(tableName).append(BLANK);
				alterTable.append("ADD COLUMN").append(BLANK);
				alterTable.append(columnName).append(BLANK);
				alterTable.append(getDataType(rs.getString("data_type"), rs.getInt("character_maximum_length"), rs.getInt("numeric_precision"), rs.getInt("numeric_scale")));
				alterTable.append(";");
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
		return alterTable == null?"":alterTable.toString();
	}
	
	/**
	 * @param tableName
	 * @param columnName
	 * @param trxName
	 * @return alter de la columna parámetro
	 */
	public static String getAlterType(String tableName, String columnName, String trxName){
		String sql = "SELECT * " +
		 			 "FROM information_schema.columns " +
		 			 "WHERE upper(table_name) = upper(?) AND upper(column_name) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer alterColumn = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			ps.setString(2, columnName);
			rs = ps.executeQuery();
			if (rs.next()) {
				alterColumn = new StringBuffer("ALTER TABLE").append(BLANK);
				alterColumn.append(tableName).append(BLANK);
				alterColumn.append("ALTER COLUMN").append(BLANK);
				alterColumn.append(columnName).append(BLANK);
				alterColumn.append("TYPE").append(BLANK);
				alterColumn.append(getDataType(rs.getString("data_type"), rs.getInt("character_maximum_length"), rs.getInt("numeric_precision"), rs.getInt("numeric_scale")));
				alterColumn.append(";");
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
		return alterColumn == null?"":alterColumn.toString();
	}
	
	/**
	 * @param tableName
	 * @param columnName
	 * @param trxName
	 * @return set o drop not null de la columna parámetro
	 */
	public static String getColumnNotNull(String tableName, String columnName, String trxName){
		String sql = "SELECT * " +
		 			"FROM information_schema.columns " +
		 			"WHERE upper(table_name) = upper(?) AND upper(column_name) = upper(?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer alterColumn = null;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			ps.setString(2, columnName);
			rs = ps.executeQuery();
			if (rs.next()) {
				alterColumn = new StringBuffer("ALTER TABLE").append(BLANK);
				alterColumn.append(tableName).append(BLANK);
				alterColumn.append("ALTER COLUMN").append(BLANK);
				alterColumn.append(columnName).append(BLANK);
				if(rs.getString("is_nullable").equalsIgnoreCase("NO")){
					alterColumn.append("SET").append(BLANK);
				}
				else{
					alterColumn.append("DROP").append(BLANK);
				}
				alterColumn.append("NOT NULL").append(BLANK);
			}
			alterColumn.append(";");
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
		return alterColumn == null?"":alterColumn.toString();
	}
	
	/**
	 * @param tableName Nombre de la tabla
	 * @param trxName nombre de la transacción
	 * @return definición de la view
	 */
	public static String getViewDefinition(String tableName, String trxName){
		String sql = "SELECT * " +
					 "FROM information_schema.views " +
					 "WHERE upper(table_name) = upper(?)"; 
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuffer viewDefinition = new StringBuffer("CREATE VIEW").append(BLANK);
		viewDefinition.append(tableName).append(BLANK);
		viewDefinition.append("AS").append(NL);
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			if (rs.next()) {
				viewDefinition.append(rs.getString("view_definition"));
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
		return viewDefinition.toString();
	}
	
	
	/**
	 * @param tableName nombre de la tabla
	 * @param trxName nombre de la transacción
	 * @return si la tabla es una view, false cc
	 */
	public static boolean isView(String tableName, String trxName){
		String sql = "SELECT table_type " +
					 "FROM information_schema.tables " +
					 "WHERE upper(table_name) = upper(?) ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean isView = false;
		try{
			ps = DB.prepareStatement(sql, trxName);
			ps.setString(1, tableName);
			rs = ps.executeQuery();
			if (rs.next()) {
				isView = rs.getString("table_type").equalsIgnoreCase("VIEW");
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
		return isView;
	}
	
	/**
	 * @param description descripción título de la cabecera
	 * @return cabecera con la descripción parámetro
	 */
	public static String getHeader(String description){
		StringBuffer header = new StringBuffer();
		header.append("----------------------------------------------------------------------").append(NL);
		header.append("----------").append(BLANK).append(description).append(NL);
		header.append("----------------------------------------------------------------------").append(NL);
		return header.toString();
	}
	
}
