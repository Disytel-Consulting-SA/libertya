package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Util;

public class CalloutExpFormat extends CalloutEngine {

	public String constantValue(Properties ctx,int WindowNo,MTab mTab,MField mField,Object value) {
		Integer longValue = 0;
		if(!Util.isEmpty((String)value, true)) {
			longValue = ((String)value).length();
		}
		mTab.setValue("Length", longValue);
		return "";
	}

}
