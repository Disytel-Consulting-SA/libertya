package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class InvoicesWithoutInOutDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** Organización */
	private Integer orgID;
	
	/** Entidad Comercial */
	private Integer bPartnerID;
	
	/** Factura */
	private Integer invoiceID;
	
	/** Fechas */
	private Timestamp dateFrom;
	private Timestamp dateTo;
	
	/** Transacción de ventas */
	private Boolean isSOTrx;
	
	public InvoicesWithoutInOutDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public InvoicesWithoutInOutDataSource(Properties ctx, Integer orgID,
			Integer bPartnerID, Integer invoiceID, Timestamp dateFrom,
			Timestamp dateTo, Boolean isSOTrx, String trxName) {
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setbPartnerID(bPartnerID);
		setInvoiceID(invoiceID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setIsSOTrx(isSOTrx);
	}
	
	@Override
	protected String getQuery() {
		String qtyColumnName = "qty";
		StringBuffer sqlIsSOTrx = new StringBuffer();
		StringBuffer whereClauseIsSOTrx = new StringBuffer();
		// En ventas se deben buscar los pendientes de los
		// pedidos relacionados con las facturas
		if(getIsSOTrx().booleanValue()){
			sqlIsSOTrx.append(" INNER JOIN c_orderline as ol ON ol.c_orderline_id = il.c_orderline_id ");
			sqlIsSOTrx.append(" INNER JOIN c_order as ord ON ord.c_order_id = ol.c_order_id ");
			whereClauseIsSOTrx.append("ord.docstatus NOT IN ('DR','IP','IN')");
			qtyColumnName = "ol.qtyreserved";
		}
		// En compras se debe restar lo remitido de las facturas en mmatchinv
		else{
			sqlIsSOTrx.append(" LEFT JOIN (SELECT c_invoiceline_id, sum(qty) as qty " +
									"FROM m_matchinv as mi " +
									"WHERE ad_client_id = "+Env.getAD_Client_ID(getCtx())+
									" GROUP BY c_invoiceline_id) as mi ON mi.c_invoiceline_id = il.c_invoiceline_id ");
			qtyColumnName = "il.qtyinvoiced - coalesce(mi.qty,0)";
		}
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT i.c_invoice_id, o.value as org_value, o.name as org_name, dt.name as doctypename, i.dateinvoiced, i.documentno, bp.value as bp_value, bp.name as bp_name, il.line, p.value as product_value, p.name as product_name, "+qtyColumnName+" as qty, il.priceactual as price ");
		sql.append(" FROM c_invoice as i ");
		sql.append(" INNER JOIN c_invoiceline as il ON il.c_invoice_id = i.c_invoice_id ");
		sql.append(" INNER JOIN m_product as p ON p.m_product_id = il.m_product_id ");
		sql.append(" INNER JOIN c_bpartner as bp ON bp.c_bpartner_id = i.c_bpartner_id ");
		sql.append(" INNER JOIN ad_org as o ON o.ad_org_id = i.ad_org_id ");
		sql.append(" INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id ");
		sql.append(sqlIsSOTrx);
		// Where Clause
		sql.append(" WHERE ");
		sql.append(getWhereClause());
		sql.append(" AND ").append(qtyColumnName).append(" > 0 ");
		sql.append(" AND ").append(whereClauseIsSOTrx.toString());
		sql.append(" ORDER BY i.dateinvoiced, i.documentno, il.line ");
		return sql.toString();
	}

	protected String getWhereClause(){
		StringBuffer whereClause = new StringBuffer(" i.ad_client_id = ? ");
		whereClause.append(" AND i.issotrx = '").append(getIsSOTrx().booleanValue()?"Y":"N").append("'");
		whereClause.append(" AND dt.docbasetype IN ('API','ARI') ");
		whereClause.append(" AND dt.doctypekey NOT IN ('RTR', 'RTI', 'RCR', 'RCI') ");
		if(!Util.isEmpty(getOrgID(), true)){
			whereClause.append(" AND i.ad_org_id = ? ");
		}
		if(getDateFrom() != null){
			whereClause.append(" AND i.dateinvoiced::date >= ?::date ");
		}
		if(getDateTo() != null){
			whereClause.append(" AND i.dateinvoiced::date <= ?::date ");
		}
		if(!Util.isEmpty(getbPartnerID(), true)){
			whereClause.append(" AND i.c_bpartner_id = ? ");
		}
		if(!Util.isEmpty(getInvoiceID(), true)){
			whereClause.append(" AND i.c_invoice_id = ? ");
		}
		whereClause.append(" AND i.docstatus IN ('CO','CL') ");
		return whereClause.toString();
	}
	
	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(!Util.isEmpty(getOrgID(), true)){
			params.add(getOrgID());
		}
		if(getDateFrom() != null){
			params.add(getDateFrom());
		}
		if(getDateTo() != null){
			params.add(getDateTo());
		}
		if(!Util.isEmpty(getbPartnerID(), true)){
			params.add(getbPartnerID());
		}
		if(!Util.isEmpty(getInvoiceID(), true)){
			params.add(getInvoiceID());
		}
		return params.toArray();
	}

	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
	
	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected Integer getbPartnerID() {
		return bPartnerID;
	}

	protected void setbPartnerID(Integer bPartnerID) {
		this.bPartnerID = bPartnerID;
	}

	protected Integer getInvoiceID() {
		return invoiceID;
	}

	protected void setInvoiceID(Integer invoiceID) {
		this.invoiceID = invoiceID;
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

	protected Boolean getIsSOTrx() {
		return isSOTrx;
	}

	protected void setIsSOTrx(Boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
