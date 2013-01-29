package org.openXpertya.process;

import java.util.EventListener;

public interface DocActionStatusListener extends EventListener  {

	/**
	 * Indicates an status change in the process of a DocAction.
	 * @param event DocActionStatus Event.
	 */
	public void docActionStatusChanged(DocActionStatusEvent event);
}
