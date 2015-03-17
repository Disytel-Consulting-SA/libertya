/*
 * @(#)OpenXpertya.java   12.feb 2007  Versión 2.2
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



package org.openXpertya;

import java.awt.Image;
import java.awt.Toolkit;

import java.io.File;

import java.net.URL;

import javax.swing.ImageIcon;

import org.compiere.plaf.CompierePLAF;
import org.compiere.plaf.CompiereTheme;

import org.openXpertya.db.CConnection;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MSystem;
import org.openXpertya.model.ModelValidationEngine;
import org.openXpertya.util.CLogFile;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Login;
import org.openXpertya.util.Splash;
import org.openXpertya.util.Util;

/**
 *  Clase principal de control de openXpertya 
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: OpenXpertya.java,v 2.2 12-10-2007
 */
public final class OpenXpertya {

    /** Descripción de Campo */
    static public final String	ID	= "$Id: OpenXpertya.java,v 2.2 $";

    /** Descripción de Campo */
    static public final String	MAIN_VERSION	= "Versi\u00f3n 15.03";

    /** Descripción de Campo */
    static public final String	DATE_VERSION	= "20-03-2015";

    /** Descripción de Campo */
    static public final String	DB_VERSION	= "20-03-2015";

    /** Descripción de Campo */
    static public final String	NAME	= "Libertya \u00AE";
    static public final String	NAME2	= "Libertya";
    
    /** Descripción de Campo */
    static public final String	URL	= "www.libertya.org";

    /** Descripción de Campo */
    static private final String	s_File16x16	= "images/OXP16.gif";

    /** Descripción de Campo */
    static private final String	s_file32x32	= "images/OXP32.gif";

    /** Descripción de Campo */
    static private final String	s_file100x30	= "images/OXP10030.png";

    /** Descripción de Campo */
    static private final String	s_file48x15	= "images/OpenXpertya.png";

    /** Descripción de Campo */
    static private String	s_supportEmail	= "";

    /** Descripción de Campo */
    static public final String	SUB_TITLE	= " Software Libre de Gesti\u00f3n";

    /** Descripción de Campo */
    static public final String	OXP_R	= "Libertya\u00AE";

    /** Descripción de Campo */
    static public final String	COPYRIGHT	= "\u00A9 2013 DISYTEL";

    /** Descripción de Campo */
    static private String	s_ImplementationVersion	= null;

    /** Descripción de Campo */
    static private String	s_ImplementationVendor	= null;

    /** Descripción de Campo */
    static private Image	s_image16;

    /** Descripción de Campo */
    static private Image	s_image48x15;

    /** Descripción de Campo */
    static private Image	s_imageLogo;

    /** Descripción de Campo */
    static private ImageIcon	s_imageIcon32;

    /** Descripción de Campo */
    static private ImageIcon	s_imageIconLogo;

    /** Descripción de Campo */
    private static CLogger	log	= null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getName() {
        return NAME;
    }		// getName

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getVersion() {
        return MAIN_VERSION + " - " + DATE_VERSION;
    }		// getVersion

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getSum() {

        StringBuffer	sb	= new StringBuffer();

        sb.append(NAME).append(" ").append(MAIN_VERSION).append(SUB_TITLE);

        return sb.toString();

    }		// getSum

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getSummary() {

        StringBuffer	sb	= new StringBuffer();

        sb.append(NAME).append(" ").append(MAIN_VERSION).append("_").append(DATE_VERSION).append(" -").append(SUB_TITLE).append("- ").append(COPYRIGHT).append("; Implementaci\u00f3n: ").append(getImplementationVersion()).append(" - ").append(getImplementationVendor());

        return sb.toString();

    }		// getSummary

    /**
     * Descripción de Método
     *
     */
    private static void setPackageInfo() {

        if (s_ImplementationVendor != null) {
            return;
        }

        Package	PaqueteOXP	= Package.getPackage("org.openXpertya");

        s_ImplementationVendor	= PaqueteOXP.getImplementationVendor();
        s_ImplementationVersion	= PaqueteOXP.getImplementationVersion();

        if (s_ImplementationVendor == null) {

            s_ImplementationVendor	= "Disytel S.A.";
            s_ImplementationVersion	= "1.0";
        }

    }		// setPackageInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getImplementationVersion() {

        if (s_ImplementationVersion == null) {
            setPackageInfo();
        }

