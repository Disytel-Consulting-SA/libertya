package org.openXpertya.JasperReport.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MOrg;
import org.openXpertya.model.MProductCategory;
import org.openXpertya.model.MProductGamas;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.Msg;

public class NegativeOnHandDataSource extends QueryDataSource {

	/** AD_MESSAGE cuando no hay familia o línea de artículo */
	protected static final String AD_MESSAGE_DEFAULT = "NoDescription";
	
	/** Subfamilia */
	private MProductCategory productCategory;
	
	/** Familia */
	private MProductGamas productGamas;
	
	/** Línea de Artículo */
	private MProductLines productLines;
	
	/** Almacén */
	private MWarehouse warehouse;
	
	/** Organización */
	private MOrg org;
	
	/** Contexto */
	private Properties ctx;
	
	public NegativeOnHandDataSource(Properties ctx, MOrg org, MWarehouse warehouse,
			MProductLines productLines, MProductGamas productGamas,
			MProductCategory productCategory, String trxName) {
		super(trxName);
		setCtx(ctx);
		setOrg(org);
		setWarehouse(warehouse);
		setProductLines(productLines);
		setProductGamas(productGamas);
		setProductCategory(productCategory);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer(
		"select coalesce(pl.m_product_lines_id,0) as m_product_lines_id, " +
				"coalesce(pl.value,'ZZ') as product_lines_value, " +
				"coalesce(pl.name,'"+Msg.getMsg(getCtx(), AD_MESSAGE_DEFAULT)+"') as product_lines_name, " +
				"coalesce(pg.m_product_gamas_id,0) as m_product_gamas_id, " +
				"coalesce(pg.value,'ZZ') as product_gamas_value, " +
				"coalesce(pg.name,'"+Msg.getMsg(getCtx(), AD_MESSAGE_DEFAULT)+"') as product_gamas_name, " +
				"pc.m_product_category_id, " +
				"pc.value as product_category_value, " +
				"pc.name as product_category_name, " +
				"p.m_product_id, " +
				"p.value as product_value, " +
				"p.name as product_name, " +
				"s.qtyonhand " +
				"from (select m_product_id, sum(qtyonhand) as qtyonhand " +
				"		from m_storage as s " +
				"		inner join m_locator as l on l.m_locator_id = s.m_locator_id " +
				"		where s.ad_org_id = ? ");
		if(getWarehouse() != null){
			sql.append(" AND m_warehouse_id = ? ");
		}
		sql.append("		group by m_product_id) as s " +
				"inner join m_product as p on p.m_product_id = s.m_product_id " +
				"inner join m_product_category as pc on pc.m_product_category_id = p.m_product_category_id " +
				"left join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id " +
				"left join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id " +
				"where qtyonhand < 0 ");
		if(getProductCategory() != null){
			sql.append(" AND pc.m_product_category_id = ? ");
		}
		if(getProductGamas() != null){
			sql.append(" AND pg.m_product_gamas_id = ? ");
		}
		if(getProductLines() != null){
			sql.append(" AND pl.m_product_lines_id = ? ");
		}
		sql.append(" order by product_lines_value, product_gamas_value, product_category_value, product_value ");
		 return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();
		params.add(getOrg().getID());
		if(getWarehouse() != null){
			params.add(getWarehouse().getID());
		}
		if(getProductCategory() != null){
			params.add(getProductCategory().getID());
		}
		if(getProductGamas() != null){
			params.add(getProductGamas().getID());
		}
		if(getProductLines() != null){
			params.add(getProductLines().getID());
		}
		return params.toArray();
	}

	protected void setProductCategory(MProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	protected MProductCategory getProductCategory() {
		return productCategory;
	}

	protected void setProductGamas(MProductGamas productGamas) {
		this.productGamas = productGamas;
	}

	protected MProductGamas getProductGamas() {
		return productGamas;
	}

	protected void setProductLines(MProductLines productLines) {
		this.productLines = productLines;
	}

	protected MProductLines getProductLines() {
		return productLines;
	}

	protected void setWarehouse(MWarehouse warehouse) {
		this.warehouse = warehouse;
	}

	protected MWarehouse getWarehouse() {
		return warehouse;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setOrg(MOrg org) {
		this.org = org;
	}

	protected MOrg getOrg() {
		return org;
	}
}
