package org.openXpertya.apps.search;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.form.VPriceInstanceMatrix;
import org.openXpertya.minigrid.IDColumn;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

@SuppressWarnings("serial")
public class InfoProductAttribute extends InfoProduct {

	protected static CLogger s_log = CLogger.getCLogger(InfoProductAttribute.class);
	
	protected JSplitPane splitPane ;
	
	protected JScrollPane attrScrollPane ;
	
	protected JTable attrTable ;
	
	protected CLabel lblDetail ;
	
	protected CButton btnDetail ;
	
	protected CButton btnPricesDetail ;
	
	protected int INDEX_QTY ;
	
	protected int INDEX_INSTANCE;
	
	protected int INDEX_PRICELIST;
	
	public InfoProductAttribute(Frame frame, boolean modal, int WindowNo, int M_Warehouse_ID, int M_PriceList_ID, String value, boolean multiSelection, String whereClause) {
		super(frame, modal, WindowNo, M_Warehouse_ID, M_PriceList_ID, value, multiSelection, whereClause);

		splitPane.setDividerLocation(0.5);
	}

	
	@Override
	protected Info_Column[] getProductLayout() {
		// return super.getProductLayout();
		if(validatePriceList==true){
		Info_Column[] s_productLayout = {
		        new Info_Column( " ","DISTINCT(p.M_Product_ID)",IDColumn.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"Discontinued" ).substring( 0,1 ),"p.Discontinued",Boolean.class ),
		        new Info_Column( Msg.translate( Env.getCtx(),"Value" ),"p.Value",String.class ),
		        new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"p.Name",String.class ),
		        new Info_Column( Msg.translate( Env.getCtx(),"QtyAvailable" ),"bomQtyAvailable(p.M_Product_ID,?,0) AS QtyAvailable",Double.class,true,true,null ),
		        new Info_Column( Msg.translate( Env.getCtx(),"PriceList" ),"bomPriceList(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceList",BigDecimal.class ),
		        // Added by Lucas Hernandez - Kunan
		        // new Info_Column( Msg.translate( Env.getCtx(),"Instance" ),"pui.M_AttributeSetInstance_ID",Integer.class ),
		        new Info_Column( Msg.translate( Env.getCtx(),"PriceListVersion" ),"pr.M_PriceList_Version_ID",Integer.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"PriceStd" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd",BigDecimal.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOnHand" ),"bomQtyOnHand(p.M_Product_ID,?,0) AS QtyOnHand",Double.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"QtyReserved" ),"bomQtyReserved(p.M_Product_ID,?,0) AS QtyReserved",Double.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOrdered" ),"bomQtyOrdered(p.M_Product_ID,?,0) AS QtyOrdered",Double.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"Unconfirmed" ),"(SELECT SUM(c.TargetQty) FROM M_InOutLineConfirm c INNER JOIN M_InOutLine il ON (c.M_InOutLine_ID=il.M_InOutLine_ID) INNER JOIN M_InOut i ON (il.M_InOut_ID=i.M_InOut_ID) WHERE c.Processed='N' AND i.M_Warehouse_ID=? AND il.M_Product_ID=p.M_Product_ID) AS Unconfirmed",Double.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"Margin" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"PriceLimit" ),"bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit",BigDecimal.class ),
		        //new Info_Column( Msg.translate( Env.getCtx(),"IsInstanceAttribute" ),"pa.IsInstanceAttribute",Boolean.class )
		    	};
		
		INDEX_NAME = 3;
		INDEX_QTY = 3;
		INDEX_INSTANCE = 5;
		INDEX_PRICELIST = 5;
		INDEX_PATTRIBUTE = 1;
		
		return s_productLayout;
		}
		else{
			Info_Column[] s_productLayout = {
			        new Info_Column( " ","DISTINCT(p.M_Product_ID)",IDColumn.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"Discontinued" ).substring( 0,1 ),"p.Discontinued",Boolean.class ),
			        new Info_Column( Msg.translate( Env.getCtx(),"Value" ),"p.Value",String.class ),
			        new Info_Column( Msg.translate( Env.getCtx(),"Name" ),"p.Name",String.class ),
			        new Info_Column( Msg.translate( Env.getCtx(),"QtyAvailable" ),"bomQtyAvailable(p.M_Product_ID,?,0) AS QtyAvailable",Double.class,true,true,null ),
			        new Info_Column( Msg.translate( Env.getCtx(),"Instance" ),"pui.M_AttributeSetInstance_ID",Integer.class ),
			        new Info_Column( Msg.translate( Env.getCtx(),"PriceListVersion" ),"pr.M_PriceList_Version_ID",Integer.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"PriceStd" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd",BigDecimal.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOnHand" ),"bomQtyOnHand(p.M_Product_ID,?,0) AS QtyOnHand",Double.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"QtyReserved" ),"bomQtyReserved(p.M_Product_ID,?,0) AS QtyReserved",Double.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"QtyOrdered" ),"bomQtyOrdered(p.M_Product_ID,?,0) AS QtyOrdered",Double.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"Unconfirmed" ),"(SELECT SUM(c.TargetQty) FROM M_InOutLineConfirm c INNER JOIN M_InOutLine il ON (c.M_InOutLine_ID=il.M_InOutLine_ID) INNER JOIN M_InOut i ON (il.M_InOut_ID=i.M_InOut_ID) WHERE c.Processed='N' AND i.M_Warehouse_ID=? AND il.M_Product_ID=p.M_Product_ID) AS Unconfirmed",Double.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"Margin" ),"bomPriceStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin",BigDecimal.class ),new Info_Column( Msg.translate( Env.getCtx(),"PriceLimit" ),"bomPriceLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit",BigDecimal.class ),
			        //new Info_Column( Msg.translate( Env.getCtx(),"IsInstanceAttribute" ),"pa.IsInstanceAttribute",Boolean.class )
			    	};
			
			INDEX_NAME = 3;
			INDEX_QTY = 3;
			INDEX_INSTANCE = 4;
			INDEX_PRICELIST = 5;
			INDEX_PATTRIBUTE = 1;		
			return s_productLayout;
		}
	}
	
	
	@Override
	protected JComponent getCenterComponent() {
		if (splitPane == null) {
			CPanel panel1 = new CPanel(new BorderLayout());
			CPanel panel2 = new CPanel(new BorderLayout());
			
			panel1.add(attrScrollPane, BorderLayout.CENTER);
			panel1.add(panel2, BorderLayout.PAGE_END);
			
			lblDetail.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0), BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
			lblDetail.setHorizontalAlignment(SwingConstants.LEFT);
			
			panel2.add(lblDetail, BorderLayout.PAGE_START);
			panel2.add(btnDetail, BorderLayout.CENTER);
			panel2.add(btnPricesDetail, BorderLayout.PAGE_END);
			
			
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, super.getCenterComponent(), panel1);
		}
		
		return splitPane;
	}
	
	@Override
	protected void jbInit() throws Exception {
		
		attrScrollPane = new JScrollPane();
		
		attrTable = new JTable();
		
		lblDetail = new CLabel(" ");
		
		btnDetail = new CButton(Msg.parseTranslation(Env.getCtx(), "@ProductDetails@"));
		
		btnPricesDetail = new CButton(Msg.parseTranslation(Env.getCtx(), "@ProductPrices@"));

		//
		
		attrScrollPane.setViewportView(attrTable);
		
		//
		
		attrTable.setModel(new ProductAttrTableModel());
		attrTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		attrTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		attrTable.setDefaultRenderer(AttrMatrixItem.class, new AttrMatrixItemRenderer());
		// Added by Lucas Hernandez - Kunan
		attrTable.setColumnSelectionAllowed(true);
		
		attrTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (arg0.getClickCount() >= 2) {
					dispose(true);
				}
			}
		});
		
		//
		
		btnDetail.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onDetailCmd();
			}
		});
	
		btnPricesDetail.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onPricesDetailCmd();
			}
		});
		
		
		//
		
		
		super.jbInit();
	}
	
	protected void onDetailCmd() {
		Integer x = getSelectedRowKey();
		if (x != null) {
			new InfoProductAttributeDetail(this, x, getWarehouseID()).setVisible(true);
		}
	}
	
	protected void onPricesDetailCmd() {
		Integer x = getSelectedRowKey();
		if (x != null) {
			// visualizar la ventana de precios como solo lectura
//			int priceListVersionID = getPickPriceList().getSelectedItem(); //Env.getContextAsInt(Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_PriceList_Version_ID");
			//VPriceInstanceMatrix ff = new VPriceInstanceMatrix(p_WindowNo, (Frame)this.getRootPane().getParent().getParent(), getSelectedRowKey(), getSelectedPriceListVersionID(), true);
			VPriceInstanceMatrix ff = new VPriceInstanceMatrix(p_WindowNo, (Frame)this.getRootPane().getParent().getParent(), getSelectedRowKey(), getListVersionID(), true);
        	AEnv.showCenterScreen( ff );
		}
	}
	
	@Override
	void saveSelectionDetail() {
		super.saveSelectionDetail();
		
		if (attrTable.getSelectedRowCount() > 0 && attrTable.getSelectedColumnCount() > 0) {
			int r = attrTable.getSelectedRow();
			int c = attrTable.getSelectedColumn();
			
			if (c > 0) {
				// Por defecto es 0.
				
				// La primer columna ("0") es el nombre del atributo
				AttrMatrixItem item = (AttrMatrixItem)attrTable.getModel().getValueAt(r, c);
				
				/*
				 * Added by Matías Cap - Disytel
				 * 
				 * Si se selecciona una instancia del conjunto de atributos de la tabla de la derecha 
				 * que no tiene instancia en sí en la base de datos, se tira un mensaje para informar. 
				 *  
				 */
				
				if(item.getM_AttributeSetInstance_ID() <= 0){
					ADialog.error(this.p_WindowNo,this,"NotInstanceForValues");									
				}
				
				/*
				 * Fin adición Matías Cap - Disytel
				 */
				
				m_M_AttributeSetInstance_ID = item.getM_AttributeSetInstance_ID();
				
				Env.setContext( Env.getCtx(),Env.WINDOW_INFO,Env.TAB_INFO,"M_AttributeSetInstance_ID",String.valueOf( item.getM_AttributeSetInstance_ID() ));
				Env.setContext(Env.getCtx(), "M_AttributeSetInstance_ID", m_M_AttributeSetInstance_ID);
				
				return;
			}
		}
		
		m_M_AttributeSetInstance_ID = 0;
	}
	
	@Override
	protected void enableButtons() {
		super.enableButtons();
		
		if (m_PAttributeButton != null) 
			m_PAttributeButton.setEnabled( false );
		
		if (m_InfoPAttributeButton != null)
			m_InfoPAttributeButton.setEnabled(false);
	}
	
	@Override
	protected String getSqlOrderBy() {
		return " Value ASC " ;
	}
	
	// Added by Lucas Hernandez - Kunan	
	public static BigDecimal getPriceValue(int M_Product_ID, int M_PriceList_Version_ID) {
		BigDecimal price = BigDecimal.ZERO;
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement("SELECT pp.pricestd FROM libertya.m_product p JOIN libertya.m_productprice pp ON p.m_product_id=pp.m_product_id WHERE p.m_product_id= ? AND pp.m_pricelist_version_id = ? ", null);
			
			int pn = 1;			
			ps.setInt(pn++, M_Product_ID);
			ps.setInt(pn++, M_PriceList_Version_ID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				price=rs.getBigDecimal(1);
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}
		
		return price;
	}
	
	// Added by Lucas Hernandez - Kunan
	private int validateDiffPrices(int M_PriceList_Version_ID, int M_Product_ID) {
		int instance=0;
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement("SELECT COUNT(*) FROM M_ProductPriceInstance WHERE M_PriceList_Version_ID = ? AND M_Product_ID = ?", null);
			
			int pn = 1;
			ps.setInt(pn++, M_PriceList_Version_ID);
			ps.setInt(pn++, M_Product_ID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				instance=rs.getInt(1);
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}
		
		return instance;
	}
		
	// Added by Lucas Hernandez - Kunan
	private BigDecimal getPriceInstance(int M_PriceList_Version_ID, int M_Product_ID,int M_AttributeSetInstance_ID) {
		BigDecimal instance=BigDecimal.ZERO;
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement("SELECT pricestd FROM M_ProductPriceInstance WHERE M_PriceList_Version_ID = ? AND M_Product_ID = ? AND M_AttributeSetInstance_ID = ? ", null);
			
			int pn = 1;
			ps.setInt(pn++, M_PriceList_Version_ID);
			ps.setInt(pn++, M_Product_ID);
			ps.setInt(pn++, M_AttributeSetInstance_ID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				instance=rs.getBigDecimal(1);
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}
		
		return instance;
	}	
	
	public static Vector<String> getAttrValues(int M_Attribute_ID) {
		Vector<String> ret = new Vector<String>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = DB.prepareStatement(" SELECT name FROM m_attributevalue WHERE m_attribute_id = ? ORDER BY seqno ", null);
			
			ps.setInt(1, M_Attribute_ID);
			
			rs = ps.executeQuery();
			
			while (rs.next())
				ret.add(rs.getString(1));
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e2) {}
		}
		
		return ret;
	}
	
	public static List<Integer> getAttrInstanceValues(int M_AttributeSetInstance_ID) {
		List<Integer> ret = new ArrayList<Integer>(5);
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement("select m_attributevalue_id from m_attributeinstance where m_attributesetinstance_id = ? ", null);
			
			int pn = 1;
			ps.setInt(pn++, M_AttributeSetInstance_ID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) 
				ret.add(rs.getInt(1));
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}
		
		return ret;
	}
	
	public static String getAttrMatrixItemsSqlFull(int M_Attribute_ID, int M_Product_ID, int M_Warehouse_ID, Map<Integer, String> atributos) {
		StringBuffer sql = new StringBuffer();
		int w;
		
		sql.append(" SELECT SUM(COALESCE(sub1.qty, 0.0)) AS SumCant, MIN(sub1.M_ATTRIBUTESETINSTANCE_ID) ");

		sql.append( getAttrMatrixItemsSql(M_Attribute_ID, M_Product_ID, M_Warehouse_ID, atributos) );
		
		sql.append(" order by ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" ma" + w + "_seqno asc, mav" + w + "_seqno asc, ");
		}
		
		sql.append(" ma1_seqno asc, mav1_seqno asc ");
		
		return sql.toString();
	}
	
	private Vector<Vector<Object>> getAttrMatrixItems(int M_Attribute_ID, int M_Product_ID, int M_Warehouse_ID, Map<Integer, String> atributos) {
		Vector<Vector<Object>> ret = new Vector<Vector<Object>>();
		int columnsCount = DB.getSQLValue(null, "select count(*) from m_attributevalue where m_attribute_id = ?", M_Attribute_ID);
		
		int w;
		// Added by Lucas Hernandez - Kunan
		//int priceList=getSelectedPriceListVersionID();
		int priceList=getListVersionID();
		
		String sql = null;
		
		sql = getAttrMatrixItemsSqlFull(M_Attribute_ID, M_Product_ID, M_Warehouse_ID, atributos);
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = DB.prepareStatement(sql.toString(), null);
			int pn = 1;
			
			ps.setInt(pn++, M_Product_ID);
			// ps.setInt(pn++, M_Attribute_ID);
			
			for (Integer x : atributos.keySet())
				ps.setInt(pn++, x);
			
			if (M_Warehouse_ID != 0)
				ps.setInt(pn++, M_Warehouse_ID);
			
			ps.setInt(pn++, M_Product_ID);
			
			rs = ps.executeQuery();
			
			boolean go = rs.next();
			
			while (go) {
				int i;
				Vector<Object> x = new Vector<Object>();  
				for (i = 0; go && i < columnsCount; i++) {
					BigDecimal b = rs.getBigDecimal(1);
					
					b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
					// Added by Lucas Hernandez - Kunan
					BigDecimal priceInstance=BigDecimal.ZERO;
					if(validatePriceList==true){
						priceInstance=getPriceInstance(priceList,M_Product_ID,rs.getInt(2));
					}
					x.add(new AttrMatrixItem(b, rs.getInt(2),priceInstance));
					
					go = rs.next();
				}
				ret.add(x);
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e2) {}
		}
		
		return ret;
	}
	
	public static Map<Integer, String> getAttributes(int M_Product_ID) {
		LinkedHashMap<Integer, String> hm = new LinkedHashMap<Integer, String>();
		
		String sql = "select ma.m_attribute_id, ma.name " +
			" from m_product " +
			" inner join m_attributeset as mas on (m_product.m_attributeset_id = mas.m_attributeset_id) " +
			" inner join m_attributeuse as mau on (mas.m_attributeset_id = mau.m_attributeset_id)  " +
			" inner join m_attribute as ma on (mau.m_attribute_id = ma.m_attribute_id) " +
			" where m_product.m_product_id = ? AND ma.isinstanceattribute = 'Y' " +
			" order by mau.seqno asc ";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = DB.prepareStatement(sql, null);
			
			ps.setInt(1, M_Product_ID);
			
			rs = ps.executeQuery();
			
			while (rs.next())
				hm.put(rs.getInt(1), rs.getString(2));
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e2) {}
		}
		
		return hm;
	}
	
	public static String getAttrMatrixItemsSql(int M_Attribute_ID, int M_Product_ID, int M_Warehouse_ID, Map<Integer, String> atributos) {
		int w;
		
		StringBuffer sql = new StringBuffer();
		
		sql.append(" FROM ( ");
		sql.append( getAttrMatrixBasicSql(M_Attribute_ID, M_Product_ID, atributos) );
		sql.append(" ) AS sub2 ");
		
		sql.append(" ");
		sql.append(" "); 
		sql.append(" LEFT JOIN (SELECT XST.M_ATTRIBUTESETINSTANCE_ID, (XST.QTYONHAND - XST.QTYRESERVED) as qty, XMAI.M_ATTRIBUTEVALUE_ID as XMAI_M_ATTRIBUTEVALUE_ID ");

		for (w = 2; w <= atributos.size(); w++) {
			sql.append(", XMAI" + w + ".M_ATTRIBUTEVALUE_ID as XMAI" + w + "_M_ATTRIBUTEVALUE_ID ");
		}
		
		sql.append("                    FROM   M_STORAGE XST ");
		
		if (M_Warehouse_ID != 0) {
			sql.append(", M_Locator XLOC ");
			/* sql.append("                           INNER JOIN M_Locator XLOC ON (XLOC.M_Locator_ID = XST.M_Locator_ID AND XLOC.M_Warehouse_ID = ?) "); */
		}
		
		sql.append(", M_ATTRIBUTESETINSTANCE XMASI ");
		sql.append(", M_ATTRIBUTEINSTANCE XMAI ");
		
		/*
		sql.append("                           INNER JOIN M_ATTRIBUTESETINSTANCE XMASI ");
		sql.append("                             ON (XST.M_ATTRIBUTESETINSTANCE_ID = XMASI.M_ATTRIBUTESETINSTANCE_ID) ");
		sql.append("                           INNER JOIN M_ATTRIBUTEINSTANCE XMAI ");
		sql.append("                             ON (XMASI.M_ATTRIBUTESETINSTANCE_ID = XMAI.M_ATTRIBUTESETINSTANCE_ID) ");
		sql.append("                           ");
		*/
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(", M_ATTRIBUTEINSTANCE XMAI" + w + " ");
			/*
			sql.append("                           INNER JOIN M_ATTRIBUTEINSTANCE XMAI" + w + " ");
			sql.append(" ON (XMASI.M_ATTRIBUTESETINSTANCE_ID = XMAI" + w + ".M_ATTRIBUTESETINSTANCE_ID) ");
			*/
		}
		
		sql.append(" ");
		sql.append(" WHERE  ");
		
		if (M_Warehouse_ID != 0) {
			sql.append(" (XLOC.M_Locator_ID = XST.M_Locator_ID AND XLOC.M_Warehouse_ID = ?) AND ");
		}
		
		sql.append(" (XST.M_PRODUCT_ID = ?) ");
		
		sql.append(" AND (XST.M_ATTRIBUTESETINSTANCE_ID = XMASI.M_ATTRIBUTESETINSTANCE_ID) ");
		sql.append(" AND (XMASI.M_ATTRIBUTESETINSTANCE_ID = XMAI.M_ATTRIBUTESETINSTANCE_ID) ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" AND (XMASI.M_ATTRIBUTESETINSTANCE_ID = XMAI" + w + ".M_ATTRIBUTESETINSTANCE_ID) ");
		}
		
		sql.append("");
		sql.append("");
		sql.append("");
		
		sql.append(" ) as sub1 ");
		sql.append("             ON (XMAI_M_ATTRIBUTEVALUE_ID = MAV1_ID ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" AND XMAI" + w + "_M_ATTRIBUTEVALUE_ID = MAV" + w + "_ID ");
		}
		
		sql.append(") ");
		
		sql.append(" group by ");
		
		for (w = 1; w <= atributos.size(); w++) {
			if (w > 1) sql.append(",");
			sql.append(" ma" + w + "_id, mav" + w + "_id, ");
			sql.append(" ma" + w + "_seqno, mav" + w + "_seqno, ");
			sql.append(" ma" + w + "_name, mav" + w + "_name ");
		}
		
		// sql.append(" ma1_id, mav1_id, ma1_seqno, mav1_seqno  ");
		sql.append(" ");
		
		return sql.toString();
	}
	
	public static String getAttrMatrixBasicSql(int M_Attribute_ID, int M_Product_ID, Map<Integer, String> atributos) {
		StringBuffer sql = new StringBuffer();
		int w;
		
		sql.append(" SELECT ");
		
		for (w = 1; w <= atributos.size(); w++) {
			if (w > 1) 
				sql.append(",");
		
				sql.append(" ma" + w + ".name as ma" + w + "_name ");
				sql.append(", ma" + w + ".M_ATTRIBUTE_ID as ma" + w + "_id ");
				sql.append(", mau" + w + ".seqno as ma" + w + "_seqno ");
				
				sql.append(", mav" + w + ".name as mav" + w + "_name ");
				sql.append(", mav" + w + ".M_ATTRIBUTEVALUE_ID as mav" + w + "_id ");
				sql.append(", mav" + w + ".seqno as mav" + w + "_seqno "); // FIXME: Falta el campo SeqNo en la tabla!
		}
		
		sql.append(" from m_attributeset as mas  ");

		sql.append(" , m_product as mpr ");
		sql.append(" , m_attributeset maspr ");
		sql.append(" ");
		sql.append(" , m_attributeuse as mau1 ");
		sql.append(" , m_attribute as ma1 ");
		sql.append(" , m_attributevalue as mav1 ");
		sql.append(" ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" , m_attributeuse as mau" + w + " "); 
			sql.append(" , m_attribute as ma" + w + " "); 
			sql.append(" , m_attributevalue as mav" + w + " ");

		}
		
		sql.append(" WHERE (mpr.m_product_id = ?) ");
		sql.append(" AND (mpr.m_attributeset_id = maspr.m_attributeset_id and mas.m_attributeset_id = maspr.m_attributeset_id) ");
		sql.append(" AND (mas.m_attributeset_id = mau1.m_attributeset_id) ");
		sql.append(" AND (mau1.m_attribute_id = ma1.m_attribute_id AND ma1.m_attribute_id = ? AND ma1.isinstanceattribute = 'Y') ");
		sql.append(" AND (ma1.m_attribute_id = mav1.m_attribute_id) ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" AND (mas.m_attributeset_id = mau" + w + ".m_attributeset_id) ");
			sql.append(" AND (mau" + w + ".m_attribute_id = ma" + w + ".m_attribute_id and ma" + w + ".m_attribute_id = ? AND ma1.isinstanceattribute = 'Y') ");
			sql.append(" AND (ma" + w + ".m_attribute_id = mav" + w + ".m_attribute_id) ");
		}
		
		
		// sql.append(" ");
		
		
		/*
		sql.append(" inner join m_product as mpr on (mpr.m_product_id = ?) ");
		sql.append(" inner join m_attributeset maspr on (mpr.m_attributeset_id = maspr.m_attributeset_id and mas.m_attributeset_id = maspr.m_attributeset_id) ");
		sql.append(" ");
		sql.append(" inner join m_attributeuse as mau1 on (mas.m_attributeset_id = mau1.m_attributeset_id) ");
		sql.append(" inner join m_attribute as ma1 on (mau1.m_attribute_id = ma1.m_attribute_id AND ma1.m_attribute_id = ? AND ma1.isinstanceattribute = 'Y')  ");
		sql.append(" inner join m_attributevalue as mav1 on (ma1.m_attribute_id = mav1.m_attribute_id)  ");
		sql.append(" ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" inner join m_attributeuse as mau" + w + " on (mas.m_attributeset_id = mau" + w + ".m_attributeset_id) "); 
			sql.append(" inner join m_attribute as ma" + w + " on (mau" + w + ".m_attribute_id = ma" + w + ".m_attribute_id and ma" + w + ".m_attribute_id = ? AND ma1.isinstanceattribute = 'Y') "); 
			sql.append(" inner join m_attributevalue as mav" + w + " on (ma" + w + ".m_attribute_id = mav" + w + ".m_attribute_id)  ");
		}
		*/
		
		sql.append(" ");
		
		return sql.toString();
	}
	
	public static String getAttrMatrixRowsLabelsSql(int M_Attribute_ID, int M_Product_ID, Map<Integer, String> atributos) {
		StringBuffer sql = new StringBuffer();
		int w;
		
		sql.append(" SELECT ");
		
		for (w = 2; w <= atributos.size(); w++) {
			if (w > 2) 
				sql.append(",");
			sql.append(" MAV" + w + "_id ");
			sql.append(", MAV" + w + "_name ");
		}
		
		sql.append(" FROM ( ");
		
		// HERE IT COMES, THE MAGIC
		sql.append( getAttrMatrixBasicSql(M_Attribute_ID, M_Product_ID, atributos) );
		
		sql.append(" ) as sub1 ");
		sql.append(" GROUP BY  ");
		
		for (w = 2; w <= atributos.size(); w++) {
			if (w > 2) 
				sql.append(",");
			sql.append(" ma" + w + "_id, mav" + w + "_name, mav" + w + "_id ");
			sql.append(", ma" + w + "_seqno, mav" + w + "_seqno ");
		}
		
		sql.append(" ORDER BY  ");
		
		for (w = 2; w <= atributos.size(); w++) {
			if (w > 2) 
				sql.append(",");
			sql.append(" ma" + w + "_seqno, mav" + w + "_seqno ");
		}
		
		return sql.toString();
	}
	
	public static Vector<String> getAttrMatrixRowsLabels(int M_Attribute_ID, int M_Product_ID, Map<Integer, String> atributos) {
		Vector<String> ret = new Vector<String>();
		
		String sql = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int w;
		
		sql = getAttrMatrixRowsLabelsSql(M_Attribute_ID, M_Product_ID, atributos);
		
		try {
			ps = DB.prepareStatement(sql.toString(), null);
			
			int pn = 1;
			ps.setInt(pn++, M_Product_ID);
			//ps.setInt(pn++, M_Attribute_ID);
			
			for (Integer x : atributos.keySet())
				ps.setInt(pn++, x);
			
			rs = ps.executeQuery();
			
			StringBuffer label = new StringBuffer();
			
			while (rs.next()) {
				label.setLength(0);
				
				pn = 2;
				
				for (w = 1; w <= atributos.size() - 1; w++) {
					if (w > 1) label.append(" - ");
					label.append(rs.getString(pn));
					
					pn += 2;
				}
				
				ret.add(label.toString());
			}
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e2) {}
		}
		
		return ret;
		
	}
	
	/**
	 * Determina si los atributos no son de tipo lista para colocar un mensaje en el la barra de estado
	 * @param M_Product_ID el id del producto a verificar
	 */
	private void determineAttribute(int M_Product_ID){
		/*
		 * Creo el script sql para determinar la cantidad de atributos de ese producto que no son de tipo lista
		 * -----------------------------------------------------------------------
		 * SELECT count(*) 
		 * FROM m_attributeuse as mau 
		 * INNER JOIN m_product as mp 
		 * ON (mp.m_product_id = M_Product_ID) and (mp.m_attributeset_id = mau.m_attributeset_id) 
		 * INNER JOIN m_attribute as ma 
		 * ON (ma.m_attribute_id = mau.m_attribute_id) 
		 * WHERE (attributevaluetype <> 'L')
		 * ------------------------------------------------------------------------
		 */
		String sql = "SELECT count(*) FROM m_attributeuse as mau INNER JOIN m_product as mp ON (mp.m_product_id = "+M_Product_ID+") and (mp.m_attributeset_id = mau.m_attributeset_id) INNER JOIN m_attribute as ma ON (ma.m_attribute_id = mau.m_attribute_id) WHERE (attributevaluetype <> 'L')";
		
		//Determino la cantidad de atributos que no son de tipo lista
		int nro = DB.getSQLValue(null, sql);
		
		//Si hay atributos de tipo lista
		if(nro > 0){
			String msg = Msg.getMsg(Env.getAD_Language(Env.getCtx()), "NotDisplayedNotListType"); 
			lblDetail.setTextDirect(msg);
			lblDetail.setForeground(CompierePLAF.getTextColor_Issue());
		}
	} 
	
	
	private synchronized void updateAttrTable() {
		ProductAttrTableModel tm = (ProductAttrTableModel)attrTable.getModel();
		Integer M_Product_ID = getSelectedRowKey();

        if (M_Product_ID == null) {
        	tm.setRowCount(0);
        	tm.setColumnCount(0);
            return;
        }
        
        /*
         * Added by Matías Cap - Disytel
         * 
         * Filtrar los artículos que son de tipo servicio.
         */
        
        //Obtengo el artículo de la base para saber si es un servicio
        StringBuffer sqlProd = new StringBuffer("select producttype from m_product where m_product_id = ?");
        
        PreparedStatement ps = null;
		ResultSet rs = null;
		
		try{
			ps = DB.prepareStatement(sqlProd.toString());
			//Setear el producto a la consulta
			ps.setInt(1, M_Product_ID);
			rs = ps.executeQuery();			
			if(rs.next()){
				//Si es un servicio, salgo
		        if(rs.getString(1).equalsIgnoreCase("S")){
		        	tm.setRowCount(0);
		        	tm.setColumnCount(0);
		            return;
		        }
			}
		} catch(Exception se){
			s_log.log(Level.SEVERE,"An error in sql",se);
		} finally{
			try{
				rs.close();
				ps.close();
			}catch(SQLException se){}
		}
        /*
         * Fin adición Matías Cap - Disytel
         */
		
        // Guardo los nombres de todos los atributos del conjunto:
        //  - Talle
        //  - Color
		Map<Integer, String> hm = getAttributes(M_Product_ID);
		
		if (hm.size() < 2) {
			// No attributes!!
			tm.setRowCount(0);
			tm.setColumnCount(0);
			return;
		}
		
		Iterator<Integer> it = hm.keySet().iterator();
		
		if (!it.hasNext()){
			tm.setRowCount(0);
			tm.setColumnCount(0);
			return;
		}
		
		// El atributo a mostrar en el encabezado
		int Attribute_ID = it.next();
		
		if (!it.hasNext()) {
			tm.setRowCount(0);
			tm.setColumnCount(0);
			return;
		}
		
		String primerOrden = hm.get(Attribute_ID);
		
		// Devuelve la lista de valores de ese atributo. Si el atributo es Color,
		// en el vector voy a tener: Amarillo, Verde, Gris.
		Vector<String> cols = getAttrValues(Attribute_ID);
		
		Vector<Vector<Object>> rows = getAttrMatrixItems(Attribute_ID, M_Product_ID, getWarehouseID(), hm);
		
		/*
		 * Adición por Matías Cap - Disytel
		 * 
		 * Si no encontró nada de items, no confecciona la tabla. Una de las causas es que los atributos 
		 * no sean de tipo lista, entonces no se devuelven atributos. Como consecuencia a esto último se 
		 * colocaba la primer columna (que existía, al ser atributo de tipo cadena o número), pero como 
		 * la query sólo toma de tipo lista, no existían valores, por lo que la tabla quedaba 
		 * sólo con la primer columna con el nombre del primer atributo.
		 * 
		 * ---------------------------------------------------------------------------
		 * 						Código anterior
		 * ---------------------------------------------------------------------------
		 * 
		 *  cols.insertElementAt("/" + primerOrden, 0);
		 *	
		 *	tm.setColumnIdentifiers(cols);
		 *	
		 *	tm.setRowCount(0);
		 *	
		 *	Vector<String> cols2 = getAttrMatrixRowsLabels(Attribute_ID, M_Product_ID, hm);
		 *	int i = 0;
		 *	
		 *	for (Vector<Object> r : rows) {
		 *		r.insertElementAt(cols2.get(i++ % cols2.size()), 0);
		 *		tm.addRow(r);
		 *	}
		 *	
		 *	tm.fireTableStructureChanged();
		 *
		 * ---------------------------------------------------------------------------
		 * 			Fin Código anterior
		 * --------------------------------------------------------------------------- 
		 * 
		 */
		
		if(rows.size() > 0){
			cols.insertElementAt("/" + primerOrden, 0);
			
			tm.setColumnIdentifiers(cols);
			
			tm.setRowCount(0);
			
			Vector<String> cols2 = getAttrMatrixRowsLabels(Attribute_ID, M_Product_ID, hm);
			int i = 0;
			
			for (Vector<Object> r : rows) {
				r.insertElementAt(cols2.get(i++ % cols2.size()), 0);
				tm.addRow(r);
			}		
		}
		else{
			//Refresco el model de la grilla por si había datos anteriores
			tm.setRowCount(0);
			tm.setColumnCount(0);
		}
		
		tm.fireTableStructureChanged();
		
		
		/*
		 * Fin modificación Matías Cap - Disytel
		 */
	}
	
	private void updateStatusLabel() {
		
		if (INDEX_QTY <= 0)
			return;
		
		int x = p_table.getSelectedRow();
		
		if (x == -1)
			return;
		
		BigDecimal cantTotal = BigDecimal.valueOf((Double)p_table.getModel().getValueAt(x, INDEX_QTY));
		BigDecimal cantTemp = BigDecimal.ZERO;
		
		int y;
		
		TableModel m = this.attrTable.getModel();
		
		int cols = m.getColumnCount();
		int rows = m.getRowCount();
		
		for (y = 0; y < rows; y++) {
			for (x = 1; x < cols; x++) {
				AttrMatrixItem item = (AttrMatrixItem)m.getValueAt(y, x);
				cantTemp = cantTemp.add(item.getQty());
			}
		}
		
		if (cantTotal.compareTo(cantTemp) > 0) {
			String msg = " ";
			try { // "Existen {0} unidades de este articulo sin catalogar."
				msg = MessageFormat.format(Msg.getMsg(Env.getCtx(), "ProductNotFullyInCatalog"), cantTotal.subtract(cantTemp));
			} catch (IllegalArgumentException e) {
				log.log(Level.SEVERE, "", e);
			}
			lblDetail.setTextDirect(msg);
			lblDetail.setForeground(CompierePLAF.getTextColor_Issue());
		} else {
			lblDetail.setTextDirect(" ");
			lblDetail.setForeground(CompierePLAF.getTextColor_Normal());
		}
		
		
		Integer product = getSelectedRowKey(); 
		
		determineAttribute(product.intValue());
		
		/*
		 * Added by Matías Cap - Disytel 
		 * 
		 * Setea a disabled el botón detalle para que no tire la excepción cuando no tiene atributos 
		 * o no tiene asignado una cantidad para esa instancia 
		 */
		if(cantTotal.equals(BigDecimal.ZERO) || cantTemp.equals(BigDecimal.ZERO)){
			btnDetail.setEnabled(false);
			btnPricesDetail.setEnabled(false);
		}
		else{
			btnDetail.setEnabled(true);
			btnPricesDetail.setEnabled(true);
		}
		/*
		 * Fin de la modificación Matías Cap - Disytel
		 */
	}
	
	// Added by Lucas Hernandez - Kunan
	protected void instanceFound(int msi){
		ProductAttrTableModel tm = (ProductAttrTableModel)attrTable.getModel();
		int cols = tm.getColumnCount();
		int rows = tm.getRowCount();
		int y,x;
		for (y = 0; y < rows; y++) {
			for (x = 1; x < cols; x++) {
				AttrMatrixItem item = (AttrMatrixItem)tm.getValueAt(y, x);
				if(item.getM_AttributeSetInstance_ID()==msi){
					attrTable.changeSelection(y, x, true,false);
				}
			}
		}
		
    }
	
	// Added by Lucas Hernandez - Kunan
	private void validateProductInstance(){
		if (INDEX_QTY <= 0)
			return;
		
		int x = p_table.getSelectedRow();
		
		if (x == -1)
			return;
		int pricelistversion = getListVersionID();
		int instance=validateDiffPrices(pricelistversion,getSelectedRowKey());		
		if(instance!=0&&validatePriceList==true){
			btnPricesDetail.setEnabled(true);			
		}
		else{
			btnPricesDetail.setEnabled(false);
		}
	}
	
	private int getListVersionID(){
		return ((Integer) p_table.getValueAt(p_table.getSelectedRow(),INDEX_PRICELIST)).intValue();		
	}
	
	protected int getWarehouseID() {
		int         M_Warehouse_ID = 0;
        KeyNamePair wh             = ( KeyNamePair )getPickWarehouse().getSelectedItem();

        if( wh != null ) {
            M_Warehouse_ID = wh.getKey();
        }
        return M_Warehouse_ID;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		
		if (e.getValueIsAdjusting())
			return;
	
		updateAttrTable();
		updateStatusLabel();
		// Added by Lucas Hernandez - Kunan
		validateProductInstance();
	}
	
	@SuppressWarnings("serial")
	private static class ProductAttrTableModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
		@Override
		public Class<?> getColumnClass(int arg0) {
			if (arg0 == 0)
				return String.class;
			return AttrMatrixItem.class;
		}
	}
	
	@SuppressWarnings("serial")
	private static class AttrMatrixItem {
		// Modified by Lucas Hernandez - Kunan
		public AttrMatrixItem(BigDecimal qty, int attributeSetInstance_ID,BigDecimal priceInstance) {
			setQty(qty);
			setM_AttributeSetInstance_ID(attributeSetInstance_ID);
			setPriceInstance(priceInstance);
		}
		
		private BigDecimal qty;
		private int M_AttributeSetInstance_ID;
		// Added by Lucas Hernandez - Kunan
		private BigDecimal priceInstance;
		
		/**
		 * @return the qty
		 */
		public BigDecimal getQty() {
			return qty;
		}
		/**
		 * @param qty the qty to set
		 */
		public void setQty(BigDecimal qty) {
			this.qty = qty;
		}
		/**
		 * @return the m_AttributeSetInstance_ID
		 */
		public int getM_AttributeSetInstance_ID() {
			return M_AttributeSetInstance_ID;
		}
		/**
		 * @param attributeSetInstance_ID the m_AttributeSetInstance_ID to set
		 */
		public void setM_AttributeSetInstance_ID(int attributeSetInstance_ID) {
			M_AttributeSetInstance_ID = attributeSetInstance_ID;
		}
		
		// Added by Lucas Hernandez - Kunan
		public BigDecimal getPriceInstance() {
			return priceInstance;
		}
		// Added by Lucas Hernandez - Kunan
		public void setPriceInstance(BigDecimal priceInstance) {
			this.priceInstance = priceInstance;
		}
		
		@Override
		public String toString() {
			return "[" + M_AttributeSetInstance_ID + ", " + qty + "]";
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class AttrMatrixItemRenderer extends DefaultTableCellRenderer {
	    public AttrMatrixItemRenderer() { super(); }

	    public void setValue(Object value) {
	    	if (value == null)
	    		value = BigDecimal.ZERO;
	    	
	    	BigDecimal qty = ((AttrMatrixItem)value).getQty();
	    	
	    	// Added by Lucas Hernandez - Kunan
	    	BigDecimal price = ((AttrMatrixItem)value).getPriceInstance();
	    	
	    	if(price.signum() > 0){
	    		setFont(getFont().deriveFont(Font.BOLD));
	    	}	
	    	
	    	if (qty.signum() < 1)
	    		setForeground(CompierePLAF.getTextColor_Issue());
	    	else
	    		setForeground(CompierePLAF.getTextColor_OK());
	    	
	    	setHorizontalTextPosition(SwingConstants.RIGHT);
	    	setHorizontalAlignment(SwingConstants.RIGHT);
	        setText( qty.toString() );
	    }
	}
	
	public int getAttributeSetInstanceID() {
		return m_M_AttributeSetInstance_ID;
	}
	
	public void queryExecute(){		
		try{
			p_table.changeSelection(0,0,false,true);
			int instance = ((Integer) p_table.getValueAt(0,INDEX_INSTANCE)).intValue();
			instanceFound(instance);
		}
		catch(NullPointerException ex){
			
		}
		catch(ArrayIndexOutOfBoundsException ex1){
			
		}
	}
}
