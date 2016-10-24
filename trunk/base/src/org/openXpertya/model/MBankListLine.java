package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MBankListLine extends X_C_BankListLine {
	private static final long serialVersionUID = 1L;

	public MBankListLine(Properties ctx, int C_BankListLine_ID, String trxName) {
		super(ctx, C_BankListLine_ID, trxName);
	}

	public MBankListLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// No se puede agregar la misma orden de pago a la lista
		if (allocationHdrInList()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "AlreadyExistsSameAllocationInBankList"));
			return false;
		}
		
//		Verificar que la OP solo tenga pagos de tipo cheques de la misma cta. bancaria que 
//	    la cabecera y opcionalmente retenciones, que no tenga otros pagos.
		if (checkAllocationWithOtherPayments()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "AllocationWithOtherBankAccountPayments"));
			return false;
		}
		
		return true;
	}
	
	private boolean allocationHdrInList() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) ");
		sql.append("FROM C_BankListLine ");
		sql.append("WHERE C_BankList_ID = " + getC_BankList_ID());
		sql.append(" AND C_AllocationHdr_ID = ?");

		int linesCount = DB.getSQLValue(get_TrxName(), sql.toString(), getC_AllocationHdr_ID());
		return linesCount > 0;
	}
	
	private boolean checkAllocationWithOtherPayments() {
		//Obtengo la cabecera de la lista
		MBankList bankList = new MBankList(getCtx(), getC_BankList_ID(), get_TrxName()); 
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT count (al.*) ");
		sql.append("FROM c_allocationhdr ah ");
		sql.append("INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id ");
		sql.append("LEFT JOIN c_payment p ON al.c_payment_id = p.c_payment_id ");
		sql.append("WHERE ah.c_allocationhdr_id = " + getC_AllocationHdr_ID());
		sql.append(" AND (al.c_cashline_id IS NOT NULL OR p.c_bankaccount_id != ?)");

		int linesCount = DB.getSQLValue(get_TrxName(), sql.toString(), bankList.getC_BankAccount_ID());
		return linesCount > 0;
	}

}
