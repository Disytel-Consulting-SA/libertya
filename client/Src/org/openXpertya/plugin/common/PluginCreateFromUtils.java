package org.openXpertya.plugin.common;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

import org.openXpertya.grid.VCreateFrom;
import org.openXpertya.model.MTab;

public class PluginCreateFromUtils extends PluginUtils {

	/**
	 * Busca en la lista de plugins activos si alguno define una clase CreateFrom
	 * para una pestaña, obteniendo el nombre de la tabla asociada a la pestaña.
	 * @param mTab Pestaña que invoca el CreateFrom
	 * @return instancia de la clase o <code>null</code> si no se encuentra una definición
	 * de CreateFrom en los plugins activos.
	 */
	public static VCreateFrom getCreateFrom(MTab mTab) {
		
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
	 * <li><code>C_Order --> VCreateFromOrder</code></li>
	 * <li><code>M_InOut --> VCreateFromInOut</code></li>
	 * <li><code>C_Repair_Order --> VCreateFromRepairOrder</code></li>
	 * </ul>
	 * @param tableName Nombre de la tabla
	 * @return 
	 */
	public static String getCreateFromClassName(String tableName) {
		int firstUnderScore = tableName.indexOf("_");
		return PluginConstants.CLASS_CREATEFROM_PREFIX + tableName.substring(firstUnderScore+1).replaceAll("_", "");
	}
	
	/**
	 * Crea la instancia de <code>createFromClass</code>
	 * @param createFromClass Clase a instanciar (debe ser subclase de {@link VCreateFrom})
	 * @param tab Pestaña que se envía como parámetro al constructor de {@link VCreateFrom}
	 * @return instancia de la clase correspondiente
	 */
	private static VCreateFrom createInstance(String createFromClass, MTab tab) {
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
			return (VCreateFrom)cons.newInstance(args);
		}
		catch (Exception e)
		{
			log.log( Level.WARNING," Imposible determinar pluginClassName para la ventana CreateFrom - Class:" + createFromClass + " e.msg:" + e.getMessage());
			return null;
		}
	}
	
}
