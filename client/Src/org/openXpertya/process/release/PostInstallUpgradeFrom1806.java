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
		
		return " ";
	}
}
