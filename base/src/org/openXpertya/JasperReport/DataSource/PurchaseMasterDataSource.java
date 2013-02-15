package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openXpertya.util.Util;

public class PurchaseMasterDataSource extends QueryDataSource {

	/** Organización */
	private Integer orgID;
	
	/** Proveedor */
	private Integer vendorID;
	
	/** Línea de Artículo */
	private Integer productLinesID;
	
	/** Asociación de nombres de fechas con las fechas correspondientes */
	private Map<String, Timestamp> weekDates;
	
	public PurchaseMasterDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public PurchaseMasterDataSource(Integer orgID, Integer vendorID,
			Integer productLinesID, Map<String, Timestamp> weekDates,
			String trxName) {
		this(trxName);
		setOrgID(orgID);
		setVendorID(vendorID);
		setProductLinesID(productLinesID);
		setWeekDates(weekDates);
	}

	
	
	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT product_value, " +
						"product_name, " +
						"coalesce(stock, 0) as stock," +
						"coalesce(reserved_qty, 0) as reserved_qty, " +
						"coalesce(sales_week_1,0) as sales_week_1, " +
						"coalesce(sales_week_2,0) as sales_week_2, " +
						"coalesce(sales_week_3,0) as sales_week_3, " +
						"coalesce(sales_week_4,0) as sales_week_4, " +
						"coalesce(receipts_week_1,0) as receipts_week_1, " +
						"coalesce(receipts_week_2,0) as receipts_week_2, " +
						"coalesce(receipts_week_3,0) as receipts_week_3, " +
						"coalesce(receipts_week_4,0) as receipts_week_4, " +
						"0.00 as stock_min, " +
						"0.00 as stock_max, " +
						"coalesce((SELECT pp.pricelist FROM m_productprice as pp INNER JOIN m_pricelist_version as plv ON pp.m_pricelist_version_id = plv.m_pricelist_version_id INNER JOIN m_pricelist as pl ON pl.m_pricelist_id = plv.m_pricelist_id WHERE pl.issopricelist = 'Y' AND pl.isactive = 'Y' AND plv.isactive = 'Y' AND pp.isactive = 'Y' AND pl.ad_org_id = ? AND pp.m_product_id = p.m_product_id ORDER BY plv.validfrom DESC LIMIT 1),0) as sales_price " +
					"FROM (SELECT p.m_product_id, p.value as product_value, p.name as product_name, sum(qtyonhand) as stock, sum(qtyreserved) as reserved_qty " +
							"FROM m_product_po as po " +
							"INNER JOIN m_product as p ON p.m_product_id = po.m_product_id " +
							"LEFT JOIN rv_storage_product as sp ON sp.m_product_id = p.m_product_id AND sp.ad_org_id = ? ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append("INNER JOIN m_product_category as pc ON pc.m_product_category_id = p.m_product_category_id ");
			sql.append("INNER JOIN m_product_gamas as pg ON pg.m_product_gamas_id = pc.m_product_gamas_id ");
			sql.append("INNER JOIN m_product_lines as pls ON pls.m_product_lines_id = pg.m_product_lines_id ");
		}
		sql.append("WHERE po.isactive = 'Y' AND p.isactive = 'Y' AND po.c_bpartner_id = ? ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" AND pls.m_product_lines_id = ? ");
		}
		sql.append("GROUP BY p.m_product_id, p.value, p.name ) as p");
		
		for (int i = 1; i < 5; i++) {
			sql.append(getSalesWeekSubQuery(i));
			sql.append(getReceiptsWeekSubQuery(i));
		}
		sql.append(" ORDER BY product_value ");
		return sql.toString();
	}
	
	protected String getSalesWeekSubQuery(int pastWeek){
		String salesWeekSql = " LEFT JOIN (SELECT m_product_id, sum(il.qtyinvoiced*dt.signo_issotrx) as sales_week_"+pastWeek+
								" FROM c_invoiceline as il " +
								"INNER JOIN c_invoice as i ON i.c_invoice_id = il.c_invoice_id " +
								"INNER JOIN c_doctype as dt ON dt.c_doctype_id = i.c_doctypetarget_id " +
								"WHERE i.ad_org_id = ? AND i.issotrx = 'Y' AND i.docstatus IN ('CL', 'CO') AND date_trunc('day', i.dateinvoiced) BETWEEN date_trunc('day',?::date) AND date_trunc('day',?::date) " +
								"GROUP BY m_product_id) as s"+pastWeek+
								" ON s"+pastWeek+".m_product_id = p.m_product_id ";
		return salesWeekSql;
	}

	protected String getReceiptsWeekSubQuery(int pastWeek){
		String receiptsWeekSql = " LEFT JOIN (SELECT m_product_id, sum(iol.movementqty*dt.signo_issotrx) as receipts_week_"+pastWeek+
									" FROM m_inoutline as iol " +
									"INNER JOIN m_inout as io ON io.m_inout_id = iol.m_inout_id " +
									"INNER JOIN c_doctype as dt ON dt.c_doctype_id = io.c_doctype_id " +
									"WHERE io.ad_org_id = ? AND io.issotrx = 'N' AND io.docstatus IN ('CL', 'CO') AND date_trunc('day',io.movementdate) BETWEEN date_trunc('day',?::date) AND date_trunc('day',?::date) " +
									"GROUP BY m_product_id) as r"+pastWeek+
									" ON r"+pastWeek+".m_product_id = p.m_product_id ";
		return receiptsWeekSql;
	}
	
	@Override
	protected Object[] getParameters() {
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(getOrgID());
		parameters.add(getOrgID());
		parameters.add(getVendorID());
		if(!Util.isEmpty(getProductLinesID(), true)){
			parameters.add(getProductLinesID());
		}
		for (int i = 1; i < 5; i++) {
			parameters.add(getOrgID());
			parameters.add(getWeekDates().get("DATE_FROM_WEEK_"+i));
			parameters.add(getWeekDates().get("DATE_TO_WEEK_"+i));
			parameters.add(getOrgID());
			parameters.add(getWeekDates().get("DATE_FROM_WEEK_"+i));
			parameters.add(getWeekDates().get("DATE_TO_WEEK_"+i));
		}
		return parameters.toArray();
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	protected Integer getVendorID() {
		return vendorID;
	}

	protected void setVendorID(Integer vendorID) {
		this.vendorID = vendorID;
	}

	protected Integer getProductLinesID() {
		return productLinesID;
	}

	protected void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	protected Map<String, Timestamp> getWeekDates() {
		return weekDates;
	}

	protected void setWeekDates(Map<String, Timestamp> weekDates) {
		this.weekDates = weekDates;
	}

	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
}
