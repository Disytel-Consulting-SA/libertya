package org.openXpertya.rc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.DiscountableDocument;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocumentDiscount;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.util.Env;

import bsh.This;

public class Invoice extends DiscountableDocument implements Serializable{

	private static final long serialVersionUID = 1L;

	/** ID interno de factura (ver despues el tema de la facturas remotas) */	
	private Integer invoiceID;
	
	/** Factura interna */	
	private MInvoice realInvoice;
	
	/**
	 * Importe manual que realmente se desea pagar de esta factura (debería ser
	 * menor que el invoiceopen de la factura real)
	 */
	private BigDecimal manualAmt;
	
	/** Contexto */
	private Properties ctx;
	
	/** Nombre de la transacción en curso */
	private String trxName;
	
	/** Líneas de la factura */
	private List<InvoiceLine> lines;
	
	/** Monto total de descuento a nivel de documento */
	private BigDecimal totalDocumentDiscount = BigDecimal.ZERO;
	
	/** Monto total de descuento de entidad comercial */
	private BigDecimal totalBPartnerDiscount = BigDecimal.ZERO;
	
	/** Monto total de descuento de esquema de vencimientos */
	private BigDecimal totalPaymentTermDiscount = BigDecimal.ZERO;
	
	private BigDecimal totalManualGeneralDiscount = BigDecimal.ZERO;
	
	/**
	 * Monto pagado para esta factura (si se paga completamente, entonces
	 * debería ser igual a {@link #manualAmt} )
	 */
	private BigDecimal paidAmt = BigDecimal.ZERO;
	
	/** Fecha */
	private Date date;
	
	/** Monto base para aplicar descuento de entidad comercial */
	private BigDecimal bpartnerDiscountSchemaBaseAmt;
	
