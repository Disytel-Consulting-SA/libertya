package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MSumsAndBalance extends X_T_SumsAndBalance {

	public MSumsAndBalance(Properties ctx, int T_SumsAndBalance_ID,
			String trxName) {
		super(ctx, T_SumsAndBalance_ID, trxName);
	}

	public MSumsAndBalance(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public void setAD_Org_ID(int ad_Org_ID) {
		set_ValueNoCheck("AD_Org_ID", ad_Org_ID);
	}
}
