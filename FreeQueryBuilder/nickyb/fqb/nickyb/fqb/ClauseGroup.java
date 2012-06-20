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

import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

public class ClauseGroup extends JPanel implements ActionListener
{
	static final String PUT		= "put";
	static final String PUSH	= "push";
	static final String UP		= "up";
	static final String DOWN	= "down";
	
	JList availableColumns;
	JList selectedColumns;
	
	QueryClauses clauses;
	
	ClauseGroup(QueryClauses clauses)
	{
		super(new GridLayout(1,2));
		this.clauses = clauses;
		
		JToolBar bar;
		JScrollPane scroll;
		
		DefaultPanel pnl;
		
		this.add(pnl = new DefaultPanel());
		pnl.setCenterComponent(scroll = new JScrollPane(availableColumns = new JList(new DefaultListModel())));
		pnl.setEastComponent(bar = new JToolBar(JToolBar.VERTICAL));
		bar.add(createButton("put >", PUT));
		bar.add(createButton("< push", PUSH));
		bar.setFloatable(false);
		
		this.add(pnl = new DefaultPanel());
		pnl.setCenterComponent(scroll = new JScrollPane(selectedColumns = new JList(new DefaultListModel())));
		pnl.setEastComponent(bar = new JToolBar(JToolBar.VERTICAL));
		bar.add(createButton("up", UP));
		bar.add(createButton("down", DOWN));
		bar.setFloatable(false);
	}
	
	private JButton createButton(String text, String command)
	{
		JButton btn = UIUtilities.createCustomButton(text,this);
		btn.setActionCommand(command);
		
		return btn;
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals(PUT))
		{
			while(availableColumns.getSelectedIndex()!=-1)
			{
				String col = (String)availableColumns.getSelectedValue();
				setAsSelected(col);
			}
		}
		else if(ae.getActionCommand().equals(PUSH))
		{
			while(selectedColumns.getSelectedIndex()!=-1)
			{
				String col = (String)selectedColumns.getSelectedValue();
				((DefaultListModel)selectedColumns.getModel()).removeElement(col);
				((DefaultListModel)availableColumns.getModel()).addElement(col);
			}
		}
		else if(ae.getActionCommand().equals(UP))
		{
			int index = selectedColumns.getSelectedIndex();
			if(index<1) return;
			Object moveUp	= ((DefaultListModel)selectedColumns.getModel()).elementAt(index);
			Object moveDown = ((DefaultListModel)selectedColumns.getModel()).elementAt(index-1);
			((DefaultListModel)selectedColumns.getModel()).setElementAt(moveUp,index-1);
			((DefaultListModel)selectedColumns.getModel()).setElementAt(moveDown,index);
			selectedColumns.setSelectedValue(moveUp,true);
		}
		else if(ae.getActionCommand().equals(DOWN))
		{
			int index = selectedColumns.getSelectedIndex();
			if(index==selectedColumns.getModel().getSize()-1) return;
			Object moveDown = ((DefaultListModel)selectedColumns.getModel()).elementAt(index);
			Object moveUp	= ((DefaultListModel)selectedColumns.getModel()).elementAt(index+1);
			((DefaultListModel)selectedColumns.getModel()).setElementAt(moveDown,index+1);
			((DefaultListModel)selectedColumns.getModel()).setElementAt(moveUp,index);
			selectedColumns.setSelectedValue(moveDown,true);
		}
		
		clauses.builder.syntax.refresh();
	}
	
	void clean()
	{
		((DefaultListModel)availableColumns.getModel()).removeAllElements();
		((DefaultListModel)selectedColumns.getModel()).removeAllElements();
	}
	
	void addColumn(String col)
	{
		((DefaultListModel)availableColumns.getModel()).addElement(col);
	}
	
	void removeColumn(String col)
	{
		((DefaultListModel)availableColumns.getModel()).removeElement(col);
		((DefaultListModel)selectedColumns.getModel()).removeElement(col);
	}
	
	void setAsSelected(String col)
	{
		((DefaultListModel)availableColumns.getModel()).removeElement(col);
		((DefaultListModel)selectedColumns.getModel()).addElement(col);
	}	
}
