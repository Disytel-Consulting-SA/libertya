/*
 * @(#)ConfigurationData.java   11.jun 2007  Versión 2.2
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.openXpertya.install.KeyStoreMgt;
import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.db.Database;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.EMail;
import org.openXpertya.util.EMailAuthenticator;
import org.openXpertya.util.Ini;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigurationData {

    /** Descripción de Campo */
    static CLogger	log	= CLogger.getCLogger(ConfigurationData.class);

    /** Descripción de Campo */
    public static final String	USUARIO_MAIL_OXP	= "USUARIO_MAIL_OXP";

    /** Descripción de Campo */
    public static final String	USUARIO_FTP_OXP	= "USUARIO_FTP_OXP";

    /** Descripción de Campo */
    public static final String	USUARIO_BD_OXP	= "USUARIO_BD_OXP";

    /** Descripción de Campo */
    public static final String	URL_BD_OXP	= "URL_BD_OXP";

    /** Descripción de Campo */
    public static final String	TIPO_BD_OXP	= "TIPO_BD_OXP";

    /** Descripción de Campo */
    public static final String	TIPO_APPS_OXP	= "TIPO_APPS_OXP";

    /** Descripción de Campo */
    public static final String	SYSTEM_BD_OXP	= "SYSTEM_BD_OXP";

    /** Descripción de Campo */
    public static final String	SERVIDOR_MAIL_OXP	= "SERVIDOR_MAIL_OXP";

    /** Descripción de Campo */
    public static final String	SERVIDOR_FTP_OXP	= "SERVIDOR_FTP_OXP";

    /** Descripción de Campo */
    public static final String	SERVIDOR_BD_OXP	= "SERVIDOR_BD_OXP";

    /** Descripción de Campo */
    public static final String	SERVIDOR_APPS_OXP	= "SERVIDOR_APPS_OXP";

    /** Descripción de Campo */
    public static final String	PUERTO_WEB_OXP	= "PUERTO_WEB_OXP";

    /** Descripción de Campo */
    public static final String	PUERTO_SSL_OXP	= "PUERTO_SSL_OXP";

    /** Descripción de Campo */
    public static final String	PUERTO_JNP_OXP	= "PUERTO_JNP_OXP";

    /** Descripción de Campo */
    public static final String	PUERTO_BD_OXP	= "PUERTO_BD_OXP";

    /** Descripción de Campo */
    public static final String	PREFIJO_FTP_OXP	= "PREFIJO_FTP_OXP";

    /** Descripción de Campo */
    public static final String	PASSWORD_MAIL_OXP	= "PASSWORD_MAIL_OXP";

    /** Descripción de Campo */
    public static final String	PASSWD_FTP_OXP	= "PASSWD_FTP_OXP";

    /** Descripción de Campo */
    public static final String	PASSWD_BD_OXP	= "PASSWD_BD_OXP";

    /** Descripción de Campo */
    public static final String	OXP_HOME	= "OXP_HOME";

    /** Descripción de Campo */
    public static final String	OPCIONES_JAVA_OXP	= "OPCIONES_JAVA_OXP";

    /** Descripción de Campo */
    public static final String	NOMBRE_BD_OXP	= "NOMBRE_BD_OXP";

    /** Descripción de Campo */
    public static final String	KEYSTORE_PASSWORD	= "libertya";

    /** Descripción de Campo */
    public static final String	KEYSTORE_OXP	= "KEYSTORE_OXP";

    /** Descripción de Campo */
    public static final String	KEYSTOREPASS_OXP	= "KEYSTOREPASS_OXP";

    /** Descripción de Campo */
    public static final String	JAVA_TYPE	= "TIPO_JAVA_OXP";

    /** Descripción de Campo */
    public static final String	JAVA_HOME	= "JAVA_HOME";

    /** Descripción de Campo */
    private static String	JAVATYPE_SUN	= "sun";

    /** Descripción de Campo */
    private static String	JAVATYPE_MAC	= "mac";

    /** Descripción de Campo */
    private static String	JAVATYPE_IBM	= "<ibm>";

    /** Descripción de Campo */
    static String[]	JAVATYPE	= new String[] { JAVATYPE_SUN, JAVATYPE_MAC, JAVATYPE_IBM };

    /** Descripción de Campo */
    public static final String	DEPLOY_APPS_OXP	= "DEPLOY_APPS_OXP";

    /** Descripción de Campo */
    private static String	DBTYPE_SYBASE	= "sybase";

    // begin e-evolution vpj-cd 02/07/2005 PostgreSQL

    /** Descripción de Campo */
    private static String	DBTYPE_POSTGRESQL	= "PostgreSQL";

    /** Descripción de Campo */
    private static String	DBTYPE_ORACLE	= "oracle";

    /** Descripción de Campo */
    private static String	DBTYPE_MYSQL	= "<mySql>";

    /** Descripción de Campo */
    private static String	DBTYPE_MS	= "<sqlServer>";

    // end e-evolution vpj-cd 02/07/2005 PostgreSQL

    /** Descripción de Campo */
    private static String	DBTYPE_DB2	= "<db2>";

    /** Descripción de Campo */
    static String[]	DBTYPE	= new String[]

    // begin e-evolution vpj-cd 02/07/2005 PostgreSQL
    // {DBTYPE_ORACLE, DBTYPE_SYBASE, DBTYPE_DB2, DBTYPE_MS, DBTYPE_MYSQL};
    {
        //DBTYPE_ORACLE, DBTYPE_SYBASE, DBTYPE_POSTGRESQL, DBTYPE_DB2, DBTYPE_MS, DBTYPE_MYSQL
    	DBTYPE_POSTGRESQL
    };

    /** Descripción de Campo */
    public static final String	CODIGOALIASKEYSTORE_OXP	= "CODIGOALIASKEYSTORE_OXP";

    /** Descripción de Campo */
    public static final String	ARCHIVO_VAR_OXP	= "LibertyaEnv.properties";

    /** Descripción de Campo */
    private static String	APPSTYPE_TOMCAT	= "<tomcatOnly>";

    /** Descripción de Campo */
    private static String	APPSTYPE_ORACLE	= "<oracleAS>";

    /** Descripción de Campo */
    private static String	APPSTYPE_JBOSS	= "jboss";

    /** Descripción de Campo */
    private static String	APPSTYPE_J2EE	= "<plainJ2EE>";

    /** Descripción de Campo */
    private static String	APPSTYPE_IBM	= "<ibmWS>";

    /** Descripción de Campo */
    static String[]	APPSTYPE	= new String[] { APPSTYPE_JBOSS, APPSTYPE_IBM, APPSTYPE_ORACLE, APPSTYPE_J2EE, APPSTYPE_TOMCAT };

    /** Descripción de Campo */
    public static final String	ALIAS_WEB_OXP	= "ALIAS_WEB_OXP";

    /** Descripción de Campo */
    public static final String	ALIASWEBKEYSTORE_OXP	= "ALIASWEBKEYSTORE_OXP";

    /** Descripción de Campo */
    public static final String	ADMIN_MAIL_OXP	= "ADMIN_MAIL_OXP";
    
    public static final String	OXP_CERT_CN = "OXP_CERT_CN";
    public static final String	OXP_CERT_ORG = "OXP_CERT_ORG";
    public static final String	OXP_CERT_ORG_UNIT = "OXP_CERT_ORG_UNIT";
    public static final String	OXP_CERT_LOCATION = "OXP_CERT_LOCATION";
    public static final String	OXP_CERT_STATE = "OXP_CERT_STATE";
    public static final String	OXP_CERT_COUNTRY = "OXP_CERT_COUNTRY";

    /** Descripción de Campo */
    protected ConfigurationPanel	p_panel	= null;

    /** Descripción de Campo */
    protected Properties	p_properties	= new Properties();

    /** Descripción de Campo */
    private Config[]	m_javaConfig	= new Config[] { new ConfigVMSun(this), new ConfigVMMac(this), null };

    // end e-evolution vpj-cd 02/07/2005 PostgreSQL

    /** Descripción de Campo */
    private Config[]	m_databaseConfig	= new Config[]

    // begin e-evolution vpj-cd 02/07/2005 PostgreSQL
    // {new ConfigOracle(this), new ConfigSybase(this), null, null, null};
    {
        //new ConfigOracle(this), new ConfigSybase(this), new ConfigPostgreSQL(this), null, null, null
    		new ConfigPostgreSQL(this), null, null, null
    };

    /** Descripción de Campo */
    private Config[]	m_appsConfig	= new Config[] { new ConfigJBoss(this), null, null, null, null };

    /** Descripción de Campo */
    private File	m_OXPHome;

    /**
     * Constructor ...
     *
     *
     * @param panel
     */
    public ConfigurationData(ConfigurationPanel panel) {

        super();
        p_panel	= panel;

    }		// ConfigurationData

	private void updateProperty(String property, String value) {
		if (value == null) value = "";
		String currentValue = (String)p_properties.get(property);
		if (currentValue == null)
			p_properties.put(property, value);
		else if (!currentValue.equals(value))
			p_properties.put(property, value);
	}
	
	public void initAppsServer()
	{
		int index = (p_panel != null ? p_panel.fAppsType.getSelectedIndex() : 0);
		initAppsServer(index);
	}	//	initAppsServer
    
    /**
     * Descripción de Método
     *
     */
    public void initAppsServer(int index) {

        if ((index < 0) || (index >= APPSTYPE.length)) {
            log.warning("AppsServerType Index invalid: " + index);
        } else if (m_appsConfig[index] == null) {

            log.warning("AppsServerType Config missing: " + APPSTYPE[index]);
            if (p_panel != null)
            	p_panel.fAppsType.setSelectedIndex(0);

        } else {
        	if (p_panel != null)
        		m_appsConfig[index].init();
        }
    }		// initAppsServer

    // end e-evolution vpj-cd 02/07/2005 PostgreSQL
    
	/**
	 * 	Init Database
	 * 	@param selected DB
	 */
	public void initDatabase(String selected)
	{
		int index = (p_panel != null ? p_panel.fDatabaseType.getSelectedIndex() : 0);
		initDatabase(selected, index);
	}	//	initDatabase

    /**
     * Descripción de Método
     *
     *
     * @param selected
     */
    public void initDatabase(String selected, int index) {

        if ((index < 0) || (index >= DBTYPE.length)) {
            log.warning("DatabaseType Index invalid: " + index);
        } else if (m_databaseConfig[index] == null) {

            log.warning("DatabaseType Config missing: " + DBTYPE[index]);
            if (p_panel != null) {
                p_panel.fDatabaseType.setSelectedIndex(0);				
			}
        } else {

        	if (p_panel != null) {
        		m_databaseConfig[index].init();
        	}

            DefaultComboBoxModel	model	= new DefaultComboBoxModel(m_databaseConfig[index].discoverDatabases(selected));
            if (p_panel != null) {
                p_panel.fDatabaseDiscovered.setModel(model);				
			}
        }

    }		// initDatabase

	/**
	 * 	Init Database
	 */
	public void initJava()
	{
		int index = (p_panel != null ? p_panel.fJavaType.getSelectedIndex() : 0);
		initJava(index);
	}	//	initDatabase

    
    /**
     * Descripción de Método
     *
     */
    public void initJava(int index) {

        if ((index < 0) || (index >= JAVATYPE.length)) {
            log.warning("JavaType Index invalid: " + index);
        } else if (m_javaConfig[index] == null) {

            log.warning("JavaType Config missing: " + JAVATYPE[index]);
            if (p_panel != null)
            	p_panel.fJavaType.setSelectedIndex(0);

        } else {
        	if (p_panel != null)
        		m_javaConfig[index].init();
        }

    }		// initDatabase

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean load() {

        String	OXPHome	= System.getProperty(OXP_HOME);

        if ((OXPHome == null) || (OXPHome.length() == 0)) {
            OXPHome	= System.getProperty("user.dir");
        }

        boolean	envLoaded	= false;
        String	fileName	= OXPHome + File.separator + ARCHIVO_VAR_OXP;
        File	env		= new File(fileName);

        if (env.exists()) {

            try {

                FileInputStream	fis	= new FileInputStream(env);

                p_properties.load(fis);
                fis.close();

            } catch (Exception e) {
                log.severe(e.toString());
            }

            log.info(env.toString());

            if (p_properties.size() > 5) {
                envLoaded	= true;
            }
            
			Properties loaded = new Properties();
			loaded.putAll(p_properties);

            //
			int javaIndex = setJavaType((String) p_properties.get(JAVA_TYPE));
            initJava(javaIndex);
            if (loaded.contains(JAVA_HOME)) {
            	setJavaHome((String) p_properties.get(JAVA_HOME));
            }
            //
            setOXPHome((String) p_properties.get(OXP_HOME));
            String	s	= (String) p_properties.get(KEYSTOREPASS_OXP);
            if ((s == null) || (s.length() == 0)) {
                s	= KEYSTORE_PASSWORD;
            }

            setKeyStore(s);

            //
            int appServerIndex = setAppsServerType((String) p_properties.get(TIPO_APPS_OXP));
            initAppsServer(appServerIndex);
            if (loaded.containsKey(SERVIDOR_APPS_OXP))
            	setAppsServer((String) p_properties.get(SERVIDOR_APPS_OXP));
            if (loaded.containsKey(DEPLOY_APPS_OXP))
            	setAppsServerDeployDir((String) p_properties.get(DEPLOY_APPS_OXP));
            if (loaded.containsKey(PUERTO_JNP_OXP))
            	setAppsServerJNPPort((String) p_properties.get(PUERTO_JNP_OXP));
            if (loaded.containsKey(PUERTO_WEB_OXP))
            	setAppsServerWebPort((String) p_properties.get(PUERTO_WEB_OXP));
            if (loaded.containsKey(PUERTO_SSL_OXP))
            	setAppsServerSSLPort((String) p_properties.get(PUERTO_SSL_OXP));

            //
            int dbTypeIndex =  setDatabaseType((String) p_properties.get(TIPO_BD_OXP));
            initDatabase((String) p_properties.get(NOMBRE_BD_OXP),dbTypeIndex);	// fills Database Options
            if (loaded.containsKey(NOMBRE_BD_OXP)) 
            	setDatabaseDiscovered((String) p_properties.get(NOMBRE_BD_OXP));
            if (loaded.containsKey(SERVIDOR_BD_OXP))
            	setDatabaseServer((String) p_properties.get(SERVIDOR_BD_OXP));
            if (loaded.containsKey(PUERTO_BD_OXP))
            	setDatabasePort((String) p_properties.get(PUERTO_BD_OXP));
            if (loaded.containsKey(NOMBRE_BD_OXP))
            	setDatabaseName((String) p_properties.get(NOMBRE_BD_OXP));
            if (loaded.containsKey(USUARIO_BD_OXP))
            	setDatabaseUser((String) p_properties.get(USUARIO_BD_OXP));
            if (loaded.containsKey(PASSWD_BD_OXP))
            	setDatabasePassword((String) p_properties.get(PASSWD_BD_OXP));
            if (loaded.containsKey(SYSTEM_BD_OXP))
            	setDatabaseSystemPassword((String) p_properties.get(SYSTEM_BD_OXP));
            
            if (p_panel != null) {
		        p_panel.fMailServer.setText((String) p_properties.get(SERVIDOR_MAIL_OXP));
		        p_panel.fMailUser.setText((String) p_properties.get(USUARIO_MAIL_OXP));
		        p_panel.fMailPassword.setText((String) p_properties.get(PASSWORD_MAIL_OXP));
		        p_panel.fAdminEMail.setText((String) p_properties.get(ADMIN_MAIL_OXP));
            }
        }

        InetAddress	localhost	= null;
        String		hostName	= "unknown";

        try {

            localhost	= InetAddress.getLocalHost();
            hostName	= localhost.getHostName();

        } catch (Exception e) {
            log.severe("Cannot get local host name");
        }

        // No environment file found - defaults
        // envLoaded = false;
        if (!envLoaded) {

            log.info("Defaults");
            initJava();

            //
            setOXPHome(OXPHome);
            setKeyStore(KEYSTORE_PASSWORD);

            // AppsServer
            initAppsServer();
            setAppsServer(hostName);

            // Database Server
            initDatabase("");
            setDatabaseName(getDatabaseDiscovered());
            setDatabaseSystemPassword("");
            setDatabaseServer(hostName);
            setDatabaseUser(OpenXpertya.NAME2.toLowerCase());
            setDatabasePassword(OpenXpertya.NAME2.toLowerCase());

            // Mail Server
            if (p_panel != null) {
                p_panel.fMailServer.setText(hostName);
                p_panel.fMailUser.setText("info");
                p_panel.fMailPassword.setText("");
                p_panel.fAdminEMail.setText("info@" + hostName);
			}

            //

        }	// !envLoaded

        // Default FTP stuff
        if (!p_properties.containsKey(SERVIDOR_FTP_OXP)) {

            p_properties.setProperty(SERVIDOR_FTP_OXP, "localhost");
            p_properties.setProperty(USUARIO_FTP_OXP, "anonymous");
            p_properties.setProperty(PASSWD_FTP_OXP, "user@host.com");
            p_properties.setProperty(PREFIJO_FTP_OXP, "my");
        }

        // Default Java Options
        if (!p_properties.containsKey(OPCIONES_JAVA_OXP)) {
            p_properties.setProperty(OPCIONES_JAVA_OXP, "-Xms1024M -Xmx1536M -XX:MaxPermSize=1024M -Dfile.encoding=UTF-8");
        }

        // Web Alias
        if (!p_properties.containsKey(ALIAS_WEB_OXP) && (localhost != null)) {
            p_properties.setProperty(ALIAS_WEB_OXP, localhost.getCanonicalHostName());
        }

        // (String)p_properties.get(URL_BD_OXP)    //      derived
        // Keystore Alias
        if (!p_properties.containsKey(CODIGOALIASKEYSTORE_OXP)) {
            p_properties.setProperty(CODIGOALIASKEYSTORE_OXP, KeyStoreMgt.CERTIFICATE_ALIAS);
        }

        if (!p_properties.containsKey(ALIASWEBKEYSTORE_OXP)) {
            p_properties.setProperty(ALIASWEBKEYSTORE_OXP, KeyStoreMgt.CERTIFICATE_ALIAS);
        }

        return true;

    }		// load

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean save() {

        // Add
        p_properties.setProperty("VERSION_PRINCIPAL_OXP", OpenXpertya.MAIN_VERSION);
        p_properties.setProperty("FECHA_VERSION_OPENXP", OpenXpertya.DATE_VERSION);
        p_properties.setProperty("BD_VERSION_OPENXP", OpenXpertya.DB_VERSION);
        log.finest(p_properties.toString());

        // Before we save, load Ini
        Ini.setClient(false);

        String	fileName	= m_OXPHome.getAbsolutePath() + File.separator + Ini.ARCHIVO_PROPIEDADES_OXP;

        Ini.loadProperties(fileName);

        // Save Environment
        fileName	= m_OXPHome.getAbsolutePath() + File.separator + ARCHIVO_VAR_OXP;

        try {

            FileOutputStream	fos	= new FileOutputStream(new File(fileName));

            p_properties.store(fos, ARCHIVO_VAR_OXP);
            fos.flush();
            fos.close();

        } catch (Exception e) {

            log.severe("Cannot save Properties to " + fileName + " - " + e.toString());
            JOptionPane.showConfirmDialog(p_panel, ConfigurationPanel.res.getString("ErrorSave"), ConfigurationPanel.res.getString("InstalarServidorOXP"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

            return false;

        } catch (Throwable t) {

            log.severe("Cannot save Properties to " + fileName + " - " + t.toString());
            JOptionPane.showConfirmDialog(p_panel, ConfigurationPanel.res.getString("ErrorSave"), ConfigurationPanel.res.getString("InstalarServidorOXP"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);

            return false;
        }

        log.info(fileName);

        return saveIni();
    }		// save

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private boolean saveIni() {

        Ini.setOXPHome(m_OXPHome.getAbsolutePath());

        // Create Connection
        String	ccType	= Database.DB_ORACLE;

        if (getDatabaseType().equals(DBTYPE_SYBASE)) {
            ccType	= Database.DB_SYBASE;
        }

        // begin vpj-cd e-evolution 03/17/2005 PostgreSQL
        if (getDatabaseType().equals(DBTYPE_POSTGRESQL)) {
            ccType	= Database.DB_POSTGRESQL;
        }

        // end vpj-cd e-evolution 03/17/2005 PostgreSQL
        CConnection	cc	= null;

        try {

            cc	= CConnection.get(ccType, getDatabaseServer(), getDatabasePort(), getDatabaseName(), getDatabaseUser(), getDatabasePassword());
            cc.setAppsHost(getAppsServer());
            cc.setRMIoverHTTP(false);

        } catch (Exception e) {

            log.log(Level.SEVERE, "connection", e);

            return false;
        }

        if (cc == null) {

            log.severe("No Connection");

            return false;
        }

        Ini.setProperty(Ini.P_CONNECTION, cc.toStringLong());
        Ini.saveProperties(false);

        return true;

    }		// saveIni

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean test() {

        String	error	= testJava();

        if (error != null) {

            log.severe(error);

            return false;
        }

        error	= testOXP();

        if (error != null) {

            log.severe(error);

            return false;
        }
        
        if (p_panel != null)
        	p_panel.setStatusBar(p_panel.lAppsServer.getText());
        error	= testAppsServer();

        if (error != null) {

            log.severe(error);

            return false;
        }

        if (p_panel != null)
        	p_panel.setStatusBar(p_panel.lDatabaseServer.getText());
        error	= testDatabase();

        if (error != null) {

            log.severe(error);

            return false;
        }
        
        if (p_panel != null)
        	p_panel.setStatusBar(p_panel.lMailServer.getText());
        error	= testMail();

        if (error != null) {

            log.severe(error);

            return false;
        }

        return true;

    }		// test

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String testAppsServer() {

        int	index	= p_panel != null 
        	? p_panel.fAppsType.getSelectedIndex()
        	: setAppsServerType((String)p_properties.get(TIPO_APPS_OXP));

        if ((index < 0) || (index >= APPSTYPE.length)) {
            return "AppsServerType Index invalid: " + index;
        } else if (m_appsConfig[index] == null) {
            return "AppsServerType Config class missing: " + index;
        }

        return m_appsConfig[index].test();

    }		// testAppsServer

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String testDatabase() {

        int	index	= p_panel != null
        	? p_panel.fDatabaseType.getSelectedIndex()
        	: setDatabaseType((String) p_properties.get(TIPO_BD_OXP));
        if ((index < 0) || (index >= DBTYPE.length)) {
            return "DatabaseType Index invalid: " + index;
        } else if (m_databaseConfig[index] == null) {
            return "DatabaseType Config class missing: " + index;
        }

        return m_databaseConfig[index].test();

    }		// testDatabase

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String testJava() {

        int	index	= p_panel != null
        	? p_panel.fJavaType.getSelectedIndex()
        	: setJavaType((String) p_properties.get(JAVA_TYPE));

        if ((index < 0) || (index >= JAVATYPE.length)) {
            return "JavaType Index invalid: " + index;
        } else if (m_javaConfig[index] == null) {
            return "JavaType Config class missing: " + index;
        }

        return m_javaConfig[index].test();

    }		// testJava

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private String testMail() {

        // Mail Server
        String	server	= p_panel != null 
        	? p_panel.fMailServer.getText()
        	: (String) p_properties.get(SERVIDOR_MAIL_OXP);
        boolean	pass	= 
        	(server != null) && 
        	(server.length() > 0) && 
        	(server.toLowerCase().indexOf("localhost") == -1) &&
        	!server.equals("127.0.0.1");
        String		error		= "Error Mail Server = " + server;
        InetAddress	mailServer	= null;

        try {

            if (pass) {
                mailServer	= InetAddress.getByName(server);
            }

        } catch (Exception e) {

            error	+= " - " + e.getMessage();
            pass	= false;
        }
        if (p_panel != null)
        	p_panel.signalOK(p_panel.okMailServer, "ErrorMailServer", pass, true, error);

        if (!pass) {
        	p_properties.setProperty(SERVIDOR_MAIL_OXP, "");
        	return error;
        }

        p_properties.setProperty(SERVIDOR_MAIL_OXP, mailServer.getHostName());

        // Mail User
        String	mailUser = p_panel != null 
        		? p_panel.fMailUser.getText()
        		: (String)p_properties.get(USUARIO_MAIL_OXP);
        String	mailPassword = p_panel != null 
        	? new String(p_panel.fMailPassword.getPassword())
        	: (String) p_properties.get(PASSWORD_MAIL_OXP);

        p_properties.setProperty(USUARIO_MAIL_OXP, mailUser);
        p_properties.setProperty(PASSWORD_MAIL_OXP, mailPassword);

        // m_errorString = "ErrorMailUser";
        // log.config("Mail User = " + mailUser + "/" + mailPassword);
        // Mail Address
        String adminEMailString	= p_panel != null 
        	? p_panel.fAdminEMail.getText()
        	: (String) p_properties.get(ADMIN_MAIL_OXP);
        InternetAddress	adminEMail		= null;

        try {
            adminEMail	= new InternetAddress(adminEMailString);
        } catch (Exception e) {

            error	= "Not valid: " + adminEMailString + " - " + e.getMessage();
            pass	= false;
        }

        //
        if (pass) {

            error	= "Not verified EMail = " + adminEMail;
            pass	= testMailServer(mailServer, adminEMail, mailUser, mailPassword);
        }
        if (p_panel != null)
        	p_panel.signalOK(p_panel.okMailUser, "ErrorMail", pass, false, error);

        if (pass) {
            log.info("OK: EMail = " + adminEMail);
			p_properties.setProperty(ADMIN_MAIL_OXP, adminEMail.toString());
			p_properties.setProperty(USUARIO_MAIL_OXP, mailUser);
			p_properties.setProperty(PASSWORD_MAIL_OXP, mailPassword);
        } else {
            log.warning(error);
        }

        p_properties.setProperty(ADMIN_MAIL_OXP, adminEMail.toString());

        return null;
    }		// testMail

    /**
     * Descripción de Método
     *
     *
     * @param mailServer
     * @param adminEMail
     * @param mailUser
     * @param mailPassword
     *
     * @return
     */
    private boolean testMailServer(InetAddress mailServer, InternetAddress adminEMail, String mailUser, String mailPassword) {

        boolean	smtpOK	= false;
        boolean	imapOK	= false;

        if (testPort(mailServer, 25, true)) {

            log.config("OK: SMTP Server contacted");
            smtpOK	= true;

        } else {
            log.info("SMTP Server NOT available");
        }

        //
        if (testPort(mailServer, 110, true)) {
            log.config("OK: POP3 Server contacted");
        } else {
            log.info("POP3 Server NOT available");
        }

        if (testPort(mailServer, 143, true)) {

            log.config("OK: IMAP4 Server contacted");
            imapOK	= true;

        } else {
            log.info("IMAP4 Server NOT available");
        }

        //
        if (!smtpOK) {

            String	error	= "No active Mail Server";
			if (p_panel != null)
				p_panel.signalOK(p_panel.okMailServer, "ErrorMailServer", false, false, error);
            log.warning(error);

            return false;
        }

        //
        try {

            EMail	em	= new EMail(mailServer.getHostName(), adminEMail.toString(), adminEMail.toString(), "OpenXpertya Server Setup Test", "Test: " + getProperties());

            em.createAuthenticator(mailUser, mailPassword);

            if (EMail.SENT_OK.equals(em.send())) {
                log.info("OK: Send Test Email to " + adminEMail);
            } else {
                log.warning("Could NOT send Email to " + adminEMail);
            }

        } catch (Exception ex) {

            log.severe(ex.getLocalizedMessage());

            return false;
        }

        //
        if (!imapOK) {
            return false;
        }

        // Test Read Mail Access
        Properties	props	= new Properties();

        props.put("mail.store.protocol", "smtp");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.host", mailServer.getHostName());
        props.put("mail.user", mailUser);
        props.put("mail.smtp.auth", "true");
        log.config("Connecting to " + mailServer.getHostName());

        //
        Session	session	= null;
        Store	store	= null;

        try {

            EMailAuthenticator	auth	= new EMailAuthenticator(mailUser, mailPassword);

            session	= Session.getDefaultInstance(props, auth);
            session.setDebug(CLogMgt.isLevelFinest());
            log.config("Session=" + session);

            // Connect to Store
            store	= session.getStore("imap");
            log.config("Store=" + store);

        } catch (NoSuchProviderException nsp) {

            log.warning("Mail IMAP Provider - " + nsp.getMessage());

            return false;

        } catch (Exception e) {

            log.warning("Mail IMAP - " + e.getMessage());

            return false;
        }

        try {

            store.connect(mailServer.getHostName(), mailUser, mailPassword);
            log.config("Store - connected");

            Folder	folder	= store.getDefaultFolder();
            Folder	inbox	= folder.getFolder("INBOX");

            log.info("OK: Mail Connect to " + inbox.getFullName() + " #Msg=" + inbox.getMessageCount());

            //
            store.close();

        } catch (MessagingException mex) {

            log.severe("Mail Connect " + mex.getMessage());

            return false;
        }

        return true;

    }		// testMailServer

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private String testOXP() {

        // OpenXpertya Home
        m_OXPHome	= new File(getOXPHome());

        boolean	pass	= m_OXPHome.exists();
        String	error	= "Not found: OXPHome = " + m_OXPHome;
        if (p_panel != null)
        	p_panel.signalOK(p_panel.okOXPHome, "ErrorOXPHome", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: OXPHome = " + m_OXPHome);
        p_properties.setProperty(OXP_HOME, m_OXPHome.getAbsolutePath());
        System.setProperty(OXP_HOME, m_OXPHome.getAbsolutePath());

        // KeyStore
        String	fileName	= KeyStoreMgt.getKeystoreFileName(m_OXPHome.getAbsolutePath());

        p_properties.setProperty(KEYSTORE_OXP, fileName);

        // KeyStore Password
        String	pw	= p_panel != null 
        	? new String(p_panel.fKeyStore.getPassword())
        	: (String) p_properties.get(KEYSTOREPASS_OXP);

        pass	= (pw != null) && (pw.length() > 0);
        error	= "Invalid Key Store Password = " + pw;
        if (p_panel != null)
        	p_panel.signalOK(p_panel.okKeyStore, "KeyStorePassword", pass, true, error);

        if (!pass) {
            return error;
        }

        p_properties.setProperty(KEYSTOREPASS_OXP, pw);

        KeyStoreMgt	ks	= p_panel != null 
        	? new KeyStoreMgt(fileName, p_panel.fKeyStore.getPassword())
        	: new KeyStoreMgt (fileName, pw.toCharArray());
		ks.setCommonName((String)p_properties.getProperty(OXP_CERT_CN));
		ks.setOrganization((String)p_properties.getProperty(OXP_CERT_ORG));
		ks.setOrganizationUnit((String)p_properties.getProperty(OXP_CERT_ORG_UNIT));
		ks.setLocation((String)p_properties.getProperty(OXP_CERT_LOCATION));
		ks.setState((String)p_properties.getProperty(OXP_CERT_STATE));
		ks.setCountry((String)p_properties.getProperty(OXP_CERT_COUNTRY));
        	
   		error = p_panel != null 
			? ks.verify((JFrame)SwingUtilities.getWindowAncestor(p_panel))
			: ks.verify(null);
        pass	= error == null;
        if (p_panel != null)
        	p_panel.signalOK(p_panel.okKeyStore, "KeyStorePassword", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: KeyStore = " + fileName);

        return null;
    }		// testOXP

    /**
     * Descripción de Método
     *
     *
     * @param host
     * @param port
     * @param shouldBeUsed
     *
     * @return
     */
    protected boolean testPort(InetAddress host, int port, boolean shouldBeUsed) {

        Socket	pingSocket	= null;

        try {
            pingSocket	= new Socket(host, port);
        } catch (Exception e) {

            if (shouldBeUsed) {
                log.severe("Open Socket " + host + ":" + port + " - " + e.getMessage());
            } else {
                log.fine(host + ":" + port + " - " + e.getMessage());
            }

            return false;
        }

        if (!shouldBeUsed) {
            log.severe("Open Socket " + host + ":" + port + " - " + pingSocket);
        }

        log.fine(host + ":" + port + " - " + pingSocket);

        if (pingSocket == null) {
            return false;
        }

        // success
        try {
            pingSocket.close();
        } catch (IOException e) {
            log.severe("close socket=" + e.toString());
        }

        return true;

    }		// testPort

    /**
     * Descripción de Método
     *
     *
     * @param protocol
     * @param server
     * @param port
     * @param file
     *
     * @return
     */
    protected boolean testPort(String protocol, String server, int port, String file) {

        URL	url	= null;

        try {
            url	= new URL(protocol, server, port, file);
        } catch (MalformedURLException ex) {

            log.severe("No URL for Protocol=" + protocol + ", Server=" + server + ": " + ex.getMessage());

            return false;
        }

        try {

            URLConnection	c	= url.openConnection();
            Object		o	= c.getContent();

            log.severe("In use=" + url);	// error

        } catch (Exception ex) {

            log.fine("Not used=" + url);	// ok

            return false;
        }

        return true;

    }		// testPort

    /**
     * Descripción de Método
     *
     *
     * @param port
     *
     * @return
     */
    protected boolean testServerPort(int port) {

        try {

            ServerSocket	ss	= new ServerSocket(port);

            log.fine(ss.getInetAddress() + ":" + ss.getLocalPort() + " - created");
            ss.close();

        } catch (Exception ex) {

            log.severe("Port " + port + ": " + ex.getMessage());

            return false;
        }

        return true;

    }		// testPort

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getAppsServer() {
        return p_panel != null 
        	? p_panel.fAppsServer.getText()
        	: (String)p_properties.get(SERVIDOR_APPS_OXP);
        
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getAppsServerDeployDir() {
        return p_panel != null 
        	? p_panel.fDeployDir.getText()
        	: (String) p_properties.get(DEPLOY_APPS_OXP);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getAppsServerJNPPort() {
    	String port = p_panel != null 
    		? p_panel.fJNPPort.getText()
    		: (String) p_properties.get(PUERTO_JNP_OXP);
    	
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            setAppsServerJNPPort("0");
        }

        return 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getAppsServerSSLPort() {
    	String port = p_panel != null
    		? p_panel.fSSLPort.getText()
    		: (String) p_properties.get(PUERTO_SSL_OXP);
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            setAppsServerSSLPort("0");
        }

        return 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getAppsServerType() {
        return p_panel != null
        	? (String) p_panel.fAppsType.getSelectedItem()
        	: (String) p_properties.get(TIPO_APPS_OXP);
    }		// setDatabaseType

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getAppsServerWebPort() {
    	String port = p_panel != null
    		? p_panel.fWebPort.getText()
    		: (String) p_properties.get(PUERTO_WEB_OXP);
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            setAppsServerWebPort("0");
        }

        return 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseDiscovered() {
        return (String) p_panel.fDatabaseDiscovered.getSelectedItem();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseName() {
        return p_panel != null
        	? p_panel.fDatabaseName.getText()
        	: (String) p_properties.get(NOMBRE_BD_OXP);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabasePassword() {

    	if (p_panel != null) {
	        char[]	pw	= p_panel.fDatabasePassword.getPassword();
	
	        if (pw != null) {
	            return new String(pw);
	        }
	        return "";
    	} else {
			String pw = (String)p_properties.get(PASSWD_BD_OXP);
			return (pw != null ? pw : "");
    	}
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public int getDatabasePort() {
    	String port = p_panel != null
    		? p_panel.fDatabasePort.getText()
    		: (String) p_properties.get(PUERTO_BD_OXP);
        try {
            return Integer.parseInt(port);
        } catch (Exception e) {
            setDatabasePort("0");
        }

        return 0;
    }		// getDatabasePort

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseServer() {
        return p_panel != null 
        	? p_panel.fDatabaseServer.getText()
        	: (String) p_properties.get(SERVIDOR_BD_OXP);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseSystemPassword() {

    	if (p_panel != null) {
	        char[]	pw	= p_panel.fSystemPassword.getPassword();
	
	        if (pw != null) {
	            return new String(pw);
	        }
	        return "";
    	} else {
			String pw = (String)p_properties.get(SYSTEM_BD_OXP);
			return (pw != null ? pw : "");
    	}
        
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseType() {
        return p_panel != null
        	? (String) p_panel.fDatabaseType.getSelectedItem()
        	: (String) p_properties.get(TIPO_BD_OXP);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getDatabaseUser() {
        return  p_panel != null
        	? p_panel.fDatabaseUser.getText()
        	: (String) p_properties.get(USUARIO_BD_OXP);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getJavaHome() {
        return  p_panel != null
        	? p_panel.fJavaHome.getText()
        	: (String) p_properties.get(JAVA_HOME);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getJavaType() {
        return p_panel != null
        	? (String) p_panel.fJavaType.getSelectedItem()
        	: (String) p_properties.get(JAVA_TYPE);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getKeyStore() {

    	
        char[]	pw	= p_panel.fKeyStore.getPassword();

        if (pw != null) {
            return new String(pw);
        }

        return "";
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String getOXPHome() {
        return p_panel != null
        	? p_panel.fOXPHome.getText()
        	: (String) p_properties.get(OXP_HOME);
    }		// getOXPHome

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    Properties getProperties() {
        return p_properties;
    }		// getProperties

    //~--- set methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param appsServer
     */
    public void setAppsServer(String appsServer) {
        if (p_panel != null) {
        	p_panel.fAppsServer.setText(appsServer);
        } else {
        	updateProperty(SERVIDOR_APPS_OXP, appsServer);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param enable
     */
    public void setAppsServerDeployDir(boolean enable) {
    	if (p_panel != null) {
	        p_panel.fDeployDir.setEnabled(enable);
	        p_panel.okDeployDir.setEnabled(enable);
	        p_panel.bDeployDir.setEnabled(enable);
    	}
    }

    /**
     * Descripción de Método
     *
     *
     * @param appsServerDeployDir
     */
    public void setAppsServerDeployDir(String appsServerDeployDir) {
        if (p_panel != null) {
        	p_panel.fDeployDir.setText(appsServerDeployDir);	
		} else {
			updateProperty(DEPLOY_APPS_OXP, appsServerDeployDir);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param enable
     */
    public void setAppsServerJNPPort(boolean enable) {
        if (p_panel != null) {
        	p_panel.fJNPPort.setEnabled(enable);		
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param appsServerJNPPort
     */
    public void setAppsServerJNPPort(String appsServerJNPPort) {
        if (p_panel != null) {
        	p_panel.fJNPPort.setText(appsServerJNPPort);	
		} else {
			updateProperty(PUERTO_JNP_OXP, appsServerJNPPort);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param enable
     */
    public void setAppsServerSSLPort(boolean enable) {
        if (p_panel != null) {
        	p_panel.fSSLPort.setEnabled(enable);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param appsServerSSLPort
     */
    public void setAppsServerSSLPort(String appsServerSSLPort) {
        if (p_panel != null) {
        	p_panel.fSSLPort.setText(appsServerSSLPort);			
		} else {
			updateProperty(PUERTO_SSL_OXP, appsServerSSLPort);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param appsType
     */
    public int setAppsServerType(String appsType) {

        int	index	= -1;

        for (int i = 0; i < APPSTYPE.length; i++) {

            if (APPSTYPE[i].equals(appsType)) {

                index	= i;

                break;
            }
        }

        if (index == -1) {

            index	= 0;
            log.warning("Invalid AppsType=" + appsType);
        }

		if (p_panel != null)
			p_panel.fAppsType.setSelectedIndex(index);
		else
			updateProperty(TIPO_APPS_OXP, appsType);
			
		return index;
    }		// setAppsServerType

    /**
     * Descripción de Método
     *
     *
     * @param enable
     */
    public void setAppsServerWebPort(boolean enable) {
        if (p_panel != null) {
        	p_panel.fWebPort.setEnabled(enable);			
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param appsServerWebPort
     */
    public void setAppsServerWebPort(String appsServerWebPort) {
        if (p_panel != null) {
        	p_panel.fWebPort.setText(appsServerWebPort);			
		} else {
			updateProperty(PUERTO_WEB_OXP, appsServerWebPort);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databaseDiscovered
     */
    public void setDatabaseDiscovered(String databaseDiscovered) {
        if (p_panel != null) {
        	p_panel.fDatabaseDiscovered.setSelectedItem(databaseDiscovered);		
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databaseName
     */
    public void setDatabaseName(String databaseName) {
        if (p_panel != null) {
        	p_panel.fDatabaseName.setText(databaseName);		
		} else {
			updateProperty(NOMBRE_BD_OXP, databaseName);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databasePassword
     */
    public void setDatabasePassword(String databasePassword) {
        if (p_panel != null) {
        	p_panel.fDatabasePassword.setText(databasePassword);			
		} else {
			updateProperty(PASSWD_BD_OXP, databasePassword);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databasePort
     */
    public void setDatabasePort(String databasePort) {
        if (p_panel != null) {
        	p_panel.fDatabasePort.setText(databasePort);		
		} else {
			updateProperty(PUERTO_BD_OXP, databasePort);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databaseServer
     */
    public void setDatabaseServer(String databaseServer) {
        if (p_panel != null) {
        	p_panel.fDatabaseServer.setText(databaseServer);			
		} else {
			updateProperty(SERVIDOR_BD_OXP, databaseServer);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databaseSystemPassword
     */
    public void setDatabaseSystemPassword(String databaseSystemPassword) {
        if (p_panel != null) {
        	p_panel.fSystemPassword.setText(databaseSystemPassword);		
		} else {
			updateProperty(SYSTEM_BD_OXP, databaseSystemPassword);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param databaseType
     */
    public int setDatabaseType(String databaseType) {

        int	index	= -1;

        for (int i = 0; i < DBTYPE.length; i++) {

            if (DBTYPE[i].equals(databaseType)) {

                index	= i;

                break;
            }
        }

        if (index == -1) {

            index	= 0;
            log.warning("Invalid DatabaseType=" + databaseType);
        }

        if (p_panel != null) {
            p_panel.fDatabaseType.setSelectedIndex(index);			
		} else {
			updateProperty(TIPO_BD_OXP, databaseType);
		}
        
        return index;
    }		// setDatabaseType

    /**
     * Descripción de Método
     *
     *
     * @param databaseUser
     */
    public void setDatabaseUser(String databaseUser) {
        if (p_panel != null) {
        	p_panel.fDatabaseUser.setText(databaseUser);    	
		} else {
			updateProperty(USUARIO_BD_OXP, databaseUser);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param javaHome
     */
    public void setJavaHome(String javaHome) {
		if (p_panel != null)
			p_panel.fJavaHome.setText(javaHome);
		else
			updateProperty(JAVA_HOME, javaHome);
    }

    /**
     * Descripción de Método
     *
     *
     * @param javaType
     */
    public int setJavaType(String javaType) {

        int	index	= -1;

        for (int i = 0; i < JAVATYPE.length; i++) {

            if (JAVATYPE[i].equals(javaType)) {

                index	= i;

                break;
            }
        }

        if (index == -1) {

            index	= 0;
            log.warning("Invalid JavaType=" + javaType);
        }

		if (p_panel != null)
			p_panel.fJavaType.setSelectedIndex(index);
		else
			updateProperty(JAVA_TYPE, javaType);
		
		return index;
    }		// setJavaType

    /**
     * Descripción de Método
     *
     *
     * @param password
     */
    public void setKeyStore(String password) {
		if (p_panel != null) {
			p_panel.fKeyStore.setText(password);
		} else {
			updateProperty(KEYSTOREPASS_OXP, password);
		}
    }

    /**
     * Descripción de Método
     *
     *
     * @param OXPHome
     */
    public void setOXPHome(String OXPHome) {
        if (p_panel != null) {
        	p_panel.fOXPHome.setText(OXPHome);	
		} else {
			updateProperty(OXP_HOME, OXPHome);
		}
    	
    }
}	// ConfigurationData



/*
 * @(#)ConfigurationData.java   11.jun 2007
 * 
 *  Fin del fichero ConfigurationData.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
