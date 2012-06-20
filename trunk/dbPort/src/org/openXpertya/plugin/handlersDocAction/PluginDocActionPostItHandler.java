package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionPostItHandler extends PluginDocActionHandler {

	public PluginDocActionPostItHandler(){
		handlerName = "PostIt";
	}

	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.prePostIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.postIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postPostIt(document);
	}

}
