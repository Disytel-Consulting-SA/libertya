package org.openXpertya.print.fiscal.msg;

public class FiscalMessage {

	private int code;
	private String title;
	private String description;
	private boolean error;
	
	/**
	 * @param code
	 * @param name
	 * @param description
	 */
	public FiscalMessage(int code, String title, String description, boolean error) {
		super();
		this.code = code;
		this.title = title;
		this.description = description;
		this.error = error;
	}

	/**
	 * @return Returns the code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return Returns the name.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Returns the error.
	 */
	public boolean isError() {
		return error;
	}

	@Override
	public String toString() {
		return getTitle() + ". " + getDescription();
	}
}
