/*
 * Copyright (C) 2004 Nicky BRAMANTE
 * 
 * This file is part of FreeQueryBuilder
 * 
 * FreeQueryBuilder is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * Send questions or suggestions to nickyb@interfree.it
 */

package nickyb.fqb.runtime;

import nickyb.fqb.QueryTokens;
import nickyb.fqb.util.*;

import java.awt.BorderLayout;
import java.awt.Cursor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class DialogMetadataSearch extends ModalDialog
{
	static final int INITIAL_WIDTH = 640;
	static final int INITIAL_HEIGHT = 480;
	
	private static String SCHEMA_COLUMN_LABEL = "schema";
	
	private SystemWindow syswin;
	
	JRadioButton rbtnColumn;
	JRadioButton rbtnTable;
	
	JLabel counter;
	
	JTable filter;
	JTable result;
	
	public DialogMetadataSearch(SystemWindow syswin)
	{
		super(syswin, "search", INITIAL_WIDTH, INITIAL_HEIGHT);
		this.syswin = syswin;
	}
	
	protected void onRunning()
	{
		try
		{
			if(syswin.builder.jdbcUseSchema())
				SCHEMA_COLUMN_LABEL = syswin.builder.getConnection().getMetaData().getSchemaTerm();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		createPerformPane();
		createResultPane();
		
		DefaultPanel pnlSouth = new DefaultPanel(2,2);
		pnlSouth.setCenterComponent(createFilterPane());
		pnlSouth.setSouthComponent(bar);
		getContentPane().add(pnlSouth,BorderLayout.SOUTH);
		
		fireTableStructureChanged();
		validate();
	}

	private void fireTableStructureChanged()
	{
		((DefaultTableModel)result.getModel()).fireTableStructureChanged();
		
		TableColumnModel columnModel = result.getColumnModel();
		try
		{
			if(!syswin.builder.jdbcUseSchema())
				columnModel.removeColumn(result.getColumn(SCHEMA_COLUMN_LABEL));
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		if(rbtnTable.isSelected())
			columnModel.removeColumn(result.getColumn("column"));
	}
	
	private void createResultPane()
	{
		DefaultTableModel model = new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		result = new JTable(model);
		
		model.addColumn(SCHEMA_COLUMN_LABEL);
		model.addColumn("table");
		model.addColumn("column");
		
		result.addMouseListener(new InternalMouseListener());
		
		counter = new JLabel("rows : 0");
		
		DefaultPanel pnlResult = new DefaultPanel();
		pnlResult.setCenterComponent(new JScrollPane(result));
		pnlResult.setSouthComponent(counter);
		getContentPane().add(pnlResult,BorderLayout.CENTER);			
	}
	
	private JComponent createFilterPane()
	{
		String[] operation = new String[]{"equals","starts with","ends with","contains"};
		
		String[] columnNames = new String[]{"label","operator","value"};
		Object[][] rowData = new Object[][]{{SCHEMA_COLUMN_LABEL,operation[0],""} , {"table",operation[0],""} , {"column",operation[0],""}};
		
		filter = new JTable(rowData ,columnNames);
		filter.setBorder(new CustomLineBorder(true,true,false,false));
		
		TableColumn tableColumn = filter.getColumn("operator");
		tableColumn.setPreferredWidth(80);
		tableColumn.setMaxWidth(80);
		tableColumn.setCellEditor(new DefaultCellEditor(new JComboBox(operation)));
		
		tableColumn = filter.getColumn("label");
		tableColumn.setPreferredWidth(60);
		tableColumn.setMaxWidth(60);
				
		return filter;
	}

	private void createPerformPane()
	{
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				DialogMetadataSearch.this.fireTableStructureChanged();
			}
		};
		
		rbtnColumn = new JRadioButton("columns");
		rbtnTable = new JRadioButton("tables", true);

		rbtnColumn.addActionListener(al);
		rbtnTable.addActionListener(al);

		ButtonGroup btnGropu = new ButtonGroup();
		btnGropu.add(rbtnColumn);
		btnGropu.add(rbtnTable);
		
		JButton btn = UIUtilities.createCustomButton("search");
		btn.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				DialogMetadataSearch.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				if(rbtnTable.isSelected())
					DialogMetadataSearch.this.performTableSearch();
				else
					DialogMetadataSearch.this.performColumnSearch();
				
				counter.setText("rows : " + DialogMetadataSearch.this.result.getRowCount());
				DialogMetadataSearch.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		
		bar.add(btn,0);
		bar.add(rbtnTable,0);
		bar.add(rbtnColumn,0);
	}
	
	private void performTableSearch()
	{
		if(filter.getCellEditor()!=null)
			filter.getCellEditor().stopCellEditing();
		
		DefaultTableModel model = (DefaultTableModel)result.getModel();
		model.setRowCount(0);

		try
		{
			String schemaPattern	= syswin.builder.jdbcUseSchema() ? this.getFilterValueAt(0) : null;
			String tablePattern		= this.getFilterValueAt(1);

			String catalog = schemaPattern == null ? null : syswin.builder.getConnection().getCatalog();
			
			ResultSet rs = syswin.builder.getConnection().getMetaData().getTables(catalog, schemaPattern, tablePattern, new String[]{"TABLE"});
			while(rs.next())
			{
				String[] row = new String[2];
				row[0] = rs.getString(2);
				row[1] = rs.getString(3);
				model.addRow(row);
			}
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private void performColumnSearch()
	{
		if(filter.getCellEditor()!=null)
			filter.getCellEditor().stopCellEditing();
		
		DefaultTableModel model = (DefaultTableModel)result.getModel();
		model.setRowCount(0);
		
		try
		{
			String schemaPattern	= syswin.builder.jdbcUseSchema() ? this.getFilterValueAt(0) : null;
			String tablePattern		= this.getFilterValueAt(1);
			String columnPattern	= this.getFilterValueAt(2);

			String catalog = schemaPattern == null ? null : syswin.builder.getConnection().getCatalog();
			
			ResultSet rs = syswin.builder.getConnection().getMetaData().getColumns(catalog, schemaPattern, tablePattern, columnPattern);
			while(rs.next())
			{
				String[] row = new String[3];
				row[0] = rs.getString(2);
				row[1] = rs.getString(3);
				row[2] = rs.getString(4);
				model.addRow(row);
			}
			rs.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private String getFilterValueAt(int row)
	{
		Object operator = filter.getValueAt(row,1);
		Object value	= filter.getValueAt(row,2);
		
		if(value == null || value.toString().equals("")) return null;
		
		if(operator.toString().equals("starts with"))
		{
			if(!value.toString().endsWith("%")) value = value.toString() + "%";
		}
		else if(operator.toString().equals("ends with"))
		{
			if(!value.toString().startsWith("%")) value = "%" + value.toString();
		}
		else if(operator.toString().equals("contains"))
		{
			if(!value.toString().startsWith("%")) value = "%" + value.toString();
			if(!value.toString().endsWith("%")) value = value.toString() + "%";
		}
		
		return value.toString().toUpperCase();
	}
	
	private class InternalMouseListener extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if(e.getClickCount() == 2)
			{
				DialogMetadataSearch.this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				
				JTable grid = (JTable)e.getSource();
				
				try
				{
					String schema	= null;
					String table	= null;
					
					if(DialogMetadataSearch.this.syswin.builder.jdbcUseSchema())
					{	
						if(grid.getValueAt(grid.getSelectedRow(),0)!=null)
							schema = grid.getValueAt(grid.getSelectedRow(),0).toString();
						
						if(grid.getValueAt(grid.getSelectedRow(),1)!=null)
							table = grid.getValueAt(grid.getSelectedRow(),1).toString();
					}
					else
					{
						if(grid.getValueAt(grid.getSelectedRow(),0)!=null)
							table = grid.getValueAt(grid.getSelectedRow(),0).toString();
					}
					
					DialogMetadataSearch.this.syswin.builder.add(new QueryTokens.Table(schema,table),true);
				}
				catch (SQLException sqle)
				{
					sqle.printStackTrace();
				}
				finally
				{
					DialogMetadataSearch.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));					
				}
			}
		}
	}
}
