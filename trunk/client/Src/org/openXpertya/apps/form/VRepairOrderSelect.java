/******************************************************************************
 *     El contenido de este fichero est� sujeto a la Licencia P�blica openXpertya versi�n 1.0 (LPO) en
 * tanto cuanto forme parte �ntegra del total del producto denominado:     openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *     Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *     Partes del c�digo son CopyRight � 2002-2005 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son CopyRight  � 2003-2005 de Consultor�a y Soporte en Redes y  Tecnolog�as de 
 * la  Informaci�n S.L., otras partes son adaptadas, ampliadas o mejoradas a partir de c�digo original
 * de terceros, recogidos en el ADDENDUM A, secci�n 3 (A.3) de dicha licencia LPO, y si dicho c�digo
 * es extraido como parte del total del producto, estar� sujeto a sus respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/licencia.html
 ******************************************************************************/

package org.openXpertya.apps.form;

import java.awt.*;
import java.awt.event.*;
import java.math.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.compiere.swing.*;
import org.openXpertya.apps.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 *	Seleccionar lineas de una orden de reparacion
 *	
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class VRepairOrderSelect extends CPanel 
	implements FormPanel, ActionListener
{
	protected int m_AD_PInstance_ID=0;	// proceso desde el que se esta llamando
	protected int m_C_Repair_Order_ID=0;	// orden desde la que se deben coger las líneas
	protected int m_C_DocTypeTarget_ID=0;	// tipo de documento que vamos a generar
	
	/**
	 *	Initialize Panel
	 *  @param WindowNo window
	 *  @param frame parent frame
	 */
	public void init (int WindowNo, FormFrame frame)
	{
		//Log.trace(Log.l1_User, "VBOMDropOrder.init");
		m_WindowNo = WindowNo;
		m_frame = frame;
		//
		if(frame.getParameter("AD_PInstance_ID")!=null)
			m_AD_PInstance_ID=((Integer)frame.getParameter("AD_PInstance_ID")).intValue();
		if(frame.getParameter("C_Repair_Order_ID")!=null)
			m_C_Repair_Order_ID=((Integer)frame.getParameter("C_Repair_Order_ID")).intValue();
		if(frame.getParameter("C_DocTypeTarget_ID")!=null)
			m_C_DocTypeTarget_ID=((Integer)frame.getParameter("C_DocTypeTarget_ID")).intValue();
		
		try
		{
			//	Top Selection Panel
			createSelectionPanel();
			m_frame.getContentPane().add(selectionPanel, BorderLayout.NORTH);
			//	Center
			createMainPanel();
			CScrollPane scroll = new CScrollPane (this);
			m_frame.getContentPane().add(scroll, BorderLayout.CENTER);
			confirmPanel.addActionListener(this);
			//	South
			m_frame.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
		}
		catch(Exception e)
		{
			//Log.error("VBOMDropOrder.init", e);
		}
		sizeIt();
	}	//	init

	/**
	 * 	Size Window
	 */
	private void sizeIt()
	{
		//	Frame
		m_frame.pack();
		Dimension size = m_frame.getPreferredSize();
		size.width = WINDOW_WIDTH;
		m_frame.setSize(size);
	}	//	size
	
	/**
	 * 	Dispose
	 */
	public void dispose()
	{
		if (m_frame != null)
			m_frame.dispose();
		m_frame = null;
		removeAll();
		if (selectionPanel != null)
			selectionPanel.removeAll();
		selectionPanel = null;
		if (m_selectionList != null)
			m_selectionList.clear();
		m_selectionList = null;
		if (m_productList != null)
			m_productList.clear();
		m_productList = null;
		if (m_qtyList != null)
			m_qtyList.clear();
		m_qtyList = null;
		if (m_buttonGroups != null)
			m_buttonGroups.clear();
		m_buttonGroups = null;
	}	//	dispose

	/**	Window No					*/
	private int         m_WindowNo = 0;
	/**	FormFrame					*/
	private FormFrame 	m_frame;
	/**	Product to create BOMs from	*/
	private MProduct 	m_product;
	/** BOM Qty						*/
	private BigDecimal	m_qty = Env.ONE;
	/**	Line Counter				*/
	private int			m_bomLine = 0;
	
	/**	List of all selectors		*/
	private ArrayList	m_selectionList = new ArrayList();
	/**	List of all quantities		*/
	private ArrayList	m_qtyList = new ArrayList();
	/** List of all lines           */
	private ArrayList	m_lineList = new ArrayList();
	/**	List of all products		*/
	private ArrayList	m_productList = new ArrayList();
	/** Alternative Group Lists		*/
	private HashMap		m_buttonGroups = new HashMap();

	private static final int	WINDOW_WIDTH = 600;	//	width of the window
	//
	private ConfirmPanel confirmPanel = new ConfirmPanel (true);
	private CPanel selectionPanel = new CPanel(new ALayout());
	
	/**************************************************************************
	 * 	Create Selection Panel
	 *	@param order
	 *	@param invoice
	 *	@param project
	 */
	private void createSelectionPanel ()
	{
		int row = 0;
		selectionPanel.setBorder(new TitledBorder("Elija las líneas que quiere incluir en el pedido"));
		
		//	Enabled in ActionPerformed
		confirmPanel.getOKButton().setEnabled(true);
		//	Size
		Dimension size = selectionPanel.getPreferredSize();
		size.width = WINDOW_WIDTH;
		selectionPanel.setPreferredSize(size);
	}	//	createSelectionPanel
	
	/**************************************************************************
	 * 	Create Main Panel.
	 * 	Called when changing Product
	 */
	private void createMainPanel ()
	{
		//Log.trace(Log.l3_Util, "VBOMDropOrder.createMainPanel", m_product);
		this.removeAll();
		this.setPreferredSize(null);
		this.invalidate();
		this.setBorder(null);
		//
		m_selectionList.clear();
		m_productList.clear();
		m_qtyList.clear();
		m_buttonGroups.clear();
		//
		this.setLayout (new ALayout());
		String title = Msg.getMsg(Env.getCtx(), "SelectProduct");
		
		if(m_C_Repair_Order_ID!=0)
			addRepairOrderLines();
		
		this.setBorder (new TitledBorder(title));
	}	//	createMainPanel
	
	private void addRepairOrderLines()
	{
		StringBuffer sql=new StringBuffer(	"SELECT rol.C_Repair_Order_Line_ID, rol.M_Product_ID, rol.QtyEntered, p.Name ")
									.append("FROM C_Repair_Order_Line rol ")
										.append("LEFT JOIN M_Product p ON (p.M_Product_ID = rol.M_Product_ID) ")
									.append("WHERE rol.IsActive='Y' ")
									.append("AND rol.IsWarranty='N' ")	// no esta en garantia
									.append("AND (rol.C_OrderLine_ID IS NULL OR rol.C_OrderLine_ID=0) ")
																	// no se ha guardado en ningun pedido
									.append("AND rol.C_Repair_Order_ID=? ")
									.append("ORDER BY rol.Line ASC");
		try
		{
			java.sql.PreparedStatement pstmt=DB.prepareStatement(sql.toString());
			pstmt.setInt(1, m_C_Repair_Order_ID);
			java.sql.ResultSet rs=pstmt.executeQuery();
			while(rs.next())
			{
				addDisplay(rs);
			}
		}
		catch(java.sql.SQLException e)
		{
			//
		}
	}
	
	/**
	 * 	Add Line to Display
	 *
	 *	@param rs resultset de la consulta
	 */
	private void addDisplay (java.sql.ResultSet rs)
	{
		//
		
		boolean selected = true;
		
		JCheckBox cb = new JCheckBox ();
		cb.setSelected(false);
		cb.setEnabled(true);
		//	cb.addActionListener(this);		//	will not change
		m_selectionList.add(cb);
		this.add(cb, new ALayoutConstraint(m_bomLine++, 0));
		
		//	Add to List & display
		try
		{
			m_productList.add (new Integer(rs.getInt("M_Product_ID")));
			VNumber qty = new VNumber ("Qty", true, false, true, DisplayType.Quantity, rs.getString("Name"));
			qty.setValue(new Integer(rs.getInt("QtyEntered")));
			qty.setReadWrite(false);
			m_lineList.add(new Integer(rs.getInt("C_Repair_Order_Line_ID")));
			m_qtyList.add(qty);
			CLabel label = new CLabel (rs.getString("Name"));
			label.setLabelFor(qty);
			this.add(label);
			this.add(qty);
		}
		catch(java.sql.SQLException e)
		{
			//
		}
	}	//	addDisplay

	
	/**
	 * 	Get Preferred Size
	 *	@return size
	 */
	public Dimension getPreferredSize ()
	{
		Dimension size = super.getPreferredSize ();
		if (size.width > WINDOW_WIDTH)
			size.width = WINDOW_WIDTH - 30;
		return size;
	}	//	getPreferredSize

	
	/**************************************************************************
	 *	Action Listener
	 *  @param e event
	 */
	public void actionPerformed (ActionEvent e)
	{	
		Object source = e.getSource();

		//	Toggle Qty Enabled
		if (source instanceof JCheckBox)
		{
			cmd_selection (source);	
			
		}
		//	OK
		else if (e.getActionCommand().equals(ConfirmPanel.A_OK))
		{
			if (cmd_save())
				dispose();
		}
		else if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL))
			dispose();
			
	}	//	actionPerformed

	/**
	 * 	Enable/disable qty based on selection
	 *	@param source JCheckBox or JRadioButton
	 */
	private void cmd_selection (Object source)
	{
		//
	}	//	cmd_selection

	/**
	 * 	Is Selection Selected
	 *	@param source CheckBox or RadioButton
	 *	@return true if selected
	 */
	private boolean isSelectionSelected (Object source)
	{
		boolean retValue = false;
		if (source instanceof JCheckBox)
			retValue = ((JCheckBox)source).isSelected();
		else if (source instanceof JRadioButton)
			retValue = ((JRadioButton)source).isSelected();
		//else
		//	Log.error("VBOMDrop.isSelectionSelected - not valid - " + source);
		return retValue;
	}	//	isSelected


	/**************************************************************************
	 * 	Save Selection
	 */
	private boolean cmd_save()
	{
		// guarda la lista de lineas seleccionadas en la base de datos
		
		if(m_AD_PInstance_ID==0)
		{
			// no se ha pasado la instancia del proceso
			return false;
		}
		
		StringBuffer sql=new StringBuffer(	"INSERT INTO C_Process_Selection ")
									.append("(Key_ID, Created, AD_PInstance_ID) ")
									.append("VALUES ")
									.append("(");
		StringBuffer sqlc=new StringBuffer(", CURRENT_TIMESTAMP, ").append(m_AD_PInstance_ID).append(")");
		
		//try
		//{
			//java.sql.PreparedStatement pstmt=DB..prepareStatement(sql.toString());
					
			int slen=m_selectionList.size();
			for(int i=0;i<slen;i++)
			{
				JCheckBox cb=(JCheckBox)m_selectionList.get(i);
			
				boolean selected = isSelectionSelected(cb);
				if(selected==true)
				{
					Integer key=(Integer)m_lineList.get(i);
					//pstmt.setInt(1, key.intValue());
					//int no=pstmt.executeUpdate();
					int no=DB.executeUpdate(sql.toString() + key.toString() + sqlc.toString(), null);
					if(no!=1)
					{
						// no se ha guardado la seleccion
						return false;
					}
				}
			}
		//}
		
		// generar el pedido
		MRepairOrder o=new MRepairOrder(Env.getCtx(), m_C_Repair_Order_ID, null);
		return o.generateOrder(m_C_DocTypeTarget_ID, false, false, m_AD_PInstance_ID, null);
	}	//	cmd_save

}	//	VBOMDropOrder
