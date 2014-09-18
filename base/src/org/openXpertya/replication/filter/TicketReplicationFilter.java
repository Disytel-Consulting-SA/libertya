package org.openXpertya.replication.filter;

/**
 * Filtrado en replicacion de tickets
 * Este filtro se encarga de limitar la replicacion de tickets (y sus lineas)
 *
 * La lógica es la siguiente:
 *  
 * Host Origen: 
 * """"""""""""
 * - Ticket transferible se replica solo a central (destino almacenado en AD_Org_Transfer_ID)
 * - Ticket transferido (autogenerado) a central y a destino (basado en AD_Org_ID)
 * 
 * Host Destino:
 * """""""""""""
 * - Ticket transferido (recibido de origen) a central y a host origen (origen basado en AD_Org_Transfer_ID)
 *
 * Se presupone que Central es el host cuyo repArrayPos es <code>CENTRAL_REPARRAY_POS</code>
 * Se presupone que las Sucursales poseen un repArrayPos mayor a <code>CENTRAL_REPARRAY_POS</code>
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_C_OrderTax;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class TicketReplicationFilter extends ReplicationFilter {

	/** Queries para recuperar el C_DocType_ID almacenado en la cabecera del ticket */
	protected static HashMap<String, String> queryForTable = new HashMap<String, String>();
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		
		// Unicamente tablas de tickets
		String tableName = group.getTableName(); 
		if (!X_C_Order.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderLine.Table_Name.equalsIgnoreCase(tableName) &&
			!X_C_OrderTax.Table_Name.equalsIgnoreCase(tableName))
			return;
		
		// Para eliminaciones, omitir el filtrado
		if (MChangeLog.OPERATIONTYPE_Deletion.equalsIgnoreCase(group.getOperation()))
			return;
		
		// estamos en el origen o en el destino? esto se determina en función del retrieveUID del registro 
		boolean atSource;
		// Es un ticket transferible o transferido?
		boolean transferido = false;
		// organización de origen
		int sourceOrgID = -1; 
		// organización de destino
		int targetOrgID = -1;
		// posision de la org. origen o destino (según si estamos en destino u origen)
		int orgPos = -1;
		
		// Recuperar info del pedido y el tipo de documento
		X_C_Order anOrder = getOrder(group, trxName);
		MDocType docType = new MDocType(Env.getCtx(), anOrder.getC_DocType_ID(), trxName);

		// Determinar si el registro se originó en este host o estoy en el host destino (a partir del retrieveUID)
		String retrieveUID = group.getAd_componentObjectUID();
		int groupOwner = Integer.parseInt(retrieveUID.substring(1, retrieveUID.indexOf("_")));
		atSource = (groupOwner == thisHostPos);
		
		// Es un ticket transferible?
		if (MDocType.DOCTYPE_Pedido_Transferible.equalsIgnoreCase(docType.getDocTypeKey())) {
			transferido = false;
			targetOrgID = anOrder.getAD_Org_Transfer_ID();
			sourceOrgID = anOrder.getAD_Org_ID();
		}
		// Es un ticket transferido?
		else if (MDocType.DOCTYPE_Pedido_Transferido.equalsIgnoreCase(docType.getDocTypeKey())) {
			transferido = true;
			targetOrgID = anOrder.getAD_Org_ID();
			sourceOrgID = anOrder.getAD_Org_Transfer_ID();
		}
		// Si no es ninguno de los dos, filtrar hacia central unicamente
		else {
			repArraySendToCentralOnly(group);
			return;
		}
		
		// Si estoy en el origen recuperar la posición de la org destino según AD_Org_ID
		// Si estoy en el destino recuperar la posición de la org origen en la que pertenece el M_Warehouse_ID
		orgPos = MReplicationHost.getReplicationPositionForOrg(atSource ? targetOrgID : sourceOrgID, trxName);
		if (orgPos < 0)
			throw new Exception("Imposible recuperar posición en el repArray para la organización posicion: " + orgPos);

		
		// Primeramente llevar todo el repArray del registro a 000... 
		StringBuilder sb = new StringBuilder(group.getRepArray());
		for (int i=0; i<sb.length(); i++)
			sb.setCharAt(i, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);

		// Si es transferido, replicar a central y hacia el otro host (host origen o destino según si estoy en destino u origen)	
		if (transferido) {
			sb.setCharAt(CENTRAL_REPARRAY_POS-1, group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1));
			try {
				sb.setCharAt(orgPos-1, group.getRepArray().charAt(orgPos-1));
			}
			catch (StringIndexOutOfBoundsException e) {
				// Organizacion (host) actualmente fuera del anillo de replicacion, no hacer nada mas
			}
		}
		// Si es transferible y estoy en el origen, replicar hacia la central
		if (!transferido && atSource)
			sb.setCharAt(CENTRAL_REPARRAY_POS-1, group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1));
		// Setear thisHostPos en 0 (casos en que el origen y destino pertenezcan a la misma organización)
		sb.setCharAt(thisHostPos-1, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		
		// Setear el repArray final
		group.setRepArray(sb.toString());
		

	}
	
	
	/**
	 * Recupera la cabecera del pedido
	 */
	protected X_C_Order getOrder(ChangeLogGroupReplication group, String trxName) throws Exception {
		String query = " SELECT C_Order_ID FROM " + group.getTableName() + " WHERE retrieveUID = ?";
		
		// Si es un query especial, recuperarlo y redefinir
		if (queryForTable.get(group.getTableName().toLowerCase()) != null)
			query = queryForTable.get(group.getTableName().toLowerCase());
		
		// Obtener el X_C_Order
		PreparedStatement pstmt = DB.prepareStatement(query, trxName);
		pstmt.setString(1, group.getAd_componentObjectUID());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return new X_C_Order(Env.getCtx(), rs.getInt("C_Order_ID"), trxName);;
		return null;
	}

}
