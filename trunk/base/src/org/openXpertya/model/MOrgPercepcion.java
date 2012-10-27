package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class MOrgPercepcion extends X_AD_Org_Percepcion {

	public static List<MOrgPercepcion> getOrgPercepciones(Properties ctx, String trxName){
		return getOrgPercepciones(ctx, Env.getAD_Org_ID(ctx), trxName);
	}
	
	public static List<MOrgPercepcion> getOrgPercepciones(Properties ctx, Integer orgID, String trxName){
		List<MOrgPercepcion> percepciones = new ArrayList<MOrgPercepcion>();
		String sql = "SELECT * FROM ad_org_percepcion WHERE ad_org_id = ? AND isactive = 'Y'";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, orgID);
			rs = ps.executeQuery();
			while(rs.next()){
				percepciones.add(new MOrgPercepcion(ctx, rs, trxName));
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
		return percepciones;
	}
	
	public MOrgPercepcion(Properties ctx, int AD_Org_Percepcion_ID,
			String trxName) {
		super(ctx, AD_Org_Percepcion_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MOrgPercepcion(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// No puede existir un registro repetido por organización e impuesto
		StringBuffer sql = new StringBuffer("SELECT count(*) as cant FROM "
				+ get_TableName() + " WHERE ad_org_id = " + getAD_Org_ID()
				+ " AND c_tax_id = " + getC_Tax_ID());
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
