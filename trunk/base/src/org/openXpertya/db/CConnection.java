/*
 * @(#)CConnection.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import org.openXpertya.OpenXpertya;
import org.openXpertya.interfaces.Server;
import org.openXpertya.interfaces.ServerHome;
import org.openXpertya.interfaces.Status;
import org.openXpertya.interfaces.StatusHome;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogMgtLog4J;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.logging.Level;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

/**
 *  OpenXpertya Connection Descriptor
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Marek Mosiewicz<marek.mosiewicz@jotel.com.pl> - support for RMI over HTTP
 *  @version    $Id: CConnection.java,v 1.63 2005/05/17 05:30:16 jjanke Exp $
 */
public class CConnection implements Serializable {

    /** Connection */
    private static CConnection	s_cc	= null;

    /** Logger */
    protected static CLogger	log	= CLogger.getCLogger(CConnection.class);

    /** Name of Connection */
    private String	m_name	= "Libertya";

    /** Application Port */
    private int	m_apps_port	= 1099;

    /** Application Host */
    private String	m_apps_host	= "localhost";

    /** Database Type */
    private String	m_type	= "";

    /** Database Connection */
    private boolean	m_okDB	= false;

    /** Apps Server Connection */
    private boolean	m_okApps	= false;

    /** Info */
    private String[]	m_info	= new String[2];

    /** Firewall port */
    private int	m_fw_port	= 0;

    /** Firewall host */
    private String	m_fw_host	= "";

    /** Connection uses Firewall */
    private boolean	m_firewall	= false;

    /** DB User name */
    private String	m_db_uid	= "libertya";

    /** DB User password */
    private String	m_db_pwd	= "libertya";

    /** Database Port */
    private int	m_db_port	= 0;

    /** Database name */
    private String	m_db_name	= "libertya";

    /** Database Host */
    private String	m_db_host	= "localhost";

    /** ConnectionException */
    private Exception	m_dbException	= null;

    /** Database */
    private BaseDatosOXP	m_db	= null;

    /** In Memory connection */
    private boolean	m_bequeath	= false;

    /** Descripción de Campo */
    private Exception	m_appsException	= null;

    /** RMI over HTTP */
    private boolean	m_RMIoverHTTP	= false;

    /** Server Version */
    private String	m_version	= null;

    /** Server Session */
    private Server	m_server	= null;

    /** ********************************************************************* */
    private InitialContext	m_iContext	= null;

    /** Descripción de Campo */
    private Hashtable	m_env	= null;

    /** DataSource */
    private DataSource	m_ds	= null;

    /**
     *  OpenXpertya Connection
     */
    protected CConnection() {}		// CConnection
    
    // dREHER, para poder cerrar el logueo actual sin salir de la aplicacion
    public void setAppServerCredential()
	{
		m_iContext = null;
		m_env = null;
		m_server = null;
	}

    /**
     *  Convert Statement
     *  @param origStatement original statement (Oracle notation)
     *  @return converted Statement
     *  @throws Exception
     */
    public String convertStatement(String origStatement) throws Exception {

        // make sure we have a good database
        if ((m_db != null) &&!m_db.getName().equals(m_type)) {
            getDatabase();
        }

        if (m_db != null) {
            return m_db.convertStatement(origStatement);
        }

        throw new Exception("CConnection.convertStatement - No Converstion Database");
    }		// convertStatement

    /**
     *  Equals
     *  @param o object
     *  @return true if o equals this
     */
    public boolean equals(Object o) {

        if (o instanceof CConnection) {

            CConnection	cc	= (CConnection) o;

            if (cc.getAppsHost().equals(m_apps_host) && (cc.getAppsPort() == m_apps_port) && cc.getDbHost().equals(m_db_host) && (cc.getDbPort() == m_db_port) && (cc.isRMIoverHTTP() == m_RMIoverHTTP) && cc.getDbName().equals(m_db_name) && cc.getType().equals(m_type) && cc.getDbUid().equals(m_db_uid) && cc.getDbPwd().equals(m_db_pwd)) {
                return true;
            }
        }

        return false;

    }		// equals

    /**
     *  Hashcode
     *  @return hashcode of name
     */
    public int hashCode() {
        return m_name.hashCode();
    }		// hashCode

    /**
     *  Testing
     *  @param args ignored
     */
    public static void main(String[] args) {

        boolean	server	= true;

        if (args.length == 0) {
            System.out.println("CConnection <server|client>");
        } else {
            server	= "server".equals(args[0]);
        }

        System.out.println("CConnection - " + (server
                ? "server"
                : "client"));

        //
        if (server) {
            OpenXpertya.startup(false);
        } else {
            OpenXpertya.startup(true);
        }

        //
        System.out.println("Connection = ");

        // CConnection[name=localhost{fundesle.openxpertya.org},AppsHost=localhost,AppsPort=1099,type=Oracle,DBhost=dev,DBport=1521,DBname=openxp,BQ=false,FW=false,FWhost=,FWport=1630,UID=openxp,PWD=openxp]
        System.out.println(Ini.getProperty(Ini.P_CONNECTION));

        CConnection	cc	= CConnection.get();

        System.out.println(">> " + cc.toStringLong());

        Connection	con	= cc.getConnection(false, Connection.TRANSACTION_READ_COMMITTED);

        new CConnectionDialog(cc);

    }		// main

