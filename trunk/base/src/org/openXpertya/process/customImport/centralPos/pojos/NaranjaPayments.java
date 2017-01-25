package org.openXpertya.process.customImport.centralPos.pojos;

import java.util.HashSet;

import org.openXpertya.model.X_I_NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaCoupons;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaHeaders;
import org.openXpertya.process.customImport.centralPos.pojos.extras.NaranjaInvoicedConcepts;

/**
 * Narnja - Conceptos facturados, Detalle de cupones y Headers.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaPayments extends Pojo {

	/** Constructor. */
	public NaranjaPayments() {
		super(joinArrays(NaranjaCoupons.filteredFields, NaranjaHeaders.filteredFields, NaranjaInvoicedConcepts.filteredFields), null, X_I_NaranjaPayments.Table_Name);
		matchingFields = new String[] { "comercio", "fecha_pago" };
	}

	/**
	 * Constructor.
	 * @param coupons cupon.
	 */
	public NaranjaPayments(NaranjaCoupons coupons) {
		super(joinArrays(NaranjaCoupons.filteredFields, NaranjaHeaders.filteredFields, NaranjaInvoicedConcepts.filteredFields), null, X_I_NaranjaPayments.Table_Name);
		matchingFields = new String[] { "comercio", "fecha_pago" };
		putAll(coupons.getValues());
	}

	/**
	 * Agrega los datos del cupon.
	 * @param coupon
	 */
	public void setCoupons(NaranjaCoupons coupon) {
		putAll(coupon.getValues());
	}

	/**
	 * Agrega los datos del header.
	 * @param header
	 */
	public void setHeader(NaranjaHeaders header) {
		putAll(header.getValues());
	}

	/**
	 * Agrega los datos de los conceptos facturados.
	 * @param invoicedConcept
	 */
	public void setInvoicedConcept(NaranjaInvoicedConcepts invoicedConcept) {
		putAll(invoicedConcept.getValues());
	}

	/**
	 * A partir de la unión de tres arrays, crea uno nuevo.
	 * @param array1
	 * @param array2
	 * @param array3
	 * @return
	 */
	private static String[] joinArrays(String[] array1, String[] array2, String[] array3) {
		HashSet<String> tmp = new HashSet<String>();
		for (String str : array1) {
			tmp.add(str);
		}
		for (String str : array2) {
			tmp.add(str);
		}
		for (String str : array3) {
			tmp.add(str);
		}
		return (String[]) tmp.toArray(new String[0]);
	}

}
