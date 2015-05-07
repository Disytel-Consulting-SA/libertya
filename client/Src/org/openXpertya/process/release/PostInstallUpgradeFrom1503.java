package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1503 extends PluginPostInstallProcess {
	
	/** UID del Informe de Ventas por Region */
	protected final static String VENTAS_POR_REGION_REPORT_UID = "CORE-AD_Process-1010399";
	protected final static String VENTAS_POR_REGION_REPORT_FILENAME = "VentasPorRegion.jrxml";
	
	/** UID del Informe de Compras por Region */
	protected final static String COMPRAS_POR_REGION_REPORT_UID = "CORE-AD_Process-1010400";
	protected final static String COMPRAS_POR_REGION_REPORT_FILENAME = "ComprasPorRegion.jrxml";
	
	/** UID del Informe de Cobranzas y Pagos */
	protected final static String INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_UID = "CORE-AD_Process-1010401";
	protected final static String INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_FILENAME = "InformeDeCobranzasYPagos.jrxml";
	
	/** UID del informe de Seguimiento de Folletos */
	protected final static String BROCHURE_REPORT_UID = "CORE-AD_JasperReport-1010129";
	protected final static String BROCHURE_REPORT_FILENAME = "BrochureReport.jasper";
	
	/** UID del Reporte de Correcciones de Comprobantes */
	protected final static String CHANGE_INVOICE_DATA_REPORT_UID = "CORE-AD_Process-1010414";
	protected final static String CHANGE_INVOICE_DATA_REPORT_FILENAME = "ChangeInvoiceDataAudit.jrxml";
	
	/** UID de la impresión de Cierre de Almacén */
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010045";
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_FILENAME = "WarehouseClose.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		/*
		 * Actualizacion de binarios
		 * """"""""""""""""""""""""" 
		 * Utilizar SIEMPRE los métodos MJasperReport.updateBinaryData() y MProcess.addAttachment() 
		 * para la carga de informes tipo Jasper, el primero para la carga en AD_JasperReport y el 
		 * segundo en reportes dinámicos, los cuales van adjuntos en el informe/proceso correspondiente.
		 */
		
		// Informe de Ventas por Region
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				VENTAS_POR_REGION_REPORT_UID,
				VENTAS_POR_REGION_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(VENTAS_POR_REGION_REPORT_FILENAME)));
		
		// Informe de Compras por Region
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				COMPRAS_POR_REGION_REPORT_UID,
				COMPRAS_POR_REGION_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(COMPRAS_POR_REGION_REPORT_FILENAME)));
		
		// Informe de Cobranzas y Pagos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_UID,
				INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_FILENAME)));
		
		// Seguimiento de Folletos
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						BROCHURE_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(BROCHURE_REPORT_FILENAME)));
		
		// Reporte de Correcciones de Comprobantes
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CHANGE_INVOICE_DATA_REPORT_UID,
				CHANGE_INVOICE_DATA_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CHANGE_INVOICE_DATA_REPORT_FILENAME)));
		
		// Impresión de Cierre de Almacén
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						WAREHOUSE_CLOSE_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(WAREHOUSE_CLOSE_JASPER_REPORT_FILENAME)));
		
		return " ";
	}
	
}
