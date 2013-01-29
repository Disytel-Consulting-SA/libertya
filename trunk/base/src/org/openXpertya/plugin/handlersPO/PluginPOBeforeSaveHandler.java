package org.openXpertya.plugin.handlersPO;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class PluginPOBeforeSaveHandler extends	PluginPOHandler {
	
	public PluginPOBeforeSaveHandler(){
		handlerName = "BeforeSave";
	}

	
	@Override
	protected MPluginStatusPO processPreAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.preBeforeSave(po, newRecord);
	}


	@Override
	protected boolean processActualAction(PO po, boolean newRecord, boolean success) {
		return po.doBeforeSave(newRecord);
	}	
	

	@Override
	protected MPluginStatusPO processPostAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.postBeforeSave(po, newRecord);
	}

	@Override
	protected String getErrorTitle() {
		return "SaveError";
	}
}
