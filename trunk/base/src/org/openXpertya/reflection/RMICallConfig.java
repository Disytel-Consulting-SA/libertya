package org.openXpertya.reflection;

public class RMICallConfig extends CallConfig {

	/** Llamador del método rmi */
	private Object caller;
	/** Nombre del método rmi a invocar */
	private String method;
	/** Tipos de datos de los parámetros al método rmi a invocar */
	private Class<?>[] parametersTypes;
	/** Valores de los parámetros al método rmi a invocar */
	private Object[] parametersValues;
	
	public RMICallConfig() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Call createCall() {
		return new RMICall();
	}
	
	// Getters y Setters

	public void setCaller(Object caller) {
		this.caller = caller;
	}

	public Object getCaller() {
		return caller;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setParametersTypes(Class<?>[] parametersTypes) {
		this.parametersTypes = parametersTypes;
	}

	public Class<?>[] getParametersTypes() {
		return parametersTypes;
	}

	public void setParametersValues(Object[] parametersValues) {
		this.parametersValues = parametersValues;
	}

	public Object[] getParametersValues() {
		return parametersValues;
	}
}
