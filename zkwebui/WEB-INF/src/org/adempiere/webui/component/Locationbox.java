/******************************************************************************
 * Copyright (C) 2008 Low Heng Sin                                            *
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
package org.adempiere.webui.component;

import org.adempiere.webui.LayoutUtils;

/**
 * 
 * @author Low Heng Sin
 *
 */
public class Locationbox extends EditorBox
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9199586798474147872L;

	public Locationbox()
    {
         super();
         initLocationClasses();
    }

     public Locationbox(String text)
     {
         super();
         initLocationClasses();
         setText(text);
     }

     private void initLocationClasses()
     {
         LayoutUtils.addSclass("location-editor-box", this);
         LayoutUtils.addSclass("location-editor-button", btn);
         addBtnColumnSclass("location-editor-cell");
     }

     @Override
     public void setEnabled(boolean enabled)
     {
         super.setEnabled(enabled);
         if (enabled)
             addBtnColumnSclass("location-editor-cell");
     }

     @Override
     public void setButtonActive()
     {
         super.setButtonActive();
         addBtnColumnSclass("location-editor-cell");
     }
}
