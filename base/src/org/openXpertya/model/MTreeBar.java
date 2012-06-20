package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MTreeBar extends X_AD_TreeBar {

	
	public MTreeBar(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	public MTreeBar(Properties ctx, int AD_TreeBar_ID, String trxName) {
		super(ctx, AD_TreeBar_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public void setClientOrg(int idClient, int idOrg)
	{
		setAD_Client_ID(idClient);
		setAD_Org_ID(idOrg);
	}
	
}
