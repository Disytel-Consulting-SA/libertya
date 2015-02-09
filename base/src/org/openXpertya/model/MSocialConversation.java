package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class MSocialConversation extends X_C_SocialConversation {

	public MSocialConversation(Properties ctx, int C_SocialConversation_ID,
			String trxName) {
		super(ctx, C_SocialConversation_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MSocialConversation(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		if (newRecord) {
			setStarted(Timestamp.valueOf(Env.getDateTime("yyyy-MM-dd HH:mm:ss.SSS ")));
		}
		return true;
	}
	
	/**
	 * Retorna una conversación a partir de una tabla y registro dado.  Si la misma no existe, la genera
	 */
	public static MSocialConversation getForTableAndRecord(Properties ctx, Integer tableID, Integer recordID, Integer windowID, Integer tabID, String trxName) {
		int conversationID = DB.getSQLValue(trxName, "SELECT C_SocialConversation_ID FROM C_SocialConversation WHERE AD_Table_ID = " + tableID + " AND recordID = " + recordID);
		MSocialConversation sc = null;
		if (conversationID < 0) {
			sc = new MSocialConversation(ctx, conversationID, trxName);
			if (tableID != null)
				sc.setAD_Table_ID(tableID);
			if (recordID != null)
				sc.setRecordID(recordID);
			if (windowID != null)
				sc.setAD_Window_ID(windowID);
			if (tabID != null)
				sc.setAD_Tab_ID(tabID);
			sc.setClientOrg(Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
			sc.setStartedBy(Env.getAD_User_ID(Env.getCtx()));
		} else {
			sc = new MSocialConversation(ctx, conversationID, trxName);
		}
		return sc;
	}
	
	public String getParticipantsNames()  {
		StringBuffer participants = new StringBuffer();
		try {
			PreparedStatement pstmt = DB.prepareStatement("SELECT u.name " +
					" FROM C_SocialSubscription ss " +
					" INNER JOIN AD_User u ON ss.AD_User_ID = u.AD_User_ID " +
					" WHERE ss.C_SocialConversation_ID = " + getC_SocialConversation_ID() + 
					" AND ss.C_SocialConversation_ID != 0 ");
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next())
				participants.append(rs.getString("name")).append(", ");
			if (participants.length() > 0)
				participants.delete(participants.length()-2, participants.length());
				
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return participants.toString();
	}
	
	/**
	 * retorna la nómina de mensajes para esta conversacion
	 * @ascOrder orden cronológico ascendente (true) o descendente (false)
	 */
	public ArrayList<MSocialMessage> getMessages(boolean ascOrder) {
		ArrayList<MSocialMessage> messages = new ArrayList<MSocialMessage>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(	" SELECT * " +
										" FROM C_SocialMessage " +
										" WHERE C_SocialConversation_ID = " + getC_SocialConversation_ID() + 
										" ORDER BY SENT " + (ascOrder ? "ASC":"DESC") , get_TrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				MSocialMessage aSocialMessage = new MSocialMessage(getCtx(), rs, get_TrxName());
				messages.add(aSocialMessage);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs = null;
			ps = null;
		}
		return messages;
	}

	/**
	 * retorna la nómina de suscripciones para esta conversacion
	 */
	public ArrayList<MSocialSubscription> getSubscriptions() {
		ArrayList<MSocialSubscription> subscriptions = new ArrayList<MSocialSubscription>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement("SELECT * FROM C_SocialSubscription WHERE C_SocialConversation_ID = " + getC_SocialConversation_ID(), get_TrxName());
			rs = ps.executeQuery();
			while (rs.next()) {
				MSocialSubscription aSocialSubscription = new MSocialSubscription(getCtx(), rs, get_TrxName());
				subscriptions.add(aSocialSubscription);
			}
			ps.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs = null;
			ps = null;
		}
		return subscriptions;
	}
	
	/** Retorna el número de conversaciones no leidas para un usario dado */
	public static int getNotReadConversationsCountForUser(int userID) {
		return getNotReadConversationsForUser(userID).size();
	}

	/** Retorna la nómina de IDs de conversaciones no leidas */
	public static ArrayList<Integer> getNotReadConversationsForUser(int userID) {
		return getConversations(" SELECT sc.C_SocialConversation_ID " +
								" FROM C_SocialSubscription ss " +
								" INNER JOIN C_SocialConversation sc ON sc.C_SocialConversation_ID = ss.C_SocialConversation_ID " +
								" WHERE ss.AD_User_ID = " + userID +
								" AND sc.AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + 
								" AND sc.C_SocialConversation_ID IN ( " + getConversationAccessSQLFilter() + " ) " +
								" AND ss.read = 'N'" +
								" ORDER BY sc.C_SocialConversation_ID ");
	}
	
	/** Retorna la nómina de IDs de conversaciones filtrando según whereClause */ 
	public static ArrayList<Integer> getConversationsForSearch(String whereClause) {
		return getConversations(" SELECT C_SocialConversation_ID " +
								" FROM C_SocialConversation " +
								" WHERE " + whereClause +																	
								" AND AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + 
								" AND C_SocialConversation_ID IN ( " + getConversationAccessSQLFilter() + " ) " +
								" ORDER BY C_SocialConversation_ID ");
	}
	
	/** Retorna el subquery encargado de filtrar según acceso */
	public static String getConversationAccessSQLFilter() {
		return
				// Conversacion sin referencia a registro (el usuario debe ser parte de la conversación)
				" SELECT DISTINCT sc.C_SocialConversation_ID " +
				" FROM C_SocialConversation sc" +
				" INNER JOIN C_SocialSubscription ss ON sc.C_SocialConversation_ID = ss.C_SocialConversation_ID " +
				" WHERE ss.AD_User_ID = " + Env.getAD_User_ID(Env.getCtx()) +
				" AND sc.AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) + 
				" AND sc.AD_Window_ID IS NULL " +
				" UNION " +
				// Conversción con referencia a registro (el perfil de login del usuario debe poseer acceso al registro mediante la ventana)
				" SELECT DISTINCT sc.C_SocialConversation_ID " +
				" FROM C_SocialConversation sc" +
				" INNER JOIN AD_Window_Access wa ON sc.AD_Window_ID = wa.AD_Window_ID " +
				" INNER JOIN AD_User_Roles ur ON ur.AD_Role_ID = wa.AD_Role_ID " +
				" WHERE ur.AD_Role_ID = " + Env.getAD_Role_ID(Env.getCtx()) + 
				" AND sc.AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()) +
				" AND sc.AD_Window_ID IS NOT NULL ";			
	}
	
	/** Retorna la nómina de IDs de conversaciones según query.
	 * @param query DEBE contener como SELECT el campo C_SocialConversation_ID */
	protected static ArrayList<Integer> getConversations(String query) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(query);
			rs = ps.executeQuery();
			while (rs.next()) {
				ids.add(rs.getInt("C_SocialConversation_ID"));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs = null;
			ps = null;
		}
		return ids;
	}
	
	
	/**
	 * Retorna el detalle a mostrar para el registro en cuestión,
	 * basado en las columnas isidentifier segun la definicion de metadatos
	 */
	public static String getDetailFrom(int tableID, int recordID, boolean noDetailReturnRecordID) {
		// Tabla y registro validos?
		if (tableID <= 0 || recordID <= 0)
			return "";
		// Recuperar el PO de manera generica
		M_Table aTable = new M_Table(Env.getCtx(), tableID, null);
		PO aPO = aTable.getPO(recordID, null);
		// Crear el resultado
		StringBuffer retValue = new StringBuffer();
		for (String aDetailPart : aPO.getIdentifierValues())
			retValue.append(aDetailPart).append(" ");
		// Si no hay identificadores, devolver al menos el recordID (si el parámetro así lo requiere)
		if (noDetailReturnRecordID && retValue.length()==0)
			retValue.append(recordID);
		return retValue.toString();
	}
	
	
	/**
	 * Retorna las direcciones de mail de los participantes, excepto la del usuario actual
	 * @param includeThisUser también incluir el usuario actualmente logueado?
	 */
	public String getEmailTo(boolean includeThisUser) {
		StringBuffer mails = new StringBuffer();
		int count = 0;
		for (MSocialSubscription aSubscription : getSubscriptions()) {
			// Recuperar usuario
			MUser user = MUser.get(getCtx(), aSubscription.getAD_User_ID());
			if (user==null)
				continue;
			// Si el usuario recuperado no quiere recibir notificaciones o bien es el logueado, entonces omitir
			if (!user.isNotifyOnConversationActivity() || (!includeThisUser && Env.getAD_User_ID(getCtx()) == user.getAD_User_ID()))
				continue;
			
			String address = user.getEMail();
			if (address == null || address.trim().length() == 0)
				continue;
			mails.append(address).append(", ");
			count++;
		}
		return count > 0 ? mails.substring(0, mails.length()-2) : "";
	}
	
	/**
	 * Retorna el subject para uso en un mail
	 */
	public String getEmailSubject() {
		return " Nueva actividad en conversación. " + getSubject();
	}
	
	/**
	 * Retorna el body para uso en un mail
	 */
	public String getEmailBody() {
		String subject = (getSubject()!=null && getSubject().trim().length()>0 ? getSubject():"(sin asunto)") ;
		String tabla = getAD_Tab_ID() > 0 ? new M_Tab(getCtx(), getAD_Tab_ID(), get_TrxName()).get_Translation("Name")  : "(sin referencia)";
		String registro = getRecordID() > 0 ? MSocialConversation.getDetailFrom(getAD_Table_ID(), getRecordID(), true) : "(sin referencia)";
		StringBuffer mensajes = new StringBuffer();
		for (MSocialMessage aMessage : getMessages(false)) {
			mensajes.append("El ").append(aMessage.getSent()).append(" ")
					.append(MUser.get(getCtx(), aMessage.getSentBy()).getName()).append(" dijo ").append("\n")
					.append(aMessage.getMsgContent()).append("\n")
					.append("\n");
		}
		
		return 
				"Conversación: " + getC_SocialConversation_ID() + "\n\n" +
				"Asunto: " + subject + "\n\n" +
				"Tabla: " + tabla + "\n\n" + 
				"Registro: " + registro + "\n\n" +
				"Mensajes: " + "\n\n" + mensajes.toString();
	}
	
	
	/**
	 * suscribe un usuario a esta conversación
	 * @throws exception en caso de error, o bien si la suscripción ya existía (en caso de exceptionOnAlreadySubscribed sea true)
	 * @return true si se genera una nueva suscripcion o false si se utiliza una existente
	 */
	public boolean subscribe(int userID, boolean exceptionOnAlreadySubscribed, boolean markAsRead) throws Exception {
		boolean retValue = true;
		// Ya existe?
		MSocialSubscription aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		if (exceptionOnAlreadySubscribed && aSubs.getC_SocialSubscription_ID() > 0)
			throw new Exception("La suscripción ya existe!");
		if (aSubs.getC_SocialSubscription_ID() > 0) {
			retValue = false;
		}
		
		// Si no existe, crearla.  Como estamos suscribiendo a un nuevo participante, se marca como no leida por defecto
		aSubs.setC_SocialConversation_ID(getC_SocialConversation_ID());
		aSubs.setAD_User_ID(userID);
		aSubs.setClientOrg(getAD_Client_ID(), getAD_Org_ID());
		aSubs.setRead(markAsRead);
		if (!aSubs.save())
			throw new Exception("Error al suscribir: " + CLogger.retrieveErrorAsString());
		
		return retValue;
	}

	/**
	 * desuscribe un usuario de esta conversacion
	 * @throws exception en caso de error, o bien si la suscripción no existía
	 */
	public void unsubscribe(int userID) throws Exception {
		// No existe?
		MSocialSubscription aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		if (aSubs.getC_SocialSubscription_ID() == 0)
			throw new Exception("La suscripción no existe!");
		
		// Intentar eliminar
		if (!aSubs.delete(true))
			throw new Exception("Error al desuscribir: " + CLogger.retrieveErrorAsString());		
	}

	
	/**
	 * marca una conversación como leida o no leida según parametro asRead
	 * @throws exception en caso de error
	 */
	public void markAsReadNotRead(int userID, boolean asRead) throws Exception {
		// No existe la suscripción?, crearla
		MSocialSubscription aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		if (aSubs.getC_SocialSubscription_ID() == 0) {
			subscribe(userID, true, false);
			// Releer
			aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		}

		// Actualizar estado y persistir
		aSubs.setRead(asRead);
		if (!aSubs.save())
			throw new Exception(CLogger.retrieveErrorAsString());		
	}	
	
	/**
	 * Retorna true si el usuario esta suscripto a esta conversación o falso en caso contrario
	 */
	public boolean isSuscribed(int userID) {
		MSocialSubscription aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		return aSubs.getC_SocialSubscription_ID() != 0;		
	}

	/**
	 * Retorna true si el usuario ya marco como leida a esta conversación o falso en caso contrario
	 */
	public boolean isRead(int userID) {
		MSocialSubscription aSubs = MSocialSubscription.getForConversationAndUser(getC_SocialConversation_ID(), userID, getCtx(), get_TrxName());
		return aSubs.getC_SocialSubscription_ID() != 0 && aSubs.isRead();
	}
	
	public int getAttachmentID() {
		int attachID = DB.getSQLValue(get_TrxName(), 	
										" SELECT AD_Attachment_ID " +
										" FROM AD_Attachment " +
										" WHERE AD_Table_ID = " + Table_ID + 
										" AND record_ID = " + getC_SocialConversation_ID());
		if (attachID < 0)
			attachID = 0;
		return attachID;
	}
	
}
