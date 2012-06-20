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

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MCostDetail extends X_M_CostDetail {

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param C_InvoiceLine_ID
     * @param M_CostElement_ID
     * @param Amt
     * @param Qty
     * @param Description
     *
     * @return
     */

    public static MCostDetail createInvoice( MAcctSchema as,int AD_Org_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int C_InvoiceLine_ID,int M_CostElement_ID,BigDecimal Amt,BigDecimal Qty,String Description ) {
        String sql = "DELETE M_CostDetail WHERE C_InvoiceLine_ID=" + C_InvoiceLine_ID;
        int no = DB.executeUpdate( sql,as.get_TrxName());

        if( no != 0 ) {
            log.config( "Deleted #" + no );
        }

        //

        MCostDetail cd = new MCostDetail( as,AD_Org_ID,M_Product_ID,M_AttributeSetInstance_ID,C_InvoiceLine_ID,0,M_CostElement_ID,Amt,Qty,Description );

        if( cd.save()) {
            return cd;
        }

        return null;
    }    // createInvoice

    /**
     * Descripción de Método
     *
     *
     * @param as
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param C_OrderLine_ID
     * @param M_CostElement_ID
     * @param Amt
     * @param Qty
     * @param Description
     *
     * @return
     */

    public static MCostDetail createOrder( MAcctSchema as,int AD_Org_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int C_OrderLine_ID,int M_CostElement_ID,BigDecimal Amt,BigDecimal Qty,String Description ) {
        String sql = "DELETE M_CostDetail WHERE C_OrderLine_ID=" + C_OrderLine_ID;
        int no = DB.executeUpdate( sql,as.get_TrxName());

        if( no != 0 ) {
            log.config( "Deleted #" + no );
        }

        //

        MCostDetail cd = new MCostDetail( as,AD_Org_ID,M_Product_ID,M_AttributeSetInstance_ID,0,C_OrderLine_ID,M_CostElement_ID,Amt,Qty,Description );

        if( cd.save()) {
            return cd;
        }

        return null;
    }    // createOrder

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MCostDetail.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_CostDetail_ID
     * @param trxName
     */

    public MCostDetail( Properties ctx,int M_CostDetail_ID,String trxName ) {
        super( ctx,M_CostDetail_ID,trxName );

        if( M_CostDetail_ID == 0 ) {

            // setC_AcctSchema_ID (0);
            // setM_Product_ID (0);

            setM_AttributeSetInstance_ID( 0 );

            // setC_OrderLine_ID (0);
            // setC_InvoiceLine_ID (0);

            setAmt( Env.ZERO );
            setQty( Env.ZERO );
            setProcessed( false );
        }
    }    // MCostDetail

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MCostDetail( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MCostDetail

    /**
     * Constructor de la clase ...
     *
     *
     * @param as
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_AttributeSetInstance_ID
     * @param C_InvoiceLine_ID
     * @param C_OrderLine_ID
     * @param M_CostElement_ID
     * @param Amt
     * @param Qty
     * @param Description
     */

    public MCostDetail( MAcctSchema as,int AD_Org_ID,int M_Product_ID,int M_AttributeSetInstance_ID,int C_InvoiceLine_ID,int C_OrderLine_ID,int M_CostElement_ID,BigDecimal Amt,BigDecimal Qty,String Description ) {
        this( as.getCtx(),0,as.get_TrxName());
        setClientOrg( as.getAD_Client_ID(),AD_Org_ID );
        setC_AcctSchema_ID( as.getC_AcctSchema_ID());
        setM_Product_ID( M_Product_ID );
        setM_AttributeSetInstance_ID( M_AttributeSetInstance_ID );
        setC_InvoiceLine_ID( C_InvoiceLine_ID );
        setC_OrderLine_ID( C_OrderLine_ID );
        setM_CostElement_ID( M_CostElement_ID );

        //

        setAmt( Amt );
        setQty( Qty );
        setDescription( Description );
    }    // MCostDetail

    /**
     * Descripción de Método
     *
     *
     * @param Amt
     */

    public void setAmt( BigDecimal Amt ) {
        if( Amt == null ) {
            super.setAmt( Env.ZERO );
        } else {
            super.setAmt( Amt );
        }
    }    // setAmt

    /**
     * Descripción de Método
     *
     *
     * @param Qty
     */

    public void setQty( BigDecimal Qty ) {
        if( Qty == null ) {
            super.setQty( Env.ZERO );
        } else {
            super.setQty( Qty );
        }
    }    // setQty

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOrder() {
        return getC_OrderLine_ID() != 0;
    }    // isOrder

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isInvoice() {
        return getC_InvoiceLine_ID() != 0;
    }    // isInvoice
}    // MCostDetail



/*
 *  @(#)MCostDetail.java   02.07.07
 * 
 *  Fin del fichero MCostDetail.java
 *  
 *  Versión 2.2
 *
 */
