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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import javax.swing.border.LineBorder;

public class DesktopRelation extends JButton implements ActionListener
{
	QueryDesktop owner;
	
	DesktopEntity leftEntity;
	DesktopEntity.Field leftField;
	
	DesktopEntity rightEntity;
	DesktopEntity.Field rightField;
	
	Point start;
	Point end;
	
	private int jointype = INNER_JOIN;
	
	static final int INNER_JOIN			= 0;
	static final int LEFT_OUTER_JOIN	= 1;
	static final int RIGHT_OUTER_JOIN	= 2;
	static final int FULL_OUTER_JOIN	= 3;
	
	DesktopRelation(QueryDesktop owner)
	{
		super("=");
		
		this.owner = owner;
		this.setSize(20,20);
		this.setBackground(Color.yellow);
		this.setBorder(LineBorder.createBlackLineBorder());
		this.addActionListener(this);
		this.addMouseListener(new RightClickHandler());
	}
	
	boolean isInner()
	{
		return jointype == INNER_JOIN;
	}
	
	boolean isLeft()
	{
		return jointype == LEFT_OUTER_JOIN;
	}
	
	boolean isRight()
	{
		return jointype == RIGHT_OUTER_JOIN;
	}
	
	boolean isFull()
	{
		return jointype == FULL_OUTER_JOIN;
	}
	
	int getType()
	{
		return jointype;
	}
	
	void setType(int jointype)
	{
		this.jointype = jointype;
		owner.repaint();
	}
	
	void remove()
	{
		owner.removeRelation(this);
	}

	public void actionPerformed(ActionEvent e)
	{
		new DialogJoin(this,false).setVisible(true);
		owner.builder.syntax.refresh();
	}
	
	void drawIt(Graphics g)
	{
		int xL = (int)leftEntity.getLocationOnScreen().getX() - (int)owner.getLocationOnScreen().getX();
		int yL = (int)leftField.getLocationOnScreen().getY() - (int)owner.getLocationOnScreen().getY();
		
		int xR = (int)rightEntity.getLocationOnScreen().getX() - (int)owner.getLocationOnScreen().getX();
		int yR = (int)rightField.getLocationOnScreen().getY() - (int)owner.getLocationOnScreen().getY();
		
		yL = yL + (leftField.getSize().height/2);
		yR = yR + (rightField.getSize().height/2);
		
		int xPlus = 0;
		int yPlus = 0;
		
		int xMinus = 0;
		int yMinus = 0;
		
		Color plusColor;
		Color minusColor;
		if(xL > xR)
		{
			plusColor = this.isRight() || this.isFull() ? Color.green : Color.black;
			minusColor = this.isLeft() || this.isFull() ? Color.green : Color.black;
			
			xPlus = xR + rightEntity.getSize().width;
			yPlus = yR;
			
			xMinus = xL;
			yMinus = yL;
		}
		else
		{
			plusColor = this.isLeft() || this.isFull() ? Color.green : Color.black;
			minusColor = this.isRight() || this.isFull() ? Color.green : Color.black;
			
			xPlus = xL + leftEntity.getSize().width;
			yPlus = yL;
			
			xMinus = xR;
			yMinus = yR;
		}

		start = new Point(xPlus + 10, yPlus);
		end = new Point(xMinus - 10, yMinus);
		
		Point center = new Point(start.x + ((end.x-start.x)/2), start.y + ((end.y-start.y)/2));
		
		g.setColor(plusColor);
		g.drawLine( xPlus, yPlus, start.x, start.y);
		g.drawLine( start.x, start.y, center.x, center.y);
		
		g.setColor(minusColor);
		g.drawLine( xMinus, yMinus, xMinus - 10, yMinus);
		g.drawLine( center.x, center.y, end.x, end.y);

		this.setLocation(center.x - 10, center.y - 10);
	}
	
	private class RightClickHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			if(!SwingUtilities.isRightMouseButton(e)) return;
			
			new DialogJoin(DesktopRelation.this,true).setVisible(true);
		}
	}
}
