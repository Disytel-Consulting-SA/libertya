package org.openXpertya.process.customImport.centralPos.pojos;

import org.openXpertya.model.X_I_VisaPayments;

import com.google.gson.internal.LinkedTreeMap;

/**
 * Visa - Pagos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class VisaPayments extends Pojo {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Principal
			"fpag", // Fecha de pago.
			"nroliq", // Número de la liquidación.
			"num_est", // Número de establecimiento.
			"impbruto", // Importe bruto.
			"signo_1", // Signo importe bruto.
			"impneto", // Importe neto.
			"signo_3", // Signo importe neto.
			// Retenciones
			"ret_ingbru", // Retención ingresos brutos.
			"signo_32", // Signo retención ingresos brutos.
			"ret_iva", // Retención I.V.A.
			"signo_30", // Signo retención I.V.A.
			"ret_gcias", // Retención ganancias.
			"signo_31", // Signo retención ganancias.
			// Percepciones
			"retiva_esp", // Percepción I.V.A. RG 3337.
			"signo_5", // Signo percepción I.V.A. RG 3337.
			// IVA
			"retiva_cuo1", // I.V.A. costo plan acelerado cuotas.
			"signo_13", // Signo I.V.A. costo plan acelerado cuotas.
			"retiva_d1", // I.V.A. DEC. 879/92.
			"signo_7", // Signo I.V.A. DEC. 879/92.
			"iva1_ad_plancuo", // I.V.A. del cargo adicional por planes cuotas.
			"signo_04_16", // Signo I.V.A. del cargo adicional por planes cuotas.
			"iva1_ad_opinter", // I.V.A. Cargo adicional por operaciones internacionales.
			"signo_04_18", // Signo I.V.A. Cargo adicional por operaciones internacionales.
			// Comisiones
			"impret", // Importe arancel.
			"signo_2", // Signo importe arancel.
			// Otros conceptos
			"dto_campania", // Descuentos por ventas de campañas.
			"signo_04_3", // Signo descuentos por ventas de campañas.
			"costo_cuoemi", // Costo plan acelerado cuotas.
			"signo_12", // Signo costo plan acelerado cuotas.
			"adic_plancuo", // Cargo adicional por planes cuotas.
			"signo_04_15", // Signo cargo adicional por planes cuotas.
			"adic_opinter", // Cargo adicional por operaciones internacionales.
			"signo_04_17" // Signo cargo adicional por operaciones internacionales.
	};

	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public VisaPayments(LinkedTreeMap<String, Object> values) {
		super(filteredFields, values, X_I_VisaPayments.Table_Name);
	}

	/** Constructor. */
	public VisaPayments() {
		super(filteredFields, null, X_I_VisaPayments.Table_Name);
	}

}
