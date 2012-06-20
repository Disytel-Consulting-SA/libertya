package org.openXpertya.fastrack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FTColumnsValidation extends FTModule {

	
	/** Asociación de una columna con una validación */
	
	private Map<Integer, Integer> columnas;
	
	
	// Constructores
	
	public FTColumnsValidation() {

	}

	public FTColumnsValidation(String trxName) {
		super(trxName);
		this.setColumnas(new HashMap<Integer, Integer>());
		this.inicializarColumnas();
	}

	
	// Getters y Setters

	public void setColumnas(Map<Integer, Integer> columnas) {
		this.columnas = columnas;
	}

	public Map<Integer, Integer> getColumnas() {
		return columnas;
	}

	
	// Métodods varios
	
	private void addColumn(Integer key,Integer value){
		this.getColumnas().put(key, value);
	}
	
	
	private void inicializarColumnas(){
		this.addColumn(5302,1000035);
	}
	
	
	/**
	 * Agrega una validación nueva a columnas 
	 */
	public void ejecutar() throws Exception {
		Set<Integer> claves = this.getColumnas().keySet();
		String sql;
		for (Integer key : claves) {
			sql = "UPDATE ad_column SET ad_val_rule_id = ? WHERE ad_column_id = ?";
			ExecuterSql.executeUpdate(sql, this.getTrxName(), new Object[]{this.getColumnas().get(key),key});
		}
	}
	
	public void deshacer() throws Exception {

	}


}
