package org.openXpertya.replication;

import org.openXpertya.plugin.install.ChangeLogGroup;

public class ChangeLogGroupReplication extends ChangeLogGroup {

	protected String repArray;
	protected boolean timeOut;
	
	public ChangeLogGroupReplication(int adTableId,
			String adComponentObjectUID, String operation, String tableName, String repArray) {
		super(adTableId, adComponentObjectUID, operation, tableName);
		this.repArray = repArray;
		// TODO Auto-generated constructor stub
	}
	
	public String getRepArray() {
		return repArray;
	}

	public void setRepArray(String repArray) {
		this.repArray = repArray;
	}

	public boolean isTimeOut() {
		return timeOut;
	}

	public void setTimeOut(boolean timeOut) {
		this.timeOut = timeOut;
	}	
	

}
