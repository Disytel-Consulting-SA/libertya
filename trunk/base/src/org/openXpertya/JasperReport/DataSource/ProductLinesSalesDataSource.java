package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.MDocType;
import org.openXpertya.util.Util;

public class ProductLinesSalesDataSource extends QueryDataSource {

	/** ID de organización */
	private Integer orgID;
	
	/** ID de Línea de Artículo */
	private Integer productLinesID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** fecha hasta */
	private Timestamp dateTo;

	
	public ProductLinesSalesDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public ProductLinesSalesDataSource(String trxName, Integer orgID, Integer productLinesID, Timestamp dateFrom, Timestamp dateTo) {
		super(trxName);
		setOrgID(orgID);
		setProductLinesID(productLinesID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select pl.m_product_lines_id, pl.value as product_line_value, pl.name as product_line_name, coalesce(s.cost,0.00) as cost, coalesce(s.sales,0.00) as sales from m_product_lines as pl left join ( ");
		sql.append("select pl.m_product_lines_id, pl.value as product_line_value, pl.name as product_line_name, sum(il.costprice * dt.signo_issotrx * il.qtyentered) as cost, sum(((il.priceentered * il.qtyentered) - il.documentdiscountamt) * dt.signo_issotrx)  as sales " +
					 "from c_invoiceline as il " +
					 "inner join c_invoice as i on i.c_invoice_id = il.c_invoice_id " +
					 "inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id "+
					 "inner join m_product as p on p.m_product_id = il.m_product_id " +
					 "inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
					 "inner join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
					 "inner join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
					 "where il.ad_org_id = ? AND pl.isactive = 'Y' AND i.docstatus IN ('CO','CL') AND dt.doctypekey NOT IN ('RCI','RCR') AND dt.docbasetype IN ('"+MDocType.DOCBASETYPE_ARInvoice+"','"+MDocType.DOCBASETYPE_ARCreditMemo +"') AND dt.doctypekey NOT IN ('RCI','RCR') AND date_trunc('day',i.dateinvoiced) >= date_trunc('day',?::date) AND date_trunc('day',i.dateinvoiced) <= date_trunc('day',?::date) AND (dt.isfiscal is null OR dt.isfiscal = 'N' OR (dt.isfiscal = 'Y' AND i.fiscalalreadyprinted = 'Y')) ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" AND pl.m_product_lines_id = ? ");
		}
		sql.append(" group by pl.m_product_lines_id, pl.value, pl.name ");
		sql.append(" ) as s on s.m_product_lines_id = pl.m_product_lines_id ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" WHERE pl.m_product_lines_id = ? ");
		}
		sql.append(" ORDER BY pl.value ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getOrgID());
		params.add(getDateFrom());
		params.add(getDateTo());
		if(!Util.isEmpty(getProductLinesID(), true)){
			params.add(getProductLinesID());
			params.add(getProductLinesID());
		}
		return params.toArray();
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected Integer getProductLinesID() {
		return productLinesID;
	}

	protected void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	protected Timestamp getDateFrom() {
		return dateFrom;
	}

	protected void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	protected Timestamp getDateTo() {
		return dateTo;
	}

	protected void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

}
