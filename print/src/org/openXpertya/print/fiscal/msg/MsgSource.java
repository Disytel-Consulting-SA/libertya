package org.openXpertya.print.fiscal.msg;


/**
 * Contenedor de mensajes. Provee la funcionalidad para retornar mensajes
 * de texto.
 * @author Franco Bonafine
 * @date 05/02/2008
 */
public interface MsgSource {

	/** 
	 * @return Retorna el mensaje de texto identificado por la <code>key</code> 
	 */
	public String get(String key);

}
