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
import java.util.ArrayList;
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
		// Si no hay filtros, continuar
		if (getFiltersForTable(trxName, group.getTableName().toLowerCase()).size() == 0)
			return;
				
		// repArray original
		String originalRepArray = group.getRepArray();
		
		// Listado de repArrays resultantes de aplicar cada filtro sobre el repArray original
		ArrayList<String> filteredRepArrays = new ArrayList<String>();
		
		// Iterar por los filtros e ir aplicando cada uno de ellos sobre el repArray original
		for (ReplicationFilter filter : getFiltersForTable(trxName, group.getTableName().toLowerCase())) {
			// Cada filtro debe recibir el repArray original, y filtrar según los criterios específicos de cada uno
			if (filter != null) {
				// Pasarle el repArray original
				group.setRepArray(originalRepArray);
				
				// Aplicar el filtro
				filter.applyFilter(trxName, group);
				
				// Acumular en la nomina de filtros resultantes
				filteredRepArrays.add(group.getRepArray());
			}	
		}
		
		// Ningún repArray resultante? (por ejemplo si filter==null dentro de la iteración de filtros)
		if (filteredRepArrays.size() == 0) {
			group.setRepArray(originalRepArray);
			return;
		}
		// Si hay un solo repArray resultante.. pasarle éste
		else if (filteredRepArrays.size() == 1) {
			group.setRepArray(filteredRepArrays.get(0));
			return;
		}

		// Caso en el que hay más de un repArray resultante, aplicar "OR lógico"
		String finalRepArray = filteredRepArrays.get(0);
		for (int i=1; i<filteredRepArrays.size(); i++)
			finalRepArray = repArrayOR(finalRepArray, filteredRepArrays.get(i));
		group.setRepArray(finalRepArray);		
	}
	
	/**
	 * Aplica una especie de "OR lógico" entre repArrayA y repArrayB, para cada posición de éstos.  Esto es: si para una posición dada
	 * alguno de los repArrays tiene un valor distinto de REPLICATION_CONFIGURATION_NO_ACTION, entonces deberá ser parte del repArray resultante.
	 * Sin embargo, si en ambas posiciones se presenta REPLICATION_CONFIGURATION_NO_ACTION, entonces deberá quedar este valor en el repArray resultante.
	 * Ejemplo:
	 * 		repArrayA = 0013, repArrayB = 0110  => retorna 0113
	 * @param repArrayA operando 1
	 * @param repArrayB operando 2
	 * @return el resultado de aplicar la lógica correspondiente
	 * @throws Exception en caso de que alguno de los repArrays sean nulos, tengan longitud cero, o distinta longitud
	 */
	protected static String repArrayOR(String repArrayA, String repArrayB) throws Exception {
		// Validaciones
		if (repArrayA == null || repArrayB == null)
			throw new Exception("repArrayOR: Reparray resultante de aplicar filtro es null");
		if (repArrayA.length() == 0 || repArrayB.length() == 0)
			throw new Exception("repArrayOR: Reparray resultante de aplicar filtro tiene longitud cero");
		if (repArrayA.length() != repArrayB.length())
			throw new Exception("repArrayOR: repArrays tienen distinta longitud");
		
		// Recorrer y apliar el "OR lógico" por posición
		StringBuilder sb = new StringBuilder(repArrayA);
		for (int i=0; i<sb.length(); i++) {
			if (repArrayA.charAt(i) != ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION)
				sb.setCharAt(i, repArrayA.charAt(i));
			else if (repArrayB.charAt(i) != ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION)
				sb.setCharAt(i, repArrayB.charAt(i));
			else
				sb.setCharAt(i, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		}
		return sb.toString();
	}
	
}
