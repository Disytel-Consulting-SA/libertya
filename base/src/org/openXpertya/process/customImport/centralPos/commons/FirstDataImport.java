package org.openXpertya.process.customImport.centralPos.commons;

import org.openXpertya.process.customImport.centralPos.http.Get;

/**
 * Importación de FirstData
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public class FirstDataImport extends CentralPosImport {
	/** URL base de FirstData. */
	protected static final String FIRSTDATA_URL = BASE_URL + "/firstdata";

	/**
	 * Construye un llamado GET para un apartado de FirstData, 
	 * agregando además, la autenticación en el Header del llamado.
	 * @param path Parte de la URL que indica qué apartado de 
	 * FirstData es el que se desea recuperar.
	 * @return Llamado get listo.
	 */
	@Override
	public Get makeGetter(String path, String token) {
		Get get = new Get(FIRSTDATA_URL + path);

		get.addHeader("Authorization", "Bearer " + token);

		return get;
	}

}
