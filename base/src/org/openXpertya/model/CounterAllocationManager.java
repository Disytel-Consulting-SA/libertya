package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;

public class CounterAllocationManager {

	/** Atributo de la preferencia para determinar si hay que usar lógica de contra allocations */
	protected static String PREFERENCE_COUNTER_ALLOCACTIONS_ACTIVE = "COUNTER_ALLOCACTIONS_ACTIVE";
	
	/** Flag de logica activa/no activa */
	protected static Boolean isCounterAllocationActive = null;
	
	/** Recupera configuracion de contra allocations y devuelve true o false según como se encuentre configurado */
	public static boolean isCounterAllocationActive(Properties ctx) {
		if (isCounterAllocationActive == null)
			isCounterAllocationActive = "Y".equalsIgnoreCase(MPreference.GetCustomPreferenceValue(PREFERENCE_COUNTER_ALLOCACTIONS_ACTIVE, Env.getAD_Client_ID(ctx)));
		return isCounterAllocationActive;
	}
	
	
	
	
	
}
