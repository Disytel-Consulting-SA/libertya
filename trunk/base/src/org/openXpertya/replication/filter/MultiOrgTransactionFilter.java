package org.openXpertya.replication.filter;

/**
 * Filtro de replicación genérico para gestión de transacciones entre más de un host (sub-anillo de replicación), 
 * basado en reglas de replicación almacenadas en un archivo properties.  
 * 
 * Dejará un 1 únicamente sii se cumplen las siguientes reglas (ambas):
 * 	- 1) Existe un valor distinto a NO_ACTION en una posición del repArray del group
 *  - 2) Si el host destino es CENTRAL_REPARRAY_POS o si el host destino es uno de los pertenecientes a thisHostSubRingHosts

 * El archivo con reglas almacena 
 * 	1) Reglas de sub-anillos de replicación, o sea hosts que tengan que compartir todos los documentos
 * 		dado que es necesario por ejemplo realizar cobranza y otras actividades en cualquiera de dichos hosts
 * 		Ejemplo de formato: [SUBRINGS]01 = 5, 6, 7
 *  2) Reglas de omisión de columnas, o sea que ciertas columnas deben omitirse en la replicación,
 *  	dado que no todos los datos en un host existen en los otros hosts pertenecientes al sub-anillo
 *  	Ejemplo de formato: [SKIPCOLS]C_Cash = AD_OrgTrx_ID, C_Activity_ID, C_Campaign_ID, C_PosJournal_ID, C_Project_ID, User1_ID, User2_ID  
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.openXpertya.model.MPreference;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.replication.ReplicationConstants;
import org.openXpertya.util.Util;

public class MultiOrgTransactionFilter extends ReplicationFilter {

	/** Preferencia que especifica la ubicación del archivo con las reglas para este filtro */
	public static final String RULES_LOCATION_PREF  = "MultiOrgTransactionFilterRulesPropertiesFileLocation"; 
	/** Prefijo para la definición de sub-anillos de replicación en el archivo de configuración de este filtro */
	public static final String RULE_SUBRINGS_PREFIX = "[SUBRINGS]";
	/** Prefijo para la definición de columnas a omitir en replicación */
	public static final String RULE_SKIPCOLS_PREFIX = "[SKIPCOLS]";
	
	/** Otros hosts pertenecientes a este sub-anillo (si es que existe) */
	static Set<Integer> thisHostSubRingHosts = null;
	/** Para cada tabla (key) -> omitir un conjunto de columnas (value). Ambas colecciones en lowercase */
	static HashMap<String, ArrayList<String>> skipColumns = null;
	
	static {
		try {
			// Carga de reglas de omisión de columnas
			skipColumns = new HashMap<String, ArrayList<String>>();
			// Carga de reglas de sub-anillos de replicación
			thisHostSubRingHosts = new HashSet<Integer>();
			// Recuperación del archivo
			Properties prop = Util.loadProperties(MPreference.GetCustomPreferenceValue(RULES_LOCATION_PREF));
			for (Entry<Object, Object> e : prop.entrySet()) {
				// Es una regla de definición de sub-anillos? 
				if (e.getKey().toString().startsWith(RULE_SUBRINGS_PREFIX)) {
					// Recorrer la nómina de hosts para un subanillo y agregar todos los hosts del mismo
					String[] hosts = e.getValue().toString().toLowerCase().split(",");
					for (String host : hosts)
						thisHostSubRingHosts.add(Integer.parseInt(host.trim()));		
					// Si esta regla de sub-anillo no contiene este host, entonces limpiar la colección
					if (!thisHostSubRingHosts.contains(thisHostPos))
						thisHostSubRingHosts.clear();
				}
				// Es una regla de definición de omisión de columnas?
				else if (e.getKey().toString().startsWith(RULE_SKIPCOLS_PREFIX)) {
					// Recorrer la nómina de columnas para esta tabla y...
					ArrayList<String> columnsToSkipForThisTable = new ArrayList<String>();
					String[] columnsToSkip = e.getValue().toString().toLowerCase().split(",");
					for (String columnToSkip : columnsToSkip) 
						columnsToSkipForThisTable.add(columnToSkip.trim());
					// ... agregarlas a la map
					skipColumns.put(e.getKey().toString().substring(RULE_SKIPCOLS_PREFIX.length()).trim().toLowerCase(), 
									columnsToSkipForThisTable);
				}
				// Cualquier otra cosa es un error
				else 
					throw new Exception("Regla no reconocida en MultiOrgTransactionFilter: " + e.getKey());
	        }
		}
		catch (Exception e) {
			throw new RuntimeException("Error al inicializar MultiOrgTransactionFilter: " + e.getMessage());
		}
	}
	
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		
		// Omitir columnas a replicar según configuración  
		if (skipColumns.get(group.getTableName().toLowerCase()) != null) {
			for (String columnName : skipColumns.get(group.getTableName().toLowerCase())) {
				// removeElement(group, columnName);  <-- Cambiado. En lugar de remover la columna, indicar destino unicamente central
				addTargetOnly(group, columnName, CENTRAL_REPARRAY_POS);
			}
		}
		
		// Dejar unicamente los hosts pertenecientes al sub-anillo (y central)
		for (int i=0; i<group.getRepArray().length(); i++) {
			if (i == CENTRAL_REPARRAY_POS-1 || thisHostSubRingHosts.contains((i+1)) ) 
				repArraySetValueAtPosition(group, i, group.getRepArray().charAt(i));
			else
				repArraySetValueAtPosition(group, i, ReplicationConstants.REPLICATION_CONFIGURATION_NO_ACTION);
		}
	}


}
