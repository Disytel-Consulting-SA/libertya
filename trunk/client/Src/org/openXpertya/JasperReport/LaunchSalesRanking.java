package org.openXpertya.JasperReport;

import java.math.BigDecimal;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SalesRankingDataSource;
import org.openXpertya.util.Msg;

public class LaunchSalesRanking extends LaunchProductLinesSales {

	@Override
	protected void loadReportParameters() throws Exception {
		super.loadReportParameters();
		addReportParameter("LIMIT", getLimit());
	}
	
	@Override
	protected String getTitle(){
		return Msg.getMsg(getCtx(), isOrderByPrice() ? "SalesRankingByPrice"
				: "SalesRankingByQty");
	}
	
	protected boolean isOrderByPrice(){
		return ((String)getParameterValue("Order")).equals("P");
	}
	
	protected Integer getLimit(){
		return ((BigDecimal)getParameterValue("Limit")).intValue();
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new SalesRankingDataSource(get_TrxName(), getOrgID(),
				getProductLineID(), getDateFrom(), getDateTo(),
				getLimit(), isOrderByPrice());
	}
}
