/*
 * @(#)MMPCCostElement.java   13.jun 2007  Versión 2.2
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
 *      Cost Element
 *
 *  @author Jorg Janke
 *  @version $Id: MProductCosting.java,v 1.4 2004/05/13 06:05:22 jjanke Exp $
 */
public class MMPCCostElement extends X_MPC_Cost_Element {

    /** Cache */

    // private static CCache s_cache = new CCache ("M_Product_Costing", 20);
    private static CLogger	log	= CLogger.getCLogger(MMPCCostElement.class);

    /** Descripción de Campo */
    MMPCCostElement[]	m_lines	= null;

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param M_Product_Costing_ID id
     * @param MPC_Cost_Element_ID
     * @param trxName
     */
    public MMPCCostElement(Properties ctx, int MPC_Cost_Element_ID, String trxName) {

        super(ctx, MPC_Cost_Element_ID, trxName);

        if (MPC_Cost_Element_ID == 0) {

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
    public MMPCCostElement(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /**
     *      Get Element Cost
     *
     * @param AD_Client_ID
     *      @return lines
     */
    public static MMPCCostElement[] getElements(int AD_Client_ID) {

        // if (m_lines != null && !requery)
        // return m_lines;
        ArrayList	list	= new ArrayList();
        String		sql	= "SELECT * FROM MPC_Cost_Element WHERE AD_Client_ID =" + AD_Client_ID;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                list.add(new MMPCCostElement(Env.getCtx(), rs, null));
            }

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

        //
        MMPCCostElement[]	retValue	= new MMPCCostElement[list.size()];

        list.toArray(retValue);

        return retValue;
    }		// getCostElement
}	// Cost Element



/*
 * @(#)MMPCCostElement.java   13.jun 2007
 * 
 *  Fin del fichero MMPCCostElement.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 13.jun 2007
