/*
 * @(#)DB_PostgreSQL.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.dbPort.*;
import org.openXpertya.util.*;
import org.postgresql.ds.PGSimpleDataSource;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.*;

import java.sql.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.sql.*;

import javax.swing.JOptionPane;

/**
 *  PostgreSQL Database Port
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *      @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke, Victor PÃ¨rez
 *  @version    $Id: DB_PostgreSQL.java,v 1.23 2005/03/11 20:29:01 jjanke Exp $
 */
public class DB_PostgreSQL implements BaseDatosOXP {

    /** Default Port */
    public static final int	DEFAULT_PORT	= 5432;

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(DB_PostgreSQL.class);

    /** Driver */
    private org.postgresql.Driver	s_driver	= null;

    /** Data Source */
    private PGSimpleDataSource	m_ds	= null;

    /** Cached Database Name */
    private String	m_dbName	= null;

    /** Statement Converter */
    private Convert	m_convert	= new Convert(Database.DB_POSTGRESQL);

    /** Connection String */
    private String	m_connection;
    
    /** Error messages Map */
    private static Map<String,String> errorMsgs;
    
    static {
    	errorMsgs = new HashMap<String,String>();
    	errorMsgs.put("23503","ReferencedRecordError");
    	errorMsgs.put("22003","NumericFieldOverflow");
    	errorMsgs.put("22001","TextFieldOverflowError");
    	errorMsgs.put("23505","DuplicateKeyError");
    }

    /**
     *  PostgreSQL Database
     */
    public DB_PostgreSQL() {}		// DB_PostgreSQL

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
    	StringBuffer retValue = new StringBuffer();
    	if(columnName.equalsIgnoreCase("c_invoice.grandtotal")){
    		retValue.append("'Tot$:' ||");
    	}
    	
        retValue.append("CAST (");

        retValue.append(columnName);
        retValue.append(" AS Text)");

        // Numbers

