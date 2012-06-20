package org.openXpertya.plugin;

public class MPluginStatusCallout extends MPluginStatus {
	
	/** Mensaje de error a mostrar */
	private String errorMessage;
	
	public MPluginStatusCallout()
	{
		setContinueStatus(STATE_TRUE_AND_SKIP);
		setErrorMessage("");
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
