package org.openXpertya.pos.model;

import java.math.BigDecimal;

public class CashPayment extends Payment {

	private BigDecimal convertedAmount;
	private String cashType;
	
	public CashPayment() {
		super();
	}

	public CashPayment(BigDecimal convertedAmount) {
		this();
		setConvertedAmount(convertedAmount);
	}

	@Override
	public boolean isCashPayment() {
		return true;
	}

	public BigDecimal getConvertedAmount() {
		return convertedAmount;
	}

	public void setConvertedAmount(BigDecimal convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	
	/**
	 * Suma un importe convertido al importe convertido actual de este pago
	 * @param amount Importe a sumar
	 * @return Devuelve el importe convertido resultante de aplicar la suma.
	 */
	public BigDecimal addConvertedAmount(BigDecimal convertedAmount) {
		setConvertedAmount(getConvertedAmount().add(convertedAmount));
		return getConvertedAmount();
	}

	public void setCashType(String cashType) {
		this.cashType = cashType;
	}

	public String getCashType() {
		return cashType;
	}

}
