package org.openXpertya.print.fiscal;

import org.openXpertya.model.MControladorFiscal;

public class FiscalPrinterLoggerManager {

	public static AbstractFiscalPrinterLogger getFiscalPrinterLogger(MControladorFiscal controladorFiscal){
		AbstractFiscalPrinterLogger logger = null;
		if(controladorFiscal.getLogTypeRecorded() != null){
			if (MControladorFiscal.LOGTYPERECORDED_All.equals(controladorFiscal
					.getLogTypeRecorded())) {
				logger = new FiscalPrinterAllLogger();
			}
			else if(MControladorFiscal.LOGTYPERECORDED_OnlyErrors.equals(controladorFiscal
					.getLogTypeRecorded())){
				logger = new FiscalPrinterErrorLogger();
			}
		}
		return logger;
	}
	
	public FiscalPrinterLoggerManager() {
		// TODO Auto-generated constructor stub
	}

}
