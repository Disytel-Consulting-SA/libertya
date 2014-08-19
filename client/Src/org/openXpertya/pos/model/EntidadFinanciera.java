package org.openXpertya.pos.model;

import java.math.BigDecimal;


public class EntidadFinanciera {

	private int id;
	private String name;
	private String cardMask;
	private boolean cashRetirement;
	private BigDecimal cashRetirementLimit;
	
	/**
	 * 
	 */
	public EntidadFinanciera() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param entidadFinancieraId
	 * @param name
	 */
	public EntidadFinanciera(int entidadFinancieraId, String name, String cardMask, boolean isCashRetirement, BigDecimal cashRetirementLimit) {
		super();
		// TODO Auto-generated constructor stub
		this.id = entidadFinancieraId;
		this.name = name;
		this.cardMask = cardMask;
		setCashRetirement(isCashRetirement);
		setCashRetirementLimit(cashRetirementLimit);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int entidadFinancieraId) {
		this.id = entidadFinancieraId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setCardMask(String cardMask) {
		this.cardMask = cardMask;
	}

	public String getCardMask() {
		return cardMask;
	}

	public boolean isCashRetirement() {
		return cashRetirement;
	}

	public void setCashRetirement(boolean cashRetirement) {
		this.cashRetirement = cashRetirement;
	}

	public BigDecimal getCashRetirementLimit() {
		return cashRetirementLimit;
	}

	public void setCashRetirementLimit(BigDecimal cashRetirementLimit) {
		this.cashRetirementLimit = cashRetirementLimit;
	}	
}
