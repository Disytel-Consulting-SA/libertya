/*
 * @(#)CalloutBOM.java   13.jun 2007  Versión 2.2
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

import java.util.*;

import java.sql.*;

import javax.swing.*;
import javax.swing.tree.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.util.*;

/**
 *      Order Callouts.
 *
 *  @author Jorg Janke
 *  @version $Id: CalloutOrder.java,v 2.0 $
 */
public class CalloutBOM extends CalloutEngine {

    /** Debug Steps */
    private boolean	steps	= false;

    /**
     *      Order Header Change - DocType.
     *              - InvoiceRuld/DeliveryRule/PaymentRule
     *              - temporary Document
     *  Context:
     *      - DocSubTypeSO
     *              - HasCharges
     *      - (re-sets Business Partner info of required)
     *
     *  @param ctx      Context
     *  @param WindowNo current Window No
     *  @param mTab     Model Tab
     *  @param mField   Model Field
     *  @param value    The new value
     *  @return Error message or ""
     */

    /**
     *      Parent cicle.
     *  @param ctx      Context
     *  @param WindowNo current Window No
     *  @param mTab     Model Tab
     *  @param mField   Model Field
     *  @param value    The new value
     */
    public String parent(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        if (isCalloutActive() || (value == null)) {
            return "";
        }

        setCalloutActive(true);

        if (steps) {
            log.warning("parent - init");
        }

        // BigDecimal QtyOrdered, PriceActual, PriceLimit, Discount, PriceList;
        // int StdPrecision = Env.getContextAsInt(ctx, WindowNo, "StdPrecision");

        /*
         * //      get values
         * QtyOrdered = (BigDecimal)mTab.getValue("QtyOrdered");
         * PriceActual = (BigDecimal)mTab.getValue("PriceActual");
         * //      PriceActual = PriceActual.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
         * Discount = (BigDecimal)mTab.getValue("Discount");
         * /
         * PriceLimit = (BigDecimal)mTab.getValue("PriceLimit");
         * //      PriceLimit = OriceLimit.setScale(StdPrecision, BigDecimal.ROUND_HALF_UP);
         * PriceList = (BigDecimal)mTab.getValue("PriceList");
         * log.fine("amt - Ordered=" + QtyOrdered + ", List=" + PriceList + ", Limit=" + PriceLimit + ", Precision=" + StdPrecision);
         * log.fine("amt ~ Actual=" + PriceActual + ", Discount=" + Discount);
         */

        // calculate Actual if discount entered
        if (mField.getColumnName().equals("M_Product_ID")) {

            Integer	M_Product_ID		= (Integer) value;
            int		MPC_Product_BOM_ID	= Env.getContextAsInt(ctx, WindowNo, "MPC_Product_BOM_ID");
            X_MPC_Product_BOM	MPC_Product_BOM	= new X_MPC_Product_BOM(ctx, MPC_Product_BOM_ID, "MPC_Product_BOM");

            if (MPC_Product_BOM.getM_Product_ID() == M_Product_ID.intValue()) {

                // ValueNamePair componet = new ValueNamePair("ValidComponent","Error Parent not be Componet");
                // mTab.fireDataStatusEEvent("ff","ff"); //("ValidComponent","Error Parent not be Componet");
                JOptionPane.showMessageDialog(null, "ValidComponent", "Error Parent not be Componet", JOptionPane.ERROR_MESSAGE);

                // ADialog.warn(WindowNo,this,"Error Parent not be Componet");
                // ADialog.
                return "";
            }
        }

        setCalloutActive(false);

        return "";

    }		// amt

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
    public String qty(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        if (isCalloutActive() || (value == null)) {
            return "";
        }

        setCalloutActive(true);

        int	M_Product_ID	= Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");

        if (steps) {
            log.warning("qty - init - M_Product_ID=" + M_Product_ID + " - ");
        }

        BigDecimal	QtyOrdered, QtyEntered;		// , PriceActual, PriceEntered;

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
            // log.fine("qty - UOM=" + C_UOM_To_ID
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

        //
        setCalloutActive(false);

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
    public String qtyLine(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {

        if (isCalloutActive() || (value == null)) {
            return "";
        }

        setCalloutActive(true);

        int	M_Product_ID	= Env.getContextAsInt(ctx, WindowNo, "M_Product_ID");

        if (steps) {
            log.warning("qty - init - M_Product_ID=" + M_Product_ID + " - ");
        }

        BigDecimal	QtyRequiered, QtyEntered;	// , PriceActual, PriceEntered;

        // No Product
        if (M_Product_ID == 0) {

            QtyEntered	= (BigDecimal) mTab.getValue("QtyEntered");
            mTab.setValue("QtyOrdered", QtyEntered);
        }

        // UOM Changed - convert from Entered -> Product
        else if (mField.getColumnName().equals("C_UOM_ID")) {

            int	C_UOM_To_ID	= ((Integer) value).intValue();

            QtyEntered		= (BigDecimal) mTab.getValue("QtyEntered");
            QtyRequiered	= MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);

            if (QtyRequiered == null) {
                QtyRequiered	= QtyEntered;
            }

            boolean	conversion	= QtyEntered.compareTo(QtyRequiered) != 0;

            // PriceActual = (BigDecimal)mTab.getValue("PriceActual");
            // PriceEntered = MUOMConversion.convertProductFrom (ctx, M_Product_ID,
            // C_UOM_To_ID, PriceActual);
            // if (PriceEntered == null)
            // PriceEntered = PriceActual;
            // log.fine("qty - UOM=" + C_UOM_To_ID
            // + ", QtyEntered/PriceActual=" + QtyEntered + "/" + PriceActual
            // + " -> " + conversion
            // + " QtyOrdered/PriceEntered=" + QtyOrdered + "/" + PriceEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyOrdered", QtyRequiered);

            // mTab.setValue("PriceEntered", PriceEntered);
        }

        // QtyEntered changed - calculate QtyOrdered
        else if (mField.getColumnName().equals("QtyEntered")) {

            int	C_UOM_To_ID	= Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");

            QtyEntered		= (BigDecimal) value;
            QtyRequiered	= MUOMConversion.convertProductFrom(ctx, M_Product_ID, C_UOM_To_ID, QtyEntered);

            if (QtyRequiered == null) {
                QtyRequiered	= QtyEntered;
            }

            boolean	conversion	= QtyEntered.compareTo(QtyRequiered) != 0;

            log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyEntered=" + QtyEntered + " -> " + conversion + " QtyOrdered=" + QtyRequiered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyOrdered", QtyRequiered);
        }

        // QtyOrdered changed - calculate QtyEntered
        else if (mField.getColumnName().equals("QtyOrdered")) {

            int	C_UOM_To_ID	= Env.getContextAsInt(ctx, WindowNo, "C_UOM_ID");

            QtyRequiered	= (BigDecimal) value;
            QtyEntered		= MUOMConversion.convertProductTo(ctx, M_Product_ID, C_UOM_To_ID, QtyRequiered);

            if (QtyEntered == null) {
                QtyEntered	= QtyRequiered;
            }

            boolean	conversion	= QtyRequiered.compareTo(QtyEntered) != 0;

            log.fine("qty - UOM=" + C_UOM_To_ID + ", QtyOrdered=" + QtyRequiered + " -> " + conversion + " QtyEntered=" + QtyEntered);
            Env.setContext(ctx, WindowNo, "UOMConversion", conversion
                    ? "Y"
                    : "N");
            mTab.setValue("QtyEntered", QtyEntered);
        }

        //
        setCalloutActive(false);

        return "";

    }		// qty
}	// CalloutOrder



/*
 * @(#)CalloutBOM.java   13.jun 2007
 * 
 *  Fin del fichero CalloutBOM.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
