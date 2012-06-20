package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.process.DocActionStatusEvent;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.Trx;

public class FiscalPrintManager {

	/**
	 * Crea una impresión de documento fiscal local o remota. Dependiendo de la
	 * organización configurada de impresión y si esa organización contiene un
	 * host de replicación admitiendo si es local o no.
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura de la cual se determina el controlador fiscal de su
	 *            tipo de documento y se verifica donde se debe imprimir
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return una impresión de documento fiscal o remota dependiendo de las
	 *         condiciones
	 */
	public static FiscalDocumentPrint createFiscalDocumentPrint(Properties ctx,
			MInvoice invoice, String trxName) {
		FiscalDocumentPrint fdp = new FiscalDocumentPrint();
		MDocType docType = new MDocType(ctx, invoice.getC_DocTypeTarget_ID(),
				trxName);
		MControladorFiscal cFiscal = MControladorFiscal.getOfDocType(docType.getID());
		// Si existe una controladora fiscal, determinar su organización
		if(cFiscal != null && cFiscal.isRemote()){
			// Obtengo el host de replicación de la organización
			MReplicationHost host = (MReplicationHost) PO.findFirst(ctx,
					"AD_ReplicationHost", "ad_org_id = ?",
					new Object[] { cFiscal.getAD_Org_ID() }, null, trxName);
			// Si no es este host entonces es remoto
			if(host != null && !host.isThisHost()){
				fdp = new RemoteFiscalDocumentPrint();
			}
		}
		return fdp;
	}

	/**
	 * Imprime una factura mediante un controlador fiscal. La impresión puede
	 * ser local o remota dependiendo de la organización configurada en la
	 * interface {@link FiscalRemotePrintable} la cual {@link MInvoice} la
	 * implementa. A su vez, dispara el evento de estado de documento sobre
	 * ésta, si el parámetro lo indica así.
	 * 
	 * @param ctx
	 *            contexto
	 * @param invoice
	 *            factura
	 * @param fireDocActionStatusChanged
	 *            true si se debe disparar el evento de estado de documento
	 * @param trxName
	 *            nombre de la transacción en curso
	 * @return resultado de la impresión, con el error indicado internamente en
	 *         caso que el resultado sea erróneo
	 */
	public static CallResult printDocument(Properties ctx, MInvoice invoice, boolean fireDocActionStatusChanged, String trxName){
		// Creo la impresión del documento fiscal
		FiscalDocumentPrint fdp = createFiscalDocumentPrint(ctx, invoice, trxName);
		if (trxName != null) {
			fdp.setTrx(Trx.get(trxName, false));
		}
		// Esto vale la pena solamente para impresiones locales
		if(fireDocActionStatusChanged){
			invoice.fireDocActionStatusChanged(new DocActionStatusEvent(
					invoice, DocActionStatusEvent.ST_FISCAL_PRINT_DOCUMENT,
					new Object[] { fdp }));
		}
		// Imprimir el documento y guardar el resultado
		CallResult result = new CallResult();
		result.setError(!fdp.printDocument(invoice));
		result.setMsg(fdp.getErrorMsg());
		return result;
	}
}
