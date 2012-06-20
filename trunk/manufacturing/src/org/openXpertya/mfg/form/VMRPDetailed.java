/*
 * @(#)VMRPDetailed.java   14.jun 2007  Versión 2.2
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



package org.openXpertya.mfg.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import java.math.BigDecimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.logging.*;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextField;

import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.apps.search.Info_Column;
import org.openXpertya.grid.ed.VCheckBox;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.minigrid.MiniTable;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MMPCMRP;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUOM;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.ASyncProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 *      VMRPDetailed
 *
 *  @author Victor Pï¿½rez, e-Evolution, S.C.
 *  @version $Id: VMRPDetailed.java,v 1.1 2004/06/22 05:24:03 vpj-cd Exp $
 */
public class VMRPDetailed extends CPanel implements FormPanel, ActionListener, VetoableChangeListener, ChangeListener, ListSelectionListener, TableModelListener, ASyncProcess {

    /** Creates new form VMRPDetailed */
    public VMRPDetailed() {
        initComponents();
    }

    /**
     *         Initialize Panel
     *     @param WindowNo window
     *     @param frame frame
     */
    public void init(int WindowNo, FormFrame frame) {

        m_WindowNo	= WindowNo;
        m_frame		= frame;

        // Log.trace(Log.l1_User, "VOrderReceipIssue.init - WinNo=" + m_WindowNo,
        // "AD_Client_ID=" + m_AD_Client_ID + ", AD_Org_ID=" + m_AD_Org_ID);
        Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "N");

        try {

            // UI
            statInit();
            fillPicks();
            jbInit();

            //
            dynInit();
            m_frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
            m_frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        } catch (Exception e) {
            Log.log(Level.SEVERE, "VMRPDetailed.init", e);
        }

    }		// init

    /** Window No */
    private int	m_WindowNo	= 0;

    /** FormFrame */
    private FormFrame	m_frame;

    /** Descripción de Campo */
    private StatusBar	statusBar	= new StatusBar();

    /** Descripción de Campo */
    private int	AD_Table_ID	= 1000027;

    /** Descripción de Campo */
    private int	AD_Window_ID	= 1000013;

    /** Descripción de Campo */
    private int	AD_Tab_ID	= 1000031;

    /** Descripción de Campo */
    private M_Table	table	= null;

    /** Descripción de Campo */
    private int	AD_Client_ID	= Env.getContextAsInt(Env.getCtx(), "AD_Client_ID");

    /** Descripción de Campo */
    private static CLogger	Log	= CLogger.getCLogger(VMRPDetailed.class);

    /** Master (owning) Window */
    protected int	p_WindowNo;

    /** Table Name */
    private String	p_tableName	= getTableName();

    /** Key Column Name */
    protected String	p_keyColumn;

    /** Enable more than one selection */
    protected boolean	p_multiSelection	= true;

    /** Initial WHERE Clause */
    protected String	p_whereClause	= "";

    /** Table */
    protected MiniTable	p_table	= new MiniTable();

    /** Model Index of Key Column */
    private int	m_keyColumnIndex	= -1;

    /** OK pressed */
    private boolean	m_ok	= false;

    /** Cancel pressed - need to differentiate between OK - Cancel - Exit */
    private boolean	m_cancel	= false;

    /** Result IDs */
    private ArrayList	m_results	= new ArrayList(3);

    /** Layout of Grid */
    protected Info_Column[]	p_layout;

    /** Main SQL Statement */
    private String	m_sqlMain;

    /** Order By Clause */
    private String	m_sqlAdd;

    /** Descripción de Campo */
    private int	cont	= 0;

    /** Descripción de Campo */
    protected int	row	= 0;

    /** Loading success indicator */
    protected boolean	p_loadedOK	= false;

    /** SO Zoom Window */
    private int	m_SO_Window_ID	= -1;

    /** PO Zoom Window */
    private int	m_PO_Window_ID	= -1;

    /** Descripción de Campo */
    private ArrayList	vect	= new ArrayList();

    /** Worker */
    private Worker	m_worker	= null;

    /** Static Layout */
    private CPanel	southPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	southLayout	= new BorderLayout();

    /** Descripción de Campo */
    ConfirmPanel	confirmPanel	= new ConfirmPanel(true, true, true, true, true, true, true);

    /** Descripción de Campo */
    protected CPanel	parameterPanel	= new CPanel();

    /** Descripción de Campo */
    private JScrollPane	scrollPane	= new JScrollPane();

    //

    /** Descripción de Campo */
    private JPopupMenu	popup	= new JPopupMenu();

    /** Descripción de Campo */
    private JMenuItem	calcMenu	= new JMenuItem();

    /** Window Width */
    static final int	INFO_WIDTH	= 800;

    /** Descripción de Campo */
    private CLabel	lProduct_ID	= new CLabel(Msg.translate(Env.getCtx(), "M_Product_ID"));

    /** Descripción de Campo */
    private VLookup	fProduct_ID;

    /** Descripción de Campo */
    private CLabel	lResource_ID	= new CLabel(Msg.translate(Env.getCtx(), "S_Resource_ID"));

    /** Descripción de Campo */
    private VLookup	fResource_ID;

    /** Descripción de Campo */
    private CLabel	lWarehouse_ID	= new CLabel(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));

    /** Descripción de Campo */
    private VLookup	fWarehouse_ID;

    /** Descripción de Campo */
    private CLabel	lPlanner_ID	= new CLabel(Msg.translate(Env.getCtx(), "Planner_ID"));

    /** Descripción de Campo */
    private VLookup	fPlanner_ID;

    //

    /** Descripción de Campo */
    private CLabel	lDueStart	= new CLabel(Msg.translate(Env.getCtx(), "DueStart"));

    /** Descripción de Campo */
    private VDate	fDueStart	= new VDate("DueStart", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "DueStart"));

    /** Descripción de Campo */
    private CLabel	lDueEnd	= new CLabel("-");

    /** Descripción de Campo */
    private VDate	fDueEnd	= new VDate("DueEnd", false, false, true, DisplayType.Date, Msg.translate(Env.getCtx(), "DueEnd"));

//  private CLabel lMaster = new CLabel();
//  private CTextField fMaster = new CTextField(10);

    /** Descripción de Campo */
    private CLabel	lBPartner	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fBPartner	= new CTextField(10);

    /** Descripción de Campo */
    private CLabel	lPlanner	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fPlanner	= new CTextField(10);

    /** Descripción de Campo */
    private CLabel	lType	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fType	= new CTextField(6);

    /** Descripción de Campo */
    private CLabel	lUOM	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fUOM	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lOrderPeriod	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fOrderPeriod	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lTimefence	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fTimefence	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lLeadtime	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fLeadtime	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lReplenishMin	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fReplenishMin	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lMinOrd	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fMinOrd	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lMaxOrd	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fMaxOrd	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lOrdMult	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fOrdMult	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lOrderQty	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fOrderQty	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lYield	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fYield	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lOnhand	= new CLabel();

    /** Descripción de Campo */
    private CTextField	fOnhand	= new CTextField(5);

    /** Descripción de Campo */
    private CLabel	lSupplyType	= new CLabel(Msg.translate(Env.getCtx(), "TypeMRP"));

    /** Descripción de Campo */
    private VLookup	fSupplyType;

