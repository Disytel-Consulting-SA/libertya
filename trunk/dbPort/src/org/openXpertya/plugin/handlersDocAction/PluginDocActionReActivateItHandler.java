package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionReActivateItHandler extends PluginDocActionHandler {

	public PluginDocActionReActivateItHandler(){
		handlerName = "ActivateIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preReActivateIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.reActivateIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postReActivateIt(document);
	}

}
