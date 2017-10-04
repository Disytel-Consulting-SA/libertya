package org.openXpertya.util;

import org.openXpertya.model.CalloutInvoiceExt;

public class LibroIVAUtils {

	/**
	 * Devuelve el filtro de tipo de documento dependiendo si está locale_ar
	 * activo o no
	 * 
	 * @param docTypeTableAlias
	 *            alias de la tabla para condiciones de tipo de documento
	 * @param invoiceTableAlias
	 *            alias de la tabla para condiciones de comprobantes
	 * @return las condiciones sql de tipo de documento y/o comprobante dentro
	 *         de la consulta del libro iva
	 */
	public static String getDocTypeFilter(String docTypeTableAlias, String invoiceTableAlias){
		return CalloutInvoiceExt.ComprobantesFiscalesActivos() ? 
						" AND ((" + docTypeTableAlias + ".isfiscaldocument = 'Y') AND (" + docTypeTableAlias
						+ ".isfiscal is null OR " + docTypeTableAlias + ".isfiscal = 'N' OR (" + docTypeTableAlias
						+ ".isfiscal = 'Y' AND " + invoiceTableAlias + ".fiscalalreadyprinted = 'Y'))) ":"";
	}

}
