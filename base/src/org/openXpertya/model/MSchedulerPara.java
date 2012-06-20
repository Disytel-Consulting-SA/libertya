package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MSchedulerPara extends X_AD_Scheduler_Para {

	public MSchedulerPara(Properties ctx, int AD_Scheduler_Para_ID,	String trxName) {
		super(ctx, AD_Scheduler_Para_ID, trxName);

	}

	public MSchedulerPara(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	
	/**
	 * Get parameters from scheduler id
	 * @param ctx context
	 * @param AD_Scheduler_ID scheduler id
	 * @param trxName trx name
	 * @return list of schedule parameters if exists
	 */
	public static List<MSchedulerPara> getOfScheduler(Properties ctx,int AD_Scheduler_ID, String trxName){
		/*
		 * Obtener los parametros del proceso
		 * ---------------------------------------
		 * SELECT *
		 * FROM ad_scheduler_para
		 * WHERE ad_scheduler_id = ?
		 * ---------------------------------------
		 */
		
		String sql = "SELECT * FROM ad_scheduler_para WHERE ad_scheduler_id = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<MSchedulerPara> list = new ArrayList<MSchedulerPara>();
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, AD_Scheduler_ID);
			rs = ps.executeQuery();
			while(rs.next()){
				list.add(new MSchedulerPara(ctx,rs,trxName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
}