    /**
     *  Supports BLOB
     *  @return true if BLOB is supported
     */
    public boolean supportsBLOB() {
        return m_db.supportsBLOB();
    }		// supportsBLOB

    /**
     *  Test ApplicationServer
     *  @return Exception or null
     */
    public Exception testAppsServer() {

        if (setAppsServerInfo()) {
            testDatabase();
        }

        return getAppsServerException();

    }		// testAppsServer

    /**
     *  Test Database Connection.
     *  -- Example --
     *  Database: PostgreSQL - 7.1.3
     *  Driver:   PostgreSQL Native Driver - PostgreSQL 7.2 JDBC2
     *  -- Example --
     *  Database: Oracle - Oracle8i Enterprise Edition Release 8.1.7.0.0 - Production With the Partitioning option JServer Release 8.1.7.0.0 - Production
     *  Driver:   Oracle JDBC driver - 9.0.1.1.0
     *  @return Exception or null
     */
    public Exception testDatabase() {

        // At this point Application Server Connection is tested.
        if (isRMIoverHTTP()) {
            return null;
        }

        if (m_ds != null) {
            getDatabase().close();
        }

        m_ds	= null;
        setDataSource();

        // the actual test
        Connection	conn	= getConnection(true, Connection.TRANSACTION_READ_COMMITTED);

        if (conn != null) {

            try {

                DatabaseMetaData	dbmd	= conn.getMetaData();

                m_info[0]	= "Database=" + dbmd.getDatabaseProductName() + " - " + dbmd.getDatabaseProductVersion();
                m_info[0]	= m_info[0].replace('\n', ' ');
                m_info[1]	= "Driver  =" + dbmd.getDriverName() + " - " + dbmd.getDriverVersion();

                if (isDataSource()) {
                    m_info[1]	+= " - via DataSource";
                }

                m_info[1]	= m_info[1].replace('\n', ' ');
                log.config(m_info[0] + " - " + m_info[1]);
                conn.close();

            } catch (Exception e) {

                log.severe(e.toString());

                return e;
            }
        }

        return m_dbException;		// from opening
    }					// testDatabase

    /**
     *  Short String representation
     *  @return appsHost{dbHost-dbName-uid}
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer(m_apps_host);

        sb.append("{").append(m_db_host).append("-").append(m_db_name).append("-").append(m_db_uid).append("}");

        return sb.toString();

    }		// toString

    /**
     *      Detail Info
     *      @return info
     */
    public String toStringDetail() {

        StringBuffer	sb	= new StringBuffer(m_apps_host);

        sb.append("{").append(m_db_host).append("-").append(m_db_name).append("-").append(m_db_uid).append("}");

        //
        Connection	conn	= getConnection(true, Connection.TRANSACTION_READ_COMMITTED);

        if (conn != null) {

            try {

                DatabaseMetaData	dbmd	= conn.getMetaData();

                sb.append("\nDatabase=" + dbmd.getDatabaseProductName() + " - " + dbmd.getDatabaseProductVersion());
                sb.append("\nDriver  =" + dbmd.getDriverName() + " - " + dbmd.getDriverVersion());

                if (isDataSource()) {
                    sb.append(" - via DataSource");
                }

                conn.close();

            } catch (Exception e) {}
        }

        conn	= null;

        return sb.toString();

    }		// toString

    /**
     *  String representation.
     *  Used also for Instanciation
     *  @return string representation
     *      @see setAttributes(String) setAttributes
     */
    public String toStringLong() {

        StringBuffer	sb	= new StringBuffer("CConnection[");

        sb.append("name=").append(m_name).append(",AppsHost=").append(m_apps_host).append(",AppsPort=").append(m_apps_port).append(",RMIoverHTTP=").append(m_RMIoverHTTP).append(",type=").append(m_type).append(",DBhost=").append(m_db_host).append(",DBport=").append(m_db_port).append(",DBname=").append(m_db_name).append(",BQ=").append(m_bequeath).append(",FW=").append(m_firewall).append(",FWhost=").append(m_fw_host).append(",FWport=").append(m_fw_port).append(",UID=").append(m_db_uid).append(",PWD=").append(m_db_pwd);	// the format is read by setAttributes
        sb.append("]");

        return sb.toString();

    }		// toStringLong

