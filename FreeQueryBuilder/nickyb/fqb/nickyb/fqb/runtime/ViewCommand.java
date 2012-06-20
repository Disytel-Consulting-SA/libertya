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

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionEvent;

import java.sql.Connection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import javax.swing.text.DefaultEditorKit;

public class ViewCommand extends DefaultScrollPane implements SystemView, TaskSource, TaskTarget
{
	private SystemWindow syswin;
	private JToggleButton switchbutton;
	
	private final static String PROMPT_START = ">>>";
	private final static String PROMPT_CONTINUE = " ->";
	
	private int startmark;
	private int breakmarks[];

	private Thread queryThread;
	private StringBuffer resultBuffer;

	private JTextArea console;
	private JLabel status;
	private JButton stop;
	
	public ViewCommand(SystemWindow syswin)
	{
		super("sql command",null,false);
		this.syswin = syswin;
		
		setView(console = new JTextArea());

		console.setTabSize(4);
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setFont(new Font("monospaced", Font.PLAIN, 13));

		console.getKeymap().removeBindings();
		console.getKeymap().setDefaultAction(new DefaultKeyTypedAction());
		
		console.getActionMap().put(DefaultEditorKit.backwardAction			, new BackwardAction());
		console.getActionMap().put(DefaultEditorKit.insertBreakAction		, new InsertBreakAction());
		console.getActionMap().put(DefaultEditorKit.deletePrevCharAction	, new DeletePrevCharAction());
		console.getActionMap().put(DefaultEditorKit.deleteNextCharAction	, new DeleteNextCharAction());
		
		console.getActionMap().put(DefaultEditorKit.cutAction , new CutAction());
		console.getActionMap().put(DefaultEditorKit.pasteAction , new PasteAction());
		
		putDummieAction(DefaultEditorKit.upAction);
		putDummieAction(DefaultEditorKit.downAction);

		JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
		toolbar.setFloatable(false);
		
		stop = toolbar.add(new ActionStopTask());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new CopyAction());
		toolbar.add(new PasteAction());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new ActionClean());
		
		setEastComponent(toolbar);
		setSouthComponent(status = new JLabel("..."));

		switchbutton = new JToggleButton(new ActionSwitch());
		appendStart();
	}
	
	private void clean()
	{
		console.setText(null);
		appendStart();
	}
	
	private boolean isCaretPositionEqualsMark()
	{
		return console.getCaretPosition() == startmark;
	}
	
	private boolean isCaretPositionValid()
	{
		return console.getCaretPosition() >= startmark && isHighlightValid();
	}
	
	private boolean isHighlightValid()
	{
		return console.getCaret().getDot() >= startmark && console.getCaret().getMark() >= startmark;
	}
	
	private void performCommand()
	{
		queryThread = new Thread(new Task(this,this));
		queryThread.start();
	}
	
	private void putDummieAction(String action)
	{
		console.getActionMap().put(action, new DummieAction());
	}
	
	private void appendStart()
	{
		breakmarks = new int[0];
		
		status.setText("...");
		console.append(PROMPT_START);
		setMark();
	}
	
	private void appendContinue()
	{
		int old[] = breakmarks;
		breakmarks = new int[old.length+1];
		System.arraycopy(old,0,breakmarks,0,old.length);
		breakmarks[old.length] = startmark;
		
		console.append(PROMPT_CONTINUE);
		setMark();
	}
	
	private void setMark()
	{
		startmark = console.getText().length();
		console.setCaretPosition(startmark);
	}
	
	private class DummieAction extends AbstractAction
	{
		public void actionPerformed(ActionEvent e)
		{
		}
	}
	
	private class WrappedAction extends AbstractAction
	{
		private Action superAction;
		
		WrappedAction(String key)
		{
			Action[] actions = ViewCommand.this.console.getActions();
			for(int i=0; i<actions.length; i++)
			{
				if(actions[i].getValue(Action.NAME).equals(key))
					superAction = actions[i];
			}
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(ViewCommand.this.isCaretPositionValid()) superAction.actionPerformed(e);
		}
	}

	private class DefaultKeyTypedAction extends WrappedAction
	{
		DefaultKeyTypedAction()
		{
			super(DefaultEditorKit.defaultKeyTypedAction);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			super.actionPerformed(e);
		}
	}
	
	private class InsertBreakAction extends WrappedAction
	{
		InsertBreakAction()
		{
			super(DefaultEditorKit.insertBreakAction);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			super.actionPerformed(e);
			
			int caretpos = ViewCommand.this.console.getCaretPosition();
			String text = ViewCommand.this.console.getText().trim();
			
			char lastchar = text.charAt(text.length()-1);
			if(lastchar == ';')
				ViewCommand.this.performCommand();
			else
				ViewCommand.this.appendContinue();
		}
	}

	private class BackwardAction extends WrappedAction
	{
		BackwardAction()
		{
			super(DefaultEditorKit.backwardAction);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(!ViewCommand.this.isCaretPositionEqualsMark()) super.actionPerformed(e);
		}
	}
	
	private class DeleteNextCharAction extends WrappedAction
	{
		DeleteNextCharAction()
		{
			super(DefaultEditorKit.deleteNextCharAction);
		}
	}
	
	private class DeletePrevCharAction extends WrappedAction
	{
		DeletePrevCharAction()
		{
			super(DefaultEditorKit.deletePrevCharAction);
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if(!ViewCommand.this.isCaretPositionEqualsMark()) super.actionPerformed(e);
		}
	}

	private class CopyAction extends DefaultEditorKit.CopyAction
	{
		CopyAction()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("clipboard.copy"));
			this.putValue(SHORT_DESCRIPTION, "copy");
		}
	}
	
	private class CutAction extends WrappedAction
	{
		CutAction()
		{
			super(DefaultEditorKit.cutAction);
		}
	}
	
	private class PasteAction extends WrappedAction
	{
		PasteAction()
		{
			super(DefaultEditorKit.pasteAction);
			
			this.putValue(SMALL_ICON, ImageStore.getIcon("clipboard.paste"));
			this.putValue(SHORT_DESCRIPTION, "paste");
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	Toolbar Actions
//	/////////////////////////////////////////////////////////////////////////////
	private class ActionStopTask extends AbstractAction
	{
		ActionStopTask()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("command.stop"));
			this.putValue(SHORT_DESCRIPTION, "stop task");
			
			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent ae)
		{
			this.setEnabled(false);
			ViewCommand.this.queryThread = null;
		}
	}
	
	private class ActionClean extends AbstractAction
	{
		ActionClean()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("command.clean"));
			this.putValue(SHORT_DESCRIPTION, "clean");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			ViewCommand.this.clean();
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	SystemView Interface
//	/////////////////////////////////////////////////////////////////////////////
	public JToggleButton getButton()
	{
		return switchbutton;
	}
	
	private class ActionSwitch extends SystemToolbar.AbstractActionSwitch
	{
		private ActionSwitch()
		{
			super("window.command","view sql command");
		}

		protected SystemWindow getWindow()
		{
			return ViewCommand.this.syswin;
		}
	}

