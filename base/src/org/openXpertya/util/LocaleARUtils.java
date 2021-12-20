package org.openXpertya.util;

import org.openXpertya.model.CalloutInvoiceExt;
import org.openXpertya.model.MDocType;

public class LocaleARUtils {

	/** LAR Activo */
	public static boolean LAR_ACTIVATED = CalloutInvoiceExt.ComprobantesFiscalesActivos();
	
	/**
	 * @param docType tipo de documento
	 * @return true si se deben realizar las validaciones de documentos Locale AR
	 *         dependiendo el tipo de documento y otros parámetros, false caso
	 *         contrario
	 */
	public static boolean doDocumentLARValidations(MDocType docType) {
		return LAR_ACTIVATED 
				&& docType.isFiscalDocument()
				&& (docType.getdocsubtypecae() == null
					|| (!docType.getdocsubtypecae().equals(MDocType.DOCSUBTYPECAE_OtrosCbtesNoCumplenRGN1415)
							&& !docType.getdocsubtypecae().equals(MDocType.DOCSUBTYPECAE_NotaDeCreditoNoCumplenRGN1415)));
	}

}
