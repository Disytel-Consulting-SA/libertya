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
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.DocumentEngine;
import org.openXpertya.process.ProcessCall;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.AuxiliarDTO;
import org.openXpertya.util.CLogMgt;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;
import org.openXpertya.util.ValueNamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MPayment extends X_C_Payment implements DocAction,ProcessCall {

	
	// Variables de instancia
	
	/**
	 * Booleano que determina si al completar esta factura se debe actualizar el
	 * saldo de cuenta corriente del cliente
	 */
	private boolean updateBPBalance = true;
	
	/**
	 * Booleano que determina si se debe confimar el trabajo adicional de cuenta
	 * corriente al procesar el/los documento/s
	 */
	private boolean confirmAditionalWorks = true;
	
	/**
	 * Resultado de la llamada de cuenta corriente que realiza trabajo adicional
	 * al procesar un documento. Al anular un payment y crearse un payment
	 * reverso, se debe guardar dentro de esta map también.
	 */	
	private Map<PO, Object> aditionalWorkResult;
	
	/**
	 * Caja diaria a asignar al contra-documento que se genera al anular este
	 * documento
	 */
	private Integer voidPOSJournalID = 0;

	/**
	 * Control que se agrega para obligatoriedad de apertura de la caja diaria
	 * asignada al contra-documento. Es decir que si este control se debe
	 * realizar y existe un valor en la caja a asignar para el contra-documento,
	 * entonces esa caja diaria debe estar abierta, sino error
	 */
	private boolean voidPOSJournalMustBeOpen = false;
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     * @param trxName
     *
     * @return
     */

    public static MPayment[] getOfBPartner( Properties ctx,int C_BPartner_ID,String trxName ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM C_Payment WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MPayment( ctx,rs,trxName ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getOfBPartner",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        MPayment[] retValue = new MPayment[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfBPartner

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Payment_ID
     * @param trxName
     */

    public MPayment( Properties ctx,int C_Payment_ID,String trxName ) {
        super( ctx,C_Payment_ID,trxName );

        // New

        if( C_Payment_ID == 0 ) {
            setDocAction( DOCACTION_Complete );
            setDocStatus( DOCSTATUS_Drafted );
            setTrxType( TRXTYPE_Sales );

            //

            setR_AvsAddr( R_AVSZIP_Unavailable );
            setR_AvsZip( R_AVSZIP_Unavailable );

            //

            setIsReceipt( true );
            setIsApproved( false );
            setIsReconciled( false );
            setIsAllocated( false );
            setIsOnline( false );
            setIsSelfService( false );
            setIsDelayedCapture( false );
            setIsPrepayment( false );
            setProcessed( false );
            setProcessing( false );
            setPosted( false );

            //

            setPayAmt( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setTaxAmt( Env.ZERO );
            setWriteOffAmt( Env.ZERO );
            setIsOverUnderPayment( false );
            setOverUnderAmt( Env.ZERO );

            //

            setDateTrx( new Timestamp( System.currentTimeMillis()));
            setDateAcct( getDateTrx());
            setTenderType( TENDERTYPE_Check );
        }
    }    // MPayment

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPayment( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPayment

    // Temporary

    /** Descripción de Campos */

    private MPaymentProcessor[] m_mPaymentProcessors = null;

    /** Descripción de Campos */

    private MPaymentProcessor m_mPaymentProcessor = null;

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPayment.class );

    /** Descripción de Campos */

    private String m_errorMessage = null;

    /** Descripción de Campos */

    public static String REVERSE_INDICATOR = "^";

    /**
     * Descripción de Método
     *
     */

    
    public boolean isReceipt(){
    	return this.getIsReceipt().equalsIgnoreCase("Y");
    }
    
    /**
     * Set Receipt
     * @param IsReceipt
     */
    public void setIsReceipt (boolean IsReceipt)
    {
    setIsReceipt((IsReceipt)?"Y":"N");
    }
    
    public void resetNew() {
        setC_Payment_ID( 0 );    // forces new Record
        set_ValueNoCheck( "DocumentNo",null );
        setDocAction( DOCACTION_Prepare );
        setDocStatus( DOCSTATUS_Drafted );
        setProcessed( false );
        setPosted( false );
        setIsReconciled( false );
        setIsAllocated( false );
        setIsOnline( false );
        setIsDelayedCapture( false );

        // setC_BPartner_ID(0);

        setC_Invoice_ID( 0 );
        setC_Order_ID( 0 );
        setC_Charge_ID( 0 );
        setC_Project_ID( 0 );
        setIsPrepayment( false );
    }    // resetNew

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCashTrx() {
        return "X".equals( getTenderType());
    }    // isCashTrx

    /**
     * Descripción de Método
     *
     *
     * @param TrxType
     * @param creditCardType
     * @param creditCardNumber
     * @param creditCardVV
     * @param creditCardExpMM
     * @param creditCardExpYY
     *
     * @return
     */

    public boolean setCreditCard( String TrxType,String creditCardType,String creditCardNumber,String creditCardVV,int creditCardExpMM,int creditCardExpYY ) {
        setTenderType( TENDERTYPE_CreditCard );
        setTrxType( TrxType );

        //

        setCreditCardType( creditCardType );
        setCreditCardNumber( creditCardNumber );
        setCreditCardVV( creditCardVV );
        setCreditCardExpMM( creditCardExpMM );
        setCreditCardExpYY( creditCardExpYY );

        //

        int check = MPaymentValidate.validateCreditCardNumber( creditCardNumber,creditCardType ).length() + MPaymentValidate.validateCreditCardExp( creditCardExpMM,creditCardExpYY ).length();

        if( creditCardVV.length() > 0 ) {
            check += MPaymentValidate.validateCreditCardVV( creditCardVV,creditCardType ).length();
        }

        return check == 0;
    }    // setCreditCard

    /**
     * Descripción de Método
     *
     *
     * @param TrxType
     * @param creditCardType
     * @param creditCardNumber
     * @param creditCardVV
     * @param creditCardExp
     *
     * @return
     */

    public boolean setCreditCard( String TrxType,String creditCardType,String creditCardNumber,String creditCardVV,String creditCardExp ) {
        return setCreditCard( TrxType,creditCardType,creditCardNumber,creditCardVV,MPaymentValidate.getCreditCardExpMM( creditCardExp ),MPaymentValidate.getCreditCardExpYY( creditCardExp ));
    }    // setCreditCard

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     * @param isReceipt
     *
     * @return
     */

    public boolean setBankACH( int C_BankAccount_ID,boolean isReceipt ) {
        setBankAccountDetails( C_BankAccount_ID );
        setIsReceipt( isReceipt );

        //

        int check = MPaymentValidate.validateRoutingNo( getRoutingNo()).length() + MPaymentValidate.validateAccountNo( getAccountNo()).length();

        return check == 0;
    }    // setBankACH

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     * @param isReceipt
     * @param tenderType
     * @param routingNo
     * @param accountNo
     *
     * @return
     */

    public boolean setBankACH( int C_BankAccount_ID,boolean isReceipt,String tenderType,String routingNo,String accountNo ) {
        setTenderType( tenderType );
        setIsReceipt( isReceipt );

        //

        if( (C_BankAccount_ID > 0) && ( (routingNo == null) || (routingNo.length() == 0) || (accountNo == null) || (accountNo.length() == 0) ) ) {
            setBankAccountDetails( C_BankAccount_ID );
        } else {
            setC_BankAccount_ID( C_BankAccount_ID );
            setRoutingNo( routingNo );
            setAccountNo( accountNo );
        }

        setCheckNo( "" );

        //

        int check = MPaymentValidate.validateRoutingNo( routingNo ).length() + MPaymentValidate.validateAccountNo( accountNo ).length();

        return check == 0;
    }    // setBankACH

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     * @param isReceipt
     * @param checkNo
     *
     * @return
     */

    public boolean setBankCheck( int C_BankAccount_ID,boolean isReceipt,String checkNo ) {
        return setBankCheck( C_BankAccount_ID,isReceipt,null,null,checkNo );
    }    // setBankCheck

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     * @param isReceipt
     * @param routingNo
     * @param accountNo
     * @param checkNo
     *
     * @return
     */

    public boolean setBankCheck( int C_BankAccount_ID,boolean isReceipt,String routingNo,String accountNo,String checkNo ) {
        setTenderType( TENDERTYPE_Check );
        setIsReceipt( isReceipt );

        //

        if( (C_BankAccount_ID > 0) && ( (routingNo == null) || (routingNo.length() == 0) || (accountNo == null) || (accountNo.length() == 0) ) ) {
            setBankAccountDetails( C_BankAccount_ID );
        } else {
            setC_BankAccount_ID( C_BankAccount_ID );
            setRoutingNo( routingNo );
            setAccountNo( accountNo );
        }

        setCheckNo( checkNo );

        //

        int check = MPaymentValidate.validateRoutingNo( routingNo ).length() + MPaymentValidate.validateAccountNo( accountNo ).length() + MPaymentValidate.validateCheckNo( checkNo ).length();

        return check == 0;    // no error message
    }                         // setBankCheck

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     */

    public void setBankAccountDetails( int C_BankAccount_ID ) {
        if( C_BankAccount_ID == 0 ) {
            return;
        }

        setC_BankAccount_ID( C_BankAccount_ID );

        //

        String sql = "SELECT b.RoutingNo, ba.AccountNo " + "FROM C_BankAccount ba" + " INNER JOIN C_Bank b ON (ba.C_Bank_ID=b.C_Bank_ID) " + "WHERE C_BankAccount_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,C_BankAccount_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                setRoutingNo( rs.getString( 1 ));
                setAccountNo( rs.getString( 2 ));
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"setsetBankAccountDetails",e );
        }
    }    // setBankAccountDetails

    /**
     * Descripción de Método
     *
     *
     * @param name
     * @param street
     * @param city
     * @param state
     * @param zip
     * @param country
     */

    public void setAccountAddress( String name,String street,String city,String state,String zip,String country ) {
        setA_Name( name );
        setA_Street( street );
        setA_City( city );
        setA_State( state );
        setA_Zip( zip );
        setA_Country( country );
    }    // setAccountAddress

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean processOnline() {
        log.info( "Amt=" + getPayAmt());

        //

        setIsOnline( true );
        setErrorMessage( null );

        // prevent charging twice

        if( isApproved()) {
            log.info( "Already processed - " + getR_Result() + " - " + getR_RespMsg());
            setErrorMessage( "Payment already Processed" );

            return true;
        }

        if( m_mPaymentProcessor == null ) {
            setPaymentProcessor();
        }

        if( m_mPaymentProcessor == null ) {
            log.log( Level.SEVERE,"No Payment Processor Model" );
            setErrorMessage( "No Payment Processor Model" );

            return false;
        }

        boolean approved = false;

        if( DB.isRemoteObjects()) {
            Server server = CConnection.get().getServer();

            try {
                if( server != null ) {        // See ServerBean
                    String trxName = null;    // unconditionally save

                    save( trxName );          // server reads from disk
                    approved = server.paymentOnline( getCtx(),getC_Payment_ID(),m_mPaymentProcessor.getC_PaymentProcessor_ID(),trxName );

                    if( CLogMgt.isLevelFinest()) {
                        s_log.fine( "processOnline - server => " + approved );
                    }

                    load( trxName );    // server saves to disk
                    setIsApproved( approved );

                    return approved;
                }

                log.log( Level.SEVERE,"processOnline - AppsServer not found" );
            } catch( RemoteException ex ) {
                log.log( Level.SEVERE,"processOnline - AppsServer error",ex );
            }
        }

        // Try locally

        try {
            PaymentProcessor pp = PaymentProcessor.create( m_mPaymentProcessor,this );

            if( pp == null ) {
                setErrorMessage( "No Payment Processor" );
            } else {
                approved = pp.processCC();

                if( approved ) {
                    setErrorMessage( null );
                } else {
                    setErrorMessage( "From " + getCreditCardName() + ": " + getR_RespMsg());
                }
            }
        } catch( Exception e ) {
            log.log( Level.SEVERE,"processOnline",e );
            setErrorMessage( "Payment Processor Error" );
        }

        setIsApproved( approved );

        return approved;
    }    // processOnline

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param pi
     * @param trx
     *
     * @return
     */

    public boolean startProcess( Properties ctx,ProcessInfo pi,Trx trx ) {
        log.info( "startProcess - " + pi.getRecord_ID());

        boolean retValue = false;

        //

        if( pi.getRecord_ID() != getID()) {
            log.log( Level.SEVERE,"startProcess - Not same Payment - " + pi.getRecord_ID());

            return false;
        }

        // Process it

        retValue = processOnline();
        save();

        return retValue;    // Payment processed
    }                       // startProcess

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

    	// Setear el valor de issotrx
    	
    	MDocType docType = MDocType.get(this.getCtx(), this.getC_DocType_ID());
    	
    	boolean valueSO = true;
    	// Si es una transacción de compra, o sea, el documento es un pago a proveedor, seteo el issotrx a 'N'
    	if(docType.getDocBaseType().equals("APP")){
    		valueSO = false;
    	}
    	
    	this.setIsSOTrx(valueSO);
    	
    	// Verificar isReceipt y setearlo dependiendo el docType
    	if(!verifyDocType()){
    		return false;
    	}
    	
        // We have a charge

        if( getC_Charge_ID() != 0 ) {
            if( newRecord || is_ValueChanged( "C_Charge_ID" )) {
                setC_Order_ID( 0 );
                setC_Invoice_ID( 0 );
                setWriteOffAmt( Env.ZERO );
                setDiscountAmt( Env.ZERO );
                setIsOverUnderPayment( false );
                setOverUnderAmt( Env.ZERO );
                setIsPrepayment( false );
            }
        }

        // We need a BPartner

        else if( (getC_BPartner_ID() == 0) &&!isCashTrx()) {
            if( getC_Invoice_ID() != 0 ) {
                ;
            } else if( getC_Order_ID() != 0 ) {
                ;
            } else {
                log.saveError( "Error",Msg.parseTranslation( getCtx(),"@NotFound@: @C_BPartner_ID@" ));

                return false;
            }
        }

        // Prepayment: No charge and order or project (not as acct dimension)

        if( newRecord || is_ValueChanged( "C_Charge_ID" ) || is_ValueChanged( "C_Invoice_ID" ) || is_ValueChanged( "C_Order_ID" ) || is_ValueChanged( "C_Project_ID" )) {
            setIsPrepayment( (getC_Charge_ID() == 0) && (getC_BPartner_ID() != 0) && ( (getC_Order_ID() != 0) || ( (getC_Project_ID() != 0) && (getC_Invoice_ID() == 0) ) ) );
        }

        if( isPrepayment()) {
            if( newRecord || is_ValueChanged( "C_Order_ID" ) || is_ValueChanged( "C_Project_ID" )) {
                setWriteOffAmt( Env.ZERO );
                setDiscountAmt( Env.ZERO );
                setIsOverUnderPayment( false );
                setOverUnderAmt( Env.ZERO );
            }
        }

        // Document Type

        if( getC_DocType_ID() == 0 ) {
            setC_DocType_ID();
        }
        
        if ( (getTenderType() != null) && (!Util.isEmpty(getC_POSPaymentMedium_ID(), true)) ){
        	MPOSPaymentMedium payMedium = new MPOSPaymentMedium(getCtx(), getC_POSPaymentMedium_ID(),get_TrxName());
        	setTenderType(payMedium.getTenderType());	
        }        

        clearTenderTypeFields();
        
        setDocumentNo();

        //

        if( getDateAcct() == null ) {
            setDateAcct( getDateTrx());
        }

        //

        if( !isOverUnderPayment()) {
            setOverUnderAmt( Env.ZERO );
        }
        
        // Es un cheque...
        if (TENDERTYPE_Check.equals(getTenderType())) {
        	// La Fecha de Vencimiento por defecto del cheque es la fecha
        	// de transacción
        	if (getDueDate() == null) {
        		setDueDate(getDateTrx());
        	}
        	// La fecha de vto debe ser mayor o igual a la fecha de transacción
        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        	String dueDateFormatted = simpleDateFormat.format(getDueDate());
        	String dateTrxFormatted = simpleDateFormat.format(getDateTrx());
			if (getDueDate().compareTo(getDateTrx()) <= 0
					&& !dueDateFormatted.equals(dateTrxFormatted)) {
        		log.saveError("SaveError", Msg.translate(getCtx(), "InvalidCheckDueDate"));
        		return false;
        	}
        }
        
        int bankAccountCurrency = new MBankAccount(getCtx(), getC_BankAccount_ID(),null).getC_Currency_ID();
		if (bankAccountCurrency != getC_Currency_ID()){
			log.saveError("SaveError", Msg.translate(getCtx(), "InvalidPayBankCurrency"));
    		return false;
		}
		
		// Para los cobros en tarjeta de crédito, el plan de EF debe ser
		// obligatorio
		if (isReceipt()
				&& MPayment.TENDERTYPE_CreditCard.equals(getTenderType())
				&& Util.isEmpty(getM_EntidadFinancieraPlan_ID(), true)) {
			log.saveError("MandatoryCreditCardPlan", "");
			return false;
		}
                
        return true;
    }    // beforeSave

    
    private void clearTenderTypeFields() {
    	String tType = getTenderType(); 
    	
    	if (!TENDERTYPE_CreditCard.equals(tType)) {
    		setCreditCardType(null);
    		setCreditCardNumber(null);
    		super.setCreditCardExpMM(0);
    		super.setCreditCardExpYY(0);
    		setCreditCardVV(null);
    	} 
    	if (!TENDERTYPE_Check.equals(tType)) {
    		setMicr(null);
    	}
    	if (!TENDERTYPE_Check.equals(tType) && !TENDERTYPE_DirectDeposit.equals(tType)) {
    		setAccountNo(null);
    		setRoutingNo(null);
    		setCheckNo(null); // Ahora aquí se guarda el nro de Transferencia también
    	}
    	if (!TENDERTYPE_Check.equals(tType) && !TENDERTYPE_CreditCard.equals(tType)) {
    		setA_Name(null);
    	}
    }
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getAllocatedAmt() {
        BigDecimal retValue = null;

        if( getC_Charge_ID() != 0 ) {
            return getPayAmt();
        }

        //

        String sql = "SELECT SUM(currencyConvert(al.Amount," + "ah.C_Currency_ID, p.C_Currency_ID,ah.DateTrx,p.C_ConversionType_ID, al.AD_Client_ID,al.AD_Org_ID)) " + "FROM C_AllocationLine al" + " INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID=ah.C_AllocationHdr_ID) " + " INNER JOIN C_Payment p ON (al.C_Payment_ID=p.C_Payment_ID) " + "WHERE al.C_Payment_ID=?" + " AND ah.IsActive='Y' AND al.IsActive='Y'";

        // + " AND al.C_Invoice_ID IS NOT NULL";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Payment_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getAllocatedAmt",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // log.fine("getAllocatedAmt - " + retValue);
        // ? ROUND(NVL(v_AllocatedAmt,0), 2);

        return retValue;
    }    // getAllocatedAmt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean testAllocation() {

        // Cash Trx always allocated

        if( isCashTrx()) {
            if( !isAllocated()) {
                setIsAllocated( true );

                return true;
            }

            return false;
        }

        //

        BigDecimal alloc = getAllocatedAmt();

        if( alloc == null ) {
            alloc = Env.ZERO;
        }

        BigDecimal total = getPayAmt();

        //////////////////////////////////////
        // openXpertya v 2.2
        // The allocation amounts are always positive numbers.
        //
        // if( !isReceipt()) {
        //     total = total.negate();
        // }
        //////////////////////////////////////
        
        boolean test   = total.compareTo( alloc ) == 0;
        boolean change = test != isAllocated();

        if( change ) {
            setIsAllocated( test );
        }

        log.fine( "Allocated=" + test + " (" + alloc + "=" + total + ")" );

        return change;
    }    // testAllocation

    /**
     * Descripción de Método
     *
     *
     * @param errorMessage
     */

    public void setErrorMessage( String errorMessage ) {
        m_errorMessage = errorMessage;
    }    // setErrorMessage

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getErrorMessage() {
        return m_errorMessage;
    }    // getErrorMessage

    /**
     * Descripción de Método
     *
     *
     * @param C_BankAccount_ID
     */

    public void setC_BankAccount_ID( int C_BankAccount_ID ) {
        if( C_BankAccount_ID == 0 ) {
            setPaymentProcessor();

            if( getC_BankAccount_ID() == 0 ) {
                throw new IllegalArgumentException( "Can't find Bank Account" );
            }
        } else {
            super.setC_BankAccount_ID( C_BankAccount_ID );
        }
    }    // setC_BankAccount_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean setPaymentProcessor() {
        return setPaymentProcessor( getTenderType(),getCreditCardType());
    }    // setPaymentProcessor

    /**
     * Descripción de Método
     *
     *
     * @param tender
     * @param CCType
     *
     * @return
     */

    public boolean setPaymentProcessor( String tender,String CCType ) {
        m_mPaymentProcessor = null;

        // Get Processor List

        if( (m_mPaymentProcessors == null) || (m_mPaymentProcessors.length == 0) ) {
            m_mPaymentProcessors = MPaymentProcessor.find( getCtx(),tender,CCType,getAD_Client_ID(),getC_Currency_ID(),getPayAmt(),get_TrxName());
        }

        // Relax Amount

        if( (m_mPaymentProcessors == null) || (m_mPaymentProcessors.length == 0) ) {
            m_mPaymentProcessors = MPaymentProcessor.find( getCtx(),tender,CCType,getAD_Client_ID(),getC_Currency_ID(),Env.ZERO,get_TrxName());
        }

        if( (m_mPaymentProcessors == null) || (m_mPaymentProcessors.length == 0) ) {
            return false;
        }

        // Find the first right one

        for( int i = 0;i < m_mPaymentProcessors.length;i++ ) {
            if( m_mPaymentProcessors[ i ].accepts( tender,CCType )) {
                m_mPaymentProcessor = m_mPaymentProcessors[ i ];
            }
        }

        if( m_mPaymentProcessor != null ) {
            setC_BankAccount_ID( m_mPaymentProcessor.getC_BankAccount_ID());
        }

        //

        return m_mPaymentProcessor != null;
    }    // setPaymentProcessor

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public ValueNamePair[] getCreditCards() {
        return getCreditCards( getPayAmt());
    }    // getCreditCards

    /**
     * Descripción de Método
     *
     *
     * @param amt
     *
     * @return
     */

    public ValueNamePair[] getCreditCards( BigDecimal amt ) {
        try {
            if( (m_mPaymentProcessors == null) || (m_mPaymentProcessors.length == 0) ) {
                m_mPaymentProcessors = MPaymentProcessor.find( getCtx(),null,null,getAD_Client_ID(),getC_Currency_ID(),amt,get_TrxName());
            }

            //

            HashMap map = new HashMap();    // to eliminate duplicates

            for( int i = 0;i < m_mPaymentProcessors.length;i++ ) {
                if( m_mPaymentProcessors[ i ].isAcceptAMEX()) {
                    map.put( CREDITCARDTYPE_Amex,getCreditCardPair( CREDITCARDTYPE_Amex ));
                }

                if( m_mPaymentProcessors[ i ].isAcceptDiners()) {
                    map.put( CREDITCARDTYPE_Diners,getCreditCardPair( CREDITCARDTYPE_Diners ));
                }

                if( m_mPaymentProcessors[ i ].isAcceptDiscover()) {
                    map.put( CREDITCARDTYPE_Discover,getCreditCardPair( CREDITCARDTYPE_Discover ));
                }

                if( m_mPaymentProcessors[ i ].isAcceptMC()) {
                    map.put( CREDITCARDTYPE_MasterCard,getCreditCardPair( CREDITCARDTYPE_MasterCard ));
                }

                if( m_mPaymentProcessors[ i ].isAcceptCorporate()) {
                    map.put( CREDITCARDTYPE_PurchaseCard,getCreditCardPair( CREDITCARDTYPE_PurchaseCard ));
                }

                if( m_mPaymentProcessors[ i ].isAcceptVisa()) {
                    map.put( CREDITCARDTYPE_Visa,getCreditCardPair( CREDITCARDTYPE_Visa ));
                }
            }    // for all payment processors

            //

            ValueNamePair[] retValue = new ValueNamePair[ map.size()];

            map.values().toArray( retValue );
            log.fine( "getCreditCards - #" + retValue.length + " - Processors=" + m_mPaymentProcessors.length );

            return retValue;
        } catch( Exception ex ) {
            ex.printStackTrace();

            return null;
        }
    }    // getCreditCards

    /**
     * Descripción de Método
     *
     *
     * @param CreditCardType
     *
     * @return
     */

    private ValueNamePair getCreditCardPair( String CreditCardType ) {
        return new ValueNamePair( CreditCardType,getCreditCardName( CreditCardType ));
    }    // getCreditCardPair

    /**
     * Descripción de Método
     *
     *
     * @param CreditCardNumber
     */

    public void setCreditCardNumber( String CreditCardNumber ) {
        super.setCreditCardNumber( MPaymentValidate.checkNumeric( CreditCardNumber ));
    }    // setCreditCardNumber

    /**
     * Descripción de Método
     *
     *
     * @param newCreditCardVV
     */

    public void setCreditCardVV( String newCreditCardVV ) {
        super.setCreditCardVV( MPaymentValidate.checkNumeric( newCreditCardVV ));
    }    // setCreditCardVV

    /**
     * Descripción de Método
     *
     *
     * @param CreditCardExpMM
     */

    public void setCreditCardExpMM( int CreditCardExpMM ) {
        if( (CreditCardExpMM < 1) || (CreditCardExpMM > 12) ) {
            ;
        } else {
            super.setCreditCardExpMM( CreditCardExpMM );
        }
    }    // setCreditCardExpMM

    /**
     * Descripción de Método
     *
     *
     * @param newCreditCardExpYY
     */

    public void setCreditCardExpYY( int newCreditCardExpYY ) {
        int CreditCardExpYY = newCreditCardExpYY;

        if( newCreditCardExpYY > 1999 ) {
            CreditCardExpYY = newCreditCardExpYY - 2000;
        }

        super.setCreditCardExpYY( CreditCardExpYY );
    }    // setCreditCardExpYY

    /**
     * Descripción de Método
     *
     *
     * @param mmyy
     *
     * @return
     */

    public boolean setCreditCardExp( String mmyy ) {
        if( MPaymentValidate.validateCreditCardExp( mmyy ).length() != 0 ) {
            return false;
        }

        //

        String exp   = MPaymentValidate.checkNumeric( mmyy );
        String mmStr = exp.substring( 0,2 );
        String yyStr = exp.substring( 2,4 );

        setCreditCardExpMM( Integer.parseInt( mmStr ));
        setCreditCardExpYY( Integer.parseInt( yyStr ));

        return true;
    }    // setCreditCardExp

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCreditCardExp() {
        String       mm       = String.valueOf( getCreditCardExpMM());
        String       yy       = String.valueOf( getCreditCardExpYY());
        StringBuffer retValue = new StringBuffer();

        if( mm.length() == 1 ) {
            retValue.append( "0" );
        }

        retValue.append( mm );

        if( yy.length() == 1 ) {
            retValue.append( "0" );
        }

        retValue.append( yy );

        //

        return( retValue.toString());
    }    // getCreditCardExp

    /**
     * Descripción de Método
     *
     *
     * @param MICR
     */

    public void setMicr( String MICR ) {
        super.setMicr( MPaymentValidate.checkNumeric( MICR ));
    }    // setBankMICR

    /**
     * Descripción de Método
     *
     *
     * @param RoutingNo
     */

    public void setRoutingNo( String RoutingNo ) {
        super.setRoutingNo( MPaymentValidate.checkNumeric( RoutingNo ));
    }    // setBankRoutingNo

    /**
     * Descripción de Método
     *
     *
     * @param AccountNo
     */

    public void setAccountNo( String AccountNo ) {
        super.setAccountNo( MPaymentValidate.checkNumeric( AccountNo ));
    }    // setBankAccountNo

    /**
     * Descripción de Método
     *
     *
     * @param CheckNo
     */

    public void setCheckNo( String CheckNo ) {
        super.setCheckNo( MPaymentValidate.checkNumeric( CheckNo ));
    }    // setBankCheckNo

    /**
     * Descripción de Método
     *
     */

    private void setDocumentNo() {

        // Cash Transfer

        if( "X".equals( getTenderType())) {
            return;
        }

        // Current Document No

        String documentNo = getDocumentNo();

        // Existing reversal

        if( (documentNo != null) && (documentNo.indexOf( REVERSE_INDICATOR ) >= 0) ) {
            return;
        }

        // If external number exists - enforce it

        if( (getR_PnRef() != null) && (getR_PnRef().length() > 0) ) {
            if( !getR_PnRef().equals( documentNo )) {
                setDocumentNo( getR_PnRef());
            }

            return;
        }

        documentNo = "";

        // Credit Card

        if( TENDERTYPE_CreditCard.equals( getTenderType())) {
        	if(getDocumentNo() == null){
        		documentNo = getCreditCardType() + " " + Obscure.obscure( getCreditCardNumber()) + " " + getCreditCardExpMM() + "/" + getCreditCardExpYY();	
        	}
        }

        // Own Check No

        else if( TENDERTYPE_Check.equals( getTenderType()) &&!isReceipt() && (getCheckNo() != null) && (getCheckNo().length() > 0) ) {
            documentNo = getCheckNo();
        }

        // Customer Check: Routing: Account #Check

        else if( TENDERTYPE_Check.equals( getTenderType()) && isReceipt()) {
            if( getRoutingNo() != null ) {
                documentNo = getRoutingNo() + ": ";
            }

            if( getAccountNo() != null ) {
                documentNo += getAccountNo();
            }

            if( getCheckNo() != null ) {
                if( documentNo.length() > 0 ) {
                    documentNo += " ";
                }

                documentNo += "#" + getCheckNo();
            }
        }

        // Set Document No

        documentNo = documentNo.trim();

        if( documentNo.length() > 0 ) {
            setDocumentNo( documentNo );
        }        	
    }    // setDocumentNo

    /**
     * Descripción de Método
     *
     *
     * @param R_PnRef
     */

    public void setR_PnRef( String R_PnRef ) {
        super.setR_PnRef( R_PnRef );

        if( R_PnRef != null ) {
            setDocumentNo( R_PnRef );
        }
    }    // setR_PnRef

    // ---------------

    /**
     * Descripción de Método
     *
     *
     * @param PayAmt
     */

    public void setPayAmt( BigDecimal PayAmt ) {
        super.setPayAmt( (PayAmt == null)
                         ?Env.ZERO
                         :PayAmt );
    }    // setPayAmt

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     * @param payAmt
     */

    public void setAmount( int C_Currency_ID,BigDecimal payAmt ) {
        if( C_Currency_ID == 0 ) {
            C_Currency_ID = MClient.get( getCtx()).getC_Currency_ID();
        }

        setC_Currency_ID( C_Currency_ID );
        setPayAmt( payAmt );
    }    // setAmount

    /**
     * Descripción de Método
     *
     *
     * @param DiscountAmt
     */

    public void setDiscountAmt( BigDecimal DiscountAmt ) {
        super.setDiscountAmt( (DiscountAmt == null)
                              ?Env.ZERO
                              :DiscountAmt );
    }    // setDiscountAmt

    /**
     * Descripción de Método
     *
     *
     * @param WriteOffAmt
     */

    public void setWriteOffAmt( BigDecimal WriteOffAmt ) {
        super.setWriteOffAmt( (WriteOffAmt == null)
                              ?Env.ZERO
                              :WriteOffAmt );
    }    // setWriteOffAmt

    /**
     * Descripción de Método
     *
     *
     * @param OverUnderAmt
     */

    public void setOverUnderAmt( BigDecimal OverUnderAmt ) {
        super.setOverUnderAmt( (OverUnderAmt == null)
                               ?Env.ZERO
                               :OverUnderAmt );
        setIsOverUnderPayment( getOverUnderAmt().compareTo( Env.ZERO ) != 0 );
    }    // setOverUnderAmt

    /**
     * Descripción de Método
     *
     *
     * @param TaxAmt
     */

    public void setTaxAmt( BigDecimal TaxAmt ) {
        super.setTaxAmt( (TaxAmt == null)
                         ?Env.ZERO
                         :TaxAmt );
    }    // setTaxAmt

    /**
     * Descripción de Método
     *
     *
     * @param ba
     */

    public void setBP_BankAccount( MBPBankAccount ba ) {
        log.fine( "setBP_BankAccount - " + ba );

        if( ba == null ) {
            return;
        }

        setC_BPartner_ID( ba.getC_BPartner_ID());
        setAccountAddress( ba.getA_Name(),ba.getA_Street(),ba.getA_City(),ba.getA_State(),ba.getA_Zip(),ba.getA_Country());
        setA_EMail( ba.getA_EMail());
        setA_Ident_DL( ba.getA_Ident_DL());
        setA_Ident_SSN( ba.getA_Ident_SSN());

        // CC

        if( ba.getCreditCardType() != null ) {
            setCreditCardType( ba.getCreditCardType());
        }

        if( ba.getCreditCardNumber() != null ) {
            setCreditCardNumber( ba.getCreditCardNumber());
        }

        if( ba.getCreditCardExpMM() != 0 ) {
            setCreditCardExpMM( ba.getCreditCardExpMM());
        }

        if( ba.getCreditCardExpYY() != 0 ) {
            setCreditCardExpYY( ba.getCreditCardExpYY());
        }

        if( ba.getCreditCardVV() != null ) {
            setCreditCardVV( ba.getCreditCardVV());
        }

        // Bank

        if( ba.getAccountNo() != null ) {
            setAccountNo( ba.getAccountNo());
        }

        if( ba.getRoutingNo() != null ) {
            setRoutingNo( ba.getRoutingNo());
        }
    }    // setBP_BankAccount

    /**
     * Descripción de Método
     *
     *
     * @param ba
     *
     * @return
     */

    public boolean saveToBP_BankAccount( MBPBankAccount ba ) {
        if( ba == null ) {
            return false;
        }

        ba.setA_Name( getA_Name());
        ba.setA_Street( getA_Street());
        ba.setA_City( getA_City());
        ba.setA_State( getA_State());
        ba.setA_Zip( getA_Zip());
        ba.setA_Country( getA_Country());
        ba.setA_EMail( getA_EMail());
        ba.setA_Ident_DL( getA_Ident_DL());
        ba.setA_Ident_SSN( getA_Ident_SSN());

        // CC

        ba.setCreditCardType( getCreditCardType());
        ba.setCreditCardNumber( getCreditCardNumber());
        ba.setCreditCardExpMM( getCreditCardExpMM());
        ba.setCreditCardExpYY( getCreditCardExpYY());
        ba.setCreditCardVV( getCreditCardVV());

        // Bank

        if( getAccountNo() != null ) {
            ba.setAccountNo( getAccountNo());
        }

        if( getRoutingNo() != null ) {
            ba.setRoutingNo( getRoutingNo());
        }

        // Trx

        ba.setR_AvsAddr( getR_AvsAddr());
        ba.setR_AvsZip( getR_AvsZip());

        //

        boolean ok = ba.save( get_TrxName());

        log.fine( "saveToBP_BankAccount - " + ba );

        return ok;
    }    // setBP_BankAccount

    /**
     * Descripción de Método
     *
     */

    private void setC_DocType_ID() {
        setC_DocType_ID( isReceipt());
    }    // setC_DocType_ID

    /**
     * Descripción de Método
     *
     *
     * @param isReceipt
     */

    public void setC_DocType_ID( boolean isReceipt ) {
        setIsReceipt( isReceipt );

        String sql = "SELECT C_DocType_ID FROM C_DocType WHERE AD_Client_ID=? AND DocBaseType=? AND IsPaymentOrderSeq = 'N' AND IsReceiptSeq = 'N' ORDER BY IsDefault DESC";
        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,get_TrxName());

            pstmt.setInt( 1,getAD_Client_ID());

            if( isReceipt ) {
                pstmt.setString( 2,MDocType.DOCBASETYPE_ARReceipt );
            } else {
                pstmt.setString( 2,MDocType.DOCBASETYPE_APPayment );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                setC_DocType_ID( rs.getInt( 1 ));
            } else {
                log.warning( "setDocType - NOT found - isReceipt=" + isReceipt );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"setDocType",e );
        }
    }    // setC_DocType_ID

    /**
     * Descripción de Método
     *
     *
     * @param C_DocType_ID
     */

    public void setC_DocType_ID( int C_DocType_ID ) {

        // if (getDocumentNo() != null && getC_DocType_ID() != C_DocType_ID)
        // setDocumentNo(null);

        super.setC_DocType_ID( C_DocType_ID );
    }    // setC_DocType_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean verifyDocType() {
        if( getC_DocType_ID() == 0 ) {
            return false;
        }

        //

        Boolean invoiceSO = null;

        // Check Invoice First

        if( getC_Invoice_ID() > 0 ) {
            String sql = "SELECT idt.IsSOTrx " + "FROM C_Invoice i" + " INNER JOIN C_DocType idt ON (i.C_DocType_ID=idt.C_DocType_ID) " + "WHERE i.C_Invoice_ID=?";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,getC_Invoice_ID());

                ResultSet rs = pstmt.executeQuery();

                if( rs.next()) {
                    invoiceSO = new Boolean( "Y".equals( rs.getString( 1 )));
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"verifyDocType - invoice",e );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception e ) {
                pstmt = null;
            }
        }    // Invoice

        // DocumentType

        Boolean           paymentSO = null;
        PreparedStatement pstmt     = null;

        try {
            String sql = "SELECT IsSOTrx " + "FROM C_DocType " + "WHERE C_DocType_ID=?";

            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_DocType_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                paymentSO = new Boolean( "Y".equals( rs.getString( 1 )));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"verifyDocType",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // No Payment info

        if( paymentSO == null ) {
            return false;
        }

        setIsReceipt( paymentSO.booleanValue());

        // We have an Invoice .. and it does not match

        if( (invoiceSO != null) && (invoiceSO.booleanValue() != paymentSO.booleanValue())) {
            return false;
        }

        // OK

        return true;
    }    // verifyDocType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrencyISO() {
        return MCurrency.getISO_Code( getCtx(),getC_Currency_ID());
    }    // getCurrencyISO

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatusName() {
        return MRefList.getListName( getCtx(),131,getDocStatus());
    }    // getDocStatusName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCreditCardName() {
        return getCreditCardName( getCreditCardType());
    }    // getCreditCardName

    /**
     * Descripción de Método
     *
     *
     * @param CreditCardType
     *
     * @return
     */

    public String getCreditCardName( String CreditCardType ) {
        if( CreditCardType == null ) {
            return "--";
        } else if( CREDITCARDTYPE_MasterCard.equals( CreditCardType )) {
            return "MasterCard";
        } else if( CREDITCARDTYPE_Visa.equals( CreditCardType )) {
            return "Visa";
        } else if( CREDITCARDTYPE_Amex.equals( CreditCardType )) {
            return "Amex";
        } else if( CREDITCARDTYPE_ATM.equals( CreditCardType )) {
            return "ATM";
        } else if( CREDITCARDTYPE_Diners.equals( CreditCardType )) {
            return "Diners";
        } else if( CREDITCARDTYPE_Discover.equals( CreditCardType )) {
            return "Discover";
        } else if( CREDITCARDTYPE_PurchaseCard.equals( CreditCardType )) {
            return "PurchaseCard";
        }

        return "?" + CreditCardType + "?";
    }    // getCreditCardName

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        String desc = getDescription();

        if( desc == null ) {
            setDescription( description );
        } else {
            setDescription( desc + " | " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @param absolute
     *
     * @return
     */

    public BigDecimal getPayAmt( boolean absolute ) {
        if( isReceipt()) {
            return super.getPayAmt();
        }

        return super.getPayAmt().negate();
    }    // getPayAmt

    /**
     * Descripción de Método
     *
     *
     * @param processAction
     *
     * @return
     */

    public boolean processIt( String processAction ) {
        m_processMsg = null;

        DocumentEngine engine = new DocumentEngine( this,getDocStatus());

        boolean status = engine.processIt( processAction,getDocAction(),log );
        
        status = this.afterProcessDocument(engine.getDocAction(), status) && status;
        
        return status;
    }    // process


    /** Descripción de Campos */

    private boolean m_justPrepared = false;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt() {
        log.info( "unlockIt - " + toString());
        setProcessing( false );

        return true;
    }    // unlockIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt() {
        log.info( "invalidateIt - " + toString());
        setDocAction( DOCACTION_Prepare );

        return true;
    }    // invalidateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt() {
        log.info( toString());
        m_processMsg = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_BEFORE_PREPARE );

        if( m_processMsg != null ) {
            return DocAction.STATUS_Invalid;
        }

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),isReceipt()
                ?MDocType.DOCBASETYPE_ARReceipt
                :MDocType.DOCBASETYPE_APPayment )) {
            m_processMsg = "@PeriodClosed@";

            return DocAction.STATUS_Invalid;
        }

        // Unsuccessful Online Payment

        if( isOnline() &&!isApproved()) {
            if( getR_Result() != null ) {
                m_processMsg = "@OnlinePaymentFailed@";
            } else {
                m_processMsg = "@PaymentNotProcessed@";
            }

            return DocAction.STATUS_Invalid;
        }

        // Waiting Payment - Need to create Invoice & Shipment

        if( (getC_Order_ID() != 0) && (getC_Invoice_ID() == 0) ) {    // see WebOrder.process
            MOrder order = new MOrder( getCtx(),getC_Order_ID(),get_TrxName());

            if( DOCSTATUS_WaitingPayment.equals( order.getDocStatus())) {
                order.setC_Payment_ID( getC_Payment_ID());
                order.setDocAction( MOrder.DOCACTION_WaitComplete );
                order.set_TrxName( get_TrxName());

                boolean ok = order.processIt( MOrder.DOCACTION_WaitComplete );

                m_processMsg = order.getProcessMsg();
                order.save( get_TrxName());

                // Set Invoice

                MInvoice[] invoices = order.getInvoices();
                int        length   = invoices.length;

                if( length > 0 ) {    // get last invoice
                    setC_Invoice_ID( invoices[ length - 1 ].getC_Invoice_ID());
                }

                //

                if( getC_Invoice_ID() == 0 ) {
                    m_processMsg = "@NotFound@ @C_Invoice_ID@";

                    return DocAction.STATUS_Invalid;
                }
            }                         // WaitingPayment
        }

        // Consistency of Invoice / Document Type and IsReceipt

        if( !verifyDocType()) {
            m_processMsg = "@PaymentDocTypeInvoiceInconsistent@";

            return DocAction.STATUS_Invalid;
        }

        // Do not pay when Credit Stop/Hold

        if( !isReceipt()) {
            MBPartner bp = new MBPartner( getCtx(),getC_BPartner_ID(),get_TrxName());

            if( MBPartner.SOCREDITSTATUS_CreditStop.equals( bp.getSOCreditStatus())) {
                m_processMsg = "@BPartnerCreditStop@ - @TotalOpenBalance@=" + bp.getTotalOpenBalance() + ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();

                return DocAction.STATUS_Invalid;
            }

            if( MBPartner.SOCREDITSTATUS_CreditHold.equals( bp.getSOCreditStatus())) {
                m_processMsg = "@BPartnerCreditHold@ - @TotalOpenBalance@=" + bp.getTotalOpenBalance() + ", @SO_CreditLimit@=" + bp.getSO_CreditLimit();

                return DocAction.STATUS_Invalid;
            }
        }

        m_justPrepared = true;

        if( !DOCACTION_Complete.equals( getDocAction())) {
            setDocAction( DOCACTION_Complete );
        }

        // No se permiten procesar pagos con importe cero.
        if (getPayAmt().compareTo(BigDecimal.ZERO) == 0) {
        	m_processMsg = "@InvalidPaymentAmountError@";
        	return DocAction.STATUS_Invalid;
        }
        
        // Se valida que la factura NO esté pagada. Además se valida que el monto pendiente
        // de la factura no supere el monto del pago.
        if (getC_Invoice_ID() != 0)  {
        	MInvoice inv = new MInvoice(getCtx(), getC_Invoice_ID(), get_TrxName());
        	if (inv.isPaid()) {
        		m_processMsg = "@PaymentError@: @InvoiceAlreadyPaid@";
        		return DocAction.STATUS_Invalid;
        	}
        	
        	BigDecimal openAmt = inv.getOpenAmt();
        	
        	if (openAmt.compareTo(getTotalAmt()) < 0) {
        		m_processMsg = "@PaymentError@: @InvalidPaymentAmountForInvoice@ ("+openAmt+")";
        		return DocAction.STATUS_Invalid;
        	}
        }

        // Disytel: Si no hay conversion, no permitir seleccionar moneda destino
 		int currencyClient = MClient.get( getCtx()).getC_Currency_ID();
 		if ((currencyClient != getC_Currency_ID() && MCurrency.currencyConvert(new BigDecimal(1), currencyClient, getC_Currency_ID(), getDateTrx(), getAD_Org_ID(), getCtx()) == null)) {
 			m_processMsg = "@NoCurrencyConversion@";
    		return DocAction.STATUS_Invalid;
 		}
 		
 		// Si la moneda del documento es diferente a la de la compañia:
 		// Se valida que exista una tasa de conversión entre las monedas para la fecha de aplicación del documento.
 		if (!validatePaymentCurrencyConvert()){
			m_processMsg = "@NoConversionRateDateAcct@";
			return DocAction.STATUS_Invalid;
		}
        
        return DocAction.STATUS_InProgress;
    }    // prepareIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt() {
        log.info( toString());
        setIsApproved( true );

        return true;
    }    // approveIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt() {
        log.info( toString());
        setIsApproved( false );

        return true;
    }    // rejectIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt() {
    	setAditionalWorkResult(new HashMap<PO, Object>());
        // Re-Check

        if( !m_justPrepared ) {
            String status = prepareIt();

            if( !DocAction.STATUS_InProgress.equals( status )) {
                return status;
            }
        }

        // Implicit Approval

        if( !isApproved()) {
            approveIt();
        }

        log.info( toString());

        // Charge Handling

        if( getC_Charge_ID() != 0 ) {
            setIsAllocated( true );
        } else {
            allocateIt();    // Create Allocation Records
            testAllocation();
        }

        // Project update

        if( getC_Project_ID() != 0 ) {

            // MProject project = new MProject(getCtx(), getC_Project_ID());

        }

        // User Validation

        String valid = ModelValidationEngine.get().fireDocValidate( this,ModelValidator.TIMING_AFTER_COMPLETE );

        if( valid != null ) {
            m_processMsg = valid;

            return DocAction.STATUS_Invalid;
        }

        MPayment counter = createCounterDoc();

        if( counter != null ) {
            m_processMsg = "@CounterDoc@: @C_Payment_ID@=" + counter.getDocumentNo();
        }

        setC_POSJournal_ID(getVoidPOSJournalID() != 0 ? getVoidPOSJournalID()
				: getC_POSJournal_ID());
        // Si ya tenía una asignada, verificar si está abierta o en verificación
		// Si no se encuentra en ninguno de los dos estados, entonces se setea a
		// 0 para que se asigne la caja diaria actual
		if (getC_POSJournal_ID() != 0
				&& !MPOSJournal.isPOSJournalOpened(getCtx(),
						getC_POSJournal_ID(), get_TrxName())) {
			// Si se debe realizar el control obligatorio de apertura y la caja
			// diaria de anulación está seteada, entonces error
			if(getVoidPOSJournalID() != 0 && isVoidPOSJournalMustBeOpen()){
				m_processMsg = MPOSJournal.POS_JOURNAL_VOID_CLOSED_ERROR_MSG;
				return STATUS_Invalid;
			}
			log.severe("POS Journal assigned with ID "+getC_POSJournal_ID()+" is closed");
			setC_POSJournal_ID(0);			
		}
		// Caja Diaria. Intenta registrar la factura
		if (getC_POSJournal_ID() == 0 && !MPOSJournal.registerDocument(this)) {
			m_processMsg = MPOSJournal.DOCUMENT_COMPLETE_ERROR_MSG;
			return STATUS_Invalid;
		}
        
        //
        MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
				get_TrxName());
        if(isUpdateBPBalance()){
    		// Verifico si el gestor de cuentas corrientes debe realizar operaciones
    		// antes de completar y eventualmente disparar la impresión fiscal
    		// Obtengo el manager actual
    		CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
    		// Actualizo el balance
    		CallResult result = new CallResult();
			try{
				result = manager.performAditionalWork(getCtx(), new MOrg(
	    				getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()), bp, this, 
	    				false, get_TrxName());
			} catch(Exception e){
				result.setMsg(e.getMessage(), true);
			}
    		// Si hubo error, obtengo el mensaje y retorno inválido
    		if (result.isError()) {
    			m_processMsg = result.getMsg();
    			return DocAction.STATUS_Invalid;
    		}
    		// Me guardo el resultado de la llamada 
    		getAditionalWorkResult().put(this, result.getResult());
        }
        
        setProcessed( true );
        setDocAction( DOCACTION_Close );

        return DocAction.STATUS_Completed;
    }    // completeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private MPayment createCounterDoc() {

        // Is this a counter doc ?

        if( getRef_Payment_ID() != 0 ) {
            return null;
        }

        // Org Must be linked to BPartner

        MOrg org                  = MOrg.get( getCtx(),getAD_Org_ID());
        int  counterC_BPartner_ID = org.getLinkedC_BPartner_ID();

        if( counterC_BPartner_ID == 0 ) {
            return null;
        }

        // Business Partner needs to be linked to Org

        MBPartner bp               = new MBPartner( getCtx(),getC_BPartner_ID(),null );
        int       counterAD_Org_ID = bp.getAD_OrgBP_ID_Int();

        if( counterAD_Org_ID == 0 ) {
            return null;
        }

        MBPartner counterBP = new MBPartner( getCtx(),counterC_BPartner_ID,null );
        MOrgInfo counterOrgInfo = MOrgInfo.get( getCtx(),counterAD_Org_ID );

        log.info( "Counter BP=" + counterBP.getName());

        // Document Type

        int             C_DocTypeTarget_ID = 0;
        MDocTypeCounter counterDT          = MDocTypeCounter.getCounterDocType( getCtx(),getC_DocType_ID());

        if( counterDT != null ) {
            log.fine( counterDT.toString());

            if( !counterDT.isCreateCounter() ||!counterDT.isValid()) {
                return null;
            }

            C_DocTypeTarget_ID = counterDT.getCounter_C_DocType_ID();
        } else    // indirect
        {
            C_DocTypeTarget_ID = MDocTypeCounter.getCounterDocType_ID( getCtx(),getC_DocType_ID());
            log.fine( "Indirect C_DocTypeTarget_ID=" + C_DocTypeTarget_ID );

            if( C_DocTypeTarget_ID <= 0 ) {
                return null;
            }
        }

        // Deep Copy

        MPayment counter = new MPayment( getCtx(),0,get_TrxName());

        counter.setAD_Org_ID( counterAD_Org_ID );
        counter.setC_BPartner_ID( counterBP.getC_BPartner_ID());
        counter.setIsReceipt( !isReceipt());
        counter.setC_DocType_ID( C_DocTypeTarget_ID );
        counter.setTrxType( getTrxType());
        counter.setTenderType( getTenderType());

        //

        counter.setPayAmt( getPayAmt());
        counter.setDiscountAmt( getDiscountAmt());
        counter.setTaxAmt( getTaxAmt());
        counter.setWriteOffAmt( getWriteOffAmt());
        counter.setIsOverUnderPayment( isOverUnderPayment());
        counter.setOverUnderAmt( getOverUnderAmt());
        counter.setC_Currency_ID( getC_Currency_ID());
        counter.setC_ConversionType_ID( getC_ConversionType_ID());

        //

        counter.setDateTrx( getDateTrx());
        counter.setDateAcct( getDateAcct());
        counter.setRef_Payment_ID( getC_Payment_ID());

        //

        String sql = "SELECT C_BankAccount_ID FROM C_BankAccount " + "WHERE C_Currency_ID=? AND AD_Org_ID IN (0,?) AND IsActive='Y' " + "ORDER BY IsDefault DESC";
        int C_BankAccount_ID = DB.getSQLValue( get_TrxName(),sql,getC_Currency_ID(),counterAD_Org_ID );

        counter.setC_BankAccount_ID( C_BankAccount_ID );

        // Refernces

        counter.setC_Activity_ID( getC_Activity_ID());
        counter.setC_Campaign_ID( getC_Campaign_ID());
        counter.setC_Project_ID( getC_Project_ID());
        counter.setUser1_ID( getUser1_ID());
        counter.setUser2_ID( getUser2_ID());
        counter.save( get_TrxName());
        log.fine( counter.toString());
        setRef_Payment_ID( counter.getC_Payment_ID());

        // Document Action

        if( counterDT != null ) {
            if( counterDT.getDocAction() != null ) {
                counter.setDocAction( counterDT.getDocAction());
                counter.processIt( counterDT.getDocAction());
                counter.save( get_TrxName());
            }
        }

        return counter;
    }    // createCounterDoc

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean allocateIt() {

        // Create invoice Allocation -     See also MCash.completeIt

    	// Cuenta cuantos allocation hay creados con este pago. Debe ser cero para
    	// poder crear uno nuevo.
    	
    	int x = MAllocationHdr.CanCreateAllocation(this, get_TrxName());
    	if (x < 0)
    		return false;
    	
    	if (x == 0) {
    	
	        if( getC_Invoice_ID() != 0 ) {
	            return allocateInvoice();
	        } else {    // Invoices of a AP Payment Selection
	            return allocatePaySelection();
	        }
        
    	}
    	
    	return true;
    }               // allocateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean allocateInvoice() {

        // calculate actual allocation

        BigDecimal allocationAmt = getPayAmt();    // underpayment

        if( (getOverUnderAmt().signum() < 0) && (getPayAmt().signum() > 0) ) {
            allocationAmt = allocationAmt.add( getOverUnderAmt());    // overpayment (negative)
        }

        MAllocationHdr alloc = new MAllocationHdr( getCtx(),false,getDateTrx(),getC_Currency_ID(),Msg.translate( getCtx(),"C_Payment_ID" ) + ": " + getDocumentNo() + " [1]",get_TrxName());

        if( !alloc.save( get_TrxName())) {
            log.log( Level.SEVERE,"Could not create Allocation Hdr" );

            return false;
        }

        MAllocationLine aLine = null;

        if( isReceipt()) {
            aLine = new MAllocationLine( alloc,allocationAmt,getDiscountAmt(),getWriteOffAmt(),getOverUnderAmt());
        } else {
        	//aLine = new MAllocationLine( alloc,allocationAmt.negate(),getDiscountAmt().negate(),getWriteOffAmt().negate(),getOverUnderAmt().negate());
        	aLine = new MAllocationLine( alloc,allocationAmt,getDiscountAmt(),getWriteOffAmt(),getOverUnderAmt());
        }

        aLine.setDocInfo( getC_BPartner_ID(),0,getC_Invoice_ID());
        aLine.setC_Payment_ID( getC_Payment_ID());

        if( !aLine.save( get_TrxName())) {
            log.log( Level.SEVERE,"Could not create Allocation Line" );

            return false;
        }

        // Should start WF

        alloc.processIt( DocAction.ACTION_Complete );
        alloc.save( get_TrxName());
        m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();

        // Get Project from Invoice

        int C_Project_ID = DB.getSQLValue( get_TrxName(),"SELECT MAX(C_Project_ID) FROM C_Invoice WHERE C_Invoice_ID=?",getC_Invoice_ID());

        if( (C_Project_ID > 0) && (getC_Project_ID() == 0) ) {
            setC_Project_ID( C_Project_ID );
        } else if( (C_Project_ID > 0) && (getC_Project_ID() > 0) && (C_Project_ID != getC_Project_ID())) {
            log.warning( "Invoice C_Project_ID=" + C_Project_ID + " <> Payment C_Project_ID=" + getC_Project_ID());
        }

        return true;
    }    // allocateInvoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean allocatePaySelection() {
        MAllocationHdr alloc = new MAllocationHdr( getCtx(),false,getDateTrx(),getC_Currency_ID(),Msg.translate( getCtx(),"C_Payment_ID" ) + ": " + getDocumentNo() + " [n]",get_TrxName());
        String sql = "SELECT psc.C_BPartner_ID, psl.C_Invoice_ID, psl.IsSOTrx, "    // 1..3
                     + " psl.PayAmt, psl.DiscountAmt, psl.DifferenceAmt, psl.OpenAmt " + "FROM C_PaySelectionLine psl" + " INNER JOIN C_PaySelectionCheck psc ON (psl.C_PaySelectionCheck_ID=psc.C_PaySelectionCheck_ID) " + "WHERE psc.C_Payment_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Payment_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int C_BPartner_ID = rs.getInt( 1 );
                int C_Invoice_ID  = rs.getInt( 2 );

                if( (C_BPartner_ID == 0) && (C_Invoice_ID == 0) ) {
                    continue;
                }

                boolean    isSOTrx      = "Y".equals( rs.getString( 3 ));
                BigDecimal PayAmt       = rs.getBigDecimal( 4 );
                BigDecimal DiscountAmt  = rs.getBigDecimal( 5 );
                BigDecimal WriteOffAmt  = rs.getBigDecimal( 6 );
                BigDecimal OpenAmt      = rs.getBigDecimal( 7 );
                BigDecimal OverUnderAmt = OpenAmt.subtract( PayAmt ).subtract( DiscountAmt ).subtract( WriteOffAmt );

                //

                if( (alloc.getID() == 0) &&!alloc.save( get_TrxName())) {
                    log.log( Level.SEVERE,"Could not create Allocation Hdr" );
                    rs.close();
                    pstmt.close();

                    return false;
                }

                MAllocationLine aLine = null;

                if( isSOTrx ) {
                    aLine = new MAllocationLine( alloc,PayAmt,DiscountAmt,WriteOffAmt,OverUnderAmt );
                } else {
                    aLine = new MAllocationLine( alloc,PayAmt.negate(),DiscountAmt.negate(),WriteOffAmt.negate(),OverUnderAmt.negate());
                }

                aLine.setDocInfo( C_BPartner_ID,0,C_Invoice_ID );
                aLine.setC_Payment_ID( getC_Payment_ID());

                if( !aLine.save( get_TrxName())) {
                    log.log( Level.SEVERE,"Could not create Allocation Line" );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"allocatePaySelection",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Should start WF

        boolean ok = true;

        if( alloc.getID() == 0 ) {
            log.fine( "No Allocation created - C_Payment_ID=" + getC_Payment_ID());
            ok = false;
        } else {
            alloc.processIt( DocAction.ACTION_Complete );
            ok           = alloc.save( get_TrxName());
            m_processMsg = "@C_AllocationHdr_ID@: " + alloc.getDocumentNo();
        }

        return ok;
    }    // allocatePaySelection

    /**
     * Descripción de Método
     *
     */

    private void deAllocate() {
        // De-Allocate all

        MAllocationHdr[] allocations = MAllocationHdr.getOfPayment( getCtx(),getC_Payment_ID(),get_TrxName());

        log.fine( "#" + allocations.length );

        for( int i = 0;i < allocations.length;i++ ) {
            allocations[ i ].set_TrxName( get_TrxName());
            allocations[ i ].setDocAction( DocAction.ACTION_Reverse_Correct );
            allocations[ i ].processIt( DocAction.ACTION_Reverse_Correct );
            allocations[ i ].save( get_TrxName());
        }

        // Unlink

        if( getC_Invoice_ID() != 0  || getC_Order_ID() != 0) {

        	MBPartner bp = new MBPartner( getCtx(),getC_BPartner_ID(),get_TrxName());

            // Invoice

            String sql = "UPDATE C_Invoice " + "SET C_Payment_ID = NULL " + "WHERE C_Invoice_ID=" + getC_Invoice_ID() + " AND C_Payment_ID=" + getC_Payment_ID();
            int no = DB.executeUpdate( sql,get_TrxName());

            log.fine( "Unlink Invoice #" + no );

            // Order

            sql = "UPDATE C_Order o " + "SET C_Payment_ID = NULL " + "WHERE EXISTS (SELECT * FROM C_Invoice i " + "WHERE o.C_Order_ID=i.C_Order_ID AND i.C_Invoice_ID=" + getC_Invoice_ID() + ")" + " AND C_Payment_ID=" + getC_Payment_ID();
            no = DB.executeUpdate( sql,get_TrxName());
            
            // Recalculo el crédito 
            
            bp.setTotalOpenBalance();
            bp.save( get_TrxName());
            
            log.fine( "Unlink Order #" + no );
        }

        //
        setC_Order_ID( 0 );
        setC_Invoice_ID( 0 );
        
        setIsAllocated( false );        
    }    // deallocate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt() {
        log.info( toString());

        return false;
    }    // postIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt() {
    	setAditionalWorkResult(new HashMap<PO, Object>());
        log.info( toString());

        // Disytel - Franco Bonafine
        // No es posible anular pagos anular pagos que están contabilizados.
        // TODO: Ver si en el caso de que el pago esté contabilizado sería factible hacer una Inversa/Correccion.
        // Comentado por Disytel - Matías Cap
		// Debido a que se crea un payment inverso esta validación ya no está
		// vigente
        /*
        if (isPosted()) {
        	m_processMsg = "@NonPostedPaymentNeededError@";
        	return false;
        }*/
        
        // If on Bank Statement, don't void it - reverse it

