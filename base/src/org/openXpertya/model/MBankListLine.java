package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MBankListLine extends X_C_BankListLine {

	public MBankListLine(Properties ctx, int C_BankListLine_ID, String trxName) {
		super(ctx, C_BankListLine_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBankListLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// No se puede agregar el mismo cheque a la lista
		String additionaWhereClause = newRecord?"":" and c_banklistline_id <> "+getID();
		if (findFirst(getCtx(), get_TableName(), " c_banklist_id = ? and c_payment_id = ?" + additionaWhereClause,
				new Object[] { getC_BankList_ID(), getC_Payment_ID() }, null, get_TrxName()) != null) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "AlreadyExistsSameCheckInBankList"));
			return false;
		}
		return true;
	}
}
