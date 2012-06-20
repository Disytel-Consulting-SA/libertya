/*
 * @(#)CopyPriceToStandard.java   14.jun 2007  Versión 2.2
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

import java.util.*;

import openXpertya.model.*;

import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *      CopyPriceToStandard
 *
 *  @author Victor Perez, e-Evolution, S.C.
 *  @version $Id: CopyPriceToStandard.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class CopyPriceToStandard extends SvrProcess {

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
    private int	p_MPC_Cost_Element_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_PriceList_Version_ID	= 0;

    /** Descripción de Campo */
    private Properties	ctx	= Env.getCtx();

    /**
     *     Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        ProcessInfoParameter[]	para	= getParameter();

        for (int i = 0; i < para.length; i++) {

            String	name	= para[i].getParameterName();

            if (para[i].getParameter() == null)
                ;
            else
                if (name.equals("MPC_Cost_Group_ID")) {
                    p_MPC_Cost_Group_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                } else
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
                                    if (name.equals("MPC_Cost_Element_ID")) {
                                        p_MPC_Cost_Element_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                    } else
                                        if (name.equals("M_PriceList_Version_ID")) {
                                            p_M_PriceList_Version_ID	= ((BigDecimal) para[i].getParameter()).intValue();
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

        // System.out.println("PARAMETROS :  p_C_AcctSchema_ID" + p_C_AcctSchema_ID + "p_MPC_Cost_Group_ID" + p_MPC_Cost_Group_ID + "p_M_Warehouse_ID" +  p_M_Warehouse_ID + "p_S_Resource_ID" + p_S_Resource_ID);
        BigDecimal	price			= Env.ZERO;
        BigDecimal	convrate		= Env.ZERO;
        int		M_PriceList_ID		= 0;
        int		M_PriceList_Version_ID	= 0;
        int		M_Product_ID		= 0;
        int		C_Currency_ID		= 0;
        BigDecimal	list			= Env.ZERO;
        MAcctSchema	schema			= new MAcctSchema(ctx, p_C_AcctSchema_ID, null);
        StringBuffer	sql			= new StringBuffer("SELECT M_Product_ID,M_PriceList_Version_ID, PriceStd FROM M_ProductPrice WHERE M_PriceList_Version_ID =" + p_M_PriceList_Version_ID + " AND PriceStd <> 0");

        try {

            // System.out.println("query " +sql.toString());
            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());
            ResultSet		rs	= pstmt.executeQuery();

            //
            while (rs.next()) {

                M_Product_ID		= rs.getInt(1);
                M_PriceList_Version_ID	= rs.getInt(2);

                // System.out.println("M_Product_ID" + product_id + "p_C_AcctSchema_ID" + p_C_AcctSchema_ID + "p_MPC_Cost_Group_ID" + p_MPC_Cost_Group_ID + "p_M_Warehouse_ID" +  p_M_Warehouse_ID + "p_S_Resource_ID" + p_S_Resource_ID);
                M_PriceList_ID	= DB.getSQLValue(get_TrxName(), "SELECT M_PriceList_ID FROM M_PriceList_Version WHERE M_PriceList_Version_ID = ? ", M_PriceList_Version_ID);
                C_Currency_ID	= DB.getSQLValue(get_TrxName(), "SELECT C_Currency_ID FROM M_PriceList WHERE M_PriceList_ID = ?", M_PriceList_ID);

                if (C_Currency_ID != schema.getC_Currency_ID()) {
                    price	= MConversionRate.convert(ctx, rs.getBigDecimal(3), C_Currency_ID, schema.getC_Currency_ID(), getAD_Client_ID(), p_AD_Org_ID);
                } else
                    price	= rs.getBigDecimal(3);

                MMPCProductCosting[]	pc	= MMPCProductCosting.getElements(M_Product_ID, p_C_AcctSchema_ID, p_MPC_Cost_Group_ID, p_M_Warehouse_ID, p_S_Resource_ID);

                if (pc != null) {

                    for (int e = 0; e < pc.length; e++) {

                        MMPCCostElement	element	= new MMPCCostElement(getCtx(), p_MPC_Cost_Element_ID, null);

                        if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Material)) {

                            pc[0].setCostTLAmt(price);
                            pc[0].save(get_TrxName());

                            break;
                        }
                    }
                }
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        return "ok";
    }
}



/*
 * @(#)CopyPriceToStandard.java   14.jun 2007
 * 
 *  Fin del fichero CopyPriceToStandard.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
