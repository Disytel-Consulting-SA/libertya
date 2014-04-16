package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

public class ResumenVentasTenderTypeDataSource extends
		ResumenVentasDataSource {

	public ResumenVentasTenderTypeDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	public ResumenVentasTenderTypeDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN,	boolean onlyDN) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID, onlyCN, onlyDN);
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype IN ('CAI','P','NCC','PA','ND','CAIA') ";
	}

	@Override
	protected List<Object> getDSWhereClauseParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getGroupFields() {
		return "tendertype";
	}
	
	@Override
	protected String getLineDescription() {
		return "Total "+getTenderTypeDescription();
	}
	
	@Override
	protected String getDSDataTable(){
		return "(SELECT * FROM v_dailysales_v2 UNION ALL SELECT * FROM v_dailysales_current_account)";
	}
}
