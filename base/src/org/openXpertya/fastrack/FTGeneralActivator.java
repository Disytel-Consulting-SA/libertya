package org.openXpertya.fastrack;

public class FTGeneralActivator extends FTModule {

	// Variables de instancia
	
	/** Nombre de la tabla a modificar (isactive) */
	
	private String tableName;
	
	
	// Constructores
	
	public FTGeneralActivator() {

	}

	public FTGeneralActivator(String tableName,String trxName){
		this.setTrxName(trxName);
		this.setTableName(tableName);
	}

	
	// Getters and Setters
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	
	//Métodos varios

	
	public void ejecutar() throws Exception {
		this.modEntityType(this.getTableName(), true);
	}
	
	
	public void deshacer() throws Exception {
		this.modEntityType(this.getTableName(), false);
	}


}
