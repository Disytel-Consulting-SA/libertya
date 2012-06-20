/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */


package org.openXpertya.apps.form;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.math.*;
import java.rmi.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.openXpertya.apps.*;
import org.openXpertya.db.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.interfaces.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

import java.sql.ResultSet;
import java.util.Properties;
/**
 *	Seleccion de paquetes a generar a partir de un albarán
 *
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A. Gonzalez, Pablo Menendez, Conserti.
 * 
 *  @version $Id: VPackGen.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
*/
public class VPackGen extends CPanel
	implements FormPanel, ActionListener, VetoableChangeListener, ChangeListener, TableModelListener, ASyncProcess
{
	/**
	 *	Standard Constructor
	 */
	public VPackGen()
	{
	}	//	VInOutGen

	/**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		
		//Log.trace(Log.l1_User, "VPackGen.init");
		Log.fine("VPackGen.init");
		m_WindowNo = WindowNo;
		m_frame = frame;
		Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "Y");
		try
		{
			fillPicks();
			jbInit();
			dynInit();
			frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
			frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
		}
		catch(Exception ex)
		{
			//Log.error("VPackGen,init", ex);
			Log.saveError("VPackGen,init", ex);
		}
	}	//	init

	/**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	FormFrame			*/
	private FormFrame 		m_frame;

	private boolean			m_selectionActive = true;
	private String          m_whereClause;
	private Object 			m_AD_Org_ID = null;
	private Object 			m_C_BPartner_ID = null;
	private Object			m_M_InOut_ID = null;
	private Object			m_M_Shipper_ID = null;
	private KeyNamePair		m_AD_PrintLabel_ID = null;
	private boolean 		envioCreado = false;
	//
	private CTabbedPane tabbedPane = new CTabbedPane();
	private CPanel selPanel = new CPanel();
	private CPanel selNorthPanel = new CPanel();
	private CPanel albaranPanel = new CPanel();
	private CPanel lineasPanel = new CPanel();
	private GridBagLayout selPanelLayout = new GridBagLayout();
	private CLabel lOrg = new CLabel();
	private VLookup fOrg;
	private CLabel lBPartner = new CLabel();
	private VLookup fBPartner;
	private CCheckBox automatico = new CCheckBox();  //insertado por ConSerTi para seleccionar toda una tabla
	private CCheckBox autoLineas = new CCheckBox();
	private FlowLayout northPanelLayout = new FlowLayout();

	private GridLayout northPanelLayout2 = new GridLayout(1,8);
	//	modificar la creacion del confirm panel para añadir la lupa

	private ConfirmPanel confirmPanelSel = new ConfirmPanel(true, false, false, false, false, true, true);
	private ConfirmPanel confirmPanelGen = new ConfirmPanel(false, true, false, false, false, false, true);
	private StatusBar statusBar = new StatusBar();
	private CPanel genPanel = new CPanel();
	private BorderLayout genLayout = new BorderLayout();
	private CTextPane info = new CTextPane();
	private JScrollPane scrollPane = new JScrollPane();
	private JScrollPane scrollDetalle = new JScrollPane();
	private MiniTable miniTable = new MiniTable();
	private MiniTable miniDetalle = new MiniTable();
	//índice inicial de columna seleccionada
	private int m_keyColumnIndex = -1;
	// para seleccionar el albarán y el transportista
	private CLabel lAlbaran = new CLabel();
	private VLookup fAlbaran;
	private CLabel lTransportista = new CLabel();
	private VLookup fTransportista;
	private CLabel lPaquetes = new CLabel();
	private VNumber fPaquetes = new VNumber();
	private StringBuffer albaranesSeleccionados = null;
	private CLabel lLineas = new CLabel();
	private CButton zoomAlbaran = null;
	private CLabel lLab = new CLabel();
	private VComboBox fLab = new VComboBox();
	//Añadido
	protected CLogger Log = CLogger.getCLogger( getClass());
	private int numeroPaquetes = 0;
	/**
	 *	Static Init.
	 *  <pre>
	 *  selPanel (tabbed)
	 *      fOrg, fBPartner
	 *      scrollPane & miniTable
	 *  genPanel
	 *      info
	 *  </pre>
	 *  @throws Exception
	 */
	void jbInit() throws Exception
	{
		CompiereColor.setBackground(this);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
		gbc.fill = GridBagConstraints.BOTH;
		
		//
		selPanel.setLayout(selPanelLayout);
		
		lOrg.setLabelFor(fOrg);
        lOrg.setText( Msg.translate( Env.getCtx(),"AD_Org_ID" ));
		lBPartner.setLabelFor(fBPartner);
		lBPartner.setText ( Msg.translate( Env.getCtx(),"C_BPartner_ID" ));
		
		// añadido por ConSerTi para seleccionar albaran y transportista
		automatico.setText(Msg.translate( Env.getCtx(),"Select all"));  //Añadido por ConSerTi para seleccionar una tabla entera
		automatico.setSelected(true);
		automatico.addActionListener(this);
		lAlbaran.setLabelFor(fAlbaran);
		lAlbaran.setText(Msg.translate( Env.getCtx(),"Shipment"));
		lTransportista.setLabelFor(fTransportista);
		lTransportista.setText(Msg.translate( Env.getCtx(),"ShipperName"));
		lPaquetes.setLabelFor(fPaquetes);
		lPaquetes.setText(Msg.translate( Env.getCtx(),"NoPackages"));
		autoLineas.setText(Msg.translate( Env.getCtx(),"Select all"));  //Añadido por ConSerTi para seleccionar una tabla entera
		autoLineas.setSelected(true);
		autoLineas.addActionListener(this);
		lLineas.setText(Msg.translate( Env.getCtx(),"Shipment Lines"));
		zoomAlbaran = ConfirmPanel.createZoomButton(false);
		zoomAlbaran.addActionListener(this);
		lLab.setLabelFor(fLab);
		lLab.setText(Msg.translate( Env.getCtx(),"AD_PrintLabel_ID"));
		fLab.addActionListener(this);
		
		
		selNorthPanel.setLayout(northPanelLayout2);
		//selNorthPanel.setLayout(northPanelLayout);
		albaranPanel.setLayout(northPanelLayout);
		lineasPanel.setLayout(northPanelLayout);
		northPanelLayout.setAlignment(FlowLayout.LEFT);

		tabbedPane.add(selPanel, Msg.getMsg(Env.getCtx(), "Select"));
		selPanel.setName("selPanel");
		
		// Panel Superior para criterior de busqueda y datos para los paquetes
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.25;
		gbc.gridheight = 2;
		//gbc.fill=GridBagConstraints.BOTH;
		//gbc.fill=GridBagConstraints.HORIZONTAL;
		selPanel.add(selNorthPanel, gbc);
		
		selNorthPanel.add(lOrg, null);
		selNorthPanel.add(fOrg, null);
		selNorthPanel.add(lBPartner, null);
		selNorthPanel.add(fBPartner, null);
		selNorthPanel.add(lTransportista, null);
		selNorthPanel.add(fTransportista, null);
		selNorthPanel.add(lPaquetes, null);
		selNorthPanel.add(fPaquetes, null);
		
		// Panel para los albaranes para restringir a un Albaran y seleccionarlos todos
		gbc.gridy = 2;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		selPanel.add(albaranPanel, gbc);
		
		albaranPanel.add(lAlbaran, null);
		albaranPanel.add(fAlbaran, null);
		albaranPanel.add(automatico, null);
		albaranPanel.add(zoomAlbaran, null);
		albaranPanel.add(lLab, null);
		albaranPanel.add(fLab, null);
		
		// Panel con la tabla de albaranes
		gbc.gridy = 3;
		gbc.gridheight = 3;
		gbc.weighty = 0.4;
		selPanel.add(scrollPane, gbc);
		
		// Panel para las lineas de albaran para seleccionarlos todos
		gbc.gridy = 6;
		gbc.gridheight = 1;
		gbc.weighty = 0;
		selPanel.add(lineasPanel, gbc);
		lineasPanel.add(lLineas, null);
		lineasPanel.add(autoLineas, null);

		// Panel con la tabla de lineas de albaran
		gbc.gridy = 7;
		gbc.gridheight = 3;
		gbc.weighty = 0.4;
		selPanel.add(scrollDetalle, gbc);

		// Confirm panel con las opciones de aceptar, cancelar y ver albaran
		gbc.gridy = 10;
		gbc.gridheight = 0;
		gbc.weighty = 0;
		selPanel.add(confirmPanelSel, gbc);

		scrollPane.getViewport().add(miniTable, null);
		scrollDetalle.getViewport().add(miniDetalle, null);
		confirmPanelSel.addActionListener(this);
		//
		
		tabbedPane.add(genPanel, Msg.getMsg(Env.getCtx(), "Generate"));
		genPanel.setLayout(genLayout);
		genPanel.add(info, BorderLayout.CENTER);
		genPanel.setEnabled(false);
		info.setBackground(CompierePLAF.getFieldBackground_Inactive());
		info.setEditable(false);
		genPanel.add(confirmPanelGen, BorderLayout.SOUTH);
		confirmPanelGen.addActionListener(this);
	}	//	jbInit

	/**
	 *	Fill Picks
	 *		Column_ID from C_Order
	 *  @throws Exception if Lookups cannot be initialized
	 */
	private void fillPicks() throws Exception
	{
		MLookup orgL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, 2163, DisplayType.TableDir);
		fOrg = new VLookup ("AD_Org_ID", true, false, true, orgL);
		lOrg.setText(Msg.translate(Env.getCtx(), "AD_Org_ID"));
		fOrg.addVetoableChangeListener(this);
		//	PrintLabel
		String sql = "SELECT AD_PrintLabel_ID, Name"
			+ " FROM AD_PrintLabel";
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				KeyNamePair kn = new KeyNamePair (rs.getInt(1), rs.getString(2));
				fLab.addItem(kn);
				fLab.setSelectedItem(kn);
			}

			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			//Log.error ("VPackGen.fillPicks", e);
			Log.saveError("VPackGen.fillPicks", e);
		}

		
		MLookup bpL = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, 2762, DisplayType.Search);
		fBPartner = new VLookup ("C_BPartner_ID", false, false, true, bpL);
		lBPartner.setText(Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		fBPartner.addVetoableChangeListener(this);
		
		MLookup alb = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, 3521, DisplayType.Search);
		fAlbaran = new VLookup ("M_InOut_ID", false, false, true, alb);
		lAlbaran.setText(Msg.translate(Env.getCtx(), "M_InOut_ID"));
		fAlbaran.addVetoableChangeListener(this);
		
		MLookup tra = MLookupFactory.get(Env.getCtx(), m_WindowNo, 0, 2077, DisplayType.TableDir);
		fTransportista = new VLookup ("M_Shipper_ID", false, false, true, tra);
		lTransportista.setText(Msg.translate(Env.getCtx(), "M_Shipper_ID"));
		fTransportista.addVetoableChangeListener(this);
	}	//	fillPicks

	/**
	 *	Dynamic Init.
	 *	- Create GridController & Panel
	 *	- AD_Column_ID from C_Order
	 */
	private void dynInit()
	{
		//  create Columns
		miniTable.addColumn("M_InOut_ID");
		miniTable.addColumn("AD_Org_ID");
		miniTable.addColumn("DocumentNo");
		miniTable.addColumn("C_Order_ID");
		miniTable.addColumn("C_BPartner_ID");
		miniTable.addColumn("DocStatus");
		miniTable.addColumn("DateOrdered");
		//
		miniTable.setMultiSelection(true);
		miniTable.setRowSelectionAllowed(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		//	sentencia añadida para que se sepa que IDColumn.class está en la 0
		m_keyColumnIndex = 0;    
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "AD_Org_ID"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "DocumentNo"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "C_Order_ID"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "C_BPartner_ID"));
		miniTable.setColumnClass(5, String.class, true, Msg.translate(Env.getCtx(), "DocStatus"));
		miniTable.setColumnClass(6, Timestamp.class, true, Msg.translate(Env.getCtx(), "DateOrdered"));
		//
		miniTable.autoSize();
		miniTable.getModel().addTableModelListener(this);
		
		//creamos las columnas
		miniDetalle.addColumn("M_InOutLine_ID");
		miniDetalle.addColumn("AD_Org_ID");
		miniDetalle.addColumn("M_InOut_ID");
		miniDetalle.addColumn("M_Product_ID");
		miniDetalle.addColumn("QtyEntered");
		miniDetalle.addColumn("Peso");
		//
		miniDetalle.setMultiSelection(true);
		miniDetalle.setRowSelectionAllowed(true);
		//  set details
		miniDetalle.setColumnClass(0, IDColumn.class, false, " ");
		//	sentencia añadida para que se sepa que IDColumn.class está en la 0
		m_keyColumnIndex = 0;    
		miniDetalle.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "AD_Org_ID"));
		miniDetalle.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "M_InOut_ID"));
		miniDetalle.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "M_Product_ID"));
		miniDetalle.setColumnClass(4, BigDecimal.class, true, Msg.translate(Env.getCtx(), "QtyEntered"));
		miniDetalle.setColumnClass(5, String.class, true,"Peso");
		//
		miniDetalle.autoSize();
		miniDetalle.getModel().addTableModelListener(this);
		//	Info
		statusBar.setStatusLine(Msg.getMsg( Env.getCtx(),"Select shipment and shipment lines"));
		statusBar.setStatusDB(" ");
		//	Tabbed Pane Listener
		tabbedPane.addChangeListener(this);		
	}	//	dynInit

	/**
	 *  Query Info
	 */
	private void executeQuery()
	{
		executeQueryAlbaranes();
		executeQueryLineas();
	}
	
	/**
	 *  Query Info
	 */
	private void executeQueryAlbaranes()
	{
		//Log.trace(Log.l1_User, "VInOutGen.executeQuery");
		Log.fine("VInOutGen.executeQuery");
		//  Create SQL
		StringBuffer sql = new StringBuffer(
			"SELECT io.M_InOut_ID, o.Name, io.DocumentNo, io.C_Order_ID, bp.Name, io.DocStatus, io.DateOrdered "
			+ "FROM M_InOut io, AD_Org o, C_BPartner bp"
			+ " WHERE io.AD_Org_ID=o.AD_Org_ID"
			+ " AND io.C_BPartner_ID=bp.C_BPartner_ID"
			+ " AND io.IsSOTrx='Y' ");
			//+"AND io.docstatus like 'CO'");

		if (m_AD_Org_ID != null)
			sql.append(" AND io.AD_Org_ID=").append(m_AD_Org_ID);
		if (m_C_BPartner_ID != null)
			sql.append(" AND io.C_BPartner_ID=").append(m_C_BPartner_ID);
		if (m_M_InOut_ID != null)
			sql.append(" AND io.M_InOut_ID=").append(m_M_InOut_ID);
		//
		sql.append(" ORDER BY o.Name,io.M_InOut_ID");
		
		//  reset table
		int row = 0;
		miniTable.setRowCount(row);
		//  Execute
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			
			//
			while (rs.next())
			{
				//MInOut albaran = new MInOut(Env.getCtx(), rs.getInt(1));
				//Modificado
				MInOut albaran = new MInOut(Env.getCtx(), rs.getInt(1),null);
				DocumentEngine engine = new DocumentEngine (albaran, albaran.getDocStatus());
				
				//compruebo si el albarán podrá ser completado al realizar el envío
				//if (engine.isValidAction(DocAction.ACTION_Complete))
				//{
					
					//  extend table
					miniTable.setRowCount(row+1);
					//  set values
					IDColumn id = new IDColumn(rs.getInt(1));
					id.setSelected(true);
					miniTable.setValueAt(id, row, 0);   						//  M_InOut_ID
					miniTable.setValueAt(rs.getString(2), row, 1);              //  AD_Org_ID
					miniTable.setValueAt(rs.getString(3), row, 2);              //  DocumentNo
					miniTable.setValueAt(rs.getString(4), row, 3);              //  C_Order_ID
					miniTable.setValueAt(rs.getString(5), row, 4);              //  C_BPartner_ID
					miniTable.setValueAt(rs.getString(6), row, 5);              //  DocStatus
					miniTable.setValueAt(rs.getTimestamp(7), row, 6);          	//  DateOrdered
					//  prepare next
					row++;
				//}
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			//Log.error("VPackGen.executeQueryAlbaranes", e);
			Log.saveError("VPackGen.executeQueryAlbaranes", e);
		}
		//
		miniTable.autoSize(); 
		
		automatico.setSelected(true);		
	}
	
	private void executeQueryLineas()
	{		
		//************************* Detalle Lineas del Albarán ********************
		//  Create SQL
		StringBuffer sql = new StringBuffer(
			"SELECT l.M_InOutLine_ID, o.Name, l.M_InOut_ID, p.Name, l.QtyEntered, (p.weight*l.QtyEntered) as peso "
			+ "FROM M_InOutLine l, AD_Org o, M_Product p");
			//, M_Inout ut
		sql.append(" WHERE l.AD_Org_ID=o.AD_Org_ID"
			+ " AND l.M_Product_ID=p.M_Product_ID ");
		//AND l.m_inout_id=ut.m_inout_id AND ut.docstatus like 'CO'
		comprobarSeleccion();
		if (albaranesSeleccionados != null)
			sql.append(albaranesSeleccionados.toString());
		else
			sql.append(" AND 1=2");
		
		sql.append(" AND l.M_InOutLine_ID NOT IN (SELECT p.M_InOutLine_ID FROM M_PackageLine p)");
		
		//
		sql.append(" ORDER BY o.Name,l.M_InOut_ID,l.M_InOutLine_ID");

		//  reset table
		int row = 0;
		miniDetalle.setRowCount(row);
		//  Execute
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
			
				miniDetalle.setRowCount(row+1);
				//  set values
				IDColumn id = new IDColumn(rs.getInt(1));
				id.setSelected(true);
				miniDetalle.setValueAt(id, row, 0);   						  //  M_InOutLine_ID
				miniDetalle.setValueAt(rs.getString(2), row, 1);              //  Org
				miniDetalle.setValueAt(rs.getString(3), row, 2);              //  M_InOut_ID
				miniDetalle.setValueAt(rs.getString(4), row, 3);              //  M_Product_ID
				miniDetalle.setValueAt(rs.getBigDecimal(5), row, 4);          //  QtyEntered
				miniDetalle.setValueAt(rs.getBigDecimal(6), row, 5);		  //  Peso	
				//  prepare next
				row++;
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			//Log.error("VPackGen.executeQueryLineas", e);
			Log.saveError("VPackGen.executeQueryLineas", e);
		}
		//
		miniDetalle.autoSize();
		
		autoLineas.setSelected(true);
	}   //  executeQuery

	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
		
	}	//	dispose
	
	
	/**
	 * Seleccionar Todos
	 * Selecciona todos los Albaranes de la tabla
	 * 
	 * @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *ConSerTi 2005-2007 
	 */	
	private void seleccionarTodos()
	{
		for (int i = 0; i < miniTable.getRowCount(); i++)
		{
			IDColumn id = (IDColumn)miniTable.getModel().getValueAt(i,0);
			id.setSelected(automatico.isSelected());
			miniTable.getModel().setValueAt(id, i, 0);
		}
		executeQueryLineas();
	} 

	/**
	 * Seleccionar Todas las lineas
	 * Selecciona todas las lineas de la tabla
	 * 
	 * @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *ConSerTi 2005-2007 
	 */	
	private void seleccionarLineas()
	{
		for (int i = 0; i < miniDetalle.getRowCount(); i++)
		{
			IDColumn id = (IDColumn)miniDetalle.getModel().getValueAt(i,0);
			id.setSelected(autoLineas.isSelected());
			miniDetalle.getModel().setValueAt(id, i, 0);
		}
	}

	
	/**
	 *	Action Listener modificado con lupa y selecctor multiple
	 *  @param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		//Log.trace(Log.l1_User, "VInOutGen.actionPerformed - " + e.getActionCommand());
		Log.fine("VInOutGen.actionPerformed - " + e.getActionCommand());
		//
		if (e.getSource().equals(automatico))
			seleccionarTodos();
		else if (e.getSource().equals(autoLineas))
			seleccionarLineas();
		
		// modificada para a�adir las acciones de la lupa 
		else if (e.getSource().equals(zoomAlbaran))
		{
			zoom();
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_ZOOM))
		{
			zoomLineas();				
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL))
		{
			dispose();
			return;
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			m_whereClause = saveSelection();
			if (m_whereClause.length() > 0 && m_selectionActive)
				generarPaquetes();
			else
				dispose();
		}
	}	//	actionPerformed

	/**
	 * Añadido para posibilitar el zoom a pedidos
	 * Realizado por ConSerTi
	 */
	void zoom()
	{
		//Log.trace(Log.l1_User, "VinoutGen.zoom");
		Log.fine("VinoutGen.zoom");
		Integer alba = getSelectedRowKey();
		if (alba == null)
			return;
		//AEnv.zoom(MInOut.Table_ID, alba.intValue(), true);
		//Modificado
		AEnv.zoom(MInOut.Table_ID, alba.intValue());
	}

	void zoomLineas()
	{
		//Log.trace(Log.l1_User, "VinoutGen.zoom");
		Log.fine("VinoutGen.zoom");
		Integer alba = getSelectedRowKeyLineas();
		if (alba == null)
			return;
		
		//MInOutLine linea = new MInOutLine(Env.getCtx(), alba.intValue());
		//Modificado
		MInOutLine linea = new MInOutLine(Env.getCtx(), alba.intValue(),null);
		AEnv.zoomLinea(MInOut.Table_ID, linea.getM_InOut_ID(), MInOutLine.Table_Name, alba.intValue(), true);
		
		linea = null;
	}

	//añadido para posibilitar el zoom a pedidos

	/**
	 *	Has Zoom 
	 *  @return (has zoom)
	 */
	boolean hasZoom()
	{
		return true;
	}	//	hasZoom
	
	//añadido para posibilitar el zoom a pedidos

	protected Integer getSelectedRowKey()
	{
		int row = miniTable.getSelectedRow();
		if (row != -1 && m_keyColumnIndex != -1)
		{
			Object data = miniTable.getModel().getValueAt(row, m_keyColumnIndex);
			if (data instanceof IDColumn)
				data = ((IDColumn)data).getRecord_ID();
			if (data instanceof Integer)
				return (Integer)data;
		}
		return null;	
	}	//añadido para posibilitar el zoom a Albaranes

	/**
	 * Leer el numero de linea seleccionado en la tabla de lineas
	 * 
	 * @return el numero de linea seleccionado el el panel de las Lineas
	 */
	protected Integer getSelectedRowKeyLineas()
	{
		int row = miniDetalle.getSelectedRow();
		if (row != -1 && m_keyColumnIndex != -1)
		{
			Object data = miniDetalle.getModel().getValueAt(row, m_keyColumnIndex);
			if (data instanceof IDColumn)
				data = ((IDColumn)data).getRecord_ID();
			if (data instanceof Integer)
				return (Integer)data;
		}
		return null;	
	}	//añadido para posibilitar el zoom a Albaranes
	
	/**
	 *	Vetoable Change Listener - requery
	 *  @param e event
	 */
	public void vetoableChange(PropertyChangeEvent e)
	{
		//Log.trace(Log.l1_User, "VInOutGen.vetoableChange - "
		//	+ e.getPropertyName() + "=" + e.getNewValue());
		Log.fine("VInOutGen.vetoableChange - "
				+ e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("AD_Org_ID"))
			m_AD_Org_ID = e.getNewValue();
		if (e.getPropertyName().equals("C_BPartner_ID"))
		{
			m_C_BPartner_ID = e.getNewValue();
			fBPartner.setValue(m_C_BPartner_ID);
		}
		if (e.getPropertyName().equals("M_InOut_ID"))
		{
			m_M_InOut_ID = e.getNewValue();
			fAlbaran.setValue(m_M_InOut_ID);

			if (m_M_InOut_ID != null)
			{
				Integer id = Integer.valueOf(m_M_InOut_ID.toString());
				//MInOut alb = new MInOut(Env.getCtx(), id.intValue());
				//Modificado
				MInOut alb = new MInOut(Env.getCtx(), id.intValue(),null);
				m_C_BPartner_ID = alb.get_Value("C_BPartner_ID");
				fBPartner.setValue(m_C_BPartner_ID);	
			}
		}
		if (e.getPropertyName().equals("M_Shipper_ID"))
		{
			m_M_Shipper_ID = e.getNewValue();
			fTransportista.setValue(m_M_Shipper_ID);
		}
		else{
			executeQuery();
		}
	}	//	vetoableChange

	/**
	 *	Change Listener (Tab changed)
	 *  @param e event
	 */
	public void stateChanged (ChangeEvent e)
	{
		int index = tabbedPane.getSelectedIndex();
		m_selectionActive = (index == 0);
	}	//	stateChanged
	
	/**
	 * Crear cadena para comprobar acotar la busqueda de lineas a los albaranes seleccionados
	 * 
	 * @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *ConSerTi 2005-2007
	 */
	public void comprobarSeleccion()
	{
		int rows = miniTable.getRowCount();
		int seleccionados = 0;
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getModel().getValueAt(i,0);
			if (id != null && id.isSelected())
				seleccionados++;
		}
		if (seleccionados == 1)
		{
			for (int i = 0; i < rows; i++)
			{
				IDColumn id = (IDColumn)miniTable.getModel().getValueAt(i,0);
				if (id != null && id.isSelected())
				{
					albaranesSeleccionados = new StringBuffer(" AND (l.M_InOut_ID=");
					albaranesSeleccionados.append(id.getRecord_ID().toString()).append(")");
				}
			}
		}
		else if (seleccionados > 1)
		{
			boolean primero = true;
		
			for (int i = 0; i < rows; i++)
			{
				IDColumn id = (IDColumn)miniTable.getModel().getValueAt(i,0);
				if (id != null && id.isSelected())
				{
					if (primero)
						albaranesSeleccionados = new StringBuffer(" AND (");
					else
						albaranesSeleccionados.append(" OR ");				
					primero = false;						
					albaranesSeleccionados.append("(l.M_InOut_ID=").append(id.getRecord_ID().toString()).append(")");
				}
			}

			if (!primero)
				albaranesSeleccionados.append(")");
			else
				albaranesSeleccionados = null;
		}
		else
			albaranesSeleccionados = null;
	}

	/**
	 *  Table Model Listener
	 *  @param e event
	 */
	public void tableChanged(TableModelEvent e)
	{
		if (e.getSource().equals(miniTable.getModel()))
		{
			executeQueryLineas();
		}
		else if (e.getSource().equals(miniDetalle.getModel()))
		{
			int rowsSelected = 0;
			int rows = miniDetalle.getRowCount();
			for (int i = 0; i < rows; i++)
			{
				IDColumn id = (IDColumn)miniDetalle.getValueAt(i, 0);     //  ID in column 0
				if (id != null && id.isSelected())
					rowsSelected++;
			}
			statusBar.setStatusDB(" " + rowsSelected + " ");	
		}
	}   //  tableChanged

	/**
	 *	Salva la selecci�n preparando una query con una clausula where que incluye
	 *  a todas las lineas seleccionadas
	 * 
	 *  @return where clause like C_InOutLine_ID IN (...)
	 */
	private String saveSelection()
	{
		//Log.trace(Log.l1_User, "VPackGen.saveSelection");
		Log.fine("VPackGen.saveSelection");
		//  ID selection may be pending
		miniDetalle.editingStopped(new ChangeEvent(this));
		//  Array of Integers
		ArrayList results = new ArrayList();

		//	Get selected entries
		int rows = miniDetalle.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniDetalle.getValueAt(i, 0);     //  ID in column 0
		//	Log.trace(Log.l6_Database, "Row=" + i + " - " + id);
			Log.fine("Row=" + i + " - " + id);
			if (id != null && id.isSelected())
				results.add(id.getRecord_ID());
		}

		if (results.size() == 0)
			return "";

		//	Query String
		String keyColumn = "M_InOutLine_ID";
		StringBuffer sb = new StringBuffer(keyColumn);
		if (results.size() > 1)
			sb.append(" IN (");
		else
			sb.append("=");
		//	Add elements
		for (int i = 0; i < results.size(); i++)
		{
			if (i > 0)
				sb.append(",");
			if (keyColumn.endsWith("_ID"))
				sb.append(results.get(i).toString());
			else
				sb.append("'").append(results.get(i).toString());
		}

		if (results.size() > 1)
			sb.append(")");
		//
		//Log.trace(Log.l4_Data, "VInOutGen.saveSelection", sb.toString());
		Log.fine("VInOutGen.saveSelection"+sb.toString());
		return sb.toString();
	}	//	saveSelection

	
	/**************************************************************************
	 *	Generar Paquetes
	 */
	private void generarPaquetes ()
	{
		if (fPaquetes.getValue() != null)
			numeroPaquetes = (new BigDecimal(fPaquetes.getValue().toString())).intValue();
			m_AD_PrintLabel_ID = (KeyNamePair)fLab.getSelectedItem();
		if (m_C_BPartner_ID != null && m_M_Shipper_ID != null && numeroPaquetes > 0 && m_AD_PrintLabel_ID != null) 
		//porque es obligatorio el campo BPartner
		{
			//	Reset Selection
			StringBuffer sql = new StringBuffer("UPDATE M_InOutLine SET IsSelected = 'N' "
				+ "WHERE IsSelected='Y'"
				+ " AND AD_Client_ID=");
			sql.append(Env.getAD_Client_ID(Env.getCtx()));
			int no = DB.executeUpdate(sql.toString());
			//Log.trace(Log.l3_Util, "VPackGen.generarPaquetes - Reset=" + no);
			Log.fine("VPackGen.generarPaquetes - Reset=" + no);
			//	Set Selection
			sql = new StringBuffer("UPDATE M_InOutLine SET IsSelected='Y' WHERE ");
				sql.append(m_whereClause);
			no = DB.executeUpdate(sql.toString());
			//Log.trace(Log.l6_Database, "VPackGen.generarPaquetes - " + sql.toString());
			Log.fine("VPackGen.generarPaquetes - " + sql.toString());
			if (no == 0)
			{
				String msg = "No hay lineas seleccionadas";     //  not translated!
				Log.fine("VPackGen.generarPaquetes"+ msg);
				info.setText(msg);
				return;
			}
			Log.fine("VPackGen.generarPaquetes - Set=" + no);
			m_selectionActive = false; //por si se llamara dos veces a esta funcion
			statusBar.setStatusLine(Msg.getMsg(Env.getCtx(), "InOutGenerateGen"));
			statusBar.setStatusDB(String.valueOf(no));

			//	Prepare Process
			//int envioCreate_ID = DB.getSQLValue("SELECT AD_Process_ID FROM AD_Process WHERE value='M_Envio_Create'");
			//Modificado
			int envioCreate_ID = DB.getSQLValue(null,"SELECT AD_Process_ID FROM AD_Process WHERE value='M_Envio_Create'");
			ProcessInfo pi = new ProcessInfo ("Crear Envio", envioCreate_ID);  // EnvioCreate
			pi.setAD_PInstance_ID (MPInstance.getAD_PInstance_ID(Env.getCtx(), pi.getAD_Process_ID(), pi.getRecord_ID()));
			//Modificado
			
			Log.fine("AD_Process_ID="+pi.getAD_Process_ID()+",Env.getCtx="+Env.getCtx()+", pi.getAD_PInstance_ID()="+pi.getAD_PInstance_ID()+", pi.getRecord_ID="+pi.getRecord_ID());
			if (pi.getAD_PInstance_ID() == 0)
			{
				info.setText(Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
				return;
			}
			//	Add Parameter - Selection=Y
			sql = new StringBuffer("INSERT INTO AD_PInstance_Para (AD_PInstance_ID,SeqNo,ParameterName, P_STRING) ");
				sql.append("VALUES (").append(pi.getAD_PInstance_ID()).append(",1,'Selection', 'Y')");
			no = DB.executeUpdate(sql.toString());
			//	Add Parameter - M_Shipper_ID
			sql = new StringBuffer("INSERT INTO AD_PInstance_Para (AD_PInstance_ID,SeqNo,ParameterName, P_STRING) ");
				sql.append("VALUES (").append(pi.getAD_PInstance_ID()).append(",2,'M_Shipper_ID',");
				sql.append(m_M_Shipper_ID.toString()).append(")");
			no = no + DB.executeUpdate(sql.toString());
			//	Add Parameter - No_Paquetes
			sql = new StringBuffer("INSERT INTO AD_PInstance_Para (AD_PInstance_ID,SeqNo,ParameterName, P_STRING) ");
				sql.append("VALUES (").append(pi.getAD_PInstance_ID()).append(",3,'No_Paquetes',");
				sql.append(fPaquetes.getValue().toString()).append(")");
			no = no + DB.executeUpdate(sql.toString());
			if (no == 0)
			{
				String msg = "No Parameter added";  //  not translated
				info.setText(msg);
				//Log.error("VInOutGen.generateShipments - " + msg);
				Log.fine("VInOutGen.generateShipments - " + msg);
				return;
			}

			//	Execute Process
			//ProcessCtl worker = new ProcessCtl(this, pi);
			//Modificado
			ProcessCtl worker = new ProcessCtl(this, pi, null);
			worker.start();     //  complete tasks in unlockUI / generateShipments_complete
		}
		else
			ADialog.info(m_WindowNo, this, "Es necesario indicar el cliente, el transportista y la cantidad de paquetes.",
					"La cantidad de bultos o paquetes ha de ser mayor que 0.");
	}	//	Generar Paquetes
	
	/**
	 *  After Generate Envio
	 *  Called from Unlock UI
	 *  @param pi process info
	 */
	private void afterGenerateEnvio (ProcessInfo proInf)
	{
		//  Switch Tabs
		tabbedPane.setSelectedIndex(1);
		//
		ProcessInfoUtil.setLogFromDB(proInf);
		StringBuffer iText = new StringBuffer();
		iText.append("<b>").append(proInf.getSummary())
			.append("</b><br>(")
			.append("Se genera un envío, con tantos paquetes como albaranes incluye.")
			//  Shipments are generated depending on the Delivery Rule selection in the Order
			.append(")<br>")
			.append(proInf.getLogInfo(true));
		info.setText(iText.toString());

		//	Reset Selection
		String sql = "UPDATE M_InOutLine SET IsSelected='N' WHERE " + m_whereClause;
		int no = DB.executeUpdate(sql);
		//Log.trace(Log.l3_Util, "VPackGen.afterGenerateEnvio - Reset=" + no);
		Log.fine("VPackGen.afterGenerateEnvio - Reset=" + no);

		//	Get results
		int[] ids = proInf.getIDs();
		if (ids == null || ids.length == 0)
			return;
		//Log.trace(Log.l3_Util, "VPackGen.afterGenerateEnvio - PrintItems=" + ids.length);
		Log.fine("VPackGen.afterGenerateEnvio - PrintItems=" + ids.length);

		confirmPanelGen.getOKButton().setEnabled(false);
		//	OK to print shipments
		if (ADialog.ask(m_WindowNo, this, "Imprimir Etiquetas", null))
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			int retValue = ADialogDialog.A_CANCEL;
			do
			{
				//cogemos el envío
 				int M_Envio_ID = ids[0];
 				//Modificado por ConSerTi
 				//int proc_id = DB.getSQLValue("SELECT AD_Process_ID FROM AD_Process WHERE value='PrintLabel'");
 				int proc_id = DB.getSQLValue(null,"SELECT AD_Process_ID FROM AD_Process WHERE value='PrintLabel'");
 				if (proc_id != -1)
 				{
 					ProcessInfo proc = new ProcessInfo("Imprimiendo etiquetas", proc_id, MEnvio.Table_ID, M_Envio_ID);

 					proc.setAD_PInstance_ID (MPInstance.getAD_PInstance_ID(Env.getCtx(), proc.getAD_Process_ID(), proc.getRecord_ID()));
 					//Modificado
 					//proc.setAD_PInstance_ID (proc.getAD_PInstance_ID());
 					if (proc.getAD_PInstance_ID() == 0)
 					{
 						info.setText(Msg.getMsg(Env.getCtx(), "ProcessNoInstance"));
 						return;
 					}

 					//	Add Parameter - PrintLabel
 					sql = "INSERT INTO AD_PInstance_Para (AD_PInstance_ID,SeqNo,ParameterName, P_STRING) "
 						+ "VALUES (" + proc.getAD_PInstance_ID() + ",1,'PrintLabel', '" + m_AD_PrintLabel_ID.getKey() + "')";
 					no = DB.executeUpdate(sql);
 					sql = "INSERT INTO AD_PInstance_Para (AD_PInstance_ID,SeqNo,ParameterName, P_STRING) "
 						+ "VALUES (" + proc.getAD_PInstance_ID() + ",2,'NoCopias', '" + numeroPaquetes + "')";
 					no = DB.executeUpdate(sql);

 					if (no == 0)
 					{
 						info.setText("No Parameter added");
 						//Log.error("VPackGen.afterGenerateEnvio - No Parameter added");
 						Log.fine("VPackGen.afterGenerateEnvio - No Parameter added");
 						return;
 					}
 					//Modificado por ConSerTi
 					//String className = DB.getSQLValueString("SELECT ClassName FROM AD_Process WHERE AD_Process_ID=?",proc_id);
 					String className = DB.getSQLValueString(null,"SELECT ClassName FROM AD_Process WHERE AD_Process_ID=?",proc_id);
 					proc.setClassName(className);
 					
 					Server server = CConnection.get().getServer();
 					try
 					{
 						if (server != null)
 						{	
 							proc = server.process (Env.getCtx(), proc);
 						}
 					}
 					catch (RemoteException ex)
 					{
 					}
				
 					DB.executeUpdate("DELETE FROM AD_PInstance_Para WHERE AD_PInstance_ID=" + proc.getAD_PInstance_ID());
 					
 				}
				
				ADialogDialog d = new ADialogDialog (m_frame,
					Env.getHeader(Env.getCtx(), m_WindowNo),
					Msg.getMsg(Env.getCtx(), "PrintoutOK?"),
					JOptionPane.QUESTION_MESSAGE);
				retValue = d.getReturnCode();
			}
			while (retValue == ADialogDialog.A_CANCEL);
			setCursor(Cursor.getDefaultCursor());
		}	//	OK to print shipments

		//
		confirmPanelGen.getOKButton().setEnabled(true);
	}   //  generateShipments_complete
	
	/*************************************************************************/

	/**
	 *  Lock User Interface.
	 *  Called from the Worker before processing
	 *  @param pi process info
	 */
	public void lockUI (ProcessInfo pi)
	{
		this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		this.setEnabled(false);
	}   //  lockUI

	/**
	 *  Unlock User Interface.
	 *  Called from the Worker when processing is done
	 *  @param pi result of execute ASync call
	 */
	public void unlockUI (ProcessInfo pi)
	{
		this.setEnabled(true);
		this.setCursor(Cursor.getDefaultCursor());
		//
		
		if (!envioCreado)
		{
			envioCreado = true;
			afterGenerateEnvio(pi);
		}
	}   //  unlockUI

	/**
	 *  Is the UI locked (Internal method)
	 *  @return true, if UI is locked
	 */
	public boolean isUILocked()
	{
		return this.isEnabled();
	}   //  isUILocked

	/**
	 *  Method to be executed async.
	 *  Called from the Worker
	 *  @param pi ProcessInfo
	 */
	public void executeASync (ProcessInfo pi)
	{
	}   //  executeASync
}	//	VInOutGen
