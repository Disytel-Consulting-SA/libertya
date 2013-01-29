package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionCloseItHandler extends PluginDocActionHandler {

	public PluginDocActionCloseItHandler(){
		handlerName = "CloseIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preCloseIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.closeIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postCloseIt(document);
	}

}
