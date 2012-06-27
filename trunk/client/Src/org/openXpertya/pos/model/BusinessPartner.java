package org.openXpertya.pos.model;

import java.math.BigDecimal;

public class BusinessPartner {

	private int id;

	private int locationId;
	
	private String taxId;
	
	private String name;
	
	private String customerName;
	
	private String customerAddress;
	
	private String customerIdentification;

	private boolean customerIdentified = false;
	
	private boolean customerSynchronized = false;
	
	private int iVACategory = 0;
	
	private int priceListId;
	
	private DiscountSchema discountSchema;
	
	private BigDecimal flatDiscount;
	
	private String discountSchemaContext;
	
	private PaymentTerm paymentTerm;
	
	private PaymentMedium paymentMedium;
	
	private boolean percepcionLiable;
	
	public BusinessPartner() {
		super();
	}

	/**
	 * @param id
	 * @param locationId
	 */
	public BusinessPartner(int id, int locationId, String taxId, String name) {
		this();
		this.id = id;
		this.locationId = locationId;
		this.taxId = taxId;
		this.name = name;
		this.priceListId = 0;
	}
	
	public BusinessPartner(int id, int locationId, String taxId, String name, int priceListId) {
		this(id,locationId,taxId,name);
		this.priceListId = priceListId;
	}
	
	public BusinessPartner(int id, int locationId, String taxId, String name, int priceListId, DiscountSchema discountSchema, BigDecimal flatDiscount, PaymentTerm paymentTerm, PaymentMedium paymentMedium) {
		this(id,locationId,taxId,name,priceListId);
		this.discountSchema = discountSchema;
		this.flatDiscount = flatDiscount;
		this.paymentTerm = paymentTerm;
		this.paymentMedium = paymentMedium;
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
	 * @return Devuelve locationId.
	 */
	public int getLocationId() {
		return locationId;
	}

	/**
	 * @param locationId
	 *            Fija o asigna locationId.
	 */
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	/**
	 * @return Devuelve taxId.
	 */
	public String getTaxId() {
		return taxId;
	}

	/**
	 * @param taxId Fija o asigna taxId.
	 */
	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the customerAddress.
	 */
	public String getCustomerAddress() {
		return customerAddress != null && customerAddress.trim().isEmpty() ? null
				: customerAddress;
	}

	/**
	 * @param customerAddress The customerAddress to set.
	 */
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	/**
	 * @return Returns the customerIdentification.
	 */
	public String getCustomerIdentification() {
		return customerIdentification != null
				&& customerIdentification.trim().isEmpty() ? null
				: customerIdentification;
	}

	/**
	 * @param customerIdentification The customerIdentification to set.
	 */
	public void setCustomerIdentification(String customerIdentification) {
		this.customerIdentification = customerIdentification;
	}

	/**
	 * @return Returns the customerName.
	 */
	public String getCustomerName() {
		return customerName != null && customerName.trim().isEmpty() ? null
				: customerName;
	}

	/**
	 * @param customerName The customerName to set.
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	/**
	 * @return Returns the customerIdentified.
	 */
	public boolean isCustomerIdentified() {
		return customerIdentified || customerSynchronized;
	}

	/**
	 * @param customerIdentified The customerIdentified to set.
	 */
	public void setCustomerIdentified(boolean customerIdentified) {
		this.customerIdentified = customerIdentified;
	}

	public String getCustomerDescription() {
		StringBuffer desc = new StringBuffer("");
		
		if (getCustomerName() != null)
			desc.append(getCustomerName()).append(" / ");
		if (getCustomerIdentification() != null)
			desc.append(getCustomerIdentification()).append(" / ");
		if (getCustomerAddress() != null)
			desc.append(getCustomerAddress());
		String result = desc.toString();
		if (result.endsWith(" / "))
			result = result.substring(0, result.length()-3);
		
		return result;
	}

	/**
	 * @return Returns the iVACategory.
	 */
	public int getIVACategory() {
		return iVACategory;
	}

	/**
	 * @param category The iVACategory to set.
	 */
	public void setIVACategory(int category) {
		iVACategory = category;
	}

	public void setPriceListId(int priceListId) {
		this.priceListId = priceListId;
	}

	public int getPriceListId() {
		return priceListId;
	}

	public void setDiscountSchema(DiscountSchema discountSchema) {
		this.discountSchema = discountSchema;
	}

	public DiscountSchema getDiscountSchema() {
		return discountSchema;
	}

	public void setFlatDiscount(BigDecimal flatDiscount) {
		this.flatDiscount = flatDiscount;
	}

	public BigDecimal getFlatDiscount() {
		return flatDiscount;
	}
	
	/**
	 * @return Indica si esta entidad comercial tiene un esquema de
	 * descuento asociado o no.
	 */
	public boolean hasDiscount() {
		return getDiscountSchema() != null;
	}

	/**
	 * @return el valor de customerSinchronyzed
	 */
	public boolean isCustomerSynchronized() {
		return customerSynchronized;
	}

	/**
	 * @param customerSynchronized el valor de customerSinchronyzed a asignar
	 */
	public void setCustomerSynchronized(boolean customerSynchronized) {
		this.customerSynchronized = customerSynchronized;
	}
	
	public void setLocation(Location location) {
		setLocationId(location.getId());
		if (isCustomerSynchronized()) {
			setCustomerAddress(location.toString());
		}
	}

	public void setDiscountSchemaContext(String discountSchemaContext) {
		this.discountSchemaContext = discountSchemaContext;
	}

	public String getDiscountSchemaContext() {
		return discountSchemaContext;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentMedium(PaymentMedium paymentMedium) {
		this.paymentMedium = paymentMedium;
	}

	public PaymentMedium getPaymentMedium() {
		return paymentMedium;
	}

	public void setPercepcionLiable(boolean percepcionLiable) {
		this.percepcionLiable = percepcionLiable;
	}

	public boolean isPercepcionLiable() {
		return percepcionLiable;
	}
}
