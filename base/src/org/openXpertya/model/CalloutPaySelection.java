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



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutPaySelection extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String payAmt( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        // get invoice info

        Integer ii = ( Integer )mTab.getValue( "C_Invoice_ID" );

        if( ii == null ) {
            return "";
        }

        int C_Invoice_ID = ii.intValue();

        if( C_Invoice_ID == 0 ) {
            return "";
        }

        //

        BigDecimal OpenAmt     = ( BigDecimal )mTab.getValue( "OpenAmt" );
        BigDecimal PayAmt      = ( BigDecimal )mTab.getValue( "PayAmt" );
        BigDecimal DiscountAmt = ( BigDecimal )mTab.getValue( "DiscountAmt" );

        setCalloutActive( true );

        BigDecimal DifferenceAmt = OpenAmt.subtract( PayAmt ).subtract( DiscountAmt );

        log.fine( " - OpenAmt=" + OpenAmt + " - PayAmt=" + PayAmt + ", Discount=" + DiscountAmt + ", Difference=" + DifferenceAmt );
        mTab.setValue( "DifferenceAmt",DifferenceAmt );
        setCalloutActive( false );

        return "";
    }    // PaySel_PayAmt

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String invoice( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        // get value

        int C_Invoice_ID = (( Integer )value ).intValue();

        if( C_Invoice_ID == 0 ) {
            return "";
        }

        int C_BankAccount_ID = Env.getContextAsInt( ctx,WindowNo,"C_BankAccount_ID" );
        Timestamp PayDate = Env.getContextAsDate( ctx,"PayDate" );

        if( PayDate == null ) {
            PayDate = new Timestamp( System.currentTimeMillis());
        }

        setCalloutActive( true );

        BigDecimal OpenAmt     = Env.ZERO;
        BigDecimal DiscountAmt = Env.ZERO;
        Boolean    IsSOTrx     = Boolean.FALSE;
        String     sql         = "SELECT currencyConvert(invoiceOpen(i.C_Invoice_ID, 0), i.C_Currency_ID," + "ba.C_Currency_ID, i.DateInvoiced, i.C_ConversionType_ID, i.AD_Client_ID, i.AD_Org_ID)," + " paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced, ?), i.IsSOTrx " + "FROM C_Invoice_v i, C_BankAccount ba " + "WHERE i.C_Invoice_ID=? AND ba.C_BankAccount_ID=?";    // #1..2

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Invoice_ID );
            pstmt.setInt( 2,C_BankAccount_ID );
            pstmt.setTimestamp( 3,PayDate );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                OpenAmt     = rs.getBigDecimal( 1 );
                DiscountAmt = rs.getBigDecimal( 2 );
                IsSOTrx     = new Boolean( "Y".equals( rs.getString( 3 )));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"PaySel_Invoice",e );
        }

        log.fine( " - OpenAmt=" + OpenAmt + " (Invoice=" + C_Invoice_ID + ",BankAcct=" + C_BankAccount_ID + ")" );
        mTab.setValue( "OpenAmt",OpenAmt );
        mTab.setValue( "PayAmt",OpenAmt.subtract( DiscountAmt ));
        mTab.setValue( "DiscountAmt",DiscountAmt );
        mTab.setValue( "DifferenceAmt",Env.ZERO );
        mTab.setValue( "IsSOTrx",IsSOTrx );
        setCalloutActive( false );

        return "";
    }    // PaySel_Invoice
}    // CalloutPaySelection



/*
 *  @(#)CalloutPaySelection.java   02.07.07
 * 
 *  Fin del fichero CalloutPaySelection.java
 *  
 *  Versión 2.2
 *
 */
