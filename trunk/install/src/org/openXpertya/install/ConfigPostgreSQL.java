/*
 * @(#)ConfigPostgreSQL.java   11.jun 2007  Versión 2.2
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

import org.openXpertya.db.DB_PostgreSQL;

//~--- Importaciones JDK ------------------------------------------------------

import java.net.InetAddress;

import java.sql.Connection;

/**
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class ConfigPostgreSQL extends Config {

    /** Descripción de Campo */
    private String[]	p_discovered	= null;

    /** Descripción de Campo */
    private DB_PostgreSQL	p_db	= new DB_PostgreSQL();

    /**
     * Constructor ...
     *
     *
     * @param data
     */
    public ConfigPostgreSQL(ConfigurationData data) {
        super(data);
    }		// ConfigSybase

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

        p_discovered	= new String[] {};

        return p_discovered;

    }		// discoveredDatabases

    /**
     * Descripción de Método
     *
     */
    public void init() {
        p_data.setDatabasePort(String.valueOf(DB_PostgreSQL.DEFAULT_PORT));
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
        if (getPanel() != null)
        	signalOK(getPanel().okDatabaseServer, "ErrorDatabaseServer", pass, true, error);
        log.info("OK: Database Server = " + databaseServer);
        setProperty(ConfigurationData.SERVIDOR_BD_OXP, databaseServer.getHostName());
        setProperty(ConfigurationData.TIPO_BD_OXP, p_data.getDatabaseType());

        // Database Port
        int	databasePort	= p_data.getDatabasePort();

        pass	= p_data.testPort(databaseServer, databasePort, true);
        error	= "DB Server Port = " + databasePort;
        if (getPanel() != null)
        	signalOK(getPanel().okDatabaseServer, "ErrorDatabasePort", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: Database Port = " + databasePort);
        setProperty(ConfigurationData.PUERTO_BD_OXP, String.valueOf(databasePort));

        // JDBC Database Info
        String	databaseName	= p_data.getDatabaseName();	// Service Name
        String	systemPassword	= p_data.getDatabaseSystemPassword();

        // URL (derived)   jdbc:sybase:Tds:prod1:5000/prod1
        String	urlSystem	= p_db.getConnectionURL(databaseServer.getHostName(), databasePort, p_db.getSystemDatabase(databaseName), p_db.getSystemUser());

        pass	= testJDBC(urlSystem, p_db.getSystemUser(), systemPassword);
        error	= "Error connecting: " + urlSystem + " - " + p_db.getSystemUser() + "/" + systemPassword;
        if (getPanel() != null)
        	signalOK(getPanel().okDatabaseSystem, "ErrorJDBC", pass, true, error);

        if (!pass) {
            return error;
        }

        log.info("OK: System Connection = " + urlSystem);
        setProperty(ConfigurationData.SYSTEM_BD_OXP, systemPassword);

        // Database User Info
        String	databaseUser		= p_data.getDatabaseUser();		// UID
        String	databasePassword	= p_data.getDatabasePassword();		// PWD

        pass	= (databasePassword != null) && (databasePassword.length() > 0);
        error	= "Invalid Database User Password";
        if (getPanel() != null)
        	signalOK(getPanel().okDatabaseUser, "ErrorJDBC", pass, true, error);

        if (!pass) {
            return error;
        }

        //
        String	url	= p_db.getConnectionURL(databaseServer.getHostName(), databasePort, databaseName, databaseUser);

        // Ignore result as it might not be imported
        pass	= testJDBC(url, databaseUser, databasePassword);
        error	= "Database imported? Cannot connect to User: " + databaseUser + "/" + databasePassword;
        if (getPanel() != null)
        	signalOK(getPanel().okDatabaseUser, "ErrorJDBC", pass, false, error);

        if (pass) {
            log.info("OK: Database User = " + databaseUser);
        } else {
            log.warning(error);
        }

        setProperty(ConfigurationData.URL_BD_OXP, url);
        setProperty(ConfigurationData.NOMBRE_BD_OXP, databaseName);
        setProperty(ConfigurationData.USUARIO_BD_OXP, databaseUser);
        setProperty(ConfigurationData.PASSWD_BD_OXP, databasePassword);

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

        try {
            Connection	conn	= p_db.getDriverConnection(url, uid, pwd);
        } catch (Exception e) {

            log.severe(e.toString());

            return false;
        }

        return true;

    }		// testJDBC
}	// ConfigPostgreSQL



/*
 * @(#)ConfigPostgreSQL.java   11.jun 2007
 * 
 *  Fin del fichero ConfigPostgreSQL.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
