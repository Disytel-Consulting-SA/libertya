/******************************************************************************
 * Copyright (C) 2009 Low Heng Sin                                            *
 * Copyright (C) 2009 Idalica Corporation                                     *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package org.adempiere.webui.apps.form;

import org.adempiere.webui.apps.form.WCreateFrom.CreateFromTableModel;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.ListModelTable;
import org.adempiere.webui.component.ListboxFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.WAppsAction;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.StatusBarPanel;
import org.adempiere.webui.window.FDialog;
import org.openXpertya.util.Trx;
import org.openXpertya.util.TrxRunnable;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zkex.zul.Borderlayout;
import org.zkoss.zkex.zul.Center;
import org.zkoss.zkex.zul.North;
import org.zkoss.zkex.zul.South;
import org.zkoss.zul.Separator;

public class WCreateFromWindow extends Window implements EventListener, WTableModelListener
{
	private static final long serialVersionUID = 1L;
	
	private WCreateFrom createFrom;
	private int windowNo;
	
	private Panel parameterPanel = new Panel();
	private ConfirmPanel confirmPanel = new ConfirmPanel(true);
	private StatusBarPanel statusBar = new StatusBarPanel();
	private WListbox dataTable = ListboxFactory.newDataTable();
	
	public static final String SELECT_ALL = "SelectAll";

	
	public WCreateFromWindow(WCreateFrom createFrom, int windowNo)
	{
		super();
		setAttribute("mode", "modal");
		
		this.createFrom = createFrom;
		this.windowNo = windowNo;
		
		try
		{
			zkInit();
			confirmPanel.addActionListener(this);
			
			statusBar.setStatusDB("");
			tableChanged(null);
		}
		catch(Exception e)
		{

		}		
    }
	
	protected void zkInit() throws Exception
	{
		Borderlayout contentPane = new Borderlayout();
		appendChild(contentPane);
		
		North north = new North();
		contentPane.appendChild(north);
		north.appendChild(parameterPanel);
		
		Center center = new Center();
        contentPane.appendChild(center);
		center.appendChild(dataTable);
		
		WAppsAction selectAllAction = new WAppsAction (SELECT_ALL, null, null);
		Button selectAllButton = selectAllAction.getButton();
		confirmPanel.addComponentsLeft(selectAllButton);
		selectAllButton.addActionListener(this);
		
		South south = new South();
		contentPane.appendChild(south);
		Panel southPanel = new Panel();
		south.appendChild(southPanel);
		southPanel.appendChild(new Separator());
		southPanel.appendChild(confirmPanel);

		southPanel.appendChild(new Separator());
		southPanel.appendChild(statusBar);
		
		setWidth("750px");
		setHeight("550px");
		setSizable(true);
		setBorder("normal");
		contentPane.setWidth("100%");
		contentPane.setHeight("100%");
	}

	public void onEvent(Event e) throws Exception
	{
		//  OK - Save
		if (e.getTarget().getId().equals(ConfirmPanel.A_OK))
		{
			try
			{
				Trx.run(new TrxRunnable()
				{
					public void run(String trxName)
					{
						if (save(trxName))
						{
							dispose();
						}
					}
				});
			}
			catch (Exception ex)
			{
				FDialog.error(windowNo, this, "Error", ex.getLocalizedMessage());
			}
		}
		//  Cancel
		else if (e.getTarget().getId().equals(ConfirmPanel.A_CANCEL))
		{
			dispose();
		}
		// Select All
		// Trifon
		else if (e.getTarget().getId().equals(SELECT_ALL)) {
			selectAll();
		}
	}

	public void selectAll() {
		ListModelTable model = dataTable.getModel();
		int rows = model.getSize();
		for (int i = 0; i < rows; i++) {
			Boolean newValue = !(Boolean)dataTable.getValueAt(i, 0);
			dataTable.setValueAt(newValue, i, 0);
		}
		info();		
	}
	
	public void tableChanged (WTableModelEvent e)
	{
		int type = -1;
		if (e != null)
		{
			type = e.getType();
			if (type != WTableModelEvent.CONTENTS_CHANGED)
				return;

			// Propagar el cambio de selección hacia las SourceEntities - FIXME: Mejorar MVC
			if (e.getColumn() == CreateFromTableModel.COL_IDX_SELECTION) {
				ListModelTable model = dataTable.getModel();
				Boolean newValue = !(Boolean)model.getValueAt(e.getFirstRow(), CreateFromTableModel.COL_IDX_SELECTION); 
				model.setValueAt(newValue, e.getFirstRow(), CreateFromTableModel.COL_IDX_SELECTION);
			}
		}
        // El OK solo se habilita si hay al menos una línea seleccionada
        confirmPanel.getOKButton().setEnabled(createFrom.getSelectedSourceEntities().size() > 0);

        info();
	}
	
	public boolean save(String trxName)
	{
		ListModelTable model = dataTable.getModel();
		int rows = model.getSize();
		if (rows == 0)
			return false;
		try {
			createFrom.doSave();
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	public void info()
	{
		ListModelTable model = dataTable.getModel();
		int rows = model.getRowCount();
		int count = 0;
		for (int i = 0; i < rows; i++)
		{
			if (((Boolean) model.getValueAt(i, 0)).booleanValue())
				count++;
		}
		setStatusLine(count, null);
		
//		createFrom.info();
	}
	
	public void setStatusLine(int selectedRowCount, String text) 
	{
		StringBuffer sb = new StringBuffer(String.valueOf(selectedRowCount));
		if (text != null && text.trim().length() > 0) {
			sb.append(" - ").append(text);
		}
		statusBar.setStatusLine(sb.toString());
		//
		confirmPanel.getOKButton().setEnabled(selectedRowCount > 0);
	}
	
	public WListbox getWListbox()
	{
		return dataTable;
	}
	
	public WListbox getDataTable()
	{
		return dataTable;
	}
	
	public Panel getParameterPanel()
	{
		return parameterPanel;
	}
	
	public ConfirmPanel getConfirmPanel()
	{
		return confirmPanel;
	}
}
