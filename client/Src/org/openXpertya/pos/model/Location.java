package org.openXpertya.pos.model;

public class Location {

	private int id;
	private String name;
	
	/**
	 * @param locationID
	 * @param name
	 */
	public Location(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return Devuelve locationID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param locationID Fija o asigna locationID.
	 */
	public void setId(int locationID) {
		this.id = id;
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

	@Override
	public String toString() {
		return getName();
	}
	
	
	
}
