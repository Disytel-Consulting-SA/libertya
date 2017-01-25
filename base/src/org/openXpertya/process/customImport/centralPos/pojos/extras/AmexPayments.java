package org.openXpertya.process.customImport.centralPos.pojos.extras;

import org.openXpertya.process.customImport.centralPos.pojos.Pojo;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Amex - Pagos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexPayments extends Pojo {

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

	public AmexPayments(LinkedTreeMap<String, Object> values) {
		super(filteredFields, values, null);
	}

	public String getNumSecPago() {
		return (String) values.get("num_sec_pago");
	}

}