//  private CLabel lDocStatus = new CLabel(Msg.translate(Env.getCtx(), "DocStatus"));
//  private VLookup fDocStatus;

    /** Descripción de Campo */
    private VCheckBox	fMaster	= new VCheckBox("IsMPS", false, false, true, Msg.translate(Env.getCtx(), "IsMPS"), "", false);

    /** Descripción de Campo */
    private VCheckBox	fMRPReq	= new VCheckBox("IsRequiredMRP", false, false, true, Msg.translate(Env.getCtx(), "IsRequiredMRP"), "", false);

    /** Descripción de Campo */
    private VCheckBox	fCreatePlan	= new VCheckBox("IsCreatePlan", false, false, true, Msg.translate(Env.getCtx(), "IsCreatePlan"), "", false);

    /** Descripción de Campo */
    private VCheckBox	fIssue	= new VCheckBox("IsIssue", false, false, true, Msg.translate(Env.getCtx(), "IsIssue"), "", false);

    /** Array of Column Info */
    private static final Info_Column[]	m_layout	= {

        new Info_Column(" ", "MPC_MRP.MPC_MRP_ID", IDColumn.class),

        // new Info_Column(Msg.translate(Env.getCtx(), "C_BPartner_ID"), "(SELECT Name FROM C_BPartner bp WHERE bp.C_BPartner_ID=MPC_MRP.C_BPartner_ID)", String.class),
        new Info_Column(Msg.translate(Env.getCtx(), "Resource"), "(SELECT Name FROM S_Resource sr WHERE sr.S_Resource_ID=MPC_MRP.S_Resource_ID)", String.class), new Info_Column(Msg.translate(Env.getCtx(), "Warehouse"), "(SELECT Name FROM M_Warehouse wh WHERE wh.M_Warehouse_ID=MPC_MRP.M_Warehouse_ID)", String.class),

        // new Info_Column(Msg.translate(Env.getCtx(), "Name"), "(SELECT p.Name FROM M_Product p WHERE p.M_Product_ID=MPC_MRP.M_Product_ID)", String.class),
        new Info_Column(Msg.translate(Env.getCtx(), "DatePromised"), "MPC_MRP.DatePromised", Timestamp.class), new Info_Column(Msg.translate(Env.getCtx(), "Gross Reqs."), "(SELECT m.Qty FROM MPC_MRP m WHERE m.Type='D' AND m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)", BigDecimal.class), new Info_Column(Msg.translate(Env.getCtx(), "Schedule Reciept."), "(SELECT m.Qty FROM MPC_MRP m WHERE m.Type='S' AND m.DocStatus='CO' AND m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)", BigDecimal.class), new Info_Column(Msg.translate(Env.getCtx(), "Plan Orders"), "(SELECT m.Qty FROM MPC_MRP m WHERE m.Type='S' AND m.DocStatus<>'CO' AND m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)", BigDecimal.class),

        // new Info_Column(Msg.translate(Env.getCtx(), "Proj QOH"), "(SELECT decode((select m.Type from MPC_MRP m where  m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID), 'D',(BOM_Qty_OnHand( p.M_Product_ID , MPC_MRP.M_Warehouse_ID )- (select m.Qty from MPC_MRP m where m.type='D' and m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)),'S',(BOM_Qty_OnHand( p.M_Product_ID , MPC_MRP.M_Warehouse_ID )+ (select m.Qty from MPC_MRP m where m.type='S' and m.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID))) OnHand FROM M_Product p WHERE p.M_Product_ID =MPC_MRP.M_Product_ID)",  BigDecimal.class),
        new Info_Column(Msg.translate(Env.getCtx(), "Proj QOH"), "bomQtyOnHand( MPC_MRP.M_Product_ID , MPC_MRP.M_Warehouse_ID, 0)", BigDecimal.class),

        // SELECT  BOM_Qty_OnHand( p.M_Product_ID , 1000017 ) AS ONHand, decode((select m.Type from MPC_MRP m where  m.MPC_MRP_ID=1043722), 'D',(BOM_Qty_OnHand( p.M_Product_ID , 1000017 )- (select m.Qty from MPC_MRP m where m.type='D' and m.MPC_MRP_ID=1043722)),'S',(BOM_Qty_OnHand( p.M_Product_ID , 1000017 )+ (select m.Qty from MPC_MRP m where m.type='S' and m.MPC_MRP_ID=1043722))) OnHand FROM M_Product p WHERE p.M_Product_ID =1000484
        // new Info_Column(Msg.translate(Env.getCtx(), "C_UOM_ID"), "(SELECT um.Name FROM M_Product p INNER JOIN C_UOM um ON (um.C_UOM_ID=p.C_UOM_ID) WHERE p.M_Product_ID=MPC_MRP.M_Product_ID)", String.class),
        //              //new Info_Column(Msg.translate(Env.getCtx(), "C_UOM_ID"), "(SELECT p.C_UOM_ID FROM M_Product p WHERE p.M_Product_ID=MPC_MRP.M_Product_ID)", String.class),
        //               // new Info_Column(Msg.translate(Env.getCtx(), "C_Order_ID"), "(SELECT DocumentNo FROM C_Order order WHERE order.C_Order_ID=MPC_MRP.C_Order_ID)", String.class),
        //              new Info_Column(Msg.translate(Env.getCtx(), "M_Requisition_ID"), "MPC_MRP.M_Requisition_ID", String.class),
        //                new Info_Column(Msg.translate(Env.getCtx(), "C_Order_ID"), "MPC_MRP.C_Order_ID", String.class),
        //                new Info_Column(Msg.translate(Env.getCtx(), "MPC_Order_ID"), "(SELECT DocumentNo FROM MPC_Order mo WHERE mMPC_MRP.MPC_Order_ID=MPC_MRP.MPC_Order_ID)", String.class),
        //              
        //                new Info_Column(Msg.translate(Env.getCtx(), "M_Warehouse_ID"), "(SELECT Name FROM M_Warehouse w WHERE w.M_Warehouse_ID=MPC_MRP.M_Warehouse_ID)", String.class),
        //                new Info_Column(Msg.translate(Env.getCtx(), "S_Resource_ID"), "(SELECT Name FROM S_Resource r WHERE r.S_Resource_ID=MPC_MRP.S_Resource_ID)", String.class),
        //                new Info_Column(Msg.translate(Env.getCtx(), "TypeMRP"), "MPC_MRP.TypeMRP", String.class),
        new Info_Column(Msg.translate(Env.getCtx(), "Details"), "MPC_MRP.Type", String.class), new Info_Column(Msg.translate(Env.getCtx(), "Tipo"), "MPC_MRP.TypeMRP", String.class), new Info_Column(Msg.translate(Env.getCtx(), "DocumentNo"), "documentNo(MPC_MRP.MPC_MRP_ID)", String.class),

        /* new Info_Column(Msg.translate(Env.getCtx(), "Orden"), "(Select decode((select m.TypeMRP from MPC_MRP m where  m.MPC_MRP_ID=mrp.MPC_MRP_ID),'POO',(SELECT ord.DocumentNo FROM C_Order ord WHERE ord.C_Order_ID=mrp.C_Order_ID),'SOO',(SELECT ord.DocumentNo FROM C_Order ord WHERE ord.C_Order_ID=mrp.C_Order_ID) ,'MOP',(SELECT mo.DocumentNo FROM MPC_Order mo WHERE mo.MPC_Order_ID=mrp.MPC_Order_ID),'POR',(SELECT rq.DocumentNo FROM M_Requisition rq WHERE rq.M_Requisition_ID=mrp.M_Requisition_ID)) from MPC_MRP  mrp where mrp.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)", String.class), */

        // new Info_Column(Msg.translate(Env.getCtx(), "Status"), "(Select decode((select m.TypeMRP from MPC_MRP m where  m.MPC_MRP_ID=mrp.MPC_MRP_ID),'POO',(SELECT ord.DocStatus FROM C_Order ord WHERE ord.C_Order_ID=mrp.C_Order_ID),'SOO',(SELECT ord.DocStatus FROM C_Order ord WHERE ord.C_Order_ID=mrp.C_Order_ID) ,'MOP',(SELECT mo.DocStatus FROM MPC_Order mo WHERE mo.MPC_Order_ID=mrp.MPC_Order_ID),'POR',(SELECT rq.DocStatus FROM M_Requisition rq WHERE rq.M_Requisition_ID=mrp.M_Requisition_ID)) from MPC_MRP  mrp where mrp.MPC_MRP_ID=MPC_MRP.MPC_MRP_ID)", String.class),
        new Info_Column(Msg.translate(Env.getCtx(), "Status"), "MPC_MRP.DocStatus", String.class), new Info_Column(Msg.translate(Env.getCtx(), "DateStartSchedule"), "MPC_MRP.DateStartSchedule", Timestamp.class), new Info_Column(Msg.translate(Env.getCtx(), "C_BPartner_ID"), "(SELECT cb.Name FROM C_BPartner cb WHERE cb.C_BPartner_ID=MPC_MRP.C_BPartner_ID)", String.class)
    };

    /**
     *      Static Setup - add fields to parameterPanel
     *  @throws Exception if Lookups cannot be initialized
     */
    private void statInit() throws Exception {

        fResource_ID	= new VLookup("S_Resource_ID", false, false, true, MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, 1001112, DisplayType.TableDir));	// eerp

        // MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 1001105, DisplayType.TableDir)); //vimifos
        lResource_ID.setLabelFor(fResource_ID);
        fResource_ID.setBackground(CompierePLAF.getInfoBackground());
        fWarehouse_ID	= new VLookup("M_Warehouse_ID", false, false, true,

        // MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 1000514, DisplayType.TableDir));//eerp
        MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, 1000514, DisplayType.TableDir));	// vimifos
        lWarehouse_ID.setLabelFor(fWarehouse_ID);
        fWarehouse_ID.setBackground(CompierePLAF.getInfoBackground());
        fPlanner_ID	= new VLookup("Planner_ID", false, false, true, MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, 1001113, DisplayType.Table));		// eerp

        // MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 1001103, DisplayType.Table)); //vimifos
        lPlanner_ID.setLabelFor(fPlanner_ID);
        fPlanner_ID.setBackground(CompierePLAF.getInfoBackground());

