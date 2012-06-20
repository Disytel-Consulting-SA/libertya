package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

public class CalloutPaymentFix extends CalloutEngine {

	public String payment(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		
		String action            = (String)mTab.getValue("Action");
		String docType           = (String)mTab.getValue("DocumentType");
		Integer cashLineID       = (Integer)mTab.getValue("C_CashLine_ID");
		Integer paymentID        = (Integer)mTab.getValue("C_Payment_ID");
		Integer allocationLineID = (Integer)mTab.getValue("C_AllocationLine_ID");
		
		cashLineID = cashLineID == null ? 0 : cashLineID;
		paymentID = paymentID == null ? 0 : paymentID;
		allocationLineID = allocationLineID == null ? 0 : allocationLineID;
		
		// Obtiene el importe que corresponde al pago seleccionado y lo asigna
		// al campo.
		BigDecimal amount = MPaymentFixLine.getPaymentAmount(action, docType,
				cashLineID, paymentID, allocationLineID, null);
		
		if (amount == null) {
			amount = BigDecimal.ZERO;
		}
		
		mTab.setValue("PayAmt", amount);
		
		return "";
	}
}
