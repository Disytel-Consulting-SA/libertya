package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionUnlockItHandler extends PluginDocActionHandler {

	public PluginDocActionUnlockItHandler(){
		handlerName = "UnlockIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preUnlockIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.unlockIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postUnlockIt(document);
	}

}
