package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
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
	
	private Map<Integer, BigDecimal> getBeforePaymentsAllocatedAmount(){
		Map<Integer, BigDecimal> paysAmt = new HashMap<Integer, BigDecimal>();
		String sqlBeforeAllocations = "select p.c_payment_id, "
				+ "		sum(CASE WHEN allocationtype = 'OPA' THEN p.payamt ELSE 0 END) as payamt, "
				+ "		sum(alo.amount) as allocatedamt "
				+ "from (select distinct bll.c_banklist_id, c_payment_id "
				+ "		from c_banklistline bll"
				+ "		inner join c_allocationline al on al.c_allocationhdr_id = bll.c_allocationhdr_id "
				+ "		where al.c_allocationhdr_id = ?) as bp "
				+ "inner join c_banklist bl on bl.c_banklist_id = bp.c_banklist_id "
				+ "inner join c_payment p on p.c_payment_id = bp.c_payment_id "
				+ "inner join c_allocationline alo on (alo.c_payment_id = p.c_payment_id and alo.c_allocationhdr_id <> ?) "
				+ "inner join c_allocationhdr aho on aho.c_allocationhdr_id = alo.c_allocationhdr_id "
				+ "inner join c_banklist bbl on bbl.c_banklist_id = aho.c_banklist_id "
				+ "where aho.c_banklist_id is not null "
				+ "		AND aho.c_banklist_id <> ? "
				+ "		AND aho.docstatus IN ('CO','CL') "
				+ "		AND bbl.datetrx::date <= bl.datetrx::date "
				+ "group by p.c_payment_id";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sqlBeforeAllocations, get_TrxName(), true);
			ps.setInt(1, getC_AllocationHdr_ID());
			ps.setInt(2, getC_AllocationHdr_ID());
			ps.setInt(3, getC_BankList_ID());
			rs = ps.executeQuery();
			BigDecimal amt;
			while(rs.next()){
				amt = rs.getBigDecimal("payamt");
				paysAmt.put(rs.getInt("c_payment_id"),
						amt.compareTo(BigDecimal.ZERO) > 0 ? amt : rs.getBigDecimal("allocatedamt"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return paysAmt;
	}
	
	private Map<Integer, BigDecimal> getCurrentPaymentsAllocatedAmount(){
		Map<Integer, BigDecimal> paysAmt = new HashMap<Integer, BigDecimal>();
		String sqlCurrentPayments = "SELECT p.c_payment_id, sum(abs((CASE WHEN allocationtype = 'OPA' THEN payamt ELSE allocatedamt END))) as amt "
				+ "FROM (SELECT ah.allocationtype, p.c_payment_id, p.payamt, sum(al.amount) as allocatedamt "
				+ "		FROM c_allocationhdr ah"
				+ "		INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id"
				+ "		INNER JOIN c_payment p ON p.c_payment_id = al.c_payment_id"
				+ "		WHERE ah.c_allocationhdr_id = ? "
				+ "		GROUP BY ah.allocationtype, p.c_payment_id, p.payamt) as p "
				+ "group by p.c_payment_id";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sqlCurrentPayments, get_TrxName());
			ps.setInt(1, getC_AllocationHdr_ID());
			rs = ps.executeQuery();
			while(rs.next()){
				paysAmt.put(rs.getInt("c_payment_id"), rs.getBigDecimal("amt"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return paysAmt;
	}
	
	/**
	 * Actualiza el total de pago electrónico en base al total de pagos de la OP
	 */
	private void updateElectronicPaymentTotal(){
		Map<Integer, BigDecimal> paysAllocatedBefore = getBeforePaymentsAllocatedAmount();
		Map<Integer, BigDecimal> paysCurrentAllocated = getCurrentPaymentsAllocatedAmount();
		BigDecimal electronicTotalAmt = BigDecimal.ZERO;
		BigDecimal currentAmt;
		for (Integer pay : paysCurrentAllocated.keySet()) {
			currentAmt = paysCurrentAllocated.get(pay);
			if(!Util.isEmpty(paysAllocatedBefore.get(pay), true)){
				if(paysAllocatedBefore.get(pay).compareTo(currentAmt) >= 0){
					currentAmt = BigDecimal.ZERO;
				}
				else{
					currentAmt = currentAmt.subtract(paysAllocatedBefore.get(pay));
				}
			}
			electronicTotalAmt = electronicTotalAmt.add(currentAmt);
		}
		
		int no = DB.executeUpdate("UPDATE " + Table_Name + " SET electronicpaymenttotal = " + electronicTotalAmt
				+ " WHERE c_banklistline_id = " + getID(), get_TrxName());
		if(no != 1){
			log.severe(" Error updating bank list ");
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
