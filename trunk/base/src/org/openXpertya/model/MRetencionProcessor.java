package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MRetencionProcessor extends X_C_RetencionProcessor {

	// Constructores
	
	public MRetencionProcessor(Properties ctx, int C_RetencionProcessor_ID,	String trxName) {
		super(ctx, C_RetencionProcessor_ID, trxName);
	}

	public MRetencionProcessor(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	
	/**
	 * Get retencion processors from client
	 * @param ctx context
	 * @param trxName trx name
	 * @return list of retencion processors
	 */
	public static List<MRetencionProcessor> getOfClient(Properties ctx, String trxName){
		//script sql
    	String sql = "SELECT * FROM c_retencionprocessor WHERE ad_client_id = ? "; 
    		
    	List<MRetencionProcessor> list = new ArrayList<MRetencionProcessor>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MRetencionProcessor(ctx,rs,trxName));				
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
	
}
