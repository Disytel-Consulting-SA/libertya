package org.openXpertya.replication.filter;

import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationCache;
import org.openXpertya.replication.ReplicationConstants;

/**
 * Filtrado en replicación por registro.  
 * Este filtro se encarga de direccionar la replicación del registro (group) 
 * en cuestión unicamente hacia el host para el cual se encuentra definido
 * (campo AD_Org_ID del registro).
 * 
 * Ejemplo:  Sea una tabla que originalmente se replica desde la central
 * hacia las sucursales, se desea que en función de la columna AD_Org_ID,
 * los hosts destinos se reduzca unicamente al indicado en dicha columna.
 * 
 * Suponiendo M_ProductPrice como tabla, la configuración del repArray es
 * entonces 0111 (de la central hacia las cuatro sucursales existentes).
 * Si un registro tiene indicado AD_Org_ID = 1234567, y dicho valor mapea
 * con la posición 3 dep repArray, entonces el repArray resultante será 0010.
 * 
 * @author fcristina
 *
 */

public class OrgReplicationFilter extends ReplicationFilter {

	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

		String orgID = "";
		// recorrer las columnas hasta recuperar AD_Org_ID
		for (ChangeLogElement element : group.getElements()) {
			if ("AD_Org_ID".equalsIgnoreCase(element.getColumnName())) {
				// Obtener la posicion donde debe replicar.  Si el registro tiene org = 0 (*), no se filtra 
				orgID = (String)element.getNewValue();
				if ("0".equals(orgID))
					break;
				
				int posInRepArray = ReplicationCache.map_RepArrayPos_OrgID.get(orgID)-1;
				
				// Llevar a 0 toda posicion diferente a la del host destino segun el Org
				StringBuilder sb = new StringBuilder(group.getRepArray());
				for (int i=0; i<sb.length(); i++)
					if (i != posInRepArray)
						sb.setCharAt(i, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
				group.setRepArray(sb.toString());
			}
		}
	}

}
