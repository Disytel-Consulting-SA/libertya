package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionRejectItHandler extends PluginDocActionHandler {

	public PluginDocActionRejectItHandler(){
		handlerName = "RejectIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preRejectIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.rejectIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postRejectIt(document);
	}

}
