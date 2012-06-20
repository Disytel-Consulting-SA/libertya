package org.openXpertya.print.fiscal.msg;


public class MsgRepository {

	private static MsgSource msgSource = DefaultMsgSource.getInstance();

	private MsgRepository() {
		
	}

	/** 
	 * @return Retorna el mensaje identificado por la <code>key</code> 
	 */
	public static String get(String key) {
		return getMsgSource().get(key);
	}
	
	/**
	 * Crea un <code>FiscalMessage</code> obteniendo el título y la descripción
	 * del mensaje a partir de dos claves.
	 * @param code Código del mensaje fiscal.
	 * @param titleKey Clave del título del mensaje.
	 * @param descKey Clave de la descripción del mensaje.
	 * @param error El mensaje es un error o no.
	 * @return Retorna el <code>FiscalMessage</code>
	 */
	public static FiscalMessage getFiscalMsg(int code, String titleKey, String descKey, boolean error) {
		String title = getMsgSource().get(titleKey);
		String description = getMsgSource().get(descKey);
		return new FiscalMessage(code, title, description, error);
	}
	
	/**
	 * @return Returns the msgSource.
	 */
	public static MsgSource getMsgSource() {
		return msgSource;
	}

	/**
	 * @param msgSource The msgSource to set.
	 */
	public static void setMsgSource(MsgSource msgSource) {
		MsgRepository.msgSource = msgSource;
	}
}
