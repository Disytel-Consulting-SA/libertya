package org.openXpertya.process.customImport.centralPos.pojos.extras;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Amex - Impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexTaxes extends Pojo {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"num_sec_pago", // Num secuencial del pago.
			"cod_imp", // Código de impuesto
			"importe_imp" // Importe del impuesto
	};

	public AmexTaxes(LinkedTreeMap<String, Object> values) {
		super(filteredFields, values, null);
	}

}
