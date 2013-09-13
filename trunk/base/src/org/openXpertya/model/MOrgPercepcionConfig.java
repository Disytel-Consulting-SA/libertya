package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MOrgPercepcionConfig extends X_AD_Org_Percepcion_Config {

	public static MOrgPercepcionConfig getOrgPercepcionConfig(Properties ctx, Integer orgID, String padrontype, String trxName){
		String sql = "SELECT * FROM ad_org_percepcion_config WHERE ad_org_id = ? AND isactive = 'Y' AND padrontype = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, orgID);
			ps.setString(2, padrontype);
			rs = ps.executeQuery();
			if(rs.next()){
				return (new MOrgPercepcionConfig(ctx, rs, trxName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return null;
	}
	
	public MOrgPercepcionConfig(Properties ctx, int AD_Org_Percepcion_Config_ID,
			String trxName) {
		super(ctx, AD_Org_Percepcion_Config_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MOrgPercepcionConfig(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// No puede existir un registro repetido por organización e impuesto
		StringBuffer sql = new StringBuffer("SELECT count(*) as cant FROM "
				+ get_TableName() + " WHERE ad_org_id = " + getAD_Org_ID()
				+ " AND padrontype = " + getPadronType());
		if(!Util.isEmpty(getID(), true)){
			sql.append(" AND ("+get_TableName()+"_ID <> "+getID()+")");
		}
		if(DB.getSQLValue(get_TrxName(), sql.toString()) > 0){
			log.saveError("OrgPercepcionRepeated", "");
			return false;
		}
		return true;
	}	
}
