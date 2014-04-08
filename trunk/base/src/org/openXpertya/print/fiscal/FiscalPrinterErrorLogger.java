package org.openXpertya.print.fiscal;


public class FiscalPrinterErrorLogger extends AbstractFiscalPrinterLogger {

	public FiscalPrinterErrorLogger() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean canSaveRecord(boolean isError){
		return isError;
	}

}
