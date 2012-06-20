/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ChangeLogProcess extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_ChangeLog_ID = 0;

    /** Descripción de Campos */

    private Boolean p_CheckNewValue = null;

    /** Descripción de Campos */

    private Boolean p_CheckOldValue = null;

    /** Descripción de Campos */

    private boolean p_SetCustomization = false;

    /** Descripción de Campos */

    private StringBuffer m_sqlUpdate = null;

    /** Descripción de Campos */

    private StringBuffer m_sqlUpdateWhere = null;

    /** Descripción de Campos */

    private boolean m_isInsert = false;

    /** Descripción de Campos */

    private StringBuffer m_sqlInsert = null;

    /** Descripción de Campos */

    private StringBuffer m_sqlInsertValue = null;

    /** Descripción de Campos */

    private M_Table m_table = null;

    /** Descripción de Campos */

    private M_Column m_column = null;

    /** Descripción de Campos */

    private int m_oldRecord_ID = 0;

    /** Descripción de Campos */

    private String m_keyColumn = null;

    /** Descripción de Campos */

    private int m_numberColumns = 0;

    /** Descripción de Campos */

    private int m_errors = 0;

    /** Descripción de Campos */

    private int m_checkFailed = 0;

    /** Descripción de Campos */

    private int m_ok = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "CheckNewValue" )) {
                p_CheckNewValue = new Boolean( "Y".equals( para[ i ].getParameter()));
            } else if( name.equals( "CheckOldValue" )) {
                p_CheckOldValue = new Boolean( "Y".equals( para[ i ].getParameter()));
            } else if( name.equals( "SetCustomization" )) {
                p_SetCustomization = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_AD_ChangeLog_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        if( p_SetCustomization ) {
            return setCustomization();
        }

        log.info( "doIt - AD_ChangeLog_ID=" + p_AD_ChangeLog_ID + ", CheckOldValue=" + p_CheckOldValue + ", CheckNewValue" + p_CheckNewValue );

        // Single Change or All Customizations

        String sql = "SELECT * FROM AD_ChangeLog WHERE AD_ChangeLog_ID=? " + "ORDER BY AD_Table_ID, Record_ID, AD_Column_ID";

        if( p_AD_ChangeLog_ID == 0 ) {
            sql = "SELECT * FROM AD_ChangeLog WHERE IsCustomization='Y' AND IsActive='Y' " + "ORDER BY AD_Table_ID, Record_ID, AD_Column_ID";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());

            if( p_AD_ChangeLog_ID != 0 ) {
                pstmt.setInt( 1,p_AD_ChangeLog_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                createStatement( new MChangeLog( getCtx(),rs,get_TrxName()),get_TrxName());
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        executeStatement();

        return "@OK@: " + m_ok + " - @Errors@: " + m_errors + " - @Failed@: " + m_checkFailed;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param cLog
     * @param trxName
     */

    private void createStatement( MChangeLog cLog,String trxName ) {

        // New Table

        if( m_table != null ) {
            if( cLog.getAD_Table_ID() != m_table.getAD_Table_ID()) {
                executeStatement();
                m_table = null;
            }
        }

        if( m_table == null ) {
            m_table = new M_Table( getCtx(),cLog.getAD_Table_ID(),trxName );
        }

        // New Record

        if( (m_sqlUpdate != null) && (cLog.getRecord_ID() != m_oldRecord_ID) ) {
            executeStatement();
        }

        // Column Info

        m_column = new M_Column( getCtx(),cLog.getAD_Column_ID(),get_TrxName());

        // Create new Statement

        if( m_sqlUpdate == null ) {
            m_keyColumn = m_table.getTableName() + "_ID";

            //

            m_sqlUpdate = new StringBuffer( "UPDATE " ).append( m_table.getTableName()).append( " SET " );

            // Single Key Only

            m_sqlUpdateWhere = new StringBuffer( " WHERE " ).append( m_table.getTableName()).append( "_ID=" ).append( cLog.getRecord_ID());
            m_oldRecord_ID = cLog.getRecord_ID();

            // Insert - new value is null and UnDo only

            m_isInsert = cLog.isNewNull() && (p_CheckNewValue != null);

            if( m_isInsert ) {
                m_sqlInsert = new StringBuffer( "INSERT INTO " ).append( m_table.getTableName()).append( "(" ).append( m_keyColumn );
                m_sqlInsertValue = new StringBuffer( ") VALUES (" ).append( cLog.getRecord_ID());

                if( !m_keyColumn.equals( m_column.getColumnName())) {
                    m_sqlInsert.append( "," ).append( m_column.getColumnName());
                    m_sqlInsertValue.append( "," ).append( getSQLValue( cLog.getOldValue()));
                }
            }

            m_numberColumns = 1;
        }

        // Just new Column

        else {
            m_sqlUpdate.append( ", " );

            // Insert

            if( m_isInsert ) {
                m_isInsert = cLog.isNewNull();
            }

            if( m_isInsert &&!m_keyColumn.equals( m_column.getColumnName())) {
                m_sqlInsert.append( "," ).append( m_column.getColumnName());
                m_sqlInsertValue.append( "," ).append( getSQLValue( cLog.getOldValue()));
            }

            m_numberColumns++;
        }

        // Update Set clause -- columnName=value

        m_sqlUpdate.append( m_column.getColumnName()).append( "=" );

        // UnDo a <- (b)

        if( p_CheckNewValue != null ) {
            m_sqlUpdate.append( getSQLValue( cLog.getOldValue()));

            if( p_CheckNewValue.booleanValue()) {
                m_sqlUpdateWhere.append( " AND " ).append( m_column.getColumnName()).append( "=" ).append( getSQLValue( cLog.getNewValue()));
            }
        }

        // ReDo (a) -> b

        else if( p_CheckOldValue != null ) {
            m_sqlUpdate.append( getSQLValue( cLog.getNewValue()));

            if( p_CheckOldValue.booleanValue()) {
                m_sqlUpdateWhere.append( " AND " ).append( m_column.getColumnName()).append( "=" ).append( getSQLValue( cLog.getOldValue()));
            }
        }
    }    // createStatement

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    private String getSQLValue( String value ) {
        if( (value == null) || (value.length() == 0) || value.equals( "NULL" )) {
            return "NULL";
        }

        // Data Types

        if( DisplayType.isNumeric( m_column.getAD_Reference_ID()) || DisplayType.isID( m_column.getAD_Reference_ID())) {
            return value;
        }

        if( DisplayType.YesNo == m_column.getAD_Reference_ID()) {
            if( value.equals( "true" )) {
                return "'Y'";
            } else {
                return "'N'";
            }
        }

        if( DisplayType.isDate( m_column.getAD_Reference_ID())) {
            return DB.TO_DATE( Timestamp.valueOf( value ));
        }

        // String, etc.

        return DB.TO_STRING( value );
    }    // getSQLValue

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean executeStatement() {
        if( m_sqlUpdate == null ) {
            return false;
        }

        int no = 0;

        // Insert SQL

        if( m_isInsert && (m_numberColumns > 2) ) {
            m_sqlInsert.append( m_sqlInsertValue ).append( ")" );
            log.fine( "executeStatement - " + m_sqlInsert );

            //

            no = DB.executeUpdate( m_sqlInsert.toString(),get_TrxName());

            if( no == -1 ) {
                m_errors++;
            } else if( no == 0 ) {
                log.warning( "executeStatement - insert failed - " + m_sqlUpdate );
                m_checkFailed++;
            } else {
                m_ok++;
            }
        } else    // Update SQL
        {
            m_sqlUpdate.append( m_sqlUpdateWhere );
            log.fine( "executeStatement - " + m_sqlUpdate );

            //

            no = DB.executeUpdate( m_sqlUpdate.toString(),get_TrxName());

            if( no == -1 ) {
                m_errors++;
            } else if( no == 0 ) {
                log.warning( "executeStatement - check failed - " + m_sqlUpdate );
                m_checkFailed++;
            } else {
                m_ok++;
            }
        }

        //

        m_sqlUpdate      = null;
        m_sqlUpdateWhere = null;
        m_sqlInsert      = null;
        m_sqlInsertValue = null;

        return no > 0;
    }    // executeStatement

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String setCustomization() {
        log.info( "setCustomization" );

        String sql = "UPDATE AD_ChangeLog SET IsCustomization='N' WHERE IsCustomization='Y'";
        int resetNo  = DB.executeUpdate( sql,get_TrxName());
        int updateNo = 0;

        // Get Tables

        sql = "SELECT * FROM AD_Table t "

        // Table with EntityType

        + "WHERE EXISTS (SELECT * FROM AD_Column c " + "WHERE t.AD_Table_ID=c.AD_Table_ID AND c.ColumnName='EntityType')"

        // Changed Tables

        + " AND EXISTS (SELECT * FROM AD_ChangeLog l " + "WHERE t.AD_Table_ID=l.AD_Table_ID)";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                M_Table      table  = new M_Table( getCtx(),rs,get_TrxName());
                StringBuffer update = new StringBuffer( "UPDATE AD_ChangeLog SET IsCustomization='Y' " + "WHERE AD_Table_ID=" ).append( table.getAD_Table_ID());

                update.append( "AND Record_ID IN (SELECT " ).append( table.getTableName()).append( "_ID FROM " ).append( table.getTableName()).append( " WHERE EntityType IN ('D','C'))" );

                int no = DB.executeUpdate( update.toString(),get_TrxName());

                log.fine( "setCustomization - " + table.getTableName() + " = " + no );
                updateNo += no;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"setCustomization",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@Reset@: " + resetNo + " - @Updated@: " + updateNo;
    }    // setCustomization
}    // ChangeLogProcess



/*
 *  @(#)ChangeLogProcess.java   02.07.07
 * 
 *  Fin del fichero ChangeLogProcess.java
 *  
 *  Versión 2.2
 *
 */
