package org.openXpertya.plugin.install;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.openXpertya.util.DB;
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
	
	/** Datos actuales de todas las columnas en base de datos */
	private static HashMap<Integer, Object[]> columnsData = null; 
	
	/**
	 * Carga inicial de todas las entradas relacionadas con las columnas
	 * De esta manera evitamos realizar consultas SQL durante 
	 * cada invocación al constructor ChangeLogElement 
	 * @return
	 */
	public static HashMap<Integer, Object[]> loadColumnData(String trxName) throws Exception
	{
		if (columnsData != null)
			return columnsData;
	
		columnsData = new HashMap<Integer, Object[]>();
		String sql = new String( " SELECT columnname, AD_Reference_ID, AD_Reference_Value_ID, isKey, ad_column_id FROM AD_Column ");
		PreparedStatement pstmt = DB.prepareStatement(sql, trxName);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next())
		{
			Object[] data =  {rs.getString(1), rs.getInt(2), rs.getInt(3), ("Y".equals(rs.getString(4))) };
			columnsData.put(rs.getInt(5), data);
		}
		rs.close();
		rs =null;
		pstmt = null;		
		
		return columnsData;
	}
	
	
	public static void freeColumnData()
	{
		columnsData = null;
		System.gc();
	}
	
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
		columnName = 	  		(String)(columnsData.get(ad_column_id)[0]);
		AD_Reference_ID = 		(Integer)(columnsData.get(ad_column_id)[1]);
		AD_Reference_Value_ID = (Integer)(columnsData.get(ad_column_id)[2]);
		isKey 				  = (Boolean)(columnsData.get(ad_column_id)[3]);
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
