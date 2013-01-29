package org.openXpertya.plugin;

public class MPluginStatus {
	
	/**
	 * Arquitectura de Plugins
	 * """""""""""""""""""""""
	 * Estado de la ejecución de un proceso de persistencia
	 */
	
	/** Posibles estados */
	public static final int STATE_FALSE = 0;
	public static final int STATE_TRUE_AND_SKIP = 1;
	public static final int STATE_TRUE_AND_CONTINUE = 2;
	
	
	/** Estado a devolver mostrar */
	private int continueStatus;

	
	public MPluginStatus()
	{
		continueStatus = STATE_TRUE_AND_CONTINUE;
	}


	public int getContinueStatus() {
		return continueStatus;
	}


	public void setContinueStatus(int continueStatus) {
		this.continueStatus = continueStatus;
	} 
	
	

}
