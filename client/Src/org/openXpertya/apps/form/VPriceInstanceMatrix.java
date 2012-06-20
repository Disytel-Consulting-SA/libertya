package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CButton;
import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.search.InfoProductAttribute;
import org.openXpertya.model.MAttributeInstance;
import org.openXpertya.model.MAttributeSetInstance;
import org.openXpertya.model.MProductPriceInstance;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;

@SuppressWarnings("serial")
public class VPriceInstanceMatrix extends CDialog {

	static public CLogger s_log = CLogger.getCLogger(VPriceInstanceMatrix.class);
	
	protected int m_srcWindowNo;
	
	protected MatrixTableModel tm;
	
	protected JScrollPane scrollPane;
	protected JTable table;
	
	protected CButton btnOk;
	protected CButton btnCancel;
	protected CButton btnReload;
	
	//
	

	protected int M_PriceList_Version_ID;
	protected int M_Product_ID;
	protected int M_Warehouse_ID;
	protected int M_AttributeSet_ID;
	 
	protected boolean m_readOnly = false;
	
	public VPriceInstanceMatrix( int srcWindowNo, Frame owner, Integer productID, Integer priceListVersionID, boolean readOnly) {
		super(owner, "Grilla de Precios", true);
		
		m_srcWindowNo = srcWindowNo;
        m_readOnly = readOnly;
        
        // incluir la alternativa de definicion de la lista de precio mediante parametro
		M_PriceList_Version_ID = Env.getContextAsInt(Env.getCtx(), srcWindowNo, "M_PriceList_Version_ID");
		if (priceListVersionID != null)
			M_PriceList_Version_ID = priceListVersionID;
		// priorizar el dato del producto recibido por parametro
		M_Product_ID = Env.getContextAsInt(Env.getCtx(), srcWindowNo, "M_Product_ID");
		if (productID != null)
			M_Product_ID = productID;
		M_Warehouse_ID = Env.getContextAsInt(Env.getCtx(), srcWindowNo, "M_Warehouse_ID");
		M_AttributeSet_ID = DB.getSQLValue(getTrxName(), "SELECT m_attributeset_id FROM M_Product WHERE M_Product_ID = ?", M_Product_ID);
		
        jbInit();
        refreshTableData();
        
        setDefaultCloseOperation(CDialog.DISPOSE_ON_CLOSE);
        
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        d.width = (int)(d.width * 0.75);
        d.height = (int)(d.height * 0.5);
        
        setPreferredSize(d);
	}
	
	private void jbInit() {
		
		setLayout(new BorderLayout());
		
		tm = new MatrixTableModel();
		table = new JTable(tm);
		scrollPane = new JScrollPane(table);
		btnCancel = new CButton(Msg.getMsg(Env.getCtx(), "Cancel"));
		btnOk = new CButton(Msg.getMsg(Env.getCtx(), "OK"));
		btnReload = new CButton(Msg.getMsg(Env.getCtx(), "Refresh"));
		
		MatrixCellEditor mce = new MatrixCellEditor();

		table.setDefaultRenderer(MatrixItem.class, new MatrixCellRenderer());
		table.setDefaultEditor(MatrixItem.class, mce);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		
		table.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				btnOk.setEnabled(!m_readOnly && !table.isEditing());
				table.setEnabled(!m_readOnly);
			}
			
