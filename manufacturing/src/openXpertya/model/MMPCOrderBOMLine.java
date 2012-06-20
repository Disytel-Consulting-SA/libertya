/*
 * @(#)MMPCOrderBOMLine.java   13.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



//package org.openXpertya.mfg.model;
package openXpertya.model;

import java.util.*;

import java.sql.*;

import java.math.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *  Order Line Model.
 *      <code>
 *                      MOrderLine ol = new MOrderLine(m_order);
                        ol.setM_Product_ID(wbl.getM_Product_ID());
                        ol.setQtyOrdered(wbl.getQuantity());
                        ol.setPrice();
                        ol.setPriceActual(wbl.getPrice());
                        ol.setTax();
                        ol.save();

 *      </code>
 *  @author Jorg Janke
 *  @version $Id: MOrderLine.java,v 1.22 2004/03/22 07:15:03 jjanke Exp $
 */
public class MMPCOrderBOMLine extends X_MPC_Order_BOMLine {

    /**
     *  Default Constructor
     *  @param ctx context
     *  @param  C_OrderLine_ID  order line to load
     * @param MPC_Order_BOMLine_ID
     * @param trxName
     */
    public MMPCOrderBOMLine(Properties ctx, int MPC_Order_BOMLine_ID, String trxName) {

        super(ctx, MPC_Order_BOMLine_ID, trxName);

        /*
         *  setQtyDelivered(new BigDecimal(0));
         * setQtyPost(new BigDecimal(0));
         * setQtyReject(new BigDecimal(0));
         * setQtyRequiered(new BigDecimal(0));
         * setQtyReserved(new BigDecimal(0));
         * setQtyScrap(new BigDecimal(0));
         */

    }		// MOrderLine

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param trxName
     */
    public MMPCOrderBOMLine(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MOrderLine

    /**
     *      Set Defaults from Order.
     *      Does not set Parent !!
     *      @param order order
     *
     * @param bom
     */
    public void setMMPCOrderBOM(MMPCOrderBOM bom) {

        setClientOrg(bom);

        // setC_BPartner_ID(order.getC_BPartner_ID());
        // setC_BPartner_Location_ID(order.getC_BPartner_Location_ID());
        // setM_Warehouse_ID(order.getM_Warehouse_ID());
        // setDateOrdered(order.getDateOrdered());
        // setDatePromised(order.getDatePromised());
        // m_M_PriceList_ID = order.getM_PriceList_ID();
        // m_IsSOTrx = order.isSOTrx();
        // setC_Currency_ID(order.getC_Currency_ID());

    }		// setOrder

    /** Descripción de Campo */
    private MProduct	m_product	= null;

    /** Descripción de Campo */
    private int	m_M_Locator_ID	= 0;

    /**
     *      Before Save
     *      @param newRecord new
     *      @return true/false
     */
    protected boolean beforeSave(boolean newRecord) {

        // Phantom
        if (MMPCProductBOMLine.COMPONENTTYPE_Phantom.equals(getComponentType()) && newRecord) {

            int	MPC_Product_BOM_ID	= MMPCProductBOM.getBOMSearchKey(getM_Product_ID());

            if (MPC_Product_BOM_ID == 0) {
                return true;
            }

            // System.out.println("getComponentType()"+ getComponentType());
            // System.out.println("MPC_Product_BOM_ID"+ MPC_Product_BOM_ID);
            MMPCProductBOM	bom	= new MMPCProductBOM(getCtx(), MPC_Product_BOM_ID, null);

            if (bom != null) {

                MMPCProductBOMLine[]	MPC_Product_BOMline	= bom.getLines();

                if (MPC_Product_BOMline == null) {
                    return true;
                }

                for (int i = 0; i < MPC_Product_BOMline.length; i++) {

                    MMPCOrderBOMLine	MPC_Order_BOMLine	= new MMPCOrderBOMLine(getCtx(), 0, null);
                    MProduct	product	= new MProduct(getCtx(), MPC_Product_BOMline[i].getM_Product_ID(), null);

                    // System.out.println("MProduct_ID"+ product.getName());
                    // System.out.println("QtyRequerided"+ getQtyRequiered());
                    MPC_Order_BOMLine.setAssay(MPC_Product_BOMline[i].getAssay());
                    MPC_Order_BOMLine.setQtyBatch(MPC_Product_BOMline[i].getQtyBatch());
                    MPC_Order_BOMLine.setQtyBOM(MPC_Product_BOMline[i].getQtyBOM());
                    MPC_Order_BOMLine.setIsQtyPercentage(MPC_Product_BOMline[i].isQtyPercentage());
                    MPC_Order_BOMLine.setComponentType(MPC_Product_BOMline[i].getComponentType());
                    MPC_Order_BOMLine.setC_UOM_ID(MPC_Product_BOMline[i].getC_UOM_ID());
                    MPC_Order_BOMLine.setForecast(MPC_Product_BOMline[i].getForecast());
                    MPC_Order_BOMLine.setIsCritical(MPC_Product_BOMline[i].isCritical());
                    MPC_Order_BOMLine.setIssueMethod(MPC_Product_BOMline[i].getIssueMethod());
                    MPC_Order_BOMLine.setLine(MPC_Product_BOMline[i].getLine());
                    MPC_Order_BOMLine.setLTOffSet(MPC_Product_BOMline[i].getLTOffSet());
                    MPC_Order_BOMLine.setM_AttributeSetInstance_ID(MPC_Product_BOMline[i].getM_AttributeSetInstance_ID());
                    MPC_Order_BOMLine.setMPC_Order_BOM_ID(getMPC_Order_BOM_ID());
                    MPC_Order_BOMLine.setMPC_Order_ID(getMPC_Order_ID());
                    MPC_Order_BOMLine.setM_Product_ID(MPC_Product_BOMline[i].getM_Product_ID());
                    MPC_Order_BOMLine.setScrap(MPC_Product_BOMline[i].getScrap());
                    MPC_Order_BOMLine.setValidFrom(MPC_Product_BOMline[i].getValidFrom());
                    MPC_Order_BOMLine.setValidTo(MPC_Product_BOMline[i].getValidTo());
                    MPC_Order_BOMLine.setM_Warehouse_ID(getM_Warehouse_ID());

                    if (MPC_Order_BOMLine.isQtyPercentage()) {

                        BigDecimal	qty	= MPC_Order_BOMLine.getQtyBatch().multiply(getQtyRequiered());

                        if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Packing)) {
                            MPC_Order_BOMLine.setQtyRequiered(qty.divide(new BigDecimal(100), 0, qty.ROUND_UP));
                        } else if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Component)) {
                            MPC_Order_BOMLine.setQtyRequiered(qty.divide(new BigDecimal(100), 4, qty.ROUND_UP));
                        } else if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Tools)) {
                            MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM());
                        }

                    } else {

                        if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Component)) {
                            MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM().multiply(getQtyRequiered()));
                        }

                        if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Packing)) {
                            MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM().multiply(getQtyRequiered()));
                        } else if (MPC_Order_BOMLine.getComponentType().equals(MPC_Order_BOMLine.COMPONENTTYPE_Tools)) {
                            MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyBOM());
                        }
                    }

                    // Set Scrap of Component
                    BigDecimal	Scrap	= new BigDecimal(MPC_Order_BOMLine.getScrap());

                    if (!Scrap.equals(Env.ZERO)) {

                        Scrap	= Scrap.divide(new BigDecimal(100), 4, BigDecimal.ROUND_UP);
                        MPC_Order_BOMLine.setQtyRequiered(MPC_Order_BOMLine.getQtyRequiered().divide(Env.ONE.subtract(Scrap), 4, BigDecimal.ROUND_HALF_UP));
                    }

                    MPC_Order_BOMLine.save(get_TrxName());
                }
            }

        }	// end Phantom

        return true;
    }

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MPCProductBOMLine[").append(getID()).append("]");

        return sb.toString();
    }

    /**
     *      Set UOM.
     *      make access public
     *      @param C_UOM_ID uom
     */
    public void setC_UOM_ID(int C_UOM_ID) {
        super.setC_UOM_ID(C_UOM_ID);
    }		// setC_UOM_ID

    /**
     *       Get Product
     *       @return product or null
     */
    public MProduct getProduct() {

        if ((m_product == null) && (getM_Product_ID() != 0)) {
            m_product	= new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
        }

        return m_product;
    }

    /**
     *      Save Temp M_Locator_ID
     *      @param M_Locator_ID id
     */
    public void setTempM_Locator_ID(int M_Locator_ID) {
        m_M_Locator_ID	= M_Locator_ID;
    }		// setTempM_Locator_ID
}	// MOrderLine



/*
 * @(#)MMPCOrderBOMLine.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderBOMLine.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
