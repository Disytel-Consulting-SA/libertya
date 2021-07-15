package org.openXpertya.plugin.report;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.PO;
import org.openXpertya.plugin.common.PluginConstants;
import org.openXpertya.plugin.common.PluginPOUtils;

public class PluginReportUtils {

	/** Itera por todos los plugins activos buscando uno que tenga igual nombre 
	 * que el className recibido y que implemente ReportPrividerInterface implemente 
	 * 
	 * @param className el nombre de la clase, por ejemplo LaunchInvoice, LaunchInOut
	 * @param report el reporte sobre el cual incorporar nuevos parametros
	 * @param po el PO base sobre el cual se va a generar el reporte.  Si es un listado este argumento es null. 
	 * */
	public static void injectParameters(String className, MJasperReport report, PO po) {
		
		for (String aPackage : PluginPOUtils.getActivePluginPackages()) {
			// El plugin contiene una clase con igual className?
			String targetClass = aPackage + "." + PluginConstants.PACKAGE_REPORT_PROVIDER_PROCESS + "." + className;
			try 
			{
				Object instance = Class.forName(targetClass).newInstance();
				((ReportProviderInterface)instance).addReportParametersToLaunch(report, po);
			}
			catch (Exception e) {
				// Omitir. Clase no encontrada
			}
		}	
	}
}
