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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DialogJoin extends ConfirmDialog
{
	JCheckBox allLeft;
	JCheckBox allRight;
	JComboBox operator;
	
	DesktopRelation relation;
	
	public DialogJoin(DesktopRelation relation, boolean deletemode)
	{
		super(relation, (deletemode ? "confirm delete join" : "edit join"));
		this.relation = relation;
		
		JLabel left = new JLabel(relation.leftField.querytoken.toString(false,false), JLabel.CENTER);
		JLabel right = new JLabel(relation.rightField.querytoken.toString(false,false), JLabel.CENTER);
		
		Border border = new CompoundBorder(LineBorder.createBlackLineBorder(), new EmptyBorder(3,4,3,4));
		left.setBorder(border);
		left.setOpaque(true);
		left.setBackground(DesktopEntity.BGCOLOR_START_JOIN);
		right.setBorder(border);
		right.setOpaque(true);
		right.setBackground(DesktopEntity.BGCOLOR_JOINED);
		
		operator = new JComboBox(new String[]{"=","<",">","<=",">=","<>"});
		operator.setSelectedItem(relation.getText());
		
		GridBagLayout gbl = new GridBagLayout();
		JPanel pane = new JPanel(gbl);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets	= new Insets(0, 0, 3, 0);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		
		gbc.weightx	= 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(left, gbc);
		pane.add(left);
		
		gbc.weightx	= 0.0;
		gbc.fill = GridBagConstraints.NONE;
		gbl.setConstraints(operator, gbc);
		pane.add(operator);
		
		gbc.weightx	= 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		gbl.setConstraints(right, gbc);
		pane.add(right);
		
		gbc.insets	= new Insets(0,0,0,0);
		allLeft = new JCheckBox("all rows from " + relation.leftEntity.querytoken.getName(), (relation.isLeft() || relation.isFull()));
		pane.add(allLeft);
		gbl.setConstraints(allLeft, gbc);
		
		allRight = new JCheckBox("all rows from " + relation.rightEntity.querytoken.getName(), (relation.isRight() || relation.isFull()));
		pane.add(allRight);
		gbl.setConstraints(allRight, gbc);
		
		allLeft.setEnabled(!deletemode);
		allRight.setEnabled(!deletemode);
		operator.setEnabled(!deletemode);
		
		getContentPane().add(pane);
	}
	
	protected void onRunning() {return;}

	protected boolean onConfirm()
	{
		if(operator.isEnabled())
		{
			int jointype = 0;
			
			jointype += allLeft.isSelected() ? DesktopRelation.LEFT_OUTER_JOIN : 0;
			jointype += allRight.isSelected() ? DesktopRelation.RIGHT_OUTER_JOIN : 0;
			
			relation.setText(operator.getSelectedItem().toString());
			relation.setType(jointype);
		}
		else
		{
			relation.owner.removeRelation(relation);
		}
		
		return true;
	}
}
