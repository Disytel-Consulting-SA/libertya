package org.openXpertya.fastrack;



public class FTFieldsDisplayLogic extends FTFields {

		
	//Constructores
	
	
	public FTFieldsDisplayLogic(String trxName) {
		super(trxName);
	}
	
	

	//Métodos varios
	
	/**
	 * Elimina la lógica de sólo lectura de los campos que no se deben mostrar en la versión fast-track
	 */
	public void ejecutar() throws Exception {
		//Seteo la lógica de despliegue a null a los fields con entitytype = 'D'
		String sql = "UPDATE ad_field SET displaylogic = null WHERE entitytype = 'D'";
		
		//Ejecuto el script
		ExecuterSql.executeUpdate(sql, this.getTrxName());
	}
	
	public void deshacer() throws Exception {
		
	}

}