//      lMaster.setText(Msg.translate(Env.getCtx(), "Master"));
//      fMaster.setBackground(CompierePLAF.getInfoBackground());
//      fMaster.setReadWrite(false);
//      lForecast_ID.setLabelFor(fForecast_ID);
//      fForecast_ID.setBackground(CompierePLAF.getInfoBackground());
//      fForecast_ID.setToolTipText(Msg.translate(Env.getCtx(), "Forecast_ID"));
        lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
        fBPartner.setBackground(CompierePLAF.getInfoBackground());
        fBPartner.setReadWrite(false);
        lPlanner.setText(Msg.translate(Env.getCtx(), "Planner_ID"));
        fPlanner.setBackground(CompierePLAF.getInfoBackground());
        fPlanner.setReadWrite(false);

//      fBPartner_ID.addActionListener(this);
        fMaster.setSelected(false);
        fMaster.setReadWrite(false);
        fMRPReq.setSelected(false);
        fMRPReq.setReadWrite(false);
        fCreatePlan.setSelected(false);
        fCreatePlan.setReadWrite(false);
        fIssue.setSelected(false);
        fIssue.setReadWrite(false);

        // columna 2
        lUOM.setText(Msg.translate(Env.getCtx(), "C_UOM_ID"));
        fUOM.setBackground(CompierePLAF.getInfoBackground());
        fUOM.setReadWrite(false);
        lType.setText(Msg.translate(Env.getCtx(), "Order_Policy"));
        fType.setBackground(CompierePLAF.getInfoBackground());
        fType.setReadWrite(false);
        lOrderPeriod.setText(Msg.translate(Env.getCtx(), "Order_Period"));
        fOrderPeriod.setBackground(CompierePLAF.getInfoBackground());
        fOrderPeriod.setReadWrite(false);
        lTimefence.setText(Msg.translate(Env.getCtx(), "TimeFence"));
        fTimefence.setBackground(CompierePLAF.getInfoBackground());
        fTimefence.setReadWrite(false);
        lLeadtime.setText(Msg.translate(Env.getCtx(), "DeliveryTime_Promised"));
        fLeadtime.setBackground(CompierePLAF.getInfoBackground());
        fLeadtime.setReadWrite(false);
        lReplenishMin.setText(Msg.translate(Env.getCtx(), "Level_Min"));
        fReplenishMin.setBackground(CompierePLAF.getInfoBackground());
        fReplenishMin.setReadWrite(false);

        // columna 3
        lMinOrd.setText(Msg.translate(Env.getCtx(), "Order_Min"));
        fMinOrd.setBackground(CompierePLAF.getInfoBackground());
        fMinOrd.setReadWrite(false);
        lMaxOrd.setText(Msg.translate(Env.getCtx(), "Order_Max"));
        fMaxOrd.setBackground(CompierePLAF.getInfoBackground());
        fMaxOrd.setReadWrite(false);
        lOrdMult.setText(Msg.translate(Env.getCtx(), "Order_Pack"));
        fOrdMult.setBackground(CompierePLAF.getInfoBackground());
        fOrdMult.setReadWrite(false);
        lOrderQty.setText(Msg.translate(Env.getCtx(), "Order_Qty"));
        fOrderQty.setBackground(CompierePLAF.getInfoBackground());
        fOrderQty.setReadWrite(false);
        lYield.setText(Msg.translate(Env.getCtx(), "Yield"));
        fYield.setBackground(CompierePLAF.getInfoBackground());
        fYield.setReadWrite(false);
        lOnhand.setText(Msg.translate(Env.getCtx(), "Qty"));
        fOnhand.setBackground(CompierePLAF.getInfoBackground());
        fOnhand.setReadWrite(false);

        // fIsSupply.addActionListener(this);
        //
        // fOrg_ID = new VLookup("AD_Org_ID", false, false, true,
        // MLookupFactory.create(Env.getCtx(), 3486, m_WindowNo, DisplayType.TableDir, false),
        // DisplayType.TableDir, m_WindowNo);
        // lOrg_ID.setLabelFor(fOrg_ID);
        // fOrg_ID.setBackground(CompierePLAF.getInfoBackground());
        fProduct_ID	= new VLookup("M_Product_ID", true, false, true,

        // MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 1001114, DisplayType.Search));//eerp
        MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, 1000483, DisplayType.Search));		// vimifos
        lProduct_ID.setLabelFor(fProduct_ID);
        fProduct_ID.setBackground(CompierePLAF.getInfoBackground());

        // fProduct_ID.addActionListener(this);
        //
        lDueStart.setLabelFor(fDueStart);
        fDueStart.setBackground(CompierePLAF.getInfoBackground());
        fDueStart.setToolTipText(Msg.translate(Env.getCtx(), "DueStart"));
        lDueEnd.setLabelFor(fDueEnd);
        fDueEnd.setBackground(CompierePLAF.getInfoBackground());
        fDueEnd.setToolTipText(Msg.translate(Env.getCtx(), "DueEnd"));
        fSupplyType	= new VLookup("TypeMRP", false, false, true,

        // MLookupFactory.get (Env.getCtx(), p_WindowNo, 0, 1001113, DisplayType.Table));//eerp
        MLookupFactory.get(Env.getCtx(), p_WindowNo, 0, 1000488, DisplayType.List));	// vimifos
        lSupplyType.setLabelFor(fSupplyType);
        fSupplyType.setBackground(CompierePLAF.getInfoBackground());

