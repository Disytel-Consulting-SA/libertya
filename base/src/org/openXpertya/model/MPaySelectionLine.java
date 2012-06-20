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

public class MPaySelectionLine extends X_C_PaySelectionLine {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_PaySelectionLine_ID
     * @param trxName
     */

    public MPaySelectionLine( Properties ctx,int C_PaySelectionLine_ID,String trxName ) {
        super( ctx,C_PaySelectionLine_ID,trxName );

        if( C_PaySelectionLine_ID == 0 ) {

            // setC_PaySelection_ID (0);
            // setPaymentRule (null);  // S
            // setLine (0);    // @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_PaySelectionLine WHERE C_PaySelection_ID=@C_PaySelection_ID@
            // setC_Invoice_ID (0);

            setIsSOTrx( false );
            setOpenAmt( Env.ZERO );
            setPayAmt( Env.ZERO );
            setDiscountAmt( Env.ZERO );
            setDifferenceAmt( Env.ZERO );
            setIsManual( false );
        }
    }    // MPaySelectionLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPaySelectionLine( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPaySelectionLine

    /**
     * Constructor de la clase ...
     *
     *
     * @param ps
     * @param Line
     * @param PaymentRule
     */

    public MPaySelectionLine( MPaySelection ps,int Line,String PaymentRule ) {
        this( ps.getCtx(),0,ps.get_TrxName());
        setClientOrg( ps );
        setC_PaySelection_ID( ps.getC_PaySelection_ID());
        setLine( Line );
        setPaymentRule( PaymentRule );
    }    // MPaySelectionLine

    /** Descripción de Campos */

    private MInvoice m_invoice = null;

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     * @param isSOTrx
     * @param OpenAmt
     * @param PayAmt
     * @param DiscountAmt
     */

    public void setInvoice( int C_Invoice_ID,boolean isSOTrx,BigDecimal OpenAmt,BigDecimal PayAmt,BigDecimal DiscountAmt ) {
        setC_Invoice_ID( C_Invoice_ID );
        setIsSOTrx( isSOTrx );
        setOpenAmt( OpenAmt );
        setPayAmt( PayAmt );
        setDiscountAmt( DiscountAmt );
        setDifferenceAmt( OpenAmt.subtract( PayAmt ).subtract( DiscountAmt ));
    }    // setInvoive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MInvoice getInvoice() {
        if( m_invoice == null ) {
            m_invoice = new MInvoice( getCtx(),getC_Invoice_ID(),get_TrxName());
        }

        return m_invoice;
    }    // getInvoice

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {
        setDifferenceAmt( getOpenAmt().subtract( getPayAmt()).subtract( getDiscountAmt()));

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
        setHeader();

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
        setHeader();

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void setHeader() {

        // Update Header

        String sql = "UPDATE C_PaySelection ps " + "SET TotalAmt = (SELECT COALESCE(SUM(psl.PayAmt),0) " + "FROM C_PaySelectionLine psl " + "WHERE ps.C_PaySelection_ID=psl.C_PaySelection_ID AND psl.IsActive='Y') " + "WHERE C_PaySelection_ID=" + getC_PaySelection_ID();

        DB.executeUpdate( sql,get_TrxName());
    }    // setHeader

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MPaySelectionLine[" );

        sb.append( getID()).append( ",C_Invoice_ID=" ).append( getC_Invoice_ID()).append( ",PayAmt=" ).append( getPayAmt()).append( ",DifferenceAmt=" ).append( getDifferenceAmt()).append( "]" );

        return sb.toString();
    }    // toString
}    // MPaySelectionLine



/*
 *  @(#)MPaySelectionLine.java   02.07.07
 * 
 *  Fin del fichero MPaySelectionLine.java
 *  
 *  Versión 2.2
 *
 */
