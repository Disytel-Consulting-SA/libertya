package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MCheckCuitControl;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ChecksByBusinessDataSource extends QueryDataSource {
	
	/** Contexto */
	private Properties ctx;
	
	/** Organización */
	private Integer orgID;
	
	/** Entidad Comercial */
	private Integer bPartnerID;
	
	/** CUIT */
	private String cuit;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Cuenta Bancaria */
	private Integer bankAccountID;
	
	/** Orden */
	private String dateOrder;
	
	/** Nombre de la columna a utilizar dependiendo el tipo de fecha */
	private String dateColumnName;
	
	/** Control de CUIT de Cheques activo */
	private boolean isCheckCUITControlActive;
	
	/** Descripción cuando ingresa CUIT */
	private String name;
	
	/** Sólo Cheques en Cartera */
	private boolean isOnlyChequesEnCartera;
	
	public ChecksByBusinessDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ChecksByBusinessDataSource(Properties ctx, Integer orgID,
			Integer bPartnerID, String cuit, Integer bankAccountID,
			Timestamp dateFrom, Timestamp dateTo, String dateOrder, 
			boolean onlyChequesEnCartera, String trxName) {
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setbPartnerID(bPartnerID);
		setCuit(cuit);
		setBankAccountID(bankAccountID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setDateOrder(dateOrder);
		setDateColumnName(dateOrder.equals("D")?"duedate":"datetrx");
		setCheckCUITControlActive(MCheckCuitControl.isCheckCUITControlActive(
				getCtx(),
				!Util.isEmpty(getOrgID(), true) ? getOrgID() : Env
						.getAD_Org_ID(getCtx()), getTrxName()));
		setName();
		setOnlyChequesEnCartera(onlyChequesEnCartera);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT p.datetrx, " +
				"	p.duedate, " +
				"	p.documentno, " +
				"	ba.description as bank_account, " +
				"	bp.name as description, " +
				"	abs(p.payamt) as amount, " +
				"	coalesce(p.a_name,bp.name) as name, " +
				"	(CASE WHEN p.duedate::date >= current_date THEN abs(p.payamt) ELSE 0.00 END) as balance " +
				"FROM c_payment as p " +
				"INNER JOIN c_bankaccount as ba ON ba.c_bankaccount_id = p.c_bankaccount_id " +
				"INNER JOIN c_bpartner as bp ON bp.c_bpartner_id = p.c_bpartner_id " +
				"WHERE p.ad_client_id = ? AND p.tendertype = 'K' AND p.docstatus IN ('CO','CL') ");
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" AND p.ad_org_id = ? ");
		}
		if(!Util.isEmpty(getbPartnerID(), true)){
			sql.append(" AND p.c_bpartner_id = ? ");
		}
		if(!Util.isEmpty(getCuit(), true)){
				sql.append(" AND translate(p.a_cuit,'-','') = translate('"
						+ getCuit() + "','-','') ");
		}
		if(getDateFrom() != null){	
			sql.append(" AND ");
			sql.append(getDateColumnName());
			sql.append("::date ");
			sql.append(">=");
			sql.append(" ?::date ");
		}
		if(getDateTo() != null){
				sql.append(" AND ").append(getDateColumnName()).append("::date ")
						.append(" <= ?::date ");
		}
		sql.append(Util.isEmpty(getBankAccountID(), true) ? ""
				: " AND p.c_bankaccount_id = ? ");
		if(isOnlyChequesEnCartera()){
			sql.append(" AND ba.ischequesencartera = 'Y' ");
		}
		sql.append(" ORDER BY ");
		sql.append(getDateColumnName());
		sql.append(" ,p.documentno ");
		return sql.toString();
	}
	
	protected void setName(){
		if(!Util.isEmpty(getCuit(), true)){
			if(isCheckCUITControlActive()){
				MCheckCuitControl checkCUITControl = MCheckCuitControl.get(
						getCtx(), !Util.isEmpty(getOrgID(), true) ? getOrgID()
								: Env.getAD_Org_ID(getCtx()), getCuit(),
						getTrxName());
				if(checkCUITControl == null){
					checkCUITControl = MCheckCuitControl.get(getCtx(), 0,
							getCuit(), getTrxName());
				}
				if(checkCUITControl != null){
					setName(checkCUITControl.getNombre());
				}
			}
		}		
	}

	@Override
	public Object getFieldValue(JRField arg0) throws JRException {
		String fieldName = arg0.getName();
		Object output = getCurrentRecord().get(fieldName);
		if(fieldName.equalsIgnoreCase("DESCRIPTION")){
			if(!Util.isEmpty(getCuit(), true)){
				output = !Util.isEmpty(getName(), true) ? getName()
						: getCurrentRecord().get("NAME");
			}
		}
		return output;
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
		if(!Util.isEmpty(getBankAccountID(), true)){
			params.add(getBankAccountID());
		}
		return params.toArray();
	}

	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
	
	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
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

	protected String getCuit() {
		return cuit;
	}

	protected void setCuit(String cuit) {
		this.cuit = cuit;
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

	protected Integer getBankAccountID() {
		return bankAccountID;
	}

	protected void setBankAccountID(Integer bankAccountID) {
		this.bankAccountID = bankAccountID;
	}

	protected String getDateOrder() {
		return dateOrder;
	}

	protected void setDateOrder(String dateOrder) {
		this.dateOrder = dateOrder;
	}

	protected String getDateColumnName() {
		return dateColumnName;
	}

	protected void setDateColumnName(String dateColumnName) {
		this.dateColumnName = dateColumnName;
	}

	protected boolean isCheckCUITControlActive() {
		return isCheckCUITControlActive;
	}

	protected void setCheckCUITControlActive(boolean isCheckCUITControlActive) {
		this.isCheckCUITControlActive = isCheckCUITControlActive;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected boolean isOnlyChequesEnCartera() {
		return isOnlyChequesEnCartera;
	}

	protected void setOnlyChequesEnCartera(boolean isOnlyChequesEnCartera) {
		this.isOnlyChequesEnCartera = isOnlyChequesEnCartera;
	}

}
