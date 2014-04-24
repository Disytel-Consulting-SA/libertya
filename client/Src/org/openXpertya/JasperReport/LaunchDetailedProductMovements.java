package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.openXpertya.JasperReport.DataSource.DetailedProductMovementsDataDource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.TotalDetailedProductMovementsDataSource;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.DB;

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
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getDateFrom().getTime());
		calendar.add(Calendar.YEAR, -1);
		calendar.getTime();
		new Timestamp(calendar.getTimeInMillis());
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.format(calendar.getTime());
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
		// Saldo inicial
		addReportParameter("INITIAL_BALANCE", getInitialBalance());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new DetailedProductMovementsDataDource(get_TrxName(), getCtx(),
				getProductID(), getWarehouseID(), getDateFrom(), getDateTo());
	}

	protected BigDecimal getInitialBalance(){
		BigDecimal balance = BigDecimal.ZERO;
		if(getDateFrom() != null){
			String sql = "select coalesce(sum(movementqty),0.00) as balance " +
						 "from m_transaction t " +
						 "inner join m_locator l on l.m_locator_id = t.m_locator_id " +
						 "inner join m_warehouse w on w.m_warehouse_id = l.m_warehouse_id " +
						 "where m_product_id = ? and w.m_warehouse_id = ? AND movementdate::date < ?::date";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = DB.prepareStatement(sql, get_TrxName());
				ps.setInt(1, getProductID());
				ps.setInt(2, getWarehouseID());
				ps.setTimestamp(3, getDateFrom());
				rs = ps.executeQuery();
				if(rs.next()){
					balance = rs.getBigDecimal("balance");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try{
					if(rs != null)rs.close();
					if(ps != null)ps.close();
				} catch(Exception e2){
					e2.printStackTrace();
				}
			}
		}
		return balance;
	}
	
}
