package org.openXpertya.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MPreference;
import org.openXpertya.model.MSocialConversation;
import org.openXpertya.model.MSocialMessage;
import org.openXpertya.model.MSocialSubscription;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class SocialConversationModel {

	/**
	 * Inicializa la conversación o recupera la existente (para el registro y tabla dados) 
	 */
	protected static void initConversation(MSocialConversation currentConversation, Integer tableID, Integer recordID, Integer windowID, Integer tabID) {
		// Crear/recuperar conversacion (cabecera)
		if (currentConversation.getC_SocialConversation_ID() == 0) {
			currentConversation.setClientOrg(Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
			currentConversation.setStartedBy(Env.getAD_User_ID(Env.getCtx()));
			if (tableID != null)
				currentConversation.setAD_Table_ID(tableID);
			if (recordID != null)
				currentConversation.setRecordID(recordID);
			if (windowID != null)
				currentConversation.setAD_Window_ID(windowID);
			if (tabID != null)
				currentConversation.setAD_Tab_ID(tabID);
		}
		currentConversation.save(); 
	}
	
	/**
	 * Envio de un nuevo mensaje en una conversación
	 */
	public static void sendMessage(MSocialConversation currentConversation, String conversationSubject, String message, Integer tableID, Integer recordID, Integer windowID, Integer tabID) throws Exception {

		// Hay mensaje a enviar?
		if (message == null || message.trim().length() == 0)
			throw new Exception(Msg.translate(Env.getCtx(), "MustWriteAMessage"));
			
		initConversation(currentConversation, tableID, recordID, windowID, tabID);
		
		currentConversation.setSubject(conversationSubject);
		if (!currentConversation.save()) 
			throw new Exception(Msg.translate(Env.getCtx(), "ErrorCreatingConversation") + ": " + CLogger.retrieveErrorAsString());
		
		// Crear mensaje
		MSocialMessage aSocialMessage = new MSocialMessage(Env.getCtx(), 0, null);
		aSocialMessage.setMsgContent(message);
		aSocialMessage.setClientOrg(Env.getAD_Client_ID(Env.getCtx()), Env.getAD_Org_ID(Env.getCtx()));
		aSocialMessage.setC_SocialConversation_ID(currentConversation.getC_SocialConversation_ID());
		aSocialMessage.setSentBy(Env.getAD_User_ID(Env.getCtx()));
		if (!aSocialMessage.save()) 
			throw new Exception(Msg.translate(Env.getCtx(), "ErrorSendingMessage") + ": " + CLogger.retrieveErrorAsString());
		
		// Marcar la conversacion como no leida para quienes están suscriptos
		for (MSocialSubscription aSubs : currentConversation.getSubscriptions()) {
			// Para el usuario que escribe el mensaje, no modificar el status
			if (aSubs.getAD_User_ID() == Env.getAD_User_ID(Env.getCtx()))
				continue;
			aSubs.setRead(false);
			if (!aSubs.save())
				throw new Exception(Msg.translate(Env.getCtx(), "ErrorUpdatingSubscription") + ": " + CLogger.retrieveErrorAsString());
		}
		
		sendEmail(currentConversation);

	}

	/**
	 * Valida si el usuario puede se agregado a la conversación según los permisos de acceso a la ventana
	 */
	public static boolean canBeSubscribed(MSocialConversation currentConversation, int userID) {
		
		// Si no hay política de seguridad aplicada, entonces puede suscribir cualquier usuario
		if (!MSocialConversation.isEnabledConversationSecurityPolicy())
			return true;
		
		// Si no referencia a una ventana/tabla en particular, entonces no hay limitaciones
		if (currentConversation.getAD_Window_ID() <= 0)
			return true;
		int count = DB.getSQLValue(null, 	" SELECT count(1) " +
											" FROM AD_User_Roles ur " +
											" INNER JOIN AD_Window_Access wa ON ur.AD_Role_ID = wa.AD_Role_ID " +
											" WHERE wa.AD_Window_ID = " + currentConversation.getAD_Window_ID() + 
											" AND ur.AD_User_ID = " + userID + 
											" AND ur.AD_Client_ID = " + Env.getAD_Client_ID(Env.getCtx()));
		return count > 0;
	}
	
	/**
	 * Suscribe/desuscribe de conversación
	 */
	public static void subscribe(MSocialConversation currentConversation, int userID, boolean actionSubscribe, boolean exceptionOnAlreadySubscribed, boolean markAsRead) throws Exception {
		initConversation(currentConversation, currentConversation.getAD_Table_ID(), currentConversation.getRecordID(), currentConversation.getAD_Window_ID(), currentConversation.getAD_Tab_ID());
		if (actionSubscribe) {
			boolean subscribed = currentConversation.subscribe(userID, exceptionOnAlreadySubscribed, markAsRead);
			// Si se está incorporando un nuevo participante, entonces enviarle mail
			if (subscribed && Env.getAD_User_ID(Env.getCtx()) != userID && !markAsRead)
				sendEmail(currentConversation, userID);
		} else {
			currentConversation.unsubscribe(userID);
		}
	}

	/**
	 * Marca conversación como leida/no leida
	 */
	public static void markAsReadNotRead(MSocialConversation currentConversation, int userID, boolean asRead) throws Exception {
		currentConversation.markAsReadNotRead(userID, asRead);

	}
	
	/**
	 * Envío asincrónico del mail, a todos los destinatarios
	 */
	public static void sendEmail(MSocialConversation aConversation) {
		sendEmail(aConversation, null);
	}
	
	/**
	 * Envío asincrónico del mail, unicamente al destinatario indicado en userIDOnly
	 */
	public static void sendEmail(MSocialConversation aConversation, Integer userIDOnly) {
		Emailer emailer = new Emailer();
		emailer.currentConversation = aConversation;
		emailer.userIDOnly = userIDOnly;
		new Thread(emailer).start();
	}

	
	public static String getAttachmentCountStr(String attachmentText, MSocialConversation currentConversation) {
		int cant = 0;
		try {
			cant = currentConversation.getAttachment(true).getEntries().length;
		} catch (Exception e) { /* No hay adjuntos o similar */ }
		int pos = attachmentText.indexOf("(");
		String newValue = attachmentText.substring(0, pos);
		newValue = newValue + "(" + cant + ")";
		return newValue;
	}
	
	// =====================================================================================
	
	public static class Emailer implements Runnable {

		public MSocialConversation currentConversation = null;
		public Integer userIDOnly = null;
		@Override
		public void run() {
			if (currentConversation == null || currentConversation.getC_SocialConversation_ID() == 0)
				return;
			
			// Recuperar informacion para incorporar al cuerpo del mail
			MClient client = new MClient(Env.getCtx(), Env.getAD_Client_ID(Env.getCtx()), null);
			String to = 		getTo();
			String subject = 	getSubject(client.getName());  
			String body = 		getBody();

			// Hay efectivamente algún destinatario?
			if (to==null || to.length()==0)
				return;
			
			// Enviar mail, loggear y presentar en consola
			EMail mail = new EMail( client, null, to, subject, body);
			String response = mail.send();
			if (!EMail.SENT_OK.equals(response)) {
				CLogger.getCLogger("Social Emailer").warning(Msg.translate(Env.getCtx(), "ErrorSendingMail") + ": " + response);
			}
		}	
		
		
		protected String getTo() {
			// Es para un usuario o para muchos?
			if (userIDOnly==null || userIDOnly <= 0)
				return currentConversation.getEmailTo(false);
			
			// Si es para un usuario en particular, verificar si quiere que se reciba mail bajo actividad
			MUser aUser = MUser.get(Env.getCtx(), userIDOnly);
			if (aUser == null)
				return null;
			
			if (aUser.isNotifyOnConversationActivity() && aUser.getEMail() != null && aUser.getEMail().length() > 0)
				return MUser.get(Env.getCtx(), userIDOnly).getEMail();
			
			// En cualquier otro caso, retornar null
			return null;
		}
		
		protected String getSubject(String clientName) {
			StringBuffer subject = new StringBuffer();
			subject.append("[LYC:").append(clientName).append("] ").append(currentConversation.getEmailSubject());
			return subject.toString();
		}
		
		protected String getBody() {
			return currentConversation.getEmailBody();
		}
		
	}
	
	
	// =====================================================================================
	
	public static class ConversationTableModel extends AbstractTableModel {
		
		String[] columnNames = {Msg.translate(Env.getCtx(), "Message")};
		ArrayList<ArrayList<Object>> rowData = new ArrayList<ArrayList<Object>>();
		
	    public String getColumnName(int col) {
	        return columnNames[col].toString();
	    }
	    
	    public int getRowCount() { return rowData.size(); }
	    
	    public int getColumnCount() { return columnNames.length; }
	    
	    public Object getValueAt(int row, int col) {
	        return rowData.get(row).get(col);
	    }
	    
	    public boolean isCellEditable(int row, int col) { return false; }
	    
	    public void setValueAt(Object value, int row, int col) {
	    	// La tabla es de solo lectura
	    }
	    
	    public void reload(MSocialConversation aConversation) throws Exception {
	    
	    	rowData = new ArrayList<ArrayList<Object>>();
	    	if (aConversation.getC_SocialConversation_ID() == 0) {
	    		fireTableDataChanged();
	    		return;
	    	}
	    	
	    	PreparedStatement ps = DB.prepareStatement(	" SELECT sm.sent, u.name, sm.msgcontent as message" +
	    												" FROM C_SocialMessage sm " +
	    												" INNER JOIN AD_User u on sm.sentby = u.ad_user_id " +
	    												" WHERE C_SocialConversation_ID = " + aConversation.getC_SocialConversation_ID() + 
	    												" ORDER BY sm.sent DESC");
	    	ResultSet rs = ps.executeQuery();
	    	while (rs.next()) {
	    		
	    		// Primera fila
	    		ArrayList<Object> aRow = new ArrayList<Object>();
	    		aRow.add(rs.getString("sent") + " " + rs.getString("name") + " " + Msg.translate(Env.getCtx(), "said") + ":");
	    		rowData.add(aRow);
	    		
	    		// Filas de contenido	    		
	    		String message = rs.getString("message");
	    		if (message == null)
	    			message = "";
	    		int maxLong = 120;
	    		while (message.length() > 0) {	// Crear líneas a fin de visualizar el contenido completo
	    			int chunk = maxLong < message.length() ? maxLong : message.length();
	    			int xtra = message.indexOf(" ", chunk);	// Llegar hasta encontrar un espacio (si es que existe)
	    			if (xtra > chunk)
	    				chunk = xtra;
	    			String line = message.substring(0, chunk);
	    			if (line.trim().length()>0) {
	    				ArrayList<Object> aRowMsj = new ArrayList<Object>();
		    			aRowMsj.add(line.toString());
		    			rowData.add(aRowMsj);
	    			}
		    		message = message.substring(chunk);	// Actualizar pendiente del mensaje 
	    		}	
	    		
	    		// Fila de separación
	    		ArrayList<Object> aRowFoot = new ArrayList<Object>();
	    		aRowFoot.add("");
	    		rowData.add(aRowFoot);
	    	}
	    	fireTableDataChanged();

	    	rs.close();
	    	ps.close();
	    	rs = null;
	    	ps = null;
	    }
	     
	}
	
	

}
