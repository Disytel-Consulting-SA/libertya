package org.openXpertya.plugin.handlersPO;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.MPluginPO;
import org.openXpertya.plugin.common.PluginConstants;

public class PluginHandler {

	/**
	 * Obtiene una copia del PO que está siendo procesado (save, delete,
	 * process).
	 * 
	 * @param po
	 *            el po que está siendo procesado
	 * @param plugin
	 *            para el cual crear la instancia
	 * @return Una instancia de LP_ o M..._Ext con los datos copiados, o null
	 *         si no existe ninguna de ellas
	 */
	protected PO getLPluginPO(PO po, MPluginPO plugin) {
		PO copy = null;
		try{
			// Primero busco la M.._Ext
			copy = getPluginPOInstance(po, plugin, plugin.getPackageName() + "."
					+ PluginConstants.PACKAGE_NAME_MODEL + "."
					+ PluginConstants.LIBERTYA_PLUGIN_PREFIX_M 
					+ M_Table.standardTableNameToClassName(po.get_TableName())
					+ PluginConstants.LIBERTYA_PLUGIN_SUFFIX_M);
			// Si no existe, busco la LP_
			if(copy == null){	
				copy = getPluginPOInstance(po, plugin, plugin.getPackageName() + "."
						+ PluginConstants.PACKAGE_NAME_MODEL + "."
						+ PluginConstants.LIBERTYA_PLUGIN_PREFIX + po.get_TableName());
			}
		} catch(Exception e){
			
		}
		return copy;
	}

	/**
	 * Obtiene una copia del PO que está siendo procesado (save, delete,
	 * process).
	 * 
	 * @param po
	 *            el po que está siendo procesado
	 * @param plugin
	 *            para el cual crear la instancia
	 * @param className
	 *            nombre de la clase a obtener
	 * @return Una instancia de la clase parámetro con los datos copiados, o
	 *         null si ésta no existe
	 */
	protected PO getPluginPOInstance(PO po, MPluginPO plugin, String className)
	{
		PO copy = null;
		
		try
		{
			Class<?> clazz = Class.forName(className);
			
			Class<?>[] paramTypes = { Properties.class, int.class, String.class };
			Object[] args = { po.getCtx(), po.getID(), po.get_TrxName() };			
			Constructor<?> cons = clazz.getConstructor(paramTypes);

			copy = (PO)cons.newInstance(args);	

			PO.deepCopyValues(po, copy);
			
			po.copyInstanceValues(copy);
		}
		catch (Exception e)	{ 
			// no existe clase correspondiente
			}

		return copy;

	}
	
	
}
