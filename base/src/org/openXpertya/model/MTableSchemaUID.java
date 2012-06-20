package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MTableSchemaUID extends X_AD_TableSchemaUID {

	/**
	 * Constructor.
	 * @param ctx
	 * @param AD_TableSchemaUID_ID
	 * @param trxName
	 */
	public MTableSchemaUID(Properties ctx, int AD_TableSchemaUID_ID,
			String trxName) {
		super(ctx, AD_TableSchemaUID_ID, trxName);
	}

	/**
	 * Constructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MTableSchemaUID(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
