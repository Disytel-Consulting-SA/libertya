package org.openXpertya.replication;

import org.openXpertya.model.MTableReplication;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * 
 * Proceso de acceso desde terminal para setear repArrays a un conjunto de registros a partir de una fecha dada
 * 
 * Este proceso está pensado en los casos en que se toma como BBDD base, una base con datos
 * ya configurados, con lo cual dichos registros NO deberían ser replicados (ni enviados ni recibidos).
 * 
 * Con lo cual todos el proceso recorrerá todas las tablas y actualizará los repArrays de los registros   
 * "pre-existentes" (pero convertidos a formato replicación mediante ReplicationPreProductionManageRetrieveUIDs)
 *  en funcion de la configuración de AD_TableReplication
 *
 * Casos:
 * 
 * 	Si una tabla tiene replicación bidireccional o de envio, los registros en cuestión quedarán marcados como replicados
 *  Si una tabla solo recibe registros, entonces quedarán seteados en 0 
 *
 * LAS TABLAS DE REPLICACION DEBEN YA ESTAR CONFIGURADAS, EN CASO CONTRARIO EL PROCESO 
 * NO FUNCIONARA DEBIDO A QUE LOS TRIGGERS NO SE ENCONTRARAN DEFINIDOS NI ACTIVOS.
 *
 */

public class ReplicationPreProductionManageRepArrays extends AbstractTerminalLaunchProcess {

	/** Fecha/hora de inicio a contemplar los registros a modificar de todas las tablas */
	static String startingDateTime;

	
	public static void main(String[] args) {
			
		// Parametros
		if (args.length!=1)
			showHelp("Se requiere parametro: 1)startingDateTime");
		startingDateTime = args[0];

		// Iniciar el proceso
		new ReplicationPreProductionManageRepArrays().execute();
	}
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String doIt() throws Exception {
		// Query de seteo del repArray
		String sqlSet   = 	" SET reparray = 'SET_VALUE_', includeInReplication = 'N'";
		String sqlWhere =	" WHERE created >= '" + startingDateTime + "'" +
							" AND retrieveUID ilike 'h1_%'" +
							" AND (AD_Client_ID = 0 OR AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + ")";
		
		String result = "";
		// Iterar por las tablas seteando el repArray
		for (String table : ReplicationTableManager.getTablesForReplication(get_TrxName())) {
			// Actualizar los repArrays
			int count = DB.executeUpdate(	" UPDATE " + table + 
											sqlSet.replace("_VALUE_", getRepArrayToSet(table)) + 
											sqlWhere
										, get_TrxName());
			if (count>0)
				result += (table + ":" + count + "\n");
		}
		return "DONE! \n" + result;	
	}

	/**
	 * Devuelve el repArray a setear en los registros de la tabla especificada 
	 */
	protected String getRepArrayToSet(String tableName) {
		
		// Recuperar la conf. de repArray para una tabla en cuestion y modificarla segun setepArraysForReplication
		String repArray = MTableReplication.getReplicationArray(M_Table.getTableID(tableName, get_TrxName()), get_TrxName());
		if (repArray==null)
			return "";

		// Cambiar: Conf. Recibir -> Sin accion
		repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_RECEIVE, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		// Cambiar: Conf. Bidireccional -> Replicado
		repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED);
		// Cambiar: Conf. Envio -> Replicado
		repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SEND, ReplicationConstants.REPARRAY_REPLICATED);
		return repArray;
	}
	
}
