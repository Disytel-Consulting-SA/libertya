package org.openXpertya.process.release;

import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1604 extends PluginPostInstallProcess {
	
	/** Reporte de Cierre de Tarjetas */
	protected final static String CREDITCARD_CLOSE_REPORT_UID = "CORE-AD_Process-1010431";
	protected final static String CREDITCARD_CLOSE_REPORT_FILENAME = "ReceptionsVendor.jrxml";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados */
	protected final static String CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_UID = "CORE-AD_Process-1010432";
	protected final static String CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME = "ListOfPurchaseOrdersDue.jrxml";
	
	/** Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos */
	protected final static String CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_FILENAME = "PurchaseOrderReport.jrxml";
	
	/** Reporte de Auditoría de Cierre de Tarjetas */ 
	protected final static String CLOSING_AUDIT_COUPONCARDS_REPORT_UID = "CORE-AD_Process-1010431";
	protected final static String CLOSING_AUDIT_COUPONCARDS_REPORT_FILENAME = "ReceptionsVendor.jrxml";
	
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
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Duplicados
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_UID,
				CREDITCARD_CLOSE_SB_DUPLICATEDCUPON_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDITCARD_CLOSE_REPORT_FILENAME)));
		
		// Reporte de Cierre de Tarjetas - Subreporte de Cupones Inválidos
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDITCARD_CLOSE_SB_INVALIDCUPON_REPORT_UID,
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
		
		return " ";
	}

}
