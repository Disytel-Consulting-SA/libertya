package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MRetencionType extends X_C_RetencionType {

	// Constructores
	
	public MRetencionType(Properties ctx, int C_RetencionType_ID, String trxName) {
		super(ctx, C_RetencionType_ID, trxName);
	}

	public MRetencionType(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Get retencion types from client
	 * @param ctx context
	 * @param trxName trx name
	 * @return list of retencion types
	 */
	public static List<MRetencionType> getOfClient(Properties ctx, String trxName){
		//script sql
    	String sql = "SELECT * FROM c_retenciontype WHERE ad_client_id = ? "; 
    		
    	List<MRetencionType> list = new ArrayList<MRetencionType>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MRetencionType(ctx,rs,trxName));				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				if(rs != null){
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	} 
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param newRecord
	 * 
	 * @return
	 */
	protected boolean beforeSave(boolean newRecord) {
		// Chequeo del producto para determinar si está habilitado para comercializar
        if(getM_Product_ID() != 0) {
        	MProduct product = new MProduct(getCtx(), getM_Product_ID(),get_TrxName());
        	if(product.ismarketingblocked()) {
        		log.saveError("Error", product.getmarketingblockeddescr());
    			return false;
        	}
        }
		return true;
	}

}
