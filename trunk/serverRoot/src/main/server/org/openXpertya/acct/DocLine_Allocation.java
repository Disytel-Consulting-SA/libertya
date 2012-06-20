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

import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 24.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DocLine_Allocation extends DocLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param DocumentType
     * @param TrxHeader_ID
     * @param TrxLine_ID
     * @param trxName
     */

    public DocLine_Allocation( String DocumentType,int TrxHeader_ID,int TrxLine_ID,String trxName ) {
        super( DocumentType,TrxHeader_ID,TrxLine_ID,trxName );
    }    // DocLine_Allocation

    /** Descripción de Campos */

    private int m_C_Invoice_ID;

    /** Descripción de Campos */

    private int m_C_Payment_ID;

    /** Descripción de Campos */

    private int m_C_CashLine_ID;

    /** Descripción de Campos */

    private int m_C_Order_ID;
    
    /** Descripcion de Campos */
    
    private int m_C_Invoice_Credit_ID; 

    /** Descripción de Campos */

    private BigDecimal m_DiscountAmt;

    /** Descripción de Campos */

    private BigDecimal m_WriteOffAmt;

    /** Descripción de Campos */

    private BigDecimal m_OverUnderAmt;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getInvoiceC_Currency_ID() {
        if( m_C_Invoice_ID == 0 ) {
            return 0;
        }

        String sql = "SELECT C_Currency_ID " + "FROM C_Invoice " + "WHERE C_Invoice_ID=?";

        return DB.getSQLValue( null,sql,m_C_Invoice_ID );
    }    // getInvoiceC_Currency_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "DocLine_Allocation[" );

        sb.append( p_TrxLine_ID ).append( ",Amt=" ).append( getAmount()).append( ",Discount=" ).append( getDiscountAmt()).append( ",WriteOff=" ).append( getWriteOffAmt()).append( ",OverUnderAmt=" ).append( getOverUnderAmt()).append( " - C_Payment_ID=" ).append( m_C_Payment_ID ).append( ",C_CashLine_ID=" ).append( m_C_CashLine_ID ).append( ",C_Invoice_ID=" ).append( m_C_Invoice_ID ).append( ",C_Invoice_Credit_ID=" ).append( m_C_Invoice_Credit_ID ).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Order_ID() {
        return m_C_Order_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param order_ID
     */

    public void setC_Order_ID( int order_ID ) {
        m_C_Order_ID = order_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getDiscountAmt() {
        return m_DiscountAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @param discountAmt
     */

    public void setDiscountAmt( BigDecimal discountAmt ) {
        m_DiscountAmt = discountAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getOverUnderAmt() {
        return m_OverUnderAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @param overUnderAmt
     */

    public void setOverUnderAmt( BigDecimal overUnderAmt ) {
        m_OverUnderAmt = overUnderAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getWriteOffAmt() {
        return m_WriteOffAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @param writeOffAmt
     */

    public void setWriteOffAmt( BigDecimal writeOffAmt ) {
        m_WriteOffAmt = writeOffAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_CashLine_ID() {
        return m_C_CashLine_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param cashLine_ID
     */

    public void setC_CashLine_ID( int cashLine_ID ) {
        m_C_CashLine_ID = cashLine_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Invoice_ID() {
        return m_C_Invoice_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param invoice_ID
     */

    public void setC_Invoice_ID( int invoice_ID ) {
        m_C_Invoice_ID = invoice_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Payment_ID() {
        return m_C_Payment_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param payment_ID
     */

    public void setC_Payment_ID( int payment_ID ) {
        m_C_Payment_ID = payment_ID;
    }

	public void setC_Invoice_Credit_ID(int m_C_Invoice_Credit_ID) {
		this.m_C_Invoice_Credit_ID = m_C_Invoice_Credit_ID;
	}

	public int getC_Invoice_Credit_ID() {
		return m_C_Invoice_Credit_ID;
	}
}    // DocLine_Allocation



/*
 *  @(#)DocLine_Allocation.java   24.03.06
 * 
 *  Fin del fichero DocLine_Allocation.java
 *  
 *  Versión 2.2
 *
 */
