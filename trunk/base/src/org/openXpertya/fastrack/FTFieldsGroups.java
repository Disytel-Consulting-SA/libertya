package org.openXpertya.fastrack;



public class FTFieldsGroups extends FTModule {

	//Constructores
	
	public FTFieldsGroups() {
		
	}

	public FTFieldsGroups(String trxName) {
		super(trxName);
	}

	//Métodos varios
	
	/**
	 * Elimino la asociación de los campos que va a la estándard con su grupo de campos para que 
	 * no aparezca el grupo de campos pelado (sin campos asociados)
	 */
	public void ejecutar() throws Exception {
		/*
		 * Elimino la relación del campo oculto con el grupo de campos
		 * ----------------------------------------------------------------------------------
		 * UPDATE ad_field 
		 * SET ad_fieldgroup_id = null 
		 * WHERE entitytype = 'D'
		 * ---------------------------------------------------------------------------------- 
		 */
		
		String sql = "UPDATE ad_field SET ad_fieldgroup_id = null WHERE entitytype = 'D'";
		
		//Ejecuto la query
		ExecuterSql.executeUpdate(sql, this.getTrxName());		
	}
	
	public void deshacer() throws Exception {

	}

}
