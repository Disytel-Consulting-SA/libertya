package org.openXpertya.process.customImport.centralPos.pojos;

import org.openXpertya.model.X_I_TrailerParticipants;

import com.google.gson.internal.LinkedTreeMap;

/**
 * FirstData - Trailer de liquidación a comercio participante e impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class TrailerParticipant extends Pojo {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"fecha_vencimiento_clearing", // Fecha de la liquidación.
			"comercio_participante", // Se utiliza para colocar la sucursal y la entidad financiera.
			"numero_liquidacion", // Número de liquidación.
			"total_importe_total", // Importe Bruto.
			"total_importe_total_signo",
			"neto_comercios", // Importe Acreditado.
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

	/**
	 * Constructor.
	 * @param values Conjunto de datos recuperados desde la API.
	 */
	public TrailerParticipant(LinkedTreeMap<String, Object> values) {
		super(filteredFields, values, X_I_TrailerParticipants.Table_Name);
	}

	/** Constructor. */
	public TrailerParticipant() {
		super(filteredFields, null, X_I_TrailerParticipants.Table_Name);
	}
	
}
