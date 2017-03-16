package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.MProcess;
import org.openXpertya.model.X_I_ProductPrice;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;
import org.openXpertya.util.Util;


public class ProductPriceTempGlobal extends SvrProcess {
	
	/** Organización parámetro */
	private Integer orgID = 0;
	/** Tipo de Tarifa parámetro */
	private String priceListType;	
	/** Tarifa parámetro */
	private Integer priceListID;
	/** Regla de Precios a aplicar */
	private Integer discountSchemaID;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		
	    for( int i = 0;i < para.length;i++ ) {
	        String name = para[ i ].getParameterName();
	
	        if( name.equals( "AD_Org_ID" )) {
	            setOrgID(para[i].getParameterAsInt());
	        } else if( name.equals( "PriceListType" )) {
	            setPriceListType((String)para[i].getParameter());
	        } else if( name.equals( "M_PriceList_ID" )) {
	            setPriceListID(para[i].getParameterAsInt());
	        } else if( name.equals( "M_DiscountSchema_ID" )) {
	            setDiscountSchemaID(para[i].getParameterAsInt());
	        } 
	    }
	}

	@Override
	protected String doIt() throws Exception {
		// Crear los precios de las sucursales, en sus tipos de precios de lista
		// o la lista de precios parámetro
		StringBuffer sql = new StringBuffer(" SELECT plv.* " +
											" FROM m_pricelist_version plv " +
											" INNER JOIN m_pricelist pl ON pl.m_pricelist_id = plv.m_pricelist_id ");
		sql.append(" WHERE plv.ad_client_id = ? ");
		List<Object> params = new ArrayList<Object>();
		params.add(Env.getAD_Client_ID(getCtx()));
		if(getOrgID() != null){
			sql.append(" AND plv.ad_org_id = ? ");
			params.add(getOrgID());
		}
		if(!Util.isEmpty(getPriceListID(), true)){
			sql.append(" AND plv.m_pricelist_id = ? ");
			params.add(getPriceListID());
		}
		if (!Util.isEmpty(getPriceListType(), true)
				&& !getPriceListType().equals("B")) {
			sql.append(" AND pl.issopricelist = '"
					+ (getPriceListType().equals("S") ? "Y" : "N") + "'");
		}
		sql.append(" ORDER BY plv.ad_org_id, pl.issopricelist desc ");
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> generatedPriceListVersions = new ArrayList<String>();
		try{
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			for (int i = 0; i < params.size();i++) {
				ps.setObject(i+1, params.get(i));
			}
			rs = ps.executeQuery();
			// Itero por las versiones y las creo
			while (rs.next()) {
				Trx.getTrx(get_TrxName()).start();
				ProductPriceTemp ppTemp = new ProductPriceTemp(getCtx(),
						rs.getInt("AD_Client_ID"), rs.getInt("AD_Org_ID"),
						rs.getInt("M_PriceList_Version_ID"),
						rs.getInt("M_PriceList_Version_Base_ID"),
						Util.isEmpty(getDiscountSchemaID(), true)?rs.getInt("M_DiscountSchema_ID"):getDiscountSchemaID(), 
						get_TrxName());
				generatedPriceListVersions.add(ppTemp.doIt());
				Trx.getTrx(get_TrxName()).commit();
			}
			// Tirar la importación de precios
			Trx.getTrx(get_TrxName()).start();
			Map<String, Object> importParams = new HashMap<String, Object>();
			importParams.put("AD_Org_ID", getOrgID());
			MProcess.execute(getCtx(), getImportPriceListProcessID(),
					X_I_ProductPrice.Table_ID, importParams, get_TrxName());
			Trx.getTrx(get_TrxName()).commit();
		} catch(Exception e){
			Trx.getTrx(get_TrxName()).rollback();
			throw e;
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		
		return generatedPriceListVersions.toString();
	}

	protected Integer getImportPriceListProcessID(){
		return DB
				.getSQLValue(get_TrxName(),
						"SELECT ad_process_id FROM ad_process WHERE value = 'ImportPriceList' LIMIT 1");
	}
	
	public Integer getOrgID() {
		return orgID;
	}

	public void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	public String getPriceListType() {
		return priceListType;
	}

	public void setPriceListType(String priceListType) {
		this.priceListType = priceListType;
	}

	public Integer getPriceListID() {
		return priceListID;
	}

	public void setPriceListID(Integer priceListID) {
		this.priceListID = priceListID;
	}

	public Integer getDiscountSchemaID() {
		return discountSchemaID;
	}

	public void setDiscountSchemaID(Integer discountSchemaID) {
		this.discountSchemaID = discountSchemaID;
	}

}
