package org.openXpertya.rc;

import java.io.Serializable;
import java.math.BigDecimal;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountableDocumentLine;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MInvoiceLine;
import org.openXpertya.util.Env;

public class InvoiceLine extends DiscountableDocumentLine implements Cloneable, Serializable{

	private static final long serialVersionUID = 1L;
	
	/** ID interno de línea de factura (ver despues el tema de la facturas remotas) */	
	private Integer invoiceLineID;
	
	/** Línea de factura real */
	private MInvoiceLine realInvoiceLine;
	
	/** Línea de factura real */
	private MInvoice realInvoice;
	
	/** Referencia a la factura */	
	private Invoice invoice;
	
	/** Cantidad */
	private BigDecimal qty;
	
	/** Taza de impuesto */
	private BigDecimal taxRate;
	
	/** Precio de lista */
	private BigDecimal priceList;
	
	/** Monto de descuento de línea */
	private BigDecimal lineDiscountAmt;
	
	/** Precio */
	private BigDecimal price;
	
	/** ID de Producto relacionado con esta línea */
	private Integer productID;
	
	/** Monto de descuento de documento asociado a esta línea */
	private BigDecimal documentDiscountAmt;
	
	/** Impuesto incluído en el precio */
	private boolean isTaxIncluded;
	
	/** ID de la factura */	
	private Integer invoiceID;
	
