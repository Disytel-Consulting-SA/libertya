package org.openXpertya.replication.filter;

import org.openXpertya.replication.ChangeLogGroupReplication;

/**
 * Toda clase que realice filtrado de replicación por registro debe
 * extender de esta clase a fin de que el framework lo invoque al
 * momento de procesar los registros a enviar.  
 * 
 * @author fcristina
 */

public abstract class ReplicationFilter {
	
	
	/**
	 * Metodo encargado de realizar filtrados adicionales
	 * sobre un array de replicación recibido por parametro
	 * 
	 * @param group registro a procesar
	 * @param trxName transaccion
	 */
	public abstract void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception;

}
