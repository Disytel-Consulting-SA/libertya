package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class PriceChangingDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** Fecha desde */
	private Timestamp dateFrom;
	
	/** Fecha hasta */
	private Timestamp dateTo;
	
	/** Lista de Precios */
	private Integer priceListID;
	
	/** Línea de Artículo */
	private Integer productLinesID;
	
	/** Usuario que actualizó el precio */
	private Integer updatedBy;
	
	/** Variación del precio con respecto al anterior */
	private BigDecimal variation;
	
	public PriceChangingDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public PriceChangingDataSource(Properties ctx, Timestamp dateFrom,
			Timestamp dateTo, Integer priceListID, Integer productLinesID,
			Integer updatedBy, BigDecimal variation, String trxName) {
		this(trxName);
		setCtx(ctx);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setPriceListID(priceListID);
		setProductLinesID(productLinesID);
		setUpdatedBy(updatedBy);
		setVariation(variation);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer("select p.m_product_id, " +
											"		p.value as product_value, " +
											"		p.name as product_name, " +
											"		pl.name as pricelist_name, " +
											"		pp.pricestd as newprice, " +
											"		pp.previouspricestd as oldprice, " +
											"		abs(1 - coalesce(pp.variationpricestd,0.00)) * 100 as desviation_perc, " +
											"		(CASE WHEN pp.variationpricestd is not null AND pp.variationpricestd >= 1 THEN '-' ELSE '+' END) as sign " +
											"from i_productprice pp " +
											"inner join m_product p on p.m_product_id = pp.m_product_id " +
											"inner join m_pricelist_version plv on plv.m_pricelist_version_id = pp.m_pricelist_version_id " +
											"inner join m_pricelist pl on pl.m_pricelist_id = plv.m_pricelist_id ");
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" inner join m_product_category pc on pc.m_product_category_id = p.m_product_category_id ");
			sql.append(" inner join m_product_gamas pg on pg.m_product_gamas_id = pc.m_product_gamas_id ");
			sql.append(" inner join m_product_lines pli on pli.m_product_lines_id = pg.m_product_lines_id ");
		}
		sql.append(" where pp.ad_client_id = ? ");
		sql.append(" and pricestd <> 0 ");
		sql.append(" and previouspricestd <> 0 ");
		sql.append(" and pp.updated::date >= ?::date ");
		if(getDateTo() != null){
			sql.append(" and pp.updated::date <= ?::date ");
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" and pli.m_product_lines_id = ? ");
		}
		if(!Util.isEmpty(getPriceListID(), true)){
			sql.append(" and pl.m_pricelist_id = ? ");
		}
		if(!Util.isEmpty(getUpdatedBy(), true)){
			sql.append(" and pp.updatedby = ? ");
		}
		if(!Util.isEmpty(getVariation(), true)){
			sql.append(" and (abs(1 - pp.variationpricestd) * 100) >= abs(?) ");
		}
		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		params.add(getDateFrom());
		if(getDateTo() != null){
			params.add(getDateTo());	
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			params.add(getProductLinesID());
		}
		if(!Util.isEmpty(getPriceListID(), true)){
			params.add(getPriceListID());
		}
		if(!Util.isEmpty(getUpdatedBy(), true)){
			params.add(getUpdatedBy());
		}
		if(!Util.isEmpty(getVariation(), true)){
			params.add(getVariation());
		}
		return params.toArray();
	}

	@Override
	protected boolean isQueryNoConvert(){
		return true;
	}
	
	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Integer getPriceListID() {
		return priceListID;
	}

	public void setPriceListID(Integer priceListID) {
		this.priceListID = priceListID;
	}

	public Integer getProductLinesID() {
		return productLinesID;
	}

	public void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public BigDecimal getVariation() {
		return variation;
	}

	public void setVariation(BigDecimal variation) {
		this.variation = variation;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
