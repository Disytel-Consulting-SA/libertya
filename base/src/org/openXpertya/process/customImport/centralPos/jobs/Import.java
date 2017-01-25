package org.openXpertya.process.customImport.centralPos.jobs;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.process.customImport.centralPos.commons.CentralPosImport;
import org.openXpertya.process.customImport.centralPos.exceptions.SaveFromAPIException;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Proceso de importación abstracto.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 * @version 1.0
 */
public abstract class Import {
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

	protected CentralPosImport centralPosImport;

	/**
	 * Constructor. Se encarga de la autenticación y obtiene el
	 * token de seguridad para realizar las consultas posteriores.
	 * @param centralPosImport Tipo de importación.
	 * @param ctx Contexto de ejecución.
	 * @param trxName Nombre de la transacción.
	 */
	public Import(CentralPosImport centralPosImport, Properties ctx, String trxName) {
		this.centralPosImport = centralPosImport;

		token = this.centralPosImport.login();

		this.ctx = ctx;
		this.trxName = trxName;
		this.extraParams = new HashMap<String, String>();
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
