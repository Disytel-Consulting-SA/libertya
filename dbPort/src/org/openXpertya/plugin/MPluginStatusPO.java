package org.openXpertya.plugin;

public class MPluginStatusPO extends MPluginStatus {
	

	/**
	 * Arquitectura de Plugins
	 * """"""""""""""""""""""" 
	 * Estado a retornar para métodos de validación en persistencia
	 */
	
	/** Mensaje de error a mostrar */
	private String errorMessage;


	public MPluginStatusPO()
	{
		errorMessage = "";	
	}


	
	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
