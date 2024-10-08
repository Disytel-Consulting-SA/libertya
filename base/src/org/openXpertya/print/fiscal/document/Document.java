package org.openXpertya.print.fiscal.document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openXpertya.print.fiscal.exception.DocumentException;

/**
 * Clase que representa un documento fiscal/no fiscal a imprimir en una
 * impresora fiscal. Cualquier documento que se deba imprimir en una
 * <code>FiscalPrinter</code> debe estar conformado por una clase que
 * especialice esta clase abstracta.
 * @author Franco Bonafine
 * @date 11/02/2008
 */
public abstract class Document implements Serializable{

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;
	// Tipos de documento.
	/** Tipo de documento: Factura */
	public static final String DT_INVOICE = "I";
	/** Tipo de documento: Nota de Crédito */
	public static final String DT_CREDIT_NOTE = "CN";
	/** Tipo de documento: Nota de Débito */
	public static final String DT_DEBIT_NOTE = "DN";
	
	// Letras de documento.
	/** Letra de Documento: A */
	public static final String DOC_LETTER_A = "A";
	/** Letra de Documento: B */
	public static final String DOC_LETTER_B = "B";
	/** Letra de Documento: C */
	public static final String DOC_LETTER_C = "C";
		
	/** Ciente asociado al documento */
	private Customer customer;
	/** Número de documento/comprobante */
	private String documentNo;
	/** Líneas del documento */
	private List<DocumentLine> lines;
	/** Descuento general */
	private DiscountLine generalDiscount;
	/**
	 * Observaciones o descripciones del documento para la cabecera de la
	 * impresión
	 */
	private List<String> headerObservations;
	/**
	 * Observaciones o descripciones del documento para el pie de la
	 * impresión
	 */
	private List<String> footerObservations;
	/** Letra del documento. */ 
	private String letter;
	/** Descuentos a nivel documento */
	private List<DiscountLine> documentDiscounts = null;
	/** Impuestos adicionales */
	private List<Tax> otherTaxes = null;
	/** Impuestos */
	private List<Tax> taxes = null;
	
	/**
	 * El documento tiene el impuesto incluído? Esto se determina en base a la
	 * tarifa asociada
	 */
	private boolean isTaxIncluded = false;
	/** Información de la compañía y organización */
	private ClientOrgInfo clientOrgInfo = null;
	/** CAE */
	private String cae;
	/** Fecha de Vto de CAE */
	private Timestamp caeDueDate;
	/** Importe del cargo que permite determinar el total real del comprobante */
	private BigDecimal chargeAmt = null;
	/** Código QR del comprobante */
	private String QRCode = null;
	
	public Document() {
		super();
		customer = new Customer();
		lines = new ArrayList<DocumentLine>();
		headerObservations = new ArrayList<String>();
		footerObservations = new ArrayList<String>();
		documentDiscounts = new ArrayList<DiscountLine>();
		otherTaxes = new ArrayList<Tax>();
		setClientOrgInfo(new ClientOrgInfo());
		setTaxes(new ArrayList<Tax>());
	}
	
	/**
	 * @return Returns the customer.
	 */
	public Customer getCustomer() {
		return customer;
	}
	
	/**
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	/**
	 * @return Returns the documentNo.
	 */
	public String getDocumentNo() {
		return documentNo;
	}
	
	/**
	 * @param documentNo The documentNo to set.
	 */
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	
	/**
	 * Agrega una línea al documento. 
	 * @param line Línea a agregar.
	 */	
	public void addLine(DocumentLine line) {
		getLines().add(line);
	}
	
	/**
	 * Elimina una línea del documento.
	 * @param line Línea a eliminar.
	 */
	public void removeLine(DocumentLine line) {
		getLines().remove(line);
	}

	/**
	 * @return Returns the lines.
	 */
	public List<DocumentLine> getLines() {
		return lines;
	}

	/**
	 * @return Returns the generalDiscount.
	 */
	public DiscountLine getGeneralDiscount() {
		return generalDiscount;
	}

	/**
	 * @param generalDiscount The generalDiscount to set.
	 */
	public void setGeneralDiscount(DiscountLine generalDiscount) {
		this.generalDiscount = generalDiscount;
	}

