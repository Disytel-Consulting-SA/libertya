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

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutInvoiceBatch extends CalloutEngine {

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

    public String date( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( value == null ) {
            return "";
        }

        mTab.setValue( "DateAcct",value );

        //

        return "";
    }    // date

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

    public String bPartner( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_BPartner_ID = ( Integer )value;

        if( (C_BPartner_ID == null) || (C_BPartner_ID.intValue() == 0) ) {
            return "";
        }

        String SQL = "SELECT p.AD_Language,p.C_PaymentTerm_ID," + "p.M_PriceList_ID,p.PaymentRule,p.POReference," + "p.SO_Description,p.IsDiscountPrinted," + "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable," + "l.C_BPartner_Location_ID,c.AD_User_ID," + "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID " + "FROM C_BPartner p " + " LEFT OUTER JOIN C_BPartner_Location l ON (p.C_BPartner_ID=l.C_BPartner_ID AND l.IsBillTo='Y' AND l.IsActive='Y')" + " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) " + "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
        boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_BPartner_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            //

            if( rs.next()) {

                // PaymentRule

                String s = rs.getString( IsSOTrx
                                         ?"PaymentRule"
                                         :"PaymentRulePO" );

                if( (s != null) && (s.length() != 0) ) {
                    if( Env.getContext( ctx,WindowNo,"DocBaseType" ).endsWith( "C" )) {    // Credits are Payment Term
                        s = "P";
                    } else if( IsSOTrx && ( s.equals( "S" ) || s.equals( "U" ))) {    // No Check/Transfer for SO_Trx
                        s = "P";    // Payment Term
                    }

                    // mTab.setValue("PaymentRule", s);

                }

                // Payment Term

                Integer ii = new Integer( rs.getInt( IsSOTrx
                        ?"C_PaymentTerm_ID"
                        :"PO_PaymentTerm_ID" ));

                if( !rs.wasNull()) {
                    mTab.setValue( "C_PaymentTerm_ID",ii );
                }

                // Location

                int locID = rs.getInt( "C_BPartner_Location_ID" );

                // overwritten by InfoBP selection - works only if InfoWindow
                // was used otherwise creates error (uses last value, may belong to differnt BP)

                if( C_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) {
                    String loc = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_Location_ID" );

                    if( loc.length() > 0 ) {
                        locID = Integer.parseInt( loc );
                    }
                }

                if( locID == 0 ) {
                    mTab.setValue( "C_BPartner_Location_ID",null );
                } else {
                    mTab.setValue( "C_BPartner_Location_ID",new Integer( locID ));
                }

                // Contact - overwritten by InfoBP selection

                int contID = rs.getInt( "AD_User_ID" );

                if( C_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) {
                    String cont = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"AD_User_ID" );

                    if( cont.length() > 0 ) {
                        contID = Integer.parseInt( cont );
                    }
                }

                if( contID == 0 ) {
                    mTab.setValue( "AD_User_ID",null );
                } else {
                    mTab.setValue( "AD_User_ID",new Integer( contID ));
                }

                // CreditAvailable

                if( IsSOTrx ) {
                    double CreditLimit = rs.getDouble( "SO_CreditLimit" );

                    if( CreditLimit != 0 ) {
                        double CreditAvailable = rs.getDouble( "CreditAvailable" );

                        if( !rs.wasNull() && (CreditAvailable < 0) ) {
                            mTab.fireDataStatusEEvent( "CreditLimitOver",DisplayType.getNumberFormat( DisplayType.Amount ).format( CreditAvailable ));
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"bPartner",e );

            return e.getLocalizedMessage();
        }

        return tax( ctx,WindowNo,mTab,mField,value );
    }    // bPartner

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

    public String charge( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Charge_ID = ( Integer )value;

        if( (C_Charge_ID == null) || (C_Charge_ID.intValue() == 0) ) {
            return "";
        }

        try {
            String            SQL   = "SELECT ChargeAmt FROM C_Charge WHERE C_Charge_ID=?";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Charge_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                mTab.setValue( "PriceEntered",rs.getBigDecimal( 1 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"charge" + e );

            return e.getLocalizedMessage();
        }

        //

        return tax( ctx,WindowNo,mTab,mField,value );
    }    // charge

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

    public String tax( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        String column = mField.getColumnName();

        if( value == null ) {
            return "";
        }

        int C_Charge_ID = 0;

        if( column.equals( "C_Charge_ID" )) {
            C_Charge_ID = (( Integer )value ).intValue();
        } else {
            C_Charge_ID = Env.getContextAsInt( ctx,WindowNo,"C_Charge_ID" );
        }

        log.fine( "C_Charge_ID=" + C_Charge_ID );

        if( C_Charge_ID == 0 ) {
            return amt( ctx,WindowNo,mTab,mField,value );    //
        }

        // Check Partner Location

        int C_BPartner_Location_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_Location_ID" );

        if( C_BPartner_Location_ID == 0 ) {
            return amt( ctx,WindowNo,mTab,mField,value );    //
        }

        log.fine( "BP_Location=" + C_BPartner_Location_ID );

        // Dates

        Timestamp billDate = Env.getContextAsDate( ctx,WindowNo,"DateInvoiced" );

        log.fine( "Bill Date=" + billDate );

        Timestamp shipDate = billDate;

        log.fine( "Ship Date=" + shipDate );

        int AD_Org_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Org_ID" );

        log.fine( "Org=" + AD_Org_ID );

        int M_Warehouse_ID = Env.getContextAsInt( ctx,"#M_Warehouse_ID" );

        log.fine( "Warehouse=" + M_Warehouse_ID );

        //

        int C_Tax_ID = Tax.get( ctx,0,C_Charge_ID,billDate,shipDate,AD_Org_ID,M_Warehouse_ID,C_BPartner_Location_ID,C_BPartner_Location_ID,Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" ));

        log.info( "Tax ID=" + C_Tax_ID );

        //

        if( C_Tax_ID == 0 ) {
            mTab.fireDataStatusEEvent( CLogger.retrieveError());
        } else {
            mTab.setValue( "C_Tax_ID",new Integer( C_Tax_ID ));
        }

        //

        return amt( ctx,WindowNo,mTab,mField,value );
    }    // tax

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

    public String amt( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        int StdPrecision = 2;    // temporary

        // get values

        BigDecimal QtyEntered   = ( BigDecimal )mTab.getValue( "QtyEntered" );
        BigDecimal PriceEntered = ( BigDecimal )mTab.getValue( "PriceEntered" );

        log.fine( "QtyEntered=" + QtyEntered + ", PriceEntered=" + PriceEntered );

        if( QtyEntered == null ) {
            QtyEntered = Env.ZERO;
        }

        if( PriceEntered == null ) {
            PriceEntered = Env.ZERO;
        }

        // Line Net Amt

        BigDecimal LineNetAmt = QtyEntered.multiply( PriceEntered );

        if( LineNetAmt.scale() > StdPrecision ) {
            LineNetAmt = LineNetAmt.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        // Calculate Tax Amount

        boolean IsSOTrx = "Y".equals( Env.getContext( Env.getCtx(),WindowNo,"IsSOTrx" ));
        boolean IsTaxIncluded = "Y".equals( Env.getContext( Env.getCtx(),WindowNo,"IsTaxIncluded" ));
        BigDecimal documentDiscountAmt = (BigDecimal)mTab.getValue( "DocumentDiscountAmt" );
        BigDecimal TaxAmt = null;

        if( mField.getColumnName().equals( "TaxAmt" )) {
            TaxAmt = ( BigDecimal )mTab.getValue( "TaxAmt" );
        } else {
            Integer taxID = ( Integer )mTab.getValue( "C_Tax_ID" );

            if( taxID != null ) {
                int  C_Tax_ID = taxID.intValue();
                MTax tax      = new MTax( ctx,C_Tax_ID,null );

                TaxAmt = tax.calculateTax( LineNetAmt.subtract(documentDiscountAmt),IsTaxIncluded,StdPrecision );
                mTab.setValue( "TaxAmt",TaxAmt );
            }
        }

        //

        if( IsTaxIncluded ) {
            mTab.setValue( "LineTotalAmt",LineNetAmt );
            mTab.setValue( "LineNetAmt",LineNetAmt.subtract( TaxAmt ));
        } else {
            mTab.setValue( "LineNetAmt",LineNetAmt );
            mTab.setValue( "LineTotalAmt",LineNetAmt.add( TaxAmt ));
        }

        setCalloutActive( false );

        return "";
    }    // amt
}    // CalloutInvoiceBatch



/*
 *  @(#)CalloutInvoiceBatch.java   02.07.07
 * 
 *  Fin del fichero CalloutInvoiceBatch.java
 *  
 *  Versión 2.2
 *
 */
