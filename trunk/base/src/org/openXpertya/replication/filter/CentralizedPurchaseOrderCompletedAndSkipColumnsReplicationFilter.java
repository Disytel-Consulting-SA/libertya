package org.openXpertya.replication.filter;

import org.openXpertya.replication.ChangeLogGroupReplication;

public class CentralizedPurchaseOrderCompletedAndSkipColumnsReplicationFilter extends CentralizedPurchaseOrderCompletedReplicationFilter {
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

		// Filtrar los POO según superclase 
		super.applyFilter(trxName, group);

		// Filtrar ademas eventuales columnas indicadas para su omisión
		// TODO: Lo correcto sería poder definir 2 filtros (el Centralized y el SkipColumns),
		//			pero la logica de union de reparrays resultantes luego de la aplicación de 
		//			2 o más filtros es el OR.  Por consiguiente como el SkipColumnsFilter no
		//			gestiona reparrays, siempre quedan todas las sucursales como destinos,
		//			ignorándose así la aplicación del OrgReplicationFilter.
		SkipColumnsFilter filter = new SkipColumnsFilter();
		filter.applyFilter(trxName, group);
	}

}
