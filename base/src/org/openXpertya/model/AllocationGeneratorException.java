package org.openXpertya.model;

/**
 * Excepción del Generador de Asignaciones
 * @author Franco Bonafine - Disytel
 * @date 27/05/2009
 */
public class AllocationGeneratorException extends Exception {

	private static final long serialVersionUID = 8634422540983027405L;

	/**
	 * 
	 */
	public AllocationGeneratorException() {
		super();
	}

	/**
	 * @param message
	 */
	public AllocationGeneratorException(String message) {
		super(message);
	}
}
