package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

public class ResumenVentasCurrentAccountPaymentsDataSource extends
		ResumenVentasPaymentMediumDataSource {

	public ResumenVentasCurrentAccountPaymentsDataSource(String trxName,
			Properties ctx, Integer orgID, Timestamp dateFrom,
			Timestamp dateTo, Integer posID, Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	public ResumenVentasCurrentAccountPaymentsDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN,	boolean onlyDN) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID, onlyCN, onlyDN);
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype = 'PCA' ";
	}
	
	@Override
	protected String getDSDataTable(){
		return "v_dailysales_current_account_payments";
	}
}
