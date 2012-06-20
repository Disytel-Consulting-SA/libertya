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

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

import nickyb.fqb.util.UIUtilities;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.openXpertya.model.M_Element;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class DesktopLoader extends JDialog implements Runnable
{
	static final int ONLY_THIS = 0;
	static final int ALL_FOREIGN_TABLES = 1;
	static final int ALL_PRIMARY_TABLES = 2;
	
	private JLabel message;
	
	private int mode;
	private boolean autoJoinRequested;
	private QueryDesktop desktop;
	private QueryTokens.Table table;
	
	private DesktopLoader(int mode, QueryDesktop desktop, QueryTokens.Table table, boolean autojoin)
	{
		super(UIUtilities.getFrameAncestor(desktop),"wait...",true);		

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setSize(275,55);
		this.setResizable(false);
				
		this.getContentPane().add(message=new JLabel("",JLabel.CENTER));
		
		this.autoJoinRequested = autojoin;
		this.desktop = desktop;
		this.table = table;
		this.mode = mode;
	}
	
	public void show()
	{
		new Thread(this).start();
		UIUtilities.centerOnScreen(this);
		super.setVisible(true);
	}
	
	public void run()
	{
		try
		{
			switch(mode)
			{
			case ALL_FOREIGN_TABLES: addAllForeignTables();break;
			case ALL_PRIMARY_TABLES: addAllPrimaryTables();break;
			default: addTable(table);
			}
		}
		catch(SQLException sqle)
		{
			System.out.println("DesktopLoader::run() -> " + sqle);
		}
		finally
		{
			this.dispose();
		}
	}
	
	public static void run(int mode, QueryDesktop desktop, QueryTokens.Table table, boolean autojoin)
	{
		new DesktopLoader(mode, desktop, table, autojoin).show();
	}
	
	private void addTable(QueryTokens.Table table)
		throws SQLException
	{
		message.setText("loading: " + table.toString(false,false));
		for(int i=0; desktop.find(table)!=null; i++)
		{
		    if(mode==ONLY_THIS && i==0 && (JOptionPane.showConfirmDialog(this,"Table already loaded, create a copy?",table.toString(false,false), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION))
		        table.setAlias(table.getName() + "_" + (char)(65+i));
		    else
		        return;
		}
		
		DesktopEntity item = creatEntity(table);
		desktop.add(item);
		
		if(autoJoinRequested && QueryBuilder.autoJoin) doAutoJoin(item);
	}
	
	private void addTables(ResultSet rs, int rsSchemaIndex, int rsTableIndex)
		throws SQLException
	{
		ArrayList list = new ArrayList();
		
		while(rs.next())
		{
			String schemaName = rs.getString(rsSchemaIndex);
			String tableName = rs.getString(rsTableIndex);
			
			list.add(new QueryTokens.Table(schemaName,tableName));
		}
		rs.close();
					
		for(ListIterator iter = list.listIterator(); iter.hasNext();)
		{
			addTable((QueryTokens.Table)iter.next());
		}
	}
	
	private void addAllForeignTables()
		throws SQLException
	{
		DatabaseMetaData dbmd = desktop.builder.getConnection().getMetaData();
		message.setText("reading...");
		
		String catalog = table.getSchema() == null ? null : dbmd.getConnection().getCatalog();
		ResultSet rs = dbmd.getExportedKeys(catalog, table.getSchema(), table.getName());
				
		addTables(rs,6,7);
	}
	
	private void addAllPrimaryTables()
		throws SQLException
	{
		DatabaseMetaData dbmd = desktop.builder.getConnection().getMetaData();
		message.setText("reading...");
		
		String catalog = table.getSchema() == null ? null : dbmd.getConnection().getCatalog();
		ResultSet rs = dbmd.getImportedKeys(catalog, table.getSchema(), table.getName());
				
		addTables(rs,2,3);
	}
	
//	begin vpj-cd e-evolution 05/16/18
	private DesktopEntity creatEntity(QueryTokens.Table table)
	//private DesktopEntity creatEntity(String schema, QueryTokens.Table table)
//	end vpj-cd e-evolution 05/16/18	
		throws SQLException
	{
		//begin vpj-cd e-evolution 05/16/18
		DesktopEntity item = new DesktopEntity(table);
		//DesktopEntity item = new DesktopEntity(schema, table);
		//end vpj-cd e-evolution 05/16/18
		item.setEnabled(desktop.builder.getConnection()!=null);
		
		if(desktop.builder.getConnection()!=null)
		{
			DatabaseMetaData dbmetadata = desktop.builder.getConnection().getMetaData();
			Hashtable primary = this.getPrimaryKeys(dbmetadata,item);
			
			String catalog = table.getSchema() == null ? null : dbmetadata.getConnection().getCatalog();
			ResultSet rsColumns = dbmetadata.getColumns(catalog, item.querytoken.getSchema(), item.querytoken.getName(), "%");
			while(rsColumns.next())
			{
				String columnName = rsColumns.getString(4);
				
				String typeName = rsColumns.getString(6);
				int size = rsColumns.getInt(7);
				String nullable = rsColumns.getString(18);
			
				String infos = "type:" + typeName + "; size:" + size + "; nullable:" + nullable;				
				//begin vpj-cd e-evolution 05/16/2005 
				org.openXpertya.model.M_Element element = org.openXpertya.model.M_Element.get(org.openXpertya.util.Env.getCtx(),columnName);
				//DesktopEntity.Field field = item.addField(columnName, primary.containsKey(columnName),size,infos);
				DesktopEntity.Field field = item.addField(columnName, org.openXpertya.util.Msg.getMsg(Env.getCtx(),element.getName()), primary.containsKey(columnName),size,infos);
				//end vpj-cd e-evolution 05/16/2005
				desktop.builder.clauses.addColumn(field.querytoken.toString(QueryBuilder.useIdentifierQuote,false));
			}
			rsColumns.close();
		}
		item.pack();
		
		return item;
	}
	
	private Hashtable getPrimaryKeys(DatabaseMetaData dbmetadata, DesktopEntity item)
	{
		Hashtable primary = new Hashtable();
		
		try
		{
			String catalog = item.querytoken.getSchema() == null ? null : dbmetadata.getConnection().getCatalog();			
			ResultSet rsPK = dbmetadata.getPrimaryKeys(catalog, item.querytoken.getSchema(), item.querytoken.getName());		
			while(rsPK.next())
				primary.put(rsPK.getString(4), rsPK.getString(6));
			rsPK.close();
		}
		catch (SQLException sqle)
		{
			System.out.println("DesktopLoader::getPrimaryKeys() -> " + sqle);
		}
		finally
		{
			return primary;
		}
	}
	
	private void doAutoJoin(DesktopEntity source)
		throws SQLException
	{
		DatabaseMetaData dbmetadata = desktop.builder.getConnection().getMetaData();
		
		joinImportedKeys(dbmetadata,source);
		joinExportedKeys(dbmetadata,source);
	}
	
	private void joinImportedKeys(DatabaseMetaData dbmetadata, DesktopEntity source)
		throws SQLException
	{
		if(desktop.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue()).length > 1)
		{
			String catalog = source.querytoken.getSchema() == null ? null : dbmetadata.getConnection().getCatalog();			
			ResultSet rsFK = dbmetadata.getImportedKeys(catalog, source.querytoken.getSchema(), source.querytoken.getName());		
			while(rsFK.next())
			{
				String pkschema = rsFK.getString(2);
				String pktable	= rsFK.getString(3);
				String pkcolumn = rsFK.getString(4);
				String fkcolumn = rsFK.getString(8);
				
				DesktopEntity item = desktop.find(pkschema, pktable);
				if(item!=null)
				{
					desktop.resetJoin();
					
					desktop.join(item,item.getField(pkcolumn));
					desktop.join(source,source.getField(fkcolumn));
				}
			}
			rsFK.close();
		}
	}

	private void joinExportedKeys(DatabaseMetaData dbmetadata, DesktopEntity source)
		throws SQLException
	{
		if(desktop.getAllFramesInLayer(QueryDesktop.PALETTE_LAYER.intValue()).length > 1)
		{
			String catalog = source.querytoken.getSchema() == null ? null : dbmetadata.getConnection().getCatalog();			
			ResultSet rsFK = dbmetadata.getExportedKeys(catalog, source.querytoken.getSchema(), source.querytoken.getName());		
			while(rsFK.next())
			{
				String pkcolumn = rsFK.getString(4);
				String fkschema = rsFK.getString(6);
				String fktable	= rsFK.getString(7);
				String fkcolumn = rsFK.getString(8);

				DesktopEntity item = desktop.find(fkschema, fktable);
				if(item!=null)
				{
					desktop.resetJoin();
					
					desktop.join(item,item.getField(fkcolumn));
					desktop.join(source,source.getField(pkcolumn));
				}
			}
			rsFK.close();
		}
	}
}
