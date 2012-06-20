package org.openXpertya.cc;

import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.PO;

public abstract class CurrentAccountStrategy {

	/**
	 * Obtengo el bpartner a partir del nombre de la columna que identifica
	 * unívocamente a los registros y su valor.
	 * 
	 * @param ctx
	 *            contexto
	 * @param columnNameUID
	 *            nombre de la columna que identifica unívocamente la entidad
	 *            comercial
	 * @param valueUID
	 *            valor de la columna descrita
	 * @param trxName
	 *            nombre de la transacción
	 * @return Entidad Comercial con el valor de esa columna única, null caso
	 *         contrario
	 */
	protected MBPartner getBPartner(Properties ctx, String columnNameUID,
			Object valueUID, String trxName) {
		return (MBPartner) getPO(ctx, MBPartner.Table_Name, columnNameUID,
				valueUID, trxName);
	}

	/**
	 * Obtengo la organización a partir del nombre de la columna que identifica
	 * unívocamente a los registros y su valor.
	 * 
	 * @param ctx
	 *            contexto
	 * @param columnNameUID
	 *            nombre de la columna que identifica unívocamente la entidad
	 *            comercial
	 * @param valueUID
	 *            valor de la columna descrita
	 * @param trxName
	 *            nombre de la transacción
	 * @return Organización con el valor de esa columna única, null caso
	 *         contrario
	 */
	protected MOrg getOrg(Properties ctx, String columnNameUID,
			Object valueUID, String trxName) {
		return (MOrg) getPO(ctx, MOrg.Table_Name, columnNameUID, valueUID,
				trxName);
	}

	/**
	 * Obtengo el PO a partir de la tabla, nombre de la columna que identifica
	 * unívocamente a los registros y su valor.
	 * 
	 * @param ctx
	 *            contexto
	 * @param tableName
	 *            nombre de la tabla
	 * @param columnNameUID
	 *            nombre de la columna que identifica unívocamente la entidad
	 *            comercial
	 * @param valueUID
	 *            valor de la columna descrita
	 * @param trxName
	 *            nombre de la transacción
	 * @return PO de esa tabla con el valor de esa columna única, null caso
	 *         contrario
	 */
	protected PO getPO(Properties ctx, String tableName, String columnNameUID,
			Object valueUID, String trxName) {
		return PO.findFirst(ctx, tableName, columnNameUID + " = ?",
				new Object[] { valueUID }, null, trxName);
	}	
}
