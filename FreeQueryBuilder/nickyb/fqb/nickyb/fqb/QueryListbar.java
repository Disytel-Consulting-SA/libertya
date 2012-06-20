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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;

public class QueryListbar extends DefaultPanel implements TableColumnModelListener
{
	private QueryBuilder builder;
	private JTable header;
	
	public QueryListbar(QueryBuilder builder)
	{
		this.builder = builder;
		
		JScrollPane scroll = new JScrollPane(header = new JTable(0,0));
		scroll.setBorder(UIUtilities.NO_BORDER);
		
		header.getTableHeader().setCursor(new Cursor(Cursor.HAND_CURSOR));
		header.getTableHeader().setBackground(new Color(255,255,225));
		header.getColumnModel().addColumnModelListener(this);
		setCenterComponent(scroll);
	}
	
	public Dimension getPreferredSize()
	{
		return new Dimension(0,16);
	}

	void addColumn(QueryTokens.Column column)
	{
		addItem(new ColumnItem(column));
	}

	void addExpression(String indentifier, String value)
	{
		addItem(new ExpressionItem(indentifier, value));
	}

	private void addItem(ExpressionItem item)
	{
		header.getTableHeader().getColumnModel().addColumn(item);
		
		String expressionValue = item.getValue();
		if(item instanceof ColumnItem)
		{
			expressionValue = ((ColumnItem)item).querytoken.toString(QueryBuilder.useIdentifierQuote,false);
			builder.clauses.group.addColumn(expressionValue);
		}
		builder.clauses.order.addExpression(expressionValue);
		builder.syntax.refresh();
	}
	
	boolean containsItem(String expressionKey)
	{
		return getItem(expressionKey)!=null;
	}
	
	int getItemCount()
	{
		return header.getTableHeader().getColumnModel().getColumnCount();
	}
	
	ExpressionItem getItem(String indentifier)
	{
		try
		{
			int index = header.getTableHeader().getColumnModel().getColumnIndex(indentifier);
			return getItemAt(index);
		}
		catch(IllegalArgumentException iae)
		{
		}
		
		return null;
	}

	ExpressionItem getItemAt(int index)
	{
		return (ExpressionItem)header.getTableHeader().getColumnModel().getColumn(index);
	}

	void setExpression(String indentifier, String value)
	{
		ExpressionItem item = this.getItem(indentifier);
		builder.clauses.order.expressionChanged(item.getValue(), value);
		item.setHeaderValue(value);
		
		this.repaint();
	}

	void removeItem(String expressionKey)
	{
		ExpressionItem item = this.getItem(expressionKey);
		if(item == null) return;
		
		header.getTableHeader().getColumnModel().removeColumn(item);
		
		String expressionValue = item.getValue();
		if(item instanceof ColumnItem)
		{
			expressionValue = ((ColumnItem)item).querytoken.toString(QueryBuilder.useIdentifierQuote,false);
			builder.clauses.group.removeColumn(expressionValue);
		}
		builder.clauses.order.removeExpression(expressionValue);
		builder.syntax.refresh();
	}
	
	public void columnAdded(TableColumnModelEvent e)
	{
	}

	public void columnMarginChanged(ChangeEvent e)
	{
	}

	public void columnMoved(TableColumnModelEvent e)
	{
		builder.syntax.refresh();
	}

	public void columnRemoved(TableColumnModelEvent e)
	{
	}

	public void columnSelectionChanged(ListSelectionEvent e)
	{
	}
	
	class ExpressionItem extends TableColumn
	{
		ExpressionItem(String value)
		{
			this(value,value);
		}
		
		ExpressionItem(String indentifier, String value)
		{
			setIdentifier(indentifier);
			setHeaderValue(value);
		}
		
		String getValue()
		{
			return getHeaderValue().toString();
		}
	}
	
	class ColumnItem extends ExpressionItem
	{
		QueryTokens.Column querytoken;
				
		ColumnItem(QueryTokens.Column querytoken)
		{
			super(querytoken.toString(false,QueryBuilder.useColumnAutoAlias));
			this.querytoken = querytoken;
		}
	}
}
