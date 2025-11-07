package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
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
	
	/** UID de la impresión cierre administrativo de tarjetas Merge fidelius 1.3*/
	protected final static String CREDIT_CARD_CLOSE_JASPER_REPORT_UID = "CORE-AD_Process-1010445";
	protected final static String CREDIT_CARD_CLOSE_JASPER_REPORT_FILENAME = "CreditCardClose.jasper";
	
	/** UID de la impresión de Entregas por Deposito Merge org.libertya.core.micro.r3000.dev.facturacion */
	protected final static String ENTREGA_POR_DEPOSITO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010068";
	protected final static String ENTREGA_POR_DEPOSITO_JASPER_REPORT_FILENAME = "WarehouseDeliverDocument.jasper";
	
	/** Reporte de Cuenta Corriente por Entidad Comercial Merge org.libertya.core.micro.r3019.dev.jacofer_14_cc*/
	protected final static String BALANCE_BY_BPARTNER_JASPER_REPORT_UID = "JACLBY-AD_JasperReport-20200413133217034-110405";
	protected final static String BALANCE_BY_BPARTNER_JASPER_REPORT_FILENAME = "CurrentAccountByBPartner.jasper";
	
	/** Merge org.libertya.core.micro.7984dd0.dev.contabilidad_en_linea from 0.0 */
	protected final static String DIARIO_DEL_MAYOR_UID = "CORE-AD_JasperReport-1000007";
	protected final static String DIARIO_DEL_MAYOR_FILENAME = "DiarioMayor.jasper";
	protected final static String LIBRO_DIARIO_UID = "CORE-AD_JasperReport-1000009";
	protected final static String LIBRO_DIARIO_FILENAME = "LibroDiario.jasper";
	protected final static String SUMAS_Y_SALDOS_UID = "CORE-AD_JasperReport-1000010";
	protected final static String SUMAS_Y_SALDOS_FILENAME = "SumasYSaldos.jasper";
	protected final static String SUMAS_Y_SALDOS_AGRUPADO_UID = "CELBD-AD_JasperReport-20240202175316548-654408";
	protected final static String SUMAS_Y_SALDOS_AGRUPADO_FILENAME = "SumasYSaldosAgrupado.jasper";
	/** Merge org.libertya.core.micro.7984dd0.dev.contabilidad_en_linea from 1.0 */
	// protected final static String SUMAS_Y_SALDOS_AGRUPADO_UID = "CELBD-AD_JasperReport-20240202175316548-654408";
	// protected final static String SUMAS_Y_SALDOS_AGRUPADO_FILENAME = "SumasYSaldosAgrupado.jasper";
	
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
		
		// Cierre administrativo de tarjetas Merge fidelius 1.3
		updateAttachment(CREDIT_CARD_CLOSE_JASPER_REPORT_UID, CREDIT_CARD_CLOSE_JASPER_REPORT_FILENAME);
		
		// Entrega por deposito Merge org.libertya.core.micro.r3000.dev.facturacion
		updateReport(ENTREGA_POR_DEPOSITO_JASPER_REPORT_UID, ENTREGA_POR_DEPOSITO_JASPER_REPORT_FILENAME);
		
		// Merge org.libertya.core.micro.7984dd0.dev.contabilidad_en_linea from 0.0
		updateReport(DIARIO_DEL_MAYOR_UID, DIARIO_DEL_MAYOR_FILENAME);
		updateReport(LIBRO_DIARIO_UID, LIBRO_DIARIO_FILENAME);
		updateReport(SUMAS_Y_SALDOS_UID, SUMAS_Y_SALDOS_FILENAME);
		updateReport(SUMAS_Y_SALDOS_AGRUPADO_UID, SUMAS_Y_SALDOS_AGRUPADO_FILENAME);
		// Merge org.libertya.core.micro.7984dd0.dev.contabilidad_en_linea from 1.0
		// updateReport(SUMAS_Y_SALDOS_AGRUPADO_UID, SUMAS_Y_SALDOS_AGRUPADO_FILENAME);
		
		
		
		// Reporte de Cuenta Corriente por Entidad Comercial Merge org.libertya.core.micro.r3019.dev.jacofer_14_cc
				MJasperReport
					.updateBinaryData(
							get_TrxName(),
							getCtx(),
							BALANCE_BY_BPARTNER_JASPER_REPORT_UID,
							JarHelper
									.readBinaryFromJar(
											jarFileURL,
											getBinaryFileURL(BALANCE_BY_BPARTNER_JASPER_REPORT_FILENAME)));
		
		return "";
	}
	
}
