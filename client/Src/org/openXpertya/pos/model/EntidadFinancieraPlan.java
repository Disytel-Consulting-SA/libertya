package org.openXpertya.pos.model;

import org.openXpertya.model.MPOSPaymentMedium;

/**
 * Representación de M_EntidadFinancieraPlan
 * 
 * @author Franco Bonafine
 *
 */
public class EntidadFinancieraPlan implements IPaymentMediumInfo {

	private int entidadFinancieraPlanID = 0;
	private int entidadFinancieraID = 0;
	private String name;
	private int coutasPago;
	private DiscountSchema discountSchema;
	
	/**
	 * Constructor de la clase
	 * @param name
	 * @param coutasPago
	 */
	public EntidadFinancieraPlan(int entidadFinancieraPlanID, int entidadFinancieraID, String name, int coutasPago) {
		super();
		this.entidadFinancieraPlanID = entidadFinancieraPlanID;
		this.entidadFinancieraID = entidadFinancieraID;
		this.name = name;
		this.coutasPago = coutasPago;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the coutasPago
	 */
	public int getCoutasPago() {
		return coutasPago;
	}
	
	/**
	 * @param coutasPago the coutasPago to set
	 */
	public void setCoutasPago(int coutasPago) {
		this.coutasPago = coutasPago;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the entidadFinancieraPlanID
	 */
	public int getEntidadFinancieraPlanID() {
		return entidadFinancieraPlanID;
	}

	/**
	 * @param entidadFinancieraPlanID the entidadFinancieraPlanID to set
	 */
	public void setEntidadFinancieraPlanID(int entidadFinancieraPlanID) {
		this.entidadFinancieraPlanID = entidadFinancieraPlanID;
	}

	/**
	 * @return the entidadFinancieraID
	 */
	public int getEntidadFinancieraID() {
		return entidadFinancieraID;
	}

	/**
	 * @param entidadFinancieraID the entidadFinancieraID to set
	 */
	public void setEntidadFinancieraID(int entidadFinancieraID) {
		this.entidadFinancieraID = entidadFinancieraID;
	}

	/**
	 * @return the discountSchema
	 */
	public DiscountSchema getDiscountSchema() {
		return discountSchema;
	}

	/**
	 * @param discountSchema the discountSchema to set
	 */
	public void setDiscountSchema(DiscountSchema discountSchema) {
		this.discountSchema = discountSchema;
	}

	/* (non-Javadoc)
	 * @see org.openXpertya.pos.model.IPaymentMediumInfo#getTenderType()
	 */
	@Override
	public String getTenderType() {
		return MPOSPaymentMedium.TENDERTYPE_CreditCard;
	}
}
