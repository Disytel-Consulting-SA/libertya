/*
 * @(#)VOrderReceiptIssue.java   14.jun 2007  Versión 2.2
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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.math.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Component;
//import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;

import openXpertya.model.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;


import javax.swing.*;
import javax.swing.tree.*;

import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.openXpertya.apps.*;
import org.openXpertya.apps.form.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *
 * @author  vpj-cd
 */

public class VOrderReceiptIssue extends CPanel
	implements FormPanel, ActionListener, VetoableChangeListener, ChangeListener, ListSelectionListener, TableModelListener, ASyncProcess
{
    
    /** Creates new form VOrderReceiptIssue */
    /*public VOrderReceiptIssue() {
        //initComponents();
    }*/
    
    
        /**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		m_WindowNo = WindowNo;
		m_frame = frame;
		log.info("VOrderReceipIssue.init - WinNo=" + m_WindowNo +                        
			"AD_Client_ID=" + m_AD_Client_ID + ", AD_Org_ID=" + m_AD_Org_ID);
		//Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "N");

		try
		{
			//	UI
                        
			fillPicks();
			jbInit();
			//
			dynInit();
			frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
			frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "VOrderReceipIssue.init", e);
		}
	}	//	init
        
                

    
        /**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	FormFrame			*/
	private FormFrame 		m_frame;
        private StatusBar statusBar = new StatusBar();
        
	private int     m_AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
	private int     m_AD_Org_ID = Env.getAD_Org_ID(Env.getCtx());
	private int     m_by = Env.getAD_User_ID(Env.getCtx());
        private MMPCOrder m_mpc_order = null;
        private static CLogger log = CLogger.getCLogger(VOrderReceiptIssue.class);
    
    /**
	 *  Static Init.
	 *  <pre>
	 *  mainPanel
	 *      northPanel
	 *      centerPanel
	 *          xMatched
	 *          xPanel
	 *          xMathedTo
	 *      southPanel
	 *  </pre>
	 *  @throws Exception
	 */
	private void jbInit() throws Exception
	{
                java.awt.GridBagConstraints gridBagConstraints;

        /*TabsReceiptsIssue = new javax.swing.JTabbedPane();
        ReceiptIssueOrder = new javax.swing.JPanel();
        PanelCenter = new javax.swing.JPanel();
        issueScrollPane = new javax.swing.JScrollPane();
        issue = new javax.swing.JTable();
        PanelBottom = new javax.swing.JPanel();
        Process = new javax.swing.JButton();
        northPanel = new javax.swing.JPanel();
        orgLabel = new javax.swing.JLabel();
        org = new javax.swing.JTextField();
        resourceLabel = new javax.swing.JLabel();
        resource = new javax.swing.JTextField();
        orderLabel = new javax.swing.JLabel();
        order = new javax.swing.JTextField();
        movementDateLabel = new javax.swing.JLabel();
        movementDate = new javax.swing.JTextField();
        deliveryQtyLabel = new javax.swing.JLabel();
        deliveryQty = new javax.swing.JTextField();
        scrapQtyLabel = new javax.swing.JLabel();
        scrapQty = new javax.swing.JTextField();
        rejectQtyLabel = new javax.swing.JLabel();
        rejectQty = new javax.swing.JTextField();
        attributeLabel = new javax.swing.JLabel();
        attribute = new javax.swing.JTextField();
        issueMethodLabel = new javax.swing.JLabel();
        issueMethod = new javax.swing.JTextField();
        Generate = new javax.swing.JPanel();*/
        
        setLayout(new java.awt.BorderLayout());
        mainPanel.setLayout(new java.awt.BorderLayout());
         
        ReceiptIssueOrder.setLayout(new java.awt.BorderLayout());

        PanelCenter.setLayout(new java.awt.BorderLayout());

        issueScrollPane.setBorder(new javax.swing.border.TitledBorder(""));

        issueScrollPane.setViewportView(issue);

        PanelCenter.add(issueScrollPane, java.awt.BorderLayout.CENTER);

        ReceiptIssueOrder.add(PanelCenter, java.awt.BorderLayout.CENTER);

        Process.setText(Msg.translate(Env.getCtx(), "OK"));


        PanelBottom.add(Process);

        ReceiptIssueOrder.add(PanelBottom, java.awt.BorderLayout.SOUTH);

        northPanel.setLayout(new java.awt.GridBagLayout());


        orderLabel.setText(Msg.translate(Env.getCtx(), "MPC_Order_ID"));
        
        northPanel.add(orderLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(order,     new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));   

        
        resourceLabel.setText(Msg.translate(Env.getCtx(), "S_Resource_ID"));
        
        northPanel.add(resourceLabel,    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(resource,     new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));   

        
        warehouseLabel.setText(Msg.translate(Env.getCtx(), "M_Warehouse_ID"));
        
        northPanel.add(warehouseLabel,    new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(warehouse,     new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0)); 
        
        
         northPanel.add(warehouseLabel,    new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
         // Product
        
         northPanel.add(producLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(product,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0)); 
        
        
        northPanel.add(uomLabel,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(uom,     new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));               
        

          northPanel.add(uomorderLabel,    new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(uomorder,     new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
        
        orderedQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyOrdered"));
        
        northPanel.add(orderedQtyLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(orderedQty,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));   

        deliveredQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyDelivered"));
        
        northPanel.add(deliveredQtyLabel,    new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(deliveredQty,     new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        openQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyToDeliver"));
        
        northPanel.add(openQtyLabel,    new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));               
        
        northPanel.add(openQty,     new GridBagConstraints(5, 3, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        /*order.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderActionPerformed(evt);
            }
        });*/
        

        
        QtyBatchsLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchs"));
        

        northPanel.add(QtyBatchsLabel,    new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(qtyBatchs,      new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        QtyBatchSizeLabel.setText(Msg.translate(Env.getCtx(), "QtyBatchSize"));
        
                northPanel.add(QtyBatchSizeLabel,      new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(qtyBatchSize,       new GridBagConstraints(5, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                
        
        
        northPanel.add(isDelivery,   new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
        northPanel.add(isBackflush,   new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
        
        
        
        
        
                                               
        toDeliverQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyToDeliver"));
        
        northPanel.add(toDeliverQtyLabel,     new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(toDeliverQty,      new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                
      

        scrapQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyScrap"));
        

        northPanel.add(scrapQtyLabel,    new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(scrapQty,      new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));

        rejectQtyLabel.setText(Msg.translate(Env.getCtx(), "QtyReject"));
        
                northPanel.add(rejectQtyLabel,      new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(rejectQty,       new GridBagConstraints(5, 6, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                
        
        movementDateLabel.setText(Msg.translate(Env.getCtx(), "MovementDate"));
        
        northPanel.add(movementDateLabel,     new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		northPanel.add(movementDate,       new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
                
                        
        locatorLabel.setText(Msg.translate(Env.getCtx(), "M_Locator_ID"));
        
        northPanel.add(locatorLabel,  new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(locator,  new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        

        attributeLabel.setText(Msg.translate(Env.getCtx(), "M_AttributeSetInstance_ID"));
        
        northPanel.add(attributeLabel,      new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
	northPanel.add(attribute,       new GridBagConstraints(5, 7, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
                      
        
        northPanel.add(backflushGroupLabel,   new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        
        northPanel.add(backflushGroup,   new GridBagConstraints(5, 5, 1, 1, 0.0, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        
        ReceiptIssueOrder.add(northPanel, java.awt.BorderLayout.NORTH);

        TabsReceiptsIssue.addTab("Receipts & Issue Order", ReceiptIssueOrder);

        TabsReceiptsIssue.addTab(Msg.translate(Env.getCtx(), "Generate"), Generate);

        add(TabsReceiptsIssue, java.awt.BorderLayout.CENTER);
        mainPanel.add(TabsReceiptsIssue, java.awt.BorderLayout.CENTER);

        add(mainPanel, java.awt.BorderLayout.NORTH);                
	}   //  jbInit    
        
        
        /**
	 *  Dynamic Init.
	 *  Table Layout, Visual, Listener
	 */
	private void dynInit()
	{
                disableToDeliver();
                issue.addColumn("MPC_OrderBOMLine_ID"); //0
                //issue.addColumn("Line");              //
                issue.addColumn("IsCritical");          //1
                issue.addColumn("Value");               //2
                issue.addColumn("M_Product_ID");        //3
                issue.addColumn("C_UOM_ID");            //4
                //issue.addColumn("BackflushGroup");     
                issue.addColumn("QtyRequiered");        //5
                issue.addColumn("QtyToDeliver");        //6
                 issue.addColumn("QtyOnHand");           //7                
                issue.addColumn("QtyScrap");            //8                
                issue.addColumn("QtyReserved");         //9
                issue.addColumn("QtyAvailable");        //10
               // issue.addColumn("QtyOnHand");           //10                
                issue.addColumn("M_Locator_ID");        //11        
                issue.addColumn("M_Warehouse_ID");      //12                                               
                issue.addColumn("QtyBom");              //13
                issue.addColumn("IsQtyPercentage");     //14
                issue.addColumn("QtyBatch");            //15
                
                issue.setMultiSelection(true);
		issue.setRowSelectionAllowed(true);
                                    
                //  set details
		issue.setColumnClass( 0, IDColumn.class   , false, " ");
                issue.setColumnClass( 1, Boolean.class    , true, Msg.translate(Env.getCtx(), "IsCritical"));
		//issue.setColumnClass( 1, Integer.class    , true, Msg.translate(Env.getCtx(), "Line"));
                issue.setColumnClass( 2, String.class    , true, Msg.translate(Env.getCtx(), "Value"));		
		issue.setColumnClass( 3, KeyNamePair.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));               
		issue.setColumnClass( 4, KeyNamePair.class, true, Msg.translate(Env.getCtx(), "C_UOM_ID"));
                //issue.setColumnClass( 5, String.class     , true, Msg.translate(Env.getCtx(), "BackflushGroup"));
                issue.setColumnClass( 5, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyRequiered"));
                issue.setColumnClass( 6, VNumber.class , false, Msg.translate(Env.getCtx(), "QtyToDeliver"));
                issue.setColumnClass( 7, VNumber.class , false, Msg.translate(Env.getCtx(), "QtyScrap"));
                issue.setColumnClass( 8, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyOnHand"));
                issue.setColumnClass( 9, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyReserved"));
                issue.setColumnClass( 10, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyAvailable"));  
               // issue.setColumnClass(10, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyOnHand"));
                issue.setColumnClass(11, BigDecimal.class , true, Msg.translate(Env.getCtx(), "M_Locator_ID"));
                issue.setColumnClass(12, KeyNamePair.class, true, Msg.translate(Env.getCtx(), "M_Warehouse_ID")); 
                issue.setColumnClass(13, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyBom"));
                issue.setColumnClass(14, Boolean.class    , true, Msg.translate(Env.getCtx(), "IsQtyPercentage"));
                issue.setColumnClass(15, BigDecimal.class , true, Msg.translate(Env.getCtx(), "QtyBatch"));
                
                //issue.setColumnClass(10, BigDecimal.class , true, Msg.translate(Env.getCtx(), "M_Locator_ID"));

                issue.autoSize();
                issue.getModel().addTableModelListener(this);
            

                
                m_sql = new StringBuffer("SELECT obl.MPC_Order_ID,obl.Line,obl.IsCritical,p.Name,obl.M_Product_ID,u.Name,obl.C_UOM_ID,obl.QtyBom,obl.IsQtyPercentage,obl.QtyBatch,obl.QtyRequiered,obl.M_Warehouse_ID , obl.QtyReserved,BOM_Qty_Available(obl.M_Product_ID,obl.M_Warehouse_ID) AS QtyAvailable , BOM_Qty_OnHand(p.M_Product_ID,obl.M_Warehouse_ID) AS QtyOnHand , obl.QtyReserved,obl.QtyScrap ");
                m_sql.append(" FROM MPC_Order_BOMLine obl ");
                m_sql.append(" INNER JOIN M_Product p ON (obl.M_Product_ID=p.M_Product_ID)");
                m_sql.append(" INNER JOIN C_UOM u ON (obl.C_UOM_ID=u.C_UOM_ID) ");              
                              
                CompiereColor.setBackground (this);
                issue.setCellSelectionEnabled(true);
                issue.getSelectionModel().addListSelectionListener(this);
                issue.setRowCount(0);
                /*TableColumn column = issue.getColumnModel().getColumn(0);
                column.setCellRenderer ( new CustomTableCellRenderer() );*/
                
		
	}   //  dynInit
        
        
        
    /**
	 *	Fill Picks
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	private void fillPicks() throws Exception
	{
            
                Properties ctx = Env.getCtx();    
                
                
                /*public static MLookup get (Properties ctx, int WindowNo, int Column_ID, int AD_Reference_ID,
		Language language, String ColumnName, int AD_Reference_Value_ID,
		boolean IsParent, String ValidationCode)*/
                
                Language language = Language.getLoginLanguage();//	Base Language
                MLookup orderL = MLookupFactory.get(ctx, m_WindowNo, 1000181, DisplayType.Search, language, "MPC_Order_ID" , 0 , false , "MPC_Order.DocStatus = '" + MMPCOrder.DOCSTATUS_Completed + "'");
                
                order = new VLookup ("MPC_Order_ID", false, false, true, orderL);
                order.setBackground(CompierePLAF.getInfoBackground());
                //order.addActionListener(this);
                order.addVetoableChangeListener(this);                
		
                
                MLookup issueMethodL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000313, DisplayType.List);
                issueMethod = new VLookup ("IssueMethod", false, false, true, issueMethodL);
                issueMethod.addVetoableChangeListener(this);
                issueMethod.addActionListener(this);
                
                MLookup resourceL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000513, DisplayType.TableDir);
                resource = new VLookup ("S_Resource_ID", false, false, false, resourceL);
                
                MLookup warehouseL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000408, DisplayType.TableDir);
                warehouse = new VLookup ("M_Warehouse_ID", false, false, false, warehouseL);
                //resource.addVetoableChangeListener(this);
                
                MLookup productL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000380, DisplayType.TableDir);
                product = new VLookup ("M_Product_ID", false, false, false, productL);
                
                MLookup uomL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000187, DisplayType.TableDir);
                uom = new VLookup ("C_UOM_ID", false, false, false, uomL);
                
                MLookup uomorderL = MLookupFactory.get (ctx, m_WindowNo, 0, 1000187, DisplayType.TableDir);
                uomorder = new VLookup ("C_UOM_ID", false, false, false, uomorderL);
                
                MLocatorLookup locatorL = new MLocatorLookup(ctx, m_WindowNo);
		locator = new VLocator ("M_Locator_ID", true, false, true, locatorL, m_WindowNo);
                
                
                
                MPAttributeLookup attributeL = new MPAttributeLookup(ctx,m_WindowNo);                
                attribute = new VPAttribute (false, false, true, m_WindowNo, attributeL);
                attribute.setValue(new Integer(0));
                                                                        //  Tab, Window 
                MFieldVO vo =  MFieldVO.createStdField(ctx , m_WindowNo , 1000031 , 1000013, false, false, false, false);
                // M_AttributeSetInstance_ID
                vo.AD_Column_ID = 1000183;                
                MField field = new MField(vo);               
                attribute.setField(field);
                
                Boolean ok = new Boolean(true);
                isDelivery.setValue(ok);
                isBackflush.setValue(ok);
                

                isDelivery.addVetoableChangeListener(this);
                isBackflush.addVetoableChangeListener(this);
                Process.addActionListener(this);      
                toDeliverQty.addActionListener(this);   
                scrapQty.addActionListener(this);
	}	//	fillPicks
        
        /**
	 *  Fill the table using m_sql
	 *  @param table table
	 */
	private void tableLoad (MiniTable table)
	{
		log.finest("tableLoad - " + m_sql + m_groupBy);
		String sql = MRole.getDefault().addAccessSQL(
			m_sql.toString(), "obl", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO)
			+ m_groupBy;
		log.finest( "tableLoad - " + sql);
		try
		{
			java.sql.Statement stmt = DB.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			table.loadTable(rs);
                        rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{			
                        log.log(Level.SEVERE, "VOrderReceiptIssue.tableFill " + sql, e);
		}
	}   //  tableLoad
        
        
        /**
	 *  Query Info
	 */
	private void executeQuery()
	{
		log.fine("VOrderReceiptIssue.executeQuery");
		int AD_Client_ID = Env.getAD_Client_ID(Env.getCtx());
                StringBuffer sql = new StringBuffer("SELECT obl.MPC_Order_BOMLine_ID , obl.IsCritical , p.Value , obl.M_Product_ID , p.Name , p.C_UOM_ID , u.Name ,");
                             sql.append(" obl.QtyRequiered  , obl.QtyReserved , bomQtyAvailable(obl.M_Product_ID,obl.M_Warehouse_ID, 0 ) AS QtyAvailable , bomQtyOnHand(obl.M_Product_ID,obl.M_Warehouse_ID,0) AS QtyOnHand  , p.M_Locator_ID , obl.M_Warehouse_ID , w.Name  , obl.QtyBom , obl.isQtyPercentage , obl.QtyBatch , obl.ComponentType , obl.QtyRequiered - QtyDelivered AS QtyOpen FROM MPC_Order_BOMLine obl");
                             sql.append(" INNER JOIN M_Product p ON (obl.M_Product_ID = p.M_Product_ID) ");
                             sql.append(" INNER JOIN C_UOM u ON (p.C_UOM_ID = u.C_UOM_ID) ");
                             sql.append(" INNER JOIN M_Warehouse w ON (w.M_Warehouse_ID = obl.M_Warehouse_ID) ");
                             sql.append(" WHERE obl.MPC_Order_ID = " + order.getValue());
                

		log.log(Level.SEVERE, "VOrderReciptIssue.executeQuery - SQL", sql.toString());                
		//  reset table
		int row = 0;
		issue.setRowCount(row);
		//  Execute
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			//pstmt.setInt(1, AD_Client_ID);
			ResultSet rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
				issue.setRowCount(row+1);
				//  set values
                                //issue.
                                IDColumn id = new IDColumn(rs.getInt(1));
                                id.setSelected(true);
                                
                                issue.setValueAt(id, row, 0);   //  MPC_OrderBOMLine_ID
                                //issue.setValueAt(new Integer(rs.getInt(2)), row, 1);//  Line
                                
                                                                
                                if (rs.getString(2).equals("Y"))                                
                                issue.setValueAt(new Boolean(true), row, 1);//  IsCritical
                                else
                                issue.setValueAt(new Boolean(false), row, 1);//  IsCritical
                                
                                issue.setValueAt(rs.getString(3), row, 2);
                                
                                KeyNamePair m_productkey = new KeyNamePair(rs.getInt(4) , rs.getString(5));
                                issue.setValueAt(m_productkey , row, 3);             //  Product
                                KeyNamePair m_uomkey = new KeyNamePair(rs.getInt(6),rs.getString(7));
                                issue.setValueAt(m_uomkey, row, 4);             //  UOM
                                //issue.setValueAt(, row , 5);
                                //issue.setValueAt(rs.getString(8), row, 5);          //  BackflushGroup
                                BigDecimal m_QtyBom = rs.getBigDecimal(15);
                                issue.setValueAt( m_QtyBom , row, 13);          //  QtyBom
                                Boolean m_QtyPercentage = null;
                                
                                if (rs.getString(16).equals("Y"))                                
                                m_QtyPercentage = new Boolean(true);
                                else
                                m_QtyPercentage = new Boolean(false);
                                                   
                                issue.setValueAt(m_QtyPercentage, row, 14); //  isQtyPercentage
                                BigDecimal m_QtyBatch = rs.getBigDecimal(17);
                                String m_ComponentType = rs.getString(18);
                                issue.setValueAt(m_QtyBatch, row, 15);          //  QtyBatch
                                //System.out.println("QtyBatch actual >>>>>>>>>>>>>>>>:" + m_QtyBatch);                               
                                //m_QtyBatch.divide();

                                //int m_mpc_order_id = ((Integer)order.getValue()).intValue();
                                boolean m_isDelivery= ((Boolean)isDelivery.getValue()).booleanValue();
                                boolean m_isBackflush = ((Boolean)isDelivery.getValue()).booleanValue();
                                
                                KeyNamePair m_warehousekey = new KeyNamePair(rs.getInt(13),rs.getString(14));
                                issue.setValueAt(m_warehousekey , row, 12);             //  Product
                             //   issue.setValueAt(rs.getBigDecimal(9), row, 8);          //  QtyReserved
                                issue.setValueAt(rs.getBigDecimal(9), row, 9);          //  QtyReserved
                                //  issue.setValueAt(rs.getBigDecimal(10) , row, 9); // OnHand
                                issue.setValueAt(rs.getBigDecimal(10) , row, 10); // OnHand
                              //  issue.setValueAt(rs.getBigDecimal(10) , row, 10); // OnHand
                                issue.setValueAt(rs.getBigDecimal(11) , row, 8); // Availale

                                // Put Qty Issue 
                                BigDecimal m_toDeliverQty = (BigDecimal)toDeliverQty.getValue();
                                BigDecimal m_openQty = (BigDecimal)openQty.getValue();
                                BigDecimal m_scrapQty = (BigDecimal)scrapQty.getValue();
                                BigDecimal m_rejectQty = (BigDecimal)rejectQty.getValue();
                                
                                if(m_scrapQty == null)
                                m_scrapQty = Env.ZERO;
                                
                                if(m_rejectQty == null)
                                m_rejectQty = Env.ZERO;
                                
                                //BigDecimal m_qtyrequiered = rs.getBigDecimal(8);   
                                System.out.println("m_ComponentType:"+ m_ComponentType);
                                
                                  if (m_ComponentType.equals(MMPCProductBOMLine.COMPONENTTYPE_Component) || m_ComponentType.equals(MMPCProductBOMLine.COMPONENTTYPE_Packing))
                                  {	
                                    if (m_QtyPercentage.booleanValue()) // Calculate Qty %
                                    {   
                                    	System.out.println("// Calculate Qty %");
                                    	VNumber viewToDeliverQty = new VNumber();
                                		viewToDeliverQty.setDisplayType(DisplayType.Number);
                                        
                                    	if (m_isDelivery && m_isBackflush) // Calculate Component for Receipt
                                    	{	
                                    		                                    		                                                            
                                    		viewToDeliverQty.setValue(m_toDeliverQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP)));
                               
                                    		//   viewtoDeliverQty.setDisplayType(1);
                                    		BigDecimal componentToDeliverQty = (BigDecimal)viewToDeliverQty.getValue();
                                    		System.out.println( " componentToDeliverQty" +componentToDeliverQty);
                                    		if (componentToDeliverQty.compareTo(Env.ZERO) != 0)
                                    		{
                                    			issue.setValueAt(m_toDeliverQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP)), row, 5); //  QtyRequiered
                                    			issue.setValueAt(componentToDeliverQty.divide(Env.ONE,4,BigDecimal.ROUND_HALF_UP), row, 6); //  QtyToDelivery
                                    			
                                    		}
                                    	}
                                    	else // Only Calculate Component not exist Receipt
                                    	{   
                                    		//viewToDeliverQty.setValue(m_openQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP)));                                    		
                                    		//   viewtoDeliverQty.setDisplayType(1);
                                    		//BigDecimal componentToDeliverQty = (BigDecimal)viewToDeliverQty.getValue();
                                    		BigDecimal componentToDeliverQty = rs.getBigDecimal(19);
                                    		if (componentToDeliverQty.compareTo(Env.ZERO) != 0)
                                    		{
                                    			issue.setValueAt(componentToDeliverQty.divide(Env.ONE,4,BigDecimal.ROUND_HALF_UP), row, 6); //  QtyToDelivery
                                    			issue.setValueAt(m_openQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP)), row, 5); //  QtyRequiered
                                    		}
                                    	}	
                                    	
                                
                                
                                        if(m_scrapQty.compareTo(Env.ZERO) != 0)
                                        {//issue.setValueAt(m_scrapQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4 ,BigDecimal.ROUND_HALF_UP)), row, 7); //  QtyScrap
                                        //    issue.setValueAt(m_scrapQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4 ,BigDecimal.ROUND_HALF_UP)), row, 8); //  QtyScrap
                                             //BigDecimal vnbd2= m_scrapQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP));			                             
                                             VNumber viewScrapQty = new VNumber();
                                             viewScrapQty.setDisplayType(DisplayType.Number);
                                             viewScrapQty.setValue(m_scrapQty.multiply(m_QtyBatch.divide(new BigDecimal(100.00) ,4,BigDecimal.ROUND_HALF_UP)));
                               
                                             //   vnm.setDisplayType(1);
                                             BigDecimal componentScrapQty = (BigDecimal)viewScrapQty.getValue();
                                             if (componentScrapQty.compareTo(Env.ZERO) != 0)
                                             {
                                             	issue.setValueAt(componentScrapQty.divide(Env.ONE,4,BigDecimal.ROUND_HALF_UP), row, 7); //  QtyToDelivery
                                             }
                                        }
                                    }
                                    else // Normal Calculate Qty 
                                    {	
                                    	System.out.println("Normal Calculate Qty ");
                                    	VNumber viewToDeliverQty = new VNumber();
                                		viewToDeliverQty.setDisplayType(DisplayType.Number);
                                    	
                                    	if (m_isDelivery && m_isBackflush) // Calculate Component for Receipt
                                    	{
                                    		viewToDeliverQty.setValue(m_toDeliverQty.multiply(m_QtyBom));
                                    		
                                    		//   viewtoDeliverQty.setDisplayType(1);
                                    		BigDecimal componentToDeliverQty = (BigDecimal)viewToDeliverQty.getValue();
                                    		System.out.println( " componentToDeliverQty" +componentToDeliverQty);
                                    		if (componentToDeliverQty.compareTo(Env.ZERO) != 0)
                                    		{                                        	
                                    			issue.setValueAt(m_toDeliverQty.multiply(m_QtyBom), row, 5);  //  QtyRequiered
                                    			issue.setValueAt(componentToDeliverQty, row, 6); //  QtyToDelivery
                                    		}
                                    	} // if (m_isDelivery)
                                    	else
                                    	{
                                    		//viewToDeliverQty.setValue(m_openQty.multiply(m_QtyBom));
                                            //   viewtoDeliverQty.setDisplayType(1);
                                            BigDecimal componentToDeliverQty = rs.getBigDecimal(19);
                                            if (componentToDeliverQty.compareTo(Env.ZERO) != 0)
                                            {                                        	
                                                 issue.setValueAt(m_openQty.multiply(m_QtyBom), row, 5);  //  QtyRequiered
                                                 issue.setValueAt(componentToDeliverQty, row, 6); //  QtyToDelivery
                                            }
                                    	}
                                        
                                                                                
                                    		if(m_scrapQty.compareTo(Env.ZERO) != 0)
                                    		{
                                        	
                                    			VNumber viewScrapQty = new VNumber();
                                    			viewScrapQty.setDisplayType(DisplayType.Number);
                                    			viewScrapQty.setValue(m_scrapQty.multiply(m_QtyBom));
                               
                                    			//    viewScrapQty.setDisplayType(1);
                                    			BigDecimal componentScrapQty = (BigDecimal)viewScrapQty.getValue();
                                    			if (componentScrapQty.compareTo(Env.ZERO) != 0)
                                    			{
                                    				issue.setValueAt(componentScrapQty, row, 7); //  QtyToDelivery
                                    			}                                                   
                                    		}
                                    	
                                      } 
                                  } 
                                  else if (m_ComponentType.equals(MMPCProductBOMLine.COMPONENTTYPE_Tools))
								  {
                                  	
                                  	
                                  	 VNumber viewToDeliverQty = new VNumber();
                                     viewToDeliverQty.setDisplayType(DisplayType.Number);                                     
                                     viewToDeliverQty.setValue(m_QtyBom);
                            
                                     //   viewtoDeliverQty.setDisplayType(1);
                                     BigDecimal componentToDeliverQty = (BigDecimal)viewToDeliverQty.getValue();
                                     if (componentToDeliverQty.compareTo(Env.ZERO) != 0)
                                     {                                        	
                                     	issue.setValueAt(m_QtyBom, row, 5);  //  QtyRequiered
                                     	issue.setValueAt(componentToDeliverQty, row, 6); //  QtyToDelivery
                                     }                                   
								  }
          
                             
				//  prepare next
				row++;
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE,"VOrderReceipIssue.executeQuery", e);                     
		}
		//
		issue.autoSize();
	//	statusBar.setStatusDB(String.valueOf(miniTable.getRowCount()));
	}   //  executeQuery
        
         
        
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    /*
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        TabsReceiptsIssue = new javax.swing.JTabbedPane();
        ReceiptIssueOrder = new javax.swing.JPanel();
        PanelCenter = new javax.swing.JPanel();
        issueScrollPane = new javax.swing.JScrollPane();
        issue = new javax.swing.JTable();
        PanelBottom = new javax.swing.JPanel();
        Process = new javax.swing.JButton();
        northPanel = new javax.swing.JPanel();
        orgLabel = new javax.swing.JLabel();
        org = new javax.swing.JTextField();
        resourceLabel = new javax.swing.JLabel();
        resource = new javax.swing.JTextField();
        orderLabel = new javax.swing.JLabel();
        order = new javax.swing.JTextField();
        movementDateLabel = new javax.swing.JLabel();
        movementDate = new javax.swing.JTextField();
        deliveryQtyLabel = new javax.swing.JLabel();
        deliveryQty = new javax.swing.JTextField();
        scrapQtyLabel = new javax.swing.JLabel();
        scrapQty = new javax.swing.JTextField();
        rejectQtyLabel = new javax.swing.JLabel();
        rejectQty = new javax.swing.JTextField();
        attributeLabel = new javax.swing.JLabel();
        attribute = new javax.swing.JTextField();
        issueMethodLabel = new javax.swing.JLabel();
        issueMethod = new javax.swing.JTextField();
        Generate = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        mainPanel.setLayout(new java.awt.BorderLayout());

        ReceiptIssueOrder.setLayout(new java.awt.BorderLayout());

        PanelCenter.setLayout(new java.awt.BorderLayout());

        issueScrollPane.setBorder(new javax.swing.border.TitledBorder(""));
        issue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        issueScrollPane.setViewportView(issue);

        PanelCenter.add(issueScrollPane, java.awt.BorderLayout.NORTH);

        ReceiptIssueOrder.add(PanelCenter, java.awt.BorderLayout.CENTER);

        Process.setText("Process");
        Process.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProcessActionPerformed(evt);
            }
        });

        PanelBottom.add(Process);

        ReceiptIssueOrder.add(PanelBottom, java.awt.BorderLayout.SOUTH);

        northPanel.setLayout(new java.awt.GridBagLayout());

        orgLabel.setText("Org");
        orgLabel.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 5, 0);
        northPanel.add(orgLabel, gridBagConstraints);

        org.setText("                                 ");
        org.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orgActionPerformed(evt);
            }
        });

        northPanel.add(org, new java.awt.GridBagConstraints());

        resourceLabel.setText("Plant");
        northPanel.add(resourceLabel, new java.awt.GridBagConstraints());

        resource.setText("                         ");
        northPanel.add(resource, new java.awt.GridBagConstraints());

        orderLabel.setText("Order");
        northPanel.add(orderLabel, new java.awt.GridBagConstraints());

        order.setText("                       ");
        order.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderActionPerformed(evt);
            }
        });

        northPanel.add(order, new java.awt.GridBagConstraints());

        movementDateLabel.setText("Movement Date");
        northPanel.add(movementDateLabel, new java.awt.GridBagConstraints());

        movementDate.setText("jTextField4");
        northPanel.add(movementDate, new java.awt.GridBagConstraints());

        deliveryQtyLabel.setText("Delivery Qty");
        northPanel.add(deliveryQtyLabel, new java.awt.GridBagConstraints());

        deliveryQty.setText("jTextField5");
        northPanel.add(deliveryQty, new java.awt.GridBagConstraints());

        scrapQtyLabel.setText("Scrap Qty");
        northPanel.add(scrapQtyLabel, new java.awt.GridBagConstraints());

        scrapQty.setText("jTextField6");
        northPanel.add(scrapQty, new java.awt.GridBagConstraints());

        rejectQtyLabel.setText("Reject");
        northPanel.add(rejectQtyLabel, new java.awt.GridBagConstraints());

        rejectQty.setText("jTextField7");
        northPanel.add(rejectQty, new java.awt.GridBagConstraints());

        attributeLabel.setText("Instance");
        northPanel.add(attributeLabel, new java.awt.GridBagConstraints());

        attribute.setText("jTextField8");
        northPanel.add(attribute, new java.awt.GridBagConstraints());

        issueMethodLabel.setText("Issue Method");
        northPanel.add(issueMethodLabel, new java.awt.GridBagConstraints());

        issueMethod.setText("jTextField9");
        northPanel.add(issueMethod, new java.awt.GridBagConstraints());

        ReceiptIssueOrder.add(northPanel, java.awt.BorderLayout.NORTH);

        TabsReceiptsIssue.addTab("Receipts & Issue Order", ReceiptIssueOrder);

        TabsReceiptsIssue.addTab("Generate", Generate);

        mainPanel.add(TabsReceiptsIssue, java.awt.BorderLayout.CENTER);

        add(mainPanel, java.awt.BorderLayout.NORTH);

    }//GEN-END:initComponents
*/
        /*
    private void orderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderActionPerformed
          // TODO add your handling code here:
    }//GEN-LAST:event_orderActionPerformed


    private void ProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProcessActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ProcessActionPerformed

    
    private void orgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_orgActionPerformed
*/
    public void actionPerformed(ActionEvent e) 
    {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean m_isBackflush = ((Boolean)isDelivery.getValue()).booleanValue();
			
        log.fine("VOrderReceiptIssue.actionPerformed Evet:" + e.getSource());
                //	Product Attribute Search
		if (e.getSource().equals(Process))
		{
                   
                    if (cmd_process())
                    {
                    dispose();
                    }
		}
                

                if (e.getSource().equals(issueMethod))                    
                {
                    if (issueMethod.getValue() != null )
                    {			
                        System.out.println("issueMethod.getValue()" + issueMethod.getValue());
			            cmd_search();
                    }
                }
                
                	//cmd_search();	
                if (e.getSource() == issueMethod)                    
                {
                    if (issueMethod.getValue() != null )
                    {			
                        System.out.println("issueMethod.getValue()" + issueMethod.getValue());
			            cmd_search();
                    }
                }
                
                 	
                if (e.getSource() == toDeliverQty || e.getSource() == scrapQty)                    
                {
                    
                    if (order.getValue() != null)
                    {
                        
                        /*if (m_isBackflush)
                        {*/
                        issue.removeAll();
                        issue.setVisible(true);
                        executeQuery();                
                        /*}
                        else*/
                            
                    
                    }
                   
                }

                
		//setCursor(Cursor.getDefaultCursor());
    }    
    
    public void dispose() {
        
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
    }
    
    public void executeASync(org.openXpertya.process.ProcessInfo processInfo) {
    }
    
    public boolean isUILocked() {
        return true;
    }
    
    public void lockUI(org.openXpertya.process.ProcessInfo processInfo) {
    }
    
    public void stateChanged(ChangeEvent e) {
    }

    public void tableChanged(TableModelEvent e) 
    {
      /*  
       int row = issue.getSelectedRow();
       int col = e.getColumn();
       
       //System.out.print("Prueba col:" + e.getColumn() +  " row:" +  issue.getSelectedRow());
       //System.out.print("Prueba jejejejjejejejejejjejejejeje" + e.getType());
       System.out.println("Evento" +  e.getSource() + e.toString());
       //TableModelEvent.INSERT
       
       if (e.getType() == TableModelEvent.INSERT && row != -1 && col == 6)
       {
           VNumber n = new VNumber();
           //issue.setValueAt(n, row , 6 );
       }*/
    }
    
    public void unlockUI(org.openXpertya.process.ProcessInfo processInfo) {
    }
    
    public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException 
    {
                
        boolean m_isBackflush = ((Boolean)isDelivery.getValue()).booleanValue();
        
        String name = e.getPropertyName();
		Object value = e.getNewValue();
		log.fine( "VOrderReceip.vetoableChange - " + name + "=" + value);
		if (value == null)
			return;

		//  MPC_Order_ID
		if (name.equals("MPC_Order_ID"))
		{                    
                    order.setValue(value);
                    
                    if (order.getValue() != null)
                    {
                    	Integer MPC_Order_ID = (Integer)order.getValue();
                        m_mpc_order = new MMPCOrder(Env.getCtx(),MPC_Order_ID.intValue(),null);
                        resource.setValue(new Integer(m_mpc_order.getS_Resource_ID()));	//	display value
                        warehouse.setValue(new Integer(m_mpc_order.getM_Warehouse_ID()));	//	display value
                        deliveredQty.setValue(m_mpc_order.getQtyDelivered());
                        orderedQty.setValue(m_mpc_order.getQtyOrdered());
                        m_mpc_order.getQtyOrdered().subtract(m_mpc_order.getQtyDelivered());
                        qtyBatchs.setValue(m_mpc_order.getQtyBatchs());
                        qtyBatchSize.setValue(m_mpc_order.getQtyBatchSize());
                        //openQty.setValue(m_mpc_order.getQtyDelivered().subtract(m_mpc_order.getQtyOrdered()));
                        openQty.setValue(m_mpc_order.getQtyOrdered().subtract(m_mpc_order.getQtyDelivered()));
                        toDeliverQty.setValue(openQty.getValue());
                        product.setValue(new Integer(m_mpc_order.getM_Product_ID()));  
                        MProduct m_product =  new MProduct(Env.getCtx(),m_mpc_order.getM_Product_ID(),null);
                        uom.setValue(new Integer(m_product.getC_UOM_ID()));
                        uomorder.setValue(new Integer(m_mpc_order.getC_UOM_ID()));                        
                        Integer m_product_id = (Integer)product.getValue();
                        Env.setContext(Env.getCtx(), m_WindowNo ,"M_Product_ID",m_product_id.intValue());
                        attribute.setValue(new Integer(m_mpc_order.getM_AttributeSetInstance_ID())); 
                        enableToDeliver();
                        
                        if (m_isBackflush)
                        {
                        issue.removeAll();    
                        issue.setVisible(true);
                        executeQuery();                        
                        }

                    }                                       
		}		//  MPC_Order_ID
                
                
                
                 // yes show the field for to make receive
                if (name.equals("IsDelivery"))
                {                       
                    issue.setVisible(true);
                        boolean m_isDelivery = ((Boolean)value).booleanValue();
                        if (m_isDelivery)
                        {
                        enableToDeliver();
//                            toDeliverQty.setReadWrite(true);
//                        movementDate.setReadWrite(true);
//                        locator.setReadWrite(true);
                            if(order.getValue() != null)
                            {    
                             executeQuery();
                            }
                        }
                        else
                        {
                        //isBackflush.setValue(new Boolean(false));    
                        disableToDeliver();
                        //issue.removeAll();
                        //issue.setVisible(true);
                        //toDeliverQty.setValue(Env.ZERO);
                       if(order.getValue() != null)
                            {    
                             executeQuery();
                            }
                        }
                 }
                
                if (name.equals("toDeliverQty"))
                {
                    if(order.getValue() != null)
                    {    
                             executeQuery();
                    }
                    
                }
                  //  MPC_Order_ID
                if (name.equals("IsBackflush"))
                {                          
                	if (m_isBackflush)
                    {
                    //backflushGroupLabel.setVisible(true);
                    //backflushGroup.setVisible(true);  	
                    issue.removeAll();    
                    issue.setVisible(true);
                    executeQuery();                        
                    }
                    else
                    {
                        //backflushGroupLabel.setVisible(false);    
                        //backflushGroup.setVisible(false);
                        issue.setVisible(false);
                    }
                 }
//                 else
//                 {
//                   issue.setVisible(false);
//                 }
                
		
            
		
                
    }
    
    public void valueChanged(ListSelectionEvent e) {
    }
    
    /**
    *  Search Button Pressed - Fill xMatched
    */
    private void cmd_search()
    {
                //  ** Add Where Clause **
		//  MPC_Order_ID
                if (order.getValue() != null)
                {
			m_sql.append(" WHERE obl.MPC_Order_ID=").append(order.getValue());
                        tableLoad(issue);
                }
                        
    }
    
     /**
    *  Search Button Pressed - Fill xMatched
    */
    private boolean cmd_process()
    {
    		   boolean m_isDelivery = ((Boolean)isDelivery.getValue()).booleanValue();
    		   boolean m_isBackflush = ((Boolean)isBackflush.getValue()).booleanValue();
    	
    		   //if (movementDate.getValue() == null)
               //         JOptionPane.showMessageDialog(null,Msg.getMsg(Env.getCtx(),"NoDate"), "Info" , JOptionPane.INFORMATION_MESSAGE);
    		   if (m_isDelivery)
    		   {	
    		   		if (locator.getValue() == null)
                        JOptionPane.showMessageDialog(null,Msg.getMsg(Env.getCtx(),"NoLocator"), "Info" , JOptionPane.INFORMATION_MESSAGE);
    		   }
               
                                                   
                //if (m_mpc_order != null && movementDate.getValue() != null && locator.getValue()!=null && m_isDelivery)
    		   if (m_mpc_order != null && movementDate.getValue() != null )
                {
                     //int m_mpc_order_id = ((Integer)order.getValue()).intValue();
                    
                     
                     
                     Timestamp m_movementDate = (Timestamp)movementDate.getValue();
                     BigDecimal m_toDeliverQty = (BigDecimal)toDeliverQty.getValue();
                     int m_M_Location_ID = 0;
                     int m_M_Location_IDissue = 0;


                     // Do if (m_isDelivery = NO) OR (m_isBackflush  = YES AND m_isDelivery = YES )
                     //if (!m_isDelivery || (m_isBackflush && m_isDelivery) )
                     //{  
                       
                        int m_M_Product_ID = 0;
                        Timestamp minGuaranteeDate = m_movementDate;                         
                        BigDecimal m_qtyToDeliver = Env.ZERO;
                        BigDecimal m_deliver = Env.ZERO;
                        //BigDecimal m_scrapQty = Env.ZERO;
                        //BigDecimal m_rejectQty = Env.ZERO;
                        BigDecimal m_scrapQtyComponet = Env.ZERO;
                        
                        boolean iscompleteqtydeliver = false;                        
                        
                        int m_C_UOM_ID = 0;
                        
                        // cheak available onhand 
                        for (int i = 0 ; i < issue.getRowCount(); i++)
                        {
                             IDColumn id = (IDColumn) issue.getValueAt(i, 0);
                             if (id != null && id.isSelected())
                             {
                                                                  
                                 KeyNamePair m_productkey = (KeyNamePair)issue.getValueAt(i,3);
                                 KeyNamePair m_uomkey = (KeyNamePair)issue.getValueAt(i,4);                                                           
                                 
                                 m_M_Product_ID = m_productkey.getKey();
                                 
                                 System.out.println("Product" + m_M_Product_ID + "Almacen" + m_mpc_order.getM_Warehouse_ID());
                                                                 
                                 m_C_UOM_ID = m_uomkey.getKey();
                                 String vacio ="";
                                  if (issue.getValueAt(i,6)!=null && !issue.getValueAt(i,6).toString().equals(vacio))
                                    {
                                         String mn = issue.getValueAt(i,6).toString();
                                         Integer mni= new Integer(0);
                                         BigDecimal mnb = new BigDecimal(issue.getValueAt(i,6).toString());
                                         m_qtyToDeliver = mnb; //(BigDecimal)issue.getValueAt(i,6);
                                    }
                                 if (issue.getValueAt(i,7)!=null && !issue.getValueAt(i,7).toString().equals(vacio))
                                    {
                                         String mns = issue.getValueAt(i,7).toString();
                                         Integer mnis= new Integer(0);
                                         BigDecimal mnbs = new BigDecimal(issue.getValueAt(i,7).toString());
                                        // m_qtyToDeliver = mnbs; //(BigDecimal)issue.getValueAt(i,6);
                                         m_scrapQtyComponet =  mnbs;//(BigDecimal)issue.getValueAt(i,8);
                                    }
                                 if(!m_isBackflush)
                                 {
                                     m_qtyToDeliver=Env.ZERO;
                                     m_scrapQtyComponet=Env.ZERO;
                                 }
                                     
                                 if (m_qtyToDeliver == null)
                                 m_qtyToDeliver = Env.ZERO; 
                                 
                                 if(m_scrapQtyComponet == null)
                                 m_scrapQtyComponet = Env.ZERO;   
                                 
                                Properties ctx = Env.getCtx();  
                                MStorage[] storages = null;
                     			MProduct product = new MProduct(ctx,m_M_Product_ID,null); 
                     			if (product != null && product.getID() != 0 && product.isStocked())
                     			{
                     				MProductCategory pc = MProductCategory.get(ctx, product.getM_Product_Category_ID(), null);
                     				String MMPolicy = pc.getMMPolicy();
                     				if (MMPolicy == null || MMPolicy.length() == 0)
                     				{
                     					MClient client = MClient.get(ctx);
                     					MMPolicy = client.getMMPolicy();
                     				}
                     				storages = MStorage.getWarehouse(Env.getCtx(), m_mpc_order.getM_Warehouse_ID(), m_M_Product_ID, m_mpc_order.getM_AttributeSetInstance_ID(),product.getM_AttributeSet_ID(), true, minGuaranteeDate,MClient.MMPOLICY_FiFo.equals(MMPolicy) ,null);
                     			}
     
                                
                         

                                 
                                // System.out.println("Ubicaciones :" + storages.length);
                                 
                     			BigDecimal onHand = Env.ZERO;
                     			for (int j = 0; j < storages.length; j++)
                     			{
                     				MStorage storage = storages[j];
                     				onHand = onHand.add(storage.getQtyOnHand());                                        
                     			}                                                                  
                                 
                                 iscompleteqtydeliver = onHand.compareTo(m_qtyToDeliver.add(m_scrapQtyComponet)) >= 0;
                                            
//                                 if(!m_isBackflush)
//                                 {
//                                    iscompleteqtydeliver =true;
//                                 }
                                     
                                 if(!iscompleteqtydeliver)
                                 break;    
                                                                 
                             }
                         }
                        
                        if (!iscompleteqtydeliver)
                        {
                            ADialog.error(m_WindowNo,this,"NoQtyAvailable");
                            return false;
                        }
                                                
                        
                        //Do Issue if ( m_isBackflush =  YES AND iscompleteqtydeliver = YES)
                        if (m_isBackflush  && iscompleteqtydeliver)  
                        {
                           
           
                          for (int ok = 0 ; ok < issue.getRowCount(); ok++)
                          {
                             IDColumn id = (IDColumn) issue.getValueAt(ok, 0);
                             if (id != null && id.isSelected())
                             {
                                                                  
                                 KeyNamePair m_productkey = (KeyNamePair)issue.getValueAt(ok,3);
                                 KeyNamePair m_uomkey = (KeyNamePair)issue.getValueAt(ok,4);
                                 
                                 m_M_Product_ID = m_productkey.getKey();
                                 m_C_UOM_ID = m_uomkey.getKey();
                                 m_qtyToDeliver = Env.ZERO;
                                 m_scrapQtyComponet = Env.ZERO;
                                 String vacio ="";
                                  if (issue.getValueAt(ok,6)!=null && !issue.getValueAt(ok,6).toString().equals(vacio))
                                    {
                                         String mn1 = issue.getValueAt(ok,6).toString();
                                         Integer mni1= new Integer(0);
                                         BigDecimal mnb1 = new BigDecimal(issue.getValueAt(ok,6).toString());
                                         m_qtyToDeliver = mnb1; //(BigDecimal)issue.getValueAt(i,6);
                                        // m_qtyToDeliver = (BigDecimal)issue.getValueAt(ok,6);
                                    }
                                 if (issue.getValueAt(ok,7)!=null && !issue.getValueAt(ok,7).toString().equals(vacio))
                                    {
                                         String mns1 = issue.getValueAt(ok,7).toString();
                                         Integer mnis1= new Integer(0);
                                         BigDecimal mnbs1 = new BigDecimal(issue.getValueAt(ok,7).toString());
                                        // m_qtyToDeliver = mnbs; //(BigDecimal)issue.getValueAt(i,6);
                                         m_scrapQtyComponet =  mnbs1;//(BigDecimal)issue.getValueAt(i,8);
                                    }
                                 //m_scrapQtyComponet =  (BigDecimal)issue.getValueAt(ok,8);
                                 if (m_qtyToDeliver == null)
                                 m_qtyToDeliver = Env.ZERO; 
                                 
                                 if(m_scrapQtyComponet == null)
                                 m_scrapQtyComponet = Env.ZERO;  

                                 BigDecimal onHand = Env.ZERO;
                                 
                                 Properties ctx = Env.getCtx();  
                                 MStorage[] storages = null;
                      			 MProduct product = new MProduct(ctx,m_M_Product_ID,null); 
                      			 if (product != null && product.getID() != 0 && product.isStocked())
                      			 {
                      				MProductCategory pc = MProductCategory.get(ctx, product.getM_Product_Category_ID(), null);
                      				String MMPolicy = pc.getMMPolicy();
                      				if (MMPolicy == null || MMPolicy.length() == 0)
                      				{
                      					MClient client = MClient.get(ctx);
                      					MMPolicy = client.getMMPolicy();
                      				}                      				
                      				storages = MStorage.getWarehouse(Env.getCtx(), m_mpc_order.getM_Warehouse_ID(), m_M_Product_ID, m_mpc_order.getM_AttributeSetInstance_ID(), product.getM_AttributeSet_ID(), true , minGuaranteeDate,MClient.MMPOLICY_FiFo.equals(MMPolicy),null);
                      			}
                      			
                                  
                                 for (int j = 0; j < storages.length; j++)
                                 {
                                 	MStorage storage = storages[j];
                                 	onHand = onHand.add(storage.getQtyOnHand());                                                                                             
                                 }
                                 
                                 //System.out.println("Surtir" + m_issueQty + "Existencia " + onHand);
                                 
                                 int MPC_Order_BOMLine_ID = ((Integer)id.getRecord_ID()).intValue();
                                 //java.util.Date today =new java.util.Date();
                                 //java.sql.Timestamp now =new java.sql.Timestamp(today.getTime());                             
                                 createIssue( MPC_Order_BOMLine_ID  , m_movementDate , m_qtyToDeliver , m_scrapQtyComponet , storages);                                  
                                 
                             }
                         }
                          
                     }
                     
                     // Do Delivery if (m_isDelivery = YES AND m_isBackflush = NO) OR (m_isBackflush = YES  AND m_isDelivery = YES AND iscompleteqtydeliver)
                     if ( (m_isDelivery && !m_isBackflush) || (m_isBackflush && m_isDelivery && iscompleteqtydeliver) )                       
                     {  
                        m_M_Location_ID = ((Integer)locator.getValue()).intValue();                        
                        Integer m_M_AttributeSetInstance_ID = (Integer)attribute.getValue();
                        
                        BigDecimal m_scrapQty = (BigDecimal)scrapQty.getValue();
                        BigDecimal m_rejectQty = (BigDecimal)rejectQty.getValue();
                        
                        if(m_scrapQty == null)
                        m_scrapQty = Env.ZERO;

                        if(m_rejectQty == null)
                        m_rejectQty = Env.ZERO;
                        
                        /*m_mpc_order.setQtyDelivered(m_mpc_order.getQtyDelivered().add(m_toDeliverQty));
                        m_mpc_order.setQtyScrap(m_mpc_order.getQtyScrap().add(m_scrapQty));*/
                        m_mpc_order.setQtyReject(m_mpc_order.getQtyReject().add(m_rejectQty));
                        m_mpc_order.save();
                        
                        int C_DocType_ID = getDocType(MDocType.DOCBASETYPE_ManufacturingOrderReceipt);
                        createCollector (m_mpc_order.getM_Product_ID(), m_M_Location_ID , m_M_AttributeSetInstance_ID.intValue() , m_movementDate , m_toDeliverQty , Env.ZERO , C_DocType_ID , 0, MMPCCostCollector.MOVEMENTTYPE_ProductionPlus);                          

                        
                        if (ADialog.ask(m_WindowNo,this,Msg.translate(Env.getCtx(), "IsCloseDocument") , Msg.translate(Env.getCtx(), "DocumentNo") + m_mpc_order.getDocumentNo()))
                        {
                            m_mpc_order.closeIt();
                            m_mpc_order.save();
                        }
                       
                        
                        ADialog.info(m_WindowNo,this, Msg.translate(Env.getCtx(), "IsDelivery") ,Msg.translate(Env.getCtx(), "DocumentNo") + m_mpc_order.getDocumentNo());
                        return true;
                     }
                     else
                     {
                     	 ADialog.info(m_WindowNo,this, Msg.translate(Env.getCtx(), "IsDelivery") ,Msg.translate(Env.getCtx(), "DocumentNo") + m_mpc_order.getDocumentNo());
                     	return true;
                     }
                    

              } 
              return false;
    }
    
        /**************************************************************************
	 * 	Create Line
	 *	@param order order
	 *	@param orderLine line
	 *	@param qty qty
	 */
	private void createIssue (int MPC_OrderBOMLine_ID , Timestamp movementdate, BigDecimal qty ,  BigDecimal qtyscrap , MStorage[] storages)                
	{
            
                //DB.executeUpdate("UPDATE MPC_Order_BOMLine SET QtyScrap = " + qtyscrap + " WHERE MPC_Order_BOMLine_ID=" + MPC_OrderBOMLine_ID);
                
		if (qty.compareTo(Env.ZERO) == 0)
			return;

                //	Inventory Lines		
                BigDecimal toIssue = qty.add(qtyscrap);
		for (int i = 0; i < storages.length; i++)
		{
			MStorage storage = storages[i];
			//	TODO Selection of ASI
			
			BigDecimal issue = toIssue;
			if (issue.compareTo(storage.getQtyOnHand()) > 0)
				issue = storage.getQtyOnHand();
	
                        
                        log.fine("createCollector - ToIssue" + issue); 
                        MMPCOrderBOMLine mpc_orderbomLine = new MMPCOrderBOMLine(Env.getCtx() , MPC_OrderBOMLine_ID,null);
                        //mpc_orderbomLine.setQtyDelivered(mpc_orderbomLine.getQtyDelivered().add(qty));
                        //mpc_orderbomLine.setQtyScrap(qtyscrap);
                        //if (!mpc_orderbomLine.save())
                        //    throw new IllegalStateException("Could not update BOM Line");
                                
                        
                        if ( mpc_orderbomLine.getQtyBatch().compareTo(Env.ZERO) == 0 && mpc_orderbomLine.getQtyBOM().compareTo(Env.ZERO) == 0 )
                        {    
                               // Method Variance
                                int C_DocType_ID = getDocType(MDocType.DOCBASETYPE_ManufacturingOrderMethodVariation);                
                                createCollector (mpc_orderbomLine.getM_Product_ID(),storage.getM_Locator_ID(),storage.getM_AttributeSetInstance_ID(), movementdate , issue , qtyscrap  , C_DocType_ID , MPC_OrderBOMLine_ID,MMPCCostCollector.MOVEMENTTYPE_Production_);
                             
                        }
                        else
                        {
                            
                                int C_DocType_ID = getDocType(MDocType.DOCBASETYPE_ManufacturingOrderIssue);                      
                                createCollector (mpc_orderbomLine.getM_Product_ID(),storage.getM_Locator_ID(),storage.getM_AttributeSetInstance_ID(), movementdate , issue, qtyscrap , C_DocType_ID , MPC_OrderBOMLine_ID, MMPCCostCollector.MOVEMENTTYPE_Production_);
                        }
                        
                        /*if (qtyscrap.compareTo(Env.ZERO) != 0)
                        {    
                            int C_DocType_ID = getDocType(MDocType.DOCBASETYPE_ManufacturingOrderUseVariation); // Use Variation
                            createCollector (mpc_orderbomLine.getM_Product_ID(),storage.getM_Locator_ID(),storage.getM_AttributeSetInstance_ID(), movementdate , qtyscrap.negate(), C_DocType_ID, MPC_OrderBOMLine_ID);
                        }*/
                        
                        toIssue = toIssue.subtract(issue);
			if (toIssue.compareTo(Env.ZERO) == 0)
				break;
                        //if (toIssue.compareTo(Env.ZERO) != 0)
			//throw new IllegalStateException("Not All Issued - Remainder=" + toIssue);   
		}	
                                
                
     }
        
    private void createCollector (int M_Product_ID ,int M_Locator_ID , int M_AttributeSetInstance_ID, Timestamp movementdate , BigDecimal qty , BigDecimal scrap, int C_DocType_ID, int MPC_Order_BOMLine_ID, String MovementType)
    {
                        MMPCCostCollector MPC_Cost_Collector = new MMPCCostCollector(Env.getCtx(), 0,null);                    
                        MPC_Cost_Collector.setMPC_Order_ID(m_mpc_order.getMPC_Order_ID());
                        MPC_Cost_Collector.setMPC_Order_BOMLine_ID(MPC_Order_BOMLine_ID);
                        MPC_Cost_Collector.setAD_OrgTrx_ID(m_mpc_order.getAD_OrgTrx_ID());
                        MPC_Cost_Collector.setC_Activity_ID(m_mpc_order.getC_Activity_ID());
                        MPC_Cost_Collector.setC_Campaign_ID(m_mpc_order.getC_Campaign_ID());
                        MPC_Cost_Collector.setC_DocType_ID(C_DocType_ID);
                        MPC_Cost_Collector.setC_DocTypeTarget_ID(C_DocType_ID);
                        MPC_Cost_Collector.setMovementType(MovementType);
                        MPC_Cost_Collector.setC_Project_ID(m_mpc_order.getC_Project_ID());
                        MPC_Cost_Collector.setDescription(m_mpc_order.getDescription());
                        MPC_Cost_Collector.setDocAction(MPC_Cost_Collector.DOCACTION_Complete);
                        MPC_Cost_Collector.setDocStatus(MPC_Cost_Collector.DOCSTATUS_Closed);
                        MPC_Cost_Collector.setIsActive(true);
                        MPC_Cost_Collector.setM_Warehouse_ID(m_mpc_order.getM_Warehouse_ID());
                        MPC_Cost_Collector.setM_Locator_ID(M_Locator_ID);
                        MPC_Cost_Collector.setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);                        
                        MPC_Cost_Collector.setS_Resource_ID(m_mpc_order.getS_Resource_ID());
                        MPC_Cost_Collector.setMovementDate(movementdate);
                        MPC_Cost_Collector.setDateAcct(movementdate);                        
                        MPC_Cost_Collector.setMovementQty(qty);
                        MPC_Cost_Collector.setScrappedQty(scrap);
                        MPC_Cost_Collector.setPosted(false);
                        MPC_Cost_Collector.setProcessed(false);
                        MPC_Cost_Collector.setProcessing(false);
                        MPC_Cost_Collector.setUser1_ID(m_mpc_order.getUser1_ID());
                        MPC_Cost_Collector.setUser2_ID(m_mpc_order.getUser1_ID());
                        MPC_Cost_Collector.setM_Locator_ID(M_Locator_ID);
                        MPC_Cost_Collector.setM_Product_ID(M_Product_ID);                        
                        if (!MPC_Cost_Collector.save())
                        {    
                        	throw new IllegalStateException("Could not create Collector");
                        }                                
                        MPC_Cost_Collector.completeIt();
                        //MPC_Cost_Collector.save();
    }
    
    private int getDocType(String DocBaseType)
    {
            MDocType[] Doc = MDocType.getOfDocBaseType(Env.getCtx(), DocBaseType);
            int C_DocType_ID = 0;
            
            if(Doc!=null)
            C_DocType_ID = Doc[0].getC_DocType_ID();
            
            return C_DocType_ID;
    }
    
    public void enableToDeliver()
    {
     issue.removeAll();
     issue.setVisible(false);
     toDeliverQtyLabel.setVisible(true);
     toDeliverQty.setVisible(true);
     scrapQtyLabel.setVisible(true);
     scrapQty.setVisible(true);
     rejectQtyLabel.setVisible(true);
     rejectQty.setVisible(true); 
     movementDateLabel.setVisible(true);
     movementDate.setVisible(true);
     attributeLabel.setVisible(true);
     attribute.setVisible(true);
     isBackflush.setVisible(true);  
     locatorLabel.setVisible(true);
     locator.setVisible(true);
    }
    
    public void disableToDeliver()
    {
      toDeliverQtyLabel.setVisible(false);
      toDeliverQty.setVisible(false);
      scrapQtyLabel.setVisible(false);
      scrapQty.setVisible(false);
      rejectQtyLabel.setVisible(false);
      rejectQty.setVisible(false); 
      //movementDateLabel.setVisible(false);
      //movementDate.setVisible(false);
      attributeLabel.setVisible(false);
      attribute.setVisible(false);
      isBackflush.setVisible(false);
      //backflushGroup.setVisible(false);
      isBackflush.setVisible(false);
      //backflushGroupLabel.setVisible(false);
      //backflushGroup.setVisible(false);
      locatorLabel.setVisible(false);
      locator.setVisible(false);
      issue.setVisible(true);
     // executeQuery();
    }    


    
    
    /*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Generate;
    private javax.swing.JPanel PanelBottom;
    private javax.swing.JPanel PanelCenter;
    private javax.swing.JButton Process;
    private javax.swing.JPanel ReceiptIssueOrder;
    private javax.swing.JTabbedPane TabsReceiptsIssue;
    private javax.swing.JTextField attribute;
    private javax.swing.JLabel attributeLabel;
    private javax.swing.JTextField deliveryQty;
    private javax.swing.JLabel deliveryQtyLabel;
    private javax.swing.JTable issue;
    private javax.swing.JTextField issueMethod;
    private javax.swing.JLabel issueMethodLabel;
    private javax.swing.JScrollPane issueScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField movementDate;
    private javax.swing.JLabel movementDateLabel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JTextField order;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JTextField org;
    private javax.swing.JLabel orgLabel;
    private javax.swing.JTextField rejectQty;
    private javax.swing.JLabel rejectQtyLabel;
    private javax.swing.JTextField resource;
    private javax.swing.JLabel resourceLabel;
    private javax.swing.JTextField scrapQty;
    private javax.swing.JLabel scrapQtyLabel;
    // End of variables declaration//GEN-END:variables
    */
    
     
    // Variables declaration - do not modify
    private CPanel mainPanel = new CPanel();
    private CPanel Generate = new CPanel();
    private CPanel PanelBottom = new CPanel(); 
    private CPanel PanelCenter = new CPanel();
    private CPanel northPanel = new CPanel();
    private CButton Process = new CButton(); 
    private CPanel ReceiptIssueOrder = new CPanel();
    private javax.swing.JTabbedPane TabsReceiptsIssue = new JTabbedPane();
    //private MPAttributeLookup attributeL = new MPAttributeLookup (Env.getCtx(), m_WindowNo);
    private VPAttribute attribute = null;
    //private VPAttribute attribute = null;                   
    
    private CLabel attributeLabel = new CLabel();
    private VNumber orderedQty = new VNumber("QtyOrdered", false, false, false, DisplayType.Quantity, "QtyOrdered");
    private CLabel orderedQtyLabel =  new CLabel();
    private VNumber deliveredQty = new VNumber("QtyDelivered", false, false, false, DisplayType.Quantity, "QtyDelivered");
    private CLabel deliveredQtyLabel =  new CLabel();
    private VNumber openQty = new VNumber("QtyOpen", false, false, false, DisplayType.Quantity, "QtyOpen");
    private CLabel openQtyLabel =  new CLabel();
    private VNumber toDeliverQty = new VNumber("QtyToDeliver", true, false, true, DisplayType.Quantity, "QtyToDeliver");
    private CLabel toDeliverQtyLabel =  new CLabel();
    private VLookup issueMethod = null;
    private CLabel issueMethodLabel = new CLabel();;
    private javax.swing.JScrollPane issueScrollPane = new JScrollPane();
    private MiniTable issue = new MiniTable();
    private VDate movementDate = new VDate("MovementDate", true, false, true, DisplayType.Date, "MovementDate");
    private CLabel movementDateLabel =  new CLabel();
    private VLookup order = null; 
    private CLabel orderLabel =  new CLabel();
    //private VLookup org = null;
    //private CLabel orgLabel =  new CLabel();
    private VNumber rejectQty = new VNumber("Qtyreject", false, false, true, DisplayType.Quantity, "QtyReject");
    private CLabel rejectQtyLabel =  new CLabel();
    private VLookup resource = null; 
    private CLabel resourceLabel =  new CLabel();
    private VLookup warehouse = null; 
    private CLabel warehouseLabel =  new CLabel();
    private VNumber scrapQty = new VNumber("Qtyscrap", false, false, true, DisplayType.Quantity, "Qtyscrap");
    private CLabel scrapQtyLabel =  new CLabel();
    private CLabel backflushGroupLabel = new CLabel(Msg.translate(Env.getCtx(), "BackflushGroup"));
    private CTextField backflushGroup = new CTextField(10);
    
    private CLabel producLabel = new CLabel(Msg.translate(Env.getCtx(), "M_Product_ID"));
    private VLookup product = null;
    private CLabel nameLabel = new CLabel(Msg.translate(Env.getCtx(), "Name"));
    private CTextField name = new CTextField(30);
    private CLabel uomLabel = new CLabel(Msg.translate(Env.getCtx(), "C_UOM_ID"));
    private VLookup  uom = null;
    private CLabel uomorderLabel = new CLabel(Msg.translate(Env.getCtx(), "Altert UOM"));
    private VLookup  uomorder = null;
    
    private CLabel locatorLabel = new CLabel(Msg.translate(Env.getCtx(), "M_Locator_ID"));
    private VLocator locator = null;
       
    private VCheckBox isDelivery = new VCheckBox ("IsDelivery", false, false, true, Msg.translate(Env.getCtx(), "IsDelivery"), "", false);       
    private VCheckBox isBackflush = new VCheckBox ("IsBackflush", false, false, true, Msg.translate(Env.getCtx(), "IsBackflush"), "", false);
    
    private CLabel QtyBatchsLabel =  new CLabel();
    private VNumber qtyBatchs = new VNumber("QtyBatchs", false, false, false, DisplayType.Quantity, "QtyBatchs");
     private CLabel QtyBatchSizeLabel =  new CLabel();
    private VNumber qtyBatchSize = new VNumber("QtyBatchSize", false, false, false, DisplayType.Quantity, "QtyBatchSize");
    
   private StringBuffer    m_sql = null;
   private String          m_dateColumn = "";
   private String          m_qtyColumn = "";
   private String          m_groupBy = "";
   

}




/*
 * @(#)VOrderReceiptIssue.java   14.jun 2007
 * 
 *  Fin del fichero VOrderPlanning.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
