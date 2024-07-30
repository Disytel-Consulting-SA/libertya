package org.openXpertya.process.customImport.fidelius.mapping;

import org.openXpertya.model.X_I_FideliusPendientes;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Datum;
import org.openXpertya.util.Env;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.Cupon;
import org.openXpertya.process.customImport.fidelius.pojos.tarjeta.pago.CuponPendiente;

/**
 * Tarjeta - Cupones Pendientes 
 * @author dREHER - 
 * @version 1.0
 */
public class TarjetaCuponPendiente extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			// Principal
			"fechaoper", // fecha de operacion
			"horaoper", // hora de operacion
			"nroterminal", // Terminal
			"equipo", // Nombre del equipo
			"nombre_comerc", // Nombre del comercio
			"tipotrx", // Tipo de transaccion.
			"id_clover", // Identificacion en Clover.
			"codcom", // Codigo de Comercio.
			"nrolote", // Número de lote.
			"ticket", // Ticket nro
			"codaut", // Codigo de autorizacion
			"factura", // Numero de factura
			"tarjeta", // Marca de tarjeta
			"nrotarjeta", // Numero de tarjeta
			"cuota_tipeada", // Cantidad de cuotas ingresada
			"importe", // Importe de venta (cupon)
			"montosec", // Monto Sec
			"fechapagoest", // Fecha de pago Estimada
			"id" // identificador
	};

	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaCuponPendiente(Datum values) {
		super(filteredFields, values, X_I_FideliusPendientes.Table_Name);
	}
	
	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public TarjetaCuponPendiente(CuponPendiente values) {
		super(filteredFields, values, X_I_FideliusPendientes.Table_Name);
	}

	/** Constructor. */
	public TarjetaCuponPendiente() {
		super(filteredFields, null, X_I_FideliusPendientes.Table_Name);
	}

	public void addDefaultField(String string) {
		super.addDefaultField(string);
	}

	public void addDefaultValue(String string) {
		super.addDefaultValue(string);
	}
}
