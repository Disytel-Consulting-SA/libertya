package org.openXpertya.process;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;

public class RejectedCheckPurchaseTrx extends RejectedCheckTrxBuilder {

	public RejectedCheckPurchaseTrx() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getDocTypePreferenceName() {
		return REJECTED_CHECK_DOCTYPEKEY_PREFIX_PREFERENCE_NAME + "_Purchase";
	}

	@Override
	public MDocType getDocType(MInvoice invoice) throws Exception {
		MDocType documentType = null;
		// Si está activo Locale_Ar verificar si podemos obtener el tipo de documento
		// Nota de Débito de proveedor
		if(LOCALE_AR_ACTIVE) {
			documentType = MDocType.getDocType(invoice.getCtx(), MDocType.DOCTYPE_VendorDebitNote,
					invoice.get_TrxName());
		}
		// Por defecto se obtiene el tipo de documento factura de proveedor
		if(documentType == null) {
			documentType = MDocType.getDocType(invoice.getCtx(), MDocType.DOCTYPE_VendorInvoice, invoice.get_TrxName());
		}
		return documentType;
	}
}
