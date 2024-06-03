package org.openXpertya.process.release;

import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom22_0 extends PluginPostInstallProcess {
	/** Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** Subreporte Informe Libro IVA Manuales */
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID = "LIVARPENH-AD_JasperReport-20210831110740889-608253";
	protected final static String LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME = "SubReport_TaxInformeLibroIva_Manuales.jasper";
	
	protected final static String SUMAS_Y_SALDOS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000010";
	protected final static String SUMAS_Y_SALDOS_JASPER_REPORT_FILENAME = "SumasYSaldos.jasper";
	
	/** Impresión de la Hoja de Ruta Merge COT ARBA*/
	protected final static String ROADMAP_JASPER_REPORT_UID = "JACOFER-AD_Process-20200109160937125-741474";
	protected final static String ROADMAP_JASPER_REPORT_FILENAME = "JacoferRoadMapPrint.jasper";
	
	/** PostInstallUpgradeFrom00 Merge Micro Cintolo*/
	protected final static String FACTURA_ELECTRONICA_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FACTURA_ELECTRONICA_FILENAME = "rpt_Factura_Electronica.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de FE
		updateReport(FE_REPORT_UID, FE_REPORT_FILENAME);
		
		// Subreporte Informe Libro IVA Manuales
		updateReport(LIVA_SUBREPORT_MANUAL_JASPER_REPORT_UID, LIVA_SUBREPORT_MANUAL_JASPER_REPORT_FILENAME);
		
		// Sumas y Saldos
		updateReport(SUMAS_Y_SALDOS_JASPER_REPORT_UID, SUMAS_Y_SALDOS_JASPER_REPORT_FILENAME);
		
		// Impresión de la Hoja de Ruta - Merge COT ARBA
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ROADMAP_JASPER_REPORT_UID,
				ROADMAP_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ROADMAP_JASPER_REPORT_FILENAME)));
		
		/** PostInstallUpgradeFrom00 Merge Micro Cintolo*/
		updateReport(FACTURA_ELECTRONICA_UID, FACTURA_ELECTRONICA_FILENAME);
		
		return "";
	}
	
}
