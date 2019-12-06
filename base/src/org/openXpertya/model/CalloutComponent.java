package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.util.Util;

public class CalloutComponent extends CalloutEngine {

	public String isMicroComponent( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		// Si es un micro componente y no tiene definido el packagename base, especificarlo
		boolean isMicroComponent = (Boolean)mTab.getValue("IsMicroComponent");
		String packageName = (String)mTab.getValue("PackageName");
		if (isMicroComponent && Util.isEmpty(packageName)) {
			mTab.setValue("PackageName", PluginConstants.PACKAGE_NAME_BASE_MICRO_COMPONENTS);
		}
		return "";
	}

}
