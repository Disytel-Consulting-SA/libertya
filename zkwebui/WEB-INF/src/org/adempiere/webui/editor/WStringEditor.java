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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.adempiere.webui.ValuePreference;
import org.adempiere.webui.component.Combobox;
import org.adempiere.webui.component.Textbox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.session.SessionManager;
import org.adempiere.webui.window.WFieldRecordInfo;
import org.adempiere.webui.window.WTextEditorDialog;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Menuitem;

/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 11, 2007
 * @version $Revision: 0.10 $
 */
public class WStringEditor extends WEditor implements ContextMenuListener
{
    private static final String EDITOR_EVENT = "EDITOR";

	private static final String[] LISTENER_EVENTS = {Events.ON_CHANGE, Events.ON_OK, Events.ON_CHANGING, Events.ON_FOCUS}; 

    private String oldValue;

    private WEditorPopupMenu	popupMenu;

    private boolean tableEditor = false;

    /**
     * to ease porting of swing form
     */
    public WStringEditor()
    {
    	this("String", false, false, true, 30, 30, "", null);
    }

    public WStringEditor(MField mField) {
    	this(mField, false);
    }

    public WStringEditor(MField mField, boolean tableEditor)
    {
        super(mField.isAutocomplete() ? new Combobox() : new Textbox(), mField);
        this.tableEditor = tableEditor;
        init(mField.getObscureType());
    }

    /**
     * to ease porting of swing form
     * @param columnName
     * @param mandatory
     * @param isReadOnly
     * @param isUpdateable
     * @param displayLength
     * @param fieldLength
     * @param vFormat
     * @param obscureType
     */
    public WStringEditor(String columnName, boolean mandatory, boolean isReadOnly, boolean isUpdateable,
    		int displayLength, int fieldLength, String vFormat, String obscureType)
    {
    	super(new Textbox(), columnName, null, null, mandatory, isReadOnly,isUpdateable);

    	init(obscureType);
    }

    @Override
    public org.zkoss.zul.Textbox getComponent() {
    	return (org.zkoss.zul.Textbox) component;
    }

    @Override
	public boolean isReadWrite() {
		return !getComponent().isReadonly();
	}

	@Override
	public void setReadWrite(boolean readWrite) {
		getComponent().setReadonly(!readWrite);
	}

	private void init(String obscureType)
    {
		
		// dREHER - iDempiere
		setChangeEventWhenEditing (true);
		
		// dREHER de entrada NO esta editando
		isEditing = false;
		
		if (mField != null)
		{
	        getComponent().setMaxlength(mField.getFieldLength());
	        int displayLength = mField.getDisplayLength();
	        if (displayLength <= 0 || displayLength > MAX_DISPLAY_LENGTH)
	        {
	            displayLength = MAX_DISPLAY_LENGTH;
	        }
	        getComponent().setCols(displayLength);

	        if (mField.getDisplayType() == DisplayType.Text)
	        {
	            getComponent().setMultiline(true);
	            getComponent().setRows(3);
	        }
	        else if (mField.getDisplayType() == DisplayType.TextLong)
	        {
	            getComponent().setMultiline(true);
	            getComponent().setRows(5);
	        }
	        else if (mField.getDisplayType() == DisplayType.Memo)
	        {
	            getComponent().setMultiline(true);
	            getComponent().setRows(8);
	        }

	        if (getComponent() instanceof Textbox) {
	        	((Textbox)getComponent()).setObscureType(obscureType);
	        	
	        	/**
	        	 * Si es link, pasar info al textbox
	        	 * dREHER
	        	 */
	        	
	        	((Textbox)getComponent()).setM_prefijoLink(prefijoLink);
	        	((Textbox)getComponent()).setM_islink(isLink);
	        	
	        	
	        }

	        popupMenu = new WEditorPopupMenu(false, false, true);
	        Menuitem editor = new Menuitem(Msg.getMsg(Env.getCtx(), "Editor"), "images/Editor16.png");
	        editor.setAttribute("EVENT", EDITOR_EVENT);
	        editor.addEventListener(Events.ON_CLICK, popupMenu);
	        popupMenu.appendChild(editor);
	        
	        if (mField != null && mField.getGridTab() != null)
			{
				WFieldRecordInfo.addMenu(popupMenu);
			}

	        getComponent().setContext(popupMenu.getId());

	        if (mField.isAutocomplete()) {
	        	Combobox combo = (Combobox)getComponent();
	        	combo.setAutodrop(true);
	        	combo.setAutocomplete(true);
	        	combo.setButtonVisible(false);
	        	List<String> items = mField.getEntries();
	        	for(String s : items) {
	        		combo.appendItem(s);
	        	}
	        }
		}
    }

