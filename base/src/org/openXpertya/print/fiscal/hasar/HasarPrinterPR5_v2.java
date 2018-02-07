package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;

import org.openXpertya.print.fiscal.comm.FiscalComm;

public class HasarPrinterPR5_v2 extends HasarPrinterPR5 {

	public HasarPrinterPR5_v2() {
		// TODO Auto-generated constructor stub
	}

	public HasarPrinterPR5_v2(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}
}
