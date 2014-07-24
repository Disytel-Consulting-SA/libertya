package org.openXpertya.print.fiscal.document;

import java.io.Serializable;
import java.math.BigDecimal;

import org.openXpertya.print.fiscal.exception.DocumentException;

/**
 * Clase que representa un pago para un documento. El monto del mismo no puede
 * ser negativo. 
 * @author Franco Bonafine
 * @date 11/02/2008
 */
public class Payment implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Monto del pago */
	private BigDecimal amount;
	/** Descripción del medio de pago */
	private String description;
	
	public Payment() {
		super();
	}
	
	/**
	 * @param amount
	 * @param description
	 */
	public Payment(BigDecimal amount, String description) {
		super();
		this.amount = amount;
		this.description = description;
	}
	
	/**
	 * @return Returns the amount.
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	
	/**
	 * @param amount The amount to set.
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Validación del pago.
	 * @throws DocumentException cuando el pago contiene datos no válidos
	 * que podrían generar estados de error al enviarlos a la impresora
	 * fiscal.
	 */
	public void validate() throws DocumentException {
		// Validar monto mayor que 0.
//		Document.validateNumber(getAmount(), ">", BigDecimal.ZERO,
//			"InvalidPaymentAmount");
		
		// Validar descripción.
		Document.validateText(getDescription(),"InvalidPaymentDescription");
	}
	
	/**
	 * @return true si este pago es efectivo, false caso contrario
	 */
	public boolean isCash(){
		return false;
	}
	
	/**
	 * @return true si este pago es un retiro de efectivo, false caso contrario
	 */
	public boolean isCashRetirement(){
		return false;
	}
}