        /*
         * if (DisplayType.isNumeric(displayType))
         * {
         *       if (displayType == DisplayType.Amount)
         *               retValue.append(" AS TEXT");
         *       else
         *               retValue.append(" AS TEXT");
         *       //if (!Language.isDecimalPoint(AD_Language))      //  reversed
         *       //retValue.append(",'NLS_NUMERIC_CHARACTERS='',.'''");
         * }
         * else if (DisplayType.isDate(displayType))
         * {
         *       retValue.append(",'")
         *               .append(Language.getLanguage(AD_Language).getDBdatePattern())
         *               .append("'");
         * }
         * retValue.append(")");
         * //
         */
        return retValue.toString();

    }		// TO_CHAR

    /**
     *  Create SQL TO Date String from Timestamp
     *
     *  @param  time Date to be converted
     *  @param  dayOnly true if time set to 00:00:00
     *
     *  @return TO_DATE('2001-01-30 18:10:20',''YYYY-MM-DD HH24:MI:SS')
     *      or  TO_DATE('2001-01-30',''YYYY-MM-DD')
     */
    public String TO_DATE(Timestamp time, boolean dayOnly) {

        if (time == null) {

            if (dayOnly) {
                return "current_date()";
            }

            return "current_date()";
        }

        StringBuffer	dateString	= new StringBuffer("TO_DATE('");

        // YYYY-MM-DD HH24:MI:SS.mmmm  JDBC Timestamp format
        String	myDate	= time.toString();

        if (dayOnly) {

            dateString.append(myDate.substring(0, 10));
            dateString.append("','YYYY-MM-DD')");

        } else {

            dateString.append(myDate.substring(0, myDate.indexOf(".")));	// cut off miliseconds
            dateString.append("','YYYY-MM-DD HH24:MI:SS')");
        }

        return dateString.toString();

    }		// TO_DATE

    /**
     * Retorna string 
     * @param columName
     * @param dayOnly
     * @return
     */
    public String FORMAT_DATE(String columnName, boolean dayOnly){
    	
    	StringBuffer	dateString	= new StringBuffer("substring("); 
    	dateString.append("CAST(");
    	dateString.append(columnName);
    	dateString.append(" AS character varying)");
    	dateString.append(" from 0 for 11)");
    	
    	
    	return dateString.toString(); 
    }
    
    
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

        if (scale > number.scale()) {

            try {
                result	= number.setScale(scale, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {

                // log.severe("Number=" + number + ", Scale=" + " - " + e.getMessage());
            }
        }

        return result.toString();

    }		// TO_NUMBER

    /**
     *      Close
     */
    public synchronized void close() {

        log.config(toString());

        if (m_ds != null) {

            try {
            	m_ds = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        m_ds	= null;
        log.info(" CONEXIONES CERRADAS. trace: " + getTrace());
    }		// close

    /**
     *  Convert an individual Oracle Style statements to target database statement syntax
     *
     *  @param oraStatement
     *  @return converted Statement
     */
    public String convertStatement(String oraStatement) {

        if (log.isLoggable(Level.FINE))
        	log.fine("En DBposgresSQL.java/convertStatement -sql:=  " + oraStatement);

        String	retValue[]	= m_convert.convert(oraStatement);

        // begin vpj-cd e-evolution 03/14/2005
        if (retValue.length == 0) {
            return oraStatement;
        }

        // end vpj-cd e-evolution 03/14/2005

        if (retValue == null)

        // begin vpj-cd 24/06/2005 e-evolution
        {

            log.log(Level.SEVERE, ("DB_PostgreSQL.convertStatement - Not Converted (" + oraStatement + ") - " + m_convert.getConversionError()));

            throw new IllegalArgumentException("DB_PostgreSQL.convertStatement - Not Converted (" + oraStatement + ") - " + m_convert.getConversionError());
        }

        // end vpj-cd 24/06/2005 e-evolution
        if (retValue.length != 1)

        // begin vpj-cd 24/06/2005 e-evolution
        {

            log.log(Level.SEVERE, ("DB_PostgreSQL.convertStatement - Convert Command Number=" + retValue.length + " (" + oraStatement + ") - " + m_convert.getConversionError()));

            throw new IllegalArgumentException("DB_PostgreSQL.convertStatement - Convert Command Number=" + retValue.length + " (" + oraStatement + ") - " + m_convert.getConversionError());
        }

        // end vpj-cd 24/06/2005 e-evolution
        // Diagnostics (show changed, but not if AD_Error
        // if (!oraStatement.equals(retValue[0]) && retValue[0].indexOf("AD_Error") == -1)
        // begin vpj-cd 24/06/2005 e-evolution
        // System.out.println("PostgreSQL =>" + retValue[0] + "<= <" + oraStatement + ">");
        // log.log(Level.SEVERE,"ver que condicion falla --> "+!oraStatement.equals(retValue[0])+"segunda condicion = "+ retValue[0].indexOf("AD_Error"));
        // log.log(Level.SEVERE, "PostgreSQL *=>" + retValue[0] + "<= <" + oraStatement + ">");
        // end vpj-cd 24/06/2005 e-evolution
        //
        // JOptionPane.showMessageDialog( null,"sentencia oracle= "+ oraStatement+"\n"+"sentencia en posgres= "+retValue[0], null, JOptionPane.INFORMATION_MESSAGE );
        return retValue[0];

    }		// convertStatement

    /**
     *      Create Pooled DataSource (Server)
     *      @param connection connection
     *      @return data dource
     */
    public ConnectionPoolDataSource createPoolDataSource(CConnection connection) {
        throw new UnsupportedOperationException("Not supported/implemented");
    }

    /**
     *      Test
     *      @param args ignored
     */
    public static void main(String[] args) {

        DB_PostgreSQL	postgresql	= new DB_PostgreSQL();

        //
        String	databaseName	= "openxp";
        String	uid		= "openxp";
        String	pwd		= "openxp";
        String	jdbcURL		= postgresql.getConnectionURL("openxp", DEFAULT_PORT, databaseName, uid);

        System.out.println(jdbcURL);

        try {

            postgresql.getDriver();

            Connection	conn	= DriverManager.getConnection(jdbcURL, uid, pwd);

            // CachedRowSetImpl crs = null;
            // crs = new CachedRowSetImpl();
            // crs.setSyncProvider("com.sun.rowset.providers.RIOptimisticProvider");
            // crs.setConcurrency(ResultSet.CONCUR_READ_ONLY);
            // crs.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
            // crs.setCommand("SELECT * FROM AD_Client");
            //
            // crs.execute(conn);
            //
            conn.close();
            conn	= null;

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }		// main

    /**
     *  Supports BLOB
     *  @return true if BLOB is supported
     */
    public boolean supportsBLOB() {
        return true;
    }		// supportsBLOB

    /**
     *  String Representation
     *  @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("DB_PostgreSQL[");

        sb.append(m_connection).append("]");

        return sb.toString();

    }		// toString

    
    protected String getTrace() {
        StringBuffer buffer = new StringBuffer();
        
        for (int i=1; i< Thread.currentThread().getStackTrace().length; i++) {
        	if (Thread.currentThread().getStackTrace()[i].getClassName().startsWith("java") || Thread.currentThread().getStackTrace()[i].getClassName().startsWith("sun") )
        		break;
        	buffer.append(Thread.currentThread().getStackTrace()[i].getClassName())
        		.append(".")
        		.append(Thread.currentThread().getStackTrace()[i].getMethodName())
        		 .append("[")
        		.append(Thread.currentThread().getStackTrace()[i].getLineNumber())
        		.append("]; \n");
        }
        return buffer.toString();

    }

    
    //~--- get methods --------------------------------------------------------

    /**
     *      Get Cached Connection
     *      @param connection connection
     *      @param autoCommit auto commit
     *      @param transactionIsolation trx isolation
     *      @return Connection
     *      @throws Exception
     */
    public synchronized Connection getCachedConnection(CConnection connection, boolean autoCommit, int transactionIsolation) throws Exception {

        if (m_ds == null) {
            getDataSource(connection);
        }

        //
        m_ds.setLoginTimeout(5);
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

        // log.severe("Database Name not set (yet) - call getConnectionURL first");
        return null;

    }		// getCatalog

    /**
     *      Get SQL Commands
     *      @param cmdType CMD_
     *      @return array of commands to be executed
     */
    public String[] getCommands(int cmdType) {

        if (CMD_CREATE_USER == cmdType) {
            return new String[] { "CREATE USER openxp;", };
        }

        //
        if (CMD_CREATE_DATABASE == cmdType) {
            return new String[] { "CREATE DATABASE openxp OWNER openxp;", "GRANT ALL PRIVILEGES ON openxp TO openxp;", "CREATE SCHEMA openxp;", "SET search_path TO openxp;" };
        }

        //
        if (CMD_DROP_DATABASE == cmdType) {
            return new String[] { "DROP DATABASE openxp;" };
        }

        //
        return null;

    }		// getCommands

    /**
     *  Get Database Connection String.
     *  Requirements:
     *      - createdb -E UNICODE openxp
     *  @param connection Connection Descriptor
     *  @return connection String
     */
    public String getConnectionURL(CConnection connection) {

        // jdbc:postgresql://hostname:portnumber/databasename?encoding=UNICODE
        StringBuffer	sb	= new StringBuffer("jdbc:postgresql:");

        sb.append("//").append(connection.getDbHost()).append(":").append(connection.getDbPort()).append("/").append(connection.getDbName()).append("?encoding=UTF-8");
        m_connection	= sb.toString();

        return m_connection;
    }		// getConnectionString

    /**
     *      Get Connection URL
     *      @param dbHost db Host
     *      @param dbPort db Port
     *      @param dbName sb Name
     *      @param userName user name
     *      @return connection url
     */
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName) {
        return "jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName;
    }		// getConnectionURL

    /**
     *      Create DataSource (Client)
     *      @param connection connection
     *      @return data dource
     */
    public DataSource getDataSource(CConnection connection) {

        // throw new UnsupportedOperationException("Not supported/implemented");
        if (m_ds != null) {
            return m_ds;
        }

        // Comentado por traer problemas bloqueantes al llegar al limite permitido de conexiones cliente
        // org.postgresql.ds.PGPoolingDataSource ds = new org.postgresql.ds.PGPoolingDataSource();
        // org.postgresql.jdbc3.Jdbc3PoolingDataSource	ds	= new org.postgresql.jdbc3.Jdbc3PoolingDataSource();

     PGSimpleDataSource ds = new PGSimpleDataSource();

        
     //   ds.setDataSourceName("openxpDS");
        ds.setServerName(connection.getDbHost());
        ds.setDatabaseName(connection.getDbName());
        ds.setUser(connection.getDbUid());
        ds.setPassword(connection.getDbPwd());
        ds.setPortNumber(connection.getDbPort());
        
        // Establecemos el numero maximo de conexiones
   //     ds.setMaxConnections(10);
  //      ds.setInitialConnections(1);
        ds.setSocketTimeout(0);
    	
        // new InitialContext().rebind("DataSource", source);
        m_ds	= ds;

        return m_ds;
    }

    /**
     *  Get Database Description
     *  @return database long name and version
     */
    public String getDescription() {
        return s_driver.toString();
    }		// getDescription

    /**
     *  Get and register Database Driver
     *  @return Driver
     *
     * @throws SQLException
     */
    public java.sql.Driver getDriver() throws SQLException {

        if (s_driver == null) {

            s_driver	= new org.postgresql.Driver();
            DriverManager.registerDriver(s_driver);
            DriverManager.setLoginTimeout(Database.CONNECTION_TIMEOUT);
        }

        return s_driver;

    }		// getDriver

    /**
     *      Get Connection from Driver
     *      @param connection info
     *      @return connection or null
     *
     * @throws SQLException
     */
    public Connection getDriverConnection(CConnection connection) throws SQLException {

        getDriver();

        return DriverManager.getConnection(getConnectionURL(connection), connection.getDbUid(), connection.getDbPwd());

    }		// getDriverConnection

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
        return Database.DB_POSTGRESQL;
    }		// getName

    /**
     *  Get RowSet
     *      @param rs ResultSet
     *  @return RowSet
     *  @throws SQLException
     */
    public RowSet getRowSet(java.sql.ResultSet rs) throws SQLException {
        throw new UnsupportedOperationException("PostgreSQL does not support RowSets");
    }		// getRowSet

    /**
     *      Get JDBC Schema
     *      @return schema (dbo)
     */
    public String getSchema() {

        // begin vpj-cd e-evolution 03/04/2005
    	
    	// Retorna el nombre del usuario como el schema
    	CConnection conn = CConnection.get();
    	String usuario = conn.getDbUid();
    	
    	return usuario;
    	
        // end vpj-cd e-evolution 03/04/2005
    }		// getSchema

    /**
     *  Get Standard JDBC Port
     *  @return standard port
     */
    public int getStandardPort() {
        return DEFAULT_PORT;
    }		// getStandardPort

    /**
     *      Get Status
     *      @return status info
     */
    public String getStatus() {
        return "";
    }		// getStatus

    /**
     *  Get Name of System Database
     *  @param databaseName database Name
     *  @return e.g. master or database Name
     */
    public String getSystemDatabase(String databaseName) {
        return "template1";
    }		// getSystemDatabase

    /**
     *  Get Name of System User
     *  @return e.g. sa, system
     */
    public String getSystemUser() {
        return "postgres";
    }		// getSystemUser

	public String getErrorMsg(SQLException exception) {
		String msg = errorMsgs.get(exception.getSQLState());
		return (msg == null? "" : msg);
	}

	
	public String TO_DATEFORMAT(String columnName, int displayType,String AD_Language) {
	    StringBuffer	retValue	= new StringBuffer("TO_CHAR (");
        retValue.append(columnName);
        retValue.append(" ,'DD/MM/YYYY')");
    
        return retValue.toString();
	}
    
}	// DB_PostgreSQL



/*
 * @(#)DB_PostgreSQL.java   02.jul 2007
 * 
 *  Fin del fichero DB_PostgreSQL.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
