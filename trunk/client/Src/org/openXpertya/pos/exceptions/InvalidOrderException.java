package org.openXpertya.pos.exceptions;

public class InvalidOrderException extends PosException {

	private String descriptionMsg;
	
	public InvalidOrderException() {
		super();
	}

	public InvalidOrderException(String descriptionMsg) {
		super();
		this.descriptionMsg = descriptionMsg;
	}

	public InvalidOrderException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidOrderException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @return Returns the descriptionMsg.
	 */
	public String getDescriptionMsg() {
		return descriptionMsg;
	}
}
