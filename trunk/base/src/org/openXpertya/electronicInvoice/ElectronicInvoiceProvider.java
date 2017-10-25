package org.openXpertya.electronicInvoice;

import java.lang.reflect.Constructor;

import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPreference;

public class ElectronicInvoiceProvider {

	/** Preferencia sobre proveedor del servicio de FE */
	public static String PREFERENCE_WSFE_PROVIDER = "WSFE_PROVIDER_CLASS"; 
	
	/** Nombre de la clase que provee el servicio de FE (si es que existe) */
	public static String wsfeProviderClass = MPreference.searchCustomPreferenceValue(PREFERENCE_WSFE_PROVIDER, 0, 0, 0, true);
	
	/** Retorna la implementacion (si es que existe) encargada de gestionar la registracion de la FE */ 
	public static ElectronicInvoiceInterface getImplementation(MInvoice inv) {
		try {
			// Si no hay proveedor alguno, retornar null
			if (wsfeProviderClass==null)
				return null;
			// Intentar instanciar y utilizar dicho proveedor
			Class<?> clazz = Class.forName(wsfeProviderClass);
			Constructor<?> constructor= clazz.getDeclaredConstructor(new Class[] { MInvoice.class });
			return (ElectronicInvoiceInterface)constructor.newInstance(new Object[] { inv });
		} catch (Exception e) {
			return null;
		}
	}
}
