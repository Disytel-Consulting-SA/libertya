package org.openXpertya.process.customImport.centralPos.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_I_CabalPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.CabalMovements;
import org.openXpertya.process.customImport.centralPos.mapping.extras.CabalPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.CabalRetention;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

public class CabalPaymentAndMovements extends GenericMap {

	public CabalPaymentAndMovements() {
		super(joinArrays(CabalPayments.filteredFields, CabalMovements.filteredFields, CabalRetention.filteredFields),
				null, X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
	}
	
	public CabalPaymentAndMovements(CabalPayments cb) {
		super(joinArrays(CabalPayments.filteredFields, CabalMovements.filteredFields, CabalRetention.filteredFields),
				cb.getValues(), X_I_CabalPayments.Table_Name);
		matchingFields = new String[] { "numero_liquidacion", "fecha_pago", "numero_comercio" };
		List<GenericDatum> data = new ArrayList<GenericDatum>();
		data.add(cb.getValues());
		setValuesList(data);
	}
	
	/**
	 * Agrega los datos del movimiento
	 * 
	 * @param movement suma de todos los movimientos de este pago
	 */
	public void setMovement(CabalMovements movement) {
		if (getValuesList() == null) {
			List<GenericDatum> data = new ArrayList<GenericDatum>();
			data.add(movement.getValues());
			setValuesList(data);
		} else {
			getValuesList().add(movement.getValues());
		}
	}
	
	/**
	 * Agrega los datos de la retención
	 * 
	 * @param retention suma de todos los movimientos de la retención
	 */
	public void setRetention(CabalRetention retention) {
		if (getValuesList() == null) {
			List<GenericDatum> data = new ArrayList<GenericDatum>();
			data.add(retention.getValues());
			setValuesList(data);
		} else {
			getValuesList().add(retention.getValues());
		}
	}
}
