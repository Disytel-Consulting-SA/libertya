package org.openXpertya.pos.model;

import java.math.BigDecimal;

public abstract class Payment {

	private BigDecimal amount;
	private int currencyId;
	private String typeName;
	private PaymentMedium paymentMedium;
	private String tenderType;
	private String description;

	/** Importe real (bruto) del pago */
	private BigDecimal realAmount;
	
	/** Cambio de este pago */
	private BigDecimal changeAmt = BigDecimal.ZERO;	
	
	/**
	 * @return Devuelve amount.
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * @param amount Fija o asigna amount.
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public boolean isCashPayment() {
		return false;
	}
	
	public boolean isCheckPayment() {
		return false;
	}
	
	public boolean isCreditCardPayment() {
		return false;
	}
	
	public boolean isCreditPayment() {
		return false;
	}
	
	public boolean isCreditNotePayment() {
		return false;
	}

	public boolean isBankTransferPayment() {
		return false;
	}
	
	/**
	 * @return Devuelve currencyId.
	 */
	public int getCurrencyId() {
		return currencyId;
	}

	/**
	 * @param currencyId Fija o asigna currencyId.
	 */
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public BigDecimal getConvertedAmount() {
		return getAmount();
	}

	/**
	 * @return the paymentMedium
	 */
	public PaymentMedium getPaymentMedium() {
		return paymentMedium;
	}

	/**
	 * @param paymentMedium the paymentMedium to set
	 */
	public void setPaymentMedium(PaymentMedium paymentMedium) {
		this.paymentMedium = paymentMedium;
	}

	/**
	 * @return Devuelve el esquema de descuento asociado a este pago. Si el
	 *         medio de pago no posee esquema de descuento devuelve
	 *         <code>null</code>
	 */
	public DiscountSchema getDiscountSchema() {
		return getPaymentMedium().getDiscountSchema();
	}

	/**
	 * Indica si el esquema de descuento asociado a este pago es igual a
	 * <code>discountSchema</code>.
	 * 
	 * @param discountSchema
	 *            Esquema de descuento a comparar
	 * @return <code>true</code> si ambos esquemas de descuentos son igual o si
	 *         ambos son <code>null</code>, <code>false</code> caso contrario
	 */
	public boolean equalsDiscountSchema(DiscountSchema discountSchema) {
		return (getDiscountSchema() == null && discountSchema == null)
				|| (getDiscountSchema() != null && discountSchema != null 
						&& getDiscountSchema().equals(discountSchema));
				
	}
	
	/**
	 * Suma un importe al importe actual de este pago
	 * @param amount Importe a sumar
	 * @return Devuelve el importe resultante de aplicar la suma.
	 */
	public BigDecimal addAmount(BigDecimal amount) {
		setAmount(getAmount().add(amount));
		return getAmount();
	}

	/**
	 * @return La instancia de {@link IPaymentMediumInfo} que representa la
	 *         información del medio de pago de este pago.
	 */
	public IPaymentMediumInfo getPaymentMediumInfo() {
		return getPaymentMedium();
	}

	@Override
	public String toString() {
		return "Payment[Amt=" + getAmount() +"]";
	}

	/**
	 * @return El importe real de este pago (sin descuentos/recargos)
	 */
	public BigDecimal getRealAmount() {
		return realAmount;
	}

	/**
	 * @param realAmount El importe real (sin descuentos/recargos) a asignar
	 */
	public void setRealAmount(BigDecimal realAmount) {
		this.realAmount = realAmount;
	}

	public void setTenderType(String tenderType) {
		this.tenderType = tenderType;
	}

	public String getTenderType() {
		return tenderType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setChangeAmt(BigDecimal changeAmt) {
		this.changeAmt = changeAmt;
	}

	public BigDecimal getChangeAmt() {
		return changeAmt;
	}

	
}
