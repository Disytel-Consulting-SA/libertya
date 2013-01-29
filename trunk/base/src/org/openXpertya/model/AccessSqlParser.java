/*
 * @(#)AccessSqlParser.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.model;

import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Util;

//~--- Importaciones JDK ------------------------------------------------------

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 *      Parse FROM in SQL WHERE clause
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: AccessSqlParser.java,v 1.11 2005/05/31 05:42:37 jjanke Exp $
 */
public class AccessSqlParser {

    /** FROM String */
    private static final String	FROM	= " FROM ";

    /** Descripción de Campo */
    private static final int	FROM_LENGTH	= FROM.length();

    /** Descripción de Campo */
    private static final String	WHERE	= " WHERE ";

    /** Descripción de Campo */
    private static final String	ON	= " ON ";

    /** Logger */
    private CLogger	log	= CLogger.getCLogger(getClass());

    /** List of Arrays */
    private ArrayList	m_tableInfo	= new ArrayList();

    /** SQL Selects */
    private String[]	m_sql;

    /** Original SQL */
    private String	m_sqlOriginal;

    /**
     *      Base Constructor.
     *      You need to set the SQL and start the parsing manually.
     */
    public AccessSqlParser() {}		// AccessSqlParser

    /**
     *      Full Constructor
     *      @param sql sql command
     */
    public AccessSqlParser(String sql) {
        setSql(sql);
    }		// AccessSqlParser

    /**
     *      Parse Original SQL.
     *      Called from setSql or Constructor.
     *
     * @return
     */
    public boolean parse() {

        if ((m_sqlOriginal == null) || (m_sqlOriginal.length() == 0)) {
            throw new IllegalArgumentException("No SQL");
        }

        //
        // if (CLogMgt.isLevelFinest())
        // log.fine(m_sqlOriginal);
        getSelectStatements();

        // analyse each select
        for (int i = 0; i < m_sql.length; i++) {

            TableInfo[]	info	= getTableInfo(m_sql[i].trim());

            m_tableInfo.add(info);
        }

        //
        if (CLogMgt.isLevelFinest()) {
            log.fine(toString());
        }

        return m_tableInfo.size() > 0;

    }		// parse

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("AccessSqlParser[");

        if (m_tableInfo == null) {
            sb.append(m_sqlOriginal);
        } else {

            for (int i = 0; i < m_tableInfo.size(); i++) {

                if (i > 0) {
                    sb.append("|");
                }

                TableInfo[]	info	= (TableInfo[]) m_tableInfo.get(i);

                for (int ii = 0; ii < info.length; ii++) {

                    if (ii > 0) {
                        sb.append(",");
                    }

                    sb.append(info[ii].toString());
                }
            }
        }

        sb.append("|").append(getMainSqlIndex());
        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- get methods --------------------------------------------------------

    /**
     *      Get main sql Statement
     *      @return main statement
     */
    public String getMainSql() {

        if (m_sql == null) {
            return m_sqlOriginal;
        }

        if (m_sql.length == 1) {
            return m_sql[0];
        }

        for (int i = m_sql.length - 1; i >= 0; i--) {

            if (m_sql[i].charAt(0) != '(') {
                return m_sql[i];
            }
        }

        return "";

    }		// getMainSql

    /**
     *      Get index of main Statements
     *      @return index of main statement or -1 if not found
     */
    public int getMainSqlIndex() {

        if (m_sql == null) {
            return -1;
        } else if (m_sql.length == 1) {
            return 0;
        }

        for (int i = m_sql.length - 1; i >= 0; i--) {

            if (m_sql[i].charAt(0) != '(') {
                return i;
            }
        }

        return -1;

    }		// getMainSqlIndex

    /**
     *      Get No of SQL Statements
     *      @return FROM clause count
     */
    public int getNoSqlStatments() {

        if (m_sql == null) {
            return 0;
        }

        return m_sql.length;

    }		// getNoSqlStatments

