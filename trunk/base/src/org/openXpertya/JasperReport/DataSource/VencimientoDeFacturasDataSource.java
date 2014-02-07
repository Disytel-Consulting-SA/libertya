package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DBException;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class VencimientoDeFacturasDataSource extends QueryDataSource {
	
	/** Contexto */
	private Properties ctx;
	/** Organización */
	private Integer orgID = null;
	/** Entidad Comercial */
	private Integer bpartnerID = null;
	/** Fecha Desde */
	private Timestamp dateFrom = null; 
	/** Fecha Hasta */
	private Timestamp dateTo = null;
	/** Filtro de fecha */
	private String dateFilter = null;
	/** Tipo de Transacción */
	private String trxType = null;
	/** Subtotales Por */
	private String subtotales_por = null;
	
	public VencimientoDeFacturasDataSource(Properties ctx, Timestamp dateFrom, Timestamp dateTo, Integer orgID, Integer bPartnerID, String dateFilter, String trxType, String trxName, String subtotales_por) throws DBException{
		super(trxName);
		setCtx(ctx);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setDateFilter(dateFilter);
		setOrgID(orgID);
		setBpartnerID(bPartnerID);
		setTrxType(trxType);
		setSubtotalesPor(subtotales_por);
	}



	@Override
	protected String getQuery() {
		String invoiceDateColumn = null;
		String paymentDateColumn = null;
		
		StringBuffer  var1 = new StringBuffer();
		var1.append("SELECT dt.printname, ");
		var1.append("       bp.name AS proveedor, ");
		var1.append("       pt.name as regla, ");
		var1.append("       ci.description, ");
		var1.append("       oi.documentno, ");
		var1.append("       oi.c_bpartner_id, ");
		var1.append("       oi.c_invoice_id, ");
		var1.append("       oi.c_invoicepayschedule_id, ");
		var1.append("       oi.c_currency_id, ");
		var1.append("       oi.issotrx, ");
		var1.append("       TRUNC(oi.dateinvoiced) as dateinvoiced, ");
		var1.append("       oi.netdays, ");
		var1.append("       oi.duedate, ");
		var1.append("       oi.daysdue, ");
		var1.append("       case when substring(dt.docbasetype from 3 for 1)='I' then oi.openamt else oi.openamt*-1 end as openamt , ");
		var1.append("       oi.ad_org_id, ");
		var1.append("       CASE ");
		var1.append("         WHEN daysdue IS NULL THEN '' ");
		var1.append("         WHEN daysdue <=- 30 THEN '3 - Mayor 30 dias' ");
		var1.append("         WHEN daysdue >- 30 ");
		var1.append("              AND daysdue <=- 7 THEN '2 - Proximos 30d' ");
		var1.append("         WHEN daysdue >- 7 ");
		var1.append("              AND daysdue <= 0 THEN '1 - Esta semana' ");
		var1.append("         WHEN daysdue > 0 THEN '0 - Vencidos' ");
		var1.append("       END     AS estado ");
		var1.append("FROM   rv_openitem oi ");
		var1.append("       INNER JOIN c_bpartner bp ");
		var1.append("         ON ( oi.c_bpartner_id = bp.c_bpartner_id ) ");
		var1.append("       INNER JOIN c_doctype dt ");
		var1.append("         ON ( oi.c_doctypetarget_id = dt.c_doctype_id ) ");
		var1.append("       INNER JOIN c_paymentterm pt ");
		var1.append("         ON ( pt.c_paymentterm_id = oi.c_paymentterm_id ) ");
		var1.append("       INNER JOIN c_invoice ci ");
		var1.append("         ON ( ci.c_invoice_id = oi.c_invoice_id) "); 
		var1.append("WHERE  oi.ad_client_id=? ");
		if(!getTrxType().equals("B")){
			var1.append(" AND oi.issotrx = '")
					.append(getTrxType().equals("C") ? "Y" : "N").append("' ");
		}
		if (!Util.isEmpty(getOrgID(), true)) {
			var1.append("       AND oi.ad_org_id = ? ");
			}
		if (!Util.isEmpty(getBpartnerID(), true)) {
			var1.append("       AND oi.c_bpartner_id = ? ");
		}
		if (getDateFrom()!=null){
			invoiceDateColumn = getDateFilter().equals("T")?"oi.dateinvoiced":"oi.duedate";
			var1.append(" AND ").append(invoiceDateColumn).append(" >= ?::date ");
		}
		if (getDateTo()!=null){
			invoiceDateColumn = invoiceDateColumn != null ? invoiceDateColumn
					: getDateFilter().equals("T") ? "oi.dateinvoiced"
							: "oi.duedate";
			var1.append(" AND ").append(invoiceDateColumn).append(" <= ?::date ");
		}		
		var1.append("       AND oi.docstatus IN ( 'CO', 'CL' ) ");
		var1.append("       AND oi.openamt > 0 ");
		
		var1.append("UNION ");
		var1.append("SELECT dt.printname, ");
		var1.append("       bp.name              AS proveedor, ");
		var1.append("       pt.name as regla, ");
		var1.append("       ci.description, ");
		var1.append("       pi.documentno, ");
		var1.append("       pi.c_bpartner_id, ");
		var1.append("       NULL, ");
		var1.append("       NULL, ");
		var1.append("       pi.c_currency_id, ");
		var1.append("       pi.isreceipt, ");
		var1.append("       TRUNC(pi.datetrx)  as dateinvoiced, ");
		var1.append("       0, ");
		var1.append("       pi.dateacct, ");
		var1.append("       NULL, ");
		var1.append("       pi.availableamt *- 1 AS pago, ");
		var1.append("       pi.ad_org_id, ");
		var1.append("       ''                   AS estado ");
		var1.append("FROM   rv_payment pi ");
		var1.append("       INNER JOIN c_bpartner bp ");
		var1.append("         ON ( pi.c_bpartner_id = bp.c_bpartner_id ) ");
		var1.append("       INNER JOIN c_doctype dt ");
		var1.append("         ON ( pi.c_doctype_id = dt.c_doctype_id ) ");
		var1.append("       INNER JOIN c_invoice ci ");
		var1.append("         ON ( ci.c_invoice_id = pi.c_invoice_id) "); 
		var1.append("       INNER JOIN c_paymentterm pt ");
		var1.append("         ON ( pt.c_paymentterm_id = ci.c_paymentterm_id ) ");
		var1.append("            WHERE  ");
		var1.append("            pi.docstatus IN ( 'CO', 'CL' ) ");
		var1.append("            AND pi.ad_client_id = ?  ");
		if(!getTrxType().equals("B")){
			var1.append(" AND pi.isreceipt = '")
					.append(getTrxType().equals("C") ? "Y" : "N").append("' ");
		}
		if (!Util.isEmpty(getOrgID(), true)) {
			var1.append("            AND pi.ad_org_id=? ");
		}
		if (!Util.isEmpty(getBpartnerID(), true)) {
			var1.append("       AND pi.c_bpartner_id = ? ");
		}
		var1.append("            AND pi.availableamt > 0 ");
		if (getDateFrom()!=null){
			paymentDateColumn = getDateFilter().equals("T")?"pi.datetrx":"pi.dateacct";
			var1.append(" AND ").append(paymentDateColumn).append(" >= ?::date ");
		}
		if (getDateTo()!=null){
			paymentDateColumn = paymentDateColumn != null ? paymentDateColumn
					: getDateFilter().equals("T") ? "oi.dateinvoiced"
							: "oi.duedate";
			var1.append(" AND ").append(paymentDateColumn).append(" <= ?::date ");
		}		
		var1=new StringBuffer("Select * from (").append(var1);
		
		if (new String ("Fecha").equals(getSubtotales_por())){
			var1.append(") AS T1 ORDER  BY dateinvoiced ");
		}else {
			var1.append(") AS T1 ORDER  BY proveedor ");
		}

		return var1.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if (!Util.isEmpty(getOrgID(), true)) {
			params.add(getOrgID());
		}
		if (!Util.isEmpty(getBpartnerID(), true)) {
			params.add(getBpartnerID());
		}
		if (getDateFrom()!=null){
			params.add(getDateFrom());
		}
		if (getDateTo()!=null){
			params.add(getDateTo());
		}		
		
		params.add(Env.getAD_Client_ID(getCtx()));
		if (!Util.isEmpty(getOrgID(), true)) {
			params.add(getOrgID());
		}
		if (!Util.isEmpty(getBpartnerID(), true)) {
			params.add(getBpartnerID());
		}
		if (getDateFrom()!=null){
			params.add(getDateFrom());
		}
		if (getDateTo()!=null){
			params.add(getDateTo());
		}
		return params.toArray();
	}

	private void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	private Properties getCtx() {
		return ctx;
	}

	private void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	private Integer getOrgID() {
		return orgID;
	}

	private void setBpartnerID(Integer bpartnerID) {
		this.bpartnerID = bpartnerID;
	}

	private Integer getBpartnerID() {
		return bpartnerID;
	}

	private void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	private Timestamp getDateFrom() {
		return dateFrom;
	}

	private void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	private Timestamp getDateTo() {
		return dateTo;
	}

	private void setDateFilter(String dateFilter) {
		this.dateFilter = dateFilter;
	}

	private String getDateFilter() {
		return dateFilter;
	}

	private void setTrxType(String trxType) {
		this.trxType = trxType;
	}

	private String getTrxType() {
		return trxType;
	}
	
	private void setSubtotalesPor(String subtotales_por) {
		this.subtotales_por = subtotales_por;		
	}
	
	private String getSubtotales_por(){
		return subtotales_por;
	}

}

