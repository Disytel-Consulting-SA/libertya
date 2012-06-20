/*
 * @(#)CrearOXP.java   12.oct 2007  Versión 2.2
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
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Element;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Util;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Class to Create a new OpenXpertya Database from a reference DB.
 *  <pre>
 *  - Create User
 *  - Create DDL (table, procedures, functions, etc.)
 *  </pre>
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CrearOXP.java,v 1.7 2005/03/11 20:29:01 jjanke Exp $
 */
public class CrearOXP {

    /** Logger */
    private static CLogger	log	= CLogger.getCLogger(CrearOXP.class);

    /** OpenXpertya Target Database */
    private BaseDatosOXP	m_dbTarget	= null;

    /** OpenXpertya Source Database */
    private BaseDatosOXP	m_dbSource	= null;

    /** Descripción de Campo */
    private int	m_databasePort	= 0;

    //

    /** Descripción de Campo */
    private String	m_databaseHost	= null;

    /** Descripción de Campo */
    private String	m_systemPassword	= null;

    /** Descripción de Campo */
    private String	m_databaseName	= null;

    /** Descripción de Campo */
    private String	m_databaseDevice	= null;

    //

    /** Descripción de Campo */
    private Properties	m_ctx	= new Properties();

    /** Cached connection */
    private Connection	m_conn	= null;

    /** Descripción de Campo */
    private String	m_UsuarioOXP	= null;

    /** Descripción de Campo */
    private String	m_OXPPassword	= null;

    /** Descripción de Campo */
    private PrintWriter	m_writer	= null;

    /**
     *      Constructor
     *      @param databaseType BaseDatosOXP.TYPE_
     *      @param databaseHost database host
     *      @param databasePort database port 0 for default
     *      @param systemPassword system password
     */
    public CrearOXP(String databaseType, String databaseHost, int databasePort, String systemPassword) {

        initDatabase(databaseType);
        m_databaseHost	= databaseHost;

        if (databasePort == 0) {
            m_databasePort	= m_dbTarget.getStandardPort();
        } else {
            m_databasePort	= databasePort;
        }

        m_systemPassword	= systemPassword;
        log.info(m_dbTarget.getName() + " on " + databaseHost);

    }		// create

    /**
     *      Check Column Name
     *      @param columnName column name
     *      @return column name with correct case
     */
    private String checkColumnName(String columnName) {
        return M_Element.getColumnName(columnName);
    }		// checkColumnName

    /**
     *      Clean Start - drop & re-create DB
     */
    public void cleanStart() {

        Connection	conn	= getConnection(true, true);

        if (conn == null) {
            throw new IllegalStateException("No Database");
        }

        //
        dropDatabase(conn);
        createUser(conn);
        createDatabase(conn);

        //
        try {

            if (conn != null) {
                conn.close();
            }

        } catch (SQLException e2) {
            log.log(Level.SEVERE, "close connection", e2);
        }

        conn	= null;

    }		// cleanStart

