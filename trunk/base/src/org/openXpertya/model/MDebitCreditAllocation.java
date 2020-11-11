package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocOptions;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.DocOptionsUtils;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MDebitCreditAllocation extends X_C_DebitCreditAllocation implements DocAction, DocOptions {
	
	private AllocationGenerator allocGen;
    private MAllocationLine[] m_lines = null;

	public MDebitCreditAllocation(Properties ctx,
			int C_CreditDebitAllocation_ID, String trxName) {
		super(ctx, C_CreditDebitAllocation_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
        DocumentEngine engine = new DocumentEngine( this,getDocStatus());
        return engine.processIt(action,getDocAction(),log);
	}

	@Override
	public boolean unlockIt() {
		return false;
	}

	@Override
	public boolean invalidateIt() {
		return false;
	}

	@Override
	public String prepareIt() {
		BigDecimal pendienteCredito = BigDecimal.ZERO;
		BigDecimal pendienteAllocation = BigDecimal.ZERO;
		
		MInvoice debito = MInvoice.get(getCtx(), getC_Invoice_Debit_ID(), get_TrxName());
		MInvoice credito = !Util.isEmpty(getC_Invoice_Credit_ID(), true)
				? new MInvoice(getCtx(), getC_Invoice_Credit_ID(), get_TrxName())
				: null;
		MAllocationHdr anticipatedHdr = !Util.isEmpty(getC_AllocationHdr_Anticipated_ID(), true)
				? new MAllocationHdr(getCtx(), getC_AllocationHdr_Anticipated_ID(), get_TrxName())
				: null;
		MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
		
		// Entidad Comercial distinta a la del crédito
		if (credito != null && (getC_BPartner_ID() != credito.getC_BPartner_ID())) {
			m_processMsg = Msg.getMsg(getCtx(), "NoSameBusinessPartnerCreditWindow");
			return DocAction.STATUS_Invalid;
		}
		
		// Entidad Comercial distinta a la del débito
		if (getC_BPartner_ID() != debito.getC_BPartner_ID()) {
			m_processMsg = Msg.getMsg(getCtx(), "NoSameBusinessPartnerDebitWindow");
			return DocAction.STATUS_Invalid;
		}
		
		// Entidad Comercial distinta a la de la OPA
		if(anticipatedHdr != null && getC_BPartner_ID() != anticipatedHdr.getC_BPartner_ID()) {
			m_processMsg = Msg.getMsg(getCtx(), "NoSameBusinessPartnerAOPRCWindow");
			return DocAction.STATUS_Invalid;
		}
		
		if (credito != null)
			pendienteCredito = DB.getSQLValueBD(get_TrxName(),"select invoiceopen(?,0)", getC_Invoice_Credit_ID(), true);
		else if (anticipatedHdr != null)
			 pendienteAllocation = DB.getSQLValueBD(get_TrxName(),"select POCRAvailable(?)", getC_AllocationHdr_Anticipated_ID(), true);
		
		BigDecimal pendienteDebito = DB.getSQLValueBD(get_TrxName(),"select invoiceopen(?,0)", getC_Invoice_Debit_ID(), true);
		
		//Si se eligió un crédito, debe tener pendiente > 0
		if (credito != null && pendienteCredito.compareTo(BigDecimal.ZERO)<1){
			m_processMsg = Msg.getMsg(getCtx(), "CreditWithOutPending");
			return DocAction.STATUS_Invalid;
		}
		
		//Si se eligió una OPA, debe tener pendiente > 0
		if (anticipatedHdr != null && pendienteAllocation.compareTo(BigDecimal.ZERO)<1){
			m_processMsg = Msg.getMsg(getCtx(), "AllocationWithOutPending");
			return DocAction.STATUS_Invalid;
		}
		
		//El débito debe tener pendiente > 0
		if (pendienteDebito.compareTo(BigDecimal.ZERO)<1){
			m_processMsg = Msg.getMsg(getCtx(), "DebitWithOutPending");
			return DocAction.STATUS_Invalid;
		}
		
		//Si se eligió un crédito, el saldo a imutar debe ser <= al pendiente del crédito
		if (credito != null && getAmount().compareTo(pendienteCredito)>0){
			m_processMsg = Msg.getMsg(getCtx(), "AmountChargedGreaterThanAmountCredit");
			return DocAction.STATUS_Invalid;
		}
		
		//Si se eligió una OPA, el saldo a imutar debe ser <= al pendiente de la OPA
		if (anticipatedHdr != null && getAmount().compareTo(pendienteAllocation)>0){
			m_processMsg = Msg.getMsg(getCtx(), "AmountChargedGreaterThanAmountOPA");
			return DocAction.STATUS_Invalid;
		}
		
		//El saldo a imputar debe ser <= al pendiente del débito
		if (getAmount().compareTo(pendienteDebito)>0){
			m_processMsg = Msg.getMsg(getCtx(), "AmountChargedGreaterThanAmountDebit");
			return DocAction.STATUS_Invalid;
		}
		
		// El estado de los comprobantes debe ser completo o cerrado
		if(!debito.isInvoiceCompletedOrClosed()){
			m_processMsg = Msg.getMsg(getCtx(), "DocumentStatus", new Object[] { debito.getDocumentNo(),
					MRefList.getListName(getCtx(), DOCSTATUS_AD_Reference_ID, credito.getDocStatus()) });
			return DocAction.STATUS_Invalid;
		}
		if(credito != null && !credito.isInvoiceCompletedOrClosed()){
			m_processMsg = Msg.getMsg(getCtx(), "DocumentStatus", new Object[] { credito.getDocumentNo(),
					MRefList.getListName(getCtx(), DOCSTATUS_AD_Reference_ID, credito.getDocStatus()) });
			return DocAction.STATUS_Invalid;
		}
		
		// Control de signos y tipos de documento asociados
		CallResult cr = controlSignDocTypes(bp, debito, credito, anticipatedHdr);
		if(cr.isError()) {
			m_processMsg = cr.getMsg();
			return DocAction.STATUS_Invalid;
		}
		
		return DocAction.STATUS_InProgress;
	}
	
	/**
	 * Controles por signos y tipos de documentos
	 * @return resultado de la operación
	 */
	protected CallResult controlSignDocTypes(MBPartner bp, MInvoice debito, MInvoice credito, MAllocationHdr anticipatedHdr){
		CallResult cr = new CallResult();
		// Determinar si lo que se está imputando es posible
		// Es decir, un débito debe estar imputado con un crédito o viceversa
		// Esto se determina en base a los signos
		// Débito signo 1 -> Crédito signo -1
		// Débito signo -1 -> Crédito signo 1
		// OPA = 1 / RCA = -1

		// Si la EC es Cliente, sólo se toman débitos con signo 1
		// Si la EC es Proveedor, sólo se toman débitos con signo -1
		// Si la EC es Ambos o Ninguno, sólo se realiza el control de signos de documentos

		boolean isOnlyCustomer = (bp.isCustomer() && !bp.isVendor());
		boolean isOnlyVendor = (!bp.isCustomer() && bp.isVendor());
		
		// Signo del débito
		MDocType debitDT = MDocType.get(getCtx(), debito.getC_DocTypeTarget_ID());
		Integer debitSign = Integer.valueOf(debitDT.getsigno_issotrx());
		
		if(isOnlyCustomer || isOnlyVendor) {
			Integer debitMust = isOnlyCustomer?1:-1;
			if(debitSign.intValue() != debitMust.intValue()) {
				cr.setMsg(Msg.getMsg(getCtx(), "DebitSignMustBe", new Object[] {debitMust}), true); 
				return cr;
			}
		}
		
		Integer creditSign = 0;
		if(credito != null) {
			MDocType creditDT = MDocType.get(getCtx(), credito.getC_DocTypeTarget_ID());
			creditSign = Integer.valueOf(creditDT.getsigno_issotrx());
		}
		else {
			creditSign = anticipatedHdr.getAllocationType()
					.equals(MAllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt) ? -1 : 1;
		}
		if(debitSign.intValue() == creditSign.intValue()) {
			cr.setMsg(Msg.getMsg(getCtx(), "DebitCreditAllocationSameSignError"), true); 
			return cr;
		}
		return cr;
	}
	
	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}
	
	/*
	 * Este metodo compara dos timestamps sin tener en cuenta la porcion del tiempo
	 * Devuelve 0 si son el mismo dia
	 * Devuelve > 0 si timestamp1 es mayor que timestamp2
	 * Devuelve < 0 si timestamp1 es menor que timestamp2
	 * TODO: Mover a una clase Utils ?
	 */
	private int timeIgnoringCompare(Timestamp  timestamp1, Timestamp timestamp2) {
		
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(timestamp1.getTime());
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(timestamp2.getTime());
		
	    if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) 
	        return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
	    if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH)) 
	        return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
	    return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
	}
	
	private MInvoicePaySchedule getFirstInvoicePaySchedule(int C_Invoice_ID) {
		return (MInvoicePaySchedule) PO.findFirst(getCtx(), MInvoicePaySchedule.Table_Name, "C_Invoice_ID = ?",
				new Object[] { C_Invoice_ID }, new String[] { "duedate" }, get_TrxName(), true);
	}
	
	private boolean checkImputationDateAfterExpiredDate(int C_Invoice_ID){
		if(!Util.isEmpty(C_Invoice_ID, true)) {
			MInvoice inv = MInvoice.get(getCtx(), C_Invoice_ID, get_TrxName());
			MInvoicePaySchedule ips = getFirstInvoicePaySchedule(inv.getC_Invoice_ID());
			//chqueo la fecha de vencimiento del pago es igual o mayor a la fecha de imputacion
			if(timeIgnoringCompare(getImputationDate(), ips.getDueDate()) < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String completeIt() {
		allocGen = new AllocationGenerator(getCtx(), get_TrxName());
		
		/*
		 * Chequeo de la fecha de vencimiento contra la fecha de imputacion
		 */
		
		//Crédito
		if (!checkImputationDateAfterExpiredDate(getC_Invoice_Credit_ID()) ){
			MInvoice inv = MInvoice.get(getCtx(), getC_Invoice_Credit_ID(), get_TrxName());
			m_processMsg = Msg.getMsg(getCtx(), "CreditDueDateGreaterThanImputationDate",
					new Object[] { inv.getDocumentNo() });
			return DocAction.STATUS_Invalid;
		}		
		//Debito
		if (!checkImputationDateAfterExpiredDate(getC_Invoice_Debit_ID()) ){
			MInvoice inv = MInvoice.get(getCtx(), getC_Invoice_Debit_ID(), get_TrxName());
			m_processMsg = Msg.getMsg(getCtx(), "DebitDueDateGreaterThanImputationDate",
					new Object[] { inv.getDocumentNo() });
			return DocAction.STATUS_Invalid;
		}	
						
		try {
			allocGen.createAllocationHdr(MAllocationHdr.ALLOCATIONTYPE_Manual, getAD_Org_ID());
			if (!Util.isEmpty(getC_Invoice_Credit_ID(),true))
				allocGen.addCreditInvoice(getC_Invoice_Credit_ID(), getAmount());
			else{
				MAllocationHdr allocation = new MAllocationHdr(getCtx(), getC_AllocationHdr_Anticipated_ID(), get_TrxName());
				MAllocationLine[] lines = allocation.getLines(false);
				BigDecimal amt= getAmount();
				int i = 0;
				while ((amt.compareTo(BigDecimal.ZERO)==1) && (i < lines.length)){
					MAllocationLine line = lines[i];
					//Me fijo si es un cashline, un payment o una retención
					if (!Util.isEmpty(line.getC_CashLine_ID(),true)){
						BigDecimal pendingcashline = DB.getSQLValueBD(get_TrxName(),"select abs(cashlineavailable(?))", line.getC_CashLine_ID(), true);
						//Si el pendiente del cashline es mayor a 0
						if (pendingcashline.compareTo(BigDecimal.ZERO) == 1) {
							//Si el valor a imputar menos el pendiente del cashline es mayor a 0
							if (amt.subtract(pendingcashline).compareTo(BigDecimal.ZERO) > 0)
								amt= amt.subtract(pendingcashline);
							else {
								//Si el valor a imputar menos el pendiente es menor a 0
								if (amt.subtract(pendingcashline).compareTo(BigDecimal.ZERO) < 0)
									pendingcashline = amt;
								amt = BigDecimal.ZERO;
							}	
							allocGen.addCreditCashLine(line.getC_CashLine_ID(), pendingcashline);
						}
					}
					else {
						if (!Util.isEmpty(line.getC_Payment_ID(),true)){
							BigDecimal pendingpayment = DB.getSQLValueBD(get_TrxName(),"select paymentavailable(?)", line.getC_Payment_ID(), true);
							//Si el pendiente del cashline es mayor a 0
							if (pendingpayment.compareTo(BigDecimal.ZERO) == 1) {
								//Si el valor a imputar menos el pendiente del cashline es mayor a 0
								if (amt.subtract(pendingpayment).compareTo(BigDecimal.ZERO) > 0)
									amt= amt.subtract(pendingpayment);
								else {
									//Si el valor a imputar menos el pendiente es menor a 0
									if (amt.subtract(pendingpayment).compareTo(BigDecimal.ZERO) < 0)
										pendingpayment = amt;
									amt = BigDecimal.ZERO;
								}	
								allocGen.addCreditPayment(line.getC_Payment_ID(), pendingpayment);
							}
						}
						else{
							//Si no es un cashline, ni un payment es una Retención
							BigDecimal pendingRetention = DB.getSQLValueBD(get_TrxName(),"select invoiceopen(?,0)", line.getC_Invoice_Credit_ID(), true);
							//Si el pendiente del cashline es mayor a 0
							if (pendingRetention.compareTo(BigDecimal.ZERO) == 1) {
								//Si el valor a imputar menos el pendiente del cashline es mayor a 0
								if (amt.subtract(pendingRetention).compareTo(BigDecimal.ZERO) > 0)
									amt= amt.subtract(pendingRetention);
								else {
									//Si el valor a imputar menos el pendiente es menor a 0
									if (amt.subtract(pendingRetention).compareTo(BigDecimal.ZERO) < 0)
										pendingRetention = amt;
									amt = BigDecimal.ZERO;
								}	
								allocGen.addCreditInvoice(line.getC_Invoice_Credit_ID(),pendingRetention);
							}
						}
					}
					i++;
				}
			}
			allocGen.addDebitInvoice(getC_Invoice_Debit_ID(), getAmount());
			allocGen.getAllocationHdr().setC_BPartner_ID(getC_BPartner_ID());
			allocGen.getAllocationHdr().setDescription(getDescription());
			allocGen.getAllocationHdr().setDateAcct(getImputationDate());
			allocGen.getAllocationHdr().setDateTrx(getImputationDate());
			allocGen.generateLines();
			allocGen.completeAllocation();
			setC_AllocationHdr_ID(allocGen.getAllocationHdr().getC_AllocationHdr_ID());
			//Actualizo el pendiente del débito en la ventana luego de imputarlela OPA/NDP
			setPendingDebitAmount(getPendingDebitAmount().subtract(getAmount()));
		} catch (AllocationGeneratorException e) {
			e.printStackTrace();
			m_processMsg = e.toString();
			return DocAction.STATUS_Invalid;
		} catch (Exception e) {
			e.printStackTrace();
			m_processMsg = e.toString();
			return DocAction.STATUS_Invalid;
		}

		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}

	@Override
	public boolean postIt() {
		return false;
	}
	
	/**
	 * @return resultado con error si el débito relacionado a la imputación,
	 *         está relacionada posteriormente con una OP
	 */
	protected CallResult controlOnlyAllocation(){
		CallResult result = new CallResult();
		String sql = "select distinct ah.documentno "
					+ " from c_allocationhdr ah "
					+ "inner join c_allocationline al on al.c_allocationhdr_id = ah.c_allocationhdr_id, "
					+ "(select dateacct as allocdate from c_allocationhdr where c_allocationhdr_id = "+getC_AllocationHdr_ID()+") as a "
					+ "where al.c_invoice_id = " + getC_Invoice_Debit_ID()
					+ " and ah.docstatus in ('CO','CL') and ah.c_allocationhdr_id <> " + getC_AllocationHdr_ID()
					+ " and (allocdate is null OR allocdate <= ah.dateacct) limit 1";
		String anotherAllocationHDrDocumentNo = DB.getSQLValueString(get_TrxName(), sql);
		if(!Util.isEmpty(anotherAllocationHDrDocumentNo, true)){
			result.setMsg(Msg.getMsg(getCtx(), "VoidDebitCreditAllocationNotBeforeOP",
					new Object[] { anotherAllocationHDrDocumentNo }), true);
		}
		return result;
	}

	@Override
	public boolean voidIt() {
		// No es posible anular una imputación si el débito relacionado está en una OP
		CallResult result = controlOnlyAllocation();
		if(result.isError()){
			setProcessMsg(result.getMsg());
			return false;
		}
		// Anular el allocation relacionado
		if (!Util.isEmpty(getC_AllocationHdr_ID(), true)){
			MAllocationHdr allocation = new MAllocationHdr(getCtx(), getC_AllocationHdr_ID(), get_TrxName());
			boolean voidItResult = allocation.voidIt();
			if (!voidItResult){
				setProcessMsg(allocation.getProcessMsg());
			}
			return voidItResult;
		}
		setProcessMsg(Msg.getMsg(getCtx(), "NoAllocationBetweenDocuments"));
		return false;
	}

	@Override
	public boolean closeIt() {
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
		return null;
	}
	
	protected boolean beforeSave(boolean newRecord) {
		MInvoice debito = MInvoice.get(getCtx(), getC_Invoice_Debit_ID(), get_TrxName());
		MInvoice credito = !Util.isEmpty(getC_Invoice_Credit_ID(), true)
				? new MInvoice(getCtx(), getC_Invoice_Credit_ID(), get_TrxName())
				: null;
		MAllocationHdr anticipatedHdr = !Util.isEmpty(getC_AllocationHdr_Anticipated_ID(), true)
				? new MAllocationHdr(getCtx(), getC_AllocationHdr_Anticipated_ID(), get_TrxName())
				: null;
		MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
		
		// No está permitido organización *
		if(Util.isEmpty(getAD_Org_ID(), true)){
			log.saveError("SaveError", Msg.getMsg(getCtx(), "InvalidOrg"));
			return false;
		}		
		// Si no agrega ningún crédito, error
		if (credito == null && anticipatedHdr == null) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "MustInsertCreditORAnticipatedAllocation"));
			return false;
		}
		// Si se agregan las dos juntas, error
		if (credito != null && anticipatedHdr != null) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "MustInsertOnlyOneCreditORAntAllocation"));
			return false;
		}
		if (credito != null && getC_BPartner_ID() != credito.getC_BPartner_ID()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "NoSameBusinessPartnerCreditWindow"));
			return false;
		}
		if (getC_BPartner_ID() != debito.getC_BPartner_ID()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "NoSameBusinessPartnerDebitWindow"));
			return false;
		}
		if (anticipatedHdr != null && getC_BPartner_ID() != anticipatedHdr.getC_BPartner_ID()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "NoSameBusinessPartnerAOPRCWindow"));
			return false;
		}
		
		// Control de signos y tipos de documento asociados
		CallResult cr = controlSignDocTypes(bp, debito, credito, anticipatedHdr);
		if(cr.isError()) {
			log.saveError("SaveError", cr.getMsg());
			return false;
		}
		
		return true;
	}

	@Override
	public int customizeValidActions(String docStatus, Object processing,
			String orderType, String isSOTrx, int AD_Table_ID,
			String[] docAction, String[] options, int index) {
		// Si el documento está completo, incorporar la opción de Cancelar 
		if (DOCSTATUS_Completed.equals(docStatus)) {
			index = DocOptionsUtils.addAction(options, DocAction.ACTION_Void, index);	
		}
		return index;
	}

}
