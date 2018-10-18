package org.openXpertya.process.customImport.centralPos.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_I_FirstDataTrailerAndDetail;
import org.openXpertya.process.customImport.centralPos.mapping.extras.DetailParticipant;
import org.openXpertya.process.customImport.centralPos.mapping.extras.TrailerParticipants;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

/**
 * FirstData - Trailer de liquidación a comercio participante e impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class FirstDataTrailerAndDetail extends GenericMap {

	public FirstDataTrailerAndDetail(TrailerParticipants trailerParticipant, DetailParticipant detailParticipant) {
		super(joinArrays(TrailerParticipants.filteredFields, DetailParticipant.filteredFields), null, X_I_FirstDataTrailerAndDetail.Table_Name);
		matchingFields = new String[] { "comercio_participante", "numero_liquidacion", "fecha_vencimiento_clearing" };

		List<GenericDatum> data = new ArrayList<GenericDatum>();
		data.add(trailerParticipant.getValues());
		data.add(detailParticipant.getValues());

		setValuesList(data);
	}

	public FirstDataTrailerAndDetail(TrailerParticipants trailerParticipant) {
		super(joinArrays(TrailerParticipants.filteredFields, DetailParticipant.filteredFields), trailerParticipant.getValues(), X_I_FirstDataTrailerAndDetail.Table_Name);
		matchingFields = new String[] { "comercio_participante", "numero_liquidacion", "fecha_vencimiento_clearing" };
	}

	public FirstDataTrailerAndDetail() {
		super(joinArrays(TrailerParticipants.filteredFields, DetailParticipant.filteredFields), null, X_I_FirstDataTrailerAndDetail.Table_Name);
		matchingFields = new String[] { "comercio_participante", "numero_liquidacion", "fecha_vencimiento_clearing" };
	}

}
