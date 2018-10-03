package org.openXpertya.pos.model;

public class Promotion {

	/** ID de la promoción */
	private int id;
	/** Tipo de Promoción */
	private String type;
	/** Nombre */
	private String name;
	/** Código Promocional */
	private String code;
	
	public Promotion(int id, String type, String name, String code) {
		setId(id);
		setType(type);
		setName(name);
		setCode(code);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
