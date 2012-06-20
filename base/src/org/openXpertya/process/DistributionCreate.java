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
import java.util.logging.Level;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MDistributionList;
import org.openXpertya.model.MDistributionListLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MOrderLine;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class DistributionCreate extends SvrProcess {

    /** Descripción de Campos */

    private int p_M_Product_ID = 0;

    /** Descripción de Campos */

    private BigDecimal p_Qty;

    /** Descripción de Campos */

    private boolean p_IsCreateSingleOrder;

    /** Descripción de Campos */

    private int p_Bill_BPartner_ID;

    /** Descripción de Campos */

    private int p_Bill_Location_ID;

    /** Descripción de Campos */

    private boolean p_IsTest;

    /** Descripción de Campos */

    private int p_M_DistributionList_ID;

//      DatePromised
//      C_DocType_ID

    /** Descripción de Campos */

    private MDistributionList m_dl;

    /** Descripción de Campos */

    private MOrder m_singleOrder = null;

    /** Descripción de Campos */

    private MProduct m_product = null;

    /** Descripción de Campos */

    private BigDecimal m_totalQty = Env.ZERO;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            // log.fine("prepare - " + para[i]);

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "M_Product_ID" )) {
                p_M_Product_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "Qty" )) {
                p_Qty = ( BigDecimal )para[ i ].getParameter();
            } else if( name.equals( "IsCreateSingleOrder" )) {
                p_IsCreateSingleOrder = "Y".equals( para[ i ].getParameter());
            } else if( name.equals( "Bill_BPartner_ID" )) {
                p_Bill_BPartner_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "p_Bill_Location_ID" )) {
                p_Bill_Location_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "IsTest" )) {
                p_IsTest = "Y".equals( para[ i ].getParameter());
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_M_DistributionList_ID = getRecord_ID();
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
        log.info( "doIt - M_DistributionList_ID=" + p_M_DistributionList_ID + ", M_Product_ID=" + p_M_Product_ID + ", Qty=" + p_Qty + ", Test=" + p_IsTest );

        if( p_IsCreateSingleOrder ) {
            log.info( "doIt - SingleOrder=" + p_IsCreateSingleOrder + ", BPartner_ID=" + p_Bill_BPartner_ID + ", Location_ID=" + p_Bill_Location_ID );
        }

        //

        if( p_M_DistributionList_ID == 0 ) {
            throw new IllegalArgumentException( "No Distribution List ID" );
        }

        m_dl = new MDistributionList( getCtx(),p_M_DistributionList_ID,get_TrxName());

        if( m_dl.getID() == 0 ) {
            throw new Exception( "Distribution List not found -  M_DistributionList_ID=" + p_M_DistributionList_ID );
        }

        //

        if( p_M_Product_ID == 0 ) {
            throw new IllegalArgumentException( "No Product" );
        }

        m_product = MProduct.get( getCtx(),p_M_Product_ID );

        if( m_product.getID() == 0 ) {
            throw new Exception( "Product not found -  M_Product_ID=" + p_M_Product_ID );
        }

        if( (p_Qty == null) || (p_Qty.signum() != 1) ) {
            throw new IllegalArgumentException( "No Quantity" );
        }

        //

        if( p_IsCreateSingleOrder && (p_Bill_BPartner_ID == 0) ) {
            throw new IllegalArgumentException( "Invoice Business Partner required for single Order" );
        }

        // Create Single Order

        if( !p_IsTest && p_IsCreateSingleOrder ) {
            MBPartner bp = new MBPartner( getCtx(),p_Bill_BPartner_ID,get_TrxName());

            if( bp.getID() == 0 ) {
                throw new IllegalArgumentException( "Single Business Partner not found - C_BPartner_ID=" + p_Bill_BPartner_ID );
            }

            //

            m_singleOrder = new MOrder( getCtx(),0,get_TrxName());
            m_singleOrder.setIsSOTrx( true );
            m_singleOrder.setC_DocTypeTarget_ID( MOrder.DocSubTypeSO_Standard );
            m_singleOrder.setBPartner( bp );

            if( p_Bill_Location_ID != 0 ) {
                m_singleOrder.setC_BPartner_Location_ID( p_Bill_Location_ID );
            }

            if( !m_singleOrder.save()) {
                throw new IllegalStateException( "Single Order not created" );
            }
        }

        MDistributionListLine[] lines   = m_dl.getLines();
        int                     counter = 0;

        for( int i = 0;i < lines.length;i++ ) {
            if( createOrder( lines[ i ] )) {
                counter++;
            }
        }

        // Update Qty

        if( m_singleOrder != null ) {
            m_singleOrder.setDescription( "# " + counter + " - " + m_totalQty );
            m_singleOrder.save();
        }

        return "@Created@ #" + counter + " - @Qty@=" + m_totalQty;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param dll
     *
     * @return
     */

    private boolean createOrder( MDistributionListLine dll ) {
        MBPartner bp = new MBPartner( getCtx(),dll.getC_BPartner_ID(),get_TrxName());

        if( bp.getID() == 0 ) {
            throw new IllegalArgumentException( "Business Partner not found - C_BPartner_ID=" + dll.getC_BPartner_ID());
        }

        // Create Order

        MOrder order = m_singleOrder;

        if( !p_IsTest && (order == null) ) {
            order = new MOrder( getCtx(),0,get_TrxName());
            order.setIsSOTrx( true );
            order.setC_DocTypeTarget_ID( MOrder.DocSubTypeSO_Standard );
            order.setBPartner( bp );

            if( dll.getC_BPartner_Location_ID() != 0 ) {
                order.setC_BPartner_Location_ID( dll.getC_BPartner_Location_ID());
            }

            if( !order.save()) {
                log.log( Level.SEVERE,"createOrder - Order not saved" );

                return false;
            }
        }

        // Calculate Qty

        BigDecimal ratio = dll.getRatio();
        BigDecimal qty   = p_Qty.multiply( ratio );

        if( qty.compareTo( Env.ZERO ) != 0 ) {
            qty = qty.divide( m_dl.getRatioTotal(),m_product.getStandardPrecision(),BigDecimal.ROUND_HALF_UP );
        }

        BigDecimal minQty = dll.getMinQty();

        if( qty.compareTo( minQty ) < 0 ) {
            qty = minQty;
        }

        m_totalQty = m_totalQty.add( qty );

        //

        if( p_IsTest ) {
            addLog( 0,null,qty,bp.getName());

            return false;
        }

        // Create Order Line

        MOrderLine line = new MOrderLine( order );

        line.setC_BPartner_ID( dll.getC_BPartner_ID());

        if( dll.getC_BPartner_Location_ID() != 0 ) {
            line.setC_BPartner_Location_ID( dll.getC_BPartner_Location_ID());
        }

        //

        line.setM_Product_ID( p_M_Product_ID,true );
        line.setQty( qty );
        line.setPrice();

        if( !line.save()) {
            log.log( Level.SEVERE,"createOrder - OrderLine not saved" );

            return false;
        }

        addLog( 0,null,qty,order.getDocumentNo() + ": " + bp.getName());

        return true;
    }    // createOrder
}    // DistributionCreate



/*
 *  @(#)DistributionCreate.java   02.07.07
 * 
 *  Fin del fichero DistributionCreate.java
 *  
 *  Versión 2.2
 *
 */
