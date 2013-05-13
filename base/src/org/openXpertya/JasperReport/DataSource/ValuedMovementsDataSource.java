package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MTransfer;
import org.openXpertya.util.Env;

public class ValuedMovementsDataSource extends ProductMovementsDataSource {

	/** Almacén */
	private Integer warehouseID;
	
	/** Versión de Lista de Precios */
	private Integer priceListVersionID;
	
	/** Cargo */
	private Integer chargeID;
	
	public ValuedMovementsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public ValuedMovementsDataSource(Properties ctx, Integer orgID,
			Timestamp dateFrom, Timestamp dateTo, String trxName) {
		super(ctx, orgID, dateFrom, dateTo, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public ValuedMovementsDataSource(Properties ctx, Integer orgID,
			Timestamp dateFrom, Timestamp dateTo, Integer warehouseID,
			Integer priceListVersionID, Integer chargeID, String trxName) {
		this(ctx, orgID, dateFrom, dateTo, trxName);
		setWarehouseID(warehouseID);
		setPriceListVersionID(priceListVersionID);
		setChargeID(chargeID);
	}

	public Integer getWarehouseID() {
		return warehouseID;
	}
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT m.tablename,  m.ad_org_id, m.orgvalue, m.orgname, m.doc_id,	m.documentno, m.description, m.datetrx,	m.type,	m.aditionalType, m.c_charge_id,	m.chargename, m_warehouse_id, warehousevalue, warehousename, m_warehouseto_id, warehousetovalue, warehousetoname, c_bpartner_id, bpartnervalue, bpartnername, sum(coalesce(pp.pricestd,0) * m.qty) as amt " +
											"FROM (SELECT tablename," +
											"				ad_org_id, " +
											"				orgvalue, " +
											"				orgname, " +
											"				doc_id, " +
											"				documentno, " +
											"				description, " +
											"				datetrx, " +
											"				type, " +
											"				aditionalType, " +
											"				c_charge_id, " +
											"				chargename, " +
											"				m_warehouse_id," +
											"				warehousevalue, " +
											"				warehousename, " +
											"				m_warehouseto_id, " +
											"				warehousetovalue, " +
											"				warehousetoname, " +
											"				c_bpartner_id, " +
											"				bpartnervalue, " +
											"				bpartnername, " +
											"				m_product_id, " +
											"				qty " +
											"		FROM v_product_movements " +
											"		WHERE docstatus IN ('CL','CO') " +
											"				AND ad_client_id = ? " +
											"				AND tablename <> 'M_InOut' " +
											"				AND qty <> 0 ");
		if(getOrgID() != null){
			sql.append(" AND ad_org_id = ? ");
		}
		if(getDateFrom() != null){
			sql.append(" AND datetrx >= ?::date ");
		}
		if(getDateTo() != null){
			sql.append(" AND datetrx <= ?::date ");
		}
		if(getWarehouseID() != null){
			sql.append(" AND ((tablename = 'M_Transfer' AND type = 'T' AND aditionaltype = 'I' AND m_warehouseto_id = ?) OR m_warehouse_id = ?) ");
		}
		if(getChargeID() != null){
			sql.append(" AND c_charge_id = ? ");
		}
		sql.append(") as m ");
		sql.append("LEFT JOIN m_productprice as pp on pp.m_product_id = m.m_product_id " +
				   "WHERE pp.m_pricelist_version_id = ? " +
				   "GROUP BY m.tablename, " +
				   "		m.ad_org_id, " +
				   "		m.orgvalue, " +
				   "		m.orgname, " +
				   "		m.doc_id, " +
				   "		m.documentno, " +
				   "		m.description, " +
				   "		m.datetrx, " +
				   "		m.type, " +
				   "		m.aditionalType, " +
				   "		m.c_charge_id, " +
				   "		m.chargename, " +
				   "		m_warehouse_id," +
				   "		warehousevalue, " +
				   "		warehousename, " +
				   "		m_warehouseto_id, " +
				   "		warehousetovalue, " +
				   "		warehousetoname, " +
				   "		c_bpartner_id, " +
				   "		bpartnervalue, " +
				   "		bpartnername ");
		sql.append(" ORDER BY orgvalue, datetrx, tablename, documentno ");
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
		if(getWarehouseID() != null){
			params.add(getWarehouseID());
			params.add(getWarehouseID());
		}
		if(getChargeID() != null){
			params.add(getChargeID());
		}
		params.add(getPriceListVersionID());
		return params.toArray();
	}

	
	
	protected String getTransferDescription(){
		return JasperReportsUtil.getListName(getCtx(),
				MTransfer.TRANSFERTYPE_AD_Reference_ID,
				(String) getCurrentRecord().get("TYPE"))
				+ " - "
				+ JasperReportsUtil.getListName(getCtx(),
						MTransfer.MOVEMENTTYPE_AD_Reference_ID,
						(String) getCurrentRecord().get("ADITIONALTYPE"));
	}
	
	protected String getProductChangeDescription(){
		return getInitProductChangeDescription();
	}
	
	protected String getSplittingDescription(){
		return getInitSplittingDescription(); 
	}
	
	protected String getMovementDescription(){
		return (String) getCurrentRecord().get("TYPE");
	}
	
	protected String getInventoryDescription(){
		return (String) getCurrentRecord().get("TYPE");
	}
	
	public void setWarehouseID(Integer warehouseID) {
		this.warehouseID = warehouseID;
	}

	public Integer getPriceListVersionID() {
		return priceListVersionID;
	}

	public void setPriceListVersionID(Integer priceListVersionID) {
		this.priceListVersionID = priceListVersionID;
	}

	public Integer getChargeID() {
		return chargeID;
	}

	public void setChargeID(Integer chargeID) {
		this.chargeID = chargeID;
	}
}
