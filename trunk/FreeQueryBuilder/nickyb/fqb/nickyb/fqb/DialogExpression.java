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

import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class DialogExpression extends ConfirmDialog
{
	private ExpressionCellEditor cell;
	
	private JList columns;
	private JList functions;
	
	private JTextArea editor;
	
	public DialogExpression(ExpressionCellEditor cell)
	{
		super(cell.owner.getComponent(), "edit expression", 450, 270);
		this.cell = cell;
		
		DefaultPanel pnlCenter = new DefaultPanel();
		
		JScrollPane scroll;
		editor = new JTextArea(cell.getCellEditorText());
		editor.setWrapStyleWord(true);
		editor.setLineWrap(true);
		
		pnlCenter.setCenterComponent(scroll = new JScrollPane(editor));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		Box south = new Box(BoxLayout.X_AXIS);
		south.add(scroll = new JScrollPane(functions = new JList(cell.owner.getFunctions())));
//		south.add(scroll = new JScrollPane(functions = new JList()));
		scroll.setBorder(new CompoundBorder(new TitledBorder("functions"), new EtchedBorder(EtchedBorder.LOWERED)));
		
		Dimension dim = scroll.getPreferredSize();
		dim.width = 150;
		
		scroll.setPreferredSize(dim);
		scroll.setMaximumSize(dim);
		scroll.setMinimumSize(dim);
		
		south.add(scroll = new JScrollPane(columns = new JList(cell.owner.getColumns())));
//		south.add(scroll = new JScrollPane(columns = new JList()));
		scroll.setBorder(new CompoundBorder(new TitledBorder("columns"), new EtchedBorder(EtchedBorder.LOWERED)));
		
		columns.addMouseListener(new MouseDoubleClick());
		functions.addMouseListener(new MouseDoubleClick());
		
		pnlCenter.setSouthComponent(south);
		this.getContentPane().add(pnlCenter);
	}
	
	protected boolean onConfirm()
	{
		cell.setCellEditorText(editor.getText());
		return true;
	}
	
	protected void onRunning()
	{
	}
	
	private class MouseDoubleClick extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if(e.getClickCount()==2)
			{
				JList l = (JList)e.getSource();
				String toAdd = l.getSelectedValue().toString();
				editor.replaceSelection(toAdd);
				
				if(l == DialogExpression.this.functions)
				{
					editor.replaceSelection("()");
					editor.setCaretPosition(editor.getCaretPosition()-1);
				}
			}
		}
	}
}
