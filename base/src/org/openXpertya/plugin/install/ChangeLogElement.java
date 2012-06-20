package org.openXpertya.plugin.install;

import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.DisplayType;

public class ChangeLogElement {

	// Variables de instancia
	
	/** Datos de la columna */
	private String columnName;
	private int AD_Reference_ID;
	private int AD_Reference_Value_ID;
	private boolean isKey;
	
	/** Old value */
	
	private Object oldValue; 
	
	/** New value */
	
	private Object newValue;
	
	/** Valor Binario */
	
	private byte[] binaryValue;
	
	/** AD_Changelog_ID */
	
	private int AD_Changelog_ID;
	
 	// Constructores
	
	public ChangeLogElement(Integer ad_column_id, String oldValue, String newValue, Object binaryValue, int changelogID){
		setColumnData(ad_column_id);
		setOldValue(valueFromColumnType(oldValue, AD_Reference_ID));
		setNewValue(valueFromColumnType(newValue, AD_Reference_ID));
		setBinaryValue((byte[])binaryValue);
		setAD_Changelog_ID(changelogID);
	}
	
	private void setColumnData(int ad_column_id)
	{
		columnName = 	  		(String)(ReplicationCache.columnsData.get(ad_column_id)[0]);
		AD_Reference_ID = 		(Integer)(ReplicationCache.columnsData.get(ad_column_id)[1]);
		AD_Reference_Value_ID = (Integer)(ReplicationCache.columnsData.get(ad_column_id)[2]);
		isKey 				  = (Boolean)(ReplicationCache.columnsData.get(ad_column_id)[3]);
	}
	
	// Varios
	
	private Object valueFromColumnType(String value, Integer displayType){
		if(value == null || value.equals("NULL")){
			return null;
		}
		Object newValue = value;
//		if(DisplayType.YesNo == displayType){
//			if(value.equalsIgnoreCase("false")){
//				newValue = "N";
//			}
//			else{
//				newValue = "Y";
//			}
//		}
		if(DisplayType.isLOB(displayType)){
			newValue = getBinaryValue();
		}
		return newValue;
	}
	
	
	@Override
	public String toString(){
		return columnName+"_"+getOldValue()+"_"+getNewValue()+"_"+getBinaryValue();
	}
	
	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}

	public Object getNewValue() {
		return newValue;
	}


	public void setBinaryValue(byte[] binaryValue) {
		this.binaryValue = binaryValue;
	}


	public byte[] getBinaryValue() {
		return binaryValue;
	}


	public String getColumnName() {
		return columnName;
	}


	public int getAD_Reference_ID() {
		return AD_Reference_ID;
	}


	public int getAD_Reference_Value_ID() {
		return AD_Reference_Value_ID;
	}


	public boolean isKey() {
		return isKey;
	}


	public int getAD_Changelog_ID() {
		return AD_Changelog_ID;
	}


	public void setAD_Changelog_ID(int aDChangelogID) {
		AD_Changelog_ID = aDChangelogID;
	}






}
