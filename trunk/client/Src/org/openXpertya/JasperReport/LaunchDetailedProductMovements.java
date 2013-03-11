package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.DetailedProductMovementsDataDource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.TotalDetailedProductMovementsDataSource;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MWarehouse;

public class LaunchDetailedProductMovements extends JasperReportLaunch {

	protected Integer getProductID(){
		return (Integer)getParameterValue("M_Product_ID");
	}
	
	protected Integer getWarehouseID(){
		return (Integer)getParameterValue("M_Warehouse_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("Date_To");
	}
	
	protected MJasperReport getStockBalanceSubreport() throws Exception{
		return getJasperReport(getCtx(), "Product Movements Detailed - Stock Balance Subreport", get_TrxName());
	}
	
	protected DetailedProductMovementsDataDource getStockBalanceSubreportDS() throws Exception{
		DetailedProductMovementsDataDource ds = new TotalDetailedProductMovementsDataSource(
				get_TrxName(), getCtx(), getProductID(), getWarehouseID(), getDateFrom(),
				getDateTo());
		ds.loadData();
		return ds;
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Artículo
		MProduct product = MProduct.get(getCtx(), getProductID());
		addReportParameter("PRODUCT_VALUE", product.getValue());
		addReportParameter("PRODUCT_NAME", product.getName());
		// Almacén
		MWarehouse warehouse = MWarehouse.get(getCtx(), getWarehouseID());
		addReportParameter("WAREHOUSE_VALUE", warehouse.getValue());
		addReportParameter("WAREHOUSE_NAME", warehouse.getName());
		// Fechas
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		// Subreporte
		MJasperReport subreport = getStockBalanceSubreport();
		if(subreport != null && subreport.getBinaryData() != null){
			addReportParameter("STOCKBALANCE_COMPILED_SUBREPORT",
					new ByteArrayInputStream(subreport.getBinaryData()));
		}
		DetailedProductMovementsDataDource subreportDS = getStockBalanceSubreportDS();
		addReportParameter("STOCKBALANCE_SUBREPORT_DATASOURCE", subreportDS);
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new DetailedProductMovementsDataDource(get_TrxName(), getCtx(),
				getProductID(), getWarehouseID(), getDateFrom(), getDateTo());
	}

}
