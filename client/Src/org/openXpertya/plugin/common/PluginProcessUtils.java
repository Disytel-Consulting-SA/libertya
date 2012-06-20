package org.openXpertya.plugin.common;

import java.util.logging.Level;



public class PluginProcessUtils extends PluginUtils {

	/**
	 * Verifica si alguno de los plugins activos contiene una clase que redefina el proceso a ejecutar
	 * Toma el primero, con lo cual respeta el orden de prioridad especificado
	 * @param className
	 * @return
	 */
	public static String findPluginProcessClass(String processClassName)
	{
		/* Buscar si algun plugin redefine la clase */
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Obtener el nombre de la clase que realiza el override del proceso 
				String aProcessClass = aPackage + "." + PluginConstants.PACKAGE_NAME_PROCESS + "." + Class.forName(processClassName).getSimpleName();

				// Si se encontró 
				if (checkClass(aProcessClass))
					return aProcessClass;
			}
			catch (Exception e)		{
				log.log( Level.FINE," Imposible determinar pluginClassName para el proceso - processClassName: " + processClassName) ;
			}
		}	
		return null;
	}
	
	/**
	 * Verifica si la clase realmente existe
	 * @param className
	 * @return
	 */
	public static boolean checkClass(String className)
	{
		try {
			Class<?> clazz = Class.forName( className );
			return true;
		}
		catch (Exception e)	{
			return false;
		}
	}
}
