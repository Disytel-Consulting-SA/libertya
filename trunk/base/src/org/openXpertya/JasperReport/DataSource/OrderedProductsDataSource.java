package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class OrderedProductsDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	/** Organización */
	private Integer orgID;
	/** Fechas */
	private Timestamp dateFrom;
	private Timestamp dateTo;
	/** Entidad Comercial */
	private Integer bPartnerID;
	/** Línea de Artículo */
	private Integer productLinesID;
	/** Familia */
	private Integer productGamasID;
	/** SubFamilia */
	private Integer productCategoryID;
	/** Marca */
	private Integer productFamilyID;
	/** Artículo */
	private Integer productID;
	/** Agrupación */
	private String groupBy;
	/** Estado de Recepción */
	private String receptionState;
	/** Tipo de Transacción */
	private boolean isSOTrx = false;
	
	public OrderedProductsDataSource(Properties ctx, Integer orgID,
			Timestamp dateFrom, Timestamp dateTo, Integer bPartnerID,
			Integer productLinesID, Integer productGamasID,
			Integer productCategoryID, Integer productFamilyID,
			Integer productID, String groupBy, String receptionState,
			boolean isSOTrx, String trxName) {
		super(trxName);
		setCtx(ctx);
		setOrgID(orgID);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setbPartnerID(bPartnerID);
		setProductLinesID(productLinesID);
		setProductGamasID(productGamasID);
		setProductCategoryID(productCategoryID);
		setProductFamilyID(productFamilyID);
		setProductID(productID);
		setGroupBy(groupBy);
		setReceptionState(receptionState);
		setSOTrx(isSOTrx);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer(
				"select *, "
						+ getGroupBy()
						+ " as group_value, "
						+ getGroupPrintColumn()
						+ " as group_name "
						+
					"from (select o.c_order_id,	o.ad_client_id,	org.ad_org_id, org.value as org_value, org.name as org_name, " +
					"			 o.documentno, o.dateordered::date as dateordered," +
					"			 bp.value as bpartner_value, bp.name as bpartner_name, ol.c_orderline_id, p.m_product_id, " +
					"			 p.value as product_value, p.name as product_name, pc.m_product_category_id, " +
					"			 pc.value as product_category_value, pc.name as product_category_name, " +
					"			 pg.m_product_gamas_id, " +
					"			 pg.value as product_gamas_value, " +
					"			 pg.name as product_gamas_name, " +
					"			 pl.m_product_lines_id, " +
					"			 pl.value as product_lines_value, " +
					"			 pl.name as product_lines_name, " +
					"			 pf.m_product_family_id, " +
					"			 pf.value as product_family_value, " +
					"			 pf.name as product_family_name, " +
					"			 ol.qtyordered, ol.qtydelivered, (ol.qtyordered - ol.qtydelivered) as qtyreserved, " +
					"			 ol.priceentered " +
					"		from c_orderline ol " +
					"		inner join c_order o on o.c_order_id = ol.c_order_id " +
					"		inner join ad_org as org on org.ad_org_id = o.ad_org_id " +
					"		inner join c_bpartner as bp on bp.c_bpartner_id = o.c_bpartner_id " +
					"		inner join m_product p on p.m_product_id = ol.m_product_id " +
					"		inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
					"		left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
					"		left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
					"		left join m_product_family as pf on pf.m_product_family_id = p.m_product_family_id " +
					"		where o.ad_client_id = ? and o.docstatus in ('CO','CL') ");
		sql.append(" and o.issotrx = '").append(isSOTrx()?"Y":"N").append("' ");
		if(!Util.isEmpty(getbPartnerID(), true)){
			sql.append(" and bp.c_bpartner_id = ? ");
		}
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" and org.ad_org_id = ? ");
		}
		if(!Util.isEmpty(getProductID(), true)){
			sql.append(" and p.m_product_id = ? ");
		}
		if(!Util.isEmpty(getProductCategoryID(), true)){
			sql.append(" and pc.m_product_category_id = ? ");
		}
		if(!Util.isEmpty(getProductGamasID(), true)){
			sql.append(" and pg.m_product_gamas_id = ? ");
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" and pl.m_product_lines_id = ? ");
		}
		if(!Util.isEmpty(getProductFamilyID(), true)){
			sql.append(" and pf.m_product_family_id = ? ");
		}
		if(!Util.isEmpty(getReceptionState(), true)){
			// Recepción Completa
			if(getReceptionState().equals("C")){
				sql.append(" and ol.qtyordered = ol.qtydelivered ");
			}
			// Sin Recepción
			else if(getReceptionState().equals("N")){
				sql.append(" and ol.qtydelivered = 0 ");
			}
			// Recepción Parcial
			else{
				sql.append(" and ol.qtyordered > ol.qtydelivered and ol.qtydelivered > 0 ");
			}
		}
		if(getDateFrom() != null){
			sql.append(" and o.dateordered::date >= ?::date ");
		}
		if(getDateTo() != null){
			sql.append(" and o.dateordered::date <= ?::date) ");
		}
		sql.append(" as ols ");
		sql.append(" ORDER BY ");
		sql.append(getGroupBy());
		sql.append(" , ");
		sql.append(" dateordered ");
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(!Util.isEmpty(getbPartnerID(), true)){
			params.add(getbPartnerID());
		}
		if(!Util.isEmpty(getOrgID(), true)){
			params.add(getOrgID());
		}
		if(!Util.isEmpty(getProductID(), true)){
			params.add(getProductID());
		}
		if(!Util.isEmpty(getProductCategoryID(), true)){
			params.add(getProductCategoryID());
		}
		if(!Util.isEmpty(getProductGamasID(), true)){
			params.add(getProductGamasID());
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			params.add(getProductLinesID());
		}
		if(!Util.isEmpty(getProductFamilyID(), true)){
			params.add(getProductFamilyID());
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

	protected String getGroupPrintColumn(){
		String printColumn = null;
		if(getGroupBy().equals("DateOrdered")){
			printColumn = "to_char(DateOrdered, 'DD/MM/YYYY')";
		}
		else if(getGroupBy().equals("DocumentNo")){
			printColumn = "DocumentNo";
		}
		else if(getGroupBy().equals("Org_Value")){
			printColumn = "Org_Name";
		}
		else if(getGroupBy().equals("Product_Category_Value")){
			printColumn = "Product_Category_Name";
		}
		else if(getGroupBy().equals("Product_Gamas_Value")){
			printColumn = "Product_Gamas_Name";
		}
		else if(getGroupBy().equals("Product_Lines_Value")){
			printColumn = "Product_Lines_Name";
		}
		else if(getGroupBy().equals("Product_Value")){
			printColumn = "Product_Name";
		}
		return printColumn;
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

	protected Integer getbPartnerID() {
		return bPartnerID;
	}

	protected void setbPartnerID(Integer bPartnerID) {
		this.bPartnerID = bPartnerID;
	}

	protected Integer getProductLinesID() {
		return productLinesID;
	}

	protected void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	protected Integer getProductGamasID() {
		return productGamasID;
	}

	protected void setProductGamasID(Integer productGamasID) {
		this.productGamasID = productGamasID;
	}

	protected Integer getProductCategoryID() {
		return productCategoryID;
	}

	protected void setProductCategoryID(Integer productCategoryID) {
		this.productCategoryID = productCategoryID;
	}

	protected Integer getProductFamilyID() {
		return productFamilyID;
	}

	protected void setProductFamilyID(Integer productFamilyID) {
		this.productFamilyID = productFamilyID;
	}

	protected Integer getProductID() {
		return productID;
	}

	protected void setProductID(Integer productID) {
		this.productID = productID;
	}

	protected String getGroupBy() {
		return groupBy;
	}

	protected void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	protected String getReceptionState() {
		return receptionState;
	}

	protected void setReceptionState(String receptionState) {
		this.receptionState = receptionState;
	}

	protected boolean isSOTrx() {
		return isSOTrx;
	}

	protected void setSOTrx(boolean isSOTrx) {
		this.isSOTrx = isSOTrx;
	}
}
