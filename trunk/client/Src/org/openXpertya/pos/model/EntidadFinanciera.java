package org.openXpertya.pos.model;

public class EntidadFinanciera {

	private int id;
	private String name;
	
	
	/**
	 * 
	 */
	public EntidadFinanciera() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param entidadFinancieraId
	 * @param name
	 */
	public EntidadFinanciera(int entidadFinancieraId, String name) {
		super();
		// TODO Auto-generated constructor stub
		this.id = entidadFinancieraId;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int entidadFinancieraId) {
		this.id = entidadFinancieraId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
	
}
