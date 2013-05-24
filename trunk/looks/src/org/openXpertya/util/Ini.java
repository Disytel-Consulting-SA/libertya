/*
 * @(#)Ini.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.util;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.compiere.plaf.CompiereTheme;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public final class Ini implements Serializable {

    /** Descripción de Campos */
    public static final String	ARCHIVO_PROPIEDADES_OXP	= "Libertya.properties";

    // Property Constants and Default Values

    /** Descripción de Campos */
    public static final String	P_UID	= "ApplicationUserID";

    //

    /** Descripción de Campos */
    public static final String	P_TRACELEVEL	= "TraceLevel";

    /** Descripción de Campos */
    public static final String	P_TRACEFILE	= "TraceFile";

    //

    /** Descripción de Campos */
    public static final String	P_STORE_PWD	= "StorePassword";

    //

    /** Descripción de Campos */
    public static final String	P_PWD	= "ApplicationPassword";

    /** Descripción de Campos */
    public static final String	P_PROCESS	= "ServerProcess";

    //

    /** Descripción de Campos */
    public static final String	P_OBJECTS	= "ServerObjects";

    //

    /** Descripción de Campos */
    public static final String	P_LANGUAGE	= "Language";

    //

    /** Descripción de Campos */
    public static final String	P_INI	= "FileNameINI";

    //

    /** Descripción de Campos */
    public static final String	P_CONTEXT	= "DataSource";

    //

    /** Descripción de Campos */
    public static final String	P_CONNECTION	= "Connection";

    /** Descripción de Campos */
    private static final String	DEFAULT_UID	= "AdminLibertya";

    /** Descripción de Campos */
    private static final String	DEFAULT_TRACELEVEL	= "WARNING";

    /** Descripción de Campos */
    private static final boolean	DEFAULT_TRACEFILE	= false;

    /** Descripción de Campos */
    private static final boolean	DEFAULT_STORE_PWD	= true;

    /** Descripción de Campos */
    private static final String	DEFAULT_PWD	= "AdminLibertya";

    /** Descripción de Campos */
    private static final boolean	DEFAULT_PROCESS	= false;

    /** Descripción de Campos */
    private static final boolean	DEFAULT_OBJECTS	= false;

    /** Descripción de Campos */
    private static final String	DEFAULT_LANGUAGE	= Language.getName(System.getProperty("user.language") + "_" + System.getProperty("user.country"));

    /** Descripción de Campos */
    private static final String	DEFAULT_INI	= "";

    /** Descripción de Campos */
    private static final String	DEFAULT_CONTEXT	= "BaseDatosOXP";

    /** Descripción de Campos */
    private static final String	DEFAULT_CONNECTION	= "";

    //

    /** Descripción de Campos */
    public static final String	P_UI_LOOK	= "UILookFeel";

    /** Descripción de Campos */
   // private static final String	DEFAULT_UI_LOOK	= CompiereLookAndFeel.NAME;
    private static final String	DEFAULT_UI_LOOK	= "LibertyaLiquid";

    //

    /** Descripción de Campos */
    public static final String	P_UI_THEME	= "UITheme";

    //

    /** Descripción de Campos */
    public static final String	P_TEMP_DIR	= "TempDir";

    //

    /** Descripción de Campos */
    public static final String	P_SHOW_TRL	= "ShowTrl";

    //

    /** Descripción de Campos */
    public static final String	P_SHOW_ADVANCED	= "ShowAdvanced";

    //

    /** Descripción de Campos */
    public static final String	P_SHOW_ACCT	= "ShowAcct";

    //

    /** Descripción de Campos */
    public static final String	P_ROLE	= "Role";

    //

    /** Descripción de Campos */
    public static final String	P_PRINTER	= "Printer";

    //

    /** Descripción de Campos */
    public static final String	P_OXPSYS	= "OXPSYS";	// Activar sólo para añadir registros al diccionario de la aplicación

    //

    /** Descripción de Campos */
    public static final String	P_ORG	= "Organization";

    //

    /** Descripción de Campos */
    public static final String	P_CLIENT	= "Client";

    //

    /** Descripción de Campos */
    public static final String	P_A_LOGIN	= "AutoLogin";

    //

    /** Descripción de Campos */
    public static final String	P_A_COMMIT	= "AutoCommit";

    /** Descripción de Campos */
    private static final String	DEFAULT_UI_THEME	= CompiereTheme.NAME;

    /** Descripción de Campos */
    private static final String	DEFAULT_TEMP_DIR	= "";

    /** Descripción de Campos */
    private static final boolean	DEFAULT_SHOW_TRL	= false;

    /** Descripción de Campos */
    private static final boolean	DEFAULT_SHOW_ADVANCED	= true;

    /** Descripción de Campos */
    private static final boolean	DEFAULT_SHOW_ACCT	= true;

    /** Descripción de Campos */
    private static final String	DEFAULT_ROLE	= "";

    /** Descripción de Campos */
    private static final String	DEFAULT_PRINTER	= "";

    /** Descripción de Campos */
    private static final boolean	DEFAULT_OXPSYS	= false;

    /** Descripción de Campos */
    private static final String	DEFAULT_ORG	= "";

    /** Descripción de Campos */
    private static final String	DEFAULT_CLIENT	= "";

    /** Descripción de Campos */
    private static final boolean	DEFAULT_A_LOGIN	= false;

    /** Descripción de Campos */
    private static final boolean	DEFAULT_A_COMMIT	= true;

    //

    /** Descripción de Campos */
    public static final String	P_WAREHOUSE	= "Warehouse";

    //

    /** Descripción de Campos */
    public static final String	P_TODAY	= "Today";

    //

    /** Descripción de Campos */
    public static final String	P_PRINTPREVIEW	= "PrintPreview";

    /** Descripción de Campos */
    private static final String	DEFAULT_WAREHOUSE	= "";

    /** Descripción de Campos */
    private static final Timestamp	DEFAULT_TODAY	= new Timestamp(System.currentTimeMillis());

    /** Descripción de Campos */
    private static final boolean	DEFAULT_PRINTPREVIEW	= false;

    //

    /** Descripción de Campos */
    private static final String	P_WARNING	= "Warning";

    /** Descripción de Campos */
    private static final String	DEFAULT_WARNING	= "Do_not_change_any_of_the_data_as_they_will_have_undocumented_side_effects.";

	/** Charset */
	public static final String P_CHARSET = "Charset";
    
    /** Descripción de Campos */
    private static final String	P_WARNING_de	= "WarningD";

    /** Descripción de Campos */
    private static final String[]	PROPERTIES	= new String[] {
        P_UID, P_PWD, P_TRACELEVEL, P_TRACEFILE, P_LANGUAGE, P_INI, P_CONNECTION, P_OBJECTS, P_PROCESS, P_STORE_PWD, P_UI_LOOK, P_UI_THEME, P_A_COMMIT, P_A_LOGIN, P_OXPSYS, P_SHOW_ACCT, P_SHOW_TRL, P_SHOW_ADVANCED, P_CONTEXT, P_TEMP_DIR, P_ROLE, P_CLIENT, P_ORG, P_PRINTER, P_WAREHOUSE, P_TODAY, P_PRINTPREVIEW, P_WARNING, P_WARNING_de
    };

    /** Descripción de Campos */
    private static final String	DEFAULT_WARNING_de	= "Einstellungen_nicht_aendern,_da_diese_undokumentierte_Nebenwirkungen_haben.";

    /** Descripción de Campos */
    private static final String[]	VALUES	= new String[] {

        DEFAULT_UID, DEFAULT_PWD, DEFAULT_TRACELEVEL, DEFAULT_TRACEFILE
                ? "Y"
                : "N", DEFAULT_LANGUAGE, DEFAULT_INI, DEFAULT_CONNECTION, DEFAULT_OBJECTS
                ? "Y"
                : "N", DEFAULT_PROCESS
                       ? "Y"
                       : "N", DEFAULT_STORE_PWD
                              ? "Y"
                              : "N", DEFAULT_UI_LOOK, DEFAULT_UI_THEME, DEFAULT_A_COMMIT
                ? "Y"
                : "N", DEFAULT_A_LOGIN
                       ? "Y"
                       : "N", DEFAULT_OXPSYS
                              ? "Y"
                              : "N", DEFAULT_SHOW_ACCT
                                     ? "Y"
                                     : "N", DEFAULT_SHOW_TRL
                ? "Y"
                : "N", DEFAULT_SHOW_ADVANCED
                       ? "Y"
                       : "N", DEFAULT_CONTEXT, DEFAULT_TEMP_DIR, DEFAULT_ROLE, DEFAULT_CLIENT, DEFAULT_ORG, DEFAULT_PRINTER, DEFAULT_WAREHOUSE, DEFAULT_TODAY.toString(), DEFAULT_PRINTPREVIEW
                ? "Y"
                : "N", DEFAULT_WARNING, DEFAULT_WARNING_de
    };

    /** Descripción de Campos */
    private static Properties	s_prop	= new Properties();

    /** Descripción de Campos */
    private static boolean	s_loaded	= false;

    /** Descripción de Campos */
    private static boolean	s_client	= true;

    /** Descripción de Campos */
    private static Logger	log	= Logger.getLogger("org.openXpertya.util.Ini");

    /** Descripción de Campos */
    public static final String	OXP_HOME	= "OXP_HOME";

    /** Descripción de Campos */
    public static final String	ENV_PREFIX	= "env.";

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param defaultValue
     *
     * @return
     */
    private static String checkProperty(String key, String defaultValue) {

        String	result	= null;

        if (key.equals(P_WARNING) || key.equals(P_WARNING_de)) {
            result	= defaultValue;
        } else if (!isClient()) {
            result	= s_prop.getProperty(key, Secure.CLEARTEXT + defaultValue);
        } else {
            result	= s_prop.getProperty(key, Secure.encrypt(defaultValue));
        }

        s_prop.setProperty(key, result);

        return result;

    }		// checkProperty

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String findOXPHome() {

        String	ch	= getOXPHome();

        if (ch != null) {
            return ch;
        }

        File[]	roots	= File.listRoots();

        for (int i = 0; i < roots.length; i++) {

            if (roots[i].getAbsolutePath().startsWith("A:")) {
                continue;
            }

            File[]	subs	= roots[i].listFiles();

            if (subs == null) {
                continue;
            }

            for (int j = 0; j < subs.length; j++) {

                if (!subs[j].isDirectory()) {
                    continue;
                }

                String	fileName	= subs[j].getAbsolutePath();

                if (fileName.indexOf("ServidorOXP") != 1) {

                    String	libDir	= fileName + File.separator + "lib";
                    File	lib	= new File(libDir);

                    if (lib.exists() && lib.isDirectory()) {
                        return fileName;
                    }
                }
            }
        }

        return ch;

    }		// findOXPHome

    /**
     * Descripción de Método
     *
     *
     * @param reload
     */
    public static void loadProperties(boolean reload) {

        if (reload || (s_prop.size() == 0)) {
            loadProperties(getFileName(s_client));
        }

    }		// loadProperties

    /**
     * Descripción de Método
     *
     *
     * @param filename
     *
     * @return
     */
    public static boolean loadProperties(String filename) {

        boolean	loadOK		= true;
        boolean	firstTime	= false;

        s_prop	= new Properties();

        FileInputStream	fis	= null;

        try {

            fis	= new FileInputStream(filename);
            s_prop.load(fis);
            fis.close();

        } catch (FileNotFoundException e) {

            log.warning(filename + " not found");
            loadOK	= false;

        } catch (Exception e) {

            log.log(Level.SEVERE, filename + " - " + e.toString());
            loadOK	= false;

        } catch (Throwable t) {

            log.log(Level.SEVERE, filename + " - " + t.toString());
            loadOK	= false;
        }

        if (!loadOK) {

            log.config(filename);
            firstTime	= true;
			if (isShowLicenseDialog()) {
				if (!IniDialog.accept())
                	System.exit(-1);
            }
        }

        // Check/set properties    defaults
        for (int i = 0; i < PROPERTIES.length; i++) {

            if (VALUES[i].length() > 0) {
                checkProperty(PROPERTIES[i], VALUES[i]);
            }
        }

        //
        String	tempDir	= System.getProperty("java.io.tmpdir");

        if ((tempDir == null) || (tempDir.length() == 1)) {
            tempDir	= getOXPHome();
        }

        if (tempDir == null) {
            tempDir	= "";
        }

        checkProperty(P_TEMP_DIR, tempDir);

        // Save if not exist or could not be read
        if (!loadOK) {
            saveProperties(true);
        }

        s_loaded	= true;
        log.info(filename + " #" + s_prop.size());

        return firstTime;

    }		// loadProperties

    /**
     * Descripción de Método
     *
     *
     * @param tryUserHome
     */
    public static void saveProperties(boolean tryUserHome) {

        String			fileName	= getFileName(tryUserHome);
        FileOutputStream	fos		= null;

        try {

            File	f	= new File(fileName);

            fos	= new FileOutputStream(f);
            s_prop.store(fos, "OpenXpertya");
            fos.flush();
            fos.close();

        } catch (Exception e) {

            log.log(Level.SEVERE, "Cannot save Properties to " + fileName + " - " + e.toString());

            return;

        } catch (Throwable t) {

            log.log(Level.SEVERE, "Cannot save Properties to " + fileName + " - " + t.toString());

            return;
        }

        log.finer(fileName);

    }		// save

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getAsString() {

        StringBuffer	buf	= new StringBuffer("Ini - ");
        Enumeration	e	= s_prop.keys();

        while (e.hasMoreElements()) {

            String	key	= (String) e.nextElement();

            buf.append(key).append("=");
            buf.append(getProperty(key)).append("; ");
        }

        return buf.toString();

    }		// toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static int getDividerLocation() {

        String	key	= "Divider";
        String	value	= (String) s_prop.get(key);

        if ((value == null) || (value.length() == 0)) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {}

        return 0;

    }		// getDividerLocation

    /**
     * Descripción de Método
     *
     *
     * @param tryUserHome
     *
     * @return
     */
    private static String getFileName(boolean tryUserHome) {

        if (System.getProperty("PropertyFile") != null) {
            return System.getProperty("PropertyFile");
        }

        //
        String	base	= null;

        if (tryUserHome && s_client) {
            base	= System.getProperty("user.home");
        }

        // Server
        if (!s_client || (base == null) || (base.length() == 0)) {

            String	home	= getOXPHome();

            if (home != null) {
                base	= home;
            }
        }

        if ((base != null) &&!base.endsWith(File.separator)) {
            base	+= File.separator;
        }

        if (base == null) {
            base	= "";
        }

        //
        return base + ARCHIVO_PROPIEDADES_OXP;

    }		// getFileName

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static String getOXPHome() {

        String	env	= System.getProperty(ENV_PREFIX + OXP_HOME);

        if (env == null) {
            env	= System.getProperty(OXP_HOME);
        }

        return env;

    }		// getOXPHome

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static Properties getProperties() {
        return s_prop;
    }		// getProperties

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */
    public static String getProperty(String key) {

        if (key == null) {
            return "";
        }

        String	retStr	= s_prop.getProperty(key, "");

        if ((retStr == null) || (retStr.length() == 0)) {
            return "";
        }

        //
        String	value	= Secure.decrypt(retStr);

        // System.out.println("Ini.get " + key + "=" + value);
        if (value == null) {
            return "";
        }

        return value;

    }		// getProperty

    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */
    public static boolean getPropertyBool(String key) {
        return getProperty(key).equals("Y");
    }		// getProperty

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     *
     * @return
     */
    public static Dimension getWindowDimension(int AD_Window_ID) {

        String	key	= "WindowDim" + AD_Window_ID;
        String	value	= (String) s_prop.get(key);

        if ((value == null) || (value.length() == 0)) {
            return null;
        }

        int	index	= value.indexOf("|");

        if (index == -1) {
            return null;
        }

        try {

            String	w	= value.substring(0, index);
            String	h	= value.substring(index + 1);

            return new Dimension(Integer.parseInt(w), Integer.parseInt(h));

        } catch (Exception e) {}

        return null;

    }		// getWindowDimension

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     *
     * @return
     */
    public static Point getWindowLocation(int AD_Window_ID) {

        String	key	= "WindowLoc" + AD_Window_ID;
        String	value	= (String) s_prop.get(key);

        if ((value == null) || (value.length() == 0)) {
            return null;
        }

        int	index	= value.indexOf("|");

        if (index == -1) {
            return null;
        }

        try {

            String	x	= value.substring(0, index);
            String	y	= value.substring(index + 1);

            return new Point(Integer.parseInt(x), Integer.parseInt(y));

        } catch (Exception e) {}

        return null;

    }		// getWindowLocation
	/** Show license dialog for first time **/
	private static boolean		s_license_dialog = true;

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static boolean isClient() {
        return s_client;
    }		// isClient

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static boolean isLoaded() {
        return s_loaded;
    }		// isLoaded

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static boolean isServerObjects() {
        return s_client && "Y".equals(getProperty(P_OBJECTS));
    }		// isServerObjects

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public static boolean isServerProcess() {
        return s_client && "Y".equals(getProperty(P_PROCESS));
    }		// isServerProcess

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param client
     */
    public static void setClient(boolean client) {
        s_client	= client;
    }		// setClient

	/**
	 * Set show license dialog for new setup
	 * @param b
	 */
	public static void setShowLicenseDialog(boolean b)
	{
		s_license_dialog = b;
	}
	
	/**
	 * Is show license dialog for new setup
	 * @return boolean
	 */
	public static boolean isShowLicenseDialog()
	{
		return s_license_dialog;
	}
	
    /**
     * Descripción de Método
     *
     *
     * @param dividerLocation
     */
    public static void setDividerLocation(int dividerLocation) {

        String	key	= "Divider";
        String	value	= String.valueOf(dividerLocation);

        s_prop.put(key, value);

    }		// setDividerLocation

    /**
     * Descripción de Método
     *
     *
     * @param OXPHome
     */
    public static void setOXPHome(String OXPHome) {

        if ((OXPHome != null) && (OXPHome.length() > 0)) {
            System.setProperty(OXP_HOME, OXPHome);
        }

    }		// setOXPHome

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     */
    public static void setProperty(String key, boolean value) {

        setProperty(key, value
                         ? "Y"
                         : "N");

    }		// setProperty

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     */
    public static void setProperty(String key, int value) {
        setProperty(key, String.valueOf(value));
    }		// setProperty

    /**
     * Descripción de Método
     *
     *
     * @param key
     * @param value
     */
    public static void setProperty(String key, String value) {

        // System.out.println("Ini.set " + key + "=" + value);
        if (s_prop == null) {
            s_prop	= new Properties();
        }

        if (key.equals(P_WARNING) || key.equals(P_WARNING_de)) {
            s_prop.setProperty(key, value);
        } else if (!isClient()) {
            s_prop.setProperty(key, Secure.CLEARTEXT + value);
        } else {
            s_prop.setProperty(key, Secure.encrypt(value));
        }
    }		// setProperty

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     * @param windowDimension
     */
    public static void setWindowDimension(int AD_Window_ID, Dimension windowDimension) {

        String	key	= "WindowDim" + AD_Window_ID;

        if (windowDimension != null) {

            String	value	= windowDimension.width + "|" + windowDimension.height;

            s_prop.put(key, value);

        } else {
            s_prop.remove(key);
        }

    }		// setWindowDimension

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     * @param windowLocation
     */
    public static void setWindowLocation(int AD_Window_ID, Point windowLocation) {

        String	key	= "WindowLoc" + AD_Window_ID;

        if (windowLocation != null) {

            String	value	= windowLocation.x + "|" + windowLocation.y;

            s_prop.put(key, value);

        } else {
            s_prop.remove(key);
        }

    }		// setWindowLocation
    
	public static boolean isCacheWindow()
	{
		return getProperty (P_CACHE_WINDOW).equals("Y");
	}	//	isCacheWindow
	/** Cache Windows			*/
	public static final String  P_CACHE_WINDOW =	"CacheWindow";

    /** Log Migration Script	*/
	public static final String  P_LOGMIGRATIONSCRIPT =		"LogMigrationScript";	//	Log migration script

	/**
	 *	Get Property as Boolean
	 *  @param key  Key
	 *  @return     Value
	 */
	public static boolean isPropertyBool (String key)
	{
		return getProperty (key).equals("Y");
	}	//	getProperty

	
	/**
	 * Get Available Encoding Charsets
	 * @return array of available encoding charsets
	 * @since 3.1.4
	 */
	public static Charset[] getAvailableCharsets() {
		Collection<Charset> col = Charset.availableCharsets().values();
		Charset[] arr = new Charset[col.size()];
		col.toArray(arr);
		return arr;
	}

	
	/**
	 * Get current charset
	 * @return current charset
	 * @since 3.1.4
	 */
	public static Charset getCharset() {
		String charsetName = getProperty(P_CHARSET);
		if (charsetName == null || charsetName.length() == 0)
			return Charset.defaultCharset();
		try {
			return Charset.forName(charsetName);
		} catch (Exception e) {
		}
		return Charset.defaultCharset();
	}

}	// Ini



/*
 * @(#)Ini.java   02.jul 2007
 * 
 *  Fin del fichero Ini.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
