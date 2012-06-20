package org.openXpertya.apps;

import java.io.Serializable;
import java.util.EventObject;

public class UserDataChangeEvent extends EventObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Booleano que determina si cambió los valores del usuario o no */
	private boolean userDataChanged;
	
	public UserDataChangeEvent(Object arg0) {
		super(arg0);
	}
	
	public UserDataChangeEvent(Object arg0, boolean userDataChanged) {
		super(arg0);
		setUserDataChanged(userDataChanged);
	}


	public void setUserDataChanged(boolean userDataChanged) {
		this.userDataChanged = userDataChanged;
	}


	public boolean isUserDataChanged() {
		return userDataChanged;
	}
	
}
