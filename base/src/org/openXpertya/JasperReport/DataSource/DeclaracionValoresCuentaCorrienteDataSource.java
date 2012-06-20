package org.openXpertya.JasperReport.DataSource;

public class DeclaracionValoresCuentaCorrienteDataSource extends
		DeclaracionValoresDataSource {

	public DeclaracionValoresCuentaCorrienteDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public DeclaracionValoresCuentaCorrienteDataSource(
			DeclaracionValoresDTO valoresDTO, String trxName) {
		super(valoresDTO, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getQuery() {
		StringBuffer superSql = new StringBuffer("SELECT description, open as ingreso, egreso  FROM (");
		StringBuffer sql = new StringBuffer("SELECT *,invoiceopen(doc_id,0) as open  FROM c_pos_declaracionvalores_v WHERE ");
		sql.append(getStdWhereClause(true));
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
		return "ARI";
	}
	
}
