package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MDocumentDiscount;

public class SeguimientoDescuentosManualesDataSource extends QueryDataSource {

	/** ID de organización */
	private Integer orgID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/**
	 * Porcentaje mínimo de filtro de información. Se muestra la información con
	 * porcentaje de descuento mayor a este valor
	 */
	private BigDecimal percentage;
	
	public SeguimientoDescuentosManualesDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public SeguimientoDescuentosManualesDataSource(String trxName, Integer orgID, Timestamp dateFrom, Timestamp dateTo, BigDecimal percentage) {
		super(trxName);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setPercentage(percentage);
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select * " +
											"from (select c_invoice_id, " +
												"documentno, " +
												"c_bpartner_id, " +
												"nombrecli, " +
												"discount_responsable, " +
												"cond_vta, " +
												"dateinvoiced, " +
												"grandtotal, " +
												"coalesce(sum(discountbaseamt),0.00) as discount_base_amt, " +
												"coalesce(sum(discountamt),0.00) as discount_amt, " +
												"coalesce(sum(discountPerc),0.00) as discount_perc " +
											"from (select i.c_invoice_id, " +
														"i.documentno, " +
														"i.c_bpartner_id, " +
														"coalesce(i.nombrecli, bp.value || ' ' || bp.name) as nombrecli, " +
														"(CASE WHEN u.description is not null AND length(trim(u.description)) > 0 THEN u.description ELSE u.name END)  as discount_responsable, " +
														"grandtotal, " +
														"discountbaseamt, " +
														"abs(discountamt) as discountamt, " +
														"(abs(discountamt)/discountbaseamt)::numeric(11,2) as discountPerc, " +
														"i.dateinvoiced, " +
														"(SELECT payment_medium_name " +
															"FROM c_allocation_detail_v as ad " +
															"WHERE ad.c_invoice_id = i.c_invoice_id " +
															"LIMIT 1) as cond_vta " +
													"from c_invoice as i " +
													"inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id " +
													"inner join c_doctype as dt on dt.c_doctype_id = i.c_doctypetarget_id " +
													"inner join c_documentdiscount as dd on dd.c_invoice_id = i.c_invoice_id " +
													"inner join ad_user as u on u.ad_user_id = dd.createdby " +
													"where i.ad_org_id = ? " +
															"AND i.docstatus IN ('CO','CL') " +
															"AND dt.docbasetype = '"+MDocType.DOCBASETYPE_ARInvoice+"' " +
															"AND dt.doctypekey NOT IN ('RCI') " +
															"AND discountamt > 0 " +
															"AND discountbaseamt <> 0 " +
															"AND c_documentdiscount_parent_id is null " +
															"AND discountkind IN ('"+MDocumentDiscount.DISCOUNTKIND_ManualDiscount+"','"+MDocumentDiscount.DISCOUNTKIND_ManualGeneralDiscount+"') " +
															"AND date_trunc('day',i.dateinvoiced) >= date_trunc('day',?::date) " +
															"AND date_trunc('day',i.dateinvoiced) <= date_trunc('day',?::date)) as d " +
											"group by c_invoice_id,	documentno, c_bpartner_id, nombrecli, discount_responsable, dateinvoiced, cond_vta, grandtotal " +
											"order by dateinvoiced, documentno) as a " +
											"where discount_perc >= (?/100)::numeric(11,2) ");
		
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { getOrgID(), getDateFrom(), getDateTo(), getPercentage() };
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

	protected BigDecimal getPercentage() {
		return percentage;
	}

	protected void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

}
