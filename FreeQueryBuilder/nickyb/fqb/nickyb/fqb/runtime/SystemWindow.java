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
import nickyb.fqb.ext.ViewBuildReport;
import nickyb.fqb.util.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//begin vpj e-evolution 05/19/2005
import org.openXpertya.db.*;
import org.openXpertya.util.Env;


public class SystemWindow extends JInternalFrame
//public class SystemWindow extends JFrame
//end vpj e-evolution 05/19/2005
{
	private CardLayout card;
	private JPanel container;
	
	public ConnectionManager manager = new ConnectionManager();
	InfoProfilePane.InfoElement pinfo;
	
	ViewBuilder builder;
	ViewCommand command;
	ViewHistory history;
	ViewBuildReport report;
	
	SystemToolbar toolbar;
	
	
	public SystemWindow(String drv, String url, String uid, String pwd)
	{
		super(UIUtilities.getTitle(null));
		
		UserSession.load();
		//begin vpj-cd e-evolution 05/16/2005
		//UIUtilities.fullScreen(this);
		//end vpj-cd e-evolution 05/16/2005

		builder = new ViewBuilder(this);
		command = new ViewCommand(this);
		history = new ViewHistory(this);
		report = new ViewBuildReport(this);
		
		toolbar = new SystemToolbar(this);
		
		container = new JPanel(card = new CardLayout());
		container.add("window.builder", builder);
		container.add("window.command", command);
		container.add("window.history", history);
		container.add("window.report", report);
		
		getContentPane().add(container	,BorderLayout.CENTER);
		getContentPane().add(toolbar	, BorderLayout.NORTH);
		
		if(drv != null && url != null)
			openConnection(drv,url,uid,pwd);
		
		//begin vpj-cd e-evolution 05/16/2005
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//end vpj-cd e-evolution 05/16/2005
		setVisible(true);
		
		WindowListener wl = new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				UserSession.save();
			}
		};
		//begin vpj-cd e-evolution 05/16/2005
		//addWindowListener(wl);
		//end vpj-cd e-evolution 05/16/2005
	}
	
	void view(String identifier)
	{
		card.show(container,identifier);
		if(identifier.equals("window.builder")) builder.getButton().setSelected(true);
		if(identifier.equals("window.command")) command.getButton().setSelected(true);
		if(identifier.equals("window.history")) history.getButton().setSelected(true);
		if(identifier.equals("window.report")) report.getButton().setSelected(true);
	}
	
	public void runQuery()
	{
		QueryModel model = builder.getModelClone();
		//begin vpj-cd e-evolution  05/19/2005
		Env.setContext(Env.getCtx(),"Query",model.toString());
		//end vpj-cd e-evolution  05/19/2005
		if(runQuery(model))
			history.add(model);
	}

	public boolean runQuery(QueryModel model)
	{
		if(builder.getConnection()==null)
		{
			JOptionPane.showMessageDialog(this, "No connection!", "FreeQueryBuilder", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if(model==null)
		{
			JOptionPane.showMessageDialog(this, "Query is empty!", "FreeQueryBuilder", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else
		{
			new QueryPreview("Preview", new QueryTask(builder,model)).setVisible(true);
			return true;
		}
	}

	public Connection getConnection()
	{
	    return builder.getConnection();
	}
	
	public void closeConnection()
	{
		try
		{
			if(builder!=null && builder.getConnection() != null)
				builder.getConnection().close();
		}
		catch(SQLException sqle)
		{
		}
		
		builder.setConnection(null);
		toolbar.connectionChanged();
	}
	
	public boolean openConnection(String drv, String url, String uid, String pwd)
	{
		InfoDriverPane.InfoElement dinfo = new InfoDriverPane.InfoElement();
		dinfo.driver = drv;
		
		InfoProfilePane.InfoElement pinfo = new InfoProfilePane.InfoElement(dinfo);
		pinfo.url = url;
		pinfo.uid = uid;
		
		return openConnection(pinfo, pwd);
	}
	
	boolean openConnection(InfoProfilePane.InfoElement pinfo, String pwd)
	{
		URLClassLoader loader = null;
		
		if(pinfo.dinfo.file!=null && !pinfo.dinfo.file.equals("$CLASSPATH"))
		{
			File file = new File(pinfo.dinfo.file);
			if(file.exists())
			{
				try
				{
					loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, ClassLoader.getSystemClassLoader());
				}
				catch(MalformedURLException mue)
				{
					mue.printStackTrace();
				}
			}
		}
				
		return openConnection(loader,pinfo,pwd);
	}
	
	private boolean openConnection(ClassLoader l, InfoProfilePane.InfoElement pinfo, String pwd)
	{
		Connection connection = null;
		
		closeConnection();
		this.pinfo = pinfo;
		
		try
		{
			manager = new ConnectionManager();
			manager.setInfo(pinfo.dinfo.driver,pinfo.url,pinfo.uid,pwd);
			manager.setLoader(l);
			
			connection = manager.open();
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, e.toString(), "Exception", 0);
			return false;
		}

		builder.setConnection(connection);
		toolbar.connectionChanged();
		
		return true;
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	Entry-Point
//	/////////////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	{
		String drv,url,uid,pwd;
		drv=url=uid=pwd=null;
		
		if(args.length > 3)
			pwd = args[3];
		
		if(args.length > 2)
			uid = args[2];
		
		if(args.length > 1)
			url = args[1];
		
		if(args.length > 0)
			drv = args[0];

		new SystemWindow(drv,url,uid,pwd).show();
	}
}
