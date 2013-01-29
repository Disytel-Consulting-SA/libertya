package org.openXpertya.print.fiscal.document;

import java.math.BigDecimal;

public class CurrentAccountInfo {

	/** Cliente cuenta corriente */
	private Customer customer;
	
	/** Condición de venta de factura de cuenta corriente */
	private String paymentRule;
	
	/** Monto de la condición de venta de cuenta corriente */
	private BigDecimal amount;
	
	public CurrentAccountInfo(Customer customer, String paymentRule, BigDecimal amount){
		setCustomer(customer);
		setPaymentRule(paymentRule);
		setAmount(amount);
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}

	public String getPaymentRule() {
		return paymentRule;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
}
