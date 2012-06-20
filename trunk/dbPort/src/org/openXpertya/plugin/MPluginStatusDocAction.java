package org.openXpertya.plugin;


public class MPluginStatusDocAction extends MPluginStatus {

	/**
	 * Arquitectura de Plugins
	 * """"""""""""""""""""""" 
	 * Estado a retornar para métodos de procesamiento de lógica de documentos
	 */
	
	
	/** m_process msg resultante de la ejecucion */
	private String processMsg;
	
	/** estado a retornar luego de la ejecucion*/ 
	private String docStatus; 
    
	
	public MPluginStatusDocAction()
	{
		processMsg = "";
		docStatus = "";
	}


	public String getProcessMsg() {
		return processMsg;
	}


	public void setProcessMsg(String processMsg) {
		this.processMsg = processMsg;
	}


	public String getDocStatus() {
		return docStatus;
	}


	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	
	

}
