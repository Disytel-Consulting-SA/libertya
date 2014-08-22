package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.PriceChangingDataSource;
import org.openXpertya.model.MUser;
import org.openXpertya.model.X_M_Product_Lines;
import org.openXpertya.util.Util;

public class LaunchPriceChanging extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		addReportParameter("VARIATION", getVariation());
		if(!Util.isEmpty(getPriceListID(), true)){
			addReportParameter("PRICELIST_NAME",
					JasperReportsUtil.getPriceListName(getCtx(),
							getPriceListID(), get_TrxName()));
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			X_M_Product_Lines line = new X_M_Product_Lines(getCtx(),
					getProductLinesID(), get_TrxName());
			addReportParameter("PRODUCT_LINES_VALUE", line.getValue());
			addReportParameter("PRODUCT_LINES_NAME", line.getName());
		}
		if(!Util.isEmpty(getUpdatedBy(), true)){
			MUser user = MUser.get(getCtx(), getUpdatedBy());
			addReportParameter("USER_NAME", user.getName());
			addReportParameter("USER_DESCRIPTION", user.getDescription());
		}
	}

	protected Integer getPriceListID(){
		return (Integer)getParameterValue("M_PriceList_ID");
	}
	
	protected Integer getProductLinesID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("Date_To");
	}
	
	protected BigDecimal getVariation(){
		return (BigDecimal)getParameterValue("Variation");
	}
	
	protected Integer getUpdatedBy(){
		return (Integer)getParameterValue("UpdatedBy");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new PriceChangingDataSource(getCtx(), getDateFrom(),
				getDateTo(), getPriceListID(), getProductLinesID(),
				getUpdatedBy(), getVariation(), get_TrxName());
	}

}
