package org.openXpertya.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Column;

public class DisplayUtil {

	/**
	 * Obtiene el display de los valores de PO en las columnas identificadoras
	 * 
	 * @param ctx
	 *            contexto
	 * @param po
	 *            po con los valores a sacar
	 * @param tableID
	 *            id de la tabla del po
	 * @param trxName
	 *            nombre de la transacción
	 * @return display de los valores a partir de los identificadores de la
	 *         tabla
	 */
	public static String getDisplayByIdentifiers(Properties ctx, PO po, Integer tableID, String trxName){
		StringBuffer display = new StringBuffer();
		X_AD_Column col;
		// Buscar las columnas que son identificadoras de la tabla parámetro
		List<PO> columnsIdentifiers = PO
				.find(ctx, X_AD_Column.Table_Name,
						"ad_table_id = ? AND isidentifier = 'Y'",
						new Object[] { tableID }, new String[]{"seqno"},
						trxName);
		// Itero por todos los identificadores y obtengo el string completo de
		// display
		for (PO column : columnsIdentifiers) {
			col = (X_AD_Column)column;
			display.append(po.get_DisplayValue(col.getColumnName(),
					po.get_Value(col.getColumnName()) != null)).append("_");
		}
		// Sacar el _ final
		if(display.indexOf("_") > -1){
			display.deleteCharAt(display.lastIndexOf("_"));
		}
		return display.toString();
	}
	
}
