package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;
import org.openXpertya.process.customImport.centralPos.pojos.amex.pagos.Datum;

/**
 * Amex - Pagos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexPayments extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"num_sec_pago", // Num secuencial del pago.
			"fecha_pago", // Fecha de pago.
			"num_est", // Número de establecimiento.
			"imp_neto_ajuste", // Importe neto del valor de los ajustes.
			"imp_bruto_est", // Importe bruto presentado por el establecimiento.
			// Comisiones
			"imp_desc_pago", // Importe descuento para este pago (Se requiere invertir signo).
			// Otros Conceptos
			"imp_tot_desc_acel" // El importe total del descuento por aceleración (Se requiere invertir signo).
	};

	public AmexPayments(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "num_sec_pago", "fecha_pago" };
	}

	public String getNumSecPago() {
		return GenericDatum.get("num_sec_pago", values);
	}

}
