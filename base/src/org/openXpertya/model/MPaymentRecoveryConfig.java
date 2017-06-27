package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Env;

public class MPaymentRecoveryConfig extends X_C_Payment_Recovery_Config {

	/**
	 * Obtener la configuración de cupones
	 * 
	 * @param ctx
	 * @param orgID
	 * @param trxName
	 * @return
	 */
	public static MPaymentRecoveryConfig get(Properties ctx, Integer orgID, String trxName) {
		return (MPaymentRecoveryConfig) PO.findFirst(ctx, Table_Name,
				"ad_client_id = ? and ad_org_id = ? and isactive = 'Y'",
				new Object[] { Env.getAD_Client_ID(ctx), orgID }, null, trxName);
	}
	
	public MPaymentRecoveryConfig(Properties ctx, int C_Payment_Recovery_Config_ID, String trxName) {
		super(ctx, C_Payment_Recovery_Config_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MPaymentRecoveryConfig(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
