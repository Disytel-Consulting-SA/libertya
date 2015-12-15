package org.openXpertya.saas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.openXpertya.util.CLogger;

public class SaaSUtils {

	/** ¿Fue verificado si la instancia es SaaS? */
	protected static boolean checked = false;
	
    /** ¿Es una instancia de tipo SaaS? */
	protected static boolean isSaasInstance = false;
    
    /** Numero de instancia Saas (0 si no corresponde) */
	protected static int instanceID = 0;
    
    /** Ubicacion LY Saas General */
	protected static String saasGeneralDir = null;
    
	
	public static boolean isSaasInstance() {
		if (!checked)
			setSaaSInstance();
		return isSaasInstance;
	}
	
	public static int getInstanceID() {
		if (!checked)
			setSaaSInstance();
		return instanceID;
	}
	
	public static String getSaasGeneralDir() {
		if (!checked)
			setSaaSInstance();
		return saasGeneralDir;
	}
    
    
    
    /** Determinar si la configuracion debe respetar instancia LY SaaS o no */
    protected static void setSaaSInstance() {
        // Configuracion LY SaaS.  En caso de ser instancia perteneciente a LY SaaS, los  
        // puertos deben quedar fijos para permitir su desplazamiento dependiendo del instanceID.
        try {
        	checked = true;
            String	OXPHome	= System.getenv("OXP_HOME");
            if ((OXPHome == null) || (OXPHome.length() == 0)) {
                OXPHome	= System.getProperty("user.dir");
            }
            String	fileName	= OXPHome + Constants.RESOURCE_INSTANCE_CFG;
            FileInputStream is = new FileInputStream(fileName);

            // Recuperar la propiedad y verificar su valor (OJO: puede tener comentarios, omitir todo más allá del valor)
       		Properties prop = new Properties();
       		prop.load(is);
       		// instanceID
       		String instanceIDStr = prop.getProperty(Constants.PROPERTY_KEY_INSTANCEID).trim();
       		if (instanceIDStr == null) {
       			CLogger.get().info("Propiedad " + Constants.PROPERTY_KEY_INSTANCEID + " de Configuracion LYSaaS no encontrada. Se omite logica relacionada.");
       			return;
       		}
       		int length = instanceIDStr.indexOf("#");
       		if (length > 0)
       			instanceIDStr = instanceIDStr.substring(0, length).trim();
       		instanceID = Integer.parseInt(instanceIDStr);
       		// generalSaaSDirectory
       		saasGeneralDir = prop.getProperty(Constants.PROPERTY_KEY_SAAS_GENERAL_DIR).trim();
       		if (saasGeneralDir == null) {
       			CLogger.get().info("Propiedad " + Constants.PROPERTY_KEY_SAAS_GENERAL_DIR + " de Configuracion LYSaaS no encontrada. Se omite logica relacionada.");
       			return;
       		}
       		length = saasGeneralDir.indexOf("#");
       		if (length > 0)
       			saasGeneralDir = saasGeneralDir.substring(0, length).trim();
       		// Validar si efectivamente es una instancia SaaS
       		isSaasInstance = ( instanceID > Constants.PROPERTY_VAL_INSTANCEID_NO_SAAS);
       		if (isSaasInstance) {
       			CLogger.get().info("Configuracion LYSaaS encontrada. Numero de instancia: " + instanceID);
       		}
        } catch (FileNotFoundException e1) {
        	CLogger.get().info(" Configuracion LYSaaS no encontrada. Se omite logica relacionada.");
        } catch (Exception e) {
        	CLogger.get().warning("Error al leer Configuracion LYSaaS. Se omite logica relacionada.  Error: " + e.getMessage());
        }
    }
}
