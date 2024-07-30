package org.openXpertya.process.customImport.fidelius.mapping;

import org.openXpertya.model.X_I_FideliusCupones;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Datum;
import org.openXpertya.util.Env;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Cupon;

/**
 * Tarjeta - Cupones 
 * @author dREHER - 
 * @version 1.0
 */
public class TarjetaCupon extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Principal
			"empresa",
			"bancopag", // banco que paga
			"fpag", // Fecha de pago.
			"fvta", // Fecha de venta.
			"fant", // Fecha de anticipo.
			"nroliq", // Número de la liquidación.
			"nroequipo", // Número de equipo.
			"nomequipo", // Nombre de equipo.
			"nrolote", // Número de lote.
			"nrocupon", // Número de cupon.
			"tarjeta", // Tarjeta (5) + Nro. Comercio (7) = Entidad Comercial (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Liquidación de Tarjetas)
			"ult4tarjeta", // ultimos 4 numeros
			"autorizacion", // codigo de autorizacion
			"cuotas", // cantidad de cuotas
			"imp_vta", // importe de venta
			"extra_cash", // extra cash
			"num_com", // Número de comercio.Tarjeta (5) + Nro. Comercio (7) = Entidad Comercial (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Liquidación de Tarjetas)
			"rechazo", // codigo de rechazo
			"arancel", // Arancel.
			"iva_arancel", // IVA Arancel.
			"cfo", // cfo total. Costo Financiero (Ventana: Liquidación de Tarjeta de Crédito. Pestaña: Otros conceptos)
			"iva_cfo", // IVA CFO
			"alic_ivacfo", // Alictuota de IVA CFO
			"tipo_oper", // Tipo de Operacion.
			"id_unico", // Identificacion
			"revisado"
	};

	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaCupon(Datum values) {
		super(filteredFields, values, X_I_FideliusCupones.Table_Name);
	}
	
	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaCupon(Cupon values) {
		super(filteredFields, values, X_I_FideliusCupones.Table_Name);
	}

	/** Constructor. */
	public TarjetaCupon() {
		super(filteredFields, null, X_I_FideliusCupones.Table_Name);
	}

	public void addDefaultField(String string) {
		super.addDefaultField(string);
	}

	public void addDefaultValue(String string) {
		super.addDefaultValue(string);
	}
}
