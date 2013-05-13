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

import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.zkoss.zul.Messagebox;

/**
 *
 * @author  <a href="mailto:agramdass@gmail.com">Ashley G Ramdass</a>
 * @date    Mar 12, 2007
 * @version $Revision: 0.10 $
 * 
 * @author Low Heng Sin
 * @date 	July 14 2008
 */
public class WebEditorFactory
{

    @SuppressWarnings("unused")
	private final static CLogger logger;
    
    static
    {
        logger = CLogger.getCLogger(WebEditorFactory.class);
    }
    
    public static WEditor getEditor(MField mField, boolean tableEditor)
    {
    	return getEditor(null, mField, tableEditor);
    }
    
    public static WEditor getEditor(MTab mTab, MField mField, boolean tableEditor)
    {
        if (mField == null)
        {
            return null;
        }

        WEditor editor = null;
        int displayType = mField.getDisplayType();
        
        /** Not a Field */
        if (mField.isHeading())
        {
            return null;
        }

        /** String (clear/password) */
        if (displayType == DisplayType.String
            || displayType == DisplayType.PrinterName 
            || (tableEditor && (displayType == DisplayType.Text || displayType == DisplayType.TextLong)))
        {
            if (mField.isEncryptedField())
            {
                editor = new WPasswordEditor(mField);
            }
            else
            {
                editor = new WStringEditor(mField, tableEditor);
            }
        }
        /** File */
        else if (displayType == DisplayType.FileName)
        {
        	editor = new WFilenameEditor(mField);
        }
        /** File Path */
        else if (displayType == DisplayType.FilePath)
        {
        	editor = new WFileDirectoryEditor(mField);
        }
        /** Number */
        else if (DisplayType.isNumeric(displayType))
        {
            editor = new WNumberEditor(mField);
        }

        /** YesNo */
        else if (displayType == DisplayType.YesNo)
        {
            editor = new WYesNoEditor(mField);
            if (tableEditor)
            	((WYesNoEditor)editor).getComponent().setLabel("");
        }

        /** Text */
        else if (displayType == DisplayType.Text || displayType == DisplayType.Memo || displayType == DisplayType.TextLong)
        {
            editor = new WStringEditor(mField);
        }
        
        /** Date */
        else if (DisplayType.isDate(displayType))
        {
        	if (displayType == DisplayType.Time)
        		editor = new WTimeEditor(mField);
        	else if (displayType == DisplayType.DateTime)
        		editor = new WDatetimeEditor(mField);
        	else
        		editor = new WDateEditor(mField);
        }
        
        /**  Button */
        else if (displayType == DisplayType.Button)
        {
            editor = new WButtonEditor(mField);
        }

        /** Table Direct */
        else if (displayType == DisplayType.TableDir || 
                displayType == DisplayType.Table || displayType == DisplayType.List
                || displayType == DisplayType.ID )
        {
            editor = new WTableDirEditor(mField);
        }
                   
        else if (displayType == DisplayType.URL)
        {
        	editor = new WUrlEditor(mField);
        }
        
        else if (displayType == DisplayType.Search)
        {
        	editor = new WSearchEditor(mField);
        }
        
        else if (displayType == DisplayType.Location)
        {
            editor = new WLocationEditor(mField);
        }
        else if (displayType == DisplayType.Locator)
        {
        	editor = new WLocatorEditor(mField); 
        }
        else if (displayType == DisplayType.Account)
        {
        	editor = new WAccountEditor(mField);
        }
        else if (displayType == DisplayType.Image)
        {
        	//editor = new WImageEditor(mField);
        }
        else if (displayType == DisplayType.Binary)
        {
        	editor = new WBinaryEditor(mField);        	
        }
        else if (displayType == DisplayType.PAttribute)
        {
        	//editor = new WPAttributeEditor(mTab, mField);
        }
        else if (displayType == DisplayType.Assignment)
        {
        	editor = new WAssignmentEditor(mField);
        }
        else
        {
            //editor = new WUnknownEditor(mField); //TODO Hernandez
        }
        
        return editor;
    }
}
