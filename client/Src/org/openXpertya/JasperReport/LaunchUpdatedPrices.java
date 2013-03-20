package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.UpdatedPricesDataSource;

public class LaunchUpdatedPrices extends JasperReportLaunch {
	
	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("PRICELIST_NAME",
				JasperReportsUtil.getPriceListName(getCtx(), getPriceListID(),
						get_TrxName()));
		addReportParameter("UPDATED", getUpdated());
	}

	protected Integer getPriceListID(){
		return (Integer)getParameterValue("M_PriceList_ID");
	}
	
	protected Timestamp getUpdated(){
		return (Timestamp)getParameterValue("FromUpdated");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new UpdatedPricesDataSource(getCtx(), getPriceListID(),
				getUpdated(), get_TrxName());
	}

}
