package org.openXpertya.recovery;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPaymentRecoveryConfig;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MReference;
import org.openXpertya.model.MTax;
import org.openXpertya.model.PO;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.HTMLMsg;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Corrección de cobros/pagos por Devolución/Recupero.
 * 
 * @author Matías Cap - Disytel
 */
public abstract class PaymentRecoveryStrategyProcess implements IRecoverySource {

	private static final String REFERENCE_TYPE_NAME = "Recovery Type";
	private static final String REFERENCE_SUBTYPE_NAME = "Recovery SubType";
	
	/** Parámetros del proceso */
	private Map<String, Object> parametersValues;
	
	/** Contexto */
	private Properties ctx;

	/** Nombre de transacción */
	private String trxName;
	
	/** Generador de Allocations */
	private AllocationGenerator allocGenerator;
	
	/** Factura origen */
	private MInvoice invoice;
	
	/** Comprobante de recupero/devolución a imputar */
	private MInvoice document;
	
	/** Organización donde se debe crear el allocation */
	private Integer orgID;
	
	/** Configuración de corrección de payments */
	private MPaymentRecoveryConfig paymentRecoveryConfig;
	
	/** Tipo de Recupero */
	private RecoveryType recoveryType;
	
	public PaymentRecoveryStrategyProcess(){
		
	}
	
	public PaymentRecoveryStrategyProcess(Properties ctx, Map<String, Object> params, String trxName){
		setCtx(ctx);
		setTrxName(trxName);
		setParametersValues(params);
	}
	
	public String doIt() throws Exception {
		// Inicializar información
		initialize();
		// Crea el allocation
		createAllocation();
		// Agrega los débitos
		addDebits();
		// Agrega los créditos
		addCredits();
		// Genera las líneas del allocation
		generateAllocationLines();
		// Completar asignación
		completeAllocation();
		// Mensaje final
		return getMsg();
	}

	protected void initialize() throws Exception{
		setInvoice(new MInvoice(getCtx(), getInvoiceID(), getTrxName()));
		setAllocGenerator(new AllocationGenerator(getCtx(), getTrxName()));
		Integer orgID = Env.getAD_Org_ID(getCtx());
		setOrgID(!Util.isEmpty(orgID, true)?orgID:getInvoice().getAD_Org_ID());
		initPaymentRecoveryConfig();
		createRecoveryType();
		createDocument();
	}
	
	protected void initPaymentRecoveryConfig() throws Exception{
		MPaymentRecoveryConfig cfc = MPaymentRecoveryConfig.get(getCtx(), getOrgID(), getTrxName());
		if(cfc == null){
			cfc = MPaymentRecoveryConfig.get(getCtx(), 0, getTrxName());
			if(cfc == null){
				MOrg org = MOrg.get(getCtx(), getOrgID());
				throw new Exception(
						Msg.getMsg(getCtx(), "PaymentRecoveryConfigNotFound", new Object[] { org.getName() }));
			}
		}
		setPaymentRecoveryConfig(cfc);
	}
	
	protected Integer getInvoiceID(){
		return (Integer) getParametersValues().get("C_INVOICE_ID");
	}
	
	/**
	 * Crea un allocation Manual, para la organización actual (si es 0 toma la
	 * de la factura) y la EC de la factura
	 * 
	 * @throws Exception
	 */
	protected void createAllocation() throws Exception{
		getAllocGenerator().createAllocationHdr(MAllocationHdr.ALLOCATIONTYPE_Manual, getOrgID(),
				getInvoice().getC_BPartner_ID());
	}
	
	/**
	 * Genera las líneas del allocation
	 * @throws Exception
	 */
	protected void generateAllocationLines() throws Exception{
		getAllocGenerator().generateLines();
	}
	
	/**
	 * Completa el allocation
	 * 
	 * @throws Exception
	 */
	protected void completeAllocation() throws Exception{
		getAllocGenerator().completeAllocation();
	}
	
	protected void addDebits() throws Exception{
		getAllocGenerator().addDebitInvoice(getDocument().getID(), getDocument().getGrandTotal());
	}
	
	protected void addCredits() throws Exception{
		getAllocGenerator().addCreditDocument(getRecoveryType().getRecoveryID(), getRecoveryType().getRecoveryAmt(),
				getRecoveryType().getAllocationDocumentType());
	}
	
	@Override
	public Integer getPaymentRecoveryID() {
		return (Integer)getParametersValues().get("C_PAYMENT_ID");
	}

	@Override
	public Integer getCashLineRecoveryID() {
		return (Integer)getParametersValues().get("C_CASHLINE_ID");
	}

	@Override
	public Integer getCreditRecoveryID() {
		return (Integer)getParametersValues().get("C_INVOICE_CREDIT_ID");
	}
	
	/**
	 * Crea el documento de recupero en base a los datos de configuración
	 * parámetro
	 * 
	 * @param docTypeID
	 *            ID del tipo de documento del comprobante
	 * @param productID
	 *            ID del artículo del comprobante
	 * @throws Exception
	 *             en caso de error
	 */
	protected MInvoice createConfigInvoice(Integer docTypeID, Integer productID, BigDecimal total) throws Exception{
		return createConfigInvoice(docTypeID, productID, total, null);
	}
	
