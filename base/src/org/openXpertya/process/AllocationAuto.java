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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPaySelectionCheck;
import org.openXpertya.model.MPaySelectionLine;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AllocationAuto extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_BP_Group_ID = 0;

    /** Descripción de Campos */

    private int p_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private boolean p_AllocateOldest = true;

    /** Descripción de Campos */

    private String p_APAR = "A";

    /** Descripción de Campos */

    private static String ONLY_AP = "P";

    /** Descripción de Campos */

    private static String ONLY_AR = "R";

    /** Descripción de Campos */

    private MPayment[] m_payments = null;

    /** Descripción de Campos */

    private MInvoice[] m_invoices = null;

    /** Descripción de Campos */

    private MAllocationHdr m_allocation = null;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            log.fine( "prepare - " + para[ i ] );

            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_BP_Group_ID" )) {
                p_C_BP_Group_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "C_BPartner_ID" )) {
                p_C_BPartner_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AllocateOldest" )) {
                p_AllocateOldest = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "APAR" )) {
                p_APAR = ( String )para[ i ].getParameter();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
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
        log.info( "doIt - C_BP_Group_ID=" + p_C_BP_Group_ID + ", C_BPartner_ID=" + p_C_BPartner_ID + ", Oldest=" + p_AllocateOldest );

        int countBP    = 0;
        int countAlloc = 0;

        if( p_C_BPartner_ID != 0 ) {
            countAlloc = allocateBP( p_C_BPartner_ID );

            if( countAlloc > 0 ) {
                countBP++;
            }
        } else if( p_C_BP_Group_ID != 0 ) {
            String sql = "SELECT C_BPartner_ID FROM C_BPartner WHERE C_BP_Group_ID=? ORDER BY Value";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,p_C_BP_Group_ID );

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    int C_BPartner_ID = rs.getInt( 1 );
                    int count         = allocateBP( C_BPartner_ID );

                    if( count > 0 ) {
                        countBP++;
                        countAlloc += count;
                    }
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"doIt",e );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception e ) {
                pstmt = null;
            }
        } else {
            String sql = "SELECT C_BPartner_ID FROM C_BPartner WHERE AD_Client_ID=? ORDER BY Value";
            PreparedStatement pstmt = null;

            try {
                pstmt = DB.prepareStatement( sql,get_TrxName());
                pstmt.setInt( 1,Env.getAD_Client_ID( getCtx()));

                ResultSet rs = pstmt.executeQuery();

                while( rs.next()) {
                    int C_BPartner_ID = rs.getInt( 1 );
                    int count         = allocateBP( C_BPartner_ID );

                    if( count > 0 ) {
                        countBP++;
                        countAlloc += count;
                    }
                }

                rs.close();
                pstmt.close();
                pstmt = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"doIt",e );
            }

            try {
                if( pstmt != null ) {
                    pstmt.close();
                }

                pstmt = null;
            } catch( Exception e ) {
                pstmt = null;
            }
        }

        //

        return "@Created@ #" + countBP + "/" + countAlloc;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     *
     * @throws Exception
     */

    private int allocateBP( int C_BPartner_ID ) throws Exception {
        getPayments( C_BPartner_ID );
        getInvoices( C_BPartner_ID );
        log.info( "allocate (1) - C_BPartner_ID=" + C_BPartner_ID + " - #Payments=" + m_payments.length + ", #Invoices=" + m_invoices.length );

        if( m_payments.length + m_invoices.length < 2 ) {
            return 0;
        }

        // Payment Info - Invoice or Pay Selection

        int count = allocateBPPaymentWithInfo();

        if( count != 0 ) {
            getPayments( C_BPartner_ID );    // for next
            getInvoices( C_BPartner_ID );
            log.info( "allocate (2) - C_BPartner_ID=" + C_BPartner_ID + " - #Payments=" + m_payments.length + ", #Invoices=" + m_invoices.length );

            if( m_payments.length + m_invoices.length < 2 ) {
                return count;
            }
        }

        // All

        int newCount = allocateBPartnerAll();

        if( newCount != 0 ) {
            count += newCount;
            getPayments( C_BPartner_ID );    // for next
            getInvoices( C_BPartner_ID );
            processAllocation();
            log.info( "allocate (3) - C_BPartner_ID=" + C_BPartner_ID + " - #Payments=" + m_payments.length + ", #Invoices=" + m_invoices.length );

            if( m_payments.length + m_invoices.length < 2 ) {
                return count;
            }
        }

        // One:One

        newCount = allocateBPOneToOne();

        if( newCount != 0 ) {
            count += newCount;
            getPayments( C_BPartner_ID );    // for next
            getInvoices( C_BPartner_ID );
            processAllocation();
            log.info( "allocate (4) - C_BPartner_ID=" + C_BPartner_ID + " - #Payments=" + m_payments.length + ", #Invoices=" + m_invoices.length );

            if( m_payments.length + m_invoices.length < 2 ) {
                return count;
            }
        }

        // Oldest First

        if( p_AllocateOldest ) {
            newCount = allocateBPOldestFirst();

            if( newCount != 0 ) {
                count += newCount;
                getPayments( C_BPartner_ID );    // for next
                getInvoices( C_BPartner_ID );
                processAllocation();
                log.info( "allocate (5) - C_BPartner_ID=" + C_BPartner_ID + " - #Payments=" + m_payments.length + ", #Invoices=" + m_invoices.length );

                if( m_payments.length + m_invoices.length < 2 ) {
                    return count;
                }
            }
        }

        // Other, e.g.
        // Allocation if "close" % and $

        return count;
    }    // alloc

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     */

    private MPayment[] getPayments( int C_BPartner_ID ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Payment " + "WHERE IsAllocated='N' AND Processed='Y' AND C_BPartner_ID=?" + " AND IsPrepayment='N' AND C_Charge_ID IS NULL ";

        if( ONLY_AP.equals( p_APAR )) {
            sql += "AND IsReceipt='N' ";
        } else if( ONLY_AR.equals( p_APAR )) {
            sql += "AND IsReceipt='Y' ";
        }

        sql += "ORDER BY DateTrx";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MPayment   payment   = new MPayment( getCtx(),rs,get_TrxName());
                BigDecimal allocated = payment.getAllocatedAmt();

                if( (allocated != null) && (allocated.compareTo( payment.getPayAmt()) == 0) ) {
                    payment.setIsAllocated( true );
                    payment.save();
                } else {
                    list.add( payment );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPayments",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_payments = new MPayment[ list.size()];
        list.toArray( m_payments );

        return m_payments;
    }    // getPayments

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     *
     * @return
     */

    private MInvoice[] getInvoices( int C_BPartner_ID ) {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_Invoice " + "WHERE IsPaid='N' AND Processed='Y' AND C_BPartner_ID=? ";

        if( ONLY_AP.equals( p_APAR )) {
            sql += "AND IsSOTrx='N' ";
        } else if( ONLY_AR.equals( p_APAR )) {
            sql += "AND IsSOTrx='Y' ";
        }

        sql += "ORDER BY DateInvoiced";;

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MInvoice invoice = new MInvoice( getCtx(),rs,get_TrxName());

                if( invoice.getOpenAmt( false,null ).compareTo( Env.ZERO ) == 0 ) {
                    invoice.setIsPaid( true );
                    invoice.save();
                } else {
                    list.add( invoice );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getInvoicess",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        m_invoices = new MInvoice[ list.size()];
        list.toArray( m_invoices );

        return m_invoices;
    }    // getInvoices

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private int allocateBPPaymentWithInfo() {
        int count = 0;

        // ****  See if there is a direct link (Invoice or Pay Selection)

        for( int p = 0;p < m_payments.length;p++ ) {
            MPayment payment = m_payments[ p ];

            if( payment.isAllocated()) {
                continue;
            }

            BigDecimal allocatedAmt = payment.getAllocatedAmt();

            log.info( "allocatePaymentWithInfo - " + payment + ", Allocated=" + allocatedAmt );

            if( (allocatedAmt != null) && (allocatedAmt.compareTo( Env.ZERO ) != 0) ) {
                continue;
            }

            BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

            if( !payment.isReceipt()) {
                availableAmt = availableAmt.negate();
            }

            log.fine( "allocatePaymentWithInfo - Available=" + availableAmt );

            //

            if( payment.getC_Invoice_ID() != 0 ) {
                for( int i = 0;i < m_invoices.length;i++ ) {
                    MInvoice invoice = m_invoices[ i ];

                    if( invoice.isPaid()) {
                        continue;
                    }

                    // log.fine("allocateIndividualPayments - " + invoice);

                    if( payment.getC_Invoice_ID() == invoice.getC_Invoice_ID()) {
                        if( payment.getC_Currency_ID() == invoice.getC_Currency_ID()) {
                            BigDecimal openAmt = invoice.getOpenAmt( true,null );

                            if( !invoice.isSOTrx()) {
                                openAmt = openAmt.negate();
                            }

                            log.fine( "allocatePaymentWithInfo - " + invoice + ", Open=" + openAmt );

                            // With Discount, etc.

                            if( availableAmt.compareTo( openAmt ) == 0 ) {
                                if( payment.allocateIt()) {
                                    addLog( 0,payment.getDateAcct(),openAmt,payment.getDocumentNo() + " [1]" );
                                    count++;
                                }

                                break;
                            }
                        } else    // Mixed Currency
                        {}
                    }             // invoice found
                }                 // for all invoices
            }                     // payment has invoice
                    else          // No direct invoice
                    {
                MPaySelectionCheck psCheck = MPaySelectionCheck.getOfPayment( getCtx(),payment.getC_Payment_ID(),get_TrxName());

                if( psCheck == null ) {
                    continue;
                }

                //

                BigDecimal          totalInvoice = Env.ZERO;
                MPaySelectionLine[] psLines      = psCheck.getPaySelectionLines( false );

                for( int i = 0;i < psLines.length;i++ ) {
                    MPaySelectionLine line    = psLines[ i ];
                    MInvoice          invoice = line.getInvoice();

                    if( payment.getC_Currency_ID() == invoice.getC_Currency_ID()) {
                        BigDecimal invoiceAmt = invoice.getOpenAmt( true,null );
                        BigDecimal overUnder = line.getOpenAmt().subtract( line.getPayAmt()).subtract( line.getDiscountAmt()).subtract( line.getDifferenceAmt());

                        invoiceAmt = invoiceAmt.subtract( line.getDiscountAmt()).subtract( line.getDifferenceAmt()).subtract( overUnder );

                        if( !invoice.isSOTrx()) {
                            invoiceAmt = invoiceAmt.negate();
                        }

                        log.fine( "allocatePaymentWithInfo - " + invoice + ", Invoice=" + invoiceAmt );
                        totalInvoice = totalInvoice.add( invoiceAmt );
                    } else    // Multi-Currency
                    {}
                }

                if( availableAmt.compareTo( totalInvoice ) == 0 ) {
                    if( payment.allocateIt()) {
                        addLog( 0,payment.getDateAcct(),availableAmt,payment.getDocumentNo() + " [n]" );
                        count++;
                    }
                }
            }    // No direct invoice
        }

        // ****  See if there is a direct link

        return count;
    }    // allocateIndividualPayments

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private int allocateBPOneToOne() throws Exception {
        int count = 0;

        for( int p = 0;p < m_payments.length;p++ ) {
            MPayment payment = m_payments[ p ];

            if( payment.isAllocated()) {
                continue;
            }

            BigDecimal allocatedAmt = payment.getAllocatedAmt();

            log.info( "allocateOneToOne - " + payment + ", Allocated=" + allocatedAmt );

            if( (allocatedAmt != null) && (allocatedAmt.compareTo( Env.ZERO ) != 0) ) {
                continue;
            }

            BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

            if( !payment.isReceipt()) {
                availableAmt = availableAmt.negate();
            }

            log.fine( "allocateOneToOne - Available=" + availableAmt );

            for( int i = 0;i < m_invoices.length;i++ ) {
                MInvoice invoice = m_invoices[ i ];

                if( (invoice == null) || invoice.isPaid()) {
                    continue;
                }

                if( payment.getC_Currency_ID() == invoice.getC_Currency_ID()) {

                    // log.fine("allocateBPartnerAll - " + invoice);

                    BigDecimal openAmt = invoice.getOpenAmt( true,null );

                    if( !invoice.isSOTrx()) {
                        openAmt = openAmt.negate();
                    }

                    BigDecimal difference = availableAmt.subtract( openAmt ).abs();

                    log.fine( "allocateOneToOne - " + invoice + ", Open=" + openAmt + " - Difference=" + difference );

                    if( difference.compareTo( Env.ZERO ) == 0 ) {
                        Timestamp dateAcct = payment.getDateAcct();

                        if( invoice.getDateAcct().after( dateAcct )) {
                            dateAcct = invoice.getDateAcct();
                        }

                        if( !createAllocation( payment.getC_Currency_ID(),"1:1 (" + availableAmt + ")",dateAcct,availableAmt,null,null,null,invoice.getC_BPartner_ID(),payment.getC_Payment_ID(),invoice.getC_Invoice_ID())) {
                            throw new ErrorOXPSystem( "Cannot create Allocation" );
                        }

                        processAllocation();
                        count++;
                        m_invoices[ i ] = null;    // remove invoice
                        m_payments[ p ] = null;
                        payment         = null;

                        break;
                    }
                } else                             // Multi-Currency
                {}
            }                                      // for all invoices
        }                                          // for all payments

        return count;
    }    // allocateOneToOne

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private int allocateBPartnerAll() throws Exception {
        int       C_Currency_ID = MClient.get( getCtx()).getC_Currency_ID();
        Timestamp dateAcct      = null;

        // Payments

        BigDecimal totalPayments = Env.ZERO;

        for( int p = 0;p < m_payments.length;p++ ) {
            MPayment payment = m_payments[ p ];

            if( payment.isAllocated()) {
                continue;
            }

            BigDecimal allocatedAmt = payment.getAllocatedAmt();

            // log.info("allocateBPartnerAll - " + payment + ", Allocated=" + allocatedAmt);

            if( (allocatedAmt != null) && (allocatedAmt.compareTo( Env.ZERO ) != 0) ) {
                continue;
            }

            BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

            if( !payment.isReceipt()) {
                availableAmt = availableAmt.negate();
            }

            // Foreign currency

            if( payment.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            // log.fine("allocateBPartnerAll - Available=" + availableAmt);

            if( (dateAcct == null) || payment.getDateAcct().after( dateAcct )) {
                dateAcct = payment.getDateAcct();
            }

            totalPayments = totalPayments.add( availableAmt );
        }

        // Invoices

        BigDecimal totalInvoices = Env.ZERO;

        for( int i = 0;i < m_invoices.length;i++ ) {
            MInvoice invoice = m_invoices[ i ];

            if( invoice.isPaid()) {
                continue;
            }

            // log.info("allocateBPartnerAll - " + invoice);

            BigDecimal openAmt = invoice.getOpenAmt( true,null );

            if( !invoice.isSOTrx()) {
                openAmt = openAmt.negate();
            }

            // Foreign currency

            if( invoice.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            // log.fine("allocateBPartnerAll - Open=" + openAmt);

            if( (dateAcct == null) || invoice.getDateAcct().after( dateAcct )) {
                dateAcct = invoice.getDateAcct();
            }

            totalInvoices = totalInvoices.add( openAmt );
        }

        BigDecimal difference = totalInvoices.subtract( totalPayments );

        log.info( "allocateBPartnerAll = Invoices=" + totalInvoices + " - Payments=" + totalPayments + " = Difference=" + difference );

        if( difference.compareTo( Env.ZERO ) == 0 ) {
            for( int p = 0;p < m_payments.length;p++ ) {
                MPayment payment = m_payments[ p ];

                if( payment.isAllocated()) {
                    continue;
                }

                BigDecimal allocatedAmt = payment.getAllocatedAmt();

                if( (allocatedAmt != null) && (allocatedAmt.compareTo( Env.ZERO ) != 0) ) {
                    continue;
                }

                BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

                if( !payment.isReceipt()) {
                    availableAmt = availableAmt.negate();
                }

                // Foreign currency

                if( payment.getC_Currency_ID() != C_Currency_ID ) {
                    continue;
                }

                if( !createAllocation( C_Currency_ID,"BP All",dateAcct,availableAmt,null,null,null,payment.getC_BPartner_ID(),payment.getC_Payment_ID(),0 )) {
                    throw new ErrorOXPSystem( "Cannot create Allocation" );
                }
            }    // for all payments

            //

            for( int i = 0;i < m_invoices.length;i++ ) {
                MInvoice invoice = m_invoices[ i ];

                if( invoice.isPaid()) {
                    continue;
                }

                BigDecimal openAmt = invoice.getOpenAmt( true,null );

                if( !invoice.isSOTrx()) {
                    openAmt = openAmt.negate();
                }

                // Foreign currency

                if( invoice.getC_Currency_ID() != C_Currency_ID ) {
                    continue;
                }

                if( !createAllocation( C_Currency_ID,"BP All",dateAcct,openAmt,null,null,null,invoice.getC_BPartner_ID(),0,invoice.getC_Invoice_ID())) {
                    throw new ErrorOXPSystem( "Cannot create Allocation" );
                }
            }    // for all invoices

            processAllocation();

            return 1;
        }        // Difference OK

        return 0;
    }    // allocateBPartnerAll

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    private int allocateBPOldestFirst() throws Exception {
        int       C_Currency_ID = MClient.get( getCtx()).getC_Currency_ID();
        Timestamp dateAcct      = null;

        // Payments

        BigDecimal totalPayments = Env.ZERO;

        for( int p = 0;p < m_payments.length;p++ ) {
            MPayment payment = m_payments[ p ];

            if( payment.isAllocated()) {
                continue;
            }

            if( payment.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            BigDecimal allocatedAmt = payment.getAllocatedAmt();

            log.info( "allocateBPOldestFirst - " + payment + ", Allocated=" + allocatedAmt );

            BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

            if( !payment.isReceipt()) {
                availableAmt = availableAmt.negate();
            }

            log.fine( "allocateBPOldestFirst - Available=" + availableAmt );

            if( (dateAcct == null) || payment.getDateAcct().after( dateAcct )) {
                dateAcct = payment.getDateAcct();
            }

            totalPayments = totalPayments.add( availableAmt );
        }

        // Invoices

        BigDecimal totalInvoices = Env.ZERO;

        for( int i = 0;i < m_invoices.length;i++ ) {
            MInvoice invoice = m_invoices[ i ];

            if( invoice.isPaid()) {
                continue;
            }

            if( invoice.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            BigDecimal openAmt = invoice.getOpenAmt( true,null );

            log.fine( "allocateBPOldestFirst - " + invoice );

            if( !invoice.isSOTrx()) {
                openAmt = openAmt.negate();
            }

            // Foreign currency

            log.fine( "allocateBPOldestFirst - Open=" + openAmt );

            if( (dateAcct == null) || invoice.getDateAcct().after( dateAcct )) {
                dateAcct = invoice.getDateAcct();
            }

            totalInvoices = totalInvoices.add( openAmt );
        }

        // must be either AP or AR balance

        if( totalInvoices.signum() != totalPayments.signum()) {
            log.fine( "allocateBPartnerAll - Signum - Invoices=" + totalInvoices.signum() + " <> Payments=" + totalPayments.signum());

            return 0;
        }

        BigDecimal difference = totalInvoices.subtract( totalPayments );
        BigDecimal maxAmt     = totalInvoices.abs().min( totalPayments.abs());

        if( totalInvoices.signum() < 0 ) {
            maxAmt = maxAmt.negate();
        }

        log.info( "allocateBPartnerAll = Invoices=" + totalInvoices + " - Payments=" + totalPayments + " = Difference=" + difference + " - Max=" + maxAmt );

        // Allocate Payments up to max

        BigDecimal allocatedPayments = Env.ZERO;

        for( int p = 0;p < m_payments.length;p++ ) {
            MPayment payment = m_payments[ p ];

            if( payment.isAllocated()) {
                continue;
            }

            if( payment.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            BigDecimal allocatedAmt = payment.getAllocatedAmt();

            if( (allocatedAmt != null) && (allocatedAmt.compareTo( Env.ZERO ) != 0) ) {
                continue;
            }

            BigDecimal availableAmt = payment.getPayAmt().add( payment.getDiscountAmt()).add( payment.getWriteOffAmt()).add( payment.getOverUnderAmt());

            if( !payment.isReceipt()) {
                availableAmt = availableAmt.negate();
            }

            allocatedPayments = allocatedPayments.add( availableAmt );

            if( ( (totalInvoices.signum() > 0) && (allocatedPayments.compareTo( maxAmt ) > 0) ) || ( (totalInvoices.signum() < 0) && (allocatedPayments.compareTo( maxAmt ) < 0) ) ) {
                BigDecimal diff = allocatedPayments.subtract( maxAmt );

                availableAmt      = availableAmt.subtract( diff );
                allocatedPayments = allocatedPayments.subtract( diff );
            }

            log.fine( "allocateBPOldestFirst - Payment Allocated=" + availableAmt );

            if( !createAllocation( C_Currency_ID,"BP Oldest (" + difference.abs() + ")",dateAcct,availableAmt,null,null,null,payment.getC_BPartner_ID(),payment.getC_Payment_ID(),0 )) {
                throw new ErrorOXPSystem( "Cannot create Allocation" );
            }

            if( allocatedPayments.compareTo( maxAmt ) == 0 ) {
                break;
            }
        }    // for all payments

        // Allocated Invoices up to max

        BigDecimal allocatedInvoices = Env.ZERO;

        for( int i = 0;i < m_invoices.length;i++ ) {
            MInvoice invoice = m_invoices[ i ];

            if( invoice.isPaid()) {
                continue;
            }

            if( invoice.getC_Currency_ID() != C_Currency_ID ) {
                continue;
            }

            BigDecimal openAmt = invoice.getOpenAmt( true,null );

            if( !invoice.isSOTrx()) {
                openAmt = openAmt.negate();
            }

            allocatedInvoices = allocatedInvoices.add( openAmt );

            if( ( (totalInvoices.signum() > 0) && (allocatedInvoices.compareTo( maxAmt ) > 0) ) || ( (totalInvoices.signum() < 0) && (allocatedInvoices.compareTo( maxAmt ) < 0) ) ) {
                BigDecimal diff = allocatedInvoices.subtract( maxAmt );

                openAmt           = openAmt.subtract( diff );
                allocatedInvoices = allocatedInvoices.subtract( diff );
            }

            if( openAmt.compareTo( Env.ZERO ) == 0 ) {
                break;
            }

            log.fine( "allocateBPOldestFirst - Invoice Allocated=" + openAmt );

            if( !createAllocation( C_Currency_ID,"BP Oldest (" + difference.abs() + ")",dateAcct,openAmt,null,null,null,invoice.getC_BPartner_ID(),0,invoice.getC_Invoice_ID())) {
                throw new ErrorOXPSystem( "Cannot create Allocation" );
            }

            if( allocatedInvoices.compareTo( maxAmt ) == 0 ) {
                break;
            }
        }    // for all invoices

        if( allocatedPayments.compareTo( allocatedInvoices ) != 0 ) {
            throw new ErrorOXPSystem( "Allocated Payments=" + allocatedPayments + " <> Invoices=" + allocatedInvoices );
        }

        processAllocation();

        return 1;
    }    // allocateOldestFirst

    /**
     * Descripción de Método
     *
     *
     * @param C_Currency_ID
     * @param description
     * @param dateAcct
     * @param Amount
     * @param DiscountAmt
     * @param WriteOffAmt
     * @param OverUnderAmt
     * @param C_BPartner_ID
     * @param C_Payment_ID
     * @param C_Invoice_ID
     *
     * @return
     */

    private boolean createAllocation( int C_Currency_ID,String description,Timestamp dateAcct,BigDecimal Amount,BigDecimal DiscountAmt,BigDecimal WriteOffAmt,BigDecimal OverUnderAmt,int C_BPartner_ID,int C_Payment_ID,int C_Invoice_ID ) {

        // Process old Allocation

        if( (m_allocation != null) && (m_allocation.getC_Currency_ID() != C_Currency_ID) ) {
            processAllocation();
        }

        // New Allocation

        if( m_allocation == null ) {
            m_allocation = new MAllocationHdr( getCtx(),false,dateAcct,    // automatic
                                               C_Currency_ID,"Auto " + description,get_TrxName());

            if( !m_allocation.save()) {
                return false;
            }
        }

        // New Allocation Line

        MAllocationLine aLine = new MAllocationLine( m_allocation,Amount,DiscountAmt,WriteOffAmt,OverUnderAmt );

        aLine.setC_BPartner_ID( C_BPartner_ID );
        aLine.setC_Payment_ID( C_Payment_ID );
        aLine.setC_Invoice_ID( C_Invoice_ID );

        return aLine.save();
    }    // createAllocation

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean processAllocation() {
        if( m_allocation == null ) {
            return true;
        }

        boolean success = m_allocation.processIt( MAllocationHdr.DOCACTION_Complete );

        if( success ) {
            success = m_allocation.save();
        } else {
            m_allocation.save();
        }

        addLog( 0,m_allocation.getDateAcct(),null,m_allocation.getDescription());
        m_allocation = null;

        return success;
    }    // processAllocation
}    // AllocationAuto



/*
 *  @(#)AllocationAuto.java   02.07.07
 * 
 *  Fin del fichero AllocationAuto.java
 *  
 *  Versión 2.2
 *
 */
