package org.openXpertya.replication.filter;

/**
 * Filtro especifico para host Central:
 * 	Replica pedidos (sus lineas e impuestos) hacia sucursales
 *  unicamente si el mismo se encuentra completado, filtrando
 *  además por la organización especificada en el pedido.
 */

import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_C_OrderTax;
import org.openXpertya.process.DocAction;
import org.openXpertya.replication.ChangeLogGroupReplication;

public class CentralizedPurchaseOrderCompletedReplicationFilter extends TicketReplicationFilter {

	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

		// Se aplica primeramente un filtrado por organización
		OrgReplicationFilter filter = new OrgReplicationFilter();
		filter.applyFilter(trxName, group);
		
		// Unicamente tablas de pedidos y relacionadas
		String tableName = group.getTableName(); 
		if (!X_C_Order.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderLine.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderTax.Table_Name.equalsIgnoreCase(tableName))
			return;
		
		// Recuperar info del pedido y el tipo de documento
		X_C_Order anOrder = getOrder(group, trxName);
		if (anOrder==null)
			return;
		
		// Si estamos en el host central la OC originada en ese encuentra en borrador, no replicar.  Esto se logra
		// modificando el valor de todas las posiciones a "replicado"
		if (DocAction.STATUS_Drafted.equalsIgnoreCase(anOrder.getDocStatus())) {
			// group.setRepArray(group.getRepArray().replace("1", "2").replace("3", "2").replace("a", "2"));
			group.setRepArray("");
		}
	
	}

	
}
