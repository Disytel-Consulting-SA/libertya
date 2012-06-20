package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Matching de Artículos. Permite realizar comparaciones entre conjunto de
 * artículos y cantidades a fin de determinar si hay un match correspondiente
 * entre dos intancias de esta clase. Utilizado comúnmente para el cálculo de
 * aplicabilidad de Combos, Promociones y Descuentos.
 * 
 * @author Franco Bonafine - Disytel
 */
public class ProductMatching {

	/** Tipo de comparación entre matchings */
	public enum MatchingCompareType {
		CONTAINS,
		EQUALS
	}
	
	/** Map de artículos y cantidades. La clave es el ID de 
	 *  artículo, el valor es su cantidad*/
	private Map<Integer, BigDecimal> products;

	/**
	 * Constructor de Matching de Artículos.
	 */
	public ProductMatching() {
		super();
		this.products = new HashMap<Integer, BigDecimal>();
	}

	/**
	 * Agrega un artículo y su cantidad a este Matching. Si el artículo ya está
	 * contenido en el matching entonces suma {@code quantity} a la
	 * cantidad actual del artículo.
	 * 
	 * @param productID
	 *            ID del artículo a agregar
	 * @param quantity
	 *            Cantidad del artículo
	 * @return <code>true</code> si el artículo fue agregado correctamente,
	 *         <code>false</code> caso contrario.
	 */
	public boolean addProduct(Integer productID, BigDecimal quantity) {
		boolean added = true;
		if (productID == null || productID == 0 || quantity == null) {
			added = false;
		} else {
			// Suma la cantidad a la cantidad existente.
			if (getProducts().containsKey(productID)) {
				quantity = quantity.add(getProducts().get(productID));
			}
			getProducts().put(productID, quantity);
		}
		return added;
	}

	/**
	 * Realiza la comparación entre este matching y
	 * <code>anotherProductMatching</code>
	 * 
	 * @param anotherProductMatching
	 *            Matching contra el cual se compara.
	 * @param type
	 *            Tipo de comparación:
	 *            <ul>
	 *            <li>{@link MatchingCompareType#EQUALS}: comparación por
	 *            igualdad exacta. Este matching debe contener los mismos
	 *            artículos y cantidades que el matching parámetro.</li>
	 *            <li> {@link MatchingCompareType#CONTAINS}: comparación por
	 *            contenido. Este matching debe contener al menos las cantidades
	 *            de los artículos del matching parámetro.</li>
	 *            </ul>
	 * @return <code>true</code> si la comparación es satisfactoria,
	 *         <code>false</code> caso contrario.
	 */
	public boolean match(ProductMatching anotherProductMatching, MatchingCompareType type) {
		if (anotherProductMatching == null) {
			return false;
		}
		
		boolean matchOk = true;
		BigDecimal requiredQty = null;
		BigDecimal myQty       = null;
		int qtyCompare;
		
		// Recorre los artículos del matching parámetros
		for (Integer productID : anotherProductMatching.getProducts().keySet()) {
			// Obtiene la cantidad requerida. Es la cantidad del artículo en el
			// matching parámetro.
			requiredQty = anotherProductMatching.getProductQty(productID);
			// Obtiene la cantidad del mismo artículo en esta instancia.
			myQty = getProductQty(productID);
			// Realiza la comparación.
			qtyCompare = myQty.compareTo(requiredQty);
			// Si la comparación es por igualdad las cantidades deben ser
			// exactamente iguales. Si es por inclusión la cantidad de este
			// matching debe ser mayor o igual a la del matching parámetro.
			matchOk = 
				(type == MatchingCompareType.EQUALS && qtyCompare == 0)
				|| (type == MatchingCompareType.CONTAINS && qtyCompare >= 0);
			
			if (!matchOk) {
				break;
			}
		}
		
		// Además, si el matching es por igualdad ambos deben contener la misma
		// cantidad de artículos y ser los mismos (esta última condición la
		// asegura la iteración anterior).
		if (type == MatchingCompareType.EQUALS && matchOk) {
			matchOk = getProducts().keySet().size() 
						== anotherProductMatching.getProducts().keySet().size();
		}
		
		return matchOk;
	}

	/**
	 * Resta las cantidades de los artículos contenidos en
	 * <code>fromProductMatching</code> a las cantidades de los artículos de
	 * este matching.
	 * 
	 * @param fromProductMatching
	 *            Matching cuyas cantidades son restadas a esta instancia.
	 */
	public void reduceQty(ProductMatching fromProductMatching) {
		if (fromProductMatching == null) {
			return;
		}
		BigDecimal qty = null;
		// Recorre los artículos y resta la cantidad
		for (Integer productID : fromProductMatching.getProducts().keySet()) {
			qty = fromProductMatching.getProductQty(productID);
			addProduct(productID, qty.negate());
		}
	}

	/**
	 * Devuelve la cantidad de un artículo determinado.
	 * @param productID ID del artículo.
	 * @return Cantidad contenida por este matching.
	 */
	public BigDecimal getProductQty(Integer productID) {
		BigDecimal qty = BigDecimal.ZERO;
		if (productID != null && getProducts().containsKey(productID)) {
			qty = getProducts().get(productID);
		}
		return qty;
	}
	
	/**
	 * @return La Map que contiene los artículos y sus cantidades.
	 */
	private Map<Integer, BigDecimal> getProducts() {
		return products;
	}

	@Override
	public String toString() {
		return getProducts().toString();
	}

}
