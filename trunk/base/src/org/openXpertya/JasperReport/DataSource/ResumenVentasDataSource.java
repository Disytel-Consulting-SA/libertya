package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public abstract class ResumenVentasDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** ID de organización */
	private Integer orgID;
	
	/** Fecha de inicio o único */
	private Timestamp dateFrom;
	
	/** Fecha de fin */
	private Timestamp dateTo;
	
	/** Usuario */
	private Integer userID;
	
	/** TPV */
	private Integer posID;
	
	/** Sólo Notas de Crédito */
	private boolean onlyCN = false;
	
	/** Sólo Notas de Débito */
	private boolean onlyDN = false;
	
	/** Formatter para las fechas para las llamadas a las funciones */
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ResumenVentasDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setUserID(userID);
		setPosID(posID);
	}
	
	public ResumenVentasDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN, boolean onlyDN) {
		this(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		setOnlyCN(onlyCN);
		setOnlyDN(onlyDN);
	}

	@Override
	protected String getQuery() {
		String groupFields = getGroupFields();
		StringBuffer sql = new StringBuffer(
				"SELECT "+groupFields+", sum(amount) as amount " +
				"FROM (SELECT s.* " +
				"		FROM "+getDSDataTable()+" as s " +
				"		WHERE s.ad_client_id = ? " + getDocumentsWhereClause());
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
		if(getDSWhereClauseParams() != null){
			params.addAll(getDSWhereClauseParams());
		}
 		return params.toArray();
	}
	
	protected Integer getOrgIDParameterCall(){
		return !Util.isEmpty(getOrgID(), true)?getOrgID():-1;
	}
	
	protected Integer getPOSIDParameterCall(){
		return !Util.isEmpty(getPosID(), true)?getPosID():-1;
	}
	
	protected Integer getUserIDParameterCall(){
		return !Util.isEmpty(getUserID(), true)?getUserID():-1;
	}
	
	protected String getDateParameterCall(Timestamp date){
		StringBuffer parameter = new StringBuffer();
		if(date != null){
			parameter.append("'");
			parameter.append(getDateFormat().format(date));
			parameter.append("'");
		}
		else{
			parameter.append("null");
		}
		parameter.append("::date");
		return parameter.toString();
	}
	
	protected String getDateFromParameterCall(){
		return getDateParameterCall(getDateFrom());
	}
	
	protected String getDateToParameterCall(){
		return getDateParameterCall(getDateTo());
	}
	
	protected String getDocumentsWhereClause(){
		String docsClause = "";
		// Sólo NC, entonces filtro para las NC
		if(isOnlyCN()){
			docsClause = getCreditsNotesFilter();
		}
		// Sólo ND, entonces filtro para las ND
		else if(isOnlyDN()){
			docsClause = getDebitsNotesFilter();
		}
		return docsClause;
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
		// Es cuenta corriente,
		if(tenderType.equals("CC")){
			tenderTypeDescription = "Cuenta Corriente";
		}
		else if(trxType != null && trxType.equals("I")){
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
	 * @return condición de filtro para informar notas de crédito
	 */
	protected String getCreditsNotesFilter(){
		return " AND (s.trxtype IN ('PA','CAIA') OR EXISTS (SELECT inv.c_invoice_id FROM c_invoice inv INNER JOIN c_doctype doc ON doc.c_doctype_id = inv.c_doctypetarget_id WHERE s.c_invoice_id = inv.c_invoice_id AND doc.docbasetype = '"
				+ MDocType.DOCBASETYPE_ARCreditMemo + "')) ";
	}
	
	/**
	 * @return condición de filtro para informar notas de crédito
	 */
	protected String getDebitsNotesFilter(){
		return " AND (s.trxtype = 'ND' OR EXISTS (SELECT inv.c_invoice_id FROM c_invoice inv INNER JOIN c_doctype doc ON doc.c_doctype_id = inv.c_doctypetarget_id WHERE s.c_invoice_id = inv.c_invoice_id AND position('CDN' in doc.doctypekey) = 1))";
	}
	
	/**
	 * @return tabla data source donde obtener la información
	 */
	protected String getDSDataTable(){
		return getParametersCall("v_dailysales_v2_filtered");
	}
	
	protected String getParametersCall(String functionName){
		return functionName+"("+getOrgIDParameterCall() + ", "
				+ getPOSIDParameterCall() + ", " + getUserIDParameterCall()
				+ ", " + getDateFromParameterCall() + ", "
				+ getDateToParameterCall() + ", " + getDateFromParameterCall()
				+ ", " + getDateToParameterCall() + ", "
				+ addInvoiceDateClause()+")";
	}
	
	/**
	 * @return suma del resultado de la query
	 * @throws Exception
	 */
	public BigDecimal getTotalAmt() throws Exception{
		StringBuffer sql = new StringBuffer("select sum(amount) as amount FROM (");
		sql.append(getQuery());
		sql.append(") as ce ");
		BigDecimal amt = (BigDecimal) DB.getSQLObject(getTrxName(),
				sql.toString(), getParameters());
		return amt == null?BigDecimal.ZERO:amt;
	}
	
	@Override
	protected boolean isQueryNoConvert(){
		return true;
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

	protected Integer getUserID() {
		return userID;
	}

	protected void setUserID(Integer userID) {
		this.userID = userID;
	}

	protected Integer getPosID() {
		return posID;
	}

	protected void setPosID(Integer posID) {
		this.posID = posID;
	}

	protected boolean isOnlyCN() {
		return onlyCN;
	}

	protected void setOnlyCN(boolean onlyCN) {
		this.onlyCN = onlyCN;
	}

	protected boolean isOnlyDN() {
		return onlyDN;
	}

	protected void setOnlyDN(boolean onlyDN) {
		this.onlyDN = onlyDN;
	}

	protected boolean addInvoiceDateClause(){
		return true;
	}

	protected DateFormat getDateFormat() {
		return dateFormat;
	}

	protected void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}
