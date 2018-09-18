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
import org.openXpertya.util.Util;


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class CalloutPayment extends CalloutEngine {

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
	public static Integer c_invoiceschedule;
	public void setfkid(Integer prueba){
		c_invoiceschedule=prueba;
	}
    public String invoice( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	log.fine("En CalloutPayment.invoice");
        Integer C_Invoice_ID = ( Integer )value;

        log.fine("Con c_Invoice_ID = " + C_Invoice_ID);

        if( isCalloutActive()    // assuming it is resetting value
                || (C_Invoice_ID == null) || (C_Invoice_ID.intValue() == 0) ) {
            return "";
        }
        
        setCalloutActive( true );
        mTab.setValue( "C_Order_ID",null );//antes C_Order_ID
        mTab.setValue( "C_Charge_ID",null );
        mTab.setValue( "IsPrepayment",Boolean.FALSE );

        //

        mTab.setValue( "WriteOffAmt",Env.ZERO );
        mTab.setValue( "IsOverUnderPayment",Boolean.FALSE );
        mTab.setValue( "OverUnderAmt",Env.ZERO );

        int C_InvoicePaySchedule_ID = 0;
        int prueba=0;
        prueba=Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" );
        
        
        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_Invoice_ID" ) == C_Invoice_ID.intValue()) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" ) != 0) ) {
            C_InvoicePaySchedule_ID = Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" );
        }
        // Payment Date

        log.fine("En calloutPayment.invoice: C_InvoicePaySchedule_ID = "+ C_InvoicePaySchedule_ID);


        Timestamp ts = ( Timestamp )mTab.getValue( "DateTrx" );
       
        if( ts == null ) {
            ts = new Timestamp( System.currentTimeMillis());
        }

        //
        
        String sql = "SELECT C_BPartner_ID,C_Currency_ID,"               // 1..2
                     + " invoiceOpen(C_Invoice_ID,?),"                  // 3               #1
                     + " invoiceDiscount(C_Invoice_ID,?,?),"
                     +" IsSOTrx "    // 4..5    #2/3
                     + "FROM C_Invoice_v WHERE C_Invoice_ID=?";            // #4
        
        
        
        try {
        	log.fine("Antes de prepareStatement con ts= "+ ts);
        	log.fine("Antes de prepareStatement con C_Invoic_id.initvalue()= "+ C_Invoice_ID.intValue());
        	log.fine("Antes de prepareStatement con C_Invoic_id.initvalue()= "+ C_Invoice_ID);
        	
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_InvoicePaySchedule_ID );
            pstmt.setTimestamp( 2,ts );
            pstmt.setInt( 3,C_InvoicePaySchedule_ID );
            pstmt.setInt( 4,C_Invoice_ID.intValue());
          
            
            ResultSet rs = pstmt.executeQuery();
            log.fine("Resultado = +++++++" + pstmt);

            if( rs.next()) {
                mTab.setValue( "C_BPartner_ID",new Integer( rs.getInt( 1 )));

                int C_Currency_ID = rs.getInt( 2 );    // Set Invoice Currency

                mTab.setValue( "C_Currency_ID",new Integer( C_Currency_ID ));

                //

                BigDecimal InvoiceOpen = rs.getBigDecimal( 3 );    // Set Invoice OPen Amount
                
                if( InvoiceOpen == null ) {
                    InvoiceOpen = Env.ZERO;
                }
                
                BigDecimal DiscountAmt = rs.getBigDecimal( 4 );    // Set Discount Amt

                if( DiscountAmt == null ) {
                    DiscountAmt = Env.ZERO;
                }

                mTab.setValue( "PayAmt",InvoiceOpen.subtract( DiscountAmt ));
                mTab.setValue( "DiscountAmt",DiscountAmt );
                mTab.setValue( "WriteOffAmt",Env.ZERO );

                // reset as dependent fields get reset
			
                Env.setContext( ctx,WindowNo,"C_Invoice_ID",C_Invoice_ID.toString());
                mTab.setValue( "C_Invoice_ID",C_Invoice_ID );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"invoice",e );
            setCalloutActive( false );

            return e.getLocalizedMessage();
        }

        setCalloutActive( false );

        return docType( ctx,WindowNo,mTab,mField,value );
    }    // invoice

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

    public String order( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Order_ID = ( Integer )value;

        if( isCalloutActive()    // assuming it is resetting value
                || (C_Order_ID == null) || (C_Order_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );
        mTab.setValue( "C_Invoice_ID",null ); //antes C_Invoice_ID
        mTab.setValue( "C_Charge_ID",null );
        mTab.setValue( "IsPrepayment",Boolean.TRUE );

        //

        mTab.setValue( "DiscountAmt",Env.ZERO );
        mTab.setValue( "WriteOffAmt",Env.ZERO );
        mTab.setValue( "IsOverUnderPayment",Boolean.FALSE );
        mTab.setValue( "OverUnderAmt",Env.ZERO );

        // Payment Date

        Timestamp ts = ( Timestamp )mTab.getValue( "DateTrx" );

        if( ts == null ) {
            ts = new Timestamp( System.currentTimeMillis());
        }

        //

        String sql = "SELECT C_BPartner_ID,C_Currency_ID, GrandTotal " + "FROM C_Order WHERE C_Order_ID=?";    // #1

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,C_Order_ID.intValue());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                mTab.setValue( "C_BPartner_ID",new Integer( rs.getInt( 1 )));

                int C_Currency_ID = rs.getInt( 2 );    // Set Order Currency

                mTab.setValue( "C_Currency_ID",new Integer( C_Currency_ID ));

                //

                BigDecimal GrandTotal = rs.getBigDecimal( 3 );    // Set Pay Amount

                if( GrandTotal == null ) {
                    GrandTotal = Env.ZERO;
                }

                mTab.setValue( "PayAmt",GrandTotal );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"order",e );
            setCalloutActive( false );

            return e.getLocalizedMessage();
        }

        setCalloutActive( false );

        return docType( ctx,WindowNo,mTab,mField,value );
    }    // order

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

    public String project( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_Project_ID = ( Integer )value;

        if( isCalloutActive()    // assuming it is resetting value
                || (C_Project_ID == null) || (C_Project_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );
        mTab.setValue( "C_Charge_ID",null );
        setCalloutActive( false );

        return "";
    }    // project

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

        if( isCalloutActive()    // assuming it is resetting value
                || (C_Charge_ID == null) || (C_Charge_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );
        mTab.setValue( "C_Invoice_ID",null );
        mTab.setValue( "C_Order_ID",null );
        mTab.setValue( "C_Project_ID",null );
        mTab.setValue( "IsPrepayment",Boolean.FALSE );

        //

        mTab.setValue( "DiscountAmt",Env.ZERO );
        mTab.setValue( "WriteOffAmt",Env.ZERO );
        mTab.setValue( "IsOverUnderPayment",Boolean.FALSE );
        mTab.setValue( "OverUnderAmt",Env.ZERO );
        setCalloutActive( false );

        return "";
    }    // charge
    
    
    public String bankaccount( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer C_BankAccount_ID = ( Integer )value;

        if( isCalloutActive()    // assuming it is resetting value
                || (C_BankAccount_ID == null) || (C_BankAccount_ID.intValue() == 0) ) {
            return "";
        }

        setCalloutActive( true );
        
        MBankAccount bankAccount = MBankAccount.get( ctx,C_BankAccount_ID );
        mTab.setValue( "C_Currency_ID", bankAccount.getC_Currency_ID());
       
        setCalloutActive( false );

        return "";
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

    public String docType( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        int C_Invoice_ID = Env.getContextAsInt( ctx,WindowNo,"C_Invoice_ID" );
        int C_Order_ID   = Env.getContextAsInt( ctx,WindowNo,"C_Order_ID" );
        int C_DocType_ID = Env.getContextAsInt( ctx,WindowNo,"C_DocType_ID" );

        log.fine( "Payment_DocType - C_Invoice_ID=" + C_Invoice_ID + ", C_DocType_ID=" + C_DocType_ID );

        MDocType dt = null;

        if( C_DocType_ID != 0 ) {
            dt = MDocType.get( ctx,C_DocType_ID );
            Env.setContext( ctx,WindowNo,"IsSOTrx",dt.isSOTrx()
                    ?"Y"
                    :"N" );
        }

        // Invoice

        if( C_Invoice_ID != 0 ) {
            MInvoice inv = new MInvoice( ctx,C_Invoice_ID,null );

            if( dt != null ) {
                if( inv.isSOTrx() != dt.isSOTrx()) {
                    return "PaymentDocTypeInvoiceInconsistent";
                }
            }
        }

        // Order Waiting Payment (can only be SO)

        if( (C_Order_ID != 0) &&!dt.isSOTrx()) {
            return "PaymentDocTypeInvoiceInconsistent";
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
     * @param oldValue
     *
     * @return
     */

    public String amounts( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value,Object oldValue ) {
        if( isCalloutActive()) {    // assuming it is resetting value
            return "";
        }

        int C_Invoice_ID = Env.getContextAsInt( ctx,WindowNo,"C_Invoice_ID" );

        // New Payment

        if( (Env.getContextAsInt( ctx,WindowNo,"C_Payment_ID" ) == 0) && (Env.getContextAsInt( ctx,WindowNo,"C_BPartner_ID" ) == 0) && (C_Invoice_ID == 0) ) {
            return "";
        }

        setCalloutActive( true );

        // Changed Column

        String colName = mField.getColumnName();

        if( colName.equals( "IsOverUnderPayment" )    // Set Over/Under Amt to Zero
                ||!"Y".equals( Env.getContext( ctx,WindowNo,"IsOverUnderPayment" ))) {
            mTab.setValue( "OverUnderAmt",Env.ZERO );
        }

        int C_InvoicePaySchedule_ID = 0;

        if( (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_Invoice_ID" ) == C_Invoice_ID) && (Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" ) != 0) ) {
            C_InvoicePaySchedule_ID = Env.getContextAsInt( ctx,Env.WINDOW_INFO,Env.TAB_INFO,"C_InvoicePaySchedule_ID" );
        }

        // Get Open Amount & Invoice Currency

        BigDecimal InvoiceOpenAmt        = Env.ZERO;
        int        C_Currency_Invoice_ID = 0;

        if( C_Invoice_ID != 0 ) {
            Timestamp ts = ( Timestamp )mTab.getValue( "DateTrx" );

            if( ts == null ) {
                ts = new Timestamp( System.currentTimeMillis());
            }

            String sql = "SELECT C_BPartner_ID,C_Currency_ID,"               // 1..2
                         + " invoiceOpen(C_Invoice_ID,?),"                   // 3               #1
                         + " invoiceDiscount(C_Invoice_ID,?,?), IsSOTrx "    // 4..5    #2/3
                         + "FROM C_Invoice WHERE C_Invoice_ID=?";            // #4

            try {
                PreparedStatement pstmt = DB.prepareStatement( sql );

                pstmt.setInt( 1,C_InvoicePaySchedule_ID );
                pstmt.setTimestamp( 2,ts );
                pstmt.setInt( 3,C_InvoicePaySchedule_ID );
                pstmt.setInt( 4,C_Invoice_ID );

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    C_Currency_Invoice_ID = rs.getInt( 2 );
                    InvoiceOpenAmt        = rs.getBigDecimal( 3 );           // Set Invoice Open Amount

                    if( InvoiceOpenAmt == null ) {
                        InvoiceOpenAmt = Env.ZERO;
                    }
                }

                rs.close();
                pstmt.close();
            } catch( SQLException e ) {
                log.log( Level.SEVERE,"amounts",e );
                setCalloutActive( false );

                return e.getLocalizedMessage();
            }
        }    // get Invoice Info

        log.fine( "amounts Open=" + InvoiceOpenAmt + ", C_Invoice_ID=" + C_Invoice_ID + ", C_Currency_ID=" + C_Currency_Invoice_ID );

        // Get Info from Tab

        BigDecimal PayAmt       = ( BigDecimal )mTab.getValue( "PayAmt" );
        BigDecimal DiscountAmt  = ( BigDecimal )mTab.getValue( "DiscountAmt" );
        BigDecimal WriteOffAmt  = ( BigDecimal )mTab.getValue( "WriteOffAmt" );
        BigDecimal OverUnderAmt = ( BigDecimal )mTab.getValue( "OverUnderAmt" );

        // Coversion de nulls a ZERO para evitar errores posteriores que se generan cuando en el
        // VNumber de alguno de estos campos se borra completamente el valor.
        PayAmt       = (PayAmt == null ? Env.ZERO : PayAmt);
        DiscountAmt  = (DiscountAmt == null ? Env.ZERO : DiscountAmt);
        WriteOffAmt  = (WriteOffAmt == null ? Env.ZERO : WriteOffAmt);
        OverUnderAmt = (OverUnderAmt == null ? Env.ZERO : OverUnderAmt); 
        
        log.fine( "amounts Pay=" + PayAmt + ", Discount=" + DiscountAmt + ", WriteOff=" + WriteOffAmt + ", OverUnderAmt=" + OverUnderAmt );

        // Get Currency Info

        int C_Currency_ID = (( Integer )mTab.getValue( "C_Currency_ID" )).intValue();
        MCurrency currency            = MCurrency.get( ctx,C_Currency_ID );
        Timestamp ConvDate            = ( Timestamp )mTab.getValue( "DateTrx" );
        int       C_ConversionType_ID = 0;
        Integer   ii                  = ( Integer )mTab.getValue( "C_ConversionType_ID" );

        if( ii != null ) {
            C_ConversionType_ID = ii.intValue();
        }

        int AD_Client_ID = Env.getContextAsInt( ctx,WindowNo,"AD_Client_ID" );
        int AD_Org_ID    = Env.getContextAsInt( ctx,WindowNo,"AD_Org_ID" );

        // Get Currency Rate

        BigDecimal CurrencyRate = Env.ONE;

        if( ( (C_Currency_ID > 0) && (C_Currency_Invoice_ID > 0) && (C_Currency_ID != C_Currency_Invoice_ID) ) || colName.equals( "C_Currency_ID" ) || colName.equals( "C_ConversionType_ID" )) {
            log.fine( "amounts - InvCurrency=" + C_Currency_Invoice_ID + ", PayCurrency=" + C_Currency_ID + ", Date=" + ConvDate + ", Type=" + C_ConversionType_ID );
            CurrencyRate = MConversionRate.getRate( C_Currency_Invoice_ID,C_Currency_ID,ConvDate,C_ConversionType_ID,AD_Client_ID,AD_Org_ID );

            if( (CurrencyRate == null) || (CurrencyRate.compareTo( Env.ZERO ) == 0) ) {

                // mTab.setValue("C_Currency_ID", new Integer(C_Currency_Invoice_ID));     //      does not work

                setCalloutActive( false );

                if( C_Currency_Invoice_ID == 0 ) {
                    return "";    // no error message when no invoice is selected
                }

                return "NoCurrencyConversion";
            }

            //

            InvoiceOpenAmt = InvoiceOpenAmt.multiply( CurrencyRate ).setScale( currency.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
            log.fine( "amounts - Rate=" + CurrencyRate + ", InvoiceOpenAmt=" + InvoiceOpenAmt );
        }

        // Currency Changed - convert all

        if( colName.equals( "C_Currency_ID" ) || colName.equals( "C_ConversionType_ID" )) {
            PayAmt = PayAmt.multiply( CurrencyRate ).setScale( currency.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
            mTab.setValue( "PayAmt",PayAmt );
            DiscountAmt = DiscountAmt.multiply( CurrencyRate ).setScale( currency.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
            mTab.setValue( "DiscountAmt",DiscountAmt );
            WriteOffAmt = WriteOffAmt.multiply( CurrencyRate ).setScale( currency.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
            mTab.setValue( "WriteOffAmt",WriteOffAmt );
            OverUnderAmt = OverUnderAmt.multiply( CurrencyRate ).setScale( currency.getStdPrecision(),BigDecimal.ROUND_HALF_UP );
            mTab.setValue( "OverUnderAmt",OverUnderAmt );
        }

        // No Invoice - Set Discount, Witeoff, Under/Over to 0

        else if( C_Invoice_ID == 0 ) {
            if( Env.ZERO.compareTo( DiscountAmt ) != 0 ) {
                mTab.setValue( "DiscountAmt",Env.ZERO );
            }

            if( Env.ZERO.compareTo( WriteOffAmt ) != 0 ) {
                mTab.setValue( "WriteOffAmt",Env.ZERO );
            }

            if( Env.ZERO.compareTo( OverUnderAmt ) != 0 ) {
                mTab.setValue( "OverUnderAmt",Env.ZERO );
            }
        }

        // PayAmt - calculate write off

        else if( colName.equals( "PayAmt" )) {
            WriteOffAmt = InvoiceOpenAmt.subtract( PayAmt ).subtract( DiscountAmt ).subtract( OverUnderAmt );
            mTab.setValue( "WriteOffAmt",WriteOffAmt );
        } else    // calculate PayAmt
        {
            PayAmt = InvoiceOpenAmt.subtract( DiscountAmt ).subtract( WriteOffAmt ).subtract( OverUnderAmt );
            mTab.setValue( "PayAmt",PayAmt );
        }

        setCalloutActive( false );

        return "";
    }    // amounts
    
    public String entidadfinancieraplan( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value,Object oldValue ) {
    	// Setear la cuenta bancaria de cupones 
    	Integer entidadFinancieraPlanID = value == null?0:(Integer)value;
    	if(entidadFinancieraPlanID > 0){
			MEntidadFinancieraPlan efPlan = new MEntidadFinancieraPlan(ctx, entidadFinancieraPlanID,
					null);
			MEntidadFinanciera ef = new MEntidadFinanciera(ctx, efPlan.getM_EntidadFinanciera_ID(),
					null);
			mTab.setValue("C_BankAccount_ID",ef.getC_BankAccount_ID());
			mTab.setValue("CreditCardType",ef.getCreditCardType());
    	}
    	
    	return "";
    }
}    // CalloutPayment



/*
 *  @(#)CalloutPayment.java   02.07.07
 * 
 *  Fin del fichero CalloutPayment.java
 *  
 *  Versión 2.2
 *
 */
