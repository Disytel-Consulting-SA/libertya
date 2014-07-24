package org.openXpertya.print.fiscal.document;

import java.math.BigDecimal;

public class CashRetirementPayment extends Payment {

	private static final long serialVersionUID = 1L;

	public CashRetirementPayment() {
		// TODO Auto-generated constructor stub
	}

	public CashRetirementPayment(BigDecimal amount, String description) {
		super(amount, description);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isCashRetirement(){
		return true;
	}
	
}
