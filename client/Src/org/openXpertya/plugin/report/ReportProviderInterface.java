package org.openXpertya.plugin.report;

import org.openXpertya.JasperReport.MJasperReport;
import org.openXpertya.model.PO;

public interface ReportProviderInterface {

	/** Incorporacion de parametros adicionales al reporte recibido. Se inyectan los siguientes argumentos:
	 * @param report el reporte sobre el cual incorporar nuevos parametros
	 * @param po el PO base sobre el cual se va a generar el reporte. Si es un listado este argumento es null. 
	 */	
	public void addReportParametersToLaunch(MJasperReport report, PO po);
}
