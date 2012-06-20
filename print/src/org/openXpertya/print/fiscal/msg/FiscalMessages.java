package org.openXpertya.print.fiscal.msg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Collección de <code>FiscalMessage</code>.
 * @author Franco Bonafine
 * @date 06/02/2008
 */
public class FiscalMessages {

	private List<FiscalMessage> msgs;

	public static FiscalMessages create(FiscalMessage msg) {
		FiscalMessages msgs = new FiscalMessages();
		msgs.add(msg);
		return msgs;
	}
	
	/**
	 * @param msgs
	 */
	public FiscalMessages() {
		super();
		this.msgs = new ArrayList<FiscalMessage>();
	}

	/**
	 * @return Returns the fiscalMsgs.
	 */
	public List<FiscalMessage> getMsgs() {
		return msgs;
	}

	/**
	 * @see java.util.List#add(FiscalMessage msg)
	 */
	public boolean add(FiscalMessage fiscalMsg) {
		return msgs.add(fiscalMsg);
	}

	/**
	 * @see java.util.List#remove(Object obj)
	 */
	public boolean remove(FiscalMessage fiscalMsg) {
		return msgs.remove(fiscalMsg);
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
	 * en la lista de mensajes fiscales es un error.
	 */
	public boolean hasErrors() {
		boolean hasError = false;
		for (Iterator msgs = getMsgs().iterator(); msgs.hasNext() && !hasError;) {
			FiscalMessage msg = (FiscalMessage) msgs.next();
			hasError = msg.isError();
		}
		return hasError;
	}
	
	/**
	 * @return Retorna una lista de <code>FiscalMessage</code> con los mensajes
	 * que son errores (<code>isError()</code> es verdadero).
	 */
	public List<FiscalMessage> getErrorMsgs() {
		List<FiscalMessage> errorMsgs = new ArrayList<FiscalMessage>();
		for (FiscalMessage msg : getMsgs()) {
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
		for (FiscalMessage message : getErrorMsgs()) {
			res.append("- ").append(message.getTitle()).
				append(". ").append(message.getDescription()).append(newLine);
		}
		return res.toString();
	}
}
