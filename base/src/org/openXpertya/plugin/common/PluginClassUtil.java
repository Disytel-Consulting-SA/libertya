package org.openXpertya.plugin.common;

public final class PluginClassUtil {

	/**
	 * Obtiene una instancia de la clase parámetro buscando entre los packages del
	 * plugin, si no existe obtiene la del CORE. Se utiliza constructor por defecto.
	 * 
	 * @param coreClass clase a instanciar 
	 * @return instancia del objeto, primero buscando entre los plugins y si no
	 *         existe la de CORE
	 */
	public static Object get(Class<?> coreClass) {
		return get(coreClass.getCanonicalName());
	}
	
	/**
	 * Obtiene una instancia de la clase parámetro buscando entre los packages del
	 * plugin, si no existe obtiene la del CORE. Se utiliza constructor por defecto.
	 * 
	 * @param coreClass nombre de clase a instanciar
	 * @return instancia del objeto, primero buscando entre los plugins y si no
	 *         existe la de CORE
	 */
	public static Object get(String coreClass) {
		// Iterar por los plugins
		String pluginPackageClass;
		Class<?> pluginClass;
		for (String aPackage : PluginPOUtils.getActivePluginPackages())	{
			try {
				// La clase parámetro le extirpamos el prefijo de CORE
				pluginPackageClass = coreClass.replace(PluginConstants.PACKAGE_NAME_PREFIXX_CORE, aPackage);
				
				pluginClass = Class.forName(pluginPackageClass);
				if(pluginClass != null) {
					return pluginClass.newInstance();
				}
			} catch(Exception e) {
				
			}
		}
		// Si llegó aca significa que no encontró ninguna clase en plugins activos,
		// instanciamos la de core
		Object instance;
		try { 
			instance = Class.forName(coreClass).newInstance();
		} catch(Exception e) {
			e.printStackTrace();
			instance = null;
		}
		
		return instance;
	}
}
