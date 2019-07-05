package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.util.DB;
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
	
	/** UID del Listado de Cupones Totalizados por Estado */
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010181-20170125125731";
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME = "CouponListByStatus.jasper";
	
	/** UID del Informe de Detalle de Margen Negativo */
	protected final static String NEGATIVE_MARGIN_DETAIL_JASPER_REPORT_UID = "T0072CORE-AD_Process-1010617-20190206133339";
	protected final static String NEGATIVE_MARGIN_DETAIL_JASPER_REPORT_FILENAME = "NegativeMarginDetail.jrxml";
	
	/** UID del informe de Movimientos Valorizados Detallado */
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010117";
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME = "ValuedMovementsDetail.jasper";
	
	/** UID del Informe de Pedidos por Línea de Artículo */
	protected final static String ORDERS_FOR_PRODUCT_LINES_REPORT_JASPER_REPORT_UID = "TA712CORE-AD_Process-1010617-20190206172121";
	protected final static String ORDERS_FOR_PRODUCT_LINES_REPORT_JASPER_REPORT_FILENAME = "OrdersForProductLines.jrxml";

	/** UID del Reporte de libro diario */
	protected final static String LIBRO_DIARIO_REPORT_UID = "CORE-AD_Process-1010379";
	protected final static String LIBRO_DIARIO_REPORT_FILENAME = "LibroDiario.jrxml";
	
	/** UID del Informe de Control de Ventas y Cierres Z */
	protected final static String VENTAS_CIERRES_Z_REPORT_JASPER_REPORT_UID = "TACC2CORE-AD_Process-1010626-20190401164139";
	protected final static String VENTAS_CIERRES_Z_REPORT_JASPER_REPORT_FILENAME = "ControlVentasXCierreZ.jrxml";
	
	/** UID del Informe de Ventas por Financiación */
	protected final static String FINANCIAL_SALES_REPORT_JASPER_REPORT_UID = "T0082CORE-AD_Process-1010627-20190502102158";
	protected final static String FINANCIAL_SALES_REPORT_JASPER_REPORT_FILENAME = "FinancialSales.jrxml";

	/** UID del Informe de Ventas por Subfamilia y Familia */
	protected final static String SALES_CATEGORY_GAMAS_REPORT_JASPER_REPORT_UID = "CORE-AD_Process-1010374";
	protected final static String SALES_CATEGORY_GAMAS_REPORT_JASPER_REPORT_FILENAME = "SalesByCategoryAndGamas.jrxml";
	
	/** UID del Informe de Cobranzas y Pagos */
	protected final static String INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_UID = "CORE-AD_Process-1010401";
	protected final static String INFORME_DE_COBRANZAS_Y_PAGOS_REPORT_FILENAME = "InformeDeCobranzasYPagos.jrxml";
	
	/** ======================================================================================= */
	/** === REPORTES PRECOMPILADOS PARA COMPATIBILIDAD VERSIONES JRE Y SIN NECESIDAD DE JDK === */
	/** ======================================================================================= */
	
	protected final static String AdvancedAllocationsReport_FILENAME = "AdvancedAllocationsReport.jasper";
	protected final static String AdvancedAllocationsReport_UID = "CORE-AD_Process-1010550";
	
	protected final static String BankListPrint_FILENAME = "BankListPrint.jasper";
	protected final static String BankListPrint_UID = "CORE-AD_Process-1010549";
	
	protected final static String BankStatementReport_FILENAME = "BankStatementReport.jasper";
	protected final static String BankStatementReport_UID = "CORE-AD_Process-1010576";
	
	protected final static String BoletaDepositoChecks_FILENAME = "BoletaDepositoChecks.jasper";
	protected final static String BoletaDepositoChecks_UID = "CORE-AD_Process-1010575";
	
	protected final static String ChangeInvoiceDataAudit_FILENAME = "ChangeInvoiceDataAudit.jasper";
	protected final static String ChangeInvoiceDataAudit_UID = "CORE-AD_Process-1010414";
	
	protected final static String ChecksIssuedByBank_FILENAME = "ChecksIssuedByBank.jasper";
	protected final static String ChecksIssuedByBank_UID = "CORE-AD_Process-1010415";
	
	protected final static String ClosingAuditCouponCards_FILENAME = "ClosingAuditCouponCards.jasper";
	protected final static String ClosingAuditCouponCards_UID = "CORE-AD_Process-1010446";
	
	protected final static String ComprasPorRegion_FILENAME = "ComprasPorRegion.jasper";
	protected final static String ComprasPorRegion_UID = "CORE-AD_Process-1010400";
	
	protected final static String ControlVentasXCierreZ_FILENAME = "ControlVentasXCierreZ.jasper";
	protected final static String ControlVentasXCierreZ_UID = "TACC2CORE-AD_Process-1010626-20190401164139";
	
	protected final static String CreateCashLineAudit_FILENAME = "CreateCashLineAudit.jasper";
	protected final static String CreateCashLineAudit_UID = "CORE-AD_Process-1010500";
	
	protected final static String CreditCardClose_FILENAME = "CreditCardClose.jasper";
	protected final static String CreditCardCloseDynamic_UID = "CORE-AD_Process-1010445";
	protected final static String CreditCardClose_UID = "CORE-AD_Process-1010444";
	
	protected final static String CurrentAccountDebts_FILENAME = "CurrentAccountDebts.jasper";
	protected final static String CurrentAccountDebts_UID = "CORE-AD_Process-1010394";
	
	protected final static String CustomerAudit_FILENAME = "CustomerAudit.jasper";
	protected final static String CustomerAudit_UID = "CORE-AD_Process-1010362";
	
	protected final static String CustomerDebitsCredits_FILENAME = "CustomerDebitsCredits.jasper";
	protected final static String CustomerDebitsCredits_UID = "CORE-AD_Process-1010346";
	
	protected final static String EntidadFinancieraAudit_FILENAME = "EntidadFinancieraAudit.jasper";
	protected final static String EntidadFinancieraAudit_UID = "CORE-AD_Process-1010359";
	
	protected final static String EntidadFinancieraBalances_FILENAME = "EntidadFinancieraBalances.jasper";
	protected final static String EntidadFinancieraBalances_UID = "CORE-AD_Process-1010361";
	
	protected final static String EntidadFinancieraCuentaCorriente_FILENAME = "EntidadFinancieraCuentaCorriente.jasper";
	protected final static String EntidadFinancieraCuentaCorriente_UID = "CORE-AD_Process-1010360";
	
	protected final static String EntidadFinancieraSalesPurchases_FILENAME = "EntidadFinancieraSalesPurchases.jasper";
	protected final static String EntidadFinancieraSalesPurchases_UID = "CORE-AD_Process-1010358";
	
	protected final static String FinancialSales_FILENAME = "FinancialSales.jasper";
	protected final static String FinancialSales_UID = "T0082CORE-AD_Process-1010627-20190502102158";
	
	protected final static String GeneratedCreditNotes_FILENAME = "GeneratedCreditNotes.jasper";
	protected final static String GeneratedCreditNotes_UID = "CORE-AD_Process-1010330";
	
	protected final static String HistoriaDeArticulos_FILENAME = "HistoriaDeArticulos.jasper";
	protected final static String HistoriaDeArticulos_UID = "CORE-AD_Process-1010341";
	
	protected final static String HistoriaDeArticulosPorMesSemana_FILENAME = "HistoriaDeArticulosPorMesSemana.jasper";
	protected final static String HistoriaDeArticulosPorMesSemana_UID = "CORE-AD_Process-1010343";
	
	protected final static String HistoriaDeCostos_FILENAME = "HistoriaDeCostos.jasper";
	protected final static String HistoriaDeCostos_UID = "CORE-AD_Process-1010344";
	
	protected final static String InformeDeCobranzasYPagos_FILENAME = "InformeDeCobranzasYPagos.jasper";
	protected final static String InformeDeCobranzasYPagos_UID = "CORE-AD_Process-1010401";
	
	protected final static String InOutReport_FILENAME = "InOutReport.jasper";
	protected final static String InOutReport_UID = "CORE-AD_Process-1010422";
	
	protected final static String InvoicesByCreditCardOwner_FILENAME = "InvoicesByCreditCardOwner.jasper";
	protected final static String InvoicesByCreditCardOwner_UID = "CORE-AD_Process-1010381";
	
	protected final static String Iva_Ventas_FILENAME = "Iva_Ventas.jasper";
	protected final static String Iva_Ventas_UID = "CORE-AD_Process-1010324";
	
	protected final static String LibroDiario_FILENAME = "LibroDiario.jasper";
	protected final static String LibroDiario_UID = "CORE-AD_Process-1010379";
	
	protected final static String LibroDiarioResumido_FILENAME = "LibroDiarioResumido.jasper";
	protected final static String LibroDiarioResumido_UID = "CORE-AD_Process-1010428";

	protected final static String ListadoCuponesTarjeta_FILENAME = "ListadoCuponesTarjeta.jasper";
	protected final static String ListadoCuponesTarjeta_UID = "CORE-AD_Process-1010405";
	
	protected final static String Listado_de_Utilidades_por_Concepto_FILENAME = "Listado_de_Utilidades_por_Concepto.jasper";
	protected final static String Listado_de_Utilidades_por_Concepto_UID = "CORE-AD_Process-1010345";
	
	protected final static String ListOfPurchaseOrdersDue_FILENAME = "ListOfPurchaseOrdersDue.jasper";
	protected final static String ListOfPurchaseOrdersDue_UID = "CORE-AD_Process-1010432";
	
	protected final static String NegativeMarginDetail_FILENAME = "NegativeMarginDetail.jasper";
	protected final static String NegativeMarginDetail_UID = "T0072CORE-AD_Process-1010617-20190206133339";
	
	protected final static String OPAnticipadas_FILENAME = "OPAnticipadas.jasper";
	protected final static String OPAnticipadas_UID = "CORE-AD_Process-1010429";
	
	protected final static String OrdersForProductLines_FILENAME = "OrdersForProductLines.jasper";
	protected final static String OrdersForProductLines_UID = "TA712CORE-AD_Process-1010617-20190206172121";
	
	protected final static String OrderStatus_FILENAME = "OrderStatus.jasper";
	protected final static String OrderStatus_UID = "CORE-AD_Process-1010615";
	
	protected final static String PhysicalInventoryAudit_FILENAME = "PhysicalInventoryAudit.jasper";
	protected final static String PhysicalInventoryAudit_UID = "CORE-AD_Process-1010369";
	
	protected final static String POSJournalSalesReps_FILENAME = "POSJournalSalesReps.jasper";
	protected final static String POSJournalSalesReps_UID = "CORE-AD_Process-1010339";
	
	protected final static String PrintFormatforJornalManuals_FILENAME = "PrintFormatforJornalManuals.jasper";
	protected final static String PrintFormatforJornalManuals_UID = "CORE-AD_Process-1010427";
	
	protected final static String PurchaseOrderReport_FILENAME = "PurchaseOrderReport.jasper";
	protected final static String PurchaseOrderReport_UID = "CORE-AD_Process-1010433";
	
	protected final static String PurchaseOrderWithInOutWithOutInvoice_FILENAME = "PurchaseOrderWithInOutWithOutInvoice.jasper";
	protected final static String PurchaseOrderWithInOutWithOutInvoice_UID = "CORE-AD_Process-1010426";
	
	protected final static String ReceptionsVendor_FILENAME = "ReceptionsVendor.jasper";
	protected final static String ReceptionsVendor_UID = "CORE-AD_Process-1010431";
	
	protected final static String ReportAuthorizedInvoiceToPay_FILENAME = "ReportAuthorizedInvoiceToPay.jasper";
	protected final static String ReportAuthorizedInvoiceToPay_UID = "CORE-AD_Process-1010421";
	
	protected final static String ReporteDeCompras_FILENAME = "ReporteDeCompras.jasper";
	protected final static String ReporteDeCompras_UID = "CORE-AD_Process-1010342";
	
	protected final static String report_return_to_vendor_FILENAME = "report_return_to_vendor.jasper";
	protected final static String report_return_to_vendor_UID = "CORE-AD_Process-1010373";
	
	protected final static String report_sales_by_province_FILENAME = "report_sales_by_province.jasper";
	protected final static String report_sales_by_province_UID = "CORE-AD_Process-1010380";
	
	protected final static String SalesByCategoryAndGamas_FILENAME = "SalesByCategoryAndGamas.jasper";
	protected final static String SalesByCategoryAndGamas_UID = "CORE-AD_Process-1010374";
	
	protected final static String SalesDiscounts_FILENAME = "SalesDiscounts.jasper";
	protected final static String SalesDiscounts_UID = "CORE-AD_Process-1010608";
	
	protected final static String SalesRecharges_FILENAME = "SalesRecharges.jasper";
	protected final static String SalesRecharges_UID = "CORE-AD_Process-1010607";
	
	protected final static String SettlementListDetailed_FILENAME = "SettlementListDetailed.jasper";
	protected final static String SettlementListDetailed_UID = "RPRT2CORE-AD_Process-1010526-20170210194428";
	
	protected final static String StockValorizadoAFecha_FILENAME = "StockValorizadoAFecha.jasper";
	protected final static String StockValorizadoAFecha_UID = "CORE-AD_Process-1010327";
	
	protected final static String StockValued_FILENAME = "StockValued.jasper";
	protected final static String StockValued_UID = "CORE-AD_Process-1010347";
	
	protected final static String TotalesPorEntidadFinanciera_FILENAME = "TotalesPorEntidadFinanciera.jasper";
	protected final static String TotalesPorEntidadFinanciera_UID = "CORE-AD_Process-1010338";
	
	protected final static String TrazabilidadDeDocumentos_FILENAME = "TrazabilidadDeDocumentos.jasper";
	protected final static String TrazabilidadDeDocumentos_UID = "CORE-AD_Process-1010430";
	
	protected final static String UnreconciledCheksReport_FILENAME = "UnreconciledCheksReport.jasper";
	protected final static String UnreconciledCheksReport_UID = "CORE-AD_Process-1010424";
	
	protected final static String UnreconciledPaymentsDetailed_FILENAME = "UnreconciledPaymentsDetailed.jasper";
	protected final static String UnreconciledPaymentsDetailed_UID = "CORE-AD_Process-1010577";
	
	protected final static String UpdatedDiscountSchemas_FILENAME = "UpdatedDiscountSchemas.jasper";
	protected final static String UpdatedDiscountSchemas_UID = "CORE-AD_Process-1010392";
	
	protected final static String UtilidadesPorArticulo_FILENAME = "UtilidadesPorArticulo.jasper";
	protected final static String UtilidadesPorArticulo_UID = "CORE-AD_Process-1010328";
	
	protected final static String VentasPorRegion_FILENAME = "VentasPorRegion.jasper";
	protected final static String VentasPorRegion_UID = "CORE-AD_Process-1010399";
	
	/** =========================================================================================== */
	/** === FIN REPORTES PRECOMPILADOS PARA COMPATIBILIDAD VERSIONES JRE Y SIN NECESIDAD DE JDK === */
	/** =========================================================================================== */
	
	
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
		
		// Listado de Cupones Totalizados por Estado
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					COUPON_LIST_BY_STATUS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME)));
		
		// Detalle de Margen Negativo
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				NEGATIVE_MARGIN_DETAIL_JASPER_REPORT_UID,
				NEGATIVE_MARGIN_DETAIL_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(NEGATIVE_MARGIN_DETAIL_JASPER_REPORT_FILENAME)));
		
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
		
		// Pedidos por Línea de Artículo
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				ORDERS_FOR_PRODUCT_LINES_REPORT_JASPER_REPORT_UID,
				ORDERS_FOR_PRODUCT_LINES_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(ORDERS_FOR_PRODUCT_LINES_REPORT_JASPER_REPORT_FILENAME)));

		// Reporte de Libro Diario
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				LIBRO_DIARIO_REPORT_UID,
				LIBRO_DIARIO_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(LIBRO_DIARIO_REPORT_FILENAME)));
		
		// Reporte Control de Ventas y Cierres Z
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				VENTAS_CIERRES_Z_REPORT_JASPER_REPORT_UID,
				VENTAS_CIERRES_Z_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(VENTAS_CIERRES_Z_REPORT_JASPER_REPORT_FILENAME)));

		// Informe de Ventas por Financiación
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				FINANCIAL_SALES_REPORT_JASPER_REPORT_UID,
				FINANCIAL_SALES_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(FINANCIAL_SALES_REPORT_JASPER_REPORT_FILENAME)));

		// Informe de Ventas por Subfamilia y Familia
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				SALES_CATEGORY_GAMAS_REPORT_JASPER_REPORT_UID,
				SALES_CATEGORY_GAMAS_REPORT_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(SALES_CATEGORY_GAMAS_REPORT_JASPER_REPORT_FILENAME)));
		
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
		
		/** ======================================================================================= */
		/** === REPORTES PRECOMPILADOS PARA COMPATIBILIDAD VERSIONES JRE Y SIN NECESIDAD DE JDK === */
		/** ======================================================================================= */

		updateAttachment(AdvancedAllocationsReport_UID, AdvancedAllocationsReport_FILENAME);
		updateAttachment(BankListPrint_UID, BankListPrint_FILENAME);
		updateAttachment(BankStatementReport_UID, BankStatementReport_FILENAME);
		updateAttachment(BoletaDepositoChecks_UID, BoletaDepositoChecks_FILENAME);
		updateAttachment(ChangeInvoiceDataAudit_UID, ChangeInvoiceDataAudit_FILENAME);
		updateAttachment(ChecksIssuedByBank_UID, ChecksIssuedByBank_FILENAME);
		updateAttachment(ClosingAuditCouponCards_UID, ClosingAuditCouponCards_FILENAME);
		updateAttachment(ComprasPorRegion_UID, ComprasPorRegion_FILENAME);
		updateAttachment(ControlVentasXCierreZ_UID, ControlVentasXCierreZ_FILENAME);
		updateAttachment(CreateCashLineAudit_UID, CreateCashLineAudit_FILENAME);
		updateAttachment(CreditCardCloseDynamic_UID, CreditCardClose_FILENAME);
		updateAttachment(CreditCardClose_UID, CreditCardClose_FILENAME);
		updateAttachment(CurrentAccountDebts_UID, CurrentAccountDebts_FILENAME);
		updateAttachment(CustomerAudit_UID, CustomerAudit_FILENAME);
		updateAttachment(CustomerDebitsCredits_UID, CustomerDebitsCredits_FILENAME);
		updateAttachment(EntidadFinancieraAudit_UID, EntidadFinancieraAudit_FILENAME);
		updateAttachment(EntidadFinancieraBalances_UID, EntidadFinancieraBalances_FILENAME);
		updateAttachment(EntidadFinancieraCuentaCorriente_UID, EntidadFinancieraCuentaCorriente_FILENAME);
		updateAttachment(EntidadFinancieraSalesPurchases_UID, EntidadFinancieraSalesPurchases_FILENAME);
		updateAttachment(FinancialSales_UID, FinancialSales_FILENAME);
		updateAttachment(GeneratedCreditNotes_UID, GeneratedCreditNotes_FILENAME);
		updateAttachment(HistoriaDeArticulos_UID, HistoriaDeArticulos_FILENAME);
		updateAttachment(HistoriaDeArticulosPorMesSemana_UID, HistoriaDeArticulosPorMesSemana_FILENAME);
		updateAttachment(HistoriaDeCostos_UID, HistoriaDeCostos_FILENAME);
		updateAttachment(InformeDeCobranzasYPagos_UID, InformeDeCobranzasYPagos_FILENAME);
		updateAttachment(InOutReport_UID, InOutReport_FILENAME);
		updateAttachment(InvoicesByCreditCardOwner_UID, InvoicesByCreditCardOwner_FILENAME);
		updateAttachment(Iva_Ventas_UID, Iva_Ventas_FILENAME);
		updateAttachment(LibroDiario_UID, LibroDiario_FILENAME);
		updateAttachment(LibroDiarioResumido_UID, LibroDiarioResumido_FILENAME);
		updateAttachment(ListadoCuponesTarjeta_UID, ListadoCuponesTarjeta_FILENAME);
		updateAttachment(Listado_de_Utilidades_por_Concepto_UID, Listado_de_Utilidades_por_Concepto_FILENAME);
		updateAttachment(ListOfPurchaseOrdersDue_UID, ListOfPurchaseOrdersDue_FILENAME);
		updateAttachment(NegativeMarginDetail_UID, NegativeMarginDetail_FILENAME);
		updateAttachment(OPAnticipadas_UID, OPAnticipadas_FILENAME);
		updateAttachment(OrdersForProductLines_UID, OrdersForProductLines_FILENAME);
		updateAttachment(OrderStatus_UID, OrderStatus_FILENAME);
		updateAttachment(PhysicalInventoryAudit_UID, PhysicalInventoryAudit_FILENAME);
		updateAttachment(POSJournalSalesReps_UID, POSJournalSalesReps_FILENAME);
		updateAttachment(PrintFormatforJornalManuals_UID, PrintFormatforJornalManuals_FILENAME);
		updateAttachment(PurchaseOrderReport_UID, PurchaseOrderReport_FILENAME);
		updateAttachment(PurchaseOrderWithInOutWithOutInvoice_UID, PurchaseOrderWithInOutWithOutInvoice_FILENAME);
		updateAttachment(ReceptionsVendor_UID, ReceptionsVendor_FILENAME);
		updateAttachment(ReportAuthorizedInvoiceToPay_UID, ReportAuthorizedInvoiceToPay_FILENAME);
		updateAttachment(ReporteDeCompras_UID, ReporteDeCompras_FILENAME);
		updateAttachment(report_return_to_vendor_UID, report_return_to_vendor_FILENAME);
		updateAttachment(report_sales_by_province_UID, report_sales_by_province_FILENAME);
		updateAttachment(SalesByCategoryAndGamas_UID, SalesByCategoryAndGamas_FILENAME);
		updateAttachment(SalesDiscounts_UID, SalesDiscounts_FILENAME);
		updateAttachment(SalesRecharges_UID, SalesRecharges_FILENAME);
		updateAttachment(SettlementListDetailed_UID, SettlementListDetailed_FILENAME);
		updateAttachment(StockValorizadoAFecha_UID, StockValorizadoAFecha_FILENAME);
		updateAttachment(StockValued_UID, StockValued_FILENAME);
		updateAttachment(TotalesPorEntidadFinanciera_UID, TotalesPorEntidadFinanciera_FILENAME);
		updateAttachment(TrazabilidadDeDocumentos_UID, TrazabilidadDeDocumentos_FILENAME);
		updateAttachment(UnreconciledCheksReport_UID, UnreconciledCheksReport_FILENAME);
		updateAttachment(UnreconciledPaymentsDetailed_UID, UnreconciledPaymentsDetailed_FILENAME);
		updateAttachment(UpdatedDiscountSchemas_UID, UpdatedDiscountSchemas_FILENAME);
		updateAttachment(UtilidadesPorArticulo_UID, UtilidadesPorArticulo_FILENAME);
		updateAttachment(VentasPorRegion_UID, VentasPorRegion_FILENAME);
		
		/** =========================================================================================== */
		/** === FIN REPORTES PRECOMPILADOS PARA COMPATIBILIDAD VERSIONES JRE Y SIN NECESIDAD DE JDK === */
		/** =========================================================================================== */
		
		return " ";
	}
	
	
	/**
	 * Actualiza los adjuntos de los informes relacionados con los reportes dinamicos unicamente si los .jasper existen en el jar 
	 */
	protected void updateAttachment(String processUID, String fileName) throws Exception {
		// leer el contenido del archivo
		byte[] report = JarHelper.readBinaryFromJar(jarFileURL, getBinaryFileURL(fileName));
		// si el informe .jasper se encuentra incluido, entonces actualizar el adjunto y la referencia desde el proceso
		if (report != null && report.length > 0) {
			MProcess.addAttachment(get_TrxName(), getCtx(),	processUID, fileName, report);
			DB.executeUpdate("UPDATE AD_Process SET jasperreport = 'attachment:" + fileName + "' WHERE AD_ComponentObjectUID = '" + processUID + "'", get_TrxName());
		}
	}
}
