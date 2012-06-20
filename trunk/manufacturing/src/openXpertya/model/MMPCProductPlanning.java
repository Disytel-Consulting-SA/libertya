/*
 * @(#)MMPCProductPlanning.java   13.jun 2007  Versión 2.2
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



package openXpertya.model;

//package openXpertya.model;

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;

import openXpertya.model.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.compiere.plaf.*;
import org.compiere.swing.*;

import java.awt.*;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.apps.form.*;
import org.openXpertya.apps.search.*;
import org.openXpertya.grid.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      Product Data Planning
 *
 *  @author Jorg Janke
 *  @version $Id: MCProductPlannning.java,v 1.4 2004/05/13 06:05:22 jjanke Exp $
 */
public class MMPCProductPlanning extends X_MPC_Product_Planning {

    /**
     *      Get from Cache
     *      @param ctx context
     *      @param M_Product_Costing_ID id
     *      @return
     */

    /*
     * public static MMPCProductCosting get (Properties ctx, int MPC_Product_Costing_ID)
     * {
     *       Integer ii = new Integer (MPC_Product_Costing_ID);
     *       MMPCProductCosting pc = (MMPCProductCosting)s_cache.get(ii);
     *       if (pc == null)
     *               pc = new MMPCProductCosting (ctx, MPC_Product_Costing_ID);
     *       return pc;
     * }       //      get
     */

    /** Cache */

    // private static CCache s_cache = new CCache ("M_Product_Costing", 20);

    /** Log */
    private static CLogger	log	= CLogger.getCLogger(MMPCProductPlanning.class);

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param M_Product_Costing_ID id
     * @param MPC_Product_Planning_ID
     * @param trxName
     */
    public MMPCProductPlanning(Properties ctx, int MPC_Product_Planning_ID, String trxName) {

        super(ctx, MPC_Product_Planning_ID, trxName);

        if (MPC_Product_Planning_ID == 0) {

            setIsDemand(false);
            setIsSupply(false);

            /*
             * setC_AcctSchema_ID(0);
             * setCostCumAmt();
             * setCostCumQty();
             * setCostLLAmt();
             * setCostTLAmt();
             * setM_Product_ID();
             * setM_Warehouse_ID();
             * setMPC_Cost_Element_ID();
             * stS_Resource_ID();
             */
        }

    }		// MPCCostElement

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MMPCProductPlanning(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_Warehouse_ID
     * @param S_Resource_ID
     *
     * @return
     */
    public static MMPCProductPlanning get(Properties ctx, int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID, int S_Resource_ID) {

        // int AD_Org_ID = Env.getContextAsInt(ctx, "AD_Org_ID");
        System.out.println("Ad_Org_ID" + AD_Org_ID + "M_Product_ID" + M_Product_ID + "M_Warehouse_ID" + M_Warehouse_ID + "S_Resource_ID" + S_Resource_ID);

        String	sql	= "SELECT * FROM MPC_Product_Planning  pp WHERE pp.AD_Org_ID = ? AND pp.M_Product_ID = ? AND pp.M_Warehouse_ID = ? AND pp.S_Resource_ID = ? ";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Org_ID);
            pstmt.setInt(2, M_Product_ID);
            pstmt.setInt(3, M_Warehouse_ID);
            pstmt.setInt(4, S_Resource_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                return new MMPCProductPlanning(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductPlanning", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_Warehouse_ID
     *
     * @return
     */
    public static MMPCProductPlanning getDemandWarehouse(Properties ctx, int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID) {

        // int AD_Org_ID = Env.getContextAsInt(ctx, "AD_Org_ID");
        System.out.println("Ad_Org_ID" + AD_Org_ID + "M_Product_ID" + M_Product_ID + "M_Warehouse_ID" + M_Warehouse_ID);

        String	sql	= "SELECT * FROM MPC_Product_Planning  pp WHERE pp.AD_Org_ID = ? AND pp.M_Product_ID = ? AND pp.M_Warehouse_ID = ? AND pp.IsDemand =  'Y'";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Org_ID);
            pstmt.setInt(2, M_Product_ID);
            pstmt.setInt(3, M_Warehouse_ID);

            // pstmt.setInt(4, S_Resource_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                return new MMPCProductPlanning(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductPlanning", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param S_Resource_ID
     *
     * @return
     */
    public static MMPCProductPlanning getDemandSupplyResource(Properties ctx, int AD_Org_ID, int M_Product_ID, int S_Resource_ID) {

        // int AD_Org_ID = Env.getContextAsInt(ctx, "AD_Org_ID");
        System.out.println("Ad_Org_ID" + AD_Org_ID + "M_Product_ID" + M_Product_ID + "S_Resource_ID" + S_Resource_ID);

        String	sql	= "SELECT * FROM MPC_Product_Planning  pp WHERE pp.AD_Org_ID = ? AND pp.M_Product_ID = ? AND pp.S_Resource_ID = ? AND  pp.IsSupply =  'Y' AND pp.IsDemand = 'Y'";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Org_ID);
            pstmt.setInt(2, M_Product_ID);
            pstmt.setInt(3, S_Resource_ID);

            // pstmt.setInt(4, S_Resource_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                return new MMPCProductPlanning(ctx, rs, null);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (Exception e) {
            log.log(Level.SEVERE, "getProductPlanning", e);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

            pstmt	= null;

        } catch (Exception e) {
            pstmt	= null;
        }

        return null;
    }
}	// Product Data Planning



/*
 * @(#)MMPCProductPlanning.java   13.jun 2007
 * 
 *  Fin del fichero MMPCProductPlanning.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