//      lSupplyType.setLabelFor(fSupplyType);
//      fSupplyType.setBackground(CompierePLAF.getInfoBackground());
//      fSupplyType.setToolTipText(Msg.translate(Env.getCtx(), "SupplyType"));
//      lDocStatus.setLabelFor(fDocStatus);
//      fDocStatus.setBackground(CompierePLAF.getInfoBackground());
//      fDocStatus.setToolTipText(Msg.translate(Env.getCtx(), "DocStatus"));
        //
        parameterPanel.setLayout(new ALayout());

        // First Row
        parameterPanel.add(lProduct_ID, new ALayoutConstraint(0, 0));
        parameterPanel.add(fProduct_ID, null);
        parameterPanel.add(lResource_ID, null);
        parameterPanel.add(fResource_ID, null);

        // parameterPanel.add(fIsSupply, new ALayoutConstraint(0,5));
        // 2nd Row
        parameterPanel.add(lWarehouse_ID, new ALayoutConstraint(1, 0));
        parameterPanel.add(fWarehouse_ID, null);
        parameterPanel.add(lDueStart, null);
        parameterPanel.add(fDueStart, null);
        parameterPanel.add(lDueEnd, null);
        parameterPanel.add(fDueEnd, null);

        // 3rd Row
        parameterPanel.add(lBPartner, new ALayoutConstraint(2, 0));
        parameterPanel.add(fBPartner, null);
        parameterPanel.add(lPlanner, new ALayoutConstraint(3, 0));
        parameterPanel.add(fPlanner, null);
        parameterPanel.add(fMaster, new ALayoutConstraint(4, 1));
        parameterPanel.add(fMRPReq, new ALayoutConstraint(5, 1));
        parameterPanel.add(fCreatePlan, new ALayoutConstraint(6, 1));
        parameterPanel.add(fIssue, new ALayoutConstraint(7, 1));

        // 3rd row and column 2
        parameterPanel.add(lUOM, new ALayoutConstraint(2, 2));
        parameterPanel.add(fUOM, new ALayoutConstraint(2, 3));
        parameterPanel.add(lType, new ALayoutConstraint(3, 2));
        parameterPanel.add(fType, new ALayoutConstraint(3, 3));
        parameterPanel.add(lOrderPeriod, new ALayoutConstraint(4, 2));
        parameterPanel.add(fOrderPeriod, new ALayoutConstraint(4, 3));
        parameterPanel.add(lTimefence, new ALayoutConstraint(5, 2));
        parameterPanel.add(fTimefence, new ALayoutConstraint(5, 3));
        parameterPanel.add(lLeadtime, new ALayoutConstraint(6, 2));
        parameterPanel.add(fLeadtime, new ALayoutConstraint(6, 3));
        parameterPanel.add(lOnhand, new ALayoutConstraint(7, 2));
        parameterPanel.add(fOnhand, new ALayoutConstraint(7, 3));

        // 3rd row and column 3
        // parameterPanel.add(lOnhand, new ALayoutConstraint(2,4));
        // parameterPanel.add(fOnhand, new ALayoutConstraint(2,5));
        parameterPanel.add(lMinOrd, new ALayoutConstraint(3, 4));
        parameterPanel.add(fMinOrd, new ALayoutConstraint(3, 5));
        parameterPanel.add(lMaxOrd, new ALayoutConstraint(4, 4));
        parameterPanel.add(fMaxOrd, new ALayoutConstraint(4, 5));
        parameterPanel.add(lOrdMult, new ALayoutConstraint(5, 4));
        parameterPanel.add(fOrdMult, new ALayoutConstraint(5, 5));
        parameterPanel.add(lOrderQty, new ALayoutConstraint(6, 4));
        parameterPanel.add(fOrderQty, new ALayoutConstraint(6, 5));
        parameterPanel.add(lYield, new ALayoutConstraint(7, 4));
        parameterPanel.add(fYield, new ALayoutConstraint(7, 5));

//      parameterPanel.add(lSupplyType, null);
//      parameterPanel.add(fSupplyType, null);
//      parameterPanel.add(lDocStatus, null);
//      parameterPanel.add(fDocStatus, null);
        // parameterPanel.add(lOrg_ID, null);
        // parameterPanel.add(fOrg_ID, null);

    }		// statInit

