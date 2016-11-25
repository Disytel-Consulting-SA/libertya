package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MAuthorizationChainDocumentType extends
		X_M_AuthorizationChainDocumentType {

	public MAuthorizationChainDocumentType(Properties ctx,
			int M_AuthorizationChainDocumentType_ID, String trxName) {
		super(ctx, M_AuthorizationChainDocumentType_ID, trxName);
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
