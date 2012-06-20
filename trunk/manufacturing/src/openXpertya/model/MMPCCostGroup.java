/*
 * @(#)MMPCCostGroup.java   13.jun 2007  Versión 2.2
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

import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;
import org.openXpertya.wf.*;

/**
 *      Cost Element
 *
 *  @author Jorg Janke
 *  @version $Id: MProductCosting.java,v 1.4 2004/05/13 06:05:22 jjanke Exp $
 */
public class MMPCCostGroup extends X_MPC_Cost_Group {

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
    MMPCCostGroup[]	m_lines	= null;

    /** Descripción de Campo */
    private static CLogger	log	= CLogger.getCLogger(MMPCCostGroup.class);

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param M_Product_Costing_ID id
     * @param MPC_Cost_Group_ID
     * @param trxName
     */
    public MMPCCostGroup(Properties ctx, int MPC_Cost_Group_ID, String trxName) {

        super(ctx, MPC_Cost_Group_ID, trxName);

        if (MPC_Cost_Group_ID == 0) {

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
    public MMPCCostGroup(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /**
     *      Get Element Cost
     *      @return lines
     */
    public MMPCCostGroup[] getCostGroups() {

        // if (m_lines != null && !requery)
        // return m_lines;
        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM MPC_Cost_Group WHERE AD_Client_ID = " + getAD_Client_ID();
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCCostGroup(getCtx(), rs, "MPC_Cost_Group"));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getCostGroups" + sql, ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {
            log.log(Level.SEVERE, "getCostGroups" + sql, ex1);
        }

        pstmt	= null;

        //
        m_lines	= new MMPCCostGroup[list.size()];
        list.toArray(m_lines);

        return m_lines;
    }		// getCostElement

    /**
     *      Get Element Cost
     *      @return lines
     */
    public static int getGLCostGroup() {

        int		MPC_Cost_Group_ID	= 0;
        ArrayList	list			= new ArrayList();
        String		sql			= "SELECT cg.MPC_Cost_Group_ID FROM MPC_Cost_Group cg WHERE cg.isGL = 'Y' AND cg.AD_Client_ID =" + Env.getContextAsInt(Env.getCtx(), "AD_Client_ID");

        log.info("getGLCostGroup() sql:" + sql);

        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                MPC_Cost_Group_ID	= rs.getInt(1);
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {

            log.log(Level.SEVERE, "getCostGroups" + sql, ex);

            return 0;
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //

        return MPC_Cost_Group_ID;

    }		// getCostElement
}	// Cost Element



/*
 * @(#)MMPCCostGroup.java   13.jun 2007
 * 
 *  Fin del fichero MMPCCostGroup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
