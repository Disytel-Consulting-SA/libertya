package org.openXpertya.util;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UserAuthorizationOperation {

	/** UID o Value del proceso de autorización */
	private String operation;
	
	/** ID del proceso de autorización */
	private Integer authProcessID;
	
	/** Descripción (log) de la operación que se está realizando */
	private String recordLog;
	
	/** Guardar inmediatamente */
	private boolean mustSave = false;
	
	/**
	 * Momento en que se creó esta autorización para registrar temporalmente el
	 * hecho
	 */
	private Timestamp authTime;
	
	/** Importe de la operación */
	private BigDecimal amount;
	
	/** Porcentaje de la operación */
	private BigDecimal percentage;
	
	public UserAuthorizationOperation() {}
	
	public UserAuthorizationOperation(String operation) {
		setAuthTime(Env.getTimestamp());
		setOperation(operation);
		initProcessID();
	}
	
	public UserAuthorizationOperation(String operation, String operationLog, boolean mustSave, Timestamp authTime, BigDecimal amount, BigDecimal percentage) {
		this(operation);
		setRecordLog(operationLog);
		setMustSave(mustSave);
		setAuthTime(authTime);
		setAmount(amount);
		setPercentage(percentage);
	}
	
	
	/**
	 * Obtiene el ID del proceso relacionado con la operación parámetro
	 * 
	 * @return ID del proceso, null o 0 en caso que no exista
	 */
	private void initProcessID(){
		// Obtengo el proceso a partir del UID
		Integer processID = DB
				.getSQLValue(
						null,
				"SELECT ad_process_id FROM ad_process WHERE ad_componentobjectuid = '"
						+ getOperation() + "'");
		// Si no existe, lo busco por el value que debería tener
		if(processID == null || processID <= 0){ 
			if (!Util.isEmpty(UserAuthConstants.getProcessValue(getOperation()), true)) {
				processID = DB.getSQLValue(
						null,
						"SELECT ad_process_id FROM ad_process WHERE upper(trim(value)) = upper(trim('"
							+ UserAuthConstants.getProcessValue(getOperation())
							+ "'))");
			}
		}
		
		setAuthProcessID(processID);
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Integer getAuthProcessID() {
		return authProcessID;
	}

	public void setAuthProcessID(Integer authProcessID) {
		this.authProcessID = authProcessID;
	}

	public String getRecordLog() {
		return recordLog;
	}

	public void setRecordLog(String recordLog) {
		this.recordLog = recordLog;
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
