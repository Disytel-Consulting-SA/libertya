package org.openXpertya.model;

import java.util.Properties;

public class CalloutProductChange extends CalloutEngine {

	public String warehouse( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(isCalloutActive())
			return "";
		setCalloutActive(true);
		
		// Colocar las ubicaciones por defecto del depósito
		if(value != null){
			int locatorID = MWarehouse.getDefaultLocatorID((Integer)value, null);
			mTab.setValue("M_Locator_ID", locatorID);
			mTab.setValue("M_Locator_To_ID", locatorID);
		}
		
		setCalloutActive(false);
		return "";
	}
	
}
