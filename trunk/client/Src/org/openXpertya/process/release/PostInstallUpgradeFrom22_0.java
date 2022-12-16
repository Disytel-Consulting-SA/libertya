package org.openXpertya.process.release;

import org.openXpertya.process.PluginPostInstallProcess;

public class PostInstallUpgradeFrom22_0 extends PluginPostInstallProcess {
	/** Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de FE
		updateReport(FE_REPORT_UID, FE_REPORT_FILENAME);
		
		return "";
	}
	
}