	/**
	 * @return Returns the header observations.
	 */
	public List<String> getHeaderObservations() {
		return headerObservations;
	}
	
	/**
	 * Agrega una observación de la cabecera del documento.
	 * @param observation Texto de la observación.
	 */
	public void addHeaderObservation(String observation) {
		headerObservations.add(observation);
	}

	/**
	 * Elimina una observación de la cabecera del documento
	 * @param observation Observación de la cabecera a eliminar.
	 */
	public void removeHeaderObservation(Object observation) {
		headerObservations.remove(observation);
	}


	/**
	 * @return Indica si el documento tiene o no asignado un descuento general.
	 */
	public boolean hasGeneralDiscount() {
		return getGeneralDiscount() != null;
	}

	/**
	 * @return Returns the letter.
	 */
	public String getLetter() {
		if(letter == null)
			letter = "";
		return letter;
	}

	/**
	 * @param letter The letter to set.
	 */
	public void setLetter(String letter) {
		this.letter = letter;
	}
	
	/**
	 * @return Retorna veraddero en caso de que el documento tenga una letra
	 * asignada.
	 */
	public boolean hasLetter() {
		return getLetter() != null;
	}
	
	/**
	 * @return Retorna el tipo de documento.
	 */
	public abstract String getDocumentType();
	
	/**
	 * Validación del documento.
	 * @throws DocumentException cuando el documento no puede enviarse 
	 * a imprimir dado que esta acción produciría un estado de error en la
	 * impresora fiscal.
	 */
	public void validate() throws DocumentException {
		try {
			// Se validan los datos del cliente.
			getCustomer().validate();
		} catch (DocumentException e) {
			// Se relanza la excepción agregando este documento como
			// parámetro.
			e.setDocument(this);
			throw e;
		}
		
		// Validar total del documento distinto de 0.
		validateNumber(getTotal(), "!=", BigDecimal.ZERO, "InvalidDocumentTotalAmount");
		
		// Validar las líneas del documento.
		for (DocumentLine docLine : getLines()) {
			docLine.validate();
		}
		
		// Validar el descuento general.
		if(hasGeneralDiscount())
			getGeneralDiscount().validate();
		
		// Validar descuentos / recargos del documento
		for (DiscountLine discount : getDocumentDiscounts()) {
			discount.validate();
		}
	}
	
	/**
	 * @return Retorna el monto total del documento.
	 */
	public BigDecimal getTotal() {
		BigDecimal sum = BigDecimal.ZERO;
		// Se suma el total de cada línea.
		for (DocumentLine docLine : getLines()) {
			sum = sum.add(docLine.getLineTotal());
		}
		// Se tienen en cuenta los descuentos
		for (DiscountLine dd : getDocumentDiscounts()) {
			sum = sum.add(dd.getAmount());
		}
		
		// Se tiene en cuenta descuento general
		if(hasGeneralDiscount()) {
			sum = sum.add(getGeneralDiscount().getAmount());
		}
		
		// Sumo otros impuestos
		for (Tax otherTax : getOtherTaxes()) {
			sum = sum.add(otherTax.getAmt());
		}
		
		return sum;
	}
	
	/**
	 * @return Retorna el monto neto del documento.
	 */
	public BigDecimal getNetAmount() {
		BigDecimal sum = BigDecimal.ZERO;
		// Se suma el total de cada línea.
		for (DocumentLine docLine : getLines()) {
			sum = sum.add(docLine.getSubtotalNet());
		}
		return sum;
	}
	
	
	/**
	 * Validación de un texto. Valida que no sea null y que contenga al menos
	 * un caracter visible. 
	 * @param text Texto a validar.
	 * @param errorMsg Mensaje de error a lanzar en caso de que no sea válido.
	 * @throws DocumentException cuando el texto no es válido.
	 */
	public static void validateText(String text, String errorMsg) throws DocumentException {
		if(text == null || text.trim().length() == 0)
			throw createDocumentException(errorMsg);
	}
	
