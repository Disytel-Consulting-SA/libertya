package org.openXpertya.print;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.X_AD_PrintForm;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MPrintForm extends X_AD_PrintForm {

	
	// Constructores
	
	public MPrintForm(Properties ctx, int AD_PrintForm_ID, String trxName) {
		super(ctx, AD_PrintForm_ID, trxName);
	}

	public MPrintForm(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	// Métodos varios 
	
	/**
	 * Get print forms of client
	 */
	public static List<MPrintForm> getOfClient(Properties ctx,String trxName){
		String sql = "SELECT * FROM ad_printform WHERE ad_client_id = ? "; 
		
    	List<MPrintForm> list = new ArrayList<MPrintForm>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_role
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MPrintForm(ctx,rs,trxName));	
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
