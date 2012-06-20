package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MTableSchemaLine extends X_AD_TableSchemaLine {

	/**
	 * Constructor.
	 * @param ctx
	 * @param AD_TableSchemaLine_ID
	 * @param trxName
	 */
	public MTableSchemaLine(Properties ctx, int AD_TableSchemaLine_ID,
			String trxName) {
		super(ctx, AD_TableSchemaLine_ID, trxName);
	}

	/**
	 * Constructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MTableSchemaLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Constructor. Asocia la línea con un esquema de tabla.
	 * @param tableSchema
	 */
	public MTableSchemaLine(MTableSchema tableSchema) {
		super(tableSchema.getCtx(), 0, tableSchema.get_TrxName());
		// Valores por defecto.
		setClientOrg(tableSchema);
		setAD_TableSchema_ID(tableSchema.getAD_TableSchema_ID());
		setIsInList(false);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Valor por defecto para el nombre de la tabla. Se obtiene
		// a partir de la tabla referenciada por esta línea.
		if (getTableName() == null) {
			String tableName = DB.getSQLValueString(get_TrxName(), 
				"SELECT TableName FROM AD_Table WHERE AD_Table_ID = ?", getAD_Table_ID()
			);
			setTableName(tableName);
		}
		return true;
	}

}
