package org.openXpertya.pos.model;

import java.math.BigDecimal;

public class PriceList {

	// Variables de instancia
	
	private int id;
	
	private String name;
	
	private String description;
	
	private int currencyID;
	
	private boolean isTaxIncluded;
	
	private boolean isPerceptionsIncluded;
	
	private boolean isSOPriceList;
	
	private boolean isDefault;
	
	private BigDecimal precision;
	
	// Constructores
	
	public PriceList() {

	}

	public PriceList(int id, String name, String description, int currencyID, boolean isTaxIncluded, boolean isPerceptionsIncluded, boolean isSOPriceList,	boolean isDefault, BigDecimal precision) {
		setId(id);
		setName(name);
		setDescription(description);
		setCurrencyID(currencyID);
		setTaxIncluded(isTaxIncluded);
		setPerceptionsIncluded(isPerceptionsIncluded);
		setSOPriceList(isSOPriceList);
		setDefault(isDefault);
		setPrecision(precision);
	}

	
	public String toString(){
		return getName();
	}
	
	// Getters y Setters
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
	}

	public int getCurrencyID() {
		return currencyID;
	}

	public void setTaxIncluded(boolean isTaxIncluded) {
		this.isTaxIncluded = isTaxIncluded;
	}

	public boolean isTaxIncluded() {
		return isTaxIncluded;
	}

	public boolean isPerceptionsIncluded() {
		return isPerceptionsIncluded;
	}

	public void setPerceptionsIncluded(boolean isPerceptionsIncluded) {
		this.isPerceptionsIncluded = isPerceptionsIncluded;
	}

	public void setSOPriceList(boolean isSOPriceList) {
		this.isSOPriceList = isSOPriceList;
	}

	public boolean isSOPriceList() {
		return isSOPriceList;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setPrecision(BigDecimal precision) {
		this.precision = precision;
	}

	public BigDecimal getPrecision() {
		return precision;
	}
	
}
