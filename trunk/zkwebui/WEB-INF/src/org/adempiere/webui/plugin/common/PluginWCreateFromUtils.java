package org.adempiere.webui.plugin.common;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import org.adempiere.webui.apps.form.WCreateFrom;
import org.openXpertya.grid.VCreateFrom;
import org.openXpertya.model.MTab;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginPOUtils;
import org.openXpertya.plugin.common.PluginProcessUtils;
import org.openXpertya.plugin.common.PluginUtils;

public class PluginWCreateFromUtils extends PluginUtils {

	/**
	 * Busca en la lista de plugins activos si alguno define una clase CreateFrom
	 * para una pestaña, obteniendo el nombre de la tabla asociada a la pestaña.
	 * @param mTab Pestaña que invoca el CreateFrom
	 * @return instancia de la clase o <code>null</code> si no se encuentra una definición
	 * de CreateFrom en los plugins activos.
	 */
	public static WCreateFrom getCreateFrom(MTab mTab) {
		
		/* Buscar si algun plugin redefine la clase CreateFrom */ 
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Obtener el nombre de la clase que realiza el override del CreateFrom 
				String createFromClass = aPackage + "." + PluginConstants.PACKAGE_NAME_CLIENT + "." + getCreateFromClassName(mTab.getTableName());
	
				// Si se encontró 
				if (PluginProcessUtils.checkClass(createFromClass)) {
					return createInstance(createFromClass, mTab); 
				}
			}
			catch (Exception e) {
				log.log( Level.WARNING," Error al determinar la clase CreateFrom - tableName:" + mTab.getTableName()) ;
				; // Error al determinar la clase 
			}
		}
		return null;
	}

	/**
	 * Devuelve el nombre de clase CreateFrom para una tabla determinada.
	 * Quita el prefijo del nombre de la tabla y elimina del nombre todos los
	 * guiones bajos (_).<br>
	 * Ejemplo:<br>
	 * <ul>
	 * <li><code>C_Order --> WCreateFromOrder</code></li>
	 * <li><code>M_InOut --> WCreateFromInOut</code></li>
	 * <li><code>C_Repair_Order --> WCreateFromRepairOrder</code></li>
	 * </ul>
	 * @param tableName Nombre de la tabla
	 * @return 
	 */
	public static String getCreateFromClassName(String tableName) {
		int firstUnderScore = tableName.indexOf("_");
		return PluginConstants.CLASS_WCREATEFROM_PREFIX + tableName.substring(firstUnderScore+1).replaceAll("_", "");
	}
	
	/**
	 * Crea la instancia de <code>createFromClass</code>
	 * @param createFromClass Clase a instanciar (debe ser subclase de {@link VCreateFrom})
	 * @param tab Pestaña que se envía como parámetro al constructor de {@link VCreateFrom}
	 * @return instancia de la clase correspondiente
	 */
	private static WCreateFrom createInstance(String createFromClass, MTab tab) {
		try 
		{
			// Se obtiene la instancia de la metaclase 
			Class<?> clazz = Class.forName(createFromClass);
		
			// instanciar y devolver el CreateFrom correspondiente
			Class<?>[] paramTypes = new Class<?>[] {
				MTab.class	
			};
			Object[] args = new Object[] {
				tab
			};
			Constructor<?> cons = clazz.getConstructor(paramTypes);
			return (WCreateFrom)cons.newInstance(args);
		}
		catch (Exception e)
		{
			log.log( Level.WARNING," Imposible determinar pluginClassName para la ventana CreateFrom - Class:" + createFromClass + " e.msg:" + e.getMessage());
			return null;
		}
	}
	

}
