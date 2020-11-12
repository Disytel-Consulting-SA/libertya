package org.openXpertya.process;

public interface ElectronicEventListener {

	public static int GENERATING_CAE = 1;
	public static int GENERATING_CAE_OK = 2;
	public static int GENERATING_CAE_ERROR = 3;
	
	public void electronicStatus(int status);
	
	public void errorOcurred(final String errorTitle, final String errorDesc);
}