    /**
     *  Update Connection Info from Apps Server
     *  @param svr Apps Server Status
     *  @throws Exception
     */
    private void updateInfoFromServer(Status svr) throws Exception {

        if (svr == null) {
            throw new IllegalArgumentException("AppsServer was NULL");
        }

        setType(svr.getDbType());
        setDbHost(svr.getDbHost());
        setDbPort(svr.getDbPort());
        setDbName(svr.getDbName());
        setDbUid(svr.getDbUid());
        setDbPwd(svr.getDbPwd());
        setBequeath(false);

        //
        setFwHost(svr.getFwHost());
        setFwPort(svr.getFwPort());

        if (getFwHost().length() == 0) {
            setViaFirewall(false);
        }

        m_version	= svr.getDateVersion();
        log.config("Server=" + getDbHost() + ", DB=" + getDbName());

    }		// update Info

    //~--- get methods --------------------------------------------------------

    /**
     *  Get/Set default client/server Connection
     *  @return Connection Descriptor
     */
    public static CConnection get() {

        if (s_cc == null) {

            String	attributes	= Ini.getProperty(Ini.P_CONNECTION);

            if ((attributes == null) || (attributes.length() == 0)) {

                CConnectionDialog	ccd	= new CConnectionDialog(new CConnection());

                s_cc	= ccd.getConnection();

                // set also in ALogin and Ctrl
                Ini.setProperty(Ini.P_CONNECTION, s_cc.toStringLong());
                Ini.saveProperties(Ini.isClient());

            } else {

                s_cc	= new CConnection();
                s_cc.setAttributes(attributes);
            }

            log.fine(s_cc.toString());
        }

        return s_cc;

    }		// get

    /**
     *  Get specific connection
     *  @param type database Type, e.g. Database.DB_ORACLE
     *  @param db_host db host
     *  @param db_port db port
     *  @param db_name db name
     *  @return connection
     */
    public static CConnection get(String type, String db_host, int db_port, String db_name) {
        return get(type, db_host, db_port, db_name, null, null);
    }		// get

    /**
     *  Get specific client connection
     *  @param type database Type, e.g. Database.DB_ORACLE
     *  @param db_host db host
     *  @param db_port db port
     *  @param db_name db name
     *  @param db_uid db user id
     *  @param db_pwd db user password
     *  @return connection
     */
    public static CConnection get(String type, String db_host, int db_port, String db_name, String db_uid, String db_pwd) {

        CConnection	cc	= new CConnection();

        cc.setAppsHost(db_host);	// set Apps=DB
        cc.setType(type);
        cc.setDbHost(db_host);
        cc.setDbPort(db_port);
        cc.setDbName(db_name);

        //
        if (db_uid != null) {
            cc.setDbUid(db_uid);
        }

        if (db_pwd != null) {
            cc.setDbPwd(db_pwd);
        }

        return cc;

    }		// get

    /**
     *  Get Application Host
     *  @return apps host
     */
    public String getAppsHost() {
        return m_apps_host;
    }

    /**
     * Get Apps Port
     * @return port
     */
    public int getAppsPort() {
        return m_apps_port;
    }

    /**
     *  Get Last Exception of Apps Server Connection attempt
     *  @return Exception or null
     */
    public Exception getAppsServerException() {
        return m_appsException;
    }		// getAppsServerException

