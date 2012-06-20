/*
 * @(#)CalloutOrder.java   13.jun 2007  Versión 2.2
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
public class CalloutOrder extends CalloutEngine {

    /** Debug Steps */
    private boolean	steps	= false;

    /**
     *      Order Line - Quantity.
     *              - called from C_UOM_ID, QtyEntered, QtyOrdered
     *              - enforces qty UOM relationship
     *  @param ctx      Context
     *  @param WindowNo current Window No
     *  @param mTab     Model Tab
     *  @param mField   Model Field
     *  @param value    The new value
     *
     * @return
     */
    public String qty(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        if (isCalloutActive() || (value == null)) {
            return "";
        }

        // setCalloutActive(true);

        int	M_Product_ID	= Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");

        if (steps) {
            log.warning("qty - init - M_Product_ID=" + M_Product_ID + " - ");
        }

        BigDecimal	QtyOrdered	= Env.ZERO;
        BigDecimal	QtyEntered	= Env.ZERO;	// , PriceActual, PriceEntered;

        // No Product
        if (M_Product_ID == 0) {

            QtyEntered	= (BigDecimal) mTab.getValue("QtyEntered");
            mTab.setValue("QtyOrdered", QtyEntered);
        }

        // UOM Changed - convert from Entered -> Product
        else if (mField.getColumnName().equals("C_UOM_ID")) {

            int	C_UOM_To_ID	= ((Integer) value).intValue();

            QtyEntered	= (BigDecimal) mTab.getValue("QtyEntered");
            QtyOrdered	= MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);

            if (QtyOrdered == null) {
                QtyOrdered	= QtyEntered;
            }

            boolean	conversion	= QtyEntered.compareTo(QtyOrdered) != 0;

            // PriceActual = (BigDecimal)mTab.getValue("PriceActual");
            // PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
            // C_UOM_To_ID, PriceActual);
            // if (PriceEntered == null)
            // PriceEntered = PriceActual;
            // log.debug("qty - UOM=" + C_UOM_To_ID
            // + ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual
            // + " -> " + conversion
            // + " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyOrdered", QtyOrdered);

            // mTab.setValue("PriceEntered", PriceEntered);
        }

        // QtyEntered changed - calculate QtyOrdered
        else if (mField.getColumnName().equals("QtyEntered")) {

            int	C_UOM_To_ID	= Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");

            QtyEntered	= (BigDecimal) value;
            QtyOrdered	= MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);

            if (QtyOrdered == null) {
                QtyOrdered	= QtyEntered;
            }

            boolean	conversion	= QtyEntered.compareTo(QtyOrdered) != 0;

            log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyOrdered=" + QtyOrdered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyOrdered", QtyOrdered);
        }

        // QtyOrdered changed - calculate QtyEntered
        else if (mField.getColumnName().equals("QtyOrdered")) {

            int	C_UOM_To_ID	= Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");

            QtyOrdered	= (BigDecimal) value;
            QtyEntered	= MUOMConversion.convertProductTo(ctx, M_Product_ID, C_UOM_To_ID, QtyOrdered);

            if (QtyEntered == null) {
                QtyEntered	= QtyOrdered;
            }

            boolean	conversion	= QtyOrdered.compareTo(QtyEntered) != 0;

            log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyOrdered + " -> " + conversion + " QtyEntered=" + QtyEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyEntered", QtyEntered);
        }

        String	DocStatus	= (String) mTab.getValue("DocStatus");

        if (DocStatus.equals(MMPCOrder.DOCSTATUS_InProgress) || DocStatus.equals(MMPCOrder.DOCSTATUS_NotApproved)) {

            int	MPC_Order_ID	= ((Integer) mTab.getValue("MPC_Order_ID")).intValue();

            QtyOrdered	= ((BigDecimal) mTab.getValue("QtyOrdered"));

            MMPCOrderBOMLine[]	obl	= MMPCOrder.getLines(MPC_Order_ID);

            for (int i = 0; i < obl.length; i++) {

                if (obl[i].isQtyPercentage()) {

                    BigDecimal	qty	= obl[i].getQtyBatch().multiply(QtyOrdered);

                    obl[i].setQtyRequiered(qty.divide(new BigDecimal(100), qty.ROUND_UP));

                } else {
                    obl[i].setQtyRequiered(obl[i].getQtyBOM().multiply(QtyOrdered));
                }

                obl[i].save();
            }
        }

        //
        // setCalloutActive(true);
        // return qtyBatch(ctx,WindowNo,mTab,mField,value);
        return "";

    }		// qty

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
    public String qtyBatch(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        Integer		AD_Workflow_ID	= ((Integer) mTab.getValue("AD_Workflow_ID"));
        BigDecimal	p_QtyEntered	= (BigDecimal) mTab.getValue("QtyEntered");

        if (AD_Workflow_ID == null) {
            return "Data found";
        }

        MWorkflow	wf		= new MWorkflow(ctx, AD_Workflow_ID.intValue(), null);
        BigDecimal	Qty		= null;
        BigDecimal	QtyBatchSize	= wf.getQtyBatchSize().divide(new BigDecimal(1), 0, BigDecimal.ROUND_UP);

        System.out.println(">>>>>>>>>>>>>>>>>>> p_QtyEntered" + p_QtyEntered + " QtyBatchSize" + QtyBatchSize + "Env.ZERO " + Env.ZERO + "QtyBatchSize.equals(Env.ZERO)" + QtyBatchSize.equals(Env.ZERO));

        if (p_QtyEntered.equals(Env.ZERO)) {
            return "";
        }

        if (QtyBatchSize.equals(Env.ZERO)) {
            Qty	= Env.ONE;
        } else {
            Qty	= p_QtyEntered.divide(QtyBatchSize, 0, BigDecimal.ROUND_UP);
        }

        mTab.setValue("QtyBatchs", Qty);
        mTab.setValue("QtyBatchSize", p_QtyEntered.divide(Qty, BigDecimal.ROUND_HALF_UP));

        return "";
    }
}	// CalloutOrder



/*
 * @(#)CalloutOrder.java   13.jun 2007
 * 
 *  Fin del fichero CalloutOrder.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
