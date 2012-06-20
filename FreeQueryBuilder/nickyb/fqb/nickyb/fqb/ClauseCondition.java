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

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import javax.swing.table.TableColumn;

public class ClauseCondition extends DynamicTable implements ExpressionCellOwner 
{
	JComboBox cbxCols;
	
	ClauseCondition(ClauseOwner clauses)
	{
		super(clauses,4);
		
		String[] operation = new String[]{"=","<",">","<=",">=","<>","like","not like","is","is not","in","not in"};
		
		TableColumn tableColumn = this.getColumn(1);
		tableColumn.setCellEditor(new DefaultCellEditor(cbxCols = new JComboBox()));
		cbxCols.setFont(UIUtilities.fontPLAIN);
		
		tableColumn = this.getColumn(2);
		tableColumn.setPreferredWidth(60);
		tableColumn.setMaxWidth(60);
		tableColumn.setResizable(false);
		tableColumn.setCellEditor(new DefaultCellEditor(new JComboBox(operation)));
		
		tableColumn = this.getColumn(3);
		tableColumn.setCellEditor(new ExpressionCellEditor(this));
		
		tableColumn = this.getColumn(4);
		tableColumn.setPreferredWidth(55);
		tableColumn.setMaxWidth(55);
		tableColumn.setResizable(false);
		tableColumn.setCellEditor(new DefaultCellEditor(new JComboBox(new String[]{"AND","OR "})));
		
		cbxCols.setEditable(true);
	}
	
	protected int addRow()
	{
		String[] row = new String[4];
		row[1] = "=";
		row[3] = "AND";
		
		return addRow(row);
	}
	
	protected int addRow(String[] rowdata)
	{
		int row = super.addRow();
		
		this.setValueAt(rowdata[0],row,1);
		this.setValueAt(rowdata[1],row,2);
		this.setValueAt(rowdata[2],row,3);
		
		if(row!=0 && this.getValueAt(row-1,4)==null)
			this.setValueAt(rowdata[3],row-1,4);
			
		return row;
	}
	
	public Object[] getColumns()
	{
		return clauses.getColumns();
	}
	
	public Object[] getFunctions()
	{
		return clauses.getFunctions();
	}
	
	public JComponent getComponent()
	{
		return this;
	}
}
