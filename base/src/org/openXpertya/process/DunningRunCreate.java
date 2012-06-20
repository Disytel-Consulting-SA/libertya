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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MDunningRun;
import org.openXpertya.model.MDunningRunEntry;
import org.openXpertya.model.MDunningRunLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DunningRunCreate extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_IncludeInDispute = false;

    /** Descripción de Campos */

    private boolean p_OnlySOTrx = false;

    /** Descripción de Campos */

    private int p_SalesRep_ID = 0;

    /** Descripción de Campos */

    private int p_C_Currency_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_C_BP_Group_ID = 0;

    /** Descripción de Campos */

    private int p_C_DunningRun_ID = 0;

    /** Descripción de Campos */

    private MDunningRun m_run = null;

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
            } else if( name.equals( "IncludeInDispute" )) {
                p_IncludeInDispute = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "OnlySOTrx" )) {
                p_OnlySOTrx = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "SalesRep_ID" )) {
                p_SalesRep_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_Currency_ID" )) {
                p_C_Currency_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BP_Group_ID" )) {
                p_C_BP_Group_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_DunningRun_ID = getRecord_ID();
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
        log.info( "C_DunningRun_ID=" + p_C_DunningRun_ID + "" + ", C_BP_Group_ID=" + p_C_BP_Group_ID + ", C_BPartner_ID=" + p_C_BPartner_ID );
        m_run = new MDunningRun( getCtx(),p_C_DunningRun_ID,get_TrxName());

        if( m_run.getID() == 0 ) {
            throw new IllegalArgumentException( "Not found MDunningRun" );
        }

        if( !m_run.deleteEntries( true )) {
            throw new IllegalArgumentException( "Cannot delete existing entries" );
        }

        if( p_SalesRep_ID == 0 ) {
            throw new IllegalArgumentException( "No SalesRep" );
        }

        if( p_C_Currency_ID == 0 ) {
            throw new IllegalArgumentException( "No Currency" );
        }

        //

        int inv = addInvoices();
        int pay = addPayments();

        return "@C_Invoice_ID@ #" + inv + " - @C_Payment_ID@=" + pay;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int addInvoices() {
        int    count = 0;
        String sql   = "SELECT i.C_Invoice_ID, i.C_Currency_ID," + " i.GrandTotal*i.MultiplierAP," + " invoiceOpen(i.C_Invoice_ID,i.C_InvoicePaySchedule_ID)*MultiplierAP," + " COALESCE(daysBetween(?,ips.DueDate),paymentTermDueDays(i.C_PaymentTerm_ID,i.DateInvoiced,?)),"    // ##1/2
                       + " i.IsInDispute, i.C_BPartner_ID " + "FROM C_Invoice_v i " + " LEFT OUTER JOIN C_InvoicePaySchedule ips ON (i.C_InvoicePaySchedule_ID=ips.C_InvoicePaySchedule_ID) " + "WHERE i.IsPaid='N' AND i.AD_Client_ID=?"    // ##3
                       + " AND i.DocStatus IN ('CO','CL')"

        // Only BP with Dunning defined

        + " AND EXISTS (SELECT * FROM C_BPartner bp " + "WHERE i.C_BPartner_ID=bp.C_BPartner_ID" + " AND bp.C_Dunning_ID=(SELECT C_Dunning_ID FROM C_DunningLevel WHERE C_DunningLevel_ID=?))";    // ##4

        if( p_C_BPartner_ID != 0 ) {
            sql += " AND i.C_BPartner_ID=?";                                                                                     // ##5
        } else if( p_C_BP_Group_ID != 0 ) {
            sql += " AND EXISTS (SELECT * FROM C_BPartner bp WHERE i.C_BPartner_ID=bp.C_BPartner_ID AND bp.C_BP_Group_ID=?)";    // ##5
        }

        if( p_OnlySOTrx ) {
            sql += " AND i.IsSOTrx='Y'";
        }

        // log.info(sql);

        // Sub Query

        String sql2 = "SELECT COUNT(*), COALESCE(TRUNC(SysDate-MAX(dr.DunningDate)),0) " + "FROM C_DunningRun dr" + " INNER JOIN C_DunningRunEntry dre ON (dr.C_DunningRun_ID=dre.C_DunningRun_ID)" + " INNER JOIN C_DunningRunLine drl ON (dre.C_DunningRunEntry_ID=drl.C_DunningRunEntry_ID) " + "WHERE drl.Processed='Y' AND drl.C_Invoice_ID=?";
        BigDecimal        DaysAfterDue       = m_run.getLevel().getDaysAfterDue();
        int               DaysBetweenDunning = m_run.getLevel().getDaysBetweenDunning();
        PreparedStatement pstmt              = null;
        PreparedStatement pstmt2             = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setTimestamp( 1,m_run.getDunningDate());
            pstmt.setTimestamp( 2,m_run.getDunningDate());
            pstmt.setInt( 3,m_run.getAD_Client_ID());
            pstmt.setInt( 4,m_run.getC_DunningLevel_ID());

            if( p_C_BPartner_ID != 0 ) {
                pstmt.setInt( 5,p_C_BPartner_ID );
            } else if( p_C_BP_Group_ID != 0 ) {
                pstmt.setInt( 5,p_C_BP_Group_ID );
            }

            //

            pstmt2 = DB.prepareStatement( sql2,get_TrxName());

            //

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int        C_Invoice_ID  = rs.getInt( 1 );
                int        C_Currency_ID = rs.getInt( 2 );
                BigDecimal GrandTotal    = rs.getBigDecimal( 3 );
                BigDecimal Open          = rs.getBigDecimal( 4 );
                int        DaysDue       = rs.getInt( 5 );
                boolean    IsInDispute   = "Y".equals( rs.getString( 6 ));
                int        C_BPartner_ID = rs.getInt( 7 );

                //

                if( !p_IncludeInDispute && IsInDispute ) {
                    continue;
                }

                if( DaysDue < DaysAfterDue.intValue()) {
                    continue;
                }

                if( Env.ZERO.compareTo( Open ) == 0 ) {
                    continue;
                }

                //

                int TimesDunned   = 0;
                int DaysAfterLast = 0;

                // SubQuery

                pstmt2.setInt( 1,C_Invoice_ID );

                ResultSet rs2 = pstmt2.executeQuery();

                if( rs2.next()) {
                    TimesDunned   = rs2.getInt( 1 );
                    DaysAfterLast = rs2.getInt( 2 );
                }

                rs2.close();

                // SubQuery

                if( (DaysBetweenDunning != 0) && (DaysAfterLast < DaysBetweenDunning) ) {
                    continue;
                }

                //

                createInvoiceLine( C_Invoice_ID,C_Currency_ID,GrandTotal,Open,DaysDue,IsInDispute,C_BPartner_ID,TimesDunned,DaysAfterLast );
                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
            pstmt2.close();
            pstmt2 = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"addInvoices",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            if( pstmt2 != null ) {
                pstmt2.close();
            }

            pstmt  = null;
            pstmt2 = null;
        } catch( Exception e ) {
            pstmt  = null;
            pstmt2 = null;
        }

        return count;
    }    // addInvoices

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param C_Currency_ID
     * @param GrandTotal
     * @param Open
     * @param DaysDue
     * @param IsInDispute
     * @param C_BPartner_ID
     * @param TimesDunned
     * @param DaysAfterLast
     */

    private void createInvoiceLine( int C_Invoice_ID,int C_Currency_ID,BigDecimal GrandTotal,BigDecimal Open,int DaysDue,boolean IsInDispute,int C_BPartner_ID,int TimesDunned,int DaysAfterLast ) {
        MDunningRunEntry entry = m_run.getEntry( C_BPartner_ID,p_C_Currency_ID,p_SalesRep_ID );

        if( entry.getID() == 0 ) {
            if( !entry.save()) {
                throw new IllegalStateException( "Cannot save MDunningRunEntry" );
            }
        }

        //

        MDunningRunLine line = new MDunningRunLine( entry );

        line.setInvoice( C_Invoice_ID,C_Currency_ID,GrandTotal,Open,DaysDue,IsInDispute,TimesDunned,DaysAfterLast );

        if( !line.save()) {
            throw new IllegalStateException( "Cannot save MDunningRunLine" );
        }
    }    // createInvoiceLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int addPayments() {
        String sql = "SELECT C_Payment_ID, C_Currency_ID, PayAmt," + " paymentAvailable(C_Payment_ID), C_BPartner_ID " + "FROM C_Payment_v p " + "WHERE AD_Client_ID=?"    // ##1
                     + " AND IsAllocated='N' AND C_BPartner_ID IS NOT NULL" + " AND C_Charge_ID IS NULL" + " AND DocStatus IN ('CO','CL')"

        // Only BP with Dunning defined

        + " AND EXISTS (SELECT * FROM C_BPartner bp " + "WHERE p.C_BPartner_ID=bp.C_BPartner_ID" + " AND bp.C_Dunning_ID=(SELECT C_Dunning_ID FROM C_DunningLevel WHERE C_DunningLevel_ID=?))";    // ##2

        if( p_C_BPartner_ID != 0 ) {
            sql += " AND C_BPartner_ID=?";                                                                                       // ##3
        } else if( p_C_BP_Group_ID != 0 ) {
            sql += " AND EXISTS (SELECT * FROM C_BPartner bp WHERE p.C_BPartner_ID=bp.C_BPartner_ID AND bp.C_BP_Group_ID=?)";    // ##3
        }

        if( p_OnlySOTrx ) {
            sql += " AND IsReceipt='Y'";
        }

        int               count = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getAD_Client_ID());
            pstmt.setInt( 2,m_run.getC_DunningLevel_ID());

            if( p_C_BPartner_ID != 0 ) {
                pstmt.setInt( 3,p_C_BPartner_ID );
            } else if( p_C_BP_Group_ID != 0 ) {
                pstmt.setInt( 3,p_C_BP_Group_ID );
            }

            if( p_OnlySOTrx ) {
                sql += " AND i.IsSOTrx='Y'";
            }

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int        C_Payment_ID  = rs.getInt( 1 );
                int        C_Currency_ID = rs.getInt( 2 );
                BigDecimal PayAmt        = rs.getBigDecimal( 3 ).negate();
                BigDecimal OpenAmt       = rs.getBigDecimal( 4 ).negate();
                int        C_BPartner_ID = rs.getInt( 5 );

                //

                if( Env.ZERO.compareTo( OpenAmt ) == 0 ) {
                    continue;
                }

                //

                createPaymentLine( C_Payment_ID,C_Currency_ID,PayAmt,OpenAmt,C_BPartner_ID );
                count++;
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"addPayments - " + sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return count;
    }    // addPayments

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     * @param C_Currency_ID
     * @param PayAmt
     * @param OpenAmt
     * @param C_BPartner_ID
     */

    private void createPaymentLine( int C_Payment_ID,int C_Currency_ID,BigDecimal PayAmt,BigDecimal OpenAmt,int C_BPartner_ID ) {
        MDunningRunEntry entry = m_run.getEntry( C_BPartner_ID,p_C_Currency_ID,p_SalesRep_ID );

        if( entry.getID() == 0 ) {
            if( !entry.save()) {
                throw new IllegalStateException( "Cannot save MDunningRunEntry" );
            }
        }

        //

        MDunningRunLine line = new MDunningRunLine( entry );

        line.setPayment( C_Payment_ID,C_Currency_ID,PayAmt,OpenAmt );

        if( !line.save()) {
            throw new IllegalStateException( "Cannot save MDunningRunLine" );
        }
    }    // createPaymentLine
}    // DunningRunCreate



/*
 *  @(#)DunningRunCreate.java   02.07.07
 * 
 *  Fin del fichero DunningRunCreate.java
 *  
 *  Versión 2.2
 *
 */
