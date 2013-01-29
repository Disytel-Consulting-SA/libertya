/*
 * @(#)BaseDatosOXP.java   12.oct 2007  Versión 2.2
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

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

//import org.openXpertya.util.CPreparedStatement;

/**
 *  Interface for OpenXpertya Databases
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: BaseDatosOXP.java,v 1.24 2005/05/17 05:30:16 jjanke Exp $
 */
public interface BaseDatosOXP {

    /** Create User commands */
    public static final int	CMD_CREATE_USER	= 0;

    /** Create Database/Schema Commands */
    public static final int	CMD_CREATE_DATABASE	= 1;

    /** Drop Database/Schema Commands */
    public static final int	CMD_DROP_DATABASE	= 2;

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
    public String TO_CHAR(String columnName, int displayType, String AD_Language);

   
    /**
     *  Create SQL TO Date String from Timestamp
     *
     *  @param  time Date to be converted
     *  @param  dayOnly true if time set to 00:00:00
     *  @return date function
     */
    public String TO_DATE(Timestamp time, boolean dayOnly);

    /**
     *      Return number as string for INSERT statements with correct precision
     *      @param number number
     *      @param displayType display Type
     *      @return number as string
     */
    public String TO_NUMBER(BigDecimal number, int displayType);

    
    public String TO_DATEFORMAT(String columnName, int displayType, String AD_Language);
    
    /**
     *      Close
     */
    public void close();

    /**
     *  Convert an individual Oracle Style statements to target database statement syntax
     *
     *  @param oraStatement oracle statement
     *  @return converted Statement
     */
    public String convertStatement(String oraStatement);

    /**
     *  Supports BLOB
     *  @return true if BLOB is supported
     */
    public boolean supportsBLOB();

    /**
     *  String Representation
     *  @return info
     */
    public String toString();

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Cached Connection on Server
     *      @param connection info
     *  @param autoCommit true if autocommit connection
     *  @param transactionIsolation Connection transaction level
     *      @return connection or null
     *
     * @throws Exception
     */
    public Connection getCachedConnection(CConnection connection, boolean autoCommit, int transactionIsolation) throws Exception;

    /**
     *      Get JDBC Catalog
     *      @return catalog
     */
    public String getCatalog();

    /**
     *      Get SQL Commands.
     *      The following variables are resolved:
     *      @SystemPassword@, @UsuarioOXP@, @OXPPassword@
     *      @SystemPassword@, @DatabaseName@, @DatabaseDevice@
     *      @param cmdType CMD_
     *      @return array of commands to be executed
     */
    public String[] getCommands(int cmdType);

    /**
     *  Get Database Connection String
     *  @param connection Connection Descriptor
     *  @return connection String
     */
    public String getConnectionURL(CConnection connection);

    /**
     *      Get Connection URL
     *      @param dbHost db Host
     *      @param dbPort db Port
     *      @param dbName db Name
     *      @param userName user name
     *      @return
     */
    public String getConnectionURL(String dbHost, int dbPort, String dbName, String userName);

    /**
     *      Create DataSource
     *      @param connection connection
     *      @return data dource
     */
    public DataSource getDataSource(CConnection connection);

    /**
     *  Get Database Description
     *  @return database long name and version
     */
    public String getDescription();

    /**
     *  Get and register Database Driver
     *  @return Driver
     *
     * @throws SQLException
     */
    public Driver getDriver() throws SQLException;

    /**
     *      Get Connection from Driver
     *      @param connection info
     *      @return connection or null
     *
     * @throws SQLException
     */
    public Connection getDriverConnection(CConnection connection) throws SQLException;

    /**
     *      Get Driver Connection
     *      @param dbUrl URL
     *      @param dbUid user
     *      @param dbPwd password
     *      @return connection
     *      @throws SQLException
     */
    public Connection getDriverConnection(String dbUrl, String dbUid, String dbPwd) throws SQLException;

    /**
     *  Get Database Name
     *  @return database short name
     */
    public String getName();

    /**
     *      Get JDBC Schema
     *      @return schema
     */
    public String getSchema();

    /**
     *  Get Standard JDBC Port
     *  @return standard port
     */
    public int getStandardPort();

    /**
     *      Get Status
     *      @return status info
     */
    public String getStatus();

    /**
     *  Get Name of System Database
     *  @param databaseName database Name
     *  @return e.g. master or database Name
     */
    public String getSystemDatabase(String databaseName);

    /**
     *  Get Name of System User
     *  @return e.g. sa, system
     */
    public String getSystemUser();
    
    /**
     * Get an error message from a SQLException.
     * @param exception <code>SQLException</code> throwed by the server.
     * @return <code>String</code> with the message or empty string <code>""</code> if no
     * error message is available.  
     */
    public String getErrorMsg(SQLException exception);

    
	/**
	 * modify sql to return a subset of the query result
	 * @param sql
	 * @param start
	 * @param end
	 * @return
	 */
	public String addPagingSQL(String sql, int start, int end);
	
	/**
	 * Is the database have sql extension that return a subset of the query result
	 * @return boolean
	 */
	public boolean isPagingSupported();
}	// BaseDatosOXP



/*
 * @(#)BaseDatosOXP.java   02.jul 2007
 * 
 *  Fin del fichero BaseDatosOXP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
