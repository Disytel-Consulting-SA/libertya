package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.model.X_I_CabalPayments;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.pagos.Datum;

public class CabalPayments extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"id",
			"fecha_pago",
			"numero_liquidacion",
			"comercio",
			"numero_comercio",
			"moneda_pago",
			"importe_venta", // Importe Bruto
			"signo_importe_bruto",
			"importe_arancel", // Arancel
			"signo_importe_arancel",
			"importe_iva_arancel", // IVA 21
			"signo_iva_sobre_arancel",
			"retencion_iva", // Retención IVA
			"signo_retencion_iva",
			"retencion_ganancias", // Retención Ganancias
			"signo_retencion_ganancias",
			"retencion_ingresos_brutos", // Retención IIBB Capital Federal
			"signo_retencion_ingresos_brutos",
			"percepcion_rg_3337", // Percepción IVA
			"signo_percepcion_3337",
			"importe_neto_final", // Importe Acreditado
			"signo_importe_neto_final"
	};

	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public CabalPayments(Datum values) {
		super(filteredFields, values, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}

	/** Constructor. */
	public CabalPayments() {
		super(filteredFields, null, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}
}
