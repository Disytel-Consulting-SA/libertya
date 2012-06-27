package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

public class ResumenVentasDocTypeDataSource extends ResumenVentasTenderTypeDataSource {

	public ResumenVentasDocTypeDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo) {
		super(trxName, ctx, orgID, dateFrom, dateTo);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype = 'I' ";
	}
}
