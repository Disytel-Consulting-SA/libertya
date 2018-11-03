package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1806 extends PluginPostInstallProcess {

	/** UID del reporte de Retenciones de la Orden de Pago */
	protected final static String RETENCIONES_ORDEN_PAGO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010231";
	protected final static String RETENCIONES_ORDEN_PAGO_JASPER_REPORT_FILENAME = "OrdenPago_Retenciones.jasper";
	
	/** UID de la impresión de codigos o cupones promocionales */
	protected final static String PROMOTIONAL_CODES_BATCH_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010251";
	protected final static String PROMOTIONAL_CODES_BATCH_JASPER_REPORT_FILENAME = "PromotionalCodeBatch.jasper";
	
	/** UID del Listado de OC */
	protected final static String PURCHASE_ORDER_REPORT_JASPER_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String PURCHASE_ORDER_REPORT_JASPER_REPORT_FILENAME = "PurchaseOrderReport.jrxml";
	
	/** UID del reporte de Orden de Pago */
	protected final static String ORDEN_PAGO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String ORDEN_PAGO_JASPER_REPORT_FILENAME = "OrdenPago.jasper";
	
	/** UID del Informe de Estado de Pedido */
	protected final static String ORDER_STATUS_REPORT_JASPER_REPORT_UID = "CORE-AD_Process-1010615";
	protected final static String ORDER_STATUS_REPORT_JASPER_REPORT_FILENAME = "OrderStatus.jrxml";
	
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
		
		// Subreporte de Retenciones de Orden de Pago
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					RETENCIONES_ORDEN_PAGO_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(RETENCIONES_ORDEN_PAGO_JASPER_REPORT_FILENAME)));

		// Reporte de Códigos Promocionales
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					PROMOTIONAL_CODES_BATCH_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(PROMOTIONAL_CODES_BATCH_JASPER_REPORT_FILENAME)));
		
		// Listado de OC
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PURCHASE_ORDER_REPORT_JASPER_REPORT_UID,
				PURCHASE_ORDER_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PURCHASE_ORDER_REPORT_JASPER_REPORT_FILENAME)));
		
		// Informe de Orden de Pago
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					ORDEN_PAGO_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(ORDEN_PAGO_JASPER_REPORT_FILENAME)));

		// Estado de Pedido
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ORDER_STATUS_REPORT_JASPER_REPORT_UID,
				ORDER_STATUS_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ORDER_STATUS_REPORT_JASPER_REPORT_FILENAME)));

		
		return " ";
	}
}
