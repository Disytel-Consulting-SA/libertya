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



package org.openXpertya.report;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MElementValue;
import org.openXpertya.model.MPeriod;
import org.openXpertya.print.MPrintFormat;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;
import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FinStatement extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

    /** Descripción de Campos */

    private String p_PostingType = "A";

    /** Descripción de Campos */

    private int p_C_Period_ID = 0;

    /** Descripción de Campos */

    private Timestamp p_DateAcct_From = null;

    /** Descripción de Campos */

    private Timestamp p_DateAcct_To = null;

    /** Descripción de Campos */

    private int p_AD_Org_ID = 0;

    /** Descripción de Campos */

    private int p_Account_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_M_Product_ID = 0;

    /** Descripción de Campos */

    private int p_C_Project_ID = 0;

    /** Descripción de Campos */

    private int p_C_Activity_ID = 0;

    /** Descripción de Campos */

    private int p_C_SalesRegion_ID = 0;

    /** Descripción de Campos */

    private int p_C_Campaign_ID = 0;

    /** Descripción de Campos */

    private boolean p_UpdateBalances = true;

    /** Descripción de Campos */

    private StringBuffer m_parameterWhere = new StringBuffer();

    /** Descripción de Campos */

    private MElementValue m_acct = null;

    /** Descripción de Campos */

    private long m_start = System.currentTimeMillis();

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        StringBuffer sb = new StringBuffer( "Record_ID=" ).append( getRecord_ID());

        // Parameter

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "PostingType" )) {
                p_PostingType = ( String )para[ i ].getParameter();
            } else if( name.equals( "C_Period_ID" )) {
                p_C_Period_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DateAcct" )) {
                p_DateAcct_From = ( Timestamp )para[ i ].getParameter();
                p_DateAcct_To   = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "Account_ID" )) {
                p_Account_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "M_Product_ID" )) {
                p_M_Product_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Project_ID" )) {
                p_C_Project_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Activity_ID" )) {
                p_C_Activity_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_SalesRegion_ID" )) {
                p_C_SalesRegion_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Campaign_ID" )) {
                p_C_Campaign_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "UpdateBalances" )) {
                p_UpdateBalances = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        // Mandatory C_AcctSchema_ID, PostingType

        m_parameterWhere.append( "C_AcctSchema_ID=" ).append( p_C_AcctSchema_ID ).append( " AND PostingType='" ).append( p_PostingType ).append( "'" );

        // Optional Account_ID

        if( p_Account_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Account,p_Account_ID ));
        }

        // Optional Org

        if( p_AD_Org_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Org,p_AD_Org_ID ));
        }

        // Optional BPartner

        if( p_C_BPartner_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_BPartner,p_C_BPartner_ID ));
        }

        // Optional Product

        if( p_M_Product_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Product,p_M_Product_ID ));
        }

        // Optional Project

        if( p_C_Project_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Project,p_C_Project_ID ));
        }

        // Optional Activity

        if( p_C_Activity_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Activity,p_C_Activity_ID ));
        }

        // Optional Campaign

        if( p_C_Campaign_ID != 0 ) {
            m_parameterWhere.append( " AND C_Campaign_ID=" ).append( p_C_Campaign_ID );
        }

        // m_parameterWhere.append(" AND ").append(MReportTree.getWhereClause(getCtx(),
        // MAcctSchemaElement.ELEMENTTYPE_Campaign, p_C_Campaign_ID));
        // Optional Sales Region

        if( p_C_SalesRegion_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_SalesRegion,p_C_SalesRegion_ID ));
        }

        //

        setDateAcct();
        sb.append( " - DateAcct " ).append( p_DateAcct_From ).append( "-" ).append( p_DateAcct_To );
        sb.append( " - Where=" ).append( m_parameterWhere );
        log.fine( sb.toString());
    }    // prepare

    /**
     * Descripción de Método
     *
     */

    private void setDateAcct() {

        // Date defined

        if( p_DateAcct_From != null ) {
            if( p_DateAcct_To == null ) {
                p_DateAcct_To = new Timestamp( System.currentTimeMillis());
            }

            return;
        }

        // Get Date from Period

        if( p_C_Period_ID == 0 ) {
            GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

            cal.setTimeInMillis( System.currentTimeMillis());
            cal.set( Calendar.HOUR_OF_DAY,0 );
            cal.set( Calendar.MINUTE,0 );
            cal.set( Calendar.SECOND,0 );
            cal.set( Calendar.MILLISECOND,0 );
            cal.set( Calendar.DAY_OF_MONTH,1 );    // set to first of month
            p_DateAcct_From = new Timestamp( cal.getTimeInMillis());
            cal.add( Calendar.MONTH,1 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );    // last of month
            p_DateAcct_To = new Timestamp( cal.getTimeInMillis());

            return;
        }

        String sql = "SELECT StartDate, EndDate FROM C_Period WHERE C_Period_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,p_C_Period_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                p_DateAcct_From = rs.getTimestamp( 1 );
                p_DateAcct_To   = rs.getTimestamp( 2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }
    }    // setDateAcct

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {

        // Update AcctSchema Balances

        if( p_UpdateBalances ) {
            FinBalance.updateBalance( p_C_AcctSchema_ID,false );
        }

        createBalanceLine();
        createDetailLines();

        int AD_PrintFormat_ID = 134;

        if( Ini.isClient()) {
            getProcessInfo().setTransientObject( MPrintFormat.get( getCtx(),AD_PrintFormat_ID,false ));
        } else {
            getProcessInfo().setSerializableObject( MPrintFormat.get( getCtx(),AD_PrintFormat_ID,false ));
        }

        log.fine(( System.currentTimeMillis() - m_start ) + " ms" );

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void createBalanceLine() {
        StringBuffer sb = new StringBuffer( "INSERT INTO T_ReportStatement " + "(AD_PInstance_ID, Fact_Acct_ID, LevelNo," + "DateAcct, Name, Description," + "AmtAcctDr, AmtAcctCr, Balance, Qty) " );

        sb.append( "SELECT " ).append( getAD_PInstance_ID()).append( ",0,0," ).append( DB.TO_DATE( p_DateAcct_From,true )).append( "," ).append( DB.TO_STRING( Msg.getMsg( Env.getCtx(),"BeginningBalance" ))).append( ",NULL," + "COALESCE(SUM(AmtAcctDr),0), COALESCE(SUM(AmtAcctCr),0), COALESCE(SUM(AmtAcctDr-AmtAcctCr),0), COALESCE(SUM(Qty),0) " + "FROM Fact_Acct_Balance " + "WHERE " ).append( m_parameterWhere ).append( " AND DateAcct < " ).append( DB.TO_DATE( p_DateAcct_From ));

        // Start Beginning of Year

        if( p_Account_ID > 0 ) {
            m_acct = new MElementValue( getCtx(),p_Account_ID,get_TrxName());

            if( !m_acct.isBalanceSheet()) {
                MPeriod first = MPeriod.getFirstInYear( getCtx(),p_DateAcct_From );

                if( first != null ) {
                    sb.append( " AND DateAcct >= " ).append( DB.TO_DATE( first.getStartDate()));
                } else {
                    log.log( Level.SEVERE,"First period not found" );
                }
            }
        }

        //

        int no = DB.executeUpdate( sb.toString());

        log.fine( "#" + no + " (Account_ID=" + p_Account_ID + ")" );
        log.finest( sb.toString());
    }    // createBalanceLine

    /**
     * Descripción de Método
     *
     */

    private void createDetailLines() {
        StringBuffer sb = new StringBuffer( "INSERT INTO T_ReportStatement " + "(AD_PInstance_ID, Fact_Acct_ID, LevelNo," + "DateAcct, Name, Description," + "AmtAcctDr, AmtAcctCr, Balance, Qty) " );

        sb.append( "SELECT " ).append( getAD_PInstance_ID()).append( ",Fact_Acct_ID,1," ).append( "DateAcct,NULL,NULL," + "AmtAcctDr, AmtAcctCr, AmtAcctDr-AmtAcctCr, Qty " + "FROM Fact_Acct " + "WHERE " ).append( m_parameterWhere ).append( " AND DateAcct BETWEEN " ).append( DB.TO_DATE( p_DateAcct_From )).append( " AND " ).append( DB.TO_DATE( p_DateAcct_To ));

        //

        int no = DB.executeUpdate( sb.toString());

        log.fine( "#" + no );
        log.finest( sb.toString());

        // Set Name,Description

        String sql_select = "SELECT e.Name, fa.Description " + "FROM Fact_Acct fa" + " INNER JOIN AD_Table t ON (fa.AD_Table_ID=t.AD_Table_ID)" + " INNER JOIN AD_Element e ON (t.TableName||'_ID'=e.ColumnName) " + "WHERE r.Fact_Acct_ID=fa.Fact_Acct_ID";

        // Translated Version ...

        sb = new StringBuffer( "UPDATE T_ReportStatement r SET (Name,Description)=(" ).append( sql_select ).append( ") " + "WHERE Fact_Acct_ID <> 0 AND AD_PInstance_ID=" ).append( getAD_PInstance_ID());

        //

        no = DB.executeUpdate( sb.toString());
        log.fine( "Name #" + no );
        log.finest( "Name - " + sb );
    }    // createDetailLines
}    // FinStatement



/*
 *  @(#)FinStatement.java   02.07.07
 * 
 *  Fin del fichero FinStatement.java
 *  
 *  Versión 2.2
 *
 */