	/**
	 * Crea el documento de recupero en base a los datos de configuración
	 * parámetro
	 * 
	 * @param docTypeID
	 *            ID del tipo de documento del comprobante
	 * @param productID
	 *            ID del artículo del comprobante
	 * @param posJournalID
	 *            ID de la caja diaria a asociar al comprobante
	 * @throws Exception
	 *             en caso de error
	 */
	protected MInvoice createConfigInvoice(Integer docTypeID, Integer productID, BigDecimal total, Integer posJournalID) throws Exception{
		// Crea el comprobante
		MInvoice invoice = new MInvoice(getCtx(), 0, getTrxName());
		invoice.setC_BPartner_ID(getInvoice().getC_BPartner_ID());
		invoice.setC_DocTypeTarget_ID(docTypeID);
		invoice.setPaymentRule(getInvoice().getPaymentRule());
		invoice.setCreateCashLine(false);
		invoice.setIsVoidable(true);
		if(!invoice.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Línea del comprobante
		MInvoiceLine invoiceLine = new MInvoiceLine(invoice);
		invoiceLine.setM_Product_ID(productID, true);
		invoiceLine.setQty(1);
		invoiceLine.setPrice(total);
		invoiceLine.setTax();
		if(Util.isEmpty(invoiceLine.getC_Tax_ID(), true)){
			MTax tax = MTax.getTaxExemptRate(getCtx(), getTrxName());
			if(tax != null){
				invoiceLine.setC_Tax_ID(tax.getID());
			}
		}
		if(!invoiceLine.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Completar
		if(!DocumentEngine.processAndSave(invoice, MInvoice.DOCACTION_Complete, false)){
			throw new Exception(invoice.getProcessMsg());
		}
		// Seteo la caja diaria origen luego de completar para que no valide si
		// se encuentra cerrada
		if(!Util.isEmpty(posJournalID, true)){
			invoice.setC_POSJournal_ID(posJournalID);
			if(!invoice.save()){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		
		return invoice;
	}
	
	/**
	 * Crea el payment de devolución
	 * 
	 * @param fromPaymentID
	 *            payment ID a partir del cual se debe realizar el payment de
	 *            devolución
	 * @return payment de devolución creado a partir del payment parámetro
	 */
	protected MPayment createReturnedPayment(Integer fromPaymentID) throws Exception{
		// Obtengo el payment original parámetro
		MPayment fromPayment = new MPayment(getCtx(), fromPaymentID, getTrxName());
		MPayment toPayment = new MPayment(getCtx(), 0, getTrxName());
		PO.copyValues(fromPayment, toPayment);
		// Tipo de documento de devolución
		String docTypeKey = fromPayment.isReceipt() ? MDocType.DOCTYPE_CustomerReceiptReturn
				: MDocType.DOCTYPE_VendorPaymentReturn;
		MDocType dtr = MDocType.getDocType(getCtx(), docTypeKey, getTrxName());
		toPayment.setC_DocType_ID(dtr.getID());
		// Guardar
		if(!toPayment.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Completar
		if(!DocumentEngine.processAndSave(toPayment, MPayment.DOCACTION_Complete, false)){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		
		return toPayment;
	}

	protected String getMsg(){
		HTMLMsg msg = new HTMLMsg();
		HTMLMsg.HTMLList list = msg.createList("summary", "ul",
				Msg.translate(getCtx(), "@Type@: ")
						+ getRecoveryListName(REFERENCE_TYPE_NAME, (String) getParametersValues().get("TYPE")) + " "
						+ getRecoveryListName(REFERENCE_SUBTYPE_NAME, (String) getParametersValues().get("SUBTYPE")));
		msg.createAndAddListElement("invoice", Msg.translate(getCtx(),
				"@C_Invoice_ID@: " + getInvoice().getDocumentNo()), list);
		msg.addList(list);
		
		appendSuccesfullyFinalMsg(msg);
		
		return msg.toString();
	}
	
	protected String getRecoveryListName(String listName, String listValue){
		return Util.isEmpty(listValue, true) ? ""
				: MRefList.getListName(getCtx(), MReference.getReferenceID(listName), listValue);
	}
	
	protected abstract void createDocument() throws Exception;
	
	protected abstract void createRecoveryType() throws Exception;
	
	protected abstract void appendSuccesfullyFinalMsg(HTMLMsg msg);

	protected AllocationGenerator getAllocGenerator() {
		return allocGenerator;
	}

	protected void setAllocGenerator(AllocationGenerator allocGenerator) {
		this.allocGenerator = allocGenerator;
	}

	protected MInvoice getInvoice() {
		return invoice;
	}

	protected void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected MPaymentRecoveryConfig getPaymentRecoveryConfig() {
		return paymentRecoveryConfig;
	}

	protected void setPaymentRecoveryConfig(MPaymentRecoveryConfig paymentRecoveryConfig) {
		this.paymentRecoveryConfig = paymentRecoveryConfig;
	}

	protected RecoveryType getRecoveryType() {
		return recoveryType;
	}

	protected void setRecoveryType(RecoveryType recoveryType) {
		this.recoveryType = recoveryType;
	}

	protected MInvoice getDocument() {
		return document;
	}

	protected void setDocument(MInvoice document) {
		this.document = document;
	}

	public Map<String, Object> getParametersValues() {
		return parametersValues;
	}

	public void setParametersValues(Map<String, Object> parametersValues) {
		this.parametersValues = parametersValues;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}
}
