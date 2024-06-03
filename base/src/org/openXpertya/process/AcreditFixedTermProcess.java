package org.openXpertya.process;


import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.apps.form.VOrdenCobroModel;
import org.openXpertya.apps.form.VOrdenPagoModel;
import org.openXpertya.model.MBankTransfer;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MFixedTerm;
import org.openXpertya.model.MFixedTermRetention;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

public class AcreditFixedTermProcess extends SvrProcess {

	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
						
		//Recupero el plazo fijo 
		MFixedTerm fixedTerm = new MFixedTerm(getCtx(), getRecord_ID(), get_TrxName());
		String trxNumber = "TRX " + (!Util.isEmpty(fixedTerm.getCertificate()) ? fixedTerm.getCertificate() : String.valueOf(fixedTerm.getID()));
		
		//Valido fechas
		Timestamp hoy = new Timestamp(TimeUtil.getToday().getTimeInMillis());
		if (TimeUtil.getDiffDays(hoy, fixedTerm.getDueDate()) > 0) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + ": la fecha de acreditación es posterior a la fecha actual");
		}
		
		//Genero y completo medio de cobro por el interés en la cuenta bancaria del plazo fijo
		MPayment interestPayment = createInterestPayment(fixedTerm, trxNumber);
		
		//Genero retenciones
		if (fixedTerm.getRetentionAmt().compareTo(BigDecimal.ZERO) > 0) {
			int hdrID = createRetentionsAndAllocation(fixedTerm, trxNumber);
			fixedTerm.setC_AllocationRetentionHdr_ID(hdrID);
		}
		
		//Genero transferencia por el capital + rendimiento desde cuenta plazo fijo a cuenta bancaria
		MBankTransfer transfer = createAcreditationBankTransfer(fixedTerm, trxNumber); 
		
		//Setear referencia a las operaciones y marco el plazo fijo como acreditado
		fixedTerm.setC_PaymentInterest_ID(interestPayment.getID());
		fixedTerm.setC_BankTransferAccreditation_ID(transfer.getID());
		fixedTerm.setAccredited(true);
		if (!fixedTerm.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + fixedTerm.getProcessMsg());
		}		
				
		return Msg.getMsg(getCtx(), "FixedTermAcreditationtionSuccess;");
	}
	
	private MPayment createInterestPayment(MFixedTerm fixedTerm, String trxNumber) throws Exception {
		MClientInfo clientInfo = MClientInfo.get(getCtx());
		MPayment interestPayment = new MPayment(getCtx(), 0, get_TrxName());
		
		interestPayment.setC_Payment_ID(0);
		interestPayment.setAD_Org_ID(fixedTerm.getAD_Org_ID());
		interestPayment.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
		interestPayment.setC_BankAccount_ID(fixedTerm.getC_BankAccountFixedTerm_ID());
		interestPayment.setAmount(fixedTerm.getC_Currency_ID(), fixedTerm.getReturnAmt().subtract(fixedTerm.getRetentionAmt()));
		interestPayment.setDateTrx(fixedTerm.getDueDate());
		interestPayment.setDateAcct(fixedTerm.getDueDate());
		interestPayment.setC_BPartner_ID(fixedTerm.getBank().getC_BPartner_ID());
		interestPayment.setIsReceipt(true);
		interestPayment.setDescription(Msg.getMsg(getCtx(), "FixedTermAcreditationDescription") + trxNumber);
		
		// Se toma el tipo de documento para transferencias salientes configurado
		// para la compañía.
		if (clientInfo.getC_OutgoingTransfer_DT_ID() != 0)
			interestPayment.setC_DocType_ID(clientInfo.getC_OutgoingTransfer_DT_ID());
		// Si no tiene el tipo de doc configura, se toma el tipo a partir del flag isReceipt. 
		else
			interestPayment.setC_DocType_ID(interestPayment.isReceipt());
				
		interestPayment.setDocumentNo(trxNumber);
		interestPayment.setC_Currency_ID(fixedTerm.getC_Currency_ID());
		interestPayment.setIsOverUnderPayment(false);
		
		//Cargo por intereses
		interestPayment.setChargeAmt(fixedTerm.getReturnAmt().intValue());			
		interestPayment.setC_Charge_ID(fixedTerm.getBankFixedTermAccount().getC_Charge_Interest_ID()); 
		
		/*
		 * Se carga la cuenta contable que debe utilizarse en la contabilidad 
		 */
		interestPayment.setACCOUNTING_C_Charge_ID(fixedTerm.getBankFixedTermAccount().getC_Charge_Interest_ID());
		
		//Guardo el cobro
		if (!interestPayment.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + interestPayment.getProcessMsg());
		}
		
		//Completo la operación de transferencia
		if (!DocumentEngine.processAndSave((DocAction) interestPayment, DocAction.ACTION_Complete, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + interestPayment.getProcessMsg());
		}
		
		return interestPayment;
	}
	
	private int createRetentionsAndAllocation(MFixedTerm fixedTerm, String trxNumber) throws Exception {
		
		/* === Recibo de cliente === */ 
	  	VOrdenCobroModel ocm = new VOrdenCobroModel(); 
	  	
	  	ocm.setPagoNormal(false, fixedTerm.getRetentionAmt());
	  	ocm.setFechaOP(fixedTerm.getDueDate());
	  	ocm.setDescription(Msg.getMsg(getCtx(), "FixedTermAcreditationDescription") + trxNumber);
	  	ocm.setDocumentNo(trxNumber);
	  	ocm.setDocumentType((MDocType.getDocType(getCtx(), MDocType.DOCTYPE_Recibo_De_Cliente, get_TrxName()).getC_DocType_ID()));
	  	ocm.setBPartnerFacturas(fixedTerm.getBank().getC_BPartner_ID());
	  	ocm.setPaymentRule(ocm.getDefaultPaymentRule());
	  	
	  	int status = ocm.doPreProcesar();
	  	
	  	switch ( status )
		{
		case VOrdenPagoModel.PROCERROR_OK:
			break;
			
		case VOrdenPagoModel.PROCERROR_INSUFFICIENT_INVOICES:
			throw new Exception(Msg.getMsg(getCtx(), "InsufficientInvoicesToPayError"));
		
		case VOrdenPagoModel.PROCERROR_NOT_SELECTED_BPARTNER:
			throw new Exception(Msg.getMsg(getCtx(), "NotSelectedBPartner"));

		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_NOT_SET:
			throw new Exception("Debe indicar el número de documento");
		
		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_ALREADY_EXISTS:
			throw new Exception("Número de documento ya existente");

		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_ALREADY_EXISTS_IN_OTHER_PERIOD:
			throw new Exception("El Nro. de Documento ingresado pertenece a un Recibo anulado pero no es posible reutilizarlo porque está fuera del período actual.");
		
		case VOrdenPagoModel.PROCERROR_DOCUMENTTYPE_NOT_SET:
			throw new Exception("Debe indicar el tipo de documento");
			
		case VOrdenPagoModel.PROCERROR_DOCUMENTNO_INVALID:
			throw new Exception("El numero de documento no coincide con el esperado en la secuencia (prefijo - valor - sufijo)");
			
		case VOrdenPagoModel.PROCERROR_BOTH_EXCHANGE_INVOICES:
			throw new Exception(Msg.getMsg(getCtx(), "BothExchangeInvoices"));
			
		default:
			throw new Exception(Msg.getMsg(getCtx(), "ValidationError"));
		}
	  	
	  	for (MFixedTermRetention retention : fixedTerm.getRetentions()) {
	  		ocm.addRetencion(retention.getC_RetencionSchema_ID(), trxNumber, retention.getRetentionAmt(), fixedTerm.getDueDate(), null, null);
	  	}
	  	
	  	status = ocm.doPostProcesar();
	  	
	  	switch (status) 
		{
		case VOrdenPagoModel.PROCERROR_OK:
			break;
		
		case VOrdenPagoModel.PROCERROR_PAYMENTS_AMT_MAX_ALLOWED:
			throw new Exception(Msg.getMsg(getCtx(), "PaymentsAmtMaxAllowedExceeded"));
			
		case VOrdenPagoModel.PROCERROR_PAYMENTS_AMT_MATCH:
			throw new Exception(Msg.getMsg(getCtx(), "PaymentsAmtMatchError"));
			
		case VOrdenPagoModel.PROCERROR_PAYMENTS_GENERATION:
			throw new Exception(Msg.getMsg(getCtx(), "PaymentsGenerationError") + ocm.getMsgAMostrar());
			
		default:
			throw new Exception(Msg.getMsg(getCtx(), "@Error@") + ocm.getMsgAMostrar());
		}
	  	
	  	return ocm.m_newlyCreatedC_AllocationHeader_ID;
	}
	
	private MBankTransfer createAcreditationBankTransfer(MFixedTerm fixedTerm, String trxNumber) throws Exception {
		//Genero la transferencia del capital + rendimiento - retenciones  
		MBankTransfer transfer = new MBankTransfer(getCtx(), 0, get_TrxName());
		transfer.setAD_Org_ID(fixedTerm.getAD_Org_ID());
		transfer.setDocStatus(MBankTransfer.DOCSTATUS_Drafted);
		transfer.setDocAction(MBankTransfer.DOCACTION_Complete);
		transfer.setDateTrx(fixedTerm.getDueDate());
		transfer.setC_BPartner_ID(fixedTerm.getBank().getC_BPartner_ID());
		transfer.setDescription(Msg.getMsg(getCtx(), "FixedTermAcreditationDescription") + trxNumber);
		transfer.setC_bankaccount_from_ID(fixedTerm.getC_BankAccountFixedTerm_ID());
		transfer.setC_bankaccount_to_ID(fixedTerm.getC_BankAccount_ID());
		transfer.setC_currency_from_ID(fixedTerm.getC_Currency_ID());
		transfer.setC_currency_to_ID(fixedTerm.getC_Currency_ID());
		transfer.setammount_from(fixedTerm.getNetAmt());
		
		//Guardo la transferencia
		if (!transfer.save()) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + transfer.getProcessMsg());
		}
		
		//Completo la operación de transferencia
		if (!DocumentEngine.processAndSave((DocAction) transfer, DocAction.ACTION_Complete, false)) {
			throw new Exception(Msg.getMsg(getCtx(), "FixedTermAcreditationError") + " " + transfer.getProcessMsg());
		}
		
		return transfer;
	}

	
}
