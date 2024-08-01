package org.openXpertya.clover.msg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collección de <code>CloverMessage</code>.
 * @author dREHER
 * @date 16/08/2023
 */
public class CloverMessages {

	private List<CloverMessage> msgs;

	public static CloverMessages create(CloverMessage msg) {
		CloverMessages msgs = new CloverMessages();
		msgs.add(msg);
		return msgs;
	}
	
	/**
	 * @param msgs
	 */
	public CloverMessages() {
		super();
		this.msgs = new ArrayList<CloverMessage>();
	}

	/**
	 * @return Returns the cloverMsgs.
	 */
	public List<CloverMessage> getMsgs() {
		return msgs;
	}

	/**
	 * @see java.util.List#add(CloverMessage msg)
	 */
	public boolean add(CloverMessage cloverMsg) {
		return msgs.add(cloverMsg);
	}

	/**
	 * @see java.util.List#remove(Object obj)
	 */
	public boolean remove(CloverMessage cloverMsg) {
		return msgs.remove(cloverMsg);
	}

	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		msgs.clear();
	}

	/** 
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		return msgs.isEmpty();
	}

	/** 
	 * @see java.util.List#size()
	 */
	public int size() {
		return msgs.size();
	}
	
	/**
	 * @return Retorna verdadero si al menos uno de los mensajes contenidos
	 * en la lista de mensajes trx clover es un error.
	 */
	public boolean hasErrors() {
		boolean hasError = false;
		for (Iterator msgs = getMsgs().iterator(); msgs.hasNext() && !hasError;) {
			CloverMessage msg = (CloverMessage) msgs.next();
			hasError = msg.isError();
		}
		return hasError;
	}
	
	/**
	 * @return Retorna una lista de <code>CloverMessage</code> con los mensajes
	 * que son errores (<code>isError()</code> es verdadero).
	 */
	public List<CloverMessage> getErrorMsgs() {
		List<CloverMessage> errorMsgs = new ArrayList<CloverMessage>();
		for (CloverMessage msg : getMsgs()) {
			if(msg.isError())
				errorMsgs.add(msg);
		}
		return errorMsgs;
	}

	/**
	 * Devuelve los mensajes de error en un String con el siguiente formato.
	 * 
	 * - Título Error1. Descripción Error1
	 * - Título Error2. Descripción Error2
	 * ...
	 * - Título ErrorN. Descripción ErrorN
	 * @return String
	 */
	public String getErrorsAsString() {
		return getErrorsAsString("\n");
	}
	
	/**
	 * Devuelve los mensajes de error en un String con el siguiente formato.
	 * 
	 * - Título Error1. Descripción Error1
	 * - Título Error2. Descripción Error2
	 * ...
	 * - Título ErrorN. Descripción ErrorN
	 * @param newLine String concatenado al final de cada línea
	 * @return String
	 */
	public String getErrorsAsString(String newLine) {
		StringBuffer res = new StringBuffer();
		for (CloverMessage message : getErrorMsgs()) {
			res.append("- ").append(message.getTitle()).
				append(". ").append(message.getDescription()).append(newLine);
		}
		return res.toString();
	}
}
