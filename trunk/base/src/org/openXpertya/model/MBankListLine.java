package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

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

	
	protected boolean afterSave( boolean newRecord,boolean success ) {
		if( !success ) {
            return success;
        }
		// Actualizar el total de pagos electrónicos de la OP
		updateElectronicPaymentTotal();
		// Actualiza el total de la lista
		updateBankListTotal();
		return success;
	}
	
	protected boolean afterDelete( boolean success ) {
		if( !success ) {
            return success;
        }
		// Actualiza el total de la lista
		updateBankListTotal();
		return success;
	}
	
	/**
	 * Actualiza el total de pago electrónico en base al total de pagos de la OP
	 */
	private void updateElectronicPaymentTotal(){
		String sql = "UPDATE "+Table_Name
				+ " SET electronicpaymenttotal = coalesce("
				+ "(SELECT sum(abs(p.payamt)) "
				+ "FROM "+X_C_AllocationHdr.Table_Name+" ah "
				+ "INNER JOIN "+X_C_AllocationLine.Table_Name+" al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "INNER JOIN "+X_C_Payment.Table_Name+" p ON p.c_payment_id = al.c_payment_id "
				+ "WHERE ah.c_allocationhdr_id = "+getC_AllocationHdr_ID()+"),0) "
				+ "WHERE C_BankListLine_ID = "+getID();
	
		int no = DB.executeUpdate(sql, get_TrxName());
		if(no != 1){
			log.severe(" Error updating bank list");
		}
	} 
	
	private void updateBankListTotal(){
		String sql = "UPDATE "+MBankList.Table_Name
					+ " SET banklisttotal = coalesce("
					+ "(SELECT sum(electronicpaymenttotal) "
					+ "FROM "+Table_Name+" bll "
					+ "WHERE bll.C_BankList_ID = "+getC_BankList_ID()+"),0) "
					+ "WHERE C_BankList_ID = "+getC_BankList_ID();
		
		int no = DB.executeUpdate(sql, get_TrxName());
		if(no != 1){
			log.severe(" Error updating bank list");
		}
	}
}
