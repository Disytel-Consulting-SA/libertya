package org.openXpertya.process.customImport.centralPos.commons;

import org.openXpertya.process.customImport.centralPos.http.Get;

/**
 * Importación de Naranja
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class NaranjaImport extends CentralPosImport {
	/** URL base de Naranja. */
	protected static final String NARANJA_URL = BASE_URL + "/naranja";

	/**
	 * Construye un llamado GET para un apartado de Naranja, 
	 * agregando además, la autenticación en el Header del llamado.
	 * @param path Parte de la URL que indica qué apartado de 
	 * Naranja es el que se desea recuperar.
	 * @return Llamado get listo.
	 */
	@Override
	public Get makeGetter(String path, String token) {
		Get get = new Get(NARANJA_URL + path);

		get.addHeader("Authorization", "Bearer " + token);

		return get;
	}

}
