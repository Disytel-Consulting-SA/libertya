package org.openXpertya.replication.filter;

/**
 * Filtrado en replicacion de transferencias segun su tipo de movimiento
 * Este filtro se encarga de limitar la replicacion de transferencias (y sus lineas)
 * en función del tipo de movimiento de las mismas.
 * 
 * La lógica es la siguiente:
 * 
 * Host Origen:
 * """"""""""""
 * - Para los Transfer salientes, replicar el registro unicamente a Central
 * - Para los Transfer entrantes, replicar el registro a host destino (basado en el M_WarehouseTo_ID de destino)
 * 								  replicar el registro también a Central 
 * 
 * Host Destino:
 * """""""""""""
 * - Para los Transfer entrantes, replicar el registro a host origen (basado en el M_Warehouse_ID de origen)
 * 								  replicar el registro también a Central
 * 
 * Se presupone que Central es el host cuyo repArrayPos es <code>CENTRAL_REPARRAY_POS</code>
 * Se presupone que las Sucursales poseen un repArrayPos mayor a <code>CENTRAL_REPARRAY_POS</code>
 */

import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MReplicationHost;
import org.openXpertya.model.X_M_Transfer;
import org.openXpertya.model.X_M_TransferLine;
import org.openXpertya.model.X_M_Warehouse;
import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class TransferReplicationFilter extends ReplicationFilter {

	public static final int CENTRAL_REPARRAY_POS = 1;
	
	public static int thisHostPos = -1;
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		
		// Recuperar por única vez la posicion de este host dentro del repArray
		if (thisHostPos == -1)
			thisHostPos = DB.getSQLValue(trxName, " SELECT replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
		
		// Unicamente tablas de transferencia
		String tableName = group.getTableName(); 
		if (!X_M_Transfer.Table_Name.equalsIgnoreCase(tableName) &&
			!X_M_TransferLine.Table_Name.equalsIgnoreCase(tableName))
			return;
		
		// Para eliminaciones, omitir el filtrado
		if (MChangeLog.OPERATIONTYPE_Deletion.equalsIgnoreCase(group.getOperation()))
			return;
		
		// transferID que se está gestionando
		int transferID = -1;
		// estamos en el origen o en el destino? esto se determina en función del retrieveUID del registro 
		boolean atSource;
		// Es un transfer saliente o entrante?
		boolean incoming = false;
		// warehouse de origen
		int sourceWarehouseID = -1;
		// warehouse de destino
		int targetWarehouseID = -1;
		// posision de la org. perteneciente al warehouse origen o destino (según si estamo en destino u origen)
		int warehousePos = -1;
		
		// Procesar los datos del group (M_Transfer o M_TransferLine, aunque este ultimo no contara con todos los campos)
		for (ChangeLogElement element : group.getElements()) {
			if ("MovementType".equalsIgnoreCase(element.getColumnName()))  
				incoming = X_M_Transfer.MOVEMENTTYPE_Incoming.equalsIgnoreCase((String)element.getNewValue());
			if (("M_Warehouse_ID").equalsIgnoreCase(element.getColumnName()))
				sourceWarehouseID = Integer.parseInt((String)element.getNewValue());
			if (("M_WarehouseTo_ID").equalsIgnoreCase(element.getColumnName()))
				targetWarehouseID = Integer.parseInt((String)element.getNewValue());				
			if (("M_Transfer_ID").equalsIgnoreCase(element.getColumnName()))
				transferID = Integer.parseInt((String)element.getNewValue());
		}			
		// Estamos procesando la tabla M_TransferLine? definir los datos no leidos en el group dado que algunos no existen en las líneas
		if (X_M_TransferLine.Table_Name.equalsIgnoreCase(tableName)) {
			X_M_Transfer aTransfer = new X_M_Transfer(Env.getCtx(), transferID, trxName);
			incoming = X_M_Transfer.MOVEMENTTYPE_Incoming.equalsIgnoreCase(aTransfer.getMovementType());
			sourceWarehouseID = aTransfer.getM_Warehouse_ID();
			targetWarehouseID = aTransfer.getM_WarehouseTo_ID();
		}
		
		// Determinar si el registro se originó en este host o estoy en el host destino (a partir del retrieveUID)
		String retrieveUID = group.getAd_componentObjectUID();
		int groupOwner = Integer.parseInt(retrieveUID.substring(1, retrieveUID.indexOf("_")));
		atSource = (groupOwner == thisHostPos);

		// Si estoy en el origen  recuperar la posición de la org en la que pertenece el M_WarehouseTo_ID 
		// Si estoy en el destino recuperar la posición de la org en la que pertenece el M_Warehouse_ID
		X_M_Warehouse aWarehouse = new X_M_Warehouse(Env.getCtx(), atSource ? targetWarehouseID : sourceWarehouseID, trxName);
		warehousePos = MReplicationHost.getReplicationPositionForOrg(aWarehouse.getAD_Org_ID(), trxName);
		if (warehousePos < 0)
			throw new Exception("Imposible recuperar posición en el repArray para la organización del warehouse: " + aWarehouse.getName());

		// Primeramente llevar todo el repArray del registro a 000... 
		StringBuilder sb = new StringBuilder(group.getRepArray());
		for (int i=0; i<sb.length(); i++)
			sb.setCharAt(i, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);

		// Si es entrante, replicar a central y hacia el otro host (host origen o destino según si estoy en destino u origen)	
		if (incoming) {
			sb.setCharAt(CENTRAL_REPARRAY_POS-1, group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1));
			try {
				sb.setCharAt(warehousePos-1, group.getRepArray().charAt(warehousePos-1));
			}
			catch (StringIndexOutOfBoundsException e) {
				// El almacén pertence a una organizacion (host) actualmente fuera del anillo de replicacion, no hacer nada mas
			}
		}
		// Si es saliente y estoy en el origen, replicar hacia la central
		if (!incoming && atSource)
			sb.setCharAt(CENTRAL_REPARRAY_POS-1, group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1));
		// Setear thisHostPos en 0 (casos en que el almacen origen y destino pertenezcan a la misma organización)
		sb.setCharAt(thisHostPos-1, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		
		// Setear el repArray final
		group.setRepArray(sb.toString());
		

	}

}
