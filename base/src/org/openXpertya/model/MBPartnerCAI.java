package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MBPartnerCAI extends X_C_BPartner_CAI {

	public MBPartnerCAI(Properties ctx, int C_BPartner_CAI_ID, String trxName) {
		super(ctx, C_BPartner_CAI_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MBPartnerCAI(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Buscar si para la misma ec, punto de venta y fecha de vencimiento ya
		// existe uno
		String whereIDClause = newRecord ? "" : " AND c_bpartner_cai_id <> "+getID();
		String sql = "SELECT cai "
					+ "FROM c_bpartner_cai "
					+ "WHERE c_bpartner_id = ? and posnumber = ? and date_trunc('day', datecai) = date_trunc('day', ?::timestamp) "
					+ whereIDClause;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, get_TrxName());
			ps.setInt(1, getC_BPartner_ID());
			ps.setInt(2, getPOSNumber());
			ps.setTimestamp(3, getDateCAI());
			rs = ps.executeQuery();
			if(rs.next()){
				log.saveError("SaveError",
						Msg.getMsg(getCtx(), "SameBPartnerCAI", new Object[] { rs.getString("cai") }));
				return false;
			}	
		} catch (Exception e) {
			log.saveError("SaveError", e.getMessage());
			return false;
		} finally{
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();	
			} catch (Exception e2) {
				log.saveError("SaveError", e2.getMessage());
				return false;
			}
		}
		
		return true;
	}
}
