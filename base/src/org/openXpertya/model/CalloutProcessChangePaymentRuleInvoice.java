package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;

public class CalloutProcessChangePaymentRuleInvoice extends CalloutProcessEngine {

	public String invoice( Properties ctx, int WindowNo, MField mField, Object value, Object oldValue) {
		String paymentRule = null;
		if(value != null){
			paymentRule = DB.getSQLValueString(null, "SELECT paymentrule FROM c_invoice WHERE c_invoice_id = ?",
					(Integer) value);
		}
		fields.get("CreditPaymentRule").setValue(paymentRule, false);
		return "";
	}
	
}
