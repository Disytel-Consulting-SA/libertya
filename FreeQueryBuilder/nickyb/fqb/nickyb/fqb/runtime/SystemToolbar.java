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

import nickyb.fqb.util.*;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class SystemToolbar extends DefaultPanel
{
	private SystemWindow syswin;
	
	private JButton dbstatus;
	private JLabel dbprofile;
	private JLabel dbuser;
	
	SystemToolbar(SystemWindow syswin)
	{
		super(8,8);
		this.syswin = syswin;
		
		addToolbar();
		addDatabasebar();
		
		setEastComponent(new SystemMemory());
	}
	
	private void addDatabasebar()
	{
		Box databasebar = new Box(BoxLayout.X_AXIS);
		
		databasebar.add(dbstatus = new JButton(new ActionDisconnect()));
		dbstatus.setBorder(new EmptyBorder(3,4,2,4));

		databasebar.add(new JLabel("database = "));
		databasebar.add(dbprofile = new JLabel("<none>"));
				
		databasebar.add(Box.createHorizontalGlue());
		
		databasebar.add(new JLabel("user = "));
		databasebar.add(dbuser = new JLabel("<none>"));
		
		databasebar.add(new JToolBar.Separator());
		
		DefaultPanel pane = new DefaultPanel();
		pane.setBorder(new CompoundBorder(new EmptyBorder(2,0,2,0), new EtchedBorder(EtchedBorder.RAISED)));
		pane.add(databasebar);
		
		setCenterComponent(pane);
	}
	
	private void addToolbar()
	{
		ButtonGroup switchgroup = new ButtonGroup();
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		toolbar.add(new ActionOpenAdministrator());
		toolbar.add(new ActionOpenMetaSearch());
		toolbar.addSeparator();

		JToggleButton switchbutton = syswin.builder.getButton();
		switchbutton.setSelected(true);
		switchgroup.add(switchbutton);
		toolbar.add(switchbutton);
		
		switchbutton = syswin.history.getButton();
		switchgroup.add(switchbutton);
		toolbar.add(switchbutton);

		toolbar.addSeparator();
		
		switchbutton = syswin.command.getButton();
		switchgroup.add(switchbutton);
		toolbar.add(switchbutton);

		toolbar.addSeparator();
		
		switchbutton = syswin.report.getButton();
		switchgroup.add(switchbutton);
		toolbar.add(switchbutton);
		
		setWestComponent(toolbar);
	}
	
	void connectionChanged()
	{
		if(syswin.builder.getConnection() == null)
		{
			dbstatus.setIcon(ImageStore.getIcon("database.disconnect"));
			dbstatus.setEnabled(false);
			
			dbprofile.setText("<none>");
			dbuser.setText("<none>");
		}
		else
		{
			dbstatus.setIcon(ImageStore.getIcon("database.connect"));
			dbstatus.setEnabled(true);
			
			dbprofile.setText(syswin.pinfo.url);
			dbuser.setText(syswin.pinfo.uid);
		}
	}	
	
	private class ActionOpenAdministrator extends AbstractAction
	{
		ActionOpenAdministrator()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("database.jdbc"));
			this.putValue(SHORT_DESCRIPTION, "open JDBC administrator window");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			new DialogAdministrator(SystemToolbar.this.syswin).setVisible(true);
		}
	}
	
	private class ActionOpenMetaSearch extends AbstractAction
	{
		ActionOpenMetaSearch()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("database.search"));
			this.putValue(SHORT_DESCRIPTION, "open metadata search window");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(syswin.builder.getConnection()==null)
				JOptionPane.showMessageDialog(SystemToolbar.this.syswin, "No connection!", "FreeQueryBuilder", JOptionPane.ERROR_MESSAGE);
			else
				new DialogMetadataSearch(SystemToolbar.this.syswin).setVisible(true);
		}
	}
	
//	private class ActionOpenPreferences extends AbstractAction
//	{
//		ActionOpenPreferences()
//		{
//			super("preferences...");
//		}
//		
//		public void actionPerformed(ActionEvent e)
//		{
//			new DialogPreferences(SystemToolbar.this.syswin).show();
//		}
//	}

	private class ActionDisconnect extends AbstractAction
	{
		ActionDisconnect()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("database.disconnect"));
			this.putValue(SHORT_DESCRIPTION, "disconnect");
			this.setEnabled(false);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(JOptionPane.showConfirmDialog(SystemToolbar.this.syswin,"Continue?","Disconnect", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				SystemToolbar.this.syswin.closeConnection();			
		}
	}
	
	public static abstract class AbstractActionSwitch extends AbstractAction
	{
		protected AbstractActionSwitch(String smallIcon, String shortDecription)
		{
			this.putValue(DEFAULT, smallIcon);
			this.putValue(SMALL_ICON, ImageStore.getIcon(smallIcon));
			this.putValue(SHORT_DESCRIPTION, shortDecription);
		}
		
		protected abstract SystemWindow getWindow();
		
		public void actionPerformed(ActionEvent e)
		{
			this.getWindow().view(this.getValue(DEFAULT).toString());
		}
	}
}
