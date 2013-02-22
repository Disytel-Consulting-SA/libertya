package org.openXpertya.replication;

/**
 * Proceso de acceso desde terminal para setear retrieveUIDs a un conjunto de registros a partir de una fecha dada 
 * 
 * El mismo permite convertir una serie de registros interpretados por el framework de replicacion
 * originalmente como pre-existentes a registros listos para replicar.  Para esto, convierte los UIDs 
 * generados inicialmente (basado en otros campos) al valor correcto usando la secuencia de replicación en cuestion.
 * 
 * Este escenario se da cuando se cargan registros ANTES de instalar el replicacion.  Luego, al momento
 * de instalar replicación, estos datos se interpretan como pre-existentes cuando en realidad no lo son.
 * 
 * LAS TABLAS DE REPLICACION DEBEN YA ESTAR CONFIGURADAS, EN CASO CONTRARIO EL PROCESO 
 * NO FUNCIONARA DEBIDO A QUE LOS TRIGGERS NO SE ENCONTRARAN DEFINIDOS NI ACTIVOS.
 */

import org.openXpertya.model.MTableReplication;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class ReplicationPreProductionManageRetrieveUIDs extends AbstractTerminalLaunchProcess {

	/** Fecha/hora de inicio a contemplar los registros a modificar de todas las tablas */
	static String startingDateTime;
	
	/** Si es true, entonces seteará los repArrays de los registros para que repliquen (ej: 011) 
	 *  Si es false, entonces directamente los marcará como replicados (ej: 022) */
	static boolean setRepArraysForReplication;
	
	/** Si es true, fuerza el seteo de retrieveUID incluso en los casos en que no esté en null */
	static boolean forceSetRetrieveUID;
	
	public static void main(String[] args) {
		
		// Parametros
		if (args.length!=3)
			showHelp("Se requieren tres parametros: 1)startingDateTime, 2)setRepArraysForReplication, 3)forceSetRetrieveUID");
		startingDateTime = args[0];
		setRepArraysForReplication = "Y".equals(args[1]);
		forceSetRetrieveUID = "Y".equals(args[2]);
		
		// Iniciar el proceso
		new ReplicationPreProductionManageRetrieveUIDs().execute();
	}
	
	
	@Override
	protected void prepare() {
		// TODO Auto-generated method stub
	}
	

	@Override
	protected String doIt() throws Exception {
		
		 showCreatedRows_ReplicationNotInstalled();
		// showCreatedRows_ReplicationInstalled();

		// Con setear el repArray, automaticamente también se generan los retrieveUID de manera automatica
		String sqlSet   = 	" SET reparray = 'SET_VALUE_', includeInReplication = '" + (setRepArraysForReplication?"Y":"N") + "'";
		String sqlWhere =	" WHERE created >= '" + startingDateTime + "'" +
							(forceSetRetrieveUID ? "" : " AND retrieveUID IS NULL ") +
							" AND (AD_Client_ID = 0 OR AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + ")";
		String sqlFinal =	" AND AD_ComponentObjectUID IS NULL ";
		
		String result = "";
		// Iterar por las tablas seteando el repArray y adicionalmente cargando retrieveUID donde falte
		for (String table : ReplicationTableManager.getTablesForReplication(get_TrxName())) {
			int ouid = DB.getSQLValue(get_TrxName(), " SELECT count(1) FROM AD_Column WHERE AD_Table_ID = " + M_Table.getTableID(table, get_TrxName()) + " AND columnname = 'AD_ComponentObjectUID'" );
			// Limpiar retrieveUIDs si corresponde
			if (forceSetRetrieveUID)
				DB.executeUpdate(" UPDATE " + table + " SET repArray = 'SKIP', retrieveUID = null " + sqlWhere  + (ouid>0?sqlFinal:""), get_TrxName());
			// Actualizar los retrieveUIDs
			int count = DB.executeUpdate(	" UPDATE " + table + 
											sqlSet.replace("_VALUE_", getRepArrayToSet(table)) + 
											sqlWhere +
											(ouid>0?sqlFinal:"")
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
		
		if (setRepArraysForReplication) {
			// Cambiar: Conf. Bidireccional -> Acción Replicar 
			repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATE_INSERT);
			// Cambiar: Conf. Recepcion -> Sin accion
			repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_RECEIVE, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		}
		else {
			// Cambiar: Conf. Bidireccional -> Replicado
			repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_RECEIVE, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
			// Cambiar: Conf. Bidireccional -> Replicado
			repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SENDRECEIVE, ReplicationConstants.REPARRAY_REPLICATED);
			// Cambiar: Conf. Envio -> Replicado
			repArray = repArray.replace(ReplicationConstants.REPLICATION_CONFIGURATION_SEND, ReplicationConstants.REPARRAY_REPLICATED);
		}
			
		return repArray;
	}
	
	/**
	 * Registros preexistentes antes de instalar replicacion.
	 * Lista de tablas basadas en AD_Table
	 */
	protected void showCreatedRows_ReplicationNotInstalled() {
		String result = "";
		int[] tableIDs = PO.getAllIDs("AD_Table", " 1 = 1 ORDER BY tablename", get_TrxName());
		for (int tableID : tableIDs) {
			String tableName = DB.getSQLValueString(get_TrxName(), " SELECT tablename FROM AD_Table WHERE AD_Table_ID = ? ", tableID);
			int ouid = DB.getSQLValue(get_TrxName(), " SELECT count(1) FROM AD_Column WHERE AD_Table_ID = " + tableID + " AND columnname = 'AD_ComponentObjectUID'" );
			int count = DB.getSQLValue(get_TrxName(), 	" SELECT COUNT(1) FROM " + tableName + 						// Contar la cantidad de registros
														" WHERE created >= '" + startingDateTime + "'" + 			// Creados a partir de fecha indicada por Horacio
														" AND (AD_Client_ID = 0 OR AD_Client_ID = 1010016)" +		// Solo configuracion general o de compañia Libertya
														(ouid>0?" AND AD_ComponentObjectUID IS NULL ":"")			// Que no sean parte de un componente 
																													//   (si es que existe columna AD_ComponentObjectUID en la tabla dada
										);
			if (count>0)
				result += (tableName + ":" + count + "\n");
		}
		showHelp(result);
	}
	
	
	/**
	 * Revision de registros preexistentes luego de instalar replicacion e importar ruleSet.
	 * Lista de tablas basadas en AD_TableReplication
	 */
	protected void showCreatedRows_ReplicationInstalled() throws Exception {
		String result = "";
		for (String tableName : ReplicationTableManager.getTablesForReplication(get_TrxName())) {
			int ouid = DB.getSQLValue(get_TrxName(), " SELECT count(1) FROM AD_Column WHERE AD_Table_ID = " + M_Table.getTableID(tableName, get_TrxName()) + " AND columnname = 'AD_ComponentObjectUID'" );
			int count = DB.getSQLValue(get_TrxName(), 	" SELECT COUNT(1) FROM " + tableName + 						// Contar la cantidad de registros
														" WHERE created >= '" + startingDateTime + "'" + 			// Creados a partir de fecha indicada por Horacio
														" AND (AD_Client_ID = 0 OR AD_Client_ID = 1010016)" +		// Solo configuracion general o de compañia Libertya
														(ouid>0?" AND AD_ComponentObjectUID IS NULL ":"")
										);
			if (count>0)
				result += (tableName + ":" + count + "\n");
		}
		showHelp(result);
	}

}

