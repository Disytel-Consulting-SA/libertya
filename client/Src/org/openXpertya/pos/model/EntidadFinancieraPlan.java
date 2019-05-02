package org.openXpertya.pos.model;

import java.util.HashMap;
import java.util.Map;

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
	/** ID utilizado para el Calculador de Descuentos */
	private Map<String, Integer> internalIDs = null;
	
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
		this.internalIDs = new HashMap<String, Integer>();
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

	@Override
	public String getTenderType() {
		return MPOSPaymentMedium.TENDERTYPE_CreditCard;
	}

	public Map<String, Integer> getInternalIDs() {
		return internalIDs;
	}

	public void setInternalIDs(Map<String, Integer> internalIDs) {
		this.internalIDs = internalIDs;
	}
	
	/**
	 * @return the internalID
	 */
	public Integer getInternalID(Payment payment) {
		// El id interno se utiliza por el discount calculator para tener los
		// descuentos por esquema de descuento. Para el caso de tarjetas de
		// crédito, los esquemas de descuento se configuran dentro de los
		// planes, y a su vez por banco ya que se pueden tener mismos planes 
		// (mismas tarjetas y mismas cuotas) pero distinto banco. 
		// Por lo tanto si tenemos varios planes con diferentes esquemas
		// de descuento y se utiliza el mismo id para todos los planes de un
		// medio de pago, entonces puede haber conflictos entre esquemas de
		// descuento diferentes.
		// Entonces, los id internos de tarjetas de crédito se manejan en los
		// planes
		return getInternalIDs().get(((CreditCardPayment)payment).getBankName());
	}
	
	public Integer setInternalID(Integer internalID, Payment payment) {
		// El id interno se utiliza por el discount calculator para tener los
		// descuentos por esquema de descuento. Para el caso de tarjetas de
		// crédito, los esquemas de descuento se configuran dentro de los
		// planes, y a su vez por banco ya que se pueden tener mismos planes 
		// (mismas tarjetas y mismas cuotas) pero distinto banco. 
		// Por lo tanto si tenemos varios planes con diferentes esquemas
		// de descuento y se utiliza el mismo id para todos los planes de un
		// medio de pago, entonces puede haber conflictos entre esquemas de
		// descuento diferentes.
		// Entonces, los id internos de tarjetas de crédito se manejan en los
		// planes
		return getInternalIDs().put(((CreditCardPayment)payment).getBankName(), internalID);
	}
}
