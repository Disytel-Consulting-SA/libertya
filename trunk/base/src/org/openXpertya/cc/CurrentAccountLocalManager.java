package org.openXpertya.cc;

import org.openXpertya.model.PO;

public class CurrentAccountLocalManager extends CurrentAccountManager {

	// Constructores
	
	public CurrentAccountLocalManager(){
		setBalanceStrategy(new BalanceLocalStrategy());
		setObtainStrategy(new ObtainLocalStrategy());
	}

	@Override
	public String getUIDColumnName(PO po) throws Exception {
		String columnName = po.get_TableName()+"_ID"; 
		// Verifico si la columna existe
		PO.existsColumnInTable(po.getCtx(), po.get_TableName(), columnName,
				po.get_TrxName(), true);
		return columnName;
	}
}
