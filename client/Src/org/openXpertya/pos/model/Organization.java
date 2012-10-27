package org.openXpertya.pos.model;

public class Organization {

	private int id;
	private String name;
	
	public Organization() {
		// TODO Auto-generated constructor stub
	}
	
	public Organization(int id, String name) {
		setId(id);
		setName(name);
	}
	
	/**
	 * @return Devuelve id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            Fija o asigna id.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
