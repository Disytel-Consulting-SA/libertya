package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.openXpertya.model.POCRGenerator.POCRType;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.GeneratorRetenciones;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

public class MPaymentBatchPO extends X_C_PaymentBatchPO implements DocAction {
	
	public MPaymentBatchPO(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	public MPaymentBatchPO(Properties ctx, int C_PaymentBatchPO_ID, String trxName) {
		super(ctx, C_PaymentBatchPO_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -192775459951184081L;

	public String completeIt() {
		POCRGenerator poGenerator = new POCRGenerator(getCtx(), POCRType.PAYMENT_ORDER,get_TrxName());
		// Lockear la secuencia si es que está marcada asi
		MDocType allocTargetDocType = MDocType.get(getCtx(), getC_DoctypeAllocTarget_ID());
		MSequence allocTargetSeq = new MSequence(getCtx(), allocTargetDocType.getDocNoSequence_ID(), get_TrxName());
		MSequenceLock seqLock = null;
		if (allocTargetDocType != null && allocTargetDocType.isLockSeq() && allocTargetSeq != null) {
			seqLock = new MSequenceLock(getCtx(), 0, null);
			seqLock.setAD_Sequence_ID(allocTargetSeq.getID());
			seqLock.setC_DocType_ID(allocTargetDocType.getID());
			seqLock.setAD_Table_ID(get_Table_ID());
			seqLock.setRecord_ID(getID());
			if(!seqLock.save()){
				setProcessMsg(CLogger.retrieveErrorAsString());
				return DocAction.STATUS_Invalid;
			}
		}
		try {
			poGenerator.setDocType(getC_DoctypeAllocTarget_ID());
			poGenerator.setCurrentSeqLock(seqLock);
			for (MPaymentBatchPODetail detail : getBatchDetails()) {
				
				//Proveedor
				MBPartner bPartner = new MBPartner(getCtx(), detail.getC_BPartner_ID(), get_TrxName());
				
				//Por el momento, solo acepta forma de pago "Cheque o Talón"
//				if (!bPartner.getBatch_Payment_Rule().equals("C")) {
//					//FIXME: Provisorio, por ahora es un solo medio de pago. Corregir y traduccción 
//					m_processMsg = "El proveedor " + bPartner.getName() + " tiene configurada una Forma de Pago distinta a 'Cheque o Talón'. Por el momento no está implementada la generación OP con otra forma de pago";
//					return DocAction.STATUS_Invalid;
//				}
				
				//Genero OP y completo datos de cabecera
				poGenerator.createAllocationHdr(X_C_AllocationHdr.ALLOCATIONTYPE_PaymentOrder);
				poGenerator.getAllocationHdr().setDateTrx(getBatchDate());
				poGenerator.getAllocationHdr().setDateAcct(getBatchDate());
				poGenerator.getAllocationHdr().setDescription(Msg.getMsg(getCtx(), "PaymentBatchPOAllocationDescription") + " " + getDocumentNo());
				poGenerator.getAllocationHdr().setIsManual(false);
				poGenerator.getAllocationHdr().setC_BPartner_ID(detail.getC_BPartner_ID());
								
				//Arrays para cáculo de retenciones
				Vector<Integer> facturasProcesar = new Vector<Integer>();
				Vector<BigDecimal> manualAmounts = new Vector<BigDecimal>();
				
				//Agrego las facturas
				for (MPaymentBatchPOInvoices invoice : detail.getInvoices()) {
					MInvoice cInvoice = new MInvoice(getCtx(), invoice.getC_Invoice_ID(), get_TrxName());
					//Importe converido a moneda original de la factura 
					BigDecimal convertedAmt = MCurrency.currencyConvert(
							invoice.getPaymentAmount(), getC_Currency_ID(),
							cInvoice.getC_Currency_ID(), getBatchDate(), invoice.getAD_Org_ID(),
							getCtx());
					poGenerator.addDebitInvoice(invoice.getC_Invoice_ID(), convertedAmt);
					facturasProcesar.add(invoice.getC_Invoice_ID());
					manualAmounts.add(invoice.getPaymentAmount());
				}
				
				//Calculo retenciones
				GeneratorRetenciones m_retGen = new GeneratorRetenciones(
						detail.getC_BPartner_ID(), 
						facturasProcesar, 
						manualAmounts, 
						detail.getPaymentAmount(), 
						false, 
						getBatchDate(),
						"P");
				m_retGen.setTrxName(get_TrxName());
				m_retGen.evaluarRetencion();
				m_retGen.save(poGenerator.getAllocationHdr());
				
				BigDecimal totalRetenciones = new BigDecimal(0);
				for (X_M_Retencion_Invoice retInvoice : m_retGen.getM_retenciones()) {
					poGenerator.addCreditInvoice(retInvoice.getC_Invoice_ID(), retInvoice.getamt_retenc());
					totalRetenciones = totalRetenciones.add(retInvoice.getamt_retenc());
				}
				
				//Genero el pago
				MBankAccount bankAccount = new MBankAccount(getCtx(), bPartner.getC_BankAccount_ID(), get_TrxName());
				X_C_BankAccountDoc chequera = bankAccount.getFirstBankAccountDoc();
				// Si no existe chequera, significa que no hay alguna o no está
				// asociada al usuario actual
				if(chequera == null){
					throw new Exception(Msg.getMsg(getCtx(), "PaymentBatchPOBankAccountDocNotFound"));
				}
				
				BigDecimal importe = detail.getPaymentAmount().subtract(totalRetenciones);
				
				//Generar y completar el payment Cheque
				MPayment pay = new MPayment(getCtx(), 0, get_TrxName());
				
				//Datos generales
				pay.setDescription(Msg.getMsg(getCtx(), "AllocationHdrAutogeneratedOnBatch") + " " + getDocumentNo());
				pay.setIsReceipt(false);
				pay.setC_DocType_ID(false);
				pay.setC_BPartner_ID(bPartner.getID());
				pay.setDateTrx(getBatchDate());
				pay.setDateAcct(getBatchDate());
				
				//Datos Cheque 
				pay.setCheckNo(String.valueOf(chequera.getCurrentNext())); // Numero de cheque
				pay.setDateEmissionCheck(getBatchDate()); // Fecha de Emision del Cheque
				pay.setMicr(bankAccount.getSucursal() + ";" + bankAccount.getAccountNo() + ";" + pay.getCheckNo()); // Sucursal; cta; No. cheque
				pay.setA_Name(bPartner.getName()); // Nombre
				pay.setTenderType(MPayment.TENDERTYPE_Check);
				pay.setBankAccountDetails(bankAccount.getID());
				pay.setAmount(getC_Currency_ID(), importe);
				
				// Fecha Vto
				pay.setDueDate(detail.getPaymentDate());
				
				// Guarda el pago
				if (!pay.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
					// Completa el pago
				} else if (!pay.processIt(DocAction.ACTION_Complete)) {
					throw new Exception(pay.getProcessMsg());
					// Guarda los cambios del procesamiento
				} else if (!pay.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				
				//Agrego el pago generado
				poGenerator.addCreditPayment(pay.getID(), pay.getPayAmt());
				
				//Genero las lineas de imputación, guardo y completo la OP
				poGenerator.generateLines();
				poGenerator.getAllocationHdr().updateTotalByLines();
				if(!poGenerator.getAllocationHdr().save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				poGenerator.completeAllocation();
				detail.setC_AllocationHdr_ID(poGenerator.getAllocationHdr().getC_AllocationHdr_ID());
				if (!detail.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				poGenerator.reset();
				
				//Actualizo el siguien número de la chequera
				chequera.setCurrentNext(chequera.getCurrentNext() + 1);
				if (!chequera.save()) {
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		} catch (AllocationGeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_processMsg = e.getMessage();
			return DocAction.STATUS_Invalid;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m_processMsg = e.getMessage();
			return DocAction.STATUS_Invalid;
		} finally{
			// Desactivo el bloqueo de secuencia
			poGenerator.setCurrentSeqLock(null);
			if(seqLock != null){
				seqLock.setIsActive(false);
				if(!seqLock.save()){
					setProcessMsg(CLogger.retrieveErrorAsString());
					return DocAction.STATUS_Invalid;
				}
			}
		}
				
		// Finaliza correctamente la acción
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}
	
	public List<MPaymentBatchPODetail> getBatchDetails() {
		List<MPaymentBatchPODetail> batchDetails = new ArrayList<MPaymentBatchPODetail>();
		//Construyo la query
		String sql = "SELECT * " + 
					 "FROM C_PaymentBatchPODetail " +
					 "WHERE " + 
					  "c_paymentbatchpo_id = ?";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			
			//Parámetros
			ps.setInt(1, this.getID());
			rs = ps.executeQuery();
			while (rs.next()) {
				batchDetails.add(new MPaymentBatchPODetail(getCtx(), rs, get_TrxName()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return batchDetails;
	}

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine(this, getDocStatus());
		return engine.processIt(action, getDocAction(), log);
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
		boolean isValid = true;
		
		// Si el tipo de documento no permite lotes fuera de fecha, se debe
		// actualizar automáticamente la fecha del lote con la fecha actual
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if(!dt.isAllowOtherBatchPaymentDate()
				&& getBatchDate().compareTo(Env.getDate()) != 0
				&& !TimeUtil.isSameDay(getBatchDate(), Env.getDate())){
			setBatchDate(Env.getDate());
			try{
				updateBatchPaymentDate();
			} catch(Exception e){
				setProcessMsg(e.getMessage());
				return DocAction.STATUS_Invalid;
			}
		}		
		
		//Valido que no haya datalles sin facturas
		for (MPaymentBatchPODetail detail : getBatchDetails()) {
			MBPartner bPartner = new MBPartner(getCtx(), detail.getC_BPartner_ID(), get_TrxName()); 
			if (detail.getInvoices() == null || detail.getInvoices().size() <= 0) {
				if (isValid)
					m_processMsg = Msg.getMsg(getCtx(), "PaymentDetailsWithoutInvoices") + " " + bPartner.getName();
				else 
					m_processMsg += ", " + bPartner.getName();
				isValid = false;
			}
		}
		
		//Valido que todos los proveedores sigan teniendo la forma de pago configurada y que coincida con la del detalle
		for (MPaymentBatchPODetail detail : getBatchDetails()) {
			MBPartner bPartner = new MBPartner(getCtx(), detail.getC_BPartner_ID(), get_TrxName()); 
			if (bPartner.getBatch_Payment_Rule() == null || !bPartner.getBatch_Payment_Rule().equals(detail.getBatch_Payment_Rule())) {
				if (isValid)
					m_processMsg = Msg.getMsg(getCtx(), "PaymentRuleChange") + " " + bPartner.getName();
				else 
					m_processMsg += ", " + bPartner.getName();
				isValid = false;
			}
		}
		
		//Valido que los importes abiertos de las facturas del lote sigan siendo los mismos
		for (MPaymentBatchPODetail detail : getBatchDetails()) {
			MBPartner bPartner = new MBPartner(getCtx(), detail.getC_BPartner_ID(), get_TrxName());
			for (MPaymentBatchPOInvoices detailInvoice : detail.getInvoices()) {
				MInvoice invoice = new MInvoice(getCtx(), detailInvoice.getC_Invoice_ID(), get_TrxName());
				MInvoicePaySchedule paySchedule = new MInvoicePaySchedule(getCtx(), detailInvoice.getC_InvoicePaySchedule_ID(), get_TrxName());
				BigDecimal convertedAmt = MCurrency.currencyConvert(
						paySchedule.getOpenAmount(), invoice.getC_Currency_ID(),
						getC_Currency_ID(), getBatchDate(), invoice.getAD_Org_ID(),
						getCtx());
				if (convertedAmt == null) {
					throw new IllegalArgumentException(Msg.getMsg(getCtx(), "ConvertionRateInvalid"));
				}
				
				if (!detailInvoice.getOpenAmount().equals(convertedAmt)) {
					if (isValid)
						m_processMsg = Msg.getMsg(getCtx(), "InvoiceOpenAmountChange") + " " + bPartner.getName() + "-" + invoice.getDocumentNo();
					else 
						m_processMsg += ", " + bPartner.getName() + "-" + invoice.getDocumentNo();
					isValid = false;
				}
			}
		}
		
		if (!isValid) {
			return DocAction.STATUS_Invalid;
		}
				
		setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}

	private void updateBatchPaymentDate() throws Exception{
		List<MPaymentBatchPODetail> details = getBatchDetails();
		for (MPaymentBatchPODetail mPaymentBatchPODetail : details) {
			if (mPaymentBatchPODetail.getPaymentDate().compareTo(getBatchDate()) < 0){
				mPaymentBatchPODetail.setPaymentDate(getCalculatedPaymentDateRule(getBatchDate()));
				if(!mPaymentBatchPODetail.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
			}
		}
	}
	
	public Timestamp getCalculatedPaymentDateRule(Timestamp baseDate){
		Timestamp calcDate = baseDate;
		if(getPaymentDateRule().equals(MPaymentBatchPO.PAYMENTDATERULE_LastDueDate)){
			Calendar baseCalendar = Calendar.getInstance();
			baseCalendar.setTimeInMillis(baseDate.getTime());
			baseCalendar.add(Calendar.DATE, Util.isEmpty(getAddDays(), true)?1:getAddDays());
			calcDate = new Timestamp(baseCalendar.getTimeInMillis());
		}
		return calcDate;
	}
	
	@Override
	public boolean approveIt() {
		return false;
	}

	@Override
	public boolean rejectIt() {
		return false;
	}

	@Override
	public boolean postIt() {
		return false;
	}

	@Override
	public boolean voidIt() {
		return true;
	}

	@Override
	public boolean closeIt() {
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
	public int getC_Currency_ID() {
		return Env.getContextAsInt(getCtx(), "$C_Currency_ID");
	}

	@Override
	public BigDecimal getApprovalAmt() {
		return null;
	}

}
