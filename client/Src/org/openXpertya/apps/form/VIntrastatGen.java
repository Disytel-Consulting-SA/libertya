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
import java.io.*;
import java.math.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.openXpertya.apps.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;


/**
 *	Generador de Informes Intrastat
 *
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A. Gonzalez, Conserti.
 * 
 *  @version $Id: VIntrastatGen.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */

public class VIntrastatGen extends CPanel
	implements FormPanel, ActionListener, VetoableChangeListener, ChangeListener, TableModelListener, ASyncProcess
{
	/**
	 *	Standard Constructor
	 */
	public VIntrastatGen()
	{
	}	//	VInOutGen

	/**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		log.fine("VIntrastatGen.init");
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
			log.saveError("VIntrastatGen.init", ex);
		}
	}	//	init

	/**	Window No			*/
	private int         	m_WindowNo = 0;
	/**	FormFrame			*/
	private FormFrame 		m_frame;

	private boolean			m_selectionActive = true;
	private String          m_whereClause;
	/** Type of declaration			*/
	private Object			p_TypeIntrastatDeclaration;
	private int 			type = 0;
	/** Print Format 			*/
	private Object 			p_AD_PrintFormat_ID;
	private int 			pf_id = 0;
	//
	private CTabbedPane tabbedPane = new CTabbedPane();
	private CPanel selPanel = new CPanel();
	private CPanel selNorthPanel = new CPanel();
	private BorderLayout selPanelLayout = new BorderLayout();
	
	private CLabel lTypeIntrastatDeclaration = new CLabel();
	private VLookup fTypeIntrastatDeclaration;
	private VDate fDateInitial = new VDate();
	private VDate fDateFinal = new VDate();
	private CLabel lAD_PrintFormat_ID = new CLabel();
	private VLookup fAD_PrintFormat_ID;

	private FlowLayout northPanelLayout = new FlowLayout();
	private ConfirmPanel confirmPanelSel = new ConfirmPanel(true, false, false, false, false, true, true);
	private ConfirmPanel confirmPanelGen = new ConfirmPanel(false, true, false, false, false, false, true);
	private StatusBar statusBar = new StatusBar();
	private CPanel genPanel = new CPanel();
	private BorderLayout genLayout = new BorderLayout();
	private CTextPane info = new CTextPane();
	private JScrollPane scrollPane = new JScrollPane();
	private MiniTable miniTable = new MiniTable();
	private int m_keyColumnIndex = -1;
	private boolean salir = true;
	 private static CLogger log = CLogger.getCLogger( VIntrastatGen.class );
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
		//
		selPanel.setLayout(selPanelLayout);
		lTypeIntrastatDeclaration.setLabelFor(fTypeIntrastatDeclaration);
		lTypeIntrastatDeclaration.setText(Msg.translate(Env.getCtx(), "TypeIntrastatDeclaration"));
		lAD_PrintFormat_ID.setLabelFor(fAD_PrintFormat_ID);
		lAD_PrintFormat_ID.setText(Msg.translate(Env.getCtx(), "AD_PrintFormat_ID"));
		selNorthPanel.setLayout(northPanelLayout);
		northPanelLayout.setAlignment(FlowLayout.LEFT);
		tabbedPane.add(selPanel, Msg.getMsg(Env.getCtx(), "Select"));
		selPanel.add(selNorthPanel, BorderLayout.NORTH);
		selNorthPanel.add(lTypeIntrastatDeclaration, null);
		selNorthPanel.add(fTypeIntrastatDeclaration, null);
		selNorthPanel.add(lAD_PrintFormat_ID, null);
		selNorthPanel.add(fAD_PrintFormat_ID, null);
		selNorthPanel.add(fDateInitial, null);
		selNorthPanel.add(fDateFinal, null);
		selPanel.setName("selPanel");
		selPanel.add(confirmPanelSel, BorderLayout.SOUTH);
		selPanel.add(scrollPane, BorderLayout.CENTER);
		scrollPane.getViewport().add(miniTable, null);
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
		//MLookup tydL = new MLookup(MLookupFactory.getLookup_List (Env.getLanguage(Env.getCtx()),1000012), 0) ;
		MLookup tydL = MLookupFactory.get (Env.getCtx(), m_WindowNo,0, 1501, DisplayType.Table);
		fTypeIntrastatDeclaration = new VLookup ("C_DocType_ID", true, false, true, tydL);
		lTypeIntrastatDeclaration.setText(Msg.translate(Env.getCtx(), "C_DocType MFG"));
		fTypeIntrastatDeclaration.addVetoableChangeListener(this);
		//
		MLookup pfL = MLookupFactory.get (Env.getCtx(), m_WindowNo,0, 7026, DisplayType.Table);
		fAD_PrintFormat_ID = new VLookup ("AD_PrintFormat_ID", true, false, true, pfL);
		lAD_PrintFormat_ID.setText(Msg.translate(Env.getCtx(), "AD_PrintFormat_ID"));
		fAD_PrintFormat_ID.addVetoableChangeListener(this);
		
	}	//	fillPicks

	/**
	 *	Dynamic Init.
	 *	- Create GridController & Panel
	 *	- AD_Column_ID from C_Order
	 */
	private void dynInit()
	{
		miniTable.addColumn("M_InOutLine_ID");
		miniTable.addColumn("MovementType");
		miniTable.addColumn("Product");
		miniTable.addColumn("CountryCode");
		miniTable.addColumn("IncotermCode");
		miniTable.addColumn("TranNatureCodeA");
		miniTable.addColumn("TranNatureCodeB");
		miniTable.addColumn("ShipmentCode");
		miniTable.addColumn("IntrastatCode");
		miniTable.addColumn("StateTermsCode");
		//
		miniTable.setRowSelectionAllowed(true);
		//  set details
		miniTable.setColumnClass(0, IDColumn.class, false, " ");
		m_keyColumnIndex = 0;
		miniTable.setColumnClass(1, String.class, true, Msg.translate(Env.getCtx(), "MovementType"));
		miniTable.setColumnClass(2, String.class, true, Msg.translate(Env.getCtx(), "Product"));
		miniTable.setColumnClass(3, String.class, true, Msg.translate(Env.getCtx(), "CountryCode"));
		miniTable.setColumnClass(4, String.class, true, Msg.translate(Env.getCtx(), "IncotermCode"));
		miniTable.setColumnClass(5, BigDecimal.class, true, Msg.translate(Env.getCtx(), "TranNatureCodeA"));
		miniTable.setColumnClass(6, BigDecimal.class, true, Msg.translate(Env.getCtx(), "TranNatureCodeB"));
		miniTable.setColumnClass(7, BigDecimal.class, true, Msg.translate(Env.getCtx(), "ShipmentCode"));
		miniTable.setColumnClass(8, BigDecimal.class, true, Msg.translate(Env.getCtx(), "IntrastatCode"));
		miniTable.setColumnClass(9, BigDecimal.class, true, Msg.translate(Env.getCtx(), "StateTermsCode"));
		//
		miniTable.autoSize();
		miniTable.getModel().addTableModelListener(this);

		//	Info
		statusBar.setStatusDB(" ");
		//	Tabbed Pane Listener
		tabbedPane.addChangeListener(this);

		fDateInitial.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		fDateInitial.addVetoableChangeListener(this);

		fDateFinal.setValue(Env.getContextAsDate(Env.getCtx(), "#Date"));
		fDateFinal.addVetoableChangeListener(this);
	}	//	dynInit

	/**
	 *  Query Info
	 */
	private void executeQuery()
	{
		salir = true;
		log.fine( "VIntrastatGen.executeQuery");
		int AD_Client_ID = Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));
		//  Create SQL
		StringBuffer sql = new StringBuffer(
			"SELECT i.M_InOutLine_ID, i.MovementType, i.product, i.CountryCode,"
			+ " i.IncotermCode, i.TranNatureCodeA, i.TranNatureCodeB,"
			+ " i.ShipmentCode, i.IntrastatCode, i.StateTermsCode"
			+ " FROM M_InOut_Intrastat_File i"
			+ " WHERE i.AD_Client_ID=?");

		sql.append(" AND i.MovementDate BETWEEN (?)");
		sql.append(" AND (?)");
		sql.append(" ORDER BY i.MovementType, i.IntrastatCode, i.MovementDate");

		//  reset table
		int row = 0;
		miniTable.setRowCount(row);
		//  Execute
		try
		{
			PreparedStatement pstmt = DB.prepareStatement(sql.toString());
			pstmt.setInt(1, AD_Client_ID);
			pstmt.setTimestamp(2, fDateInitial.getTimestamp());
			pstmt.setTimestamp(3, fDateFinal.getTimestamp());
			ResultSet rs = pstmt.executeQuery();
			//
			while (rs.next())
			{
				//  extend table
				miniTable.setRowCount(row+1);
				//  set values
				miniTable.setValueAt(new IDColumn(rs.getInt(1)), row, 0);	// M_InOutLine_ID
				miniTable.setValueAt(rs.getString(2), row, 1);				//  MovementType
				miniTable.setValueAt(rs.getString(3), row, 2);              //  product
				miniTable.setValueAt(rs.getString(4), row, 3);              //  CountryCode
				miniTable.setValueAt(rs.getString(5), row, 4);              //  IncotermCode
				miniTable.setValueAt(rs.getBigDecimal(6), row, 5);          //  TranNatureCodeA
				miniTable.setValueAt(rs.getBigDecimal(7), row, 6);          //  TranNatureCodeB
				miniTable.setValueAt(rs.getBigDecimal(8), row, 7);          //  ShipmentCode
				miniTable.setValueAt(rs.getBigDecimal(9), row, 8);          //  IntrastatCode
				miniTable.setValueAt(rs.getBigDecimal(10), row, 9);         //  StateTermsCode
			
				//  prepare next
				row++;
				salir = false;
			}
			rs.close();
			pstmt.close();
		}
		catch (SQLException e)
		{
			log.saveError("VIntrastatGen.executeQuery", e);
		}
		//
		miniTable.autoSize();
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
	 *	Action Listener modificado con lupa y selecctor multiple
	 *  @param e event
	 */
	public void actionPerformed (ActionEvent e)
	{
		log.fine("VIntrastatGen.actionPerformed - " + e.getActionCommand());
		//
		if (e.getActionCommand().equals(ConfirmPanel.A_ZOOM))
		{
			zoom();				
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL))
		{
			dispose();
			return;
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			if (salir)
			{
				dispose();
				return;
			}
			else
				generateDeclaration();
		}
	}	//	actionPerformed
	
	/**
	 * A�adido para posibilitar el zoom a pedidos
	 * Realizado por ConSerTi
	 */
	void zoom()
	{
		log.fine("VinoutGen.zoom");
		Integer linea = getSelectedRowKey();
		if (linea == null)
			return;
		MInOutLine m_inoutline = new MInOutLine(Env.getCtx(), linea.intValue(),null);
		AEnv.zoomLinea(MOrder.Table_ID, m_inoutline.getM_InOut_ID(), MInOutLine.Table_Name, linea.intValue(), true);
	}
	
	//a�adido para posibilitar el zoom a pedidos

	/**
	 *	Has Zoom 
	 *  @return (has zoom)
	 */
	boolean hasZoom()
	{
		return true;
	}	//	hasZoom

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
	
	}	//a�adido para posibilitar el zoom a las lineas de albar�n

	/**
	 *	Vetoable Change Listener - requery
	 *  @param e event
	 */
	public void vetoableChange(PropertyChangeEvent e)
	{
		log.fine("VInOutGen.vetoableChange - "
			+ e.getPropertyName() + "=" + e.getNewValue());
		if (e.getPropertyName().equals("TypeIntrastatDeclaration"))
		{
			p_TypeIntrastatDeclaration = e.getNewValue();
			fTypeIntrastatDeclaration.setValue(p_TypeIntrastatDeclaration);
			type = Integer.parseInt(fTypeIntrastatDeclaration.getValue().toString());
		}
		if (e.getPropertyName().equals("AD_PrintFormat_ID"))
		{
			p_AD_PrintFormat_ID = e.getNewValue();
			fAD_PrintFormat_ID.setValue(p_AD_PrintFormat_ID);	//	display value
			pf_id = Integer.parseInt(fAD_PrintFormat_ID.getValue().toString());
		}
		executeQuery();
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
	 *  Table Model Listener
	 *  @param e event
	 */
	public void tableChanged(TableModelEvent e)
	{
		int rowsSelected = 0;
		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0
			if (id != null && id.isSelected())
				rowsSelected++;
		}
		statusBar.setStatusDB(" " + rowsSelected + " ");
	}   //  tableChanged

	/**
	 *	Save Selection & return selecion Query or ""
	 *  @return where clause like C_Order_ID IN (...)
	 */
	private String saveSelection()
	{
		log.fine("IntrastatGenerate.saveSelection");
		//  ID selection may be pending
		miniTable.editingStopped(new ChangeEvent(this));
		//  Array of Integers
		ArrayList results = new ArrayList();

		//	Get selected entries
		int rows = miniTable.getRowCount();
		for (int i = 0; i < rows; i++)
		{
			IDColumn id = (IDColumn)miniTable.getValueAt(i, 0);     //  ID in column 0

			if (id != null)
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
			sb.append(results.get(i).toString());
		}

		if (results.size() > 1)
			sb.append(")");
		//
		log.fine( "IntrastatGenerate.saveSelection"+ sb.toString());
		return sb.toString();
	}	//	saveSelection

	/**************************************************************************
	 *	Generate Declaration
	 */
	private void generateDeclaration ()
	{
		type = Integer.parseInt(fTypeIntrastatDeclaration.getValue().toString());
		pf_id = Integer.parseInt(fAD_PrintFormat_ID.getValue().toString());

		if (type != 0 && type != (-1) && pf_id != 0 && pf_id != (-1))
		{
			Timestamp p_DateInitial = fDateInitial.getTimestamp();
			Timestamp p_DateFinal = fDateFinal.getTimestamp();
			String whereClause = saveSelection();
			confirmPanelGen.getOKButton().setEnabled(false);
			//	OK to print shipments
			if (ADialog.ask(m_WindowNo, this, "�Aumentar contador de declaraciones?"))
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				int AD_Client_ID = Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

				StringBuffer sql = new StringBuffer(
						"UPDATE M_InOutLine SET DeclarationNo=(DeclarationNo + 1)"
						+ " WHERE AD_Client_ID=");
				sql.append(AD_Client_ID).append(" AND ").append(whereClause);
 		
				int no = DB.executeUpdate(sql.toString());
				
				setCursor(Cursor.getDefaultCursor());
			}	//	OK to print shipments
			
			switch (type)
			{
				case 1: //declaraci�n en un fichero en csv
					String fout = Msg.getMsg(Env.getLanguage(Env.getCtx()),"In")+ "_" + p_DateInitial.toString() + ".csv";
					String fin = Msg.getMsg(Env.getLanguage(Env.getCtx()),"Out")+ "_" + p_DateInitial.toString() + ".csv";
					File outFile = new File(fout); 
					File inFile = new File(fin); 
					try { 
						outFile.createNewFile();
						inFile.createNewFile();
					} catch (IOException e) {
						log.saveError("IntrastatGenerate - can not create file", e);
					}		
					Writer inwriter = null;
					Writer outwriter = null;
					try
					{
						FileWriter fwout = new FileWriter (outFile, false);
						FileWriter fwin = new FileWriter (inFile, false);
						outwriter = new BufferedWriter(fwout);
						inwriter = new BufferedWriter(fwin);
					}
					catch (FileNotFoundException fnfe)
					{
						log.saveError("IntratatGenerate - " , fnfe.toString());
					}
					catch (IOException e)
					{
					}
				
					MPrintFormat pfFile = new MPrintFormat(Env.getCtx(), pf_id, null);

					for (int i = 0; i<=1; i++)
					{
						MQuery query = new MQuery("M_InOut_Intrastat_File");
						if (p_DateInitial != null && p_DateFinal != null)
							query.addRangeRestriction("MovementDate", p_DateInitial, p_DateFinal);

						if (i == 1) //Salidas Exportaci�n
						{
							query.addRestriction("MovementType", MQuery.LIKE, "%-");
							ReportEngine re = new ReportEngine(Env.getCtx(), pfFile, query, null);
							re.createFileText(outwriter, pfFile.getLanguage(), ";", false, false);
						}
						else  //Entradas Importaci�n
						{
							query.addRestriction("MovementType", MQuery.LIKE, "%+");
							ReportEngine re = new ReportEngine(Env.getCtx(), pfFile, query, null);
				 			re.createFileText(inwriter, pfFile.getLanguage(), ";", false, false);
						}
					}
				
					try
					{
						outwriter.flush();
						outwriter.close();
						inwriter.flush();
						inwriter.close();
					}
					catch (IOException e)
					{
					}
					break;
				
				case 2: //informe en papel con cabecera incluida

					MPrintFormat pfPaper = new MPrintFormat(Env.getCtx(), pf_id, null);

					MQuery query = new MQuery("M_InOut_Intrastat_File");
					if (p_DateInitial != null && p_DateFinal != null)
						query.addRangeRestriction("MovementDate", p_DateInitial, p_DateFinal);

					ReportEngine re = new ReportEngine(Env.getCtx(), pfPaper, query, null);

					new Viewer(re);
			
					break;
				
				case 3: //declaraci�n eviada por edifact
					break;
			}
			
			info.setText(Msg.getMsg(Env.getCtx(), "Informe generado"));
		}
		else
			info.setText(Msg.getMsg(Env.getCtx(), "Es necesario indicar tipo y formato para la declaraci�n."));

		//  Switch Tabs
		tabbedPane.setSelectedIndex(1);
		confirmPanelGen.getOKButton().setEnabled(true);
		salir = true;		
	}	//	generateDeclaration
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

}	//	VIntrastatGen
