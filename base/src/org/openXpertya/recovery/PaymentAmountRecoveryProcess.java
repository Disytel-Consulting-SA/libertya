package org.openXpertya.recovery;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MFactAcct;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPayment;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * 
 * Este caso corresponde cuando el importe del payment registrado en el sistema
 * es diferente a lo que esta impreso en el comprobante papel. Por ejemplo, un
 * cupón de tarjeta que se carga por posnet tiene un importe distinto al payment
 * en tarjeta de Libertya. Para los casos que se explicarán a continuación, vale
 * aclarar que el importe del payment quedará con el importe real parámetro.
 * <ol>
 * En base a la diferencia entre el importe real y el importe registrado, se
 * debe tratar de forma diferente:
 * <li>Si el importe real del payment es mayor al registrado, esto es, el
 * payment del sistema es de importe menor al real.
 * <ul>
 * <li>Crear una ND en base a la configuración de recupero con el importe de
 * diferencia.</li>
 * <li>Incorporar esta ND en el mismo allocation donde se encuentran la factura
 * origen e imputarla en una línea con el payment original.</li>
 * </ul></li>
 * <li>Si el importe real del payment es menor al registrado, esto es, el
 * payment del sistema es de importe mayor al real.
 * <ul>
 * <li>Crear una NC en base a la configuración de recupero con el importe de
 * diferencia.</li>
 * <li>Incorporar esta NC en el mismo allocation donde se encuentran la factura
 * origen e imputarla a esta última ya que terminará con importe mayor que el
 * payment.</li>
 * <li>Modificar la línea de imputación entre la factura y el payment original
 * por el importe real del payment.</li>
 * </ul></li>
 * </ol>
 * 
 * @author Matías Cap - Disytel
 *
 */
public class PaymentAmountRecoveryProcess extends PaymentRecoveryStrategyProcess {
	
	/** Payment parámetro con el importe erróneo */
	private MPayment badPayment;
	
	/** Diferencia entre el importe real y el importe del payment */
	private BigDecimal diffAmt;
	
	/**
	 * Línea de allocation donde se encuentra relacionada la factura y el
	 * payment
	 */
	private MAllocationLine allocLineRelated;
	
	/** Allocation relacionada entre la factura y el payment */
	private MAllocationHdr allocHdrRelated;
	
	/**
	 * NC de recupero cuando el importe real del pago es menor. Se debe
	 * compensar con una NC asociada a la factura
	 */
	private MInvoice invoiceCredit;

	public PaymentAmountRecoveryProcess(){
		
	}
	
	public PaymentAmountRecoveryProcess(Properties ctx, Map<String, Object> params, String trxName){
		super(ctx, params, trxName);
	}
	
	@Override
	protected void createAllocation() throws Exception{
		// Obtener el allocation donde se encuentra la factura y el payment
		Integer allocationLineID = DB.getSQLValue(getTrxName(),
				"SELECT distinct c_allocationline_id FROM c_allocationline WHERE c_invoice_id = ? and c_payment_id = ?",
				getInvoice().getID(), getBadPayment().getID());
		if(Util.isEmpty(allocationLineID, true)){
			throw new Exception(Msg.getMsg(getCtx(), "PaymentRecoveryAllocationNotFound"));
		}
		setAllocLineRelated(new MAllocationLine(getCtx(), allocationLineID, getTrxName()));
		setAllocHdrRelated(new MAllocationHdr(getCtx(), getAllocLineRelated().getC_AllocationHdr_ID(), getTrxName()));
		// Se reactiva el allocation para poder agregarle líneas
		if (!DocumentEngine.processAndSave(getAllocHdrRelated(), MAllocationHdr.DOCACTION_Re_Activate,
				false)) {
			throw new Exception(getAllocHdrRelated().getProcessMsg());
		}
	}
	
	protected BigDecimal getRealAmt(){
		return (BigDecimal)getParametersValues().get("AMT");
	}
	
