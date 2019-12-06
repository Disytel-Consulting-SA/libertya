package org.openXpertya.plugin.install;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Util;

public class ChangeLogElement {

	// Variables de instancia
	
	/** Datos de la columna */
	private String columnName;
	private int AD_Reference_ID;
	private int AD_Reference_Value_ID;
	private boolean isKey;
	// Exclusivo para replicación: hosts destino donde replicar un elemento (si la colección esta vacía, se interpreta que es para todos los hosts)
	private Set<Integer> targetOnly = new HashSet<Integer>();
	
	/** Old value */
	
	private Object oldValue; 
	
	/** New value */
	
	private Object newValue;
	
	/** Valor Binario */
	
	private byte[] binaryValue;
	
	/** AD_Changelog_ID */
	
	private int AD_Changelog_ID;
	
	/** changeLogUID */
	
	private String changeLogUID;
	
	// Constructores
	
	/** Constructor especifico para almacenar informacion bajo encoding Base64.  
	 * Este constructor se incorpora para la replicación de binarios bajo dicho encoding. */	
	public ChangeLogElement(Integer ad_column_id, String newBase64EncodedValue, int changelogID){
		this(ad_column_id, newBase64EncodedValue, changelogID, null);
	}
	
	/** Constructor especifico para almacenar informacion bajo encoding Base64.  
	 * Este constructor se incorpora para la replicación de binarios bajo dicho encoding. */
	public ChangeLogElement(Integer ad_column_id, String newBase64EncodedValue, int changelogID, String changeLogUID){
		setColumnData(ad_column_id);
		setNewValue(newBase64EncodedValue);
		setAD_Changelog_ID(changelogID);
		setChangeLogUID(changeLogUID);
	}
	
 	
	/** Constructor original.  Concebido originalmente para la gestión/instalacion de componentes */
	public ChangeLogElement(Integer ad_column_id, String oldValue, String newValue, Object binaryValue, int changelogID){
		this(ad_column_id, oldValue, newValue, binaryValue, changelogID, null);
	}
	
	/** Constructor original.  Concebido originalmente para la gestión/instalacion de componentes */
	public ChangeLogElement(Integer ad_column_id, String oldValue, String newValue, Object binaryValue, int changelogID, String changeLogUID){
		setColumnData(ad_column_id);
		setOldValue(valueFromColumnType(oldValue, AD_Reference_ID));
		setNewValue(valueFromColumnType(newValue, AD_Reference_ID));
		setBinaryValue((byte[])binaryValue);
		setAD_Changelog_ID(changelogID);
		setChangeLogUID(changeLogUID);
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
		if(value == null || value.equalsIgnoreCase("NULL")){
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

	public String getChangeLogUID() {
		return changeLogUID;
	}


	public void setChangeLogUID(String changeLogUID) {
		this.changeLogUID = changeLogUID;
	}
	
	public Set<Integer> getTargetOnly() {
		return targetOnly;
	}

	public void setTargetOnly(Set<Integer> targetOnly) {
		this.targetOnly = targetOnly;
	}

	public void addTarget(Integer target) {
		this.targetOnly.add(target);
	}


}
