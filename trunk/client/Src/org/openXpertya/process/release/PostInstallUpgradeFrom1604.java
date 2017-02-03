package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1604 extends PluginPostInstallProcess {
	
	/** Reporte de Cierre de Tarjetas */
	protected final static String CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010444";
	protected final static String DYNAMIC_CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010445";
	protected final static String CREDITCARD_CLOSE_REPORT_FILENAME = "CreditCardClose.jrxml";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados */
	protected final static String CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME = "CreditCardReport-DuplicateSubreport.jrxml";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos */
	protected final static String CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME = "CreditCardReport-InvalidSubreport.jrxml";
	
	/** Reporte de Auditoría de Cierre de Tarjetas */ 
	protected final static String CLOSING_AUDIT_COUPONCARDS_REPORT_UID = "CORE-AD_Process-1010446";
	protected final static String CLOSING_AUDIT_COUPONCARDS_REPORT_FILENAME = "ClosingAuditCouponCards.jrxml";
	
	/** UID del reporte del Orden de Pago*/
	protected final static String ORDEN_PAGO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String ORDEN_PAGO_JASPER_REPORT_FILENAME = "OrdenPago.jasper";
	
	/** Informe de Recibos de Cliente */
	protected final static String CUSTOMER_RECEIPT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000016";
	protected final static String CUSTOMER_RECEIPT_JASPER_REPORT_FILENAME = "ReciboCliente.jasper";
	
	/** Informe de Recibos de Cliente - ReciboCliente-OtrosMedios */
	protected final static String CUSTOMER_RECEIPT_OTHERPAYMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000019";
	protected final static String CUSTOMER_RECEIPT_OTHERPAYMENTS_JASPER_REPORT_FILENAME = "ReciboCliente_OtherPayments.jasper";
	
	/** Informe de Recibos de Cliente - ReciboCliente-NotasDeCredito */
	protected final static String CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010132";
	protected final static String CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_FILENAME = "ReciboCliente-NotasDeCredito.jasper";
	
	/** IVA Ventas General */
	protected final static String IVA_VENTA_GENERAL_REPORT_UID = "CORE-AD_Process-1010324";
	protected final static String IVA_VENTA_GENERAL_REPORT_FILENAME = "Iva_Ventas.jrxml";
	
	/** UID del Listado de Cupones de Tarjeta */
	protected final static String LISTADO_DE_CUPONES_DE_TARJETA_REPORT_UID = "CORE-AD_Process-1010405";
	protected final static String LISTADO_DE_CUPONES_DE_TARJETA_REPORT_FILENAME = "ListadoCuponesTarjeta.jrxml";
	
	/** UID del Reporte de Deudas de Cuentas Corrientes */
	protected final static String CURRENT_ACCOUNT_DEBTS_REPORT_UID = "CORE-AD_Process-1010394";
	protected final static String CURRENT_ACCOUNT_DEBTS_REPORT_FILENAME = "CurrentAccountDebts.jrxml";

	/** UID del Reporte de Consulta de Comprobantes Elcetronicos Emitidos */
	protected final static String WSFE_CONSULTA_COMPROBANTES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010133";
	protected final static String WSFE_CONSULTA_COMPROBANTES_JASPER_REPORT_FILENAME = "WSFEConsultaComprobante.jasper";
	
	/** UID del Reporte de Cheques Emitidos por Banco */
	protected final static String CHECKS_ISSUED_BY_BANK_REPORT_UID = "CORE-AD_Process-1010415";
	protected final static String CHECKS_ISSUED_BY_BANK_REPORT_FILENAME = "ChecksIssuedByBank.jrxml";
	
	/** Listado de OC Vencidas o Sin Novedades */
	protected final static String PURCHASE_ORDER_DUE_REPORT_UID = "CORE-AD_Process-1010432";
	protected final static String PURCHASE_ORDER_DUE_REPORT_FILENAME = "ListOfPurchaseOrdersDue.jrxml";
	
	/** UID del Informe de Relacion OC-Remito-Factura */
	protected final static String TRAZABILIDAD_DE_DOCUMENTOS_REPORT_UID = "CORE-AD_Process-1010430";
	protected final static String TRAZABILIDAD_DE_DOCUMENTOS_REPORT_FILENAME = "TrazabilidadDeDocumentos.jrxml";
	
	/** UID del Reporte de cheques sin Conciliar */
	protected final static String CHEQUES_SIN_CONCILIAR_REPORT_UID = "CORE-AD_Process-1010424";
	protected final static String CHEQUES_SIN_CONCILIAR_REPORT_FILENAME = "UnreconciledCheksReport.jrxml";
	
	/** UID del Listado de Control de Facturas sin Remitos */
	protected final static String INVOICES_WITHOUT_INOUT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010125";
	protected final static String INVOICES_WITHOUT_INOUT_JASPER_REPORT_FILENAME = "InvoicesWithoutInOut.jasper";

	/** UID del Reporte de libro diario */
	protected final static String LIBRO_DIARIO_REPORT_UID = "CORE-AD_Process-1010379";
	protected final static String LIBRO_DIARIO_REPORT_FILENAME = "LibroDiario.jrxml";
	
	/** UID del Reporte de libro diario resumido */
	protected final static String LIBRO_DIARIO_RESUMIDO_REPORT_UID = "CORE-AD_Process-1010428";
	protected final static String LIBRO_DIARIO_RESUMIDO_REPORT_FILENAME = "LibroDiarioResumido.jrxml";

	/** UID Impresión Cheques */
	protected final static String CHECK_PRINTING_JASPER_REPORT_UID = "SSTE2CORE-AD_JasperReport-1010144-20161018155925";
	protected final static String CHECK_PRINTING_JASPER_REPORT_FILENAME = "ChequesFrances.jasper";
	
	/** UID Informe de Lote de Pagos */
	protected final static String PAYMENT_BATCH_JASPER_REPORT_UID = "SSTE2CORE-AD_JasperReport-1010171-20161024123925";
	protected final static String PAYMENT_BATCH_JASPER_REPORT_FILENAME = "InformeLoteDePagos.jasper";

	/** UID Subreporte Informe de Lote de Pagos */
	protected final static String PAYMENT_BATCH_SUBREPORT_JASPER_REPORT_UID = "SSTE2CORE-AD_JasperReport-1010170-20161024123925";
	protected final static String PAYMENT_BATCH_SUBREPORT_JASPER_REPORT_FILENAME = "InformeLoteDePagos_subreport.jasper";
	
	/** Listado de OC */
	protected final static String PURCHASE_ORDER_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String PURCHASE_ORDER_REPORT_FILENAME = "PurchaseOrderReport.jrxml";
	
	/** Reporte de Recepciones de Proveedor */
	protected final static String RECEPTIONS_VENDOR_REPORT_UID = "CORE-AD_Process-1010431";
	protected final static String RECEPTIONS_VENDOR_REPORT_FILENAME = "ReceptionsVendor.jrxml";
	
	/** UID del Informe de Débitos y Créditos de Cliente */
	protected final static String CUSTOMER_DEBITS_CREDITS_REPORT_UID = "CORE-AD_Process-1010346";
	protected final static String CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME = "CustomerDebitsCredits.jrxml";
	
	/** UID del Informe DE Auditoría de Creación de Línea de Caja */
	protected final static String CREATE_CASHLINE_AUDIT_REPORT_UID = "CORE-AD_Process-1010500";
	protected final static String CREATE_CASHLINE_AUDIT_REPORT_FILENAME = "CreateCashLineAudit.jrxml";
	
	/** UID del Informe de Cheques por Cuenta */
	protected final static String CHECKS_BY_ACCOUNT_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010104";
	protected final static String CHECKS_BY_ACCOUNT_JASPER_REPORT_FILENAME = "ChecksByAccount.jasper";
	
	/** UID del Reporte de Remitos */
	protected final static String IN_OUT_REPORT_UID = "CORE-AD_Process-1010422";
	protected final static String IN_OUT_REPORT_FILENAME = "InOutReport.jrxml";
	
	/** UID del Informe de Ranking de Ventas */
	protected final static String SALES_RANKING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010099";
	protected final static String SALES_RANKING_JASPER_REPORT_FILENAME = "SalesRanking.jasper";
	
	/** UID del Informe de Cambio de Precios */
	protected final static String PRICE_CHANGING_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010121";
	protected final static String PRICE_CHANGING_JASPER_REPORT_FILENAME = "PriceChanging.jasper";

	/** UID del Listado de Cupones Totalizados por Estado */
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010181-20170125125731";
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME = "CouponListByStatus.jasper";
	
	/** UID del Reporte de Control de Tarjetas */
	protected final static String CARD_CONTROL_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010177-20170125125355";
	protected final static String CARD_CONTROL_JASPER_REPORT_FILENAME = "CardControl.jasper";
	
	/** UID del Listado de Liquidaciones */
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010180-20170125125715";
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_FILENAME = "SettlementList.jasper";
	
	/** UID del informe Reporte de Compras*/
	protected final static String REPORTE_DE_COMPRAS_REPORT_UID = "CORE-AD_Process-1010342";
	protected final static String REPORTE_DE_COMPRAS_REPORT_FILENAME = "ReporteDeCompras.jrxml";
	
	/** UID del informe de Movimientos Valorizados */
	protected final static String VALUED_MOVEMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010102";
	protected final static String VALUED_MOVEMENTS_JASPER_REPORT_FILENAME = "ValuedMovements.jasper";
	
	/** UID del informe de Movimientos Valorizados Detallado */
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010117";
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME = "ValuedMovementsDetail.jasper";
	
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
		
		// Reporte de Cierre de Tarjetas
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME)));
		
		// Reporte de Auditoría de Cierre de Tarjetas
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CLOSING_AUDIT_COUPONCARDS_REPORT_UID,
				CLOSING_AUDIT_COUPONCARDS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CLOSING_AUDIT_COUPONCARDS_REPORT_FILENAME)));
		
		// Informe de Orden de Pago
		MJasperReport.updateBinaryData(get_TrxName(), getCtx(),
				ORDEN_PAGO_JASPER_REPORT_UID, JarHelper.readBinaryFromJar(
						jarFileURL,
						getBinaryFileURL(ORDEN_PAGO_JASPER_REPORT_FILENAME)));
		
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
		
		// Actualización del reporte de Recibos de cliente - Otros Medios
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CUSTOMER_RECEIPT_OTHERPAYMENTS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CUSTOMER_RECEIPT_OTHERPAYMENTS_JASPER_REPORT_FILENAME)));
		
		// Reporte de Recibos de cliente - Notas de Crédito
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_FILENAME)));
		
		// Actualización del reporte Iva Ventas
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				IVA_VENTA_GENERAL_REPORT_UID,
				IVA_VENTA_GENERAL_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(IVA_VENTA_GENERAL_REPORT_FILENAME)));
		
		// Listado de Cupones de Tarjeta
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				LISTADO_DE_CUPONES_DE_TARJETA_REPORT_UID,
				LISTADO_DE_CUPONES_DE_TARJETA_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(LISTADO_DE_CUPONES_DE_TARJETA_REPORT_FILENAME)));
		
		// Reporte de Deudas de Cuentas Corrientes
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CURRENT_ACCOUNT_DEBTS_REPORT_UID,
				CURRENT_ACCOUNT_DEBTS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CURRENT_ACCOUNT_DEBTS_REPORT_FILENAME)));

		// Reporte de consulta de comprobantes electronicos emitidos
		MJasperReport
		.updateBinaryData(
				get_TrxName(),
				getCtx(),
				WSFE_CONSULTA_COMPROBANTES_JASPER_REPORT_UID,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(WSFE_CONSULTA_COMPROBANTES_JASPER_REPORT_FILENAME)));

		// Reporte de Cheques Emitidos por Banco
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CHECKS_ISSUED_BY_BANK_REPORT_UID,
				CHECKS_ISSUED_BY_BANK_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CHECKS_ISSUED_BY_BANK_REPORT_FILENAME)));
		
		// Listado de OC Vencidas o Sin Novedades
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PURCHASE_ORDER_DUE_REPORT_UID,
				PURCHASE_ORDER_DUE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PURCHASE_ORDER_DUE_REPORT_FILENAME)));
		
		// Informe de Relacion OC-Remito-Factura
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				TRAZABILIDAD_DE_DOCUMENTOS_REPORT_UID,
				TRAZABILIDAD_DE_DOCUMENTOS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(TRAZABILIDAD_DE_DOCUMENTOS_REPORT_FILENAME)));
		
		// Reporte de cheques sin Conciliar
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CHEQUES_SIN_CONCILIAR_REPORT_UID,
				CHEQUES_SIN_CONCILIAR_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CHEQUES_SIN_CONCILIAR_REPORT_FILENAME)));
		
		// Informe de Control de Facturas Sin Remito
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					INVOICES_WITHOUT_INOUT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(INVOICES_WITHOUT_INOUT_JASPER_REPORT_FILENAME)));

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
		
		// Reporte de Libro Diario Resumido
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				LIBRO_DIARIO_RESUMIDO_REPORT_UID,
				LIBRO_DIARIO_RESUMIDO_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(LIBRO_DIARIO_RESUMIDO_REPORT_FILENAME)));

		// Impresión de cheques - Francés
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CHECK_PRINTING_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CHECK_PRINTING_JASPER_REPORT_FILENAME)));
		
		// Informe Lote de Pagos
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					PAYMENT_BATCH_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(PAYMENT_BATCH_JASPER_REPORT_FILENAME)));
		
		// Informe Lote de Pagos - Subreporte
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					PAYMENT_BATCH_SUBREPORT_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(PAYMENT_BATCH_SUBREPORT_JASPER_REPORT_FILENAME)));
		
		// Listado de OC
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PURCHASE_ORDER_REPORT_UID,
				PURCHASE_ORDER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PURCHASE_ORDER_REPORT_FILENAME)));
		
		// Reporte de Recepciones de Proveedor
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				RECEPTIONS_VENDOR_REPORT_UID,
				RECEPTIONS_VENDOR_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(RECEPTIONS_VENDOR_REPORT_FILENAME)));
		
		// Informe de Débitos y Créditos de Clientes
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CUSTOMER_DEBITS_CREDITS_REPORT_UID,
				CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CUSTOMER_DEBITS_CREDITS_REPORT_FILENAME)));
		
		// Informe de Auditoría de Creación de Línea de Caja
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREATE_CASHLINE_AUDIT_REPORT_UID,
				CREATE_CASHLINE_AUDIT_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREATE_CASHLINE_AUDIT_REPORT_FILENAME)));
		
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
		
		// Reporte de Remitos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				IN_OUT_REPORT_UID,
				IN_OUT_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(IN_OUT_REPORT_FILENAME)));
		
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
		
		// Informe de Cambio de Precios
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					PRICE_CHANGING_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(PRICE_CHANGING_JASPER_REPORT_FILENAME)));
		
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

		// Reporte de Control de Tarjetas
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CARD_CONTROL_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CARD_CONTROL_JASPER_REPORT_FILENAME)));
		
		// Listado de Liquidaciones
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					SETTLEMENT_LIST_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(SETTLEMENT_LIST_JASPER_REPORT_FILENAME)));
		
		// Reporte de Compras
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				REPORTE_DE_COMPRAS_REPORT_UID,
				REPORTE_DE_COMPRAS_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(REPORTE_DE_COMPRAS_REPORT_FILENAME)));
		
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
		
		return " ";
	}
	
}
