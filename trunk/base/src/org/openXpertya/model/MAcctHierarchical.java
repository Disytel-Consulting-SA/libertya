package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * Personalización del la clase modelo de la tabla temporal T_Acct_Hierarchical.
 */
public class MAcctHierarchical extends X_T_Acct_Hierarchical {

	public MAcctHierarchical(Properties ctx, int T_Acct_Hierarchical_ID,
			String trxName) {
		super(ctx, T_Acct_Hierarchical_ID, trxName);
	}

	public MAcctHierarchical(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public void setAD_Org_ID(int ad_Org_ID) {
		set_ValueNoCheck("AD_Org_ID", ad_Org_ID);
	}

}