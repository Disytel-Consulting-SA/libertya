package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class MPromotionCode extends X_C_Promotion_Code {

	/**
	 * Obtiene el código promocional válido para el código y fecha parámetro
	 * 
	 * @param ctx
	 *            contexto actual
	 * @param code
	 *            código promocional
	 * @param validDate
	 *            fecha de validez, si este parámetro es null entonces se toma
	 *            la fecha actual
	 * @param trxName
	 *            transacción actual
	 * @return código promocional válido
	 */
	public static MPromotionCode getValid(Properties ctx, String code, Timestamp validDate, String trxName){
		MPromotionCode pc = null;
		if(Util.isEmpty(code, true)){
			return pc;
		}		
		String sql = " select * "
					+ " from "+Table_Name
					+ " where ad_client_id = ? "
						+ " and code = '"+code+"'"
						+ " and isactive = 'Y' "
						+ " and ValidFrom::date <= ?::date AND (ValidTo::date IS NULL OR ?::date <= ValidTo::date) ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			validDate = validDate == null?Env.getDate():validDate;
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			ps.setTimestamp(2, validDate);
			ps.setTimestamp(3, validDate);
			rs = ps.executeQuery();
			if(rs.next()){
				pc = new MPromotionCode(ctx, rs, trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return pc;
	}
	
	/**
	 * Obtener la promo por el código
	 * @param ctx
	 * @param code
	 * @param trxName
	 * @return
	 */
	public static MPromotionCode getFromCode(Properties ctx, String code, String trxName){
		MPromotionCode pc = null;
		if(Util.isEmpty(code, true)){
			return pc;
		}
		String sql = " select * "
					+ " from "+Table_Name
					+ " where ad_client_id = ? "
						+ " and code = '"+code+"'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			if(rs.next()){
				pc = new MPromotionCode(ctx, rs, trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return pc;
	}
	
	public MPromotionCode(Properties ctx, int C_Promotion_Code_ID, String trxName) {
		super(ctx, C_Promotion_Code_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MPromotionCode(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

}
