package org.openXpertya.pos.model;

import java.sql.Timestamp;

public class PriceListVersion {

	// Variables de instancia
	
	private int id;
	
	private String name;
	
	private String description;
	
	private int discountSchemaID;
	
	private Timestamp validFrom;
	
	// Constructores
	
	public PriceListVersion() {
		
	}

	public PriceListVersion(int id, String name, String description, int discountSchemaID, Timestamp validFrom) {
		setId(id);
		setName(name);
		setDescription(description);
		setDiscountSchemaID(discountSchemaID);
		setValidFrom(validFrom);
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

	public void setDiscountSchemaID(int discountSchemaID) {
		this.discountSchemaID = discountSchemaID;
	}

	public int getDiscountSchemaID() {
		return discountSchemaID;
	}

	public void setValidFrom(Timestamp validFrom) {
		this.validFrom = validFrom;
	}

	public Timestamp getValidFrom() {
		return validFrom;
	}

}
