package org.openXpertya.cc;

import org.openXpertya.model.PO;

public class CurrentAccountLocalManager extends CurrentAccountManager {

	// Constructores
	
	public CurrentAccountLocalManager(){
		setBalanceStrategy(new BalanceLocalStrategy());
		setObtainStrategy(new ObtainLocalStrategy());
	}

	@Override
	public String getUIDColumnName(PO po) {
		return po.get_TableName()+"_ID";
	}	
}
