/*
 * @(#)ConfigVMSun.java   11.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.install;

import org.openXpertya.util.CLogMgt;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.File;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigVMSun extends Config {

    /**
     * Constructor ...
     *
     *
     * @param data
     */
    public ConfigVMSun(ConfigurationData data) {
        super(data);
    }		// ConfigVMSun

    /**
     * Descripción de Método
     *
     */
    public void init() {

        // Java Home, e.g. D:\j2sdk1.4.1\jre
        String	javaHome	= System.getProperty("java.home");

        log.fine(javaHome);

        if (javaHome.endsWith("jre")) {
            javaHome	= javaHome.substring(0, javaHome.length() - 4);
        }

        p_data.setJavaHome(javaHome);
    }		// init

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String test() {

        // Java Home
        File	javaHome	= new File(p_data.getJavaHome());
        boolean	pass		= javaHome.exists();
        String	error		= "Not found: Java Home";
        if (getPanel() != null)
        	signalOK(getPanel().okJavaHome, "ErrorJavaHome", pass, true, error);

        if (!pass) {
            return error;
        }

        // Look for tools.jar to make sure that it is not the JRE
        File	tools	= new File(p_data.getJavaHome() + File.separator + "lib" + File.separator + "tools.jar");

        pass	= tools.exists();
        error	= "Not found: Java SDK = " + tools;
        if (getPanel() != null)
        	signalOK(getPanel().okJavaHome, "ErrorJavaHome", pass, true, error);

        if (!pass) {
            return error;
        }

        //
        if (CLogMgt.isLevelFinest()) {
            CLogMgt.printProperties(System.getProperties(), "System", true);
        }

        //
        log.info("OK: JavaHome=" + javaHome.getAbsolutePath());
        setProperty(ConfigurationData.JAVA_HOME, javaHome.getAbsolutePath());
        System.setProperty(ConfigurationData.JAVA_HOME, javaHome.getAbsolutePath());

        // Java Version
        final String	VERSION		= "1.4.1";
        final String	VERSION2	= "1.4.2";

        // begin vpj-cd e-evolution 01/28/2005 1.5
        final String	VERSION3	= "1.5.0";
        
        
        final String	VERSION4	= "1.6.0";

        // begin vpj-cd e-evolution 01/28/2005 1.5
        pass	= false;

        String	jh	= javaHome.getAbsolutePath();

        if (jh.indexOf(VERSION) != -1) {	// file name has version = assuming OK
            pass	= true;
        }

        if (!pass && (jh.indexOf(VERSION3) != -1)) {	//
            pass	= true;
        }

        if (!pass && (jh.indexOf(VERSION4) != -1)) {	//
            pass	= true;
        }

        // end vpj-cd e-evolution 01/28/2005 1.5
        String	thisJH	= System.getProperty("java.home");

        if (thisJH.indexOf(jh) != -1)		// we are running the version currently
        {

            String	thisJV	= System.getProperty("java.version");

            pass	= thisJV.indexOf(VERSION) != -1;

            if (!pass && (thisJV.indexOf(VERSION2) != -1)) {
                pass	= true;
            }

            // begin vpj-cd e-evolution 01/28/2005 1.5
            if (!pass && (thisJV.indexOf(VERSION3) != -1)) {
                pass	= true;
            }
            
            if (!pass && (thisJV.indexOf(VERSION4) != -1)) {
                pass	= true;
            }            

            // end vpj-cd e-evolution 01/28/2005 1.5
            if (pass) {
                log.info("OK: Version=" + thisJV);
            }
        }

        error	= "Wrong Java Version: Should be " + VERSION3;
        if (getPanel() != null)
        	signalOK(getPanel().okJavaHome, "ErrorJavaHome", pass, true, error);

        if (!pass) {
            return error;
        }

        //
        setProperty(ConfigurationData.JAVA_TYPE, p_data.getJavaType());

        return null;
    }		// test
}	// ConfigVMSun



/*
 * @(#)ConfigVMSun.java   11.jun 2007
 * 
 *  Fin del fichero ConfigVMSun.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
