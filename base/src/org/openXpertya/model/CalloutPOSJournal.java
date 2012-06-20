package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

public class CalloutPOSJournal extends CalloutEngine {

	public String cashStmtAmount(Properties ctx, int WindowNo, MTab mTab,
			MField mField, Object value) {
		
		String cashValue = (String)mTab.getValue("CashValue");
		Integer qty = (Integer)mTab.getValue("Qty"); 
		BigDecimal amount = BigDecimal.ZERO;
		
		if (cashValue != null && !cashValue.isEmpty() && qty != null && qty > 0) {
			amount = MPOSCashStatement.getCashAmount(cashValue, qty);
		}
		
		mTab.setValue("Amount", amount);
		
		return "";
	}
}
