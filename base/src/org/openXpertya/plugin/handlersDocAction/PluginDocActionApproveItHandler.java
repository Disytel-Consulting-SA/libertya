package org.openXpertya.plugin.handlersDocAction;

import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.process.DocAction;

public class PluginDocActionApproveItHandler extends PluginDocActionHandler {

	public PluginDocActionApproveItHandler(){
		handlerName = "ApproveIt";
	}
	
	
	@Override
	protected MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin) {
		return plugin.preApproveIt(document);
	}

	
	@Override
	protected String processActualAction(DocAction document) {
		return document.approveIt() ? TRUE : FALSE; 
	}

	
	@Override
	protected MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin) {
		return plugin.postApproveIt(document);
	}

}
