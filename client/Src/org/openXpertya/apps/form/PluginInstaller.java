package org.openXpertya.apps.form;

import org.openXpertya.OpenXpertya;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
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
 */

public class PluginInstaller extends VPluginInstaller {

	protected static boolean commitWithInstallErrors = false;
	protected static boolean commitWithPostinstallErrors = false;
	
	/** Numero de errores en instalacion */
	protected int installErrorsLength = 0;
	/** Numero de errores en post-instalacion */
	protected int postInstallErrorsLength = 0;
	
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
			System.exit(1);			
		}
	}
	
	/**
	 * Efectua los pasos de actualización
	 */
	public String construct(String jarURL) {
		
		try {
			/* Iniciar la transacción y setear componente global */
			m_trx = Trx.createTrxName();
			Trx.getTrx(m_trx).start();
			PluginUtils.startInstalation(m_trx);
							
			/* Instalacion por etapas: pre - install - post */
			PluginUtils.appendStatus(" === Instalando Componente y Version. Registrando Plugin === ");
			VPluginInstallerUtils.createComponentAndVersion(m_ctx, m_trx, m_component_props);

			/* Preinstalacion - Sentencias SQL */
			PluginUtils.appendStatus(" === Ejecutando sentencias de preinstalacion === ");
			VPluginInstallerUtils.doPreInstall(m_ctx, m_trx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_PREINSTALL);
			
			/* Comprobar secuencia por modificaciones a nivel SQL */
			PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - preinstalacion === ");
			VPluginInstallerUtils.sequenceCheck(m_ctx, m_trx);
			
			/* Instalacion - Carga de metadatos */
			PluginUtils.appendStatus(" === Insertando metadatos de instalación === ");
			VPluginInstallerUtils.doInstall(m_ctx, m_trx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_INSTALL);
			// Almacenar la longitud del log de errores luego de la instalacion
			if (PluginUtils.getErrorStatus() != null && PluginUtils.getErrorStatus().length()>0)
				installErrorsLength = PluginUtils.getErrorStatus().length();
			
			/* Comprobar secuencia por modificaciones relacionadas en metadatos */
			PluginUtils.appendStatus(" === Comprobando secuencias de nuevos componentes - metadatos === ");
			VPluginInstallerUtils.sequenceCheck(m_ctx, m_trx);

			/* Información adicional del plugin */
			PluginUtils.appendStatus(" === Registrando informacion adicional del componente === ");
			VPluginInstallerUtils.setAditionalValues(m_ctx, m_trx, m_component_props);
			
			/* PostInstalacion - Invocar proceso genérico o ad-hoc */
			PluginUtils.appendStatus(" === Disparando proceso de postinstalación === ");
			ProcessInfo pi = VPluginInstallerUtils.doPostInstall(m_ctx, m_trx, jarURL, PluginConstants.URL_INSIDE_JAR + PluginConstants.FILENAME_POSTINSTALL, m_component_props, null);
			performInstallFinalize(pi);	// fuerza la invocación en todos los casos
			
		} catch (Exception e) {
			handleException("Error en ejecucion.", new Exception(e.toString()));
			System.exit(1);
		}
		return null;
	}

	
	/**
	 * Finalización del proceso de instalación de un plugin (con existencia de postInstall)
	 * Debe manejar las excepciones correspondientes
	 */
	protected void performInstallFinalize(ProcessInfo pi)
	{
		try
		{
			/* Indicar que no hubo post instalacion */
			if (pi == null)
				PluginUtils.appendStatus("Sin post instalacion");
			
			/* Contemplar error al ejecutar el proceso de postInstall */
			if (pi != null && pi.isError())
				throw new Exception(" Excepcion al ejecutar postInstall - " + pi.getSummary());

			// Almacenar la longitud del log de errores luego de la post-instalacion
			if (PluginUtils.getErrorStatus() != null && PluginUtils.getErrorStatus().length()>0)
				postInstallErrorsLength = PluginUtils.getErrorStatus().length();

			// Hubo errores en instalacion o en postinstalacion?
			boolean errorsOnInstall = installErrorsLength > 0;
			boolean errorsOnPostInstall = postInstallErrorsLength - installErrorsLength > 0; 
			boolean errors = errorsOnInstall || errorsOnPostInstall;
			
			/* Si hubo errores, solo commitear si corresponde*/
			if ((errorsOnInstall && !commitWithInstallErrors) || (errorsOnPostInstall && !commitWithPostinstallErrors))
			{
				handleException("Instalación cancelada.", new Exception(" Instalación cancelada debido a errores en: " + (errorsOnInstall?"INSTALL":"") + " " + (errorsOnPostInstall?"POSTINSTALL":"")));
				System.exit(1);
			}
			
			/* Finalizar la transacción y resetear componente global */
			Trx.getTrx(m_trx).commit();
			Trx.getTrx(m_trx).close();
			PluginUtils.stopInstalation();
		
			/* Informar todo OK */
			PluginUtils.appendStatus(" === Instalación finalizada " + (errors?"con errores":"") + " === ");
			writeInstallLog();										//	<-- directamente se genera un archivo donde se almacena el log
		}
		catch (Exception e)
		{
			handleException("Error al realizar la post-instalación: ", e);	
		}
	}

	
	/**
	 * En caso de error al realizar la instalación rollbackear la trx, detener la instalacion e informar 
	 */
	protected void handleException(String msg, Exception e)
	{
		/* Error en algún punto, rollback e informar al usuario */
		Trx.getTrx(m_trx).rollback();
		Trx.getTrx(m_trx).close();
		PluginUtils.stopInstalation();
		PluginUtils.appendStatus(msg + e.getMessage());
		// detailsTextPane.setText(PluginUtils.getInstallStatus()); <-- comentado, el componente visual se colgaba con el volumen de datos a mostrar
		writeInstallLog();										//	<-- directamente se genera un archivo donde se almacena el log
	}

	
}

