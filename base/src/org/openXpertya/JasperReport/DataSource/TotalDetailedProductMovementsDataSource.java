package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

public class TotalDetailedProductMovementsDataSource extends
		DetailedProductMovementsDataDource {

	public TotalDetailedProductMovementsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TotalDetailedProductMovementsDataSource(String trxName, Properties ctx, Integer productID, Integer warehouseID, Timestamp dateFrom, Timestamp dateTo) {
		super(trxName, ctx, productID, warehouseID, dateFrom, dateTo);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected String getSQLOrderBy(){
		return "";
	}

	@Override
	protected String getSelectSQL(){
		return "receiptvalue, doctypename, qty ";
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT receiptvalue, doctypename, sum(qty) as qty FROM (");
		sql.append(super.getQuery());
		sql.append(") as t ");
		sql.append(" GROUP BY receiptvalue, doctypename ");
		sql.append(" ORDER BY receiptvalue, doctypename ");
		return sql.toString();
	}

}
