package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1110 extends PluginPostInstallProcess {

	/** UID de la impresión de Transferencia de Mercadería */
	protected final static String MATERIAL_TRANSFER_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010044";
	protected final static String MATERIAL_TRANSFER_JASPER_REPORT_FILENAME = "MaterialTransfer.jasper";
	
	/** UID de la impresión de Cierre de Almacén */
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010045";
	protected final static String WAREHOUSE_CLOSE_JASPER_REPORT_FILENAME = "WarehouseClose.jasper";
	
	/** UID del Informe de inventario de bienes de uso */
	protected final static String INVENTORY_ASSETS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010046";
	protected final static String INVENTORY_ASSETS_JASPER_REPORT_FILENAME = "InventoryAssetsReport.jasper";
	
	/** UID del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010047";
	protected final static String DECLARACION_VALORES_JASPER_REPORT_FILENAME = "DeclaracionDeValores.jasper";
	
	/** UID del Subreporte de transacciones del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010048";
	protected final static String DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport.jasper";
	
	/** UID del Subreporte de valores del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010049";
	protected final static String DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport_Valores.jasper";
	
	/** UID del Reporte de Cambio de Artículos */
	protected final static String CAMBIO_JASPER_REPORT_UID = "R182CORE-AD_JasperReport-1010046-20120329191043";
	protected final static String CAMBIO_JASPER_REPORT_FILENAME = "rpt_Cambio.jasper";
	
	/** UID del Reporte de Retiro por Depósito */
	protected final static String WAREHOUSE_DELIVER_DOCUMENT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010068";
	protected final static String WAREHOUSE_DELIVER_DOCUMENT_JASPER_REPORT_FILENAME = "WarehouseDeliverDocument.jasper";
	
	/** UID de la impresión de Entradas/Simples */
	protected final static String INVENTORY_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010069";
	protected final static String INVENTORY_JASPER_REPORT_FILENAME = "Inventory.jasper";
	
	protected String doIt() throws Exception {
		super.doIt();
		
		// Impresión de Transferencia de Mercadería
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						MATERIAL_TRANSFER_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(MATERIAL_TRANSFER_JASPER_REPORT_FILENAME)));
		
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
		
		// Informe de inventario de bienes de uso
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				INVENTORY_ASSETS_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(INVENTORY_ASSETS_JASPER_REPORT_FILENAME)));
		
		// Informe de Declaración de Valores
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				DECLARACION_VALORES_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(DECLARACION_VALORES_JASPER_REPORT_FILENAME)));

		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_FILENAME)));

		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_FILENAME)));
		
		// Informe de Cambio
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				CAMBIO_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CAMBIO_JASPER_REPORT_FILENAME)));

		// Informe de Retiro por Depósito
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				WAREHOUSE_DELIVER_DOCUMENT_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(WAREHOUSE_DELIVER_DOCUMENT_JASPER_REPORT_FILENAME)));
		
		// Impresión de Entradas/Salidas Simples
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				INVENTORY_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(INVENTORY_JASPER_REPORT_FILENAME)));
		
		return " ";
	}
	
}
