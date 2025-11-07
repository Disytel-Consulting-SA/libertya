/******************************************************************************
 * Product: Posterita Ajax UI 												  *
 * Copyright (C) 2007 Posterita Ltd.  All Rights Reserved.                    *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Posterita Ltd., 3, Draper Avenue, Quatre Bornes, Mauritius                 *
 * or via info@posterita.org or http://www.posterita.org/                     *
 *****************************************************************************/

package org.adempiere.webui.editor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.adempiere.webui.component.Urlbox;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.window.FDialog;
import org.adempiere.webui.window.WFieldRecordInfo;
import org.openXpertya.model.MField;
import org.openXpertya.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

public class WUrlEditor extends WEditor implements ContextMenuListener
{
	private static final String[] LISTENER_EVENTS = {Events.ON_CLICK, Events.ON_CHANGE, Events.ON_OK};
	private String oldValue;
	private WEditorPopupMenu popupMenu;

	/** dREHER sep 24, define prefijo */
	private String prefijo = "";
	private boolean isLink = false;

	public WUrlEditor(MField mField)
	{
		
		super(new Urlbox(), mField);
		
		// dREHER sep 24
		prefijo = mField.getPrefijoLink();
		isLink = mField.isLink();
		
		getComponent().setButtonImage("/images/Online10.png");
		
		popupMenu = new WEditorPopupMenu(false, false, true);
		popupMenu.addMenuListener(this);
		if (mField != null && mField.getGridTab() != null)
		{
			WFieldRecordInfo.addMenu(popupMenu);
		}
		getComponent().setContext(popupMenu.getId());
		
		
		if(isLink) {
			//getComponent().setEnabled(false);
			//getComponent().getTextbox().setReadonly(true);
			// getComponent().getTextbox().setEnabled(false);
			getComponent().setButtonActive();
			getComponent().setLink(true);
		}
	}


	@Override
	public void setValue(Object value)
	{
        if (value == null)
        {
        	oldValue = null;
            getComponent().setText("");
        }
        else
        {
        	oldValue = String.valueOf(value);
            getComponent().setText(oldValue);
        }
	}

	@Override
	public Object getValue()
	{
		return getComponent().getText();
	}

	@Override
	public String getDisplay()
	{
		return getComponent().getText();
	}

	@Override
	public Urlbox getComponent() {
		return (Urlbox) component;
	}


	@Override
	public boolean isReadWrite() {
		return getComponent().isEnabled();
	}


	@Override
	public void setReadWrite(boolean readWrite) {
		getComponent().setEnabled(readWrite);
	}


	public void onEvent(Event event)
	{
		if (Events.ON_CHANGE.equals(event.getName()) || Events.ON_OK.equals(event.getName()))
		{
			String newValue = getComponent().getText();
			if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    	    return;
	    	}
	        if (oldValue == null && newValue == null) {
	        	return;
	        }
			ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newValue);
			fireValueChange(changeEvent);
			oldValue = newValue;
		}
		else if (Events.ON_CLICK.equals(event.getName()))
		{
			String urlString =getComponent().getText();
			
			String url = (prefijo==null?"":prefijo) + urlString;
			try {
				url = formatUrl(url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			System.out.println("WUrlEditor. Es link, url=" + url);
			
            String message = null;
			if (url != null && url.length() > 0)
			{
				try
                {
					if(!isLink)
						Env.startBrowser(url);
					else
						Clients.evalJavaScript("window.open('" + url + "', '_blank');");
                    return;
                }
                catch(Exception e)
                {
                    message = e.getMessage();
                }
				
			}
            FDialog.warn(0, this.getComponent(), "URLnotValid", message);

		}
	}
	
	// dREHER sep 24 Método para validar y corregir la URL (si no tiene protocolo, agregar 'https://')
    private String formatUrl(String url) throws UnsupportedEncodingException {
    	
    	// url = URLEncoder.encode(url, "UTF-8");
    	
        if (url != null && !url.startsWith("http")) {
            // Si la URL no comienza con 'http', asumimos que necesita 'https://'
            url = "https://" + url;
        }
        return url;
	}

	public String[] getEvents()
    {
        return LISTENER_EVENTS;
    }


	@Override
	public void onMenu(ContextMenuEvent evt) 
	{
		if (WEditorPopupMenu.CHANGE_LOG_EVENT.equals(evt.getContextEvent()))
		{
			WFieldRecordInfo.start(mField);
		}
	}


}
