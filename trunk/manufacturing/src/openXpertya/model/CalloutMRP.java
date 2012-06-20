/*
 * @(#)CalloutMRP.java   13.jun 2007  Versión 2.2
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

import java.math.*;

import java.sql.*;

import java.util.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;
import org.openXpertya.wf.*;

/**
 *      Order Callouts.
 *
 *  @author Fundesle
 *  @version $Id: CalloutOrder.java,v 2.0 $
 */
public class CalloutMRP extends CalloutEngine {

/**     Debug Steps                     */
    private boolean	steps	= false;

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
    public String OrderLine(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        setCalloutActive(true);

        String	sql	= new String("SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.C_OrderLine_ID = ? ");

        // MOrderLine ol = new MOrderLine(Env.getCtx(), C_OrderLine_ID);
        Integer	C_OrderLine_ID	= (Integer) mTab.getValue("C_OrderLine_ID");
        Integer	M_Product_ID	= (Integer) mTab.getValue("M_Product_ID");

        if (C_OrderLine_ID != null) {

            String	Desc		= (String) mTab.getValue("Description");
            Timestamp	Today		= new Timestamp(System.currentTimeMillis());
            String	Name		= Today.toString();
            BigDecimal	QtyOrdered	= (BigDecimal) mTab.getValue("QtyOrdered");
            BigDecimal	QtyDelivered	= (BigDecimal) mTab.getValue("QtyDelivered");
            Timestamp	DatePromised	= (Timestamp) mTab.getValue("DatePromised");
            Timestamp	DateOrdered	= (Timestamp) mTab.getValue("DateOrdered");

            // int M_Product_ID = ((Integer)mTab.getValue("M_Product_ID")).intValue();
            int	M_Warehouse_ID	= ((Integer) mTab.getValue("M_Warehouse_ID")).intValue();
            int	C_Order_ID	= ((Integer) mTab.getValue("C_Order_ID")).intValue();
            int	C_BPartner_ID	= ((Integer) mTab.getValue("C_BPartner_ID")).intValue();
            boolean	IsSOTrx	= "Y".equals(Env.getContext(ctx, WindowNo, "IsSOTrx"));
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setInt(1, C_OrderLine_ID.intValue());

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), rs.getInt(1), "MPC_MRP");

