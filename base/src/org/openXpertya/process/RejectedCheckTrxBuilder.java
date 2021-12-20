package org.openXpertya.process;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MPreference;
import org.openXpertya.util.Util;

public abstract class RejectedCheckTrxBuilder {

	/** Locale AR activo? */
	protected final boolean LOCALE_AR_ACTIVE = CalloutInvoiceExt.ComprobantesFiscalesActivos();
	
	/**
	 * Nombre de la Preference con la clave (doctypekey) del tipo de documento
	 * del débito de cheque rechazado
	 */
	protected final static String REJECTED_CHECK_DOCTYPEKEY_PREFIX_PREFERENCE_NAME = "RejectedCheckDocTypeKey";
	
	/**
	 * Obtener el objeto encargado de realizar las operaciones sobre las
	 * transacciones dependiendo si es cheque de cliente o de proveedor
	 * 
	 * @param check cheque rechazado
	 * @return objeto composite para determinar cuestiones por tipo de transacción
	 */
	public static RejectedCheckTrxBuilder get(MPayment check) {
		RejectedCheckTrxBuilder recdt = null;
		if(check.isReceipt()) {
			recdt = new RejectedCheckSalesTrx();
		}
		else {
			recdt = new RejectedCheckPurchaseTrx();	
		}
		return recdt;
	}

	/**
	 * Setear el tipo de documento correcto al débito parámetro
	 * 
	 * @param invoice débito generado por cheque rechazado
	 */
	public void setDocType(MInvoice invoice) throws Exception {
		MDocType documentType = null;
		// Si existe la preference, buscamos ese doctype
		String rejectedCheckDoctypekey = MPreference.searchCustomPreferenceValue(
				getDocTypePreferenceName(), invoice.getAD_Client_ID(), invoice.getAD_Org_ID(), null,
				true);
		if(!Util.isEmpty(rejectedCheckDoctypekey, true)){
			documentType = MDocType.getDocType(invoice.getCtx(), rejectedCheckDoctypekey, invoice.get_TrxName());
		}
		if(documentType == null) {
			documentType = getDocType(invoice);
		}
		invoice.setC_DocType_ID(documentType.getID());
		invoice.setC_DocTypeTarget_ID(documentType.getID());
	}
	
	/**
	 * Seteo datos necesario en el débito y devuelve el tipo de documento a setear
	 * 
	 * @return tipo de documento dependiendo la trx
	 */
	public abstract MDocType getDocType(MInvoice invoice) throws Exception;
	
	/**
	 * @return nombre de la preference con el tipo de doc a crear
	 */
	public abstract String getDocTypePreferenceName();
}
