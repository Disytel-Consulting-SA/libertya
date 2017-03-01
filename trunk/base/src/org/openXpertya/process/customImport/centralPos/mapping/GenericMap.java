package org.openXpertya.process.customImport.centralPos.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/** @author Kevin Feuerschvenger - Sur Software S.H. */
public class GenericMap {
	/** Campos a almacenar en la DB. */
	protected String[] filteredFields;
	/** Campos por los cuales se realizará el mapeo entre cabecera y lineas.
	 * IMPORTANTE: Estos campos deben estar en "filteredFields" */
	public String[] matchingFields;
	/** Conjunto de datos recuperados desde la API. */
	protected GenericDatum values;
	/** Listado de Conjunto de datos recuperados desde la API. */
	protected List<GenericDatum> valuesList;
	/** Tabla de importación en donde se almacenarán los datos. */
	private String importTableName;

	/**
	 * Constructor.
	 * @param filteredFields Campos a recuperar.
	 * @param values Valores obtenidos desde la API.
	 * @param importTableName Nombre de la tabla de destino.
	 */
	public GenericMap(String[] filteredFields, GenericDatum values, String importTableName) {
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
				// "i_errormsg," +
				"i_isimported," +
				"processing," +
				"processed,";
	}

	@Override
	public String toString() {
		if (filteredFields != null || filteredFields.length == 0) {
			StringBuffer str = new StringBuffer();
			for (String s : filteredFields) {
				str.append(s + ": " + GenericDatum.get(s, values) + ", ");
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
				// "''," + 						// i_errormsg
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

		if (valuesList != null && !valuesList.isEmpty()) {
			Set<String> alreadyAdded = new HashSet<String>();
			for (String field : filteredFields) {
				for (GenericDatum datum : valuesList) {
					if (!alreadyAdded.contains(field)) {
						String refDatum = GenericDatum.get(field, datum);
						if (refDatum != null && !refDatum.trim().isEmpty() && !refDatum.equals("null")) {
							sql.append(field + ",");
							alreadyAdded.add(field);
						}
					}
				}
			}
		} else {
			for (String field : filteredFields) {
				String refDatum = GenericDatum.get(field, values);
				if (refDatum != null && !refDatum.trim().isEmpty() && !refDatum.equals("null")) {
					sql.append(field + ",");
				}
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") VALUES (");

		sql.append(defaultValues(ctx));

		if (valuesList != null && !valuesList.isEmpty()) {
			Set<String> alreadyAdded = new HashSet<String>();
			for (String field : filteredFields) {
				for (GenericDatum datum : valuesList) {
					if (!alreadyAdded.contains(field)) {
						String refDatum = GenericDatum.get(field, datum);
						if (refDatum != null && !refDatum.trim().isEmpty() && !refDatum.equals("null")) {
							sql.append("'" + GenericDatum.get(field, datum) + "',");
							alreadyAdded.add(field);
						}
					}
				}
			}
		} else {
			for (String field : filteredFields) {
				String refDatum = GenericDatum.get(field, values);
				if (refDatum != null && !refDatum.trim().isEmpty() && !refDatum.equals("null")) {
					sql.append("'" + GenericDatum.get(field, values) + "',");
				}
			}
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
	public static boolean match(GenericMap head, GenericMap detail) {
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
	 * A partir de la union de n arrays, crea uno nuevo sin repetir elementos.
	 * @param arrays Arreglos de strings a unir.
	 * @return array con elementos no repetidos.
	 */
	protected static String[] joinArrays(String[]... arrays) {
		if (arrays.length == 0) {
			return new String[0];
		}
		Set<String> tmp = new HashSet<String>();

		for (String[] arr : arrays) {
			for (String str : arr) {
				tmp.add(str);
			}
		}
		return (String[]) tmp.toArray(new String[0]);
	}

	// GETTERS & SETTERS

	public Object getValue(String key) {
		return values != null ? GenericDatum.get(key, values) : null;
	}

	public GenericDatum getValues() {
		return values;
	}

	public void setValues(GenericDatum values) {
		this.values = values;
	}

	public String getTableName() {
		return importTableName;
	}

	public String[] getFields() {
		return filteredFields == null ? new String[0] : filteredFields;
	}

	public List<GenericDatum> getValuesList() {
		return valuesList;
	}

	public void setValuesList(List<GenericDatum> valuesList) {
		this.valuesList = valuesList;
	}

}
