package org.openXpertya.plugin.common;

public class PluginConstants {

	/** ----------------------- PACKAGES PARA REDEFINICION DE LOGICA ----------------------- */
	
	/** Package donde se almacenan los plugins M */
	public static final String PACKAGE_NAME_MODEL = "model";
	
	/** Package donde se almacenan los plugins callouts */
	public static final String PACKAGE_NAME_CALLOUT = "callout";
	
	/** Package donde se almacenan los plugins process */
	public static final String PACKAGE_NAME_PROCESS = "process";
	
	/** Package donde se almacenan los plugins info */
	public static final String PACKAGE_NAME_INFO = "info";

	/** Package donde se almacenan clases relacionadas con el Cliente de la App */
	public static final String PACKAGE_NAME_CLIENT = "client";
	
	/** Package donde se almacena la logica para lookups */
	public static final String PACKAGE_NAME_LOOKUP= "lookup";
	
	/** --------------------------- PREFIJOS ------------------------------------------- */

	/** Prefijo para clases Callout */
	public static final String CLASS_CALLOUT_PREFIX = "Callout";
	
	/** Prefijo para clases Info */
	public static final String CLASS_INFO_PREFIX = "Info";

	/** Prefijo para clases CreateFrom */
	public static final String CLASS_CREATEFROM_PREFIX = "VCreateFrom";

		
	/** ----------------------- DEFINICION DE ARCHIVOS DE PLUGIN ----------------------- */
	
	/** Ubicación de los archivos properties, sql y xml dentro del jar */
	public static final String URL_INSIDE_JAR = "";
	
	/** Nombre del manifest con el detalle del plugin */
	public static final String PLUGIN_MANIFEST = "manifest.properties";  
	
	/** Nombre del archivo de preinstall */
	public static final String FILENAME_PREINSTALL = "preinstall.sql";
	
	/** Nombre del archivo de install */
	public static final String FILENAME_INSTALL = "install.xml";
	
	/** Nombre del archivo de postinstall */
	public static final String FILENAME_POSTINSTALL = "postinstall.xml";
	
	
	
	/** ----------------------- DEFINICION DE CAMPOS DEL MANIFEST ----------------------- */

	/** Autor del plugin */
	public static final String PROP_AUTHOR = "AUTHOR";
	
	/** Prefijo del plugin */
	public static final String PROP_PREFIX = "PREFIX";
	
	/** Version del plugin */
	public static final String PROP_VERSION = "VERSION";
	
	/** Nivel de core del plugin */
	public static final String PROP_CORELEVEL = "CORELEVEL";
	
	/** Nombre publico del plugin */
	public static final String PROP_PUBLICNAME = "PUBLICNAME";
	
	/** Package del plugin */
	public static final String PROP_PACKAGENAME = "PACKAGENAME";
	
	/** Nombre de la clase para el postinstall (la referencia al OUID) */
	public static final String PROP_INSTALLPROCESS = "INSTALLPROCESS";

	/** Referencia a la entrada AD_ComponentObjectUID en tabla AD_Component */
	public static final String PROP_COMPONENTUID = "COMPONENTUID";

	/** Referencia a la entrada AD_ComponentObjectUID en tabla AD_ComponentVersion */
	public static final String PROP_COMPONENTVERSIONUID = "COMPONENTVERSIONUID";
	
	/** Patch */
	public static final String PROP_PATCH = "PATCH";
	
	/** Ultimo changelog exportado */
	public static final String PROP_LAST_CHANGELOG = "LAST_CHANGELOG";
	
	/** Fecha y hora de exportación */
	public static final String PROP_EXPORT_TIMESTAMP = "EXPORT_TIMESTAMP"; 
	
	/** ----------------------- CONSTANTES PARA GENERATE MODEL ----------------------- */
	
	/** Prefijo para clases X_ de plugins Libertya Plugin */
	public static final String LIBERTYA_PLUGIN_PREFIX = "LP_";
	
	/** ----------------------- CONSTANTES PARA CLASS NAMES ----------------------- */
	
	/** Prefijo para clases M que extienden de las LP_ */
	public static final String LIBERTYA_PLUGIN_PREFIX_M = "M";
	
	/** Sufijo para clases M que extienden de las LP_ */
	public static final String LIBERTYA_PLUGIN_SUFFIX_M = "_Ext";
	
	/** ----------------------- CONSTANTES PARA PROCESO POSTINSTALL ----------------------- */
	
	/** Proceso generico para la ejecución del plugin postInstall */
	public static final String POST_INSTALL_PROCESS_NAME  = "PluginPostInstallProcess";
	
	/** Nombre reservado para el parametro con el XML a procesar */
	public static final String XML_CONTENT_PARAMETER_NAME = "_xml_content_";
	
	/** Nombre reservado para el parametro que indica la ubicacion del jar que esta siendo instalado */
	public static final String JAR_FILE_URL = "_jar_file_url_";
	
	/** Si es un proceso de postinstall del core, deberá utilizar este nombre package */
	public static final String PROCESSES_PACKAGE_CONTAINER = "org.openXpertya";
	
	/** Si es un proceso de postinstall del core, deberá utilizar este nombre package */
	public static final String POSTINSTALL_BINARIES_DIR = "binarios";
	
	/** ----------------------- CONSTANTES PARA PROCESO POSTINSTALL ----------------------- */
	
	/** Proceso generico para la ejecución del plugin postInstall */
	public static final String PLUGIN_INSTALATION_TRXNAME = "PluginInstallationTrxName";
}
