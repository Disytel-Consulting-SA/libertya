package org.openXpertya.pos.exceptions;

import org.openXpertya.pos.model.Product;

public class ProductAddValidationFailed extends PosException {

	private Product product = null;
	
	public ProductAddValidationFailed(Product product, String message) {
		super(message);
		this.product = product; 
	}

	public ProductAddValidationFailed(Product product, String message, String description) {
		super(message, description);
		this.product = product;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

}
