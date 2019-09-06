package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.util.AUserAuthModel;
import org.openXpertya.util.Env;

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
	
	/** Indica si es severo o error para que sea más llamativo (Rojo y Negrita) */
	private boolean severe = false;
	
	/** Registro de log de esta autorización */
	private String operationLog;
	
	/** Flag que determina que debe guardarse la autorización */
	private boolean mustSave = false;
	
	/**
	 * Momento en que se creó esta autorización para registrar temporalmente el
	 * hecho
	 */
	private Timestamp authTime = Env.getTimestamp();
	
	/** Importe de la operación */
	private BigDecimal amount;
	
	/** Porcentaje de la operación */
	private BigDecimal percentage;
	
	public AuthOperation() {
		setOpDescription(null);
		setOperationType(null);
		setAuthorized(false);
		setAuthorizeMoment(null);
		setLazyAuthorization(false);
		setSevere(false);
		setOperationLog(null);
	}
	
	public AuthOperation(String operationType, String opDescription, String authorizeMoment) {
		this();
		setOpDescription(opDescription);
		setOperationType(operationType);
		setAuthorizeMoment(authorizeMoment);
	}

	public AuthOperation(String operationType, String opDescription, String authorizeMoment, boolean isSevere) {
		this(operationType, opDescription, authorizeMoment);
		setSevere(isSevere);
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

	public boolean isSevere() {
		return severe;
	}

	public void setSevere(boolean severe) {
		this.severe = severe;
	}

	public String getOperationLog() {
		return operationLog;
	}

	public void setOperationLog(String operationLog) {
		this.operationLog = operationLog;
	}

	public boolean isMustSave() {
		return mustSave;
	}

	public void setMustSave(boolean mustSave) {
		this.mustSave = mustSave;
	}

	public Timestamp getAuthTime() {
		return authTime;
	}

	public void setAuthTime(Timestamp authTime) {
		this.authTime = authTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
}
