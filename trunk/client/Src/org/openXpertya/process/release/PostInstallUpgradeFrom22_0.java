package org.openXpertya.process.release;

import org.openXpertya.process.PluginPostInstallProcess;

public class PostInstallUpgradeFrom22_0 extends PluginPostInstallProcess {
	/** Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** Subreporte Informe Libro IVA Manuales */
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID = "LIVARPENH-AD_JasperReport-20210831110740889-608253";
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME = "SubReport_TaxInformeLibroIva_Manuales.jasper";
	
	protected final static String SUMAS_Y_SALDOS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000010";
	protected final static String SUMAS_Y_SALDOS_JASPER_REPORT_FILENAME = "SumasYSaldos.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de FE
		updateReport(FE_REPORT_UID, FE_REPORT_FILENAME);
		
		// Subreporte Informe Libro IVA Manuales
		updateReport(LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID, LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME);
		
		// Sumas y Saldos
		updateReport(SUMAS_Y_SALDOS_JASPER_REPORT_UID, SUMAS_Y_SALDOS_JASPER_REPORT_FILENAME);
		
		return "";
	}
	
}