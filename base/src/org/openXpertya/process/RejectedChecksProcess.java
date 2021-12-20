package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBoletaDepositoLine;
import org.openXpertya.model.MCheckCuitControl;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MPInstance;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MProcess;
import org.openXpertya.model.MTax;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_Payment;
import org.openXpertya.model.X_M_BoletaDepositoLine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.HTMLMsg.HTMLList;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class RejectedChecksProcess extends AbstractSvrProcess {
	
	/** Cheque */
	private MPayment check;
	
	/** Tipo de documento del débito a generar */
	private Integer docTypeID;
	
	/** Artículo para el documento a generar */
	private Integer productID;
	
	/** CUIT Librador */
	private String cuitLibrador;
	
	/** Entidad Comercial del cheque */
	private MBPartner bPartner;
	
	/** Documento débito generado por el cheque */
	private MInvoice invoice;
	
	/**
	 * Descripción de los cheques de tercero rechazados generados a partir del
	 * cheque parámetro
	 */
	private List<String> chequesTerceroRejectedMsg = new ArrayList<String>();
	
	/** Contra cheque generado por el cheque de la línea de la boleta de depósito */
	private MPayment reverseCheckBoletaDeposito;
	
	/**
	 * Cheque que se genera cuando la boleta de depósito genera un cheque por
	 * cada cheque
	 */
	private MPayment eachCheckBoletaDeposito;
	
	@Override
	protected String doIt() throws Exception {
		// Rechazar cheque
		rejectCheck();
		// Obtener datos inciales
		initializeData();
		// Bloquear CUIT de cheque
		stopCheckCUIT();
		// Bloquear Cuenta Corriente
		stopCurrentAccount();
		// Crear ND por el cheque
		createDebitDocument();
		// Mensaje final
		return getMsg();
	}
	
	/**
	 * Rechazar cheque
	 * @throws Exception
	 */
	protected void rejectCheck() throws Exception{
		// Cheque
		Integer paymentID = ((Integer) getParametersValues().get(
				"C_PAYMENT_ID")).intValue();
		// Fecha de Rechazo
		Timestamp rejectedDate = (Timestamp) getParametersValues().get(
				"REJECTEDDATE");
		// Comentarios/Observaciones de Rechazo
		String rejectedComments = (String)getParametersValues().get(
				"REJECTEDCOMMENTS");
		// Rechazar Cheque
		MPayment check = new MPayment(getCtx(), paymentID, get_TrxName());
		doCheckRejection(check, rejectedDate, rejectedComments);
		setCheck(check);
		// Si es un cheque de tercero, entonces rechazar el cheque que se haya
		// creado en base a éste
		rejectGeneratedChequeTercero(rejectedDate, rejectedComments);
		// Verificar por boletas de depósito
		rejectChecksBoletaDeposito(rejectedDate, rejectedComments);
	}
	
	/**
	 * Realizar el rechazo de un cheque
	 * @param checkToReject cheque a rechazar
	 * @param rejectDate fecha de rechazo
	 * @param rejectComments comentarios del rechazo
	 * @throws Exception
	 */
	protected void doCheckRejection(MPayment checkToReject, Timestamp rejectDate, String rejectComments) throws Exception{
		checkToReject.setCheckStatus(MPayment.CHECKSTATUS_Rejected);
		checkToReject.setRejectedDate(rejectDate);
		checkToReject.setRejectedComments(rejectComments);
		if(!checkToReject.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	/**
	 * Obtener datos necesarios para el proceso
	 * @throws Exception
	 */
	protected void initializeData() throws Exception{
		// Cheque
		Integer docTypeID = ((Integer) getParametersValues().get(
				"C_DOCTYPE_ID"));
		setDocTypeID(docTypeID);
		// Obtengo la EC del cheque
		MBPartner checkBP = new MBPartner(getCtx(), getCheck()
				.getC_BPartner_ID(), get_TrxName()); 
		setbPartner(checkBP);
		// Obtengo el CUIT Librador del cheque, sino el de la EC
		setCuitLibrador(!Util.isEmpty(getCheck().getA_CUIT(), true) ? getCheck()
				.getA_CUIT() : getbPartner().getTaxID());
		// Artículo configurado en la compañía para el rechazo del cheque
		MClientInfo clientInfo = MClientInfo.get(getCtx(), getCheck()
				.getAD_Client_ID());
		if(Util.isEmpty(clientInfo.getM_Product_RejectedCheck_ID(), true)){
			throw new Exception(Msg.getMsg(getCtx(),
					"RejectedCheckProductNotConfigured"));
		}
		setProductID(clientInfo.getM_Product_RejectedCheck_ID());
	}
	
	/**
	 * Rechazar el cheque generado por el cheque de tercero
	 * 
	 * @param rejectedDate
	 *            fecha de rechazo
	 * @param rejectedComments
	 *            comentario del rechazo
	 * @throws Exception
	 */
	protected void rejectGeneratedChequeTercero(Timestamp rejectedDate, String rejectedComments) throws Exception{
		List<PO> payment = PO.find(getCtx(),
				X_C_Payment.Table_Name, "Original_Ref_Payment_ID = ? and docstatus in ('CO','CL')",
				new Object[] { getCheck().getID() }, null, get_TrxName());
		for (PO po : payment) {
			getChequeTerceroRejectedMsg().add(rejectNewCheck((MPayment)po));
		}
	}
	
	/**
	 * Rechazar cheques de la boleta de depósito
	 * @param rejectedDate fecha de rechazo
	 * @param rejectedComments comentarios del rechazo
	 * @throws Exception
	 */
	protected void rejectChecksBoletaDeposito(Timestamp rejectedDate, String rejectedComments) throws Exception{
		// Si es un cheque generado por una boleta de depósito generando un
		// cheque por cada cheque de la boleta, entonces busco el cheque
		// original en la línea de la boleta y lo rechazo.
		// Si es una boleta de depósito que genera un documento por el total de
		// la boleta, no se hace nada
		MBoletaDepositoLine boletaDepositoLine = (MBoletaDepositoLine) PO.findFirst(getCtx(),
				X_M_BoletaDepositoLine.Table_Name, "C_Payment_ID = ? and exists (select m_boletadeposito_id from m_boletadeposito bd where docstatus in ('CO','CL') and bd.m_boletadeposito_id = M_BoletaDepositoLine.m_boletadeposito_id)",
				new Object[] { getCheck().getID() }, null, get_TrxName());
		if(boletaDepositoLine != null){
			// Rechazar el contradocumento del cheque de la línea de la boleta
			// de depósito
			MPayment reversePayment = new MPayment(getCtx(),
					boletaDepositoLine.getC_Reverse_Payment_ID(), get_TrxName());
			doCheckRejection(reversePayment, rejectedDate, rejectedComments);
			setReverseCheckBoletaDeposito(reversePayment);
			// Rechazar el cheque de la línea de la boleta de depósito cuando se
			// realiza un cheque por cada línea
			if(!Util.isEmpty(boletaDepositoLine.getC_Depo_Payment_ID(), true)){
				MPayment payment = new MPayment(getCtx(),
						boletaDepositoLine.getC_Depo_Payment_ID(), get_TrxName());
				doCheckRejection(payment, rejectedDate, rejectedComments);
				setEachCheckBoletaDeposito(payment);
			}
		}
	}
	
	/**
	 * Bloquear CUIT de Cheque. Setear a 0 el límite de la sucursal y de la
	 * sucursal *
	 * 
	 * @throws Exception
	 */
	protected void stopCheckCUIT() throws Exception{
		MOrgInfo orgInfo = MOrgInfo.get(getCtx(), Env.getAD_Org_ID(getCtx()));
		if (getCheck().isReceipt() 
				&& orgInfo.isCheckCuitControl()
				&& !Util.isEmpty(getCuitLibrador(), true)) {
			// Obtengo el control de cuit para la organización del cheque y le seteo a 0
			// el límite
			MCheckCuitControl.get(getCtx(), getCheck().getAD_Org_ID(),
					getCuitLibrador(), true, BigDecimal.ZERO, get_TrxName());
			// Obtengo el control de cuit para la organización * y le seteo a 0
			// el límite
			MCheckCuitControl.get(getCtx(), 0, getCuitLibrador(), true,
					BigDecimal.ZERO, get_TrxName());
		}
	}

	/**
	 * Bloquear Cuenta Corriente de la EC del cheque al estado de cuenta
	 * corriente manual
	 * 
	 * @throws Exception
	 */
	protected void stopCurrentAccount() throws Exception{
		// Setear la entidad comercial con el crédito inhabilitado siempre y
		// cuando la cuenta corriente esté habilitada
		if (getCheck().isReceipt() 
				&& !getbPartner().getSOCreditStatus().equals(MBPartner.SOCREDITSTATUS_NoCreditCheck)) {
			getbPartner().setSOCreditStatus(MBPartner.SOCREDITSTATUS_CreditDisabled);
			if(!getbPartner().save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}
	
	/**
	 * Crear el documento de débito por el cheque
	 * @throws Exception
	 */
	protected MInvoice createDebitDocument() throws Exception{
		MTax tax = MTax.getTaxExemptRate(getCtx(), get_TrxName());
		MInvoice invoice = new MInvoice(getCtx(), 0, get_TrxName());
		invoice.setIsSOTrx(getCheck().isReceipt());
		invoice.setBPartner(getbPartner());
		// Setear el tipo de documento
		invoice = setDocType(invoice);
		invoice.setCUIT(getbPartner().getTaxID());
		invoice.setCreateCashLine(false);
		invoice.setDocAction(MInvoice.DOCACTION_Complete);
		invoice.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		invoice.setCurrentAccountVerified(true);
		// Monto inicial de cuenta corriente
		invoice.setInitialCurrentAccountAmt(getCheck().getPayAmt());
		invoice.setSkipExtraValidations(true);
		if(!invoice.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Línea
		MInvoiceLine invoiceLine = new MInvoiceLine(invoice);
		// Setear el artículo
		invoiceLine.setM_Product_ID(getProductID());
		invoiceLine.setQty(1);		
		invoiceLine.setPriceEntered(getCheck().getPayAmt());
		invoiceLine.setPriceActual(getCheck().getPayAmt());
		invoiceLine.setPriceList(getCheck().getPayAmt());
		invoiceLine.setC_Tax_ID(tax.getID());
		invoiceLine.setLineNetAmt();
		if(!invoiceLine.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Completar el documento
		if (!DocumentEngine.processAndSave(invoice,
				MInvoice.DOCACTION_Complete, false)) {
			throw new Exception(invoice.getProcessMsg());
		}
		// Recargar la factura
		setInvoice(new MInvoice(getCtx(), invoice.getID(), get_TrxName()));
		getCheck().setC_Invoice_Check_Rejected_ID(invoice.getID());
		if(!getCheck().save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		return invoice;
	}
	
	/**
	 * Seteo del tipo de documento a la factura de débito
	 * 
	 * @param invoice
	 *            factura
	 * @return factura con el tipo de documento seteado
	 * @throws Exception
	 */
	protected MInvoice setDocType(MInvoice invoice) throws Exception{
		Integer docTypeID = getDocTypeID();
		if(Util.isEmpty(docTypeID, true)){
			RejectedCheckTrxBuilder rc = RejectedCheckTrxBuilder.get(getCheck());
			rc.setDocType(invoice);
		}
		else {
			invoice.setC_DocTypeTarget_ID(docTypeID);
			invoice.setC_DocType_ID(docTypeID);
		}
		return invoice;
	}
	
	protected boolean isCurrentAccountDisabled(){ 
		return !getbPartner().getSOCreditStatus().equals(
				MBPartner.SOCREDITSTATUS_NoCreditCheck);
	}			
	
	protected String getMsg(){
		HTMLMsg msg = new HTMLMsg();
		HTMLList actions = msg.createList("actions", "ul");
		// Cheque rechazado
		msg.createAndAddListElement("check",
				Msg.getMsg(getCtx(), "RejectedCheck") + " : "
						+ getCheck().getDocumentNo(), actions);
		if (getChequeTerceroRejectedMsg() != null
				|| getReverseCheckBoletaDeposito() != null
				|| getEachCheckBoletaDeposito() != null) {
			HTMLList otherChecks = msg.createList("otherChecks", "ul");
			// Cheque generado por el cheque de tercero
			if(getChequeTerceroRejectedMsg().size() > 0){
				msg.createAndAddListElement("checktercero",
						Msg.getMsg(getCtx(), "GeneratedCheckOfChequeTercero") + " : "
								+ getChequeTerceroRejectedMsg(), otherChecks);
			}
			// Contra cheque generado por boleta de depósito
			if(getReverseCheckBoletaDeposito() != null){
				msg.createAndAddListElement("reversecheckboletadeposito",
						Msg.getMsg(getCtx(), "ReverseCheckFromBoletaDeposito") + " : "
								+ getReverseCheckBoletaDeposito().getDocumentNo(), otherChecks);
			}
			// Cheque generado por boleta de depósito cuando se genera un cheque
			// por cada línea de la boleta
			if(getEachCheckBoletaDeposito() != null){
				msg.createAndAddListElement("eachcheckboletadeposito",
						Msg.getMsg(getCtx(), "EachCheckGeneratedInBoletaDeposito") + " : "
								+ getEachCheckBoletaDeposito().getDocumentNo(), otherChecks);
			}
			msg.createAndAddListElement("othersChecksLE", otherChecks.toString(), actions);
		}
		// CUIT bloqueado
		MOrgInfo orgInfo = MOrgInfo.get(getCtx(), Env.getAD_Org_ID(getCtx()));
		if (getCheck().isReceipt() 
				&& orgInfo.isCheckCuitControl()
				&& Util.isEmpty(getCuitLibrador(), true)) {
			msg.createAndAddListElement("checkcuitcontrol",
					Msg.getMsg(getCtx(), "CheckCUITHold") + " : "
							+ getbPartner().getTaxID(), actions);
		}
		// Cuenta corriente
		if (getCheck().isReceipt() && isCurrentAccountDisabled()) {
			msg.createAndAddListElement("currentaccount",
					Msg.getMsg(getCtx(), "Credit_Status_D") + " : "
							+ getbPartner().getName(), actions);
		}
		// Documento débito creado
		msg.createAndAddListElement("document",
				Msg.getMsg(getCtx(), "DocumentCreated") + " : "
						+ getInvoice().getDocumentNo(), actions);
		msg.addList(actions);
		return msg.toString();
	}
	
	/**
	 * Ejecuta recursivamente este proceso de rechazo del cheque parámetro
	 * 
	 * @param ctp cheque a rechazar
	 * @return info de la ejecución del proceso
	 * @throws Exception
	 */
	protected String rejectNewCheck(MPayment ctp) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("C_Payment_ID", ctp.getID());
		params.put("RejectedDate", (Timestamp) getParametersValues().get("REJECTEDDATE"));
		if((String)getParametersValues().get("REJECTEDCOMMENTS") != null) {
			params.put("RejectedComments", (String) getParametersValues().get("REJECTEDCOMMENTS"));
		}
		ProcessInfo pi = MProcess.execute(getCtx(), getProcessInfo().getAD_Process_ID(), 0, params, get_TrxName());
		if(pi.isError()) {
			throw new Exception(pi.getSummary());
		}
		
		return pi.getSummary();
	}
	
	protected MPayment getCheck() {
		return check;
	}

	protected void setCheck(MPayment check) {
		this.check = check;
	}

	protected MBPartner getbPartner() {
		return bPartner;
	}

	protected void setbPartner(MBPartner bPartner) {
		this.bPartner = bPartner;
	}

	protected MInvoice getInvoice() {
		return invoice;
	}

	protected void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	protected Integer getDocTypeID() {
		return docTypeID;
	}

	protected void setDocTypeID(Integer docTypeID) {
		this.docTypeID = docTypeID;
	}

	protected String getCuitLibrador() {
		return cuitLibrador;
	}

	protected void setCuitLibrador(String cuitLibrador) {
		this.cuitLibrador = cuitLibrador;
	}

	protected Integer getProductID() {
		return productID;
	}

	protected void setProductID(Integer productID) {
		this.productID = productID;
	}

	protected MPayment getReverseCheckBoletaDeposito() {
		return reverseCheckBoletaDeposito;
	}

	protected void setReverseCheckBoletaDeposito(
			MPayment reverseCheckBoletaDeposito) {
		this.reverseCheckBoletaDeposito = reverseCheckBoletaDeposito;
	}

	protected MPayment getEachCheckBoletaDeposito() {
		return eachCheckBoletaDeposito;
	}

	protected void setEachCheckBoletaDeposito(MPayment eachCheckBoletaDeposito) {
		this.eachCheckBoletaDeposito = eachCheckBoletaDeposito;
	}

	protected List<String> getChequeTerceroRejectedMsg() {
		return chequesTerceroRejectedMsg;
	}

	protected void setChequeTerceroRejectedMsg(List<String> chequesTerceroRejectedMsg) {
		this.chequesTerceroRejectedMsg = chequesTerceroRejectedMsg;
	}
	

}
