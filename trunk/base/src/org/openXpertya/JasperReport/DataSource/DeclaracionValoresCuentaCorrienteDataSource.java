package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

public class DeclaracionValoresCuentaCorrienteDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCuentaCorrienteDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DeclaracionValoresCuentaCorrienteDataSource(Properties ctx,
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(ctx, valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getQuery() {
		StringBuffer superSql = new StringBuffer("SELECT description, open as ingreso, egreso  FROM (");
		StringBuffer sql = new StringBuffer("SELECT dv.*,initialcurrentaccountamt as open  FROM c_pos_declaracionvalores_v as dv INNER JOIN c_invoice as i ON i.c_invoice_id = dv.doc_id WHERE ");
		sql.append(getStdWhereClause(true,"dv"));
		superSql.append(sql);
		superSql.append(") as inv ");
		superSql.append(" WHERE (open > 0) ");
		superSql.append(" ORDER BY description");
		return superSql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return getStdWhereClauseParams();
	}

	@Override
	protected String getTenderType() {
		return "'ARI'";
	}
	
}
