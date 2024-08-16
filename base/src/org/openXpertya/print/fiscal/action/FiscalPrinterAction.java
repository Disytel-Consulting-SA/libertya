package org.openXpertya.print.fiscal.action;

import org.openXpertya.model.FiscalDocumentPrint;

public abstract class FiscalPrinterAction {

	// Variables de instancia
	
	/** Mensaje de error */
	
	private String errorMsg;
	
	/** Mensaje de descripción de error */
	
	private String errorDesc;
	
	/** Nombre de transacción a utilizar, en caso que utilice */
	
	private String trxName;
	
	/** Acceso a la impresora */
	
	private FiscalDocumentPrint fdp;
	
	// Constructores
	
	public FiscalPrinterAction(FiscalDocumentPrint fdp, String trxName){
		setFdp(fdp);
		setTrxName(trxName);
	}
	
	// Métodos abstractos
	
	/**
	 * Ejecuta la acción correspondiente
	 */
	public abstract boolean execute();
	
	// Getters y Setters
	
	protected void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	protected void setTrxName(String trxName) {
		this.trxName = trxName;
	}


	protected String getTrxName() {
		return trxName;
	}

	protected void setFdp(FiscalDocumentPrint fdp) {
		this.fdp = fdp;
	}

	protected FiscalDocumentPrint getFdp() {
		return fdp;
	}
}
