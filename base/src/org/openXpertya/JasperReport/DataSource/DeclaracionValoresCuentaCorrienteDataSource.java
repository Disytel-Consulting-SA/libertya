package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DeclaracionValoresCuentaCorrienteDataSource extends
		DeclaracionValoresSubreportDataSource {

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
		StringBuffer sql = new StringBuffer("SELECT dv.*,invoice_grandtotal-coalesce((select sum(amount+writeoffamt) as amount from c_allocationhdr as ah inner join c_allocationline as al on al.c_allocationhdr_id = ah.c_allocationhdr_id where ah.isactive = 'Y' and c_invoice_id = dv.c_invoice_id and dv.c_posjournal_id = c_posjournal_id group by c_invoice_id, c_posjournal_id),0) as open  " +
											"FROM "+super.getDSDataTable()+" as dv " +
											"JOIN c_invoice i on i.c_invoice_id = dv.c_invoice_id " + 
											"WHERE ");
		sql.append(getStdWhereClause(true,"dv",true,false));
		sql.append(" AND (allocation_active is null OR allocation_active = 'Y') ");
		sql.append(" AND (i.paymentrule = 'P') ");
		superSql.append(sql);
		superSql.append(") as inv ");
		superSql.append(" WHERE (open > 0) ");
		// NC libres
		superSql.append(" UNION ALL ");
		superSql.append(" SELECT invoice_documentno, invoice_grandtotal, ingreso, egreso, egreso * -1 as total ");
		superSql.append(" FROM ");
		superSql.append(super.getDSDataTable());
		superSql.append(" as nc ");
		superSql.append(" JOIN c_invoice i on i.c_invoice_id = nc.c_invoice_id ");
		superSql.append(" WHERE ");
		superSql.append(getStdWhereClause(false, "nc"));
		superSql.append(" AND (nc.tendertype = 'ARC') ");
		superSql.append(" AND (i.paymentrule = 'P') ");
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
