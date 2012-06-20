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

public class CalloutRequisition extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String product( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        Integer M_Product_ID = ( Integer )value;

        if( (M_Product_ID == null) || (M_Product_ID.intValue() == 0) ) {
            return "";
        }

        // setCalloutActive(true);
        //

        int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,WindowNo,"C_BPartner_ID" );
        BigDecimal      Qty     = ( BigDecimal )mTab.getValue( "Qty" );
        boolean         isSOTrx = false;
        MProductPricing pp      = new MProductPricing( M_Product_ID.intValue(),C_BPartner_ID,Qty,isSOTrx );

        //

        int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );

        pp.setM_PriceList_ID( M_PriceList_ID );

        int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

        pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

        Timestamp orderDate = ( Timestamp )mTab.getValue( "DateRequired" );

        pp.setPriceDate( orderDate );

        //

        mTab.setValue( "PriceActual",pp.getPriceStd());
        Env.setContext( ctx,WindowNo,"EnforcePriceLimit",pp.isEnforcePriceLimit()
                ?"Y"
                :"N" );    // not used
        Env.setContext( ctx,WindowNo,"DiscountSchema",pp.isDiscountSchema()
                ?"Y"
                :"N" );

        // setCalloutActive(false);

        return "";
    }    // product

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */

    public String amt( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
        if( isCalloutActive() || (value == null) ) {
            return "";
        }

        setCalloutActive( true );

        // Qty changed - recalc price

        if( mField.getColumnName().equals( "Qty" ) && "Y".equals( Env.getContext( ctx,WindowNo,"DiscountSchema" ))) {
            int M_Product_ID = Env.getContextAsInt( ctx,WindowNo,WindowNo,"M_Product_ID" );
            int C_BPartner_ID = Env.getContextAsInt( ctx,WindowNo,WindowNo,"C_BPartner_ID" );
            BigDecimal      Qty     = ( BigDecimal )value;
            boolean         isSOTrx = false;
            MProductPricing pp      = new MProductPricing( M_Product_ID,C_BPartner_ID,Qty,isSOTrx );

            //

            int M_PriceList_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_ID" );

            pp.setM_PriceList_ID( M_PriceList_ID );

            int M_PriceList_Version_ID = Env.getContextAsInt( ctx,WindowNo,"M_PriceList_Version_ID" );

            pp.setM_PriceList_Version_ID( M_PriceList_Version_ID );

            Timestamp orderDate = ( Timestamp )mTab.getValue( "DateInvoiced" );

            pp.setPriceDate( orderDate );

            //

            mTab.setValue( "PriceActual",pp.getPriceStd());
        }

        int        StdPrecision = Env.getContextAsInt( ctx,WindowNo,"StdPrecision" );
        BigDecimal Qty          = ( BigDecimal )mTab.getValue( "Qty" );
        BigDecimal PriceActual  = ( BigDecimal )mTab.getValue( "PriceActual" );

        // get values

        log.fine( "amt - Qty=" + Qty + ", Price=" + PriceActual + ", Precision=" + StdPrecision );

        // Multiply

        BigDecimal LineNetAmt = Qty.multiply( PriceActual );

        if( LineNetAmt.scale() > StdPrecision ) {
            LineNetAmt = LineNetAmt.setScale( StdPrecision,BigDecimal.ROUND_HALF_UP );
        }

        mTab.setValue( "LineNetAmt",LineNetAmt );
        log.info( "amt - LineNetAmt=" + LineNetAmt );

        //

        setCalloutActive( false );

        return "";
    }    // amt
}    // CalloutRequisition



/*
 *  @(#)CalloutRequisition.java   02.07.07
 * 
 *  Fin del fichero CalloutRequisition.java
 *  
 *  Versión 2.2
 *
 */
