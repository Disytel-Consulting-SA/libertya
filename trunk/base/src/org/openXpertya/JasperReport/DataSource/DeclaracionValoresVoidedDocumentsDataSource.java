package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresVoidedDocumentsDataSource extends
		DeclaracionValoresSubreportDataSource {

	public DeclaracionValoresVoidedDocumentsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresVoidedDocumentsDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}

	@Override
	protected String getTenderType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer(getStdSelect(true));
		sql.append(getStdWhereClause(false, null, false, true));
		sql.append(" AND generated_invoice_documentno is not null ");
		sql.append(" ORDER BY invoice_documentno "); 
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

}
