package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MFactAcctBalanceConfig extends X_Fact_Acct_Balance_Config {

	public MFactAcctBalanceConfig(Properties ctx, int Fact_Acct_Balance_Config_ID, String trxName) {
		super(ctx, Fact_Acct_Balance_Config_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MFactAcctBalanceConfig(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave( boolean newRecord ) {
		MAcctSchema as = MAcctSchema.get(getCtx(), getC_AcctSchema_ID());
		// Ya existe la misma columna para el esquema contable
		String columnWhereClause = newRecord?"":" and fact_acct_balance_config_id <> "+getID();
		if (PO.existRecordFor(getCtx(), get_TableName(), "C_AcctSchema_ID = ? and ad_column_id = ?" + columnWhereClause,
				new Object[] { getC_AcctSchema_ID(), getAD_Column_ID() }, get_TrxName())) {
			log.saveError("SaveError", "SameFactAcctBalanceColumn");
			return false;
		}
		// El balance contable debe estar activo para el esquema actual
		if(!as.isFactAcctBalanceActive()){
			log.saveError("SaveError", Msg.getMsg(getCtx(), "FactBalanceInactive"));
			return false;
		}
		return true;
	}
	
}
