package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionPrepareItHandler extends PluginDocActionHandler {

	public PluginDocActionPrepareItHandler(){
		handlerName = "PrepareIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.prePrepareIt(document);
	}
	
	
	@Override
	protected String processActualAction(DocAction document) {
		return document.prepareIt();
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postPrepareIt(document);
	}


}
