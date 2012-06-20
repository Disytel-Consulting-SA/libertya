package org.openXpertya.JasperReport.DataSource;

import net.sf.jasperreports.engine.JREmptyDataSource;

public class OXPJasperEmptyDataSource extends JREmptyDataSource implements OXPJasperDataSource {

	@Override
	public void loadData() throws Exception {
		// No hace nada porque es un data source vacío
	}
}
