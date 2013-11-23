package org.openXpertya.apps.form;

import org.openXpertya.OpenXpertya;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.Env;
import org.openXpertya.utils.JarHelper;

/**
 * Instalador de componentes desde terminal.  
 * 		Extiende de VPluginInstaller (subclase de CPanel) 
 * 		solo por conveniencia en reutilización de métodos ya implementados
 * 
 * Recibe los siguientes parámetros:
 * 
 * 		1. nombre del jar a instalar. obligatorio
 * 		2. commit con errores en install.xml (Y/N).  
 * 		3. commit con errores en postinstall.xml (Y/N). 
 * 
 * Ejemplo: java PluginInstaller org.libertya.core.upgrade.jar
 * 
 * IMPORTANTE: 	Si el proceso post-install es uno que en sus metadatos tiene configurado
 * """""""""""	parámetros adicionales a especificar por el usuario durante la instalacion, 
 * 				este instalador NO será de utilidad dado que el mismo tiene por finalidad 
 * 				una instalación desatendida. En ese caso no quedará otra alternativa más  
 * 				que utilizar el instalador de plugins tradicional, desde el cliente Swing.
 */

public class PluginInstaller extends VPluginInstaller {

	protected static boolean commitWithInstallErrors = false;
	protected static boolean commitWithPostinstallErrors = false;
	
	public static void main(String[] args) {
		
		// Sin jar no hay upgrade!
		if (args.length < 3) {
			System.err.println("Debe especificar 3 parámetros: ");
			System.err.println("\t	1) URL del archivo .jar ");
			System.err.println("\t	2) Commit con errores en install ");
			System.err.println("\t	3) Commit con errores en postinstall ");
			System.err.println("Ejemplo: java -classpath lib/OXP.jar:lib/OXPXLib.jar org.openXpertya.apps.form.PluginInstaller /tmp/ejemplo.jar N Y ");
			System.exit(1);
		}
			
	  	// OXP_HOME seteada?
	  	String oxpHomeDir = System.getenv("OXP_HOME"); 
	  	if (oxpHomeDir == null) { 
	  		System.err.println("ERROR: La variable de entorno OXP_HOME no está seteada ");
	  		System.exit(1);
	  	}
	  	
	  	// Cargar el entorno basico
	  	System.setProperty("OXP_HOME", oxpHomeDir);
	  	if (!OpenXpertya.startupEnvironment( false )) {
	  		System.err.println("ERROR: Error al iniciar la configuracion... Postgres esta levantado?");
	  		System.exit(1);
	  	}
	  	
	  	// Configuracion general en contexto
	  	Env.setContext(Env.getCtx(), "#AD_Client_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Org_ID", 0);
	  	Env.setContext(Env.getCtx(), "#AD_Language", "es_AR");
	  	m_ctx = Env.getCtx();
	  	
		PluginInstaller installer = new PluginInstaller();
		commitWithInstallErrors = "Y".equalsIgnoreCase(args[1]);
		commitWithPostinstallErrors = "Y".equalsIgnoreCase(args[2]);
		installer.showPluginDetails(args[0]);
		installer.construct(args[0]);
	}
	
	/**
	 * Lee el archivo de upgrade
	 */
	protected void showPluginDetails(String jarURL) {
		try {
			m_component_props = JarHelper.readPropertiesFromJar(jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.PLUGIN_MANIFEST);
			String pluginStatus = VPluginInstallerUtils.validatePlugin(m_component_props, m_ctx);
			if (pluginStatus.length() > 0) {
				System.err.println("ERROR: " + pluginStatus);
				System.exit(1);
			}
		}
		catch(Exception e) {
			handleException("Error al leer el archivo. ", new Exception(e.toString()));
		}
	}
	
	/**
	 * Efectua los pasos de actualización
	 */
	public String construct(String jarURL) {
		
		try {
			/* Delegar lógica de instalación (pre, install, post) centralizada */
			ProcessInfo pi = VPluginInstallerUtils.performInstall(jarURL, m_ctx, m_component_props, null);
			performInstallFinalize(pi);	// fuerza la invocación en todos los casos
		} catch (Exception e) {
			handleException("Error en ejecucion.", new Exception(e.toString()));
		}
		return null;
	}

	
	/**
	 * Finalización del proceso de instalación de un plugin (con existencia de postInstall)
	 * Debe manejar las excepciones correspondientes
	 */
	public void performInstallFinalize(ProcessInfo pi)
	{
		try
		{
			VPluginInstallerUtils.performInstallFinalize(pi, this, m_component_props);
		}
		catch (Exception e)
		{
			handleException("Error al realizar la post-instalación: ", e);	
		}
	}

	
	/**
	 * En caso de error al realizar la instalación delegar al instalador y finalizar 
	 */
	protected void handleException(String msg, Exception e)
	{
		VPluginInstallerUtils.handleException(msg, e, m_component_props);
		System.exit(1);
	}

	
	/**
	 * Retorna true si no hay errores en instalacion, o bien si los mismos son admitidos (independientemente)
	 */
	public boolean confirmCommit(boolean errorsOnInstall, boolean errorsOnPostInstall) {
		return (!errorsOnInstall || commitWithInstallErrors) && (!errorsOnPostInstall || commitWithPostinstallErrors);
	}

	
	
}

