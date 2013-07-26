package org.openXpertya.JasperReport;

import java.util.Map;
import java.util.Properties;

public abstract class DynamicJasperReport {

	public DynamicJasperReport(){}
	
	/** Agregar parámetros específicos del reporte */
	public abstract void addReportParameters(Properties ctx, Map<String, Object> params);
	
}
