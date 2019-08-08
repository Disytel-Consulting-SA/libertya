package org.openXpertya.process.release;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.process.PluginPostInstallProcess;
import org.openXpertya.utils.JarHelper;

public class PostInstallUpgradeFrom1907 extends PluginPostInstallProcess {

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
