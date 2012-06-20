package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.model.DiscountableDocument;
import org.openXpertya.model.MDocumentDiscount;

/**
 * Wrapper de Pedido de TPV que permite calcular descuentos dentro del mismo
 * mediante la clase {@link DiscountCalculator}.
 */
public class DiscountableOrderWrapper extends DiscountableDocument {

	/** Pedido de TPV */
	private Order order;
	
	/**
	 * Constructor del Wrapper de Pedido de TPV.
	 * @param order Pedido de TPV Wrappeado.
	 */
	public DiscountableOrderWrapper(Order order) {
		super();
		this.order = order;
	}

	@Override
	public BigDecimal getLinesTotalAmt() {
		return getOrder().getOrderProductsTotalAmt();
	}
	
	@Override
	protected List<? extends Object> getOriginalLines() {
		return getOrder().getOrderProducts();
	}

	@Override
	protected IDocumentLine createDocumentLine(Object originalLine) {
		return new DiscountableOrderProductWrapper((OrderProduct)originalLine, this);
	}
	
	@Override
	public Date getDate() {
		return getOrder().getDate();
	}

	@Override
	public void setTotalDocumentDiscount(BigDecimal discountAmount) {
		getOrder().setTotalDocumentDiscount(discountAmount);		
	}
	
	@Override
	public boolean isCalculateNetDiscount() {
		return false;
	}
	
	@Override
	public void setDocumentReferences(MDocumentDiscount documentDiscount) {
		documentDiscount.setC_Order_ID(getOrder().getGeneratedOrderID());
		documentDiscount.setC_Invoice_ID(getOrder().getGeneratedInvoiceID());
	}
	
	@Override
	public void setDocumentDiscountChargeID(int chargeID) {
		// No se utiliza el cargo		
	}
	
	@Override
	public void setTotalBPartnerDiscount(BigDecimal discountAmount) {
		getOrder().setTotalBPartnerDiscount(discountAmount);		
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	@Override
	public void setTotalManualGeneralDiscount(BigDecimal discountAmount) {
		getOrder().setTotalManualGeneralDiscount(discountAmount);		
	}
}
