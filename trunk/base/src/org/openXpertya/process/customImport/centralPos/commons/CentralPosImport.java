package org.openXpertya.process.customImport.centralPos.commons;

import org.openXpertya.model.MPreference;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.Post;
import org.openXpertya.process.customImport.centralPos.http.utils.DefaultResponse;

/**
 * Importación desde CentralPos.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public abstract class CentralPosImport {
	public static final String FIRSTDATA = "FD";
	public static final String NARANJA = "NA";
	public static final String AMEX = "AM";
	public static final String VISA = "VI";

	/** URL Bsae de CentralPos. */
	protected static final String BASE_URL = getPref("CentralPosBaseURL");
	/** Resultados por página. */
	public static final int RESULTS_PER_PAGE = getResultsPerPage();
	/** Resultados por página por defecto (en caso de no encontrarlo en AD_Preference). */
	private static final int DEFAULT_RESULTS_PER_PAGE = 100;

	/**
	 * @return Cantidad de resultados a recuperar por página,
	 * configurado en AD_Preference. En caso de que no lo
	 * encuentre, devuelve un valor por defecto.
	 */
	private static int getResultsPerPage() {
		int result = Integer.valueOf(getPref("CentralPosPageSize"));
		return result > 0 ? result : DEFAULT_RESULTS_PER_PAGE;
	}

	/**
	 * Realiza la autenticación y obtiene el token de seguridad para
	 * realizar las consultas posteriores.<br>
	 * IMPORTANTE: Debe existir en ad_preference los valores
	 * para "CentralPosEmail" y "CentralPosPassword".
	 */
	public String login() {
		Post loginPost = new Post(BASE_URL + "/auth/login");

		loginPost.addParam("email", getPref("CentralPosEmail"));
		loginPost.addParam("password", getPref("CentralPosPassword"));

		DefaultResponse response = new DefaultResponse(loginPost.execute());

		return (String) response.get("token");
	}

	/**
	 * Obtiene un valor de la tabla AD_Preference.
	 * @param name nombre del atributo a recuperar.
	 * @return valor del atributo solicitado.
	 */
	protected static String getPref(String name) {
		return MPreference.GetCustomPreferenceValue(name);
	}

	public abstract Get makeGetter(String path, String token);
	
}
