package org.openXpertya.model;

import org.openXpertya.model.FiscalDocumentPrint.Actions;


/**
 * Adaptador de listener de impresor de documentos. Contiene implementaciones
 * vacias de los métodos definidos en la interfaz <code>FiscalDocumentPrintListener</code>.
 * @author Franco Bonafine
 * @date 03/03/2008
 */
public class FiscalDocumentPrintAdapter implements FiscalDocumentPrintListener  {

	private FiscalDocumentPrint fdp = null;
	
	public void actionStarted(FiscalDocumentPrint source, int action) {
		return;
	}

	public void documentPrintEndedOk(FiscalDocumentPrint source) {
		return;
	}

	public void errorOcurred(FiscalDocumentPrint source, String errorTitle, String errorDesc) {
		return;
	}

	public void statusReported(FiscalDocumentPrint source, MControladorFiscal cFiscal, String status) {
		return;
	}

	public void actionEnded(boolean ok, Actions action) {
		return;
	}

	@Override
	public void setFiscalDocumentPrint(FiscalDocumentPrint fdp) {
		this.fdp = fdp; 
	}
	
	public FiscalDocumentPrint getFiscalDocumentPrint() {
		return fdp;
	}

	@Override
	public void documentPrintAsk(FiscalDocumentPrint source, String errorTitle,
			String errorDesc, String printerStatus) {
		// TODO Auto-generated method stub
		
	}
}
