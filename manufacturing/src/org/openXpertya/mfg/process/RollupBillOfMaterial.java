/*
 * @(#)RollupBillOfMaterial.java   14.jun 2007  Versión 2.2
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
 *      Rollup Bill of Material
 *
 *  @author Victor Perez, e-Evolution, S.C.
 *  @version $Id: CreateCost.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class RollupBillOfMaterial extends SvrProcess {

    /**  */
    private int	p_AD_Org_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Warehouse_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Product_Category_ID	= 0;

    /** Descripción de Campo */
    private int	p_M_Product_ID	= 0;

    /** Descripción de Campo */
    private int	p_MPC_Cost_Group_ID	= 0;

    /** Descripción de Campo */
    private int	p_S_Resource_ID	= 0;

    /** Descripción de Campo */
    private int	p_C_AcctSchema_ID	= 0;

    /** Descripción de Campo */
    private int	Elementtypeint	= 0;

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
                        if (name.equals("M_Product_Category_ID")) {
                            p_M_Product_Category_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                        } else
                            if (name.equals("M_Product_ID")) {
                                p_M_Product_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                            } else
                                if (name.equals("S_Resource_ID")) {
                                    p_S_Resource_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                } else
                                    if (name.equals("MPC_Cost_Group_ID")) {
                                        p_MPC_Cost_Group_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                                    } else
                                        if (name.equals("C_AcctSchema_ID")) {
                                            p_C_AcctSchema_ID	= ((BigDecimal) para[i].getParameter()).intValue();
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

        int	lowlevel	= MMPCMRP.getMaxLowLevel();
        int	Level		= lowlevel;

        // System.out.println("Low Level >>>>>>>>>>>>>>>>"+lowlevel);

        // Calculate Rollup for all levels
        for (int index = lowlevel; index >= 0; index--) {

            StringBuffer	sql	= new StringBuffer("SELECT p.M_Product_ID FROM M_Product p WHERE p.ProductType = '" + MProduct.PRODUCTTYPE_Item + "' AND AD_Client_ID = ? AND p.LowLevel = " + index);

            if (p_M_Product_ID != 0) {
                sql.append(" AND p.M_Product_ID = " + p_M_Product_ID);
            }

            if (p_M_Product_Category_ID != 0) {
                sql.append(" AND p.M_Product_Category_ID = " + p_M_Product_Category_ID);
            }

            // System.out.print("sql :" + sql.toString());

            PreparedStatement	pstmt	= null;

            try {

                pstmt	= DB.prepareStatement(sql.toString());
                pstmt.setInt(1, getAD_Client_ID());

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {

                    int			M_Product_ID	= rs.getInt("M_Product_ID");
                    StringBuffer	sqlw		= new StringBuffer("SELECT p.M_Warehouse_ID FROM M_Warehouse p WHERE IsActive = 'Y' AND AD_Client_ID = " + getAD_Client_ID());

                    if (p_M_Warehouse_ID != 0) {
                        sqlw.append(" AND p.M_Warehouse_ID = " + p_M_Warehouse_ID);
                    }

                    System.out.print("sql :" + sqlw.toString());

                    PreparedStatement	pstmtw	= null;

                    pstmtw	= DB.prepareStatement(sqlw.toString());

                    ResultSet	rsw		= pstmtw.executeQuery();
                    int		M_Warehouse_ID	= 0;

                    while (rsw.next()) {

                        M_Warehouse_ID	= rsw.getInt(1);

                        // System.out.println("WAREHOUSE ************" +M_Warehouse_ID);
                        MMPCProductCosting[]	pc	= MMPCProductCosting.getElements(M_Product_ID, p_C_AcctSchema_ID, p_MPC_Cost_Group_ID, M_Warehouse_ID, p_S_Resource_ID);

                        // System.out.println("M_Product_ID" + M_Product_ID + "p_C_AcctSchema_ID" + p_C_AcctSchema_ID + "p_MPC_Cost_Group_ID" + p_MPC_Cost_Group_ID + "p_M_Warehouse_ID" +  M_Warehouse_ID + "p_S_Resource_ID" + p_S_Resource_ID);
                        MProduct	product	= new MProduct(getCtx(), M_Product_ID, null);

                        System.out.println("--------------------------Product" + product.getValue() + "-" + product.getName());

                        if (pc != null) {

                            for (int e = 0; e < pc.length; e++) {

                                MMPCCostElement	element	= new MMPCCostElement(getCtx(), pc[e].getMPC_Cost_Element_ID(), null);

                                // check if element cost is of type Labor
                                System.out.println("Exist Elemet " + e);

                                // System.out.println("M_Product_ID" + M_Product_ID + "p_C_AcctSchema_ID" + p_C_AcctSchema_ID + "p_MPC_Cost_Group_ID" + p_MPC_Cost_Group_ID + "p_M_Warehouse_ID" +  M_Warehouse_ID + "p_S_Resource_ID" + p_S_Resource_ID);
                                // System.out.println("Material" +element.MPC_ELEMENTTYPE_Material);
                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Material)) {

                                    BigDecimal	Material	= getCostLL(element.MPC_ELEMENTTYPE_Material, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    System.out.println("Material" + Material);

                                    if (pc[e].getCostTLAmt().compareTo(Env.ZERO) == 0) {

                                        pc[e].setCostLLAmt(Material);
                                        pc[e].save(get_TrxName());
                                    }

                                    continue;
                                }

                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Labor)) {

                                    BigDecimal	Labor	= getCostLL(element.MPC_ELEMENTTYPE_Labor, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    System.out.println("Labor" + Labor);
                                    pc[e].setCostLLAmt(Labor);
                                    pc[e].save(get_TrxName());

                                    continue;
                                }

                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Burden)) {

                                    BigDecimal	Burder	= getCostLL(element.MPC_ELEMENTTYPE_Labor, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    System.out.println("Burder" + Burder);
                                    pc[e].setCostLLAmt(Burder);
                                    pc[e].save(get_TrxName());

                                    continue;
                                }

                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Overhead)) {

                                    BigDecimal	Overhead	= getCostLL(element.MPC_ELEMENTTYPE_Overhead, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    System.out.println("Overhead" + Overhead);
                                    pc[e].setCostLLAmt(Overhead);
                                    pc[e].save(get_TrxName());

                                    continue;
                                }

                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Subcontract)) {

                                    BigDecimal	Subcontract	= getCostLL(element.MPC_ELEMENTTYPE_Subcontract, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    System.out.println("Subcontract" + Subcontract);
                                    pc[e].setCostLLAmt(Subcontract);
                                    pc[e].save(get_TrxName());

                                    continue;
                                }

                                if (element.getMPC_ElementType().equals(element.MPC_ELEMENTTYPE_Distribution)) {

                                    BigDecimal	Distribution	= getCostLL(element.MPC_ELEMENTTYPE_Distribution, p_AD_Org_ID, M_Product_ID, M_Warehouse_ID, p_S_Resource_ID, p_MPC_Cost_Group_ID, p_C_AcctSchema_ID);

                                    pc[e].setCostLLAmt(Distribution);
                                    System.out.println("Distribution" + Distribution);
                                    pc[e].save(get_TrxName());

                                    continue;
                                }
                            }

                        }	// end if

                    }		// end while warehouse

                    rsw.close();
                    pstmtw.close();

                }		// end while product

                rs.close();
                pstmt.close();

            } catch (Exception e) {

                // log.log(Level.SEVERE,"doIt - " + sql, e);
                return null;
            }
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
    private BigDecimal getCostLL(String MPC_ElementType, int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID, int S_Resource_ID, int MPC_Cost_Group_ID, int C_AcctSchema_ID) {

        System.out.println("getcostLL ***** " + MPC_ElementType);

        BigDecimal	cost		= Env.ZERO;
        BigDecimal	total		= Env.ZERO;
        BigDecimal	totalcost	= Env.ZERO;
        MMPCProductBOM	bom		= new MMPCProductBOM(getCtx(), getMPC_Product_BOM_ID(AD_Org_ID, M_Product_ID, M_Warehouse_ID, S_Resource_ID, MPC_ElementType), null);
        MMPCProductBOMLine[]	bomlines	= bom.getLines();

        System.out.println("no. de materias primas **********+ " + bomlines.length);

        for (int i = 0; i < bomlines.length; i++) {

            MMPCProductBOMLine	bomline	= bomlines[i];

            // int m_S_Resource_ID = ;
            // get the rate for this resource
            MMPCProductCosting[]	pc	= MMPCProductCosting.getElements(bomline.getM_Product_ID(), C_AcctSchema_ID, MPC_Cost_Group_ID, M_Warehouse_ID, S_Resource_ID);

            System.out.println("Producto de la linea *************   " + bomline.getM_Product_ID());

            for (int e = 0; e < pc.length; e++) {

                MMPCCostElement	element	= new MMPCCostElement(getCtx(), pc[e].getMPC_Cost_Element_ID(), null);

                // check if element cost is of type Labor
                if (element.getMPC_ElementType().equals(MPC_ElementType)) {

                    cost	= cost.add(pc[e].getCostTLAmt()).add(pc[e].getCostLLAmt());

                    BigDecimal	QtyPercentage	= bomline.getQtyBatch().divide(new BigDecimal(100), 8, BigDecimal.ROUND_UP);
                    BigDecimal	QtyBOM	= bomline.getQtyBOM();
                    BigDecimal	Scrap	= new BigDecimal(bomline.getScrap());

                    Scrap	= Scrap.divide(new BigDecimal(100), 4, BigDecimal.ROUND_UP);	// convert to decimal

                    BigDecimal	QtyTotal	= Env.ZERO;

                    System.out.println("elementos pc[e]  " + pc[e].getM_Product_ID() + " cost ll " + pc[e].getCostLLAmt() + " cost tl " + pc[e].getCostTLAmt());
                    System.out.println("cost:" + cost + "QtyPercentage:" + QtyPercentage + "QtyBOM" + QtyBOM);

                    if (bomline.isQtyPercentage()) {
                        QtyTotal	= QtyPercentage.divide(Env.ONE.subtract(Scrap), 4, BigDecimal.ROUND_HALF_UP);
                    } else {
                        QtyTotal	= QtyBOM.divide(Env.ONE.subtract(Scrap), 4, BigDecimal.ROUND_HALF_UP);
                    }

                    totalcost	= totalcost.add(cost.multiply(QtyTotal));
                    System.out.println("Cost Total" + totalcost);
                }

                cost	= Env.ZERO;
            }
        }

        // Calculate Yield Cost
        MMPCProductPlanning	pps	= MMPCProductPlanning.getDemandSupplyResource(getCtx(), AD_Org_ID, M_Product_ID, S_Resource_ID);

        if (pps != null) {

            int	Yield	= pps.getYield();

            if (Yield != 0) {

                BigDecimal	DecimalYield	= new BigDecimal(Yield / 100);

                if (!DecimalYield.equals(Env.ZERO))
                    totalcost	= totalcost.divide(DecimalYield, 4, BigDecimal.ROUND_HALF_UP);
            }
        }

        return totalcost;
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
     * @param MPC_ElementType
     *
     * @return
     */
    private int getMPC_Product_BOM_ID(int AD_Org_ID, int M_Product_ID, int M_Warehouse_ID, int S_Resource_ID, String MPC_ElementType) {

        boolean	pp	= false;
        String	sqlec	= "SELECT MPC_Cost_Element_ID FROM MPC_Cost_Element WHERE MPC_ElementType=? AND AD_CLient_ID = " + getAD_Client_ID();
        PreparedStatement	pstmtec	= null;

        try {

            pstmtec	= DB.prepareStatement(sqlec);
            pstmtec.setString(1, MPC_ElementType);

            ResultSet	rsec	= pstmtec.executeQuery();

            if (rsec.next()) {
                Elementtypeint	= rsec.getInt(1);
            }

            rsec.close();
            pstmtec.close();
            pstmtec	= null;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getLines", ex);
        }

        String	sql	= "SELECT * FROM MPC_Product_Costing WHERE M_Product_ID=? AND ( C_Acctschema_ID = ? AND MPC_Cost_Group_ID = ? AND MPC_Cost_Element_ID = ?  AND M_Warehouse_ID = ? AND S_Resource_ID = ?) AND AD_Client_ID = ?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, M_Product_ID);
            pstmt.setInt(2, p_C_AcctSchema_ID);
            pstmt.setInt(3, p_MPC_Cost_Group_ID);
            pstmt.setInt(4, Elementtypeint);
            pstmt.setInt(5, M_Warehouse_ID);
            pstmt.setInt(6, S_Resource_ID);
            pstmt.setInt(7, getAD_Client_ID());

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                pp	= true;
            }

            rs.close();
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getLines", ex);
        }

        // System.out.println("pp ********** " +pp);
        MProduct	M_Product	= new MProduct(getCtx(), M_Product_ID, null);

        if (pp) {

            try {

                // System.out.println("producto value ********** * " +M_Product.getValue());
                StringBuffer	sqlprod	= new StringBuffer("SELECT MPC_Product_BOM_ID FROM MPC_Product_BOM WHERE Value Like '" + M_Product.getValue() + "%' AND AD_Client_ID = " + getAD_Client_ID() + " ORDER BY Value");
                PreparedStatement	pstmtprod	= DB.prepareStatement(sqlprod.toString());

                // System.out.println("query ********** * " +sqlprod.toString());
                ResultSet	rsprod	= pstmtprod.executeQuery();

                if (rsprod.next()) {

                    // System.out.println("producto del bom ********** * " +rsprod.getInt(1));
                    return rsprod.getInt(1);
                }

                rsprod.close();
                pstmtprod.close();
            } catch (SQLException s) {

                // log.log(Level.SEVERE, s);
            }
        }

        return 0;
    }
}	// OrderOpen



/*
 * @(#)RollupBillOfMaterial.java   14.jun 2007
 * 
 *  Fin del fichero RollupBillOfMaterial.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