    /**
     *      Parses  m_sqlOriginal and creates Array of m_sql statements
     */
    private void getSelectStatements() {

        String[]	sqlIn	= new String[] { m_sqlOriginal };
        String[]	sqlOut	= null;

        try {
            sqlOut	= getSubSQL(sqlIn);
        } catch (Exception e) {

            log.log(Level.SEVERE, m_sqlOriginal, e);

            throw new IllegalArgumentException(m_sqlOriginal);
        }

        // a sub-query was found
        while (sqlIn.length != sqlOut.length) {

            sqlIn	= sqlOut;

            try {
                sqlOut	= getSubSQL(sqlIn);
            } catch (Exception e) {

                log.log(Level.SEVERE, m_sqlOriginal, e);

                throw new IllegalArgumentException(sqlOut.length + ": " + m_sqlOriginal);
            }
        }

        m_sql	= sqlOut;

        /**
         * List & check 
         * for (int i = 0; i < m_sql.length; i++)
         * {
         *       if (m_sql[i].indexOf("SELECT ",2) != -1)
         *               log.log(Level.SEVERE, "#" + i + " Has embedded SQL - " + m_sql[i]);
         *       else
         *               log.fine("#" + i + " - " + m_sql[i]);
         * }
         * /** *
         */

    }		// getSelectStatements

    /**
     *      Get (original) Sql
     *      @return sql
     */
    public String getSql() {
        return m_sqlOriginal;
    }		// getSql

    /**
     *      Get Sql Statements
     *
     * @param index
     *      @return index index of query
     */
    public String getSqlStatement(int index) {

        if ((index < 0) || (index > m_sql.length)) {
            return null;
        }

        return m_sql[index];

    }		// getSqlStatement

