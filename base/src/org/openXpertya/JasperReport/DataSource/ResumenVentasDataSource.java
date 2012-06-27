package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.util.Env;

public abstract class ResumenVentasDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** ID de organización */
	private Integer orgID;
	
	/** Fecha de inicio o único */
	private Timestamp dateFrom;
	
	/** Fecha de fin */
	private Timestamp dateTo;
	
	public ResumenVentasDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo) {
		super(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
	}

	@Override
	protected String getQuery() {
		String groupFields = getGroupFields();
		StringBuffer sql = new StringBuffer(
				"SELECT "+groupFields+", sum(amount) as amount FROM (SELECT * FROM v_dailysales WHERE ad_client_id = ? AND docstatus <> 'DR' AND ad_org_id = ? AND issotrx = 'Y' AND "
						+ getDateWhereClause());
		sql.append(getDSWhereClause() == null?"":getDSWhereClause());
		sql.append(" ) as ventas ");
		sql.append(" GROUP BY ").append(groupFields);
		sql.append(" ORDER BY ").append(groupFields);
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		params.add(getOrgID());
		params.add(getDateFrom());
		if(getDateTo() != null){
			params.add(getDateTo());
		}
		if(getDSWhereClauseParams() != null){
			params.addAll(getDSWhereClauseParams());
		}
 		return params.toArray();
	}
	
	/**
	 * @return la cláusula where de las fechas
	 */
	protected String getDateWhereClause(){
		StringBuffer dateClause = new StringBuffer(" datetrx >= ?::date ");
		if(getDateTo() != null){
			dateClause.append("  AND datetrx <= ?::date ");
		}
		return dateClause.toString();
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().toUpperCase().equals("LINE_DESCRIPTION")){
			value = getLineDescription();
		}
		return value;
	}
	
	/**
	 * @return la descripción del campo tendertype
	 */
	public String getTenderTypeDescription(){
		String tenderType = (String)getCurrentRecord().get("TENDERTYPE");
		String trxType = (String)getCurrentRecord().get("TRXTYPE");
		String tenderTypeDescription = "";
		// Es cuenta corriente
		if(tenderType.equals("CC")){
			tenderTypeDescription = "Cuenta Corriente";
		}
		else if(trxType.equals("I")){
			tenderTypeDescription = JasperReportsUtil.getListName(getCtx(),
					MDocType.DOCBASETYPE_AD_Reference_ID, tenderType);
		}
		else {
			if(tenderType.equals(MPOSPaymentMedium.TENDERTYPE_Credit)){
				tenderType = "N";
			}
			tenderTypeDescription = JasperReportsUtil.getListName(getCtx(),
					MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID, tenderType);
		}
		return tenderTypeDescription;
	}
	
	/**
	 * @return cláusula where con condiciones propias de las subclases
	 */
	protected abstract String getDSWhereClause();

	/**
	 * @return parámetros de la cláusula where con condiciones propias de las
	 *         subclases
	 */
	protected abstract List<Object> getDSWhereClauseParams();

	/**
	 * @return la lista entre comas (sin agregar la última) de los nombres de
	 *         las columnas por las cuales agrupar la suma del monto
	 */
	protected abstract String getGroupFields();
	
	/**
	 * @return ĺa descripción de la línea a imprimir en el reporte
	 */
	protected abstract String getLineDescription();
	
	public void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	public Integer getOrgID() {
		return orgID;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}

}
