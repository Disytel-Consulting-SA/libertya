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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;

public class DefaultPanel extends javax.swing.JPanel
{
	public DefaultPanel()
	{
		this(0,0);
	}

	public DefaultPanel(int hgap, int vgap)
	{
		super(new BorderLayout(hgap,vgap));
	}
	
	public final void setLayout(LayoutManager mgr)
	{
		if(!(mgr instanceof BorderLayout))
			throw new IllegalArgumentException("this is a BorderLayout panel");
		
		super.setLayout(mgr);
	}
	
	public final void add(Component comp, Object constraints)
	{
		if(!(this.getLayout() instanceof BorderLayout))
			super.setLayout(new BorderLayout());
		
		super.add(comp,constraints);
	}

	public void setNorthComponent(Component comp)
	{
		add(comp, BorderLayout.NORTH);
	}
	
	public void setSouthComponent(Component comp)
	{
		add(comp, BorderLayout.SOUTH);
	}

	public void setCenterComponent(Component comp)
	{
		add(comp, BorderLayout.CENTER);
	}
	
	public void setEastComponent(Component comp)
	{
		add(comp, BorderLayout.EAST);
	}
	
	public void setWestComponent(Component comp)
	{
		add(comp, BorderLayout.WEST);
	}
}
