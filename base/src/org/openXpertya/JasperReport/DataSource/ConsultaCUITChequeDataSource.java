package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MCheckCuitControl;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ConsultaCUITChequeDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** Organización */
	private Integer orgID;
	
	/** CUIT */
	private String cuit;
	
	public ConsultaCUITChequeDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ConsultaCUITChequeDataSource(Properties ctx, Integer orgID, String cuit, String trxName) {
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setCuit(cuit);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT distinct o.ad_org_id, o.value as org_value, o.name as org_name " +
											 "FROM c_payment as p " +
											 "INNER JOIN ad_org as o ON o.ad_org_id = p.ad_org_id " +
											 "WHERE p.ad_client_id = ? AND p.tendertype = '"
																	+ MPayment.TENDERTYPE_Check + "'");
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" AND p.ad_org_id = ? ");
		}
		sql.append(" ORDER BY o.value ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(!Util.isEmpty(getOrgID(), true)){
			params.add(getOrgID());
		}
		return params.toArray();
	}
	
	public Object getFieldValue(JRField field) throws JRException {
		// Obtiene el valor del campo del registro actual, a partir del nombre del
		// campo del reporte jasper.
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		Integer orgID = (Integer)getCurrentRecord().get("AD_ORG_ID");
		MCheckCuitControl checkCuitOrg = MCheckCuitControl.get(getCtx(),
				orgID, getCuit(), getTrxName());
		if(field.getName().equalsIgnoreCase("limit")){
			value = checkCuitOrg != null ? checkCuitOrg.getCheckLimit()
					: BigDecimal.ZERO;
		}
		else if(field.getName().equalsIgnoreCase("balance")){
			try{			
				value = checkCuitOrg != null ? checkCuitOrg.getBalance(Env
						.getDate()) : BigDecimal.ZERO;
			} catch(Exception e){
				value = BigDecimal.ZERO;
			}
		}
		return value;
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

	protected String getCuit() {
		return cuit;
	}

	protected void setCuit(String cuit) {
		this.cuit = cuit;
	}

}
