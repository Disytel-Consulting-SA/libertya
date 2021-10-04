package org.openXpertya.replication.filter;

import org.openXpertya.model.X_M_Product;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstantsWS;

public class ExternalAttachmentProductsOnlyReplicationFilter extends ExternalAttachmentReplicationFilter {
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		// Solo replicar adjuntos para la tabla de articulos
		if (!X_M_Product.Table_Name.equals(group.getTableName())) {
			repArraySetValueAllPositions(group, ReplicationConstantsWS.REPLICATION_CONFIGURATION_NO_ACTION);
			return;
		}	
		// En caso de que sea un adjunto de articulo, delegar a la logica de la superclase 
		super.applyFilter(trxName, group);
	}

}
