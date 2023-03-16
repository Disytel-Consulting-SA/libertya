package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;
import org.openXpertya.model.DiscountableDocument;
import org.openXpertya.model.MDocumentDiscount;
import org.openXpertya.model.MPromotionCode;

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
	public BigDecimal getLinesTotalAmt(boolean documentDiscountApplied) {
		return getOrder().getOrderProductsTotalAmt(false, documentDiscountApplied);
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
		return true;
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

	@Override
	public Integer getOrgID() {
		return getOrder().getOrgID();
	}

	@Override
	public Integer getBPartnerID() {
		return getOrder().getBusinessPartner() != null ? getOrder()
				.getBusinessPartner().getId() : null;
	}

	@Override
	public Integer getDocTypeID() {
		// TODO Por ahora no se maneja esto aquí, se debe crear una clase nueva
		// TipoDeDocumento que esté en el modelo de TPV y actualizando cada vez
		// que se elige una entidad comercial, para una ayuda ver el método
		// getNextDocumentNo de POSOnline.
		// Este método se creó para la parte de percepciones para filtrar los
		// tipos de documento de retenciones, el hecho es que desde TPV no se
		// pueden crear documentos de retenciones, por lo pronto esto no se
		// codifica, con el flag de instancia de TPV por ahora es suficiente
		return null;
	}

	@Override
	public boolean isSOTrx() {
		// TPV siempre es transacción de ventas
		return true;
	}

	@Override
	public boolean isApplyPercepcion() {
		return true;
	}

	@Override
	public void setDocumentReferences(MPromotionCode promotionCode) {
		promotionCode.setC_Invoice_ID(getOrder().getGeneratedInvoiceID());
	}

	@Override
	public BigDecimal getTotalDocumentDiscount() {
		return getOrder().getTotalDocumentDiscount();
	}

	@Override
	public BigDecimal getTaxBaseAmt() {
		return getOrder().getTotalTaxBaseAmt(false, true);
	}

	@Override
	public int getCurrencyID() {
		// TODO Por ahora no se usa por aca, cuando agreguemos posibilidad de crear
		// comprobantes en distintas monedas en TPV entonces hay que modificar este dato
		return 0;
	}

	@Override
	public String getDeliveryViaRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isVoiding() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IDocument getCreditRelatedDocument() {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
	public BigDecimal getLinesTotalAmt(boolean includeOtherTaxesAmt) {
		return getOrder().getOrderProductsTotalAmt(includeOtherTaxesAmt, false);
	}*/
}
