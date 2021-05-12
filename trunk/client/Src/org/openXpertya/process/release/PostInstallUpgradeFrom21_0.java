package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom21_0 extends PluginPostInstallProcess {
	
	/** Listado de OC */
	protected final static String LISTADO_DE_OC_JASPER_REPORT_UID = "CORE-AD_Process-1010433";
	protected final static String LISTADO_DE_OC_JASPER_REPORT_FILENAME = "PurchaseOrderReport.jasper";
	
	/** Listado de OC Vencidas o Sin Novedades */
	protected final static String ORDERS_DUE_JASPER_REPORT_UID = "CORE-AD_Process-1010432";
	protected final static String ORDERS_DUE_JASPER_REPORT_FILENAME = "ListOfPurchaseOrdersDue.jasper";
	
	/** Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** Informe de Cobranza y Pagos */
	protected final static String COBRANZAS_PAGOS_REPORT_UID = "CORE-AD_Process-1010401";
	protected final static String COBRANZAS_PAGOS_REPORT_FILENAME = "InformeDeCobranzasYPagos.jasper";
	
	@Override
	protected String doIt() throws Exception {
		super.doIt();
		
		// Listado de OC
		updateReport(LISTADO_DE_OC_JASPER_REPORT_UID, LISTADO_DE_OC_JASPER_REPORT_FILENAME);
		
		// Listado de OC Vencidas o Sin Novedades
		updateReport(ORDERS_DUE_JASPER_REPORT_UID, ORDERS_DUE_JASPER_REPORT_FILENAME);
		
		// Impresión de FE
		updateReport(FE_REPORT_UID, FE_REPORT_FILENAME);
		
		// Informe de Cobranza y Pagos
		updateReport(COBRANZAS_PAGOS_REPORT_UID, COBRANZAS_PAGOS_REPORT_FILENAME);
		
		/*
		 * Actualizacion de binarios
		 * """"""""""""""""""""""""" 
		 * Utilizar SIEMPRE los métodos MJasperReport.updateBinaryData() y MProcess.addAttachment() 
		 * para la carga de informes tipo Jasper, el primero para la carga en AD_JasperReport y el 
		 * segundo en reportes dinámicos, los cuales van adjuntos en el informe/proceso correspondiente.
		 * 
		 * Idealmente incorporar informes ya precompilados para evitar 
		 * la necesidad de contar con javac en los equipos de los usuarios
		 */
		
		return " ";
	}

}
