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

import java.awt.GridLayout;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;

public class DialogReferences extends ConfirmDialog
{
	DesktopEntity item;
	
	JList foreignTables;
	JList primaryTables;
	
	public DialogReferences(DesktopEntity item)
	{
		super(item.getQueryBuilder(), item.getHeader() + " - references", 440, 280);
        
        this.item = item;
        
		foreignTables = new JList(new DefaultListModel());
		primaryTables = new JList(new DefaultListModel());
		
		JPanel pnlCenter = new JPanel(new GridLayout(1,2,2,2));
		
		pnlCenter.add(new DefaultScrollPane("foreign tables",foreignTables,true));
		pnlCenter.add(new DefaultScrollPane("primary tables",primaryTables,true));		
				
		this.getContentPane().add(pnlCenter);
	}
	
	protected void onRunning()
	{
		try
		{
			loadExportedKeys();
			loadImportedKeys();
		}
		catch(SQLException sqle)
		{
			System.out.println(sqle);
		}
	}
	
	private void loadImportedKeys()
		throws SQLException
	{
		DatabaseMetaData dbmd = item.getQueryBuilder().getConnection().getMetaData();
		
		String catalog = item.querytoken.getSchema() == null ? null : dbmd.getConnection().getCatalog();
		ResultSet rs = dbmd.getImportedKeys(catalog, item.querytoken.getSchema(), item.querytoken.getName());		
		while(rs.next())
		{
			String pkschema = rs.getString(2);
			String pktable	= rs.getString(3);
			
			if(!pktable.equals(item.querytoken.getName()))
			{
				String pkElement = pkschema==null?pktable:pkschema+"."+pktable;
				
				if(!((DefaultListModel)primaryTables.getModel()).contains(pkElement))
					((DefaultListModel)primaryTables.getModel()).addElement(pkElement);
			}
		}
		rs.close();
	}

	private void loadExportedKeys()
		throws SQLException
	{
		DatabaseMetaData dbmd = item.getQueryBuilder().getConnection().getMetaData();
		
		String catalog = item.querytoken.getSchema() == null ? null : dbmd.getConnection().getCatalog();
		ResultSet rs = dbmd.getExportedKeys(catalog, item.querytoken.getSchema(), item.querytoken.getName());		
		while(rs.next())
		{
			String fkschema = rs.getString(6);
			String fktable	= rs.getString(7);
			String fkcolumn	= rs.getString(8);

			if(!fktable.equals(item.querytoken.getName()))
			{
				String fkElement = fkschema==null?fktable:fkschema+"."+fktable;
				
				if(!((DefaultListModel)foreignTables.getModel()).contains(fkElement))
					((DefaultListModel)foreignTables.getModel()).addElement(fkElement);
			}
		}
		rs.close();
	}
	
	private void addToSource(Object[] tables)
	{
		for(int i=0; i<tables.length; i++)
		{
			String schema = null;
			String table = tables[i].toString();
			
			if(table.indexOf('.')!=-1)
			{
				schema = table.substring(0,table.indexOf('.'));
				table = table.substring(table.indexOf('.')+1);
			}
			
			item.getQueryBuilder().add(new QueryTokens.Table(schema,table),true);
		}
	}
	
	protected boolean onConfirm()
	{
		addToSource(foreignTables.getSelectedValues());
		addToSource(primaryTables.getSelectedValues());
		
		return true;
	}
}
