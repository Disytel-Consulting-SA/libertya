package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MPayment;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class RejectedChecksDataSource extends QueryDataSource {

	/** Organización */
	private Integer orgID;
	/** Entidad Comercial */
	private Integer bPartnerID;
	/** Fecha Desde */
	private Timestamp dateFrom;
	/** Fecha Hasta */
	private Timestamp dateTo;
	/** Estado del Documento */
	private String docStatus;
	/** Estado del Cheque */
	private String checkStatus;
	/** Transacción de ventas */
	private String isReceipt;
	/** Contexto */
	private Properties ctx;
	
	public RejectedChecksDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public RejectedChecksDataSource(Properties ctx, Integer orgID,
			Integer bpartnerID, Timestamp dateFrom, Timestamp dateTo, 
			String docStatus, String checkStatus, String isReceipt, 
			String trxName) {
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setbPartnerID(bpartnerID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setDocStatus(docStatus);
		setCheckStatus(checkStatus);
		setIsReceipt(isReceipt);
	}
	
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().toUpperCase().equals("CHECKSTATUS")){
			value = MRefList.getListName(getCtx(),
					MPayment.CHECKSTATUS_AD_Reference_ID, (String) value);
		}
		return value;
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select p.datetrx,	" +
					"		coalesce(p.checkno, p.documentno) as checkno," +
					"		p.description, " +
					"		p.a_bank, " +
					"		p.duedate, " +
					"		bp.value, " +
					"		bp.name, " +
					"		p.payamt, " +
					"		p.checkstatus, " +
					"		p.rejecteddate, " +
					"		p.rejectedcomments " +
					"from c_payment as p " +
					"inner join c_bpartner as bp on bp.c_bpartner_id = p.c_bpartner_id " +
					"where p.ad_client_id = ?");
		sql.append(" and p.isreceipt = '").append(getIsReceipt()).append("' ");
		sql.append(" and p.tendertype = '").append(MPayment.TENDERTYPE_Check).append("' ");
		sql.append(" and p.docstatus NOT IN ('DR','IP') ");
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" and p.ad_org_id = ? ");
		}
		if(!Util.isEmpty(getbPartnerID(), true)){
			sql.append(" and p.c_bpartner_id = ? ");
		}
		if(getDateFrom() != null){
			sql.append(" and p.datetrx::date >= ?::date ");
		}
		if(getDateTo() != null){
			sql.append(" and p.datetrx::date <= ?::date ");
		}
		if(!Util.isEmpty(getDocStatus(), true)){
			sql.append(" and p.docstatus = '").append(getDocStatus()).append("' ");
		}
		if(!Util.isEmpty(getCheckStatus(), true)){
			sql.append(" and p.checkstatus = '").append(getCheckStatus()).append("' ");
		}
		sql.append(" ORDER BY p.datetrx, p.duedate, bp.value ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(!Util.isEmpty(getOrgID(), true)){
			params.add(getOrgID());
		}
		if(!Util.isEmpty(getbPartnerID(), true)){
			params.add(getbPartnerID());
		}
		if(getDateFrom() != null){
			params.add(getDateFrom());
		}
		if(getDateTo() != null){
			params.add(getDateTo());
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

	protected String getDocStatus() {
		return docStatus;
	}

	protected void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	protected String getCheckStatus() {
		return checkStatus;
	}

	protected void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}

	protected String getIsReceipt() {
		return isReceipt;
	}

	protected void setIsReceipt(String isReceipt) {
		this.isReceipt = isReceipt;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
