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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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
import org.openXpertya.util.ValueNamePair;

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
    static private final String	RELEASE_VERSION_FALLBACK	= "25.0";
    static private final String	RELEASE_VERSION_FILENAME	= "VERSION";
    static private final String	BUILD_INFO_FILENAME	= "BUILD_INFO.properties";
    static private final String	BUILD_INFO_FILE_PROPERTY	= "BUILD_INFO_FILE";
    static private final String	BUILD_INFO_KEY_BRANCH	= "branch";
    static private final String	BUILD_INFO_KEY_COMMIT	= "commit";
    static private final String	BUILD_INFO_KEY_CHANNEL	= "channel";
    static private final String	BUILD_CHANNEL_DEV	= "dev";
    static private final String	BUILD_CHANNEL_RELEASE	= "release";
    static private final String	VERSION_PREFIX_ES	= "Versi\u00f3n ";
    static private final String	VERSION_PREFIX_EN	= "Version ";
    static private final String	RELEASE_VERSION	= loadReleaseVersion();

    /** Descripción de Campo */
    static public final String	MAIN_VERSION	= VERSION_PREFIX_ES + RELEASE_VERSION;

    /** Descripción de Campo */
    static public final String	DATE_VERSION	= "12-11-2025";

    /** Descripción de Campo */
    static public final String	DB_VERSION	= "12-11-2025";

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
    static public final String	COPYRIGHT	= "\u00A9 2025 DISYTEL";

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
    static private Properties	s_buildInfo;

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
     * Version de release visible
     * @return version release
     */
    public static String getReleaseVersion() {
        return RELEASE_VERSION;
    }

    /**
     * Version de visualizacion (considerando canal/commit de build)
     * @return version visible
     */
    public static String getDisplayVersion() {
        String releaseVersion = getReleaseVersion();
        Properties buildInfo = getBuildInfo();
        String branch = getBuildInfoValue(buildInfo, BUILD_INFO_KEY_BRANCH);
        String commit = getBuildInfoValue(buildInfo, BUILD_INFO_KEY_COMMIT);
        String channel = getBuildInfoValue(buildInfo, BUILD_INFO_KEY_CHANNEL);

        boolean devBuild = false;
        if (BUILD_CHANNEL_DEV.equalsIgnoreCase(channel)) {
            devBuild = true;
        } else if (BUILD_CHANNEL_RELEASE.equalsIgnoreCase(channel)) {
            devBuild = false;
        } else {
            devBuild = BUILD_CHANNEL_DEV.equalsIgnoreCase(branch);
        }

        if (!devBuild) {
            return releaseVersion;
        }
        if (Util.isEmpty(commit, true)) {
            return releaseVersion + "-dev";
        }
        return releaseVersion + "-dev-" + commit;
    }

    /**
     * Etiqueta de version a mostrar
     * @return etiqueta de version
     */
    public static String getDisplayVersionLabel() {
        return VERSION_PREFIX_ES + getDisplayVersion();
    }

    /**
     * Version de build para mostrar en UI
     * @return version de build legible
     */
    public static String getBuildVersionLabel() {
        String buildVersion = getImplementationVersion();
        if (buildVersion == null) {
            return "";
        }
        buildVersion = buildVersion.trim();
        if (buildVersion.startsWith(VERSION_PREFIX_ES)) {
            return buildVersion.substring(VERSION_PREFIX_ES.length()).trim();
        }
        if (buildVersion.startsWith(VERSION_PREFIX_EN)) {
            return buildVersion.substring(VERSION_PREFIX_EN.length()).trim();
        }
        return buildVersion;
    }

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
     * Carga version release desde archivo VERSION
     * @return version release
     */
    private static String loadReleaseVersion() {
        String fileVersion = readVersionFromFile(new File(getOXPHome(), RELEASE_VERSION_FILENAME));
        if (!Util.isEmpty(fileVersion, true)) {
            return fileVersion;
        }
        InputStream is = null;
        try {
            is = OpenXpertya.class.getResourceAsStream(RELEASE_VERSION_FILENAME);
            String resourceVersion = readVersionFromStream(is);
            if (!Util.isEmpty(resourceVersion, true)) {
                return resourceVersion;
            }
        } catch (Exception e) {
        	// Usar fallback
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return RELEASE_VERSION_FALLBACK;
    }

    /**
     * Lee archivo VERSION externo
     * @param file archivo version
     * @return version o null
     */
    private static String readVersionFromFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return readVersionFromStream(is);
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * Lee primera linea significativa de stream de version
     * @param is stream
     * @return version o null
     * @throws IOException
     */
    private static String readVersionFromStream(InputStream is) throws IOException {
        if (is == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                return line;
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {}
            }
        }
        return null;
    }

    /**
     * Obtiene build-info cacheado (archivo editable en OXP_HOME)
     * @return propiedades de build
     */
    private static synchronized Properties getBuildInfo() {
        if (s_buildInfo != null) {
            return s_buildInfo;
        }
        s_buildInfo = new Properties();
        File buildInfo = getBuildInfoFile();
        if (buildInfo == null) {
            return s_buildInfo;
        }
        FileInputStream fis = null;
        InputStreamReader isr = null;
        try {
            fis = new FileInputStream(buildInfo);
            isr = new InputStreamReader(fis, "UTF-8");
            Properties rawBuildInfo = new Properties();
            rawBuildInfo.load(isr);
            s_buildInfo = normalizeBuildInfo(rawBuildInfo);
        } catch (Exception e) {
        	// No bloquear startup/login por metadatos de build
        } finally {
        	if (isr != null) {
        		try {
        			isr.close();
        		} catch (IOException e) {}
        	}
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {}
            }
        }
        return s_buildInfo;
    }

    /**
     * Obtiene valor de propiedad de build-info sin espacios
     * @param buildInfo propiedades
     * @param key clave
     * @return valor o vacio
     */
    private static String getBuildInfoValue(Properties buildInfo, String key) {
        if (buildInfo == null || key == null) {
            return "";
        }
        String value = buildInfo.getProperty(key);
        return value == null ? "" : value.trim();
    }

    /**
     * Determina la ubicacion de BUILD_INFO.properties para distintos entornos
     * @return archivo si existe, null en caso contrario
     */
    private static File getBuildInfoFile() {
    	String configuredPath = System.getProperty(BUILD_INFO_FILE_PROPERTY);
    	if (!Util.isEmpty(configuredPath, true)) {
    		File configuredFile = new File(configuredPath.trim());
    		if (isReadableFile(configuredFile)) {
    			return configuredFile;
    		}
    	}
    	File buildInfo = new File(getOXPHome(), BUILD_INFO_FILENAME);
    	if (isReadableFile(buildInfo)) {
    		return buildInfo;
    	}
    	String oxpHome = Ini.findOXPHome();
    	if (!Util.isEmpty(oxpHome, true)) {
    		buildInfo = new File(oxpHome, BUILD_INFO_FILENAME);
    		if (isReadableFile(buildInfo)) {
    			return buildInfo;
    		}
    	}
    	String userDir = System.getProperty("user.dir");
    	if (!Util.isEmpty(userDir, true)) {
    		buildInfo = new File(userDir, BUILD_INFO_FILENAME);
    		if (isReadableFile(buildInfo)) {
    			return buildInfo;
    		}
    	}
    	buildInfo = new File(BUILD_INFO_FILENAME);
    	if (isReadableFile(buildInfo)) {
    		return buildInfo;
    	}
    	return null;
    }

    /**
     * Indica si un archivo existe y es legible
     * @param file archivo
     * @return true si el archivo puede leerse
     */
    private static boolean isReadableFile(File file) {
    	return file != null && file.exists() && file.isFile() && file.canRead();
    }

    /**
     * Normaliza build-info para evitar problemas de encoding/BOM
     * @param rawBuildInfo build-info crudo
     * @return build-info normalizado
     */
    private static Properties normalizeBuildInfo(Properties rawBuildInfo) {
    	Properties normalized = new Properties();
    	if (rawBuildInfo == null) {
    		return normalized;
    	}
    	Enumeration keys = rawBuildInfo.propertyNames();
    	while (keys.hasMoreElements()) {
    		String rawKey = String.valueOf(keys.nextElement());
    		String key = sanitizeBuildInfoToken(rawKey);
    		if (Util.isEmpty(key, true)) {
    			continue;
    		}
    		String value = sanitizeBuildInfoToken(rawBuildInfo.getProperty(rawKey));
    		normalized.setProperty(key, value);
    	}
    	return normalized;
    }

    /**
     * Quita espacios y BOM de una propiedad
     * @param token valor crudo
     * @return valor normalizado
     */
    private static String sanitizeBuildInfoToken(String token) {
    	if (token == null) {
    		return "";
    	}
    	String normalized = token.trim();
    	while (normalized.startsWith("\uFEFF")) {
    		normalized = normalized.substring(1).trim();
    	}
    	return normalized;
    }

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

        // Already started (check log and db connection)
        if (log != null && DB.getDatabase() != null) {
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
        
        try {
	        // Novedades bajo LY21.0: Visualizar el mensaje por unica vez
        	String property = "LY.NEWS.RELEASE.21.0";
	        String plaf = UIManager.getLookAndFeel().getName();
	        if (!"Libertya".equals(plaf) && !"Y".equals(Ini.getProperty(property))) {
	        	// Forzar el uso del nuevo look & feel al menos en la primer ejecucion de la nueva version
	        	ValueNamePair lyplaf = new ValueNamePair("org.libertya.plaf.LibertyaLookAndFeel", "Libertya");
	        	CompierePLAF.setPLAF(lyplaf, null, null);
	        	
	        	// Notificar una unica vez por usuario
	        	JOptionPane.showMessageDialog(null, " Novedades Libertya 21.0 \n\n " +
	        										" Hemos renovado la interfaz de usuario de Libertya. \n " +
	        										" Si quieres volver a la interfaz anterior puedes hacerlo desde \n " + 
	        			 							" Herramientas -> Preferencias -> Tema del Interfaz del Usuario");
	        	Ini.setProperty(property, true);
	        	Ini.saveProperties(true);
	        }
        } catch (Exception e) { /* Nada que hacer */ }

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
