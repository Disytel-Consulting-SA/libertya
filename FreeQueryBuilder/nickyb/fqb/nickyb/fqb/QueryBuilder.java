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

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
//import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class QueryBuilder extends DefaultPanel
{
	public static String identifierQuoteString 	= "\"";
	public static boolean useIdentifierQuote 	= true;
	public static boolean useColumnAutoAlias 	= true;
	public static boolean autoJoin				= true;
	
	public static int maxColumnNameLength = 0;
	
	private boolean joinMode;
	private Connection connection;

	QuerySyntax  syntax;
	QueryBrowser browser;
	QueryClauses clauses;
	QueryDesktop desktop;
	QueryListbar listbar;
	QueryToolbar toolbar;
	
	public QueryBuilder()
	{
		this(null);
	}

	public QueryBuilder(Connection connection)
	{
		super(2,2);
		setBorder(new CustomLineBorder(true,false,false,false));

		JSplitPane splitL = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitL.setOneTouchExpandable(true);
		splitL.setLeftComponent(browser = new QueryBrowser(this));
		splitL.setRightComponent(syntax = new QuerySyntax(this));
		
		JScrollPane scroll = new JScrollPane(desktop = new QueryDesktop(this));
		scroll.getVerticalScrollBar().setUnitIncrement(25);
		
		JSplitPane splitR = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitR.setOneTouchExpandable(true);
		splitR.setLeftComponent(scroll);
		splitR.setRightComponent(clauses = new QueryClauses(this));
		
		
		JSplitPane split = new JSplitPane();
		split.setLeftComponent(splitL);
		split.setRightComponent(splitR);
		split.setDividerLocation(QueryBrowser.DEFAULT_WiDTH);
		split.setOneTouchExpandable(true);
		
		this.setNorthComponent(listbar = new QueryListbar(this));
		this.setCenterComponent(split);
		this.setEastComponent(toolbar = new QueryToolbar(this));
		
		this.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent evt)
			{
				JSplitPane split = (JSplitPane)QueryBuilder.this.getComponent(1);
				((JSplitPane)split.getLeftComponent()).setDividerLocation(0.50);
				((JSplitPane)split.getRightComponent()).setDividerLocation(0.80);
				split.validate();
				
				QueryBuilder.this.removeComponentListener(this);
			} 
		});
		
		this.setConnection(connection);
		this.transferFocus();
	}
	
	void changeWorkMode()
	{
		joinMode = !joinMode;
		desktop.workModeChanged();
	}
	
	boolean isJoinMode()
	{
		return joinMode;
	}
	
	public void clean()
	{
		desktop.closeAllFrames();
		clauses.clean();
		syntax.clean();
	}
	
	String[] allFunctions = new String[0];
	private void addFunctions(String tokenize)
	{
		StringTokenizer st = new StringTokenizer(tokenize,",");
		
		String[] appo = new String[st.countTokens() + allFunctions.length];
		System.arraycopy(allFunctions,0,appo,0,allFunctions.length);
		
		for(int i=allFunctions.length; st.hasMoreTokens(); i++)
			appo[i] = st.nextToken();
		
		allFunctions = appo;
	}
	
	private void loadFunctions()
		throws SQLException
	{
		DatabaseMetaData dbmd = connection.getMetaData();
		
		addFunctions(dbmd.getNumericFunctions());
		addFunctions(dbmd.getStringFunctions());
		addFunctions(dbmd.getSystemFunctions());
		addFunctions(dbmd.getTimeDateFunctions());
		
		Arrays.sort(allFunctions);
	}
	
	public String[] getAllFunctions()
	{
		return allFunctions;
	}
	
	public void add(QueryTokens.Table table, boolean autojoin)
	{
		DesktopLoader.run(DesktopLoader.ONLY_THIS,desktop,table,autojoin);
	}

	public QueryModel getModelClone()
	{
		if(syntax.querymodel==null) return null;
		return (QueryModel)syntax.querymodel.clone();
	}

	public Connection getConnection()
	{
		return connection;
	}
	
	protected QueryBrowser getBrowser()
	{
		return browser;
	}
	
	protected QueryToolbar getToolbar()
	{
		return toolbar;
	}
	
	public void setConnection(Connection connection)
	{
		this.allFunctions = new String[0];
		this.connection = connection;
		this.clean();
		
		try
		{
			browser.connectionChanged();
			
			if(connection!=null)
			{
				QueryBuilder.identifierQuoteString = connection.getMetaData().getIdentifierQuoteString();
				QueryBuilder.maxColumnNameLength = connection.getMetaData().getMaxColumnNameLength();
				
				loadFunctions();
			}
			
		}
		catch(SQLException sqle)
		{
			System.out.println(sqle);
		}
	}

	public boolean jdbcUseSchema() throws SQLException
	{
		if(connection!=null)
			return connection.getMetaData().getMaxSchemaNameLength() > 0;
		else
			return false;
	}
}
