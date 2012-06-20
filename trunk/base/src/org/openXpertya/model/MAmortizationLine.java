package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MAmortizationLine extends X_M_AmortizationLine {

	public MAmortizationLine(Properties ctx, int M_AmortizationLine_ID,
			String trxName) {
		super(ctx, M_AmortizationLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAmortizationLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
