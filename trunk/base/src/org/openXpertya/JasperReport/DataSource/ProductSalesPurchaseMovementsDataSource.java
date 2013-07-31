package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.util.Util;

public class ProductSalesPurchaseMovementsDataSource extends QueryDataSource {

	/** Artículo */
	private Integer productID;
	
	/** Organización */
	private Integer orgID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Transacción de ventas */
	private String isSOTrx;
	
	public ProductSalesPurchaseMovementsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ProductSalesPurchaseMovementsDataSource(Integer orgID,
			Integer productID, Timestamp dateFrom, Timestamp dateTo,
			String isSOTrx, String trxName) {
		this(trxName);
		setOrgID(orgID);
		setProductID(productID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setIsSOTrx(isSOTrx);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select i.dateinvoiced, dt.name as doctypename, i.c_invoice_id, i.documentno, coalesce(i.nombrecli,bp.name) as bpartner_name, sum(qtyinvoiced*(CASE WHEN i.issotrx = 'N' AND signo_issotrx = -1 THEN 1 WHEN i.issotrx = 'N' AND signo_issotrx = 1 THEN -1 ELSE signo_issotrx END)) as qty, sum(linenetamount+taxamt) as linetotalamt " +
											"from c_invoiceline as il " +
											"inner join c_invoice as i on il.c_invoice_id = i.c_invoice_id " +
											"inner join c_doctype as dt on i.c_doctypetarget_id = dt.c_doctype_id " +
											"inner join c_bpartner as bp on i.c_bpartner_id = bp.c_bpartner_id " +
											"where m_product_id = ? AND i.docstatus IN ('CL','CO','RE','VO') AND i.issotrx = '"+getIsSOTrx()+"'");
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" AND i.ad_org_id = ? ");
		}
		if(getDateFrom() != null){
			sql.append(" AND (date_trunc('day', i.dateinvoiced) >= date_trunc('day', ?::date)) ");
		}
		if(getDateTo() != null){
			sql.append(" AND (date_trunc('day', i.dateinvoiced) <= date_trunc('day', ?::date)) ");
		}
		sql.append(" group by i.dateinvoiced, dt.name, i.c_invoice_id, i.documentno, bp.name, i.nombrecli ");
		sql.append(" order by i.dateinvoiced, i.documentno ");
		
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getProductID());
		if(!Util.isEmpty(getOrgID(), true)){
			params.add(getOrgID());
		}
		if(getDateFrom() != null){
			params.add(getDateFrom());
		}
		if(getDateTo() != null){
			params.add(getDateTo());
		}
		return params.toArray();
	}

	protected Integer getProductID() {
		return productID;
	}

	protected void setProductID(Integer productID) {
		this.productID = productID;
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
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

	protected String getIsSOTrx() {
		return isSOTrx;
	}

	protected void setIsSOTrx(String isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

}
