package org.openXpertya.fastrack;

public class FTFieldsEliminate extends FTFields {

	//Constructores
	
	public FTFieldsEliminate() {
		
	}

	public FTFieldsEliminate(String trxName) {
		super(trxName);
	}

	//Métodos varios
	
	public void ejecutar() throws Exception{
		this.hideEntityType(this.getTableName(), true);
	}

	
	public void deshacer() throws Exception{
		this.modEntityType(this.getTableName(), false);
	}
	
	

}
