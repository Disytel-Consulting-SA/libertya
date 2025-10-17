package org.openXpertya.apps;

import java.security.SecureRandom;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MUser;
import org.openXpertya.reflection.CallResult;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class UtilsLogin {

	/**
     * El metodo verifica que el usuario este cargado y tenga un email valido.
     * Una vez verificado esto, genera una clave aleatoria de 8 caracteres alfanumerica: 8 caracteres, combinando letras mayusculas/minusculas y numeros
     * y envia email al usuario indicando la nueva clave. Ademas guarda como fecha de cambio de clave una vencida, para forzar reinicio de clave
     * en el proximo login
     * 
     * @author dREHER
     */
    public static CallResult olvideContrasena(String userName) {
    	CallResult cr = new CallResult();
    	
		int AD_User_ID = DB.getSQLValue(null, "SELECT AD_User_ID FROM AD_User WHERE Name=? AND IsActive='Y'", userName);
		if(AD_User_ID > 0) {
			
			MUser user = new MUser(Env.getCtx(), AD_User_ID, null);
			if(Util.isEmpty(user.getEMail(), true))
				cr.setMsg("No es posible recuperar contraseña porque no tiene mail asignado. Contactar con soporte", true);
			else {
			
				String passwordTemp = generateRandomPW();
				user.setPassword(passwordTemp);
				user.setLastPasswordChangeDate(null);
				user.save();
				
				debug("Se genero la nueva clave aleatoria: X" + passwordTemp + "_4D");
				boolean isSendOk = true;
				// Envio email con la nueva clave
				try {
					isSendOk = sendEmail(user, passwordTemp);
				}catch(Exception e) {
                    // e.printStackTrace();
					System.out.println("Llamado desde webui puede dar algun error...");
                }
				
				if(isSendOk)
					cr.setMsg("Se envió contraseña provisoria a su correo <" + user.getEMail() + "> correctamente", false);
				else
					cr.setMsg("No es posible enviar email. Contactar con soporte", true);
			}
			
		}else
			cr.setMsg("No existe el usuario indicado o no está activo", true);

		
		debug("olvideContrasena: " + cr.getMsg());
		return cr;
	}

    public static boolean sendEmail(MUser user, String passwordTemp) {
    	MClient m_client = MClient.get(Env.getCtx(), user.getAD_Client_ID());
    	String message = "Se realizó un blanqueo de contraseña, se asingó la clave <" + passwordTemp +
    			"> para ingreso provisorio, el sistema le solicitará la cambie al ingresar nuevamente";
    	
   	    System.setProperty("mail.address.map", "/META-INF/javamail.default.address.map");

    	
    	EMail em = new EMail( m_client, null, user, "Nueva contraseña Libertya", message);
		String status = em.send();

    	if( status.equals( EMail.SENT_OK )) {
    		debug("sendEmail.Envio el correo Ok");
    		return true;
    	} 
		
    	return false;
	}

	private static void debug(String string) {
    	System.out.println("--> ALogin. " + string);
	}

	/**
     * Metodo que genera una clave aleatoria que respete la premisa:
     * 8 caracteres, combinando letras mayusculas/minusculas y numeros
     * 
     * @return
     */
	private static String generateRandomPW() {
		 final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		 final SecureRandom RANDOM = new SecureRandom();

		 StringBuilder key = new StringBuilder(8);
		 for (int i = 0; i < 8; i++) {
			 int index = RANDOM.nextInt(CHARACTERS.length());
			 key.append(CHARACTERS.charAt(index));
		 }
		 return key.toString();

	}

	
}
