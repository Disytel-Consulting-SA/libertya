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
package org.adempiere.webui.util;

import java.util.Properties;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.openXpertya.model.MField;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MTable;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

/**
 * Transfer data from editor to MTab
 * @author hengsin
 *
 */
public class GridTabDataBinder implements ValueChangeListener {

	private final static CLogger logger = CLogger.getCLogger(GridTabDataBinder.class);
	
	private MTab mTab;

	/**
	 * 
	 * @param mTab
	 */
	public GridTabDataBinder(MTab mTab) {
		this.mTab = mTab;
	}
	
	/**
	 * @param e
	 */
	public void valueChange(ValueChangeEvent e)
    {
        if (mTab.isProcessed())       //  only active records
        {
            Object source = e.getSource();
            if (source instanceof WEditor)
            {
            	// Elaine 2009/05/06
            	WEditor editor = (WEditor) source;
            	MField mField = editor.getGridField();
            	
            	if(mField != null)
            	{
            		if(!mField.isEditable(true))
            		{
            			logger.config("(" + mTab.toString() + ") " + e.getPropertyName());
            			return;
            		}
            	}
            	else if(!editor.isReadWrite())
            	{
            		logger.config("(" + mTab.toString() + ") " + e.getPropertyName());
            		return;            		
            	}
            }
            else
            {
                logger.config("(" + mTab.toString() + ") " + e.getPropertyName());
                return;
            }
        }   //  processed
        logger.config("(" + mTab.toString() + ") "
            + e.getPropertyName() + "=" + e.getNewValue() + " (" + e.getOldValue() + ") "
            + (e.getOldValue() == null ? "" : e.getOldValue().getClass().getName()));
        

        //  Get Row/Col Info
        MTable mTable = mTab.getTableModel();
        int row = mTab.getCurrentRow();
        int col = mTable.findColumn(e.getPropertyName());
        //
        if (e.getNewValue() == null && e.getOldValue() != null 
            && e.getOldValue().toString().length() > 0)     //  some editors return "" instead of null
//        	  this is the original code from GridController, don't know what it does there but it breaks ignore button for web ui        
//            mTable.setChanged (true);  
        	mTable.setValueAt (e.getNewValue(), row, col);
        else
        {
        	
        	Object newValue = e.getNewValue();
			Integer newValues[] = null;
			
			if (newValue instanceof Integer[])
			{
				newValues = ((Integer[])newValue);
				newValue = newValues[0];
				
				if (newValues.length > 1)
				{
					Integer valuesCopy[] = new Integer[newValues.length - 1];
					System.arraycopy(newValues, 1, valuesCopy, 0, valuesCopy.length);
					newValues = valuesCopy;
				}
				else
				{
					newValues = null;
				}
			}
			else if (newValue instanceof Object[])
			{
				logger.severe("Multiple values can only be processed for IDs (Integer)");
				throw new IllegalArgumentException("Multiple Selection values not available for this field. " + e.getPropertyName());
			}
			
           	mTable.setValueAt (newValue, row, col);
            //  Force Callout
            if ( e.getPropertyName().equals("S_ResourceAssignment_ID") )
            {
                MField mField = mTab.getField(col);
                if (mField != null && mField.getCallout().length() > 0)
                {
                    mTab.processFieldChange(mField);     //  Dependencies & Callout
                }
            }
            
			if (newValues != null && newValues.length > 0)
			{
				// Save data, since record need to be used for generating clones.
				if (!mTab.dataSave(false))
				{
					//throw new AdempiereException("SaveError");
				}
				
				// Retrieve the current record ID
				int recordId = mTab.getKeyID(mTab.getCurrentRow());
				
				Trx trx = Trx.get(Trx.createTrxName(), true);
				trx.start();
				try
				{
					saveMultipleRecords(Env.getCtx(), mTab.getTableName(), e.getPropertyName(), recordId, newValues, trx.getTrxName());
					trx.commit();
					mTab.dataRefreshAll();
				}
				catch(Exception ex)
				{
					trx.rollback();
					logger.severe(ex.getMessage());
					//throw new AdempiereException("SaveError");
				}
				finally
				{
					trx.close();
				}
			}
        }

    } // ValueChange
	
	/**************************************************************************
	 * Save Multiple records - Clone a record and assign new values to each 
	 * clone for a specific column.
	 * @param ctx context
	 * @param tableName Table Name
	 * @param columnName Column for which value need to be changed
	 * @param recordId Record to clone
	 * @param values Values to be assigned to clones for the specified column
	 * @param trxName Transaction
	 * @throws Exception If error is occured when loading the PO or saving clones
	 * 
	 * @author ashley
	 */
	protected void saveMultipleRecords(Properties ctx, String tableName, 
			String columnName, int recordId, Integer[] values, 
			String trxName) throws Exception
	{
		if (values == null)
		{
			return ;
		}
		
		int oldRow = mTab.getCurrentRow();
		MField lineField = mTab.getField("Line");	
		
		for (int i = 0; i < values.length; i++)
		{
			if (!mTab.dataNew(true))
			{
				throw new IllegalStateException("Could not clone tab");
			}
			
			mTab.setValue(columnName, values[i]);
			
			if (lineField != null)
			{
				mTab.setValue(lineField, 0);
			}
			
			if (!mTab.dataSave(false))
			{
				throw new IllegalStateException("Could not update tab");
			}
			
			mTab.setCurrentRow(oldRow);
		}
	}
}
