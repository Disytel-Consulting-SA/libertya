package org.openXpertya.pos.model;

public class User {

	private String name;
	private String password;
	private boolean overwriteLimitPrice;
	private boolean poSSupervisor;
		
	public User() {
		super();
	}

	/**
	 * @param name
	 * @param password
	 */
	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	/**
	 * @return Devuelve name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name Fija o asigna name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Devuelve password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password Fija o asigna password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Devuelve overwriteLimitPrice.
	 */
	public boolean isOverwriteLimitPrice() {
		return overwriteLimitPrice;
	}

	/**
	 * @param overwriteLimitPrice Fija o asigna overwriteLimitPrice.
	 */
	public void setOverwriteLimitPrice(boolean overwriteLimitPrice) {
		this.overwriteLimitPrice = overwriteLimitPrice;
	}

	/**
	 * @return Devuelve poSSupervisor.
	 */
	public boolean isPoSSupervisor() {
		return poSSupervisor;
	}

	/**
	 * @param poSSupervisor Fija o asigna poSSupervisor.
	 */
	public void setPoSSupervisor(boolean poSSupervisor) {
		this.poSSupervisor = poSSupervisor;
	}
	
	
}
