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
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TrialBalance extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_AcctSchema_ID = 0;

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

    private String p_AccountValue_From = null;

    /** Descripción de Campos */

    private String p_AccountValue_To = null;

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

    private String p_PostingType = "A";

    /** Descripción de Campos */

    private int p_AD_OrgTrx_ID = 0;

    /** Descripción de Campos */

    private int p_C_LocFrom_ID = 0;

    /** Descripción de Campos */

    private int p_C_LocTo_ID = 0;

    /** Descripción de Campos */

    private int p_User1_ID = 0;

    /** Descripción de Campos */

    private int p_User2_ID = 0;

    /** Descripción de Campos */

    private StringBuffer m_parameterWhere = new StringBuffer();

    /** Descripción de Campos */

    private MElementValue m_acct = null;

    /** Descripción de Campos */

    private long m_start = System.currentTimeMillis();

    /** Descripción de Campos */

    private static String s_insert = "INSERT INTO T_TrialBalance " + "(AD_PInstance_ID, Fact_Acct_ID," + " AD_Client_ID, AD_Org_ID, Created,CreatedBy, Updated,UpdatedBy," + " C_AcctSchema_ID, Account_ID, AccountValue, DateTrx, DateAcct, C_Period_ID," + " AD_Table_ID, Record_ID, Line_ID," + " GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType," + " C_Currency_ID, AmtSourceDr, AmtSourceCr, AmtSourceBalance," + " AmtAcctDr, AmtAcctCr, AmtAcctBalance, C_UOM_ID, Qty," + " M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID," + " C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID," + " User1_ID, User2_ID, A_Asset_ID, Description)";

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        StringBuffer sb = new StringBuffer( "prepare - AD_PInstance_ID=" ).append( getAD_PInstance_ID());

        // Parameter

        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "C_Period_ID" )) {
                p_C_Period_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "DateAcct" )) {
                p_DateAcct_From = ( Timestamp )para[ i ].getParameter();
                p_DateAcct_To   = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equals( "AD_Org_ID" )) {
                p_AD_Org_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "Account_ID" )) {
                p_Account_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "AccountValue" )) {
                p_AccountValue_From = ( String )para[ i ].getParameter();
                p_AccountValue_To   = ( String )para[ i ].getParameter_To();
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
            } else if( name.equals( "PostingType" )) {
                p_PostingType = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        // Mandatory C_AcctSchema_ID

        m_parameterWhere.append( "C_AcctSchema_ID=" ).append( p_C_AcctSchema_ID );

        // Optional Account_ID

        if( p_Account_ID != 0 ) {
            m_parameterWhere.append( " AND " ).append( MReportTree.getWhereClause( getCtx(),MAcctSchemaElement.ELEMENTTYPE_Account,p_Account_ID ));
        }

        if( (p_AccountValue_From != null) && (p_AccountValue_From.length() == 0) ) {
            p_AccountValue_From = null;
        }

        if( (p_AccountValue_To != null) && (p_AccountValue_To.length() == 0) ) {
            p_AccountValue_To = null;
        }

        if( (p_AccountValue_From != null) && (p_AccountValue_To != null) ) {
            m_parameterWhere.append( " AND (Account_ID IS NULL OR EXISTS (SELECT * FROM C_ElementValue ev " ).append( "WHERE Account_ID=ev.C_ElementValue_ID AND ev.Value >= " ).append( DB.TO_STRING( p_AccountValue_From )).append( " AND ev.Value <= " ).append( DB.TO_STRING( p_AccountValue_To )).append( "))" );
        } else if( (p_AccountValue_From != null) && (p_AccountValue_To == null) ) {
            m_parameterWhere.append( " AND (Account_ID IS NULL OR EXISTS (SELECT * FROM C_ElementValue ev " ).append( "WHERE Account_ID=ev.C_ElementValue_ID AND ev.Value >= " ).append( DB.TO_STRING( p_AccountValue_From )).append( "))" );
        } else if( (p_AccountValue_From == null) && (p_AccountValue_To != null) ) {
            m_parameterWhere.append( " AND (Account_ID IS NULL OR EXISTS (SELECT * FROM C_ElementValue ev " ).append( "WHERE Account_ID=ev.C_ElementValue_ID AND ev.Value <= " ).append( DB.TO_STRING( p_AccountValue_To )).append( "))" );
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

        // Mandatory Posting Type

        m_parameterWhere.append( " AND PostingType='" ).append( p_PostingType ).append( "'" );

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
            log.log( Level.SEVERE,"setDateAcct",e );
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
        createBalanceLine();
        createDetailLines();

        // int AD_PrintFormat_ID = 134;
        // getProcessInfo().setTransientObject (MPrintFormat.get (getCtx(), AD_PrintFormat_ID, false));

        log.fine(( System.currentTimeMillis() - m_start ) + " ms" );

        return "";
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void createBalanceLine() {
        StringBuffer sql = new StringBuffer( s_insert );

        // (AD_PInstance_ID, Fact_Acct_ID,

        sql.append( "SELECT " ).append( getAD_PInstance_ID()).append( ",0," );

        // AD_Client_ID, AD_Org_ID, Created,CreatedBy, Updated,UpdatedBy,

        sql.append( getAD_Client_ID()).append( "," );

        if( p_AD_Org_ID == 0 ) {
            sql.append( "0" );
        } else {
            sql.append( p_AD_Org_ID );
        }

        sql.append( ", SysDate," ).append( getAD_User_ID()).append( ",SysDate," ).append( getAD_User_ID()).append( "," );

        // C_AcctSchema_ID, Account_ID, AccountValue, DateTrx, DateAcct, C_Period_ID,

        sql.append( p_C_AcctSchema_ID ).append( "," );

        if( p_Account_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_Account_ID );
        }

        if( p_AccountValue_From != null ) {
            sql.append( "," ).append( DB.TO_STRING( p_AccountValue_From ));
        } else if( p_AccountValue_To != null ) {
            sql.append( ",' '" );
        } else {
            sql.append( ",null" );
        }

        Timestamp balanceDay = p_DateAcct_From;    // TimeUtil.addDays(p_DateAcct_From, -1);

        sql.append( ",null," ).append( DB.TO_DATE( balanceDay,true )).append( "," );

        if( p_C_Period_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_Period_ID );
        }

        sql.append( "," );

        // AD_Table_ID, Record_ID, Line_ID,

        sql.append( "null,null,null," );

        // GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType,

        sql.append( "null,null,null,null,'" ).append( p_PostingType ).append( "'," );

        // C_Currency_ID, AmtSourceDr, AmtSourceCr, AmtSourceBalance,

        sql.append( "null,null,null,null," );

        // AmtAcctDr, AmtAcctCr, AmtAcctBalance, C_UOM_ID, Qty,

        sql.append( " COALESCE(SUM(AmtAcctDr),0),COALESCE(SUM(AmtAcctCr),0)," + "COALESCE(SUM(AmtAcctDr),0)-COALESCE(SUM(AmtAcctCr),0)," + " null,COALESCE(SUM(Qty),0)," );

        // M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID,

        if( p_M_Product_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_M_Product_ID );
        }

        sql.append( "," );

        if( p_C_BPartner_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_BPartner_ID );
        }

        sql.append( "," );

        if( p_AD_OrgTrx_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_AD_OrgTrx_ID );
        }

        sql.append( "," );

        if( p_C_LocFrom_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_LocFrom_ID );
        }

        sql.append( "," );

        if( p_C_LocTo_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_LocTo_ID );
        }

        sql.append( "," );

        // C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID,

        if( p_C_SalesRegion_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_SalesRegion_ID );
        }

        sql.append( "," );

        if( p_C_Project_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_Project_ID );
        }

        sql.append( "," );

        if( p_C_Campaign_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_Campaign_ID );
        }

        sql.append( "," );

        if( p_C_Activity_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_C_Activity_ID );
        }

        sql.append( "," );

        // User1_ID, User2_ID, A_Asset_ID, Description)

        if( p_User1_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_User1_ID );
        }

        sql.append( "," );

        if( p_User2_ID == 0 ) {
            sql.append( "null" );
        } else {
            sql.append( p_User2_ID );
        }

        sql.append( ", null,null" );

        //

        sql.append( " FROM Fact_Acct WHERE AD_Client_ID=" ).append( getAD_Client_ID()).append( " AND " ).append( m_parameterWhere ).append( " AND DateAcct < " ).append( DB.TO_DATE( p_DateAcct_From,true ));

        // Start Beginning of Year

        if( p_Account_ID > 0 ) {
            m_acct = new MElementValue( getCtx(),p_Account_ID,get_TrxName());

            if( !m_acct.isBalanceSheet()) {
                MPeriod first = MPeriod.getFirstInYear( getCtx(),p_DateAcct_From );

                if( first != null ) {
                    sql.append( " AND DateAcct >= " ).append( DB.TO_DATE( first.getStartDate(),true ));
                } else {
                    log.log( Level.SEVERE,"createBalanceLine - first period not found" );
                }
            }
        }

        //

        int no = DB.executeUpdate( sql.toString());

        if( no == 0 ) {
            log.fine( "createBalanceLine - " + sql );
        }

        log.fine( "createBalanceLine #" + no + " (Account_ID=" + p_Account_ID + ")" );
    }    // createBalanceLine

    /**
     * Descripción de Método
     *
     */

    private void createDetailLines() {
        StringBuffer sql = new StringBuffer( s_insert );

        // (AD_PInstance_ID, Fact_Acct_ID,

        sql.append( "SELECT " ).append( getAD_PInstance_ID()).append( ",Fact_Acct_ID," );

        // AD_Client_ID, AD_Org_ID, Created,CreatedBy, Updated,UpdatedBy,

        sql.append( getAD_Client_ID()).append( ",AD_Org_ID,Created,CreatedBy, Updated,UpdatedBy," );

        // C_AcctSchema_ID, Account_ID, DateTrx, AccountValue, DateAcct, C_Period_ID,

        sql.append( "C_AcctSchema_ID, Account_ID, null, DateTrx, DateAcct, C_Period_ID," );

        // AD_Table_ID, Record_ID, Line_ID,

        sql.append( "AD_Table_ID, Record_ID, Line_ID," );

        // GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType,

        sql.append( "GL_Category_ID, GL_Budget_ID, C_Tax_ID, M_Locator_ID, PostingType," );

        // C_Currency_ID, AmtSourceDr, AmtSourceCr, AmtSourceBalance,

        sql.append( "C_Currency_ID, AmtSourceDr,AmtSourceCr, AmtSourceDr-AmtSourceCr," );

        // AmtAcctDr, AmtAcctCr, AmtAcctBalance, C_UOM_ID, Qty,

        sql.append( " AmtAcctDr,AmtAcctCr, AmtAcctDr-AmtAcctCr, C_UOM_ID,Qty," );

        // M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID,

        sql.append( "M_Product_ID, C_BPartner_ID, AD_OrgTrx_ID, C_LocFrom_ID,C_LocTo_ID," );

        // C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID,

        sql.append( "C_SalesRegion_ID, C_Project_ID, C_Campaign_ID, C_Activity_ID," );

        // User1_ID, User2_ID, A_Asset_ID, Description)

        sql.append( "User1_ID, User2_ID, A_Asset_ID, Description" );

        //

        sql.append( " FROM Fact_Acct WHERE AD_Client_ID=" ).append( getAD_Client_ID()).append( " AND " ).append( m_parameterWhere ).append( " AND DateAcct >= " ).append( DB.TO_DATE( p_DateAcct_From,true )).append( " AND TRUNC(DateAcct) <= " ).append( DB.TO_DATE( p_DateAcct_To,true ));

        //

        int no = DB.executeUpdate( sql.toString());

        if( no == 0 ) {
            log.fine( "createDetailLines - " + sql );
        }

        log.fine( "createDetailLines #" + no + " (Account_ID=" + p_Account_ID + ")" );

        // Update AccountValue

        String sql2 = "UPDATE T_TrialBalance tb SET AccountValue = " + "(SELECT Value FROM C_ElementValue ev WHERE ev.C_ElementValue_ID=tb.Account_ID) " + "WHERE tb.Account_ID IS NOT NULL";

        no = DB.executeUpdate( sql2 );

        if( no > 0 ) {
            log.fine( "createDetailLines Set AccountValue #" + no );
        }
    }    // createDetailLines
}    // TrialBalance



/*
 *  @(#)TrialBalance.java   02.07.07
 * 
 *  Fin del fichero TrialBalance.java
 *  
 *  Versión 2.2
 *
 */
