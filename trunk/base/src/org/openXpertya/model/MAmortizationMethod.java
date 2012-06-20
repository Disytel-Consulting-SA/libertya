package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MAmortizationMethod extends X_M_Amortization_Method {

	public static String getApplication(Integer amortizationMethodID, String trxName){
		return DB.getSQLValueString(
				trxName,
				"SELECT amortizationappperiod FROM m_amortization_method WHERE m_amortization_method_id = ?",
				amortizationMethodID);
	}
	
	public MAmortizationMethod(Properties ctx, int M_Amortization_Method_ID,
			String trxName) {
		super(ctx, M_Amortization_Method_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MAmortizationMethod(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
