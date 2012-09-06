package org.openXpertya.plugin.handlersDocAction;

import java.util.Vector;

import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginDocAction;
import org.openXpertya.plugin.MPluginStatus;
import org.openXpertya.plugin.MPluginStatusDocAction;
import org.openXpertya.plugin.handlersPO.PluginHandler;
import org.openXpertya.process.DocAction;
import org.openXpertya.util.Util;

public abstract class PluginDocActionHandler extends PluginHandler {
	
	/** Valores de retorno para casos en que actualAction no sea prepareIt o completeIt (los cuales devuelven un String) */
	public static final String TRUE = "Y";
	public static final String FALSE = "N";
	
	/** Nombre del handler */
	protected String handlerName = "";
	
	/** Accion previa de los plugins sobre clase M */
	protected abstract MPluginStatusDocAction processPreAction(DocAction document, MPluginDocAction plugin);
	
	/** Accion real de clase M */
	protected abstract String processActualAction(DocAction document);
	
	/** Accion posterior de los plugins sobre clase M */
	protected abstract MPluginStatusDocAction processPostAction(DocAction document, MPluginDocAction plugin);
	
	/**
	 * Entrada principal a la gestión de persistencia mediante clases M + Plugins
	 * @param po - PO a almacenar
	 * @param newRecord - Nuevo registro?
	 * @param pluginList - Lista de plugins activos en el sistema
	 * @param log - CLogger
	 * @param handler - Manejador de persistencia por plugin
	 * @return verdadero o falso según la correcta ejecución de las validaciones
	 */
	public String processAction(DocAction document, Vector<MPluginDocAction> pluginList)
	{
		/**
		 * ===================== preAction() =====================
	     */
		int nextStatus = MPluginStatus.STATE_TRUE_AND_CONTINUE;
		MPluginStatusDocAction pluginStatusDocAction = null;
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin
			PO copy = getLPluginPO((PO)document, pluginList.get(i));
			
			// invocar preAction y ver estado
			pluginStatusDocAction = this.processPreAction((copy!=null?(DocAction)copy:document), pluginList.get(i));
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)
				PO.deepCopyValues(copy, (PO)document);
			
			if (pluginStatusDocAction != null)
			{
				// El primer plugin en ejecucion define el nextStatus
				if (i==0)
					nextStatus = pluginStatusDocAction.getContinueStatus();
				
				// determinar proximo paso a seguir
				if (pluginStatusDocAction.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					((PO)document).setProcessMsg(pluginStatusDocAction.getProcessMsg());
					return pluginStatusDocAction.getDocStatus();
				}
			}
		}
		
		/**
		 * ===================== Action() =====================
	     */	
		String actualActionStatus = (nextStatus == MPluginStatus.STATE_TRUE_AND_SKIP && !Util
				.isEmpty(pluginStatusDocAction.getDocStatus(), true)) ? pluginStatusDocAction
				.getDocStatus() : DocAction.STATUS_Invalid;
		if (nextStatus == MPluginStatus.STATE_TRUE_AND_CONTINUE)
		{
			// guardar el estado a fin de retornar al final del metodo si todo anda ok
			actualActionStatus = processActualAction(document);
			
			// en caso de ser inválido, detener la ejecución
			if (actualActionStatus.equals(DocAction.STATUS_Invalid) || actualActionStatus.equals(FALSE))
				return DocAction.STATUS_Invalid;
		}
			

		/**
		 * ===================== postAction() =====================
	     */		
		for (int i=0; i < pluginList.size(); i++)
		{
			// obtener el LP_ correspondiente según el plugin
			PO copy = getLPluginPO((PO)document, pluginList.get(i));			
			
			// -- postAction()  
			pluginStatusDocAction = this.processPostAction((copy!=null?(DocAction)copy:document), pluginList.get(i));
			
			// reincorporar las modificaciones realizadas en el plugin
			if (copy!=null)
				PO.deepCopyValues(copy, (PO)document);
			
			if (pluginStatusDocAction != null)
			{
				// determinar proximo paso a seguir
				if (pluginStatusDocAction.getContinueStatus() == MPluginStatus.STATE_FALSE)
				{
					((PO)document).setProcessMsg(pluginStatusDocAction.getProcessMsg());
					return pluginStatusDocAction.getDocStatus();
				}
			}
		}	

		return actualActionStatus;
	}

	
	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	
}
