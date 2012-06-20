package org.openXpertya.pos.model;

import java.util.Arrays;
import java.util.List;

public abstract class CreditCard {

	/** Separador del string */
	public static final String STR_SEPARATOR = "&";
	
	/** Nombre del cliente */
	private String customerName = null;
	
	/** Nro de tarjeta */
	private String creditCardNo = null;

	/** String insertado por el lector de tarjeta */
	private String creditCardStr = null;
	
	/** Lista de string separados por el separador configurado */
	private List<String> creditCardStrParts;
	
	public CreditCard(String creditCardStr){
		setCreditCardStr(creditCardStr);
	}

	/**
	 * Método que cada subclase debe modificar y cargar los campos de nombre de
	 * cliente y nro de tarjeta
	 */
	public void loadFields(){
		// Cargo las partes del string del lector
		loadCreditCardStrParts();
		// Aviso a la tarjeta correspondiente que cargue los campos
		getFields();
	}
	
	/**
	 * Cargo las partes del string devuelto por el lector
	 */
	protected void loadCreditCardStrParts(){
		setCreditCardStrParts(Arrays.asList(getCreditCardStr().split(STR_SEPARATOR)));
	}
	
	/**
	 * Cada subclase debe cargar por campos de cada tarjeta 
	 */
	protected abstract void getFields();
	
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardStr(String creditCardStr) {
		this.creditCardStr = creditCardStr;
	}

	public String getCreditCardStr() {
		return creditCardStr;
	}

	public void setCreditCardStrParts(List<String> creditCardStrParts) {
		this.creditCardStrParts = creditCardStrParts;
	}

	public List<String> getCreditCardStrParts() {
		return creditCardStrParts;
	}
	
}
