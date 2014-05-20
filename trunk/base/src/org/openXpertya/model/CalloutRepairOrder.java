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

import javax.swing.JOptionPane;

import org.openXpertya.model.MRole;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutRepairOrder extends CalloutEngine {

    /** Descripción de Campos */

    private boolean steps = false;

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

    public String docType( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_DocType_ID = ( Integer )value;    // Actually C_DocTypeTarget_ID

        if( (C_DocType_ID == null) || (C_DocType_ID.intValue() == 0) ) {
            return "";
        }

        // Re-Create new DocNo, if there is a doc number already
        // and the existing source used a different Sequence number

        String  oldDocNo = ( String )mTab.getValue( "DocumentNo" );
        boolean newDocNo = ( oldDocNo == null );

        if( !newDocNo && oldDocNo.startsWith( "<" ) && oldDocNo.endsWith( ">" )) {
            newDocNo = true;
        }

        Integer oldC_DocType_ID = ( Integer )mTab.getValue( "C_DocType_ID" );

        try {
            String SQL            = "SELECT d.DocSubTypeSO,d.HasCharges,'N',"                        // 1..3
                                    + "d.IsDocNoControlled,s.CurrentNext,s.CurrentNextSys,"          // 4..6
                                    + "s.AD_Sequence_ID,d.IsSOTrx "                                  // 7..8
                                    + "FROM C_DocType d, AD_Sequence s "
                                    + "WHERE C_DocType_ID=?"    // #1
                                    + " AND d.DocNoSequence_ID=s.AD_Sequence_ID(+)";
            int    AD_Sequence_ID = 0;

            // Get old AD_SeqNo for comparison

            if( !newDocNo && (oldC_DocType_ID.intValue() != 0) ) {
                PreparedStatement pstmt = DB.prepareStatement( SQL );

                pstmt.setInt( 1,oldC_DocType_ID.intValue());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    AD_Sequence_ID = rs.getInt( "AD_Sequence_ID" );
                }

                rs.close();
                pstmt.close();
            }

            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_DocType_ID.intValue());

            ResultSet rs           = pstmt.executeQuery();
            String    DocSubTypeSO = "";
            boolean   IsSOTrx      = true;

            if( rs.next())                                                                           // we found document type
            {

                // Set Context:    Document Sub Type for Sales Orders

                DocSubTypeSO = rs.getString( 1 );

                if( DocSubTypeSO == null ) {
                    DocSubTypeSO = "--";
                }

                Env.setContext( ctx,WindowNo,"OrderType",DocSubTypeSO );

                // No Drop Ship other than Standard

                if( !DocSubTypeSO.equals( MOrder.DocSubTypeSO_Standard )) {
                    mTab.setValue( "IsDropShip","N" );
                }

                // Delivery Rule

                if( DocSubTypeSO.equals( MOrder.DocSubTypeSO_POS )) {
                    mTab.setValue( "DeliveryRule",MOrder.DELIVERYRULE_Force );
                } else if( DocSubTypeSO.equals( MOrder.DocSubTypeSO_Prepay )) {
                    mTab.setValue( "DeliveryRule",MOrder.DELIVERYRULE_AfterReceipt );
                } else {
                    mTab.setValue( "DeliveryRule",MOrder.DELIVERYRULE_Availability );
                }

                // Invoice Rule

                if( DocSubTypeSO.equals( MOrder.DocSubTypeSO_POS ) || DocSubTypeSO.equals( MOrder.DocSubTypeSO_Prepay ) || DocSubTypeSO.equals( MOrder.DocSubTypeSO_OnCredit )) {
                    mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_Immediate );
                } else {
                    mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_AfterDelivery );
                }

                // Payment Rule - POS Order

                if( DocSubTypeSO.equals( MOrder.DocSubTypeSO_POS )) {
                    mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_Cash );
                } else {
                    mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_OnCredit );
                }

                // IsSOTrx

                if( "N".equals( rs.getString( 8 ))) {
                    IsSOTrx = false;
                }

                // Set Context:

                Env.setContext( ctx,WindowNo,"HasCharges",rs.getString( 2 ));

                // DocumentNo

                if( rs.getString( 4 ).equals( "Y" ))    // IsDocNoControlled
                {
                    if( !newDocNo && (AD_Sequence_ID != rs.getInt( 7 ))) {
                        newDocNo = true;
                    }

                    if( newDocNo ) {
                        if( Ini.getPropertyBool( Ini.P_OXPSYS ) && (Env.getAD_Client_ID( Env.getCtx()) < 1000000) ) {
                            mTab.setValue( "DocumentNo","<" + rs.getString( 6 ) + ">" );
                        } else {
                            mTab.setValue( "DocumentNo","<" + rs.getString( 5 ) + ">" );
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();

            // When BPartner is changed, the Rules are not set if
            // it is a POS or Credit Order (i.e. defaults from Standard BPartner)
            // This re-reads the Rules and applies them.

            if( DocSubTypeSO.equals( MOrder.DocSubTypeSO_POS ) || DocSubTypeSO.equals( MOrder.DocSubTypeSO_Prepay )) {    // not for POS/PrePay
                ;
            } else {
                SQL = "SELECT PaymentRule,C_PaymentTerm_ID,"                                                  // 1..2
                      + "InvoiceRule,DeliveryRule,"                                                           // 3..4
                      + "FreightCostRule,DeliveryViaRule, "                                                   // 5..6
                      + "PaymentRulePO,PO_PaymentTerm_ID " + "FROM C_BPartner " + "WHERE C_BPartner_ID=?";    // #1
                pstmt = DB.prepareStatement( SQL );

                int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );

                pstmt.setInt( 1,C_BPartner_ID );

                //

                rs = pstmt.executeQuery();

                if( rs.next()) {

                    // PaymentRule

                    String s = rs.getString( IsSOTrx
                                             ?"PaymentRule"
                                             :"PaymentRulePO" );

                    if( (s != null) && (s.length() != 0) ) {
                        if( IsSOTrx && ( s.equals( "B" ) || s.equals( "S" ) || s.equals( "U" ))) {    // No Cash/Check/Transfer for SO_Trx
                            s = "P";                             // Payment Term
                        }

                        if( !IsSOTrx && ( s.equals( "B" ))) {    // No Cash for PO_Trx
                            s = "P";    // Payment Term
                        }

                        mTab.setValue( "PaymentRule",s );
                    }

                    // Payment Term

                    Integer ii = new Integer( rs.getInt( IsSOTrx
                            ?"C_PaymentTerm_ID"
                            :"PO_PaymentTerm_ID" ));

                    if( !rs.wasNull()) {
                        mTab.setValue( "C_PaymentTerm_ID",ii );
                    }

                    // InvoiceRule

                    s = rs.getString( 3 );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "InvoiceRule",s );
                    }

                    // DeliveryRule

                    s = rs.getString( 4 );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "DeliveryRule",s );
                    }

                    // FreightCostRule

                    s = rs.getString( 5 );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "FreightCostRule",s );
                    }

                    // DeliveryViaRule

                    s = rs.getString( 6 );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "DeliveryViaRule",s );
                    }
                }

                rs.close();
                pstmt.close();
            }    // re-read customer rules
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"docType",e );

            return e.getLocalizedMessage();
        }

        return "";
    }    // docType

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

        setCalloutActive( true );

        String sql =  "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
        			+ "p.M_PriceList_ID,p.PaymentRule,p.POReference,"
        			+ "p.SO_Description,p.IsDiscountPrinted,"
        			+ "p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule,"
        			+ "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
        			+ "lship.C_BPartner_Location_ID,c.AD_User_ID,"
        			+ "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID,"
        			+ "lbill.C_BPartner_Location_ID AS Bill_Location_ID, p.SOCreditStatus "
        			+ "FROM C_BPartner p"
        			+ " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')"
        			+ " LEFT OUTER JOIN C_BPartner_Location lship ON (p.C_BPartner_ID=lship.C_BPartner_ID AND lship.IsShipTo='Y' AND lship.IsActive='Y')"
        			+ " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) "
        			+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
        boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_BPartner_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // PriceList (indirect: IsTaxIncluded & Currency)

                Integer ii = new Integer( rs.getInt( IsSOTrx
                        ?"M_PriceList_ID"
                        :"PO_PriceList_ID" ));

                if( !rs.wasNull()) {
                    mTab.setValue( "M_PriceList_ID",ii );
                } else {    // get default PriceList
                    int i = Env.getContextAsInt( ctx,"#M_PriceList_ID" );

                    if( i != 0 ) {
                        mTab.setValue( "M_PriceList_ID",new Integer( i ));
                    }
                }

                // Bill-To

                mTab.setValue( "Bill_BPartner_ID",C_BPartner_ID );

                int bill_Location_ID = rs.getInt( "Bill_Location_ID" );

                if( bill_Location_ID == 0 ) {
                    mTab.setValue( "Bill_Location_ID",null );
                } else {
                    mTab.setValue( "Bill_Location_ID",new Integer( bill_Location_ID ));
                }

                // Ship-To Location

                int shipTo_ID = rs.getInt( "C_BPartner_Location_ID" );

                // overwritten by InfoBP selection - works only if InfoWindow
                // was used otherwise creates error (uses last value, may belong to differnt BP)

                if( C_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) {
                    String loc = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_Location_ID" );

                    if( loc.length() > 0 ) {
                        shipTo_ID = Integer.parseInt( loc );
                    }
                }

                if( shipTo_ID == 0 ) {
                    mTab.setValue( "C_BPartner_Location_ID",null );
                } else {
                    mTab.setValue( "C_BPartner_Location_ID",new Integer( shipTo_ID ));
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
                    mTab.setValue( "Bill_User_ID",new Integer( contID ));
                }

                // CreditAvailable

                if( IsSOTrx ) {
                    double CreditLimit    = rs.getDouble( "SO_CreditLimit" );
                    String SOCreditStatus = rs.getString( "SOCreditStatus" );

                    if( CreditLimit != 0 ) {
                        double CreditAvailable = rs.getDouble( "CreditAvailable" );

                        if( !rs.wasNull() && (CreditAvailable < 0) ) {
                            mTab.fireDataStatusEEvent( "CreditLimitOver",DisplayType.getNumberFormat( DisplayType.Amount ).format( CreditAvailable ));
                        }
                    }
                }

                // PO Reference

                String s = rs.getString( "POReference" );

                if( (s != null) && (s.length() != 0) ) {
                    mTab.setValue( "POReference",s );
                } else {
                    mTab.setValue( "POReference",null );
                }

                // SO Description

                s = rs.getString( "SO_Description" );

                if( (s != null) && (s.trim().length() != 0) ) {
                    mTab.setValue( "Description",s );
                }

                // IsDiscountPrinted

                s = rs.getString( "IsDiscountPrinted" );

                if( (s != null) && (s.length() != 0) ) {
                    mTab.setValue( "IsDiscountPrinted",s );
                } else {
                    mTab.setValue( "IsDiscountPrinted","N" );
                }

                // Defaults, if not Walkin Receipt or Walkin Invoice

                String OrderType = Env.getContext( ctx,WindowNo,"OrderType" );

                mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_AfterDelivery );
                mTab.setValue( "DeliveryRule",MOrder.DELIVERYRULE_Availability );
                mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_OnCredit );

                if( OrderType.equals( MOrder.DocSubTypeSO_Prepay )) {
                    mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_Immediate );
                    mTab.setValue( "DeliveryRule",MOrder.DELIVERYRULE_AfterReceipt );
                } else if( OrderType.equals( MOrder.DocSubTypeSO_POS )) {    // for POS
                    mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_Cash );
                } else {

                    // PaymentRule

                    s = rs.getString( IsSOTrx
                                      ?"PaymentRule"
                                      :"PaymentRulePO" );

                    if( (s != null) && (s.length() != 0) ) {
                        if( s.equals( "B" )) {                                     // No Cache in Non POS
                            s = "P";
                        }

                        if( IsSOTrx && ( s.equals( "S" ) || s.equals( "U" ))) {    // No Check/Transfer for SO_Trx
                            s = "P";    // Payment Term
                        }

                        mTab.setValue( "PaymentRule",s );
                    }

                    // Payment Term

                    ii = new Integer( rs.getInt( IsSOTrx
                                                 ?"C_PaymentTerm_ID"
                                                 :"PO_PaymentTerm_ID" ));

                    if( !rs.wasNull()) {
                        mTab.setValue( "C_PaymentTerm_ID",ii );
                    }

                    // InvoiceRule

                    s = rs.getString( "InvoiceRule" );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "InvoiceRule",s );
                    }

                    // DeliveryRule

                    s = rs.getString( "DeliveryRule" );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "DeliveryRule",s );
                    }

                    // FreightCostRule

                    s = rs.getString( "FreightCostRule" );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "FreightCostRule",s );
                    }

                    // DeliveryViaRule

                    s = rs.getString( "DeliveryViaRule" );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "DeliveryViaRule",s );
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"bPartner",e );
            setCalloutActive( false );

            return e.getLocalizedMessage();
        }

        setCalloutActive( false );

        return "";
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

    public String bPartnerBill( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive()) {
            return "";
        }

        Integer bill_BPartner_ID = ( Integer )value;

        if( (bill_BPartner_ID == null) || (bill_BPartner_ID.intValue() == 0) ) {
            return "";
        }

        String sql =  "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
        			+ "p.M_PriceList_ID,p.PaymentRule,p.POReference,"
        			+ "p.SO_Description,p.IsDiscountPrinted,"
        			+ "p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule,"
        			+ "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
        			+ "c.AD_User_ID,"
        			+ "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID,"
        			+ "lbill.C_BPartner_Location_ID AS Bill_Location_ID "
        			+ "FROM C_BPartner p"
        			+ " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')"
        			+ " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) "
        			+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
        boolean IsSOTrx = "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ));

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,bill_BPartner_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // PriceList (indirect: IsTaxIncluded & Currency)

                Integer ii = new Integer( rs.getInt( IsSOTrx
                        								?"M_PriceList_ID"
                        								:"PO_PriceList_ID" ));

                if( !rs.wasNull()) {
                    mTab.setValue( "M_PriceList_ID",ii );
                } else {    // get default PriceList
                    int i = Env.getContextAsInt( ctx,"#M_PriceList_ID" );

                    if( i != 0 ) {
                        mTab.setValue( "M_PriceList_ID",new Integer( i ));
                    }
                }

                int bill_Location_ID = rs.getInt( "Bill_Location_ID" );

                // overwritten by InfoBP selection - works only if InfoWindow
                // was used otherwise creates error (uses last value, may belong to differnt BP)

                if( bill_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) {
                    String loc = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_Location_ID" );

                    if( loc.length() > 0 ) {
                        bill_Location_ID = Integer.parseInt( loc );
                    }
                }

                if( bill_Location_ID == 0 ) {
                    mTab.setValue( "Bill_Location_ID",null );
                } else {
                    mTab.setValue( "Bill_Location_ID",new Integer( bill_Location_ID ));
                }

                // Contact - overwritten by InfoBP selection

                int contID = rs.getInt( "AD_User_ID" );

                if( bill_BPartner_ID.toString().equals( Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_BPartner_ID" ))) {
                    String cont = Env.getContext( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"AD_User_ID" );

                    if( cont.length() > 0 ) {
                        contID = Integer.parseInt( cont );
                    }
                }

                if( contID == 0 ) {
                    mTab.setValue( "Bill_User_ID",null );
                } else {
                    mTab.setValue( "Bill_User_ID",new Integer( contID ));
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

                // PO Reference

                String s = rs.getString( "POReference" );

                if( (s != null) && (s.length() != 0) ) {
                    mTab.setValue( "POReference",s );
                } else {
                    mTab.setValue( "POReference",null );
                }

                // SO Description

                s = rs.getString( "SO_Description" );

                if( (s != null) && (s.trim().length() != 0) ) {
                    mTab.setValue( "Description",s );
                }

                // IsDiscountPrinted

                s = rs.getString( "IsDiscountPrinted" );

                if( (s != null) && (s.length() != 0) ) {
                    mTab.setValue( "IsDiscountPrinted",s );
                } else {
                    mTab.setValue( "IsDiscountPrinted","N" );
                }

                // Defaults, if not Walkin Receipt or Walkin Invoice

                String OrderType = Env.getContext( ctx,WindowNo,"OrderType" );

                mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_AfterDelivery );
                mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_OnCredit );

                if( OrderType.equals( MOrder.DocSubTypeSO_Prepay )) {
                    mTab.setValue( "InvoiceRule",MOrder.INVOICERULE_Immediate );
                } else if( OrderType.equals( MOrder.DocSubTypeSO_POS )) {    // for POS
                    mTab.setValue( "PaymentRule",MOrder.PAYMENTRULE_Cash );
                } else {

                    // PaymentRule

                    s = rs.getString( IsSOTrx
                                      ?"PaymentRule"
                                      :"PaymentRulePO" );

                    if( (s != null) && (s.length() != 0) ) {
                        if( s.equals( "B" )) {                                     // No Cache in Non POS
                            s = "P";
                        }

                        if( IsSOTrx && ( s.equals( "S" ) || s.equals( "U" ))) {    // No Check/Transfer for SO_Trx
                            s = "P";    // Payment Term
                        }

                        mTab.setValue( "PaymentRule",s );
                    }

                    // Payment Term

                    ii = new Integer( rs.getInt( IsSOTrx
                                                 ?"C_PaymentTerm_ID"
                                                 :"PO_PaymentTerm_ID" ));

                    if( !rs.wasNull()) {
                        mTab.setValue( "C_PaymentTerm_ID",ii );
                    }

                    // InvoiceRule

                    s = rs.getString( "InvoiceRule" );

                    if( (s != null) && (s.length() != 0) ) {
                        mTab.setValue( "InvoiceRule",s );
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"bPartnerBill",e );

            return e.getLocalizedMessage();
        }

        return "";
    }    // bPartnerBill

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

    public String priceList( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer M_PriceList_ID = ( Integer )value;

        if( (M_PriceList_ID == null) || (M_PriceList_ID.intValue() == 0) ) {
            return "";
        }

        if( steps ) {
            log.warning( "priceList - init" );
        }

        String SQL =  "SELECT pl.IsTaxIncluded,pl.EnforcePriceLimit,pl.C_Currency_ID,c.StdPrecision,"
        			+ "plv.M_PriceList_Version_ID,plv.ValidFrom "
        			+ "FROM M_PriceList pl,C_Currency c,M_PriceList_Version plv "
        			+ "WHERE pl.C_Currency_ID=c.C_Currency_ID"
        			+ " AND pl.M_PriceList_ID=plv.M_PriceList_ID"
        			+ " AND pl.M_PriceList_ID=? "    // 1
                    + "ORDER BY plv.ValidFrom DESC";

        // Use newest price list - may not be future

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,M_PriceList_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Tax Included
                mTab.setValue( "IsTaxIncluded",new Boolean( "Y".equals( rs.getString( 1 ))));

                // Price Limit Enforce
                Env.setContext( ctx,WindowNo,"EnforcePriceLimit",rs.getString( 2 ));

                // Currency
                Integer ii = new Integer( rs.getInt( 3 ));
                mTab.setValue( "C_Currency_ID",ii );

                // PriceList Version
                Env.setContext( ctx,WindowNo,"M_PriceList_Version_ID",rs.getInt( 5 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"priceList",e );

            return e.getLocalizedMessage();
        }

        if( steps ) {
            log.warning( "priceList - fini" );
        }

        return "";
    }    // priceList

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

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	Integer M_Product_ID = ( Integer )value;
        log.fine("En CalloutRepairOrder.product con Propiertes ="+ ctx+ " MTab= "+ mTab+ " Mfiel= "+ mField+ "Object= "+ value );

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        if( steps ) {
            log.warning( "product - init" );
        }

        setCalloutActive( true );

        //

        mTab.setValue( "C_Charge_ID",null );

        // Set Attribute

        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
        } else {
            mTab.setValue( "M_AttributeSetInstance_ID",null );
        }

        //int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
        int C_Repair_Order_ID=Env.getContextAsInt( ctx,WindowNo,"C_Repair_Order_ID" );
        MRepairOrder o=new MRepairOrder(ctx, C_Repair_Order_ID, null);
        int C_BPartner_ID=o.getC_BPartner_ID();
        BigDecimal Qty     = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        boolean    IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );
        MProductPricing pp = new MProductPricing( M_Product_ID.intValue(),C_BPartner_ID,Qty,IsSOTrx );

        //

        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );

        pp.setM_PriceList_ID( M_PriceList_ID );

        int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

        pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

        Timestamp orderDate = ( Timestamp )mTab.getValue( "DateOrdered" );

        pp.setPriceDate( orderDate );

        //

        mTab.setValue( "PriceList",pp.getPriceList());
        mTab.setValue( "PriceLimit",pp.getPriceLimit());
        //mTab.setValue( "PriceActual",pp.getPriceStd()); Original
        //mTab.setValue( "PriceEntered",pp.getPriceStd()); Original
        mTab.setValue( "PriceActual",pp.getPriceList()); 
        mTab.setValue( "PriceEntered",pp.getPriceList());
        mTab.setValue( "C_Currency_ID",new Integer( pp.getC_Currency_ID()));
        mTab.setValue( "Discount",pp.getDiscount());
        mTab.setValue( "C_UOM_ID",new Integer( pp.getC_UOM_ID()));
        mTab.setValue( "QtyOrdered",mTab.getValue( "QtyEntered" ));
        Env.setContext( ctx,WindowNo,"EnforcePriceLimit",pp.isEnforcePriceLimit()
                ?"Y"
                :"N" );
        Env.setContext( ctx,WindowNo,"DiscountSchema",pp.isDiscountSchema()
                ?"Y"
                :"N" );

        // Check/Update Warehouse Setting
        // int M_Warehouse_ID = Env.getContextAsInt(ctx, Env.WINDOW_INFO, "M_Warehouse_ID");
        // Integer wh = (Integer)mTab.getValue("M_Warehouse_ID");
        // if (wh.intValue() != M_Warehouse_ID)
        // {
        // mTab.setValue("M_Warehouse_ID", new Integer(M_Warehouse_ID));
        // ADialog.warn(,WindowNo, "WarehouseChanged");
        // }

        if( "Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" ))) {
            MProduct product = MProduct.get( ctx,M_Product_ID.intValue());

            if( product.isStocked()) {
                int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );
                BigDecimal available = MStorage.getQtyAvailable( M_Warehouse_ID,M_Product_ID.intValue(),null );

                if( available == null ) {
                    mTab.fireDataStatusEEvent( "NoQtyAvailable","0" );
                } else if( available.compareTo( Env.ZERO ) <= 0 ) {
                    mTab.fireDataStatusEEvent( "NoQtyAvailable",available.toString());
                }
            }
        }

        //

        setCalloutActive( false );

        if( steps ) {
            log.warning( "product - fini" );
        }

        return tax( ctx,WindowNo,mTab,mField,value );
        
    }// product

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
        log.fine("El c_charge_id es = "+ C_Charge_ID); 


        if( (C_Charge_ID == null) || (C_Charge_ID.intValue() == 0) ) {
            return "";
        }

        // No Product defined

        if( mTab.getValue( "M_Product_ID" ) != null ) {
            mTab.setValue( "C_Charge_ID",null );

            return "ChargeExclusively";
        }

        mTab.setValue( "M_AttributeSetInstance_ID",null );
        mTab.setValue( "S_ResourceAssignment_ID",null );
        mTab.setValue( "C_UOM_ID",new Integer( 100 ));    // EA
        Env.setContext( ctx,WindowNo,"DiscountSchema","N" );
        

        try {
            String            SQL   = "SELECT ChargeAmt FROM C_Charge WHERE C_Charge_ID=?";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Charge_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                mTab.setValue( "PriceEntered",rs.getBigDecimal( 1 ));
                mTab.setValue( "PriceActual",rs.getBigDecimal( 1 ));
                mTab.setValue( "PriceLimit",Env.ZERO );
                mTab.setValue( "PriceList",Env.ZERO );
                mTab.setValue( "Discount",Env.ZERO );
                
            }
            BigDecimal QtyOrdered=(BigDecimal)(mTab.getValue( "QtyOrdered"));
            BigDecimal  PriceActual=(BigDecimal)(mTab.getValue( "PriceActual"));
            PriceActual.setScale( 2,BigDecimal.ROUND_HALF_UP );
            QtyOrdered.setScale( 2,BigDecimal.ROUND_HALF_UP );
            BigDecimal LineNetAmt = QtyOrdered.multiply( PriceActual );
            
            mTab.setValue( "LineNetAmt",LineNetAmt );
            
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"charge..." + e );

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

        if( steps ) {
            log.warning( "tax - init" );
        }

        // Check Product

        int M_Product_ID = 0;

        if( column.equals( "M_Product_ID" )) {
            M_Product_ID = (( Integer )value ).intValue();
        } else {
            M_Product_ID = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );
        }

        int C_Charge_ID = 0;

        if( column.equals( "C_Charge_ID" )) {
            C_Charge_ID = (( Integer )value ).intValue();
        } else {
            C_Charge_ID = Env.getContextAsInt( ctx,WindowNo,"C_Charge_ID" );
        }

        log.fine( "Product=" + M_Product_ID + ", C_Charge_ID=" + C_Charge_ID );

        if( (M_Product_ID == 0) && (C_Charge_ID == 0) ) {
            return amt( ctx,WindowNo,mTab,mField,value );    //
        }

        // Check Partner Location

        int shipC_BPartner_Location_ID = 0;

        if( column.equals( "C_BPartner_Location_ID" )) {
            shipC_BPartner_Location_ID = (( Integer )value ).intValue();
        } else {
            shipC_BPartner_Location_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_Location_ID" );
        }

        if( shipC_BPartner_Location_ID == 0 ) {
            return amt( ctx,WindowNo,mTab,mField,value );    //
        }

        log.fine( "Ship BP_Location=" + shipC_BPartner_Location_ID );

        //

        Timestamp billDate = Env.getContextAsDate( ctx,WindowNo,"DateOrdered" );

        log.fine( "Bill Date=" + billDate );

        Timestamp shipDate = Env.getContextAsDate( ctx,WindowNo,"DatePromised" );

        log.fine( "Ship Date=" + shipDate );

        int AD_Org_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Org_ID" );

        log.fine( "Org=" + AD_Org_ID );

        int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );

        log.fine( "Warehouse=" + M_Warehouse_ID );

        int billC_BPartner_Location_ID = Env.getContextAsInt( ctx,WindowNo,"Bill_Location_ID" );

        if( billC_BPartner_Location_ID == 0 ) {
            billC_BPartner_Location_ID = shipC_BPartner_Location_ID;
        }

        log.fine( "Bill BP_Location=" + billC_BPartner_Location_ID );

        //

        int C_Tax_ID = Tax.get( ctx,M_Product_ID,C_Charge_ID,billDate,shipDate,AD_Org_ID,M_Warehouse_ID,billC_BPartner_Location_ID,shipC_BPartner_Location_ID,"Y".equals( Env.getContext( ctx,WindowNo,"IsSOTrx" )));

        log.info( "Tax ID=" + C_Tax_ID );

        //

        if( C_Tax_ID == 0 ) {
            mTab.fireDataStatusEEvent( CLogger.retrieveError());
        } else {
            mTab.setValue( "C_Tax_ID",new Integer( C_Tax_ID ));
        }

        //

        if( steps ) {
            log.warning( "fini" );
        }

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
        log.fine("......comienza calloutOrder amt.......");

        if( steps ) {
            log.warning( "amt - init" );
        }
        int M_Product_ID   = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );
        if (M_Product_ID==0)
       
        	//setCalloutActive( false );
        	return "";

        int C_UOM_To_ID    = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );
        
        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );
        int StdPrecision = MPriceList.getStandardPrecision( ctx,M_PriceList_ID );
        BigDecimal QtyEntered,QtyOrdered,PriceEntered,PriceActual,PriceLimit,Discount,PriceList;
        //Modificado por ConSerTi. Si no hay producto (es un cargo), retornamos sin hacer nada.
        	
        // get values

        QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
        QtyOrdered = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        log.fine( "QtyEntered=" + QtyEntered + ", Ordered=" + QtyOrdered + ", UOM=" + C_UOM_To_ID );

        //

        PriceEntered = ( BigDecimal )mTab.getValue( "PriceEntered" );
        PriceActual  = ( BigDecimal )mTab.getValue( "PriceActual" );
        Discount     = ( BigDecimal )mTab.getValue( "Discount" );
        PriceLimit   = ( BigDecimal )mTab.getValue( "PriceLimit" );
        PriceList    = ( BigDecimal )mTab.getValue( "PriceList" );
        log.fine( "PriceList=" + PriceList + ", Limit=" + PriceLimit + ", Precision=" + StdPrecision );
        log.fine( "<...>  PriceEntered=" + PriceEntered + ", Actual=" + PriceActual + ", Discount=" + Discount );

        // Qty changed - recalc price

        if(( mField.getColumnName().equals( "QtyOrdered" ) || mField.getColumnName().equals( "QtyEntered" ) || mField.getColumnName().equals( "M_Product_ID" )) &&!"N".equals( Env.getContext( ctx,WindowNo,"DiscountSchema" ))) {
            //int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
        	int C_Repair_Order_ID=Env.getContextAsInt( ctx,WindowNo,"C_Repair_Order_ID" );
        	MRepairOrder o=new MRepairOrder(ctx, C_Repair_Order_ID, null);
        	int C_BPartner_ID=o.getC_BPartner_ID();

            if( mField.getColumnName().equals( "QtyEntered" )) {
                QtyOrdered = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );
            }

            if( QtyOrdered == null ) {
                QtyOrdered = QtyEntered;
            }

            boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );
            MProductPricing pp = new MProductPricing( M_Product_ID,C_BPartner_ID,QtyOrdered,IsSOTrx );

            pp.setM_PriceList_ID( M_PriceList_ID );

            int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

            pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

            Timestamp date = ( Timestamp )mTab.getValue( "DateOrdered" );

            pp.setPriceDate( date );

            //

            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,pp.getPriceStd());

            if( PriceEntered == null ) {
                PriceEntered = pp.getPriceStd();
            }

            //

            log.fine( "QtyChanged -> PriceActual=" + pp.getPriceStd() + ", PriceEntered=" + PriceEntered + ", Discount=" + pp.getDiscount());
            mTab.setValue( "PriceActual",pp.getPriceStd());
            mTab.setValue( "Discount",pp.getDiscount());
            mTab.setValue( "PriceEntered",PriceEntered );
            Env.setContext( ctx,WindowNo,"DiscountSchema",pp.isDiscountSchema()
                    ?"Y"
                    :"N" );
        } else if( mField.getColumnName().equals( "PriceActual" )) {
            PriceActual  = ( BigDecimal )value;
            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceActual );

            if( PriceEntered == null ) {
                PriceEntered = PriceActual;
            }

            //

            log.fine( "PriceActual=" + PriceActual + " -> PriceEntered=" + PriceEntered );
            mTab.setValue( "PriceEntered",PriceEntered );
        } else if( mField.getColumnName().equals( "PriceEntered" )) {
            PriceEntered = ( BigDecimal )value;
            PriceActual  = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,PriceEntered );

            if( PriceActual == null ) {
                PriceActual = PriceEntered;
            }

            //

            log.fine( "PriceEntered=" + PriceEntered + " -> PriceActual=" + PriceActual );
            mTab.setValue( "PriceActual",PriceActual );
        }

        // Discount entered - Calculate Actual/Entered

        if( mField.getColumnName().equals( "Discount" )) {
            PriceActual = new BigDecimal(( 100.0 - Discount.doubleValue()) / 100.0 * PriceList.doubleValue());

            if( PriceActual.scale() > StdPrecision ) {
                PriceActual = PriceActual.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
            }

            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceActual );

            if( PriceEntered == null ) {
                PriceEntered = PriceActual;
            }

            mTab.setValue( "PriceActual",PriceActual );
            mTab.setValue( "PriceEntered",PriceEntered );
        }

        // calculate Discount

        else {
            if( PriceList.intValue() == 0 ) {
                Discount = Env.ZERO;
            } else {
            	log.fine("pricelist = "+ PriceList.doubleValue()+ " - "+ "priceActual = "+PriceActual.doubleValue()+" / "+" pricelist*100.0 ="+ PriceList.doubleValue() * 100.0);
                Discount = new BigDecimal(( PriceList.doubleValue() - PriceActual.doubleValue()) / PriceList.doubleValue() * 100.0 );
            }

            if( Discount.scale() > 2 ) {
                Discount = Discount.setScale( 2,BigDecimal.ROUND_HALF_UP );
            }

            mTab.setValue( "Discount",Discount );
        }

        log.fine( "..PriceEntered=" + PriceEntered + ", Actual=" + PriceActual + ", Discount=" + Discount );

        // Check PriceLimit

        if( !( mField.getColumnName().equals( "QtyOrdered" ) || mField.getColumnName().equals( "QtyEntered" ))) {
            String  epl   = Env.getContext( ctx,WindowNo,"EnforcePriceLimit" );
            boolean check = (epl != null) && epl.equals( "Y" );

            if( check && MRole.getDefault().isOverwritePriceLimit()) {
                check = false;
            }

            // Check Price Limit?

            if( check && (PriceLimit.doubleValue() != 0.0) && (PriceActual.compareTo( PriceLimit ) < 0) ) {
                PriceActual  = PriceLimit;
                PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceLimit );

                if( PriceEntered == null ) {
                    PriceEntered = PriceLimit;
                }

                log.fine( "(under) PriceEntered=" + PriceEntered + ", Actual" + PriceLimit );
                mTab.setValue( "PriceActual",PriceLimit );
                mTab.setValue( "PriceEntered",PriceEntered );
                mTab.fireDataStatusEEvent( "UnderLimitPrice","" );

                // Repeat Discount calc

                if( PriceList.intValue() != 0 ) {
                	log.fine("Repeat Discount cal..pricelist = "+ PriceList.doubleValue()+ " - "+ "priceActual = "+PriceActual.doubleValue()+" / "+" pricelist*100.0 ="+ PriceList.doubleValue() * 100.0);
                    Discount = new BigDecimal(( PriceList.doubleValue() - PriceActual.doubleValue()) / PriceList.doubleValue() * 100.0 );

                    if( Discount.scale() > 2 ) {
                        Discount = Discount.setScale( 2,BigDecimal.ROUND_HALF_UP );
                    }

                    mTab.setValue( "Discount",Discount );
                }
            }
        }

        // Line Net Amt

        BigDecimal LineNetAmt = QtyOrdered.multiply( PriceActual );

        if( LineNetAmt.scale() > StdPrecision ) {
            LineNetAmt = LineNetAmt.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        log.info( "LineNetAmt=" + LineNetAmt );
        mTab.setValue( "LineNetAmt",LineNetAmt );

        //

        setCalloutActive( false );

        return "";
    }    // amt

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

    public String qty( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	PreparedStatement pstmt = null;
        ResultSet         rs;
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );
        log.finer("----------- Empreznado CallloutOrder qty----------");

        int M_Product_ID = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );
        //Modificado por ConSerTi. Si no hay producto (es un cargo), retornamos sin hacer nada.
               
        if (M_Product_ID==0)
        
        	//setCalloutActive( false );
        	return "";
        if( steps ) {
            log.warning( "init - M_Product_ID=" + M_Product_ID + " - " );
        }

        BigDecimal QtyOrdered,QtyEntered,PriceActual,PriceEntered;

        // No Product

        if( M_Product_ID == 0 ) {
            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            mTab.setValue( "QtyOrdered",QtyEntered );
        }
        
        // UOM Changed - convert from Entered -> Product

        else if( mField.getColumnName().equals( "C_UOM_ID" )) {
            int C_UOM_To_ID = (( Integer )value ).intValue();

            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            QtyOrdered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( QtyOrdered == null ) {
                QtyOrdered = QtyEntered;
            }

            boolean conversion = QtyEntered.compareTo( QtyOrdered ) != 0;

            PriceActual  = ( BigDecimal )mTab.getValue( "PriceActual" );
            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceActual );

            if( PriceEntered == null ) {
                PriceEntered = PriceActual;
            }

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual + " ---> " + conversion + " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyOrdered",QtyOrdered );
            mTab.setValue( "PriceEntered",PriceEntered );
        }

        // QtyEntered changed - calculate QtyOrdered

        else if( mField.getColumnName().equals( "QtyEntered" )) {
            int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            QtyOrdered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( QtyOrdered == null ) {
                QtyOrdered = QtyEntered;
            }
            //Añadido por ConSerTi. Ahora no tiene en cuenta el pedido minimo que esta definido en el producto. Tiene que 
            //dar una alerta si se intenta hace un pedido inferior al marcado.
            if(M_Product_ID!=0)//Temenos Producto
            {
            	//Vemos si tiene definino un minimo de pedido.
            	
            	String sql = new String( "SELECT order_min"
            							+ " FROM M_Product_Po"
            							+ " WHERE M_Product_ID = " + M_Product_ID);
                
                try {
                    pstmt = DB.prepareStatement( sql );
                    rs    = pstmt.executeQuery();
                    while (rs.next())
                   	{
           
                    	//Añadido por ConSerTi. Si el numero de unidades pedidas no alcanza el minimo establecdio, se advierte al usuario
                    	//y se cambia automaticamente al numero especificado en las propiedades de compra del articulo.
                    	if (QtyEntered.compareTo(rs.getBigDecimal("order_min"))== -1)
                    	{	
                    		JOptionPane.showMessageDialog( null,"El pedido minimo para este producto es de "+rs.getInt("order_min")+"\n"+ "Se Actualizara automaticamente la linea del pedido.\n"+"Si desea hacer un pedio inferior, debera cambiar dicho valor en las propiedades del produto","PRODUCTO CON CANTIDAD MINIMA DE PEDIDO", JOptionPane.INFORMATION_MESSAGE );
                    		QtyEntered=rs.getBigDecimal("order_min");
                    		mTab.setValue( "QtyEntered",QtyEntered );
                    		mTab.setValue("Ordered", QtyEntered);
                    	}
                   	}
                } catch( Exception e ) {
                    log.log( Level.SEVERE,"\n " + "No se puede ejecutar la consulta" );
                }
               
                	
            }//if
            //Fin del añadido.

            boolean conversion = QtyEntered.compareTo( QtyOrdered ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyOrdered=" + QtyOrdered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyOrdered",QtyOrdered );
        }

        // QtyOrdered changed - calculate QtyEntered

        else if( mField.getColumnName().equals( "QtyOrdered" )) {
            int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            QtyOrdered = ( BigDecimal )value;
            QtyEntered = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,QtyOrdered );

            if( QtyEntered == null ) {
                QtyEntered = QtyOrdered;
            }
            //Añadido por ConSerTi. Ahora no tiene en cuenta el pedido minimo que esta definido en el producto. Tiene que 
            //dar una alerta si se intenta hace un pedido inferior al marcado.
            if(M_Product_ID!=0)//Temenos Producto
            {
            	//Vemos si tiene definino un minimo de pedido.
            	
            	String sql = new String( "SELECT order_min"
            							+ " FROM M_Product_Po"
            							+ " WHERE M_Product_ID = " + M_Product_ID);
                
                try {
                    pstmt = DB.prepareStatement( sql );
                    rs    = pstmt.executeQuery();
                    while (rs.next())
                   	{
                    	JOptionPane.showMessageDialog( null,"CallourOrder con order_min= "+rs.getInt("order_min")+" y el pedido introducido es de = "+ QtyEntered,null, JOptionPane.INFORMATION_MESSAGE );
                   
                   	}
                } catch( Exception e ) {
                    log.log( Level.SEVERE,"\n " + "No se puede ejecutar la consulta" );
                }
               
                	
            }//if
            //Fin del añadido.


            boolean conversion = QtyOrdered.compareTo( QtyEntered ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyOrdered + " -> " + conversion + " QtyEntered=" + QtyEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyEntered",QtyEntered );
        }
        

        //

        setCalloutActive( false );

        return "";
    }    // qty
}    // CalloutRepairOrder



/*
 *  @(#)CalloutOrder.java   02.07.07
 * 
 *  Fin del fichero CalloutOrder.java
 *  
 *  Versión 2.2
 *
 */
