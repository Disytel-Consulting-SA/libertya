package org.openXpertya.plugin.common;

public class DynamicResult extends DynamicArgument {

	/** El resultado fue un error */
	protected boolean error = false;
	/** Mensaje de error */
	protected String errorMsg = "";
	
	public boolean isError() {
		return error;
	}
	
	public void setError(boolean error) {
		this.error = error;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
}
