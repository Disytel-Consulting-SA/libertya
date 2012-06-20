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

import nickyb.fqb.QueryModel;
import nickyb.fqb.util.*;

import java.awt.Cursor;
import java.awt.Dimension;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

public class ViewHistory extends DefaultScrollPane
{
	private SystemWindow syswin;
	private JToggleButton switchbutton;
	
	private JButton btnRun;
	private JButton btnEdit;
	
	private JLabel status;
	private JTable queries;
	
	public ViewHistory(SystemWindow syswin)
	{
		super("history",null,false);
		this.syswin = syswin;
		
		JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
		toolbar.setFloatable(false);
		
		toolbar.add(new ActionEditComment());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new ActionEditQuery());
		toolbar.add(new ActionDeleteQuery());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new ActionRunPreview());
		toolbar.add(new JToolBar.Separator(new Dimension(0,10)));
		toolbar.add(new ActionBuildReport());
		
		setEastComponent(toolbar);
		setSouthComponent(status = new JLabel("..."));
		
		setView(queries = new JTable(new DataModel()));
		queries.setShowVerticalLines(false);
		queries.setIntercellSpacing(new Dimension(0,0));
		queries.addMouseListener(new DoubleClickHandler());
		queries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queries.getTableHeader().setPreferredSize(new Dimension(0,0));
		queries.getTableHeader().setVisible(false);
		
		TableColumn tableColumn = queries.getColumn(queries.getColumnName(0));
		tableColumn.setPreferredWidth(110);
		tableColumn.setMaxWidth(110);
		tableColumn.setResizable(false);
		
		switchbutton = new JToggleButton(new ActionSwitch());
	}
	
	public void add(QueryModel qmodel)
	{
		((DataModel)queries.getModel()).add(qmodel);
	}
	
	private QueryModel getSelected()
	{
		Vector models = (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS);
		return (QueryModel)models.elementAt(queries.getSelectedRow());			
	}
	
	String getSelectedPlusValue(int col)
	{
		Vector plus = (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS_PLUS);
		String[] values = (String[])plus.elementAt(queries.getSelectedRow());
		
		return values == null ? null : values[col];
	}
	
	void setSelectedComment(String value)
	{
		int row = queries.getSelectedRow();
		
		Vector plus = (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS_PLUS);
		String[] values = (String[])plus.elementAt(row);
		
		if(values == null)
		{
			values = new String[3];
			plus.setElementAt(values,row);
		}
		
		values[1] = value;
		((DataModel)queries.getModel()).fireTableDataChanged();
		
		queries.setRowSelectionInterval(row,row);
	}
	
	String getSelectedSyntax()
	{
		return getSelected().getSyntax();
	}
	
	private void doDeleteQuery()
	{
		int row = queries.getSelectedRow();
		if(row==-1) return;
		
		((DataModel)queries.getModel()).remove(this.getSelected());
		
		if(row>=queries.getRowCount())
		{
			row = queries.getRowCount()-1;
		}
		
		if(row==-1) return;
		queries.setRowSelectionInterval(row,row);
	}
	
	private void doEditQuery()
	{
		if(queries.getSelectedRow()==-1) return;
		
		status.setText("loading...");
		syswin.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		this.getSelected().load(ViewHistory.this.syswin.builder);
		syswin.view("window.builder");
		
		syswin.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		status.setText("...");
	}
	
	private void doRunPreview()
	{
		if(queries.getSelectedRow()==-1) return;
		syswin.runQuery(this.getSelected());
	}
	
	private void doBuildReport()
	{
		if(queries.getSelectedRow()==-1) return;
		syswin.report.createReport(this.getSelected());
		syswin.view("window.report");
	}
	
	private class ActionDeleteQuery extends AbstractAction
	{
		ActionDeleteQuery()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("history.delete"));
			this.putValue(SHORT_DESCRIPTION, "delete query");
		}
			
		public void actionPerformed(ActionEvent ae)
		{
			ViewHistory.this.doDeleteQuery();
		}
	}
	
	private class ActionEditQuery extends AbstractAction
	{
		ActionEditQuery()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("history.edit"));
			this.putValue(SHORT_DESCRIPTION, "edit query");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			ViewHistory.this.doEditQuery();
		}
	}
	
	private class ActionRunPreview extends AbstractAction
	{
		ActionRunPreview()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("builder.preview"));
			this.putValue(SHORT_DESCRIPTION, "run preview");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			ViewHistory.this.doRunPreview();
		}
	}
	
	private class ActionEditComment extends AbstractAction
	{
		ActionEditComment()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("history.comment"));
			this.putValue(SHORT_DESCRIPTION, "edit comment");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			if(ViewHistory.this.queries.getSelectedRow()==-1) return;
			new DialogQuery(ViewHistory.this).setVisible(true);
		}
	}
	
	private class ActionBuildReport extends AbstractAction
	{
		ActionBuildReport()
		{
			this.putValue(SMALL_ICON, ImageStore.getIcon("report.new"));
			this.putValue(SHORT_DESCRIPTION, "new report");
		}
		
		public void actionPerformed(ActionEvent ae)
		{
		    ViewHistory.this.doBuildReport();
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
			super("window.history","view history");
		}
		
		protected SystemWindow getWindow()
		{
			return ViewHistory.this.syswin;
		}
	}
	
	private class DoubleClickHandler extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			if(e.getClickCount()!=2) return;
			ViewHistory.this.doEditQuery();
		}
	}
	
