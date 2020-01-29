package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.model.X_I_CabalPayments;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.retenciones.Datum;

public class CabalRetention extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"id",
			"fecha_pago",
			"numero_liquidacion",
			"numero_comercio",
			"iva_cf_alicuota_10_5",
			"signo_iva_cf_alicuota_10_5",
			"iva_cf_alicuota_21",
			"signo_iva_cf_alicuota_21"
	};
	
	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public CabalRetention(Datum values) {
		super(filteredFields, values, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}

	/** Constructor. */
	public CabalRetention() {
		super(filteredFields, null, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}

}
