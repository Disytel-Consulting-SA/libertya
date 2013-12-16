package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Env;

public class ValuedMovementsDetailDataSource extends ValuedMovementsDataSource {

	/** Línea de Artículo */
	private Integer productLinesID;
	
	public ValuedMovementsDetailDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public ValuedMovementsDetailDataSource(Properties ctx, Integer orgID,
			Timestamp dateFrom, Timestamp dateTo, String trxName) {
		super(ctx, orgID, dateFrom, dateTo, trxName);
		// TODO Auto-generated constructor stub
	}

	public ValuedMovementsDetailDataSource(Properties ctx, Integer orgID,
			Timestamp dateFrom, Timestamp dateTo, Integer warehouseID,
			Integer priceListVersionID, Integer chargeID, Integer productLinesID, 
			String trxName) {
		super(ctx, orgID, dateFrom, dateTo, warehouseID, priceListVersionID,
				chargeID, trxName);
		setProductLinesID(productLinesID);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("SELECT tablename, ad_org_id, orgvalue, orgname, doc_id, documentno, datetrx, c_charge_id, chargename, m_warehouse_id, warehousevalue, warehousename, m_warehouseto_id, warehousetovalue, warehousetoname, m_product_id, productvalue, productname, productlinesvalue, productlinesname, pricestd, sum(qty) as qty, sum(amt) as amt " +
											" FROM (SELECT tablename, m.ad_org_id, orgvalue, orgname, doc_id, documentno, datetrx, c_charge_id, chargename, m_warehouse_id, warehousevalue, warehousename, m_warehouseto_id, warehousetovalue, warehousetoname, m.m_product_id, productvalue, productname, productlinesvalue, productlinesname, qty, coalesce(pp.pricestd,0) as pricestd, coalesce(pp.pricestd,0) * m.qty as amt " +
											" FROM (SELECT tablename, ad_org_id, orgvalue, orgname, doc_id, documentno, datetrx, c_charge_id, chargename, m_warehouse_id, warehousevalue, warehousename, m_warehouseto_id, warehousetovalue, warehousetoname, m_product_id, productvalue, productname, productlinesvalue, productlinesname, qty " +
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
		if(getProductLinesID() != null){
			sql.append(" AND m_product_lines_id = ? ");
		}
		sql.append(") as m ");
		sql.append("LEFT JOIN m_productprice as pp on pp.m_product_id = m.m_product_id " +
				   "WHERE pp.m_pricelist_version_id = ? ) as a " +
				   "GROUP BY tablename, ad_org_id, orgvalue, orgname, doc_id, documentno, datetrx, c_charge_id, chargename, m_warehouse_id, warehousevalue, warehousename, m_warehouseto_id, warehousetovalue, warehousetoname, m_product_id, productvalue, productname, productlinesvalue, productlinesname, pricestd ");
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
		if(getProductLinesID() != null){
			params.add(getProductLinesID());
		}
		params.add(getPriceListVersionID());
		return params.toArray();
	}

	protected Integer getProductLinesID() {
		return productLinesID;
	}

	protected void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}
}
