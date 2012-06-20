package org.openXpertya.pos.exceptions;

public class PosException extends Exception {

	private String message = null;
	private String description = null;
	
	public PosException() {
		super();
	}

	public PosException(String message) {
		super(message);
	}

	public PosException(String message, Throwable cause) {
		super(message, cause);
	}

	public PosException(Throwable cause) {
		super(cause);
	}

	public PosException(String message, String description) {
		super(message);
		this.description = description;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		if (message != null)
			return message;
		else
			return super.getMessage();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
