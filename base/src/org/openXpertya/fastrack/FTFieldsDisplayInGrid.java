package org.openXpertya.fastrack;

public class FTFieldsDisplayInGrid extends FTFields {

	// Constructores 
	
	public FTFieldsDisplayInGrid() {

	}

	public FTFieldsDisplayInGrid(String trxName) {
		super(trxName);
	}


	// Métodos varios
		
	public void ejecutar() throws Exception {
		// Ocultar en grilla todo lo que es botón y los campos de compañía 
		String sql = "UPDATE ad_field SET isdisplayedingrid = 'N' WHERE (ad_field_id IN (SELECT ad_field_id FROM ad_field as f INNER JOIN ad_column as c ON (f.ad_column_id = c.ad_column_id) WHERE (ad_reference_id = 28) OR (f.name = 'Client')))";
		
		// Ejecuto el script
		ExecuterSql.executeUpdate(sql, this.getTrxName());
	}

	
	public void deshacer() throws Exception {

	}

	
}
