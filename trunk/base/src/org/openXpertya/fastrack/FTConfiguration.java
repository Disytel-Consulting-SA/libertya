package org.openXpertya.fastrack;

public class FTConfiguration {

	//Variables de instancia 
	
	/** País */
	
	private int country_id;
	
	/** Región */
	
	private int region_id;

	/** Compañía template */
	
	private int client_template_id;
	
	/** Nueva compañía */
	
	private int new_client_id;
	
	/** Moneda */
	
	private int c_currency_id;
	
	/** Nombre de la transacción */
	
	private String trxName;
	

	//Constructores
	
	
	public FTConfiguration(){
		
	}
	
	//Getters and Setters
	
	public void setCountry_id(int country_id) {
		this.country_id = country_id;
	}

	public int getCountry_id() {
		return country_id;
	}

	public void setRegion_id(int region_id) {
		this.region_id = region_id;
	}

	public int getRegion_id() {
		return region_id;
	}

	public void setClient_template_id(int client_template_id) {
		this.client_template_id = client_template_id;
	}

	public int getClient_template_id() {
		return client_template_id;
	}

	public void setNew_client_id(int new_client_id) {
		this.new_client_id = new_client_id;
	}

	public int getNew_client_id() {
		return new_client_id;
	}

	public void setC_currency_id(int c_currency_id) {
		this.c_currency_id = c_currency_id;
	}

	public int getC_currency_id() {
		return c_currency_id;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	public String getTrxName() {
		return trxName;
	}
	
	
	
	
}
