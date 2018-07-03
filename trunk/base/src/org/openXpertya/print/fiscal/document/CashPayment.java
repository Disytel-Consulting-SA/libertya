package org.openXpertya.print.fiscal.document;

import java.math.BigDecimal;

public class CashPayment extends Payment {

	private static final long serialVersionUID = 1L;
	
	public CashPayment() {
		// TODO Auto-generated constructor stub
	}

	public CashPayment(BigDecimal amount, String description) {
		super(amount, description, TenderType.EFECTIVO);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isCash(){
		return true;
	}	
}
