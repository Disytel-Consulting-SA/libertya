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



package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MFactAcct;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Doc_Allocation extends Doc implements DocProjectSplitterInterface  {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ass
     * @param AD_Table_ID
     * @param TableName
     */

    protected Doc_Allocation( MAcctSchema[] ass,int AD_Table_ID,String TableName ) {
        super( ass );
        p_AD_Table_ID = AD_Table_ID;
        p_TableName   = TableName;
    }    // Doc_Allocation

    /** Descripción de Campos */

    private static final BigDecimal TOLERANCE = new BigDecimal( 0.02 );

    /**
     * Descripción de Método
     *
     *
     * @param rs
     *
     * @return
     */

    protected boolean loadDocumentDetails( ResultSet rs ) {
        p_vo.DocumentType = DOCTYPE_Allocation;

        try {
            p_vo.DateDoc = rs.getTimestamp( "DateTrx" );

            // Contained Objects

            p_lines = loadLines();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadDocumentDetails",e );
        }

        return false;
    }    // loadDocumentDetails

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private DocLine[] loadLines() {
        ArrayList list = new ArrayList();
        String    sql  = 
        	" SELECT al.* FROM C_AllocationLine al " +
        	" INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID)" +
        	// No se debe contabilizar ninguna línea de una OPA o RCA.
        	" WHERE al.C_AllocationHdr_ID=? AND ah.AllocationType NOT IN ('OPA','RCA')"; 

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql,m_trxName );

            pstmt.setInt( 1,getRecord_ID());

            ResultSet rs = pstmt.executeQuery();

            //

            while( rs.next()) {
                int                Line_ID = rs.getInt( "C_AllocationLine_ID" );
                DocLine_Allocation docLine = new DocLine_Allocation( p_vo.DocumentType,getRecord_ID(),Line_ID,getTrxName());

                docLine.loadAttributes( rs,p_vo );

                //

                docLine.setC_Payment_ID( rs.getInt( "C_Payment_ID" ));
                docLine.setC_CashLine_ID( rs.getInt( "C_CashLine_ID" ));
                docLine.setC_Invoice_ID( rs.getInt( "C_Invoice_ID" ));
                docLine.setC_Order_ID( rs.getInt( "C_Order_ID" ));
                docLine.setC_Invoice_Credit_ID(rs.getInt("C_Invoice_Credit_ID"));

                //

                docLine.setAmount( rs.getBigDecimal( "Amount" ));
                docLine.setDiscountAmt( rs.getBigDecimal( "DiscountAmt" ));
                docLine.setWriteOffAmt( rs.getBigDecimal( "WriteOffAmt" ));
                docLine.setOverUnderAmt( rs.getBigDecimal( "OverUnderAmt" ));

                // Get Payment Conversion Rate

                if( docLine.getC_Payment_ID() != 0 ) {
                    MPayment payment = new MPayment( getCtx(),docLine.getC_Payment_ID(),m_trxName );
                    int C_ConversionType_ID = payment.getC_ConversionType_ID();

                    docLine.setC_ConversionType_ID( C_ConversionType_ID );
                }

                //

                log.fine( docLine.toString());
                list.add( docLine );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadLines",e );
        }

        // Return Array

        DocLine[] dl = new DocLine[ list.size()];

        list.toArray( dl );

        return dl;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getBalance() {
        BigDecimal retValue = Env.ZERO;

        return retValue;
    }    // getBalance

    /**
     * Descripción de Método
     *
     *
     * @param as
     *
     * @return
     */

    public Fact createFact( MAcctSchema as ) {

        // create Fact Header

        Fact fact = new Fact( this,as,Fact.POST_Actual );

        for( int i = 0;i < p_lines.length;i++ ) {
            DocLine_Allocation line = ( DocLine_Allocation )p_lines[ i ];

            p_vo.C_BPartner_ID = line.getC_BPartner_ID();

            // CashBankTransfer - all references null and Discount/WriteOff = 0

            if( (line.getC_Payment_ID() != 0) && (line.getC_Invoice_ID() == 0) && (line.getC_Order_ID() == 0) && (line.getC_CashLine_ID() == 0) && (line.getC_BPartner_ID() == 0) && (Env.ZERO.compareTo( line.getDiscountAmt()) == 0) && (Env.ZERO.compareTo( line.getWriteOffAmt()) == 0) ) {
                continue;
            }

            // Receivables/Liability Amt

            BigDecimal invoiceAmt = line.getAmount().add( line.getDiscountAmt()).add( line.getWriteOffAmt());
            BigDecimal allocationAccounted = null;    // AR/AP balance corrected
            FactLine   fl                  = null;
            MAccount   bpAcct              = null;    // Liability/Receivables

            //

            MPayment payment = null;

            if( line.getC_Payment_ID() != 0 ) {
                payment = new MPayment( getCtx(),line.getC_Payment_ID(),m_trxName );
            }

            MInvoice invoice = null;

            if( line.getC_Invoice_ID() != 0 ) {
                invoice = new MInvoice( getCtx(),line.getC_Invoice_ID(),m_trxName );
            }

            //

            if( invoice == null ) {

                // Payment Only

                if( (line.getC_Invoice_ID() == 0) && (line.getC_Payment_ID() != 0) ) {
                    fl = fact.createLine( line,getPaymentAcct( as,line.getC_Payment_ID()),p_vo.C_Currency_ID,line.getAmount(),null );

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                } else {
                    p_vo.Error = "Cannot determine SO/PO";
                    log.log( Level.SEVERE,"createFact - " + p_vo.Error );

                    return null;
                }
            }

            // Sales Invoice

            else if( invoice.isSOTrx()) {

                // Payment/Cash    DR

                if( line.getC_Payment_ID() != 0 ) {
                    fl = fact.createLine( line,getPaymentAcct( as,line.getC_Payment_ID()),p_vo.C_Currency_ID,line.getAmount(),null );

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                } else if( line.getC_CashLine_ID() != 0 ) {
                    fl = fact.createLine( line,getCashAcct( as,line.getC_CashLine_ID()),p_vo.C_Currency_ID,line.getAmount(),null );

                    MCashLine cashLine = new MCashLine( getCtx(),line.getC_CashLine_ID(),m_trxName );

                    if( (fl != null) && (cashLine.getID() != 0) ) {
                        fl.setAD_Org_ID( cashLine.getAD_Org_ID());
                    }
                } else if(line.getC_Invoice_Credit_ID() != 0){
                	// Si es factura de clientes entonces crear una línea para la cuenta de clientes
                	MInvoice invoiceCredit = new MInvoice(getCtx(),line.getC_Invoice_Credit_ID(),m_trxName);
                	
                	// Se obtiene el tipo de documento 
            		MDocType docType = MDocType.get(getCtx(), invoiceCredit.getC_DocTypeTarget_ID());
            		if(invoiceCredit.isSOTrx()){
            			BigDecimal amountDebit;
            			BigDecimal amountCredit;
            			
            			// Si el doc base del invoice credit está basado en un abono, entonces va para el debito
            			if(docType.getDocBaseType().equalsIgnoreCase(MDocType.DOCBASETYPE_ARCreditMemo)){
            				amountDebit = line.getAmount();
            				amountCredit = null;
            			}
            			else{
            				amountDebit = null;
            				amountCredit = line.getAmount();
            			}
            			
                		// Actualizar la cuenta del cliente
                		bpAcct = getAccount( Doc.ACCTTYPE_C_Receivable,as );
                		fl = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,amountDebit,amountCredit);
                		
                		if( (fl != null) && (invoiceCredit.getID() != 0) ) {
                            fl.setAD_Org_ID( invoiceCredit.getAD_Org_ID());
                        }
                	} 
                	else{
                		// Actualizar la cuenta de los proveedores
                		bpAcct = getAccount( Doc.ACCTTYPE_V_Liability,as );
                        fl = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,line.getAmount(),null );    // payment currency
                        
                        if( (fl != null) && (invoiceCredit.getID() != 0) ) {
                            fl.setAD_Org_ID( invoiceCredit.getAD_Org_ID());
                        }
                	}
                }

                // Discount                DR

                if( Env.ZERO.compareTo( line.getDiscountAmt()) != 0 ) {
                    fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_DiscountExp,as ),p_vo.C_Currency_ID,line.getDiscountAmt(),null );

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                }

                // Write off               DR

                if( Env.ZERO.compareTo( line.getWriteOffAmt()) != 0 ) {
                    fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_WriteOff,as ),p_vo.C_Currency_ID,line.getWriteOffAmt(),null );

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                }

                // AR Invoice Amount       CR

                bpAcct = getAccount( Doc.ACCTTYPE_C_Receivable,as );
                fl     = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,null,invoiceAmt );    // payment currency

                if( fl != null ) {
                    allocationAccounted = fl.getAcctBalance().negate();
                }

                if( (fl != null) && (invoice != null) ) {
                    fl.setAD_Org_ID( invoice.getAD_Org_ID());
                }
            }

            // Purchase Invoice

            else {
            	// antes los allocation de proveedor eran negativos. Ahora no. 
            	// Jorge V- Disytel 
            	//    invoiceAmt = invoiceAmt.negate();    // allocation is negative

                // AP Invoice Amount       DR

                bpAcct = getAccount( Doc.ACCTTYPE_V_Liability,as );
                fl     = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,invoiceAmt,null );    // payment currency

                if( fl != null ) {
                    allocationAccounted = fl.getAcctBalance();
                }

                if( (fl != null) && (invoice != null) ) {
                    fl.setAD_Org_ID( invoice.getAD_Org_ID());
                }

                // Discount                CR

                if( Env.ZERO.compareTo( line.getDiscountAmt()) != 0 ) {
                    fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_DiscountRev,as ),p_vo.C_Currency_ID,null,line.getDiscountAmt().negate());

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                }

                // Write off               CR

                if( Env.ZERO.compareTo( line.getWriteOffAmt()) != 0 ) {
                    fl = fact.createLine( line,getAccount( Doc.ACCTTYPE_WriteOff,as ),p_vo.C_Currency_ID,null,line.getWriteOffAmt());

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                }

                // Payment/Cash    CR

                if( line.getC_Payment_ID() != 0 ) {
                	//line.getAmount().negate()
                    fl = fact.createLine( line,getPaymentAcct( as,line.getC_Payment_ID()),p_vo.C_Currency_ID,null,line.getAmount());

                    if( (fl != null) && (payment != null) ) {
                        fl.setAD_Org_ID( payment.getAD_Org_ID());
                    }
                } else if( line.getC_CashLine_ID() != 0 ) {
                    fl = fact.createLine( line,getCashAcct( as,line.getC_CashLine_ID()),p_vo.C_Currency_ID,null,line.getAmount());

                    MCashLine cashLine = new MCashLine( getCtx(),line.getC_CashLine_ID(),m_trxName );

                    if( (fl != null) && (cashLine.getID() != 0) ) {
                        fl.setAD_Org_ID( cashLine.getAD_Org_ID());
                    }
                } else if(line.getC_Invoice_Credit_ID() != 0){
                	// Si es factura de clientes entonces crear una línea para la cuenta de clientes
                	MInvoice invoiceCredit = new MInvoice(getCtx(),line.getC_Invoice_Credit_ID(),m_trxName);
                	// Se obtiene el tipo de documento 
            		MDocType docType = MDocType.get(getCtx(), invoiceCredit.getC_DocTypeTarget_ID());
                	if(invoiceCredit.isSOTrx()){
                		// Actualizar la cuenta del cliente
                		bpAcct = getAccount( Doc.ACCTTYPE_C_Receivable,as );
                		fl = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,null,line.getAmount());
                		
                		if( (fl != null) && (invoiceCredit.getID() != 0) ) {
                            fl.setAD_Org_ID( invoiceCredit.getAD_Org_ID());
                        }
                	} 
                	else{
                		BigDecimal amountDebit;
            			BigDecimal amountCredit;
            			
            			// Si el doc base del invoice credit está basado en un abono, entonces va para el debito
            			if(docType.getDocBaseType().equalsIgnoreCase(MDocType.DOCBASETYPE_APCreditMemo)){
            				amountDebit = null;
            				amountCredit = line.getAmount();
            			}
            			else{
            				amountDebit = line.getAmount();
            				amountCredit = null;
            			}
            			
                		// Actualizar la cuenta de los proveedores
                		bpAcct = getAccount( Doc.ACCTTYPE_V_Liability,as );
                        fl = fact.createLine( line,bpAcct,p_vo.C_Currency_ID,amountDebit,amountCredit);    // payment currency
                        
                        if( (fl != null) && (invoiceCredit.getID() != 0) ) {
                            fl.setAD_Org_ID( invoiceCredit.getAD_Org_ID());
                        }
                	}
                }
            }

            // VAT Tax Correction

            if( as.isDiscountCorrectsTax() && (invoice != null) ) {
                BigDecimal taxCorrectionAmt = line.getDiscountAmt().add( line.getWriteOffAmt());

                if( as.isDiscountCorrectsTax() && (Env.ZERO.compareTo( taxCorrectionAmt ) != 0) ) {
                    if( !createTaxCorrection( as,fact,line,getAccount( invoice.isSOTrx()
                            ?Doc.ACCTTYPE_DiscountExp
                            :Doc.ACCTTYPE_DiscountRev,as ),getAccount( Doc.ACCTTYPE_WriteOff,as ))) {
                        p_vo.Error = "Cannot create Tax correction";

                        return null;
                    }
                }
            }
            
            // SE QUITO EL SIGUIENTE CODIGO YA QUE AL INCORPORAR MULTIMONEDA, LA DIFERENCIA DE CAMBIO SE REPRESENTA EN UNA NOTA DE DEBITO/CREDITO
            
            // Realized Gain & Loss            
