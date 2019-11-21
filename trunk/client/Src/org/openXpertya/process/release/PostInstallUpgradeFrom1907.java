package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.MProcess;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1907 extends PluginPostInstallProcess {

	/** UID del informe de Movimientos Valorizados Detallado */
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010117";
	protected final static String VALUED_MOVEMENTS_DETAIL_JASPER_REPORT_FILENAME = "ValuedMovementsDetail.jasper";
	
	/** UID del informe de Autorizaciones de Usuarios */
	protected final static String USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_UID = "T0122CORE-AD_Process-1010645-20190906165716";
	protected final static String USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME = "UserAuthorizations.jasper";
	
	/** UID de la impresión de Solicitudes de NC */
	protected final static String CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_UID = "CORE-AD_JasperReport-1010274";
	protected final static String CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_FILENAME = "rpt_SolicitudNC.jasper";
	
	/** UID del informe de gestión de Solicitudes de NC */
	protected final static String CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_UID = "CORE-AD_Process-1010649";
	protected final static String CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME = "CreditRequestReport.jasper";
	
	/** UID de Impresión de FE */
	protected final static String FE_REPORT_UID = "CORE-AD_JasperReport-1010118";
	protected final static String FE_REPORT_FILENAME = "rpt_Factura_Electronica.jasper";
	
	/** UID del Subreporte de Notas de Crédito en Impresión de OP */
	protected final static String OP_NC_REPORT_UID = "CORE-AD_JasperReport-1010277";
	protected final static String OP_NC_REPORT_FILENAME = "OrdenPago_NotasDeCredito.jasper";
	
	/** UID Impresión de OP */
	protected final static String OP_REPORT_UID = "CORE-AD_JasperReport-1000012";
	protected final static String OP_REPORT_FILENAME = "OrdenPago.jasper";
	
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
		
		// Autorizaciones de Usuarios
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_UID,
				USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(USER_AUTHORIZATIONS_DETAIL_JASPER_REPORT_FILENAME)));


		// Impresión de SNCP
		MJasperReport
			.updateBinaryData(
					get_TrxName(),
					getCtx(),
					CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_UID,
					JarHelper
							.readBinaryFromJar(
									jarFileURL,
									getBinaryFileURL(CREDIT_REQUEST_PRINT_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Informe de SNCP
		MProcess.addAttachment(
				get_TrxName(),
				getCtx(),
				CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_UID,
				CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME,
				JarHelper
						.readBinaryFromJar(
								jarFileURL,
								getBinaryFileURL(CREDIT_REQUEST_REPORT_DETAIL_JASPER_REPORT_FILENAME)));
		
		// Impresión de FE
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						FE_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(FE_REPORT_FILENAME)));
		
		// Subreporte de Notas de Crédito en Impresión de OP
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						OP_NC_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(OP_NC_REPORT_FILENAME)));
		
		// Impresión de OP
		MJasperReport
				.updateBinaryData(
						get_TrxName(),
						getCtx(),
						OP_REPORT_UID,
						JarHelper
								.readBinaryFromJar(
										jarFileURL,
										getBinaryFileURL(OP_REPORT_FILENAME)));
		
		return " ";
	}
	

}
