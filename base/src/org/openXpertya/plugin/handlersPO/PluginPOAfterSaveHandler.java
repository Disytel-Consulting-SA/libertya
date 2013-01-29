package org.openXpertya.plugin.handlersPO;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class PluginPOAfterSaveHandler extends PluginPOHandler {

	public PluginPOAfterSaveHandler(){
		handlerName = "AfterSave";
	}

	
	@Override
	protected MPluginStatusPO processPreAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.preAfterSave(po, newRecord, success);
	}

	
	@Override
	protected boolean processActualAction(PO po, boolean newRecord, boolean success) {
		return po.doAfterSave(newRecord, success);
	}

	
	@Override
	protected MPluginStatusPO processPostAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.postAfterSave(po, newRecord, success);
	}

	@Override
	protected String getErrorTitle() {
		return "SaveError";
	}
}
