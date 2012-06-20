package org.openXpertya.pos.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductList {

	public static final int MASI_UPC_EXACT_MATCH   = 2000;
	public static final int UPC_EXACT_MATCH        = 2100;
	public static final int VALUE_EXACT_MATCH      = 2200;
	public static final int PO_UPC_EXACT_MATCH     = 2300;
	public static final int BP_CODE_EXACT_MATCH    = 2400;
	public static final int NAME_EXACT_MATCH       = 2500;
	public static final int MASI_UPC_PARTIAL_MATCH = 3000;
	public static final int UPC_PARTIAL_MATCH      = 3100;
	public static final int VALUE_PARTIAL_MATCH    = 3200;
	public static final int PO_UPC_PARTIAL_MATCH   = 3300;
	public static final int BP_CODE_PARTIAL_MATCH  = 3400;
	public static final int NAME_PARTIAL_MATCH     = 3500;
		
	private static List<Integer> matchTypes;
	static {
		matchTypes = new ArrayList<Integer>();
		matchTypes.add(MASI_UPC_EXACT_MATCH);
		matchTypes.add(UPC_EXACT_MATCH);
		matchTypes.add(VALUE_EXACT_MATCH);
		matchTypes.add(PO_UPC_EXACT_MATCH);
		matchTypes.add(BP_CODE_EXACT_MATCH);
		matchTypes.add(NAME_EXACT_MATCH);
		matchTypes.add(MASI_UPC_PARTIAL_MATCH);
		matchTypes.add(UPC_PARTIAL_MATCH);
		matchTypes.add(VALUE_PARTIAL_MATCH);
		matchTypes.add(PO_UPC_PARTIAL_MATCH);
		matchTypes.add(BP_CODE_PARTIAL_MATCH);
		matchTypes.add(NAME_PARTIAL_MATCH);
		Collections.sort(matchTypes);
	}
	
	private Map<Integer,List<Product>> productsMap;
	
	private List<Product> productsCache;

	public ProductList() {
		super();
		setProductsMap(new HashMap<Integer,List<Product>>());
	}
	
	/**
	 * Agrega un producto a la lista con un determinado tipo de matching. 
	 * @param product Producto a agregar.
	 * @param matchType Tipo de matching.
	 */
	public void addProduct(Product product, int matchType) {
		getProductsOfMatchType(matchType).add(product);
		productsCache = null; //reset
	}
	
	/**
	 * Retorna la lista de productos de una determinada clase. 
	 */
	public List<Product> getProductsOfMatchType(int matchType) {
		List<Product> list = getProductsMap().get(matchType);
		if (list == null) {
			list = new ArrayList<Product>();
			getProductsMap().put(matchType, list);
		}
		return list;
	}
	
	public boolean hasExactProduct() {
		return getExactProductCount() > 0;
	}
	
	public Product getExactProduct() {
		return getProducts().get(0);
	}
	
	public boolean isEmpty() {
		return getProducts().isEmpty();
	}

	/**
	 * @return Returns the productsMap.
	 */
	private Map<Integer, List<Product>> getProductsMap() {
		return productsMap;
	}

	/**
	 * @param productsMap The productsMap to set.
	 */
	private void setProductsMap(Map<Integer, List<Product>> productsMap) {
		this.productsMap = productsMap;
	}

	/**
	 * @return Returns the products.
	 */
	public List<Product> getProducts() {
		if (productsCache == null) {
			productsCache = new ArrayList<Product>();
			for (Integer matchType : matchTypes) {
				productsCache.addAll(getProductsOfMatchType(matchType));
			}
		}
		return productsCache;
	}
	
	/**
	 * @return Devuelve el número de artículos contenidos en esta lista.
	 */
	public int size() {
		return getProducts().size();
	}
	
	/**
	 * @return Devuelve el primer artículo de la lista. Si la lista está vacía
	 * devuelve <code>null</code>.
	 */
	public Product firstProduct() {
		Product product = null;
		if (getProducts().size() >= 1) {
			product = getProducts().get(0);
		}
		return product;
	}
	
	/**
	 * @return Devuelve la cantidad de artículos encontrados por matching exacto
	 */
	public int getExactProductCount() {
		return 
			getProductsOfMatchType(MASI_UPC_EXACT_MATCH).size() +
			getProductsOfMatchType(UPC_EXACT_MATCH).size() +
			getProductsOfMatchType(VALUE_EXACT_MATCH).size() +
			getProductsOfMatchType(PO_UPC_EXACT_MATCH).size() +
			getProductsOfMatchType(BP_CODE_EXACT_MATCH).size() +
			getProductsOfMatchType(NAME_EXACT_MATCH).size();
	}

}
