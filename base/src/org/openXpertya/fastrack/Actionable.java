package org.openXpertya.fastrack;

public interface Actionable {

	//Métodos
	
	public abstract void ejecutar() throws Exception;
	
	public abstract void deshacer() throws Exception;
	
}
