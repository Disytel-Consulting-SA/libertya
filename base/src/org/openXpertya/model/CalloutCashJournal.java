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

public class CalloutCashJournal extends CalloutEngine {

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
        if( isCalloutActive()) {    // assuming it is resetting value
            return "";
        }

        setCalloutActive( true );

        Integer C_Invoice_ID = ( Integer )value;

        if( (C_Invoice_ID == null) || (C_Invoice_ID.intValue() == 0) ) {
            mTab.setValue( "C_Currency_ID",null );
            setCalloutActive( false );

            return "";
        }

        // Date

        Timestamp ts = Env.getContextAsDate( ctx,WindowNo,"DateAcct" );    // from C_Cash

        if( ts == null ) {
            ts = new Timestamp( System.currentTimeMillis());
        }

        //

        String sql = "SELECT C_BPartner_ID, C_Currency_ID,"         // 1..2
                     + "invoiceOpen(C_Invoice_ID, 0), IsSOTrx, "    // 3..4
                     + "paymentTermDiscount(invoiceOpen(C_Invoice_ID, 0),C_PaymentTerm_ID,DateInvoiced,?) " + "FROM C_Invoice WHERE C_Invoice_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setTimestamp( 1,ts );
            pstmt.setInt( 2,C_Invoice_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                mTab.setValue( "C_Currency_ID",new Integer( rs.getInt( 2 )));

                BigDecimal PayAmt      = rs.getBigDecimal( 3 );
                BigDecimal DiscountAmt = rs.getBigDecimal( 5 );
                boolean    isSOTrx     = "Y".equals( rs.getString( 4 ));

                if( !isSOTrx ) {
                    PayAmt      = PayAmt.negate();
                    DiscountAmt = DiscountAmt.negate();
                }

                //

                mTab.setValue( "Amount",PayAmt.subtract( DiscountAmt ));
                mTab.setValue( "DiscountAmt",DiscountAmt );
                mTab.setValue( "WriteOffAmt",Env.ZERO );
                Env.setContext( ctx,WindowNo,"InvTotalAmt",PayAmt.toString());
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"invoice",e );
            setCalloutActive( false );

            return e.getLocalizedMessage();
        }

        setCalloutActive( false );

        return "";
    }    // CashJournal_Invoice

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

    public String amounts( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {

        // Needs to be Invoice

        if( isCalloutActive() ||!"I".equals( mTab.getValue( "CashType" ))) {
            return "";
        }

        // Check, if InvTotalAmt exists

        String total = Env.getContext( ctx,WindowNo,"InvTotalAmt" );

        if( (total == null) || (total.length() == 0) ) {
            return "";
        }

        BigDecimal InvTotalAmt = new BigDecimal( total );

        setCalloutActive( true );

        BigDecimal PayAmt      = ( BigDecimal )mTab.getValue( "Amount" );
        BigDecimal DiscountAmt = ( BigDecimal )mTab.getValue( "DiscountAmt" );
        BigDecimal WriteOffAmt = ( BigDecimal )mTab.getValue( "WriteOffAmt" );
        String     colName     = mField.getColumnName();

        log.fine( colName + " - Invoice=" + InvTotalAmt + " - Amount=" + PayAmt + ", Discount=" + DiscountAmt + ", WriteOff=" + WriteOffAmt );

        // Amount - calculate write off

        if( colName.equals( "Amount" )) {
            WriteOffAmt = InvTotalAmt.subtract( PayAmt ).subtract( DiscountAmt );
            mTab.setValue( "WriteOffAmt",WriteOffAmt );
        } else    // calculate PayAmt
        {
            PayAmt = InvTotalAmt.subtract( DiscountAmt!=null?DiscountAmt:Env.ZERO ).subtract( WriteOffAmt!=null?WriteOffAmt:Env.ZERO );
            mTab.setValue( "Amount",PayAmt );
        }

        setCalloutActive( false );

        return "";
    }    // amounts
}    // CalloutCashJournal



/*
 *  @(#)CalloutCashJournal.java   02.07.07
 * 
 *  Fin del fichero CalloutCashJournal.java
 *  
 *  Versión 2.2
 *
 */
