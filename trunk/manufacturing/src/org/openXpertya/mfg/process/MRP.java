/*
 * @(#)MRP.java   14.jun 2007  Versión 2.2
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

import openXpertya.model.MMPCOrder;
import openXpertya.model.MMPCOrderBOMLine;
import openXpertya.model.MMPCProductBOM;
import openXpertya.model.MMPCProductPlanning;

import org.openXpertya.model.MMPCMRP;
import org.openXpertya.model.MNote;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MRequisition;
import org.openXpertya.model.MRequisitionLine;
import org.openXpertya.model.MSequence;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

import java.math.BigDecimal;

import java.util.logging.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *      Re-Open Order Process (from Closed to Completed)
 *
 *  @author Victor Pï¿½rez, e-Evolution, S.C.
 *  @version $Id: CreateCost.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class MRP extends SvrProcess {

    // private int               p_M_Warehouse_ID = 0;

    /** Descripción de Campo */
    private int	p_AD_Org_ID	= 0;

    /** Descripción de Campo */
    private int	p_S_Resource_ID	= 0;

    //

    /** Descripción de Campo */
    private String	p_Version	= "1";

    /** Descripción de Campo */
    private int	AD_Client_ID	= 0;

    // Global Variables

    /** Descripción de Campo */
    private BigDecimal	QtyProjectOnHand	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Scrap	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	QtyNetReqs	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	QtyPlanned	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	QtyGrossReqs	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	DeliveryTime_Promised	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	TransfertTime	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Order_Period	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Order_Max	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Order_Min	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Order_Pack	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	Order_Qty	= Env.ZERO;

    /** Descripción de Campo */
    private BigDecimal	QtyScheduledReceipts	= Env.ZERO;

    /** Descripción de Campo */
    private int	M_Product_ID	= 0;

    /** Descripción de Campo */
    private int	MPC_Product_BOM_ID	= 0;

    /** Descripción de Campo */
    private int	AD_Workflow_ID	= 0;

    /** Descripción de Campo */
    private int	S_Resource_ID	= 0;

    /** Descripción de Campo */
    private String	Order_Policy	= MMPCProductPlanning.ORDER_POLICY_OrderFixedQuantity;

    /** Descripción de Campo */
    private int	SupplyPlanner_ID	= 0;

    /** Descripción de Campo */
    private int	SupplyM_Warehouse_ID	= 0;

    /** Descripción de Campo */
    private int	DemandPlanner_ID	= 0;

    /** Descripción de Campo */
    private int	M_Warehouse_ID	= 0;

    /** Descripción de Campo */
    private int	Yield	= 0;

    /** Descripción de Campo */
    private Timestamp	DatePromisedFrom	= null;

    /** Descripción de Campo */
    private Timestamp	DatePromisedTo	= null;

    /** Descripción de Campo */
    private boolean	IsCreatePlan	= true;

    /**
     *  Prepare - e.g., get Parameters.
     */
    protected void prepare() {

        AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

        ProcessInfoParameter[]	para	= getParameter();

        for (int i = 0; i < para.length; i++) {

            String	name	= para[i].getParameterName();

            if (para[i].getParameter() == null)
                ;
            else
                if (name.equals("AD_Org_ID")) {
                    p_AD_Org_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                } else
                    if (name.equals("S_Resource_ID")) {
                        p_S_Resource_ID	= ((BigDecimal) para[i].getParameter()).intValue();
                    } else
                        if (name.equals("Version")) {
                            p_Version	= (String) para[i].getParameter();
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

        deleteMRP();

        return runMRP();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean deleteMRP() {

        System.out.println("begin deleteMRP()");

        // String sql = "DELETE FROM MPC_MRP  WHERE TypeMRP = 'MOP' AND EXISTS(SELECT MPC_Order_ID FROM MPC_Order o WHERE o.MPC_Order_ID = MPC_Order_ID AND o.DocStatus IN ('NA','CL')) AND AD_Client_ID=" + AD_Client_ID;
        String	sql	= "DELETE FROM MPC_MRP  WHERE TypeMRP = 'MOP' AND DocStatus IN ('NA','CL') AND AD_Client_ID=" + AD_Client_ID;

        DB.executeUpdate(sql);

        // sql = "DELETE FROM MPC_MRP mrp WHERE mrp.TypeMRP = 'POR' AND EXISTS(SELECT M_Requisition_ID FROM M_Requisition r WHERE r.M_Requisition_ID = mrp.M_Requisition_ID AND (r.DocStatus='DR' AND r.DocStatus='CL') ) AND mrp.AD_Client_ID = " + AD_Client_ID;
        sql	= "DELETE FROM MPC_MRP WHERE TypeMRP = 'POR' AND DocStatus='DR' AND AD_Client_ID = " + AD_Client_ID;
        DB.executeUpdate(sql);
        sql	= "DELETE FROM AD_Note WHERE AD_Table_ID =  " + MMPCMRP.Table_ID + " AND AD_Client_ID = " + AD_Client_ID;
        DB.executeUpdate(sql);
        sql	= "SELECT o.MPC_Order_ID FROM MPC_Order o WHERE o.DocStatus = 'NA' AND o.AD_Client_ID = " + AD_Client_ID;

        try {

            PreparedStatement	pstmt	= null;

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MMPCOrder	order	= new MMPCOrder(getCtx(), rs.getInt(1), null);

                order.delete(true);
            }

            rs.close();
            pstmt.close();

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }

        sql	= "SELECT r.M_Requisition_ID FROM M_Requisition r WHERE  r.DocStatus = 'DR' AND r.AD_Client_ID = " + AD_Client_ID;

        try {

            PreparedStatement	pstmt	= null;

            pstmt	= DB.prepareStatement(sql);

            // pstmt.setInt(1, p_M_Warehouse_ID);
            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MRequisition	r	= new MRequisition(getCtx(), rs.getInt(1), null);
                MRequisitionLine[]	rlines	= r.getLines();

                for (int i = 0; i < rlines.length; i++) {

                    MRequisitionLine	line	= rlines[i];

                    line.delete(true);
                }

                r.delete(true);
            }

            rs.close();
            pstmt.close();
            System.out.println("end deleteMRP()");

            return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_Order_ID
     *
     * @return
     */
    public boolean createGrossRequirements(int MPC_Order_ID) {

        // Get work order requirements
        String	sql	= "SELECT ol.MPC_Order_BOMLine_ID FROM MPC_Order_BOMLine ol  WHERE o.M_Warehouse_ID = ? AND o.MPC_Order_ID = ? AND ol.AD_Client_ID = " + AD_Client_ID;
        ;
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, MPC_Order_ID);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {

                MMPCOrderBOMLine	ol	= new MMPCOrderBOMLine(Env.getCtx(), rs.getInt(1), null);

                MMPCMRP.MPC_Order_BOMLine(ol, null);
            }

            rs.close();
            pstmt.close();

            return true;

        } catch (Exception e) {

            log.log(Level.SEVERE, "doIt - " + sql, e);

            return false;
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param o
     *
     * @return
     */
    public boolean createScheduledReceipts(MMPCOrder o) {

        MMPCOrder	order		= new MMPCOrder(getCtx(), o.getMPC_Order_ID(), null);
        int		MPC_MRP_ID	= MMPCMRP.MPC_Order(order, null);
        MNote		note		= new MNote(Env.getCtx(), 1000015, o.getPlanner_ID(), MMPCMRP.Table_ID, MPC_MRP_ID, "Order:" + o.getDocumentNo() + " Release:" + o.getDateStartSchedule(), Msg.getMsg(Env.getCtx(), "MRP-060"), null);

        note.save();

        return true;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String runMRP() {

        // Get Gross Requieriments Independence Demand
        StringBuffer	result	= new StringBuffer("");

        result.append("Run MRP .......................................\n");

        try {

            // String sql = "SELECT LowLevel FROM MPC_MRP mrp INNER JOIN M_Product p ON (p.M_Product_ID =  mrp.M_Product_ID) WHERE mrp.M_Warehouse_ID = ? ORDER BY  p.LowLevel DESC ";
            MProduct		product				= null;
            Timestamp		DatePromised			= null;
            Timestamp		Today				= new Timestamp(System.currentTimeMillis());
            String		sql				= null;
            ResultSet		rs				= null;
            PreparedStatement	pstmt				= null;
            int			BeforeMPC_MRP_ID		= 0;
            BigDecimal		CurrentQtyGrossReqs		= Env.ZERO;
            Timestamp		DateStartSchedule		= null;
            Timestamp		DateFinishSchedule		= null;
            Timestamp		BeforeDateFinishSchedule	= null;
            Timestamp		BeforeDateStartSchedule		= null;

            DB.executeUpdate("UPDATE MPC_MRP SET IsAvailable ='Y' WHERE Type = 'S' AND AD_Client_ID = " + AD_Client_ID);
            Order_Policy	= MMPCProductPlanning.ORDER_POLICY_OrderFixedQuantity;

            int	lowlevel	= MMPCMRP.getMaxLowLevel();

            // int lowlevel = 0;
            int	Level	= MMPCMRP.getMaxLowLevel();	// lowlevel;                            ;

            System.out.println("Low Level >>>>>>>>>>>>>>>>" + lowlevel);

            // Calculate MRP for all levels
            for (int index = 0; index <= lowlevel; index++) {

                System.out.println(".............Levels :" + Level);
                sql	= "SELECT p.M_Product_ID ,p.Name , p.LowLevel , mrp.Qty , mrp.DatePromised, mrp.Type , mrp.TypeMRP , mrp.DateOrdered , mrp.M_Warehouse_ID , mrp.MPC_MRP_ID ,  mrp.DateStartSchedule , mrp.DateFinishSchedule FROM MPC_MRP mrp INNER JOIN M_Product p ON (p.M_Product_ID =  mrp.M_Product_ID) WHERE mrp.Type='D' AND p.LowLevel = " + index + " AND mrp.AD_Client_ID = " + AD_Client_ID + " ORDER BY  p.LowLevel DESC ,  p.M_Product_ID , mrp.DatePromised  ";

                // sql = "SELECT p.M_Product_ID ,p.Name , p.LowLevel , mrp.Qty , mrp.DatePromised, mrp.Type , mrp.TypeMRP , mrp.DateOrdered , mrp.M_Warehouse_ID , mrp.MPC_MRP_ID FROM MPC_MRP mrp INNER JOIN M_Product p ON (p.M_Product_ID =  mrp.M_Product_ID) WHERE  mrp.Type='D' AND p.M_Product_ID = 1000470  ORDER BY  p.LowLevel DESC ,  p.M_Product_ID , mrp.DatePromised ";
                System.out.println("Select:" + sql);
                pstmt	= DB.prepareStatement(sql);
                rs	= pstmt.executeQuery();

                int	lastrow	= 0;

                while (rs.next()) {
                    lastrow++;
                }

                rs	= pstmt.executeQuery();
                System.out.println("Last Rows" + lastrow);

                while (rs.next()) {

                    String	Type	= rs.getString("Type");
                    String	TypeMRP	= rs.getString("TypeMRP");

                    // Set Global Variable
                    DatePromised	= rs.getTimestamp("DatePromised");
                    DateStartSchedule	= rs.getTimestamp("DateStartSchedule");
                    ;
                    DateFinishSchedule	= rs.getTimestamp("DateFinishSchedule");
                    ;

                    BigDecimal	Qty	= rs.getBigDecimal("Qty");

                    M_Warehouse_ID	= rs.getInt("M_Warehouse_ID");

                    if (Type.equals("D") && TypeMRP.equals("FCT") && DatePromised.compareTo(Today) <= 0) {
                        continue;
                    }

                    if (product == null || product.getM_Product_ID() != rs.getInt("M_Product_ID")) {

                        System.out.println("Cantidad Remanente = " + QtyGrossReqs);

                        // if exist QtyGrossReq of last Demand verify plan
                        if (!QtyGrossReqs.equals(Env.ZERO)) {

                            // QtyGrossReqs = BeforeQty.add(QtyGrossReqs);
                            calculatePlan(BeforeMPC_MRP_ID, product, QtyGrossReqs, DateStartSchedule, DateFinishSchedule);

                            // QtyGrossReqs = Env.ZERO;
                        }

                        product	= new MProduct(Env.getCtx(), rs.getInt("M_Product_ID"), null);
                        System.out.println("-------------------------------------------------------------------------------------Nuevo producto:" + product.getName());
                        setProduct(product);

                        // first DatePromised.compareTo for ORDER_POLICY_PeriodOrderQuantity
                        if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_PeriodOrderQuantity)) {

                            DatePromisedFrom	= DatePromised;
                            DatePromisedTo	= TimeUtil.addDays(DatePromised, Order_Period.intValue());
                        }
                    }

                    // Creae Notice for Demand due
                    if (DatePromised.compareTo(Today) > 0) {

                        MNote	note	= new MNote(Env.getCtx(), 1000013, DemandPlanner_ID, MMPCMRP.Table_ID, rs.getInt("MPC_MRP_ID"), product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-040"), null);

                        note.save();
                        System.out.println("Error: Orden vencida reprogramar order o cancelar");
                    }

                    // put value of before demand
                    BeforeMPC_MRP_ID		= rs.getInt("MPC_MRP_ID");

                    // mrp.DateStartSchedule , mrp.DateFinishSchedule
                    BeforeDateStartSchedule	= rs.getTimestamp("DateStartSchedule");
                    BeforeDateFinishSchedule	= rs.getTimestamp("DateFinishSchedule");

                    // Verify if is ORDER_POLICY_PeriodOrderQuantity and DatePromised < DatePromisedTo then Accumaltion QtyGrossReqs
                    if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_PeriodOrderQuantity) && DatePromised.compareTo(DatePromisedTo) < 0) {

                        QtyGrossReqs	= QtyGrossReqs.add(rs.getBigDecimal("Qty"));

                        // BeforeQty = BeforeQty + Env.ZERO;
                        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXX  Acumulation   QtyGrossReqs:" + QtyGrossReqs);

                        continue;

                    } else
                        if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_PeriodOrderQuantity))		// if not then create new range for next period
                        {

                            DatePromisedFrom	= DatePromised;
                            DatePromisedTo	= TimeUtil.addDays(DatePromised, Order_Period.intValue());

                            // DatePromisedFrom = BeforeDateFinishSchedule;
                            // DatePromisedTo = TimeUtil.addDays(BeforeDateFinishSchedule , Order_Period.intValue());
                            System.out.println("POQ");
                            System.out.println("Create new Range for next Period     DatePromisedFrom:" + DatePromisedFrom + " DatePromisedTo:" + DatePromisedTo);
                            CurrentQtyGrossReqs	= rs.getBigDecimal("Qty");
                            System.out.println("CurrentQtyGrossReqs" + CurrentQtyGrossReqs);
                        }

                    // If  Order_Policy = LoteForLote then always create new range for next period and put QtyGrossReqs
                    if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_LoteForLote)) {

                        System.out.println("LFL");
                        DatePromisedFrom	= DatePromised;
                        DatePromisedTo		= DatePromised;
                        QtyGrossReqs		= rs.getBigDecimal("Qty");
                    }

                    System.out.println("Begin DatePromisedFrom:" + DatePromisedFrom + " DatePromisedTo:" + DatePromisedTo);

                    // calculatePlan(rs.getInt("MPC_MRP_ID"),product, Qty ,DatePromised,rs.getTimestamp("DateOrdered"));
                    calculatePlan(rs.getInt("MPC_MRP_ID"), product, Qty, DateStartSchedule, DateFinishSchedule);

                    if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_PeriodOrderQuantity)) {
                        QtyGrossReqs	= CurrentQtyGrossReqs;
                    }

                    // end while
                }

                // if exist QtyGrossReq of last Demand verify plan
                if (!QtyGrossReqs.equals(Env.ZERO)) {

                    // QtyGrossReqs = BeforeQty.add(QtyGrossReqs);
                    calculatePlan(BeforeMPC_MRP_ID, product, QtyGrossReqs, BeforeDateStartSchedule, BeforeDateFinishSchedule);

                    // QtyGrossReqs = Env.ZERO;
                }

                rs.close();
                pstmt.close();
                Level	= Level - 1;

            }		// end for
        }		// try
                catch (SQLException ex) {
            log.log(Level.SEVERE, "getLines", ex);
        }

        System.out.println("Resultado:" + result.toString());

        return "ok";
    }

    /**
     * Descripción de Método
     *
     *
     * @param product
     */
    private void setProduct(MProduct product) {

        // System.out.println("!!!!!!!!!!!!!!!Nuevo producto:");
        // M_Product_ID = rs.getInt("M_Product_ID");
        // product = new MProduct(getCtx(), M_Product_ID);
        M_Product_ID	= product.getM_Product_ID();

        // Demand Date
        // M_Warehouse_ID = rs.getInt("M_Warehouse_ID");
        MMPCProductPlanning	ppd	= MMPCProductPlanning.getDemandWarehouse(getCtx(), p_AD_Org_ID, M_Product_ID, M_Warehouse_ID);

        DatePromisedTo		= null;
        DatePromisedFrom	= null;

        // QtyGrossReqs = Env.ZERO;
        // public static MMPCProductPlanning getDemandWarehouse(Properties ctx , int AD_Org_ID , int M_Product_ID, int M_Warehouse_ID)

        if (ppd != null) {

            TransfertTime	= ppd.getTransfertTime();
            MPC_Product_BOM_ID	= ppd.getMPC_Product_BOM_ID();
            S_Resource_ID	= ppd.getS_Resource_ID();
            DemandPlanner_ID	= ppd.getPlanner_ID();
            System.out.println("TransfertTime" + TransfertTime);
            System.out.println("MPC_Product_BOM_ID" + MPC_Product_BOM_ID);
            System.out.println("S_Resource_ID" + S_Resource_ID);

            if (MPC_Product_BOM_ID == 0) {
                MPC_Product_BOM_ID	= MMPCProductBOM.getBOMSearchKey(M_Product_ID);
            }

        } else {

            MNote	note	= new MNote(Env.getCtx(), 1000020, 0, MMPCMRP.Table_ID, 0, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-110"), null);

            note.save();
            System.out.println("Error:no existe datos del almacen de demanda");

            // continue;
        }

        // Supply Data
        MMPCProductPlanning	pps	= MMPCProductPlanning.getDemandSupplyResource(getCtx(), p_AD_Org_ID, M_Product_ID, S_Resource_ID);

        if (pps != null) {

            AD_Workflow_ID		= pps.getAD_Workflow_ID();
            DeliveryTime_Promised	= pps.getDeliveryTime_Promised();
            IsCreatePlan		= pps.isCreatePlan();
            Order_Max			= pps.getOrder_Max();
            Order_Min			= pps.getOrder_Min();
            Order_Pack			= pps.getOrder_Pack();
            Order_Qty			= pps.getOrder_Qty();
            Order_Period		= pps.getOrder_Period();
            Order_Policy		= pps.getOrder_Policy();
            SupplyM_Warehouse_ID	= pps.getM_Warehouse_ID();
            SupplyPlanner_ID		= pps.getPlanner_ID();
            Yield			= pps.getYield();
            System.out.println("S_Resource_ID" + S_Resource_ID);
            System.out.println("AD_Workflow_I" + AD_Workflow_ID);
            System.out.println("DeliveryTime_Promised" + DeliveryTime_Promised);
            System.out.println("IsCreatePlan" + IsCreatePlan);
            System.out.println("Order_Max" + Order_Max);
            System.out.println("Order_Min" + Order_Min);
            System.out.println("Order_Pack" + Order_Pack);
            System.out.println("Order_Period" + Order_Period);
            System.out.println("Order_Policy" + Order_Policy);
            System.out.println("SupplyM_Warehouse_ID" + SupplyM_Warehouse_ID);

        } else {

            MNote	note	= new MNote(Env.getCtx(), 1000021, 0, MMPCMRP.Table_ID, 0, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-120"), null);

            note.save();
            System.out.println("Erro:no existe almacen de suministro");

            // continue;
        }

        if (AD_Workflow_ID == 0)
            System.out.println("Error: No existe Flujo de Trabajo para el Producto" + M_Product_ID);
        if (Order_Policy == null)
            Order_Policy	= MMPCProductPlanning.ORDER_POLICY_LoteForLote;

        // QtyOnHand = getOnHand(M_Product_ID);
        QtyProjectOnHand	= MMPCMRP.getOnHand(M_Product_ID);
        if (QtyProjectOnHand == null)
            QtyProjectOnHand	= Env.ZERO;
        System.out.println("Existencia >>>>>>>>>>>>>>>>>>>>>>>:M_Product_ID" + M_Product_ID + "Total Existencia:" + QtyProjectOnHand);

        // result.append("---------------------------------------------------------------\n");
        // result.append("Product " + rs.getString("Name") + " On Hand: " + QtyOnHand + "\n");
        // result.append("--------------------   -------------------------------------------\n");
        // result.append("        Due Date        Gross Reqs     Sched Rcpt     Project On Hand     Order Plan \n");
        QtyScheduledReceipts	= Env.ZERO;	// get Supply this Product

        String	sqlsupply	= "SELECT mrp.MPC_MRP_ID , mrp.Qty FROM MPC_MRP mrp WHERE mrp.DocStatus NOT IN('DR','NA') AND mrp.IsAvailable = 'Y' AND mrp.Type = 'S' AND mrp.M_Product_ID = " + M_Product_ID + " AND mrp.AD_Client_ID = " + AD_Client_ID;

        System.out.println(sqlsupply);

        try {

            PreparedStatement	supplypstmt	= DB.prepareStatement(sqlsupply);
            ResultSet		rssupply	= supplypstmt.executeQuery();

            while (rssupply.next()) {

                QtyScheduledReceipts	= QtyScheduledReceipts.add(rssupply.getBigDecimal("Qty"));
                DB.executeUpdate("UPDATE MPC_MRP SET IsAvailable = 'N' WHERE MPC_MRP_ID = " + rssupply.getInt("MPC_MRP_ID") + " AND AD_Client_ID = " + AD_Client_ID);
            }

            supplypstmt.close();
            rssupply.close();
            System.out.println("Inicial --------------------------------------------------------> QtyScheduledReceipts " + QtyScheduledReceipts);

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "getLines" + sqlsupply, ex);
        }

        // QtyProjectOnHand =  QtyProjectOnHand.add(QtyScheduledReceipts);
        // QtyScheduledReceipts = Env.ZERO;
    }

    /**
     * Descripción de Método
     *
     *
     * @param MPC_MPR_ID
     * @param product
     * @param Qty
     * @param BeforeDateStartSchedule
     * @param BeforeDateFinishSchedule
     */
    private void calculatePlan(int MPC_MPR_ID, MProduct product, BigDecimal Qty, Timestamp BeforeDateStartSchedule, Timestamp BeforeDateFinishSchedule) {

        // Set Yield o QtyGrossReqs
        Timestamp	Today	= new Timestamp(System.currentTimeMillis());

        System.out.println("BeforeDateStartSchedule:" + BeforeDateStartSchedule);
        System.out.println("BeforeDateFinishSchedule:" + BeforeDateFinishSchedule);

        BigDecimal	DecimalYield	= new BigDecimal(Yield / 100);

        if (!DecimalYield.equals(Env.ZERO))
            QtyGrossReqs	= QtyGrossReqs.divide(DecimalYield, 4, BigDecimal.ROUND_HALF_UP);

        // System.out.println("Producto Renglon" + rs.getRow());
        System.out.println("###################### Requisition Poduct:" + product.getName() + "Create Plan:" + IsCreatePlan + " OrderPlan:" + QtyPlanned);
        System.out.println(" DatePromisedFrom:" + DatePromisedFrom + " DatePromisedTo:" + DatePromisedTo);
        System.out.println("QtyScheduledReceipts:" + QtyScheduledReceipts);
        System.out.println("    QtyProjectOnHand:" + QtyProjectOnHand);
        System.out.println("        QtyGrossReqs:" + QtyGrossReqs);
        System.out.println("              Supply:" + (QtyScheduledReceipts).add(QtyProjectOnHand));
        QtyNetReqs	= ((QtyScheduledReceipts).add(QtyProjectOnHand)).subtract(QtyGrossReqs);
        System.out.println("=         QtyNetReqs:" + QtyNetReqs);

        if (QtyNetReqs.compareTo(Env.ZERO) > 0) {

            QtyProjectOnHand		= QtyNetReqs;
            QtyNetReqs			= Env.ZERO;
            QtyScheduledReceipts	= Env.ZERO;
            QtyPlanned			= Env.ZERO;
            QtyGrossReqs		= Env.ZERO;

            return;

        } else {

            QtyPlanned			= QtyNetReqs.negate();
            QtyGrossReqs		= Env.ZERO;
            QtyScheduledReceipts	= Env.ZERO;
        }

        // Check Order Min
        if (QtyPlanned.compareTo(Env.ZERO) > 0 && Order_Min.compareTo(Env.ZERO) > 0) {

            QtyPlanned	= QtyPlanned.max(Order_Min);

            MNote	note	= new MNote(Env.getCtx(), 1000017, SupplyPlanner_ID, MMPCMRP.Table_ID, MPC_MPR_ID, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-080"), null);

            note.save();
        }

        // Check Order Max
        if (QtyPlanned.compareTo(Order_Max) > 0 && Order_Max.compareTo(Env.ZERO) > 0) {

            System.out.println("Error: Orden Planeada exede el maximo a ordenar");

            MNote	note	= new MNote(Env.getCtx(), 1000018, SupplyPlanner_ID, MMPCMRP.Table_ID, MPC_MPR_ID, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-090"), null);

            note.save();
        }

        // Check Order Pack
        if (Order_Pack.compareTo(Env.ZERO) > 0 && QtyPlanned.compareTo(Env.ZERO) > 0)
            QtyPlanned	= Order_Pack.multiply(QtyPlanned.divide(Order_Pack, 0, BigDecimal.ROUND_UP));
        QtyProjectOnHand	= QtyPlanned.add(QtyNetReqs);
        System.out.println("QtyPlanned:" + QtyPlanned);
        System.out.println("QtyProjectOnHand:" + QtyProjectOnHand);

        if (IsCreatePlan && QtyPlanned.compareTo(Env.ZERO) > 0) {

            int	loops	= 1;

            if (Order_Policy.equals(MMPCProductPlanning.ORDER_POLICY_OrderFixedQuantity)) {

                if (Order_Qty.compareTo(Env.ZERO) != 0)
                    loops	= (QtyPlanned.divide(Order_Qty, 0, BigDecimal.ROUND_UP)).intValue();
                QtyPlanned	= Order_Qty;
            }

            for (int ofq = 1; ofq <= loops; ofq++) {

                System.out.println("Comprado:" + product.isPurchased() + "Fabricado:" + product.isBOM());

                if (product.isPurchased())	// then create M_Requisition
                {

                    System.out.println("Create Requisistion");

                    int	C_DocType_ID	= MMPCMRP.getDocType("POR", true);

                    // 4Layers - Check that document type exists
                    if (C_DocType_ID == 0) {

                        log.severe("Not found default document type for docbasetype 'POR'");

                        MNote	note	= new MNote(Env.getCtx(), 1000018, SupplyPlanner_ID, MMPCMRP.Table_ID, MPC_MPR_ID, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-130"), null);

                        note.save();

                        continue;
                    }

                    // 4Layers - end

                    int	M_PriceList_ID	= Env.getContextAsInt(getCtx(), "#M_PriceList_ID");

                    // 4Layers - Check that pricelist exists
                    if (M_PriceList_ID == 0) {

                        log.info("No default pricelist has been retrieved");

                        MNote	note	= new MNote(Env.getCtx(), 1000018, SupplyPlanner_ID, MMPCMRP.Table_ID, MPC_MPR_ID, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-140"), null);

                        note.save();

                        continue;
                    }

                    // 4Layers - end

                    MRequisition	req	= new MRequisition(getCtx(), 0, null);

                    req.setAD_User_ID(SupplyPlanner_ID);

                    // req.setDateRequired(TimeUtil.addDays(BeforeDateStartSchedule , (DeliveryTime_Promised.add(TransfertTime)).negate().intValue()));
                    req.setDateRequired(BeforeDateStartSchedule);
                    req.setDescription("Generate from MRP");
                    req.setM_Warehouse_ID(M_Warehouse_ID);
                    req.setDocumentNo(MSequence.getDocumentNo(C_DocType_ID, null));
                    req.setM_PriceList_ID(M_PriceList_ID);
                    req.save();

                    MRequisitionLine	reqline	= new MRequisitionLine(getCtx(), 0, null);

                    reqline.setLine(10);
                    reqline.setM_Requisition_ID(req.getM_Requisition_ID());
                    reqline.setM_Product_ID(M_Product_ID);
                    reqline.setPrice(M_PriceList_ID);
                    reqline.setPriceActual(new BigDecimal(0));
                    reqline.setQty(QtyPlanned);
                    reqline.save();

                    // Set Correct Dates for Plan
                    String	rsql	= "SELECT mrp.MPC_MRP_ID FROM MPC_MRP mrp WHERE M_Requisition_ID = " + req.getM_Requisition_ID();

                    try {

                        PreparedStatement	rpstmt	= DB.prepareStatement(rsql);
                        ResultSet		rrs	= rpstmt.executeQuery();

                        while (rrs.next()) {

                            System.out.println("Set Correct Dates for Plan");

                            MMPCMRP	mrp	= new MMPCMRP(getCtx(), rrs.getInt(1), null);

                            mrp.setDateOrdered(Today);
                            mrp.setDatePromised(BeforeDateStartSchedule);
                            mrp.setDateStartSchedule(TimeUtil.addDays(BeforeDateStartSchedule, (DeliveryTime_Promised.add(TransfertTime)).negate().intValue()));
                            mrp.setDateFinishSchedule(BeforeDateStartSchedule);
                            mrp.save();
                        }

                        rpstmt.close();
                        rrs.close();

                    } catch (SQLException ex) {
                        log.log(Level.SEVERE, "getLines" + rsql, ex);
                    }

                    return;

                } else
                    if (product.isBOM())	// else create MPC_Order
                    {

                        System.out.println("MPC_Product_BOM_ID" + MPC_Product_BOM_ID + "AD_Workflow_ID" + AD_Workflow_ID);

                        if (MPC_Product_BOM_ID != 0 && AD_Workflow_ID != 0) {

                            System.out.println("Manufacturing Order Create");

                            int	C_DocType_ID	= MMPCMRP.getDocType("MOP", true);

                            if (C_DocType_ID == 0) {

                                log.severe("Not found default document type for docbasetype 'MOP'");

                                MNote	note	= new MNote(Env.getCtx(), 1000018, SupplyPlanner_ID, MMPCMRP.Table_ID, MPC_MPR_ID, product.getValue() + " " + product.getName(), Msg.getMsg(Env.getCtx(), "MRP-130"), null);

                                note.save();

                                continue;
                            }

                            // System.out.println("-----------> DateStartSchedule" + DateStartSshedule +" DatePromisedFrom:" +  DatePromisedFrom + " DatePromisedTo:" +   DatePromisedTo);
                            MMPCOrder	order	= new MMPCOrder(getCtx(), 0, null);

                            order.setLine(10);
                            order.setDocumentNo(MSequence.getDocumentNo(C_DocType_ID, null));
                            order.setS_Resource_ID(S_Resource_ID);
                            order.setM_Warehouse_ID(SupplyM_Warehouse_ID);
                            order.setM_Product_ID(M_Product_ID);
                            order.setM_AttributeSetInstance_ID(0);
                            order.setMPC_Product_BOM_ID(MPC_Product_BOM_ID);
                            order.setAD_Workflow_ID(AD_Workflow_ID);
                            order.setPlanner_ID(SupplyPlanner_ID);
                            order.setQtyDelivered(Env.ZERO);
                            order.setQtyReject(Env.ZERO);
                            order.setQtyScrap(Env.ZERO);
                            order.setDateOrdered(Today);
                            order.setDatePromised(BeforeDateStartSchedule);
                            if (DeliveryTime_Promised.compareTo(Env.ZERO) == 0)
                                order.setDateStartSchedule(TimeUtil.addDays(BeforeDateStartSchedule, (MMPCMRP.getDays(order.getS_Resource_ID(), order.getAD_Workflow_ID(), QtyPlanned).add(TransfertTime)).negate().intValue()));
                            else
                                order.setDateStartSchedule(TimeUtil.addDays(BeforeDateStartSchedule, (DeliveryTime_Promised.add(TransfertTime)).negate().intValue()));
                            order.setDateFinishSchedule(BeforeDateStartSchedule);
                            order.setQtyEntered(QtyPlanned);
                            order.setQtyOrdered(QtyPlanned);
                            order.setC_UOM_ID(product.getC_UOM_ID());
                            order.setPosted(false);
                            order.setProcessed(false);
                            order.setC_DocTypeTarget_ID(C_DocType_ID);
                            order.setC_DocType_ID(C_DocType_ID);
                            order.setPriorityRule(order.PRIORITYRULE_Medium);
                            order.setDocStatus(order.STATUS_NotApproved);
                            order.setDocAction(order.DOCACTION_None);
                            order.save();

                            return;
                        }
                    }

            }		// end for oqf

        } else {
            System.out.println("No Create Plan");
        }

        QtyGrossReqs	= Qty;		// rs.getBigDecimal("Qty");
    }
}



/*
 * @(#)MRP.java   14.jun 2007
 * 
 *  Fin del fichero MRP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
