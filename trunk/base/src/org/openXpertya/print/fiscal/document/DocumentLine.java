package org.openXpertya.print.fiscal.document;

import java.io.Serializable;
import java.math.BigDecimal;

import org.openXpertya.print.fiscal.exception.DocumentException;
import org.openXpertya.print.fiscal.msg.MsgRepository;

public class DocumentLine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Descripción de la línea. */
	private String description;
	/** Cantidad del ítem. */
	private BigDecimal quantity;
	/** Precio unitario del ítem. */
	private BigDecimal unitPrice;
	/** Porcentaje de aplicación del IVA. */
	private BigDecimal ivaRate;
	/** Precio incluye IVA o no */
	private boolean priceIncludeIva = true;
	/** Línea de descuento asociada a esta línea de documento */
	private DiscountLine discount;
	/** Número de línea */
	private int lineNumber;

	public DocumentLine() {
	
	}
	
	public DocumentLine(int lineNumber, String description, BigDecimal quantity, BigDecimal unitPrice, BigDecimal ivaPercent, boolean priceIncludeIva) {
		super();
		this.description = description;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.ivaRate = ivaPercent;
		this.priceIncludeIva = priceIncludeIva;
		this.lineNumber = lineNumber;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the ivaRate.
	 */
	public BigDecimal getIvaRate() {
		return ivaRate;
	}

	/**
	 * @param ivaRate
	 *            The ivaRate to set.
	 */
	public void setIvaRate(BigDecimal ivaRate) {
		this.ivaRate = ivaRate;
	}

	/**
	 * @return Returns the priceIncludeIva.
	 */
	public boolean isPriceIncludeIva() {
		return priceIncludeIva;
	}

	/**
	 * @param priceIncludeIva
	 *            The priceIncludeIva to set.
	 */
	public void setPriceIncludeIva(boolean priceIncludeIva) {
		this.priceIncludeIva = priceIncludeIva;
	}

	/**
	 * @return Returns the quantity.
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            The quantity to set.
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return Returns the substract.
	 */
	public boolean isSubstract() {
		return getSubtotal().compareTo(BigDecimal.ZERO) < 0;
	}

	/**
	 * @return Returns the unitPrice.
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @return El precio unitario en valor absoluto.
	 */
	public BigDecimal getAbsUnitPrice() {
		return getUnitPrice().abs();
	}
	
	/**
	 * @param unitPrice
	 *            The unitPrice to set.
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return Returns the discount.
	 */
	public DiscountLine getDiscount() {
		return discount;
	}

	/**
	 * @param discount The discount to set.
	 */
	public void setDiscount(DiscountLine discount) {
		this.discount = discount;
	}
	
	/**
	 * Asigna un descuento o recargo a la línea del documeto.
	 * @param description Descripción del descuento / recargo.
	 * @param amount Monto del descuento / recargo.
	 * @param amountIncludeIva Indica si el descuento contiene el IVA.
	 */
	public void setDiscount(String description, BigDecimal amount, Boolean amountIncludeIva) {
		setDiscount(new DiscountLine(description, amount, amountIncludeIva));
	}
	
	/**
	 * Aplica un descuento a partir de un porcentaje.
	 * @param description Descripción del descuento.
	 * @param percent Porcentaje del precio total de la línea.
	 */
	public void setDiscount(String description, BigDecimal percent) {
		BigDecimal amount = getSubtotal().abs().multiply(percent).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
		setDiscount(description, amount.negate(), isPriceIncludeIva());
	}
	
	/**
	 * @return Retorna el subtotal de la línea. No contabiliza el deescuento / recargo.
	 */
	public BigDecimal getSubtotal() {
		BigDecimal ivaAmt = BigDecimal.ZERO;
		BigDecimal subtotal = getUnitPrice().multiply(getQuantity()); 
		if(!isPriceIncludeIva()) {
			ivaAmt = subtotal.multiply(getIvaRate()).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);
		}
		return subtotal.add(ivaAmt);
	}
	
	/**
	 * @return Retorna el monto total de la línea contabilizando el descuento / recargo. 
	 */
	public BigDecimal getLineTotal() {
		BigDecimal lineTotal = getSubtotal();
		if(hasDiscount())
			lineTotal = lineTotal.add(getDiscount().getAmount());
		return lineTotal;
	}
	
	/**
	 * @return Indica si la línea del documento tiene un descuento / recargo
	 * asociado.
	 */
	public boolean hasDiscount() {
		return getDiscount() != null;
	}
	
	/**
	 * Validación de la línea de documento.
	 * @throws DocumentException cuando la línea del documento contiene
	 * errores que producirán estados de error en la impresora fiscal.
	 */
	public void validate() throws DocumentException {
		// Validar cantidad mayor que 0.
		Document.validateNumber(getQuantity(), ">", BigDecimal.ZERO,
				createErrorMsg("LineQuantityInvalid"));
		
		// Validar precio unitario distinto que 0.
		Document.validateNumber(getUnitPrice(), "!=", BigDecimal.ZERO,
				createErrorMsg("LineUnitPriceInvalid"));

		// Validar descripción.
		Document.validateText(getDescription(), createErrorMsg("LineDescriptionInvalid"));
		
		// Si tiene descuento se realiza la validación del mismo.
		if(hasDiscount())
			getDiscount().validate();
	}

	/**
	 * @return Returns the lineNumber.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @param lineNumber The lineNumber to set.
	 */
	public void setLineNumber(int number) {
		this.lineNumber = number;
	}
	
	/**
	 * Crea un mensaje de error informativo incluyendo el número de línea.
	 * @param errorMsg Mensaje de error original.
	 * @return Mensaje nuevo ya parseado por el repositori de mensajes.
	 */
	protected String createErrorMsg(String errorMsg) {
		return MsgRepository.get(errorMsg) + 
			   " (" + MsgRepository.get("Line") + " " + getLineNumber() + ")";
	}
}