                    if (QtyOrdered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0) {

                        mrp.setDescription(Desc);
                        mrp.setC_BPartner_ID(C_BPartner_ID);
                        mrp.setQty(QtyOrdered.subtract(QtyDelivered));
                        mrp.setDatePromised(DatePromised);
                        mrp.setDateOrdered(DateOrdered);
                        mrp.setM_Product_ID(M_Product_ID.intValue());
                        mrp.setM_Warehouse_ID(M_Warehouse_ID);
                        mrp.save();

                    } else {
                        mrp.delete(true);
                    }
                }

                if ((rs.getRow() == 0) && (QtyOrdered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0)) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), 0, "MPC_MRP");

                    mrp.setC_OrderLine_ID(C_OrderLine_ID.intValue());
                    mrp.setC_BPartner_ID(C_BPartner_ID);
                    mrp.setName(Name);
                    mrp.setDescription(Desc);
                    mrp.setC_Order_ID(C_Order_ID);
                    mrp.setQty(QtyOrdered.subtract(QtyDelivered));
                    mrp.setDatePromised(DatePromised);
                    mrp.setDateOrdered(DateOrdered);
                    mrp.setM_Product_ID(M_Product_ID.intValue());
                    mrp.setM_Warehouse_ID(M_Warehouse_ID);

                    // mrp.setS_Resource_ID();
                    if (IsSOTrx) {

                        mrp.setType("D");
                        mrp.setTypeMRP("SOO");

                    } else {

                        mrp.setType("S");
                        mrp.setTypeMRP("POO");
                    }

                    mrp.save();
                }

                rs.close();
                pstmt.close();

            } catch (Exception e) {

                // log.error ("doIt - " + sql, e);
                System.out.println("doIt - " + sql + e);
            }

        }	// C_OrderLine_ID !=  null

        return "";
    }

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
    public String MPCOrder(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        setCalloutActive(true);

        String	sql	= new String("SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.MPC_Order_ID = ? ");

        // MOrderLine ol = new MOrderLine(Env.getCtx(), C_OrderLine_ID);
        Integer	MPC_Order_ID	= ((Integer) mTab.getValue("MPC_Order_ID"));

        if (MPC_Order_ID != null) {

            String	Desc		= (String) mTab.getValue("Description");
            Timestamp	Today		= new Timestamp(System.currentTimeMillis());
            String	Name		= Today.toString();
            BigDecimal	QtyOrdered	= (BigDecimal) mTab.getValue("QtyOrdered");
            BigDecimal	QtyDelivered	= (BigDecimal) mTab.getValue("QtyDelivered");
            Timestamp	DatePromised	= (Timestamp) mTab.getValue("DatePromised");
            Timestamp	DateOrdered	= (Timestamp) mTab.getValue("DateOrdered");
            int		M_Product_ID	= ((Integer) mTab.getValue("M_Product_ID")).intValue();
            int	M_Warehouse_ID	= ((Integer) mTab.getValue("M_Warehouse_ID")).intValue();

            // int C_Order_ID = ((Integer)mTab.getValue("C_Order_ID")).intValue();
            MMPCOrder	o	= new MMPCOrder(Env.getCtx(), MPC_Order_ID.intValue(), "MPC_Order");
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setInt(1, MPC_Order_ID.intValue());

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), rs.getInt(1), "MPC_MRP");

                    if (QtyOrdered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0) {

                        mrp.setDescription(Desc);
                        mrp.setQty(QtyOrdered.subtract(QtyDelivered));
                        mrp.setDatePromised(DatePromised);
                        mrp.setDateOrdered(DateOrdered);
                        mrp.setM_Product_ID(M_Product_ID);
                        mrp.setM_Warehouse_ID(M_Warehouse_ID);
                        mrp.save();

                    } else {
                        mrp.delete(true);
                    }
                }

                if ((rs.getRow() == 0) || (QtyOrdered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0)) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), 0, "MPC_MRP");

                    mrp.setMPC_Order_ID(MPC_Order_ID.intValue());
                    mrp.setDescription(Desc);
                    mrp.setName(Name);

                    // mrp.setC_Order_ID(o.getC_Order_ID());
                    mrp.setQty(QtyOrdered.subtract(QtyDelivered));
                    mrp.setDatePromised(DatePromised);
                    mrp.setDateOrdered(DateOrdered);
                    mrp.setM_Product_ID(M_Product_ID);
                    mrp.setM_Warehouse_ID(M_Warehouse_ID);

                    // mrp.setS_Resource_ID();
                    mrp.setType("S");
                    mrp.setTypeMRP("MOP");
                    mrp.save();
                }

                rs.close();
                pstmt.close();

            } catch (Exception e) {

                // log.error ("doIt - " + sql, e);
                System.out.println("doIt - " + sql + e);
            }
        }

        return "";
    }

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
    public String MPCOrderLine(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        setCalloutActive(true);

        String	sql	= new String("SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE mrp.MPC_Order_BOMLine_ID = ? ");

        // MOrderLine ol = new MOrderLine(Env.getCtx(), C_OrderLine_ID);
        Integer	MPC_Order_BOMLine_ID	= ((Integer) mTab.getValue("MPC_Order_ID"));

        if (MPC_Order_BOMLine_ID != null) {

            String	Desc		= (String) mTab.getValue("Description");
            Timestamp	Today		= new Timestamp(System.currentTimeMillis());
            String	Name		= Today.toString();
            BigDecimal	QtyRequiered	= (BigDecimal) mTab.getValue("QtyRequiered");
            BigDecimal	QtyDelivered	= (BigDecimal) mTab.getValue("QtyDelivered");
            Timestamp	DatePromised	= (Timestamp) mTab.getValue("DatePromised");
            Timestamp	DateOrdered	= (Timestamp) mTab.getValue("DateOrdered");
            int		M_Product_ID	= ((Integer) mTab.getValue("M_Product_ID")).intValue();
            int	M_Warehouse_ID	= ((Integer) mTab.getValue("M_Warehouse_ID")).intValue();
            MMPCOrderBOMLine	ol	= new MMPCOrderBOMLine(Env.getCtx(), MPC_Order_BOMLine_ID.intValue(), "MPC_Order_BOM_Line");
            MMPCOrder	o	= new MMPCOrder(Env.getCtx(), ol.getMPC_Order_ID(), "MPC_Order");
            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql);
                pstmt.setInt(1, MPC_Order_BOMLine_ID.intValue());

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), rs.getInt(1), "MPC_MRP");

                    if (QtyRequiered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0) {

                        mrp.setDescription(o.getDescription());
                        mrp.setQty(ol.getQtyRequiered().subtract(ol.getQtyDelivered()));
                        mrp.setDatePromised(o.getDatePromised());
                        mrp.setDateOrdered(o.getDateOrdered());
                        mrp.setM_Product_ID(ol.getM_Product_ID());
                        mrp.setM_Warehouse_ID(ol.getM_Warehouse_ID());
                        mrp.save();

                    } else {
                        mrp.delete(true);
                    }
                }

                if ((rs.getRow() == 0) || (QtyRequiered.subtract(QtyDelivered).compareTo(Env.ZERO) > 0)) {

                    MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), 0, "MPC_MRP");

                    // MOrder o = new MOrder(Env.getCtx(), ol.getC_Order_ID());

                    mrp.setMPC_Order_BOMLine_ID(MPC_Order_BOMLine_ID.intValue());
                    mrp.setDescription(Desc);
                    mrp.setName(Name);
                    mrp.setMPC_Order_ID(o.getMPC_Order_ID());
                    mrp.setQty(QtyRequiered.subtract(QtyDelivered));
                    mrp.setDatePromised(DatePromised);
                    mrp.setDateOrdered(DateOrdered);
                    mrp.setM_Product_ID(M_Product_ID);
                    mrp.setM_Warehouse_ID(M_Warehouse_ID);

                    // mrp.setS_Resource_ID();
                    mrp.setType("D");
                    mrp.setTypeMRP("MOP");
                    mrp.save();
                }

                rs.close();
                pstmt.close();

            } catch (Exception e) {

                // log.error ("doIt - " + sql, e);
                System.out.println("doIt - " + sql + e);
            }
        }

        return "";
    }
}



/*
 * @(#)CalloutMRP.java   13.jun 2007
 * 
 *  Fin del fichero CalloutMRP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
