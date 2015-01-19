package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Util;

public class OrderReceivesPaymentReportDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** Organización */
	private Integer orgID;
	
	/** Transacción de ventas */
	private boolean isSOTrx;
	
	/** Mostrar Adelantados */
	private boolean showPrePayments;
	
	/** Cajas diarias */
	private DeclaracionValoresDTO valoresDTO;
	
	/** TPV */
	private Integer posID;
	
	/** Fecha desde */
	private Timestamp dateFrom;

	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Grupo de EC */
	private Integer bpGroupID;
	
	public OrderReceivesPaymentReportDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public OrderReceivesPaymentReportDataSource(Properties ctx, Integer orgID,
			boolean isSOTrx, boolean showPrePayments,
			DeclaracionValoresDTO valoresDTO, Integer posID, 
			Timestamp dateFrom, Timestamp dateTo, 
			Integer bpGroupID, String trxName) {
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setSOTrx(isSOTrx);
		setShowPrePayments(showPrePayments);
		setValoresDTO(valoresDTO);
		setPosID(posID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setBpGroupID(bpGroupID);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select ah.documentno, ah.datetrx, ah.grandtotal, bp.value, bp.name " +
											"from c_allocationhdr as ah " +
											"inner join c_bpartner as bp on bp.c_bpartner_id = ah.c_bpartner_id " +
											"where ah.ad_org_id = ?	" +
											"	AND ah.allocationtype in " +getOPRCSetWhereClause() +
											"	AND ah.docstatus IN ('CO','CL')");
		if(getDateFrom() != null){
			sql.append("	AND ah.datetrx::date >= ?::date ");
		}
		if(getDateTo() != null){
			sql.append("	AND ah.datetrx::date <= ?::date ");
		}
		if(!Util.isEmpty(getBpGroupID(), true)){
			sql.append("	AND bp.c_bp_group_id = ? ");
		}
		if (!Util.isEmpty(getPosID(), true) && getValoresDTO() != null
				&& getValoresDTO().getJournalIDs().size() > 0) {
			sql.append("	AND ah.c_posjournal_id IN ").append(
					getValoresDTO().getJournalIDs().toString()
							.replaceAll("]", ")").replaceAll("\\[", "("));
		}
		sql.append(" ORDER BY ah.documentno ");		
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getOrgID());
		if(getDateFrom() != null){
			params.add(getDateFrom());
		}
		if(getDateTo() != null){
			params.add(getDateTo());
		}
		if(!Util.isEmpty(getBpGroupID(), true)){
			params.add(getBpGroupID());
		}
		return params.toArray();
	}
	
	protected String getOPRCSetWhereClause(){
		return "("
				+ (isSOTrx() ? "'RC'" : "'OP'")
				+ (isShowPrePayments() ? (isSOTrx() ? ",'RCA'" : ",'OPA'") : "")
				+ ")";
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected boolean isSOTrx() {
		return isSOTrx;
	}

	protected void setSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}

	protected boolean isShowPrePayments() {
		return showPrePayments;
	}

	protected void setShowPrePayments(boolean showPrePayments) {
		this.showPrePayments = showPrePayments;
	}

	protected DeclaracionValoresDTO getValoresDTO() {
		return valoresDTO;
	}

	protected void setValoresDTO(DeclaracionValoresDTO valoresDTO) {
		this.valoresDTO = valoresDTO;
	}

	protected Integer getPosID() {
		return posID;
	}

	protected void setPosID(Integer posID) {
		this.posID = posID;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
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

	protected Integer getBpGroupID() {
		return bpGroupID;
	}

	protected void setBpGroupID(Integer bpGroupID) {
		this.bpGroupID = bpGroupID;
	}

	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
}