        return s_ImplementationVersion;

    }		// getImplementationVersion

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getImplementationVendor() {

        if (s_ImplementationVendor == null) {
            setPackageInfo();
        }

        return s_ImplementationVendor;

    }		// getImplementationVendor

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static int getCheckSum() {
        return getSum().hashCode();
    }		// getCheckSum

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getSummaryAscii() {

        String	retValue	= getSummary();

        // Registered Trademark
        retValue	= Util.replace(retValue, "\u00AE", "(r)");

        // Trademark
        retValue	= Util.replace(retValue, "\u2122", "(tm)");

        // Copyright
        retValue	= Util.replace(retValue, "\u00A9", "(c)");

        // Cr
        retValue	= Util.replace(retValue, Env.NL, " ");
        retValue	= Util.replace(retValue, "\n", " ");

        return retValue;

    }		// getSummaryAscii

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getJavaInfo() {
        return System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
    }		// getJavaInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getOSInfo() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("sun.os.patch.level");
    }		// getJavaInfo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getURL() {
        return "http://" + URL;
    }		// getURL

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getSubtitle() {
        return SUB_TITLE;
    }		// getSubitle

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Image getImage16() {

        if (s_image16 == null) {

            Toolkit	tk	= Toolkit.getDefaultToolkit();
            URL		url	= org.openXpertya.OpenXpertya.class.getResource(s_File16x16);

            // System.out.println(url);
            if (url == null) {
                return null;
            }

            s_image16	= tk.getImage(url);
        }

        return s_image16;

    }		// getImage16

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Image getImageLogoSmall() {

        if (s_image48x15 == null) {

            Toolkit	tk	= Toolkit.getDefaultToolkit();
            URL		url	= org.openXpertya.OpenXpertya.class.getResource(s_file48x15);

            // System.out.println(url);
            if (url == null) {
                return null;
            }

            s_image48x15	= tk.getImage(url);
        }

        return s_image48x15;

    }		// getImageLogoSmall

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Image getImageLogo() {

        if (s_imageLogo == null) {

            Toolkit	tk	= Toolkit.getDefaultToolkit();
            URL		url	= org.openXpertya.OpenXpertya.class.getResource(s_file100x30);

            // System.out.println(url);
            if (url == null) {
                return null;
            }

            s_imageLogo	= tk.getImage(url);
        }

        return s_imageLogo;

    }		// getImageLogo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static ImageIcon getImageIcon32() {

        if (s_imageIcon32 == null) {

            URL	url	= org.openXpertya.OpenXpertya.class.getResource(s_file32x32);

            // System.out.println(url);
            if (url == null) {
                return null;
            }

            s_imageIcon32	= new ImageIcon(url);
        }

        return s_imageIcon32;

    }		// getImageIcon32

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static ImageIcon getImageIconLogo() {

        if (s_imageIconLogo == null) {

            URL	url	= org.openXpertya.OpenXpertya.class.getResource(s_file100x30);

            // System.out.println(url);
            if (url == null) {
                return null;
            }

            s_imageIconLogo	= new ImageIcon(url);
        }

        return s_imageIconLogo;

    }		// getImageIconLogo

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getOXPHome() {

        // Try Environment
        String	retValue	= Ini.getOXPHome();

        // Look in current Directory
        if ((retValue == null) && (System.getProperty("user.dir").indexOf("ServidorOXP") != -1)) {

            retValue	= System.getProperty("user.dir");

            int	pos	= retValue.indexOf("ServidorOXP");

            retValue	= retValue.substring(pos + 9);
        }

        if (retValue == null) {
            retValue	= File.separator + "ServidorOXP";
        }

        return retValue;
    }		// getHome

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getSupportEMail() {
        return s_supportEmail;
    }		// getSupportEMail

    /**
     * Descripción de Método
     *
     *
     * @param email
     */
    public static void setSupportEMail(String email) {
        s_supportEmail	= email;
    }		// setSupportEMail

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     *
     * @return
     */
    public static synchronized boolean startup(boolean isClient) {

        // Already started
        if (log != null) {
            return true;
        }

        // Check Version
        if (!Login.isJavaOK(isClient) && isClient) {
            System.exit(1);
        }

        /*
         * Fix a bug en impresión de reportes Jasper.
         * 
         * Elevaba excepción "sun.awt.X11.XException: Cannot write XdndAware property"
         *   de manera aleatoria al intentar imprimir un reporte Jasper. Este problema
         *   se detectó a partir de la versión 1.6.0_24.
         *   
         * Referencia para el fix (aunque es un fix de otro error):
         * 		http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=7027598
         */
        System.setProperty("suppressSwingDropSupport", "true");
        
        CLogMgt.initialize(isClient);
        Ini.setClient(isClient);

        // Init Log
        log	= CLogger.getCLogger(OpenXpertya.class);

        // Greeting
        log.info(getSummaryAscii());
        log.info(getOXPHome() + " - " + getJavaInfo() + " - " + getOSInfo());

        // Load System environment
        // EnvLoader.load(Ini.ENV_PREFIX);
        // Set XML environment explicitly to standard 1.4.0 distribution
        // begin vpj-cd e-evolution.com 06/22/2004
        // System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
        // "org.apache.crimson.jaxp.DocumentBuilderFactoryImpl");  //      System Default
        // "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        // System.setProperty("javax.xml.parsers.SAXParserFactory",
        // "org.apache.crimson.jaxp.SAXParserFactoryImpl");        //      System Default
        // "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        // end vpj-cd e-evolution.com 06/22/2004
        // System properties
        Ini.loadProperties(false);

        // Set up Log
        CLogMgt.setLevel(Ini.getProperty(Ini.P_TRACELEVEL));

        if (isClient && Ini.getPropertyBool(Ini.P_TRACEFILE) && (CLogFile.get(false, null) == null)) {
            CLogMgt.addHandler(CLogFile.get(true, Ini.findOXPHome()));
        }

        // Set UI
        if (isClient) {

            CompiereTheme.load();
            CompierePLAF.setPLAF(null);
        }

        // Set Default Database Connection from Ini
        CConnection	cc	= CConnection.get();

        DB.setDBTarget(cc);

        if (isClient) {		// don't test connection
            return false;	// need to call
        }

        return startupEnvironment(isClient);
    }		// startup

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     *
     * @return
     */
    public static boolean startupEnvironment(boolean isClient) {

        startup(isClient);

        if (!DB.isConnected() && !isClient) {
        	log.severe("No hay base de datos: desconectada");
        	return false;
        }
        
        if (!DB.isConnected()) {
            log.severe("No hay base de datos: desconectada");
            System.exit(1);
        }

        // Initialize main cached Singletons
        ModelValidationEngine.get();

        try {

            MSystem	system	= MSystem.get(Env.getCtx());	// Initializes Base Context too

            if (isClient) {
                MClient.get(Env.getCtx(), 0);		// Login Client loaded later
            } else {
                MClient.getAll(Env.getCtx());
            }

            // Document.setKey(system.getSummary());

        } catch (Exception e) {
            log.warning("Problemas con las variables de entorno: " + e.toString());
        }

        // Start Workflow Document Manager (in other package) for PO
        String	className	= null;

        try {

            className	= "org.openXpertya.wf.DocWorkflowManager";
            Class.forName(className);

            // Initialize Archive Engine
            className	= "org.openXpertya.print.ArchiveEngine";
            Class.forName(className);

        } catch (Exception e) {
            log.warning("No arranca: " + className + " - " + e.getMessage());
        }

        return true;

    }		// startupEnvironment

    /**
     * Descripción de Método
     *
     *
     * @param args
     */
    public static void main(String[] args) {

        Splash.getSplash();
        startup(true);		// error exit and initUI

        // Start with class as argument - or if nothing provided with Client
        String	className	= "org.openXpertya.apps.AMenu";

        for (int i = 0; i < args.length; i++) {

            if (!args[i].equals("-debug"))	// ignore -debug
            {

                className	= args[i];

                break;
            }
        }

        //
        try {

            Class	startClass	= Class.forName(className);

            startClass.newInstance();

        } catch (Exception e) {

            System.err.println("Iniciando Libertya: " + className + " - " + e.toString());
            e.printStackTrace();
        }

    }		// main
    
    
    /**
     * dREHER
     * Descripcion de Metodo
     *
     * Devuelve la info de coneccion a la base de datos
     *
     * @return
     */
    public static String getDatabaseInfo(){
        String dataConnection = Ini.getProperty(Ini.P_CONNECTION);
        String dataBaseName = "";
        String dataDBServer = "";
        try{
            int posI = dataConnection.indexOf("DBname=");
            if( posI > -1){
                dataBaseName = dataConnection.substring(posI + 7);
                int posF = dataBaseName.indexOf(",");
                dataBaseName = dataBaseName.substring(0, posF);
            }
            posI = dataConnection.indexOf("DBhost=");
            if( posI > -1){
                dataDBServer = dataConnection.substring(posI + 7);
                int posF = dataDBServer.indexOf(",");
                dataDBServer = dataDBServer.substring(0, posF);
            }
           
            dataConnection = dataDBServer + ":" + dataBaseName;
        }catch(Exception ex){
            log.warning("Error al leer informacion de conexion con BD!");
        }
           
        return dataConnection;
    }
}	// OpenXpertya



/*
 * @(#)OpenXpertya.java   12.Oct 2007
 * 
 *  Fin del fichero OpenXpertya.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 12.feb 2007