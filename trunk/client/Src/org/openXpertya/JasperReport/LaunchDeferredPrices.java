package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.DeferredPricesDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.X_M_Product_Category;
import org.openXpertya.model.X_M_Product_Family;
import org.openXpertya.model.X_M_Product_Gamas;
import org.openXpertya.model.X_M_Product_Lines;
import org.openXpertya.util.Util;

public class LaunchDeferredPrices extends JasperReportLaunch {
	
	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("PRICELIST_NAME",
				JasperReportsUtil.getPriceListName(getCtx(), getPriceListID(),
						get_TrxName()));
		addReportParameter("DEFERRED_PRICELIST_NAME",
				JasperReportsUtil.getPriceListName(getCtx(), getDeferredPriceListID(),
						get_TrxName()));
		if(!Util.isEmpty(getProductLinesID(), true)){
			X_M_Product_Lines line = new X_M_Product_Lines(getCtx(),
					getProductLinesID(), get_TrxName());
			addReportParameter("PRODUCT_LINES_VALUE", line.getValue());
			addReportParameter("PRODUCT_LINES_NAME", line.getName());
		}
		if(!Util.isEmpty(getProductGamasID(), true)){
			X_M_Product_Gamas gama = new X_M_Product_Gamas(getCtx(),
					getProductGamasID(), get_TrxName());
			addReportParameter("PRODUCT_GAMAS_VALUE", gama.getValue());
			addReportParameter("PRODUCT_GAMAS_NAME", gama.getName());
		}
		if(!Util.isEmpty(getProductCategoryID(), true)){
			X_M_Product_Category category = new X_M_Product_Category(getCtx(),
					getProductCategoryID(), get_TrxName());
			addReportParameter("PRODUCT_CATEGORY_VALUE", category.getValue());
			addReportParameter("PRODUCT_CATEGORY_NAME", category.getName());
		}
		if(!Util.isEmpty(getProductFamilyID(), true)){
			X_M_Product_Family family = new X_M_Product_Family(getCtx(),
					getProductFamilyID(), get_TrxName());
			addReportParameter("PRODUCT_FAMILY_VALUE", family.getValue());
			addReportParameter("PRODUCT_FAMILY_NAME", family.getName());
		}
	}

	protected Integer getPriceListID(){
		return (Integer)getParameterValue("M_PriceList_ID");
	}
	
	protected Integer getDeferredPriceListID(){
		return (Integer)getParameterValue("M_PriceList_Deferred_ID");
	}
	
	protected Integer getProductCategoryID(){
		return (Integer)getParameterValue("M_Product_Category_ID");
	}
	
	protected Integer getProductGamasID(){
		return (Integer)getParameterValue("M_Product_Gamas_ID");
	}
	
	protected Integer getProductLinesID(){
		return (Integer)getParameterValue("M_Product_Lines_ID");
	}
	
	protected Integer getProductFamilyID(){
		return (Integer)getParameterValue("M_Product_Family_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new DeferredPricesDataSource(getCtx(), getPriceListID(),
				getDeferredPriceListID(), getProductLinesID(),
				getProductGamasID(), getProductCategoryID(),
				getProductFamilyID(), get_TrxName());
	}

}
