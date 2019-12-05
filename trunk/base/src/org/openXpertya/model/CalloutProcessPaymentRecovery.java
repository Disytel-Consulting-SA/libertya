package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class CalloutProcessPaymentRecovery extends CalloutProcessEngine {

	public String invoice( Properties ctx, int WindowNo, MField mField, Object value, Object oldValue) {
		if(value == null) {
			return "";
		}
		Integer invoiceID = (Integer)value;
		Integer posJournalID = 0;
		Integer posJournalAssigned = (Integer)fields.get("C_POSJournal_ID").getValue();
		if(!Util.isEmpty(posJournalAssigned)) {
			return "";
		}
		if(!Util.isEmpty(invoiceID, true)){
			posJournalID = DB.getSQLValue(null, "SELECT c_posjournal_id FROM c_invoice WHERE c_invoice_id = ?",
					invoiceID);
		}
		fields.get("C_POSJournal_ID").setValue(posJournalID, false);
		return "";
	}
}
