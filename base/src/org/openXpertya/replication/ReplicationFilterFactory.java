package org.openXpertya.replication;

/**
 * Gestión centralizada de las clases que implementan la
 * lógica de filtrado de replicación a nivel de registro.
 * 
 * Las clases en cuestión deben:
 * 		1) Ser configuradas en el diccionario de datos, en
 *    	   la tabla AD_TableReplication, indicando su nombre 
 * 		2) Extender de la clase ReplicacionFilter, e implemntar
 *    	   el método abstracto: applyFilter()
 *    
 * @author fcristina
 * 
 */

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Vector;

import org.openXpertya.replication.filter.ReplicationFilter;
import org.openXpertya.util.DB;

public class ReplicationFilterFactory {

	/** Filtros de replicación a aplicar, organizados por el nombre de la tabla. */
	protected static HashMap<String, Vector<ReplicationFilter>> filters = new HashMap<String, Vector<ReplicationFilter>>();
	/** Cache de instancias de filtros, organizadas por su nombre */
	protected static HashMap<String, ReplicationFilter> filterInstances = new HashMap<String, ReplicationFilter>();
	
	
	/**
	 * Busca en la configuración, los filters a utilizar
	 * @return
	 */
	public static Vector<ReplicationFilter> getFiltersForTable(String trxName, String tableName) throws Exception {
		// Si todavia no se buscaron los filtros para la tabla dada, hacerlo
		if (filters.get(tableName) == null) {
			// Lista con las instancias de filtros
			Vector<ReplicationFilter> filterList = new Vector<ReplicationFilter>();

			// Recuperar la configuración para la tabla (las clases están separadas por ;)
			String classes = DB.getSQLValueString(trxName,  " SELECT filters FROM AD_TableReplication WHERE AD_Table_ID = ? ", 
															ReplicationCache.tablesIDs.get(tableName));
			if (classes !=null) {
				String[] classNames = classes.split(";");
				// Iterar por las clases, e instanciar los filtros (o recuperar de la cache si corresponde)
				for (int i=0; i<classNames.length; i++) {
					// Verificar si la clase definita esta ok
					String aFilterName = classNames[i].trim();
					if (aFilterName.length() == 0)
						continue;
						
					// ver si el filtro ya esta instanciado en la cache, de no estarlo agregarlo 
					if (filterInstances.get(aFilterName) == null) {
						// Crear una instancia y reutilizarla en el tiempo para los posteriores filtrads
						Class<?> clazz	= Class.forName(aFilterName);
						Class<?>[] paramTypes = {};
						Object[] args = {};			
						Constructor<?> cons = clazz.getConstructor(paramTypes);
						Object aFilterInstance = cons.newInstance(args);
						filterInstances.put(aFilterName, (ReplicationFilter)aFilterInstance);
					}
					filterList.add(filterInstances.get(aFilterName));
				}
			}
			
			// Incorporar los filtros (o vector vacio en caso contrario)
			filters.put(tableName, filterList); 
		}
		return filters.get(tableName);
	}
	
	/**
	 * Aplica el conjunto de filtros para un reparray y tabla dados
	 * @param trxName transaccion actual
	 * @param tableID AD_Table_ID
	 * @param repArray replication array al cual aplicar los filtros
	 * @return
	 */
	public static void applyFilters(String trxName, ChangeLogGroupReplication group) throws Exception
	{
		for (ReplicationFilter filter : getFiltersForTable(trxName, group.getTableName().toLowerCase()))
			if (filter != null)
				filter.applyFilter(trxName, group);
	}
	
}
