package org.openXpertya.pos.model;

public class PaymentTerm {

	// Variables de instancia
	
	/** Id */
	private int id = 0;
	/** Nombre */
	private String name;
	/** Id de medio de pago */
	private Integer posPaymentMediumID = 0;
	
	
	public PaymentTerm(int id, String name, Integer posPaymentMediumID) {
		setId(id);
		setName(name);
		setPosPaymentMediumID(posPaymentMediumID);
	}
	
	// Getters y Setters

	public void setId(int id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void setPosPaymentMediumID(Integer posPaymentMediumID) {
		this.posPaymentMediumID = posPaymentMediumID;
	}

	public Integer getPosPaymentMediumID() {
		return posPaymentMediumID;
	}

	@Override
	public String toString(){
		return getName();
	}
}
