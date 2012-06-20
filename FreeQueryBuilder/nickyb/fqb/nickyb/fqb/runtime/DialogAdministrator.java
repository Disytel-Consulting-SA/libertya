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

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;

import java.awt.event.ActionEvent;

import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class DialogAdministrator extends ModalDialog implements TreeSelectionListener
{
	private final static int LEVEL_ROOT = 1;
	private final static int LEVEL_DRV  = 2;
	private final static int LEVEL_PROF = 3;
	
	protected SystemWindow syswin;
	
	private CardLayout card;
	private JPanel detail;
	private JTree tree;
	
	protected InfoDriverPane dinfopane;
	protected InfoProfilePane pinfopane;
	protected InfoClasspathPane cinfopane;
	
	private JButton btnAdd;
	private JButton btnRemove;
	
	public DialogAdministrator(SystemWindow syswin)
	{
		super(syswin, "JDBC administrator", 640,480);
		this.syswin = syswin;
		
		tree = new JTree(new DefaultMutableTreeNode("JDBC",true));
		tree.putClientProperty("JTree.lineStyle", "Angled");
		tree.addTreeSelectionListener(this);
		
		Box bar = new Box(BoxLayout.X_AXIS);
		bar.add(btnAdd = UIUtilities.createCustomButton("add",this));
		bar.add(btnRemove = UIUtilities.createCustomButton("remove",this));
		bar.add(Box.createHorizontalGlue());
		btnRemove.setEnabled(false);
		
		JScrollPane scroll = new JScrollPane(tree);
		scroll.setMaximumSize(new Dimension(220,100));
		scroll.setMinimumSize(new Dimension(220,100));
		scroll.setPreferredSize(new Dimension(220,100));
		
		DefaultPanel side = new DefaultPanel(2,2);
		side.setCenterComponent(scroll);
		side.setSouthComponent(bar);
		
		detail = new JPanel(card = new CardLayout());
		detail.add("level1",cinfopane = new InfoClasspathPane(this));
		detail.add("level2",dinfopane = new InfoDriverPane(this));
		detail.add("level3",pinfopane = new InfoProfilePane(this));
		
		detail.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
		side.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),new EmptyBorder(5,5,5,5)));
		
		getContentPane().add(detail	, BorderLayout.CENTER);
		getContentPane().add(side	, BorderLayout.WEST);
	}

	protected void onRunning()
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("JDBC",true);
		DefaultTreeModel model = new DefaultTreeModel(root,true);
		
		String declared_drivers = UserSession.jdbc.get(UserSession.JDBC_DRIVERS).toString();
		String declared_profiles = UserSession.jdbc.get(UserSession.JDBC_PROFILES).toString();
		
		StringTokenizer k = new StringTokenizer(declared_drivers,";");
		while(k.hasMoreTokens())
		{
			InfoDriverPane.InfoElement dinfo = new InfoDriverPane.InfoElement(k.nextToken());
			dinfo.file = UserSession.jdbc.get(dinfo.name + ".file").toString();
			if(!dinfo.file.startsWith("$")) cinfopane.addArchive(dinfo.file);
			dinfo.driver = UserSession.jdbc.get(dinfo.name + ".driver").toString();
			dinfo.example = UserSession.jdbc.get(dinfo.name + ".example").toString();
			
			root.add(new DefaultMutableTreeNode(dinfo,true));
		}
		
		k = new StringTokenizer(declared_profiles,";");
		while(k.hasMoreTokens())
		{
			String name = k.nextToken();
			String drv = UserSession.jdbc.get(name + ".drv").toString();
			if(drv.startsWith("$"))
			{
				for(Enumeration e = root.children(); e.hasMoreElements();)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
					InfoDriverPane.InfoElement dinfo = (InfoDriverPane.InfoElement)node.getUserObject();
					if(dinfo.name.equals(drv.substring(1)))
					{
						InfoProfilePane.InfoElement pinfo = new InfoProfilePane.InfoElement(dinfo,name);
						pinfo.url = UserSession.jdbc.get(pinfo.name + ".url").toString();
						pinfo.uid = UserSession.jdbc.get(pinfo.name + ".uid").toString();
			
						node.add(new DefaultMutableTreeNode(pinfo,false));
					}
				}
			}
		}
		
		tree.setModel(model);
		tree.setSelectionPath(new TreePath(root));
	}
	
	protected void save()
	{
		DefaultMutableTreeNode needreload = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
		((DefaultTreeModel)tree.getModel()).reload(needreload);
		
		StringBuffer drivers = new StringBuffer();
		StringBuffer profiles = new StringBuffer();
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
		for(int i=0; i<root.getChildCount(); i++)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getChildAt(i);
			drivers.append(node + ";");
			
			InfoDriverPane.InfoElement dinfo = (InfoDriverPane.InfoElement)node.getUserObject();
			UserSession.jdbc.put(dinfo.name+".file",dinfo.file);
			UserSession.jdbc.put(dinfo.name+".driver",dinfo.driver);
			UserSession.jdbc.put(dinfo.name+".example",dinfo.example);
			
			for(int j=0; j<node.getChildCount(); j++)
			{
				DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(j);
				InfoProfilePane.InfoElement pinfo = (InfoProfilePane.InfoElement)child.getUserObject();
				
				profiles.append(pinfo.name + ";");
				
				UserSession.jdbc.put(pinfo.name+".drv","$"+dinfo.name);
				UserSession.jdbc.put(pinfo.name+".url",pinfo.url);
				UserSession.jdbc.put(pinfo.name+".uid",pinfo.uid);
			}
		}
		UserSession.jdbc.put(UserSession.JDBC_DRIVERS,drivers.toString());
		UserSession.jdbc.put(UserSession.JDBC_PROFILES,profiles.toString());
	}
	
	public void valueChanged(TreeSelectionEvent e)
	{
		if(tree.getSelectionCount() == 1)
		{
			int level = e.getPath().getPathCount();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
			
			card.show(detail, "level"+level);
			
			btnRemove.setEnabled(level!=LEVEL_ROOT);
			
			if(level == LEVEL_DRV)
				dinfopane.setInfo((InfoDriverPane.InfoElement)node.getUserObject());
			else if(level == LEVEL_PROF)
				pinfopane.setInfo((InfoProfilePane.InfoElement)node.getUserObject());
		}
	}

	public void actionPerformed(ActionEvent ae)
	{
		int level = -1;

		DefaultTreeModel model = null;
		DefaultMutableTreeNode node = null;

		if(tree.getSelectionCount() == 1)
		{
			level	= tree.getSelectionPath().getPathCount();
			node	= (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
			model	= (DefaultTreeModel)tree.getModel();
		}
				
		if(ae.getSource() == btnAdd)
		{
			if(level==-1) return;
			
			DefaultMutableTreeNode child;
			if(level==LEVEL_ROOT)
			{
				node.add(child = new DefaultMutableTreeNode(new InfoDriverPane.InfoElement(),true));
			}
			else
			{
				if(level==LEVEL_PROF)
				{
					node = (DefaultMutableTreeNode)node.getParent();
					tree.setSelectionPath(new TreePath(node.getPath()));
				}
				
				InfoProfilePane.InfoElement pinfo = new InfoProfilePane.InfoElement((InfoDriverPane.InfoElement)node.getUserObject());
				node.add(child = new DefaultMutableTreeNode(pinfo,false));
			}
			
			model.reload(node);
			tree.setSelectionPath(new TreePath(child.getPath()));
		}
		else if(ae.getSource() == btnRemove)
		{
			if(level==-1 || level == LEVEL_ROOT) return;
			
			tree.setSelectionPath(new TreePath(node.getPreviousNode().getPath()));
			
			if(level == LEVEL_DRV)
			{
				for(int i=0; i<node.getChildCount(); i++)
				{
					DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
					InfoProfilePane.InfoElement pinfo = (InfoProfilePane.InfoElement)child.getUserObject();
					
					UserSession.jdbc.remove(pinfo.name+".drv");
					UserSession.jdbc.remove(pinfo.name+".url");
					UserSession.jdbc.remove(pinfo.name+".uid");
				}
				
				InfoDriverPane.InfoElement dinfo = (InfoDriverPane.InfoElement)node.getUserObject();
				UserSession.jdbc.remove(dinfo.name+".file");
				UserSession.jdbc.remove(dinfo.name+".driver");
				UserSession.jdbc.remove(dinfo.name+".example");
			}
			else
			{
				InfoProfilePane.InfoElement pinfo = (InfoProfilePane.InfoElement)node.getUserObject();
					
				UserSession.jdbc.remove(pinfo.name+".drv");
				UserSession.jdbc.remove(pinfo.name+".url");
				UserSession.jdbc.remove(pinfo.name+".uid");
			}
			
			model.removeNodeFromParent(node);
			DialogAdministrator.this.save();
		}
		else
		{
			super.actionPerformed(ae);
		}
	}
}
