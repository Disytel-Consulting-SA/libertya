package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ProductLinesSalesDataSource;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProductLines;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchProductLinesSales extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("TITLE", getTitle());
		MOrg org = MOrg.get(getCtx(), getOrgID());
		addReportParameter("ORG_VALUE", org.getValue());
		addReportParameter("ORG_NAME", org.getName());
		if(!Util.isEmpty(getProductLineID(), true)){
			MProductLines productLine = new MProductLines(getCtx(),
					getProductLineID(), get_TrxName());
			addReportParameter("PRODUCT_LINE_VALUE", productLine.getValue());
			addReportParameter("PRODUCT_LINE_NAME", productLine.getName());
		}
		addReportParameter("ALL_PRODUCT_LINES_MSG", getAllProductLinesMsg());
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
	}

	protected String getTitle(){
		return Msg.getMsg(getCtx(), "SalesByProductLines");
	}

	protected String getAllProductLinesMsg(){
		return Msg.getMsg(getCtx(), "AllProductLines");
	}
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Integer getProductLineID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		Timestamp dateTo = (Timestamp)getParameterValue("Date_To");
		if(dateTo == null){
			dateTo = Env.getDate();
		}
		return dateTo;
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ProductLinesSalesDataSource(get_TrxName(), getOrgID(),
				getProductLineID(), getDateFrom(), getDateTo());
	}

}
