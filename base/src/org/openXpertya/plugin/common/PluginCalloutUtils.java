package org.openXpertya.plugin.common;

import java.lang.reflect.Method;
import java.util.logging.Level;

import org.openXpertya.plugin.common.PluginConstants;

public class PluginCalloutUtils extends PluginUtils {

	
	/** Prefijo previo al metodo (columna) de clase (tabla) */
	public static final String PREFIX_PRE = "pre";
	
	/** Prefijo posterior al metodo (columna) de clase (tabla) */ 
	public static final String PREFIX_POST = "post";
	
	
	/**
	 * Dada una tableName, retorna el ClassName a instanciar para ejecutar el plugin
	 * x ej.  C_InvoiceLine se traduce en CalloutInvoiceLine
	 * @param tableName
	 * @return
	 */
	public static String getCalloutClassName(String tableName)
	{
		int firstUnderScore = tableName.indexOf("_");
		return PluginConstants.CLASS_CALLOUT_PREFIX + tableName.substring(firstUnderScore+1);
	}
	
	
	/**
	 * Inyecta los packages+clase+metodo para la invocacion de callouts desde plugins
	 * @param callout
	 * @param tableName
	 * @param columnName
	 */
	public static String insertPluginClasses(String calloutList, String tableName, String columnName)
	{
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			String aCalloutClass = "";

			// Obtener el nombre de la clase
			aCalloutClass = aPackage + "." + PluginConstants.PACKAGE_NAME_CALLOUT + "." + getCalloutClassName(tableName);
			
			// Si existe, incorporar al inicio del listado de callouts, junto con el nombre del metodo (pre+columnName)
			if (checkMethod(aCalloutClass, PREFIX_PRE, columnName))
				calloutList = aCalloutClass + "." + PREFIX_PRE + columnName + ";" + calloutList;
			
			// De ser asi, incorporar al final del listado de callouts, junto con el nombre del metodo (post+columnName)
			if (checkMethod(aCalloutClass, PREFIX_POST, columnName))
				calloutList = calloutList + ";" + aCalloutClass + "." + PREFIX_POST + columnName;			
			
		}
		return calloutList;			
	}
	
	/**
	 * Determina si existe los metodos pre y post para una columna de una tabla dada, para un plugin dado 
	 * @param aCalloutClass
	 * @param prefix
	 * @param columnName
	 * @return
	 */
	public static boolean checkMethod(String aCalloutClass, String prefix, String columnName)
	{
		try 
		{
			// Existe el metodo con nombre de columnName 
			// Recordar convencion para plugins: columnName = methodName (ademas de pre y post)
			Class<?> clazz = Class.forName( aCalloutClass );
			boolean found = false;
			for (Method method : clazz.getMethods())
				if (method.getName().equalsIgnoreCase(prefix + columnName))
					found = true;
			return found;
		}
		catch (Exception e)		{
			log.log( Level.INFO," Callout no implementado para tabla - calloutClass:" + aCalloutClass + " prefix:" + prefix + " columnName:" + columnName );
			return false; // si no existe, es porque no está implementado el callout para esta tabla
		}
	}
	
}
