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

import org.openXpertya.model.MAccount;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocLine_Invoice extends DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine_Invoice( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        super( DocumentType,TrxHeader_ID,TrxLine_ID,trxName );
    }    // DocLine_Invoice

    /** Descripción de Campos */

    private BigDecimal m_LineNetAmt = null;

    /** Descripción de Campos */

    private BigDecimal m_ListAmt = Env.ZERO;

    /** Descripción de Campos */

    private BigDecimal m_DiscountAmt = Env.ZERO;

    /**
     * Descripción de Método
     *
     *
     * @param LineNetAmt
     * @param PriceList
     * @param Qty
     */

    public void setAmount( BigDecimal LineNetAmt,BigDecimal PriceList,BigDecimal Qty ) {
        m_LineNetAmt = (LineNetAmt == null)
                       ?Env.ZERO
                       :LineNetAmt;

        if( (PriceList != null) && (Qty != null) ) {
            m_ListAmt = PriceList.multiply( Qty );
        }

        if( m_ListAmt.equals( Env.ZERO )) {
            m_ListAmt = m_LineNetAmt;
        }

        m_DiscountAmt = m_ListAmt.subtract( m_LineNetAmt );

        //

        setAmount( m_ListAmt,m_DiscountAmt );

        // Log.trace(this,Log.l6_Database, "DocLine_Invoice.setAmount",
        // "LineNet=" + m_LineNetAmt + ", List=" + m_ListAmt + ", Discount=" + m_DiscountAmt
        // + " => Amount=" + getAmount());

    }    // setAmounts

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getDiscount() {
        return m_DiscountAmt;
    }    // getDiscount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getListAmount() {
        return m_ListAmt;
    }    // getListAmount

    /**
     * Descripción de Método
     *
     *
     * @param diff
     */

    public void setLineNetAmtDifference( BigDecimal diff ) {
        String msg = "setLineNetAmtDifference - Diff=" + diff + " - LineNetAmt=" + m_LineNetAmt;

        m_LineNetAmt  = m_LineNetAmt.subtract( diff );
        m_DiscountAmt = m_ListAmt.subtract( m_LineNetAmt );
        setAmount( m_ListAmt,m_DiscountAmt );
        msg += " -> " + m_LineNetAmt;
        log.fine( msg );
    }    // setLineNetAmtDifference

    /**
     * Descripción de Método
     *
     *
     * @param AcctType
     * @param as
     *
     * @return
     */

    public MAccount getAccount( int AcctType,MAcctSchema as ) {

        // Charge Account

        if( (getM_Product_ID() == 0) && (getC_Charge_ID() != 0) ) {
            BigDecimal amt = new BigDecimal( -1 );    // Revenue (-)

            if( p_DocumentType.indexOf( "AP" ) != -1 ) {
                amt = new BigDecimal( +1 );           // Expense (+)
            }

            MAccount acct = getChargeAccount( as,amt );

            if( acct != null ) {
                return acct;
            }
        }

        // Product Account

        return p_productInfo.getAccount( AcctType,as );
    }    // getAccount
}    // DocLine_Invoice



/*
 *  @(#)DocLine_Invoice.java   24.03.06
 * 
 *  Fin del fichero DocLine_Invoice.java
 *  
 *  Versión 2.2
 *
 */
