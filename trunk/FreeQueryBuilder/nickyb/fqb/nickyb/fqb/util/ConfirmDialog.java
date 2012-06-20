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


import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

public abstract class ConfirmDialog extends ModalDialog
{
	protected JButton btnConfirm;
	
	protected ConfirmDialog(Component owner, String title)
	{
		this(owner, title, INITIAL_WIDTH, INITIAL_HEIGHT);
	}
	
	protected ConfirmDialog(Component owner, String title, int width, int height)
	{
		super(owner, title, new Dimension(width, height));
		
		bar.add(btnConfirm = UIUtilities.createCustomButton("ok",this),1);
	}
	
	protected abstract boolean onConfirm();
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand().equals("ok"))
		{
			if(!onConfirm()) return;
		}
		
		super.actionPerformed(ae);
	}
}
