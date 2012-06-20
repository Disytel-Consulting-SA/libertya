/*
 * @(#)CreateCostElement.java   14.jun 2007  Versión 2.2
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



package org.openXpertya.mfg.process;

import java.util.logging.*;

import java.math.*;

import java.sql.*;

//import org.compiere.model.*;
import openXpertya.model.*;

import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      Re-Open Order Process (from Closed to Completed)
 *
 *  @author Victor Pï¿½rez, e-Evolution, S.C.
 *  @version $Id: CreateCostElement.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class CreateCostElement extends SvrProcess {

    /**  */
    private int	p_AD_Org_ID	= 0;

    /** Descripción de Campo */
    private int	p_C_AcctSchema_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Warehouse_ID	= 0;

    /** Descripción de Campo */
    private int	p_S_Resource_ID	= 0;

    /** Descripción de Campo */
    private int	p_MPC_Cost_Group_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Product_ID	= 0;

/**
         *  Prepare - e.g., get Parameters.
         */
    protected void prepare() {

        ProcessInfoParameter[]	para	= getParameter();

        for (int i = 0; i < para.length; i++) {

            String	name	= para[i].getParameterName();

            if (para[i].getParameter() == null)
                ;
            else
                if (name.equals("AD_Org_ID")) {
                    p_AD_Org_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                } else
                    if (name.equals("C_AcctSchema_ID")) {
                        p_C_AcctSchema_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                    } else
                        if (name.equals("M_Warehouse_ID")) {
                            p_M_Warehouse_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                        } else
                            if (name.equals("S_Resource_ID")) {
                                p_S_Resource_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                            } else
                                if (name.equals("MPC_Cost_Group_ID")) {
                                    p_MPC_Cost_Group_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                } else
                                    if (name.equals("M_Product_ID")) {
                                        p_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                    } else
                                        log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
        }

    }		// prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */
    protected String doIt() throws Exception {

        int	AD_Client_ID	= getAD_Client_ID();
        String	sql		= "SELECT p.M_Product_ID FROM M_Product p where AD_Client_ID=" + AD_Client_ID;

        if (p_M_Product_ID != 0)
            sql	= sql + " and p.M_Product_ID =" + p_M_Product_ID;

        boolean			existe	= false;
        MMPCCostElement[]	ce	= MMPCCostElement.getElements(getAD_Client_ID());

        try {

            PreparedStatement	pstmt			= DB.prepareStatement(sql);
            int			m_MPC_Cost_Element_ID	= 0;
            ResultSet		rs			= pstmt.executeQuery();

            while (rs.next()) {

                int	m_M_Product_ID	= rs.getInt(1);

                for (int j = 0; j < ce.length; j++) {

                    m_MPC_Cost_Element_ID	= ce[j].getMPC_Cost_Element_ID();

                    MMPCProductCosting	pc	= new MMPCProductCosting(getCtx(), 0, null);

                    if (!pc.getElement(m_M_Product_ID, p_C_AcctSchema_ID, p_MPC_Cost_Group_ID, m_MPC_Cost_Element_ID, p_M_Warehouse_ID, p_S_Resource_ID))	// && !existe)
                    {

                        log.info("Create Cost Element for Product" + m_M_Product_ID + " Warehouse:" + p_M_Warehouse_ID + " Plant: " + p_S_Resource_ID);
                        pc.setM_Product_ID(m_M_Product_ID);
                        pc.setC_AcctSchema_ID(p_C_AcctSchema_ID);
                        pc.setMPC_Cost_Group_ID(p_MPC_Cost_Group_ID);
                        pc.setMPC_Cost_Element_ID(m_MPC_Cost_Element_ID);
                        pc.setM_Warehouse_ID(p_M_Warehouse_ID);
                        pc.setS_Resource_ID(p_S_Resource_ID);
                        pc.save(get_TrxName());
                    }
                }
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        return "ok";
    }
}	// Create Cost Element



/*
 * @(#)CreateCostElement.java   14.jun 2007
 * 
 *  Fin del fichero CreateCostElement.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
