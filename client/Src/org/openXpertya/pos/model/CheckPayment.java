package org.openXpertya.pos.model;

import java.sql.Timestamp;

public class CheckPayment extends Payment {

	private String bankName;

	private String checkNumber;

	private Timestamp emissionDate;

	private Timestamp acctDate;
	
	private int bankAccountID;
	
	private String cuitLibrador = null;

	public CheckPayment() {
		super();
	}

	/**
	 * @param bankId
	 * @param checkNumber
	 * @param emissionDate
	 * @param acctDate
	 * @param bankAccountID
	 */
	public CheckPayment(String bankName, String checkNumber, Timestamp emissionDate,
			Timestamp acctDate, int bankAccountID) {
		this();
		this.bankName = bankName;
		this.checkNumber = checkNumber;
		this.emissionDate = emissionDate;
		this.acctDate = acctDate;
		this.bankAccountID = bankAccountID;
	}

	/**
	 * @return Devuelve acctDate.
	 */
	public Timestamp getAcctDate() {
		return acctDate;
	}

	/**
	 * @param acctDate
	 *            Fija o asigna acctDate.
	 */
	public void setAcctDate(Timestamp acctDate) {
		this.acctDate = acctDate;
	}

	/**
	 * @return Devuelve bankName.
	 */
	public String getBankName() {
		return bankName;
	}

	/**
	 * @param bankName
	 *            Fija o asigna bankName.
	 */
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	/**
	 * @return Devuelve checkNumber.
	 */
	public String getCheckNumber() {
		return checkNumber;
	}

	/**
	 * @param checkNumber
	 *            Fija o asigna checkNumber.
	 */
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}

	/**
	 * @return Devuelve emissionDate.
	 */
	public Timestamp getEmissionDate() {
		return emissionDate;
	}

	/**
	 * @param emissionDate
	 *            Fija o asigna emissionDate.
	 */
	public void setEmissionDate(Timestamp emissionDate) {
		this.emissionDate = emissionDate;
	}

	@Override
	public boolean isCheckPayment() {
		return true;
	}

	/**
	 * @return the bankAccountID
	 */
	public int getBankAccountID() {
		return bankAccountID;
	}

	/**
	 * @param bankAccountID the bankAccountID to set
	 */
	public void setBankAccountID(int bankAccountID) {
		this.bankAccountID = bankAccountID;
	}

	/**
	 * @return the cuitLibrador
	 */
	public String getCuitLibrador() {
		return cuitLibrador;
	}

	/**
	 * @param cuitLibrador the cuitLibrador to set
	 */
	public void setCuitLibrador(String cuitLibrador) {
		this.cuitLibrador = cuitLibrador;
	}
	
	

}
