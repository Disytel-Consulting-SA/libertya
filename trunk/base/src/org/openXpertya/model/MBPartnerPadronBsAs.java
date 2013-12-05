package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class MBPartnerPadronBsAs extends X_C_BPartner_Padron_BsAs {

	public MBPartnerPadronBsAs(Properties ctx, int C_BPartner_Padron_BsAs_ID,
			String trxName) {
		super(ctx, C_BPartner_Padron_BsAs_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBPartnerPadronBsAs(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param numericColumnName
	 * @param bpartnerID
	 * @param date
	 * @param padronType
	 * @param trxName
	 * @return
	 */
	public static BigDecimal getBPartnerPerc(String numericColumnName, Integer bpartnerID, Timestamp date, String padronType, String trxName){
		String sql = " SELECT "+numericColumnName+
					 " FROM "+Table_Name+" as p "+
					 " INNER JOIN c_bpartner as bp ON trim(bp.taxid) = trim(p.cuit) "+
					 " WHERE bp.c_bpartner_id = ? " +
					 "			AND alta_baja NOT IN ('B') "+
					 "			AND padrontype = '"+padronType+"'"+
					 "			AND ?::date between fecha_desde::date AND fecha_hasta::date " +
					 " ORDER BY p.updated DESC" +
					 " LIMIT 1 ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal perc = null;
		try {
			ps = DB.prepareStatement(sql, trxName, true);
			ps.setInt(1, bpartnerID);
			ps.setTimestamp(2, date);
			rs = ps.executeQuery();
			if(rs.next()){
				perc = rs.getBigDecimal(numericColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return perc;
	}
	
	public static BigDecimal getBPartnerPerc(String numericColumnName, String taxID, Timestamp date, String padronType, String trxName){
		if(Util.isEmpty(taxID, true)){
			return null;
		}
		String taxIDNoScripts = taxID.replace("-", "").trim();
		String sql = " SELECT "+numericColumnName+
					 " FROM "+Table_Name+" as p "+
					 " WHERE p.cuit = '" +taxIDNoScripts+"' " +
					 "			AND alta_baja NOT IN ('B') "+
					 "			AND padrontype = '"+padronType+"'"+
					 "			AND ?::date between fecha_desde::date AND fecha_hasta::date " +
					 " ORDER BY p.updated DESC" +
					 " LIMIT 1 ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal perc = null;
		try {
			ps = DB.prepareStatement(sql, trxName, true);
			ps.setTimestamp(1, date);
			rs = ps.executeQuery();
			if(rs.next()){
				perc = rs.getBigDecimal(numericColumnName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return perc;
	}
}
