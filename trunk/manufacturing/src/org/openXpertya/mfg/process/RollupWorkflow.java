/*
 * @(#)RollupWorkflow.java   14.jun 2007  Versión 2.2
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
import org.openXpertya.wf.*;

/**
 *      Rollup of Rouning
 *
 *  @author Victor Perez, e-Evolution, S.C.
 *  @version $Id: CreateCost.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class RollupWorkflow extends SvrProcess {

    /**  */
    private int	p_AD_Org_ID	= 0;

    /** Descripción de Campo */
    private int	p_C_AcctSchema_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Warehouse_ID	= 0;

    /** Descripción de Campo */
    private int	p_S_Resource_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Product_ID	= 0;

    /** Descripción de Campo */
    private int	p_MPC_Cost_Group_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Product_Category_ID	= 0;

    // private String            p_ElementType = "";

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
                    if (name.equals("M_Warehouse_ID")) {
                        p_M_Warehouse_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                    } else
                        if (name.equals("M_Product_ID")) {
                            p_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                        } else
                            if (name.equals("S_Resource_ID")) {
                                p_S_Resource_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                            } else
                                if (name.equals("MPC_Cost_Group_ID")) {
                                    p_MPC_Cost_Group_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                }

                                // else if (name.equals("ElementType"))
                                // {
                                // p_ElementType = (String)para[i].getParameter();
                                //
                                // }
                                else
                                    if (name.equals("C_AcctSchema_ID")) {
                                        p_C_AcctSchema_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                    }

                                    /*
                                     * else if (name.equals("M_Produc_Category_ID"))
                                     * {
                                     *       p_M_Product_Category_ID = ((BigDecimal)para[i].getParameter()).intValue();
                                     *
                                     * }
                                     */
                                    else
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

        int	AD_Client_ID	= getAD_Client_ID();	// Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
        StringBuffer	sql	= new StringBuffer("SELECT p.M_Product_ID FROM M_Product p WHERE p.ProductType = '" + MProduct.PRODUCTTYPE_Item + "' AND");

        if (p_M_Product_ID != 0) {
            sql.append(" p.M_Product_ID = " + p_M_Product_ID + " AND ");
        }

        sql.append(" p.AD_Client_ID = " + AD_Client_ID);
        sql.append(" ORDER BY p.LowLevel");

        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql.toString());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                // System.out.println("Exist Product" );
                int			M_Product_ID	= rs.getInt("M_Product_ID");
                MMPCProductCosting[]	pc		= MMPCProductCosting.getElements(M_Product_ID, p_C_AcctSchema_ID, p_MPC_Cost_Group_ID, p_M_Warehouse_ID, p_S_Resource_ID);

                for (int e = 0; e < pc.length; e++) {

                    MMPCCostElement	element	= new MMPCCostElement(getCtx(), pc[e].getMPC_Cost_Element_ID(), null);

                    // check if element cost is of type Labor
                    if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Labor)) {

                        BigDecimal	Labor	= getCost(element.MPC_ELEMENTTYPE_Labor, p_AD_Org_ID, M_Product_ID, p_M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                        log.info("Labor : " + Labor);
                        pc[e].setCostTLAmt(Labor);
                        pc[e].save(get_TrxName());

                        continue;
                    }

                    if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Burden)) {

                        BigDecimal	Burden	= getCost(element.MPC_ELEMENTTYPE_Burden, p_AD_Org_ID, M_Product_ID, p_M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                        log.info("Burden : " + Burden);

                        // System.out.println("-------------------------------------------------------------Burden:" + Burden);
                        pc[e].setCostTLAmt(Burden);
                        pc[e].save(get_TrxName());

                        continue;
                    }
                }
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return null;
        }

        return "ok";
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_ElementType
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_Warehouse_ID
     * @param S_Resource_ID
     * @param MPC_Cost_Group_ID
     * @param C_AcctSchema_ID
     *
     * @return
     */
    private BigDecimal getCost(String MPC_ElementType, int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID, int S_Resource_ID, int MPC_Cost_Group_ID, int C_AcctSchema_ID) {

        BigDecimal	totalcost	= Env.ZERO;
        BigDecimal	cost		= Env.ZERO;
        int		AD_Workflow_ID	= getAD_Workflow_ID(AD_Org_ID, M_Product_ID, M_Warehouse_ID, S_Resource_ID);

        // System.out.println(".................................................................................AD_Workflow_ID=" + AD_Workflow_ID);
        if (AD_Workflow_ID != 0) {

            // System.out.println("................................................................................Exist AD_Workflow_ID=" + AD_Workflow_ID);
            MWorkflow	Workflow	= new MWorkflow(getCtx(), AD_Workflow_ID, null);
            MWFNode[]	nodes		= Workflow.getNodes(false);

            for (int i = 0; i < nodes.length; i++) {

                MWFNode		node	= (MWFNode) nodes[i];
                BigDecimal	rate	= getRate(MPC_ElementType, node.getS_Resource_ID(), AD_Org_ID, C_AcctSchema_ID, MPC_Cost_Group_ID, M_Warehouse_ID, S_Resource_ID);
                String	sql	= "SELECT CASE WHEN ow.DurationUnit = 's'  THEN 1 * ( (onode.SetupTime/ow.QtyBatchSize) + onode.Duration ) WHEN ow.DurationUnit = 'm' THEN 60 * ( (onode.SetupTime/ow.QtyBatchSize)  + onode.Duration) WHEN ow.DurationUnit = 'h'  THEN 3600 * ( (onode.SetupTime/ow.QtyBatchSize)  + onode.Duration) WHEN ow.DurationUnit = 'Y'  THEN 31536000 *  ( (onode.SetupTime/ow.QtyBatchSize)  + onode.Duration) WHEN ow.DurationUnit = 'M' THEN 2592000 * ( (onode.SetupTime/ow.QtyBatchSize)  + onode.Duration ) WHEN ow.DurationUnit = 'D' THEN 86400 * ((onode.SetupTime/ow.QtyBatchSize)  + onode.Duration) END  AS load FROM AD_WF_Node onode INNER JOIN AD_Workflow ow ON (ow.AD_Workflow_ID =  onode.AD_Workflow_ID)  WHERE onode.AD_WF_Node_ID = ?  AND onode.AD_Client_ID = ?";
                int	seconds	= DB.getSQLValue(null, sql, node.getAD_WF_Node_ID(), node.getAD_Client_ID());

                // System.out.println("seconds" + seconds);
                int	C_UOM_ID	= DB.getSQLValue(null, "SELECT C_UOM_ID FROM M_Product WHERE S_Resource_ID = ? ", node.getS_Resource_ID());
                MUOM	oum	= new MUOM(getCtx(), C_UOM_ID, null);

                if (oum.isHour()) {

                    BigDecimal	time	= new BigDecimal(seconds);

                    cost	= cost.add(time.multiply(rate).divide(new BigDecimal(3600), BigDecimal.ROUND_HALF_UP, 6));
                    System.out.println("Yes isHour" + seconds);

                    // System.out.println("seconds/3600"+ seconds/3600);
                    // System.out.println("time.multiply(rate)"+ time.multiply(rate));
                    System.out.println("Cost" + cost);
                }

                // totalcost.add(cost);
                // System.out.println("Node" + node.getName() + " MPC_ElementType"+ MPC_ElementType +" Duration=" + node.getDuration() +  " rate:" + rate + " Cost:" +  cost);
                log.info("Node" + node.getName() + " MPC_ElementType" + MPC_ElementType + " Duration=" + node.getDuration() + " rate:" + rate + " Cost:" + cost);
            }

            return cost;
        }

        return cost;
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_ElementType
     * @param S_Resource_ID
     * @param AD_Org_ID
     * @param C_AcctSchema_ID
     * @param MPC_Cost_Group_ID
     * @param M_Warehouse_ID
     * @param S_ResourcePlant_ID
     *
     * @return
     */
    private BigDecimal getRate(String MPC_ElementType, int S_Resource_ID, int AD_Org_ID, int C_AcctSchema_ID, int MPC_Cost_Group_ID, int M_Warehouse_ID, int S_ResourcePlant_ID) {

        int	M_Product_ID	= getM_Product_ID(S_Resource_ID);

        // System.out.println("...................................................................RATE:Org :" + AD_Org_ID + " S_ResourceProduct_ID:" + S_Resource_ID + " C_AcctSchema_ID :" + C_AcctSchema_ID+ " M_Warehouse_ID:" + M_Warehouse_ID + " PLAN:" + S_ResourcePlant_ID);
        // get the rate for this resource public static MMPCProductCosting[] getElements (int M_Product_ID, int C_AcctSchema_ID, int MPC_Cost_Group_ID , int M_Warehouse_ID, int S_Resource_ID , boolean requery)
        MMPCProductCosting[]	pc	= MMPCProductCosting.getElements(M_Product_ID, C_AcctSchema_ID, MPC_Cost_Group_ID, M_Warehouse_ID, S_ResourcePlant_ID);

        if (pc != null) {

            // System.out.println("............." + "MMPCProductCosting[].size=" + pc.length);
            BigDecimal	rate	= Env.ZERO;

            for (int e = 0; e < pc.length; e++) {

                MMPCCostElement	element	= new MMPCCostElement(getCtx(), pc[e].getMPC_Cost_Element_ID(), null);

                // check if element cost is of type Labor
                if (element.getMPC_ElementType().equals(MPC_ElementType)) {

                    rate	= rate.add(pc[e].getCostTLAmt());
                    log.info("Org" + AD_Org_ID + "S_Resource" + S_Resource_ID + "C_AcctSchema_ID " + C_AcctSchema_ID + "M_Warehouse_ID" + M_Warehouse_ID + "PLAN" + S_ResourcePlant_ID);

                    // System.out.println("Org" + AD_Org_ID + "S_Resource" + S_Resource_ID + "C_AcctSchema_ID " + C_AcctSchema_ID+ "M_Warehouse_ID" + M_Warehouse_ID + "PLAN" + S_ResourcePlant_ID);
                    log.info("Element rate=" + MPC_ElementType + "rate:" + rate);

                    // System.out.println("Element rate=" + MPC_ElementType +  "rate:" + rate);
                }
            }

            return rate;
        }

        return Env.ZERO;
    }

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     *
     * @return
     */
    private int getM_Product_ID(int S_Resource_ID) {

        QueryDB		query	= new QueryDB("org.openXpertya.model.X_M_Product");
        String		filter	= "S_Resource_ID = " + S_Resource_ID;
        java.util.List	results	= query.execute(filter);
        Iterator	select	= results.iterator();

        while (select.hasNext()) {

            X_M_Product	M_Product	= (X_M_Product) select.next();

            return M_Product.getM_Product_ID();
        }

        return 0;
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     * @param M_Product_ID
     * @param M_Warehouse_ID
     * @param S_Resource_ID
     *
     * @return
     */
    private int getAD_Workflow_ID(int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID, int S_Resource_ID) {

        MMPCProductPlanning	pp	= MMPCProductPlanning.get(getCtx(), AD_Org_ID, M_Product_ID, M_Warehouse_ID, S_Resource_ID);
        MProduct	M_Product	= new MProduct(getCtx(), M_Product_ID, null);
        int		AD_Workflow_ID	= 0;

        if (pp == null) {

            // System.out.println("pp.getAD_Workflow_ID() ............. " + pp.getAD_Workflow_ID());
            QueryDB		query	= new QueryDB("org.openXpertya.model.X_AD_Workflow");
            String		filter	= "Name = '" + M_Product.getName() + "'";
            java.util.List	results	= query.execute(filter);
            Iterator		select	= results.iterator();

            while (select.hasNext()) {

                X_AD_Workflow	AD_Workflow	= (X_AD_Workflow) select.next();

                return AD_Workflow.getAD_Workflow_ID();
            }
        } else {
            AD_Workflow_ID	= pp.getAD_Workflow_ID();
        }

        // System.out.println("Product" + pp.getM_Product_ID() + "Workflow" + pp.getAD_Workflow_ID());
        return AD_Workflow_ID;
    }
}	// OrderOpen



/*
 * @(#)RollupWorkflow.java   14.jun 2007
 * 
 *  Fin del fichero RollupWorkflow.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
