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
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.model.MBankStatement;
import org.openXpertya.model.MBankStatementLine;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.X_I_BankStatement;
import org.openXpertya.util.Env;
import org.openXpertya.util.ErrorOXPSystem;
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BankStatementPayment extends SvrProcess {

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
        int Table_ID  = getTable_ID();
        int Record_ID = getRecord_ID();

        log.info( "doIt - Table_ID=" + Table_ID + ", Record_ID=" + Record_ID );

        if( Table_ID == X_I_BankStatement.Table_ID ) {
            return createPayment( new X_I_BankStatement( getCtx(),Record_ID,get_TrxName()));
        } else if( Table_ID == MBankStatementLine.Table_ID ) {
            return createPayment( new MBankStatementLine( getCtx(),Record_ID,get_TrxName()));
        }

        throw new ErrorOXPSystem( "??" );
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param ibs
     *
     * @return
     *
     * @throws Exception
     */

    private String createPayment( X_I_BankStatement ibs ) throws Exception {
        if( (ibs == null) || (ibs.getC_Payment_ID() != 0) ) {
            return "--";
        }

        log.fine( "createPayment - " + ibs );

        if( (ibs.getC_Invoice_ID() == 0) && (ibs.getC_BPartner_ID() == 0) ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @C_Invoice_ID@ / @C_BPartner_ID@" );
        }

        if( ibs.getC_BankAccount_ID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @C_BankAccount_ID@" );
        }

        //

        MPayment payment = createPayment( ibs.getC_Invoice_ID(),ibs.getC_BPartner_ID(),ibs.getC_Currency_ID(),ibs.getStmtAmt(),ibs.getTrxAmt(),ibs.getC_BankAccount_ID(),(ibs.getStatementLineDate() == null)
                ?ibs.getStatementDate()
                :ibs.getStatementLineDate(),ibs.getDateAcct(),ibs.getDescription());

        if( payment == null ) {
            throw new ErrorOXPSystem( "Could not create Payment" );
        }

        ibs.setC_Payment_ID( payment.getC_Payment_ID());
        ibs.setC_Currency_ID( payment.getC_Currency_ID());
        ibs.setTrxAmt( payment.getPayAmt());
        ibs.save();

        //

        String retString = "@C_Payment_ID@ = " + payment.getDocumentNo();

        if( payment.getOverUnderAmt().compareTo( Env.ZERO ) != 0 ) {
            retString += " - @OverUnderAmt@=" + payment.getOverUnderAmt();
        }

        return retString;
    }    // createPayment - Import

    /**
     * Descripción de Método
     *
     *
     * @param bsl
     *
     * @return
     *
     * @throws Exception
     */

    private String createPayment( MBankStatementLine bsl ) throws Exception {
        if( (bsl == null) || (bsl.getC_Payment_ID() != 0) ) {
            return "--";
        }

        log.fine( "createPayment - " + bsl );

        if( (bsl.getC_Invoice_ID() == 0) && (bsl.getC_BPartner_ID() == 0) ) {
            throw new ErrorUsuarioOXP( "@NotFound@ @C_Invoice_ID@ / @C_BPartner_ID@" );
        }

        //

        MBankStatement bs = new MBankStatement( getCtx(),bsl.getC_BankStatement_ID(),get_TrxName());

        //

        MPayment payment = createPayment( bsl.getC_Invoice_ID(),bsl.getC_BPartner_ID(),bsl.getC_Currency_ID(),bsl.getStmtAmt(),bsl.getTrxAmt(),bs.getC_BankAccount_ID(),bsl.getStatementLineDate(),bsl.getDateAcct(),bsl.getDescription());

        if( payment == null ) {
            throw new ErrorOXPSystem( "Could not create Payment" );
        }

        // update statement

        bsl.setPayment( payment );
        bsl.setIsReconciled(true);
        bsl.save();

        //

        String retString = "@C_Payment_ID@ = " + payment.getDocumentNo();

        if( payment.getOverUnderAmt().compareTo( Env.ZERO ) != 0 ) {
            retString += " - @OverUnderAmt@=" + payment.getOverUnderAmt();
        }

        return retString;
    }    // createPayment

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param C_BPartner_ID
     * @param C_Currency_ID
     * @param StmtAmt
     * @param TrxAmt
     * @param C_BankAccount_ID
     * @param DateTrx
     * @param DateAcct
     * @param Description
     *
     * @return
     */

    private MPayment createPayment( int C_Invoice_ID,int C_BPartner_ID,int C_Currency_ID,BigDecimal StmtAmt,BigDecimal TrxAmt,int C_BankAccount_ID,Timestamp DateTrx,Timestamp DateAcct,String Description ) {

        // Trx Amount = Payment overwrites Statement Amount if defined

        BigDecimal PayAmt = TrxAmt;

        if( (PayAmt == null) || (Env.ZERO.compareTo( PayAmt ) == 0) ) {
            PayAmt = StmtAmt;
        }

        if( (C_Invoice_ID == 0) && ( (PayAmt == null) || (Env.ZERO.compareTo( PayAmt ) == 0) ) ) {
            throw new IllegalStateException( "@PayAmt@ = 0" );
        }

        if( PayAmt == null ) {
            PayAmt = Env.ZERO;
        }

        //

        MPayment payment = new MPayment( getCtx(),0,get_TrxName());

        payment.setC_BankAccount_ID( C_BankAccount_ID );
        payment.setTenderType( MPayment.TENDERTYPE_Check );

        if( DateTrx != null ) {
            payment.setDateTrx( DateTrx );
        } else if( DateAcct != null ) {
            payment.setDateTrx( DateAcct );
        }

        if( DateAcct != null ) {
            payment.setDateAcct( DateAcct );
        } else {
            payment.setDateAcct( payment.getDateTrx());
        }

        payment.setDescription( Description );

        //

        if( C_Invoice_ID != 0 ) {
            MInvoice invoice = new MInvoice( getCtx(),C_Invoice_ID,null );

            payment.setC_DocType_ID( invoice.isSOTrx());    // Receipt
            payment.setC_Invoice_ID( invoice.getC_Invoice_ID());
            payment.setC_BPartner_ID( invoice.getC_BPartner_ID());

            if( PayAmt.compareTo( Env.ZERO ) != 0 )         // explicit Amount
            {
                payment.setC_Currency_ID( C_Currency_ID );

                if( invoice.isSOTrx()) {
                    payment.setPayAmt( PayAmt );
                } else {                                    // payment is likely to be negative
                    payment.setPayAmt( PayAmt.negate());
                }

                payment.setOverUnderAmt( invoice.getGrandTotal( true ).subtract( payment.getPayAmt()));
            } else                       // set Pay Amout from Invoice
            {
                payment.setC_Currency_ID( invoice.getC_Currency_ID());
                payment.setPayAmt( invoice.getGrandTotal( true ));
            }
        } else if( C_BPartner_ID != 0 ) {
            payment.setC_BPartner_ID( C_BPartner_ID );
            payment.setC_Currency_ID( C_Currency_ID );

            if( PayAmt.signum() < 0 )    // Payment
            {
                payment.setPayAmt( PayAmt.abs());
                payment.setC_DocType_ID( false );
            } else                       // Receipt
            {
                payment.setPayAmt( PayAmt );
                payment.setC_DocType_ID( true );
            }
        } else {
            return null;
        }

        payment.setIsReconciled(true);
        payment.save();

        //

        payment.processIt( MPayment.DOCACTION_Complete );
        payment.save();

        return payment;
    }    // createPayment
}    // BankStatementPayment



/*
 *  @(#)BankStatementPayment.java   02.07.07
 * 
 *  Fin del fichero BankStatementPayment.java
 *  
 *  Versión 2.2
 *
 */
