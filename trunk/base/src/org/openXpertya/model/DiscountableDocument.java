package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.DiscountCalculator.IDocumentLine;

/**
 * <p>
 * Esta clase implementa algunos métodos de la interfaz {@link IDocument}
 * necesarios para proporcionar la lógica básica de un documento pasible de
 * aplicación de descuentos mediante un {@link DiscountCalculator}. Salvo algún
 * caso extraordinario, todos los documentos a los que se le apliquen descuentos
 * especializaran esta clase en vez de implementar directamente
 * {@link IDocument}.
 * </p>
 * <p>
 * Proporciona soporte para mantener las instancias de los wrappers 
 * ({@link IDocumentLine}) de las líneas originales del documento que wrappea
 * esta clase. Los wrappers de las líneas deben ser mantenidos a lo largo de la
 * vida del documento dado que el wrapper contiene atributos de estado
 * necesarios para el correcto calculo de descuentos por Combos, Promociones o
 * Esquemas.
 * </p>
 */
public abstract class DiscountableDocument implements IDocument {

	/**
	 * Líneas wrappeadas del documento. La clave de esta Map es la línea
	 * original, el valor es la línea wrappeada
	 */
	private Map<Object, IDocumentLine> wrappedLines;

	/**
	 * Constructor único de la clase.
	 */
	public DiscountableDocument() {
		super();
		this.wrappedLines = new HashMap<Object, IDocumentLine>();
	}

	@Override
	public BigDecimal getLinesTotalAmt() {
		BigDecimal totalAmt = BigDecimal.ZERO;
		for (IDocumentLine line : getDocumentLines()) {
			totalAmt = totalAmt.add(line.getTotalAmt());
		}
		return totalAmt;
	}

	
	@Override
	public BigDecimal getLinesTotalAmt(boolean includeOtherTaxesAmt) {
		BigDecimal totalAmt = BigDecimal.ZERO;
		for (IDocumentLine line : getDocumentLines()) {
			totalAmt = totalAmt.add(line.getTotalAmt());
		}
		return totalAmt;
	}
	
	@Override
	public List<IDocumentLine> getDocumentLines() {
		// Lista de líneas a devolver
		List<IDocumentLine> documentLines = new ArrayList<IDocumentLine>();
		// Map auxiliar que contiene los wrappers de las líneas originales en
		// este momento contenidas por el documento.
		Map<Object, IDocumentLine> newWrappedLines = new HashMap<Object, IDocumentLine>();
		// Línea auxiliar
		IDocumentLine wrappedLine = null;
		
		// Obtiene las líneas originales actuales del documento.
		for (Object originalLine : getOriginalLines()) {
			// Verifica si ya existe una referencia al wrapper de la línea
			// original (implica que la línea ya fue wrappeada anteriormente).
			// Si es así, se utiliza el wrapper existente.
			if (getWrappedLines().containsKey(originalLine)) {
				wrappedLine = getWrappedLines().get(originalLine);
			// Si no, se crea un nuevo wrapper para la línea.
			} else {
				wrappedLine = createDocumentLine(originalLine);
			}
			// Agrega el wrapper de la línea a la lista a devolve y a la Map que
			// almacena los wrappers de las líneas actuales del documento (con
			// esto filtramos las líneas que hayan sido eliminadas del
			// documento).
			documentLines.add(wrappedLine);
			newWrappedLines.put(originalLine, wrappedLine);
		}
		
		// Se elimina el contenido de la Map antigua y se lo reemplaza con los
		// wrappers de las líneas actuales. Aquí es en donde se mantiene el
		// sincronismo entre las líneas originales del documento y los wrappers
		// almacenados en esta clase.
		getWrappedLines().clear();
		getWrappedLines().putAll(newWrappedLines);
		
		return documentLines;
	}

	/**
	 * @return La lista de líneas originales del documento wrappeado. Las
	 *         subclases deben implementar este método de forma que devuelve la
	 *         lista actual de líneas originales (no IDocumentLine) del
	 *         documento en cuestión. Si el documento no contiene líneas
	 *         devuelve una lista vacía.
	 */
	protected abstract List<? extends Object> getOriginalLines();

	/**
	 * Crea el wrapper concreto para una línea original del documento. El tipo
	 * de {@code originalLine} será el mismo contenido en la lista devuelta por
	 * el método {@link #getOriginalLines()}.
	 * 
	 * @param originalLine
	 *            Línea original del documento que aún no tiene un wrapper
	 *            concreto creado.
	 * @return Wrapper concreto instanciado a partir de la línea original.
	 */
	protected abstract IDocumentLine createDocumentLine(Object originalLine);
	
	/**
	 * @return La Map con las líneas originales y sus respectivos wrappers
	 *         descontables.
	 */
	private Map<Object, IDocumentLine> getWrappedLines() {
		return wrappedLines;
	}
	
	/**
	 * @return lista de percepciones a aplicar al documento
	 */
	public List<MTax> getApplyPercepcion(GeneratorPercepciones generator) throws Exception{
		return generator.getDebitApplyPercepciones();
	}
	
	/**
	 * @return lista de percepciones a aplicar al documento
	 */
	public List<DocumentTax> getAppliedPercepciones(){
		return null;
	}
}
