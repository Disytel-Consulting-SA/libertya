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

import java.awt.Component;
import java.math.*;

import java.sql.*;
import java.text.MessageFormat;

import java.util.*;
import java.util.logging.*;

import javax.swing.JOptionPane;

import org.openXpertya.model.MRole;
import org.openXpertya.model.attribute.RecommendedAtributeInstance;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutOrder extends CalloutEngine {

	protected static CLogger s_log = CLogger.getCLogger( CalloutOrder.class );
	
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
                                    + "s.AD_Sequence_ID,d.IsSOTrx, d.AllowChangePriceList "                                  // 7..8
                                    + "FROM C_DocType d, AD_Sequence s " + "WHERE C_DocType_ID=?"    // #1
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

        String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID," + "p.M_PriceList_ID,p.PaymentRule,p.POReference," + "p.SO_Description,p.IsDiscountPrinted," + "p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule," + "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable," + "lship.C_BPartner_Location_ID,c.AD_User_ID," + "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID," + "lbill.C_BPartner_Location_ID AS Bill_Location_ID, p.SOCreditStatus, C_Categoria_Iva_ID " + "FROM C_BPartner p" + " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')" + " LEFT OUTER JOIN C_BPartner_Location lship ON (p.C_BPartner_ID=lship.C_BPartner_ID AND lship.IsShipTo='Y' AND lship.IsActive='Y')" + " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) " + "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
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
                	// Disytel: no default value
                   /* int i = Env.getContextAsInt( ctx,"#M_PriceList_ID" );

                    if( i != 0 ) {
                        mTab.setValue( "M_PriceList_ID",new Integer( i ));
                    }*/
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

                // Seteo Comercial/Usuario dependiendo la EC seleccionada
                
                MBPartner ec = new MBPartner(ctx, C_BPartner_ID, null);
            	int contEC = ec.getSalesRep_ID();
            	
            	if (contEC == 0){
            		mTab.setValue( "SalesRep_ID",null );
            	} else {
            		mTab.setValue( "SalesRep_ID",contEC );
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
                    
                    Integer categoriaIva = rs.getInt("C_Categoria_Iva_ID");
    				
    				// Se setea el codigo de iva del bPartner en el contexto
                    if(!Util.isEmpty(categoriaIva, true)){
                    	int codigoIva =  MCategoriaIva.getCodigo(categoriaIva, null);
        				Env.setContext(ctx, WindowNo, "CodigoCategoriaIVA", codigoIva);
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
        } catch ( NumberFormatException e ) {
        	log.log( Level.SEVERE,"bPartner",e );
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

        String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID," + "p.M_PriceList_ID,p.PaymentRule,p.POReference," + "p.SO_Description,p.IsDiscountPrinted," + "p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule," + "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable," + "c.AD_User_ID," + "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID," + "lbill.C_BPartner_Location_ID AS Bill_Location_ID " + "FROM C_BPartner p" + " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')" + " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) " + "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
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
                	// Disytel: no default value                	
                 /*   int i = Env.getContextAsInt( ctx,"#M_PriceList_ID" );

                    if( i != 0 ) {
                        mTab.setValue( "M_PriceList_ID",new Integer( i ));
                    }*/
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

        String SQL = "SELECT pl.IsTaxIncluded,pl.EnforcePriceLimit,pl.C_Currency_ID,c.StdPrecision," + "plv.M_PriceList_Version_ID,plv.ValidFrom " + "FROM M_PriceList pl,C_Currency c,M_PriceList_Version plv " + "WHERE pl.C_Currency_ID=c.C_Currency_ID" + " AND pl.M_PriceList_ID=plv.M_PriceList_ID" + " AND pl.M_PriceList_ID=? "    // 1
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

		// Manejar la modificación de precios de las líneas dependiendo del tipo
		// de documento
		handlePricesModification(ctx, mTab);
        
        return "";
    }    // priceList
    
    
    public void handlePricesModification(Properties ctx, MTab mTab){
    	// Verificar si el tipo de documento permite modificar la tarifa, en ese
		// caso, se debe avisar con un warning si existen líneas con precios
		// modificados
        Integer docTypeID = (Integer)mTab.getValue("C_DocTypeTarget_ID");
        if(!Util.isEmpty(docTypeID, true)){
        	MDocType docType = new MDocType(ctx, docTypeID, null);
        	if(docType.isAllowChangePriceList()){
        		boolean isSOTrx = docType.isSOTrx(); 
        		// Si el id del pedido no existe es porque es un registro nuevo
        		Integer orderID = (Integer)mTab.getValue("C_Order_ID"); 
        		Integer M_PriceList_ID = (Integer)mTab.getValue("M_PriceList_ID");;
        		StringBuffer msg = new StringBuffer(Msg.getMsg(ctx, "ProductsPriceListDiffersPriceActualWarn"));
        		boolean showWarn = false;
        		if(isSOTrx && !Util.isEmpty(orderID,true)){
					// La tarifa nueva debe ser distinta a la que tiene el
					// pedido en la base, sino no modifico los valores
        			MOrder order = new MOrder(ctx, orderID, null);
        			boolean validatePrices = order.getM_PriceList_ID() != M_PriceList_ID;
        			if(!validatePrices){
    					// Otra validación a tener en cuenta es que las fechas
    					// difieran, en ese caso se debe verificar que las versiones
    					// de la lista de precio sean distintas dependiendo la fecha
        				Timestamp dateOrdered = (Timestamp)mTab.getValue("DateOrdered");
						validatePrices = dateOrdered != null
								&& dateOrdered
										.compareTo(order.getDateOrdered()) != 0;
        				if(validatePrices){
        					MPriceList priceList = MPriceList.get(ctx, M_PriceList_ID, null);
							MPriceListVersion pricelistVersionOrder = priceList
									.getPriceListVersion(
											order.getDateOrdered(), true);
							MPriceListVersion pricelistVersionActual = priceList
									.getPriceListVersion(dateOrdered, true);
							validatePrices = (pricelistVersionOrder == null && pricelistVersionActual != null)
									|| (pricelistVersionOrder != null && pricelistVersionActual == null)
									|| (pricelistVersionActual.getID() != pricelistVersionOrder
											.getID());
        				}
        			}
        			
					if (validatePrices
							&& (MOrder.DOCSTATUS_Drafted.equals(order.getDocStatus()) 
									|| MOrder.DOCSTATUS_InProgress.equals(order.getDocStatus()))) {
        				String lineNoMsg = Msg.getElement(ctx, "Line");
            			String productMsg = Msg.getElement(ctx, "M_Product_ID");
            			String priceActualMsg = Msg.getElement(ctx, "PriceActual");
            			// Verificar los artículos que pertenecen al pedido que poseen el
            			// precio modificado
            			PreparedStatement ps = null;
            			ResultSet rs = null;
            			String sql = "SELECT ol.line, p.value, p.name, ol.pricelist, ol.priceactual " +
            						 "FROM c_orderline as ol " +
            						 "INNER JOIN m_product as p on ol.m_product_id = p.m_product_id " +
            						 "WHERE c_order_id = ? AND ol.priceactual <> ol.pricelist " +
            						 "ORDER BY ol.line";
            			try {
            				ps = DB.prepareStatement(sql);
            				ps.setInt(1, orderID);
            				rs = ps.executeQuery();
            				HTMLMsg linesMsg = new HTMLMsg();
            				HTMLMsg.HTMLList lineList;
            				int listID = 0;
            				int lineID;
            				while(rs.next()){
            					showWarn = true;
            					lineID = 0;
            					// Crea la lista
            					lineList = linesMsg.createList("list_"+listID, "ul");
            					// Nro de Línea
            					linesMsg.createAndAddListElement("linelist_" + lineID++,
            							lineNoMsg + ": " + rs.getString("line"), lineList);
            					// Artículo
            					linesMsg.createAndAddListElement("linelist_" + lineID++,
            							productMsg + ": " + rs.getString("value") + " - "
            									+ rs.getString("name"), lineList);
            					// Precio actual
            					linesMsg.createAndAddListElement(
            							"linelist_" + lineID++,
            							priceActualMsg
            									+ ": "
            									+ String.valueOf(rs
            											.getBigDecimal("priceactual")),
            							lineList);
            					// Agrego la lista al mensaje
            					linesMsg.addList(lineList);
            					listID++;
            				}
            				msg.append(linesMsg.toString());
            				msg.append(Msg.getMsg(ctx, "SaveChanges?"));
            			} catch (Exception e) {
            				log.severe(e.getMessage());
            			} finally{
            				try {
            					if(rs != null)rs.close();
            					if(ps != null)ps.close();
            				} catch (Exception e2) {
            					log.severe(e2.getMessage());
            				}
            			}
        			}
        		}
        		
        		if(showWarn){
        			mTab.setCurrentRecordWarning(msg.toString());
        		}
        		else{
        			mTab.clearCurrentRecordWarning();
        		}
        	}
        }
    }

    
    public String partner_location( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Location_ID = ( Integer )value;
        Integer location=null;
        String full_location="";
        if( (C_Location_ID == null) || (C_Location_ID.intValue() == 0) ) {
            return "";
        }
        log.fine("La location_id="+C_Location_ID);
        String sql="SELECT c_location_id from C_BPartner_Location where c_bpartner_id=?";
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Location_ID.intValue());

            ResultSet rs = pstmt.executeQuery();
            
            while( rs.next()) {
             location = new Integer( rs.getInt( 1 ));
             log.fine("Dentro del if location="+location);
            }
            rs.close();
            pstmt.close();
        }catch( SQLException e ) {
            log.log( Level.SEVERE,"priceList",e );

            return e.getLocalizedMessage();
        }
        
        MLocation loc = new MLocation(ctx,location.intValue(),null);
        full_location=loc.toStringCR();
        log.fine("Full location con toStringCR:"+loc.toStringCR());
        log.fine("Full location con toString simplemente:"+loc.toString());
        log.fine("Full location con toStringX:"+loc.toStringX());
        full_location=loc.getAddress1()+","+loc.getCity()+","+loc.getCityRegionPostal()+","+loc.getCountryName();
        log.fine("y con todo"+full_location);
        mTab.setValue("C_BPartner_Location_ID", new String (full_location));
        

        return "";
    }    // priceList
    public static RecommendedAtributeInstance[] getAttributeSetInstanceSuggestion(Component c, int M_Product_ID, int M_Warehouse_ID, BigDecimal Qty) throws SQLException {
    	
    	/*
    	 * si no existen suficiente mercancia con el mismo conjunto de atributos, solicitara confirmacion para 
    	 * partir las lineas de entrada, de manera que si para suplir el pedido, hay que meter usar mercancia 
    	 * con 3 conjuntos de atributos, partira las lineas, para incluir esta informacion. Si el usuario dice 
    	 * que ni quiere partir las lineas, buscara un conjunto de atributos capaz de suplir el pedido, aunque 
    	 * su fecha de caducidad sea anterior. Si no existier� conjunto capaz de suplir el pedido sin partir 
    	 * lineas, las partira obligatoriamente. 
    	 */
    	
    	RecommendedAtributeInstance[] raiMulti = MProduct.getRecommendedAtributeInstance(M_Product_ID, Qty, false, M_Warehouse_ID);
    	RecommendedAtributeInstance[] raiUni = MProduct.getRecommendedAtributeInstance(M_Product_ID, Qty, true, M_Warehouse_ID);
    	RecommendedAtributeInstance[] rai = raiMulti;
    	
    	s_log.log(Level.CONFIG, "+++ getASIS: Product: " + M_Product_ID + " Qty: " + Qty + " Warehouse: " + M_Warehouse_ID);
    	s_log.log(Level.CONFIG, "+++ getASIS: raiMulti: " + raiMulti.length + " raiUni: " + raiUni.length);
    	
    	if (raiMulti.length > 1) {
    		int x = JOptionPane.showConfirmDialog(c, Msg.translate(Env.getCtx(), "RaiWouldLikeToSplit"), "", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    		
    		if (x == JOptionPane.NO_OPTION) {
    			if (raiUni.length == 1) {
    				rai = raiUni;
    			} else {
    				JOptionPane.showMessageDialog(c, Msg.translate(Env.getCtx(), "RaiWillSplitAnyways"), "", JOptionPane.WARNING_MESSAGE);
    			}
    		}
    	}
    	
    	if (rai == null || rai.length == 0) {
    		/* "No existen suficientes elementos en el almacén para satisfacer el pedido." */
    		JOptionPane.showMessageDialog(c, Msg.translate(Env.getCtx(), "NotEnoughStocked"), "", JOptionPane.ERROR_MESSAGE);
    		return null;
    	}
    	
   		return rai;
    }
    
    public static RecommendedAtributeInstance[] updateAttributeSetInstance(MTab mTab, int WindowNo, int M_Product_ID, int M_Warehouse_ID) throws SQLException {
        RecommendedAtributeInstance[] rai = null;
        BigDecimal Qty = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        
        if (!MAttributeSet.ProductNeedsInstanceAttribute(M_Product_ID, null)) {
        	mTab.setValue( "M_AttributeSetInstance_ID", 0);
        	return null;
        }
        
        if (Qty == null)
        	Qty = ( BigDecimal )mTab.getValue( "QtyEntered" );
        
       	rai = getAttributeSetInstanceSuggestion(Env.getWindow(WindowNo), M_Product_ID, M_Warehouse_ID, Qty);
        
        if (rai != null && rai.length > 0) {
        	mTab.setValue( "M_AttributeSetInstance_ID", rai[0].getM_AtributeInstance_ID());
        	mTab.setValue( "QtyOrdered", rai[0].getQtyOnHand());
        	mTab.setValue( "QtyEntered", rai[0].getQtyOnHand());
        	mTab.setValue( "MovementQty", rai[0].getQtyOnHand());
        }
        
        return rai;
    }
    
    private void updateMasi(Properties ctx,int WindowNo,MTab mTab, Integer M_Product_ID) {
    	
    	if (M_Product_ID == null)
    		return;
    	
    	Integer M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );
    	Integer C_Order_ID = (Integer)mTab.getValue( "C_Order_ID" );
    	
    	// Set Attribute

        if (MAttributeSet.GetCondicionesAtributosByWindowNo(WindowNo, mTab.getTabNo()).isAutoSuggestAttributeSetInstance()) {
	        RecommendedAtributeInstance[] rai = null;
	        
	        try {
	        	rai = updateAttributeSetInstance(mTab, WindowNo, M_Product_ID, M_Warehouse_ID);
	        } catch (SQLException e) {
	        	log.log(Level.SEVERE, "getAttributeSetInstanceSuggestion", e);
	        }
	        
	        if (rai != null && rai.length > 1) {
	        	
	        	if (!mTab.dataSave(false) || mTab.getValue( "C_OrderLine_ID" ) == null)
	        		return ;
	        	
	    		MOrder oo = new MOrder(ctx, C_Order_ID, null);
    			MOrderLine cur = new MOrderLine(ctx, (Integer)mTab.getValue( "C_OrderLine_ID" ), null );
	
	    		for (int i = 1; i < rai.length; i++) {
	    			RecommendedAtributeInstance r = rai[i];
	    			MOrderLine l = new MOrderLine(oo);

	    			PO.copyValues(cur, l);
	    			
	    			l.setQty(r.getQtyOnHand());
	    			l.setM_AttributeSetInstance_ID(r.getM_AtributeInstance_ID());
	    			// l.setC_Tax_ID((Integer)mTab.getValue("C_Tax_ID"));
	    			
	    			l.save();
	    		}
	        }

	        mTab.dataRefresh();
	        
        } else {
	        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
	            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
	        } else {
	            mTab.setValue( "M_AttributeSetInstance_ID",null );
	        }
        }
    }
    
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
    	Integer M_Product_ID = mField.getColumnName().equals("M_Product_ID") ? ( Integer )value : (Integer)mTab.getValue("M_Product_ID") ;
        boolean    IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );

        log.fine("En CalloutOrder.product con Propiertes ="+ ctx+ " MTab= "+ mTab+ " Mfiel= "+ mField+ "Object= "+ value );

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        if( steps ) {
            log.warning( "product - init" );
        }

        if (isCalloutActive())
        	return "";
        
        setCalloutActive( true );

        //
        
        if (mField.getColumnName().equals("M_Product_ID")) {
	        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
	            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
	        } else {
	            mTab.setValue( "M_AttributeSetInstance_ID",null );
	        }
        }
        
        //
        
        int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
        int C_Order_ID = (Integer)mTab.getValue( "C_Order_ID" );
        BigDecimal Qty     = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );
        
        mTab.setValue( "C_Charge_ID",null );

        // int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
        // BigDecimal Qty     = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        Integer M_AttributeSetInstance_ID = (Integer)mTab.getValue("M_AttributeSetInstance_ID");
        if (M_AttributeSetInstance_ID == null)
        	M_AttributeSetInstance_ID = new Integer(0);
        
        MProductPricing pp = new MProductPricing( M_Product_ID.intValue(),C_BPartner_ID,Qty,IsSOTrx,M_AttributeSetInstance_ID );

        //

        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );

        pp.setM_PriceList_ID( M_PriceList_ID );

        int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

        pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

        Timestamp orderDate = ( Timestamp )mTab.getValue( "DateOrdered" );

        pp.setPriceDate( orderDate );

        //

        // Disytel: Conversion entre el precio de la tarifa y la moneda destino de la cabecera
        String orderID = Env.getContext(ctx, WindowNo, 0, "C_Order_ID");
        MOrder order = new MOrder(ctx, Integer.parseInt(orderID), null);
        
        int priceListCurrency = (new MPriceList(ctx, order.getM_PriceList_ID(), null)).getC_Currency_ID();
        int targetCurrency = order.getC_Currency_ID();
        
        if (priceListCurrency == targetCurrency)
        {
            mTab.setValue( "PriceList",pp.getPriceList());
            mTab.setValue( "PriceLimit",pp.getPriceLimit());
            mTab.setValue( "PriceActual",pp.getPriceStd());
            mTab.setValue( "PriceEntered",pp.getPriceStd());
            mTab.setValue( "C_Currency_ID",pp.getC_Currency_ID());
        }
        else
        {
            mTab.setValue( "PriceList", MCurrency.currencyConvert(pp.getPriceList(), priceListCurrency, targetCurrency, order.getDateOrdered(), order.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceLimit", MCurrency.currencyConvert(pp.getPriceLimit(), priceListCurrency, targetCurrency, order.getDateOrdered(), order.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceActual", MCurrency.currencyConvert(pp.getPriceStd(), priceListCurrency, targetCurrency, order.getDateOrdered(), order.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceEntered", MCurrency.currencyConvert(pp.getPriceStd(), priceListCurrency, targetCurrency, order.getDateOrdered(), order.getAD_Org_ID(), ctx) );
            mTab.setValue( "C_Currency_ID", targetCurrency);  
        }
        
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

        // Validación de cantidad mínima de producto
        
        String m = validarOrderMin(mTab, M_Product_ID, C_BPartner_ID);
       
        // Actualizar M_AttributeSetInstance_ID
        
        // updateMasi(ctx, WindowNo, mTab, M_Product_ID);
        
        Qty = ( BigDecimal )mTab.getValue( "QtyOrdered" );
        
        //

        setCalloutActive( false );

        validateProductQty(ctx, WindowNo, mTab);
        
        if( steps ) {
            log.warning( "product - fini" );
        }

        String res = tax( ctx,WindowNo,mTab,mField,value ); 
        if (res !=null && "".equals(res) && m != null)
        {
        	BigDecimal theMinimum = dameOrderMin(mTab, M_Product_ID, Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" ));
    		mTab.setValue( "QtyEntered", theMinimum);        	
        	return m;
        }
        else
        	return res;

        
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
            //mTab.setValue( "C_Charge_ID",null );
            //mTab.dataRefresh(mTab.getCurrentRow());
            //return "ChargeExclusively";
        	return "";
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
        	// Bug fix: No encontraba en el contexto la property correspondiente
        	// 			Se obtiene directamente desde la MOrder almacenada
            // shipC_BPartner_Location_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_Location_ID" );
        	MOrder order = new MOrder(ctx, (Integer)mTab.getValue("C_Order_ID"), null);
        	shipC_BPartner_Location_ID = order.getC_BPartner_Location_ID();
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

        int C_Tax_ID = 0;
        // Si los Comprobantes fiscales están activos se busca la tasa de impuesto a partir de la categoría de IVA debe estar condicionado 
        if (CalloutInvoiceExt.ComprobantesFiscalesActivos()) {
        	int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
            C_Tax_ID = DB.getSQLValue( null,"SELECT C_Tax_ID FROM C_Categoria_Iva ci INNER JOIN C_BPartner bp ON (ci.C_Categoria_Iva_ID = bp.C_Categoria_Iva_ID) WHERE bp.C_BPartner_ID = ?",C_BPartner_ID );
        }
        
        if( C_Tax_ID == 0 ) {
        	C_Tax_ID = Tax.get( ctx,M_Product_ID,C_Charge_ID,billDate,shipDate,AD_Org_ID,M_Warehouse_ID,billC_BPartner_Location_ID,shipC_BPartner_Location_ID,Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" ));
        }

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

    public static BigDecimal dameOrderMin(MTab mTab, int M_Product_ID, int C_BPartner_ID) {
    	Integer UomID = (Integer)mTab.getValue("C_UOM_ID");
    	BigDecimal QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
    	
    	if ("Y".equals( Env.getContext( Env.getCtx(), mTab.getWindowNo(),"IsSOTrx" )))
    		return null;
    	
    	
    	// Convertir a la unidad del ARTICULO
    	
    	if (UomID == null)
    		return null;
    	
    	QtyEntered = MUOMConversion.convertProductFrom(Env.getCtx(), M_Product_ID, UomID, QtyEntered);
    	
    	if (QtyEntered == null)
    		return null;
    	
        MProductPO[] pos = MProductPO.getOfProduct(Env.getCtx(), M_Product_ID, null);
        if (QtyEntered != null && pos.length > 0) {
        	for (MProductPO p : pos) {
        		if (p.getC_BPartner_ID() == C_BPartner_ID) {
	        		BigDecimal OrderMin = p.getOrder_Min();
	        		if (OrderMin != null) {
	        			OrderMin = MUOMConversion.convertProductFrom(Env.getCtx(), M_Product_ID, p.getC_UOM_ID(), OrderMin);
	        			return OrderMin;
	        		}
	        		
	        		// Puede haber como maximo uno solo
	        		// CONSTRAINT m_product_po_key PRIMARY KEY (c_bpartner_id, m_product_id),
	        		break;  
        		}
        	}
        }
        
        return null;	
    	
    }
    
    public static String validarOrderMin(MTab mTab, int M_Product_ID, int C_BPartner_ID) {
    	BigDecimal OrderMin = dameOrderMin(mTab, M_Product_ID, C_BPartner_ID);
    	BigDecimal QtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
    	
		if (OrderMin != null && OrderMin.compareTo(QtyEntered) > 0) 
			return MessageFormat.format( Msg.getMsg(Env.getCtx(), "QtyEnteredLessThanMinQty"), OrderMin);

		return null;

    }
    
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
        
        // En pestañas con tabs incluidas, el valor de M_Product_ID puede ser erroneo en el contexto
        //int M_Product_ID   = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );
        Integer M_Product_ID = (Integer)mTab.getValue("M_Product_ID");
        
        if (M_Product_ID == null || M_Product_ID==0) {
        	setCalloutActive( false );
        	return "";
        }
        
        // Validaci�n de cantidad m�nima de producto
        
        
       
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
            if( mField.getColumnName().equals( "QtyEntered" )) {
                QtyOrdered = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );
            }

            if( QtyOrdered == null ) {
                QtyOrdered = QtyEntered;
            }
            
            Integer M_AttributeSetInstance_ID = (Integer)mTab.getValue("M_AttributeSetInstance_ID");
            if (M_AttributeSetInstance_ID == null)
            	M_AttributeSetInstance_ID = new Integer(0);
            
            if(mField.getColumnName().equals( "M_Product_ID" ) && !"N".equals( Env.getContext( ctx,WindowNo,"DiscountSchema" ))){
            
            	int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
            	boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );
            
	            MProductPricing pp = new MProductPricing( M_Product_ID,C_BPartner_ID,QtyOrdered,IsSOTrx,M_AttributeSetInstance_ID );
	
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
            }
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

        // Calcula el Line Total Amt
        Integer taxID = (Integer)mTab.getValue("C_Tax_ID");
        BigDecimal lineTaxAmt = BigDecimal.ZERO;
        if (taxID != null && taxID > 0) {
            MPriceList priceList = MPriceList.get(ctx, M_PriceList_ID, null);
            MTax tax = MTax.get(ctx, taxID, null);
			lineTaxAmt = tax.calculateTax(LineNetAmt,
					priceList.isTaxIncluded(),
					MPriceList.getStandardPrecision(ctx, M_PriceList_ID));
        }
        BigDecimal lineTotalAmt = LineNetAmt.add(lineTaxAmt);
        mTab.setValue( "LineTotalAmt",lineTotalAmt);

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
        String m = null;
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );
        log.finer("----------- Empreznado CallloutOrder qty----------");

        int M_Product_ID = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );
        //Modificado por ConSerTi. Si no hay producto (es un cargo), retornamos sin hacer nada.
               
        if (M_Product_ID==0) {
        	setCalloutActive( false );
        	return "";
        }
        
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
            //Da una alerta si se intenta hace un pedido inferior al marcado y se setea el minimo
            if(M_Product_ID!=0)//Temenos Producto
            {
            	//Vemos si tiene definino un minimo de pedido.
            	m = validarOrderMin(mTab, M_Product_ID, Env.getContextAsInt( ctx,WindowNo, "C_BPartner_ID" ));
            	if (m != null && !"".equals(m)) 
            	{
            		QtyEntered = dameOrderMin(mTab, M_Product_ID, Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" ));
            		mTab.setValue( "QtyEntered",QtyEntered );
             		QtyOrdered = QtyEntered;
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
            /*
            //Añadido por ConSerTi. Ahora no tiene en cuenta el pedido minimo que esta definido en el producto. Tiene que 
            //dar una alerta si se intenta hace un pedido inferior al marcado.
            if(M_Product_ID!=0)//Temenos Producto
            {
            	//Vemos si tiene definino un minimo de pedido.
            	
            	String sql = new String( "SELECT order_min" + " FROM M_Product_Po" + " WHERE M_Product_ID = " + M_Product_ID);
                
                try {
                    pstmt = DB.prepareStatement( sql );
                    rs    = pstmt.executeQuery();
                    while (rs.next())
                   	{
                    //	JOptionPane.showMessageDialog( null,"CallourOrder con order_min= "+rs.getInt("order_min")+" y el pedido introducido es de = "+ QtyEntered,null, JOptionPane.INFORMATION_MESSAGE );
                   
                   	}
                } catch( Exception e ) {
                    log.log( Level.SEVERE,"\n " + "No se puede ejecutar la consulta" );
                }
               
                	
            }//if
            //Fin del añadido.
             */

            boolean conversion = QtyOrdered.compareTo( QtyEntered ) != 0;

            log.fine( "UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyOrdered + " -> " + conversion + " QtyEntered=" + QtyEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyEntered",QtyEntered );
        }
        
        // updateMasi(ctx, WindowNo, mTab, (Integer)mTab.getValue( "M_Product_ID" ));
        
        //

        setCalloutActive( false );

        validateProductQty(ctx, WindowNo, mTab);
        
        if (m != null && !"".equals(m)) 
        	return m;
        return "";
        
    }    // qty
    
    public String pricegross( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	//setCalloutActive( true );
    	log.fine("Entro en el callout del pricegross con el value="+value);
    	Integer C_Product_ID = ( Integer )value;
    	if( (C_Product_ID == null) || (C_Product_ID.intValue() == 0) ) {
            return "";
        }
        BigDecimal pricegross=null;
        try {
            String            SQL   = "SELECT price_gross FROM m_discountschemabreak where m_product_id=? ";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_Product_ID.intValue());
           
            ResultSet rs = pstmt.executeQuery();
            if( rs.next()) {
            	log.fine("Esto es lo que saca de pricegross="+rs.getBigDecimal(1));
            	pricegross = rs.getBigDecimal(1);
            }
            if(pricegross!=null){
            	log.fine("Entro en que pricegross es distinto de 0");
            	mTab.setValue( "PriceList",pricegross);
            	mTab.setValue("PriceEntered", pricegross);
            	mTab.setValue("PriceActual",pricegross);
            }
            
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"charge..." + e );
            //setCalloutActive( false );
            return e.getLocalizedMessage();
        }
        //setCalloutActive( false );
        
     return "";
    }
    
    /**
     * Valida el stock disponible del producto, y setea un mensaje de warning en la pestaña
     * en caso de que el stock no alcance para la línea.
     */
    protected void validateProductQty( Properties ctx,int WindowNo,MTab mTab) {
        boolean valid = true;
    	Integer mProductID = (Integer)mTab.getValue("M_Product_ID");
    	BigDecimal qtyEntered = (BigDecimal)mTab.getValue("QtyEntered");
    	int M_Warehouse_ID = Env.getContextAsInt( ctx,WindowNo,"M_Warehouse_ID" );
    	boolean isSOTrx = "Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx"));
    	int c_Order_ID = Env.getContextAsInt( ctx,WindowNo,"C_Order_ID" );
    	BigDecimal qtyInOrder = BigDecimal.ZERO;
    	BigDecimal qtyAvl = BigDecimal.ZERO;
    	
    	if (c_Order_ID > 0) {
    		String sql = 
    			" SELECT COALESCE(SUM(QtyEntered),0.0) AS OrderQty FROM C_OrderLine " +
    			" WHERE C_Order_ID = ? AND M_Product_ID = ?";
    		
    		qtyInOrder = (BigDecimal)DB.getSQLObject(null, sql, new Object[] {c_Order_ID, mProductID}); 
    	}
    	
    	// Solo se valida si es pedido a cliente y se seleccionó un producto.
    	if(mProductID != null && mProductID > 0 && isSOTrx) {
            MProduct product = MProduct.get( ctx, mProductID);

            if( product.isStocked()) {
                qtyAvl = MStorage.getQtyAvailable( M_Warehouse_ID, mProductID,null );
                qtyAvl = qtyAvl == null ? BigDecimal.ZERO : qtyAvl.subtract(qtyInOrder);
                if(qtyEntered.compareTo(qtyAvl) > 0)
                	valid = false;
            }
        }
    	
    	if (valid) 
    		mTab.clearCurrentRecordWarning();
    	else {
    		mTab.setCurrentRecordWarning(Msg.getMsg(ctx,"InsufficientStockWarning",new Object[] { qtyAvl }));
    	}
    }
    
    public String dateOrdered( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	if( isCalloutActive() || (value == null) ) {
            return "";
        }
    	
    	handlePricesModification(ctx, mTab);
    	
    	setCalloutActive( false );
    	return "";
    }
   
}    // CalloutOrder



/*
 *  @(#)CalloutOrder.java   02.07.07
 * 
 *  Fin del fichero CalloutOrder.java
 *  
 *  Versión 2.2
 *
 */
