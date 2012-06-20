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

package nickyb.fqb.runtime;

import nickyb.fqb.*;
import nickyb.fqb.util.*;

import java.awt.event.ActionEvent;

import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class ViewBuilder extends QueryBuilder implements SystemView, TreeSelectionListener
{
	private SystemWindow syswin;
	
	private AbstractAction updateData;
	private JToggleButton switchbutton;
	
	private QueryTokens.Table selectedTable;
	
	public ViewBuilder(SystemWindow syswin)
	{
		this.syswin = syswin;
		
		this.getToolbar().add(new ActionRunPreview());
		
		this.getBrowser().addBefore(updateData = new ActionUpdateData(),false);
		this.getBrowser().addTreeSelectionListener(this);
		
		switchbutton = new JToggleButton(new ActionSwitch());
	}
	
	public void valueChanged(TreeSelectionEvent evt)
	{
		try
		{
			selectedTable = null;
			
			if(jdbcUseSchema())
				updateData.setEnabled(evt.getPath().getPathCount() == 3);
			else
				updateData.setEnabled(evt.getPath().getPathCount() == 2);
			
			if(updateData.isEnabled())
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)evt.getPath().getLastPathComponent();
				
				String schema = null;
				if(evt.getPath().getPathCount() == 3)
				{
					schema = evt.getPath().getParentPath().getLastPathComponent().toString();
					schema = schema.substring(0,schema.indexOf("(")).trim();
				}
				
				selectedTable = new QueryTokens.Table(schema,node.toString());
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private class ActionRunPreview extends AbstractAction
	{
		private ActionRunPreview()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.preview"));
			this.putValue(SHORT_DESCRIPTION, "run preview");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			ViewBuilder.this.syswin.runQuery();
		}
	}
	
	private class ActionUpdateData extends AbstractAction
	{
		private ActionUpdateData()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.update"));
			this.putValue(SHORT_DESCRIPTION, "update data");
			
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			new DialogUpdateData(ViewBuilder.this,selectedTable).setVisible(true);
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
			super("window.builder","view query builder");
		}
		
		protected SystemWindow getWindow()
		{
			return ViewBuilder.this.syswin;
		}
	}
}
