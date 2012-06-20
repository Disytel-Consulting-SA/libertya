package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionReverseAccrualItHandler extends PluginDocActionHandler {

	public PluginDocActionReverseAccrualItHandler(){
		handlerName = "ReverseAccrualIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preReverseAccrualIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.reverseAccrualIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postReverseAccrualIt(document);
	}

}
