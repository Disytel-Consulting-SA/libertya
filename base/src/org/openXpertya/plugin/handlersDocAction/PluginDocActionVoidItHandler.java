package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionVoidItHandler extends PluginDocActionHandler {

	public PluginDocActionVoidItHandler(){
		handlerName = "VoidIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preVoidIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.voidIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postVoidIt(document);
	}


}