    /**
     *      Get Sub SQL of sql statements
     *      @param sqlIn array of input sql
     *      @return array of resulting sql
     */
    private String[] getSubSQL(String[] sqlIn) {

        ArrayList	list	= new ArrayList();

        for (int sqlIndex = 0; sqlIndex < sqlIn.length; sqlIndex++) {

            String	sql	= sqlIn[sqlIndex];
            int		index	= sql.indexOf("(SELECT ", 7);

            while (index != -1) {

                int	endIndex		= index + 1;
                int	parenthesisLevel	= 0;

                // search for the end of the sql
                while (endIndex++ < sql.length()) {

                    char	c	= sql.charAt(endIndex);

                    if (c == ')') {

                        if (parenthesisLevel == 0) {
                            break;
                        } else {
                            parenthesisLevel--;
                        }

                    } else if (c == '(') {
                        parenthesisLevel++;
                    }
                }

                String	subSQL	= sql.substring(index, endIndex + 1);

                list.add(subSQL);

                // remove inner SQL (##)
                sql	= sql.substring(0, index + 1) + "##" + sql.substring(endIndex);
                index	= sql.indexOf("(SELECT ", 7);
            }

            list.add(sql);	// last SQL
        }

        String[]	retValue	= new String[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getSubSQL

    /**
     *      Get Table Info
     *
     * @param index
     *      @return table info
     */
    public TableInfo[] getTableInfo(int index) {

        if ((index < 0) || (index > m_tableInfo.size())) {
            return null;
        }

        TableInfo[]	retValue	= (TableInfo[]) m_tableInfo.get(index);

        return retValue;

    }		// getTableInfo

    /**
     *      Get Table Info for SQL
     *      @param sql sql
     *      @return array of table info for sql
     */
    private TableInfo[] getTableInfo(String sql) {

        ArrayList	list	= new ArrayList();

        // remove ()
        if (sql.startsWith("(") && sql.endsWith(")")) {
            sql	= sql.substring(1, sql.length() - 1);
        }

        int	fromIndex	= sql.indexOf(FROM);

        if (fromIndex != sql.lastIndexOf(FROM)) {
            log.log(Level.WARNING, "getTableInfo - More than one FROM clause - " + sql);
        }

        while (fromIndex != -1) {

            String	from	= sql.substring(fromIndex + FROM_LENGTH);
            int		index	= from.lastIndexOf(WHERE);	// end at where

            if (index != -1) {
                from	= from.substring(0, index);
            }

            from	= Util.replace(from, " AS ", " ");
            from	= Util.replace(from, " as ", " ");
            from	= Util.replace(from, " INNER JOIN ", ", ");
            from	= Util.replace(from, " LEFT OUTER JOIN ", ", ");
            from	= Util.replace(from, " RIGHT OUTER JOIN ", ", ");
            from	= Util.replace(from, " FULL JOIN ", ", ");

            // Remove ON clause - assumes that there is no IN () in the clause
            index	= from.indexOf(ON);

            while (index != -1) {

                int	indexClose	= from.indexOf(')');	// does not catch "IN (1,2)" in ON
                int	indexNextOn	= from.indexOf(ON, index + 4);

                if (indexNextOn != -1) {
                    indexClose	= from.lastIndexOf(')', indexNextOn);
                }

                if (indexClose != -1) {
                    from	= from.substring(0, index) + from.substring(indexClose + 1);
                } else {

                    log.log(Level.SEVERE, "parse - could not remove ON " + from);

                    break;
                }

                index	= from.indexOf(ON);
            }

//          log.fine("getTableInfo - " + from);
            StringTokenizer	tableST	= new StringTokenizer(from, ",");

            while (tableST.hasMoreTokens()) {

                String		tableString	= tableST.nextToken().trim();
                StringTokenizer	synST		= new StringTokenizer(tableString, " ");
                TableInfo	tableInfo	= null;

                if (synST.countTokens() > 1) {
                    tableInfo	= new TableInfo(synST.nextToken(), synST.nextToken());
                } else {
                    tableInfo	= new TableInfo(tableString);
                }

//              log.fine("getTableInfo -- " + tableInfo);
                list.add(tableInfo);
            }

            //
            sql		= sql.substring(0, fromIndex);
            fromIndex	= sql.lastIndexOf(FROM);
        }

        TableInfo[]	retValue	= new TableInfo[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getTableInfo

    //~--- set methods --------------------------------------------------------

    /**
     *      Set Sql and parse it
     *      @param sql sql
     */
    public void setSql(String sql) {

        if (sql == null) {
            throw new IllegalArgumentException("No SQL");
        }

        m_sqlOriginal	= sql;

        //
        parse();

    }		// setSQL

    /**
     *      Table Info VO
     */
    public class TableInfo {

        /** Descripción de Campo */
        private String	m_synonym;

        /** Descripción de Campo */
        private String	m_tableName;

        /**
         *      Short Constuctor - no syn
         *      @param tableName table
         */
        public TableInfo(String tableName) {
            this(tableName, null);
        }	// TableInfo

        /**
         *      Constructor
         *      @param tableName table
         *      @param synonym synonym
         */
        public TableInfo(String tableName, String synonym) {

            m_tableName	= tableName;
            m_synonym	= synonym;

        }	// TableInfo

        /**
         *      String Representation
         *      @return info
         */
        public String toString() {

            StringBuffer	sb	= new StringBuffer(m_tableName);

            if (getSynonym().length() > 0) {
                sb.append("=").append(m_synonym);
            }

            return sb.toString();

        }	// toString

        //~--- get methods ----------------------------------------------------

        /**
         *      Get Table Synonym
         *      @return synonym
         */
        public String getSynonym() {

            if (m_synonym == null) {
                return "";
            }

            return m_synonym;

        }	// getSynonym

        /**
         *      Get TableName
         *      @return table name
         */
        public String getTableName() {
            return m_tableName;
        }	// getTableName
    }		// TableInfo
}		// AccessSqlParser



/*
 * @(#)AccessSqlParser.java   02.jul 2007
 * 
 *  Fin del fichero AccessSqlParser.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
