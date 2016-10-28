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
		
		return " ";
	}
	
}