//  private  Info_Column[] m_layout = {
//            //new ColumnInfo(" "," ", IDColumn.class, true, true, ""),
//          new Info_Column(Msg.translate(Env.getCtx(), "Select"), p_tableName +".MPC_Order_ID", IDColumn.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "DocumentNo"), p_tableName + ".DocumentNo", String.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "DocumentNo"), p_tableName + ".Line", Integer.class),
//          new Info_Column(Msg.translate(Env.getCtx(), "M_Product_ID"), "(SELECT Name FROM M_Product p WHERE p.M_Product_ID=" +p_tableName + ".M_Product_ID)", String.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "C_UOM_ID"), "(SELECT Name FROM C_UOM u WHERE u.C_UOM_ID=" +p_tableName + ".C_UOM_ID)", String.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "QtyEntered"), p_tableName+".QtyEntered",  BigDecimal.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "QtyOrdered"), p_tableName+".QtyOrdered",  BigDecimal.class),
//          new Info_Column(Msg.translate(Env.getCtx(), "DateOrdered"), p_tableName+".DateOrdered", Timestamp.class),
//            new Info_Column(Msg.translate(Env.getCtx(), "DateStartSchedule"), p_tableName+".DateStartSchedule", Timestamp.class),
//          new Info_Column(Msg.translate(Env.getCtx(), "DateFinishSchedule"), p_tableName+".DateFinishSchedule", Timestamp.class)          
//          
//            //new Info_Column(Msg.translate(Env.getCtx(), "DateOrdered"), "MPC_MRP.DatePromided", Timestamp.class),
//          //new Info_Column(Msg.translate(Env.getCtx(), "ConvertedAmount"), "C_Base_Convert(MPC_MRP.GrandTotal,MPC_MRP.C_Currency_ID,MPC_MRP.AD_Client_ID,MPC_MRP.DateAcct, MPC_MRP.AD_Org_ID)", BigDecimal.class),
//          //new Info_Column(Msg.translate(Env.getCtx(), "IsSOTrx"), "MPC_MRP.IsSOTrx", Boolean.class),
//          //new Info_Column(Msg.translate(Env.getCtx(), "Description"), "MPC_MRP.Description", String.class),
//          //new Info_Column(Msg.translate(Env.getCtx(), "POReference"), "MPC_MRP.POReference", String.class)
//  };

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {	// GEN-BEGIN:initComponents

        mainPanel	= new javax.swing.JPanel();
        OrderPlanning	= new javax.swing.JTabbedPane();
        PanelOrder	= new javax.swing.JPanel();
        PanelFind	= new javax.swing.JPanel();
        PanelCenter	= new javax.swing.JPanel();
        PanelBottom	= new javax.swing.JPanel();
        Results		= new javax.swing.JPanel();
        setLayout(new java.awt.BorderLayout());
        mainPanel.setLayout(new java.awt.BorderLayout());
        PanelOrder.setLayout(new java.awt.BorderLayout());
        PanelOrder.add(PanelFind, java.awt.BorderLayout.NORTH);
        PanelOrder.add(PanelCenter, java.awt.BorderLayout.CENTER);
        PanelOrder.add(PanelBottom, java.awt.BorderLayout.SOUTH);
        OrderPlanning.addTab("Order", PanelOrder);
        OrderPlanning.addTab("Results", Results);
        mainPanel.add(OrderPlanning, java.awt.BorderLayout.CENTER);
        add(mainPanel, java.awt.BorderLayout.CENTER);

    }		// GEN-END:initComponents

    /**
     *      Static Init
     *  @throws Exception
     */
    protected void jbInit() throws Exception {

        // this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanel.setLayout(new java.awt.BorderLayout());
        setLayout(new java.awt.BorderLayout());
        southPanel.setLayout(southLayout);
        southPanel.add(confirmPanel, BorderLayout.CENTER);
        southPanel.add(statusBar, BorderLayout.SOUTH);

        /*
         * m_frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
         * m_frame.getContentPane().add(parameterPanel, BorderLayout.NORTH);
         * m_frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
         */
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(parameterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        /*
         * add(southPanel, BorderLayout.SOUTH);
         * add(parameterPanel, BorderLayout.NORTH);
         * add(scrollPane, BorderLayout.CENTER);
         */
        scrollPane.getViewport().add(p_table, null);

        //
        confirmPanel.addActionListener(this);
        confirmPanel.getResetButton().setVisible(hasReset());
        confirmPanel.getCustomizeButton().setVisible(hasCustomize());
        confirmPanel.getHistoryButton().setVisible(hasHistory());
        confirmPanel.getZoomButton().setVisible(hasZoom());

        //
        JButton	print	= ConfirmPanel.createPrintButton(true);

        print.addActionListener(this);
        confirmPanel.addButton(print);

        //
        popup.add(calcMenu);
        calcMenu.setText(Msg.getMsg(Env.getCtx(), "Calculator"));
        calcMenu.setIcon(new ImageIcon(org.openXpertya.OpenXpertya.class.getResource("images/Calculator16.gif")));
        calcMenu.addActionListener(this);

        //
        p_table.getSelectionModel().addListSelectionListener(this);

        /*
         * ListSelectionModel rowSM = p_table.getSelectionModel();
         * rowSM.addListSelectionListener(new ListSelectionListener() {
         * public void valueChanged(ListSelectionEvent e) {
         *   //Ignore extra messages.
         *   if (e.getValueIsAdjusting()) return;
         *
         *   ListSelectionModel lsm = (ListSelectionModel)e.getSource();
         *   if (lsm.isSelectionEmpty())
         *   {
         *       //int row = 0;
         *
         *   } else
         *   {
         *       int row = lsm.getMinSelectionIndex();
         *       System.out.println("Row Select" + row);
         *   }
         * }
         * });
         */
        enableButtons();
    }		// jbInit

    /**
     *  Dynamic Init.
     *  Table Layout, Visual, Listener
     */
    private void dynInit() {}

    /**
     *      Fill Picks
     *              Column_ID from C_Order
     *  @throws Exception if Lookups cannot be initialized
     */
    private void fillPicks() throws Exception {

        prepareTable(m_layout, getTableName(), find(), "4,9,3,2");

        // executeQuery();
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        // Confirm Panel
        //              String cmd = e.getActionCommand();
        //              if (cmd.equals(ConfirmPanel.A_OK))                    
        //              {
        //                  m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  
        //                    System.out.println("Row ...ID " + row);
        //                    int rows[] = p_table.getSelectedRows();
        //                    
        //                    for (int r = 0 ; r < rows.length ; r ++ )
        //                    {    
        //
        //                             IDColumn id = (IDColumn)p_table.getValueAt(rows[r], 0);  
        //                             if (id != null)
        //                             {    
        //                             Integer MPC_Order_ID = id.getRecord_ID();
        //                             MMPCOrder order = new MMPCOrder(Env.getCtx(), MPC_Order_ID.intValue());
        //                             order.setDocStatus(order.prepareIt());
        //                             order.setDocAction(order.DOCACTION_Complete);
        //                             order.save();
        //                             /*{                                 
        //                                javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) p_table.getModel();
        //                                model.removeRow(rows[r]);                                
        //                                p_table.setModel(model);
        //                                }
        //                              */
        //                             }
        //                    }        
        //  Confirm Panel
        String	cmd	= e.getActionCommand();

        if (cmd.equals(ConfirmPanel.A_OK)) {
            m_frame.dispose();
        } else if (cmd.equals(ConfirmPanel.A_CANCEL)) {

            m_cancel	= true;
            m_frame.dispose();

        } else if (cmd.equals(ConfirmPanel.A_ZOOM)) {
            zoom();
        } else {

            // prepareTable (m_layout, getTableName(), find() , "");
            System.out.println("REfresh antes del query");
            executeQuery();
            System.out.println("REfresh despues del query");

            BigDecimal	valorold	= Env.ZERO;
            BigDecimal	valorold2	= Env.ZERO;
            String	valorold2st	= "";
            int		pold		= 0;

            // ADialog.info(m_WindowNo,this,"ProcessOK");
        }

        // executeQuery();
        m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Descripción de Método
     *
     */
    public void dispose() {

        if (m_frame != null) {
            m_frame.dispose();
        }

        m_frame	= null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void executeASync(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isUILocked() {
        return false;
    }

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void lockUI(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void stateChanged(ChangeEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void tableChanged(TableModelEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void unlockUI(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private String find() {

        // StringBuffer select =  new StringBuffer("");
        //        Find find = new Find (Env.getFrame(this), AD_Window_ID, this.getName(),AD_Table_ID , getTableName() ,"", getFields(), 1);
        //      MQuery query = find.getQuery();
        //        //select.append("SELECT * FROM " + table.getName() + " WHERE " + query.toString());               
        //        
        //      return query.getWhereClause();  
        StringBuffer	sql	= new StringBuffer();

        if (fProduct_ID.getValue() != null) {

            sql.append(" AND MPC_MRP.M_Product_ID=?");
            sql.append(" AND ((MPC_MRP.TYPEMRP IN ('SOO','MOP','POO','POR')) OR (MPC_MRP.TypeMRP='FCT' AND MPC_MRP.DatePromised >= SYSDATE))");

            // System.out.println("*******lineas********"+p_table.getRowCount());
            setHead(fProduct_ID.getValue().toString());
            setMRP(fProduct_ID.getValue().toString());
        }

        if (fResource_ID.getValue() != null) {
            sql.append(" AND MPC_MRP.S_Resource_ID=?");
        }

        if (fWarehouse_ID.getValue() != null) {
            sql.append(" AND MPC_MRP.M_Warehouse_ID=?");
        }

        // if (fPlanner_ID.getValue() != null)
        // sql.append(" AND MPC_MRP.Planner_ID=?");
        if ((fDueStart.getValue() != null) || (fDueStart.getValue() != null)) {

            Timestamp	from	= (Timestamp) fDueStart.getValue();
            Timestamp	to	= (Timestamp) fDueEnd.getValue();

            if ((from == null) && (to != null)) {
                sql.append(" AND TRUNC(MPC_MRP.DatePromised) <= ?");
            } else if ((from != null) && (to == null)) {
                sql.append(" AND TRUNC(MPC_MRP.DatePromised) >= ?");
            } else if ((from != null) && (to != null)) {
                sql.append(" AND TRUNC(MPC_MRP.DatePromised) BETWEEN ? AND ?");
            }
        }

        // if (fSupplyType.getValue() != null)
        // sql.append(" AND MPC_MRP.TypeMRP=?");
        // if (fDocStatus.getValue() != null)
        // sql.append(" AND MPC_MRP.=?");
        //
        // if (fAmtFrom.getValue() != null || fAmtTMPC_MRP.getValue() != null)
        // {
        // BigDecimal from = (BigDecimal)fAmtFrom.getValue();
        // BigDecimal to = (BigDecimal)fAmtTMPC_MRP.getValue();
        // if (from == null && to != null)
        // sql.append(" AND o.GrandTotal <= ?");
        // else if (from != null && to == null)
        // sql.append(" AND MPC_MRP.GrandTotal >= ?");
        // else if (from != null && to != null)
        // sql.append(" AND MPC_MRP.GrandTotal BETWEEN ? AND ?");
        // }
        // sql.append(" AND MPC_MRP.Type=?");

        // Log.trace(Log.l6_Database, "InfoOrder.setWhereClause", sql.toString());
        return sql.toString();
    }

    /**
     * Descripción de Método
     *
     *
     * @param product
     */
    private void setHead(String product) {

        String		IsMPS		= "";
        String		IsRequiredMRP	= "";
        String		IsCreatePlan	= "";
        String		IsIssue		= "";
        BigDecimal	pbabd		= Env.ZERO;
        StringBuffer	sql		= new StringBuffer("SELECT mpp.IsMPS,mpp.Order_Period, mpp.IsRequiredMRP,mpp.IsCreatePlan, mpp.IsIssue, mpp.DeliveryTime_Promised,mpp.TimeFence, mpp.Order_Min, mpp.Order_Max,mpp.Order_Pack, mpp.Order_Qty, mpp.Yield, mpp.Order_Policy FROM MPC_Product_Planning mpp WHERE  mpp.M_Product_ID=? AND mpp.AD_Client_ID=?");

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setString(1, product);
            pstmt.setInt(2, AD_Client_ID);

            ResultSet	rs	= pstmt.executeQuery();

            //
            while (rs.next()) {

                IsMPS	= rs.getString(1);

                if (IsMPS.equals("N")) {
                    fMaster.setSelected(false);
                } else {
                    fMaster.setSelected(true);
                }

                IsRequiredMRP	= rs.getString(3);

                if (IsRequiredMRP.equals("N")) {
                    fMRPReq.setSelected(false);
                } else {
                    fMRPReq.setSelected(true);
                }

                IsCreatePlan	= rs.getString(4);

                if (IsCreatePlan.equals("N")) {
                    fCreatePlan.setSelected(false);
                } else {
                    fCreatePlan.setSelected(true);
                }

                IsIssue	= rs.getString(5);

                if (IsIssue.equals("N")) {
                    fIssue.setSelected(false);
                } else {
                    fIssue.setSelected(true);
                }

                if (rs.getString(2) != null) {
                    fOrderPeriod.setText(rs.getString(2).toString());
                }

                if (rs.getString(6) != null) {
                    fLeadtime.setText(rs.getString(6).toString());
                }

                if (rs.getString(7) != null) {
                    fTimefence.setText(rs.getString(7).toString());
                }

                if (rs.getString(8) != null) {
                    fMinOrd.setText(rs.getString(8).toString());
                }

                if (rs.getString(9) != null) {
                    fMaxOrd.setText(rs.getString(9).toString());
                }

                if (rs.getString(10) != null) {
                    fOrdMult.setText(rs.getString(10).toString());
                }

                if (rs.getString(11) != null) {
                    fOrderQty.setText(rs.getString(11).toString());
                }

                if (rs.getString(12) != null) {
                    fYield.setText(rs.getString(12).toString());
                }

                if (rs.getString(13) != null) {
                    fType.setText(rs.getString(13).toString());
                }
            }

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            Log.log(Level.SEVERE, "No KeyColumn - " + sql, ex);
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @param product
     */
    private void setMRP(String product) {

        String		BPartner	= "";
        String		Planner		= "";
        String		Onhand		= "";
        String		Type		= "";
        int		UOM		= 0;
        String		Level_min	= "";
        StringBuffer	sql		= new StringBuffer("SELECT mrp.C_BPartner_ID, mrp.Type, mrp.Planner_ID FROM MPC_MRP mrp WHERE mrp.M_Product_ID=? AND mrp.AD_Client_ID = ?");

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setString(1, product);
            pstmt.setInt(2, AD_Client_ID);

            ResultSet	rs	= pstmt.executeQuery();

            //
            while (rs.next()) {

                BPartner	= rs.getString(1);
                fBPartner.setText(BPartner);
                Planner	= rs.getString(3);
                fPlanner.setText(Planner);

                Integer	M_Product_ID	= new Integer(product);

                Onhand	= (MMPCMRP.getOnHand(M_Product_ID.intValue())).toString();
                fOnhand.setText(Onhand);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            Log.log(Level.SEVERE, "No KeyColumn - " + sql, ex);
        }

        sql	= new StringBuffer("SELECT mrp.C_UOM_ID FROM M_Product mrp WHERE mrp.AD_Client_ID=? AND mrp.M_Product_ID=?");

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setInt(1, AD_Client_ID);
            pstmt.setString(2, product);

            ResultSet	rs	= pstmt.executeQuery();

            //
            if (rs.next()) {

                UOM	= rs.getInt(1);

                MUOM		um	= new MUOM(Env.getCtx(), UOM, null);
                KeyNamePair	kum	= new KeyNamePair(um.getC_UOM_ID(), um.getName());

                fUOM.setText(kum.toString());
            }

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            Log.log(Level.SEVERE, "setMRP - " + sql, ex);
        }

        sql	= new StringBuffer("SELECT  mr.Level_Min FROM M_Replenish mr WHERE mr.AD_Client_ID=? AND mr.M_Product_ID=?");

        try {

            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setInt(1, AD_Client_ID);
            pstmt.setString(2, product);

            ResultSet	rs	= pstmt.executeQuery();

            //
            while (rs.next()) {

                Level_min	= rs.getString(1);
                fReplenishMin.setText(Level_min);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException ex) {
            Log.log(Level.SEVERE, "No KeyColumn - " + sql, ex);
        }
    }

    /*
     * private MField[] getFields()
     * {
     *   ArrayList list = new ArrayList();
     *
     *   M_Column[] cols = table.getColumns(true);
     *
     *   for (int c = 0 ; c < cols.length ; c++)
     *   {
     *           StringBuffer sql = new StringBuffer("SELECT * FROM AD_Column WHERE AD_Column_ID = " + cols[c].getAD_Column_ID());
     *
     *           try
     *           {
     *                   PreparedStatement pstmt = DB.prepareStatement(sql.toString());
     *                   ResultSet rs = pstmt.executeQuery();
     *                   while (rs.next())
     *                   {
     *                       MFieldVO vo = MFieldVO.create (Env.getCtx(), m_WindowNo, AD_Tab_ID , AD_Window_ID, true,rs);
     *                       MField field = new MField(vo);
     *                       //System.out.println("Columna -------:" + field.getColumnName());
     *                       list.add(field);
     *                   }
     *
     *                   rs.close();
     *                   pstmt.close();
     *           }
     *           catch (SQLException e)
     *           {
     *                   s_log.error("getAllIDs\nSQL=" + sql.toString(), e);
     *           }
     *
     *   }
     *
     *   /
     *           MField[] lines = new MField[list.size ()];
     *           list.toArray (lines);
     *           return lines;
     * }
     *
     */

    /**
     *  Reset Parameters
     *      To be overwritten by concrete classes
     */
    void doReset() {}

    /**
     *  Has Reset (false)
     *      To be overwritten by concrete classes
     *  @return true if it has reset (default false)
     */
    boolean hasReset() {
        return false;
    }

    /**
     *  History dialog
     *      To be overwritten by concrete classes
     */
    void showHistory() {}

    /**
     *  Has History (false)
     *      To be overwritten by concrete classes
     *  @return true if it has history (default false)
     */
    boolean hasHistory() {
        return false;
    }

    /**
     *  Customize dialog
     *      To be overwritten by concrete classes
     */
    void customize() {}

    /**
     *  Has Customize (false)
     *      To be overwritten by concrete classes
     *  @return true if it has customize (default false)
     */
    boolean hasCustomize() {
        return false;
    }

    /**
     *  Zoom action
     *      To be overwritten by concrete classes
     */
    void zoom() {

        Log.info("InfoMRPDeatiled.zoom");

        Integer	MPC_MPR_ID	= getSelectedRowKey();
        int	AD_WindowNo	= 0;

        if (MPC_MPR_ID == null) {
            return;
        }

        MQuery	query	= null;

        // int AD_WindowNo = getAD_Window_ID("C_Order", fIsSOTrx.isSelected());
        MMPCMRP	mrp	= new MMPCMRP(Env.getCtx(), MPC_MPR_ID.intValue(), null);
        String	typemrp	= mrp.getTypeMRP();

        if (typemrp.equals("POO")) {

            AD_WindowNo	= 181;
            query	= new MQuery("C_Order");
            query.addRestriction("C_Order_ID", MQuery.EQUAL, mrp.getC_Order_ID());

        } else if (typemrp.equals("SOO")) {

            AD_WindowNo	= 143;
            query	= new MQuery("C_Order");
            query.addRestriction("C_Order_ID", MQuery.EQUAL, mrp.getC_Order_ID());

        } else if (typemrp.equals("MOP")) {

            AD_WindowNo	= 1000013;
            query	= new MQuery("MPC_Order");
            query.addRestriction("MPC_Order_ID", MQuery.EQUAL, mrp.getMPC_Order_ID());

        } else if (typemrp.equals("POR")) {

            AD_WindowNo	= 322;
            query	= new MQuery("M_Requisition");
            query.addRestriction("M_Requisition_ID", MQuery.EQUAL, mrp.getM_Requisition_ID());

        } else if (typemrp.equals("FCT")) {

            AD_WindowNo	= 328;
            query	= new MQuery("M_Forecat");
            query.addRestriction("M_Forecast_ID", MQuery.EQUAL, mrp.getM_Forecast_ID());
        }

        if (AD_WindowNo == 0) {
            return;
        }

        Log.info("AD_WindowNo " + AD_WindowNo);
        zoom(AD_WindowNo, query);
    }

    /**
     *      Zoom to target
     *  @param AD_Window_ID window id
     *  @param zoomQuery zoom query
     */
    void zoom(int AD_Window_ID, MQuery zoomQuery) {

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final AWindow	frame	= new AWindow();

        if (!frame.initWindow(AD_Window_ID, zoomQuery)) {
            return;
        }

        // Modal Window causes UI lock
        // if (isModal())
        // {
        // setModal(false);        //      remove modal option has no effect
        // dispose();                      //      VLookup.actionButton - Result = null (not cancelled)
        // }
        // else
        // setCursor(Cursor.getDefaultCursor());

        // VLookup gets info after method finishes
        new Thread() {

            public void run() {

                try {
                    sleep(50);
                } catch (Exception e) {}

                AEnv.showCenterScreen(frame);
            }

        }.start();

    }		// zoom

    /**
     *  Has Zoom (false)
     *      To be overwritten by concrete classes
     *  @return true if it has zoom (default false)
     */
    boolean hasZoom() {
        return true;
    }

    /**
     *  Enable OK, History, Zoom if row selected
     */
    void enableButtons() {

        boolean	enable	= true;		// p_table.getSelectedRow() != -1;

        confirmPanel.getOKButton().setEnabled(true);

        if (hasHistory()) {
            confirmPanel.getHistoryButton().setEnabled(enable);
        }

        if (hasZoom()) {
            confirmPanel.getZoomButton().setEnabled(enable);
        }

    }		// enableButtons

    /**
     *  Execute Query
     */
    void executeQuery() {

        // ignore when running
        if ((m_worker != null) && m_worker.isAlive()) {
            return;
        }

        m_worker	= new Worker();
        m_worker.start();
    }		// executeQuery

    /**
     *  Prepare Table, Construct SQL (m_m_sqlMain, m_sqlAdd)
     *  and size Window
     *  @param layout layout array
     *  @param from from clause
     *  @param staticWhere where clause
     *  @param orderBy order by clause
     */
    protected void prepareTable(Info_Column[] layout, String from, String staticWhere, String orderBy) {

        p_layout	= layout;

        StringBuffer	sql	= new StringBuffer("SELECT ");

        // add columns & sql
        for (int i = 0; i < layout.length; i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append(layout[i].getColSQL());

            // adding ID column
            if (layout[i].isIDcol()) {
                sql.append(",").append(layout[i].getIDcolSQL());
            }

            // add to model
            p_table.addColumn(layout[i].getColHeader());

            if (layout[i].isColorColumn()) {
                p_table.setColorColumn(i);
            }

            if (layout[i].getColClass() == IDColumn.class) {
                m_keyColumnIndex	= i;
            }
        }

        // set editors (two steps)
        for (int i = 0; i < layout.length; i++) {
            p_table.setColumnClass(i, layout[i].getColClass(), layout[i].isReadOnly(), layout[i].getColHeader());
        }

        sql.append(" FROM ").append(from);

        //
        StringBuffer	where	= new StringBuffer("MPC_MRP.DocStatus IN ('IP','CO','DR','NA')  AND MPC_MRP.IsActive='Y' and MPC_MRP.Qty!=0 ");

        sql.append(" WHERE ").append(where.toString());

        if (!staticWhere.equals("")) {
            sql.append(staticWhere);
        }

        System.out.println("query completo ************   " + sql.toString());
        m_sqlMain	= sql.toString();
        System.out.println("m_sqlMain    " + m_sqlMain);
        m_sqlAdd	= "";

        if ((orderBy != null) && (orderBy.length() > 0)) {
            m_sqlAdd	= " ORDER BY " + orderBy;
        }

        if (m_keyColumnIndex == -1) {
            Log.log(Level.SEVERE, "No KeyColumn - " + sql);
        }

        // Table Selection
        p_table.setRowSelectionAllowed(true);

        // p_table.addMouseListener(this);
        p_table.setMultiSelection(false);
        p_table.setEditingColumn(0);
        p_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Window Sizing
        parameterPanel.setPreferredSize(new Dimension(INFO_WIDTH, parameterPanel.getPreferredSize().height));
        scrollPane.setPreferredSize(new Dimension(INFO_WIDTH, 400));

    }		// prepareTable

    /**
     *  Get the key of currently selected row
     *  @return selected key
     */
    protected Integer getSelectedRowKey() {

        int	row	= p_table.getSelectedRow();

        if ((row != -1) && (m_keyColumnIndex != -1)) {

            Object	data	= p_table.getModel().getValueAt(row, m_keyColumnIndex);

            if (data instanceof IDColumn) {
                data	= ((IDColumn) data).getRecord_ID();
            }

            if (data instanceof Integer) {
                return (Integer) data;
            }
        }

        return null;

    }		// getSelectedRowKey

    /**
     *  Set Parameters for Query.
     *  (as defined in getSQLWhere)
     *  @param pstmt statement
     *  @throws SQLException
     */
    void setParameters(PreparedStatement pstmt) throws SQLException {

        int	index	= 1;

        if (fProduct_ID.getValue() != null) {

            Integer	pp	= (Integer) fProduct_ID.getValue();

            pstmt.setInt(index++, pp.intValue());
            Log.fine("Product=" + pp);
        }

        if (fResource_ID.getValue() != null) {

            Integer	r	= (Integer) fResource_ID.getValue();

            pstmt.setInt(index++, r.intValue());
            Log.fine("Resource=" + r);
        }

        if (fWarehouse_ID.getValue() != null) {

            Integer	w	= (Integer) fWarehouse_ID.getValue();

            pstmt.setInt(index++, w.intValue());
            Log.fine("Warehouse=" + w);
        }

//      if (fPlanner_ID.getValue() != null)
//      {
//            Integer f = (Integer)fPlanner_ID.getValue();
//            pstmt.setInt(index++, f.intValue());
//            Log.trace(Log.l5_DData, "Forecast=" + f);
//      }

        //
        //
        if ((fDueStart.getValue() != null) || (fDueEnd.getValue() != null)) {

            Timestamp	from	= (Timestamp) fDueStart.getValue();
            Timestamp	to	= (Timestamp) fDueEnd.getValue();

            Log.fine("Date From=" + from + ", To=" + to);

            if ((from == null) && (to != null)) {
                pstmt.setTimestamp(index++, to);
            } else if ((from != null) && (to == null)) {
                pstmt.setTimestamp(index++, from);
            } else if ((from != null) && (to != null)) {

                pstmt.setTimestamp(index++, from);
                pstmt.setTimestamp(index++, to);
            }
        }

//      if (fSupplyType.getValue() != null)
//      {
//            String st = (String)fSupplyType.getValue();
//            pstmt.setString(index++, st);
//            Log.trace(Log.l5_DData, "SupplyType=" + st);
//      }
        //
//      if (fAmtFrom.getValue() != null || fAmtTo.getValue() != null)
//      {
//            BigDecimal from = (BigDecimal)fAmtFrom.getValue();
//            BigDecimal to = (BigDecimal)fAmtTo.getValue();
//            Log.trace(Log.l5_DData, "Amt From=" + from + ", To=" + to);
//            if (from == null && to != null)
//                    pstmt.setBigDecimal(index++, to);
//            else if (from != null && to == null)
//                    pstmt.setBigDecimal(index++, from);
//            else if (from != null && to != null)
//            {
//                    pstmt.setBigDecimal(index++, from);
//                    pstmt.setBigDecimal(index++, to);
//            }
//      }
//       if (fIsSupply.isSelected())
//      {
//            
//            pstmt.setString(index++, "S");
//            
//      }
//       else
//       {
//           pstmt.setString(index++, "D");
//       }
        // pstmt.setString(index++, fIsSOTrx.isSelected() ? "Y" : "N");

    }		// setParameters

    /**
     *  Get Table name Synonym
     *  @return table name
     */
    String getTableName() {

        table		= new M_Table(Env.getCtx(), AD_Table_ID, null);
        p_tableName	= table.getTableName();

        return p_tableName;

    }		// getTableName

    // Variables declaration - do not modify//GEN-BEGIN:variables

    /** Descripción de Campo */
    private javax.swing.JTabbedPane	OrderPlanning;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelBottom;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelCenter;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelFind;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelOrder;

    /** Descripción de Campo */
    private javax.swing.JPanel	Results;

    /** Descripción de Campo */
    private javax.swing.JPanel	mainPanel;

    // End of variables declaration//GEN-END:variables

    /**
     *      Worker
     */
    class Worker extends Thread {

        /**
         *  Do Work (load data)
         */
        public void run() {

            vect.clear();
            cont	= 0;
            Log.fine("Info.Worker.run");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // setStatusLine(Msg.getMsg(Env.getCtx(), "StartSearch"), false);

            // Clear Table
            p_table.setRowCount(0);

            //
            // System.out.println("########" + m_sqlMain);

            StringBuffer	sql	= new StringBuffer(m_sqlMain);

            // String dynWhere = "" ;//find ();
            String	dynWhere	= find();

            if (dynWhere.length() > 0) {

                System.out.println("where" + dynWhere);
                sql.append(dynWhere);		// includes first AND
            }

            sql.append(m_sqlAdd);

            String	xSql	= Msg.parseTranslation(Env.getCtx(), sql.toString());		// Variables

            xSql	= MRole.getDefault().addAccessSQL(xSql, getTableName(), MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);

            try {

                PreparedStatement	pstmt	= DB.prepareStatement(xSql);

                Log.fine("SQL=" + xSql);
                setParameters(pstmt);

                // Log.trace(Log.l6_Database, "Info.Worker.run - start query");
                ResultSet	rs	= pstmt.executeQuery();

                // Log.trace(Log.l6_Database, "Info.Worker.run - end query");
                while (!isInterrupted() & rs.next()) {

                    int	row	= p_table.getRowCount();

                    p_table.setRowCount(row + 1);

                    int	colOffset	= 1;	// columns start with 1

                    for (int col = 0; col < p_layout.length; col++) {

                        Object	data		= null;
                        Class	c		= p_layout[col].getColClass();
                        int	colIndex	= col + colOffset;

                        if (c == IDColumn.class) {

                            IDColumn	id	= new IDColumn(rs.getInt(colIndex));

                            id.setSelected(true);

                            // data = new IDColumn(rs.getInt(colIndex));
                            data	= id;
                            p_table.setColumnReadOnly(0, false);

                            // p_table.setColumnReadOnly(1, false);

                        } else if (c == Boolean.class) {
                            data	= new Boolean("Y".equals(rs.getString(colIndex)));
                        } else if (c == Timestamp.class) {
                            data	= rs.getTimestamp(colIndex);
                        } else if (c == BigDecimal.class) {
                            data	= rs.getBigDecimal(colIndex);
                        } else if (c == Double.class) {
                            data	= new Double(rs.getDouble(colIndex));
                        } else if (c == Integer.class) {
                            data	= new Integer(rs.getInt(colIndex));
                        } else if (c == KeyNamePair.class) {

                            String	display	= rs.getString(colIndex);
                            int		key	= rs.getInt(colIndex + 1);

                            data	= new KeyNamePair(key, display);
                            colOffset++;

                        } else {
                            data	= rs.getString(colIndex);
                        }

                        // store
                        p_table.setValueAt(data, row, col);

                        // Log.trace(Log.l6_Database, "r=" + row + ", c=" + col + " " + m_layout[col].getColHeader(),
                        // "data=" + data.toString() + " " + data.getClass().getName() + " * " + m_table.getCellRenderer(row, col));
                    }
                }

                Log.fine("Info.Worker.run - interrupted=" + isInterrupted());
                rs.close();
                pstmt.close();

            } catch (SQLException e) {
                Log.log(Level.SEVERE, "Info.Worker.run - " + xSql, e);
            }

            p_table.autoSize();

            //
            setCursor(Cursor.getDefaultCursor());

            int	no	= p_table.getRowCount();

            System.out.println("no de lineas de p_Table      " + no);

            for (int p = 0; p < p_table.getRowCount(); p++) {

                Timestamp	datepromised	= (Timestamp) p_table.getValueAt(p, 3);
                Timestamp	today		= new Timestamp(System.currentTimeMillis());

                if (p_table.getValueAt(p, 8).toString().equals("D") || (p_table.getValueAt(p, 8).toString().equals("D") && p_table.getValueAt(p, 9).toString().equals("FCT") && datepromised.after(today)))

                // if (p_table.getValueAt(p,8).toString().equals("D"))
                {

                    BigDecimal	QtyGrossReqs	= (p_table.getValueAt(p, 4) != null)
                                                  ? new BigDecimal(p_table.getValueAt(p, 4).toString())
                                                  : Env.ZERO;

                    vect.add(cont, QtyGrossReqs);
                    cont++;
                    vect.add(cont, p_table.getValueAt(p, 8));
                    cont++;
                }

                if (p_table.getValueAt(p, 8).toString().equals("S")) {

                    BigDecimal	QtyScheduledReceipts	= (p_table.getValueAt(p, 5) != null)
                            ? new BigDecimal(p_table.getValueAt(p, 5).toString())
                            : Env.ZERO;

                    QtyScheduledReceipts	= QtyScheduledReceipts.add((p_table.getValueAt(p, 6) != null)
                            ? new BigDecimal(p_table.getValueAt(p, 6).toString())
                            : Env.ZERO);
                    vect.add(cont, QtyScheduledReceipts);
                    cont++;
                    vect.add(cont, p_table.getValueAt(p, 8));
                    cont++;
                }
            }

            BigDecimal	OnHand	= (fOnhand.getValue().toString().length() != 0)
                                  ? new BigDecimal(fOnhand.getValue().toString())
                                  : Env.ZERO;
            BigDecimal	QtyOrder	= Env.ZERO;
            int		renglon		= 0;

            for (int p = 0; p < vect.size(); p = p + 2) {

                QtyOrder	= (BigDecimal) vect.get(p);

                if (vect.get(p + 1).toString().equals("D")) {

                    OnHand	= OnHand.subtract(QtyOrder);
                    p_table.setValueAt(OnHand, renglon, 7);
                }

                if (vect.get(p + 1).toString().equals("S")) {

                    OnHand	= OnHand.add(QtyOrder);
                    p_table.setValueAt(OnHand, renglon, 7);
                }

                renglon++;
            }

        }	// run
    }		// Worker
}



/*
 * @(#)VMRPDetailed.java   14.jun 2007
 * 
 *  Fin del fichero VMRPDetailed.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