			public void focusLost(FocusEvent e) {
				btnOk.setEnabled(!m_readOnly && !table.isEditing());
				table.setEnabled(!m_readOnly);
			}
		});
		
		DefaultTableModel tm = (DefaultTableModel)table.getModel();
		
		tm.addTableModelListener(new TableModelListener(){
			public void tableChanged(TableModelEvent e) {
				JTable t = table;
				TableModel tm = t.getModel();
				
				int rows = t.getRowCount();
				int cols = t.getColumnCount();
				
				if (rows < 1 || cols < 2)
					return;
				
				Object value = tm.getValueAt(0, 1);
				Component r = t.getCellRenderer(0, 1).getTableCellRendererComponent(t, value, false, false, 0, 1);
				
				// Actualiza el alto de las filas de la tabla en base a los Items
				t.setRowHeight(r.getMinimumSize().height + t.getRowMargin());
			}
		});
		
		table.revalidate();

		GridLayout gr = new GridLayout(1,3);
		CPanel panel1 = new CPanel(gr);

		gr.setHgap(5);
		gr.setVgap(5);
		
		btnOk.setIcon(org.openXpertya.images.ImageFactory.getImageIcon("Ok24.gif"));
		btnCancel.setIcon(org.openXpertya.images.ImageFactory.getImageIcon("Cancel24.gif"));
		btnReload.setIcon(org.openXpertya.images.ImageFactory.getImageIcon("Refresh24.gif"));
		
		btnOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onBtnOk();
			}
		});
		
		btnCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onBtnCancel();
			}
		});
		
		btnReload.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				refreshTableData();
			}
		});
		
		panel1.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		panel1.add(btnCancel);
		panel1.add(btnReload);
		panel1.add(btnOk);
		
		add(scrollPane, BorderLayout.CENTER);
		add(panel1, BorderLayout.SOUTH);
	}
	
	private void updateItemsWithCurrentLines(Vector<Vector<Object>> items, Map<Integer, String> hm) {
		
		List<MProductPriceInstance> precios = getCurrentPriceList();
		
		for (MProductPriceInstance p : precios) {
			Set<Integer> values = new HashSet<Integer>( InfoProductAttribute.getAttrInstanceValues(p.getM_AttributeSetInstance_ID()) );
			
			MatrixItem item = null;
			
			for (int row = 0; item == null && row < items.size(); row++) {
				for (int column = 0; item == null && column < items.get(row).size(); column++) {
					MatrixItem x = (MatrixItem)items.get(row).get(column);
					if (x.getValuesIds().equals(values))
						item = x;
				}
			}
			
			if ( item != null ) {
				item.setEnteredPrice(item.getEnteredPrice().add(p.getPriceStd()));
				item.setProductPriceInstance(p);
			}
		}
	}
	
	protected void refreshTableData() {
		DefaultTableModel tm = (DefaultTableModel)table.getModel();
		
		Map<Integer, String> hm = InfoProductAttribute.getAttributes(M_Product_ID);

		tm.setRowCount(0);
		tm.setColumnCount(0);
		
		if (hm.size() < 2)
			return;
		
		Iterator<Integer> it = hm.keySet().iterator();
		
		if (!it.hasNext())
			return;
		
		// El atributo a mostrar en el encabezado
		int Attribute_ID = it.next();
		
		// La lista de nombres de encabezados de columnas
		
		Vector<String> cols = InfoProductAttribute.getAttrValues(Attribute_ID);
		
		// La lista de nombres de encabezados de filas
		
		Vector<String> cols2 = InfoProductAttribute.getAttrMatrixRowsLabels(Attribute_ID, M_Product_ID, hm);
		
		String primerOrden = hm.get(Attribute_ID);
		cols.insertElementAt("/" + primerOrden, 0);
		
		tm.setColumnIdentifiers(cols);
		
		Vector<Vector<Object>> rows = getMatrixItems(Attribute_ID, hm);
		
		// Actualizo los elementos de la matriz con los valores ingresados en las lineas
		
		updateItemsWithCurrentLines(rows, hm);
		
		//
		
		int i = 0;
		
		for (Vector<Object> r : rows) {
			r.insertElementAt(cols2.get(i++ % cols2.size()), 0);
			tm.addRow(r);
		}
		
		tm.fireTableStructureChanged();
	}
	
	public static int getValuesCount(int M_Attribute_ID) {
		return DB.getSQLValue(null, "SELECT count(*) FROM m_attributevalue WHERE m_attribute_id = ?", M_Attribute_ID);
	}
	
	public static int getAttributeUseCount(int M_AttributeSet_ID) {
		return DB.getSQLValue(null, "SELECT COUNT(*) FROM m_attributeuse WHERE m_attributeset_id = ?", M_AttributeSet_ID);
	}
	
	private Vector<Vector<Object>> getMatrixItems(int M_Attribute_ID, Map<Integer, String> atributos) {
		Vector<Vector<Object>> ret = new Vector<Vector<Object>>();
		String sql = getAttrMatrixItemsSql(M_Attribute_ID, M_Product_ID, M_Warehouse_ID, atributos);
		int columnsCount = getValuesCount(M_Attribute_ID);
		
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement(sql, null);
			
			//
			
			int pn = 1;
			
			ps.setInt(pn++, M_Product_ID);
			
			for (Integer x : atributos.keySet())
				ps.setInt(pn++, x);
			
			if (M_Warehouse_ID != 0)
				ps.setInt(pn++, M_Warehouse_ID);
			
			ps.setInt(pn++, M_Product_ID);

			//
			
			ResultSet rs = ps.executeQuery();
			
			boolean go = rs.next();
			
			while (go) {
				int i;
				Vector<Object> x = new Vector<Object>();  
				for (i = 0; go && i < columnsCount; i++) {
					BigDecimal b = rs.getBigDecimal(1);
					
					b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
					
					MatrixItem item = new MatrixItem(rs.getInt(2));
					
					for (int w = 1; w <= atributos.size(); w++) 
						item.addValueId(rs.getInt(w + 2));
					
					x.add(item);
					
					go = rs.next();
				}
				ret.add(x);
			}
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "", e);
		} finally {
			try{
				if (ps != null) ps.close();
			} catch (SQLException e) {}
		}
		
		return ret;
	}
	
	@Override
	public void dispose() {
		setVisible(false);
		super.dispose();
	}
	
	public String getTrxName() {
		return null;
	}
	
	private List<MProductPriceInstance> getCurrentPriceList() {
		List<MProductPriceInstance> ret = new ArrayList<MProductPriceInstance>();
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement("SELECT * FROM M_ProductPriceInstance WHERE M_PriceList_Version_ID = ? AND M_Product_ID = ?", getTrxName());
			
			int pn = 1;
			ps.setInt(pn++, M_PriceList_Version_ID);
			ps.setInt(pn++, M_Product_ID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				ret.add(new MProductPriceInstance(Env.getCtx(), rs, getTrxName()));
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
		
		return ret;
	}
	
	public static MAttributeSetInstance findExistingMasi(int M_AttributeSet_ID, Set<Integer> values) {
		StringBuffer sql = new StringBuffer();
		MAttributeSetInstance ret = null;
		int auCount = getAttributeUseCount(M_AttributeSet_ID);
		
		if (auCount < 1 || values.size() < 1 || auCount != values.size())
			return null;
		
		/*
		 * La siguiente consulta devuelve la lista de instancias que tienen los values_id 
		 * especificados por parametros.
		 *  
		 */
		
		/*
		SELECT *
		FROM M_AttributeSetInstance asi
		WHERE asi.M_AttributeSet_ID = 1000005 AND asi.isActive = 'Y' AND serno IS NULL AND lot IS NULL AND m_lot_id IS NULL 
		AND (
		  SELECT COUNT(*) FROM m_attributeinstance ai3 WHERE (asi.M_AttributeSetInstance_id=ai3.M_AttributeSetInstance_id) 
		  ) = 2
		AND NOT EXISTS ( 
		  SELECT * FROM m_attributeinstance ai2 
		  WHERE (asi.M_AttributeSetInstance_id=ai2.M_AttributeSetInstance_id) AND ai2.M_AttributeValue_ID NOT IN (1000006,1000003) 
		  )
		 */
		
		sql.append(
				" SELECT M_AttributeSetInstance_ID " +
				" FROM M_AttributeSetInstance asi " +
				" WHERE asi.M_AttributeSet_ID = ? AND asi.isActive = 'Y' AND serno IS NULL AND lot IS NULL AND m_lot_id IS NULL " +
				" AND ( " +
				"   SELECT COUNT(*) FROM m_attributeinstance ai3 WHERE (asi.M_AttributeSetInstance_id=ai3.M_AttributeSetInstance_id)  " +
				"   ) = " + auCount + " " +
				" AND NOT EXISTS ( " +
				"   SELECT * FROM m_attributeinstance ai2 " +
				"   WHERE (asi.M_AttributeSetInstance_id=ai2.M_AttributeSetInstance_id) AND ai2.M_AttributeValue_ID NOT IN (");
		
		int i = 0;
		for (Integer x : values) {
			if (i > 0)
				sql.append(",");
			sql.append(x);
			i++;
		}
		
		sql.append(") " +
				"  ) ");
		
		PreparedStatement ps = null;
		
		try {
			ps = DB.prepareStatement(sql.toString(), null);
			
			int pn = 1;
			ps.setInt(pn++, M_AttributeSet_ID);
			
			ResultSet rs = ps.executeQuery();
			
			if (rs.next())
				ret = new MAttributeSetInstance(Env.getCtx(), rs.getInt(1), null);
			
			rs.close();
			ps.close();
			
			ps = null;
			
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "findExistingMasi - " + sql.toString(), e);
		} finally {
			if (ps != null) {
				try { ps.close(); } catch (SQLException e) { }
			}
		}
		
		return ret;
	}
	
	private MAttributeSetInstance createNewMasi(int m_attributeset_id, Set<Integer> values) {
		MAttributeSetInstance masi = new MAttributeSetInstance(Env.getCtx(), 0, m_attributeset_id, getTrxName());
		
		masi.save();
		
		for (Integer valueId : values) {
			int m_attribute_id = DB.getSQLValue(getTrxName(), " SELECT m_attribute_id FROM m_attributevalue WHERE m_attributevalue_id = ? " , valueId); 
			String valueStr = DB.getSQLValueString(getTrxName(), " SELECT value FROM m_attributevalue WHERE m_attributevalue_id = ? " , valueId);
			
			MAttributeInstance mai = new MAttributeInstance(Env.getCtx(), m_attribute_id, masi.getM_AttributeSetInstance_ID(), valueId, valueStr, getTrxName());
			
			mai.save();
		}
		
		masi.setDescription();
		masi.save();
		
		return masi;
	}
	
	private void displayLastError() {
		ValueNamePair pair = CLogger.retrieveError();
		if (pair != null)
			ADialog.error(m_srcWindowNo, this, (pair.getValue() != null) ? pair.getValue() : "", (pair.getName() != null) ? pair.getName() : "");
	}
	
	protected void onBtnOk() {
		MatrixTableModel tm = (MatrixTableModel)table.getModel();
		Vector<MatrixItem> itemList = new Vector<MatrixItem>();
		Vector<MProductPriceInstance> lineList = new Vector<MProductPriceInstance>();
		
		int column , row;
		
		// Busco las celdas de la tabla con un Qty ingresado > 0
		
		for ( row = 0; row < tm.getRowCount(); row++ ) {
			for ( column = 1; column < tm.getColumnCount(); column++ ) {
				MatrixItem item = (MatrixItem)tm.getValueAt(row, column);
				
				if (item.getEnteredPrice().signum() > 0) {
					itemList.add(item);
				}
			}
		}
		
		// Cargo las lineas de precios existentes
		
		for ( MProductPriceInstance price : getCurrentPriceList() ) {
			lineList.add(price);
		}
		
		// Primero actualizo las lineas que ya están creadas
		
		for ( int i = 0; i < itemList.size(); ) {
			MatrixItem item = itemList.get(i);
			MProductPriceInstance line = item.getProductPriceInstance();
			
			if (line != null) {
				line.setPriceLimit(item.getEnteredPrice());
				line.setPriceList(item.getEnteredPrice());
				line.setPriceStd(item.getEnteredPrice());

				if (!line.save()) {
					displayLastError();
				}
				
				itemList.remove(i);
				
				// Borro la linea de precio de la lista de precios a actualizar
				
				for (int j = 0; j < lineList.size(); ) {
					MProductPriceInstance line2 = lineList.get(j);
					
					// La PK de M_ProductPriceInstance es (masi;mprod;mplv)
					
					if (line.getM_AttributeSetInstance_ID() == line2.getM_AttributeSetInstance_ID() &&
							line.getM_Product_ID() == line2.getM_Product_ID() &&
							line.getM_PriceList_Version_ID() == line2.getM_PriceList_Version_ID()) 
					{
						lineList.remove(j); 
						break;
					} 
					else 
					{
						j++;
					}
				}
			} else {
				i++;
			}
		}
		
		// En caso de que hagan falta nuevas lineas, las creo
		
		while ( lineList.size() < itemList.size() ) {
			MProductPriceInstance line = new MProductPriceInstance(Env.getCtx(), 0, getTrxName());
			
			if (lineList.size() > 0)
				MProductPriceInstance.copyValues(lineList.firstElement(), line);
			
			lineList.add(line);
		}
		
		// Asigno los valores a las lineas restantes
		
		for ( int i = 0; i < itemList.size(); i++ ) {
			MatrixItem item = itemList.get(i);
			MProductPriceInstance line = lineList.get(i);
			
			line.setM_Product_ID(M_Product_ID);
			line.setM_PriceList_Version_ID(M_PriceList_Version_ID);
			
			line.setPriceLimit(item.getEnteredPrice());
			line.setPriceList(item.getEnteredPrice());
			line.setPriceStd(item.getEnteredPrice());
			
			int masi_id;
			
			// Tengo que crear un nuevo MASI ? 
			
			if (item.getMasi() > 0) 
			{
				// Si ya hay stock en el item seleccionado, utilizo el mismo MASI
				
				masi_id = item.getMasi();
			} 
			else 
			{
				// Si no existe MASI para la combinación elegida, crear uno nuevo
				
				// FIXME: En vez de crear uno nuevo, buscar si hay alguno que puedo reutilizar!
				// FIXME: Cambiarlo tambien en la otra ventanita
				
				MAttributeSetInstance masi = findExistingMasi(M_AttributeSet_ID, item.getValuesIds());
				
				if (masi == null)
					masi = createNewMasi(M_AttributeSet_ID, item.getValuesIds());
				
				masi_id = masi.getM_AttributeSetInstance_ID();
			}
			
			line.setM_AttributeSetInstance_ID(masi_id);
			
			if (!line.save()) {
				displayLastError();
			}
		}
		
		for ( int i = itemList.size(); i < lineList.size(); i++ ) {
			MProductPriceInstance line = lineList.get(i);

			line.delete(true);
		}
		
		dispose();
	}
	
	protected void onBtnCancel() {
		dispose();
	}
	
	/* +++++++++++ */
	
	private static String getAttrMatrixItemsSql(int M_Attribute_ID, int M_Product_ID, int M_Warehouse_ID, Map<Integer, String> atributos) {
		StringBuffer sql = new StringBuffer();
		int w;
		
		sql.append(" SELECT SUM(COALESCE(sub1.qty, 0.0)) AS SumCant, MIN(sub1.M_ATTRIBUTESETINSTANCE_ID) ");

		for (w = 1; w <= atributos.size(); w++) {
			sql.append(" , mav" + w + "_id ");
		}
		
		sql.append( InfoProductAttribute.getAttrMatrixItemsSql(M_Attribute_ID, M_Product_ID, M_Warehouse_ID, atributos) );
		
		sql.append(" order by ");
		
		for (w = 2; w <= atributos.size(); w++) {
			sql.append(" ma" + w + "_seqno asc, mav" + w + "_seqno asc, ");
		}
		
		sql.append(" ma1_seqno asc, mav1_seqno asc ");
		
		return sql.toString();
	}
	
	/* +++++++++++ */
	
	private static class MatrixTableModel extends DefaultTableModel {
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex > 0)
				return MatrixItem.class;
			return String.class;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 0;
		}
	}
	
	private static class MatrixItem {
		private BigDecimal m_enteredPrice = BigDecimal.ZERO;
		private int m_masi = 0;
		private MProductPriceInstance m_ppi = null;
		private Set<Integer> valuesIds = new HashSet<Integer>();
		
		public MatrixItem(int masi) {
			this.m_masi = masi;
		}
		
		public void setEnteredPrice(BigDecimal p) {
			this.m_enteredPrice = p;
		}
		
		public BigDecimal getEnteredPrice() {
			return this.m_enteredPrice;
		}
		
		public int getMasi() {
			return this.m_masi;
		}
		
		public void setMasi(int m) {
			this.m_masi = m;
		}
		
		public MProductPriceInstance getProductPriceInstance() {
			return this.m_ppi;
		}
		
		public void setProductPriceInstance(MProductPriceInstance m) {
			setMasi(m.getM_AttributeSetInstance_ID());
			this.m_ppi = m;
		}

		public void addValueId(int vid) {
			valuesIds.add(vid);
		}
		
		public Set<Integer> getValuesIds() {
			return valuesIds;
		}
	}
	
	private static class MatrixCellRenderer extends JPanel implements TableCellRenderer {

		private JTextField field1 = new JTextField();
		
		Border unselectedBorder = null;
	    Border selectedBorder = null;
	    
		public MatrixCellRenderer() {
			GridLayout gl = new GridLayout(1,1);
			gl.setHgap(5);
			setLayout(gl);
			
			field1.setEditable(false);
			
			field1.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
			//field2.setBorder(BorderFactory.createEmptyBorder());
			
			field1.setHorizontalAlignment(JTextField.RIGHT);
			
			add(field1);
			
			Dimension d1 = new Dimension(new Dimension(field1.getMinimumSize()));
			d1.height += 6;
			setMinimumSize(d1);
			setPreferredSize(d1);
		}
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			MatrixItem item = (MatrixItem)value;
			
			if (item != null) {
				field1.setText(item.getEnteredPrice().toString());
				
				if (item.getEnteredPrice().signum() > 0)
					field1.setForeground(CompierePLAF.getTextColor_OK());
				else 
					field1.setForeground(CompierePLAF.getTextColor_Issue());
			} 
			
            if (isSelected) {
                if (selectedBorder == null)
                    selectedBorder = BorderFactory.createMatteBorder(2,2,2,2,table.getSelectionBackground());
                
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null)
                    unselectedBorder = BorderFactory.createMatteBorder(2,2,2,2,table.getBackground());
                
                setBorder(unselectedBorder);
            }
            
			return this;
		}
	}
	
	private static class MatrixCellEditor extends AbstractCellEditor implements TableCellEditor {
		
		private MatrixItem currentItem;
		private JTextField field1;
		
		public MatrixCellEditor() {
			field1 = new JTextField();
			
			field1.setHorizontalAlignment(JTextField.RIGHT);
		}
		
		public Object getCellEditorValue() {
			return currentItem;
		}

		public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
			currentItem = (MatrixItem)arg1;
			field1.setText(currentItem.getEnteredPrice().toString());
			return field1;
		}
		
		@Override
		public boolean stopCellEditing() {
			try {
				currentItem.setEnteredPrice(new BigDecimal(field1.getText()));
			} catch (NumberFormatException e) {
				currentItem.setEnteredPrice(BigDecimal.ZERO);
			}
			return super.stopCellEditing();
		}
	}
}


