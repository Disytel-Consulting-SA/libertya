package org.openXpertya.replication.filter;

import org.openXpertya.model.X_M_ProductPrice;
import org.openXpertya.model.X_M_ProductPriceInstance;
import org.openXpertya.replication.ChangeLogGroupReplication;

public class ProductPriceReplicationFilter extends ReplicationFilter {

	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group)
			throws Exception {
		
		// Para las sucursales, PriceList debe ser igual que PriceSTD
		if (X_M_ProductPrice.Table_Name.equalsIgnoreCase(group.getTableName()) || X_M_ProductPriceInstance.Table_Name.equalsIgnoreCase(group.getTableName()))
			setNewValueForElement(group, "PriceList", getNewValueForElement(group, "PriceStd"));
		
		// Este filtro no debe realizar cambios sobre el reparray, llevar todo a cero y dejar que el OrgReplicationFilter se encargue
		repArraySetValueAllPositions(group, '0');
		
	}

}
