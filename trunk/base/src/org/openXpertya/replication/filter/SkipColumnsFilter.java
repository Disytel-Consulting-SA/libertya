package org.openXpertya.replication.filter;

/** 
 * Filtro encargado de omitir ciertas columnas a replicar
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import org.openXpertya.model.MPreference;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.util.Util;

public class SkipColumnsFilter extends ReplicationFilter {

	/** Preferencia que especifica la ubicación del archivo con las reglas para este filtro */
	public static final String RULES_LOCATION_PREF  = "SkipColumnsFilterRulesPropertiesFileLocation"; 
	/** Prefijo para la definición de columnas a omitir en replicación */
	public static final String RULE_SKIPCOLS_PREFIX = "[SKIPCOLS]";

	/** Para cada tabla (key) -> omitir un conjunto de columnas (value). Ambas colecciones en lowercase */
	static HashMap<String, ArrayList<String>> skipColumns = null;

	
	static {
		try {
			// Carga de reglas de omisión de columnas
			skipColumns = new HashMap<String, ArrayList<String>>();
			// Recuperación del archivo
			Properties prop = Util.loadProperties(MPreference.GetCustomPreferenceValue(RULES_LOCATION_PREF));
			for (Entry<Object, Object> e : prop.entrySet()) {
				// Es una regla de definición de omisión de columnas?
				if (e.getKey().toString().startsWith(RULE_SKIPCOLS_PREFIX)) {
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
					throw new Exception("Regla no reconocida en SkipColumnsFilter: " + e.getKey());
	        }
		}
		catch (Exception e) {
			throw new RuntimeException("Error al inicializar SkipColumnsFilter: " + e.getMessage());
		}
	}
	
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		
		// Omitir columnas a replicar según configuración  
		if (skipColumns.get(group.getTableName().toLowerCase()) != null) {
			for (String columnName : skipColumns.get(group.getTableName().toLowerCase())) {
				removeElement(group, columnName);
			}
		}

	}

}
