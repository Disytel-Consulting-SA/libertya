package org.openXpertya.fastrack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FTWindowsZoom extends FTModule {

	/** Asociación de una tabla con dos ventanas zoom */
	
	private Map<Integer, Integer[]> zooms;
	
	// Contructores
	
	public FTWindowsZoom() {

	}

	public FTWindowsZoom(String trxName) {
		super(trxName);
		this.setZooms(new HashMap<Integer, Integer[]>());
		this.inicializarZooms();
	}

	// Métodos varios
	

	public void setZooms(Map<Integer, Integer[]> zooms) {
		this.zooms = zooms;
	}

	public Map<Integer, Integer[]> getZooms() {
		return zooms;
	}

	
	// Métodos varios
	
	public void addZoom(Integer key,Integer[] value){
		this.getZooms().put(key, value);
	}
	
	public void inicializarZooms(){
		this.addZoom(335, new Integer[]{1000084, 1000083});
	}
	

	/**
	 * Cambia las ventanas de zoom de una ventana específica
	 */
	
	public void ejecutar() throws Exception {
		Set<Integer> claves = this.getZooms().keySet();
		Integer[] valores;
		String sql;
		
		for (Integer key : claves) {
			valores = this.getZooms().get(key);
			sql = "UPDATE ad_table SET ad_window_id = "+((valores[0] != null)?valores[0].intValue():"null")+", po_window_id = "+((valores[0] != null)?valores[1].intValue():"null")+" WHERE ad_table_id = "+key.intValue();
			ExecuterSql.executeUpdate(sql,this.getTrxName());
		}
	}

	public void deshacer() throws Exception {

	}

	
}
