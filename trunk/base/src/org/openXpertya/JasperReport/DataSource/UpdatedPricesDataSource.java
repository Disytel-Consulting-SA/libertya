package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.Properties;

public class UpdatedPricesDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** ID de Lista de Precios */
	private Integer priceListID;
	
	/** Actualizado desde */
	private Timestamp updated;
	
	public UpdatedPricesDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}

	public UpdatedPricesDataSource(Properties ctx, Integer priceListID, Timestamp updated, String trxName) {
		this(trxName);
		setCtx(ctx);
		setPriceListID(priceListID);
		setUpdated(updated);
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
					 "			price::numeric(11,2) as price, " +
					 "			updated " +
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
					 "				pp.pricestd as price," +
					 "				pp.updated " +
					 "			from m_productprice as pp " +
					 "			inner join m_pricelist_version as plv on plv.m_pricelist_version_id = pp.m_pricelist_version_id "+
					 "			inner join m_product as p on p.m_product_id = pp.m_product_id " +
					 "			inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
					 "			left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
					 "			left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
					 "			where plv.m_pricelist_id = ? AND date_trunc('day',pp.updated) >= date_trunc('day',?::timestamp)) as prices " +
					 " order by lines_value, gamas_value, category_value, product_value";
		return sql;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[] { getPriceListID(), getUpdated() };
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

	protected Timestamp getUpdated() {
		return updated;
	}

	protected void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

}
