package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
		// Monto pendiente de la facturas
		StringBuffer superSql = new StringBuffer("SELECT invoice_documentno, invoice_grandtotal, open as ingreso, egreso, open as total   FROM (");
		StringBuffer sql = new StringBuffer("SELECT dv.*,initialcurrentaccountamt as open  FROM c_pos_declaracionvalores_v as dv INNER JOIN c_invoice as i ON i.c_invoice_id = dv.doc_id WHERE ");
		sql.append(getStdWhereClause(true,"dv"));
		superSql.append(sql);
		superSql.append(") as inv ");
		superSql.append(" WHERE (open > 0) ");
		// NC libres
		superSql.append(" UNION ALL ");
		superSql.append(" SELECT invoice_documentno, invoice_grandtotal, ingreso, egreso, egreso * -1 as total FROM c_pos_declaracionvalores_v WHERE ");
		superSql.append(getStdWhereClause(false));
		superSql.append(" AND (tendertype = 'ARC') ");
		superSql.append(" ORDER BY invoice_documentno");
		
		return superSql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.addAll(Arrays.asList(getStdWhereClauseParams()));
		params.addAll(Arrays.asList(getStdWhereClauseParams()));
		return params.toArray();
	}

	@Override
	protected String getTenderType() {
		return "'ARI'";
	}
	
}
