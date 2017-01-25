package org.openXpertya.process.customImport.centralPos.exceptions;

/**
 * Excepción durante la importación desde la API.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class SaveFromAPIException extends Exception {
	private static final long serialVersionUID = 1L;

	public SaveFromAPIException(String errMsg) {
		super(errMsg);
	}

}
