package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ProductSalesPurchaseMovementsDataSource;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProduct;
import org.openXpertya.util.Util;

public class LaunchProductSalesPurchaseMovements extends JasperReportLaunch {

	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("DateInvoiced");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateInvoiced_To");
	}
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected String isSOTrx(){
		return (String)getParameterValue("IsSOTrx");
	}
	
	protected Integer getProductID(){
		return (Integer)getParameterValue("M_Product_ID");
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		MProduct product = MProduct.get(getCtx(), getProductID());
		String orgValue = null;
		String orgName = null;
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			orgValue = org.getValue();
			orgName	= org.getName();
		}
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		addReportParameter("ORG_VALUE", orgValue);
		addReportParameter("ORG_NAME", orgName);
		addReportParameter("PRODUCT_VALUE", product.getValue());
		addReportParameter("PRODUCT_NAME", product.getName());
		addReportParameter("ISSOTRX", isSOTrx().equals("Y"));
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ProductSalesPurchaseMovementsDataSource(getOrgID(),
				getProductID(), getDateFrom(), getDateTo(), isSOTrx(),
				get_TrxName());
	}

}
