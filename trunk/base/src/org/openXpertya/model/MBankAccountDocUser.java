package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MBankAccountDocUser extends X_C_BankAccountDocUser {

	public MBankAccountDocUser(Properties ctx, int C_BankAccountDocUser_ID, String trxName) {
		super(ctx, C_BankAccountDocUser_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBankAccountDocUser(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Si el documento de cuenta bancaria no se encuentra configurada para
		// asignación de usuarios, error
		X_C_BankAccountDoc bad = new X_C_BankAccountDoc(getCtx(), getC_BankAccountDoc_ID(), get_TrxName());
		if (!bad.isUserAssigned()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "BankAccountDocIsNotUserAssigned"));
			return false;
		}
		return true;
	}
}
