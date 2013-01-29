package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionCompleteItHandler extends PluginDocActionHandler {

	public PluginDocActionCompleteItHandler(){
		handlerName = "CompleteIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preCompleteIt(document);
	}
	
	
	@Override
	protected String processActualAction(DocAction document) {
		return document.completeIt();
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postCompleteIt(document);
	}


}
