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

package nickyb.fqb.util;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class DefaultScrollPane extends DefaultPanel
{
	public final static Color headerbackground = new Color(204,204,255);
	public final static Border hideBottomLine = new CustomLineBorder(true,true,false,true);
	
	private JScrollPane scroll;
	
	public DefaultScrollPane(String header, JComponent view, boolean opaque)
	{
		this(new JLabel(header),view,opaque);
	}
	
	public DefaultScrollPane(JComponent header, JComponent view, boolean opaque)
	{
		header.setOpaque(opaque);
		header.setBorder(UIUtilities.NO_BORDER);
		for(int i=0; i<header.getComponentCount(); i++)
		{
			if(header.getComponent(i) instanceof JComponent)
			{
				JComponent innerheader = (JComponent)header.getComponent(i);
				innerheader.setBorder(UIUtilities.NO_BORDER);
				innerheader.setOpaque(opaque);
			}
		}
		
		DefaultPanel headerpane = new DefaultPanel();
		headerpane.setBorder(new CompoundBorder(hideBottomLine, new EmptyBorder(1,1,1,1)));
		if(!opaque) headerpane.setBackground(headerbackground);
		headerpane.setCenterComponent(header);
		
		setNorthComponent(headerpane);
		setView(view);
	}
	
	public void setView(JComponent view)
	{
		setCenterComponent(scroll = new JScrollPane(view));
	}
	
	public void setHorizontalScrollBarVisible(boolean b)
	{
		scroll.setHorizontalScrollBarPolicy(b?JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED:JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	public void setVerticalScrollBarVisible(boolean b)
	{
		scroll.setVerticalScrollBarPolicy(b?JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED:JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}
}