//	/////////////////////////////////////////////////////////////////////////////
//	TableModel
//	/////////////////////////////////////////////////////////////////////////////
	private class DataModel extends AbstractTableModel
	{
		public boolean isCellEditable(int rowIndex, int columnIndex) {return false;}		
		
		private String getCurrentDate()
		{
			DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT);
			return formatter.format(new Date());
		} 
		
		private void add(QueryModel qmodel)
		{
			if(getRow(qmodel)!=-1) return;
			
			getModels().addElement(qmodel);
		
			Vector plus = (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS_PLUS);
			plus.addElement(new String[]{this.getCurrentDate(),null,ViewHistory.this.syswin.pinfo.name});
			
			fireTableDataChanged();
		}
		
		private void remove(QueryModel qmodel)
		{
			int row = getRow(qmodel);
			if(row==-1) return;
			
			getModels().removeElementAt(row);
			getModelsPlus().removeElementAt(row);
			
			fireTableDataChanged();
		}
		
		private int getRow(QueryModel qmodel)
		{
			Vector models = getModels();
			for(int i=0; i<models.size(); i++)
			{
				QueryModel qmodel2 = (QueryModel)models.elementAt(i);
				if(qmodel.toString().equals(qmodel2.toString())) return i;
			}
			
			return -1;
		}
		
		public int getColumnCount()
		{
			return 2;
		}
	
		public int getRowCount()
		{
			return getModels().size();
		}
	
		public Object getValueAt(int row, int col)
		{
			if(row >= getModelsPlus().size()) getModelsPlus().setSize(row+1);
			
			if(getModelsPlus().elementAt(row) == null)
			{
				return col == 1 ? getModels().elementAt(row).toString() : null;
			}
			else
			{
				String[] rowdata = (String[])getModelsPlus().elementAt(row);
				if(col != 1) return rowdata[col];
				
				return rowdata[col] == null ? getModels().elementAt(row).toString() : rowdata[col];
			}
		}

		public void setValueAt(Object value, int row, int col)
		{
			System.out.println(value + ":" + row + ":" + col);
		}
		
		private Vector getModels()
		{
			return (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS);
		}
		
		private Vector getModelsPlus()
		{
			return (Vector)UserSession.histroy.get(UserSession.HISTORY_MODELS_PLUS);
		}
	}
}
