/*
 * @(#)MMPCProductCosting.java   13.jun 2007  Versión 2.2
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

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *      Product Costing Model
 *
 *  @author Jorg Janke
 *  @version $Id: MProductCosting.java,v 1.4 2004/05/13 06:05:22 jjanke Exp $
 */
public class MMPCProductCosting extends X_MPC_Product_Costing {

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
    static MMPCProductCosting[]	m_lines	= null;

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param M_Product_Costing_ID id
     * @param trxName
     */
    public MMPCProductCosting(Properties ctx, int M_Product_Costing_ID, String trxName) {

        super(ctx, M_Product_Costing_ID, trxName);

        if (M_Product_Costing_ID == 0) {

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

    }		// MProductCosting

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MMPCProductCosting(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MProductCosting

    /**
     *      Get Element Cost
     *
     * @param M_Product_ID
     * @param C_AcctSchema_ID
     * @param MPC_Cost_Group_ID
     * @param M_Warehouse_ID
     * @param S_Resource_ID
     *      @return lines
     */
    public static MMPCProductCosting[] getElements(int M_Product_ID, int C_AcctSchema_ID, int MPC_Cost_Group_ID, int M_Warehouse_ID, int S_Resource_ID) {

        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM MPC_Product_Costing WHERE AD_Client_ID = ? AND M_Product_ID=? AND  C_Acctschema_ID = ? AND MPC_Cost_Group_ID = ? AND M_Warehouse_ID = ? AND S_Resource_ID = ?  ";
        PreparedStatement	pstmt	= null;

        try {

            int	AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Client_ID);
            pstmt.setInt(2, M_Product_ID);
            pstmt.setInt(3, C_AcctSchema_ID);
            pstmt.setInt(4, MPC_Cost_Group_ID);
            pstmt.setInt(5, M_Warehouse_ID);
            pstmt.setInt(6, S_Resource_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCProductCosting(Env.getCtx(), rs, "MPC_Product_Costing"));
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {

            // log.error("getLines", ex);
            System.out.println("getLines" + ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //
        m_lines	= new MMPCProductCosting[list.size()];
        list.toArray(m_lines);

        return m_lines;

    }		// getMInOutLines

    /**
     *      Get Element Cost
     *
     * @param M_Product_ID
     * @param C_AcctSchema_ID
     * @param MPC_Cost_Group_ID
     * @param MPC_Cost_Element_ID
     * @param M_Warehouse_ID
     * @param S_Resource_ID
     *      @return lines
     */
    public boolean getElement(int M_Product_ID, int C_AcctSchema_ID, int MPC_Cost_Group_ID, int MPC_Cost_Element_ID, int M_Warehouse_ID, int S_Resource_ID) {

        // if (m_lines != null && !requery)
        // return m_lines;
        // ArrayList list = new ArrayList();
        MMPCProductCosting	pc	= null;
        String			sql	= "SELECT * FROM MPC_Product_Costing WHERE AD_Client_ID =? AND  M_Product_ID=? AND ( C_Acctschema_ID = ? AND MPC_Cost_Group_ID = ? AND MPC_Cost_Element_ID = ?  AND M_Warehouse_ID = ? AND S_Resource_ID = ?)";
        PreparedStatement	pstmt	= null;

        try {

            int	AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, AD_Client_ID);
            pstmt.setInt(2, M_Product_ID);
            pstmt.setInt(3, C_AcctSchema_ID);
            pstmt.setInt(4, MPC_Cost_Group_ID);
            pstmt.setInt(5, MPC_Cost_Element_ID);
            pstmt.setInt(6, M_Warehouse_ID);
            pstmt.setInt(7, S_Resource_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                return true;
            }

            // pc = new  MMPCProductCosting(getCtx(), rs);
            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getLines", ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        return false;
    }		// getMInOutLines
}	// MProductCosting



/*
 * @(#)MMPCProductCosting.java   13.jun 2007
 * 
 *  Fin del fichero MMPCProductCosting.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
