package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1705 extends PluginPostInstallProcess {

	/** UID del Listado de Cupones Totalizados por Estado */
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010181-20170125125731";
	protected final static String COUPON_LIST_BY_STATUS_JASPER_REPORT_FILENAME = "CouponListByStatus.jasper";
	
	/** UID del Listado de Liquidaciones */
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_UID = "RPRT2CORE-AD_JasperReport-1010180-20170125125715";
	protected final static String SETTLEMENT_LIST_JASPER_REPORT_FILENAME = "SettlementList.jasper";
	
	/** UID del Listado de Cheques en Boletas de Depósito */
	protected final static String BOLETADEPOSITOCHECKS_JASPER_REPORT_UID = "CORE-AD_Process-1010575";
	protected final static String BOLETADEPOSITOCHECKS_JASPER_REPORT_FILENAME = "BoletaDepositoChecks.jrxml";
	
	/** UID del Listado de Extractos Bancarios */
	protected final static String BANKSTATEMENTLIST_JASPER_REPORT_UID = "CORE-AD_Process-1010576";
	protected final static String BANKSTATEMENTLIST_JASPER_REPORT_FILENAME = "BankStatementReport.jrxml";
	
	/** UID del Reporte de Detalle de Cobros/Pagos sin Conciliar */
	protected final static String UNRECONCILEDPAYMENTSDETAILED_JASPER_REPORT_UID = "CORE-AD_Process-1010577";
	protected final static String UNRECONCILEDPAYMENTSDETAILED_JASPER_REPORT_FILENAME = "UnreconciledPaymentsDetailed.jrxml";
	
	/** UID del reporte del Orden de Pago*/
	protected final static String ORDEN_PAGO_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String ORDEN_PAGO_JASPER_REPORT_FILENAME = "OrdenPago.jasper";
	
	/** UID del Subreporte de Totales por Tipo del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_TOTALXKIND_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010226";
	protected final static String DECLARACION_VALORES_TOTALXKIND_JASPER_REPORT_FILENAME = "DeclaracionDeValores_TotalXKind.jasper";

	/** UID del Informe de Declaración de Valores */
	protected final static String DECLARACION_VALORES_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010047";
	protected final static String DECLARACION_VALORES_JASPER_REPORT_FILENAME = "DeclaracionDeValores.jasper";

	/** UID Impresión Cheques */
	protected final static String CHECK_PRINTING_JASPER_REPORT_UID = "SSTE2CORE-AD_JasperReport-1010144-20161018155925";
	protected final static String CHECK_PRINTING_JASPER_REPORT_FILENAME = "ChequesFrances.jasper";
	
	/** UID Informe de Lote de Pagos */
	protected final static String PAYMENT_BATCH_JASPER_REPORT_UID = "SSTE2CORE-AD_JasperReport-1010171-20161024123925";
	protected final static String PAYMENT_BATCH_JASPER_REPORT_FILENAME = "InformeLoteDePagos.jasper";
	
	/** UID del Subreporte de Otros-Medios en la impresión de la OP */
	protected final static String OP_OTHER_PAYMENTS_JASPER_REPORT_UID = "CORE-AD_JasperReport-1000015";
	protected final static String OP_OTHER_PAYMENTS_JASPER_REPORT_FILENAME = "OrdenPago_OtherPayments.jasper";
	
	/** Listado de OC */
	protected final static String PURCHASE_ORDER_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String PURCHASE_ORDER_REPORT_FILENAME = "PurchaseOrderReport.jrxml";
	
	/** Listado de OC que Recibieron Mercadería y no Poseen Factura Cargada */
	protected final static String PURCHASE_ORDER_WITH_INOUT_WITH_OUT_INVOICE_REPORT_UID = "CORE-AD_Process-1010426";
	protected final static String PURCHASE_ORDER_WITH_INOUT_WITH_OUT_INVOICE_REPORT_FILENAME = "PurchaseOrderWithInOutWithOutInvoice.jrxml";
	
	/** UID del Listado de Liquidaciones Detallado */
	protected final static String SETTLEMENT_LIST_DETAILED_JASPER_REPORT_UID = "RPRT2CORE-AD_Process-1010526-20170210194428";
	protected final static String SETTLEMENT_LIST_DETAILED_REPORT_FILENAME = "SettlementListDetailed.jrxml";
	
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
		
		// Listado de Cheques en Boletas de Depósito
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				BOLETADEPOSITOCHECKS_JASPER_REPORT_UID,
				BOLETADEPOSITOCHECKS_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(BOLETADEPOSITOCHECKS_JASPER_REPORT_FILENAME)));
		
		// Listado de Extractos Bancarios
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				BANKSTATEMENTLIST_JASPER_REPORT_UID,
				BANKSTATEMENTLIST_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(BANKSTATEMENTLIST_JASPER_REPORT_FILENAME)));
		
		// Reporte de Detalle de Cobros/Pagos sin Conciliar
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				UNRECONCILEDPAYMENTSDETAILED_JASPER_REPORT_UID,
				UNRECONCILEDPAYMENTSDETAILED_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(UNRECONCILEDPAYMENTSDETAILED_JASPER_REPORT_FILENAME)));
		
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
		
		// Subreporte de Totales por Tipo del Informe de Declaración de Valores
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					DECLARACION_VALORES_TOTALXKIND_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(DECLARACION_VALORES_TOTALXKIND_JASPER_REPORT_FILENAME)));
		
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
		
		// UID del Subreporte de Otros-Medios en la impresión de la OP
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					OP_OTHER_PAYMENTS_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(OP_OTHER_PAYMENTS_JASPER_REPORT_FILENAME)));
		
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
		
		// Listado de OC que Recibieron Mercadería y no Poseen Factura Cargada
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				PURCHASE_ORDER_WITH_INOUT_WITH_OUT_INVOICE_REPORT_UID,
				PURCHASE_ORDER_WITH_INOUT_WITH_OUT_INVOICE_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(PURCHASE_ORDER_WITH_INOUT_WITH_OUT_INVOICE_REPORT_FILENAME)));
		
		// Listado de Liquidaciones Detallado
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				SETTLEMENT_LIST_DETAILED_JASPER_REPORT_UID,
				SETTLEMENT_LIST_DETAILED_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(SETTLEMENT_LIST_DETAILED_REPORT_FILENAME)));
		
		return " ";
	}

}
