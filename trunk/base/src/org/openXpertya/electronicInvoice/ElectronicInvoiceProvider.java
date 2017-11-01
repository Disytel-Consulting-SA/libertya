package org.openXpertya.electronicInvoice;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPreference;
import org.openXpertya.util.Env;

public class ElectronicInvoiceProvider {

	/** Preferencia sobre proveedor del servicio de FE */
	public static String PREFERENCE_WSFE_PROVIDER = "WSFE_PROVIDER_CLASS";
	
	/** Preferencia sobre proveedor del servicio de FE de Exportacion */
	public static String PREFERENCE_WSFEX_PROVIDER = "WSFEX_PROVIDER_CLASS";
	
	/** Nombre de la clase que provee el servicio de FE (si es que existe) */
	public static String wsfeProviderClass = MPreference.searchCustomPreferenceValue(PREFERENCE_WSFE_PROVIDER, 0, 0, 0, true);
	
	/** Nombre de la clase que provee el servicio de FEX (si es que existe) */
	public static String wsfexProviderClass = MPreference.searchCustomPreferenceValue(PREFERENCE_WSFEX_PROVIDER, 0, 0, 0, true);
	
	/** Listado de tipos de documento de exportacion segun definicion de FE de AFIP */
	private static ArrayList<String> exportacionDocTypes;	
	static {
		// Nomina de tipos de documento de exportacin
		exportacionDocTypes = new ArrayList<String>();
		exportacionDocTypes.add(MDocType.DOCSUBTYPECAE_FacturaDeExportaciónE);
		exportacionDocTypes.add(MDocType.DOCSUBTYPECAE_NotaDeDébitoPorOperacionesEnElExterior);
		exportacionDocTypes.add(MDocType.DOCSUBTYPECAE_NotaDeCréditoPorOperacionesEnElExterior);;
	}
	
	/** Retorna la implementacion (si es que existe) encargada de gestionar la registracion de la FE */ 
	public static ElectronicInvoiceInterface getImplementation(MInvoice inv) {
		try {
			// Recuperar el docType de la factura para determinar si es de exportacion o no
			MDocType docType = new MDocType(Env.getCtx(), inv.getC_DocTypeTarget_ID(), inv.get_TrxName());	
			if (exportacionDocTypes.contains(docType.getdocsubtypecae())){
				return getProvider(inv, wsfexProviderClass);
			}
			return getProvider(inv, wsfeProviderClass);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/** Retorna el provider si es que existe, ya sea de exportacion o no */
	protected static ElectronicInvoiceInterface getProvider(MInvoice inv, String providerClass) throws Exception {
		// Si no hay proveedor alguno, retornar null
		if (providerClass==null)
			return null;
		// Intentar instanciar y utilizar dicho proveedor
		Class<?> clazz = Class.forName(providerClass);
		Constructor<?> constructor= clazz.getDeclaredConstructor(new Class[] { MInvoice.class });
		return (ElectronicInvoiceInterface)constructor.newInstance(new Object[] { inv });
	}


}
