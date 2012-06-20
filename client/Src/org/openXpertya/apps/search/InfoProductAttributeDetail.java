package org.openXpertya.apps.search;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

@SuppressWarnings("serial")
public class InfoProductAttributeDetail extends CDialog {

	protected CLogger log = CLogger.getCLogger( getClass());
	
	//
	
	protected CLabel lblTitle = new CLabel("Hola");
	
	protected JScrollPane scrollPane1 = new JScrollPane();
	
	protected JTable table1 = new JTable();
	
	protected CButton cmdClose = new CButton(Msg.parseTranslation(Env.getCtx(), "@Close@"));
	
	protected CButton cmdRefresh = new CButton(Msg.parseTranslation(Env.getCtx(), "@Refresh@"));
	
	//
	
	protected int M_Product_ID;
	
	protected MProduct product;
	
	protected int M_Warehouse_ID;
	
	//
	
	InfoProductAttributeDetail(java.awt.Dialog owner, int M_Product_ID, int M_Warehouse_ID) {
		super(owner, Msg.parseTranslation(Env.getCtx(), "@Detail@"), true);
		
		//
		
		this.M_Product_ID = M_Product_ID;
		this.M_Warehouse_ID = M_Warehouse_ID;
		product = new MProduct(Env.getCtx(), M_Product_ID, null);
		
		//
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		jbInit();

		refreshData();
		
		AEnv.positionCenterWindow(owner, this);
	}
	
	private void jbInit() {
		Container c = getContentPane();
		
		c.setLayout(new BorderLayout());
		
		//
		
		scrollPane1.setViewportView(table1);
		// scrollPane1.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		
		//
		
		DefaultTableModel tm = new DefaultTableModel();
		Vector<String> columnas = new Vector<String>();
		
		columnas.add("Instancia");
		columnas.add("Stock");
		tm.setColumnIdentifiers(columnas);
		
		table1.setModel(tm);
		table1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		TableColumn col = table1.getColumnModel().getColumn(0);
		
		col.setPreferredWidth((int)(col.getWidth() * 2.8));
		
		//
		
		lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
		lblTitle.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		//
		
		cmdClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		cmdClose.setIcon(org.openXpertya.images.ImageFactory.getImageIcon("Cancel24.gif"));
		
		//
		
		cmdRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				refreshData();
			}
		});
		
		cmdRefresh.setIcon(org.openXpertya.images.ImageFactory.getImageIcon("Refresh24.gif"));
		
		//
		
		CPanel panel1 = new CPanel(new BorderLayout());
		
		panel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		panel1.add(cmdRefresh, BorderLayout.LINE_START);
		panel1.add(cmdClose, BorderLayout.LINE_END);
		
		c.add(lblTitle, BorderLayout.NORTH);
		c.add(scrollPane1, BorderLayout.CENTER);
		c.add(panel1, BorderLayout.SOUTH);
		
		//
		
		setMinimumSize(new Dimension(cmdRefresh.getPreferredSize().width + cmdClose.getPreferredSize().width + 20, 400 ));
		setPreferredSize(new Dimension(300, 400));
		
		pack();
	}

	private Vector<Vector<Object>> getItems() {
		Vector<Vector<Object>> items = new Vector<Vector<Object>>();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		StringBuffer sql = new StringBuffer();
		
		try {
			Map<Integer, String> atributos = InfoProductAttribute.getAttributes(this.M_Product_ID);
			
			int M_Attribute_ID = atributos.keySet().iterator().next();
			
			sql.append(" SELECT ");
			
			for (int w = 1; w <= atributos.size(); w++) 
				sql.append(" mav" + w + "_name, ");
			
			sql.append(" COALESCE(SUM(sub1.qty), 0.0) AS SumCant, MIN(sub1.M_ATTRIBUTESETINSTANCE_ID) ");
			sql.append( InfoProductAttribute.getAttrMatrixItemsSql(M_Attribute_ID, this.M_Product_ID, this.M_Warehouse_ID, atributos) );
			sql.append( " HAVING SUM(sub1.qty) > 0.0 " );
			
			sql.append( " ORDER BY " );
			for (int w = 1; w <= atributos.size(); w++) {
				if (w > 1) sql.append(", ");
				sql.append(" ma" + w + "_seqno asc, mav" + w + "_seqno asc ");
			}
			
			ps = DB.prepareStatement(sql.toString(), null);
			
			int pn = 1;
			
			// prod attr war prod 
			
			ps.setInt(pn++, M_Product_ID);
			
			for (Integer x : atributos.keySet())
				ps.setInt(pn++, x);
			
			if (M_Warehouse_ID != 0)
				ps.setInt(pn++, M_Warehouse_ID);
			
			ps.setInt(pn++, M_Product_ID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				Vector<Object> v = new Vector<Object>();
				StringBuffer nombre = new StringBuffer();
				
				int w = 1;
				
				for (; w <= atributos.size(); w++) 
					nombre.append(rs.getString(w) + " - ");
				
				nombre.append(rs.getString(w + 1));
				
				v.add(nombre.toString());
				v.add(rs.getBigDecimal(w).setScale(2));
				
				items.add(v);
			}
			
		} catch (SQLException e) {
			log.log(Level.SEVERE, "", e);
		} finally {
			try {
				if (ps != null) ps.close();
			} catch (SQLException e2) {}
		}
		
		return items;
	}
	
	protected void refreshData() {
		String str = product.getName();
		
		if (product.getDescription() != null)
			str = str + " - " + product.getDescription();
		
		lblTitle.setTextDirect(str);
		
		Vector<Vector<Object>> items = getItems(); 
		
		DefaultTableModel tm = (DefaultTableModel)table1.getModel();
		
		tm.setRowCount(0);
		
		for (Vector<Object> v : items)
			tm.addRow(v);
	}
}
