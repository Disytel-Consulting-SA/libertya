package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MDocumentActionAccess extends X_AD_Document_Action_Access {

	public MDocumentActionAccess(Properties ctx, int AD_Document_Action_Access_ID, String trxName) {
		super(ctx, AD_Document_Action_Access_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MDocumentActionAccess(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	protected boolean beforeSave(boolean newRecord) {
		// No puede existir un registro repetido para mismo perfil, tipo de
		// document y acción
		String idSql = newRecord?"":" AND AD_Document_Action_Access_ID <> "+getID();
		if (PO.existRecordFor(getCtx(), get_TableName(),
				"ad_role_id = ? and c_doctype_id = ? and ad_ref_list_id = ?" + idSql,
				new Object[] { getAD_Role_ID(), getC_DocType_ID(), getAD_Ref_List_ID() }, get_TrxName())) {
			log.saveError("ExistentDocumentActionAccess","");
			return false;
		}
		return true;
	}
}
