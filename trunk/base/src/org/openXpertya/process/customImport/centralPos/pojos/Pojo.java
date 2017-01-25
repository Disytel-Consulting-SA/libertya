package org.openXpertya.process.customImport.centralPos.pojos;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

import com.google.gson.internal.LinkedTreeMap;

public class Pojo {
	/** Campos a almacenar en la DB. */
	protected String[] filteredFields;
	/** Campos por los cuales se realizará el mapeo entre cabecera y lineas.
	 * IMPORTANTE: Estos campos deben estar en "filteredFields" */
	public String[] matchingFields;
	/** Conjunto de datos recuperados desde la API. */
	protected LinkedTreeMap<String, Object> values;
	/** Tabla de importación en donde se almacenarán los datos. */
	private String importTableName;

	/**
	 * Constructor.
	 * @param filteredFields Campos a recuperar.
	 * @param values Valores obtenidos desde la API.
	 * @param importTableName Nombre de la tabla de destino.
	 */
	public Pojo(String[] filteredFields, LinkedTreeMap<String, Object> values, String importTableName) {
		this.importTableName = importTableName;
		this.filteredFields = filteredFields;
		this.values = values;
	}

	/** @return Campos standar de libertya, y campos standar de tablas de importación. */
	protected String defaultFields() {
		return "ad_client_id," +
				"ad_org_id," +
				"isactive," +
				"created," +
				"createdby," +
				"updated," +
				"updatedby," +
				"i_errormsg," +
				"i_isimported," +
				"processing," +
				"processed,";
	}

	@Override
	public String toString() {
		if (filteredFields != null || filteredFields.length == 0) {
			StringBuffer str = new StringBuffer();
			for (String s : filteredFields) {
				str.append(s + ": " + values.get(s) + ", ");
			}
			str.deleteCharAt(str.length() - 2);
			return "{" + str.toString() + "}";
		}
		return "Invalid pojo";
	}

	/**
	 * @param ctx Contexto de ejecución.
	 * @return Valores correspondientes a los campos standar  
	 * de libertya, y campos standar de tablas de importación.
	 */
	protected String defaultValues(Properties ctx) {
		return Env.getAD_Client_ID(ctx) + "," + // ad_client_id
				Env.getAD_Org_ID(ctx) + "," +   // ad_org_id
				"'Y'," + 						// isactive
				"'" + Env.getDate() + "'," +	// created
				Env.getAD_User_ID(ctx) +"," + 	// createdby
				"'" + Env.getDate() + "'," + 	// updated
				Env.getAD_User_ID(ctx) + "," + 	// updatedby
				"''," + 						// i_errormsg
				"'N'," + 						// i_isimported
				"'N'," + 						// processing
				"'Y',"; 						// processed
	}

	/**
	 * Genera una sentencia INSERT a partir de los campos a 
	 * almacenar, y los datos recuperados desde la API.
	 * @param ctx Contexto de ejecución.
	 * @param trxName Nombre de la transaccion.
	 * @return Cantidad de elementos insertados.
	 */
	public int save(Properties ctx, String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("INSERT INTO " + importTableName + "(");

		sql.append(defaultFields());

		for (String field : filteredFields) {
			sql.append(field + ",");
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(") VALUES (");

		sql.append(defaultValues(ctx));

		for (String field : filteredFields) {
			sql.append("'" + values.get(field) + "',");
		}

		sql.deleteCharAt(sql.length() - 1);
		sql.append(")");

		return DB.executeUpdate(sql.toString(), true);
	}

	/**
	 * Compara entre los campos en común de dos pojos, 
	 * para determinar si pertenecen a la misma transacción.
	 * @param head pojo1
	 * @param detail pojo2
	 * @return <code>true</code> si coinciden, caso contrario, <code>false</code>.
	 */
	public static boolean match(Pojo head, Pojo detail) {
		if (head == null || detail == null || head.matchingFields == null || head.matchingFields.length == 0) {
			return false;
		}
		for (String field : head.matchingFields) {
			Object o1 = head.getValue(field);
			Object o2 = detail.getValue(field);
			if (o1 == null || o2 == null || !o1.equals(o2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Agrega todos los valores pasados por parámetro a la lista de valores del pojo.
	 * @param values valores a agregar.
	 */
	protected void putAll(LinkedTreeMap<String, Object> values) {
		for (String key : values.keySet()) {
			if (values.get(key) != null) {
				String value = (String) values.get(key);
				if (!value.trim().equalsIgnoreCase("null")) {
					getValues().put(key, values.get(key));
				}
			}
		}
	}
	
	// GETTERS & SETTERS

	public Object getValue(String key) {
		return values != null ? values.get(key) : null;
	}

	public LinkedTreeMap<String, Object> getValues() {
		if (values == null) {
			values = new LinkedTreeMap<String, Object>();
		}
		return values;
	}

	public void setValues(LinkedTreeMap<String, Object> values) {
		this.values = values;
	}

	public String getTableName() {
		return importTableName;
	}

	public String[] getFields() {
		return filteredFields == null ? new String[0] : filteredFields;
	}

}
