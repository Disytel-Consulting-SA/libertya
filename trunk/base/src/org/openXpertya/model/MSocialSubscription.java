package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MSocialSubscription extends X_C_SocialSubscription {

	public MSocialSubscription(Properties ctx, int C_SocialSubscription_ID,
			String trxName) {
		super(ctx, C_SocialSubscription_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MSocialSubscription(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Retorna la subscripción para un usuario y una conversación.
	 * Si existe recupera la existente.
	 * Si no existe recupera el objeto para ser un nuevo registro
	 */
	public static MSocialSubscription getForConversationAndUser(int socialConversationID, int userID, Properties ctx, String trxName) {
		MSocialSubscription subscription = null;
		int id = DB.getSQLValue(trxName, " SELECT C_SocialSubscription_ID " +
											" FROM C_SocialSubscription " +
											" WHERE C_SocialConversation_ID = " + socialConversationID +
											" AND AD_User_ID = " + userID);		
		if (id<0)
			id = 0;
		subscription = new MSocialSubscription(ctx, id, trxName);
		
		return subscription;
	}

}
