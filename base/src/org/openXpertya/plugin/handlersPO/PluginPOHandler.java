package org.openXpertya.plugin.handlersPO;

import java.util.Vector;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.MPluginStatus;
import org.openXpertya.plugin.MPluginStatusPO;
import org.openXpertya.util.CLogger;

public abstract class PluginPOHandler extends PluginHandler {

	/** Nombre del handler */
	protected String handlerName = "";
	
	/** Accion previa de los plugins sobre clase M */
	protected abstract MPluginStatusPO processPreAction(PO po, MPluginPO plugin, boolean newRecord, boolean success);
	
	/** Accion real de clase M */
	protected abstract boolean processActualAction(PO po, boolean newRecord, boolean success);
	
	/** Accion posterior de los plugins sobre clase M */
	protected abstract MPluginStatusPO processPostAction(PO po, MPluginPO plugin, boolean newRecord, boolean success);
	
	/**
	 * Entrada principal a la gestión de persistencia mediante clases M + Plugins
	 * @param po - PO a almacenar
	 * @param newRecord - Nuevo registro?
	 * @param pluginList - Lista de plugins activos en el sistema
	 * @param log - CLogger
	 * @param handler - Manejador de persistencia por plugin
	 * @return verdadero o falso según la correcta ejecución de las validaciones
	 */
	public boolean processPO(PO po, boolean newRecord, boolean success, Vector<MPluginPO> pluginList, CLogger log)
	{
		/**
		 * ===================== preAction() =====================
	     */
		int nextStatus = MPluginStatus.STATE_TRUE_AND_CONTINUE;
		MPluginStatusPO pluginStatusPO = null;
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin
			PO copy = getLPluginPO(po, pluginList.get(i));
			
			// invocar preAction y ver estado
			pluginStatusPO = this.processPreAction((copy!=null?copy:po), pluginList.get(i), newRecord, success);
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)
				PO.deepCopyValues(copy, po);
			
			if (pluginStatusPO != null)
			{
				// El primer plugin en ejecucion define el nextStatus
				if (i==0)
					nextStatus = pluginStatusPO.getContinueStatus();
				
				// determinar proximo paso a seguir
				if (pluginStatusPO.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					log.saveError(getErrorTitle(), pluginStatusPO.getErrorMessage());
					log.fine( getHandlerName() + " - false - " + pluginList.get(i).getPackageName());
					return false;
				}
			}
		}
		
		/**
		 * ===================== Action() =====================
	     */		
		if (nextStatus == MPluginStatus.STATE_TRUE_AND_CONTINUE && !processActualAction(po, newRecord, success))
				return false;

		/**
		 * ===================== postAction() =====================
	     */		
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin			
			PO copy = getLPluginPO(po, pluginList.get(i));
			
			// -- postAction()  
			pluginStatusPO = this.processPostAction((copy!=null?copy:po), pluginList.get(i), newRecord, success);
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)			
				PO.deepCopyValues(copy, po);
			
			if (pluginStatusPO != null)
			{
				// determinar proximo paso a seguir
				if (pluginStatusPO.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					log.saveError(getErrorTitle(), pluginStatusPO.getErrorMessage());				
					log.fine( getHandlerName() + " - false - " + pluginList.get(i).getPackageName());
					return false;
				}
			}
		}	

		return true;
	}

	
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	/**
	 * @return Devuelve el AD_Message que se muestra en el título del diálogo
	 * al producirse un error en la validación que realiza el handler.
	 */
	protected String getErrorTitle() {
		return "Error";
	}
	
}
