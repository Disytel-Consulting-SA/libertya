package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MInOut;
import org.openXpertya.model.MInventory;
import org.openXpertya.model.MMovement;
import org.openXpertya.model.MProductChange;
import org.openXpertya.model.MSplitting;
import org.openXpertya.model.MTransfer;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class ProductMovementsDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** Organización */
	private Integer orgID;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Descripción inicial de Cambio de Artículo */
	private String initProductChangeDescription;
	
	/** Descripción inicial de Fraccionamiento */
	private String initSplittingDescription;
	
	public ProductMovementsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ProductMovementsDataSource(Properties ctx, Integer orgID, Timestamp dateFrom, Timestamp dateTo, String trxName){
		this(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setInitProductChangeDescription(Msg.translate(getCtx(),
				"M_ProductChange_ID"));
		setInitSplittingDescription(Msg.translate(getCtx(), "M_Splitting_ID"));
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT tablename, " +
									"				ad_org_id, " +
									"				orgvalue, " +
									"				orgname, " +
									"				documentno, " +
									"				description, " +
									"				datetrx, " +
									"				m_product_id, " +
									"				qty, " +
									"				type, " +
									"				aditionalType, " +
									"				c_charge_id, " +
									"				chargename, " +
									"				productname, " +
									"				productvalue, " +
									"				order_documentno " +
									"FROM v_product_movements " +
									"WHERE docstatus IN ('CL','CO') " +
									"		AND ad_client_id = ? ");
		if(getOrgID() != null){
			sql.append(" AND ad_org_id = ? ");
		}
		if(getDateFrom() != null){
			sql.append(" AND datetrx >= ?::date ");
		}
		if(getDateTo() != null){
			sql.append(" AND datetrx <= ?::date ");
		}
		sql.append(" ORDER BY productvalue, orgvalue, datetrx ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(getOrgID() != null){
			params.add(getOrgID());
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
	public Object getFieldValue(JRField field) throws JRException {
		Object value = getCurrentRecord().get(field.getName().toUpperCase());
		if(field.getName().equalsIgnoreCase("DESCRIPTION")){
			value = getDescriptionByTable();
		}
		return value;
	}

	protected String getDescriptionByTable(){
		String tablename = (String)getCurrentRecord().get("TABLENAME");
		String description = "";
		if(tablename.equalsIgnoreCase(MTransfer.Table_Name)){
			description = getTransferDescription();			
		}
		else if(tablename.equalsIgnoreCase(MProductChange.Table_Name)){
			description = getProductChangeDescription();
		}
		else if(tablename.equalsIgnoreCase(MInOut.Table_Name)){
			description = getInOutDescription();
		}
		else if(tablename.equalsIgnoreCase(MSplitting.Table_Name)){
			description = getSplittingDescription();
		}
		else if(tablename.equalsIgnoreCase(MInventory.Table_Name)){
			description = getInventoryDescription();
		}
		else if(tablename.equalsIgnoreCase(MMovement.Table_Name)){
			description = getMovementDescription();
		}
		return description;
	}
	
	protected String getTransferDescription(){
		return JasperReportsUtil.getListName(getCtx(),
				MTransfer.TRANSFERTYPE_AD_Reference_ID,
				(String) getCurrentRecord().get("TYPE"))
				+ " - "
				+ JasperReportsUtil.getListName(getCtx(),
						MTransfer.MOVEMENTTYPE_AD_Reference_ID,
						(String) getCurrentRecord().get("ADITIONALTYPE"))
				+ (Util.isEmpty((String) getCurrentRecord().get("CHARGENAME"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("CHARGENAME"))
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("DESCRIPTION"));
	}
	
	protected String getProductChangeDescription(){
		return getInitProductChangeDescription()
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
							+ (String) getCurrentRecord().get("DESCRIPTION"));
	}
	
	protected String getInOutDescription(){
		return (String) getCurrentRecord().get("TYPE")
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("DESCRIPTION"));
	}
	
	protected String getSplittingDescription(){
		return getInitSplittingDescription()
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("DESCRIPTION")); 
	}
	
	protected String getInventoryDescription(){
		return (String) getCurrentRecord().get("TYPE")
				+ (Util.isEmpty((String) getCurrentRecord().get("CHARGENAME"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("CHARGENAME"))
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("DESCRIPTION"));
	}
	
	protected String getMovementDescription(){
		return (String) getCurrentRecord().get("TYPE")
				+ (Util.isEmpty((String) getCurrentRecord().get("DESCRIPTION"),
						true) ? "" : " - "
						+ (String) getCurrentRecord().get("DESCRIPTION"));
	}
	
	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	protected Timestamp getDateFrom() {
		return dateFrom;
	}

	protected void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	protected Timestamp getDateTo() {
		return dateTo;
	}

	protected void setInitProductChangeDescription(
			String initProductChangeDescription) {
		this.initProductChangeDescription = initProductChangeDescription;
	}

	protected String getInitProductChangeDescription() {
		return initProductChangeDescription;
	}

	protected void setInitSplittingDescription(String initSplittingDescription) {
		this.initSplittingDescription = initSplittingDescription;
	}

	protected String getInitSplittingDescription() {
		return initSplittingDescription;
	}

}
