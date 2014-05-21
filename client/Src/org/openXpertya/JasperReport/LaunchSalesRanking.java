package org.openXpertya.JasperReport;

import java.math.BigDecimal;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SalesRankingDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchSalesRanking extends LaunchProductLinesSales {

	@Override
	protected void loadReportParameters() throws Exception {
		super.loadReportParameters();
		addReportParameter("LIMIT", getLimit());
		if(!Util.isEmpty(getBPartnerID(), true)){
			MBPartner bpartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("VENDOR_VALUE", bpartner.getValue());
			addReportParameter("VENDOR_NAME", bpartner.getName());
		}
	}
	
	@Override
	protected String getTitle(){
		return Msg.getMsg(getCtx(), isOrderByPrice() ? "SalesRankingByPrice"
				: "SalesRankingByQty");
	}
	
	protected boolean isOrderByPrice(){
		return/opt/iReport-2.0.1/bin/SalesRanking.jasper/opt/iReport-2.0.1/bin/SalesRanking.jasper/opt/iReport-2.0.1/bin/SalesRanking.jasper/opt/iReport-2.0.1/bin/SalesRanking.jasper/opt/iReport-2.0.1/bin/SalesRanking.jasperring)getParameterValue("Order")).equals("P");
	}
	
	protected Integer getLimit(){
		return ((BigDecimal)getParameterValue("Limit")).intValue();
	}
	
	protected Integer getBPartnerID(){
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new SalesRankingDataSource(get_TrxName(), getOrgID(),
				getProductLineID(), getDateFrom(), getDateTo(),
				getLimit(), isOrderByPrice(), getBPartnerID());
	}
}