    public void onEvent(Event event)
    {
    	
    	if (Events.ON_CHANGE.equals(event.getName()) || Events.ON_OK.equals(event.getName()))
    	{
	        String newValue = getComponent().getValue();
	        
	        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    	    return;
	    	}
	        if (oldValue == null && newValue == null) {
	        	return;
	        }
	        
	        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newValue);
	        super.fireValueChange(changeEvent);
	        
	        oldValue = newValue;
    	}else {
    		if (Events.ON_FOCUS.equals(event.getName()) && 1==2) {  // dREHER por ahora lo desactivamos ya que no le gusto a los usuarios
    			
    			debug("Foco en el campo...");
    			
    			// Obtener el ID HTML del componente
                String componentId = getComponent().getUuid();

                // Mostrar mensaje temporal al usuario mediante JavaScript
                String jsScript = 
                    "var comp = document.getElementById('" + componentId + "');" +
                    "if (comp) {" +
                    "  var rect = comp.getBoundingClientRect();" + // Obtiene posición del componente
                    "  var msg = document.createElement('div');" +
                    "  msg.style.position = 'absolute';" +
                    "  msg.style.backgroundColor = '#f0f8ff';" + // Fondo azul claro
                    "  msg.style.border = '1px solid #ccc';" +
                    "  msg.style.padding = '5px';" +
                    "  msg.style.color = '#000';" +
                    "  msg.style.zIndex = '9999';" +
                    "  msg.style.top = (window.scrollY + rect.top - 40) + 'px';" + // 40px encima del campo
                    "  msg.style.left = (window.scrollX + rect.left) + 'px';" +
                    "  msg.innerHTML = 'Al final de la edicion, presione Enter para guardar los cambios';" +
                    "  document.body.appendChild(msg);" +
                    "  setTimeout(function() { document.body.removeChild(msg); }, 3000);" + // Desaparece después de 3 segundos
                    "}";
                Clients.evalJavaScript(jsScript);
    	}
    }
    }

	/**
	 * Por las capas de clases y manejadores de eventos de Libertya+ZK no se puede 
	 * trabajar con onChanging, ya que se producen efectos no deseados, esto requeriria
	 * mucho mas trabajo de analisis e investigacion y seguramente de reescritura de
	 * bastante codigo
	 * 
	 * @param event
	 * dREHER
	 */
    public void onEvent_iDempiere(Event event)
    {
    	// iDempiere
    	boolean isStartEdit = INIT_EDIT_EVENT.equalsIgnoreCase (event.getName());
    	System.out.println("WStringEditor. onEvent: " + event.getName());
    	
    	if (Events.ON_CHANGE.equals(event.getName()) || Events.ON_OK.equals(event.getName()))
    	{
	        String newValue = getComponent().getValue();
	        
	        if (!isStartEdit && oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    	    return;
	    	}
	        if (!isStartEdit && oldValue == null && newValue == null) {
	        	return;
	        }    
	        
	        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newValue);
	        
	        changeEvent.setIsInitEdit(isStartEdit);
	        
	        super.fireValueChange(changeEvent);
	        
	        oldValue = getComponent().getValue(); // IDEMPIERE-963 - check again the value could be changed by callout
    	}
    }
    
    public void onEventNOFUNCA(Event event) {
    	
    	debug("onEvent.isEditing=" + isEditing);
    	
        if (Events.ON_CHANGE.equals(event.getName())) {
        	
        	debug("onChange...");
        	
            // Manejar el evento onChange (valor cambiado)
            handleValueChange(getComponent().getValue()); 
            isEditing = false; // Reiniciar el estado de edición
            
        } else if (Events.ON_CHANGING.equals(event.getName()) && !isEditing) {
            InputEvent inputEvent = (InputEvent) event;

            debug("onChanging...");
            
            // Detectar que se ha iniciado la edición
            if (!isEditing) {
                isEditing = true;

                // Forzar el disparo del evento onChange
                // Events.postEvent(new Event(Events.ON_CHANGE, getComponent(), inputEvent.getValue()));
                
                // Manejar el valor mientras se edita
                debug("Texto ingresado en onChanging: " + inputEvent.getValue());
                
                String newValue = inputEvent.getValue();
                handleValueChange(newValue);
                
            }else
            	debug("Ya no esta activo onChanging...");
            
        } else if (Events.ON_OK.equals(event.getName())) {
            
        	// Confirmación con Enter
            handleValueChange(getComponent().getValue());
            isEditing = false; // Reiniciar el estado de edición
        }
    }

    private void handleValueChange(String newValue) {
        debug("handeValueChange.Valor cambiado: " + newValue);

        // Habilitar el botón de guardado aquí
        enableSaveButton(true, newValue);

        // Reiniciar el estado de edición
        // isEditing = false;
    }

    private void enableSaveButton(boolean enable, String newValue) {
        
        String oldValue = getComponent().getValue();
        
        // Habilitar o deshabilitar el botón de guardado
        debug("Botón de guardado habilitado: " + enable + " - valor anterior: " + oldValue + " - nuevo valor: " + newValue);
        
        if (oldValue != null && newValue != null && oldValue.equals(newValue) ) {
    	    return;
    	}
        if (oldValue == null && newValue == null) {
        	return;
        }
        
        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newValue);
        super.fireValueChange(changeEvent);
        
        oldValue = newValue;
        getComponent().setValue(newValue);
        
        debug("enableSaveButton. newValue=" + newValue + "  -  component.getValue()=" + getComponent().getValue());
        
        // oldValue = getComponent().getValue(); // IDEMPIERE-963 - check again the value could be changed by callout
    }

    @Override
    public String getDisplay()
    {
        return getComponent().getValue();
    }

    @Override
    public Object getValue()
    {
        return getComponent().getValue();
    }

    @Override
    public void setValue(Object value)
    {
        if (value != null)
        {
            getComponent().setValue(value.toString());
        }
        else
        {
            getComponent().setValue("");
        }
        
        oldValue = getComponent().getValue();
    }

    protected void setTypePassword(boolean password)
    {
        if (password)
        {
            getComponent().setType("password");
        }
        else
        {
            getComponent().setType("text");
        }
    }

    @Override
    public String[] getEvents()
    {
        return LISTENER_EVENTS;
    }

    public WEditorPopupMenu getPopupMenu()
	{
	   	return popupMenu;
	}

    public void onMenu(ContextMenuEvent evt)
	{
		if (WEditorPopupMenu.PREFERENCE_EVENT.equals(evt.getContextEvent()))
		{
			if (MRole.getDefault().isShowPreference())
				ValuePreference.start (this.getGridField(), getValue());
			return;
		}
		else if (EDITOR_EVENT.equals(evt.getContextEvent()))
		{
			WTextEditorDialog dialog = new WTextEditorDialog(this.getColumnName(), getDisplay(),
					isReadWrite(), mField.getFieldLength());
			dialog.setAttribute(Window.MODE_KEY, Window.MODE_MODAL);
			SessionManager.getAppDesktop().showWindow(dialog);
			if (!dialog.isCancelled()) {
				getComponent().setText(dialog.getText());
				String newText = getComponent().getValue();
		        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, newText);
		        super.fireValueChange(changeEvent);
		        oldValue = newText;
			}
		}
		else if (WEditorPopupMenu.CHANGE_LOG_EVENT.equals(evt.getContextEvent()))
		{
			WFieldRecordInfo.start(mField);
		}
	}

	@Override
	public void dynamicDisplay() {
		//referesh auto complete list
		if (mField.isAutocomplete()) {
        	Combobox combo = (Combobox)getComponent();
        	List<String> items = mField.getEntries();
        	if (items.size() != combo.getItemCount())
        	{
        		combo.removeAllItems();
        		for(String s : items) {
            		combo.appendItem(s);
            	}
        	}
        }
	}


}
