package org.openXpertya.process.customImport.centralPos.mapping;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_I_NaranjaPayments;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaCoupons;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaHeaders;
import org.openXpertya.process.customImport.centralPos.mapping.extras.NaranjaInvoicedConcepts;
import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

/**
 * Narnja - Conceptos facturados, Detalle de cupones y Headers.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaPayments extends GenericMap {

	private List<GenericDatum> data;

	/** Constructor. */
	public NaranjaPayments() {
		super(joinArrays(NaranjaCoupons.filteredFields, NaranjaHeaders.filteredFields, NaranjaInvoicedConcepts.filteredFields), null, X_I_NaranjaPayments.Table_Name);
		matchingFields = new String[] { "comercio", "nro_liquidacion", "fecha_pago" };
	}

	/**
	 * Constructor.
	 * @param coupons cupon.
	 */
	public NaranjaPayments(NaranjaCoupons coupons) {
		super(joinArrays(NaranjaCoupons.filteredFields, NaranjaHeaders.filteredFields, NaranjaInvoicedConcepts.filteredFields), null, X_I_NaranjaPayments.Table_Name);
		matchingFields = new String[] { "comercio", "nro_liquidacion", "fecha_pago" };

		data = new ArrayList<GenericDatum>();
		data.add(coupons.getValues());
		setValuesList(data);
	}

	/**
	 * Agrega los datos del cupon.
	 * @param coupon
	 */
	public void setCoupons(NaranjaCoupons coupon) {
		if (getValuesList() == null) {
			data = new ArrayList<GenericDatum>();
			data.add(coupon.getValues());
			setValuesList(data);
		} else {
			getValuesList().add(coupon.getValues());
		}
	}

	/**
	 * Agrega los datos del header.
	 * @param header
	 */
	public void setHeader(NaranjaHeaders header) {
		if (getValuesList() == null) {
			data = new ArrayList<GenericDatum>();
			data.add(header.getValues());
			setValuesList(data);
		} else {
			getValuesList().add(header.getValues());
		}
	}

	/**
	 * Agrega los datos de los conceptos facturados.
	 * @param invoicedConcept
	 */
	public void setInvoicedConcept(NaranjaInvoicedConcepts invoicedConcept) {
		if (getValuesList() == null) {
			data = new ArrayList<GenericDatum>();
			data.add(invoicedConcept.getValues());
			setValuesList(data);
		} else {
			getValuesList().add(invoicedConcept.getValues());
		}
	}

}
