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

package nickyb.fqb.ext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import it.frb.Connessione;
import it.frb.DataPanel;
import it.frb.action.ActionControl_E;
import it.frb.action.ActionControl_I;
import it.frb.action.ActionControl_O;
import it.frb.action.ActionControl_S;

import nickyb.fqb.QueryBuilder;
import nickyb.fqb.QueryModel;
import nickyb.fqb.QueryTokens;

import nickyb.fqb.runtime.SystemToolbar;
import nickyb.fqb.runtime.SystemView;
import nickyb.fqb.runtime.SystemWindow;

import nickyb.fqb.util.DefaultScrollPane;
import nickyb.fqb.util.ImageStore;
import nickyb.fqb.util.UIUtilities;

public class ViewBuildReport extends DataPanel implements Connessione, SystemView
{
    static final String DEFAULT_TITLE = "FreeReportBuilder [pinom@users.sourceforge.net]";
    
	private SystemWindow syswin;
	private JToggleButton switchbutton;
	
	private JLabel title;
	
	static
	{
		org.zaval.awt.gdp.LibraryAdapter.setLibraryAdapter(new org.zaval.awt.gdp.JLibraryAdapter()); 		
	}
	
	public ViewBuildReport(SystemWindow syswin)
	{
	    super(false);
	    this.syswin = syswin;
		
		JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
		toolbar.setFloatable(false);
		
		toolbar.add(new ActionClean());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(putReportAction("control O",new ActionOpen()));
		toolbar.add(putReportAction("control S",new ActionSave()));
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new ActionGroupAdd());
		toolbar.add(new ActionGroupDel());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(putReportAction("control E",new ActionRunPreview()));
		
		title = new JLabel(DEFAULT_TITLE);
		title.setOpaque(true);
		title.setBorder(UIUtilities.NO_BORDER);
		title.setBorder(new CompoundBorder(DefaultScrollPane.hideBottomLine, new EmptyBorder(1,1,1,1)));
		title.setBackground(DefaultScrollPane.headerbackground);
		
		this.add(title,BorderLayout.NORTH);
		this.add(toolbar,BorderLayout.EAST);

		switchbutton = new JToggleButton(new ActionSwitch());
	}
	
	private AbstractAction putReportAction(String key,AbstractAction action)
	{
		this.getActionMap().put(key,action);
	    return action;
	}
	
	private void invokeReportAction(String key)
	{
	    this.getActionMap().get(key).actionPerformed(null);
	}
	
	public void createReport(QueryModel query)
	{
	    doClean();
	    this.setSql(query.getSyntax());
		
		ArrayList header = new ArrayList();
		ArrayList detail = new ArrayList();
		
		Object[] expressions = query.getOutputColumns();
		boolean[] isExpression = new boolean[expressions.length];
		for(int i=0; i<expressions.length; i++)
		{
			if(isExpression[i] = !(expressions[i] instanceof QueryTokens.Column))
			{
				header.add("expression_" + i);
				detail.add(expressions[i]);
			}
			else
			{
				QueryTokens.Column col = (QueryTokens.Column)expressions[i];
				header.add(col.getAlias());
				detail.add(col.toString(QueryBuilder.useIdentifierQuote,false));
			}
		}
		
		ActionControl_I actionImport = (ActionControl_I)this.getActionMap().get("control I");
		actionImport.setHeaderDetailComponents(header,detail);
		actionImport.actionPerformed(null);
		
		for(int i=0; i<isExpression.length; i++)
		{
			if(isExpression[i])
			{
				this.getDetailPanel().getComponent(i).setBackground(Color.cyan);
				this.getDetailPanel().getComponent(i).setName("expression_" + i);
			}
		}
	}
	
	private void doClean()
	{
	    invokeReportAction("F9");
	    while(this.getGroupCount()>0) invokeReportAction("control F5");
	}
	
	public Connection getConnessione() throws SQLException
	{
		return syswin.getConnection();
	}
	
	/* Request of new connection */
	public Connection getConnessione(String arg0) throws SQLException
	{
		try
		{
			return syswin.manager.open();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}

	public String getURLDatabase()
	{
		return null;
	}

	public String getUidDatabase()
	{
		return null;
	}

	public String getPwdDatabase()
	{
		return null;
	}

	public boolean getDbUseResultSetForwardAndReverse()
	{
		return false;
	}

	public boolean getDbUseMultiStatement()
	{
		return false;
	}

//	/////////////////////////////////////////////////////////////////////////////
//	Toolbar actions
//	/////////////////////////////////////////////////////////////////////////////
	
	private class ActionClean extends AbstractAction
	{
		ActionClean()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("command.clean"));
			this.putValue(SHORT_DESCRIPTION, "clean...");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    ViewBuildReport.this.doClean();
		}
	}
	
	private class ActionOpen extends ActionControl_O
	{
		ActionOpen()
		{
		    super("string");
			this.putValue(SMALL_ICON, ImageStore.getIcon("store.open"));
			this.putValue(SHORT_DESCRIPTION, "open...");
			
			setDataPanel(ViewBuildReport.this);
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    super.actionPerformed(ae);
		}
	}
	
	private class ActionSave extends ActionControl_S
	{
		ActionSave()
		{
			super("string");
			this.putValue(SMALL_ICON, ImageStore.getIcon("store.save"));
			this.putValue(SHORT_DESCRIPTION, "save...");
			
			setDataPanel(ViewBuildReport.this);
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			super.actionPerformed(ae);
		}
	}
	
	private class ActionGroupAdd extends AbstractAction
	{
		ActionGroupAdd()
		{
			super("string");
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.group.add"));
			this.putValue(SHORT_DESCRIPTION, "add group");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			ViewBuildReport.this.invokeReportAction("F5");
		}
	}
	
	private class ActionGroupDel extends AbstractAction
	{
		ActionGroupDel()
		{
			super("string");
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.group.del"));
			this.putValue(SHORT_DESCRIPTION, "delete group");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			ViewBuildReport.this.invokeReportAction("control F5");
		}
	}
	
	private class ActionRunPreview extends ActionControl_E
	{
		ActionRunPreview()
		{
		    super("string");
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.preview"));
			this.putValue(SHORT_DESCRIPTION, "run preview");
			
			setDataPanel(ViewBuildReport.this);
			setConnessione(ViewBuildReport.this);
		}
		
		public void actionPerformed(ActionEvent ae)
		{
            if (org.zaval.awt.gdp.DgnController.getCurrentDgn() != null)
                org.zaval.awt.gdp.DgnController.stopCurrentDgn();
            
			try
            {
                if(ViewBuildReport.this.getConnessione()==null)
                {
                	JOptionPane.showMessageDialog(ViewBuildReport.this.syswin, "No connection!", "FreeQueryBuilder", JOptionPane.ERROR_MESSAGE);
                	return;
                }
            }
            catch (SQLException sqle)
            {
                sqle.printStackTrace();
            }
		    
            new ReportPreview(ViewBuildReport.this).setVisible(true);
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	SystemView Interface
//	/////////////////////////////////////////////////////////////////////////////
	public JToggleButton getButton()
	{
		return switchbutton;
	}
	
	private class ActionSwitch extends SystemToolbar.AbstractActionSwitch
	{
		private ActionSwitch()
		{
			super("window.report","view report builder");
		}
		
		protected SystemWindow getWindow()
		{
			return ViewBuildReport.this.syswin;
		}
	}
}
