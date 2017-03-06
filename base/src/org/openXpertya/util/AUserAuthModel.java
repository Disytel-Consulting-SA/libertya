package org.openXpertya.util;

import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MUser;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Process_Access;
import org.openXpertya.reflection.CallResult;

public abstract class AUserAuthModel {
	
	/**
	 * @return la subclase a partir de la configuración de la compañía del
	 *         contexto
	 */
	public static AUserAuthModel get(){
		// Verifico si la compañía posee claves únicas activas, en ese caso
		// obtengo la clase para autorización con claves únicas, sino
		// autorización por usuario y clave
		Integer clientID = Env.getAD_Client_ID(Env.getCtx());
		MClientInfo clientInfo = MClientInfo.get(Env.getCtx(), clientID);
		AUserAuthModel userAuthModel = clientInfo.isUniqueKeyActive()?new AUKUserAuthModel():new AUPUserAuthModel();
		return userAuthModel;
	}
	
	/**
	 * Verifica si es posible la autorización a partir de datos de autorización
	 * de usuario
	 * 
	 * @param userAuthData
	 *            datos de usuario necesarios para verificar si está autorizado
	 *            para realizar las operaciones correspondientes
	 * @return resultado de la llamada
	 */
	public CallResult validateAuthorization(UserAuthData userAuthData){
		Properties ctx = Env.getCtx();
		CallResult resultReturn = new CallResult();
		// ----------------------------------
		// 		Pre-Validaciones
		// ----------------------------------
		CallResult preResult = preValidateAuthorization(userAuthData);
		// Si las validaciones iniciales retornan error y no había un error
		// anterior en la ejecución
		if (preResult != null && preResult.isError() && !resultReturn.isError()) {
			resultReturn.setMsg(preResult.getMsg(), true);
			return resultReturn;
		}
		// ----------------------------------
		// 		Validaciones
		// ----------------------------------
		// Validación para verificar si el usuario existe
		MUser user = getUser(userAuthData); 
		if(user == null){
			resultReturn.setMsg(Msg.getMsg(ctx, "AuthorizeUserEnteredNotExist"), true);
			return resultReturn;
		}
		// Validación por clave vencida
		if(MUser.isPasswordExpired(ctx, user.getID(), null)){
			resultReturn.setMsg(Msg.getMsg(ctx, "PasswordExpired"), true);
			return resultReturn;
		}
		// Verificar si el usuario posee permisos para realizar la operación
		// indicada
		boolean authorizedUser = isUserAuthorized(user.getID(), userAuthData.getAuthOperations());
		String notAuthorizedUserMsg = Msg.getMsg(ctx,
				"NotAllowedUserToCompleteOperation",
				new Object[] { UserAuthConstants.getProcessValue(userAuthData
						.getAuthOperations().toString().replaceAll("]", "")
						.replaceAll("\\[", "")) });
		String notAuthorizedUserShortMsg = Msg.getMsg(ctx,
				"NotAllowedUserToCompleteOperationShort");
		// La validación de perfil de Supervidor en el TPV pisa el permiso por
		// proceso, por lo tanto si el usuario posee permiso a nivel perfil de
		// supervisor de TPV pero no a nivel proceso (operación a autorizar)
		// entonces se permite la autorización. Esto se debe a que debe quedar
		// la compatibilidad con la forma anterior de autorización en instancias
		// ya instaladas.
		if(userAuthData.isForPOS()){
			authorizedUser = authorizedUser || userAuthData.isPosSupervisor();
			notAuthorizedUserMsg += " @OR@ @UserWithoutSupervisorAuth@";
		}
		// Si definitivamente no está autorizado el usuario a autorizar,
		// entonces error 
		if(!authorizedUser){
			resultReturn.setMsg(
					userAuthData.isShowAditionalNoAccessErrorMsg() ? Msg
							.parseTranslation(ctx, notAuthorizedUserMsg) : Msg
							.getMsg(ctx, notAuthorizedUserShortMsg), true);
			return resultReturn;
		}
		// ----------------------------------
		// 		Post-Validaciones
		// ----------------------------------		
		CallResult postResult = postValidateAuthorization(userAuthData);
		// Si las validaciones posteriores retornan error y no había un error
		// anterior en la ejecución
		if(postResult != null && postResult.isError() && !resultReturn.isError()){
			resultReturn.setMsg(postResult.getMsg(), true);
			return resultReturn;
		}
		return resultReturn;
	}

