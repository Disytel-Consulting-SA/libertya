package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

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
	public boolean doAfterSave(boolean newRecord, boolean success) {
		if (newRecord && !getDocStatus().equals(DOCSTATUS_Voided)) {
			generateAllChildrens();
		}
		//Si marco la liquidación como conciliada, pongo los cupones como procesados
		//para que ya no puedan utilizarse el "incluir", caso contrario lo pongo "no procesados"
		setCouponsProcessed(isReconciled());
		return true;
	}
	
	private void setCouponsProcessed(boolean procesed) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + MCouponsSettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ?");
		sql.append("	AND include = 'Y'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();

			while (rs.next()) {
				MCouponsSettlements coupon = new MCouponsSettlements(getCtx(), rs, get_TrxName());
				coupon.setProcessed(procesed);
				coupon.setReconciledFlag(true);
				if (!coupon.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "reconciledSettlementError", e);
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
	protected boolean beforeSave(boolean newRecord) {
		int found = 0;

		// Validación de unicidad mediante Entidad Comercial y número de liquidación.
		if (newRecord) {
			StringBuffer sql = new StringBuffer();
	
			sql.append("SELECT ");
			sql.append("	COUNT(C_CreditCardSettlement_ID) ");
			sql.append("FROM ");
			sql.append("	" + Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_BPartner_ID = ? ");
			sql.append("	AND SettlementNo = ? ");
	
			found = DB.getSQLValue(get_TrxName(), sql.toString(), getC_BPartner_ID(), getSettlementNo());
	
			if (found != 0) {
				log.saveError("SaveError", Msg.getMsg(getCtx(), "CreditCardSettlementDuplicated"));
			}
			
		}
		
		// Si se marca conciliado, todos los cupones deben estar incluídos y
		// conciliados
		if(!newRecord && is_ValueChanged("IsReconciled") && isReconciled()){
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	count(*) ");
			sql.append("FROM ");
			sql.append("	" + MCouponsSettlements.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	C_CreditCardSettlement_ID = ? ");
			sql.append("	AND include = 'Y' ");
			sql.append("	AND isreconciled = 'N' ");
			
			int no = DB.getSQLValue(get_TrxName(), sql.toString(), getID());
			if(no > 0){
				log.saveError("SaveError", Msg.getMsg(getCtx(), "ExistsNotReconciledIncludedCoupons"));
				return false;
			}
		}
		
		//Validación para que el número de liquidación solo pueda ser numérico
		if (!Util.isEmpty(getSettlementNo(), true) && !getSettlementNo().matches("\\^?\\d*\\^?")) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "SettlementNumberMustBeNumeric"));
			found = 1;
		}
		
		return found == 0;
	}

	/**
	 * Para una entidad comercial de tipo "tarjeta" (Las que están asociadas a alguna
	 * E. Financiera), recupera la cuenta de banco de la E.Financiera más reciente 
	 * @param tarjeta
	 * @return
	 */
	private Integer getBankAccountId(MBPartner tarjeta) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT  ");
		sql.append("	ef.c_bankaccount_id ");
		sql.append("FROM  ");
		sql.append("	" + MBPartner.Table_Name + " bp ");
		sql.append("	INNER JOIN " + MEntidadFinanciera.Table_Name + " ef ON ef.c_bpartner_id = bp.c_bpartner_id ");
		sql.append("WHERE  ");
		sql.append("	bp.c_bpartner_id = ? ");
		sql.append("ORDER BY ");
		sql.append("	ef.updated DESC ");
		sql.append("LIMIT 1; ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, tarjeta.getC_BPartner_ID());
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getBankAccountId", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}

		return null;
	}

	private void makeIva() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_Tax_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_Tax.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	IsPercepcion = 'N' "); // Impuestos que no sean percepción.
		sql.append("	AND IsActive = 'Y' ");
		sql.append("	AND Ad_Client_Id = " + Env.getAD_Client_ID(getCtx()));

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				X_C_IVASettlements children = new X_C_IVASettlements(getCtx(), 0, get_TrxName());
				children.setAD_Client_ID(getAD_Client_ID());
				children.setAD_Org_ID(getAD_Org_ID());
				children.setC_CreditCardSettlement_ID(getC_CreditCardSettlement_ID());
				children.setC_Tax_ID(rs.getInt(1));
				children.setAmount(BigDecimal.ZERO);

				if (!children.save()) {
					CLogger.retrieveErrorAsString();
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "generateAllChildrens", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	private void makePerceptions() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_Tax_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_Tax.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	IsPercepcion = 'Y' "); // Impuesto de tipo percepción.
		sql.append("	AND IsActive = 'Y' ");
		sql.append("	AND Ad_Client_Id = " + Env.getAD_Client_ID(getCtx()));

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			int number = 10000;

			while (rs.next()) {

				X_C_PerceptionsSettlement children = new X_C_PerceptionsSettlement(getCtx(), 0, get_TrxName());
				children.setAD_Client_ID(getAD_Client_ID());
				children.setAD_Org_ID(getAD_Org_ID());
				children.setC_CreditCardSettlement_ID(getC_CreditCardSettlement_ID());
				children.setInternalNo(String.valueOf(number));
				children.setC_Tax_ID(rs.getInt(1));
				children.setAmount(BigDecimal.ZERO);

				if (!children.save()) {
					CLogger.retrieveErrorAsString();
				} else {
					number++;
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "generateAllChildrens", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	private void makeWithholdings() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ");
		sql.append("	" + X_C_RetencionSchema.Table_Name);
		sql.append(" WHERE ");
		sql.append("	RetencionApplication = 'S' "); // Solo del tipo "Retencion Sufrida".
		sql.append("	AND IsActive = 'Y' "); // Filtro los esquemas activos.
		sql.append("	AND Ad_Client_Id = " + Env.getAD_Client_ID(getCtx()));

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			rs = ps.executeQuery();

			while (rs.next()) {

				X_C_WithholdingSettlement children = new X_C_WithholdingSettlement(getCtx(), 0, get_TrxName());
				children.setAD_Client_ID(getAD_Client_ID());
				children.setAD_Org_ID(getAD_Org_ID());
				children.setC_CreditCardSettlement_ID(getC_CreditCardSettlement_ID());
				MRetencionSchema retSchema = new MRetencionSchema(getCtx(), rs, get_TrxName()); 
				children.setC_RetencionSchema_ID(retSchema.getID());
				children.setC_Region_ID(retSchema.getC_Region_ID());
				children.setAmount(BigDecimal.ZERO);

				if (!children.save()) {
					CLogger.retrieveErrorAsString();
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "generateAllChildrens", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	private void makeCommissionConcepts() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_CardSettlementConcepts_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_CardSettlementConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	Type = ? ");
		sql.append("	AND Ad_Client_Id = " + Env.getAD_Client_ID(getCtx()));
		sql.append("	AND IsActive = 'Y'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, X_C_CardSettlementConcepts.TYPE_Commission);
			rs = ps.executeQuery();

			while (rs.next()) {

				X_C_CommissionConcepts children = new X_C_CommissionConcepts(getCtx(), 0, get_TrxName());
				children.setAD_Client_ID(getAD_Client_ID());
				children.setAD_Org_ID(getAD_Org_ID());
				children.setC_CreditCardSettlement_ID(getC_CreditCardSettlement_ID());
				children.setC_CardSettlementConcepts_ID(rs.getInt(1));
				children.setAmount(BigDecimal.ZERO);

				if (!children.save()) {
					CLogger.retrieveErrorAsString();
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "generateAllChildrens", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	private void makeExpenseConcepts() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_CardSettlementConcepts_ID ");
		sql.append("FROM ");
		sql.append("	" + X_C_CardSettlementConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	Type = ? ");
		sql.append("	AND Ad_Client_Id = " + Env.getAD_Client_ID(getCtx()));
		sql.append("	AND IsActive = 'Y'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, X_C_CardSettlementConcepts.TYPE_Others);
			rs = ps.executeQuery();

			while (rs.next()) {

				X_C_ExpenseConcepts children = new X_C_ExpenseConcepts(getCtx(), 0, get_TrxName());
				children.setAD_Client_ID(getAD_Client_ID());
				children.setAD_Org_ID(getAD_Org_ID());
				children.setC_CreditCardSettlement_ID(getC_CreditCardSettlement_ID());
				children.setC_Cardsettlementconcepts_ID(rs.getInt(1));
				children.setAmount(BigDecimal.ZERO);

				if (!children.save()) {
					CLogger.retrieveErrorAsString();
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "generateAllChildrens", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
	}

	/**
	 * Al generarse el registro de Liquidación de Tarjetas debe generarse automáticamente
	 * los registros en estas pestañas para que el usuario luego cargue los valores, y
	 * deje en Cero los que no corresponden.
	 */
	public void generateAllChildrens() {
		makeIva();
		makePerceptions();
		makeWithholdings();
		makeCommissionConcepts();
		makeExpenseConcepts();
	}

	/**
	 * Al completar la Liquidación todos los
	 * registros en cero se deben eliminar
	 */
	public void removeUnusedChildrens() {

		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_IVASettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());
		sql.append("	AND Amount = 0 ");

		DB.executeUpdate(sql.toString());

		sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_PerceptionsSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());
		sql.append("	AND Amount = 0 ");

		DB.executeUpdate(sql.toString());

		sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_WithholdingSettlement.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());
		sql.append("	AND Amount = 0 ");

		DB.executeUpdate(sql.toString());

		sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_CommissionConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());
		sql.append("	AND Amount = 0 ");

		DB.executeUpdate(sql.toString());

		sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_ExpenseConcepts.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());
		sql.append("	AND Amount = 0 ");

		DB.executeUpdate(sql.toString());
	}

	public void calculateSettlementCouponsTotalAmount(String trxName) {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	amount ");
		sql.append("FROM ");
		sql.append("	" + MCouponsSettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = ?");
		sql.append("	AND include = 'Y'");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString(), trxName);
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();

			BigDecimal amt = BigDecimal.ZERO;

			while (rs.next()) {
				amt = amt.add(rs.getBigDecimal("amount"));
			}

			X_C_CreditCardSettlement settlement = new X_C_CreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), trxName);
			settlement.setCouponsTotalAmount(amt);
			if (!settlement.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}

		} catch (Exception e) {
			log.log(Level.SEVERE, "calculateSettlementCouponsTotalAmount", e);
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
		sql.append("	c.c_payment_id ");
		sql.append("FROM ");
		sql.append("	" + X_C_CreditCardCouponFilter.Table_Name + " f ");
		sql.append("	INNER JOIN " + X_C_CouponsSettlements.Table_Name + " c ");
		sql.append("		ON c.c_creditcardcouponfilter_id = f.c_creditcardcouponfilter_id ");
		sql.append("WHERE ");
		sql.append("	f.c_creditcardsettlement_id = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			ps.setInt(1, getC_CreditCardSettlement_ID());
			rs = ps.executeQuery();
			while (rs.next()) {
				// Los cupones incluidos en la liquidación cambian su estado auditoría.
				sql = new StringBuffer();
				sql.append("UPDATE ");
				sql.append("	" + X_C_Payment.Table_Name + " ");
				sql.append("SET ");
				sql.append("	auditstatus = '" + status + "' ");
				sql.append("WHERE ");
				sql.append("	c_payment_id = " + rs.getInt(1));

				DB.executeUpdate(sql.toString(), get_TrxName());
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

	public void setPayment(String documentNo, String currencySymbol, String amount, String dateTrx, String dateAcct) {
		// Genera el campo de texto "Pago" con: Nro Transferencia, Importe, Fecha de Emisión, Fecha Contable.
		// Ej: Nro Transf.: 965389 - Importe: $ 190.31 - F.Emisión: 25/05/1810 - F.Contable: 25/05/1810
		setPayment("Nro Transf.: " + documentNo + " - Importe: " + currencySymbol + " " + amount +
				" - F.Emisión: " + dateTrx + " - F.Contable: " + dateAcct);
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

		// Validación de diferencias.
		// El importe bruto debe ser igual al importe acreditado mas todos los descuentos.

		// Importe bruto
		BigDecimal amt1 = getAmount();
		amt1.setScale(2, BigDecimal.ROUND_HALF_UP);

		// Importe acreditado
		BigDecimal amt2 = getNetAmount();
		amt2 = amt2.add(getIVAAmount());
		amt2 = amt2.add(getPerception());
		amt2 = amt2.add(getWithholding());
		amt2 = amt2.add(getCommissionAmount());
		amt2 = amt2.add(getExpenses());
		amt2.setScale(2, BigDecimal.ROUND_HALF_UP);

		boolean validSettlement = amt1.equals(amt2);
		if (!validSettlement) {
			m_processMsg = Msg.getMsg(Env.getAD_Language(getCtx()), "CreditCardSettlementAmountsMismatch");
			return DocAction.STATUS_Invalid;
		}
		removeUnusedChildrens();

		// Si el acreditado es 0, entonces no se debe generar un payment
		if(!Util.isEmpty(getNetAmount(), true)){
			// Genera el pago correspondiente.
			MPayment payment = new MPayment(getCtx(), 0, get_TrxName());

			payment.setAD_Client_ID(getAD_Client_ID());
			payment.setAD_Org_ID(getAD_Org_ID());

			// Monto = Importe acreditado.
			payment.setAmount(getC_Currency_ID(), getNetAmount());

			// Fecha del pago es la misma que la liquidación.
			payment.setDateAcct(getPaymentDate());
			payment.setDateTrx(getPaymentDate());

			MBPartner bpartner = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());

			payment.setC_BPartner_ID(bpartner.getC_BPartner_ID());

			// Obtengo el tipo de documento "Cobro a Cliente".
			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	C_DocType_ID ");
			sql.append("FROM ");
			sql.append("	" + MDocType.Table_Name + " ");
			sql.append("WHERE ");
			sql.append("	AD_Client_ID = " + getAD_Client_ID() + " ");
			sql.append("	AND DocTypeKey = '" + MDocType.DOCTYPE_CustomerReceipt + "'");

			int C_DocType_ID = DB.getSQLValue(get_TrxName(), sql.toString());
			payment.setC_DocType_ID(C_DocType_ID);

			// Tipo de Pago: Transferencia
			payment.setTenderType(MPayment.TENDERTYPE_DirectDeposit);

			payment.setC_BankAccount_ID(getBankAccountId(bpartner));

			boolean saveOk = true;

			// Guarda el pago
			if (!payment.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				saveOk = false;
				// Completa el pago
			} else if (!payment.processIt(DocAction.ACTION_Complete)) {
				m_processMsg = payment.getProcessMsg();
				saveOk = false;
				// Guarda los cambios del procesamiento
			} else if (!payment.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				saveOk = false;
			}

			if (!saveOk) {
				return DocAction.STATUS_Invalid;
			}

			BigDecimal payAmt = payment.getPayAmt();
			payAmt = payAmt.setScale(2, BigDecimal.ROUND_HALF_EVEN);

			X_C_Currency currency = new X_C_Currency(getCtx(), getC_Currency_ID(), get_TrxName());

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			// Asigno el pago a la liquidación.
			setC_Payment_ID(payment.getC_Payment_ID());
			// Genero la descripción del pago.
			setPayment(payment.getDocumentNo(), currency.getCurSymbol(), payAmt.toString(), sdf.format(payment.getDateTrx()), sdf.format(payment.getDateAcct()));

			if (!save()) {
				CLogger.retrieveErrorAsString();
				setDocAction(DOCACTION_Complete);
				return DocAction.STATUS_Drafted;
			}
		}
		
		setProcessed(true);
		setDocAction(DOCACTION_Void);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		log.info("postIt - " + toString());
		return false;
	}

	/**
	 * Retorna el valor negado de un BigDecimal.
	 * @param input número de entrada a negar.
	 * @return Valor negado.
	 */
	private BigDecimal negativeValue(BigDecimal input) {
		if (input == null || input.signum() == 0) {
			return BigDecimal.ZERO;
		}
		if (input.signum() == -1) {
			return input;
		}
		return input.negate();
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

		// Se revierte el estado de los cupones a "A verificar".
		changeCouponsAuditStatus(X_C_Payment.AUDITSTATUS_ToVerify);

		MCreditCardSettlement copy = new MCreditCardSettlement(getCtx(), 0, get_TrxName());
		PO.copyValues(this, copy);

		copy.setAD_Org_ID(getAD_Org_ID());
		copy.setAmount(negativeValue(getAmount()));
		copy.setNetAmount(negativeValue(getNetAmount()));
		copy.setWithholding(negativeValue(getWithholding()));
		copy.setPerception(negativeValue(getPerception()));
		copy.setExpenses(negativeValue(getExpenses()));
		copy.setCouponsTotalAmount(BigDecimal.ZERO);
		copy.setSettlementNo(getSettlementNo()+"^");
		copy.setIVAAmount(negativeValue(getIVAAmount()));
		copy.setCommissionAmount(negativeValue(getCommissionAmount()));
		copy.setC_Payment_ID(getC_Payment_ID());
		copy.setDocStatus(DOCSTATUS_Voided);
		copy.setDocAction(DOCACTION_None);
		copy.setProcessed(true);

		boolean saveOk = true;

		try {
			if(!copy.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				saveOk = false;
			}
		} catch (Exception e) {
			m_processMsg = e.getMessage();
			saveOk = false;
		}

		if (!saveOk) {
			return false;
		}

		//Anula el pago generado por la liquidación si es que existe
		if(!Util.isEmpty(getC_Payment_ID(), true)){
			MPayment payment = new MPayment(getCtx(), getC_Payment_ID(), get_TrxName());

			if (!payment.processIt(DocAction.ACTION_Void)) {
				m_processMsg = payment.getProcessMsg();
				saveOk = false;
				// Guarda los cambios del procesamiento
			} else if (!payment.save()) {
				m_processMsg = CLogger.retrieveErrorAsString();
				saveOk = false;
			}

			if (!saveOk) {
				return false;
			}
		}

		// Desvincula los cupones
		StringBuffer sql = new StringBuffer();

		sql.append("DELETE FROM ");
		sql.append("	" + X_C_CouponsSettlements.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_CreditCardSettlement_ID = " + getC_CreditCardSettlement_ID());

		DB.executeUpdate(sql.toString(), get_TrxName());

		setSettlementNo("^" + getSettlementNo());
		setCouponsTotalAmount(BigDecimal.ZERO);
		if (!save()) {
			m_processMsg = CLogger.retrieveErrorAsString();
			return false;
		}
		
		// Se replican todos los registros de las pestañas adicionales
		// (Iva, comisiones, retenciones, percepciones, otros conceptos)

		String[] toReplicate = new String[] {
				X_C_IVASettlements.Table_Name,
				X_C_CommissionConcepts.Table_Name,
				X_C_WithholdingSettlement.Table_Name,
				X_C_PerceptionsSettlement.Table_Name,
				X_C_ExpenseConcepts.Table_Name
		};

		for (String tableName : toReplicate) {
			sql = new StringBuffer();

			sql.append("SELECT ");
			sql.append("	* ");
			sql.append("FROM ");
			sql.append("	" + tableName + " ");
			sql.append("WHERE ");
			sql.append("	C_CreditCardSettlement_ID = ? ");

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = DB.prepareStatement(sql.toString());
				ps.setInt(1, getC_CreditCardSettlement_ID());
				rs = ps.executeQuery();

				while (rs.next()) {
					PO to = null;

					// IVA
					if (tableName.equals(X_C_IVASettlements.Table_Name)) {
						X_C_IVASettlements from = new X_C_IVASettlements(getCtx(), rs, get_TrxName());
						to = new X_C_IVASettlements(getCtx(), 0, get_TrxName());
						PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
						((X_C_IVASettlements)to).setC_IVASettlements_ID(0);
						((X_C_IVASettlements)to).setC_CreditCardSettlement_ID(copy.getC_CreditCardSettlement_ID());
						((X_C_IVASettlements)to).setAmount(negativeValue(from.getAmount()));
						if (!to.save()) {
							CLogger.retrieveErrorAsString();
							return false;
						}
					}
					// Comisiones
					if (tableName.equals(X_C_CommissionConcepts.Table_Name)) {
						X_C_CommissionConcepts from = new X_C_CommissionConcepts(getCtx(), rs, get_TrxName());
						to = new X_C_CommissionConcepts(getCtx(), 0, get_TrxName());
						PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
						((X_C_CommissionConcepts)to).setC_CommissionConcepts_ID(0);
						((X_C_CommissionConcepts)to).setC_CreditCardSettlement_ID(copy.getC_CreditCardSettlement_ID());
						((X_C_CommissionConcepts)to).setAmount(negativeValue(from.getAmount()));
					}
					// Retenciones
					if (tableName.equals(X_C_WithholdingSettlement.Table_Name)) {
						X_C_WithholdingSettlement from = new X_C_WithholdingSettlement(getCtx(), rs, get_TrxName());
						to = new X_C_WithholdingSettlement(getCtx(), 0, get_TrxName());
						PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
						((X_C_WithholdingSettlement)to).setC_WithholdingSettlement_ID(0);
						((X_C_WithholdingSettlement)to).setC_CreditCardSettlement_ID(copy.getC_CreditCardSettlement_ID());
						((X_C_WithholdingSettlement)to).setAmount(negativeValue(from.getAmount()));
					}
					// Percepciones
					if (tableName.equals(X_C_PerceptionsSettlement.Table_Name)) {
						X_C_PerceptionsSettlement from = new X_C_PerceptionsSettlement(getCtx(), rs, get_TrxName());
						to = new X_C_PerceptionsSettlement(getCtx(), 0, get_TrxName());
						PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
						((X_C_PerceptionsSettlement)to).setC_PerceptionsSettlement_ID(0);
						((X_C_PerceptionsSettlement)to).setC_CreditCardSettlement_ID(copy.getC_CreditCardSettlement_ID());
						((X_C_PerceptionsSettlement)to).setAmount(negativeValue(from.getAmount()));
					}
					// Otros conceptos
					if (tableName.equals(X_C_ExpenseConcepts.Table_Name)) {
						X_C_ExpenseConcepts from = new X_C_ExpenseConcepts(getCtx(), rs, get_TrxName());
						to = new X_C_ExpenseConcepts(getCtx(), 0, get_TrxName());
						PO.copyValues(from, to, from.getAD_Client_ID(), from.getAD_Org_ID());
						((X_C_ExpenseConcepts)to).setC_ExpenseConcepts_ID(0);
						((X_C_ExpenseConcepts)to).setC_CreditCardSettlement_ID(copy.getC_CreditCardSettlement_ID());
						((X_C_ExpenseConcepts)to).setAmount(negativeValue(from.getAmount()));
					}

				}
			} catch (Exception e) {
				log.log(Level.SEVERE, "MCreditCardSettlement.voidIt", e);
			} finally {
				try {
					rs.close();
					ps.close();
				} catch (SQLException e) {
					log.log(Level.SEVERE, "Cannot close statement or resultset");
				}
			}
		}

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
