package org.openXpertya.util;

import java.util.List;



public class UserAuthData {
	
	/** Nombre de usuario */
	private String userName;
	
	/** Clave */
	private String password;
	
	/** Booleano que determina si estamos en TPV o no */
	private boolean isForPOS;
	
	/** Operaciones a autorizar */
	private List<String> authOperations;
	
	/** El usuario es perfil Supervisor de TPV */
	private boolean isPosSupervisor;
	
	/** ID de usuario */
	private Integer userID;
	
	public UserAuthData(){
		setForPOS(false);
		setPosSupervisor(false);
	}
	
	public UserAuthData(Integer userID) {
		this();
		setUserID(userID);
	}
	
	public UserAuthData(String userName, String password) {
		this();
		setUserName(userName);
		setPassword(password);
	}
	
	// Getters y Setters

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setForPOS(boolean isForPOS) {
		this.isForPOS = isForPOS;
	}

	public boolean isForPOS() {
		return isForPOS;
	}

	public void setAuthOperations(List<String> authOperations) {
		this.authOperations = authOperations;
	}

	public List<String> getAuthOperations() {
		return authOperations;
	}

	public void setPosSupervisor(boolean isPosSupervisor) {
		this.isPosSupervisor = isPosSupervisor;
	}

	public boolean isPosSupervisor() {
		return isPosSupervisor;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public Integer getUserID() {
		return userID;
	}

}
