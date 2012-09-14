package org.openXpertya.model;

import java.util.Properties;

public class CalloutCheckCuitControl extends CalloutEngine {

	public String org( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		if(value == null)
			return "";
		
		if (isCalloutActive())
        	return "";
        
        setCalloutActive( true );
        
        // Setear el límite de cheque inicial por organización
		mTab.setValue("CheckLimit",
				MCheckCuitControl.getInitialCheckLimit((Integer) value, null));
        
        setCalloutActive( false );
        
        return "";
	}
	
}
