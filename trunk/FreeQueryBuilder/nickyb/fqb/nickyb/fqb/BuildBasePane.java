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

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.openXpertya.util.DB;

import nickyb.fqb.util.DefaultPanel;
import nickyb.fqb.util.DefaultScrollPane;
import nickyb.fqb.util.ImageStore;
import nickyb.fqb.util.Task;
import nickyb.fqb.util.TaskSource;
import nickyb.fqb.util.TaskTarget;

public abstract class BuildBasePane extends DefaultPanel implements TaskSource,TaskTarget
{
	JLabel status;
	JTextArea syntax;
	Connection connection;
	QueryTokens.Table querytoken;
	
	private AbstractAction actionexec;
	private AbstractAction actioncopy;

	public BuildBasePane(Connection connection)
	{
		super(2,2);
		this.connection = connection;
		
		JToolBar header = new JToolBar();
		header.add(new JLabel("syntax"));
		header.add(Box.createHorizontalGlue());
		header.add(actionexec = new ActionExecute());
		header.add(actioncopy = new ActionCopySyntax());
		
		DefaultScrollPane scroll = new DefaultScrollPane(header, syntax = new JTextArea(), false);
		scroll.setMaximumSize(new Dimension(80,80));
		scroll.setMinimumSize(new Dimension(80,80));
		scroll.setPreferredSize(new Dimension(80,80));
		
		syntax.setWrapStyleWord(true);
		syntax.setEditable(false);
		syntax.setLineWrap(true);
		syntax.setOpaque(false);
		syntax.setTabSize(4);
		
		DefaultPanel south = new DefaultPanel();
		south.setCenterComponent(scroll);
		south.setSouthComponent(status = new JLabel("..."));
		this.setSouthComponent(south);
		
		initComponents();
	}
	
	public BuildBasePane(Connection connection,QueryTokens.Table querytoken)
	{
		this(connection);
		this.setQueryToken(querytoken);
	}
	
	abstract void addField(QueryTokens.Column column);
	abstract void initComponents();
	
	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		actionexec.setEnabled(b);
		actioncopy.setEnabled(b);
	}
	
	public void setQueryToken(QueryTokens.Table querytoken)
	{
		this.querytoken = querytoken;
		if(querytoken==null) return;

		try
		{
			DatabaseMetaData dbmetadata = this.getConnection().getMetaData();
			
			String catalog = querytoken.getSchema() == null ? null : dbmetadata.getConnection().getCatalog();
			ResultSet rsColumns = dbmetadata.getColumns(catalog, querytoken.getSchema(), querytoken.getName(), "%");
			while(rsColumns.next())
			{
				String columnName = rsColumns.getString(4);
					
				String typeName = rsColumns.getString(6);
				int size = rsColumns.getInt(7);
				String nullable = rsColumns.getString(18);
				
				//begin vpj-cd e-evolution 05/16/2005
				String desc="";
				try
				{
					
					String sql = "SELECT Name FROM AD_Element WHERE UPPER(ColumnName)=UPPER('" +columnName +"')";
					PreparedStatement pstmt = null;
					
						pstmt = DB.prepareStatement (sql);
						
						ResultSet rs = pstmt.executeQuery ();
						if (rs.next ())
							desc = rs.getString(1);
						rs.close ();
						pstmt.close ();
						pstmt = null;
				}
				catch(SQLException esql)
				{
				}
				//end vpj-cd e-evolution 05/16/2005
					
				String infos = "type:" + typeName + "; size:" + size + "; nullable:" + nullable;
				//begin vpj-cd e-evolution 05/16/2005vpj-cd e-evolution 05/16/2005
				//addField(new QueryTokens.Column(querytoken,columnName));
				addField(new QueryTokens.Column(querytoken,columnName,desc + " [" +columnName +"]"));
				//end vpj-cd e-evolution 05/16/2005
			}
			rsColumns.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			fireRefreshSyntax();
		}
	}
	
	public void fireRefreshSyntax()
	{
		syntax.setText(this.getSyntax());
	}
	
	public Connection getConnection()
	{
		return connection;
	}
	
	public void onTaskFinished()
	{
	}

	public void onTaskStarting()
	{
	}

	public boolean continueRun()
	{
		return true;
	}

	public StringBuffer getOutputBuffer()
	{
		return new StringBuffer();
	}

	public void message(String text)
	{
		status.setText(text);
	}
	
	protected String getTable()
	{
	    return (querytoken == null ? "" : querytoken.toString(QueryBuilder.useIdentifierQuote,false));
	}
	
	private class ActionCopySyntax extends AbstractAction
	{
		ActionCopySyntax()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("clipboard.copy"));
			this.putValue(SHORT_DESCRIPTION, "copy syntax");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			BuildBasePane.this.syntax.selectAll();
			BuildBasePane.this.syntax.copy();
		}
	}
	
	private class ActionExecute extends AbstractAction
	{
		ActionExecute()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.execute"));
			this.putValue(SHORT_DESCRIPTION, "execute");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			new Thread(new Task(BuildBasePane.this,BuildBasePane.this)).start();
		}
	}
}
