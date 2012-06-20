package org.openXpertya.fastrack;

public abstract class FTFields extends FTModule {

	
	//Variables de instancia
	
	/** Tabla que modifica este módulo */
	
	private String tableName = "ad_field";
	
	
	//Constructores
	
	/**
	 * Constructor por defecto. Valor por defecto al nombre de la tabla: Ad_field
	 */
	
	
	public FTFields(){
		
	}
	
	
	public FTFields(String trxName){
		this.setTrxName(trxName);
	}
	
	
	//Getters y Setters
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public String getTableName() {
		return tableName;
	}

}
