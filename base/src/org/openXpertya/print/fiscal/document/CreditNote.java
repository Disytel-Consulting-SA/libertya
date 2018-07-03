package org.openXpertya.print.fiscal.document;

import org.openXpertya.print.fiscal.exception.DocumentException;

public class CreditNote extends Document {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8150087561653272447L;
	/** Número del comprobante original por el cual se generó la nota
	 * de crédito */
	private String originalDocumentNo;

	/** Letra del comprobante original */
	private String originalLetter;
	/** Punto de venta del comprobante original */
	private Integer originalPOS;
	/** Número de comprobante del comprobante original */
	private Integer originalNo;
	
	/**
	 * @return Returns the originalDocumentNo.
	 */
	public String getOriginalDocumentNo() {
		return originalDocumentNo;
	}

	/**
	 * @param originalDocumentNo The originalDocumentNo to set.
	 */
	public void setOriginalDocumentNo(String originalDocumentNo) {
		this.originalDocumentNo = originalDocumentNo;
	}

	@Override
	public String getDocumentType() {
		return DT_CREDIT_NOTE;
	}

	@Override
	public void validate() throws DocumentException {
		super.validate();
		
		// Validar que exista el número de documento original.
		Document.validateText(getOriginalDocumentNo(),"InvalidOriginalDocNumber");
		
		// Validar cantidad de líneas mayor que 0.
		if(getLines().isEmpty()) 
			throw createDocumentException("InvalidDocumentLinesCount", this);
	}

	public String getOriginalLetter() {
		return originalLetter;
	}

	public void setOriginalLetter(String originalLetter) {
		this.originalLetter = originalLetter;
	}

	public Integer getOriginalPOS() {
		return originalPOS;
	}

	public void setOriginalPOS(Integer originalPOS) {
		this.originalPOS = originalPOS;
	}

	public Integer getOriginalNo() {
		return originalNo;
	}

	public void setOriginalNo(Integer originalNo) {
		this.originalNo = originalNo;
	}
}
