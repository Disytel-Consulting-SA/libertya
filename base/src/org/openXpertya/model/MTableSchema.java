package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MTableSchema extends X_AD_TableSchema {

	/**
	 * Constructor.
	 * @param ctx
	 * @param AD_TableSchema_ID
	 * @param trxName
	 */
	public MTableSchema(Properties ctx, int AD_TableSchema_ID, String trxName) {
		super(ctx, AD_TableSchema_ID, trxName);
	}

	/**
	 * Constructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MTableSchema(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Agrega una nueva línea al esquema referenciando a una tabla determinada.
	 * @param tableID ID de tabla para la línea
	 * @param isInList Indica si la tabla se agrega a la lista o no.
	 * @return Devuelve la instancia de MTableSchemaLine agregada, o null si
	 * no se pudo guardar la línea.
	 */
	public MTableSchemaLine addTable(int tableID, boolean isInList) {
		MTableSchemaLine newLine = null;
		// Solo se permite una línea por tabla.
		if (!contains(tableID)) {
			newLine = new MTableSchemaLine(this);
			newLine.setAD_Table_ID(tableID);
			newLine.setIsInList(isInList);
			// Si no se puede guardar devuelve null (el log contiene le msg de error)
			if (!newLine.save()) {
				newLine = null;
			}
		}
		return newLine;
	}

	/**
	 * Agrega una nueva línea al esquema referenciando a una tabla determinada.
	 * La línea no será agregada a lista ordenada de tablas (IsInList = False).
	 * @param tableID ID de tabla para la línea
	 * @return Devuelve la instancia de MTableSchemaLine agregada, o null si
	 * no se pudo guardar la línea.
	 */
	public MTableSchemaLine addTable(int tableID) {
		return addTable(tableID, false);
	}
	
	/**
	 * Indica si este esquema contiene al menos una línea que referencia
	 * a una determinada tabla.
	 * @param tableID ID de tabla buscada.
	 * @return true/false según corresponda.
	 */
	public boolean contains(int tableID) {
		String sql = 
			"SELECT COUNT(*) " +
			"FROM AD_TableSchemaLine " +
			"WHERE AD_TableSchema_ID = ? AND AD_Table_ID = ?";
		
		Long count = (Long)DB.getSQLObject(get_TrxName(), sql, 
				new Object[] { getAD_TableSchema_ID(), tableID }
		);
		return count != null && count > 0;
	}

}
