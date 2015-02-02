package org.openXpertya.model;

/**
 * Esta clase queda deprecada debido al uso del formulario VSocialConversation / WSocialConversation
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class CalloutSocialMessage extends CalloutEngine {

	public void loadParticipants(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		try {
			PreparedStatement pstmt = DB.prepareStatement("SELECT u.name " +
															" FROM C_SocialSubscription ss " +
															" INNER JOIN AD_User u ON ss.AD_User_ID = u.AD_User_ID " +
															" WHERE ss.C_SocialConversation_ID = " + (Integer)mTab.getValue("C_SocialConversation_ID") + 
															" UNION SELECT '" + Env.getContext(ctx, "#AD_User_Name") + "'");
			ResultSet rs = pstmt.executeQuery();
			StringBuffer participants = new StringBuffer();
			while (rs.next()) {
				participants.append(rs.getString("name")).append(" ");
			}
			mTab.setValue("InThisConversation", participants.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String newParticipant(Properties ctx, int WindowNo, MTab mTab, MField mField, Object value) {
		if (value!=null && mTab.isInserting()) { 
			int count = DB.getSQLValue(null, "SELECT count(1) FROM C_SocialSubscription WHERE AD_User_ID = " + Integer.parseInt(value.toString()) + " AND C_SocialConversation_ID = " + (Integer)mTab.getValue("C_SocialConversation_ID"));
			if (count==0) {
				X_C_SocialSubscription newSusbscriber = new X_C_SocialSubscription(ctx, 0, null);
				newSusbscriber.setClientOrg(Env.getAD_Client_ID(ctx), Env.getAD_Org_ID(ctx));
				newSusbscriber.setAD_User_ID((Integer)value);
				newSusbscriber.setC_SocialConversation_ID((Integer)mTab.getValue("C_SocialConversation_ID"));
				if (!newSusbscriber.save()) {
					return "Error al incorporar participante: " + CLogger.retrieveErrorAsString();
				}
				mTab.setValue(mField, null);
			}
		}
		// Recargar listado de participantes
		if (mTab.isInserting()){
			loadParticipants(ctx, WindowNo, mTab, mField, value);
		}
		return "";
	}
}
