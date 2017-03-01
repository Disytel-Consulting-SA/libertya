package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MExternalService;
import org.openXpertya.model.X_C_ExternalServiceAttributes;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.process.customImport.centralPos.http.Get;
import org.openXpertya.process.customImport.centralPos.http.Post;
import org.openXpertya.process.customImport.centralPos.pojos.login.Login;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Proceso de importación abstracto.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public abstract class Import {
	public static final String EXTERNAL_SERVICE_AMEX =      "Central Pos - Amex";
	public static final String EXTERNAL_SERVICE_FIRSTDATA = "Central Pos - FirstData";
	public static final String EXTERNAL_SERVICE_NARANJA =   "Central Pos - Naranja";
	public static final String EXTERNAL_SERVICE_VISA =      "Central Pos - Visa";

	/** Resultados por página por defecto (en caso de no encontrarlo en las configuraciones). */
	private static final int DEFAULT_RESULTS_PER_PAGE = 100;

	/** Logger. */
	protected CLogger log;
	/** Token de autenticación. */
	protected String token;
	/** Contexto. */
	protected Properties ctx;
	/** Nombre de la transacción. */
	protected String trxName;
	/** Parámetros adicionales de consulta. Opcionales. */
	protected Map<String, String> extraParams;
	/** Configuración de Servicios Externos. */
	protected MExternalService externalService;
	/** Cantidad de elementos por pagina. */
	protected int resultsPerPage;

	/**
	 * Constructor.
	 * @param conf Nombre de la configuraciín de Servicios Externos.
	 * @param ctx Contexto.
	 * @param trxName Nombre de la Transacción.
	 * @throws Exception Si la autenticación falla.
	 */
	public Import(String conf, Properties ctx, String trxName) throws Exception {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	C_ExternalService_ID ");
		sql.append("FROM ");
		sql.append("	" + MExternalService.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	name = ? ");

		int C_ExternalService_ID = DB.getSQLValue(trxName, sql.toString(), conf);

		externalService = new MExternalService(ctx, C_ExternalService_ID, trxName);

		resultsPerPage = DEFAULT_RESULTS_PER_PAGE;
		X_C_ExternalServiceAttributes perPage = externalService.getAttributeByName("Elementos por Página");
		if (perPage != null) {
			resultsPerPage = Integer.valueOf(perPage.getName());
		}

		token = login();

		if (token == null) {
			throw new Exception(CLogger.retrieveErrorString(Msg.getMsg(Env.getAD_Language(ctx), "CentralPosBadLogin")));
		}

		this.ctx = ctx;
		this.trxName = trxName;
		this.extraParams = new HashMap<String, String>();
	}

	/**
	 * Realiza la autenticación y obtiene el token de seguridad para
	 * realizar las consultas posteriores.<br>
	 * IMPORTANTE: Deben estar apropiadamente configurados los campos "Email y Contraseña".
	 */
	public String login() {
		X_C_ExternalServiceAttributes attr = externalService.getAttributeByName("URL Login");
		Post loginPost = new Post(attr.getName());

		loginPost.addParam("email", externalService.getUserName());
		loginPost.addParam("password", externalService.getPassword());

		Login loginResponse = (Login) loginPost.execute(Login.class);

		return loginResponse.getToken();
	}

	/**
	 * Construye un llamado GET, agregando además,
	 * la autenticación en el Header del llamado.
	 * @param url Url.
	 * @return Llamado get listo.
	 */
	public Get makeGetter(String url) {
		Get get = new Get(url);
		get.addHeader("Authorization", "Bearer " + token);
		return get;
	}

	/**
	 * Construye un llamado GET, agregando además,
	 * la autenticación en el Header del llamado.
	 * @return Llamado get listo.
	 */
	public Get makeGetter() {
		Get get = new Get(externalService.getURL());
		get.addHeader("Authorization", "Bearer " + token);
		return get;
	}

	/**
	 * Inicia la importación.
	 * @return Total de elementos importados.
	 * @throws SaveFromAPIException
	 */
	public abstract String excecute() throws SaveFromAPIException;

	/**
	 * Agrega parámetros adicionales a las consultas.
	 * @param name Nombre del parámetro.
	 * @param value Valor del parámetro.
	 */
	public void addParam(String name, Object value) {
		extraParams.put(name, String.valueOf(value));
	}

	/**
	 * Setea la clase encargada de emitir logs.
	 * @param log
	 */
	public void setCLogger(CLogger log) {
		this.log = log;
	}

	/**
	 * Retorna un mensaje de información. Utilizado para informar
	 * el estado de importación hacia las tablas temporales.
	 * @param params parámetros del mensaje.
	 * @return String con el mensaje de información correspondiente.
	 */
	protected String msg(Object[] params) {
		return Msg.getMsg(Env.getAD_Language(ctx), "CentralPosResultMsg", params);
	}

}
