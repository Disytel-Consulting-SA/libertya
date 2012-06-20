package org.openXpertya.pos.view.table;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public abstract class AbstractPoSTableModel extends AbstractTableModel {

	private List<String> columnNames;

	public AbstractPoSTableModel() {
		super();
		columnNames = new Vector<String>();
	}

	public int getRowCount() {
		return getObjects().size();
	}
	
	public int getColumnCount() {
		return getColumnNames().size();
	}
	
	@Override
	public String getColumnName(int col) {
		try {
			return getColumnNames().get(col);
		} catch (Exception e) {
			return "Column" + col;
		}
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		if(getObjects() == null || getObjects().size() == 0)
			return super.getColumnClass(col);
		
		return getValueAt(0,col).getClass();
	}
	
	/**
	 * @return Devuelve columnNames.
	 */
	public List<String> getColumnNames() {
		return columnNames;
	}

	/**
	 * @param columnNames Fija o asigna columnNames.
	 */
	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}
	
	public abstract List getObjects();

	public void addColumName(String name) {
		getColumnNames().add(name);
	}
}
