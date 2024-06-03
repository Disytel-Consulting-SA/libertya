package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MFixedTermRetention extends X_C_FixedTermRetention {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4906110257897948320L;
	
	public MFixedTermRetention(Properties ctx, int C_FixedTerm_ID, String trxName) {
		super(ctx, C_FixedTerm_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MFixedTermRetention(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}	
	
	protected boolean afterSave(boolean newRecord, boolean success) {
		MFixedTerm fixedTerm = new MFixedTerm(getCtx(), getC_FixedTerm_ID(), get_TrxName());
		fixedTerm.calculateAndSetRetentionAmt();
		if (!fixedTerm.save()) {
			log.saveError("SaveError", Msg.translate(getCtx(), "FixedTermRetentionCalculationError"));
			return false;
		}
		
		return true;
	}

}