	/** Currency_ID */	
	private Integer C_Currency_ID = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );

	/**
	 * Crea una línea de factura a partir de la línea de factura real, se copian
	 * los datos necesarios. Además se asocia la factura a la que pertenece, la
	 * cual puede ser null en caso que se desee asignar luego. Se realiza la
	 * sincronización de la línea de factura creada con la línea de factura
	 * real.
	 * 
	 * @param invoice
	 *            factura cabecera de la línea
	 * @param invoiceLine
	 *            línea de factura real
	 * @return línea de factura creada
	 */
	public static InvoiceLine createFrom(Invoice invoice, MInvoiceLine invoiceLine){
		InvoiceLine newInvoiceLine = new InvoiceLine(invoice);
		newInvoiceLine.setInvoiceLineID(invoiceLine.getID());
		newInvoiceLine.setPrice(invoiceLine.getPriceEntered());
		newInvoiceLine.setPriceList(invoiceLine.getPriceList());
		newInvoiceLine.setProductID(invoiceLine.getM_Product_ID());
		newInvoiceLine.setQty(invoiceLine.getQtyInvoiced());
		newInvoiceLine.setTaxIncluded(invoiceLine.isTaxIncluded());
		newInvoiceLine.setTaxRate(invoiceLine.getTaxRate());
		newInvoiceLine.sincronize(invoiceLine);
		newInvoiceLine.setInvoiceID(invoiceLine.getInvoice().getID());
		newInvoiceLine.setRealInvoice(new MInvoice(invoice.getCtx(), invoiceLine.getInvoice().getID(), invoice.getTrxName()));
		return newInvoiceLine;
	}
	
	/**
	 * Constructor
	 * @param invoice
	 */
	public InvoiceLine(Invoice invoice) {
		super(invoice);
	}

	/**
	 * Actualiza la línea de factura interna real. No actualiza la factura real
	 * de la base de datos, para la sincronización se debe llamar al método
	 * {@link #sincronizeInvoiceLine()}. El método
	 * {@link #updateInvoiceLine(MInvoiceLine)} sí realiza la
	 * sincronización automáticamente.
	 * 
	 * @param invoiceLineID
	 *            id de la línea de factura
	 */
	public void updateInvoiceLine(Integer invoiceLineID){
		setInvoiceLineID(invoiceLineID);
	}

	/**
	 * Sincroniza el id de la línea de factura registrada con la factura real de la BD
	 */
	public void sincronize(){
		setRealInvoiceLine(getInvoiceLine(getInvoiceLineID()));
	}
	
	/**
	 * Sincroniza el id de la línea de factura parámetro con la factura real de la BD
	 */
	public void sincronize(Integer invoiceLineID){
		setRealInvoiceLine(getInvoiceLine(invoiceLineID));
	}	
	
	/**
	 * Sincroniza la línea de factura parámetro
	 */
	public void sincronize(MInvoiceLine line){
		setRealInvoiceLine(line);
	}	
	
	/**
	 * Actualiza la línea de factura interna real. Realiza la sincronización de este objeto con la
	 * línea de factura real.
	 * 
	 * @param invoiceLine
	 *            línea de factura real
	 */
	public void updateInvoiceLine(MInvoiceLine invoiceLine){
		setInvoiceLineID(invoiceLine.getID());
		sincronize(invoiceLine);
	}	

	/**
	 * @param invoiceLineID id de la línea de factura
	 * @return línea de factura real de la bd
	 */
	protected MInvoiceLine getInvoiceLine(Integer invoiceLineID){
		return new MInvoiceLine(getInvoice().getCtx(), invoiceLineID, getInvoice().getTrxName());
	}	

	@Override
	public IDocument getDocument() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoiceLineID(Integer invoiceLineID) {
		this.invoiceLineID = invoiceLineID;
	}

	public Integer getInvoiceLineID() {
		return invoiceLineID;
	}

	public void setRealInvoiceLine(MInvoiceLine invoiceLine) {
		this.realInvoiceLine = invoiceLine;
	}

	public MInvoiceLine getRealInvoiceLine() {
		return realInvoiceLine;
	}

	
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	
	
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public void setPriceList(BigDecimal priceList) {
		this.priceList = priceList;
	}

	public void setProductID(Integer productID) {
		this.productID = productID;
	}

	public BigDecimal getDocumentDiscountAmt() {
		return documentDiscountAmt;
	}
	
	public void setTaxIncluded(boolean isTaxIncluded) {
		this.isTaxIncluded = isTaxIncluded;
	} 
	
	public Integer getInvoiceID() {
		return invoiceID;
	}

	public void setInvoiceID(Integer invoiceID) {
		this.invoiceID = invoiceID;
	}	
	
	public MInvoice getRealInvoice(){
		//return new MInvoice(getInvoice().getCtx(), invoiceID, getInvoice().getTrxName() );
		return this.realInvoice;
	}
	
	public void setRealInvoice(MInvoice realInvoice) {
		this.realInvoice = realInvoice;
	}
	
	/*
	 * **********************************************
	 * 	 HEREDADOS DE DISCOUNTABLE DOCUMENT LINE
	 * **********************************************
	 */

	@Override
	public BigDecimal getLineBonusAmt() {
		// Nada ya que no se juega con bonus en esta funcionalidad  
		return null;
	}

	@Override
	public BigDecimal getLineDiscountAmt() {
		return lineDiscountAmt;
	}

	@Override
	public BigDecimal getPrice() {
		if(C_Currency_ID != getRealInvoice().getC_Currency_ID()){
			return MCurrency.currencyConvert(price, getRealInvoice().getC_Currency_ID(), C_Currency_ID, getRealInvoice().getDateInvoiced(), getRealInvoice().getAD_Org_ID(), getInvoice().getCtx());	
		}
		return price;
	}

	@Override
	public BigDecimal getPriceList() {
		if(C_Currency_ID != getRealInvoice().getC_Currency_ID()){
			return MCurrency.currencyConvert(priceList, getRealInvoice().getC_Currency_ID(), C_Currency_ID, getRealInvoice().getDateInvoiced(), getRealInvoice().getAD_Org_ID(), getInvoice().getCtx());	
		}
		return priceList;
	}

	@Override
	public int getProductID() {
		return productID;
	}

	@Override
	public BigDecimal getQty() {
		return qty;
	}

	@Override
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	@Override
	public boolean isTaxIncluded() {
		return isTaxIncluded;
	}

	@Override
	public void setDocumentDiscountAmt(BigDecimal discountAmt) {
		this.documentDiscountAmt = discountAmt;
	}

	@Override
	public void setLineBonusAmt(BigDecimal lineBonusAmt) {
		// No se debe actualizar nada	
	}

	@Override
	public void setLineDiscountAmt(BigDecimal lineDiscountAmt) {
		this.lineDiscountAmt = lineDiscountAmt;
	}

	@Override
	public void setPrice(BigDecimal newPrice) {
		this.price = newPrice;
	}
	
	/*
	 * **********************************************
	 */

	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}

	@Override
	public void setDiscount(BigDecimal discount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BigDecimal getDiscount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getLineManualDiscountID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLineManualDiscountID(Integer lineManualDiscountID) {
		// TODO Auto-generated method stub
		
	}
}
