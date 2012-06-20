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

import java.awt.Color;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.sql.Connection;

import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;

import javax.swing.event.InternalFrameEvent;

import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class DesktopEntity extends JInternalFrame implements TaskSource
{
	private JMenu header;
	private JPanel fields;
	
	private Vector fieldsDisplaySize = new Vector();
	
	Vector selectedFields = new Vector();
	Vector joinedFields	= new Vector();
	
	QueryTokens.Table querytoken;

	static Color BGCOLOR_JOINED = new Color(225,235,224);
	static Color BGCOLOR_DEFAULT = Color.white;
	static Color BGCOLOR_SELECTED = UIManager.getColor("Table.selectionBackground");
	static Color BGCOLOR_START_JOIN = new Color(255,230,230);
    //begin vpj-cd e-evolution 05/16/2005
	public DesktopEntity(QueryTokens.Table table)
	//public DesktopEntity(String schema , QueryTokens.Table table)
    //end vpj-cd e-evolution 05/16/2005
	{
		super(table.getName(), false, true);
		querytoken = table;
		
		setLayer(QueryDesktop.PALETTE_LAYER);
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);

		getContentPane().add(fields = new JPanel(new GridLayout(0,1,0,0)));
		
		this.setJMenuBar(new JMenuBar());
		//begin vpj-cd 05/16/2005
		this.setHeader();
		//this.setHeader(schema, table);
		//end vpj-cd 05/16/2005
		header.add(new SelectAll());
		header.add(new DeselectAll());
		header.addSeparator();
		header.add(new OpenAllForeignTables());
		header.add(new OpenAllPrimaryTables());
		header.addSeparator();
		header.add(new References());
		header.addSeparator();
		header.add(new Preview());
	}
	
    
	private void setHeader()	
	{	
		//begin vpj-cd e-evolution 05/16/2005
		//this.getJMenuBar().add(header = new JMenu(querytoken.getAlias()));
		//this.getJMenuBar().add(header = new JMenu(querytoken.getName()));
		org.openXpertya.model.M_Table table = org.openXpertya.model.M_Table.get(org.openXpertya.util.Env.getCtx(),querytoken.getName());
		this.getJMenuBar().add(header = new JMenu(org.openXpertya.util.Msg.getMsg(Env.getCtx(), table.getName())+ " [" + querytoken.getName() + "]" ));
		//end vpj-cd e-evolution 05/16/20005
		
	}
	
	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		header.setEnabled(b);
	}

	public String getHeader()
	{
		return header.getText();
	}
	
	//begin vpj-cd e-evolution 05/16/2005 
	//Field addField(String label, boolean key, int displaySize, String infos)
	Field addField(String label, String desc, boolean key, int displaySize, String infos)
	//end vpj-cd e-evolution 05/16/2005
	{
		String tooltip = label;
		if(infos!=null) tooltip = label + " [ " + infos + " ]";
        //begin vpj-cd e-evolution 05/16/2005
		Field field = new Field(querytoken,label , desc ,key);
		//end vpj-cd e-evolution 05/16/2005		
		field.setToolTipText(tooltip);
		
		field.addMouseListener(new ClickHandler());
		field.setBackground(DesktopEntity.BGCOLOR_DEFAULT);
		field.setFont(UIUtilities.fontPLAIN);
		field.setEnabled(false);
		field.setBorder(null);
		
		if(displaySize == 0) displaySize = 10;
			
		fieldsDisplaySize.addElement(new Integer(displaySize));
		fields.add(field);
		
		return field;
	}
	
	public int findFieldPosition(String label)
	{
		//begin vpj-cd e-evolution 05/16/2005 
		org.openXpertya.model.M_Element element = org.openXpertya.model.M_Element.get(org.openXpertya.util.Env.getCtx(),label);
		//end vpj-cd e-evolution 05/16/2005
		for(int i=0; i<fields.getComponentCount(); i++)
		{
			Field field = (Field)fields.getComponent(i);
			//begin vpj-cd e-evolution 05/16/2005 
			//if(field.getText().equals(label)) return i;
			if(field.getText().equals(org.openXpertya.util.Msg.getMsg(Env.getCtx(),element.getName()))) return i;
			//end vpj-cd e-evolution 05/16/2005
		}
		
		return -1;		
	}
	
	Field getField(String label)
	{
		int index = findFieldPosition(label);
		return index!=-1?(Field)fields.getComponent(index):null;
	}
	
	QueryDesktop getQueryDesktop()
	{
		return (QueryDesktop)this.getDesktopPane();
	}
	
	QueryBuilder getQueryBuilder()
	{
		return getQueryDesktop().builder;
	}
	
	protected void fireInternalFrameEvent(int id)
	{
		if(id == InternalFrameEvent.INTERNAL_FRAME_CLOSING)
		{
			for(int i=0; i<fields.getComponentCount(); i++)
			{
				Field field = (Field)fields.getComponent(i);
				
				this.getQueryBuilder().listbar.removeItem(field.querytoken.toString(false,QueryBuilder.useColumnAutoAlias));
				this.getQueryBuilder().clauses.removeColumn(field.querytoken.toString(QueryBuilder.useIdentifierQuote,false));
			}
			
			this.getQueryDesktop().removeAllRelation(this);
			this.getQueryBuilder().syntax.refresh();			
		}
		
		super.fireInternalFrameEvent(id);
	}
	
	void workModeChanged()
	{
		for(int i=0; i<fields.getComponentCount(); i++)
		{
			fields.getComponent(i).setBackground(BGCOLOR_DEFAULT);
		}
		
		Color higlightColor = this.getQueryBuilder().isJoinMode() ?  BGCOLOR_JOINED : BGCOLOR_SELECTED;
		Vector higlights	= this.getQueryBuilder().isJoinMode() ? joinedFields : selectedFields;
		
		for(int i=0; i<higlights.size(); i++)
			((Field)higlights.elementAt(i)).setBackground(higlightColor);
	}

	void joined(Field column)
	{
		if(this.getQueryBuilder().isJoinMode())
			column.setBackground(BGCOLOR_JOINED);
		
		joinedFields.addElement(column);
	}

	void unjoined(Field column)
	{
		joinedFields.removeElement(column);
		column.setBackground(BGCOLOR_DEFAULT);
		
		if(this.getQueryBuilder().isJoinMode())
		{
			if(joinedFields.contains(column))
				column.setBackground(BGCOLOR_JOINED);
		}
		else if(selectedFields.contains(column))
			column.setBackground(BGCOLOR_SELECTED);
	}
	
	void setSelectionStatus(String colName, boolean status)
	{
		Field col = getField(colName);
		if(col!=null) setSelectionStatus(col,status);
	}
	
	private void setSelectionStatus(Field field, boolean status)
	{
		if(status)
		{
			if(!selectedFields.contains(field))
			{
				selectedFields.addElement(field);
			
				if(!this.getQueryBuilder().isJoinMode())
					field.setBackground(DesktopEntity.BGCOLOR_SELECTED);
	
				this.getQueryBuilder().listbar.addColumn(field.querytoken);
			}			
		}
		else
		{
			if(selectedFields.contains(field))
			{
				selectedFields.removeElement(field);
			
				if(!this.getQueryBuilder().isJoinMode())
					field.setBackground(DesktopEntity.BGCOLOR_DEFAULT);
					
				this.getQueryBuilder().listbar.removeItem(field.querytoken.toString(false,QueryBuilder.useColumnAutoAlias));
			}			
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	TaskSource Interface
//	/////////////////////////////////////////////////////////////////////////////
	public Connection getConnection()
	{
		return this.getQueryBuilder().getConnection();
	}
	
	public String getSyntax()
	{
		return "SELECT * FROM " + querytoken.toString();
	}

//	/////////////////////////////////////////////////////////////////////////////
//	Field
//	/////////////////////////////////////////////////////////////////////////////
	public class Field extends JCheckBox
	{
		QueryTokens.Column querytoken;
		
		//begin vpj-cd e-evolution 05/16/2005
		//public Field(QueryTokens.Table owner, String name, boolean iskey)
		//{
		//	this(new QueryTokens.Column(owner, name),iskey);
		//}		
		public Field(QueryTokens.Table owner, String name, String desc, boolean iskey)
		{			
			this(new QueryTokens.Column(owner, name, desc),iskey);
		}
		//end vpj-cd 05/16/2005
		
		
		
		public Field(QueryTokens.Column querytoken, boolean iskey)
		{
			//begin vpj-cd e-evolution 05/16/2005
			//super(querytoken.getName(),iskey);
			super(querytoken.getDesc(),iskey);
			//end vpj-cd 05/16/2005			
			this.querytoken = querytoken;
			this.setOpaque(true);
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	Highlights
//	/////////////////////////////////////////////////////////////////////////////
	private class ClickHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent evt)
		{
			if(evt.getModifiers() == MouseEvent.BUTTON3_MASK) return;
			
			Field col = (Field)evt.getSource();
			
			if(DesktopEntity.this.getQueryBuilder().isJoinMode())
				DesktopEntity.this.getQueryDesktop().join(DesktopEntity.this, col);
			else
			{
				if(selectedFields.contains(col))
					DesktopEntity.this.setSelectionStatus(col, false);
				else
					DesktopEntity.this.setSelectionStatus(col, true);
			}
		}
	}

//	/////////////////////////////////////////////////////////////////////////////
//	Menu Actions
//	/////////////////////////////////////////////////////////////////////////////
	private class SelectAll extends AbstractAction
	{
		private SelectAll()
		{
			super("select all");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			for(int i=0; i<fields.getComponentCount(); i++)
				DesktopEntity.this.setSelectionStatus((Field)fields.getComponent(i), true);		
		}
	}
	
	private class DeselectAll extends AbstractAction
	{
		private DeselectAll()
		{
			super("deselect all");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			for(int i=0; i<fields.getComponentCount(); i++)
				DesktopEntity.this.setSelectionStatus((Field)fields.getComponent(i), false);		
		}
	}
	
	private class OpenAllForeignTables extends AbstractAction
	{
		private OpenAllForeignTables()
		{
			super("open all foreign tables");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			DesktopLoader.run(DesktopLoader.ALL_FOREIGN_TABLES, DesktopEntity.this.getQueryDesktop(), DesktopEntity.this.querytoken, true);
		}
	}
	
	private class OpenAllPrimaryTables extends AbstractAction
	{
		private OpenAllPrimaryTables()
		{
			super("open all primary tables");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			DesktopLoader.run(DesktopLoader.ALL_PRIMARY_TABLES, DesktopEntity.this.getQueryDesktop(), DesktopEntity.this.querytoken, true);
		}
	}
	
	private class References extends AbstractAction
	{
		private References()
		{
			super("references...");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			new DialogReferences(DesktopEntity.this).setVisible(true);
		}
	}
	
	boolean continueWithPreview = false;
	private class Preview extends AbstractAction implements Runnable
	{
		private Preview()
		{
			super("preview...");
		}
		
		public void run()
		{
			continueWithPreview = false;
			
			new DialogCount(DesktopEntity.this.getHeader(), DesktopEntity.this).setVisible(true);
			
			if(continueWithPreview) new QueryPreview(DesktopEntity.this.getHeader(),DesktopEntity.this).setVisible(true);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			new Thread(this).start();
		}
	}
}
