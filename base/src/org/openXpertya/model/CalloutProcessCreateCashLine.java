package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Util;

public class CalloutProcessCreateCashLine extends CalloutProcessEngine {

	public String posJournal( Properties ctx, int WindowNo, MField mField, Object value, Object oldValue) {
		Integer posJournalID = (Integer)value;
		Integer cashID = null;
		if(!Util.isEmpty(posJournalID, true)){
			// Busco la caja de la caja diaria
			cashID = MPOSJournal.getCashID(ctx, posJournalID, null);
		}
		fields.get("C_Cash_ID").setValue(cashID, false);
		return "";
	}
	
}