//	/////////////////////////////////////////////////////////////////////////////
//	TaskSource Interface
//	/////////////////////////////////////////////////////////////////////////////
	public Connection getConnection()
	{
		return syswin.builder.getConnection();
	}
	
	public String getSyntax()
	{
		StringBuffer syntax = new StringBuffer();
		
		for(int i=0; i<breakmarks.length; i++)
		{
			if(breakmarks.length-1 == i)
				syntax.append(console.getText().substring(breakmarks[i],startmark-PROMPT_CONTINUE.length()));
			else
				syntax.append(console.getText().substring(breakmarks[i],breakmarks[i+1]-PROMPT_CONTINUE.length()));
		}
		
		syntax.append(console.getText().substring(startmark).trim());
		return syntax.substring(0, syntax.length()-1);
	}

//	/////////////////////////////////////////////////////////////////////////////
//	TaskTarget Interface
//	/////////////////////////////////////////////////////////////////////////////
	public boolean continueRun()
	{
		return queryThread != null;
	}

	public void onTaskFinished()
	{
		queryThread = null;
		
		console.append(resultBuffer.toString());
		console.append(status.getText() + "\n");
		appendStart();
		
		stop.setEnabled(false);
		console.setEditable(true);
		console.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void onTaskStarting()
	{
		console.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		console.setEditable(false);
		stop.setEnabled(true);
		
		resultBuffer = new StringBuffer();
	}

	public void message(String text)
	{
		status.setText(text);
	}

	public StringBuffer getOutputBuffer()
	{
		return resultBuffer;
	}
}
