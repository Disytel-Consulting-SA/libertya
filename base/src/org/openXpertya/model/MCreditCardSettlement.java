package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;

/**
 * Liquidacion de tarjetas.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MCreditCardSettlement extends X_C_CreditCardSettlement implements DocAction {
	private static final long serialVersionUID = 1L;

	private boolean m_justPrepared = false;
	/**
	 * Load contructor.
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MCreditCardSettlement(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	/**
	 * Constructor standar.
	 * @param ctx
	 * @param C_CreditCardSettlement_ID
	 * @param trxName
	 */
	public MCreditCardSettlement(Properties ctx, int C_CreditCardSettlement_ID, String trxName) {
		super(ctx, C_CreditCardSettlement_ID, trxName);
		if (C_CreditCardSettlement_ID == 0) {
			setDocAction(DOCACTION_Complete); // CO
			setDocStatus(DOCSTATUS_Drafted); // DR

			setIsApproved(false);
			setPosted(false);
			setProcessed(false);
			setProcessing(false);
		}
	}

	@Override
	protected boolean beforeDelete() {
		String trxName = get_TrxName();
		if ((trxName == null) || (trxName.length() == 0)) {
			log.warning("No transaction");
		}
		if (isPosted()) {
			if (!MPeriod.isOpen(getCtx(), getPaymentDate(), MDocType.DOCBASETYPE_PaymentAllocation)) {
				log.warning("Period Closed");
				return false;
			}
			setPosted(false);
			if (MFactAcct.delete(Table_ID, getID(), trxName) < 0) {
				return false;
			}
		}
		setIsActive(false);
		return true;
	}

	/**
	 * Operaciones luego de procesar el documento
	 * @param processAction
	 * @param status
	 * @return
	 */
	public boolean afterProcessDocument(String processAction, boolean status) {
		if ((MCreditCardSettlement.DOCACTION_Complete.equals(processAction) || 
				MCreditCardSettlement.DOCACTION_Reverse_Correct.equals(processAction) || 
				MCreditCardSettlement.DOCACTION_Void.equals(processAction)) && status) {
			if (!save()) {
				log.severe(CLogger.retrieveErrorAsString());
			}
		}
		return true;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		boolean status = engine.processIt(action, getDocAction(), log);
		status = this.afterProcessDocument(engine.getDocAction(), status) && status;
		return status;
	}

	@Override
	public boolean unlockIt() {
		log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	}

	@Override
	public boolean invalidateIt() {
		log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	}

	@Override
	public String prepareIt() {
		log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);

		if (m_processMsg != null) {
			return DocAction.STATUS_Invalid;
		}

		if (!MPeriod.isOpen(getCtx(), getPaymentDate(), MDocType.DOCBASETYPE_PaymentAllocation)) {
			m_processMsg = "@PeriodClosed@";

			return DocAction.STATUS_Invalid;
		}

		m_justPrepared = true;

		if (!DOCACTION_Complete.equals(getDocAction())) {
			setDocAction(DOCACTION_Complete);
		}
		return DocAction.STATUS_InProgress;
	}

	@Override
	public boolean approveIt() {
		log.info("approveIt - " + toString());
		setIsApproved(true);
		return true;
	}

	@Override
	public boolean rejectIt() {
		log.info("rejectIt - " + toString());
		setIsApproved(false);
		return true;
	}

	private void changeCouponsAuditStatus(String status) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT DISTINCT ");
		sql.append("	P.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_CreditCardCouponFilter.Table_Name + " f ");
		sql.append("	INNER JOIN " + X_C_CouponsSettlements.Table_Name + " c ");
		sql.append("		ON c.c_creditcardcouponfilter_id = f.c_creditcardcouponfilter_id ");
		sql.append("	INNER JOIN " + X_C_Payment.Table_Name + " p ");
		sql.append("		ON p.c_payment_id = c.c_payment_id ");
		sql.append("WHERE ");
		sql.append("	f.c_creditcardsettlement_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();
			if (rs.next()) {
				// Los cupones incluidos en la liquidación cambian su estado auditoría.
				sql = new StringBuffer();
				sql.append("UPDATE ");
				sql.append("	" + X_C_Payment.Table_Name + " ");
				sql.append("SET ");
				sql.append("	auditstatus = '" + status + "' ");
				sql.append("WHERE ");
				sql.append("	c_payment_id = " + rs.getInt(1));

				DB.executeUpdate(sql.toString());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "MCreditCardSettlement.changeCouponsAuditStatus", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	@Override
	public String completeIt() {
		// Re-Check
		if (!m_justPrepared && !existsJustPreparedDoc()) {
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status)) {
				return status;
			}
		}
		// Implicit Approval
		if (!isApproved()) {
			approveIt();
		}

		// User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);

		if (valid != null) {
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}

		changeCouponsAuditStatus(X_C_Payment.AUDITSTATUS_Paid);

		// Genera el pago correspondiente.
		MPayment payment = new MPayment(getCtx(), 0, get_TrxName());

		payment.setAmount(getC_Currency_ID(), getAmount());

		X_M_EntidadFinanciera ef = new X_M_EntidadFinanciera(getCtx(), getM_EntidadFinanciera_ID(), get_TrxName());

		payment.setC_BPartner_ID(ef.getC_BPartner_ID());

		// Obtengo el tipo de documento "Cobro a Cliente".
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_DocType_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_DocType.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	ad_client_id = " + getAD_Client_ID() + " ");
		sql.append("	AND doctypekey = 'CR' ");

		int C_DocType_ID = DB.getSQLValue(get_TrxName(), sql.toString());
		payment.setC_DocType_ID(C_DocType_ID);

		// Tipo de Pago: Transferencia
		payment.setTenderType(MPayment.TENDERTYPE_DirectDeposit);

		payment.setC_BankAccount_ID(ef.getC_BankAccount_ID());

		if (!payment.save()) {
			CLogger.retrieveErrorAsString();
			setDocAction(DOCACTION_Complete);
			return DocAction.STATUS_Drafted;
		}

		BigDecimal payAmt = payment.getPayAmt();
		payAmt = payAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN);

		X_C_Currency currency = new X_C_Currency(getCtx(), getC_Currency_ID(), get_TrxName());

		// Genera el campo de texto "Pago" con: Nro Transferencia, Importe, Fecha de Emisión, Fecha Contable.
		// Ej: Nro Transf.: 965389 - Importe: $ 190.31 - F.Emisión: 25/05/1810 - F.Contable: 25/05/1810
		setPayment("Nro Transf.: " + payment.getDocumentNo() +
				" - Importe: " + currency.getCurSymbol() + " " + payAmt +
				" - F.Emisión: " + payment.getDateTrx() +
				" - F.Contable: " + payment.getDateAcct());

		if (!save()) {
			CLogger.retrieveErrorAsString();
			setDocAction(DOCACTION_Complete);
			return DocAction.STATUS_Drafted;
		}
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		log.info("postIt - " + toString());
		return false;
	}

	private BigDecimal negativeValue(BigDecimal input) {
		if (input == null) {
			return BigDecimal.ZERO;
		}
		final BigDecimal negativeOperationSymbol = new BigDecimal(-1);
		return input.multiply(negativeOperationSymbol);
	}

	@Override
	public boolean voidIt() {
		log.info("voidIt - " + toString());
		if (DOCSTATUS_Closed.equals(getDocStatus()) || 
				DOCSTATUS_Reversed.equals(getDocStatus()) || 
				DOCSTATUS_Voided.equals(getDocStatus())) {

			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		changeCouponsAuditStatus(X_C_Payment.AUDITSTATUS_ToVerify);

		MCreditCardSettlement copy = new MCreditCardSettlement(getCtx(), 0, get_TrxName());

		copy.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID());
		copy.setPaymentDate(getPaymentDate());
		copy.setPayment(getPayment());
		copy.setAmount(negativeValue(getAmount()));
		copy.setNetAmount(negativeValue(getNetAmount()));
		copy.setWithholding(negativeValue(getWithholding()));
		copy.setPerception(negativeValue(getPerception()));
		copy.setExpenses(negativeValue(getExpenses()));
		copy.setCouponsTotalAmount(negativeValue(getCouponsTotalAmount()));
		copy.setDocStatus(DOCSTATUS_Closed);
		copy.setIsReconciled(isReconciled());
		copy.setPosted(isPosted());
		copy.setDocAction(DOCACTION_None);
		copy.setC_Currency_ID(getC_Currency_ID());
		copy.setSettlementNo(getSettlementNo());
		copy.setIVAAmount(getIVAAmount());
		copy.setCommissionAmount(getCommissionAmount());

		if (!copy.save()) {
			CLogger.retrieveErrorAsString();
			return false;
		}
		setSettlementNo("^" + getSettlementNo());
		if (!save()) {
			CLogger.retrieveErrorAsString();
			return false;
		}

		// Desvincula los cupones
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_CouponsSettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	c_creditcardsettlement_id = " + getC_CreditCardSettlement_ID());

		DB.executeUpdate(sql.toString(), get_TrxName());

		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean closeIt() {
		log.info(toString());
		setProcessed(true);
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		return false;
	}

	@Override
	public boolean reverseAccrualIt() {
		return false;
	}

	@Override
	public boolean reActivateIt() {
		return false;
	}

	@Override
	public String getSummary() {
		return null;
	}

	@Override
	public int getDoc_User_ID() {
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return getNetAmount();
	}

}