    /**
     *      Create Tables and copy data
     *      @param whereClause optional where clause
     *      @param dropFirst drop first
     *      @return true if executed
     */
    public boolean copy(String whereClause, boolean dropFirst) {

        log.info(whereClause);

        if (getConnection(false, true) == null) {
            return false;
        }

        //
        boolean		success	= true;
        int		count	= 0;
        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM AD_Table";

        if ((whereClause != null) && (whereClause.length() > 0)) {
            sql	+= " WHERE " + whereClause;
        }

        sql	+= " ORDER BY TableName";

        //
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            DatabaseMetaData	md	= pstmt.getConnection().getMetaData();
            ResultSet		rs	= pstmt.executeQuery();

            while (rs.next() && success) {

                M_Table	table	= new M_Table(m_ctx, rs, null);

                if (table.isView()) {
                    continue;
                }

                if (dropFirst) {
                    executeCommands(new String[] { "DROP TABLE " + table.getTableName() }, m_conn, false, false);
                }

                //
                if (createTable(table, md)) {

                    list.add(table.getTableName());
                    count++;

                } else {
                    success	= false;
                }
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {

            log.log(Level.SEVERE, sql, e);
            success	= false;
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        if (!success) {
            return false;
        }

        /** Enable Contraints */
        enableConstraints(list);
        databaseBuild();
        log.info("#" + count);

        try {

            if (m_conn != null) {
                m_conn.close();
            }

        } catch (SQLException e2) {
            log.log(Level.SEVERE, "close connection", e2);
        }

        m_conn	= null;

        return success;

    }		// copy

    /**
     *  Create Database
     *
     * @param sysConn
     *  @return true if success
     */
    public boolean createDatabase(Connection sysConn) {

        log.info(m_databaseName + "(" + m_databaseDevice + ")");

        return executeCommands(m_dbTarget.getCommands(BaseDatosOXP.CMD_CREATE_DATABASE), sysConn, true, false);

    }		// createDatabase

    /**
     *      Create Table
     *
     * @param mTable
     * @param md
     *      @return true if created
     */
    private boolean createTable(M_Table mTable, DatabaseMetaData md) {

        String	tableName	= mTable.getTableName();

        log.info(tableName);

        String	catalog	= m_dbSource.getCatalog();
        String	schema	= m_dbSource.getSchema();
        String	table	= tableName.toUpperCase();

        //
        M_Column[]	columns	= mTable.getColumns(false);
        StringBuffer	sb	= new StringBuffer("CREATE TABLE ");

        sb.append(tableName).append(" (");

        try {

            // Columns
            boolean	first		= true;
            ResultSet	sourceColumns	= md.getColumns(catalog, schema, table, null);

            while (sourceColumns.next()) {

                sb.append(first
                          ? ""
                          : ", ");
                first	= false;

                // Case sensitive Column Name
                M_Column	column		= null;
                String		columnName	= sourceColumns.getString("COLUMN_NAME");

                // begin e-evolution vpj-cd 06/08/2005
                // System.out.println("Column Name" + columnName);
                // end e-evolution vpj-cd 06/08/2005

                for (int i = 0; i < columns.length; i++) {

                    String	cn	= columns[i].getColumnName();

                    if (cn.equalsIgnoreCase(columnName)) {

                        columnName	= cn;
                        column		= columns[i];

                        break;
                    }
                }

                sb.append(columnName).append(" ");

                // Data Type & Precision
                int	sqlType		= sourceColumns.getInt("DATA_TYPE");		// sql.Types
                String	typeName	= sourceColumns.getString("TYPE_NAME");		// DB Dependent
                int	size		= sourceColumns.getInt("COLUMN_SIZE");
                int	decDigits	= sourceColumns.getInt("DECIMAL_DIGITS");

                if (sourceColumns.wasNull()) {
                    decDigits	= -1;
                }

                if (typeName.equals("NUMBER")) {

                    /**
                     * Oracle Style        
                     * if (decDigits == -1)
                     *       sb.append(typeName);
                     * else
                     *       sb.append(typeName).append("(")
                     *               .append(size).append(",").append(decDigits).append(")");
                     * /** Other DBs           
                     */
                    int	dt	= column.getAD_Reference_ID();

                    if (DisplayType.isID(dt)) {
                        sb.append("INTEGER");
                    } else {

                        int	scale	= DisplayType.getDefaultPrecision(dt);

                        sb.append("DECIMAL(").append(18 + scale).append(",").append(scale).append(")");
                    }
                } else if (typeName.equals("DATE") || typeName.equals("BLOB") || typeName.equals("CLOB")) {
                    sb.append(typeName);
                } else if (typeName.equals("CHAR") || typeName.startsWith("VARCHAR")) {
                    sb.append(typeName).append("(").append(size).append(")");
                } else if (typeName.startsWith("NCHAR") || typeName.startsWith("NVAR")) {
                    sb.append(typeName).append("(").append(size / 2).append(")");
                } else if (typeName.startsWith("TIMESTAMP")) {
                    sb.append("DATE");
                } else {
                    log.severe("Do not support data type " + typeName);
                }

                // Default
                String	def	= sourceColumns.getString("COLUMN_DEF");

                if (def != null) {
                    sb.append(" DEFAULT ").append(def);
                }

                // Null
                if (sourceColumns.getInt("NULLABLE") == DatabaseMetaData.columnNoNulls) {
                    sb.append(" NOT NULL");
                } else {
                    sb.append(" NULL");
                }

                // Check Contraints

            }		// for all columns

            sourceColumns.close();

            // Primary Key
            ResultSet	sourcePK	= md.getPrimaryKeys(catalog, schema, table);

            // TABLE_CAT=null, TABLE_SCHEM=REFERENCE, TABLE_NAME=A_ASSET, COLUMN_NAME=A_ASSET_ID, KEY_SEQ=1, PK_NAME=A_ASSET_KEY
            first	= true;

            boolean	hasPK	= false;

            while (sourcePK.next()) {

                hasPK	= true;

                // begin vpj-cd e-evolution 06/14/2005
                String	PK_NAME	= sourcePK.getString("PK_NAME");

                if (PK_NAME.indexOf("KEY") < 0) {
                    PK_NAME	= PK_NAME + "_KEY";
                }

                // end vpj-cd e-evolution
                if (first) {

                    // begin vpj-cd e-evolution 06/14/2005
                    // sb.append(", CONSTRAINT ").append(sourcePK.getString("PK_NAME")).append(" PRIMARY KEY (");
                    sb.append(", CONSTRAINT ").append(PK_NAME).append(" PRIMARY KEY (");

                    // end vpj-cd e-evolution 06/14/2005
                } else {
                    sb.append(",");
                }

                first	= false;

                String	columnName	= sourcePK.getString("COLUMN_NAME");

                sb.append(checkColumnName(columnName));
            }

            if (hasPK) {		// close constraint
                sb.append(")");		// USING INDEX TABLESPACE INDX
            }

            sourcePK.close();

            //
            sb.append(")");	// close create table
        } catch (Exception ex) {

            log.log(Level.SEVERE, "createTable", ex);

            return false;
        }

        // Execute Create Table
        if (!executeCommands(new String[] { sb.toString() }, m_conn, false, true)) {
            return true;	// continue
        }

        // Create Inexes
        // begin vpj-cd e-evolution 03/11/2005
        // createTableIndexes(mTable, md);
        // end vpj-cd e-evolution 03/11/2005
        return createTableData(mTable);

    }		// createTable

    /**
     *      Create/Copy Table Data
     *      @param mTable model table
     *      @return true if data created/copied
     */
    private boolean createTableData(M_Table mTable) {

        boolean	success	= true;
        int	count	= 0;
        int	errors	= 0;
        long	start	= System.currentTimeMillis();

        // Get Table Data
        String			sql	= "SELECT * FROM " + mTable.getTableName();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql, mTable.get_TrxName());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                if (createTableDataRow(rs, mTable)) {
                    count++;
                } else {
                    errors++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {

            log.log(Level.SEVERE, sql, e);
            success	= false;
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        long	elapsed	= System.currentTimeMillis() - start;

        log.config("Inserted=" + count + " - Errors=" + errors + " - " + elapsed + " ms");

        return success;

    }		// createTableData

    /**
     *      Create Table Data Row
     *      @param rs result set
     *      @param mTable table
     *      @return true if created
     */
    private boolean createTableDataRow(ResultSet rs, M_Table mTable) {

        StringBuffer	insert	= new StringBuffer("INSERT INTO ").append(mTable.getTableName()).append(" (");
        StringBuffer	values	= new StringBuffer();

        //
        M_Column[]	columns	= mTable.getColumns(false);

        for (int i = 0; i < columns.length; i++) {

            if (i != 0) {

                insert.append(",");
                values.append(",");
            }

            M_Column	column		= columns[i];
            String	columnName	= column.getColumnName();

            insert.append(columnName);

            //
            int	dt	= column.getAD_Reference_ID();

            try {

                Object	value	= rs.getObject(columnName);

                if (rs.wasNull()) {
                    values.append("NULL");
                } else if (columnName.endsWith("_ID")		// Record_ID, C_ProjectType defined as Button
                           || DisplayType.isNumeric(dt) || (DisplayType.isID(dt) &&!columnName.equals("AD_Language"))) {

                    BigDecimal	bd	= rs.getBigDecimal(columnName);
                    String	s	= m_dbTarget.TO_NUMBER(bd, dt);

                    values.append(s);

                } else if (DisplayType.isDate(dt)) {

                    Timestamp	ts		= rs.getTimestamp(columnName);
                    String	tsString	= m_dbTarget.TO_DATE(ts, dt == DisplayType.Date);

                    values.append(tsString);

                } else if (DisplayType.isLOB(dt)) {

                    // ignored
                    values.append("NULL");
                } else if (DisplayType.isText(dt) || (dt == DisplayType.YesNo) || (dt == DisplayType.List) || (dt == DisplayType.Button) || columnName.equals("AD_Language")) {

                    String	s	= rs.getString(columnName);

                    values.append(DB.TO_STRING(s));

                } else {

                    log.warning("Unknown DisplayType=" + dt + " - " + value + " [" + value.getClass().getName() + "]");
                    values.append("NuLl");
                }

            } catch (Exception e) {
                log.log(Level.SEVERE, columnName, e);
            }

        }	// for all columns

        //
        insert.append(") VALUES (").append(values).append(")");

        // begin vpj-cd e-evolution 06/14/2005
        // System.out.println("Insert SQL:" + insert.toString());
        // end vpj-cd e-evolution 06/14/2005
        return executeCommands(new String[] { insert.toString() }, m_conn, false, false);	// do not convert as text is converted

    }		// createTableDataRow

    /**
     *      Create Table Indexes
     *      @param mTable table
     * @param md
     */
    private void createTableIndexes(M_Table mTable, DatabaseMetaData md) {

        String	tableName	= mTable.getTableName();

        log.info(tableName);

        String	catalog	= m_dbSource.getCatalog();
        String	schema	= m_dbSource.getSchema();
        String	table	= tableName.toUpperCase();

        try {
            ResultSet	sourceIndex	= md.getIndexInfo(catalog, schema, table, false, false);
        } catch (Exception e) {}

    }		// createTableIndexes

    /**
     *  Create User
     *
     * @param sysConn
     *  @return true if success
     */
    public boolean createUser(Connection sysConn) {

        log.info(m_UsuarioOXP + "/" + m_OXPPassword);

        return executeCommands(m_dbTarget.getCommands(BaseDatosOXP.CMD_CREATE_USER), sysConn, true, false);

    }		// createUser

    /**
     * Descripción de Método
     *
     */
    private void databaseBuild() {

        // Build Script
        String	fileName	= "C:\\OpenXpertya\\db\\database\\DatabaseBuild.sql";
        File	file		= new File(fileName);

        if (!file.exists()) {
            log.severe("No file: " + fileName);
        }

        // FileReader reader = new FileReader (file);
    }		// databaseBuild

    /**
     *  Drop Database
     *
     * @param sysConn
     *  @return true if success
     */
    public boolean dropDatabase(Connection sysConn) {

        log.info(m_databaseName);

        return executeCommands(m_dbTarget.getCommands(BaseDatosOXP.CMD_DROP_DATABASE), sysConn, true, false);

    }		// dropDatabase

    /**
     *      Enable Constraints
     *      @param list list
     *      @return true if constraints enabled/created
     */
    private boolean enableConstraints(ArrayList list) {

        log.info("");

        return false;

    }		// enableConstraints

    /**
     *      Execute Script
     *
     * @param script
     *      @return true if executed
     */
    public boolean execute(File script) {
        return false;
    }		// createTables

    /**
     *      Execute Commands
     *      @param cmds array of SQL commands
     *      @param conn connection
     *      @param batch tf true commit as batch
     * @param doConvert
     *      @return true if success
     */
    private boolean executeCommands(String[] cmds, Connection conn, boolean batch, boolean doConvert) {

        if ((cmds == null) || (cmds.length == 0)) {

            log.warning("No Commands");

            return false;
        }

        Statement	stmt		= null;
        String		cmd		= null;
        String		cmdOriginal	= null;

        try {

            if (conn == null) {

                conn	= getConnection(false, false);

                if (conn == null) {
                    return false;
                }
            }

            if (conn.getAutoCommit() == batch) {
                conn.setAutoCommit(!batch);
            }

            stmt	= conn.createStatement();

            // Commands
            for (int i = 0; i < cmds.length; i++) {

                cmd		= cmds[i];
                cmdOriginal	= cmds[i];

                if ((cmd == null) || (cmd.length() == 0)) {
                    continue;
                }

                //
                if (cmd.indexOf('@') != -1) {

                    cmd	= Util.replace(cmd, "@SystemPassword@", m_systemPassword);
                    cmd	= Util.replace(cmd, "@UsuarioOXP@", m_UsuarioOXP);
                    cmd	= Util.replace(cmd, "@OXPPassword@", m_OXPPassword);
                    cmd	= Util.replace(cmd, "@SystemPassword@", m_systemPassword);
                    cmd	= Util.replace(cmd, "@DatabaseName@", m_databaseName);

                    if (m_databaseDevice != null) {
                        cmd	= Util.replace(cmd, "@DatabaseDevice@", m_databaseDevice);
                    }
                }

                if (doConvert) {
                    cmd	= m_dbTarget.convertStatement(cmd);
                }

                writeLog(cmd);
                log.finer(cmd);

                int	no	= stmt.executeUpdate(cmd);

                log.finest("# " + no);
            }

            //
            stmt.close();
            stmt	= null;

            //
            if (batch) {
                conn.commit();
            }

            //
            return true;

        } catch (Exception e) {

            String	msg	= e.getMessage();

            if ((msg == null) || (msg.length() == 0)) {
                msg	= e.toString();
            }

            msg	+= " (";

            if (e instanceof SQLException) {
                msg	+= "State=" + ((SQLException) e).getSQLState() + ",ErrorCode=" + ((SQLException) e).getErrorCode();
            }

            msg	+= ")";

            if ((cmdOriginal != null) &&!cmdOriginal.equals(cmd)) {
                msg	+= " - " + cmdOriginal;
            }

            msg	+= "\n=>" + cmd;
            log.log(Level.SEVERE, msg);
        }

        // Error clean up
        try {

            if (stmt != null) {
                stmt.close();
            }

        } catch (SQLException e1) {
            log.log(Level.SEVERE, "close statement", e1);
        }

        stmt	= null;

        return false;

    }		// execureCommands

    /**
     *      Create OpenXpertya Database
     *      @param databaseType Database.DB_
     */
    private void initDatabase(String databaseType) {

        try {

            for (int i = 0; i < Database.DB_NAMES.length; i++) {

                if (Database.DB_NAMES[i].equals(databaseType)) {

                    m_dbTarget	= (BaseDatosOXP) Database.DB_CLASSES[i].newInstance();

                    break;
                }
            }

        } catch (Exception e) {

            log.severe(e.toString());
            e.printStackTrace();
        }

        if (m_dbTarget == null) {
            throw new IllegalStateException("No database: " + databaseType);
        }

        // Source Database
        m_dbSource	= DB.getDatabase();

    }		// createDatabase

    /**
     *      Create DB
     *      @param args
     */
    public static void main(String[] args) {

        OpenXpertya.startup(true);
        CLogMgt.setLevel(Level.FINE);
        CLogMgt.setLoggerLevel(Level.FINE, null);

        // C_UOM_Conversion
        // I_BankStatement
        //
        // Sybase
        // begin vpj-cd e-Evolution 03/03/2005 PostgreSQL
        // PostgreSQL
        // CrearOXP cc = new CrearOXP (Database.DB_SYBASE, "dev2", 0, "");
        // cc.setUsuarioOXP("openxp", "openxp");
        // cc.setDatabaseName("openxp", "openxp");
        CrearOXP	cc	= new CrearOXP(Database.DB_POSTGRESQL, "openxp", 5432, "postgres");

        cc.setUsuarioOXP("openxp", "openxp");
        cc.setDatabaseName("openxp", "openxp");

        // end begin vpj-cd e-Evolution 03/03/2005 PostgreSQL
        if (!cc.testConnection()) {
            return;
        }

        cc.cleanStart();

        //
        cc.copy(null, false);

        // cc.copy("TableName = 'AD_WF_NodeNext'", true);

    }		// main

    /**
     *      Test Connection
     *      @return connection
     */
    public boolean testConnection() {

        String	dbUrl	= m_dbTarget.getConnectionURL(m_databaseHost, m_databasePort, m_databaseName, m_dbTarget.getSystemUser());	// OPENXPERTYA NO ESTA DEFINIDO TODAVIA

        log.info(dbUrl + " - " + m_dbTarget.getSystemUser() + "/" + m_systemPassword);

        try {

            Connection	conn	= m_dbTarget.getDriverConnection(dbUrl, m_dbTarget.getSystemUser(), m_systemPassword);

            //
            JDBCInfo	info	= new JDBCInfo(conn);

            if (CLogMgt.isLevelFinest()) {

                info.listCatalogs();
                info.listSchemas();
            }

        } catch (Exception e) {

            log.log(Level.SEVERE, "test", e);

            return false;
        }

        return true;

    }		// testConnection

    /**
     *      Write to File Log
     *      @param cmd cmd
     */
    private void writeLog(String cmd) {

        try {

            if (m_writer == null) {

                File	file	= File.createTempFile("create", ".log");

                m_writer	= new PrintWriter(new FileWriter(file));
                log.info(file.toString());
            }

            m_writer.println(cmd);
            m_writer.flush();

        } catch (Exception e) {
            log.severe(e.toString());
        }

    }		// writeLog

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Connection
     *      @param asSystem if true execute as db system administrator
     * @param createNew
     *      @return connection or null
     */
    private Connection getConnection(boolean asSystem, boolean createNew) {

        if (!createNew && (m_conn != null)) {
            return m_conn;
        }

        //
        String	dbUrl	= m_dbTarget.getConnectionURL(m_databaseHost, m_databasePort, (asSystem
                ? m_dbTarget.getSystemDatabase(m_databaseName)
                : m_databaseName), (asSystem
                                    ? m_dbTarget.getSystemUser()
                                    : m_UsuarioOXP));

        try {

            if (asSystem) {
                m_conn	= m_dbTarget.getDriverConnection(dbUrl, m_dbTarget.getSystemUser(), m_systemPassword);
            } else {
                m_conn	= m_dbTarget.getDriverConnection(dbUrl, m_UsuarioOXP, m_OXPPassword);
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, dbUrl, e);
        }

        return m_conn;

    }		// getConnection

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Database Name
     *  @param databaseName db name
     *  @param databaseDevice device or table space
     */
    public void setDatabaseName(String databaseName, String databaseDevice) {

        m_databaseName		= databaseName;
        m_databaseDevice	= databaseDevice;

    }		// createDatabase

    /**
     *  Set OpenXpertya User
     *  @param UsuarioOXP OXP id
     *  @param OXPPassword OXP password
     */
    public void setUsuarioOXP(String UsuarioOXP, String OXPPassword) {

        m_UsuarioOXP	= UsuarioOXP;
        m_OXPPassword	= OXPPassword;

    }		// setUsuarioOXP
}	// CrearOXP



/*
 * @(#)CrearOXP.java   02.jul 2007
 * 
 *  Fin del fichero CrearOXP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
