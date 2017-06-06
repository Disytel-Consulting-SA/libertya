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

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.adempiere.webui.ValuePreference;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.event.ContextMenuEvent;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.window.WFieldRecordInfo;
import org.openXpertya.model.MField;
import org.openXpertya.model.MRole;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 11, 2007
 * @version $Revision: 0.10 $
 *
 * @author Low Heng Sin
 */
public class WNumberEditor extends WEditor implements ContextMenuListener
{
    public static final String[] LISTENER_EVENTS = {Events.ON_CHANGE, Events.ON_OK};

    public static final int MAX_DISPLAY_LENGTH = 20;

    private BigDecimal oldValue;

	private int displayType;

	private WEditorPopupMenu popupMenu;

    public WNumberEditor()
    {
    	this("Number", false, false, true, DisplayType.Number, "");
    }

    /**
     *
     * @param mField
     */
    public WNumberEditor(MField mField)
    {
        super(new NumberBox(mField.getDisplayType() == DisplayType.Integer),
                mField);
        this.displayType = mField.getDisplayType();
        init();
    }

    /**
     *
     * @param mField
     * @param integral
     */
    public WNumberEditor(MField mField, boolean integral)
    {
        super(new NumberBox(integral), mField);
        this.displayType = integral ? DisplayType.Integer : DisplayType.Number;
        init();
    }

    /**
     *
     * @param columnName
     * @param mandatory
     * @param readonly
     * @param updateable
     * @param displayType
     * @param title
     */
    public WNumberEditor(String columnName, boolean mandatory, boolean readonly, boolean updateable,
			int displayType, String title)
    {
		super(new NumberBox(displayType == DisplayType.Integer), columnName, title, null, mandatory,
				readonly, updateable);
		this.displayType = displayType;
		init();
	}

	private void init()
    {
		if (mField != null)
		{
			getComponent().setTooltiptext(mField.getDescription());
		}

		if (!DisplayType.isNumeric(displayType))
			displayType = DisplayType.Number;
		DecimalFormat format = DisplayType.getNumberFormat(displayType, AEnv.getLanguage(Env.getCtx()));
		getComponent().getDecimalbox().setFormat(format.toPattern());
		
		popupMenu = new WEditorPopupMenu(true, true, false);
    	if (mField != null && mField.getGridTab() != null)
		{
			WFieldRecordInfo.addMenu(popupMenu);
		}
    	getComponent().setContext(popupMenu.getId());
    }

	/**
	 * Event handler
	 * @param event
	 */
    public void onEvent(Event event)
    {
    	if (Events.ON_CHANGE.equalsIgnoreCase(event.getName()) || Events.ON_OK.equalsIgnoreCase(event.getName()))
    	{
	        BigDecimal newValue = getComponent().getValue();
	        if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
	    	    return;
	    	}
	        if (oldValue == null && newValue == null) {
	        	return;
	        }
	    	// Compatibilidad con Callouts: en caso de almacenar un dato entero, debe retornarse este tipo, y no un BigDecimal
	        ValueChangeEvent changeEvent = new ValueChangeEvent(this, this.getColumnName(), oldValue, (displayType==DisplayType.Integer && newValue!=null ? newValue.intValue() : newValue));
	        super.fireValueChange(changeEvent);
	        oldValue = newValue;
    	}
    }

    @Override
	public NumberBox getComponent() {
		return (NumberBox) component;
	}

	@Override
	public boolean isReadWrite() {
		return getComponent().isEnabled();
	}

	@Override
	public void setReadWrite(boolean readWrite) {
		getComponent().setEnabled(readWrite);
	}

	@Override
    public String getDisplay()
    {
        return getComponent().getText();
    }

    @Override
    public Object getValue()
    {
    	// Compatibilidad con Callouts: en caso de almacenar un dato entero, debe retornarse este tipo, y no un BigDecimal
    	if (displayType==DisplayType.Integer && getComponent()!=null && getComponent().getValue()!=null) {
    		return ((BigDecimal)(getComponent().getValue())).intValue();
    	}
        return getComponent().getValue();
    }

    @Override
    public void setValue(Object value)
    {
    	if (value == null)
    		oldValue = null;
    	else if (value instanceof BigDecimal)
    		oldValue = (BigDecimal) value;
    	else if (value instanceof Number)
    		oldValue = new BigDecimal(((Number)value).doubleValue());
    	else
    		oldValue = new BigDecimal(value.toString());
    	getComponent().setValue(oldValue);
    }

    @Override
    public String[] getEvents()
    {
        return LISTENER_EVENTS;
    }

    /**
     * Handle context menu events
     * @param evt
     */
    public void onMenu(ContextMenuEvent evt)
	{
	 	if (WEditorPopupMenu.PREFERENCE_EVENT.equals(evt.getContextEvent()) && mField != null)
		{
			if (MRole.getDefault().isShowPreference())
				ValuePreference.start (this.getGridField(), getValue());
			return;
		}
	 	else if (WEditorPopupMenu.CHANGE_LOG_EVENT.equals(evt.getContextEvent()))
		{
			WFieldRecordInfo.start(mField);
		}
	}
}
