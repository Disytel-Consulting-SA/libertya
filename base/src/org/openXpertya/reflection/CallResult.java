package org.openXpertya.reflection;

import java.io.Serializable;
import java.util.Properties;

import org.openXpertya.util.Language;
import org.openXpertya.util.Msg;

/**
 * Esta clase hace las veces de resultado de la llamada. La llamada puede
 * provocar un error o no, si hay error en la variable de instancia msg se
 * debería guardar el mensaje de error.
 * 
 * @author Matías Cap
 * 
 */

public class CallResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Variables de instancia
	
	/** Objeto resultante del metodo rmi */
	
	private Object result;
	
	/** Determina si la llamada tiene error o no */
	
	private boolean isError;
	
	/** Mensaje de error */
	
	private String msg;
	
	/** Booleano que determina si se debe mostrar el error o no */
	private boolean showError;
	
	// Constructores
	
	public CallResult(){
		setError(false);
		setShowError(true);
	}

	/**
	 * Seteo un mensaje en base a la traducción del AD_Message con el lenguaje
	 * parámetro. También se setea como error el resultado en base al
	 * parámetro.
	 * 
	 * @param AD_Message
	 * @param language
	 * @param isError
	 */
	public void setMsg(String AD_Message, Language language, boolean isError){
		setMsg(Msg.getMsg(language, AD_Message), isError);
	}
	
	/**
	 * Seteo un mensaje en base a la traducción del AD_Message con el lenguaje
	 * parámetro. También se setea como error el resultado en base al
	 * parámetro.
	 * 
	 * @param AD_Message
	 * @param AD_Language
	 * @param isError
	 */
	public void setMsg(String AD_Message, String AD_Language, boolean isError){
		setMsg(Msg.getMsg(AD_Language, AD_Message), isError);
	}

	/**
	 * Seteo un mensaje en base a la traducción del AD_Message con el lenguaje
	 * del contexto parámetro. También se setea como error el resultado en base
	 * al parámetro.
	 * 
	 * @param ctx
	 * @param AD_Message
	 * @param isError
	 */
	public void setMsg(Properties ctx, String AD_Message, boolean isError){
		setMsg(Msg.getMsg(ctx, AD_Message), isError);
	}

	/**
	 * Seteo un mensaje en base a la traducción del AD_Message con el lenguaje
	 * del contexto parámetro y sus parámetros. También se setea como error el
	 * resultado en base al parámetro.
	 * 
	 * @param ctx
	 * @param AD_Message
	 * @param args
	 * @param isError
	 */
	public void setMsg(Properties ctx, String AD_Message, Object[] args, boolean isError){
		setMsg(Msg.getMsg(ctx, AD_Message, args), isError);
	}
	
	
	/**
	 * Parseo el mensaje parámetro que puede venir con varios AD_Message
	 * entremezclados, luego de ello lo seteo al mensaje local y coloco si fue
	 * error o no.
	 * 
	 * @param ctx
	 * @param msgToParse
	 * @param isError
	 */
	public void parseMsg(Properties ctx, String msgToParse, boolean isError){
		setMsg(Msg.parseTranslation(ctx, msgToParse), isError);
	}
	
	/**
	 * Seteo el mensaje y si es error o no.
	 * @param msg mensaje
	 * @param isError true si es error, false caso contrario
	 */
	public void setMsg(String msg, boolean isError){
		setMsg(msg);
		setError(isError);
	}
	
	// Getters y Setters

	public void setResult(Object result) {
		this.result = result;
	}

	public Object getResult() {
		return result;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}

	public boolean isError() {
		return isError;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public void setShowError(boolean showError) {
		this.showError = showError;
	}

	public boolean isShowError() {
		return showError;
	}
	
}
