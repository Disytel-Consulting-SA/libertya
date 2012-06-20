package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;

/**
 * <p>
 * Esta clase implementa algunos métodos de la interfaz {@link IDocumentLine}
 * necesarios para proporcionar la lógica básica de un documento pasible de
 * aplicación de descuentos mediante un {@link DiscountCalculator}. Salvo algún
 * caso extraordinario, todas las líneas de documentos a los que se le apliquen
 * descuentos especializaran esta clase en vez de implementar directamente
 * {@link IDocumentLine}.
 * </p>
 */
public abstract class DiscountableDocumentLine implements IDocumentLine {
	
	/** Documento al cual pertenece esta línea */
	private IDocument document = null;
	
	/** Cantidad a la cual se le ha aplicado algún Combo o Promoción */
	private BigDecimal discountedQty = BigDecimal.ZERO;

	/**
	 * @param document Documento al cual pertenece esta línea
	 */
	public DiscountableDocumentLine(IDocument document) {
		super();
		this.document = document;
	}

	@Override
	public IDocument getDocument() {
		return document;
	}

	@Override
	public BigDecimal getTotalAmt() {
		return getTaxedAmount(getPrice()).multiply(getQty());
	}

	@Override
	public void setDiscountedQty(BigDecimal discountedQty) {
		if (discountedQty == null) {
			discountedQty = BigDecimal.ZERO;
		} else if (discountedQty.compareTo(getQty()) > 0) {
			discountedQty = getQty();
		}
		this.discountedQty = discountedQty;
	}

	@Override
	public BigDecimal getDiscountedQty() {
		return discountedQty;
	}

	@Override
	public BigDecimal getAvailableQty() {
		return getQty().subtract(getDiscountedQty());
	}

	@Override
	public BigDecimal getTaxedAmount(BigDecimal amount) {
		if (amount == null) {
			return null;
		}
		if (isTaxIncluded()) {
			return amount;
		} else {
			return amount.add(amount.multiply(getTaxRateMultiplier()));
		}
	}
	
	/**
	 * Devuelve el multiplicador de la tasa de impuesto de esta línea de documento
	 * 
	 * @param documentLine
	 *            Línea de documento
	 * @return Valor entre 0 y 1 que representa el multiplicador de la tasa de
	 *         impuesto
	 */
	protected BigDecimal getTaxRateMultiplier() {
		return getTaxRate().divide(new BigDecimal(100), 10,
				BigDecimal.ROUND_HALF_UP);
	}
}
