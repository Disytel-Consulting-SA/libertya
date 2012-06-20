package org.openXpertya.model;

import java.util.List;
import java.util.Properties;

import org.openXpertya.interfaces.FiscalPrint;
import org.openXpertya.print.fiscal.FiscalPrinterEventListener;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class RemoteFiscalDocumentPrint extends FiscalDocumentPrint {

	public RemoteFiscalDocumentPrint() {
		// TODO Auto-generated constructor stub
	}

	public RemoteFiscalDocumentPrint(
			FiscalPrinterEventListener printerEventListener) {
		super(printerEventListener);
		// TODO Auto-generated constructor stub
	}

	public RemoteFiscalDocumentPrint(
			FiscalPrinterEventListener printerEventListener,
			FiscalDocumentPrintListener documentPrintListener) {
		super(printerEventListener, documentPrintListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean printDocument(PO document) {
		// Realizo la impresión remotamente del PO
		// Se valida que el documento tenga asignado el tipo de documento.
		Integer docType_ID = (Integer)document.get_Value("C_DocTypeTarget_ID");
		if(docType_ID == null || docType_ID == 0)
			throw new IllegalArgumentException("Error: the document has no type");
		
		// Obtengo el nombre y valor de la columna de clave única para el tipo
		// de documento del documento parámetro
		MDocType docType = new MDocType(ctx, docType_ID, null);
		setPrinterDocType(docType.getFiscalDocument());
		
		String docTypecolumnNameUID = getUIDColumnName(docType);
		Object docTypecolumnValueUID = getUIDColumnValue(docType);
		
		// Obtengo la impresora fiscal que es la que contiene la organización
		// donde conectarme
		MControladorFiscal cFiscal = MControladorFiscal.getOfDocType(docType_ID);
		if(cFiscal == null)
			throw new IllegalArgumentException("Error: the document is not fiscal");
		
		// Se asigna el documento OXP.
		setOxpDocument(document);
		
		// Obtengo la factura original si es que existe		
		MInvoice originalInvoice = null;
		Integer originalInvoiceID = (Integer)document.get_Value("C_Invoice_Orig_ID"); 
		if(!Util.isEmpty(originalInvoiceID, true)){
			originalInvoice = new MInvoice(ctx, originalInvoiceID, null);
		}
		
		// Creo un documento imprimible
		Document documentFiscalPrintable = createDocument((MInvoice) document,
				originalInvoice);
		
		// Obtengo el servidor a partir de la organización configurada y de la
		// configuración de host para ella
		CallResult result = null;
		FiscalPrint server = null;
		try {
			server = getConnection(ctx, cFiscal.getAD_Org_ID(), null, true);
			result = server.printDocument(ctx, document,
					documentFiscalPrintable, originalInvoice,
					docTypecolumnNameUID, docTypecolumnValueUID);
			// Obtener las modificaciones al documento remotamente y pasarlos al local		
			if(!result.isError()){
				List<Object> dataReturned = (List<Object>)result.getResult();
				MInvoice documentReturned = (MInvoice)dataReturned.get(0);
				setReturnedInvoiceInfo(documentReturned, (MInvoice)document);
				if(!document.save()){
					throw new Exception(CLogger.retrieveErrorAsString());
				}
				fireDocumentPrintEndedOk();
			}
			else{
				fireErrorOcurred("PrintFiscalDocumentError", result.getMsg());
			}
		} catch (Exception e) {
			result.setMsg(e.getMessage(), true);
		}		
		setErrorMsg(result.getMsg());
		return !result.isError();
	}
	
	/**
	 * Recupero la conexión a la sucursal
	 * 
	 * @param ctx
	 *            contexto
	 * @param trxName
	 *            nombre de la transacción
	 * @return conexión, null en caso de error
	 */
	protected FiscalPrint getConnection(Properties ctx, Integer orgID, String trxName, boolean throwIfNull) throws Exception{
		FiscalPrintConnection conn = new FiscalPrintConnection(orgID, trxName);
		if(conn == null && throwIfNull){
			throw new Exception("Error getting connection to organization");
		}
		return conn.getFiscalPrintConnection();
	}
	
	/**
	 * Obtengo el valor de la columna unívoca del registro parámetro. <br>
	 * CUIDADO: No funciona para claves múltiples.
	 * 
	 * @param po
	 *            registro o entidad
	 * @return Valor de la columna que identifica unívocamente del regsitro
	 *         parámetro
	 */
	public Object getUIDColumnValue(PO po) {
		// Obtengo la columna que identifica univocamente al PO parámetro
		String columnName = getUIDColumnName(po);
		// Obtengo el valor de esa columna para este PO
		Object value = DB.getSQLObject(po.get_TrxName(), "SELECT " + columnName
				+ " FROM " + po.get_TableName() + " WHERE "
				+ po.get_TableName() + "_id = ?", new Object[] { po.getID() });
		return value;
	}

	/**
	 * @param po
	 *            objeto persistente
	 * @return nombre de la columna de clave única para un PO en particular
	 */
	public String getUIDColumnName(PO po) {
		return "retrieveuid";
	}
	
	@Override
	protected boolean canSaveOxpDocument(){
		return false;
	}
	
	
	protected void setReturnedInvoiceInfo(MInvoice from, MInvoice to){
		to.setDocumentNo(from.getDocumentNo());
		to.setFiscalAlreadyPrinted(from.isFiscalAlreadyPrinted());
		to.setNumeroComprobante(from.getNumeroComprobante());
		to.setCAI(from.getCAI());
		to.setDateCAI(from.getDateCAI());
	}
}
