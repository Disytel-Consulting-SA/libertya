package org.openXpertya.saas;

import java.io.File;

public class Constants {

	// ================ De instancia ===================
	
	/** Subdirectorio en donde se alojan los archivos de instancia SaaS dentro del ServidorOXP */
	public static String SUBDIR_SAAS_INSTANCE = File.separator + "utils" + File.separator + "saas";
	
	/** Subdirectorio en donde se alojan los archivos de configuración de instancia SaaS dentro del ServidorOXP */
	public static String SUBDIR_SAAS_INSTANCE_CFG = File.separator + "cfg";
	
	/** Nombre del archivo de configuracion de instancia */
	public static String FILENAME_INSTANCE_CFG = "InstanceConfiguration.cfg";

	// ================ General ===================

	/** Subdirectorio en donde se alojan los archivos de ejecucion generales SaaS  */
	public static String SUBDIR_SAAS_GENERAL_CFG = File.separator + "cfg";
	
	/** Subdirectorio en donde se alojan los archivos de ejecucion generales SaaS  */
	public static String SUBDIR_SAAS_GENERAL_BIN = File.separator + "bin";
	
	/** Script para configurar puertos automaticamente */
	public static String EXEC_INSTANCE_PORTS = "setInstancePorts.sh";
	
	// ================ Recursos ===================
	
	/** Ubicacion completa (relativa) del archivo de configuracion de instancia */
	public static String RESOURCE_INSTANCE_CFG = SUBDIR_SAAS_INSTANCE + File.separator + SUBDIR_SAAS_INSTANCE_CFG + File.separator + FILENAME_INSTANCE_CFG;
	
	// ================ Claves de Propiedades ===================
	
	/** Propiedad instanceID, la cual especifica el numero identificador de la instancia */
	public static String PROPERTY_KEY_INSTANCEID = "instanceID";
	
	/** Propiedad que especifica la ubicación del archivo de configuración general LYSaaS en el equipo */
	public static String PROPERTY_KEY_SAAS_GENERAL_DIR = "lysaasGeneralDir";

	// ================ Valores de Propiedades ===================
	
	/** Valor por el cual la instancia no es una configurada para SaaS */
	public static int    PROPERTY_VAL_INSTANCEID_NO_SAAS = 0;
}
