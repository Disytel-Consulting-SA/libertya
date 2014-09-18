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

	
	static {
		// Recuperar por única vez la posicion de este host dentro del repArray
		if (thisHostPos == -1)
			thisHostPos = DB.getSQLValue(null, " SELECT replicationarraypos FROM AD_ReplicationHost WHERE thisHost = 'Y' ");
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
	 * Remueve de los elementos el especificado bajo el nombre columnName
	 * @param group grupo de elementos
	 * @param columnName criterio de busqueda del elemento a eliminar
	 * @return true si fue removido o false en caso contrario
	 */
	protected boolean removeElement(ChangeLogGroupReplication group, String columnName) {
		// Buscar la columna
		int pos = -1;
		for (ChangeLogElement element : group.getElements()) {
			pos++;
			if (columnName.equalsIgnoreCase(element.getColumnName()))
				break;
		}
		// Si está, eliminarla 
		if (pos>0) {
			group.getElements().remove(pos);
			return true;
		}
		// Si no está retornar false
		return false;
	}
	
	
	/**
	 * Retorna nuevo repArray con value en todas las posiciones de group
	 */
	protected String repArraySetValueAllPositions(String repArray, char value) {
		StringBuilder sb = new StringBuilder(repArray);
		for (int i=0; i<sb.length(); i++)
			sb.setCharAt(i, value);
		return sb.toString();
	}
	
	/**
	 * Retorna nuevo repArray con value modificado en la posicion index
	 */
	protected String repArraySetValueAtPosition(String repArray, int index, char value) {
		StringBuilder sb = new StringBuilder(repArray);
		sb.setCharAt(index, value);
		return sb.toString();
	}
	
	/**  Modifica el repArray para que envie unicamente a central */
	protected void repArraySendToCentralOnly(ChangeLogGroupReplication group)  {
		// Estado previo para central
		char previousState = group.getRepArray().charAt(CENTRAL_REPARRAY_POS-1);
		// No enviar a ninguna sucursal...
		group.setRepArray(repArraySetValueAllPositions(group.getRepArray(), ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION));
		// ...pero a central sí enviar
		group.setRepArray(repArraySetValueAtPosition(group.getRepArray(), CENTRAL_REPARRAY_POS-1, previousState));	
	}
}
