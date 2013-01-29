package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionReverseCorrectItHandler extends PluginDocActionHandler {

	public PluginDocActionReverseCorrectItHandler(){
		handlerName = "ReverseCorrectIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preReverseCorrectIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.reverseCorrectIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postReverseCorrectIt(document);
	}

}
