package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CalloutOrgPercepcion extends CalloutEngine {

	public String processor( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		 if (isCalloutActive())
	        	return "";
	        
        setCalloutActive( true );
		String soportaPadron = "N";
		if(value != null){
			Integer percepcionProcessorID = (Integer)value;
			soportaPadron = DB
					.getSQLValueString(
							null,
							"SELECT SupportRegister FROM C_RetencionProcessor WHERE C_RetencionProcessor_ID = ?",
							percepcionProcessorID);
			soportaPadron = Util.isEmpty(soportaPadron, true)?"N":soportaPadron;
		}
		Env.setContext(ctx, WindowNo, "SupportRegister", soportaPadron);
		setCalloutActive( false );
		return "";
	}

}