	/**
	 * Validación de un número. 
	 * @param number Número a validar.
	 * @param operand Operación a realizar con <code>otherNumber</code> (<code><=, <, >, >=, ==, !=</code>).
	 * @param otherNumber Número a comparar.
	 * @param errorMsg Mensaje de error a lanzar en caso de que no sea válido.
	 * @throws DocumentException cuando el número no cumple la condición.
	 */
	public static void validateNumber(BigDecimal number, String operand, BigDecimal otherNumber, String errorMsg) throws DocumentException {
		boolean operResult = false;
		if(operand.equals("<="))
			operResult = number.compareTo(otherNumber) <= 0;
		else if(operand.equals("<"))
			operResult = number.compareTo(otherNumber) < 0;
		else if(operand.equals(">"))
			operResult = number.compareTo(otherNumber) > 0;
		else if(operand.equals(">="))	
			operResult = number.compareTo(otherNumber) >= 0;
		else if(operand.equals("=="))
			operResult = number.compareTo(otherNumber) == 0;
		else if(operand.equals("!="))
			operResult = number.compareTo(otherNumber) != 0;
		else
			operResult = false;
		
		if(number == null || !operResult) 
			throw createDocumentException(errorMsg);
	}

	/**
	 * Método factory de excepciones de documento. 
	 * @param msg Mensaje de la excepción.
	 * @return La excepción.
	 */
	protected static DocumentException createDocumentException(String msg) {
		return createDocumentException(msg, null);
	}

	/**
	 * Método factory de excepciones de documento. 
	 * @param msg Mensaje de la excepción.
	 * @param document Documento origen de la excepción.
	 * @return La excepción.
	 */
	protected static DocumentException createDocumentException(String msg, Document document) {
		return new DocumentException(msg, document);
	}

	/**
	 * @return el valor de documentDiscounts
	 */
	public List<DiscountLine> getDocumentDiscounts() {
		return documentDiscounts;
	}
	
	/**
	 * @return Indica si ese documento tiene descuentos de encabezado asociados.
	 */
	public boolean hasDocumentDiscounts() {
		return !getDocumentDiscounts().isEmpty();
	}
	
	/**
	 * Agrega un descuento a nivel documento a este documento
	 * @param discount Línea de descuento a agregar.
	 */
	public void addDocumentDiscount(DiscountLine discount) {
		getDocumentDiscounts().add(discount);
	}

	public List<String> getFooterObservations() {
		return footerObservations;
	}
	
	public void setFooterObservations(List<String> observations){
		this.footerObservations = observations;
	}
	
	/**
	 * Agrega una observación del pie del documento.
	 * @param observation Texto de la observación.
	 */
	public void addFooterObservation(String observation) {
		footerObservations.add(observation);
	}

	/**
	 * Elimina una observación del pie del documento
	 * @param observation Observación del pie a eliminar.
	 */
	public void removeFooterObservation(Object observation) {
		footerObservations.remove(observation);
	}

	public void setOtherTaxes(List<Tax> otherTaxes) {
		this.otherTaxes = otherTaxes;
	}

	public List<Tax> getOtherTaxes() {
		return otherTaxes;
	}

	public boolean isTaxIncluded() {
		return isTaxIncluded;
	}

	public void setTaxIncluded(boolean isTaxIncluded) {
		this.isTaxIncluded = isTaxIncluded;
	}

	public ClientOrgInfo getClientOrgInfo() {
		return clientOrgInfo;
	}

	public void setClientOrgInfo(ClientOrgInfo clientOrgInfo) {
		this.clientOrgInfo = clientOrgInfo;
	}

	public String getCae() {
		return cae;
	}

	public void setCae(String cae) {
		this.cae = cae;
	}

	public Timestamp getCaeDueDate() {
		return caeDueDate;
	}

	public void setCaeDueDate(Timestamp caeDueDate) {
		this.caeDueDate = caeDueDate;
	}

	public List<Tax> getTaxes() {
		return taxes;
	}

	public void setTaxes(List<Tax> taxes) {
		this.taxes = taxes;
	}

	public BigDecimal getChargeAmt() {
		return chargeAmt;
	}

	public void setChargeAmt(BigDecimal chargeAmt) {
		this.chargeAmt = chargeAmt;
	}
	
	public String getQRCode() {
		return QRCode;
	}

	public void setQRCode(String QRCode) {
		this.QRCode = QRCode;
	}
}
