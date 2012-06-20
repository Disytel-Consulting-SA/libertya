package org.openXpertya.print.fiscal.document;

public class DebitNote extends Invoice {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707454345218614844L;

	@Override
	public String getDocumentType() {
		return DT_DEBIT_NOTE;
	}
}
