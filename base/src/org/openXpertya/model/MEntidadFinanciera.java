package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;


public class MEntidadFinanciera extends X_M_EntidadFinanciera {

	public MEntidadFinanciera (Properties ctx, int M_EntidadFinanciera_ID, String trxName) {
		super(ctx, M_EntidadFinanciera_ID, trxName);
	}
	
	public MEntidadFinanciera (Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

}
