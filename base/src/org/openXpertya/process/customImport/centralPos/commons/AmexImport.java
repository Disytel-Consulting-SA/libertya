package org.openXpertya.process.customImport.centralPos.commons;

import org.openXpertya.process.customImport.centralPos.http.Get;

/**
 * Importación de Amex
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class AmexImport extends CentralPosImport {
	/** URL base de Amex. */
	protected static final String AMEX_URL = BASE_URL + "/amex";

	/**
	 * Construye un llamado GET para un apartado de Amex, 
	 * agregando además, la autenticación en el Header del llamado.
	 * @param path Parte de la URL que indica qué apartado de 
	 * Amex es el que se desea recuperar.
	 * @return Llamado get listo.
	 */
	@Override
	public Get makeGetter(String path, String token) {
		Get get = new Get(AMEX_URL + path);

		get.addHeader("Authorization", "Bearer " + token);

		return get;
	}

}
