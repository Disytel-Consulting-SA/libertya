/*
 * @(#)MMPCOrderCost.java   13.jun 2007  Versión 2.2
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
public class MMPCOrderCost extends X_MPC_Order_Cost {

    /**
     *  Default Constructor
     *  @param ctx context
     *  @param  C_Order_ID    order to load, (0 create new order)
     * @param MPC_Order_Cost_ID
     * @param trxName
     */
    public MMPCOrderCost(Properties ctx, int MPC_Order_Cost_ID, String trxName) {

        super(ctx, MPC_Order_Cost_ID, trxName);

        // New
        if (MPC_Order_Cost_ID == 0) {}

    }		// MOrder

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param MPC_Product_Costing
     * @param MPC_Order_ID
     * @param trxName
     */
    public MMPCOrderCost(Properties ctx, MMPCProductCosting MPC_Product_Costing, int MPC_Order_ID, String trxName) {

        super(ctx, 0, trxName);
        setC_AcctSchema_ID(MPC_Product_Costing.getC_AcctSchema_ID());
        setCostCumAmt(MPC_Product_Costing.getCostCumAmt());
        setCostCumQty(MPC_Product_Costing.getCostCumQty());
        setCostLLAmt(MPC_Product_Costing.getCostLLAmt());
        setCostTLAmt(MPC_Product_Costing.getCostTLAmt());
        setM_Product_ID(MPC_Product_Costing.getM_Product_ID());
        setM_Warehouse_ID(MPC_Product_Costing.getM_Warehouse_ID());
        setMPC_Cost_Element_ID(MPC_Product_Costing.getMPC_Cost_Element_ID());
        setS_Resource_ID(MPC_Product_Costing.getS_Resource_ID());
        save(get_TrxName());

    }		// MOrder

    /**
     *  Load Constructor
     *  @param ctx context
     *  @param rs result set record
     * @param trxName
     */
    public MMPCOrderCost(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, "MPC_Order_Cost");
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

    // save
}	// MOrder



/*
 * @(#)MMPCOrderCost.java   13.jun 2007
 * 
 *  Fin del fichero MMPCOrderCost.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
