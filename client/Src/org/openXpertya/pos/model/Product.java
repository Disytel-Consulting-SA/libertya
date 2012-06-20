package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Product {

	private int id;

	private String code;

	private String description;

	private BigDecimal stdPrice;
	
	private BigDecimal limitPrice;

	private int attributeSetInstanceID;
	
	private String masiDescription;
	
	private boolean taxIncludedInPrice = false;
	
	private boolean masiMandatory = false;
	
	private List<Integer> vendorsIds;
	
	private int productCategoryID;
	
	private String checkoutPlace;
	
	private boolean sold = true;
	
	public Product() {
		super();
	}

	/**
	 * @param id
	 * @param code
	 * @param description
	 * @param stdPrice
	 * @param limitPrice
	 * @param M_AttributeSetInstance_ID
	 * @param masiDescription
	 * @param taxIncludedInPrice
	 * @param masiMandatory
	 * @param productCategoryID
	 * @param vendorsIds
	 * @param checkoutPlace
	 * @param sold
	 */
	public Product(int id, String code, String description, BigDecimal stdPrice, BigDecimal limitPrice, int M_AttributeSetInstance_ID, String masiDescription, boolean taxIncludedInPrice, boolean masiMandatory, int productCategoryID, List<Integer> vendorsIds, String checkoutPlace, boolean sold) {
		super();
		setId(id);
		setCode(code);
		setDescription(description);
		setStdPrice(stdPrice);
		setLimitPrice(limitPrice);
		setAttributeSetInstanceID(M_AttributeSetInstance_ID);
		setMasiDescription(masiDescription);
		setTaxIncludedInPrice(taxIncludedInPrice);
		setMasiMandatory(masiMandatory);
		setProductCategoryID(productCategoryID);
		setVendorsIds(vendorsIds);
		setCheckoutPlace(checkoutPlace);
		setSold(sold);
	}
	
	/**
	 * @return Devuelve code.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            Fija o asigna code.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return Devuelve description.
	 */
	public String getDescription() {
		String desc = description;
		if (getAttributeSetInstanceID() > 0)
			desc += " (" + getMasiDescription() + ")";
		return desc;
	}

	/**
	 * @param description
	 *            Fija o asigna description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Devuelve id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            Fija o asigna id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Devuelve price.
	 */
	public BigDecimal getStdPrice() {
		return stdPrice;
	}

	/**
	 * @param price
	 *            Fija o asigna price.
	 */
	public void setStdPrice(BigDecimal price) {
		this.stdPrice = price;
	}

	/**
	 * @return Devuelve limitPrice.
	 */
	public BigDecimal getLimitPrice() {
		return limitPrice;
	}

	/**
	 * @param limitPrice Fija o asigna limitPrice.
	 */
	public void setLimitPrice(BigDecimal limitPrice) {
		this.limitPrice = limitPrice;
	}

	/**
	 * @return the m_AttributeSetInstance_ID
	 */
	public int getAttributeSetInstanceID() {
		return attributeSetInstanceID;
	}

	/**
	 * @param attributeSetInstance_ID the m_AttributeSetInstance_ID to set
	 */
	public void setAttributeSetInstanceID(int attributeSetInstance_ID) {
		attributeSetInstanceID = attributeSetInstance_ID;
	}

	/**
	 * @return the masiDescription
	 */
	public String getMasiDescription() {
		return masiDescription;
	}

	/**
	 * @param masiDescription the masiDescription to set
	 */
	public void setMasiDescription(String masiDescription) {
		this.masiDescription = masiDescription;
	}

	/**
	 * @return Returns the taxIncludedInPrice.
	 */
	public boolean isTaxIncludedInPrice() {
		return taxIncludedInPrice;
	}

	/**
	 * @param taxIncludedInPrice The taxIncludedInPrice to set.
	 */
	public void setTaxIncludedInPrice(boolean taxIncludedInPrice) {
		this.taxIncludedInPrice = taxIncludedInPrice;
	}

	/**
	 * @return Returns the masiMandatory.
	 */
	public boolean isMasiMandatory() {
		return masiMandatory;
	}

	/**
	 * @param masiMandatory The masiMandatory to set.
	 */
	public void setMasiMandatory(boolean masiMandatory) {
		this.masiMandatory = masiMandatory;
	}

	/**
	 * Indica si la configuración de instancia de atributos del artículo es válida.
	 * Si la configuración del artículo requiere que la instancia de atributo sea
	 * obligatoria, entonces el ID de instancia de atributo debe ser mayor que cero.
	 * Si la instancia de atributos NO es obligatoria, entonces el ID de instancia
	 * puede ser 0.
	 */
	public boolean validateMasi() {
		return !isMasiMandatory() || getAttributeSetInstanceID() > 0;
	}

	public void setVendorsIds(List<Integer> vendorsIds) {
		this.vendorsIds = vendorsIds;
	}

	public List<Integer> getVendorsIds() {
		return vendorsIds;
	}

	public void setProductCategoryID(int productCategoryID) {
		this.productCategoryID = productCategoryID;
	}

	public int getProductCategoryID() {
		return productCategoryID;
	}

	/**
	 * @return the checkoutPlace
	 */
	public String getCheckoutPlace() {
		return checkoutPlace;
	}

	/**
	 * @param checkoutPlace the checkoutPlace to set
	 */
	public void setCheckoutPlace(String checkoutPlace) {
		this.checkoutPlace = checkoutPlace;
	}

	/**
	 * @return the sold
	 */
	public boolean isSold() {
		return sold;
	}

	/**
	 * @param sold the sold to set
	 */
	public void setSold(boolean sold) {
		this.sold = sold;
	}
	
	
}
