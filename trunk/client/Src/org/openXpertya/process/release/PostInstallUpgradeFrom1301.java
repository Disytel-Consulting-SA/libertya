package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MAttachment;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.util.DB;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1301 extends PluginPostInstallProcess {

	/** UID del informe de movimientos de compra/venta por artículo */
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010090";
	protected final static String PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME = "ProductSalesPurchaseMovements.jasper";
	
	/** UID del informe de Maestras de Compras */
	protected final static String PURCHASE_MASTER_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010091";
	protected final static String PURCHASE_MASTER_JASPER_REPORT_FILENAME = "PurchaseMasterReport.jasper";
	
	/** UID del informe de Movimientos de artículo detallado */
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010092";
	protected final static String PRODUCT_MOVEMENTS_DETAILED_JASPER_REPORT_FILENAME = "ProductMovementsWithStockBalance.jasper";
	
	/** UID del subreporte del informe de Movimientos de artículo detallado */
	protected final static String PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010093";
	protected final static String PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_FILENAME = "ProductMovementsWithStockBalance_Subreport.jasper";
	
	/** UID de la impresión de Fraccionamiento */
	protected final static String SPLITTING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010094";
	protected final static String SPLITTING_JASPER_REPORT_FILENAME = "Product Splitting.jasper";
	
	/** UID del informe de Precios Actualizados */
	protected final static String UPDATED_PRICES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010095";
	protected final static String UPDATED_PRICES_JASPER_REPORT_FILENAME = "UpdatedPrices.jasper";
	
	/** UID del reporte de Resumen de Ventas */
	protected final static String SALES_SUMMARY_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010074";
	protected final static String SALES_SUMMARY_JASPER_REPORT_FILENAME = "ResumenVentas.jasper";
	
	/** UID del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010047";
	protected final static String DECLARACION_VALORES_JASPER_REPORT_FILENAME = "DeclaracionDeValores.jasper";
	
	/** UID del Informe de Ventas por Línea de Artículo */
	protected final static String PRODUCT_LINES_SALES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010096";
	protected final static String PRODUCT_LINES_SALES_JASPER_REPORT_FILENAME = "ProductLinesSales.jasper";
	
	/** UID del Informe de Ventas por Horario */
	protected final static String SALES_BY_HOUR_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010097";
	protected final static String SALES_BY_HOUR_JASPER_REPORT_FILENAME = "SalesForHour.jasper";
	
	/** UID del Informe de Ranking de Ventas */
	protected final static String SALES_RANKING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010099";
	protected final static String SALES_RANKING_JASPER_REPORT_FILENAME = "SalesRanking.jasper";
	
	/** UID del Informe de Comprobantes Registrados */
	protected final static String MANUAL_DISCOUNTS_FOLLOWING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010098";
	protected final static String MANUAL_DISCOUNTS_FOLLOWING_JASPER_REPORT_FILENAME = "SeguimientoDescuentos.jasper";
	
	/** UID del Informe de Comprobantes Registrados */
	protected final static String REGISTERED_DOCUMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010100";
	protected final static String REGISTERED_DOCUMENTS_JASPER_REPORT_FILENAME = "RegisteredDocuments.jasper";
	
	/** UID del informe IVA Ventas General*/
	protected final static String IVA_VENTAS_GENERAL_REPORT_UID = "CORE-AD_Process-1010324";
	/** Nombre del .jrxml del informe IVA Ventas General*/
	protected final static String IVA_VENTAS_GENERAL_REPORT_FILENAME = "Iva_Ventas.jrxml";
	
	/** UID del Subreporte del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010048";
	protected final static String DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport.jasper";
	
	/** UID del Subreporte de Valores del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010049";
	protected final static String DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport_Valores.jasper";

	/** UID del Subreporte de Ventas */
	protected final static String DECLARACION_VALORES_SUBREPORT_VENTAS_RECEIPT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010072";
	protected final static String DECLARACION_VALORES_SUBREPORT_VENTAS_RECEIPT_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport_VentasReceipt.jasper";
	
	/** UID del Subreporte de Anulados del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_SUBREPORT_ANULADOS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010101";
	protected final static String DECLARACION_VALORES_SUBREPORT_ANULADOS_JASPER_REPORT_FILENAME = "DeclaracionDeValores_Subreport_Anulados.jasper";
	
	/** UID del Informe de Libro de IVA */
	protected final static String LIBRO_IVA_REPORT_JASPER_REPORT_UID = "LIVA2CORE-AD_JasperReport-1010047-20121031201418";
	protected final static String LIBRO_IVA_REPORT_JASPER_REPORT_FILENAME = "InformeLibroIVA.jasper";
	
	/** UID del Subreporte de impuestos del Informe de Libro de IVA */
	protected final static String LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_UID = "LIVA2CORE-AD_JasperReport-1010053-20121031201630";
	protected final static String LIBRO_IVA_TAX_SUBREPORT_JASPER_REPORT_FILENAME = "SubReport_TaxInformeLibroIva.jasper";	

	protected String doIt() throws Exception {
		super.doIt();
		
		// Movimientos de venta/compra de artículo
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_SALES_PURCHASE_MOVEMENTS_JASPER_REPORT_FILENAME)));
		
		// Maestra de Compras
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PURCHASE_MASTER_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PURCHASE_MASTER_JASPER_REPORT_FILENAME)));
		
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
		
		// Subreporte de Movimientos de artículo detallado
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(PRODUCT_MOVEMENTS_DETAILED_SUBREPORT_JASPER_REPORT_FILENAME)));
		
		// Impresión de Fraccionamiento
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						SPLITTING_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(SPLITTING_JASPER_REPORT_FILENAME)));
		
		// Informe de Precios actualizados
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						UPDATED_PRICES_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(UPDATED_PRICES_JASPER_REPORT_FILENAME)));
		
		// Reporte de Resumen de Ventas
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						SALES_SUMMARY_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(SALES_SUMMARY_JASPER_REPORT_FILENAME)));
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
		
		// Informe de Ventas por Líneas de Artículo
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					PRODUCT_LINES_SALES_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(PRODUCT_LINES_SALES_JASPER_REPORT_FILENAME)));
		
		// Informe de Ventas por Horario
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					SALES_BY_HOUR_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(SALES_BY_HOUR_JASPER_REPORT_FILENAME)));
		
		// Informe de Ranking de Ventas
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					SALES_RANKING_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(SALES_RANKING_JASPER_REPORT_FILENAME)));
		
		// Informe de Seguimiento de Descuentos
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					MANUAL_DISCOUNTS_FOLLOWING_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(MANUAL_DISCOUNTS_FOLLOWING_JASPER_REPORT_FILENAME)));

		// Informe de Comprobantes Registrados
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					REGISTERED_DOCUMENTS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(REGISTERED_DOCUMENTS_JASPER_REPORT_FILENAME)));
		
		// Informe de IVA Ventas General
		MAttachment att  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att.setAD_Table_ID(MProcess.Table_ID);
		String getIDFromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		att.setRecord_ID(DB.getSQLValue(get_TrxName(), getIDFromUID, IVA_VENTAS_GENERAL_REPORT_UID));
		att.addEntry("Iva_Ventas.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(IVA_VENTAS_GENERAL_REPORT_FILENAME)));
		if(!att.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Subreporte del Informe de Declaración de Valores
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_JASPER_REPORT_FILENAME)));

		// Subreporte de Valores del Informe de Declaración de Valores
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_VALORES_JASPER_REPORT_FILENAME)));
		
		// Subreporte de Ventas
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DECLARACION_VALORES_SUBREPORT_VENTAS_RECEIPT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_VENTAS_RECEIPT_JASPER_REPORT_FILENAME)));
		
		// Subreporte de Anulados del Informe de Declaración de Valores
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DECLARACION_VALORES_SUBREPORT_ANULADOS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DECLARACION_VALORES_SUBREPORT_ANULADOS_JASPER_REPORT_FILENAME)));
		
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
		
		return " ";
	}

}
