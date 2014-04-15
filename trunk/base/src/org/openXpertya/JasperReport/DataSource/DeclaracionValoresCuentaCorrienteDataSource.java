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
			DeclaracionValoresDTO valoresDTO, String select, String groupBy,
			String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
	}

	@Override
	protected String getQuery() {
		// Monto pendiente de la facturas
		StringBuffer superSql = new StringBuffer("SELECT invoice_documentno, invoice_grandtotal, open as ingreso, egreso, open as total   FROM (");
		StringBuffer sql = new StringBuffer("SELECT dv.*,invoice_grandtotal-coalesce(ds.amount,0) as open  " +
											"FROM c_pos_declaracionvalores_ventas as dv LEFT JOIN (select c_invoice_id as alloc_invoice_id, c_posjournal_id as alloc_journal_id, sum(amount) as amount " +
																								"from c_allocationhdr as ah " +
																								"inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id " +
																								"where ah.isactive = 'Y' " +
																								"group by c_invoice_id, c_posjournal_id) as ds on ds.alloc_invoice_id = dv.c_invoice_id and dv.c_posjournal_id = ds.alloc_journal_id WHERE ");
		sql.append(getStdWhereClause(true,"dv",true,false));
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
