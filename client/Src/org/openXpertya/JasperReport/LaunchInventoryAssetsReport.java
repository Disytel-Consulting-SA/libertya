package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.JasperReportLaunch;
import org.openXpertya.JasperReport.DataSource.InventoryAssetsReportDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;

public class LaunchInventoryAssetsReport extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("FECHA", getDate());
		addReportParameter("ISGROUPED", isGrouped());
	}
	
	protected Timestamp getDate(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected boolean isGrouped(){
		return ((String)getParameterValue("isGrouped")).equals("Y");
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new InventoryAssetsReportDataSource(getCtx(), getDate(),
				isGrouped(), get_TrxName());
	}

}
