/*
 * @(#)ConfigOracle.java   11.jun 2007  Versión 2.2
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

import oracle.jdbc.OracleDriver;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.net.InetAddress;

import java.sql.Connection;
import java.sql.DriverManager;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigOracle extends Config {

    /** Descripción de Campo */
    private static OracleDriver	s_driver	= null;

    /** Descripción de Campo */
    private String[]	p_discovered	= null;

    /**
     * Constructor ...
     *
     *
     * @param data
     */
    public ConfigOracle(ConfigurationData data) {
        super(data);
    }		// ConfigOracle

    /**
     * Descripción de Método
     *
     *
     * @param selected
     *
     * @return
     */
    public String[] discoverDatabases(String selected) {

        if (p_discovered != null) {
            return p_discovered;
        }

        //
        ArrayList	list	= new ArrayList();

        // default value to lowercase or null
        String	def	= selected;

        if ((def != null) && (def.trim().length() == 0)) {
            def	= null;
        }

        if (def != null) {
            list.add(def.toLowerCase());
        }

        // Search for Oracle Info
        String		path	= System.getProperty("java.library.path");
        String[]	entries	= path.split(File.pathSeparator);

        for (int e = 0; e < entries.length; e++) {

            String	entry	= entries[e].toLowerCase();

            if ((entry.indexOf("ora") != -1) && entry.endsWith("bin")) {

                StringBuffer	sb	= getTNS_File(entries[e].substring(0, entries[e].length() - 4));
                String[]	tnsnames	= getTNS_Names(sb);

                if (tnsnames != null) {

                    for (int i = 0; i < tnsnames.length; i++) {

                        String	tns	= tnsnames[i];		// is lower case

                        if (!tns.equals(def)) {
                            list.add(tns);
                        }
                    }

                    break;
                }
            }

        }							// for all path entries

        p_discovered	= new String[list.size()];
        list.toArray(p_discovered);

        return p_discovered;

    }		// discoverDatabases

    /**
     * Descripción de Método
     *
     */
    public void init() {
        p_data.setDatabasePort("1521");
    }		// init

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String test() {

        // Database Server
        String	server	= p_data.getDatabaseServer();
        boolean	pass	= (server != null) && (server.length() > 0) && (server.toLowerCase().indexOf("localhost") == -1) &&!server.equals("127.0.0.1");
        String		error		= "Not correct: DB Server = " + server;
        InetAddress	databaseServer	= null;

        try {

            if (pass) {
                databaseServer	= InetAddress.getByName(server);
            }

        } catch (Exception e) {

            error	+= " - " + e.getMessage();
            pass	= false;
        }

        signalOK(getPanel().okDatabaseServer, "ErrorDatabaseServer", pass, true, error);
        log.info("OK: Database Server = " + databaseServer);
        setProperty(ConfigurationData.SERVIDOR_BD_OXP, databaseServer.getHostName());
        setProperty(ConfigurationData.TIPO_BD_OXP, p_data.getDatabaseType());

        // Database Port
        int	databasePort	= p_data.getDatabasePort();

        pass	= p_data.testPort(databaseServer, databasePort, true);
        error	= "DB Server Port = " + databasePort;
        signalOK(getPanel().okDatabaseServer, "ErrorDatabasePort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: Database Port = " + databasePort);
        setProperty(ConfigurationData.PUERTO_BD_OXP, String.valueOf(databasePort));

        // JDBC Database Info
        String	databaseName	= p_data.getDatabaseName();	// Service Name
        String	systemPassword	= p_data.getDatabaseSystemPassword();

        pass	= (systemPassword != null) && (systemPassword.length() > 0);
        error	= "No Database System Password entered";
        signalOK(getPanel().okDatabaseSystem, "ErrorJDBC", pass, true, error);

        if (!pass) {
            return error;
        }

        //
        // URL (derived)   jdbc:oracle:thin:@//prod1:1521/prod1
        String	url	= "jdbc:oracle:thin:@//" + databaseServer.getHostName() + ":" + databasePort + "/" + databaseName;

        pass	= testJDBC(url, "system", systemPassword);
        error	= "Error connecting: " + url + " - as system/" + systemPassword;
        signalOK(getPanel().okDatabaseSystem, "ErrorJDBC", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: Connection = " + url);
        setProperty(ConfigurationData.URL_BD_OXP, url);
        log.info("OK: Database System User " + databaseName);
        setProperty(ConfigurationData.NOMBRE_BD_OXP, databaseName);
        setProperty(ConfigurationData.SYSTEM_BD_OXP, systemPassword);

        // Database User Info
        String	databaseUser		= p_data.getDatabaseUser();		// UID
        String	databasePassword	= p_data.getDatabasePassword();		// PWD

        pass	= (databasePassword != null) && (databasePassword.length() > 0);
        error	= "Invalid Database User Password";
        signalOK(getPanel().okDatabaseUser, "ErrorJDBC", pass, true, error);

        if (!pass) {
            return error;
        }

        // Ignore result as it might not be imported
        pass	= testJDBC(url, databaseUser, databasePassword);
        error	= "Database imported? Cannot connect to User: " + databaseUser + "/" + databasePassword;
        signalOK(getPanel().okDatabaseUser, "ErrorJDBC", pass, false, error);

        if (pass) {
            log.info("OK: Database User = " + databaseUser);
        } else {
            log.warning(error);
        }

        setProperty(ConfigurationData.USUARIO_BD_OXP, databaseUser);
        setProperty(ConfigurationData.PASSWD_BD_OXP, databasePassword);

        // TNS Name Info
        String	sqlplus	= "sqlplus system/" + systemPassword + "@" + databaseName + " @utils/oracle/Test.sql";

        log.config(sqlplus);
        pass	= testSQL(sqlplus);
        error	= "Error connecting via: " + sqlplus;
        signalOK(getPanel().okDatabaseSQL, "ErrorTNS", pass, true, error);

        if (pass) {
            log.info("OK: Database SQL Connection");
        }

        // OCI Test
        if (!System.getProperty("SkipOCI", "").equals("Y")) {

            url		= "jdbc:oracle:oci8:@" + databaseName;
            pass	= testJDBC(url, "system", systemPassword);

            if (pass) {
                log.info("OK: Connection = " + url);
            } else {
                log.warning("Cannot connect via Net8: " + url);
            }
        }

        log.warning("OCI Test Skipped");

        //
        return null;
    }		// test

    /**
     * Descripción de Método
     *
     *
     * @param url
     * @param uid
     * @param pwd
     *
     * @return
     */
    private boolean testJDBC(String url, String uid, String pwd) {

        log.fine("Url=" + url + ", UID=" + uid);

        try {

            if (s_driver == null) {

                s_driver	= new OracleDriver();
                DriverManager.registerDriver(s_driver);
            }

            Connection	con	= DriverManager.getConnection(url, uid, pwd);

        } catch (UnsatisfiedLinkError ule) {

            log.warning("Check [ORACLE_HOME]/jdbc/Readme.txt for (OCI) driver setup");
            log.warning(ule.toString());

        } catch (Exception e) {

            log.severe(e.toString());

            return false;
        }

        return true;

    }		// testJDBC

    /**
     * Descripción de Método
     *
     *
     * @param sqlplus
     *
     * @return
     */
    private boolean testSQL(String sqlplus) {

        StringBuffer	sbOut	= new StringBuffer();
        StringBuffer	sbErr	= new StringBuffer();
        int		result	= -1;

        try {

            Process	p	= Runtime.getRuntime().exec(sqlplus);
            InputStream	in	= p.getInputStream();
            int		c;

            while ((c = in.read()) != -1) {

                sbOut.append((char) c);
                System.out.print((char) c);
            }

            in.close();
            in	= p.getErrorStream();

            while ((c = in.read()) != -1) {
                sbErr.append((char) c);
            }

            in.close();

            // Get result
            try {

                Thread.yield();
                result	= p.exitValue();

            } catch (Exception e)	// Timing issue on Solaris.
            {

                Thread.sleep(200);	// .2 sec
                result	= p.exitValue();
            }

        } catch (Exception ex) {
            log.severe(ex.toString());
        }

        log.finer(sbOut.toString());

        if (sbErr.length() > 0) {
            log.warning(sbErr.toString());
        }

        return result == 0;

    }		// testSQL

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @param oraHome
     *
     * @return
     */
    private StringBuffer getTNS_File(String oraHome) {

        String	tnsnames	= oraHome + File.separator + "network" + File.separator + "admin" + File.separator + "tnsnames.ora";
        File	tnsfile	= new File(tnsnames);

        if (!tnsfile.exists()) {
            return null;
        }

        log.fine(tnsnames);

        StringBuffer	sb	= new StringBuffer();

        try {

            FileReader	fr	= new FileReader(tnsfile);
            int		c;

            while ((c = fr.read()) != -1) {
                sb.append((char) c);
            }

        } catch (IOException ex) {

            log.severe("Error Reading " + tnsnames);
            ex.printStackTrace();

            return null;
        }

        if (sb.length() == 0) {
            return null;
        }

        return sb;

    }		// getTNS_File

    /**
     * Descripción de Método
     *
     *
     * @param tnsnames
     *
     * @return
     */
    private String[] getTNS_Names(StringBuffer tnsnames) {

        if (tnsnames == null) {
            return null;
        }

        ArrayList	list	= new ArrayList();
        Pattern		pattern	= Pattern.compile("$", Pattern.MULTILINE);
        String[]	lines	= pattern.split(tnsnames);

        for (int i = 0; i < lines.length; i++) {

            String	line	= lines[i].trim();

            log.finest(i + ": " + line);

            if (false)									// get TNS Name
            {

                if ((line.length() > 0) && Character.isLetter(line.charAt(0))		// no # (
                        && (line.indexOf("=") != -1) && (line.indexOf("EXTPROC_") == -1) && (line.indexOf("_HTTP") == -1)) {

                    String	entry	= line.substring(0, line.indexOf('=')).trim().toLowerCase();

                    log.fine(entry);
                    list.add(entry);
                }

            } else	// search service names
            {

                if ((line.length() > 0) && (line.toUpperCase().indexOf("SERVICE_NAME") != -1)) {

                    String	entry	= line.substring(line.indexOf('=') + 1).trim().toLowerCase();
                    int	index	= entry.indexOf(')');

                    if (index != 0) {
                        entry	= entry.substring(0, index).trim();
                    }

                    log.fine(entry);
                    list.add(entry);
                }
            }
        }

        // Convert to Array
        if (list.size() == 0) {
            return null;
        }

        String[]	retValue	= new String[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getTNS_Names
}	// ConfigOracle



/*
 * @(#)ConfigOracle.java   11.jun 2007
 * 
 *  Fin del fichero ConfigOracle.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
