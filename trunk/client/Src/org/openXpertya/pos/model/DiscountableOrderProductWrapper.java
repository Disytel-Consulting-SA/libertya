package org.openXpertya.pos.model;

import java.math.BigDecimal;

import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountableDocumentLine;
import org.openXpertya.model.MDocumentDiscount;

/**
 * Wrapper de la línea de Pedido de TPV que permite calcular descuentos dentro
 * de la misma mediante la clase {@link DiscountCalculator}.
 */
public class DiscountableOrderProductWrapper extends DiscountableDocumentLine {

	/** Línea del pedido TPV */
	private OrderProduct orderProduct;
	
	/**
	 * Constructor del Wrapper de la línea de pedido TPV.
	 * @param orderProduct Línea de pedido Wrappeada
	 * @param orderWrapper Wrapper del pedido que contiene esta línea
	 */
	public DiscountableOrderProductWrapper(OrderProduct orderProduct, DiscountableOrderWrapper orderWrapper) {
		super(orderWrapper);
		this.orderProduct = orderProduct;
	}

	@Override
	public int getProductID() {
		return getOrderProduct().getProduct().getId();
	}

	@Override
	public BigDecimal getQty() {
		return getOrderProduct().getCount();
	}

	@Override
	public BigDecimal getTotalAmt() {
		return getOrderProduct().getTotalAmt();
	}
	
	@Override
	public BigDecimal getPriceList() {
		return getOrderProduct().getPriceList();
	}
	
	@Override
	public BigDecimal getPrice() {
		return getOrderProduct().getPrice();
	}

	@Override
	public void setPrice(BigDecimal newPrice) {
		getOrderProduct().setPrice(newPrice);
	}
	
	@Override
	public void setDocumentDiscountAmt(BigDecimal discountAmt) {
		getOrderProduct().setTotalDocumentDiscount(discountAmt);
	}

	@Override
	public BigDecimal getLineDiscountAmt() {
		return getOrderProduct().getLineDiscountAmt();
	}

	@Override
	public void setLineDiscountAmt(BigDecimal lineDiscountAmt) {
		getOrderProduct().setLineDiscountAmt(lineDiscountAmt);
	}

	@Override
	public BigDecimal getLineBonusAmt() {
		return getOrderProduct().getLineBonusAmt();
	}

	@Override
	public void setLineBonusAmt(BigDecimal lineBonusAmt) {
		getOrderProduct().setLineBonusAmt(lineBonusAmt);
	}
	
	@Override
	public BigDecimal getTaxRate() {
		return getOrderProduct().getTaxRate();
	}
	
	@Override
	public boolean isTaxIncluded() {
		return getOrderProduct().getProduct().isTaxIncludedInPrice();
	}
	
	public boolean isPerceptionIncluded() {
		return getOrderProduct().getProduct().isPerceptionIncludedInPrice();
	}

	/**
	 * @return the orderProduct
	 */
	public OrderProduct getOrderProduct() {
		return orderProduct;
	}
	
	@Override
	public String toString() {
		return "OrderProductWrapper["
				+ getOrderProduct().getProduct().getDescription() + ", Qty="
				+ getOrderProduct().getCount() + ", QtyAvl=" + getAvailableQty()
				+ "]";
	}

	@Override
	public void setDiscount(BigDecimal discount) {
		getOrderProduct().setDiscount(discount,
				getOrderProduct().getDiscountApplication());
	}

	@Override
	public BigDecimal getDiscount() {
		return getOrderProduct().getDiscount();
	}

	@Override
	public Integer getLineManualDiscountID() {
		return getOrderProduct().getLineManualDiscountID();
	}

	@Override
	public void setLineManualDiscountID(Integer lineManualDiscountID) {
		getOrderProduct().setLineManualDiscountID(lineManualDiscountID);
	}

	@Override
	public BigDecimal getTaxedAmount(BigDecimal amount) {
		if (amount == null) 
			return null;
		
		return getOrderProduct().getTaxedAmount(amount);
	}
	
	@Override
	public BigDecimal getTaxedAmount(BigDecimal amount, boolean includeOthertaxes) {
		if (amount == null)
			return null;
		
		return getOrderProduct().getTaxedAmount(amount)
				.add(getOrderProduct().getOtherTaxedAmount(getOrderProduct().getNetAmount(amount)));
	}

	@Override
	public BigDecimal getDocumentDiscountAmt() {
		return getOrderProduct().getTotalDocumentDiscount();
	}

	@Override
	public BigDecimal getTemporalTotalDocumentDiscountAmt() {
		return getOrderProduct().getTemporalTotalDocumentDiscount();
	}

	@Override
	public void setTemporalTotalDocumentDiscountAmt(
			BigDecimal temporalTotalDocumentDiscount) {
		getOrderProduct().setTemporalTotalDocumentDiscount(temporalTotalDocumentDiscount);
		
	}

	@Override
	public void setDocumentReferences(MDocumentDiscount documentDiscount) {
		documentDiscount.setC_OrderLine_ID(getOrderProduct().getOrderLineID());
		documentDiscount.setC_InvoiceLine_ID(getOrderProduct().getInvoiceLineID());
	}

	@Override
	public Integer getDocumentLineID() {
		// Las líneas de pedido de tpv no poseen ID
		return 0;
	}

	@Override
	public void setGeneratedInvoiceLineID(Integer generatedInvoiceLineID) {
		getOrderProduct().setInvoiceLineID(generatedInvoiceLineID);
	}
}
