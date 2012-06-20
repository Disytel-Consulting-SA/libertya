package org.openXpertya.apps;

import java.util.EventListener;

public interface UserDataChangeListener extends EventListener {
	
	public void userDataChanged(UserDataChangeEvent event);
}
