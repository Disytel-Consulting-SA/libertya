package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.model.MDocType;

public class ResumenVentasDocTypeDataSource extends ResumenVentasPaymentMediumDataSource {

	public ResumenVentasDocTypeDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	public ResumenVentasDocTypeDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN,	boolean onlyDN) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID, onlyCN, onlyDN);
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype = 'I' AND tendertype IN ('"+MDocType.DOCBASETYPE_ARInvoice+"','"+MDocType.DOCBASETYPE_ARCreditMemo +"') ";
	}
}
