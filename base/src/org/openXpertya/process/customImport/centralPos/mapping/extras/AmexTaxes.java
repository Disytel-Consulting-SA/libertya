package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.amex.impuestos.Datum;

/**
 * Amex - Impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexTaxes extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"num_sec_pago", // Num secuencial del pago.
			"fecha_pago",
			"cod_imp", // Código de impuesto.
			"importe_imp", // Importe del impuesto.
			"porc_imp" // Porcentaje de este impuesto.
	};

	public AmexTaxes(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "num_sec_pago", "fecha_pago", "cod_imp" };
	}

}
