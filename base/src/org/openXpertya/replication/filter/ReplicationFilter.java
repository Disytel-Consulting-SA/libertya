package org.openXpertya.replication.filter;

import org.openXpertya.plugin.install.ChangeLogElement;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.DB;

/**
 * Toda clase que realice filtrado de replicación por registro debe
 * extender de esta clase a fin de que el framework lo invoque al
 * momento de procesar los registros a enviar.  
 * 
 * @author fcristina
 */

public abstract class ReplicationFilter {

	/** Posicion del host central.  TODO: Esto debería estar desharcodeado */
	public static final int CENTRAL_REPARRAY_POS = 1;
	/** Posicion de este host dentro del anillo de replicacion */
	public static int thisHostPos = -1;
	/** AD_Org_ID de este host dentro del anillo de replicacion */
	public static int thisHostOrg = -1;

	
	static {
		// Recuperar por única vez la posicion de este host dentro del repArray
		if (thisHostPos == -1)
			thisHostPos = DB.getSQLValue(null, " SELECT replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
		// Recuperar por única vez la posicion de este host dentro del repArray
		if (thisHostOrg == -1)
			thisHostOrg = DB.getSQLValue(null, " SELECT ad_org_id FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
	}
	
	/**
	 * Metodo encargado de realizar filtrados adicionales
	 * sobre un array de replicación recibido por parametro
	 * 
	 * @param group registro a procesar
	 * @param trxName transaccion
	 */
	public abstract void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception;
	
	/**
	 * Retorna el valor de un elemento (columna) del grupo
	 * @param group grupo de elementos 
	 * @param columnName nombre del elemento a buscar
	 * @return el valor para esa columna, o null si no encuentra la columna
	 */
	protected Object getNewValueForElement(ChangeLogGroupReplication group, String columnName) {
		// Buscar el dato y retornarlo
		for (ChangeLogElement element : group.getElements()) {
			if (columnName.equalsIgnoreCase(element.getColumnName())) {
				return element.getNewValue();
			}
		}
		// Si no está retornar null
		return null;
	}

	/**
	 *	Retorna la posicion de un elemento en un changegroup a partir del columnname, o -1 si no lo encuentra 
	 */
	protected static int getElementPos(ChangeLogGroupReplication group, String columnName) {
		// Buscar la columna
		int pos = -1;
		for (ChangeLogElement element : group.getElements()) {
			pos++;
			if (columnName.equalsIgnoreCase(element.getColumnName()))
				break;
		}
		return pos;
	}
	
	/**
	 * Remueve de los elementos el especificado bajo el nombre columnName
	 * @param group grupo de elementos
	 * @param columnName criterio de busqueda del elemento a eliminar
	 * @return true si fue removido o false en caso contrario
	 */
	public static boolean removeElement(ChangeLogGroupReplication group, String columnName) {
		// Buscar la columna
		int pos = getElementPos(group, columnName);
		// Si está, eliminarla 
		if (pos>0) {
			group.getElements().remove(pos);
			return true;
		}
		// Si no está retornar false
		return false;
	}
	
	/**
	 * Agrega un host destino a la nómina de hosts donde debe replicarse esta columna.
	 * @param group grupo de elementos
	 * @param columnName criterio de busqueda del elemento a eleminar
	 * @param target host destino donde debe replicarse esta columna
	 * @return true si fue removido o false en caso contrario
	 */
	public static boolean addTargetOnly(ChangeLogGroupReplication group, String columnName, Integer target) {
		// Buscar la columna
		int pos = getElementPos(group, columnName);
		// Si está, incorporar el target
		if (pos>0) {
			group.getElements().get(pos).addTarget(target);
			return true;
		}
		// Si no está retornar false
		return false;
	}
	
	
	/**
	 * Setea el repArray del group con value en todas las posiciones de group
	 */
	public static void repArraySetValueAllPositions(ChangeLogGroupReplication group, char value) {
		StringBuilder sb = new StringBuilder(group.getRepArray());
		for (int i=0; i<sb.length(); i++)
			sb.setCharAt(i, value);
		group.setRepArray(sb.toString());
	}
	
	/**
	 * Setea el repArray del group con value modificado en la posicion index
	 */
	public static void repArraySetValueAtPosition(ChangeLogGroupReplication group, int index, char value) {
		StringBuilder sb = new StringBuilder(group.getRepArray());
		sb.setCharAt(index, value);
		group.setRepArray(sb.toString());
	}
	
	/** 
	 * Modifica el repArray para que envie unicamente a central 
	 */
	public static void repArraySendToCentralOnly(ChangeLogGroupReplication group)  {
		// Estado previo para central
		char previousState = group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1);
		// No enviar a ninguna sucursal...
		repArraySetValueAllPositions(group, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		// ...pero a central sí enviar
		repArraySetValueAtPosition(group, CENTRAL_REPARRAY_POS-1, previousState);	
	}
	
	/** 
	 * Retorna true si el changegroup fue originado en el host actual, o false si fue originado en otro host 
	 */
	public static boolean atSource(ChangeLogGroupReplication group) {
		// Determinar si el registro se originó en este host o estoy en el host destino (a partir del retrieveUID)
		String retrieveUID = group.getAd_componentObjectUID();
		int groupOwner = Integer.parseInt(retrieveUID.substring(1, retrieveUID.indexOf("_")));
		return (groupOwner == thisHostPos);
	}
}
