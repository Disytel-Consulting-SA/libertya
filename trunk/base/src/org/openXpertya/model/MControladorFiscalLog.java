package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MControladorFiscalLog extends X_C_Controlador_Fiscal_Log {

	public MControladorFiscalLog(Properties ctx,
			int C_Controlador_Fiscal_Log_ID, String trxName) {
		super(ctx, C_Controlador_Fiscal_Log_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MControladorFiscalLog(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
}
