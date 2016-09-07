package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;

import org.openXpertya.model.MDocType;

public class SalesByHourDataSource extends QueryDataSource {

	/** ID de Organización */
	private Integer orgID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	public SalesByHourDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public SalesByHourDataSource(Integer orgID, Timestamp dateFrom, Timestamp dateTo, String trxName) {
		super(trxName);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
	}

	@Override
	protected String getQuery() {
		String sql = "select hour, total::numeric(22,9), sales::numeric(22,9), cant_tickets::integer, (CASE WHEN total = 0 THEN 0 ELSE sales/total END)::numeric(11,2) as part_total " +
					 "from (select hour, total, sum(grandtotal) as sales, sum(cant_ticket) as cant_tickets " +
					 "		from (select hour, time_ini, time_end, coalesce(grandtotal,0.00) as grandtotal, coalesce(total,0.00) as total, c_invoice_id, coalesce(cant_ticket,0.00) as cant_ticket " +
					 "				from (select h.hour, coalesce(time_ini,h.date) as time_ini, coalesce(time_end, h.date_to) as time_end, grandtotal, c_invoice_id, cant_ticket " +
					 "						from c_salesbyhour_hours as h " +
					 "						left join (select i.c_invoice_id, " +
					 "									(CASE dt.signo_issotrx WHEN 1 THEN 1 ELSE 0 END) as cant_ticket," +
					 "									grandtotal * dt.signo_issotrx as grandtotal, " +
					 "									date_trunc('hour', dateinvoiced) as time_ini, " +
					 "									(date_trunc('hour', dateinvoiced) + interval '1 hour' - interval '1 minute') as time_end, " +
					 "									extract('hour' from dateinvoiced)::integer as hour, " +
					 "									extract('minute' from dateinvoiced)::integer as minute " +
					 "						from c_invoice as i " +
					 "						inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
					 "						where i.ad_org_id = ? AND i.docstatus IN ('CO','CL') AND dt.docbasetype IN ('"+MDocType.DOCBASETYPE_ARInvoice+"','"+MDocType.DOCBASETYPE_ARCreditMemo +"') AND dt.doctypekey NOT IN ('RCI','RCR') AND date_trunc('day',i.dateinvoiced) >= date_trunc('day',?::date) AND date_trunc('day',i.dateinvoiced) <= date_trunc('day',?::date) "
					 		+ "						AND (dt.isfiscal is null OR dt.isfiscal = 'N' OR (dt.isfiscal = 'Y' AND i.fiscalalreadyprinted = 'Y'))) as i on i.hour = h.hour) as a, " +
					 "			(select sum(total) as total " +
					 "				from c_salesbyhour_hours as ht " +
					 "				left join (select extract('hour' from dateinvoiced)::integer as hour, " +
					 "								extract('minute' from dateinvoiced)::integer as minute, " +
					 "								sum(grandtotal * dt.signo_issotrx) as total " +
					 "							from c_invoice as i " +
					 "							inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
					 "							where i.ad_org_id = ? AND i.docstatus IN ('CO','CL') AND dt.docbasetype IN ('"+MDocType.DOCBASETYPE_ARInvoice+"','"+MDocType.DOCBASETYPE_ARCreditMemo +"') AND dt.doctypekey NOT IN ('RCI','RCR') AND date_trunc('day',i.dateinvoiced) >= date_trunc('day',?::date) AND date_trunc('day',i.dateinvoiced) <= date_trunc('day',?::date) " +
					 "									AND (dt.isfiscal is null OR dt.isfiscal = 'N' OR (dt.isfiscal = 'Y' AND i.fiscalalreadyprinted = 'Y'))  " +
					 "							group by extract('hour' from dateinvoiced), extract('minute' from dateinvoiced)) as t on t.hour = ht.hour) as total) as todo " +
					 "group by hour, total) as todo_total " +
					 "order by hour";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { getOrgID(), getDateFrom(), getDateTo(),
				getOrgID(), getDateFrom(), getDateTo() };
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
