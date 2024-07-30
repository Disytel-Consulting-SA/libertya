package org.openXpertya.process.customImport.fidelius.mapping;

import org.openXpertya.model.X_I_FideliusLiquidaciones;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Datum;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Liquidacion;

/**
 * Tarjeta - Liquidaciones Pagos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class TarjetaPayments extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Principal
			"empresa", // N/A
			"fpag", // Fecha de pago.
			"fpres", // N/A
			"fant", // N/A
			"nroliq", // Número de la liquidación.
			"anticipo", // N/A
			"tarjeta", // Tarjeta (5) + Nro. Comercio (7) = Entidad Comercial (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Liquidación de Tarjetas)
			"bancopag", // banco que paga
			"num_com", // Número de comercio.Tarjeta (5) + Nro. Comercio (7) = Entidad Comercial (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Liquidación de Tarjetas)
			"pciaiibb", // provincia de IIBB
			"impbruto", // Importe bruto.
			"impneto", // Importe neto
			"totdesc", // N/A
			"promo", // N/A
			"arancel", // Arancel.
			"iva_arancel", // N/A
			"cfo_total", // cfo total. Costo Financiero (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Otros conceptos)
			"cfo_21", // N/A
			"cfo_105", // N/A
			"cfo_adel", // cfo adel. Costo plan acelerado cuotas (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Otros conceptos)
			"iva_cfo21", // N/A
			"iva_cfo105", // N/A
			"iva_adel21", // N/A
			"iva_total", // IVA 21 (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: IVA). Suma con otros IVA
			"ret_iibb", // Retención de IIBB XXXX (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Retenciones). La Provincia se toma del ítem 8
			"ret_ibsirtac", // Retención de IIBB XXXX (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Retenciones). La Provincia se toma del ítem 8
			"ret_iva", // Retención Impuesto IVA Sufrida (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Retenciones)
			"ret_gcia", // Impuesto a las Ganancias Sufrida (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Retenciones)
			"perc_iva", // 01 Percepción IVA (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Percepciones)
			"perc_iibb", // 01 Percepción IIBB N/A
			"ret_munic", // N/A
			"liq_anttn", // N/A
			"perc_1135tn", // N/A
			"dto_financ", // N/A
			"iva_dtofinanc", // IVA Descuento N/A
			"deb_cred", // debito/credito N/A
			"saldos", // Saldos N/A
			"otros_costos", // Campo con información variable. Se colocará manualmente luego de analizar en que sección imputarlo
			"iva_otros", // IVA 21 (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: IVA). Suma con otros IVA
			"plan_a1218", // PROMO CUOTAS AHORA 12/18 (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Otros conceptos)
			"iva_plana1218", // IVA 21 (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: IVA). Suma con otros IVA
			"porc_ivaplana1218", // porcentaje iva plan ahora 12/18
			"cuit" // cuit
	};

	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaPayments(Datum values) {
		super(filteredFields, values, X_I_FideliusLiquidaciones.Table_Name);
	}
	
	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaPayments(Liquidacion values) {
		super(filteredFields, values, X_I_FideliusLiquidaciones.Table_Name);
	}

	/** Constructor. */
	public TarjetaPayments() {
		super(filteredFields, null, X_I_FideliusLiquidaciones.Table_Name);
	}
}