//        if( getC_BankStatementLine_ID() > 0 ) {
        boolean reversed = reverseCorrectIt();
//        }
        
        // Disytel - Franco Bonafine
        // No es posible anular pagos que se encuentran en alguna asignación.
        // Primero se deben revertir las asignaciones y luego anular el pago.
        // En caso de exista, se ignora la asignación que causó la anulación de este pago
//        if (isInAllocation(getVoiderAllocationID())) {
//        	m_processMsg = "@FreePaymentNeededError@";
//        	return false;
//        }
//        
//        addDescription(MRefList.getListName(getCtx(), DOCSTATUS_AD_Reference_ID, DOCSTATUS_Voided) + " (" + getPayAmt() + ")" );
//        
//        setPayAmt( Env.ZERO );
//        setDiscountAmt( Env.ZERO );
//        setWriteOffAmt( Env.ZERO );
//        setOverUnderAmt( Env.ZERO );
//        setIsAllocated( false );
//
//        // Unlink & De-Allocate
//
//        // deAllocate(); Ya no se revierten las asignaciones automáticamente. Las debe revertir manualmente el usuario.
//
        if(reversed){
        	setProcessed( true );
        	setDocStatus(DOCSTATUS_Voided);
            setDocAction( DOCACTION_None );        	
        }
        
        return reversed;
    }    // voidIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt() {
        log.info( toString());
        setDocAction( DOCACTION_None );

        return true;
    }    // closeIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt() {
        log.info( toString());

        // Std Period open?

        if( !MPeriod.isOpen( getCtx(),getDateAcct(),isReceipt()
                ?MDocType.DOCBASETYPE_ARReceipt
                :MDocType.DOCBASETYPE_APPayment )) {
            m_processMsg = "@PeriodClosed@";

            return false;
        }
        
        // Disytel - Franco Bonafine
        // No es posible anular pagos que se encuentran en alguna asignación.
        // Primero se deben revertir las asignaciones y luego anular el pago.
        // En caso de exista, se ignora la asignación que causó la anulación de este pago
        if (isInAllocation(getVoiderAllocationID())) {
        	m_processMsg = "@FreePaymentNeededError@";
        	return false;
        }

        // Auto Reconcile if not on Bank Statement

        boolean reconciled = false;    // getC_BankStatementLine_ID() == 0;

        // Create Reversal

        MPayment reversal = new MPayment( getCtx(),0,get_TrxName());

        copyValues( this,reversal );
        reversal.setClientOrg( this );
        reversal.setC_Order_ID( 0 );
        reversal.setC_Invoice_ID( 0 );

        //

        reversal.setDocumentNo( getDocumentNo() + REVERSE_INDICATOR );    // indicate reversals
        reversal.setDocStatus( DOCSTATUS_Drafted );
        reversal.setDocAction( DOCACTION_Complete );

        //

        reversal.setPayAmt( getPayAmt().negate());
        reversal.setDiscountAmt( getDiscountAmt().negate());
        reversal.setWriteOffAmt( getWriteOffAmt().negate());
        reversal.setOverUnderAmt( getOverUnderAmt().negate());

        //

        reversal.setIsAllocated( true );
        reversal.setIsReconciled( reconciled );    // to put on bank statement
        reversal.setIsOnline( false );
        reversal.setIsApproved( true );
        reversal.setR_PnRef( null );
        reversal.setR_Result( null );
        reversal.setR_RespMsg( null );
        reversal.setR_AuthCode( null );
        reversal.setR_Info( null );
        reversal.setProcessing( false );
        reversal.setOProcessing( "N" );
        reversal.setProcessed( false );
        reversal.setPosted( false );
        reversal.setDescription( getDescription());
        reversal.addDescription( "{->" + getDocumentNo() + ")" );
        reversal.save( get_TrxName());
        // No confirmo el trabajo adicional de cuentas corrientes porque se debe
		// realizar luego de anular la factura
        reversal.setConfirmAditionalWorks(false);
        
        // Se asigna la misma caja diaria del documento a anular
        reversal.setVoidPOSJournalID(getVoidPOSJournalID());
		reversal.setVoidPOSJournalMustBeOpen(isVoidPOSJournalMustBeOpen());
		reversal.setC_POSJournal_ID(getC_POSJournal_ID());
        
        // Post Reversal

        if( !reversal.processIt( DocAction.ACTION_Complete )) {
            m_processMsg = "@ReversalError@: " + reversal.getProcessMsg();

            return false;
        }

        reversal.closeIt();
        // Me traigo el trabajo adicional de cuentas corrientes y lo confirmo
		// después 
		getAditionalWorkResult().put(reversal,
				reversal.getAditionalWorkResult().get(reversal));
		if(isUpdateBPBalance()){
        	MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(),
					get_TrxName());
    		// Verifico si el gestor de cuentas corrientes debe realizar operaciones
    		// antes de completar 
    		CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
    		// Actualizo el balance
    		CallResult result = new CallResult();
			try{
				result = manager.performAditionalWork(getCtx(), new MOrg(
	    				getCtx(), Env.getAD_Org_ID(getCtx()), get_TrxName()), bp, this, 
	    				false, get_TrxName());
			} catch(Exception e){
				result.setMsg(e.getMessage(), true);
			}
    		// Si hubo error, obtengo el mensaje y retorno inválido
    		if (result.isError()) {
    			m_processMsg = result.getMsg();
    			return false;
    		}
    		// Me guardo el resultado de la llamada 
    		getAditionalWorkResult().put(this, result.getResult());
        }       
		
		// Si es un cheque de tercero lo que estoy anulando, entonces al cheque
		// original se lo debe dejar Completo para que se puedan realizar las
		// acciones pertienentes. Los cheques originales sino quedan en estado
		// Cerrado y no se pueden anular
		if(!Util.isEmpty(getOriginal_Ref_Payment_ID(), true)){
			MPayment originalPayment = new MPayment(getCtx(),
					getOriginal_Ref_Payment_ID(), get_TrxName());
			originalPayment.setDocStatus(MPayment.DOCSTATUS_Completed);
			originalPayment.setDocAction(MPayment.DOCSTATUS_Closed);
			if(!originalPayment.save()){
				m_processMsg = CLogger.retrieveErrorAsString();
				return false;
			}
		}
		
        // Disytel - FB
        // Dejamos como Revertido el documento inverso a fin de mantener la consistencia
        // de visibilidad con el documento revertido, de modo que ambos documentos aparezcan
        // en el mismo lugar
        //reversal.setDocStatus( DOCSTATUS_Closed );
        reversal.setDocStatus( DOCSTATUS_Reversed );
        reversal.setDocAction( DOCACTION_None );
        reversal.save( get_TrxName());

        // Disytel FB - Ya no se desasignan automáticamente los pagos. Se debe revertir la asignación manualmente.
        // Unlink & De-Allocate

        // deAllocate();
        /////////////////////////
        setIsReconciled( reconciled );
        setIsAllocated( true );    // the allocation below is overwritten

        // Set Status

        addDescription( "(" + reversal.getDocumentNo() + "<-)" );
        setDocStatus(DOCSTATUS_Reversed);
        setDocAction( DOCACTION_None );
        setProcessed( true );

        // Create automatic Allocation

        MAllocationHdr alloc = new MAllocationHdr( getCtx(),false,getDateTrx(),getC_Currency_ID(),Msg.translate( getCtx(),"C_Payment_ID" ) + ": " + reversal.getDocumentNo(),get_TrxName());

        if( !alloc.save( get_TrxName())) {
            log.warning( "Automatic allocation - hdr not saved" );
        } else {
        	
            // Original Allocation

            MAllocationLine aLine = new MAllocationLine( alloc,getPayAmt(),Env.ZERO,Env.ZERO,Env.ZERO );

            aLine.setDocInfo( getC_BPartner_ID(),0,0 );
            aLine.setPaymentInfo( getC_Payment_ID(),0 );

            if( !aLine.save( get_TrxName())) {
                log.warning( "Automatic allocation - line not saved" );
            }

            // Reversal Allocation

            aLine = new MAllocationLine( alloc,reversal.getPayAmt(),Env.ZERO,Env.ZERO,Env.ZERO );
            aLine.setDocInfo( reversal.getC_BPartner_ID(),0,0 );
            aLine.setPaymentInfo( reversal.getC_Payment_ID(),0 );

            if( !aLine.save( get_TrxName())) {
                log.warning( "Automatic allocation - reversal line not saved" );
            }
        }
        alloc.setUpdateBPBalance(false);
        alloc.processIt( DocAction.ACTION_Complete );
        alloc.save( get_TrxName());

        //

        StringBuffer info = new StringBuffer( reversal.getDocumentNo());

        info.append( " - @C_AllocationHdr_ID@: " ).append( alloc.getDocumentNo());

        //

        m_processMsg = info.toString();

        return true;
    }    // reverseCorrectionIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int getC_BankStatementLine_ID() {
        String sql = "SELECT C_BankStatementLine_ID FROM C_BankStatementLine WHERE C_Payment_ID=?";
        int id = DB.getSQLValue( get_TrxName(),sql,getC_Payment_ID());

        if( id < 0 ) {
            return 0;
        }

        return id;
    }    // getC_BankStatementLine_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt() {
        log.info( toString());

        return false;
    }    // reverseAccrualIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt() {
        log.info( toString());

        if( reverseCorrectIt()) {
            return true;
        }

        return false;
    }    // reActivateIt

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPayment[" );

        sb.append( getID()).append( "-" ).append( getDocumentNo()).append( ",Receipt=" ).append( isReceipt()).append( ",PayAmt=" ).append( getPayAmt()).append( ",Discount=" ).append( getDiscountAmt()).append( ",WriteOff=" ).append( getWriteOffAmt()).append( ",OverUnder=" ).append( getOverUnderAmt());

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary() {
        StringBuffer sb = new StringBuffer();

        sb.append( getDocumentNo());

        // : Total Lines = 123.00 (#1)

        sb.append( ": " ).append( Msg.translate( getCtx(),"PayAmt" )).append( "=" ).append( getPayAmt()).append( "," ).append( Msg.translate( getCtx(),"WriteOffAmt" )).append( "=" ).append( getWriteOffAmt());

        // - Description

        if( (getDescription() != null) && (getDescription().length() > 0) ) {
            sb.append( " - " ).append( getDescription());
        }

        return sb.toString();
    }    // getSummary


    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID() {
        return getCreatedBy();
    }    // getDoc_User_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt() {
        if( isReceipt()) {
            return getWriteOffAmt();
        }

        return getPayAmt();
    }    // getApprovalAmt
    
    /**
     * @return Retorna el monto del pago + el descuento + el writeoff
     */
    public BigDecimal getTotalAmt() {
    	return getPayAmt().add(getWriteOffAmt()).add(getDiscountAmt());
    }
    
    /**
     * Este método fué redefinido con el fin de evitar la validación de
     * campo actualizable que realiza el modelo luego de una corrección
     * para no permitir el cambio de tipo de pago desde la interfaz.
     */
    public void setTenderType(String tenderType) {
    	set_ValueNoCheck("TenderType", tenderType);
    }
    
    /**
     * Verifica si el pago se encuentra en alguna asignación válida del sistema.
     * @param exceptAllocIDs IDs de asignaciones que se deben ignorar para determinar
     * la condición de existencia.
     * @return Verdadero en caso de que exista al menos una asignación en estado
     * CO o CL que contenga una línea activa cuyo pago es este pago.
     */
    protected boolean isInAllocation(Integer[] exceptAllocIDs) {
    	StringBuffer sql = new StringBuffer(); 
    	sql.append(" SELECT COUNT(*) ");
    	sql.append(" FROM C_AllocationLine al ");
    	sql.append(" INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID) ");
    	sql.append(" WHERE al.IsActive = 'Y' AND ah.DocStatus IN ('CO','CL') ");
    	sql.append("   AND al.C_Payment_ID = ? ");
    	if (exceptAllocIDs.length > 0) {
    		sql.append(" AND ah.C_AllocationHdr_ID NOT IN (");
    		for (int i = 0; i < exceptAllocIDs.length; i++) {
				Integer allocID = exceptAllocIDs[i];
    			sql.append(allocID);
    			sql.append(i == exceptAllocIDs.length - 1? ")" : ",");
			}
    	}
    	int allocCount = DB.getSQLValue(get_TrxName(), sql.toString(), getC_Payment_ID());
    	return allocCount > 0;
    }

    /**
     * Verifica si el pago se encuentra en alguna asignación válida del sistema.
     * @param exceptThisAllocID ID de asignacion que se deben ignorar para determinar
     * la condición de existencia. Si el parámetro es NULL entonces no se ignora 
     * ninguna asignación.
     * @return Verdadero en caso de que exista al menos una asignación en estado
     * CO o CL que contenga una línea activa cuyo pago es este pago.
     */
    protected boolean isInAllocation(Integer exceptThisAllocID) {
    	Integer[] exceptAllocs;
    	if (exceptThisAllocID != null)
    		exceptAllocs = new Integer[] { exceptThisAllocID };
    	else 
    		exceptAllocs = new Integer[] { };
    	return isInAllocation(exceptAllocs);    	
    }

    /**
     * Verifica si el pago se encuentra en alguna asignación válida del sistema.
     * @return Verdadero en caso de que exista al menos una asignación en estado
     * CO o CL que contenga una línea activa cuyo pago es este pago.
     */
    protected boolean isInAllocation() {
    	return isInAllocation(new Integer[] { });    	
    }

    
    /**
     * ID de la asignación que intenta anular este pago. En el caso de que desde una
     * asignación se quiera anular un pago, es necesario que este pago sepa cual es la
     * asignación que lo está anulando para evitar la validación de asignaciones
     * de pagos, de modo que la asignación anuladora no se tenga en cuenta en la validación.
     */
    private Integer voiderAllocationID = null;

	/**
	 * @return the voiderAllocationID
	 */
	public Integer getVoiderAllocationID() {
		return voiderAllocationID;
	}

	/**
	 * @param voiderAllocationID the voiderAllocationID to set
	 */
	public void setVoiderAllocationID(Integer voiderAllocationID) {
		this.voiderAllocationID = voiderAllocationID;
	}
    
	
	public void setUpdateBPBalance(boolean updateBPBalance) {
		this.updateBPBalance = updateBPBalance;
	}

	public boolean isUpdateBPBalance() {
		return updateBPBalance;
	} 
	
	
	/**
	 * Operaciones luego de procesar el documento
	 */
	public boolean afterProcessDocument(String processAction, boolean status) {

		// Setear el crédito de la entidad comercial
		// Si pudo completar
		if ((getDocStatus().equals(MInvoice.DOCSTATUS_Completed)
				|| getDocStatus().equals(MInvoice.DOCSTATUS_Reversed) || getDocStatus()
				.equals(MInvoice.DOCSTATUS_Voided)) 
				&& status) {
			// Guardar el payment con el nuevo estado a fin de recalcular
			// correctamente el crédito de la entidad comercial
			this.save();
			// Si debo actualizar el saldo de la entidad comercial
			if(isUpdateBPBalance() && isConfirmAditionalWorks()){
				MBPartner bp = new MBPartner(getCtx(), getC_BPartner_ID(), get_TrxName());
				// Obtengo el manager actual
				CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
				// Actualizo el balance
				CallResult result = new CallResult();
				try{
					result = manager.afterProcessDocument(getCtx(),
							new MOrg(getCtx(), getAD_Org_ID(), get_TrxName()), bp,
							getAditionalWorkResult(), get_TrxName());
				} catch(Exception e){
					result.setMsg(e.getMessage(), true);
				}
				// Si hubo error, obtengo el mensaje y retorno inválido
				if (result.isError()) {
					log.severe(result.getMsg());
				}
			}
		}
		return true;
	}
	
	
	@Override
	public void setAuxiliarInfo(AuxiliarDTO auxDTO, boolean processed){
		auxDTO.setAuthCode(getAuthCode());
		// Monto convertido
		BigDecimal payAmt = MConversionRate.convertBase(
				getCtx(),
				getPayAmt(true), // CM adjusted
				getC_Currency_ID(), getDateAcct(), 0, getAD_Client_ID(),
				getAD_Org_ID());
		auxDTO.setAmt(payAmt);
		auxDTO.setDateTrx(getDateTrx());
		auxDTO.setDocType(MCentralAux.DOCTYPE_PaymentReceipt);
		auxDTO.setDocumentNo(getDocumentNo());
		auxDTO.setTenderType(getTenderType());
		// Signo en base al receipt
		auxDTO.setSign(isReceipt()?-1:1);
		auxDTO
				.setTransactionType(isReceipt() ? MCentralAux.TRANSACTIONTYPE_Customer
						: MCentralAux.TRANSACTIONTYPE_Vendor);
		auxDTO.setDocStatus(processed ? getDocStatus() : getDocAction());
		auxDTO.setDueDate(getDueDate());
		testAllocation();
		auxDTO.setPrepayment(!isAllocated());
		auxDTO.setReconciled(isReconciled());
		// HACK: EL matching de autorización se setea falso porque después se
		// realiza en la eliminación de transacciones
		setAuthMatch(false);
	}    
	
	
	public void setAditionalWorkResult(Map<PO, Object> aditionalWorkResult) {
		this.aditionalWorkResult = aditionalWorkResult;
	}

	public Map<PO, Object> getAditionalWorkResult() {
		return aditionalWorkResult;
	}

	public void setConfirmAditionalWorks(boolean confirmAditionalWorks) {
		this.confirmAditionalWorks = confirmAditionalWorks;
	}

	public boolean isConfirmAditionalWorks() {
		return confirmAditionalWorks;
	}
	
	private boolean validatePaymentCurrencyConvert() {
		int currecy_Client = Env.getContextAsInt( Env.getCtx(), "$C_Currency_ID" );
		if (getC_Currency_ID() != currecy_Client) {
			return (MCurrency.currencyConvert(new BigDecimal(1), currecy_Client, getC_Currency_ID(), getDateAcct(), getAD_Org_ID(), getCtx()) != null);
		}
		return true;
	}

	public void setVoidPOSJournalID(Integer voidPOSJournalID) {
		this.voidPOSJournalID = voidPOSJournalID;
	}

	public Integer getVoidPOSJournalID() {
		return voidPOSJournalID;
	}

	public void setVoidPOSJournalMustBeOpen(boolean voidPOSJournalMustBeOpen) {
		this.voidPOSJournalMustBeOpen = voidPOSJournalMustBeOpen;
	}

	public boolean isVoidPOSJournalMustBeOpen() {
		return voidPOSJournalMustBeOpen;
	}
	
	/**
	 *  CreditCard Exp  MMYY
	 *  @param delimiter / - or null
	 *  @return Exp
	 */
	public String getCreditCardExp(String delimiter)
	{
		String mm = String.valueOf(getCreditCardExpMM());
		String yy = String.valueOf(getCreditCardExpYY());

		StringBuffer retValue = new StringBuffer();
		if (mm.length() == 1)
			retValue.append("0");
		retValue.append(mm);
		//
		if (delimiter != null)
			retValue.append(delimiter);
		//
		if (yy.length() == 1)
			retValue.append("0");
		retValue.append(yy);
		//
		return (retValue.toString());
	}   //  getCreditCardExp
	
	//TODO Hernandez
	/**
	 *  Set Cash BankAccount Info
	 *
	 *  @param C_BankAccount_ID bank account
	 *  @param isReceipt true if receipt
	 * 	@param tenderType - Cash (Payment)
	 *  @return true if valid
	 */
	public boolean setBankCash (int C_BankAccount_ID, boolean isReceipt, String tenderType)
	{
		setTenderType (tenderType);
		setIsReceipt (isReceipt);
		//
		if (C_BankAccount_ID > 0)
			setBankAccountDetails(C_BankAccount_ID);
		else
		{
			setC_BankAccount_ID(C_BankAccount_ID);
		}
		//
		return true;
	}   //  setBankCash

}   // MPayment



/*
 *  @(#)MPayment.java   02.07.07
 * 
 *  Fin del fichero MPayment.java
 *  
 *  Versión 2.2
 *
 */
