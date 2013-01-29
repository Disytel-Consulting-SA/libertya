package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionInvalidateItHandler extends PluginDocActionHandler {

	public PluginDocActionInvalidateItHandler(){
		handlerName = "InvalidateIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preInvalidateIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.invalidateIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postInvalidateIt(document);
	}

}
