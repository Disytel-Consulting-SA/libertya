package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MMicroComponentPrefix extends X_AD_MicroComponentPrefix {

	public MMicroComponentPrefix(Properties ctx,
			int AD_MicroComponentPrefix_ID, String trxName) {
		super(ctx, AD_MicroComponentPrefix_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MMicroComponentPrefix(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Solo caracteres permitidos
		for (String forbiddenChar : MComponent.FORBIDDEN_PREFIX_CHARARCTERS) {
			if (getPrefix().contains(forbiddenChar)) {
				log.saveError("Error", "El prefijo no puede contener el caracter: " + forbiddenChar);
				return false;
			}
		}
		return true;
	}
	
}