    /**
     *  Create Connection - no not close.
     *      Sets m_dbException
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation Connection transaction level
     *  @return Connection
     */
    public Connection getConnection(boolean autoCommit, int transactionIsolation) {

        Connection	conn	= null;

        m_dbException	= null;
        m_okDB		= false;

        //
        getDatabase();		// updates m_db

        if (m_db == null) {

            m_dbException	= new IllegalStateException("No Database Connector");

            return null;
        }

        //

        try {

            // if (!Ini.isClient()                     //      Server
            // && trxLevel != Connection.TRANSACTION_READ_COMMITTED)           // PO_LOB.save()
            // {
            Exception	ee	= null;

            try {
                conn	= m_db.getCachedConnection(this, autoCommit, transactionIsolation);
            } catch (Exception e) {
                ee	= e;
            }

            if (conn == null) {

                Thread.yield();
                log.config("retrying - " + ee);
                conn	= m_db.getCachedConnection(this, autoCommit, transactionIsolation);
            }

            // System.err.println ("CConnection.getConnection(Cache) - " + getConnectionURL() + ", AutoCommit=" + autoCommit + ", TrxLevel=" + trxLevel);
            // }
            // else if (isDataSource())        //      Client
            // {
            // conn = m_ds.getConnection();
            // System.err.println ("CConnection.getConnection(DataSource) - " + getConnectionURL() + ", AutoCommit=" + autoCommit + ", TrxLevel=" + trxLevel);
            // }
            // else
            // {
            // conn = m_db.getDriverConnection (this);
            // System.err.println ("CConnection.getConnection(Driver) - " + getConnectionURL() + ", AutoCommit=" + autoCommit + ", TrxLevel=" + trxLevel);
            // }
            // Verify Connection
            if (conn != null) {

                if (conn.getTransactionIsolation() != transactionIsolation) {
                    conn.setTransactionIsolation(transactionIsolation);
                }

                if (conn.getAutoCommit() != autoCommit) {
                    conn.setAutoCommit(autoCommit);
                }

                m_okDB	= true;
            }
        } catch (UnsatisfiedLinkError ule) {

            String	msg	= ule.getLocalizedMessage() + " -> Did you set the LD_LIBRARY_PATH ? - " + getConnectionURL();

            m_dbException	= new Exception(msg);
            log.severe(msg);

        } catch (SQLException ex) {

            m_dbException	= ex;

            if (conn == null) {

                log.log(Level.SEVERE, getConnectionURL() + ", (1) AutoCommit=" + autoCommit + ",TrxIso=" + getTransactionIsolationInfo(transactionIsolation)

                // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                + " - " + ex.getMessage());

            } else {

                try {

                    log.severe(getConnectionURL() + ", (2) AutoCommit=" + conn.getAutoCommit() + "->" + autoCommit + ", TrxIso=" + getTransactionIsolationInfo(conn.getTransactionIsolation()) + "->" + getTransactionIsolationInfo(transactionIsolation)

                    // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                    + " - " + ex.getMessage());

                } catch (Exception ee) {

                    log.severe(getConnectionURL() + ", (3) AutoCommit=" + autoCommit + ", TrxIso=" + getTransactionIsolationInfo(transactionIsolation)

                    // + " (" + getDbUid() + "/" + getDbPwd() + ")"
                    + " - " + ex.getMessage());
                }
            }

        } catch (Exception ex) {

            m_dbException	= ex;
            log.log(Level.SEVERE, getConnectionURL(), ex);
        }

        // System.err.println ("CConnection.getConnection - " + conn);
        return conn;

    }		// getConnection

    /**
     *  Get Connection String
     *  @return connection string
     */
    public String getConnectionURL() {

        getDatabase();		// updates m_db

        if (m_db != null) {
            return m_db.getConnectionURL(this);
        } else {
            return "";
        }

    }		// getConnectionURL

    /**
     *  Get Server Connection
     *  @return DataSource
     */
    public DataSource getDataSource() {
        return m_ds;
    }		// getDataSource

    /**
     *  Get Database
     *  @return database
     */
    public BaseDatosOXP getDatabase() {

        // different driver
        if ((m_db != null) &&!m_db.getName().equals(m_type)) {
            m_db	= null;
        }

        if (m_db == null) {

            try {

                for (int i = 0; i < Database.DB_NAMES.length; i++) {

                    if (Database.DB_NAMES[i].equals(m_type)) {

                        m_db	= (BaseDatosOXP) Database.DB_CLASSES[i].newInstance();

                        break;
                    }
                }

            } catch (Exception e) {
                log.severe(e.toString());
            }
        }

        return m_db;
    }		// getDatabase

    /**
     *  Get Database Exception of last connection attempt
     *  @return Exception or null
     */
    public Exception getDatabaseException() {
        return m_dbException;
    }		// getConnectionException

    /**
     *  Get Database Host name
     *  @return db host name
     */
    public String getDbHost() {
        return m_db_host;
    }		// getDbHost

    /**
     *  Get Database Name (Service Name)
     *  @return db name
     */
    public String getDbName() {
        return m_db_name;
    }		// getDbName

    /**
     *      Get DB Port
     *      @return port
     */
    public int getDbPort() {
        return m_db_port;
    }		// getDbPort

    /**
     *  Get Database Password
     *  @return db password
     */
    public String getDbPwd() {
        return m_db_pwd;
    }		// getDbPwd

    /**
     *  Get Database User
     *  @return db user
     */
    public String getDbUid() {
        return m_db_uid;
    }		// getDbUid

    /**
     * Method getFwHost
     * @return String
     */
    public String getFwHost() {
        return m_fw_host;
    }

    /**
     * Get Firewall port
     * @return firewall port
     */
    public int getFwPort() {
        return m_fw_port;
    }

    /**
     *  Get Info.
     *  - Database, Driver, Status Info
     *  @return info
     */
    public String getInfo() {

        StringBuffer	sb	= new StringBuffer(m_info[0]);

        sb.append(" - ").append(m_info[1]).append("\n").append(getDatabase().toString()).append("\nAppsServerOK=").append(isAppsServerOK(false)).append(", DatabaseOK=").append(isDatabaseOK());

        return sb.toString();

    }		// getInfo

    /**
     *  Get Application Server Initial Context
     *  @param useCache if true, use existing cache
     *  @return Initial Context or null
     */
    public InitialContext getInitialContext(boolean useCache) {

        if (useCache && (m_iContext != null)) {
            return m_iContext;
        }

        // Set Environment
        if ((m_env == null) ||!useCache) {
            m_env	= getInitialEnvironment(getAppsHost(), getAppsPort(), isRMIoverHTTP());
        }

        String	connect	= (String) m_env.get(Context.PROVIDER_URL);

        Env.setContext(Env.getCtx(), Context.PROVIDER_URL, connect);

        // Get Context
        m_iContext	= null;

        try {
            m_iContext	= new InitialContext(m_env);
        } catch (Exception ex) {

            m_okApps		= false;
            m_appsException	= ex;

            if (connect == null) {
                connect	= (String) m_env.get(Context.PROVIDER_URL);
            }

            log.severe(connect + "\n - " + ex.toString() + "\n - " + m_env);

            if (CLogMgt.isLevelFinest()) {
                ex.printStackTrace();
            }
        }

        return m_iContext;

    }		// getInitialContext

    /**
     *      Get Initial Context
     *      @param env environment
     *      @return Initial Context
     */
    public static InitialContext getInitialContext(Hashtable env) {

        InitialContext	iContext	= null;

        try {
            iContext	= new InitialContext(env);
        } catch (Exception ex) {

            log.warning("CConection.getInitialContext - " + env.get(Context.PROVIDER_URL) + "\n - " + ex.toString() + "\n - " + env);
            iContext	= null;

            if (CLogMgt.isLevelFinest()) {
                ex.printStackTrace();
            }
        }

        return iContext;

    }		// getInitialContext

    /**
     *      Get Initial Environment
     *      @param AppsHost host
     *      @param AppsPort port
     *      @param RMIoverHTTP true if tunnel through HTTP
     *      @return environment
     */
    public static Hashtable getInitialEnvironment(String AppsHost, int AppsPort, boolean RMIoverHTTP) {

        // Set Environment
        Hashtable	env	= new Hashtable();
        String		connect	= AppsHost;

        if (RMIoverHTTP) {

            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.HttpNamingContextFactory");

            if (AppsHost.indexOf("://") == -1) {
                connect	= "http://" + AppsHost + ":" + AppsPort + "/invoker/JNDIFactory";
            }

            env.put(Context.PROVIDER_URL, connect);

        } else {

            env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");

            if (AppsHost.indexOf("://") == -1) {
                connect	= "jnp://" + AppsHost + ":" + AppsPort;
            }

            env.put(Context.PROVIDER_URL, connect);
        }

        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");

        // HTTP - default timeout 0
        env.put(org.jnp.interfaces.TimedSocketFactory.JNP_TIMEOUT, "5000");	// timeout in ms
        env.put(org.jnp.interfaces.TimedSocketFactory.JNP_SO_TIMEOUT, "5000");

        // JNP - default timeout 5 sec
        env.put(org.jnp.interfaces.NamingContext.JNP_DISCOVERY_TIMEOUT, "5000");

        return env;
    }		// getInitialEnvironment

    /**
     *  Get Name
     *  @return connection name
     */
    public String getName() {
        return m_name;
    }

    /**
     *      Get Server
     *      @return Server
     */
    public Server getServer() {

        if (m_server == null) {

            try {

            	// Eloy Gomez:
            	// Cambiamos el tipo de contexto inicial para elservidor de aplicaciones para evitar un
            	// bug con la cache del JDBC de PostgreSQL.
            	// TODO: Comprobar en futuras versiones que esto este solucionado
            	// para activarlo de nuevo.
            	//InitialContext	ic	= getInitialContext(true);
            	InitialContext	ic	= getInitialContext(false);

                if (ic != null) {

                    ServerHome	serverHome	= (ServerHome) ic.lookup(ServerHome.JNDI_NAME);

                    if (serverHome != null) {
                        m_server	= serverHome.create();
                    }
                }

            } catch (Exception ex) {
                log.log(Level.SEVERE, "", ex);
            }
        }

        return m_server;

    }		// getServer

    /**
     *  Get Server Connection - do close
     *  @param autoCommit true if autocommit connection
     *  @param trxLevel Connection transaction level
     *  @return Connection
     */
    public Connection getServerConnection(boolean autoCommit, int trxLevel) {

        Connection	conn	= null;

        // Server Connection
        if (m_ds != null) {

            try {

                conn	= m_ds.getConnection();
                conn.setAutoCommit(autoCommit);
                conn.setTransactionIsolation(trxLevel);
                m_okDB	= true;

            } catch (SQLException ex) {

                m_dbException	= ex;
                log.log(Level.SEVERE, "createServerConnection", ex);
            }
        }

        // Server
        return conn;

    }		// getServerConnection

    /**
     *  Get Apps Server Version
     *  @return db host name
     */
    public String getServerVersion() {
        return m_version;
    }		// getServerVersion

    /**
     *      Get Status Info
     *      @return info
     */
    public String getStatus() {

        StringBuffer	sb	= new StringBuffer(m_apps_host);

        sb.append("{").append(m_db_host).append("-").append(m_db_name).append("-").append(m_db_uid).append("}");

        if (m_db != null) {
            sb.append(m_db.getStatus());
        }

        return sb.toString();

    }		// getStatus

    /**
     *      Get Transaction Isolation Info
     *      @param transactionIsolation trx iso
     *      @return clear test
     */
    public static String getTransactionIsolationInfo(int transactionIsolation) {

        if (transactionIsolation == Connection.TRANSACTION_NONE) {
            return "NONE";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED) {
            return "READ_COMMITTED";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_UNCOMMITTED) {
            return "READ_UNCOMMITTED";
        }

        if (transactionIsolation == Connection.TRANSACTION_REPEATABLE_READ) {
            return "REPEATABLE_READ";
        }

        if (transactionIsolation == Connection.TRANSACTION_READ_COMMITTED) {
            return "SERIALIZABLE";
        }

        return "<?" + transactionIsolation + "?>";

    }		// getTransactionIsolationInfo

    /**
     *  Get Database Type
     *  @return database type
     */
    public String getType() {
        return m_type;
    }

    /**
     *  Is Application Server OK
     *  @param tryContactAgain try to contact again
     *  @return true if Apps Server exists
     */
    public boolean isAppsServerOK(boolean tryContactAgain) {

        if (!tryContactAgain) {
            return m_okApps;
        }

        // Get Context
        if (m_iContext == null) {

            getInitialContext(false);

            if (!m_okApps) {
                return false;
            }
        }

        // Contact it
        try {

            StatusHome	statusHome	= (StatusHome) m_iContext.lookup(StatusHome.JNDI_NAME);
            Status	status	= statusHome.create();

            m_version	= status.getDateVersion();
            status.remove();
            m_okApps	= true;

        } catch (Exception ce) {
            m_okApps	= false;
        } catch (Throwable t) {
            m_okApps	= false;
        }

        return m_okApps;

    }		// isAppsOK

    /**
     *  Is it a bequeath connection
     *  @return true if bequeath connection
     */
    public boolean isBequeath() {
        return m_bequeath;
    }

    /**
     *  Has Server Connection
     *  @return true if DataSource exists
     */
    public boolean isDataSource() {
        return m_ds != null;
    }		// isDataSource

    /**
     *  Is Database Connection OK
     *  @return true if database connection is OK
     */
    public boolean isDatabaseOK() {
        return m_okDB;
    }		// isDatabaseOK

    /**
     *  Is Oracle DB
     *  @return true if Oracle
     */
    public boolean isOracle() {
        return Database.DB_ORACLE.equals(m_type);
    }		// isOracle

    /**
     *  Is PostgreSQL DB
     *  @return true if PostgreSQL
     */
    public boolean isPostgreSQL() {
        return Database.DB_POSTGRESQL.equals(m_type);
    }		// isPostgreSQL

    /**
     *      RMI over HTTP
     *      @return true if RMI over HTTP
     */
    public boolean isRMIoverHTTP() {
        return m_RMIoverHTTP;
    }		// isRMIoverHTTP

    /**
     *  Is Sybase DB
     *  @return true if Sybase
     */
    public boolean isSybase() {
        return Database.DB_SYBASE.equals(m_type);
    }		// isSybase

    /**
     *  Is DB via Firewall
     *  @return true if via firewall
     */
    public boolean isViaFirewall() {
        return m_firewall;
    }

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Application Host
     *  @param apps_host apps host
     */
    public void setAppsHost(String apps_host) {

        m_apps_host	= apps_host;
        m_name		= toString();
        m_okApps	= false;
    }

    /**
     * Set Apps Port
     * @param apps_port apps port
     */
    public void setAppsPort(int apps_port) {

        m_apps_port	= apps_port;
        m_okApps	= false;
    }

    /**
     *      Set Apps Port
     *      @param apps_portString appd port as String
     */
    public void setAppsPort(String apps_portString) {

        try {

            if ((apps_portString == null) || (apps_portString.length() == 0)) {
                ;
            } else {
                setAppsPort(Integer.parseInt(apps_portString));
            }

        } catch (Exception e) {
            log.severe(e.toString());
        }

    }		// setAppsPort

    /**
     *  Set Application Server Status.
     *  update okApps
     *  @return true ik OK
     */
    private boolean setAppsServerInfo() {

        m_okApps	= false;
        m_appsException	= null;

        //
        getInitialContext(false);

        if (m_iContext == null) {
            return m_okApps;
        }

        // Prevent error trace
        CLogMgtLog4J.enable(false);

        try {

            StatusHome	statusHome	= (StatusHome) m_iContext.lookup(StatusHome.JNDI_NAME);
            Status	status	= statusHome.create();

            //
            updateInfoFromServer(status);

            //
            status.remove();
            m_okApps	= true;

        } catch (CommunicationException ce)	// not a "real" error
        {

            // m_appsException = ce;
            String	connect	= (String) m_env.get(Context.PROVIDER_URL);

            log.warning(connect + "\n - " + ce.toString() + "\n - " + m_env);
        } catch (Exception e) {

            m_appsException	= e;

            String	connect	= (String) m_env.get(Context.PROVIDER_URL);

            log.warning(connect + "\n - " + e.toString() + "\n - " + m_env);
        }

        CLogMgtLog4J.enable(true);

        return m_okApps;

    }		// setAppsServerInfo

    /**
     *  Set Attributes from String (pases toStringLong())
     *  @param attributes attributes
     */
    private void setAttributes(String attributes) {

        try {

            setName(attributes.substring(attributes.indexOf("name=") + 5, attributes.indexOf(",AppsHost=")));
            setAppsHost(attributes.substring(attributes.indexOf("AppsHost=") + 9, attributes.indexOf(",AppsPort=")));

            if (attributes.indexOf(",RMIoverHTTP=") > 0)	// new attribute, may not exist
            {

                setAppsPort(attributes.substring(attributes.indexOf("AppsPort=") + 9, attributes.indexOf(",RMIoverHTTP=")));
                setRMIoverHTTP(attributes.substring(attributes.indexOf("RMIoverHTTP=") + 12, attributes.indexOf(",type=")));

            } else {
                setAppsPort(attributes.substring(attributes.indexOf("AppsPort=") + 9, attributes.indexOf(",type=")));
            }

            //
            setType(attributes.substring(attributes.indexOf("type=") + 5, attributes.indexOf(",DBhost=")));
            setDbHost(attributes.substring(attributes.indexOf("DBhost=") + 7, attributes.indexOf(",DBport=")));
            setDbPort(attributes.substring(attributes.indexOf("DBport=") + 7, attributes.indexOf(",DBname=")));
            setDbName(attributes.substring(attributes.indexOf("DBname=") + 7, attributes.indexOf(",BQ=")));

            //
            setBequeath(attributes.substring(attributes.indexOf("BQ=") + 3, attributes.indexOf(",FW=")));
            setViaFirewall(attributes.substring(attributes.indexOf("FW=") + 3, attributes.indexOf(",FWhost=")));
            setFwHost(attributes.substring(attributes.indexOf("FWhost=") + 7, attributes.indexOf(",FWport=")));
            setFwPort(attributes.substring(attributes.indexOf("FWport=") + 7, attributes.indexOf(",UID=")));

            //
            setDbUid(attributes.substring(attributes.indexOf("UID=") + 4, attributes.indexOf(",PWD=")));
            setDbPwd(attributes.substring(attributes.indexOf("PWD=") + 4, attributes.indexOf("]")));

            //

        } catch (Exception e) {
            log.severe(attributes + " - " + e.toString());
        }

    }		// setAttributes

    /**
     * Set Bequeath
     * @param bequeath bequeath connection
     */
    public void setBequeath(boolean bequeath) {

        m_bequeath	= bequeath;
        m_okDB		= false;
    }

    /**
     * Set Bequeath
     * @param bequeathString bequeath connection as String (true/false)
     */
    public void setBequeath(String bequeathString) {

        try {
            setBequeath(Boolean.valueOf(bequeathString).booleanValue());
        } catch (Exception e) {
            log.severe(e.toString());
        }

    }		// setBequeath

    /**
     *  Create DB Connection
     * @return data source != null
     */
    public boolean setDataSource() {

        // System.out.println ("CConnection.setDataSource - " + m_ds + " - Client=" + Ini.isClient());
        if ((m_ds == null) && Ini.isClient()) {

            if (getDatabase() != null) {	// no db selected
                m_ds	= getDatabase().getDataSource(this);
            }

            // System.out.println ("CConnection.setDataSource - " + m_ds);
        }

        return m_ds != null;
    }		// setDataSource

    /**
     *      Set Data Source
     *      @param ds data source
     *      @return data source != null
     */
    public boolean setDataSource(DataSource ds) {

        if ((ds == null) && (m_ds != null)) {
            getDatabase().close();
        }

        m_ds	= ds;

        return m_ds != null;

    }		// setDataSource

    /**
     *  Set Database host name
     *  @param db_host db host
     */
    public void setDbHost(String db_host) {

        m_db_host	= db_host;
        m_name		= toString();
        m_okDB		= false;

    }		// setDbHost

    /**
     *  Set Database Name (Service Name)
     *  @param db_name db name
     */
    public void setDbName(String db_name) {

        m_db_name	= db_name;
        m_name		= toString();
        m_okDB		= false;

    }		// setDbName

    /**
     * Set DB Port
     * @param db_port db port
     */
    public void setDbPort(int db_port) {

        m_db_port	= db_port;
        m_okDB		= false;

    }		// setDbPort

    /**
     * Set DB Port
     * @param db_portString db port as String
     */
    public void setDbPort(String db_portString) {

        try {

            if ((db_portString == null) || (db_portString.length() == 0)) {
                ;
            } else {
                setDbPort(Integer.parseInt(db_portString));
            }

        } catch (Exception e) {
            log.severe(e.toString());
        }

    }		// setDbPort

    /**
     *  Set DB password
     *  @param db_pwd db user password
     */
    public void setDbPwd(String db_pwd) {

        m_db_pwd	= db_pwd;
        m_okDB		= false;

    }		// setDbPwd

    /**
     *  Set Database User
     *  @param db_uid db user id
     */
    public void setDbUid(String db_uid) {

        m_db_uid	= db_uid;
        m_name		= toString();
        m_okDB		= false;

    }		// setDbUid

    /**
     * Method setFwHost
     * @param fw_host String
     */
    public void setFwHost(String fw_host) {

        m_fw_host	= fw_host;
        m_okDB		= false;
    }

    /**
     * Set Firewall port
     * @param fw_port firewall port
     */
    public void setFwPort(int fw_port) {

        m_fw_port	= fw_port;
        m_okDB		= false;
    }

    /**
     * Set Firewall port
     * @param fw_portString firewall port as String
     */
    public void setFwPort(String fw_portString) {

        try {

            if ((fw_portString == null) || (fw_portString.length() == 0)) {
                ;
            } else {
                setFwPort(Integer.parseInt(fw_portString));
            }

        } catch (Exception e) {
            log.severe(e.toString());
        }
    }

    /**
     *  Set Name
     */
    protected void setName() {
        m_name	= toString();
    }		// setName

    /**
     *  Set Name
     *  @param name connection name
     */
    public void setName(String name) {
        m_name	= name;
    }		// setName

    /**
     *      Set RMI over HTTP
     *      @param RMIoverHTTP HTTP tunnel
     */
    public void setRMIoverHTTP(boolean RMIoverHTTP) {
        m_RMIoverHTTP	= RMIoverHTTP;
    }		// setRMIoverHTTP

    /**
     *      Set RMI over HTTP
     *      @param RMIoverHTTP HTTP tunnel
     */
    public void setRMIoverHTTP(String RMIoverHTTP) {

        try {
            setRMIoverHTTP(Boolean.valueOf(RMIoverHTTP).booleanValue());
        } catch (Exception e) {
            log.severe(e.toString());
        }

    }		// setRMIoverHTTP

    /**
     *  Set Database Type and default settings.
     *  Checked against installed databases
     *  @param type database Type, e.g. Database.DB_ORACLE
     */
    public void setType(String type) {

        for (int i = 0; i < Database.DB_NAMES.length; i++) {

            if (Database.DB_NAMES[i].equals(type)) {

                m_type	= type;
                m_okDB	= false;

                break;
            }
        }

        // Oracle
        if (isOracle()) {

            if (getDbPort() != DB_Oracle.DEFAULT_PORT) {
                setDbPort(DB_Oracle.DEFAULT_PORT);
            }

            setFwPort(DB_Oracle.DEFAULT_CM_PORT);

        } else {

            setBequeath(false);
            setViaFirewall(false);
        }

        // PostgreSQL
        if (isPostgreSQL()) {

            if (getDbPort() != DB_PostgreSQL.DEFAULT_PORT) {
                setDbPort(DB_PostgreSQL.DEFAULT_PORT);
            }

        } else if (isSybase()) {

            if (getDbPort() != DB_Sybase.DEFAULT_PORT) {
                setDbPort(DB_Sybase.DEFAULT_PORT);
            }
        }

    }		// setType

    /**
     * Method setViaFirewall
     * @param viaFirewall boolean
     */
    public void setViaFirewall(boolean viaFirewall) {

        m_firewall	= viaFirewall;
        m_okDB		= false;
    }

    /**
     * Method setViaFirewall
     * @param viaFirewallString String
     */
    public void setViaFirewall(String viaFirewallString) {

        try {
            setViaFirewall(Boolean.valueOf(viaFirewallString).booleanValue());
        } catch (Exception e) {
            log.severe(e.toString());
        }
    }
}	// CConnection



/*
 * @(#)CConnection.java   02.jul 2007
 * 
 *  Fin del fichero CConnection.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