	/** Currency_ID */	
	private Integer C_Currency_ID = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );
	
	/**
	 * Constructor
	 * @param ctx
	 * @param trxName
	 */
	public Invoice(Properties ctx, String trxName) {
		setCtx(ctx);
		setTrxName(trxName);
		setManualAmt(BigDecimal.ZERO);
	}

	/**
	 * Actualiza la factura interna real. No actualiza la factura real de la
	 * base de datos, para la sincronización se debe llamar al método
	 * {@link #sincronizeInvoice()}. El método
	 * {@link #updateInvoice(MInvoice)} sí realiza la sincronización
	 * automáticamente.
	 * 
	 * @param invoiceID
	 *            id de factura
	 */
	public void updateInvoice(Integer invoiceID){
		setInvoiceID(invoiceID);
	}
	
	/**
	 * Actualiza la factura interna real. Realiza la sincronización de este objeto con la
	 * factura real.
	 * 
	 * @param invoice
	 *            factura real
	 */
	public void updateInvoice(MInvoice invoice){
		setInvoiceID(invoice.getID());
		sincronize(invoice);
	}

	/**
	 * Sincroniza el id de la factura registrada con la factura real de la BD
	 */
	public void sincronize(){
		setRealInvoice(getInvoice(getInvoiceID()));
		loadData();
		loadLines();
	}
	
	/**
	 * Sincroniza el id de la factura parámetro con la factura real de la BD
	 */
	public void sincronize(Integer invoiceID){
		setInvoiceID(invoiceID);
		sincronize();
	}
	
	/**
	 * Sincroniza la factura parámetro con la factura real de la BD
	 */
	public void sincronize(MInvoice invoice){
		setRealInvoice(invoice);
		setInvoiceID(invoice.getID());
		loadData();
		loadLines();
	}

	/**
	 * Carga la información de {@link This} en base a la información de la
	 * factura registrada y sincronizada
	 */
	protected void loadData(){
		setDate(getRealInvoice().getDateInvoiced());
		if(C_Currency_ID != getRealInvoice().getC_Currency_ID()){
			setManualAmt(MCurrency.currencyConvert(getRealInvoice().getGrandTotal(), getRealInvoice().getC_Currency_ID(), C_Currency_ID, getRealInvoice().getDateInvoiced(), getRealInvoice().getAD_Org_ID(), getCtx()));
		}
		else{
			setManualAmt(getRealInvoice().getGrandTotal());	
		}
	}
	
	/**
	 * Realiza la carga de las líneas de la factura, creando las nuevas líneas a
	 * partir de las de la factura real.
	 */
	protected void loadLines(){
		setLines(new ArrayList<InvoiceLine>());
		MInvoiceLine[] lines = getRealInvoice().getLines();
		for (MInvoiceLine mInvoiceLine : lines) {
			getLines().add(InvoiceLine.createFrom(this, mInvoiceLine));
		}
	}
	
	
	/**
	 * @param invoiceID id de la factura
	 * @return factura real de la bd
	 */
	protected MInvoice getInvoice(Integer invoiceID){
		return new MInvoice(getCtx(), invoiceID, getTrxName());
	}

	/**
	 * @param withDocumentDiscount
	 *            true si se debe contemplar descuentos/recargos, false caso
	 *            contrario
	 * @return el total manual ingresado a pagar de esta factura contemplando o
	 *         no recargos/descuentos a nivel de documento si es que se
	 *         asociaron
	 */
	public BigDecimal getTotalAmt(boolean withDocumentDiscount) {
		BigDecimal docDiscount = BigDecimal.ZERO;
		if(withDocumentDiscount){
			docDiscount = getTotalDocumentDiscount();
		}
		return getManualAmt().subtract(docDiscount);
	}
	
	public void setRealInvoice(MInvoice realInvoice) {
		this.realInvoice = realInvoice;
	}

	public MInvoice getRealInvoice() {
		return realInvoice;
	}

	public void setInvoiceID(Integer invoiceID) {
		this.invoiceID = invoiceID;
	}


	public Integer getInvoiceID() {
		return invoiceID;
	}


	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}

	public void setManualAmt(BigDecimal manualAmt) {
		this.manualAmt = manualAmt;
		setBpartnerDiscountSchemaBaseAmt(manualAmt);
	}

	public BigDecimal getManualAmt() {
		return manualAmt;
	}
	
	public BigDecimal getManualAmtOriginalCurrency() {
		if(C_Currency_ID != getRealInvoice().getC_Currency_ID()){
			return MCurrency.currencyConvert(manualAmt, C_Currency_ID, getRealInvoice().getC_Currency_ID(), getRealInvoice().getDateInvoiced(), getRealInvoice().getAD_Org_ID(), getCtx());	
		}
		return manualAmt;
	}

	public void setLines(List<InvoiceLine> lines) {
		this.lines = lines;
	}

	public List<InvoiceLine> getLines() {
		return lines;
	}
	
	public BigDecimal getTotalDocumentDiscount() {
		return totalDocumentDiscount;
	}

	/*
	 * **********************************************
	 * 		HEREDADOS DE DISCOUNTABLE DOCUMENT
	 * **********************************************
	 */
	
	@Override
	protected IDocumentLine createDocumentLine(Object originalLine) {
		return (InvoiceLine)originalLine;
	}

	@Override
	protected List<? extends Object> getOriginalLines() {
		return getLines();
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Override
	public boolean isCalculateNetDiscount() {
		return false;
	}

	@Override
	public void setDocumentDiscountChargeID(int chargeID) {
		// No se utiliza aquí
	}

	@Override
	public void setDocumentReferences(MDocumentDiscount documentDiscount) {
		documentDiscount.setC_Invoice_ID(getInvoiceID());
	}

	@Override
	public void setTotalBPartnerDiscount(BigDecimal discountAmount) {
		totalBPartnerDiscount = discountAmount; 
	}

	@Override
	public void setTotalDocumentDiscount(BigDecimal discountAmount) {
		this.totalDocumentDiscount = discountAmount;		
	}
	
	/*
	 * **********************************************
	 */
	
	public BigDecimal getTotalBPartnerDiscount() {
		return totalBPartnerDiscount;
	}

	public void setPaidAmt(BigDecimal paidAmt) {
		this.paidAmt = paidAmt;
	}

	public BigDecimal getPaidAmt() {
		return paidAmt;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setBpartnerDiscountSchemaBaseAmt(
			BigDecimal bpartnerDiscountSchemaBaseAmt) {
		this.bpartnerDiscountSchemaBaseAmt = bpartnerDiscountSchemaBaseAmt;
	}

	public BigDecimal getBpartnerDiscountSchemaBaseAmt() {
		return bpartnerDiscountSchemaBaseAmt;
	}

	public void setTotalPaymentTermDiscount(BigDecimal totalPaymentTermDiscount) {
		this.totalPaymentTermDiscount = totalPaymentTermDiscount;
	}

	public BigDecimal getTotalPaymentTermDiscountOriginalCurrency() {
		if(C_Currency_ID != getRealInvoice().getC_Currency_ID()){
			return MCurrency.currencyConvert(totalPaymentTermDiscount, C_Currency_ID, getRealInvoice().getC_Currency_ID(), getRealInvoice().getDateInvoiced(), getRealInvoice().getAD_Org_ID(), getCtx());	
		}
		return totalPaymentTermDiscount;
	}
	
	public BigDecimal getTotalPaymentTermDiscount() {
		return totalPaymentTermDiscount;
	}
	
	public void setTotalManualGeneralDiscount(BigDecimal discountAmount) {
		this.totalManualGeneralDiscount = discountAmount;		
	}
	
	public BigDecimal getTotalManualGeneralDiscount() {
		return this.totalManualGeneralDiscount;		
	}
}
