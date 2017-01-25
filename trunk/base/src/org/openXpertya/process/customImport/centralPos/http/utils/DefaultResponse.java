package org.openXpertya.process.customImport.centralPos.http.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Objeto de respuesta por defecto tras un llamado Http.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class DefaultResponse extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public DefaultResponse(Map<String, Object> response) {
		super(response);
	}

}
