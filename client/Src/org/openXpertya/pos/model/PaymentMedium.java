package org.openXpertya.pos.model;

import java.util.List;

import org.openXpertya.model.MPOSPaymentMedium;


/**
 * Representación de C_POSPaymentMedium
 * 
 * @author Franco Bonafine
 */
public class PaymentMedium implements IPaymentMediumInfo {

	private int id;
	private String name;
	private String tenderType;
	private int currencyID;
	private String tenderTypeName;
	
	/** ID utilizado para el Calculador de Descuentos */
	private Integer internalID = null;
	
	/** ID de Entidad Financiera. Solo para TenderType = Tarjeta */
	private Integer entidadFinancieraID = null;
	/** Lista de planes de tarjeta de crédito. Solo para TenderType = Tarjeta */
	private List<EntidadFinancieraPlan> creditCardPlans;
	
	/** Plazo de cobro de un cheque. Solo para TenderType = Cheque */
	private Integer checkDeadLine = null;
	
	/** Banco del medio de Pago (campo Bank, lista de validación). Este 
	 * atributo guarda el Value de la lista */
	private String bank = null; 
	
	/** Esquema de descuento. Todos los MP menos tarjeta */
	private DiscountSchema discountSchema = null;
	
	/**
	 * Constructor de la clase
	 * @param name
	 * @param tenderType
	 * @param currencyID
	 */
	public PaymentMedium(int id, String name, String tenderType, int currencyID) {
		super();
		this.id = id;
		this.name = name;
		this.tenderType = tenderType;
		this.currencyID = currencyID;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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
	 * @return the tenderType
	 */
	public String getTenderType() {
		return tenderType;
	}
	/**
	 * @param tenderType the tenderType to set
	 */
	public void setTenderType(String tenderType) {
		this.tenderType = tenderType;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @return the currencyID
	 */
	public int getCurrencyID() {
		return currencyID;
	}

	/**
	 * @param currencyID the currencyID to set
	 */
	public void setCurrencyID(int currencyID) {
		this.currencyID = currencyID;
	}

	/**
	 * @return the creditCardPlans
	 */
	public List<EntidadFinancieraPlan> getCreditCardPlans() {
		return creditCardPlans;
	}

	/**
	 * @param creditCardPlans the creditCardPlans to set
	 */
	public void setCreditCardPlans(List<EntidadFinancieraPlan> creditCardPlans) {
		this.creditCardPlans = creditCardPlans;
	}

	/**
	 * @return the entidadFinancieraID
	 */
	public Integer getEntidadFinancieraID() {
		return entidadFinancieraID;
	}

	/**
	 * @param entidadFinancieraID the entidadFinancieraID to set
	 */
	public void setEntidadFinancieraID(Integer entidadFinancieraID) {
		this.entidadFinancieraID = entidadFinancieraID;
	}
	
	
	/**
	 * @return Indica si este medio de pago es de tipo Tarjeta de Crédito. 
	 */
	public boolean isCreditCard() {
		return getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_CreditCard);
	}
	
	/**
	 * @return Indica si este medio de pago es de tipo Cheque.
	 */
	public boolean isCheck() {
		return getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_Check);
	}

	/**
	 * @return Indica si este medio de pago es de tipo Crédito
	 */
	public boolean isCredit() {
		return getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_Credit);
	}
	
	/**
	 * @return Indica si este medio de pago es de tipo Nota de Crédito
	 */
	public boolean isCreditNote() {
		return getTenderType().equals(MPOSPaymentMedium.TENDERTYPE_CreditNote);
	}
	
	/**
	 * @return the checkDeadLine
	 */
	public Integer getCheckDeadLine() {
		return checkDeadLine;
	}

	/**
	 * @param checkDeadLine the checkDeadLine to set
	 */
	public void setCheckDeadLine(Integer checkDeadLine) {
		this.checkDeadLine = checkDeadLine;
	}

	/**
	 * @return the tenderTypeName
	 */
	public String getTenderTypeName() {
		if (tenderTypeName == null) {
			return getName();
		}
		return tenderTypeName;
	}

	/**
	 * @param tenderTypeName the tenderTypeName to set
	 */
	public void setTenderTypeName(String tenderTypeName) {
		this.tenderTypeName = tenderTypeName;
	}

	/**
	 * @return the bank
	 */
	public String getBank() {
		return bank;
	}

	/**
	 * @param bank the bank to set
	 */
	public void setBank(String bank) {
		// Aseguramos no guardar string vacios
		if (bank != null && bank.trim().length() == 0) {
			bank = null;
		}
		this.bank = bank;
	}
	
	/**
	 * @return Indica si este medio de pago tiene un banco asociado
	 * (solo para Tarjetas y Cheques)
	 */
	public boolean hasBank() {
		return getBank() != null;
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

	/**
	 * @return the internalID
	 */
	public Integer getInternalID() {
		return internalID;
	}

	/**
	 * @param internalID the internalID to set
	 */
	public void setInternalID(Integer internalID) {
		this.internalID = internalID;
	}
	
}
