package org.openXpertya.plugin.common;

import java.util.logging.Level;



public class PluginProcessUtils extends PluginUtils {

	/**
	 * Verifica si alguno de los plugins activos contiene una clase que redefina el proceso a ejecutar
	 * Toma el primero, con lo cual respeta el orden de prioridad especificado
	 * @param processClassName
	 * @return
	 */
	public static String findPluginProcessClass(String processClassName) {
		return findPluginProcessClass(processClassName, PluginConstants.PACKAGE_NAME_PROCESS);
	}
	
	/**
	 * Verifica si alguno de los plugins activos contiene una clase que redefina 
	 * Toma el primero, con lo cual respeta el orden de prioridad especificado
	 * @param className
	 * @return
	 */
	public static String findPluginDynamicJasperReportClass(String className) {
		return findPluginProcessClass(className, PluginConstants.PACKAGE_NAME_DYNAMIC_JASPER_REPORT);
	}
	
	/**
	 * Verifica si alguno de los plugins activos contiene una clase que redefina
	 * el proceso a ejecutar Toma el primero, con lo cual respeta el orden de
	 * prioridad especificado. El nombre del paquete standard que recibe como
	 * parámetro es el nombre del paquete en el framework donde se encuentran
	 * estas clases
	 * 
	 * @param processClassName
	 *            nombre de la clase
	 * @param packageStd
	 *            paquete std donde se deberían encontrar las clases, por
	 *            ejemplo para procesos el paquete standard es process
	 * @return la clase del plugin que redefine la del CORE
	 */
	public static String findPluginProcessClass(String processClassName, String packageStd)
	{
		/* Buscar si algun plugin redefine la clase */
		for (String aPackage : PluginPOUtils.getActivePluginPackages())
		{
			try 
			{
				// Obtener el nombre de la clase que realiza el override del proceso 
				String aProcessClass = aPackage + "." + packageStd + "." + Class.forName(processClassName).getSimpleName();

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
