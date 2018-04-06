package org.openXpertya.util;

public class SalesUtil {

	/**
	 * @param baseTableName
	 *            nombre de tabla base donde se encuentran los impuestos del
	 *            documento
	 * @param baseColumnName
	 *            nombre de columna de referencia con el documento
	 * @param baseColumnID
	 *            id de referencia al documento
	 * @param manualTaxes
	 *            true si es la suma de los importes manuales, false caso
	 *            contrario
	 * @return consulta sql con la suma de los importes de impuesto e impuesto
	 *         base del documento
	 */
	public static String getSQLTaxAmountsForTotals(String baseTableName, String baseColumnName, Integer baseColumnID, boolean manualTaxes){
		String sql = "SELECT coalesce(sum(taxamt),0) as taxamt, coalesce(sum(taxbaseamt),0) as taxbaseamt "
				+ " FROM "+baseTableName+" as b "
				+ " INNER JOIN c_tax t on t.c_tax_id = b.c_tax_id "
				+ " INNER JOIN c_taxcategory tc on tc.c_taxcategory_id = t.c_taxcategory_id "
				+ " WHERE b."+baseColumnName+"="+baseColumnID 
				+ " 		AND tc.ismanual = '"+(manualTaxes?"Y":"N")+"' ";
		return sql;
	}
	
	
}
