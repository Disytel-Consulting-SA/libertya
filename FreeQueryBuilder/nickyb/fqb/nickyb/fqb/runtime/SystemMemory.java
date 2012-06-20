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

import nickyb.fqb.util.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class SystemMemory extends JButton implements Runnable, ActionListener
{
	private Thread me;
	private JProgressBar bar;
	
	public SystemMemory()
	{
		addActionListener(this);
		setBorder(new CompoundBorder(new EmptyBorder(2,0,2,0), new EtchedBorder(EtchedBorder.RAISED)));
		
		add(bar = new JProgressBar());
		bar.setBorder(new CompoundBorder(new EmptyBorder(0,5,0,5), new EtchedBorder(EtchedBorder.LOWERED)));
		
		me = new Thread(this);
		me.start();
	}

	public void run()
	{
		while(true)
		{
			Runtime r = Runtime.getRuntime();
			
			float freeMemory	= (float) r.freeMemory();
			float totalMemory	= (float) r.totalMemory();
			
			int allocated	= (int)(totalMemory/1024);
			int used		= (int)((totalMemory - freeMemory)/1024);
			
			bar.setMaximum(allocated);
			bar.setValue(used);
			
			String toolTip = String.valueOf( NumberFormat.getInstance().format(used) ) + "K used";
			toolTip += " / ";
			toolTip += String.valueOf( NumberFormat.getInstance().format(allocated) ) + "K allocated ";
			
			bar.setToolTipText(toolTip);
			
			try
			{
				if(Thread.currentThread() == me)
//					me.sleep(1975);
				    Thread.sleep(1975);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent e)
	{
		if(JOptionPane.showConfirmDialog(UIUtilities.getFrameAncestor(this),"Invoke garbage collector ?","InfoMemory", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			System.gc();
	}
}
