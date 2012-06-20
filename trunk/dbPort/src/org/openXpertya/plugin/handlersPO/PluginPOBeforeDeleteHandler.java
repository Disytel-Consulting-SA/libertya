package org.openXpertya.plugin.handlersPO;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class PluginPOBeforeDeleteHandler extends	PluginPOHandler {

	public PluginPOBeforeDeleteHandler(){
		handlerName = "BeforeDelete";
	}
	
	
	@Override
	protected MPluginStatusPO processPreAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.preBeforeDelete(po);
	}	

	
	@Override
	protected boolean processActualAction(PO po, boolean newRecord, boolean success) {
		return po.doBeforeDelete();
	}

	
	@Override
	protected MPluginStatusPO processPostAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.postBeforeDelete(po);
	}

	@Override
	protected String getErrorTitle() {
		return "DeleteError";
	}
}
