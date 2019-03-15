package org.openXpertya.acct;

import java.math.BigDecimal;

public class DocTax_Discount extends DocTax {
	
	/** Importe de Descuento */
	private BigDecimal discountAmt = BigDecimal.ZERO;
	
	public DocTax_Discount(int C_Tax_ID, String name, BigDecimal rate, BigDecimal taxBaseAmt, BigDecimal amount) {
		super(C_Tax_ID, name, rate, taxBaseAmt, amount);
		// TODO Auto-generated constructor stub
	}

	public BigDecimal getDiscountAmt() {
		return discountAmt;
	}

	public void setDiscountAmt(BigDecimal discountAmt) {
		this.discountAmt = discountAmt;
	}	
}
