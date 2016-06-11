package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1604 extends PluginPostInstallProcess {
	
	/** Reporte de Cierre de Tarjetas */
	protected final static String CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010444";
	protected final static String DYNAMIC_CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010445";
	protected final static String CREDITCARD_CLOSE_REPORT_FILENAME = "CreditCardCloseReport.jrxml";
	
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
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				DYNAMIC_CREDITCARD_CLOSE_REPORT_UID,
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
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
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_UID,
				CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CUSTOMER_RECEIPT_CREDITNOTES_JASPER_REPORT_FILENAME)));
				
		return " ";
	}
	
}
