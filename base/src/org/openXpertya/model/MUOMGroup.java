package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MUOMGroup extends X_C_UOM_Group {

	/**
	 * Constructor by id
	 * @param ctx
	 * @param C_UOM_Group_ID
	 * @param trxName
	 */
	
	public MUOMGroup(Properties ctx, int C_UOM_Group_ID, String trxName) {
		super(ctx, C_UOM_Group_ID, trxName);

	}

	/**
	 * Constructor by Result Set
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MUOMGroup(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);

	}
	
	/**
	 * Get uom groups of client
	 * @param ctx
	 * @param trxName
	 * @return
	 */
	public static List<MUOMGroup> getOfClient(Properties ctx,String trxName){
		//script sql
    	String sql = "SELECT * FROM c_uom_group WHERE ad_client_id = ?"; 
    		
    	List<MUOMGroup> list = new ArrayList<MUOMGroup>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MUOMGroup(ctx,rs,trxName));				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	
	

}
