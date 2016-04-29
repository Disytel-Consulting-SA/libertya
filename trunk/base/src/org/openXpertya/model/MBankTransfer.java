package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;


/**
 *  Transferencias bancarias entre cuentas propias
 * 
 * (migrado de version OXP1.9 Disytel)
 * @fecha 31/08/2007
 * 
 */

public class MBankTransfer extends X_C_BankTransfer implements DocAction {


	public MBankTransfer(Properties ctx, int C_BankTransfer_ID, String trxName) {
		super(ctx, C_BankTransfer_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBankTransfer(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public int getC_Currency_ID() {
		// TODO Auto-generated method stub

		return getC_currency_to_ID();
	}
	
	/**
	* @Author: Luciano Villaba - Disytel 
	* @Fecha: 16/04/2007
	* @Comentario: Genera los pagos que asientan la anulacion de la transferencia.
	* Estos pagos, a su vez se conciliaran bancariamente. 
	* @Parametros:
	*/
	private boolean generarPagosAnulacion(){
		return generarPagos(true);
	}
	
	/**
	* @Author: Luciano Villaba - Disytel 
	* @Fecha: 16/04/2007
	* @Comentario: Genera los pagos que asientan la transferencia.
	* Estos pagos, a su vez se conciliaran bancariamente. 
	* @Parametros:
	*/
	private boolean generarPagos(){
		return generarPagos(false);
	}
	
	/**
	* @Author: Mauro Hernández - Disytel 
	* @Fecha: 20/12/2013
	* @Comentario: Se asigna el mismo valor de importe origen a importe destino.
	* @Parametros:
	* @Modificado: 
	*/
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		setammount_to(getammount_from());
		return true;
	}
	
	
	/**
	* @Author: Jorge Vidal - Disytel 
	* @Fecha: 17/08/2006
	* @Comentario: Genera los pagos que asientan la transferencia.
	* Estos pagos, a su vez se conciliaran bancariamente. 
	* @Parametros:Boolean anular: true si el pago a generar es de una anulacion
	* @Modificado: Luciano Villalba
	*/
	private boolean generarPagos(boolean anular) {
		//final int zeroChargeID = 1000317;   // "CARGO CERO" EN BASE DE DATOS
		BigDecimal minusOne = new BigDecimal(-1);
		MPayment pagoOrigen = new MPayment(getCtx(), 0, get_TrxName());
		MPayment pagoDestino = new MPayment(getCtx(), 0, get_TrxName());
		MBankAccount cuentaOrigen = new MBankAccount(getCtx(),
				getC_bankaccount_from_ID(), get_TrxName());
		MBankAccount cuentaDestino = new MBankAccount(getCtx(),
				getC_bankaccount_to_ID(), get_TrxName());
		
		MClientInfo clientInfo = MClientInfo.get(getCtx());

		try {
			pagoOrigen.setC_Payment_ID(0);
			pagoOrigen.setAD_Client_ID(getAD_Client_ID());
			pagoOrigen.setAD_Org_ID(getAD_Org_ID());
			pagoOrigen.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
			pagoOrigen.setC_BankAccount_ID(getC_bankaccount_from_ID());
			pagoOrigen.setAmount(getC_currency_from_ID(), getammount_from().multiply(anular?BigDecimal.ONE:minusOne));
			pagoOrigen.setDateTrx(getDateTrx());
			pagoOrigen.setDateAcct(getDateTrx());
			pagoOrigen.setC_BPartner_ID(getC_BPartner_ID());
			pagoOrigen.setIsReceipt(anular);
			pagoOrigen.setC_Project_ID(getC_Project_ID());
			// Se toma el tipo de documento para transferencias salientes configurado
			// para la compañía.
			if (clientInfo.getC_OutgoingTransfer_DT_ID() != 0)
				pagoOrigen.setC_DocType_ID(clientInfo.getC_OutgoingTransfer_DT_ID());
			// Si no tiene el tipo de doc configura, se toma el tipo a partir del flag isReceipt. 
			else
				pagoOrigen.setC_DocType_ID(pagoOrigen.isReceipt());
			// Descripción.
			if(anular)
				pagoOrigen.setDescription("ANULACION - Transferencia " + /*MFecha.timestampToStringDDmmYYYY(*/getDateTrx()/*)*/ + " $" + getammount_from());
			else
				pagoOrigen.setDescription("Transferencia " + /*MFecha.timestampToStringDDmmYYYY(*/getDateTrx()/*)*/ + " $" + getammount_from().multiply(minusOne));
			
			pagoOrigen.setDocumentNo("TRX " + getC_banktransfer_ID());
			pagoOrigen.setC_Currency_ID(getC_currency_from_ID());
			pagoOrigen.setA_Name(cuentaOrigen.getDescription());
			pagoOrigen.setIsOverUnderPayment(false);
			//pagoOrigen.setChargeAmt(getammount_from().intValue()); // (????)
			
			/*
			 * Se adiciona la informacion (a titulo informativo unicamente) sobre charges al pago origen
			 * Al anular el pago no se presentar charges adicionales.
			 */
			pagoOrigen.setChargeAmt(!anular && getcharge_amt_from()!=null?getcharge_amt_from().intValue():0);			
			pagoOrigen.setC_Charge_ID(!anular && getcharge_from_ID()!=0?getcharge_from_ID():0); //zeroChargeID
			if (!pagoOrigen.save()) 
				return false;
			
			// Se completa el pago.
			boolean procOk = pagoOrigen.processIt(DOCACTION_Complete);
			if (!procOk || !pagoOrigen.save()) {
				m_processMsg = pagoOrigen.getProcessMsg();
				return false;
			}
							
			setC_payment_from_ID(pagoOrigen.getC_Payment_ID());

			pagoDestino.setC_Payment_ID(0);
			pagoDestino.setAD_Client_ID(getAD_Client_ID());
			pagoDestino.setAD_Org_ID(getAD_Org_ID());
			pagoDestino.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
			pagoDestino.setC_BankAccount_ID(getC_bankaccount_to_ID());
			pagoDestino.setAmount(getC_currency_to_ID(), getammount_to().multiply(anular?minusOne:BigDecimal.ONE));
			pagoDestino.setDateTrx(getDateTrx());
			pagoDestino.setDateAcct(getDateTrx());
			pagoDestino.setC_BPartner_ID(getC_BPartner_ID());
			pagoDestino.setIsReceipt(!anular);
			pagoDestino.setC_Project_ID(getC_Project_ID());
			// Se toma el tipo de documento para transferencias salientes configurado
			// para la compañía.
			if (clientInfo.getC_OutgoingTransfer_DT_ID() != 0)
				pagoDestino.setC_DocType_ID(clientInfo.getC_OutgoingTransfer_DT_ID());
			// Si no tiene el tipo de doc configura, se toma el tipo a partir del flag isReceipt. 
			else
				pagoDestino.setC_DocType_ID(pagoDestino.isReceipt());
			
			if(anular)
				pagoDestino.setDescription("ANULACION - Transferencia " + /*MFecha.timestampToStringDDmmYYYY(*/getDateTrx()/*)*/ + " $" + getammount_to().multiply(minusOne));
			else
				pagoDestino.setDescription("Transferencia " + /*MFecha.timestampToStringDDmmYYYY(*/getDateTrx()/*)*/ + " $" + getammount_to());
			
			pagoDestino.setDocumentNo("TRX " + getC_banktransfer_ID());
			pagoDestino.setC_Currency_ID(getC_currency_to_ID());
			pagoDestino.setA_Name(cuentaDestino.getDescription());
			pagoDestino.setIsOverUnderPayment(false);
			pagoDestino.setChargeAmt(0);
			//pagoDestino.setC_Charge_ID(zeroChargeID); //(????)
			
			if (!pagoDestino.save())
				return false;

			// Se completa el pago.
			procOk = pagoDestino.processIt(DOCACTION_Complete);
			if (!procOk || !pagoDestino.save()) {
				m_processMsg = pagoDestino.getProcessMsg();
				return false;
			}
			setC_payment_to_ID(pagoDestino.getC_Payment_ID());

		} catch (RuntimeException e) {
			e.printStackTrace();
			log.severe("Error al generar los pagos para la transferencia");
			return false;
		}

		return true;
	}

	/***************************************************************************
	 * Process document
	 * 
	 * @param processAction
	 *            document action
	 * @return true if performed
	 */
	public boolean processIt(String processAction) {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(processAction, getDocAction(), log);
	} // process

	public boolean approveIt() {
		log.info("approveIt - " + toString());
		// setIsApproved(true);
		return true;
	} // approveIt

	/***************************************************************************
	 * Complete Document Generar pagos
	 * 
	 * @return new status (Complete, In Progress, Invalid, Waiting ..)
	 */
	public String completeIt() {

		if (generarPagos()) {
			setProcessed(true);
			setDocAction(DOCACTION_Void);//Cambiado Luciano. Antes->DOCACTION_Close
			return DocAction.STATUS_Completed;
		} else {
			setProcessed(false);
			setDocAction(DOCACTION_Complete);
			return DocAction.STATUS_Invalid;

		}
	} // completeIt

	/**
	 * Reject Approval
	 * 
	 * @return true if success
	 */
	public boolean rejectIt() {
		log.info("rejectIt - " + toString());
		// setIsApproved(false);
		return true;
	} // rejectIt

	/**
	 * Unlock Document.
	 * 
	 * @return true if success
	 */
	public boolean unlockIt() {
		log.info("unlockIt - " + toString());
		setProcessing(false);
		return true;
	} // unlockIt

	/**
	 * Invalidate Document
	 * 
	 * @return true if success
	 */
	public boolean invalidateIt() {
		log.info("invalidateIt - " + toString());
		setDocAction(DOCACTION_Prepare);
		return true;
	} // invalidateIt

	public String prepareIt() {
		log.info("prepareIt - " + toString());

		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		// return DOCACTION_Complete;

		if (getammount_from().compareTo(BigDecimal.ZERO) == 0
				|| getammount_to().compareTo(BigDecimal.ZERO) == 0) {
			m_processMsg = "Importes invalidos";
			setDocAction(DOCSTATUS_Invalid);
			return DOCSTATUS_Invalid;
		}

		if (getC_currency_from_ID() == getC_currency_to_ID()) {
			if (!(getammount_from().compareTo(
					getammount_to()/*.add(getCharge_Amt_To())*/) == 0)) {
				m_processMsg = "Importes invalidos";
				setDocAction(DOCSTATUS_Invalid);
				return DOCSTATUS_Invalid;
			}
		}

		return DOCSTATUS_InProgress; // Por Jorgev - Disytel
	} // prepareIt

	/**
	 * Post Document - nothing
	 * 
	 * @return true if success
	 */
	public boolean postIt() {
		log.info("postIt - " + toString());
		return false;
	} // postIt

	/**
	 * Void Document.
	 * 
	 * @return true if success
	 */
	public boolean voidIt() {
		log.info("voidIt - " + toString());

		// addDescription(Msg.getMsg(getCtx(), "Voided") + " (" +
		// getGrandTotal() + ")");
		
		/*
		 *Luciano.
		 *Cuando uno de los payments esta conciliado no se debe poder anular.
		 * */
		MPayment pagoTo = new MPayment(getCtx(),getC_payment_to_ID(), get_TrxName());
		MPayment pagoFrom = new MPayment(getCtx(),getC_payment_from_ID(), get_TrxName());
		if(pagoTo.isReconciled() || pagoFrom.isReconciled()){
			m_processMsg = "No se pueden anular movimientos conciliados";
			return false;
		}
		if(!generarPagosAnulacion()){
			m_processMsg = "No se pudieron generar los contra-movimientos";
			return false;
		}
			
		
		setProcessed(true);
		setDocAction(DOCACTION_None);
		setDocStatus(DOCSTATUS_Voided);
		return true;
	} // voidIt

	/**
	 * Close Document.
	 * 
	 * @return true if success
	 */
	public boolean closeIt() {
		log.info("closeIt - " + toString());
		setDocAction(DOCACTION_None);
		return true;
	} // closeIt

	/**
	 * Reverse Correction
	 * 
	 * @return true if success
	 */
	public boolean reverseCorrectIt() {
		// m_processMsg = ""
		return true;
	} // reverseCorrectionIt

	/**
	 * Reverse Accrual - none
	 * 
	 * @return true if success
	 */
	public boolean reverseAccrualIt() {
		log.info("reverseAccrualIt - " + toString());
		return false;
	} // reverseAccrualIt

	/**
	 * Re-activate
	 * 
	 * @return true if success
	 */
	public boolean reActivateIt() {
		log.info("reActivateIt - " + toString());
		return false;
	} // reActivateIt

	/**
	 * String Representation
	 * 
	 * @return info
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("MBankTransfer[");
		sb.append(getID()).append("- Importe origen").append(getammount_from())
				.append(",ImporteDestino=").append(getammount_to());
		// .append(",CuentaDestino=").append(getC_Bank )
		// .append(",GrandTotal=").append(getGrandTotal());

		return sb.toString();
	} // toString

	/***************************************************************************
	 * Get Summary
	 * 
	 * @return Summary of Document
	 */
	public String getSummary() {
		StringBuffer sb = new StringBuffer();
		sb.append(getDateTrx());
		sb.append(": ").append(Msg.translate(getCtx(), "Importe")).append("=")
				.append(getammount_from());

		return sb.toString();
	} // getSummary


	/**
	 * Get Document Owner (Responsible)
	 * 
	 * @return AD_User_ID
	 */
	public int getDoc_User_ID() {
		return getCreatedBy();
	} // getDoc_User_ID

	/**
	 * Get Document Approval Amount
	 * 
	 * @return amount payment(AP) or write-off(AR)
	 */
	public BigDecimal getApprovalAmt() {
		return Env.ZERO;
	} // getApprovalAmt

}
