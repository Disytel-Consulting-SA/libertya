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



package org.openXpertya.sqlj;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Descripción de Clase
 *
 *
 * @versión    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PaymentTerm {

    /**
     * Descripción de Método
     *
     *
     * @param p_C_PaymentTerm_ID
     * @param p_DocDate
     * @param p_PayDate
     *
     * @return
     *
     * @throws SQLException
     */

    public static int dueDays( int p_C_PaymentTerm_ID,Timestamp p_DocDate,Timestamp p_PayDate ) throws SQLException {

        // Parameter

        if( (p_C_PaymentTerm_ID == 0) || (p_DocDate == null) ) {
            return 0;
        }

        // Pay Date

        Timestamp PayDate = p_PayDate;

        if( PayDate == null ) {
            PayDate = new Timestamp( System.currentTimeMillis());
        }

        PayDate = OpenXpertya.trunc( PayDate );

        // Get Due Date

        Timestamp DueDate = null;
        String    sql     = "SELECT * " + "FROM C_PaymentTerm " + "WHERE C_PaymentTerm_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_PaymentTerm_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            boolean IsDueFixed = "Y".equals( rs.getString( "IsDueFixed" ));

            //

            if( IsDueFixed ) {
                int FixMonthDay    = rs.getInt( "FixMonthDay" );
                int FixMonthOffset = rs.getInt( "FixMonthOffset" );
                int FixMonthCutoff = rs.getInt( "FixMonthCutoff" );

                //

                DueDate = calculateDateDue( p_DocDate,FixMonthDay,FixMonthOffset,FixMonthCutoff );
            } else {
                int NetDays = rs.getInt( "NetDays" );

                DueDate = OpenXpertya.addDays( p_DocDate,NetDays );
            }
        }

        rs.close();
        pstmt.close();

        //

        if( DueDate == null ) {
            return 0;
        }

        return OpenXpertya.getDaysBetween( DueDate,PayDate );
    }    // dueDays

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Invoice_ID
     * @param p_PayDate
     *
     * @return
     *
     * @throws SQLException
     */

    public static int invoiceDueDays( int p_C_Invoice_ID,Timestamp p_PayDate ) throws SQLException {

        // Parameter

        if( p_C_Invoice_ID == 0 ) {
            return 0;
        }

        int    retValue = 0;
        String sql      = "SELECT C_PaymentTerm_ID, DateInvoiced " + "FROM C_Invoice " + "WHERE C_Invoice_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Invoice_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            int       C_PaymentTerm_ID = rs.getInt( 1 );
            Timestamp DocDate          = rs.getTimestamp( 2 );

            retValue = dueDays( C_PaymentTerm_ID,DocDate,p_PayDate );
        }

        rs.close();
        pstmt.close();

        return retValue;
    }    // invoiceDueDays

    /**
     * Descripción de Método
     *
     *
     * @param p_C_PaymentTerm_ID
     * @param p_DocDate
     *
     * @return
     *
     * @throws SQLException
     */

    public static Timestamp dueDate( int p_C_PaymentTerm_ID,Timestamp p_DocDate ) throws SQLException {

        // Parameter

        if( (p_C_PaymentTerm_ID == 0) || (p_DocDate == null) ) {
            return null;
        }

        // Due Date

        Timestamp DueDate = OpenXpertya.trunc( p_DocDate );

        // Get Due Date

        String sql = "SELECT * " + "FROM C_PaymentTerm " + "WHERE C_PaymentTerm_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_PaymentTerm_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            boolean IsDueFixed = "Y".equals( rs.getString( "IsDueFixed" ));

            //

            if( IsDueFixed ) {
                int FixMonthDay    = rs.getInt( "FixMonthDay" );
                int FixMonthOffset = rs.getInt( "FixMonthOffset" );
                int FixMonthCutoff = rs.getInt( "FixMonthCutoff" );

                //

                DueDate = calculateDateDue( p_DocDate,FixMonthDay,FixMonthOffset,FixMonthCutoff );
            } else {
                int NetDays = rs.getInt( "NetDays" );

                if( NetDays != 0 ) {
                    DueDate = OpenXpertya.addDays( DueDate,NetDays );
                }
            }
        }

        rs.close();
        pstmt.close();

        //

        return DueDate;
    }    // dueDate

    /**
     * Descripción de Método
     *
     *
     * @param p_C_Invoice_ID
     *
     * @return
     *
     * @throws SQLException
     */

    public static Timestamp invoiceDueDate( int p_C_Invoice_ID ) throws SQLException {

        // Parameter

        if( p_C_Invoice_ID == 0 ) {
            return null;
        }

        // Due Date

        Timestamp DueDate = null;
        String    sql     = "SELECT C_PaymentTerm_ID, DateInvoiced " + "FROM C_Invoice " + "WHERE C_Invoice_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_Invoice_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            int       C_PaymentTerm_ID = rs.getInt( 1 );
            Timestamp DocDate          = rs.getTimestamp( 2 );

            DueDate = dueDate( C_PaymentTerm_ID,DocDate );
        }

        rs.close();
        pstmt.close();

        //

        return DueDate;
    }    // invoiceDueDate

    /**
     * Descripción de Método
     *
     *
     * @param DocDate
     * @param FixMonthDay
     * @param FixMonthOffset
     * @param FixMonthCutoff
     *
     * @return
     */

    private static Timestamp calculateDateDue( Timestamp DocDate,int FixMonthDay,int FixMonthOffset,int FixMonthCutoff ) {
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime( DocDate );
        cal.set( Calendar.HOUR_OF_DAY,0 );
        cal.set( Calendar.MINUTE,0 );
        cal.set( Calendar.SECOND,0 );
        cal.set( Calendar.MILLISECOND,0 );

        // Cutoff

        cal.set( Calendar.DAY_OF_MONTH,FixMonthCutoff );

        if( DocDate.after( cal.getTime())) {
            FixMonthOffset += 1;
        }

        cal.add( Calendar.MONTH,FixMonthOffset );

        // Due Date

        int maxDay = cal.getActualMaximum( Calendar.DAY_OF_MONTH );

        if( FixMonthDay > maxDay ) {                                    // 32 -> 28
            cal.set( Calendar.DAY_OF_MONTH,maxDay );
        } else if( (FixMonthDay >= 30) && (maxDay > FixMonthDay) ) {    // 30 -> 31
            cal.set( Calendar.DAY_OF_MONTH,maxDay );
        } else {
            cal.set( Calendar.DAY_OF_MONTH,FixMonthDay );
        }

        //

        java.util.Date temp = cal.getTime();

        return new Timestamp( temp.getTime());
    }    // calculateDateDue

    /**
     * Descripción de Método
     *
     *
     * @param p_Amount
     * @param p_C_PaymentTerm_ID
     * @param p_DocDate
     * @param p_PayDate
     *
     * @return
     *
     * @throws SQLException
     */

    public static BigDecimal discount( BigDecimal p_Amount,int p_C_PaymentTerm_ID,Timestamp p_DocDate,Timestamp p_PayDate ) throws SQLException {

        // No Data - No Discount

        if( (p_Amount == null) || (p_C_PaymentTerm_ID == 0) || (p_DocDate == null) ) {
            return null;
        }

        if( p_Amount.signum() == 0 ) {
            return OpenXpertya.ZERO;
        }

        // Parameters

        Timestamp PayDate = p_PayDate;

        if( PayDate == null ) {
            PayDate = new Timestamp( System.currentTimeMillis());
        }

        PayDate = OpenXpertya.trunc( PayDate );

        //

        int precision = 2;

        //

        BigDecimal discount = null;
        String     sql      = "SELECT * " + "FROM C_PaymentTerm " + "WHERE C_PaymentTerm_ID=?";
        PreparedStatement pstmt = OpenXpertya.prepareStatement( sql );

        pstmt.setInt( 1,p_C_PaymentTerm_ID );

        ResultSet rs = pstmt.executeQuery();

        if( rs.next()) {
            int     DiscountDays      = rs.getInt( "DiscountDays" );
            int     DiscountDays2     = rs.getInt( "DiscountDays2" );
            int     GraceDays         = rs.getInt( "GraceDays" );
            boolean IsNextBusinessDay = "Y".equals( rs.getString( "IsNextBusinessDay" ));
            BigDecimal Discount  = rs.getBigDecimal( "Discount" );
            BigDecimal Discount2 = rs.getBigDecimal( "Discount2" );

            //

            Timestamp Discount1Date = OpenXpertya.addDays( p_DocDate,DiscountDays + GraceDays );
            Timestamp Discount2Date = OpenXpertya.addDays( p_DocDate,DiscountDays2 + GraceDays );

            // Next Business Day

            if( IsNextBusinessDay ) {
                Discount1Date = OpenXpertya.nextBusinessDay( Discount1Date );
                Discount2Date = OpenXpertya.nextBusinessDay( Discount2Date );
            }

            // Discount 1

            if( !PayDate.after( Discount1Date )) {
                discount = p_Amount.multiply( Discount );

                // Discount 2

            } else if( !PayDate.after( Discount2Date )) {
                discount = p_Amount.multiply( Discount2 );
            } else {
                discount = OpenXpertya.ZERO;
            }

            // Divide

            if( discount.signum() != 0 ) {
                discount = discount.setScale( precision ).divide( new BigDecimal( 100 ),BigDecimal.ROUND_HALF_UP );
            }
        }

        rs.close();
        pstmt.close();

        //

        return discount;
    }    // discount
}    // PaymentTerm



/*
 *  @(#)PaymentTerm.java   23.03.06
 * 
 *  Fin del fichero PaymentTerm.java
 *  
 *  Versión 2.2
 *
 */
