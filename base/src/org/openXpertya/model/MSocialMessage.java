package org.openXpertya.model;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class MSocialMessage extends X_C_SocialMessage {

	public MSocialMessage(Properties ctx, int C_SocialMessage_ID, String trxName) {
		super(ctx, C_SocialMessage_ID, trxName);
	}

	public MSocialMessage(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		int count = DB.getSQLValue(null, "SELECT count(1) FROM C_SocialSubscription WHERE AD_User_ID = " + Env.getAD_User_ID(getCtx()) + " AND C_SocialConversation_ID = " + getC_SocialConversation_ID());
		if (count==0) {
			X_C_SocialSubscription newSusbscriber = new X_C_SocialSubscription(getCtx(), 0, null);
			newSusbscriber.setClientOrg(Env.getAD_Client_ID(getCtx()), Env.getAD_Org_ID(getCtx()));
			newSusbscriber.setAD_User_ID(Env.getAD_User_ID(getCtx()));
			newSusbscriber.setC_SocialConversation_ID(getC_SocialConversation_ID());
			if (!newSusbscriber.save()) {
				return false;
			}
			if (getInThisConversation()==null || getInThisConversation().length()==0) {
				setInThisConversation(Env.getContext(getCtx(), "#AD_User_Name"));
			}
		}
		setSent(Timestamp.valueOf(Env.getDateTime("yyyy-MM-dd HH:mm:ss.SSS ")));
		return true;
	}

}
