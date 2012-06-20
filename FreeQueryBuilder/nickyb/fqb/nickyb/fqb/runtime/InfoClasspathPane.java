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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import java.io.File;

import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;

import nickyb.fqb.util.DefaultPanel;
import nickyb.fqb.util.DefaultScrollPane;
import nickyb.fqb.util.UIUtilities;

public class InfoClasspathPane extends JTabbedPane
{
	private DialogAdministrator admin;
	
	private JList env;
	private JList ext;
	private JList cmd;
	private JList run;
	
	public InfoClasspathPane(DialogAdministrator admin)
	{
		this.admin = admin;
		
		env = new JList(new DefaultListModel());
		ext = new JList(new DefaultListModel());
		cmd = new JList(new DefaultListModel());
		run = new JList(new DefaultListModel());
		
		JPanel pnlclasspath = new JPanel(new GridLayout(3,1,3,3));
		addTab("$CLASSPATH", pnlclasspath);
		
		pnlclasspath.add(new DefaultScrollPane("sun.boot.class.path",env,false));
		pnlclasspath.add(new DefaultScrollPane("java.class.path",cmd,false));
		pnlclasspath.add(new DefaultScrollPane("java.ext.dirs",ext,false));
		
		String bootPath	= System.getProperty("sun.boot.class.path");
		String classPath= System.getProperty("java.class.path");
		String extDir	= System.getProperty("java.ext.dirs");
		
		File fileExtDir = new File(extDir);
		String[] exts = fileExtDir.list();
		for(int i=0; i<exts.length; i++)
			((DefaultListModel)ext.getModel()).addElement(exts[i]);

		StringTokenizer tokenizer = new StringTokenizer(classPath,";");
		while(tokenizer.hasMoreElements())
			((DefaultListModel)cmd.getModel()).addElement(tokenizer.nextElement());

		tokenizer = new StringTokenizer(bootPath,";");
		while(tokenizer.hasMoreElements())
			((DefaultListModel)env.getModel()).addElement(tokenizer.nextElement());
		
		DefaultPanel pnlruntime = new DefaultPanel();
		addTab("$RUNTIME", pnlruntime);
		Box bar = new Box(BoxLayout.X_AXIS);
		bar.add(UIUtilities.createCustomButton(new ActionArchiveAdd()));
		bar.add(UIUtilities.createCustomButton(new ActionArchiveRemove()));
		bar.add(Box.createHorizontalGlue());
		
		pnlruntime.setCenterComponent(new JScrollPane(run));
		pnlruntime.setSouthComponent(bar);
	}
	
	void addArchive(String filename)
	{
		((DefaultListModel)this.run.getModel()).addElement(filename);
		admin.dinfopane.addFile(filename);
	}
	
	void removeArchive()
	{
		int index = InfoClasspathPane.this.run.getSelectedIndex();
		if(index!=-1)
		{
			String filename = InfoClasspathPane.this.run.getSelectedValue().toString();
			
			((DefaultListModel)InfoClasspathPane.this.run.getModel()).removeElement(filename);
			admin.dinfopane.removeFile(filename);
		}
	}
	
	private class ActionArchiveAdd extends AbstractAction
	{
		ActionArchiveAdd()
		{
			super("add...");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setAcceptAllFileFilterUsed(false);
			chooser.setFileFilter(new FileFilter()
			{
				public boolean accept(File file)
				{
					return file.isDirectory() || file.getName().endsWith(".jar") || file.getName().endsWith(".zip");
				}
				public String getDescription()
				{
					return "Archive files (*.jar, *.zip)";
				}
			});
				
			if(chooser.showOpenDialog(InfoClasspathPane.this) == JFileChooser.APPROVE_OPTION)
			{
				InfoClasspathPane.this.addArchive(chooser.getSelectedFile().getAbsolutePath());
			}			
		}			
	}
	
	private class ActionArchiveRemove extends AbstractAction
	{
		ActionArchiveRemove()
		{
			super("remove");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			InfoClasspathPane.this.removeArchive();
		}			
	}
}
