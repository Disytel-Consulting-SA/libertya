package org.openXpertya.pos.model;

import java.sql.Timestamp;

public class BankTransferPayment extends Payment {

	private String transferNumber;
	
	private int bankAccountID;
	
	private Timestamp transferDate;
	
	/**
	 * Constructor de la clase
	 * @param transferNumber
	 * @param bankAccountID
	 * @param transferDate
	 */
	public BankTransferPayment(String transferNumber, int bankAccountID,
			Timestamp transferDate) {
		super();
		this.transferNumber = transferNumber;
		this.bankAccountID = bankAccountID;
		this.transferDate = transferDate;
	}

	/**
	 * @return the transferNumber
	 */
	public String getTransferNumber() {
		return transferNumber;
	}

	/**
	 * @param transferNumber the transferNumber to set
	 */
	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
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
	 * @return the transferDate
	 */
	public Timestamp getTransferDate() {
		return transferDate;
	}

	/**
	 * @param transferDate the transferDate to set
	 */
	public void setTransferDate(Timestamp transferDate) {
		this.transferDate = transferDate;
	}

	@Override
	public boolean isBankTransferPayment() {
		return true;
	}

}
