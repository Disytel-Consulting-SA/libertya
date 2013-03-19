package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SplittingDataSource;
import org.openXpertya.model.MLocator;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MSplitting;
import org.openXpertya.model.MWarehouse;

public class LaunchSplitting extends JasperReportLaunch {

	/** Fraccionamiento */
	private MSplitting splitting;
	
	@Override
	protected void loadReportParameters() throws Exception {
		setSplitting(new MSplitting(getCtx(), getRecord_ID(), get_TrxName()));
		MWarehouse warehouse = MWarehouse.get(getCtx(), getSplitting().getM_Warehouse_ID());
		MProduct product = MProduct.get(getCtx(), getSplitting().getM_Product_ID());
		MLocator locator = MLocator.get(getCtx(), getSplitting().getM_Locator_ID());
		
		addReportParameter("DOCUMENTNO", getSplitting().getDocumentNo());
		addReportParameter("COMMENTS", getSplitting().getComments());
		addReportParameter("DATE", getSplitting().getDateTrx());
		addReportParameter("WAREHOUSE_VALUE", warehouse.getValue());
		addReportParameter("WAREHOUSE_NAME", warehouse.getName());
		addReportParameter("PRODUCT_VALUE", product.getValue());
		addReportParameter("PRODUCT_NAME", product.getName());
		
		addReportParameter("UM", JasperReportsUtil.getUOMName(getCtx(),
				getSplitting().getC_UOM_ID(), get_TrxName()));
		addReportParameter("QTY", getSplitting().getProductQty());
		addReportParameter("LOCATOR", locator.getValue());
		addReportParameter("SPLITTING_QTY", getSplitting().getSplitQty());
		addReportParameter("MERMA_QTY", getSplitting().getShrinkQty());
		
		addReportParameter("CONVERSION_UM", JasperReportsUtil.getUOMName(
				getCtx(), getSplitting().getC_Conversion_UOM_ID(),
				get_TrxName()));
		addReportParameter("QTY_CONVERTED", getSplitting()
				.getConvertedProductQty());
		addReportParameter("QTY_SPLITTING_CONVERTED", getSplitting()
				.getConvertedSplitQty());
		addReportParameter("MERMA_QTY_CONVERTED", getSplitting()
				.getConvertedShrinkQty());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new SplittingDataSource(getSplitting().getID(), get_TrxName());
	}

	protected MSplitting getSplitting() {
		return splitting;
	}

	protected void setSplitting(MSplitting splitting) {
		this.splitting = splitting;
	}

}
