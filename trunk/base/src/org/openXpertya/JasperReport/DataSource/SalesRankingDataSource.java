package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;

import org.openXpertya.model.MDocType;
import org.openXpertya.util.Util;

public class SalesRankingDataSource extends ProductLinesSalesDataSource {
	
	/** Límite de registros */
	private Integer limit;
	
	/** Ordenar por precio? */
	private boolean orderByPrice;
	
	public SalesRankingDataSource(String trxName, Integer orgID,
			Integer productLinesID, Timestamp dateFrom, Timestamp dateTo, 
			Integer limit, boolean orderByPrice) {
		super(trxName, orgID, productLinesID, dateFrom, dateTo);
		setLimit(limit);
		setOrderByPrice(orderByPrice);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select value as product_value, name as product_name, " + getFacturadoSelectColumn() + " as facturado " +
					 "from (select p.m_product_id, p.value, p.name, sum(((CASE i.istaxincluded WHEN 'Y' THEN linenetamt ELSE linetotalamt END) - il.documentdiscountamt) * dt.signo_issotrx) as total, sum(qtyinvoiced * dt.signo_issotrx) as qty " +
					 "		from c_invoiceline as il " +
					 "		inner join m_product as p on p.m_product_id = il.m_product_id " +
					 "		left  join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
					 "		left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
					 "		left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
					 "		inner join c_invoice as i on i.c_invoice_id = il.c_invoice_id " +
					 "		inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
					 "		where i.docstatus IN ('CO','CL') AND i.ad_org_id = ? AND i.issotrx = 'Y' AND dt.docbasetype IN ('"+MDocType.DOCBASETYPE_ARInvoice+"','"+MDocType.DOCBASETYPE_ARCreditMemo+"') AND dt.doctypekey NOT IN ('RCI','RCR') AND date_trunc('day',dateinvoiced) >= date_trunc('day',?::date) AND date_trunc('day',dateinvoiced) <= date_trunc('day',?::date) ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" AND pl.m_product_lines_id = ").append(getProductLinesID());
		}
		sql.append(" group by p.m_product_id, p.value, p.name) as s order by ");
		sql.append(getOrderSQLStatement());
		sql.append(Util.isEmpty(getLimit(), true)?"":(" LIMIT "+getLimit()));
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[]{getOrgID(), getDateFrom(), getDateTo()};
	}
	
	/**
	 * @return senntencia sql del order by
	 */
	public String getOrderSQLStatement(){
		return " " + (isOrderByPrice() ? "total" : "qty") + " desc ";
	}
	
	public String getFacturadoSelectColumn(){
		return isOrderByPrice() ? "total" : "qty";
	}

	protected Integer getLimit() {
		return limit;
	}

	protected void setLimit(Integer limit) {
		this.limit = limit;
	}

	protected boolean isOrderByPrice() {
		return orderByPrice;
	}

	protected void setOrderByPrice(boolean orderByPrice) {
		this.orderByPrice = orderByPrice;
	}
	
}
