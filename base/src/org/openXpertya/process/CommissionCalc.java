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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;

import org.openXpertya.model.MCommission;
import org.openXpertya.model.MCommissionAmt;
import org.openXpertya.model.MCommissionDetail;
import org.openXpertya.model.MCommissionLine;
import org.openXpertya.model.MCommissionRun;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MUser;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.ErrorOXPSystem;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Language;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CommissionCalc extends SvrProcess {

    /** Descripción de Campos */

    private Timestamp p_StartDate;

    //

    /** Descripción de Campos */

    private Timestamp m_EndDate;

    /** Descripción de Campos */

    private MCommission m_com;

    //

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
            } else if( name.equals( "StartDate" )) {
                p_StartDate = ( Timestamp )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }
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
        log.info( "C_Commission_ID=" + getRecord_ID() + ", StartDate=" + p_StartDate );

        if( p_StartDate == null ) {
            p_StartDate = new Timestamp( System.currentTimeMillis());
        }

        m_com = new MCommission( getCtx(),getRecord_ID(),get_TrxName());

        if( m_com.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "No Commission" );
        }

        // Create Commission

        MCommissionRun comRun = new MCommissionRun( m_com );

        setStartEndDate();
        comRun.setStartDate( p_StartDate );

        // 01-Jan-2000 - 31-Jan-2001 - USD

        SimpleDateFormat format = DisplayType.getDateFormat( DisplayType.Date );
        String description = format.format( p_StartDate ) + " - " + format.format( m_EndDate ) + " - " + MCurrency.getISO_Code( getCtx(),m_com.getC_Currency_ID());

        comRun.setDescription( description );

        if( !comRun.save()) {
            throw new ErrorOXPSystem( "Could not save Commission Run" );
        }

        MCommissionLine[] lines = m_com.getLines();

        for( int i = 0;i < lines.length;i++ ) {

            // Amt for Line - Updated By Trigger

            MCommissionAmt comAmt = new MCommissionAmt( comRun,lines[ i ].getC_CommissionLine_ID());

            if( !comAmt.save()) {
                throw new ErrorOXPSystem( "Could not save Commission Amt" );
            }

            //

            StringBuffer sql = new StringBuffer();

            if( MCommission.DOCBASISTYPE_Receipt.equals( m_com.getDocBasisType())) {

                // Should go via Allocation - now only fully paid single allocation

                if( m_com.isListDetails()) {
                    sql.append( "SELECT h.C_Currency_ID, l.LineNetAmt, l.QtyInvoiced, " + "NULL, l.C_InvoiceLine_ID, p.DocumentNo||'_'||h.DocumentNo, COALESCE(prd.Value,l.Description), h.DateInvoiced " + "FROM C_Payment p" + " INNER JOIN C_Invoice h ON (p.C_Invoice_ID = h.C_Invoice_ID)" + " INNER JOIN C_InvoiceLine l ON (h.C_Invoice_ID = l.C_Invoice_ID)" + " LEFT OUTER JOIN M_Product prd ON (l.M_Product_ID = prd.M_Product_ID) " + "WHERE p.DocStatus IN ('CL','CO','RE')" + " AND h.IsSOTrx='Y'" + " AND p.AD_Client_ID = ?" + " AND p.DateTrx BETWEEN ? AND ?" );
                } else {
                    sql.append( "SELECT h.C_Currency_ID, SUM(l.LineNetAmt) AS Amt, SUM(l.QtyInvoiced) AS Qty, " + "NULL, NULL, NULL, NULL, MAX(h.DateInvoiced) " + "FROM C_Payment p" + " INNER JOIN C_Invoice h ON (p.C_Invoice_ID = h.C_Invoice_ID)" + " INNER JOIN C_InvoiceLine l ON (h.C_Invoice_ID = l.C_Invoice_ID) " + "WHERE p.DocStatus IN ('CL','CO','RE')" + " AND h.IsSOTrx='Y'" + " AND p.AD_Client_ID = ?" + " AND p.DateTrx BETWEEN ? AND ?" );
                }
            } else if( MCommission.DOCBASISTYPE_Order.equals( m_com.getDocBasisType())) {
                if( m_com.isListDetails()) {
                    sql.append( "SELECT h.C_Currency_ID, l.LineNetAmt, l.QtyOrdered, " + "l.C_OrderLine_ID, NULL, h.DocumentNo, COALESCE(prd.Value,l.Description),h.DateOrdered " + "FROM C_Order h" + " INNER JOIN C_OrderLine l ON (h.C_Order_ID = l.C_Order_ID)" + " LEFT OUTER JOIN M_Product prd ON (l.M_Product_ID = prd.M_Product_ID) " + "WHERE h.DocStatus IN ('CL','CO')" + " AND h.IsSOTrx='Y'" + " AND h.AD_Client_ID = ?" + " AND h.DateOrdered BETWEEN ? AND ?" );
                } else {
                    sql.append( "SELECT h.C_Currency_ID, SUM(l.LineNetAmt) AS Amt, SUM(l.QtyOrdered) AS Qty, " + "NULL, NULL, NULL, NULL, MAX(h.DateOrdered) " + "FROM C_Order h" + " INNER JOIN C_OrderLine l ON (h.C_Order_ID = l.C_Order_ID) " + "WHERE h.DocStatus IN ('CL','CO')" + " AND h.IsSOTrx='Y'" + " AND h.AD_Client_ID = ?" + " AND h.DateOrdered BETWEEN ? AND ?" );
                }
            } else    // Invoice Basis
            {
                if( m_com.isListDetails()) {
                    sql.append( "SELECT h.C_Currency_ID, l.LineNetAmt, l.QtyInvoiced, " + "NULL, l.C_InvoiceLine_ID, h.DocumentNo, COALESCE(prd.Value,l.Description),h.DateInvoiced " + "FROM C_Invoice h" + " INNER JOIN C_InvoiceLine l ON (h.C_Invoice_ID = l.C_Invoice_ID)" + " LEFT OUTER JOIN M_Product prd ON (l.M_Product_ID = prd.M_Product_ID) " + "WHERE h.DocStatus IN ('CL','CO','RE')" + " AND h.IsSOTrx='Y'" + " AND h.AD_Client_ID = ?" + " AND h.DateInvoiced BETWEEN ? AND ?" );
                } else {
                    sql.append( "SELECT h.C_Currency_ID, SUM(l.LineNetAmt) AS Amt, SUM(l.QtyInvoiced) AS Qty, " + "NULL, NULL, NULL, NULL, MAX(h.DateInvoiced) " + "FROM C_Invoice h" + " INNER JOIN C_InvoiceLine l ON (h.C_Invoice_ID = l.C_Invoice_ID) " + "WHERE h.DocStatus IN ('CL','CO','RE')" + " AND h.IsSOTrx='Y'" + " AND h.AD_Client_ID = ?" + " AND h.DateInvoiced BETWEEN ? AND ?" );
                }
            }

            // CommissionOrders/Invoices

            if( lines[ i ].isCommissionOrders()) {
                MUser[] users = MUser.getOfBPartner( getCtx(),m_com.getC_BPartner_ID());

                if( (users == null) || (users.length == 0) ) {
                    throw new ErrorUsuarioOXP( "Commission Business Partner has no Users/Contact" );
                }

                if( users.length == 1 ) {
                    int SalesRep_ID = users[ 0 ].getAD_User_ID();

                    sql.append( " AND h.SalesRep_ID=" ).append( SalesRep_ID );
                } else {
                    log.warning( "Not 1 User/Contact for C_BPartner_ID=" + m_com.getC_BPartner_ID() + " but " + users.length );
                    sql.append( " AND h.SalesRep_ID IN (SELECT AD_User_ID FROM AD_User WHERE C_BPartner_ID=" ).append( m_com.getC_BPartner_ID()).append( ")" );
                }
            }

            // Organization

            if( lines[ i ].getOrg_ID() != 0 ) {
                sql.append( " AND h.AD_Org_ID=" ).append( lines[ i ].getOrg_ID());
            }

            // BPartner

            if( lines[ i ].getC_BPartner_ID() != 0 ) {
                sql.append( " AND h.C_BPartner_ID=" ).append( lines[ i ].getC_BPartner_ID());
            }

            // BPartner Group

            if( lines[ i ].getC_BP_Group_ID() != 0 ) {
                sql.append( " AND h.C_BPartner_ID IN " + "(SELECT C_BPartner_ID FROM C_BPartner WHERE C_BP_Group_ID=" ).append( lines[ i ].getC_BP_Group_ID()).append( ")" );
            }

            // Sales Region

            if( lines[ i ].getC_SalesRegion_ID() != 0 ) {
                sql.append( " AND h.C_BPartner_Location_ID IN " + "(SELECT C_BPartner_Location_ID FROM C_BPartner_Location WHERE C_SalesRegion_ID=" ).append( lines[ i ].getC_SalesRegion_ID()).append( ")" );
            }

            // Product

            if( lines[ i ].getM_Product_ID() != 0 ) {
                sql.append( " AND l.M_Product_ID=" ).append( lines[ i ].getM_Product_ID());
            }

            // Product Category

            if( lines[ i ].getM_Product_Category_ID() != 0 ) {
                sql.append( " AND l.M_Product_ID IN " + "(SELECT M_Product_ID FROM M_Product WHERE M_Product_Category_ID=" ).append( lines[ i ].getM_Product_Category_ID()).append( ")" );
            }

            // Grouping

            if( !m_com.isListDetails()) {
                sql.append( " GROUP BY h.C_Currency_ID" );
            }

            //

            log.fine( "Line=" + lines[ i ].getLine() + " - " + sql );

            //

            createDetail( sql.toString(),comAmt );
            comAmt.calculateCommission();
            comAmt.save();
        }    // for all commission lines

        // comRun.updateFromAmt();
        // comRun.save();

        // Save Last Run

        m_com.setDateLastRun( p_StartDate );
        m_com.save();

        return "@C_CommissionRun_ID@ = " + comRun.getDocumentNo() + " - " + comRun.getDescription();
    }    // doIt

    /**
     * Descripción de Método
     *
     */

    private void setStartEndDate() {
        GregorianCalendar cal = new GregorianCalendar( Language.getLoginLanguage().getLocale());

        cal.setTimeInMillis( p_StartDate.getTime());
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        // Yearly

        if( MCommission.FREQUENCYTYPE_Yearly.equals( m_com.getFrequencyType())) {
            cal.set( Calendar.DAY_OF_YEAR,1 );
            p_StartDate = new Timestamp( cal.getTimeInMillis());

            //

            cal.add( Calendar.YEAR,1 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );
            m_EndDate = new Timestamp( cal.getTimeInMillis());
        }

        // Quarterly

        else if( MCommission.FREQUENCYTYPE_Quarterly.equals( m_com.getFrequencyType())) {
            cal.set( Calendar.DAY_OF_MONTH,1 );

            int month = cal.get( Calendar.MONTH );

            if( month < Calendar.APRIL ) {
                cal.set( Calendar.MONTH,Calendar.JANUARY );
            } else if( month < Calendar.JULY ) {
                cal.set( Calendar.MONTH,Calendar.APRIL );
            } else if( month < Calendar.OCTOBER ) {
                cal.set( Calendar.MONTH,Calendar.JULY );
            } else {
                cal.set( Calendar.MONTH,Calendar.OCTOBER );
            }

            p_StartDate = new Timestamp( cal.getTimeInMillis());

            //

            cal.add( Calendar.MONTH,3 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );
            m_EndDate = new Timestamp( cal.getTimeInMillis());
        }

        // Weekly

        else if( MCommission.FREQUENCYTYPE_Weekly.equals( m_com.getFrequencyType())) {
            cal.set( Calendar.DAY_OF_WEEK,Calendar.SUNDAY );
            p_StartDate = new Timestamp( cal.getTimeInMillis());

            //

            cal.add( Calendar.DAY_OF_YEAR,7 );
            m_EndDate = new Timestamp( cal.getTimeInMillis());
        }

        // Monthly

        else {
            cal.set( Calendar.DAY_OF_MONTH,1 );
            p_StartDate = new Timestamp( cal.getTimeInMillis());

            //

            cal.add( Calendar.MONTH,1 );
            cal.add( Calendar.DAY_OF_YEAR,-1 );
            m_EndDate = new Timestamp( cal.getTimeInMillis());
        }

        log.fine( "setStartEndDate = " + p_StartDate + " - " + m_EndDate );
    }    // setStartEndDate

    /**
     * Descripción de Método
     *
     *
     * @param sql
     * @param comAmt
     */

    private void createDetail( String sql,MCommissionAmt comAmt ) {
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_com.getAD_Client_ID());
            pstmt.setTimestamp( 2,p_StartDate );
            pstmt.setTimestamp( 3,m_EndDate );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {

                // CommissionAmount, C_Currency_ID, Amt, Qty,

                MCommissionDetail cd = new MCommissionDetail( comAmt,rs.getInt( 1 ),rs.getBigDecimal( 2 ),rs.getBigDecimal( 3 ));

                // C_OrderLine_ID, C_InvoiceLine_ID,

                cd.setLineIDs( rs.getInt( 4 ),rs.getInt( 5 ));

                // Reference, Info,

                String s = rs.getString( 6 );

                if( s != null ) {
                    cd.setReference( s );
                }

                s = rs.getString( 7 );

                if( s != null ) {
                    cd.setInfo( s );
                }

                // Date

                Timestamp date = rs.getTimestamp( 8 );

                cd.setConvertedAmt( date );

                //

                if( !cd.save()) {    // creates memory leak
                    throw new IllegalArgumentException( "CommissionCalc - Detail Not saved" );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createDetail",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }
    }    // createDetail
}    // CommissionCalc



/*
 *  @(#)CommissionCalc.java   02.07.07
 * 
 *  Fin del fichero CommissionCalc.java
 *  
 *  Versión 2.2
 *
 */
