package org.openXpertya.pos.model;

import org.openXpertya.util.Util;

public class EntidadFinanciera {

	private int id;
	private String name;
	private String cardMask;
	
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
	public EntidadFinanciera(int entidadFinancieraId, String name, String cardMask) {
		super();
		// TODO Auto-generated constructor stub
		this.id = entidadFinancieraId;
		this.name = name;
		this.cardMask = cardMask;
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
}
