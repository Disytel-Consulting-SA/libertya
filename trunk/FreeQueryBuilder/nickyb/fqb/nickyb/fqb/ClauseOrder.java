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

package nickyb.fqb;

import nickyb.fqb.util.*;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.UIManager;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ClauseOrder extends JPanel implements ActionListener
{
	static final String PUT		= "put";
	static final String PUSH	= "push";
	static final String UP		= "up";
	static final String DOWN	= "down";
	
	JList availableExpressions;
	JTable selectedExpressions;
	
	QueryClauses clauses;
	
	ClauseOrder(QueryClauses clauses)
	{
		super(new GridLayout(1,2));
		
		this.clauses = clauses;
		
		DefaultPanel pnl;
		JToolBar bar;
		JScrollPane scroll;
		
		DefaultTableModel model = new DefaultTableModel(0,2)
		{
			public boolean isCellEditable(int row, int column)
			{
				return column == 0;
			}
		};

		this.add(pnl = new DefaultPanel());
		pnl.setCenterComponent(scroll = new JScrollPane(availableExpressions = new JList(new DefaultListModel())));
		pnl.setEastComponent(bar = new JToolBar(JToolBar.VERTICAL));
		bar.add(createButton("put >", PUT));
		bar.add(createButton("< push", PUSH));
		bar.setFloatable(false);
		
		this.add(pnl = new DefaultPanel());
		pnl.setCenterComponent(scroll = new JScrollPane(selectedExpressions = new JTable(model)));
		pnl.setEastComponent(bar = new JToolBar(JToolBar.VERTICAL));
		bar.add(createButton("up", UP));
		bar.add(createButton("down", DOWN));
		bar.setFloatable(false);
		scroll.getViewport().setBackground(selectedExpressions.getBackground());
		
		TableColumn tableColumn = selectedExpressions.getColumn(selectedExpressions.getColumnName(0));
		tableColumn.setCellEditor(new CheckBoxCellRenderer());
		tableColumn.setCellRenderer(new CheckBoxCellRenderer());
		tableColumn.setPreferredWidth(15);
		tableColumn.setMaxWidth(15);
		tableColumn.setResizable(false);
		
		selectedExpressions.setIntercellSpacing(new Dimension(0,0));
		selectedExpressions.setShowGrid(false);
		selectedExpressions.setColumnSelectionAllowed(false);
		selectedExpressions.setDefaultRenderer(Boolean.class, new CheckBoxCellRenderer());
		selectedExpressions.getTableHeader().setPreferredSize(new Dimension(0,0));
		selectedExpressions.getTableHeader().setVisible(false);
	}
	
	private JButton createButton(String text, String command)
	{
		JButton btn = UIUtilities.createCustomButton(text,this);
		btn.setActionCommand(command);
		
		return btn;
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals(PUT))
		{
			while(availableExpressions.getSelectedIndex()!=-1)
			{
				String exp = (String)availableExpressions.getSelectedValue();
				setAsSelected(exp,true);
			}
		}
		else if(ae.getActionCommand().equals(PUSH))
		{
			while(selectedExpressions.getSelectedRow()!=-1)
			{
				String exp = (String)selectedExpressions.getValueAt(selectedExpressions.getSelectedRow(),1);
				((DefaultTableModel)selectedExpressions.getModel()).removeRow(selectedExpressions.getSelectedRow());
				((DefaultListModel)availableExpressions.getModel()).addElement(exp);
			}
		}
		else if(ae.getActionCommand().equals(UP))
		{
			int row = selectedExpressions.getSelectedRow();
			if(row<1) return;
			((DefaultTableModel)selectedExpressions.getModel()).moveRow(row,row,--row);
			selectedExpressions.setRowSelectionInterval(row,row);
			scrollToRow(row);
		}
		else if(ae.getActionCommand().equals(DOWN))
		{
			int row = selectedExpressions.getSelectedRow();
			if(row==selectedExpressions.getRowCount()-1) return;
			((DefaultTableModel)selectedExpressions.getModel()).moveRow(row,row,++row);
			selectedExpressions.setRowSelectionInterval(row,row);
			scrollToRow(row);
		}
		
		clauses.builder.syntax.refresh();
	}
	
	private void scrollToRow(int row)
	{
		java.awt.Rectangle rect = selectedExpressions.getCellRect(row, 1, false);
		selectedExpressions.scrollRectToVisible(rect);
		selectedExpressions.revalidate();
	}
	
	void clean()
	{
		((DefaultListModel)availableExpressions.getModel()).removeAllElements();
		((DefaultTableModel)selectedExpressions.getModel()).setRowCount(0);
	}
	
	void addExpression(String exp)
	{
		((DefaultListModel)availableExpressions.getModel()).addElement(exp);
	}
	
	void expressionChanged(String oldValue, String newValue)
	{
		if(((DefaultListModel)availableExpressions.getModel()).contains(oldValue))
		{
			((DefaultListModel)availableExpressions.getModel()).removeElement(oldValue);
			((DefaultListModel)availableExpressions.getModel()).addElement(newValue);
		}
		else
		{
			for(int i=0; i<selectedExpressions.getRowCount(); i++)
			{
				if(selectedExpressions.getValueAt(i,1).toString().equals(oldValue))
				{
					selectedExpressions.setValueAt(newValue,i,1);
					break;
				}
			}
		}
	}
	
	void removeExpression(String exp)
	{
		((DefaultListModel)availableExpressions.getModel()).removeElement(exp);
		
		for(int i=0; i<selectedExpressions.getRowCount(); i++)
		{
			if(selectedExpressions.getValueAt(i,1).toString().equals(exp))
			{
				((DefaultTableModel)selectedExpressions.getModel()).removeRow(i);
				break;
			}
		}
	}
	
	void setAsSelected(String exp, boolean asc)
	{
		((DefaultListModel)availableExpressions.getModel()).removeElement(exp);
		((DefaultTableModel)selectedExpressions.getModel()).addRow(new Object[]{new Boolean(asc), exp});
	}	
	
	private class CheckBoxCellRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ItemListener
	{
		protected Border noFocusBorder;
		private JCheckBox checkBox;
		
		public CheckBoxCellRenderer()
		{
			super();
			
			if(noFocusBorder == null)
			{
				noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			}
			
			checkBox = new JCheckBox();
			checkBox.addItemListener(this);
			checkBox.setOpaque(true);
			checkBox.setBorder(noFocusBorder);
		}

		public void itemStateChanged(ItemEvent ie)
		{
			fireEditingStopped();
			ClauseOrder.this.clauses.builder.syntax.refresh();
		}

		private Component getCell(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			checkBox.setSelected(((Boolean)value).booleanValue());
			checkBox.setFont(table.getFont());
			
			if (isSelected)
			{
				checkBox.setBackground(table.getSelectionBackground());
				checkBox.setForeground(table.getSelectionForeground());
			}
			else
			{
				checkBox.setBackground(table.getBackground());
				checkBox.setForeground(table.getForeground());
			}
			
			checkBox.setBorder((hasFocus) ? UIManager.getBorder("Table.focusCellHighlightBorder") : noFocusBorder);

			return checkBox;
		}
		
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
		{
			return getCell(table, value, isSelected, true, row, column);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			return getCell(table, value, isSelected, true, row, column);
		}
		
		public Object getCellEditorValue()
		{
			return new Boolean(checkBox.isSelected());
		}
	}
}
