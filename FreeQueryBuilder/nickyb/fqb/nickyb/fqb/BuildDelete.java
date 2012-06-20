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

import java.sql.Connection;

import javax.swing.JComponent;

import nickyb.fqb.util.DefaultScrollPane;

public class BuildDelete extends BuildBasePane implements ClauseOwner
{
	private ClauseCondition where;
	
	public BuildDelete(Connection connection)
	{
		super(connection);
	}

	void initComponents()
	{
		setCenterComponent(new DefaultScrollPane("where",where = new ClauseCondition(this),false));
	}
	
	void addField(QueryTokens.Column column)
	{
		where.cbxCols.addItem(column.toString(QueryBuilder.useIdentifierQuote,false));
	}	
	
	public Object[] getColumns()
	{
		return new Object[0];
	}

	public JComponent getComponent()
	{
		return this;
	}

	public Object[] getFunctions()
	{
		return new Object[0];
	}

	public String getSyntax()
	{
		StringBuffer buffer = new StringBuffer();
		
		if(where!=null)
		{
			String append = null;
			for(int i=0; i<where.getModel().getRowCount()-1; i++)
			{
				Object[] rowdata = new Object[4];
				for(int j=0; j<4;j++) rowdata[j] = where.getModel().getValueAt(i,j+1);
				
				buffer.append(new QueryTokens.Condition(append,
														(rowdata[0]!=null ? rowdata[0].toString() : null),
														(rowdata[1]!=null ? rowdata[1].toString() : null),
														(rowdata[2]!=null ? rowdata[2].toString() : null)));
				
				append = (rowdata[3]!=null ? " " + rowdata[3].toString() : null);
			}
		}
		
		if(buffer.length()>0) buffer.insert(0," WHERE ");
		
		return "DELETE FROM " + this.getTable() + buffer;
	}
}
