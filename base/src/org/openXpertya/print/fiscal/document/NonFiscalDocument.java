package org.openXpertya.print.fiscal.document;

import java.util.ArrayList;
import java.util.List;

import org.openXpertya.print.fiscal.exception.DocumentException;

/**
 * Clase que representa un documento no fiscal a imprimir en una
 * impresora fiscal. Esta clase no es una subclase de {@link Document}
 * ya que simplemente contiene una lista de líneas {@link String}
 * que son enviadas una a una a la impresora fiscal.
 * @author Franco Bonafine
 * @date 13/04/2010
 */
public class NonFiscalDocument {

	/** Lista de líneas que se imprimen en el orden en que aparecen */
	private List<String> lines;

	/** Cantidad de copias que se imprimen */
	private int copies = 0;
	
	/** Información de la compañía y organización */
	private ClientOrgInfo clientOrgInfo = null;
	
	/**
	 * Observaciones o descripciones del documento para el pie de la
	 * impresión
	 */
	private List<String> footerObservations;
	
	/**
	 * Constructor por defecto de la clase.
	 */
	public NonFiscalDocument() {
		super();
		lines = new ArrayList<String>();
		footerObservations = new ArrayList<String>();
	}

	/**
	 * @return the lines
	 */
	public List<String> getLines() {
		return lines;
	}

	/**
	 * Agrega una línea al final de la lista de líneas del documento
	 * @param line Línea a agregar
	 */
	public void addLine(String line) {
		getLines().add(line);
	}

	/**
	 * @return Devuelve la cantidad de líneas que contiene el documento
	 */
	public int linesCount() {
		return getLines().size();
	}
	
	/**
	 * Validación del documento.
	 * @throws DocumentException cuando el documento no puede enviarse 
	 * a imprimir dado que esta acción produciría un estado de error en la
	 * impresora fiscal.
	 */
	public void validate() throws DocumentException {
		// Validar cantidad de líneas mayor que 0.
		if(getLines().isEmpty()) 
			throw Document.createDocumentException("InvalidDocumentLinesCount");
	}

	/**
	 * @return the copies
	 */
	public int getCopies() {
		return copies;
	}

	/**
	 * @param copies the copies to set
	 */
	public void setCopies(int copies) {
		this.copies = copies;
	}

	public ClientOrgInfo getClientOrgInfo() {
		return clientOrgInfo;
	}

	public void setClientOrgInfo(ClientOrgInfo clientOrgInfo) {
		this.clientOrgInfo = clientOrgInfo;
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
}
