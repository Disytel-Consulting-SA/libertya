package org.openXpertya.plugin;

import java.util.Properties;

import org.openXpertya.model.PO;


public abstract class MPluginPO {

	
	/**
	 * Arquitectura de Plugins
	 * """""""""""""""""""""""
	 * Todo plugin que comprenda logica de negocios debe extender de esta clase
	 * Será deberán redefinir los métodos que sean necesarios.  
	 * Cada método deberá retornar un MPluginStatusPO, indicando:
	 * 	1) El estado próximo de la ejecución a realizar (continueStatus)
	 *  2) En caso de error, indicar el mismo a fin de informar al usuario 
	 */
	
	/** Nombre del plugin */
	protected String packageName;

	/** Persistent Object base */
	protected PO m_po;
	
	/** Contexto */
	protected Properties m_ctx;
	
	/** Nombre de transaccion */
	protected String m_trx;
	
	/** Estado de la ejecucion */
	protected MPluginStatusPO status_po;
	
	public MPluginPO(PO po, Properties ctx, String trxName, String aPackage)
	{
		m_po = po;
		m_ctx = ctx;
		m_trx = trxName;
		packageName = aPackage;
		status_po = new MPluginStatusPO();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	
	/**
	 * Ejecución previa al beforeSave
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO preBeforeSave(PO po, boolean newRecord) {
		return status_po;
	}
	
	
	/**
	 * Ejecución posterior al beforeSave
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO postBeforeSave(PO po, boolean newRecord) {
		return status_po;
	}
	

	/**
	 * Ejecución previa al afterSave
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO preAfterSave(PO po, boolean newRecord, boolean success) {
		return status_po;
	}
	
	
	/**
	 * Ejecución posterior al afterSave
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO postAfterSave(PO po, boolean newRecord, boolean success) {
		return status_po;
	}


	
	/**
	 * Ejecución previa al beforeDelete
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO preBeforeDelete(PO po) {
		return status_po;
	}
	
	
	/**
	 * Ejecución posterior al beforeDelete
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO postBeforeDelete(PO po) {
		return status_po;
	}
	

	/**
	 * Ejecución previa al afterDelete
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO preAfterDelete(PO po, boolean success) {
		return status_po;
	}
	
	
	/**
	 * Ejecución posterior al afterDelete
	 * @return estado del procesamiento
	 */
	public MPluginStatusPO postAfterDelete(PO po, boolean success) {
		return status_po;
	}
	
}
