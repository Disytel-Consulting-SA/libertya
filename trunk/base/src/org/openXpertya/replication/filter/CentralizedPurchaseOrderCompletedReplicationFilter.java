package org.openXpertya.replication.filter;

/**
 * Filtro especifico para host Central:
 * 	Replica pedidos (sus lineas e impuestos) hacia sucursales
 *  unicamente si el mismo se encuentra completado, filtrando
 *  además por la organización especificada en el pedido.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.openXpertya.model.MPreference;
import org.openXpertya.model.X_C_Order;
import org.openXpertya.model.X_C_OrderLine;
import org.openXpertya.model.X_C_OrderTax;
import org.openXpertya.process.DocAction;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstantsWS;
import org.openXpertya.util.Util;

public class CentralizedPurchaseOrderCompletedReplicationFilter extends TicketReplicationFilter {

	/** Hosts pertenecientes a sub-anillos (si es que existen) */
	static ArrayList<Set<Integer>> subRingsHosts = null;
	
	static {
		try {
			// Recuperación del archivo
			Properties prop = Util.loadProperties(MPreference.GetCustomPreferenceValue(MultiOrgTransactionFilter.RULES_LOCATION_PREF));
			subRingsHosts = new ArrayList<Set<Integer>>();
			for (Entry<Object, Object> e : prop.entrySet()) {
				// Es una regla de definición de sub-anillos? 
				if (e.getKey().toString().startsWith(MultiOrgTransactionFilter.RULE_SUBRINGS_PREFIX)) {
					Set<Integer> currentSubRingHosts = new HashSet<Integer>();
					// Recorrer la nómina de hosts para un subanillo y agregar todos los hosts
					String[] hosts = e.getValue().toString().toLowerCase().split(",");
					for (String host : hosts)
						currentSubRingHosts.add(Integer.parseInt(host.trim()));		
					// Incorporar a la colección de subanillos
					subRingsHosts.add(currentSubRingHosts);
				}
			}
			
		} catch (Exception e) {
			throw new RuntimeException("Error al inicializar CentralizedPurchaseOrderCompletedReplicationFilter: " + e.getMessage());
		}
	}
	
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

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
		
		// Se aplica primeramente un filtrado por organización
		OrgReplicationFilter filter = new OrgReplicationFilter();
		filter.applyFilter(trxName, group);

		// Si el destino es parte de un sub-anillo, entonces enviar el pedido a los restantes hosts
		includeSubRingsInFilter(group);
		
		// Si estamos en el host central y la OC originada se encuentra en borrador, no replicar. 
		if (DocAction.STATUS_Drafted.equalsIgnoreCase(anOrder.getDocStatus())) {
			group.setRepArray("");
		}
	
	}
	
	/**
	 * Si el host destino pertenece a un sub-anillo de replicación, entonces se propaga la  
	 * replicación de los pedidos/lineas/impuestos hacia los otros hosts del subanillo
	 */
	protected void includeSubRingsInFilter(ChangeLogGroupReplication group) {
		// Nuevo reparray a partir del actual
		StringBuilder newRepArray = new StringBuilder(group.getRepArray());
		
		// Para cada posicion del reparray (omitiendo central)
		for (int repArrayPos=1; repArrayPos<group.getRepArray().length(); repArrayPos++) {
			// Si no tiene marca de replicacion para esta posicion, continuar con la siguiente
			if (group.getRepArray().charAt(repArrayPos) == ReplicationConstantsWS.REPLICATION_CONFIGURATION_NO_ACTION)
				continue;
			// Por cada definición de sub-anillo, validar si hay que incluir en el repArray
			for (Set<Integer> subRing : subRingsHosts) {
				// Si esta definicion de subanillo no contiene el host destino, continuar con la siguiente
				if (!subRing.contains(repArrayPos+1)) 
					continue;
				// Propagar el valor del host destino hacia los demás hosts del subanillo
				for (Integer host : subRing) {
					// Saltear host destino (no hay que propagar)
					if (host == repArrayPos+1)
						continue;
					// Propagar la config de replicación hacia el otro host del subanillo
					newRepArray.setCharAt(host-1, group.getRepArray().charAt(repArrayPos));
				}
			}
		}
		
		// Actualizar repArray con eventuales cambios
		group.setRepArray(newRepArray.toString());
	}

	
}
