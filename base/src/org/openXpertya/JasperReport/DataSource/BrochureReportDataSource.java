package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;

public class BrochureReportDataSource extends QueryDataSource {

	private Integer orgID;
	private Integer brochureID;
	private String orderBy;
	private String order;
	private String isSOTrx;
	private Timestamp dateFrom;
	private Timestamp dateTo;
	
	public BrochureReportDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public BrochureReportDataSource(String trxName, Integer orgID,
			Integer brochureID, String orderBy, String order, String isSOTrx,
			Timestamp dateFrom, Timestamp dateTo) {
		this(trxName);
		setOrgID(orgID);
		setBrochureID(brochureID);
		setOrderBy(orderBy);
		setOrder(order);
		setIsSOTrx(isSOTrx);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
	}

	@Override
	protected String getQuery() {
		String sql = "select * " +
					"from (select m_product_id,	product_value,product_name,pricestd,qtyinvoiced,totalpricestd," +
					"				totalpriceentered, (CASE WHEN qtyinvoiced <> 0 THEN abs(totalpriceentered)/qtyinvoiced ELSE 0 END) as averageprice " +
					"		from (select m_product_id,product_value,product_name,pricestd," +
					"					sum(qtyinvoiced * signo_issotrx) as qtyinvoiced," +
					"					sum(pricestd * qtyinvoiced * signo_issotrx) as totalpricestd, " +
					"					sum(priceentered * qtyinvoiced * signo_issotrx) as totalpriceentered " +
					"				from (select p.m_product_id, p.value as product_value, p.name as product_name, " +
					"							coalesce(determineproductpricestd(p.m_product_id, il.ad_org_id, i.issotrx),il.pricelist) as pricestd, " +
					"							il.qtyinvoiced,dt.signo_issotrx,il.priceentered " +
					"						from m_brochureline as bl " +
					"						inner join c_invoiceline as il on il.m_product_id = bl.m_product_id " +
					"						inner join c_invoice as i on i.c_invoice_id = il.c_invoice_id " +
					"						inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
					"						inner join m_product as p on p.m_product_id = il.m_product_id " +
					"						where m_brochure_id = ? and il.ad_org_id = ? " +
					"							and i.dateinvoiced::date between ?::date and ?::date " +
					"							and i.docstatus in ('CO','CL') " +
					"							and i.issotrx = '"+getIsSOTrx()+"') as a " +
					"group by m_product_id,product_value,product_name,pricestd) as a2 ) as a3";
		sql += " order by "+getOrderByColumn()+getOrder();
		return sql;
	}

	protected String getOrderByColumn(){
		String column = "";
		if(getOrderBy().equals("P")){
			column = " product_value ";
		} 
		else if(getOrderBy().equals("AP")){
			column = " averageprice ";
		}
		else if(getOrderBy().equals("PL")){
			column = " pricestd ";
		}
		else if(getOrderBy().equals("Q")){
			column = " qtyinvoiced ";
		}
		else if(getOrderBy().equals("TP")){
			column = " totalpricestd ";
		}
		else{
			column = " totalpriceentered ";
		}
		return column;
	}
	
	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
	
	@Override
	protected Object[] getParameters() {
		return new Object[]{getBrochureID(), getOrgID(), getDateFrom(), getDateTo()};
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected Integer getBrochureID() {
		return brochureID;
	}

	protected void setBrochureID(Integer brochureID) {
		this.brochureID = brochureID;
	}

	protected String getOrderBy() {
		return orderBy;
	}

	protected void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	protected String getOrder() {
		return order;
	}

	protected void setOrder(String order) {
		this.order = order;
	}

	protected String getIsSOTrx() {
		return isSOTrx;
	}

	protected void setIsSOTrx(String isSOTrx) {
		this.isSOTrx = isSOTrx;
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
