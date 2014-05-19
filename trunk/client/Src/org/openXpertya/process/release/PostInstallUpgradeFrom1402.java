package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1402 extends PluginPostInstallProcess {

	/** UID del informe de Movimientos de artículo detallado */
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010092";
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_FILENAME = "ProductMovementsWithStockBalance.jasper";
	
	/** UID del Informe de Libro de IVA */
	protected final static String LIBRO_IVA_REPORT_JASPER_REPORT_UID = "LIVA2CORE-AD_JasperReport-1010047-20121031201418";
	protected final static String LIBRO_IVA_REPORT_JASPER_REPORT_FILENAME = "InformeLibroIVA.jasper";
	
	/** UID del Subreporte de impuestos del Informe de Libro de IVA */
	protected final static String LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_UID = "LIVA2CORE-AD_JasperReport-1010053-20121031201630";
	protected final static String LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_FILENAME = "SubReport_TaxInformeLibroIva.jasper";
	
	/** UID del Libro de Mayor */
	protected final static String DIARIO_DEL_MAYOR_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000007";
	protected final static String DIARIO_DEL_MAYOR_JASPER_REPORT_FILENAME = "DiarioMayor.jasper";
	
	/** UID del Informe de Ventas por Provincia */
	protected final static String REPORT_SALES_BY_PROVINCE_REPORT_UID = "CORE-AD_Process-1010380";
	protected final static String REPORT_SALES_BY_PROVINCE_REPORT_FILENAME = "report_sales_by_province.jrxml";
	
	/** UID del Informe de Libro Diario */
	protected final static String REPORT_JOURNAL_BOOK_REPORT_UID = "CORE-AD_Process-1010379";
	protected final static String REPORT_JOURNAL_BOOK_REPORT_FILENAME = "LibroDiario.jrxml";
	
	/** UID del Informe de inventario de bienes de uso */
	protected final static String INVENTORY_ASSETS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010046";
	protected final static String INVENTORY_ASSETS_JASPER_REPORT_FILENAME = "InventoryAssetsReport.jasper";
	
	/** UID del informe de Precios Actualizados */
	protected final static String UPDATED_PRICES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010095";
	protected final static String UPDATED_PRICES_JASPER_REPORT_FILENAME = "UpdatedPrices.jasper";
	
	/** UID del Informe de Comprobantes por Titular de Tarjeta */
	protected final static String INVOICES_BY_CREDIT_CARD_OWNER_REPORT_UID = "CORE-AD_Process-1010381";
	protected final static String INVOICES_BY_CREDIT_CARD_OWNER_REPORT_FILENAME = "InvoicesByCreditCardOwner.jrxml";

	/** Informe de Recibos de Cliente */
	protected final static String CUSTOMER_RECEIPT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000016";
	protected final static String CUSTOMER_RECEIPT_JASPER_REPORT_FILENAME = "ReciboCliente.jasper";
	
	/** IVA Ventas General */
	protected final static String IVA_VENTA_GENERAL_REPORT_UID = "CORE-AD_Process-1010324";
	protected final static String IVA_VENTA_GENERAL_REPORT_FILENAME = "Iva_Ventas.jrxml";
	
	protected String doIt() throws Exception {
		super.doIt();
		
		/*
		 * Actualizacion de binarios
		 * """"""""""""""""""""""""" 
		 * Utilizar SIEMPRE los métodos MJasperReport.updateBinaryData() y MProcess.addAttachment() 
		 * para la carga de informes tipo Jasper, el primero para la carga en AD_JasperReport y el 
		 * segundo en reportes dinámicos, los cuales van adjuntos en el informe/proceso correspondiente.
		 */
		
		// Movimientos de artículo detallado
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_FILENAME)));
		
		// Informe de Libro de IVA
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					LIBRO_IVA_REPORT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(LIBRO_IVA_REPORT_JASPER_REPORT_FILENAME)));
		
		// Subreporte de impuestos del Informe de Libro de IVA
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_FILENAME)));
		
		// Informe de Libro del Mayor
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DIARIO_DEL_MAYOR_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DIARIO_DEL_MAYOR_JASPER_REPORT_FILENAME)));
				
		// Informe de Ventas por Provincia
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				REPORT_SALES_BY_PROVINCE_REPORT_UID,
				REPORT_SALES_BY_PROVINCE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(REPORT_SALES_BY_PROVINCE_REPORT_FILENAME)));
		
		// Informe de Libro Diario
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				REPORT_JOURNAL_BOOK_REPORT_UID,
				REPORT_JOURNAL_BOOK_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(REPORT_JOURNAL_BOOK_REPORT_FILENAME)));
		
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

		// Reporte de precios actualizados
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				UPDATED_PRICES_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(UPDATED_PRICES_JASPER_REPORT_FILENAME)));

		// Informe de Comprobantes por Titular de Tarjeta
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				INVOICES_BY_CREDIT_CARD_OWNER_REPORT_UID,
				INVOICES_BY_CREDIT_CARD_OWNER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(INVOICES_BY_CREDIT_CARD_OWNER_REPORT_FILENAME)));
		
		// Actualización del reporte de Recibos de cliente
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				CUSTOMER_RECEIPT_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CUSTOMER_RECEIPT_JASPER_REPORT_FILENAME)));
		
		// Informe IVA Ventas General
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				IVA_VENTA_GENERAL_REPORT_UID,
				IVA_VENTA_GENERAL_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(IVA_VENTA_GENERAL_REPORT_FILENAME)));
		
		return " ";
	}

}
