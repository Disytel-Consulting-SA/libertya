package org.openXpertya.process.customImport.centralPos.mapping.extras;

import org.openXpertya.process.customImport.centralPos.mapping.GenericMap;
import org.openXpertya.process.customImport.centralPos.pojos.firstdata.detalle.Datum;

/**
 * FirstData - Detalle liquidación comercio participante
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class DetailParticipant extends GenericMap {

	/** Campos a almacenar en la DB. */
	public static String[] filteredFields = {
			"comercio_participante",
			"numero_liquidacion",
			"importe_total",
			"importe_total_signo",
			"codigo_movimiento"
	};

	public DetailParticipant(Datum values) {
		super(filteredFields, values, null);
		matchingFields = new String[] { "comercio_participante", "numero_liquidacion" };
	}

}
