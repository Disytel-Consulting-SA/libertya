package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.util.Env;

public class DeferredPricesDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** ID de Lista de Precios */
	private Integer priceListID;
	
	/** ID de Lista de Precios Diferida */
	private Integer deferredPriceListID;
	
	public DeferredPricesDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public DeferredPricesDataSource(Properties ctx, Integer priceListID, Integer deferredPriceListID, String trxName) {
		this(trxName);
		setCtx(ctx);
		setPriceListID(priceListID);
		setDeferredPriceListID(deferredPriceListID);
	}
	
	@Override
	protected String getQuery() {
		String sql = "select lines_value, " +
					 "			lines_name, " +
					 "			m_product_lines_id, " +
					 "			gamas_value, " +
					 "			gamas_name, " +
					 "			m_product_gamas_id, " +
					 "			category_value, " +
					 "			category_name, " +
					 "			m_product_category_id, " +
					 "			product_value, " +
					 "			product_name, " +
					 "			m_product_id, " +
					 "			price::numeric(11,2), " +
					 "			deferredPrice::numeric(11,2), " +
					 "			(CASE WHEN deferredPrice >= price THEN '+' ELSE '-' END) as sign, " +
					 "			((CASE WHEN (deferredPrice = 0 OR price = 0) THEN 1 ELSE abs(((CASE WHEN deferredPrice >= price THEN price ELSE deferredPrice END)/(CASE WHEN deferredPrice >= price THEN deferredPrice ELSE price END))-1) END) * 100)::numeric(11,2) as proportionPerc " +
					 " from (select (CASE WHEN pl.value is null THEN 'SD' ELSE pl.value END) as lines_value, " +
					 "				(CASE WHEN pl.name is null THEN 'SIN DESCRIPCION' ELSE pl.name END) as lines_name, " +
					 "				(CASE WHEN pl.m_product_lines_id is null THEN 0 ELSE pl.m_product_lines_id END) as m_product_lines_id, " +
					 "				(CASE WHEN pg.value is null THEN 'SD' ELSE pg.value END) as gamas_value, " +
					 "				(CASE WHEN pg.name is null THEN 'SIN DESCRIPCION' ELSE pg.name END) as gamas_name, " +
					 "				(CASE WHEN pg.m_product_gamas_id is null THEN 0 ELSE pg.m_product_gamas_id END) as m_product_gamas_id, " +
					 "				pc.value as category_value, " +
					 "				pc.name as category_name, " +
					 "				pc.m_product_category_id, " +
					 "				p.value as product_value, " +
					 "				p.name as product_name, " +
					 "				p.m_product_id, " +
					 "				coalesce((select pp.pricestd " +
					 "							from m_pricelist as pl " +
					 "							inner join m_pricelist_version as plv on plv.m_pricelist_id = pl.m_pricelist_id " +
					 "							inner join m_productprice as pp on pp.m_pricelist_version_id = plv.m_pricelist_version_id " +
					 "							where pl.m_pricelist_id = ? and pp.m_product_id = p.m_product_id " +
					 "							order by plv.validfrom desc " +
					 "							limit 1),0) as price, " +
					 "				coalesce((select pp.pricestd " +
					 "							from m_pricelist as pl " +
					 "							inner join m_pricelist_version as plv on plv.m_pricelist_id = pl.m_pricelist_id " +
					 "							inner join m_productprice as pp on pp.m_pricelist_version_id = plv.m_pricelist_version_id " +
					 "							where pl.m_pricelist_id = ? and pp.m_product_id = p.m_product_id " +
					 "							order by plv.validfrom desc " +
					 "							limit 1),0) as deferredPrice " +
					 "			from m_product as p " +
					 "			inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
					 "			left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
					 "			left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
					 "			where p.ad_client_id = ?) as prices " +
					 " where price <> deferredPrice " +
					 " order by lines_value, gamas_value, category_value, product_value";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { getPriceListID(), getDeferredPriceListID(),
				Env.getAD_Client_ID(getCtx()) };
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setPriceListID(Integer priceListID) {
		this.priceListID = priceListID;
	}

	protected Integer getPriceListID() {
		return priceListID;
	}

	protected void setDeferredPriceListID(Integer deferredPriceListID) {
		this.deferredPriceListID = deferredPriceListID;
	}

	protected Integer getDeferredPriceListID() {
		return deferredPriceListID;
	}

}
