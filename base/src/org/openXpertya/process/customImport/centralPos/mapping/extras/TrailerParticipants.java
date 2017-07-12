package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.trailer.Datum;

/**
 * FirstData - Trailer de liquidación a comercio participante e impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class TrailerParticipants extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"fecha_vencimiento_clearing",  // Fecha de la liquidación.
			"producto",  //Se utiliza para distinguir entre las distintas tarjetas que trae FirstData
			"comercio_participante",  // Se utiliza para colocar la sucursal y la entidad financiera.
			"numero_liquidacion",  // Número de liquidación.
			"total_importe_total",  // Importe Bruto.
			"total_importe_total_signo",
			"neto_comercios",  // Importe Acreditado.
			"neto_comercios_signo",
			"iva_aranceles_ri", // Se suma al campo Importe IVA.
			"iva_aranceles_ri_signo",
			"iva_dto_pago_anticipado", // Se suma al campo Importe IVA.
			"iva_dto_pago_anticipado_signo",
			"ret_iva_ventas", // Crea un Registro en la pestaña de Retenciones y se suma al total de retenciones.
			"ret_iva_ventas_signo",
			"percepc_iva_r3337", // Importe Percepciones.
			"percepc_iva_r3337_signo",
			"ret_imp_ganancias", // Crea un Registro en la pestaña de Retenciones y se suma al total de retenciones.
			"ret_imp_ganancias_signo",
			"ret_imp_ingresos_brutos", // Crea un Registro en la pestaña de Retenciones según provincia, y se suma al total de retenciones.
			"ret_imp_ingresos_brutos_signo",
			"arancel", // Arancel/Comisión.
			"arancel_signo",
			"costo_financiero", // Importe Gastos.
			"costo_financiero_signo"
	};

	public TrailerParticipants(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "comercio_participante", "numero_liquidacion" };
	}

	public String getSettlementNo() {
		return GenericDatum.get("numero_liquidacion", values);
	}

}
