package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

/***
 * Esta clase data source permite obtener el detalle y total de los artículos
 * filtrados en la super clase
 * 
 * @author Matías Cap - Disytel
 *
 */
public class DeclaracionValoresProductsFiltered extends DeclaracionValoresVentasDataSource {

	public DeclaracionValoresProductsFiltered(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeclaracionValoresProductsFiltered(Properties ctx, DeclaracionValoresDTO valoresDTO, String select,
			String groupBy, String orderBy, String trxName) {
		super(ctx, valoresDTO, select, groupBy, orderBy, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT p.m_product_id, p.name, sum(grandtotal * signo_issotrx) as total ");
		sql.append(" FROM ( SELECT pji.* FROM ").append(getDSDataTable()).append(" as pji ");
		sql.append(" INNER JOIN c_posjournal as pj on pj.c_posjournal_id = pji.c_posjournal_id ");
		sql.append(" WHERE ").append(getStdWhereClause(false, null, false, false));
		sql.append(" ) as i ");
		sql.append(" INNER JOIN c_invoiceline il ON il.c_invoice_id = i.c_invoice_id ");
		sql.append(" INNER JOIN m_product p ON p.m_product_id = il.m_product_id ");
		sql.append(" GROUP BY p.m_product_id, p.name ");
		sql.append(" ORDER BY p.name ");
		return sql.toString();
	}
	
	@Override
	protected String getDSDataTable(){
		return getDSFunView("c_posjournalinvoices_v_filtered");
	}
	
	@Override
	protected String getTenderType() {
		return "'ARI','ARC'";
	}

	@Override
	protected String getFilterProductSetOperator(){
		return " IN ";
	}
}
