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
				" AND ((" + docTypeTableAlias+ ".isfiscaldocument = 'Y') "
				
						+ " AND (" + docTypeTableAlias + ".isfiscal is null "
								+ " OR " + docTypeTableAlias+ ".isfiscal = 'N' "
								+ " OR (" + docTypeTableAlias + ".isfiscal = 'Y' AND " + invoiceTableAlias+ ".fiscalalreadyprinted = 'Y')) " 

						+ " AND (" + docTypeTableAlias + ".iselectronic is null	"
								+ " OR "+ docTypeTableAlias + ".iselectronic = 'N' "
								+ " OR (" + docTypeTableAlias + ".iselectronic = 'Y' AND " + invoiceTableAlias + ".cae is not null)))" 
				: "";
	}

	
	/**
	 * <ul>
	 * Devuelve el filtro de estado de documentos:
	 * <li>Ventas: Todos los estados. </li>
	 * <li>Compras: Completos o Cerrados. LOCALE AR: Anulados o Revertidos si se
	 * encuentran impresos fiscalmente para tipos de documento que requieren
	 * impresión fiscal o si poseen CAE en caso que el tipo de documento sea
	 * electrónico.</li>
	 * </ul>
	 * <br>
	 * <ul>
	 * Combinaciones L_AR:
	 * <li>Si el tipo de doc requiere impresión fiscal y se encuentra impreso
	 * fiscalmente, entonces se debe mostrar, sin importar el estado</li>
	 * <li>Si el tipo de doc es electrónico y posee cae, entonces se debe
	 * mostrar, sin importar el estado</li>
	 * </ul>
	 * 
	 * @param transactionType
	 *            tipo de transacción: Ventas (C), Compras (V) o Ambos (B)
	 * @param docTypeTableAlias
	 *            alias de la tabla para condiciones de tipo de documento
	 * @param invoiceTableAlias
	 *            alias de la tabla para condiciones de comprobantes
	 * @return las condiciones sql de estado de documento
	 */
	public static String getDocStatusFilter(String transactionType, String docTypeTableAlias, String invoiceTableAlias){
		String docStatusClause = " (" + invoiceTableAlias + ".docstatus = 'CO'::bpchar OR " + invoiceTableAlias
				+ ".docstatus = 'CL'::bpchar OR " + invoiceTableAlias + ".docstatus = 'RE'::bpchar OR "
				+ invoiceTableAlias + ".docstatus = 'VO'::bpchar OR " + invoiceTableAlias
				+ ".docstatus = '??'::bpchar) ";
		// Si no es ambos
		if (!transactionType.equals("B")) {
			// Si no es transacción de ventas, C = Customer(Cliente)
			if (!transactionType.equals("C")) {
				docStatusClause = " (CASE WHEN " + invoiceTableAlias + ".issotrx = 'N' "
										+ " THEN (" + invoiceTableAlias + ".docstatus = 'CO'::bpchar OR " + invoiceTableAlias + ".docstatus = 'CL'::bpchar OR " + invoiceTableAlias + ".docstatus = '??'::bpchar OR " + invoiceTableAlias + ".docstatus = 'RE'::bpchar) "
										+ " ELSE (" + invoiceTableAlias + ".docstatus = 'CO'::bpchar OR " + invoiceTableAlias + ".docstatus = 'CL'::bpchar OR " + invoiceTableAlias + ".docstatus = '??'::bpchar) "
										+ " END) ";
				if(CalloutInvoiceExt.ComprobantesFiscalesActivos()){
					String docStatusClauseFiscalVoid = " OR (" + docTypeTableAlias + ".isfiscal = 'Y' AND " + invoiceTableAlias+ ".fiscalalreadyprinted = 'Y' AND " + invoiceTableAlias+ ".docstatus IN ('RE', 'VO') ) ";
					String docStatusClauseElectronicVoid = " OR (" + docTypeTableAlias + ".iselectronic = 'Y' AND " + invoiceTableAlias+ ".cae is not null AND " + invoiceTableAlias+ ".docstatus IN ('RE', 'VO') ) ";
					docStatusClause = " ( " + docStatusClause + docStatusClauseFiscalVoid
							+ docStatusClauseElectronicVoid + " ) ";
				}
			}
		}
		// Para transacciones de compras se omiten los anulados
		String purchaseOnlyRevertWhereClause = " ( CASE WHEN " + invoiceTableAlias + ".issotrx = 'N' THEN "
				+ invoiceTableAlias + ".docstatus <> 'VO' ELSE 1=1 END ) ";
		
		docStatusClause = " AND "+docStatusClause;
		docStatusClause += " AND "+purchaseOnlyRevertWhereClause; 
		return docStatusClause;
	}
}
