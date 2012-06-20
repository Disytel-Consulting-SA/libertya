package org.openXpertya.fastrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FTFieldsReadOnly extends FTFields {

	//Variables de instancia
	
	/** Colección con todos los campos a setear de sólo lectura */
	
	private Collection<Integer> fields;
	
	//Constructores
	
	public FTFieldsReadOnly() {
		ArrayList<Integer> campos = new ArrayList<Integer>();
		campos.add(1137);
		campos.add(10829);
		campos.add(2958);
		campos.add(1128);
		campos.add(3395);
		campos.add(3383);
		campos.add(3373);		
		campos.add(10827);
		campos.add(4125);
		campos.add(1003380);
		campos.add(1003381);
		campos.add(9614);
		campos.add(9623);
		campos.add(9627);
		campos.add(1003879);
		this.setFields(campos);
	}

	public FTFieldsReadOnly(String trxName) {
		this();
		this.setTrxName(trxName);
	}

	
	//Getters y Setters
	
	public void setFields(Collection<Integer> fields) {
		this.fields = fields;
	}

	public Collection<Integer> getFields() {
		return fields;
	}
	
	//Métodos varios

	public void ejecutar() throws Exception{
		
		Iterator<Integer> iteraFields = this.getFields().iterator();
		
		while(iteraFields.hasNext()){
			this.setReadOnly(this.getTableName(), iteraFields.next().intValue());
		}
	}
	
	public void deshacer() {


	}

}
