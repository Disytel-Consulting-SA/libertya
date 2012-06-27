package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.DeferredPricesDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;

public class LaunchDeferredPrices extends JasperReportLaunch {
	
	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("PRICELIST_NAME",
				JasperReportsUtil.getPriceListName(getCtx(), getPriceListID(),
						get_TrxName()));
		addReportParameter("DEFERRED_PRICELIST_NAME",
				JasperReportsUtil.getPriceListName(getCtx(), getDeferredPriceListID(),
						get_TrxName()));
	}

	protected Integer getPriceListID(){
		return (Integer)getParameterValue("M_PriceList_ID");
	}
	
	protected Integer getDeferredPriceListID(){
		return (Integer)getParameterValue("M_PriceList_Deferred_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new DeferredPricesDataSource(getCtx(), getPriceListID(),
				getDeferredPriceListID(), get_TrxName());
	}

}
