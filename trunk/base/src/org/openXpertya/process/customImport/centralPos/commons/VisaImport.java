package org.openXpertya.process.customImport.centralPos.commons;

import org.openXpertya.process.customImport.centralPos.http.Get;

/**
 * Importación de Visa
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class VisaImport extends CentralPosImport {
	/** URL base de Visa. */
	protected static final String VISA_URL = BASE_URL + "/visa";

	/**
	 * Construye un llamado GET para un apartado de Visa, 
	 * agregando además, la autenticación en el Header del llamado.
	 * @param path Parte de la URL que indica qué apartado de 
	 * Visa es el que se desea recuperar.
	 * @return Llamado get listo.
	 */
	@Override
	public Get makeGetter(String path, String token) {
		Get get = new Get(VISA_URL + path);

		get.addHeader("Authorization", "Bearer " + token);

		return get;
	}

}
