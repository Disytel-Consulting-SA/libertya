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
	
	/** UID del informe Stock Valorizado a Fecha*/
	protected final static String STOCK_VALORIZADO_A_FECHA_REPORT_UID = "CORE-AD_Process-1010327";
	/** Nombre del .jrxml del informe Stock Valorizado a Fecha*/
	protected final static String STOCK_VALORIZADO_A_FECHA_REPORT_FILENAME = "StockValorizadoAFecha.jrxml";
	
	/** UID del informe Listado de Utilidades por Artículo*/
	protected final static String LISTADO_DE_UTILIDADES_POR_ARTICULO_REPORT_UID = "CORE-AD_Process-1010328";
	/** Nombre del .jrxml del informe Listado de Utilidades por Artículo*/
	protected final static String LISTADO_DE_UTILIDADES_POR_ARTICULO_REPORT_FILENAME = "UtilidadesPorArticulo.jrxml";

	/** UID del informe de Movimientos Valorizados */
	protected final static String VALUED_MOVEMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010102";
	protected final static String VALUED_MOVEMENTS_JASPER_REPORT_FILENAME = "ValuedMovements.jasper";
	
	/** UID del Listado de Notas de Crédito */
	protected final static String GENERATED_CREDIT_NOTES_REPORT_UID = "CORE-AD_Process-1010330";
	protected final static String GENERATED_CREDIT_NOTES_REPORT_FILENAME = "GeneratedCreditNotes.jrxml";
	
	/** UID del formato de impresión de Inventario y Entradas/Salidas Simples */
	protected final static String INVENTORY_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010069";
	protected final static String INVENTORY_JASPER_REPORT_FILENAME = "Inventory.jasper";
	
	/** UID del informe Cheques por Caja */
	protected final static String POSJOURNAL_CHECKS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010103";
	protected final static String POSJOURNAL_CHECKS_JASPER_REPORT_FILENAME = "POSJournalChecks.jasper";
	
	/** UID del Informe de Cheques por Cuenta */
	protected final static String CHECKS_BY_ACCOUNT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010104";
	protected final static String CHECKS_BY_ACCOUNT_JASPER_REPORT_FILENAME = "ChecksByAccount.jasper";
	
	/** UID del Reporte de Totales por Entidad Financiera */
	protected final static String TOTALES_POR_ENTIDAD_FINANCIERA_REPORT_UID = "CORE-AD_Process-1010338";
	protected final static String TOTALES_POR_ENTIDAD_FINANCIERA_REPORT_FILENAME = "TotalesPorEntidadFinanciera.jrxml";
	
	/** UID del Reporte de Operadores por Caja */
	protected final static String POSJOURNAL_SALES_REPS_REPORT_UID = "CORE-AD_Process-1010339";
	protected final static String POSJOURNAL_SALES_REPS_REPORT_FILENAME = "POSJournalSalesReps.jrxml";
	
	/** UID del Informe de Declaración de Valores por Organización */
	protected final static String DECLARACION_VALORES_X_ORG_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010105";
	protected final static String DECLARACION_VALORES_X_ORG_JASPER_REPORT_FILENAME = "DeclaracionDeValoresXOrg.jasper";
	
	/** UID del Subreporte del Informe de Declaración de Valores por Organización */
	protected final static String DECLARACION_VALORES_X_ORG_SUBREPORT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010106";
	protected final static String DECLARACION_VALORES_X_ORG_SUBREPORT_JASPER_REPORT_FILENAME = "DeclaracionDeValoresXOrg_Subreport.jasper";
	
	/** UID del informe Historia de Artículos*/
	protected final static String HISTORIA_DE_ARTICULOS_REPORT_UID = "CORE-AD_Process-1010341";
	/** Nombre del .jrxml del informe Historia de Artículos*/
	protected final static String HISTORIA_DE_ARTICULOS_REPORT_FILENAME = "HistoriaDeArticulos.jrxml";
	
	/** UID del informe Reporte de Compras*/
	protected final static String REPORTE_DE_COMPRAS_REPORT_UID = "CORE-AD_Process-1010342";
	/** Nombre del .jrxml del informe Reporte de Compras*/
	protected final static String REPORTE_DE_COMPRAS_REPORT_FILENAME = "ReporteDeCompras.jrxml";
	
	/** UID del informe Historia de Artículos por Mes / Semana*/
	protected final static String HISTORIA_DE_ARTICULOS_MES_SEMANA_REPORT_UID = "CORE-AD_Process-1010343";
	/** Nombre del .jrxml del informe Historia de Artículos por Mes / Semana*/
	protected final static String HISTORIA_DE_ARTICULOS_MES_SEMANA_REPORT_FILENAME = "HistoriaDeArticulosPorMesSemana.jrxml";
	
	/** UID del informe Historia de Costos*/
	protected final static String HISTORIA_DE_COSTOS_REPORT_UID = "CORE-AD_Process-1010344";
	/** Nombre del .jrxml del informe Historia de Costos*/
	protected final static String HISTORIA_DE_COSTOS_REPORT_FILENAME = "HistoriaDeCostos.jrxml";
	
	/** UID del informe Listado de Utilidades por Concepto*/
	protected final static String LISTADO_DE_UTILIDADES_POR_CONCEPTO_REPORT_UID = "CORE-AD_Process-1010345";
	/** Nombre del .jrxml del informe Listado de Utilidades por Concepto*/
	protected final static String LISTADO_DE_UTILIDADES_POR_CONCEPTO_REPORT_FILENAME = "Listado_de_Utilidades_por_Concepto.jrxml";
	
	/** UID del Informe de Débitos y Créditos de Cliente */
	protected final static String CUSTOMER_DEBITS_CREDITS_REPORT_UID = "CORE-AD_Process-1010346";
	protected final static String CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME = "CustomerDebitsCredits.jrxml";
	
	/** UID del Informe de Stock Valorizado */
	protected final static String STOCK_VALUED_REPORT_UID = "CORE-AD_Process-1010347";
	protected final static String STOCK_VALUED_REPORT_FILENAME = "StockValued.jrxml";
	
	/** UID del Informe de Venta/Compra de Entidades Financieras */
	protected final static String ENTIDADES_FINANCIERAS_SALES_PURCHASES_REPORT_UID = "CORE-AD_Process-1010358";
	protected final static String ENTIDADES_FINANCIERAS_SALES_PURCHASES_REPORT_FILENAME = "EntidadFinancieraSalesPurchases.jrxml";
	
	/** UID del Informe de Auditoría de Entidades Financieras */
	protected final static String ENTIDADES_FINANCIERAS_AUDIT_REPORT_UID = "CORE-AD_Process-1010359";
	protected final static String ENTIDADES_FINANCIERAS_AUDIT_REPORT_FILENAME = "EntidadFinancieraAudit.jrxml";
	
	/** UID del Informe de Cuenta Corriente de Entidades Financieras */
	protected final static String ENTIDADES_FINANCIERAS_CURRENT_ACCOUNT_REPORT_UID = "CORE-AD_Process-1010360";
	protected final static String ENTIDADES_FINANCIERAS_CURRENT_ACCOUNT_REPORT_FILENAME = "EntidadFinancieraCuentaCorriente.jrxml";
	
	/** UID del Informe de Saldos de Entidades Financieras */
	protected final static String ENTIDADES_FINANCIERAS_BALANCES_REPORT_UID = "CORE-AD_Process-1010361";
	protected final static String ENTIDADES_FINANCIERAS_BALANCES_REPORT_FILENAME = "EntidadFinancieraBalances.jrxml";
	
	/** UID del informe de Movimientos Valorizados Detallado */
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010117";
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME = "ValuedMovementsDetail.jasper";
	
	/** UID del Informe de Auditoría de Inventario Físico */
	protected final static String PHYSICAL_INVENTORY_AUDIT_REPORT_UID = "CORE-AD_Process-1010369";
	protected final static String PHYSICAL_INVENTORY_AUDIT_REPORT_FILENAME = "PhysicalInventoryAudit.jrxml";
	
	/** UID del reporte del Informe de Vencimientos */
	protected final static String VENCIMIENTOS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010043";
	protected final static String VENCIMIENTOS_JASPER_REPORT_FILENAME = "ListadoVencimientos.jasper";
	
	/** UID del reporte del Orden de Pago*/
	protected final static String ORDEN_PAGO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String ORDEN_PAGO_JASPER_REPORT_FILENAME = "OrdenPago.jasper";
	
	/** UID del Reporte Devoluciones a Proveedores */
	protected final static String DEVOLUCIONES_A_PROVEEDORES_REPORT_UID = "CORE-AD_Process-1010373";
	protected final static String DEVOLUCIONES_A_PROVEEDORES_REPORT_FILENAME = "report_return_to_vendor.jrxml";
	
	/** UID del Reporte Ventas por Subfamilia y Familia */
	protected final static String VENTAS_POR_SUBFAMILIA_Y_FAMILIA_REPORT_UID = "CORE-AD_Process-1010374";
	protected final static String VENTAS_POR_SUBFAMILIA_Y_FAMILIA_REPORT_FILENAME = "sales_report_by_subfamily_and_family_articles.jrxml";
	
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
		String getID_VentasGeneral_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int ventasGeneral_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_VentasGeneral_FromUID, IVA_VENTAS_GENERAL_REPORT_UID);
		
		String getAttachment_VentasGeneral = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int ventasGeneral_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_VentasGeneral, MProcess.Table_ID, ventasGeneral_Process_Record_ID);
		
		if(ventasGeneral_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ ventasGeneral_Process_Record_ID, get_TrxName());
		}
		MAttachment att  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att.setAD_Table_ID(MProcess.Table_ID);
		att.setRecord_ID(ventasGeneral_Process_Record_ID);
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
		
		// Informe de Stock Valorizado a Fecha
		String getID_StockValorizado_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int stockValorizado_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_StockValorizado_FromUID, STOCK_VALORIZADO_A_FECHA_REPORT_UID);
		
		String getAttachment_StockValorizado = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int stockValorizado_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_StockValorizado, MProcess.Table_ID, stockValorizado_Process_Record_ID);
		
		if(stockValorizado_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ stockValorizado_Process_Record_ID, get_TrxName());
		}
		MAttachment att_StockValorizado  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_StockValorizado.setAD_Table_ID(MProcess.Table_ID);
		att_StockValorizado.setRecord_ID(stockValorizado_Process_Record_ID);
		att_StockValorizado.addEntry("StockValorizadoAFecha.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(STOCK_VALORIZADO_A_FECHA_REPORT_FILENAME)));
		if(!att_StockValorizado.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Informe de Listado de Utilidades por Artículo
		String getID_ListadoUtilidadesArticulo_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int listadoUtilidadesArticulo_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_ListadoUtilidadesArticulo_FromUID, LISTADO_DE_UTILIDADES_POR_ARTICULO_REPORT_UID);
		
		String getAttachment_ListadoUtilidadesArticulo = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int listadoUtilidadesArticulo_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_ListadoUtilidadesArticulo, MProcess.Table_ID, listadoUtilidadesArticulo_Process_Record_ID);
		
		if(listadoUtilidadesArticulo_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ listadoUtilidadesArticulo_Process_Record_ID, get_TrxName());
		}
		MAttachment att_ListadoUtilidadesArticulo  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_ListadoUtilidadesArticulo.setAD_Table_ID(MProcess.Table_ID);
		att_ListadoUtilidadesArticulo.setRecord_ID(listadoUtilidadesArticulo_Process_Record_ID);
		att_ListadoUtilidadesArticulo.addEntry("UtilidadesPorArticulo.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(LISTADO_DE_UTILIDADES_POR_ARTICULO_REPORT_FILENAME)));
		if(!att_ListadoUtilidadesArticulo.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Reporte de Movimientos Valorizados
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					VALUED_MOVEMENTS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(VALUED_MOVEMENTS_JASPER_REPORT_FILENAME)));
		
		// Listado de Notas de Crédito
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				GENERATED_CREDIT_NOTES_REPORT_UID,
				GENERATED_CREDIT_NOTES_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(GENERATED_CREDIT_NOTES_REPORT_FILENAME)));
		
		// Formato de impresión de Inventario y Entradas/Salidas Simples
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					INVENTORY_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(INVENTORY_JASPER_REPORT_FILENAME)));
	
		// Informe de Cheques por caja
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						POSJOURNAL_CHECKS_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(POSJOURNAL_CHECKS_JASPER_REPORT_FILENAME)));
		
		// Informe de Cheques por Cuenta
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						CHECKS_BY_ACCOUNT_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(CHECKS_BY_ACCOUNT_JASPER_REPORT_FILENAME)));
	
		// Reporte de Totales por Entidad Financiera
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				TOTALES_POR_ENTIDAD_FINANCIERA_REPORT_UID,
				TOTALES_POR_ENTIDAD_FINANCIERA_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(TOTALES_POR_ENTIDAD_FINANCIERA_REPORT_FILENAME)));
		
		// Reporte de Totales por Entidad Financiera
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				POSJOURNAL_SALES_REPS_REPORT_UID,
				POSJOURNAL_SALES_REPS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(POSJOURNAL_SALES_REPS_REPORT_FILENAME)));
		
		// Informe de Declaración de Valores por Organización
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						DECLARACION_VALORES_X_ORG_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(DECLARACION_VALORES_X_ORG_JASPER_REPORT_FILENAME)));
		
		// Subreporte del Informe de Declaración de Valores por Organización
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						DECLARACION_VALORES_X_ORG_SUBREPORT_JASPER_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(DECLARACION_VALORES_X_ORG_SUBREPORT_JASPER_REPORT_FILENAME)));
		
		// Informe de Historia de Artículos
		String getID_HistoriaDeArticulos_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int historiaDeArticulos_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_HistoriaDeArticulos_FromUID, HISTORIA_DE_ARTICULOS_REPORT_UID);
		
		String getAttachment_HistoriaDeArticulos = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int historiaDeArticulos_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_HistoriaDeArticulos, MProcess.Table_ID, historiaDeArticulos_Process_Record_ID);
		
		if(historiaDeArticulos_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ historiaDeArticulos_Process_Record_ID, get_TrxName());
		}
		MAttachment att_HistoriaDeArticulos  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_HistoriaDeArticulos.setAD_Table_ID(MProcess.Table_ID);
		att_HistoriaDeArticulos.setRecord_ID(historiaDeArticulos_Process_Record_ID);
		att_HistoriaDeArticulos.addEntry("HistoriaDeArticulos.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(HISTORIA_DE_ARTICULOS_REPORT_FILENAME)));
		if(!att_HistoriaDeArticulos.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Informe de Reporte de Compras
		String getID_ReporteDeCompras_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int reporteDeCompras_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_ReporteDeCompras_FromUID, REPORTE_DE_COMPRAS_REPORT_UID);
		
		String getAttachment_ReporteDeCompras = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int reporteDeCompras_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_ReporteDeCompras, MProcess.Table_ID, reporteDeCompras_Process_Record_ID);
		
		if(reporteDeCompras_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ reporteDeCompras_Process_Record_ID, get_TrxName());
		}
		MAttachment att_ReporteDeCompras  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_ReporteDeCompras.setAD_Table_ID(MProcess.Table_ID);
		att_ReporteDeCompras.setRecord_ID(reporteDeCompras_Process_Record_ID);
		att_ReporteDeCompras.addEntry("ReporteDeCompras.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(REPORTE_DE_COMPRAS_REPORT_FILENAME)));
		if(!att_ReporteDeCompras.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Informe de Historia de Artículos por Mes / Semana
		String getID_HistoriaDeArticulosPorMesSemana_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int historiaDeArticulosPorMesSemana_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_HistoriaDeArticulosPorMesSemana_FromUID, HISTORIA_DE_ARTICULOS_MES_SEMANA_REPORT_UID);
		
		String getAttachment_HistoriaDeArticulosPorMesSemana = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int historiaDeArticulosPorMesSemana_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_HistoriaDeArticulosPorMesSemana, MProcess.Table_ID, historiaDeArticulosPorMesSemana_Process_Record_ID);
		
		if(historiaDeArticulosPorMesSemana_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ historiaDeArticulosPorMesSemana_Process_Record_ID, get_TrxName());
		}
		MAttachment att_HistoriaDeArticulosPorMesSemana  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_HistoriaDeArticulosPorMesSemana.setAD_Table_ID(MProcess.Table_ID);
		att_HistoriaDeArticulosPorMesSemana.setRecord_ID(historiaDeArticulosPorMesSemana_Process_Record_ID);
		att_HistoriaDeArticulosPorMesSemana.addEntry("HistoriaDeArticulosPorMesSemana.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(HISTORIA_DE_ARTICULOS_MES_SEMANA_REPORT_FILENAME)));
		if(!att_HistoriaDeArticulosPorMesSemana.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Informe de Historia de Costos
		String getID_HistoriaDeCostos_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int historiaDeCostos_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_HistoriaDeCostos_FromUID, HISTORIA_DE_COSTOS_REPORT_UID);
		
		String getAttachment_HistoriaDeCostos = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int historiaDeCostos_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_HistoriaDeCostos, MProcess.Table_ID, historiaDeCostos_Process_Record_ID);
		
		if(historiaDeCostos_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ historiaDeCostos_Attachment_Record_ID, get_TrxName());
		}
		MAttachment att_HistoriaDeCostos  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_HistoriaDeCostos.setAD_Table_ID(MProcess.Table_ID);
		att_HistoriaDeCostos.setRecord_ID(historiaDeCostos_Process_Record_ID);
		att_HistoriaDeCostos.addEntry("HistoriaDeCostos.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(HISTORIA_DE_COSTOS_REPORT_FILENAME)));
		if(!att_HistoriaDeCostos.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
				
		// Informe de Listado de Utilidades por Marca / Subfamilia / Familia / Línea / Proveedor
		String getID_ListadoUtilidadesConcepto_FromUID = " SELECT AD_Process_ID FROM AD_Process WHERE AD_ComponentObjectUID = ?";
		int listadoUtilidadesConcepto_Process_Record_ID = DB.getSQLValue(get_TrxName(), getID_ListadoUtilidadesConcepto_FromUID, LISTADO_DE_UTILIDADES_POR_CONCEPTO_REPORT_UID);
		
		String getAttachment_ListadoUtilidadesConcepto = "SELECT AD_Attachment_ID FROM AD_Attachment WHERE AD_Table_ID = ? AND Record_ID = ?";
		int listadoUtilidadesConcepto_Attachment_Record_ID = DB.getSQLValue(get_TrxName(), getAttachment_ListadoUtilidadesConcepto, MProcess.Table_ID, listadoUtilidadesConcepto_Process_Record_ID);
		
		if(listadoUtilidadesConcepto_Attachment_Record_ID > 0){
			DB.executeUpdate("DELETE FROM AD_Attachment WHERE AD_Table_ID = "+ MProcess.Table_ID +" AND Record_ID = "+ listadoUtilidadesConcepto_Process_Record_ID, get_TrxName());
		}
		MAttachment att_ListadoUtilidadesConcepto  = new MAttachment(getCtx(), 0, get_TrxName()); 
		att_ListadoUtilidadesConcepto.setAD_Table_ID(MProcess.Table_ID);
		att_ListadoUtilidadesConcepto.setRecord_ID(listadoUtilidadesConcepto_Process_Record_ID);
		att_ListadoUtilidadesConcepto.addEntry("Listado_de_Utilidades_por_Concepto.jrxml", JarHelper.readBinaryFromJar(jarFileURL,getBinaryFileURL(LISTADO_DE_UTILIDADES_POR_CONCEPTO_REPORT_FILENAME)));
		if(!att_ListadoUtilidadesConcepto.save()){
			throw new Exception ("Error al guardar jrxml ");
		}
		
		// Informe de Débitos y Créditos de Cliente
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CUSTOMER_DEBITS_CREDITS_REPORT_UID,
				CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME)));
		
		// Informe de Stock Valorizado
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				STOCK_VALUED_REPORT_UID,
				STOCK_VALUED_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(STOCK_VALUED_REPORT_FILENAME)));
		
		// Informe de Venta/Compra de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ENTIDADES_FINANCIERAS_SALES_PURCHASES_REPORT_UID,
				ENTIDADES_FINANCIERAS_SALES_PURCHASES_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ENTIDADES_FINANCIERAS_SALES_PURCHASES_REPORT_FILENAME)));
		
		// Informe de Auditoría de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ENTIDADES_FINANCIERAS_AUDIT_REPORT_UID,
				ENTIDADES_FINANCIERAS_AUDIT_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ENTIDADES_FINANCIERAS_AUDIT_REPORT_FILENAME)));
		
		// Informe de Cuenta Corriente de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ENTIDADES_FINANCIERAS_CURRENT_ACCOUNT_REPORT_UID,
				ENTIDADES_FINANCIERAS_CURRENT_ACCOUNT_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ENTIDADES_FINANCIERAS_CURRENT_ACCOUNT_REPORT_FILENAME)));
		
		// Informe de Saldos de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ENTIDADES_FINANCIERAS_BALANCES_REPORT_UID,
				ENTIDADES_FINANCIERAS_BALANCES_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ENTIDADES_FINANCIERAS_BALANCES_REPORT_FILENAME)));
		
		// Reporte de Movimientos Valorizados Detallado
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Informe de Saldos de Entidades Financieras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PHYSICAL_INVENTORY_AUDIT_REPORT_UID,
				PHYSICAL_INVENTORY_AUDIT_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PHYSICAL_INVENTORY_AUDIT_REPORT_FILENAME)));

		// Informe de vencimientos
		MJasperReport.updateBinaryData(get_TrxName(), getCtx(),
				VENCIMIENTOS_JASPER_REPORT_UID, JarHelper.readBinaryFromJar(
						jarFileURL,
						getBinaryFileURL(VENCIMIENTOS_JASPER_REPORT_FILENAME)));
		
		// Informe de Orden de Pago
		MJasperReport.updateBinaryData(get_TrxName(), getCtx(),
				ORDEN_PAGO_JASPER_REPORT_UID, JarHelper.readBinaryFromJar(
						jarFileURL,
						getBinaryFileURL(ORDEN_PAGO_JASPER_REPORT_FILENAME)));
						
		// Reporte Devoluciones a Proveedores
		MProcess.addAttachment(
				get_TrxName(), 
				getCtx(),
				DEVOLUCIONES_A_PROVEEDORES_REPORT_UID,
				DEVOLUCIONES_A_PROVEEDORES_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(DEVOLUCIONES_A_PROVEEDORES_REPORT_FILENAME)));
	
		// Reporte Ventas Por Subfamilia y Familia
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				VENTAS_POR_SUBFAMILIA_Y_FAMILIA_REPORT_UID,
				VENTAS_POR_SUBFAMILIA_Y_FAMILIA_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(VENTAS_POR_SUBFAMILIA_Y_FAMILIA_REPORT_FILENAME)));
		
		return " ";
	}

}