	protected void updatePaymentAmount() throws Exception{
		// Si fue contabilizado, entonces se debe eliminar la contabilidad generada
		if(getBadPayment().isPosted()){
			MFactAcct.delete(getBadPayment().get_Table_ID(), getBadPayment().getID(), getTrxName());
			getBadPayment().setPosted(false);
		}
		getBadPayment().setPayAmt(getRealAmt());
		if(!getBadPayment().save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	protected void updateAllocationLineRelated() throws Exception{
		getAllocLineRelated().setAmount(getRealAmt());
		if(!getAllocLineRelated().save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	@Override
	protected void createRecoveryType() throws Exception {
		RecoveryType rt = null;
		// Obtener el payment erróneo
		setBadPayment(new MPayment(getCtx(), getPaymentRecoveryID(), getTrxName()));
		// Actualizo la diferencia de importes para determinar la resolución
		setDiffAmt(getRealAmt().subtract(getBadPayment().getPayAmt()));
		// Si la diferencia es mayor a 0, entonces el importe real es mayor que el registrado
		if(getDiffAmt().compareTo(BigDecimal.ZERO) > 0){
			rt = createRecoveryHigher();
		}
		// Si el importe real es menor, entonces 
		else{
			rt = createRecoveryLess();
		}
		setRecoveryType(rt);
		
	}
	
	/***
	 * Crea la corrección del payment para cuando el importe real del pago es
	 * menor al registrado en el sistema, entonces el recupero en este caso es
	 * una NC por la diferencia creada a partir de la configuración. Luego, se
	 * debe modificar la línea del allocation relacionada al pago y factura con
	 * el importe real del payment y asociar la NC contra la factura en una
	 * nueva linea del mismo allocation.
	 * 
	 * @return el tipo de recupero, en este caso la NC.
	 */
	protected RecoveryType createRecoveryLess() throws Exception{
		MInvoice invoiceCredit = createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Credit_Recovery_ID(),
				getPaymentRecoveryConfig().getM_Product_Recovery_ID(), getDiffAmt().abs());
		setInvoiceCredit(invoiceCredit);
		return RecoveryTypeFactory.getInstance(this, RecoveryTypeFactory.RECOVERY_INVOICE);
	}
	
	@Override
	public Integer getCreditRecoveryID() {
		return getInvoiceCredit().getID();
	}
	
	/**
	 * Crea la corrección del payment para cuando el importe real del pago es
	 * mayor al registrado en el sistema, entonces el recupero es el mismo
	 * payment modificado. Luego, se imputa a una ND en el mismo allocation, en
	 * una línea nueva.
	 * 
	 * @return el tipo de recupero creado, en este caso de tipo payment,
	 *         referenciando al pago erróneo
	 */
	protected RecoveryType createRecoveryHigher() throws Exception{
		return RecoveryTypeFactory.getInstance(this, RecoveryTypeFactory.RECOVERY_PAYMENT);
	}
	
	@Override
	protected void createDocument() throws Exception {
		MInvoice debit = null;
		// Si la diferencia es mayor a 0, entonces el importe real es mayor que el registrado
		if(getDiffAmt().compareTo(BigDecimal.ZERO) > 0){
			debit = createDocumentHigher();
		}
		// Si el importe real es menor, entonces 
		else{
			debit = createDocumentLess();
		}
		setDocument(debit);
	}
	
	protected MInvoice createDocumentLess() throws Exception{
		return getInvoice();
	}
	
	protected MInvoice createDocumentHigher() throws Exception{
		return createConfigInvoice(getPaymentRecoveryConfig().getC_DocType_Recovery_ID(),
				getPaymentRecoveryConfig().getM_Product_Recovery_ID(), getDiffAmt().abs());
	}
	
	@Override
	protected void addCredits() throws Exception{
		// Actualizar el importe del payment 
		updatePaymentAmount();
	}

	@Override
	protected void addDebits() throws Exception{
		// No hace nada en el allocation generator
	}
	
	@Override
	protected void generateAllocationLines() throws Exception{
		// Modificar la línea del allocation si es requerido
		if(getInvoiceCredit() != null){
			updateAllocationLineRelated();
		}
		MAllocationLine newAllocLine = new MAllocationLine(getAllocHdrRelated());
		newAllocLine.setC_Invoice_ID(getDocument().getID());
		getRecoveryType().addToAllocationLine(newAllocLine);
		newAllocLine.setAmount(getDiffAmt().abs());
		if(!newAllocLine.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	@Override
	protected void completeAllocation() throws Exception{
		// Completar el allocation
		if (!DocumentEngine.processAndSave(getAllocHdrRelated(), MAllocationHdr.DOCACTION_Complete,
				false)) {
			throw new Exception(getAllocHdrRelated().getProcessMsg());
		}
	}

	protected MPayment getBadPayment() {
		return badPayment;
	}

	protected void setBadPayment(MPayment badPayment) {
		this.badPayment = badPayment;
	}

	protected BigDecimal getDiffAmt() {
		return diffAmt;
	}

	protected void setDiffAmt(BigDecimal diffAmt) {
		this.diffAmt = diffAmt;
	}

	protected MAllocationLine getAllocLineRelated() {
		return allocLineRelated;
	}

	protected void setAllocLineRelated(MAllocationLine allocLineRelated) {
		this.allocLineRelated = allocLineRelated;
	}

	protected MInvoice getInvoiceCredit() {
		return invoiceCredit;
	}

	protected void setInvoiceCredit(MInvoice invoiceCredit) {
		this.invoiceCredit = invoiceCredit;
	}

	@Override
	protected void appendSuccesfullyFinalMsg(HTMLMsg msg) {
		HTMLMsg.HTMLList list = msg.createList("summarycustom", "ul", Msg.getMsg(getCtx(), "Resolutions"));
		msg.createAndAddListElement("payment", Msg.translate(getCtx(),
				"@PaymentProcessed@ " + getBadPayment().getDocumentNo() + ". @Amt@ " + getRealAmt()), list);
		msg.createAndAddListElement("allocation", Msg.translate(getCtx(),
				"@C_AllocationHdr_ID@ @Updated@: " + getAllocHdrRelated().getDocumentNo()), list);
		if(getInvoiceCredit() != null){
			msg.createAndAddListElement(
					"invoicecredit", Msg.translate(getCtx(), "@InvoiceCreditGenerated@: "
							+ getInvoiceCredit().getDocumentNo() + ". @Amt@ " + getInvoiceCredit().getGrandTotal()),
					list);
		}
		else{
			msg.createAndAddListElement(
					"invoicedebit", Msg.translate(getCtx(), "@InvoiceDebitGenerated@: "
							+ getDocument().getDocumentNo() + ". @Amt@ " + getDocument().getGrandTotal()),
					list);
		}
		
		msg.addList(list);
	}

	protected MAllocationHdr getAllocHdrRelated() {
		return allocHdrRelated;
	}

	protected void setAllocHdrRelated(MAllocationHdr allocHdrRelated) {
		this.allocHdrRelated = allocHdrRelated;
	}

}
