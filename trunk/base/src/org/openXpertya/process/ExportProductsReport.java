package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.model.X_T_Product;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;


public class ExportProductsReport extends SvrProcess {

	/** ID del artículo parámetro */
	private Integer productID;
	
	/** ID de la Subfamilia parámetro */
	private Integer productCategoryID;

	/** ID de la Familia parámetro */
	private Integer productGamasID;
	
	/** ID de la Línea de Artículo parámetro */
	private Integer productLinesID;
	
	/** ID de la Marca parámetro */
	private Integer productFamilyID;
	
	/** ID del proveedor parámetro */
	private Integer bpartnerID;
	
	/** ID de la Organización parámetro */
	private Integer orgID;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		String name = null;
		for( int i = 0;i < para.length;i++ ) {
			name = para[ i ].getParameterName();
            if(name.equals( "M_Product_ID")) {
            	setProductID(para[i].getParameterAsInt());
            } else if( name.equals( "M_Product_Category_ID" )) {
            	setProductCategoryID(para[i].getParameterAsInt());
            } else if( name.equals( "M_Product_Gamas_ID" )) {
            	setProductGamasID(para[i].getParameterAsInt());
            } else if( name.equals( "M_Product_Lines_ID" )) {
            	setProductLinesID(para[i].getParameterAsInt());
            } else if( name.equals( "M_Product_Family_ID" )) {
            	setProductFamilyID(para[i].getParameterAsInt());
            } else if( name.equals( "C_BPartner_ID" )) {
            	setBpartnerID(para[i].getParameterAsInt());
            } else if( name.equals( "AD_Org_ID" )) {
            	setOrgID(para[i].getParameterAsInt());
            }
        }
	}

	@Override
	protected String doIt() throws Exception {
		// Borrar registros anteriores de una semana o de este ad_pinstance 
		deleteOldRecords(X_T_Product.Table_Name, getAD_PInstance_ID(), get_TrxName());
		// Consulta de selección de artículos
		StringBuffer sql = new StringBuffer("select " +getAD_PInstance_ID() +", " + 
													"p.m_product_id, " +
													"p.ad_client_id, " +
													"o.ad_org_id, " +
													"p.isactive, " +
													getAD_User_ID() + ", " +
													getAD_User_ID() + ", " +
													"p.value," +
													"p.name," +
													"p.description," +
													"p.upc," +
													"p.ispurchased," +
													"p.issold, " +
													"o.value as org_value, " +
													"o.name as org_name, " +
													"u.c_uom_id," +
													"u.x12de355," +
													"u.uomsymbol," +
													"pc.m_product_category_id," +
													"pc.value as productcategory_value," +
													"pc.name as productcategory_name," +
													"tc.c_taxcategory_id," +
													"tc.name as taxcategory_name," +
													"p.producttype," +
													"p.checkoutplace " +
													 (!Util.isEmpty(getProductGamasID(), true)
																					|| !Util.isEmpty(getProductLinesID(), true) ? ", pg.m_product_gamas_id, pg.value as productgamas_value, pg.name as productgamas_name "
																					: "")
																			+
													 (!Util.isEmpty(getProductLinesID(), true) ? ", pl.m_product_lines_id, pl.value as productlines_value, pl.name as productlines_name "
																					: "")
																			+
													(!Util.isEmpty(getProductFamilyID(), true) ? ", pf.m_product_family_id, pf.value as productfamily_value, pf.name as productfamily_name "
																					: "")
																			+
													(!Util.isEmpty(getBpartnerID(), true) ? ", po.c_bpartner_id, bp.value as bpartner_value, bp.name as bpartner_name "
																					: "")
																			+
												"from m_product p " +
												"inner join ad_org o ON o.ad_org_id = p.ad_org_id " +
												"inner join m_product_category pc on pc.m_product_category_id = p.m_product_category_id " +
												"inner join c_uom u on u.c_uom_id = p.c_uom_id " +
												"inner join c_taxcategory tc on tc.c_taxcategory_id = p.c_taxcategory_id ");
		if(!Util.isEmpty(getProductFamilyID(), true)){
			sql.append(" inner join m_product_family as pf on pf.m_product_family_id = p.m_product_family_id ");
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" inner join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id ");
			sql.append(" inner join m_product_lines as pl on pl.m_product_lines_id = pg.m_product_lines_id ");
		}
		if (!Util.isEmpty(getProductGamasID(), true)
				&& Util.isEmpty(getProductLinesID(), true)) {
			sql.append(" inner join m_product_gamas as pg on pg.m_product_gamas_id = pc.m_product_gamas_id ");
		}		
		if(!Util.isEmpty(getBpartnerID(), true)){
			sql.append(" inner join m_product_po as po on po.m_product_id = p.m_product_id ");
			sql.append(" inner join c_bpartner as bp on po.c_bpartner_id = bp.c_bpartner_id ");
		}
		// WHERE
		sql.append(" where p.ad_client_id = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(getAD_Client_ID());
		if(!Util.isEmpty(getOrgID(), true)){
			sql.append(" and o.ad_org_id = ? ");
			params.add(getOrgID());
		}
		if(!Util.isEmpty(getProductFamilyID(), true)){
			sql.append(" and pf.m_product_family_id = ? ");
			params.add(getProductFamilyID());
		}
		if(!Util.isEmpty(getProductGamasID(), true)){
			sql.append(" and pg.m_product_gamas_id = ? ");
			params.add(getProductGamasID());
		}
		if(!Util.isEmpty(getProductLinesID(), true)){
			sql.append(" and pl.m_product_lines_id = ? ");
			params.add(getProductLinesID());
		}
		if(!Util.isEmpty(getBpartnerID(), true)){
			sql.append(" and po.c_bpartner_id = ? ");
			params.add(getBpartnerID());
		}
		if(!Util.isEmpty(getProductID(), true)){
			sql.append(" and p.m_product_id = ? ");
			params.add(getProductID());
		}
		sql.append(" order by p.value ");
		// El SQL resultante es la concatenación del insert y el select
		String sqlInsert = getSQLInsert()+sql.toString();
		PreparedStatement ps = DB.prepareStatement(sqlInsert, get_TrxName());
		int i = 1;
		for (Object parameterValue : params) {
			ps.setObject(i++, parameterValue);
		}
		
		int inserted = ps.executeUpdate();
		
		return ""+inserted;
	}

	protected String getSQLInsert(){
		return "INSERT INTO t_product(ad_pinstance_id, m_product_id, ad_client_id, " +
				"ad_org_id, isactive, createdby, updatedby, value, name, " +
				"description, upc, ispurchased, issold, orgvalue, orgname, c_uom_id, x12de355, uomsymbol, " +
				"m_product_category_id, productcategory_value, productcategory_name, " +
				"c_taxcategory_id, c_taxcategory_name, producttype, checkoutplace " +
				 (!Util.isEmpty(getProductGamasID(), true)
										|| !Util.isEmpty(getProductLinesID(), true) ? ", m_product_gamas_id, productgamas_value, productgamas_name "
										: "")
								+
				 (!Util.isEmpty(getProductLinesID(), true) ? ", m_product_lines_id, productlines_value, productlines_name "
										: "")
								+
				 (!Util.isEmpty(getProductFamilyID(), true) ? ", m_product_family_id, productfamily_value, productfamily_name "
										: "")
						+
				 (!Util.isEmpty(getBpartnerID(), true) ? ", c_bpartner_id, bpartner_value, bpartner_name "
										: "") + ")";
	}
	
	protected Integer getProductID() {
		return productID;
	}

	protected void setProductID(Integer productID) {
		this.productID = productID;
	}

	protected Integer getProductCategoryID() {
		return productCategoryID;
	}

	protected void setProductCategoryID(Integer productCategoryID) {
		this.productCategoryID = productCategoryID;
	}

	protected Integer getProductGamasID() {
		return productGamasID;
	}

	protected void setProductGamasID(Integer productGamasID) {
		this.productGamasID = productGamasID;
	}

	protected Integer getProductLinesID() {
		return productLinesID;
	}

	protected void setProductLinesID(Integer productLinesID) {
		this.productLinesID = productLinesID;
	}

	protected Integer getProductFamilyID() {
		return productFamilyID;
	}

	protected void setProductFamilyID(Integer productFamilyID) {
		this.productFamilyID = productFamilyID;
	}

	protected Integer getBpartnerID() {
		return bpartnerID;
	}

	protected void setBpartnerID(Integer bpartnerID) {
		this.bpartnerID = bpartnerID;
	}

	protected Integer getOrgID() {
		return orgID;
	}

	protected void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

}
