package org.openXpertya.replication.filter;

import org.openXpertya.model.X_M_Locator;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstants;

public class StorageReplicationFilter extends ReplicationFilter {

	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		try {
			// Filtrado unicamente para MStorage
			if (X_M_Locator.Table_Name.equalsIgnoreCase(group.getTableName()))
				return;
			
			// Obtener la Org del Locator al cual apunta el Storage
			int locatorOrgID = Integer.parseInt((String)getNewValueForElement(group, "M_Locator_ID"));
			
			// Si la org del locator no es igual a la org de este host => NO REPLICAR A CENTRAL 	
			if (locatorOrgID != thisHostOrg)
				repArraySetValueAllPositions(group, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);

		} catch (Exception e) {
			StringBuffer error = new StringBuffer();
			error.append("Error al aplicar StorageReplicationFilter sobre registro ").append(group.getAd_componentObjectUID()).append(": ").append(e.getMessage());
			System.out.println(error);
		}
	}

}
