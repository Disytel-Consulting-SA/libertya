package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.cupones.Datum;

/**
 * Naranja - Detalle de cupones con vencimiento en el mes de pago
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaCoupons extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Asociación con Detalle
			"comercio", // Comercio.
			"nro_liquidacion", 
			"fecha_pago", // Fecha de pago.
			// Importe total
			"compra", // Importe total de la compra.
			"tipo_mov" // Tipo de movimiento (Segun string se determina el signo de "compra").
	};

	public NaranjaCoupons(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "comercio", "nro_liquidacion", "fecha_pago" };
	}

}
