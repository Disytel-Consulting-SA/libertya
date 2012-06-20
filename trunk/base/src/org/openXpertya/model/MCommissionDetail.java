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
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCommissionDetail extends X_C_CommissionDetail {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ignored
     * @param trxName
     */

    public MCommissionDetail( Properties ctx,int ignored,String trxName ) {
        super( ctx,0,trxName );

        if( ignored != 0 ) {
            throw new IllegalArgumentException( "Multi-Key" );
        }
    }    // MCommissionDetail

    /**
     * Constructor de la clase ...
     *
     *
     * @param amt
     * @param C_Currency_ID
     * @param Amt
     * @param Qty
     */

    public MCommissionDetail( MCommissionAmt amt,int C_Currency_ID,BigDecimal Amt,BigDecimal Qty ) {
        super( amt.getCtx(),0,amt.get_TrxName());
        setClientOrg( amt );
        setC_CommissionAmt_ID( amt.getC_CommissionAmt_ID());
        setC_Currency_ID( C_Currency_ID );
        setActualAmt( Amt );
        setActualQty( Qty );
        setConvertedAmt( Env.ZERO );
    }    // MCommissionDetail

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCommissionDetail( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCommissionDetail

    /**
     * Descripción de Método
     *
     *
     * @param C_OrderLine_ID
     * @param C_InvoiceLine_ID
     */

    public void setLineIDs( int C_OrderLine_ID,int C_InvoiceLine_ID ) {
        if( C_OrderLine_ID != 0 ) {
            setC_OrderLine_ID( C_OrderLine_ID );
        }

        if( C_InvoiceLine_ID != 0 ) {
            setC_InvoiceLine_ID( C_InvoiceLine_ID );
        }
    }    // setLineIDs

    /**
     * Descripción de Método
     *
     *
     * @param date
     */

    public void setConvertedAmt( Timestamp date ) {
        BigDecimal amt = MConversionRate.convertBase( getCtx(),getActualAmt(),getC_Currency_ID(),date,0,    // type
            getAD_Client_ID(),getAD_Org_ID());

        if( amt != null ) {
            setConvertedAmt( amt );
        }
    }    // setConvertedAmt

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
        if( !newRecord ) {
            updateAmtHeader();
        }

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
        if( success ) {
            updateAmtHeader();
        }

        return success;
    }    // afterDelete

    /**
     * Descripción de Método
     *
     */

    private void updateAmtHeader() {
        MCommissionAmt amt = new MCommissionAmt( getCtx(),getC_CommissionAmt_ID(),get_TrxName());

        amt.calculateCommission();
        amt.save();
    }    // updateAmtHeader
}    // MCommissionDetail



/*
 *  @(#)MCommissionDetail.java   02.07.07
 * 
 *  Fin del fichero MCommissionDetail.java
 *  
 *  Versión 2.2
 *
 */
