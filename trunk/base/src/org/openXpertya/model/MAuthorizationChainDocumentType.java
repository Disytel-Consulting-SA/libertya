package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class MAuthorizationChainDocumentType extends
		X_M_AuthorizationChainDocumentType {

	public static MAuthorizationChainDocumentType get(Properties ctx, Integer orgID, Integer docTypeID, Integer authorizationChainID, String trxName){
		MAuthorizationChainDocumentType acdt = null;
		String sql = " SELECT * "
					+ " FROM "+ Table_Name
					+ " WHERE ad_client_id = ? and c_doctype_id = ? and m_authorizationchain_id = ? and (ad_org_id = 0 ";
		if(!Util.isEmpty(orgID, true)){
			sql += " OR ad_org_id = "+orgID;
		}
		sql += ") ";
		sql += " ORDER BY ad_org_id desc ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			ps.setInt(2, docTypeID);
			ps.setInt(3, authorizationChainID);
			rs = ps.executeQuery();
			if(rs.next()){
				acdt = new MAuthorizationChainDocumentType(ctx, rs, trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null) ps.close();
				if(rs != null) rs.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return acdt;
	}
	
	public MAuthorizationChainDocumentType(Properties ctx,
			int M_AuthorizationChainDocumentType_ID, String trxName) {
		super(ctx, M_AuthorizationChainDocumentType_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MAuthorizationChainDocumentType(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Verificar que no exista una autorización para el mismo tipo de documento y organización
		if (DB.getSQLValue(
				this.get_TrxName(),
				"SELECT aut.M_AuthorizationChain_ID FROM M_AuthorizationChainDocumentType autDoc "
						+ " INNER JOIN M_AuthorizationChain aut ON (aut.M_AuthorizationChain_ID = autDoc.M_AuthorizationChain_ID) "
						+ "	WHERE autDoc.C_DocType_ID = " + this.getC_DocType_ID() 
						+ " AND autDoc.AD_Org_ID = " + getAD_Org_ID()   
						+ " AND autDoc.AD_Client_ID = " + getAD_Client_ID() 
						+ " AND autDoc.M_AuthorizationChain_ID <> " + getM_AuthorizationChain_ID()
						+ " AND aut.isActive = 'Y'") > 0) {
			log.saveError(Msg.getMsg(getCtx(), "AlreadyExistsAnAuthorizationChain"),"");
			return false;
		}
		return true;
	}

}
