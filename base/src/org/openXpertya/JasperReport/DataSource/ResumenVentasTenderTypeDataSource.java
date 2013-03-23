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

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype IN ('CAI','P') ";
	}

	@Override
	protected List<Object> getDSWhereClauseParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getGroupFields() {
		return "trxtype, tendertype";
	}
	
	@Override
	protected String getLineDescription() {
		return "Total "+getTenderTypeDescription();
	}

}
