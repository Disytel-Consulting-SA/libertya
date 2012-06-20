/*
 * @(#)MMPCOrderBOM.java   13.jun 2007  Versión 2.2
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
 *  Order Model.
 *      Please do not set DocStatus and C_DocType_ID directly.
 *      They are set in the process() method.
 *      Use DocAction and C_DocTypeTarget_ID instead.
 *
 *  @author Jorg Janke
 *  @version $Id: MOrder.java,v 1.40 2004/04/13 04:19:30 jjanke Exp $
 */
public class MMPCOrderBOM extends X_MPC_Order_BOM {

    /**
     *  Default Constructor
     *  @param ctx context
     *  @param  C_Order_ID    order to load, (0 create new order)
     * @param MPC_Order_BOM_ID
     * @param trxName
     */
    public MMPCOrderBOM(Properties ctx, int MPC_Order_BOM_ID, String trxName) {

        super(ctx, MPC_Order_BOM_ID, trxName);

        // New
        if (MPC_Order_BOM_ID == 0) {

            // setDocStatus(DOCSTATUS_Drafted);
            // setDocAction (DOCACTION_Prepare);
            //
            // setDeliveryRule (DELIVERYRULE_Availability);
            // setFreightCostRule (FREIGHTCOSTRULE_FreightIncluded);
            // setInvoiceRule (INVOICERULE_Immediate);
            // setPaymentRule(PAYMENTRULE_OnCredit);
            // setPriorityRule (PRIORITYRULE_Medium);
            // setDeliveryViaRule (DELIVERYVIARULE_Pickup);
            //
            // setIsDiscountPrinted (false);
            // setIsSelected (false);
            // setIsTaxIncluded (false);
            // setIsSOTrx (true);
            // /setIsDropShip(false);
            // setSendEMail (false);
            //
            // setIsApproved(false);
            // setIsPrinted(false);
            // setIsCreditApproved(false);
            // setIsDelivered(false);
            // setIsInvoiced(false);
            // setIsTransferred(false);
            // setIsSelfService(false);
            //
            // setProcessed(false);
            // setProcessing(false);
            // setPosted(false);
            // setDateAcct (new Timestamp(System.currentTimeMillis()));
            // setDatePromised (new Timestamp(System.currentTimeMillis()));
            // setDateOrdered (new Timestamp(System.currentTimeMillis()));
            // setFreightAmt (Env.ZERO);
            // setChargeAmt (Env.ZERO);
            // setTotalLines (Env.ZERO);
            // setGrandTotal (Env.ZERO);
        }

    }		// MOrder

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param trxName
     */
    public MMPCOrderBOM(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MOrder

    /**
     *      Overwrite Client/Org if required
     *      @param AD_Client_ID client
     *      @param AD_Org_ID org
     */
    public void setClientOrg(int AD_Client_ID, int AD_Org_ID) {
        super.setClientOrg(AD_Client_ID, AD_Org_ID);
    }		// setClientOrg

    /**
     *      Set AD_Org_ID
     *      @param AD_Org_ID Org ID
     */
    public void setAD_Org_ID(int AD_Org_ID) {
        super.setAD_Org_ID(AD_Org_ID);
    }		// setAD_Org_ID

    /**
     * **********************************************************************
     *
     * @return
     */

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MMPCOrderBOM[").append(getID()).append("-").append(getDocumentNo()).append("]");

        return sb.toString();

    }		// toString

    /**
     *      Get Invoices of Order
     *      @return invoices
     */
    public MMPCOrderBOMLine[] getLines() {
        return getLines(getMPC_Order_BOM_ID());
    }		// getLines

    /**
     *      Get Invoices of Order
     *      @param C_Order_ID id
     *
     * @param MPC_Order_ID
     *      @return invoices
     */
    public MMPCOrderBOMLine[] getLines(int MPC_Order_ID) {

        ArrayList	list	= new ArrayList();
        QueryDB		query	= new QueryDB("org.openXpertya.model.X_MPC_Order_BOMLine");
        String		filter	= "MPC_Order_ID = " + MPC_Order_ID;
        List		results	= query.execute(filter);
        Iterator	select	= results.iterator();

        while (select.hasNext()) {

            X_MPC_Order_BOMLine	bomline	= (X_MPC_Order_BOMLine) select.next();

            System.out.println("linea de product bom2 ************ " + bomline.getMPC_Order_BOMLine_ID());
            list.add(new MMPCOrderBOMLine(getCtx(), bomline.getMPC_Order_BOM_ID(), "MPC_Order_BOM_Line"));

            // list.add(bomline);
        }

        MMPCOrderBOMLine[]	retValue	= new MMPCOrderBOMLine[list.size()];

        list.toArray(retValue);

        return retValue;

    }		// getLines
}	// MOrder



/*
 * @(#)MMPCOrderBOM.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderBOM.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