//            if( (invoice != null) && ( (p_vo.C_Currency_ID != as.getC_Currency_ID()    // payment allocation in foreign currency
//                    ) || (p_vo.C_Currency_ID != line.getInvoiceC_Currency_ID())))    // invoice <> payment currency
//                    {
//                if( !createRealizedGainLoss( as,fact,bpAcct,invoice.isSOTrx(),line.getC_Invoice_ID(),invoiceAmt,allocationAccounted,line.getOverUnderAmt().add( line.getDiscountAmt()))) {
//                    p_vo.Error = "Cannot create Realized Gain&Loss";
//
//                    return null;
//                }
//            }
        }    // for all lines

        // reset line info

        p_vo.C_BPartner_ID = 0;
        fact = fixFactLines(fact);
        return fact;
    }    // createFact

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param C_Payment_ID
     *
     * @return
     */

    private MAccount getPaymentAcct( MAcctSchema as,int C_Payment_ID ) {
        p_vo.C_BankAccount_ID = 0;

        // Doc.ACCTTYPE_UnallocatedCash (AR) or C_Prepayment
        // or Doc.ACCTTYPE_PaymentSelect (AP) or V_Prepayment

        int accountType = Doc.ACCTTYPE_UnallocatedCash;

        //

        String sql = "SELECT p.C_BankAccount_ID, d.DocBaseType, p.IsReceipt, p.IsPrepayment " + "FROM C_Payment p INNER JOIN C_DocType d ON (p.C_DocType_ID=d.C_DocType_ID) " + "WHERE C_Payment_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,m_trxName );
            pstmt.setInt( 1,C_Payment_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                p_vo.C_BankAccount_ID = rs.getInt( 1 );

                if( DOCTYPE_APPayment.equals( rs.getString( 2 ))) {
                    accountType = Doc.ACCTTYPE_PaymentSelect;
                }

                // Prepayment

                if( "Y".equals( rs.getString( 4 )))          // Prepayment
                {
                    if( "Y".equals( rs.getString( 3 ))) {    // Receipt
                        accountType = Doc.ACCTTYPE_C_Prepayment;
                    } else {
                        accountType = Doc.ACCTTYPE_V_Prepayment;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPaymentAcct",e );
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

        if( p_vo.C_BankAccount_ID <= 0 ) {
            log.log( Level.SEVERE,"getPaymentAcct NONE for C_Payment_ID=" + C_Payment_ID );

            return null;
        }

        return getAccount( accountType,as );
    }    // getPaymentAcct

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param C_CashLine_ID
     *
     * @return
     */

    private MAccount getCashAcct( MAcctSchema as,int C_CashLine_ID ) {
        String sql = "SELECT c.C_CashBook_ID " + "FROM C_Cash c, C_CashLine cl " + "WHERE c.C_Cash_ID=cl.C_Cash_ID AND cl.C_CashLine_ID=?";

        p_vo.C_CashBook_ID = DB.getSQLValue( null,sql,C_CashLine_ID );

        if( p_vo.C_CashBook_ID <= 0 ) {
            log.log( Level.SEVERE,"getCashAcct NONE for C_CashLine_ID=" + C_CashLine_ID );

            return null;
        }

        return getAccount( Doc.ACCTTYPE_CashTransfer,as );
    }    // getCashAcct

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param fact
     * @param acct
     * @param SOTrx
     * @param C_Invoice_ID
     * @param allocationSource
     * @param allocationAccounted
     * @param differenceAmt
     *
     * @return
     */

    private boolean createRealizedGainLoss( MAcctSchema as,Fact fact,MAccount acct,boolean SOTrx,int C_Invoice_ID,BigDecimal allocationSource,BigDecimal allocationAccounted,BigDecimal differenceAmt ) {

        // TODO: Does not work with Split Payment Schedule

        BigDecimal invoiceSource    = null;
        BigDecimal invoiceAccounted = null;

        //

        String sql = "SELECT " + ( SOTrx
                                   ?"SUM(AmtSourceDr), SUM(AmtAcctDr)"                               // so
                                   :"SUM(AmtSourceDr), SUM(AmtAcctCr)" )                             // po
                                   + " FROM Fact_Acct " + "WHERE AD_Table_ID=318 AND Record_ID=?"    // Invoice
                                   + " AND C_AcctSchema_ID=?" + " AND PostingType='A'";

        // AND C_Currency_ID=102

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,m_trxName );
            pstmt.setInt( 1,C_Invoice_ID );
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                invoiceSource    = rs.getBigDecimal( 1 );
                invoiceAccounted = rs.getBigDecimal( 2 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createRealizedGainLoss",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Requires that Invoice is Posted

        if( (invoiceSource == null) || (invoiceAccounted == null) ) {
            log.warning( "createRealizedGainLoss - Invoice bot posted yet" );

            return false;
        }

        log.fine( "createRealizedGainLoss - Invoice=" + invoiceSource + "/" + invoiceAccounted + " - Allocation=" + allocationSource + "/" + allocationAccounted );

        BigDecimal acctDifference = acctDifference = invoiceAccounted.subtract( allocationAccounted );    // gain is negative

        // Full Payment

        if( (differenceAmt.compareTo( Env.ZERO ) == 0) || (allocationSource.compareTo( invoiceSource ) == 0) ) {
            log.fine( "createRealizedGainLoss (full) = " + acctDifference );
        } else    // Partial Payment
        {

            // percent of total payment

            double percent = allocationSource.doubleValue() / ( allocationSource.doubleValue() + differenceAmt.doubleValue());

            acctDifference = allocationSource.multiply( new BigDecimal( percent ));

            // ignore Tolerance

            if( acctDifference.abs().compareTo( TOLERANCE ) < 0 ) {
                acctDifference = Env.ZERO;
            }

            // Round

            int precision = as.getStdPrecision();

            if( acctDifference.scale() > precision ) {
                acctDifference = acctDifference.setScale( precision,BigDecimal.ROUND_HALF_UP );
            }

            //

            log.fine( "createRealizedGainLoss (partial) = " + acctDifference + " - Percent=" + percent + ", DifferenceAmt=" + differenceAmt );
        }

        if( Env.ZERO.compareTo( acctDifference ) == 0 ) {
            log.fine( "createRealizedGainLoss - No Difference" );

            return true;
        }

        MAccount gain = MAccount.get( as.getCtx(),as.getAcctSchemaDefault().getRealizedGain_Acct());
        MAccount loss = MAccount.get( as.getCtx(),as.getAcctSchemaDefault().getRealizedLoss_Acct());

        //

        if( SOTrx ) {
            fact.createLine( null,loss,gain,as.getC_Currency_ID(),acctDifference );
            fact.createLine( null,acct,as.getC_Currency_ID(),acctDifference.negate());
        } else {
            fact.createLine( null,acct,as.getC_Currency_ID(),acctDifference );
            fact.createLine( null,loss,gain,as.getC_Currency_ID(),acctDifference.negate());
        }

        return true;
    }    // createRealizedGainLoss

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param fact
     * @param line
     * @param DiscountAccount
     * @param WriteOffAccoint
     *
     * @return
     */

    private boolean createTaxCorrection( MAcctSchema as,Fact fact,DocLine_Allocation line,MAccount DiscountAccount,MAccount WriteOffAccoint ) {
        log.info( "createTaxCorrection - " + line );

        Doc_AllocationTax tax = new Doc_AllocationTax( DiscountAccount,line.getDiscountAmt(),WriteOffAccoint,line.getWriteOffAmt());

        // Get Source Amounts with account

        String sql = "SELECT * " + "FROM Fact_Acct " + "WHERE AD_Table_ID=318 AND Record_ID=?"    // Invoice
                     + " AND C_AcctSchema_ID=?" + " AND Line_ID IS NULL";    // header lines like tax or total
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,m_trxName );
            pstmt.setInt( 1,line.getC_Invoice_ID());
            pstmt.setInt( 2,as.getC_AcctSchema_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                tax.addInvoiceFact( new MFactAcct( getCtx(),rs,fact.get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"createTaxCorrection",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Invoice Not posted

        if( tax.getLineCount() == 0 ) {
            log.warning( "createTaxCorrection - Invoice not posted yet - " + line );

            return false;
        }

        // size = 1 if no tax

        if( tax.getLineCount() < 2 ) {
            return true;
        }

        return tax.createEntries( as,fact,line );
    }    // createTaxCorrection

	@Override
	public String applyCustomSettings(Fact fact, int index) {
    	DocProjectSplitter projectSplitter = new DocProjectSplitter(this);
    	if (!projectSplitter.splitLinesByProject(fact))
    		return STATUS_Error;
    	
    	// Realizar nuevamente el balanceo
    	doBalancing(index, projectSplitter.getLastProjectID());
    	
    	return STATUS_Posted;		
	}

    /** Implementación de DocProjectSplitterInterface */
	public HashMap<Integer, BigDecimal> getProjectPercentageQuery(FactLine factLine) {
		
		// Obtener la allocationLine
		MAllocationLine allocLine = new MAllocationLine(factLine.getCtx(), factLine.getLine_ID(), factLine.get_TrxName());  

		// Devolver los valores segun la invoice
		return Doc_Invoice.calculateProjectPercentageQuery(allocLine.getC_Invoice_ID(), factLine.get_TrxName(), getSchemaCurrency(factLine));
	}

    /** Implementación de DocProjectSplitterInterface */
	public int getProjectsInLinesQuery(FactLine factLine) 
	{
		// Obtener la allocationLine
		MAllocationLine allocLine = new MAllocationLine(factLine.getCtx(), factLine.getLine_ID(), factLine.get_TrxName());  
		
		// Si la allocation no tiene InvoiceID es porque es un pago adelantado... 
		if (allocLine.getC_Invoice_ID() == 0) 
			return 1;
		
		return DB.getSQLValue(factLine.get_TrxName(), "SELECT COUNT(DISTINCT(COALESCE(C_Project_ID,0))) FROM C_InvoiceLine WHERE C_Invoice_ID = ?", allocLine.getC_Invoice_ID());
	}
    
	/** Implementación de DocProjectSplitterInterface */
	public boolean requiresSplit(FactLine factLine) {
		return true;
	}

	@Override
	protected String loadDocumentDetails() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Corrige las lineas de contabilidad que usan para debe y haber la misma cuenta contable.
	 * Por ejemplo, las lineas de retenciones y NC.
	 * La estrategia inicial es recorrer de a pares todas las lineas generadas y si encuentro dos lineas
	 * consecutivas que utilizan la misma cuenta y tienen el mismo monto, las saco.
	 */
	protected Fact fixFactLines(Fact fact){
		Fact corrected = new Fact(this, fact.getAcctSchema(), Fact.POST_Actual );
		FactLine[] lines = fact.getLines();
		//Chequeo si la cantidad de asientos es par, si no arrojo error.
		if((lines.length % 2) != 0) {
			p_vo.Error = "Error fixing fact. Odd number of lines";
            log.log( Level.SEVERE,"fixingFact - " + p_vo.Error );
            return null;
        }
		for(int i=0; i < lines.length; i+=2) {
			//Si tienen la misma cuenta contable
			if(lines[i].getAccount().getAccount_ID() == lines[i+1].getAccount().getAccount_ID()) {
				//Además tienen el mismo monto una en debe y la otra en haber. Chequeo en ambas direcciones
				if(lines[i].getDocLine().getAmtAcctDr().compareTo(lines[i+1].getDocLine().getAmtAcctCr()) == 0
						|| lines[i].getDocLine().getAmtAcctCr().compareTo(lines[i+1].getDocLine().getAmtAcctDr()) == 0) {
					continue;
				}
			} else {
				corrected.add(lines[i]);
				corrected.add(lines[i+1]);
			}
		}
		return corrected;
	}
}    // Doc_Allocation


/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

class Doc_AllocationTax {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DiscountAccount
     * @param DiscountAmt
     * @param WriteOffAccount
     * @param WriteOffAmt
     */

    public Doc_AllocationTax( MAccount DiscountAccount,BigDecimal DiscountAmt,MAccount WriteOffAccount,BigDecimal WriteOffAmt ) {
        m_DiscountAccount = DiscountAccount;
        m_DiscountAmt     = DiscountAmt;
        m_WriteOffAccount = WriteOffAccount;
        m_WriteOffAmt     = WriteOffAmt;
    }    // Doc_AllocationTax

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private MAccount m_DiscountAccount;

    /** Descripción de Campos */

    private BigDecimal m_DiscountAmt;

    /** Descripción de Campos */

    private MAccount m_WriteOffAccount;

    /** Descripción de Campos */

    private BigDecimal m_WriteOffAmt;

    /** Descripción de Campos */

    private ArrayList m_facts = new ArrayList();

    /** Descripción de Campos */

    private int m_totalIndex = 0;

    /**
     * Descripción de Método
     *
     *
     * @param fact
     */

    public void addInvoiceFact( MFactAcct fact ) {
        m_facts.add( fact );
    }    // addInvoiceLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getLineCount() {
        return m_facts.size();
    }    // getLineCount

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param fact
     * @param line
     *
     * @return
     */

    public boolean createEntries( MAcctSchema as,Fact fact,DocLine line ) {

        // get total index (the Receivables/Liabilities line)

        BigDecimal total = Env.ZERO;

        for( int i = 0;i < m_facts.size();i++ ) {
            MFactAcct factAcct = ( MFactAcct )m_facts.get( i );

            if( factAcct.getAmtSourceDr().compareTo( total ) > 0 ) {
                total        = factAcct.getAmtSourceDr();
                m_totalIndex = i;
            }

            if( factAcct.getAmtSourceCr().compareTo( total ) > 0 ) {
                total        = factAcct.getAmtSourceCr();
                m_totalIndex = i;
            }
        }

        MFactAcct factAcct = ( MFactAcct )m_facts.get( m_totalIndex );

        log.info( "createEntries - Total Invoice = " + total + " - " + factAcct );

        int precision = as.getStdPrecision();

        for( int i = 0;i < m_facts.size();i++ ) {

            // No Tax Line

            if( i == m_totalIndex ) {
                continue;
            }

            factAcct = ( MFactAcct )m_facts.get( i );
            log.info( "createEntries " + i + ": " + factAcct );

            // Create Tax Account

            MAccount taxAcct = factAcct.getMAccount();

            if( (taxAcct == null) || (taxAcct.getID() == 0) ) {
                log.severe( "createEntries - Tax Account not found/created" );

                return false;
            }

            // Discount Amount

            if( Env.ZERO.compareTo( m_DiscountAmt ) != 0 ) {

                // Original Tax is DR - need to correct it CR

                if( Env.ZERO.compareTo( factAcct.getAmtSourceDr()) != 0 ) {
                    BigDecimal amount = calcAmount( factAcct.getAmtSourceDr(),total,m_DiscountAmt,precision );

                    if( amount.compareTo( Env.ZERO ) != 0 ) {
                        fact.createLine( line,m_DiscountAccount,as.getC_Currency_ID(),amount,null );
                        fact.createLine( line,taxAcct,as.getC_Currency_ID(),null,amount );
                    }
                }

                // Original Tax is CR - need to correct it DR

                else {
                    BigDecimal amount = calcAmount( factAcct.getAmtSourceCr(),total,m_DiscountAmt,precision );

                    if( amount.compareTo( Env.ZERO ) != 0 ) {
                        fact.createLine( line,taxAcct,as.getC_Currency_ID(),amount,null );
                        fact.createLine( line,m_DiscountAccount,as.getC_Currency_ID(),null,amount );
                    }
                }
            }    // Discount

            // WriteOff Amount

            if( Env.ZERO.compareTo( m_WriteOffAmt ) != 0 ) {

                // Original Tax is DR - need to correct it CR

                if( Env.ZERO.compareTo( factAcct.getAmtSourceDr()) != 0 ) {
                    BigDecimal amount = calcAmount( factAcct.getAmtSourceDr(),total,m_WriteOffAmt,precision );

                    if( amount.compareTo( Env.ZERO ) != 0 ) {
                        fact.createLine( line,m_WriteOffAccount,as.getC_Currency_ID(),amount,null );
                        fact.createLine( line,taxAcct,as.getC_Currency_ID(),null,amount );
                    }
                }

                // Original Tax is CR - need to correct it DR

                else {
                    BigDecimal amount = calcAmount( factAcct.getAmtSourceCr(),total,m_WriteOffAmt,precision );

                    if( amount.compareTo( Env.ZERO ) != 0 ) {
                        fact.createLine( line,taxAcct,as.getC_Currency_ID(),amount,null );
                        fact.createLine( line,m_WriteOffAccount,as.getC_Currency_ID(),null,amount );
                    }
                }
            }    // WriteOff
        }        // for all lines

        return true;
    }    // createEntries

    /**
     * Descripción de Método
     *
     *
     * @param tax
     * @param total
     * @param amt
     * @param precision
     *
     * @return
     */

    private BigDecimal calcAmount( BigDecimal tax,BigDecimal total,BigDecimal amt,int precision ) {
        log.fine( "calcAmount - Tax=" + tax + " / Total=" + total + " * Amt=" + amt );

        if( (tax.compareTo( Env.ZERO ) == 0) || (total.compareTo( Env.ZERO ) == 0) || (amt.compareTo( Env.ZERO ) == 0) ) {
            return Env.ZERO;
        }

        BigDecimal retValue = tax.multiply( amt ).divide( total,precision,BigDecimal.ROUND_HALF_UP );

        log.fine( "calcAmount - Result=" + retValue );

        return retValue;
    }    // calcAmount
}    // Doc_AllocationTax



/*
 *  @(#)Doc_Allocation.java   24.03.06
 * 
 *  Fin del fichero Doc_Allocation.java
 *  
 *  Versión 2.2
 *
 */
