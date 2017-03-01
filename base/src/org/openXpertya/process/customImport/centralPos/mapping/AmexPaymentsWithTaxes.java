package org.openXpertya.process.customImport.centralPos.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_I_AmexPaymentsAndTaxes;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.AmexTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

/**
 * Amex - Pegos e Impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexPaymentsWithTaxes extends GenericMap {

	/**
	 * Constructor.
	 * @param payment pago.
	 * @param tax impuesto.
	 */
	public AmexPaymentsWithTaxes(AmexPayments payment, AmexTaxes tax) {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), null, X_I_AmexPaymentsAndTaxes.Table_Name);

		matchingFields = new String[] { "num_sec_pago" };

		List<GenericDatum> data = new ArrayList<GenericDatum>();
		data.add(payment.getValues());
		data.add(tax.getValues());

		setValuesList(data);
	}

	/**
	 * Constructor.
	 * @param payment pago.
	 */
	public AmexPaymentsWithTaxes(AmexPayments payment) {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), payment.getValues(), X_I_AmexPaymentsAndTaxes.Table_Name);
		matchingFields = new String[] { "num_sec_pago" };
	}

	/** Constructor. */
	public AmexPaymentsWithTaxes() {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), null, X_I_AmexPaymentsAndTaxes.Table_Name);
		matchingFields = new String[] { "num_sec_pago" };
	}

}
