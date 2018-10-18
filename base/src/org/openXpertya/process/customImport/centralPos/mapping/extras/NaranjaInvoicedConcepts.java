package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.naranja.conceptos.Datum;

/**
 * Naranja - Conceptos facturados a descontar en el mes de pago
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaInvoicedConcepts extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Asociación con Header
			"comercio", // Comercio.
			"nro_liquidacion", 
			"fecha_pago", // Fecha de pago.
			// Comisiones
			"importe_ara_vto", // Importe descontado de arancel facturado en vto.
			"signo_ara_vto", // Signo importe descontado de arancel facturado en vto.
			"importe_ara_facturado_30", // Importe descontado de arancel facturado hace 30 días.
			"signo_ara_facturado_30", // Signo importe descontado de arancel facturado hace 30 días.
			"importe_ara_facturado_60", // Importe descontado de arancel facturado hace 60 días.
			"signo_ara_facturado_60", // Signo importe descontado de arancel facturado hace 60 días.
			"importe_ara_facturado_90", // Importe descontado de arancel facturado hace 90 días.
			"signo_ara_facturado_90", // Signo importe descontado de arancel facturado hace 90 días.
			"importe_ara_facturado_120", // Importe descontado de arancel facturado hace 120 días.
			"signo_ara_facturado_120", // Signo importe descontado de arancel facturado hace 120 días.
			// IVA
			"imp_iva_21_vto", // Importe descontado por I.V.A. inscripto 21% facturado en vto.
			"sig_iva_21_vto", // Signo importe descontado por I.V.A. inscripto 21% facturado en vto.
			"imp_iva_21_facturado_30", // Importe descontado por I.V.A. inscripto 21% facturado hace 30 días.
			"sig_iva_21_facturado_30", // Signo importe descontado por I.V.A. inscripto 21% facturado hace 30 días.
			"imp_iva_21_facturado_60", // Importe descontado por I.V.A. inscripto 21% facturado hace 60 días.
			"sig_iva_21_facturado_60", // Signo importe descontado por I.V.A. inscripto 21% facturado hace 60 días.
			"imp_iva_21_facturado_90", // Importe descontado por I.V.A. inscripto 21% facturado hace 90 días.
			"sig_iva_21_facturado_90", // Signo importe descontado por I.V.A. inscripto 21% facturado hace 90 días.
			"imp_iva_21_facturado_120", // Importe descontado por I.V.A. inscripto 21% facturado hace 120 días.
			"sig_iva_21_facturado_120", // Signo importe descontado por I.V.A. inscripto 21% facturado hace 120 días.
			// Gastos
			"imp_acre_liq_ant_vto", // Importe descontado por acreditación de liquidación anterior facturado en vto.
			"sig_acre_liq_ant_vto", // Signo importe descontado por acreditación de liquidación anterior facturado en vto.
			"imp_acre_liq_ant_facturado_30", // Importe descontado por acreditación de liquidación anterior facturado hace 30 días.
			"sig_acre_liq_ant_facturado_30", // Signo importe descontado por acreditación de liquidación anterior facturado hace 30 días.
			"imp_acre_liq_ant_facturado_60", // Importe descontado por acreditación de liquidación anterior facturado hace 60 días.
			"sig_acre_liq_ant_facturado_60", // Signo importe descontado por acreditación de liquidación anterior facturado hace 60 días.
			"imp_acre_liq_ant_facturado_90", // Importe descontado por acreditación de liquidación anterior facturado hace 90 días.
			"sig_acre_liq_ant_facturado_90", // Signo importe descontado por acreditación de liquidación anterior facturado hace 90 días.
			"imp_acre_liq_ant_facturado_120", // Importe descontado por acreditación de liquidación anterior facturado hace 120 días.
			"sig_acre_liq_ant_facturado_120", // Signo importe descontado por acreditación de liquidación anterior facturado hace 120 días.
			"imp_int_plan_esp_vto", // Importe descontado de interés anticipo plan especial facturado en vto.
			"sig_int_plan_esp_vto", // Signo importe descontado de interés anticipo plan especial facturado en vto.
			"imp_int_plan_esp_facturado_30", // Importe descontado de intereses por anticipo plan especial facturado hace 30 días.
			"sig_int_plan_esp_facturado_30", // Signo importe descontado de intereses por anticipo plan especial facturado hace 30 días.
			"imp_int_plan_esp_facturado_60", // Importe descontado de intereses por anticipo plan especial facturado hace 60 días.
			"sig_int_plan_esp_facturado_60", // Signo importe descontado de intereses por anticipo plan especial facturado hace 60 días.
			"imp_int_plan_esp_facturado_90", // Importe descontado de intereses por anticipo plan especial facturado hace 90 días.
			"sig_int_plan_esp_facturado_90", // Signo importe descontado de intereses por anticipo plan especial facturado hace 90 días.
			"imp_int_plan_esp_facturado_120", // Importe descontado de intereses por anticipo plan especial facturado hace 120 días.
			"sig_int_plan_esp_facturado_120" // Signo importe descontado de intereses por anticipo plan especial facturado hace 120 días.
	};

	public NaranjaInvoicedConcepts(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "comercio", "nro_liquidacion", "fecha_pago" };
	}

}
