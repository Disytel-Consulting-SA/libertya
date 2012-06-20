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
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MDunningRunLine extends X_C_DunningRunLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_DunningRunLine_ID
     * @param trxName
     */

    public MDunningRunLine( Properties ctx,int C_DunningRunLine_ID,String trxName ) {
        super( ctx,C_DunningRunLine_ID,trxName );

        if( C_DunningRunLine_ID == 0 ) {
            setAmt( Env.ZERO );
            setOpenAmt( Env.ZERO );
            setConvertedAmt( Env.ZERO );
            setFeeAmt( Env.ZERO );
            setInterestAmt( Env.ZERO );
            setTotalAmt( Env.ZERO );
            setDaysDue( 0 );
            setTimesDunned( 0 );
            setIsInDispute( false );
            setProcessed( false );
        }
    }    // MDunningRunLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MDunningRunLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MDunningRunLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param parent
     */

    public MDunningRunLine( MDunningRunEntry parent ) {
        this( parent.getCtx(),0,parent.get_TrxName());
        setClientOrg( parent );
        setC_DunningRunEntry_ID( parent.getC_DunningRunEntry_ID());

        //

        m_parent          = parent;
        m_C_CurrencyTo_ID = parent.getC_Currency_ID();
    }    // MDunningRunLine

    /** Descripción de Campos */

    private MDunningRunEntry m_parent = null;

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /** Descripción de Campos */

    private MPayment m_payment = null;

    /** Descripción de Campos */

    private int m_C_CurrencyFrom_ID = 0;

    /** Descripción de Campos */

    private int m_C_CurrencyTo_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MDunningRunEntry getParent() {
        if( m_parent == null ) {
            m_parent = new MDunningRunEntry( getCtx(),getC_DunningRunEntry_ID(),get_TrxName());
        }

        return m_parent;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice getInvoice() {
        if( getC_Invoice_ID() == 0 ) {
            m_invoice = null;
        } else if( m_invoice == null ) {
            m_invoice = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());
        }

        return m_invoice;
    }    // getInvoice

    /**
     * Descripción de Método
     *
     *
     * @param invoice
     */

    public void setInvoice( MInvoice invoice ) {
        m_invoice = invoice;

        if( invoice != null ) {
            m_C_CurrencyFrom_ID = invoice.getC_Currency_ID();
            setAmt( invoice.getGrandTotal());
            setOpenAmt( getAmt());    // not correct
            setConvertedAmt( MConversionRate.convert( getCtx(),getOpenAmt(),getC_CurrencyFrom_ID(),getC_CurrencyTo_ID(),getAD_Client_ID(),getAD_Org_ID()));
        } else {
            m_C_CurrencyFrom_ID = 0;
            setAmt( Env.ZERO );
            setOpenAmt( Env.ZERO );
            setConvertedAmt( Env.ZERO );
        }
    }    // setInvoice

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param C_Currency_ID
     * @param GrandTotal
     * @param Open
     * @param DaysDue
     * @param IsInDispute
     * @param TimesDunned
     * @param DaysAfterLast
     */

    public void setInvoice( int C_Invoice_ID,int C_Currency_ID,BigDecimal GrandTotal,BigDecimal Open,int DaysDue,boolean IsInDispute,int TimesDunned,int DaysAfterLast ) {
        setC_Invoice_ID( C_Invoice_ID );
        m_C_CurrencyFrom_ID = C_Currency_ID;
        setAmt( GrandTotal );
        setOpenAmt( Open );
        setConvertedAmt( MConversionRate.convert( getCtx(),getOpenAmt(),C_Currency_ID,getC_CurrencyTo_ID(),getAD_Client_ID(),getAD_Org_ID()));
        setIsInDispute( IsInDispute );
        setDaysDue( DaysDue );
        setTimesDunned( TimesDunned );
    }    // setInvoice

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MPayment getPayment() {
        if( getC_Payment_ID() == 0 ) {
            m_payment = null;
        } else if( m_payment == null ) {
            m_payment = new MPayment( getCtx(),getC_Payment_ID(),get_TrxName());
        }

        return m_payment;
    }    // getPayment

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     * @param C_Currency_ID
     * @param PayAmt
     * @param OpenAmt
     */

    public void setPayment( int C_Payment_ID,int C_Currency_ID,BigDecimal PayAmt,BigDecimal OpenAmt ) {
        setC_Payment_ID( C_Payment_ID );
        m_C_CurrencyFrom_ID = C_Currency_ID;
        setAmt( PayAmt );
        setOpenAmt( OpenAmt );
        setConvertedAmt( MConversionRate.convert( getCtx(),getOpenAmt(),C_Currency_ID,getC_CurrencyTo_ID(),getAD_Client_ID(),getAD_Org_ID()));
    }    // setPayment

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_CurrencyFrom_ID() {
        if( m_C_CurrencyFrom_ID == 0 ) {
            if( getC_Invoice_ID() != 0 ) {
                m_C_CurrencyFrom_ID = getInvoice().getC_Currency_ID();
            } else if( getC_Payment_ID() != 0 ) {
                m_C_CurrencyFrom_ID = getPayment().getC_Currency_ID();
            }
        }

        return m_C_CurrencyFrom_ID;
    }    // getC_CurrencyFrom_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_CurrencyTo_ID() {
        if( m_C_CurrencyTo_ID == 0 ) {
            m_C_CurrencyTo_ID = getParent().getC_Currency_ID();
        }

        return m_C_CurrencyTo_ID;
    }    // getC_CurrencyTo_ID

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Set Amt

        if( (getC_Invoice_ID() == 0) && (getC_Payment_ID() == 0) ) {
            setAmt( Env.ZERO );
            setOpenAmt( Env.ZERO );
        }

        // Converted Amt

        if( Env.ZERO.compareTo( getOpenAmt()) == 0 ) {
            setConvertedAmt( Env.ZERO );
        } else if( Env.ZERO.compareTo( getConvertedAmt()) == 0 ) {
            setConvertedAmt( MConversionRate.convert( getCtx(),getOpenAmt(),getC_CurrencyFrom_ID(),getC_CurrencyTo_ID(),getAD_Client_ID(),getAD_Org_ID()));
        }

        // Total

        setTotalAmt( getConvertedAmt().add( getFeeAmt()).add( getInterestAmt()));

        //

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {
        updateEntry();

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        updateEntry();

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void updateEntry() {
        String sql = "UPDATE C_DunningRunEntry e " + "SET (Amt,Qty)=(SELECT SUM(Amt),COUNT(*) FROM C_DunningRunLine l " + "WHERE e.C_DunningRunEntry_ID=l.C_DunningRunEntry_ID) " + "WHERE C_DunningRunEntry_ID=" + getC_DunningRunEntry_ID();

        DB.executeUpdate( sql,get_TrxName());
    }    // updateEntry
}    // MDunningRunLine



/*
 *  @(#)MDunningRunLine.java   02.07.07
 * 
 *  Fin del fichero MDunningRunLine.java
 *  
 *  Versión 2.2
 *
 */
