package org.openXpertya.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MUser;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_Process_Access;
import org.openXpertya.model.X_C_User_Authorization;
import org.openXpertya.reflection.CallResult;

public abstract class AUserAuthModel {
	
	/** Autorizaciones realizadas */
	private List<X_C_User_Authorization> authorizationsDone = new ArrayList<X_C_User_Authorization>();
	
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
	private CallResult validateAuthorization(Properties ctx, UserAuthData userAuthData){
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
		userAuthData.setUserID(user.getID());
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
	 * Valida que las operaciones puedan autorizarse y registros la autorización
	 * 
	 * @param userAuthData
	 *            datos de usuario y operaciones a autorizar
	 * @return resultado de la autorización
	 */
	public CallResult validateAuthorization(UserAuthData userAuthData){
		Properties ctx = Env.getCtx();
		CallResult resultReturn = validateAuthorization(ctx, userAuthData);
		if(!resultReturn.isError()){
			resultReturn = manageDoneAuthorizations(userAuthData);
		}
		return resultReturn;
	}
	
	/**
	 * Gestionar autorizaciones de usuario
	 * 
	 * @param userAuthData
	 *            datos de autorizaciones de usuario
	 * @return resultado de la operación
	 */
	public CallResult manageDoneAuthorizations(UserAuthData userAuthData){
		CallResult result = new CallResult();
		// Iterar por las autorizaciones para realizar las acciones de cada una
		X_C_User_Authorization ua;
		for (UserAuthorizationOperation ao : userAuthData.getAuthOperations()) {
			ua = createUserAuthorization(userAuthData, ao);
			// Si hay que guardar la autorización, se guarda en la BD
			if(ao.isMustSave()){
				if(!ua.save()){
					result.setMsg(CLogger.retrieveErrorAsString(), true);	
				}
			}
			// Sino queda registrada como autorización realizada hasta tanto se
			// libere o se confirme
			else{
				getAuthorizationsDone().add(ua);
			}
		}
		return result;
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
	protected boolean isUserAuthorized(Integer userID, UserAuthorizationOperation authOperation){
		// Obtengo ID del proceso a partir de la operación parámetro
		Integer processID = authOperation.getAuthProcessID();
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
	protected boolean isUserAuthorized(Integer userID, List<UserAuthorizationOperation> authOperations){
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

	/**
	 * Resetea las autorizaciones realizadas
	 */
	public void resetAuthorizationsDone(){
		getAuthorizationsDone().clear();
	}
	
	/**
	 * Registra en BD las autorizaciones realizadas
	 * 
	 * @throws Exception
	 *             en caso de error en el registro de alguna de ellas
	 */
	public void confirmAuthorizationsDone(String trxName, Integer invoiceID) throws Exception{
		for (X_C_User_Authorization auth : getAuthorizationsDone()) {
			// Campo Factura
			auth.setC_Invoice_ID(invoiceID);
			if(!auth.save(trxName)){
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
		resetAuthorizationsDone();
	}
	
	/**
	 * Crea las autorizaciones a guardar en la base de datos basados en una
	 * autorización de usuario
	 * 
	 * @param userAuthData
	 *            datos de autorizaciones de usuario
	 * @return lista de objectos a guardar
	 */
	protected List<X_C_User_Authorization> createUserAuthorization(UserAuthData userAuthData){
		List<X_C_User_Authorization> uas = new ArrayList<X_C_User_Authorization>();
		for (UserAuthorizationOperation ao : userAuthData.getAuthOperations()) {
			uas.add(createUserAuthorization(userAuthData, ao));
		}
		return uas;
	}
	
	/**
	 * Crea una autorización basada en los datos del usuario y en la OP
	 * autorizada parámetro
	 * 
	 * @param userAuthData
	 *            datos de usuario
	 * @param authorizationOP
	 *            operación autorizada
	 * @return PO con la operación autorizada
	 */
	protected X_C_User_Authorization createUserAuthorization(UserAuthData userAuthData, UserAuthorizationOperation authorizationOP){
		X_C_User_Authorization ua = new X_C_User_Authorization(Env.getCtx(), 0, null);
		ua.setAD_Org_ID(Env.getAD_Org_ID(ua.getCtx()));
		ua.setAD_Process_ID(authorizationOP.getAuthProcessID());
		ua.setAD_User_Auth_ID(userAuthData.getUserID());
		ua.setAD_User_Login_ID(Env.getAD_User_ID(ua.getCtx()));
		ua.setOperationLog(Msg.parseTranslation(ua.getCtx(), authorizationOP.getRecordLog()));
		ua.setAmount(authorizationOP.getAmount());
		ua.setPercentage(authorizationOP.getPercentage());
		ua.setAuthTime(authorizationOP.getAuthTime());
		return ua;
	}
	
	public List<X_C_User_Authorization> getAuthorizationsDone() {
		return authorizationsDone;
	}

	public void setAuthorizationsDone(List<X_C_User_Authorization> authorizationsDone) {
		this.authorizationsDone = authorizationsDone;
	}
}