	/**
	 * Verifica si el usuario parámetro está autorizado a ejecutar la operación
	 * parámetro. A su vez, verifica que el proceso relacionado con la operación
	 * exista.
	 * 
	 * @param userID
	 *            id de usuario
	 * @param authOperation
	 *            operación
	 * @return true si el usuario está autorizado a ejecutar la operación
	 *         parámetro y el proceso relacionado con la operación existe, false
	 *         caso contrario
	 */
	protected boolean isUserAuthorized(Integer userID, String authOperation){
		// Obtengo el proceso a partir del UID
		Integer processID = DB
				.getSQLValue(
						null,
				"SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = '"
						+ authOperation + "'");
		// Si no existe, lo busco por el value que debería tener
		if(processID == null || processID <= 0){
			// Si no existe correspondiente al uid del proceso, entonces error
			if (Util.isEmpty(UserAuthConstants.getProcessValue(authOperation), true)) {
				return false;
			}
			processID = DB
			.getSQLValue(
					null,
					"SELECT ad_process_id FROM ad_process WHERE upper(trim(value)) = upper(trim('"
							+ UserAuthConstants.getProcessValue(authOperation)
							+ "'))");
		}
		// Verifico el acceso del usuario al proceso
		return (processID != null && processID > 0)
				&& PO.hasAccessToComponent(Env.getCtx(), userID,
						X_AD_Process_Access.Table_Name, "", "ad_process_id",
						processID, null);
	}
	
	
	/**
	 * Verifica si el usuario parámetro está autorizado a ejecutar las operaciones
	 * parámetro. A su vez, verifica que el proceso relacionado con la operación
	 * exista.
	 * 
	 * @param userID
	 *            id de usuario
	 * @param authOperations
	 *            operaciones a autorizar
	 * @return true si el usuario está autorizado a ejecutar la operación
	 *         parámetro y el proceso relacionado con la operación existe, false
	 *         caso contrario
	 */
	protected boolean isUserAuthorized(Integer userID, List<String> authOperations){
		boolean authorized = true;
		for (int i = 0; i < authOperations.size() && authorized ; i++) {
			authorized = isUserAuthorized(userID, authOperations.get(i));
		}
		return authorized;
	}
	
	
	/**
	 * Obtener el usuario de autorización
	 * 
	 * @param userAuthData
	 *            datos del usuario necesarios para obtenerlo
	 * @return usuario que corresponde con los datos pasados, null en caso que
	 *         no exista
	 */
	public MUser getUser(UserAuthData userAuthData){
		Integer userID = userAuthData.getUserID();
		if(Util.isEmpty(userID, true)){
			userID = getUserID(userAuthData);
		}
		return Util.isEmpty(userID, true) ? null : MUser.get(Env.getCtx(),
				userID);
	}

	/**
	 * Obtener el id de usuario de autorización
	 * 
	 * @param userAuthData
	 *            datos del usuario necesarios para obtenerlo
	 * @return id de usuario que corresponde con los datos pasados, null en caso
	 *         que no exista
	 */
	public Integer getUserID(UserAuthData userAuthData){
		Integer userID = userAuthData.getUserID();
		if(Util.isEmpty(userID, true)){
			MUser user = searchUser(userAuthData);
			userID = user != null ? user.getID() : null;
		}
		return userID; 
	}
	
	/**
	 * Cada subclase debe buscar el usuario a partir de los datos parámetro
	 * 
	 * @return usuario encontrado a partir de los datos, null caso contrario
	 */
	protected abstract MUser searchUser(UserAuthData userAuthData); 
	
	/**
	 * Validaciones de cada subclase al iniciar las validaciones de autorización
	 * 
	 * @param userAuthData
	 *            datos de usuario necesarios para verificar si está autorizado
	 *            para realizar las operaciones correspondientes
	 * @return resultado de la llamada
	 */
	public abstract CallResult preValidateAuthorization(UserAuthData userAuthData);
	
	/**
	 * Validaciones de cada subclase antes de finalizar las validaciones de
	 * autorización
	 * 
	 * @param userAuthData
	 *            datos de usuario necesarios para verificar si está autorizado
	 *            para realizar las operaciones correspondientes
	 * @return resultado de la llamada
	 */
	public abstract CallResult postValidateAuthorization(UserAuthData userAuthData);
}
