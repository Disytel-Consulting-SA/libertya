package org.openXpertya.fastrack;

import java.util.HashMap;
import java.util.Iterator;

public class FTFieldsDefaultValue extends FTFields {

	//Variables de instancia
		
	/** Colección de fields con valor por defecto */
	
	private HashMap fields; 
	
	
	//Constructores
	
	public FTFieldsDefaultValue(){
		HashMap campos = new HashMap();
		campos.put(3781, "*");
		this.setFields(campos);
	}
	
	public FTFieldsDefaultValue(String trxName){
		this();
		this.setTrxName(trxName);
	}
	

	//Getters y setters

	public void setFields(HashMap fields) {
		this.fields = fields;
	}

	public HashMap getFields() {
		return fields;
	}
	
	
	//Métodos varios
	
	/**
	 * 
	 */
	public void setDefaultValue(int idField, String value) throws Exception{
		/*
		 * Realizo el script sql
		 * -----------------------------------------------------------------------------
		 * UPDATE ad_column 
		 * SET defaultvalue = "+value+" 
		 * WHERE ad_column_id = (SELECT ad_column_id 
		 * 							FROM ad_field 
		 * 							WHERE ad_field_id = "+idField+")
		 * -----------------------------------------------------------------------------
		*/
		String sql = "UPDATE ad_column SET defaultvalue = '"+value+"' WHERE ad_column_id = (SELECT ad_column_id FROM ad_field WHERE ad_field_id = "+idField+")";
		
		//Ejecuto el sql
		ExecuterSql.executeUpdate(sql, this.getTrxName());
	}
	
	/**
	 * 
	 */
	public void ejecutar() throws Exception{
		//Obtengo el iterador de los campos
		Iterator<Integer> iteraKeys = this.getFields().keySet().iterator();
		
		int key;
		String valor = new String();
		//Mientras haya campos
		while(iteraKeys.hasNext()){
			//Obtengo la clave
			key = iteraKeys.next().intValue();
			
			//Obtengo el valor de esa clave
			valor = (String)this.getFields().get(key);
			
			//Modificar el valor por defecto
			this.setDefaultValue(key, valor);
		}
	}
	
	public void deshacer() {
		
	}

}
