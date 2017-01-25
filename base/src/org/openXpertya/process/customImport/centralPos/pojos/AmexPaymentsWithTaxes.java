package org.openXpertya.process.customImport.centralPos.pojos;

import java.util.HashSet;

import org.openXpertya.model.X_I_AmexPaymentsAndTaxes;
import org.openXpertya.process.customImport.centralPos.pojos.extras.AmexPayments;
import org.openXpertya.process.customImport.centralPos.pojos.extras.AmexTaxes;

/**
 * Amex - Pegos e Impuestos
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexPaymentsWithTaxes extends Pojo {

	/**
	 * Constructor.
	 * @param payment pago.
	 * @param tax impuesto.
	 */
	public AmexPaymentsWithTaxes(AmexPayments payment, AmexTaxes tax) {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), null, X_I_AmexPaymentsAndTaxes.Table_Name);

		matchingFields = new String[] { "num_sec_pago" };

		putAll(payment.getValues());
		putAll(tax.getValues());
	}

	/**
	 * Constructor.
	 * @param payment pago.
	 */
	public AmexPaymentsWithTaxes(AmexPayments payment) {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), payment.getValues(), X_I_AmexPaymentsAndTaxes.Table_Name);
	}

	/** Constructor. */
	public AmexPaymentsWithTaxes() {
		super(joinArrays(AmexPayments.filteredFields, AmexTaxes.filteredFields), null, X_I_AmexPaymentsAndTaxes.Table_Name);
	}

	/**
	 * A partir de la union de dos arrays, crea uno nuevo.
	 * @param array1
	 * @param array2
	 * @return
	 */
	private static String[] joinArrays(String[] array1, String[] array2) {
		HashSet<String> tmp = new HashSet<String>();
		for (String str : array1) {
			tmp.add(str);
		}
		for (String str : array2) {
			tmp.add(str);
		}
		return (String[]) tmp.toArray(new String[0]);
	}

}
