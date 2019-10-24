package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.model.X_I_CabalPayments;
import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.cabal.movimientos.Datum;

public class CabalMovements extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"id",
			"fecha_pago",
			"numero_liquidacion",
			"numero_comercio",
			"costo_fin_cup"
	};
	
	/**
	 * Constructor.
	 * @param values valores a insertar en la tabla.
	 */
	public CabalMovements(Datum values) {
		super(filteredFields, values, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}

	/** Constructor. */
	public CabalMovements() {
		super(filteredFields, null, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}

}
