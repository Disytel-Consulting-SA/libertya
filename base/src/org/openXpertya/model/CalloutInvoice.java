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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutInvoice extends CalloutEngine {

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
        Integer C_DocType_ID = ( Integer )value;
        
        if( (C_DocType_ID == null) || (C_DocType_ID.intValue() == 0) ) {
            return "";
        }
        
        // Para seteo manual del nro de documento no debo sugerir nada
//        Boolean manualDocumentNo = null;
//        Object manualObj = mTab.getValue("ManualDocumentNo");
//        if(manualObj != null){
//        	if(manualObj instanceof String){
//        		manualDocumentNo = ((String)mTab.getValue("ManualDocumentNo")).equals("Y");
//        	}
//        	else{
//        		manualDocumentNo = (Boolean)mTab.getValue("ManualDocumentNo");
//        	}
//        }
//        if(manualDocumentNo != null && manualDocumentNo){
//        	return "";
//        }

        try {
            String SQL = "SELECT d.HasCharges,'N',d.IsDocNoControlled," + "s.CurrentNext, d.DocBaseType " + "FROM C_DocType d, AD_Sequence s " + "WHERE C_DocType_ID=?"    // 1
                         + " AND d.DocNoSequence_ID=s.AD_Sequence_ID(+)";
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_DocType_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {

                // Charges - Set Context

                Env.setContext( ctx,WindowNo,"HasCharges",rs.getString( 1 ));

                // DocumentNo

                if( rs.getString( 3 ).equals( "Y" )) {
                	if(mTab.isInserting() || mField.isChanged()){
                		mTab.setValue( "DocumentNo","<" + rs.getString( 4 ) + ">" );
                	}
                }

                // DocBaseType - Set Context

                String s = rs.getString( 5 );

                Env.setContext( ctx,WindowNo,"DocBaseType",s );

                // AP Check & AR Credit Memo

                if( s.startsWith( "AP" )) {
                	if(mTab.isInserting() || mField.isChanged()){
                		mTab.setValue( "PaymentRule","S" );    // Check
                	}
                } else if( s.endsWith( "C" )) {
                	if(mTab.isInserting() || mField.isChanged()){
                		mTab.setValue( "PaymentRule","P" );    // OnCredit
                	}
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"socType",e );

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

        String SQL = "SELECT p.AD_Language,p.C_PaymentTerm_ID," + "p.M_PriceList_ID,p.PaymentRule,p.POReference," + "p.SO_Description,p.IsDiscountPrinted," + "p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable," + "l.C_BPartner_Location_ID,c.AD_User_ID," + "p.PO_PriceList_ID, p.PaymentRulePO, p.PO_PaymentTerm_ID " + "FROM C_BPartner p " + " LEFT OUTER JOIN C_BPartner_Location l ON (p.C_BPartner_ID=l.C_BPartner_ID AND l.IsBillTo='Y' AND l.IsActive='Y')" + " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID) " + "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";    // #1
        boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,C_BPartner_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            //

            if( rs.next()) {

                // PriceList & IsTaxIncluded & Currency

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

                    mTab.setValue( "PaymentRule",s );
                }

                // Payment Term

                ii = new Integer( rs.getInt( IsSOTrx
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

                // PO Reference

                s = rs.getString( "POReference" );

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
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"bPartner",e );

            return e.getLocalizedMessage();
        }

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

    public String paymentTerm( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_PaymentTerm_ID = ( Integer )value;
        int     C_Invoice_ID     = Env.getContextAsInt( ctx,WindowNo,"C_Invoice_ID" );

        if( (C_PaymentTerm_ID == null) || (C_PaymentTerm_ID.intValue() == 0) || (C_Invoice_ID == 0) ) {    // not saved yet
            return "";
        }

        //

        MPaymentTerm pt = new MPaymentTerm( ctx,C_PaymentTerm_ID.intValue(),null );

        if( pt.getID() == 0 ) {
            return "PaymentTerm not found";
        }

        boolean valid = pt.apply( C_Invoice_ID );

        mTab.setValue( "IsPayScheduleValid",valid
                ?"Y"
                :"N" );

        return "";
    }    // paymentTerm

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

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        if (isCalloutActive())
        	return "";
        
        setCalloutActive( true );
        mTab.setValue( "C_Charge_ID",null );

        // Set Attribute

        if (mField.getColumnName().equals("M_Product_ID")) {
	        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_Product_ID" ) == M_Product_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" ) != 0) ) {
	            mTab.setValue( "M_AttributeSetInstance_ID",new Integer( Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID" )));
	        } else {
	            mTab.setValue( "M_AttributeSetInstance_ID",null );
	        }
        }
        
        boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );
        int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,WindowNo,"C_BPartner_ID" );
        BigDecimal      Qty = ( BigDecimal )mTab.getValue( "QtyInvoiced" );
        
        Integer M_AttributeSetInstance_ID = (Integer)mTab.getValue("M_AttributeSetInstance_ID");
        if (M_AttributeSetInstance_ID == null)
        	M_AttributeSetInstance_ID = new Integer(0);
        
        MProductPricing pp  = new MProductPricing( M_Product_ID.intValue(),C_BPartner_ID,Qty,IsSOTrx,M_AttributeSetInstance_ID );

        //

        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );

        pp.setM_PriceList_ID( M_PriceList_ID );

        int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

        pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

        Timestamp date = Env.getContextAsDate( ctx,WindowNo,"DateInvoiced" );

        pp.setPriceDate( date );

        // Disytel: Conversion entre el precio de la tarifa y la moneda destino de la cabecera
        String invoiceID = Env.getContext(ctx, WindowNo, 0, "C_Invoice_ID");
        MInvoice invoice = new MInvoice(ctx, Integer.parseInt(invoiceID), null);
        
        int priceListCurrency = (new MPriceList(ctx, invoice.getM_PriceList_ID(), null)).getC_Currency_ID();
        int targetCurrency = invoice.getC_Currency_ID();
        
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
            mTab.setValue( "PriceList", MCurrency.currencyConvert(pp.getPriceList(), priceListCurrency, targetCurrency, invoice.getDateInvoiced(), invoice.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceLimit", MCurrency.currencyConvert(pp.getPriceLimit(), priceListCurrency, targetCurrency, invoice.getDateInvoiced(), invoice.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceActual", MCurrency.currencyConvert(pp.getPriceStd(), priceListCurrency, targetCurrency, invoice.getDateInvoiced(), invoice.getAD_Org_ID(), ctx) );
            mTab.setValue( "PriceEntered", MCurrency.currencyConvert(pp.getPriceStd(), priceListCurrency, targetCurrency, invoice.getDateInvoiced(), invoice.getAD_Org_ID(), ctx) );
            mTab.setValue( "C_Currency_ID", targetCurrency);        	
        }
        // mTab.setValue("Discount", pp.getDiscount());

        mTab.setValue( "C_UOM_ID",new Integer( pp.getC_UOM_ID()));
        Env.setContext( ctx,WindowNo,"EnforcePriceLimit",pp.isEnforcePriceLimit()
                ?"Y"
                :"N" );
        Env.setContext( ctx,WindowNo,"DiscountSchema",pp.isDiscountSchema()
                ?"Y"
                :"N" );

        // Descuento de la cabecera
		BigDecimal generalDiscountManual = DB
				.getSQLValueBD(
						null,
						"SELECT ManualGeneralDiscount FROM c_invoice WHERE c_invoice_id = ?",
						invoice.getID());
		if(generalDiscountManual.compareTo(BigDecimal.ZERO) != 0){
	        int StdPrecision = MPriceList.getStandardPrecision( ctx,M_PriceList_ID );
	        BigDecimal priceList = (BigDecimal)mTab.getValue("PriceList");
	        BigDecimal priceActual = (BigDecimal)mTab.getValue("PriceActual");
			BigDecimal priceUnit = priceList.compareTo(BigDecimal.ZERO) != 0 ? priceList
					: priceActual;
			BigDecimal lineDiscountAmtUnit = priceUnit.multiply(
					generalDiscountManual).divide(new BigDecimal(100), StdPrecision,
					BigDecimal.ROUND_HALF_UP);
			// Seteo el precio ingresado con el precio de lista - monto de
			// descuento
			BigDecimal realPrice = priceUnit.subtract(lineDiscountAmtUnit);
			BigDecimal lineDiscountAmt = lineDiscountAmtUnit
					.multiply((BigDecimal) mTab.getValue("QtyEntered"));
			mTab.setValue( "PriceEntered", realPrice);
			mTab.setValue( "PriceActual", realPrice);
			mTab.setValue( "LineDiscountAmt", lineDiscountAmt);
		}
		
        setCalloutActive( false );

        return tax( ctx,WindowNo,mTab,mField,value );
    }    // product

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

        int shipC_BPartner_Location_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_Location_ID" );

        if( shipC_BPartner_Location_ID == 0 ) {
            return amt( ctx,WindowNo,mTab,mField,value );    //
        }

        log.fine( "Ship BP_Location=" + shipC_BPartner_Location_ID );

        int billC_BPartner_Location_ID = shipC_BPartner_Location_ID;

        log.fine( "Bill BP_Location=" + billC_BPartner_Location_ID );

        // Dates

        Timestamp billDate = Env.getContextAsDate( ctx,WindowNo,"DateInvoiced" );

        log.fine( "Bill Date=" + billDate );

        Timestamp shipDate = billDate;

        log.fine( "Ship Date=" + shipDate );

        int AD_Org_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Org_ID" );

        log.fine( "Org=" + AD_Org_ID );

        int M_Warehouse_ID = Env.getContextAsInt( ctx,"M_Warehouse_ID" );

        log.fine( "Warehouse=" + M_Warehouse_ID );

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
    	//JOptionPane.showMessageDialog( null,"En CalloutInvoice, processCallout-amt con WindowNO= "+WindowNo+"\n"+"mtab= "+mTab+"\n"+"mField= "+mField+"\n"+"Value= " + value,"..Fin", JOptionPane.INFORMATION_MESSAGE );
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        // log.log(Level.WARNING,"amt - init");

        int C_UOM_To_ID    = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );
        
        // En pestañas con tabs incluidas, el valor de M_Product_ID puede ser erroneo en el contexto
        //int M_Product_ID   = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" ); 
        Integer M_Product_ID = (Integer)mTab.getValue("M_Product_ID");
        M_Product_ID = (M_Product_ID == null? 0 : M_Product_ID);
        
        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );
        int StdPrecision = MPriceList.getStandardPrecision( ctx,M_PriceList_ID );
        BigDecimal QtyEntered,QtyInvoiced,PriceEntered,PriceActual,PriceLimit,Discount,PriceList;

        // get values

        QtyEntered  = ( BigDecimal )mTab.getValue( "QtyEntered" );
        QtyInvoiced = ( BigDecimal )mTab.getValue( "QtyInvoiced" );
        log.fine( "QtyEntered=" + QtyEntered + ", Invoiced=" + QtyInvoiced + ", UOM=" + C_UOM_To_ID );

        //

        PriceEntered = ( BigDecimal )mTab.getValue( "PriceEntered" );
        PriceActual  = ( BigDecimal )mTab.getValue( "PriceActual" );

        // Discount = (BigDecimal)mTab.getValue("Discount");

        PriceLimit = ( BigDecimal )mTab.getValue( "PriceLimit" );
        PriceList  = ( BigDecimal )mTab.getValue( "PriceList" );
        log.fine( "PriceList=" + PriceList + ", Limit=" + PriceLimit + ", Precision=" + StdPrecision );
        log.fine( "PriceEntered=" + PriceEntered + ", Actual=" + PriceActual );    // + ", Discount=" + Discount);

        // Qty changed - recalc price

        Integer invoiceID = (Integer)mTab.getValue("C_Invoice_ID");
        MInvoice invoice = new MInvoice(ctx, invoiceID, null);
        
        if(( mField.getColumnName().equals( "QtyInvoiced" ) || mField.getColumnName().equals( "QtyEntered" ) || mField.getColumnName().equals( "M_Product_ID" )) &&!"N".equals( Env.getContext( ctx,WindowNo,"DiscountSchema" ))) {
            int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" );
            
            if( mField.getColumnName().equals( "QtyEntered" )) {
                QtyInvoiced = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );
            }

            if( QtyInvoiced == null ) {
                QtyInvoiced = QtyEntered;
            }

            if(!invoice.isManageDragOrderDiscounts()){
	            boolean IsSOTrx = Env.getContext( ctx,WindowNo,"IsSOTrx" ).equals( "Y" );
	            
	            Integer M_AttributeSetInstance_ID = (Integer)mTab.getValue("M_AttributeSetInstance_ID");
	            if (M_AttributeSetInstance_ID == null)
	            	M_AttributeSetInstance_ID = new Integer(0);
	            
	            MProductPricing pp = new MProductPricing( M_Product_ID,C_BPartner_ID,QtyInvoiced,IsSOTrx,M_AttributeSetInstance_ID );
	
	            pp.setM_PriceList_ID( M_PriceList_ID );
	
	            int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );
	
	            pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );
	
	            Timestamp date = ( Timestamp )mTab.getValue( "DateInvoiced" );
	
	            pp.setPriceDate( date );
	
	            //
	
	            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,pp.getPriceStd());
	
	            if( PriceEntered == null ) {
	                PriceEntered = pp.getPriceStd();
	            }
	
	            //
	
	            log.fine( "amt - QtyChanged -> PriceActual=" + pp.getPriceStd() + ", PriceEntered=" + PriceEntered + ", Discount=" + pp.getDiscount());
	            mTab.setValue( "PriceActual",pp.getPriceStd());
	
	            // mTab.setValue("Discount", pp.getDiscount());
	
	            mTab.setValue( "PriceEntered",PriceEntered );
	            Env.setContext( ctx,WindowNo,"DiscountSchema",pp.isDiscountSchema()
	                    ?"Y"
	                    :"N" );
            }
            else{
            	mTab.setValue( "PriceActual",PriceEntered);
            }
        } else if( mField.getColumnName().equals( "PriceActual" )) {
            PriceActual  = ( BigDecimal )value;
            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceActual );

            if( PriceEntered == null ) {
                PriceEntered = PriceActual;
            }

            log.fine( "amt - PriceActual=" + PriceActual + " -> PriceEntered=" + PriceEntered );
            mTab.setValue( "PriceEntered",PriceEntered );
        } else if( mField.getColumnName().equals( "PriceEntered" )) {
            PriceEntered = ( BigDecimal )value;
            PriceActual  = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,PriceEntered );

            if( PriceActual == null ) {
                PriceActual = PriceEntered;
            }

            //

            log.fine( "amt - PriceEntered=" + PriceEntered + " -> PriceActual=" + PriceActual );
            mTab.setValue( "PriceActual",PriceActual );
        }

        // Check PriceLimit

        if( !( mField.getColumnName().equals( "QtyInvoiced" ) || mField.getColumnName().equals( "QtyEntered" ))) {
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

                log.fine( "amt =(under) PriceEntered=" + PriceEntered + ", Actual" + PriceLimit );
                mTab.setValue( "PriceActual",PriceLimit );
                mTab.setValue( "PriceEntered",PriceEntered );
                mTab.fireDataStatusEEvent( "UnderLimitPrice","" );

                // Repeat Discount calc

                if( PriceList.intValue() != 0 ) {
                    Discount = new BigDecimal(( PriceList.doubleValue() - PriceActual.doubleValue()) / PriceList.doubleValue() * 100.0 );

                    if( Discount.scale() > 2 ) {
                        Discount = Discount.setScale( 2,BigDecimal.ROUND_HALF_UP );
                    }

                    // mTab.setValue ("Discount", Discount);

                }
            }
        }

        // Line Net Amt

        BigDecimal LineNetAmt = QtyInvoiced.multiply( PriceActual );

        if( LineNetAmt.scale() > StdPrecision ) {
            LineNetAmt = LineNetAmt.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        log.info( "amt = LineNetAmt=" + LineNetAmt );
        mTab.setValue( "LineNetAmt",LineNetAmt );

        // Calculate Tax Amount for PO

        // Modified by - Matías Cap - Disytel
		// Luego de las modificaciones realizadas para permitir modificar el
		// monto del impuesto, para facturas de cliente también se debe
		// actualizar el taxamt de las líneas. Igual no tiene sentido que no se actualice. 
        BigDecimal TaxAmt = Env.ZERO;

        if( mField.getColumnName().equals( "TaxAmt" )) {
            TaxAmt = ( BigDecimal )mTab.getValue( "TaxAmt" );
        } else {
            Integer taxID = ( Integer )mTab.getValue( "C_Tax_ID" );

            if( taxID != null ) {
                int  C_Tax_ID = taxID.intValue();
                MTax tax      = new MTax( ctx,C_Tax_ID,null );

                TaxAmt = tax.calculateTax( LineNetAmt,isTaxIncluded( WindowNo ),StdPrecision );
                mTab.setValue( "TaxAmt",TaxAmt );
            }
        }

        // Add it up

        mTab.setValue( "LineTotalAmt",LineNetAmt.add( TaxAmt ));

        setCalloutActive( false );

        return "";
    }    // amt

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     *
     * @return
     */

    private boolean isTaxIncluded( int WindowNo ) {
        String ss = Env.getContext( Env.getCtx(),WindowNo,"IsTaxIncluded" );

        // Not Set Yet

        if( ss.length() == 0 ) {
            int M_PriceList_ID = Env.getContextAsInt( Env.getCtx(),WindowNo,"M_PriceList_ID" );

            if( M_PriceList_ID == 0 ) {
                return false;
            }

            ss = DB.getSQLValueString( null,"SELECT IsTaxIncluded FROM M_PriceList WHERE M_PriceList_ID=?",M_PriceList_ID );

            if( ss == null ) {
                ss = "N";
            }

            Env.setContext( Env.getCtx(),WindowNo,"IsTaxIncluded",ss );
        }

        return "Y".equals( ss );
    }    // isTaxIncluded

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
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        int M_Product_ID = Env.getContextAsInt( ctx,WindowNo,"M_Product_ID" );

        // log.log(Level.WARNING,"qty - init - M_Product_ID=" + M_Product_ID);

        BigDecimal QtyInvoiced,QtyEntered,PriceActual,PriceEntered;

        // No Product

        if( M_Product_ID == 0 ) {
            QtyEntered = ( BigDecimal )mTab.getValue( "QtyEntered" );
            mTab.setValue( "QtyInvoiced",QtyEntered );
        }

        // UOM Changed - convert from Entered -> Product

        else if( mField.getColumnName().equals( "C_UOM_ID" )) {
            int C_UOM_To_ID = (( Integer )value ).intValue();

            QtyEntered  = ( BigDecimal )mTab.getValue( "QtyEntered" );
            QtyInvoiced = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( QtyInvoiced == null ) {
                QtyInvoiced = QtyEntered;
            }

            boolean conversion = QtyEntered.compareTo( QtyInvoiced ) != 0;

            PriceActual  = ( BigDecimal )mTab.getValue( "PriceActual" );
            PriceEntered = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,PriceActual );

            if( PriceEntered == null ) {
                PriceEntered = PriceActual;
            }

            log.fine( "qty - UOM=" + C_UOM_To_ID + ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual + " -> " + conversion + " QtyInvoiced/PriceEntered=" + QtyInvoiced + "/" + PriceEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyInvoiced",QtyInvoiced );
            mTab.setValue( "PriceEntered",PriceEntered );
        }

        // QtyEntered changed - calculate QtyInvoiced

        else if( mField.getColumnName().equals( "QtyEntered" )) {
            int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            QtyEntered  = ( BigDecimal )value;
            QtyInvoiced = MUOMConversion.convertProductFrom( ctx,M_Product_ID,C_UOM_To_ID,QtyEntered );

            if( QtyInvoiced == null ) {
                QtyInvoiced = QtyEntered;
            }

            boolean conversion = QtyEntered.compareTo( QtyInvoiced ) != 0;

            log.fine( "qty - UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyInvoiced=" + QtyInvoiced );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyInvoiced",QtyInvoiced );
        }

        // QtyInvoiced changed - calculate QtyEntered

        else if( mField.getColumnName().equals( "QtyInvoiced" )) {
            int C_UOM_To_ID = Env.getContextAsInt( ctx,WindowNo,"C_UOM_ID" );

            QtyInvoiced = ( BigDecimal )value;
            QtyEntered  = MUOMConversion.convertProductTo( ctx,M_Product_ID,C_UOM_To_ID,QtyInvoiced );

            if( QtyEntered == null ) {
                QtyEntered = QtyInvoiced;
            }

            boolean conversion = QtyInvoiced.compareTo( QtyEntered ) != 0;

            log.fine( "qty - UOM=" + C_UOM_To_ID + ", QtyInvoiced=" + QtyInvoiced + " -> " + conversion + " QtyEntered=" + QtyEntered );
            Env.setContext( ctx,WindowNo,"UOMConversion",conversion
                    ?"Y"
                    :"N" );
            mTab.setValue( "QtyEntered",QtyEntered );
        }
        
        setCalloutActive( false );

        return "";
    }    // qty
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */
    
    private BigDecimal totalTax(){ 
        
    	BigDecimal amount = Env.ZERO;    	
    	String sql = "SELECT taxamt FROM C_InvoiceTax " + "WHERE C_Invoice_ID = " + Env.getContext(Env.getCtx(),"C_Invoice_ID");
    	    	
    	try {
        	PreparedStatement pstmt = DB.prepareStatement( sql );
        	ResultSet rs = pstmt.executeQuery();
            
            while( rs.next()) {
    	          amount = amount.add( rs.getBigDecimal(0));
       	    }
            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
        	log.log( Level.SEVERE,"totalTax",e );
        }
   	 return amount;
        }  // totalTax
}    // CalloutInvoice



/*
 *  @(#)CalloutInvoice.java   02.07.07
 * 
 *  Fin del fichero CalloutInvoice.java
 *  
 *  Versión 2.2
 *
 */
