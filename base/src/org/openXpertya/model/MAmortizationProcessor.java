package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MAmortizationProcessor extends X_M_Amortization_Processor {

	public MAmortizationProcessor(Properties ctx,
			int M_Amortization_Processor_ID, String trxName) {
		super(ctx, M_Amortization_Processor_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAmortizationProcessor(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
