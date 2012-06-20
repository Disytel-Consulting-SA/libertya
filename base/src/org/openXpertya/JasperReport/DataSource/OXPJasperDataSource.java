package org.openXpertya.JasperReport.DataSource;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * Interfaz que debe implementar los Data Sources que utilicen el framework OXP para la 
 * presentación de reportes Jasper.
 * @author Franco Bonafine - Disytel
 * @date 12/12/2008
 */
public interface OXPJasperDataSource extends JRDataSource {

	/**
	 * Realiza la carga de los datos que contiene el Data Source dejándolos
	 * listos para la utilización por parte del Engine de Jasper para mostrar
	 * el reporte.
	 * @throws Exception cuando se produce algún error en la carga de los datos.
	 */
	public void loadData() throws Exception;
}
