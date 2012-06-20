package org.openXpertya.pos.model;


public class CreditPayment extends Payment {

	// Variables de instancia
	
	/** Esquema de vencimientos */
	private PaymentTerm paymentTerm;
	
	public CreditPayment(PaymentTerm paymentTerm) {
		super();
		setPaymentTerm(paymentTerm);
	}

	@Override
	public boolean isCreditPayment() {
		return true;
	}

	// Getters y Setters
	
	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}
}
