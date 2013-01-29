/*
 * @(#)DB_Sybase.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.dbPort.Convert;
import org.openXpertya.util.CCachedRowSet;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import javax.sql.DataSource;
import javax.sql.RowSet;

/**
 *      Sybase Database Port
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: DB_Sybase.java,v 1.9 2005/03/11 20:29:01 jjanke Exp $
 */
public class DB_Sybase implements BaseDatosOXP {

    /** What driver to use - Sybase or jTDS - requires change in tools/build.xml */
    private static final boolean	JTDS	= true;

    /** Drver */
    private static Driver	s_driver	= null;

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(DB_Sybase.class);

    /** Descripción de Campo */
    private static final String[]	MONTHS	= new String[] {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    /** Default Port */
    public static final int	DEFAULT_PORT	= 5000;

    /** Data Source */
    private DataSource	m_ds	= null;

    /** Cached Database Name */
    private String	m_dbName	= null;

    /** Statement Converter */
    private Convert	m_convert	= new Convert(Database.DB_SYBASE);

    /** Connection String */
    private String	m_connectionURL;

    /**
     *      DB Sybase Port
     */
    public DB_Sybase() {

        try {
            getDriver();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }

    }		// DB_Sybase

    /**
     *  Create SQL for formatted Date, Number
     *
     *  @param  columnName  the column name in the SQL
     *  @param  displayType Display Type
     *  @param  AD_Language 6 character language setting (from Env.LANG_*)
     *
     *  @return TRIM(TO_CHAR(columnName,'9G999G990D00','NLS_NUMERIC_CHARACTERS='',.'''))
     *      or TRIM(TO_CHAR(columnName,'TM9')) depending on DisplayType and Language
     *  @see org.openXpertya.util.DisplayType
     *  @see org.openXpertya.util.Env
     *
     */
    public String TO_CHAR(String columnName, int displayType, String AD_Language) {
        return "";
    }		// TO_CHAR

    /**
     *  Create SQL TO Date String from Timestamp
     *
     *  @param  time Date to be converted
     *  @param  dayOnly true if time set to 00:00:00
     *  @return date function
     */
    public String TO_DATE(Timestamp time, boolean dayOnly) {

        if (time == null) {

            if (dayOnly) {
                return "convert(date,getdate())";
            }

            return "getdate()";
        }

        GregorianCalendar	cal	= new GregorianCalendar();

        cal.setTime(time);

        //
        StringBuffer	dateString	= new StringBuffer("convert(datetime,'");

        // yyyy.mm.dd      - format 2 p.411
        if (dayOnly) {

            int		yyyy	= cal.get(Calendar.YEAR);
            String	format	= "102";	// "SQL Standard" format

            if (yyyy < 100) {
                format	= "2";
            }

            dateString.append(yyyy).append(".").append(getXX(cal.get(Calendar.MONTH) + 1)).append(".").append(getXX(cal.get(Calendar.DAY_OF_MONTH))).append("',").append(format).append(")");
        }

        // mon dd yyy hh:mi:ss - format 116
        else {

            int		yyyy	= cal.get(Calendar.YEAR);
            String	format	= "116";	// n/a format

            if (yyyy < 100) {
                format	= "16";
            }

            dateString.append(MONTHS[cal.get(Calendar.MONTH)]).append(" ").append(getXX(cal.get(Calendar.DAY_OF_MONTH))).append(" ").append(getXX(cal.get(Calendar.YEAR))).append(" ").append(getXX(cal.get(Calendar.HOUR_OF_DAY))).append(":").append(getXX(cal.get(Calendar.MINUTE))).append(":").append(getXX(cal.get(Calendar.SECOND))).append("',").append(format).append(")");
        }

        return dateString.toString();

    }		// TO_DATE

    /**
     *      Return number as string for INSERT statements with correct precision
     *      @param number number
     *      @param displayType display Type
     *      @return number as string
     */
    public String TO_NUMBER(BigDecimal number, int displayType) {

        if (number == null) {
            return "NULL";
        }

        BigDecimal	result	= number;
        int		scale	= DisplayType.getDefaultPrecision(displayType);

        if (number.scale() > scale) {

            try {
                result	= number.setScale(scale, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
                log.severe("Number=" + number + ", Scale=" + " - " + e.getMessage());
            }
        }

        return result.toString();

    }		// TO_NUMBER

    /**
     *      Close
     */
    public void close() {
        m_ds	= null;
    }		// close

    /**
     *      Convert Oracle style Statement
     *      @param oraStatement oracle style statement
     *      @return statement
     */
    public String convertStatement(String oraStatement) {

        String	retValue[]	= m_convert.convert(oraStatement);

        if (retValue == null) {
            throw new IllegalArgumentException("Not Converted (" + oraStatement + ") - " + m_convert.getConversionError());
        }

        if (retValue.length != 1) {
            throw new IllegalArgumentException("Convert Command Number=" + retValue.length + " (" + oraStatement + ") - " + m_convert.getConversionError());
        }

        // Diagnostics (show changed, but not if AD_Error
        if (!oraStatement.equals(retValue[0]) && (retValue[0].indexOf("AD_Error") == -1)) {
            log.finest("=>" + retValue[0] + "<= [" + oraStatement + "]");
        }

        //
        return retValue[0];

    }		// convertStatement

    /**
     *      Test
     *      @param args ignored
     */
    public static void main(String[] args) {

        DB_Sybase	sybase	= new DB_Sybase();

        //
        String	databaseName	= "openxp";
        String	uid		= "openxp";
        String	pwd		= "openxp";
        String	jdbcURL		= sybase.getConnectionURL("openxp", DEFAULT_PORT, databaseName, uid);

        System.out.println(jdbcURL);

        try {

            sybase.getDriver();

            Connection	conn	= DriverManager.getConnection(jdbcURL, uid, pwd);
            RowSet	rs	= CCachedRowSet.getRowSet("SELECT * FROM AD_Client", conn);

            //
            conn.close();
            conn	= null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }		// main

    /**
     *      Supports BLOB
     *      @return true
     */
    public boolean supportsBLOB() {
        return true;
    }		// supportsBLOB

    /**
     *  String Representation
     *  @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("DB_Sybase[");

        sb.append(m_connectionURL);
        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Cached Connection
     *      @param connection connection
     *      @param autoCommit auto commit
     *      @param transactionIsolation trx isolation
     *      @return Connection
     *      @throws Exception
     */
    public Connection getCachedConnection(CConnection connection, boolean autoCommit, int transactionIsolation) throws Exception {

        if (m_ds == null) {
            getDataSource(connection);
        }

        //
        Connection	conn	= m_ds.getConnection();

        // Connection conn = getDriverConnection(connection);
        //
        conn.setAutoCommit(autoCommit);
        conn.setTransactionIsolation(transactionIsolation);

        return conn;

    }		// getCachedConnection

    /**
     *      Get JDBC Catalog
     *      @return catalog (database name)
     */
    public String getCatalog() {

        if (m_dbName != null) {
            return m_dbName;
        }

        log.severe("Database Name not set (yet) - call getConnectionURL first");

        return null;

    }		// getCatalog

    /**
     *      Get SQL Commands.
     *      The following variables are resolved:
     *      @SystemPassword@, @UsuarioOXP@, @OXPPassword@
     *      @SystemPassword@, @DatabaseName@, @DatabaseDevice@
     *      @param cmdType CMD_
     *      @return array of commands to be executed
     */
    public String[] getCommands(int cmdType) {

        if (CMD_CREATE_USER == cmdType) {
            return new String[] {};
        }

        //
        if (CMD_CREATE_DATABASE == cmdType) {
            return new String[] { "CREATE database @DatabaseName@ on @DatabaseDevice@ = 200", "sp_configure \"enable java\", 1" };
        }

        //
        if (CMD_DROP_DATABASE == cmdType) {
            return new String[] { "DROP database @DatabaseName@" };
        }

        //
        return null;

    }		// getCommands

    /**
     *      Get Connection URL
     *      @param connection connection
     *      @return url
     */
    public String getConnectionURL(CConnection connection) {

        StringBuffer	sb	= null;

        if (JTDS) {
            sb	= new StringBuffer("jdbc:jtds:sybase://");
        } else {
            sb	= new StringBuffer("jdbc:sybase:Tds:");
        }

        //
        sb.append(connection.getDbHost()).append(":").append(connection.getDbPort()).append("/").append(connection.getDbName());

        // optional parameters via ?...
        m_connectionURL	= sb.toString();
        m_dbName	= connection.getDbName();

        return m_connectionURL;

    }		// getConnectionURL

    /**
     *      Get Connection URL.
     *      Mainly used for connection test
     *      @param dbHost db Host
     *      @param dbPort db Port
     *      @param dbName db Name (optional)
     *      @param userName user name (ignored)
     *      @return connection url
     */
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {

        StringBuffer	sb	= null;

        if (JTDS) {
            sb	= new StringBuffer("jdbc:jtds:sybase://");
        } else {
            sb	= new StringBuffer("jdbc:sybase:Tds:");
        }

        sb.append(dbHost).append(":").append(dbPort);

        //
        if ((dbName != null) && (dbName.length() > 0)) {

            m_dbName	= dbName;
            sb.append("/").append(dbName);
        }

        return sb.toString();

    }		// getConnectionURL

    /**
     *      Get Data Source
     *      @param connection connection
     *      @return n/a
     */
    public DataSource getDataSource(CConnection connection) {

        if (m_ds != null) {
            return m_ds;
        }

        if (JTDS) {

            net.sourceforge.jtds.jdbcx.JtdsDataSource	ds	= new net.sourceforge.jtds.jdbcx.JtdsDataSource();

            ds.setServerType(net.sourceforge.jtds.jdbc.Driver.SYBASE);
            ds.setTds("5.0");
            ds.setServerName(connection.getDbHost());
            ds.setPortNumber(connection.getDbPort());
            ds.setDatabaseName(connection.getDbName());

            //
            ds.setUser(connection.getDbUid());
            ds.setPassword(connection.getDbPwd());
            m_ds	= ds;

        } else {

            com.sybase.jdbc3.jdbc.SybDataSource	ds	= new com.sybase.jdbc3.jdbc.SybDataSource();

            ds.setServerName(connection.getDbHost());
            ds.setPortNumber(connection.getDbPort());
            ds.setDatabaseName(connection.getDbName());
            ds.setDataSourceName("SybaseDS");

            //
            ds.setUser(connection.getDbUid());
            ds.setPassword(connection.getDbPwd());
            m_ds	= ds;
        }

        // m_ds.setLoginTimeout(10);
        //
        return m_ds;

    }		// getDataSource

    /**
     *      Get Description
     *      @return info
     */
    public String getDescription() {

        return s_driver.toString();

        // s_driver.getMajorVersion() + " - " + s_driver.getMinorVersion();

    }		// getDrescription

    /**
     *      Get Driver
     *      @return driver
     *      @throws SQLException
     */
    public Driver getDriver() throws SQLException {

        if (s_driver == null) {

            if (JTDS) {
                s_driver	= new net.sourceforge.jtds.jdbc.Driver();
            } else {
                s_driver	= new com.sybase.jdbc3.jdbc.SybDriver();
            }

            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(Database.CONNECTION_TIMEOUT);
        }

        return s_driver;

    }		// getDriver

    /**
     *      Get Driver Connection
     *      @param connection connection info
     *      @return new connection
     *      @throws SQLException
     */
    public Connection getDriverConnection(CConnection connection) throws SQLException {

        getDriver();

        return DriverManager.getConnection(getConnectionURL(connection), connection.getDbUid(), connection.getDbPwd());

    }		// getDiverConnection

    /**
     *      Get Driver Connection
     *      @param dbUrl URL
     *      @param dbUid user
     *      @param dbPwd password
     *      @return connection
     *      @throws SQLException
     */
    public Connection getDriverConnection(String dbUrl, String dbUid, String dbPwd) throws SQLException {

        getDriver();

        return DriverManager.getConnection(dbUrl, dbUid, dbPwd);

    }		// getDriverConnection

    /**
     *  Get Database Name
     *  @return database short name
     */
    public String getName() {
        return Database.DB_SYBASE;
    }		// getName

    /**
     *      Get JDBC Schema
     *      @return schema (dbo)
     */
    public String getSchema() {
        return "dbo";
    }		// getSchema

    /**
     *      Get Standard Port
     *      @return port
     */
    public int getStandardPort() {
        return DEFAULT_PORT;
    }		// getStndardPort

    /**
     *      Get Status
     *      @return status info
     */
    public String getStatus() {

        StringBuffer	sb	= new StringBuffer("Status");

        return sb.toString();

    }		// getStatus

    /**
     *  Get Name of System Database
     *  @param databaseName database Name ignored
     *  @return e.g. master or database Name
     */
    public String getSystemDatabase(String databaseName) {
        return "master";
    }		// getSystemDatabase

    /**
     *  Get Name of System User
     *  @return e.g. sa, system
     */
    public String getSystemUser() {
        return "sa";
    }		// getSystemUser

    /**
     *      Get integer as two string digits (leading zero)
     *      @param x integer
     *      @return string of x
     */
    private String getXX(int x) {

        if (x < 10) {
            return "0" + x;
        }

        return String.valueOf(x);

    }		// getXX

	public String getErrorMsg(SQLException exception) {
		// Not implemented yet!
		return "";
	}

	public String TO_DATEFORMAT(String columnName, int displayType, String AD_Language) {
		StringBuffer	retValue	= new StringBuffer("TO_CHAR (");
        retValue.append(columnName);
        retValue.append(" ,'DD/MM/YYYY')");

        return retValue.toString();
	}

	@Override
	public String addPagingSQL(String sql, int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPagingSupported() {
		// TODO Auto-generated method stub
		return false;
	}	 
    
    
}	// DB_Sybase



/*
 * @(#)DB_Sybase.java   02.jul 2007
 * 
 *  Fin del fichero DB_Sybase.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
