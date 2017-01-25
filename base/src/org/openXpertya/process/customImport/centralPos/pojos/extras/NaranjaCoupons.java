package org.openXpertya.process.customImport.centralPos.pojos.extras;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Naranja - Detalle de cupones con vencimiento en el mes de pago
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaCoupons extends Pojo {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Asociación con Detalle
			"comercio", // Comercio.
			"fecha_pago", // Fecha de pago.
			// Importe total
			"compra", // Importe total de la compra.
			"tipo_mov" // Tipo de movimiento (Segun string se determina el signo de "compra").
	};

	public NaranjaCoupons(LinkedTreeMap<String, Object> values) {
		super(filteredFields, values, null);
	}

}
