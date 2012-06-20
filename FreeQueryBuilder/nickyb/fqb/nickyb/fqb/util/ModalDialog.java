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
import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;

import javax.swing.border.EmptyBorder;

public abstract class ModalDialog extends JDialog implements ActionListener, Runnable
{
	protected static final int INITIAL_WIDTH = 320;
	protected static final int INITIAL_HEIGHT = 200;
	
	protected Box bar;
	protected JButton btnExit;
	
	protected ModalDialog(Component owner, String title)
	{
		this(owner, title, INITIAL_WIDTH, INITIAL_HEIGHT);
	}
	
	protected ModalDialog(Component owner, String title, int width, int height)
	{
		this(owner, title, new Dimension(width, height));
	}
	
	protected ModalDialog(Component owner, String title, Dimension size)
	{
		super(UIUtilities.getFrameAncestor(owner), title,true);
		
		DefaultPanel pnlContent = new DefaultPanel(3,3);
		pnlContent.setBorder(new EmptyBorder(3,3,3,3));
		
		bar = new Box(BoxLayout.X_AXIS);
		bar.add(Box.createHorizontalGlue());
		bar.add(btnExit = UIUtilities.createCustomButton("exit",this));
		
		pnlContent.setSouthComponent(bar);
		
		setContentPane(pnlContent);
		setSize(size.width,size.height);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		WindowListener wl = new WindowAdapter()
		{
			public void windowOpened(WindowEvent we)
			{
				new Thread(ModalDialog.this).start();
			}
		};
		this.addWindowListener(wl);
		
		UIUtilities.centerOnScreen(this);
	}
	
	protected void setBarEnabled(boolean b)
	{
		for(int i=0; i<bar.getComponentCount(); i++)
		{
			bar.getComponent(i).setEnabled(b);
		}
	}
	
	public final void run()
	{
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		setBarEnabled(false);
		onRunning();
		setBarEnabled(true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	protected abstract void onRunning();
	
	public void actionPerformed(ActionEvent ae)
	{
		this.dispose();
	}
}
