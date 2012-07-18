package org.openXpertya.pos.model;

import org.openXpertya.util.AUserAuthModel;

public class AuthOperation {

	/** Descripción de la operación a autorizar */
	private String opDescription;
	
	/** Tipo de operación a la cual pertenece */
	private String operationType;
	
	/** Booleano que determina si fue autorizado o no */
	private boolean authorized;
	
	/**
	 * Tipo de operación que describe el momento a autorizar donde se debe
	 * autorizar
	 */
	private String authorizeMoment;
	
	/**
	 * Booleano que determina si tiene una autorización perezoza, es decir, este
	 * flag permite que cuando se recorren las operaciones para autorizar se
	 * marquen autorizadas o no
	 */
	private boolean lazyAuthorization;
	
	/** Autorización que autorizó esta operación */
	private AUserAuthModel userAthorized;
	
	public AuthOperation() {
		setOpDescription(null);
		setOperationType(null);
		setAuthorized(false);
		setAuthorizeMoment(null);
		setLazyAuthorization(false);
	}
	
	public AuthOperation(String operationType, String opDescription, String authorizeMoment) {
		this();
		setOpDescription(opDescription);
		setOperationType(operationType);
		setAuthorizeMoment(authorizeMoment);
	}

	public void setOpDescription(String opDescription) {
		this.opDescription = opDescription;
	}

	public String getOpDescription() {
		return opDescription;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOperationType() {
		return operationType;
	}

	public void setAuthorized(boolean authorized) {
		this.authorized = authorized;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public void setAuthorizeMoment(String authorizeMoment) {
		this.authorizeMoment = authorizeMoment;
	}

	public String getAuthorizeMoment() {
		return authorizeMoment;
	}

	public void setUserAthorized(AUserAuthModel userAthorized) {
		this.userAthorized = userAthorized;
	}

	public AUserAuthModel getUserAthorized() {
		return userAthorized;
	}

	public void setLazyAuthorization(boolean lazyAuthorization) {
		this.lazyAuthorization = lazyAuthorization;
	}

	public boolean isLazyAuthorization() {
		return lazyAuthorization;
	}

}
