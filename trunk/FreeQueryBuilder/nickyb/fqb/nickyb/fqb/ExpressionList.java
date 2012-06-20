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

import java.util.Vector;

import javax.swing.JComponent;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import javax.swing.table.TableColumn;

public class ExpressionList extends DynamicTable implements ExpressionCellOwner
{
	private int keyCounter = 0;
	Vector keys = new Vector();
	
	ExpressionList(ClauseOwner clauses)
	{
		super(clauses,1);
		this.getModel().addTableModelListener(new ChangeHandler());
		
		TableColumn tableColumn = this.getColumn(1);
		tableColumn.setCellEditor(new ExpressionCellEditor(this));
	}
	
	private QueryBuilder getQueryBuilder()
	{
		return ((QueryClauses)clauses).builder;
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
	void add(String value)
	{
		int row = this.addRow();
		this.setValueAt(value,row,1);
	}
	
	protected void deleteEvent(int row)
	{
		this.getQueryBuilder().listbar.removeItem(keys.elementAt(row).toString());
		keys.removeElementAt(row);
	}
	
	protected void insertEvent(int row)
	{
		keys.insertElementAt("USER_EXPRESSION_"+(++keyCounter),row);
	}
	
	protected void updateEvent(int row, int col)
	{
		if(col == 1)
		{
			String key = keys.elementAt(row).toString();
			String value = this.getValueAt(row, col).toString();
			
			if(this.getQueryBuilder().listbar.containsItem(key))
				this.getQueryBuilder().listbar.setExpression(key,value);
			else
				this.getQueryBuilder().listbar.addExpression(key,value);
		}
	}
	
	class ChangeHandler implements TableModelListener
	{
		public void tableChanged(TableModelEvent tme)
		{
			switch(tme.getType())
			{
			case TableModelEvent.DELETE:
				deleteEvent(tme.getFirstRow());
				break;
			case TableModelEvent.INSERT:
				insertEvent(tme.getFirstRow());
				break;
			case TableModelEvent.UPDATE:
				updateEvent(tme.getFirstRow(), tme.getColumn());
				break;
			}
		}
	}
}
