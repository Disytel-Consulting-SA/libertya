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

import org.openXpertya.model.MPaySelection;
import org.openXpertya.model.MPaySelectionLine;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class PaySelectionCreateFrom extends SvrProcess {

    /** Descripción de Campos */

    private boolean p_OnlyDiscount = false;

    /** Descripción de Campos */

    private boolean p_OnlyDue = false;

    /** Descripción de Campos */

    private String p_PaymentRule = null;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int p_C_BP_Group_ID = 0;

    /** Descripción de Campos */

    private int p_C_PaySelection_ID = 0;

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
            } else if( name.equals( "OnlyDiscount" )) {
                p_OnlyDiscount = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "OnlyDue" )) {
                p_OnlyDue = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "PaymentRule" )) {
                p_PaymentRule = ( String )para[ i ].getParameter();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BP_Group_ID" )) {
                p_C_BP_Group_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_C_PaySelection_ID = getRecord_ID();
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
        log.info( "doIt - C_PaySelection_ID=" + p_C_PaySelection_ID + ", OnlyDiscount=" + p_OnlyDiscount + ", OnlyDue=" + p_OnlyDue + ", PaymentRule=" + p_PaymentRule + ", C_BP_Group_ID=" + p_C_BP_Group_ID + ", C_BPartner_ID=" + p_C_BPartner_ID );

        MPaySelection psel = new MPaySelection( getCtx(),p_C_PaySelection_ID,get_TrxName());

        if( psel.getID() == 0 ) {
            throw new IllegalArgumentException( "Not found C_PaySelection_ID=" + p_C_PaySelection_ID );
        }

        if( psel.isProcessed()) {
            throw new IllegalArgumentException( "@Processed@" );
        }

         log.fine("la fecha es" +psel.getPayDate());

        String sql = "SELECT C_Invoice_ID,"

        // Open

        + " currencyConvert(invoiceOpen(i.C_Invoice_ID, 0)" + ",i.C_Currency_ID, ?,?, i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID),"    // ##1/2 Currency_To,PayDate

        // Discount

        + " currencyConvert(paymentTermDiscount(i.GrandTotal,i.C_PaymentTerm_ID,i.DateInvoiced, ?)"    // ##3 PayDate
        + ",i.C_Currency_ID, ?,?,i.C_ConversionType_ID,i.AD_Client_ID,i.AD_Org_ID),"    // ##4/5 Currency_To,PayDate
        + " PaymentRule, IsSOTrx " + "FROM C_Invoice i " + "WHERE IsSOTrx='N' AND IsPaid='N' AND DocStatus IN ('CO','CL')" + " AND AD_Client_ID=?"    // ##6

        // Existing Payments - Will reselect Invoice if prepared but not paid

        + " AND NOT EXISTS (SELECT * FROM C_PaySelectionLine psl " + "WHERE i.C_Invoice_ID=psl.C_Invoice_ID AND psl.IsActive='Y'" + " AND psl.C_PaySelectionCheck_ID IS NOT NULL)";

        // PaymentRule (optional)
        
        log.fine("El PaymentRule = "+ p_PaymentRule);
        if( p_PaymentRule != null ) {
            sql += " AND PaymentRule=?";    // ##
        }

        // OnlyDiscount

        if( p_OnlyDiscount ) {
            if( p_OnlyDue ) {
                sql += " AND (";
            } else {
                sql += " AND ";
            }

            sql += "paymentTermDiscount(invoiceOpen(C_Invoice_ID,0), C_PaymentTerm_ID, DateInvoiced, ?) > 0";    // ##
        }

        // OnlyDue

        if( p_OnlyDue ) {
            if( p_OnlyDiscount ) {
                sql += " OR ";
            } else {
                sql += " AND ";
            }

            sql += "paymentTermDueDays(C_PaymentTerm_ID, DateInvoiced, ?) >= 0";    // ##

            if( p_OnlyDiscount ) {
                sql += ")";
            }
        }

        // Business Partner

        if( p_C_BPartner_ID != 0 ) {
            sql += " AND C_BPartner_ID=?";                                                                                            // ##

            // Business Partner Group

        } else if( p_C_BP_Group_ID != 0 ) {
            sql += " AND EXISTS (SELECT * FROM C_BPartner bp " + "WHERE bp.C_BPartner_ID=i.C_BPartner_ID AND bp.C_BP_Group_ID=?)";    // ##
        }

        //

        int               lines           = 0;
        int               C_CurrencyTo_ID = psel.getC_Currency_ID();
        PreparedStatement pstmt           = null;
        log.fine("En payselection create con = sql= "+ sql);

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());

            int index = 1;
            
            pstmt.setInt( index++,C_CurrencyTo_ID );
             pstmt.setTimestamp( index++,psel.getPayDate());

            //

            pstmt.setTimestamp( index++,psel.getPayDate());
            pstmt.setInt( index++,C_CurrencyTo_ID );
            pstmt.setTimestamp( index++,psel.getPayDate());

            //

            pstmt.setInt( index++,psel.getAD_Client_ID());

            if( p_PaymentRule != null ) {
                pstmt.setString( index++,p_PaymentRule );
            }

            if( p_OnlyDiscount ) {
                pstmt.setTimestamp( index++,psel.getPayDate());
            }

            if( p_OnlyDue ) {
                pstmt.setTimestamp( index++,psel.getPayDate());
            }

            if( p_C_BPartner_ID != 0 ) {
                pstmt.setInt( index++,p_C_BPartner_ID );
            } else if( p_C_BP_Group_ID != 0 ) {
                pstmt.setInt( index++,p_C_BP_Group_ID );
            }

            //
            log.fine("Con la sql= " + sql);
            log.fine("con pstmt= "+ pstmt);
            ResultSet rs = pstmt.executeQuery();
           
            while( rs.next()) {
            	log.fine("En el while");
                int        C_Invoice_ID = rs.getInt( 1 );
                BigDecimal PayAmt       = rs.getBigDecimal( 2 );

                if( (C_Invoice_ID == 0) || (Env.ZERO.compareTo( PayAmt ) == 0) ) {
                    continue;
                }

                BigDecimal DiscountAmt = rs.getBigDecimal( 3 );
                String     PaymentRule = rs.getString( 4 );
                boolean    isSOTrx     = "Y".equals( rs.getString( 5 ));

                //

                lines++;

                MPaySelectionLine pselLine = new MPaySelectionLine( psel,lines * 10,PaymentRule );

                pselLine.setInvoice( C_Invoice_ID,isSOTrx,PayAmt,PayAmt.subtract( DiscountAmt ),DiscountAmt );

                if( !pselLine.save()) {
                    pstmt.close();

                    throw new IllegalStateException( "Cannot save MPaySelectionLine" );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"doIt - " + sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@C_PaySelectionLine_ID@  - #" + lines;
    }    // doIt
}    // PaySelectionCreateFrom



/*
 *  @(#)PaySelectionCreateFrom.java   02.07.07
 * 
 *  Fin del fichero PaySelectionCreateFrom.java
 *  
 *  Versión 2.2
 *
 */
