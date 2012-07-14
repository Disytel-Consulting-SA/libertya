package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MProductLines extends X_M_Product_Lines {

	public MProductLines(Properties ctx, int M_Product_Lines_ID, String trxName) {
		super(ctx, M_Product_Lines_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MProductLines(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
