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

import java.awt.Cursor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class QueryPreview extends JFrame implements ActionListener, TaskTarget
{
	private StringBuffer resultBuffer;
	private Thread queryThread;

	private JButton stop;
	private JLabel status;
	private JTextArea output;
	
	public QueryPreview(String title, TaskSource query)
	{
		super(UIUtilities.getTitle(title));
		
		DefaultPanel pnlSouth = new DefaultPanel();
		pnlSouth.setBorder(new CompoundBorder(LineBorder.createGrayLineBorder(), new EmptyBorder(3,4,3,4)));
		pnlSouth.setCenterComponent(status = new JLabel());
		pnlSouth.setEastComponent(stop = UIUtilities.createCustomButton("stop",this));
		
		DefaultPanel contentPane = new DefaultPanel(2,2);
		contentPane.setCenterComponent(new JScrollPane(output = new JTextArea()));
		contentPane.setSouthComponent(pnlSouth);
		
		setContentPane(contentPane);
		setSize(640,480);
		
		UIUtilities.centerOnScreen(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		WindowListener wl = new WindowAdapter()
		{
			public void windowClosing(WindowEvent we)
			{
				QueryPreview.this.queryThread = null;
				QueryPreview.this.dispose();
			}
		};
		addWindowListener(wl);
		
		output.setTabSize(4);
		output.setEditable(false);
		output.setText(query.getSyntax());
		
		startThread(query);
	}
	
	private void startThread(TaskSource query)
	{
		queryThread = new Thread(new Task(query,this));
		queryThread.start();
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		stop.setEnabled(false);
		queryThread = null;
	}
	
	public boolean continueRun()
	{
		return queryThread != null;
	}

	public void onTaskFinished()
	{
		actionPerformed(null);
				
		output.setText(resultBuffer.toString());
		output.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void onTaskStarting()
	{
		output.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		resultBuffer = new StringBuffer();
	}

	public StringBuffer getOutputBuffer()
	{
		return resultBuffer;
	}

	public void message(String text)
	{
		status.setText(text);
	}
}
