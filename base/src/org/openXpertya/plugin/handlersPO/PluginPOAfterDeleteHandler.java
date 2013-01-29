package org.openXpertya.plugin.handlersPO;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatusPO;

public class PluginPOAfterDeleteHandler extends PluginPOHandler {

	public PluginPOAfterDeleteHandler(){
		handlerName = "AfterDelete";
	}
	
	
	@Override
	protected MPluginStatusPO processPreAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.preAfterDelete(po, success);
	}	
	
	
	@Override
	protected boolean processActualAction(PO po, boolean newRecord, boolean success) {
		return po.doAfterDelete(success);
	}

	
	@Override
	protected MPluginStatusPO processPostAction(PO po, MPluginPO plugin, boolean newRecord, boolean success) {
		return plugin.postAfterDelete(po, success);
	}

	@Override
	protected String getErrorTitle() {
		return "DeleteError";
	}
}
