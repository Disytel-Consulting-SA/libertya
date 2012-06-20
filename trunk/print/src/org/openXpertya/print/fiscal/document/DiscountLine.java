package org.openXpertya.print.fiscal.document;

import java.io.Serializable;
import java.math.BigDecimal;

import org.openXpertya.print.fiscal.exception.DocumentException;

/**
 * Línea de descuento o recargo. El signo del <code>amount</code> de la línea
 * indica si la línea es un descuento (signo negativo) o si la línea es un recargo
 * (signo positivo). 
 * @author Franco Bonafine
 * @date 12/02/2008
 */
public class DiscountLine implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Descripción de la línea de descuento / recargo */
	private String description;
	/** Monto del descuento */
	private BigDecimal amount;
	/** El monto tiene el IVA incluído o no */ 
	private boolean amountIncludeIva = true;
	/** Tasa de impuesto */
	private BigDecimal taxRate = null;

	/**
	 * @param description
	 * @param amount
	 * @param substract
	 * @param amountIncludeIva
	 */
	public DiscountLine(String description, BigDecimal amount, boolean amountIncludeIva) {
		super();
		this.description = description;
		this.amount = amount;
		this.amountIncludeIva = amountIncludeIva;
	}
	
	/**
	 * @param description
	 * @param amount
	 * @param substract
	 * @param amountIncludeIva
	 * @param taxRate
	 */
	public DiscountLine(String description, BigDecimal amount,
			boolean amountIncludeIva, BigDecimal taxRate) {
		super();
		this.description = description;
		this.amount = amount;
		this.amountIncludeIva = amountIncludeIva;
		this.taxRate = taxRate;
	}

	/**
	 * @return Returns the amount.
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	
	/**
	 * @return Retorna el monto en valor absoluto.
	 */
	public BigDecimal getAbsAmount() {
		return amount.abs();
	}

	/**
	 * @param amount
	 *            The amount to set.
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * @return Returns the amountIncludeIva.
	 */
	public boolean isAmountIncludeIva() {
		return amountIncludeIva;
	}

	/**
	 * @param amountIncludeIva
	 *            The amountIncludeIva to set.
	 */
	public void setAmountIncludeIva(boolean amountIncludeIva) {
		this.amountIncludeIva = amountIncludeIva;
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
	 * Validación de la línea de descuento.
	 * @throws DocumentException cuando la línea del descuento contiene
	 * errores que producirán estados de error en la impresora fiscal.
	 */
	public void validate() throws DocumentException {
		// Validar descripción.
		Document.validateText(getDescription(), "DiscountDescriptionInvalid");
	}

	/**
	 * @return el valor de taxRate
	 */
	public BigDecimal getTaxRate() {
		return taxRate;
	}

	/**
	 * @param taxRate el valor de taxRate a asignar
	 */
	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
}
