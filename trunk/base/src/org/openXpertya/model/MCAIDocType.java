package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MCAIDocType extends X_C_CAI_DocType {

	public MCAIDocType(Properties ctx, int C_CAI_DocType_ID, String trxName) {
		super(ctx, C_CAI_DocType_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCAIDocType(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Controlar que el tipo de documento esté marcado para controlar CAI
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID());
		if(!dt.isCAIControl()) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "DocTypeNOCAIControl"));
			return false;
		}
		
		// Ya existe ese tipo de documento para el cai
		if (PO.existRecordFor(getCtx(), get_TableName(),
				"c_cai_id = ? and c_doctype_id = ?"
						+ (newRecord ? "" : " and c_cai_doctype_id <> " + getC_CAI_DocType_ID()),
				new Object[] { getC_CAI_ID(), getC_DocType_ID() }, get_TrxName())) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "AlreadyExistsDocTypeCAI"));
			return false;
		}
		
		// El tipo de documento no debe tener otro CAI asignado que se superponga en las
		// fechas
		String sql = "select c.cai " + 
				"	from c_cai_doctype cd " + 
				"	join c_cai c on c.c_cai_id = cd.c_cai_id " + 
				"	where cd.c_cai_id <> ? " + 
				"		and cd.c_doctype_id = ? " + 
				"		and cd.isactive = 'Y' " + 
				"		and (?::date between c.validfrom::date and c.datecai::date " + 
				"			or ?::date between c.validfrom::date and c.datecai::date) ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			X_C_CAI cai = new X_C_CAI(getCtx(), getC_CAI_ID(), get_TrxName());
			ps = DB.prepareStatement(sql, get_TrxName(), true);
			int i = 1;
			ps.setInt(i++, getC_CAI_ID());
			ps.setInt(i++, getC_DocType_ID());
			ps.setTimestamp(i++, cai.getValidFrom());
			ps.setTimestamp(i++, cai.getDateCAI());
			rs = ps.executeQuery();
			if(rs.next()) {
				log.saveError("SaveError",
						Msg.getMsg(getCtx(), "AlreadyExistsDocTypeCAIOnDate", new Object[] { rs.getString("cai") }));
				return false;
			}
		} catch(Exception e) {
			log.saveError("SaveError", e.getMessage());
			return false;
		} finally {
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				log.saveError("SaveError", e2.getMessage());
				return false;
			}
		}
		
		return true;
	}
	
}
