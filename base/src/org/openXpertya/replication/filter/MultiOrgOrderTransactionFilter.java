package org.openXpertya.replication.filter;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_C_OrderTax;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.util.Env;

/**
 * Filtro de replicación.
 * 
 * Excepción MultiOrg para C_Order, C_OrderLine y C_OrderTax; las cuales pueden
 * ser tickets (transferidos y transferibles) o presupuestos.  En estos casos
 * estos registros NO deben ser replicados al sub-anillo de replicación, únicamente
 * deben ser replicados hacia el host central 
 *
 */

public class MultiOrgOrderTransactionFilter extends MultiOrgTransactionFilter {

	/**
	 * 
	 * 
• Replicar
    ∘ Ticket Migrado: Nuevo tipo de documento a crear con clave SOSOTM.
    ∘ Ticket Histórico: Clave SOSOTH.
• No Replicar
    ∘ Presupuestos: Clave SOON.
    ∘ Pedidos o Tickets Transferibles: Clave SOSOT.
    ∘ Pedidos o Tickets Transferidos: Clave SOSOTD.
	 */
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		// Unicamente tablas relacionadas con C_Order
		String tableName = group.getTableName(); 
		if (!X_C_Order.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderLine.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderTax.Table_Name.equalsIgnoreCase(tableName))
			return;
		
		// Recuperar pedido y tipo de documento del mismo
		X_C_Order anOrder = TicketReplicationFilter.getOrder(group, trxName);
		MDocType docType = new MDocType(Env.getCtx(), anOrder.getC_DocType_ID(), trxName);

		// Es uno de los 3 casos en que se debe enviar únicamente a central?
		if (MDocType.DOCTYPE_Proposal.equals(docType.getDocTypeKey()) ||
				MDocType.DOCTYPE_Pedido_Transferible.equals(docType.getDocTypeKey()) ||
				MDocType.DOCTYPE_Pedido_Transferido.equals(docType.getDocTypeKey())) {
			repArraySendToCentralOnly(group);
		} else {
			super.applyFilter(trxName, group);
		}
	}

}
