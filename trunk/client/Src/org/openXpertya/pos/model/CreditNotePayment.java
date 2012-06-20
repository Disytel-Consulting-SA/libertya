package org.openXpertya.pos.model;

import java.math.BigDecimal;

public class CreditNotePayment extends Payment {

	private int invoiceID = 0;
	private BigDecimal availableAmt = null;
	private BigDecimal balanceAmt = null;
	private boolean returnCash = false;
	private BigDecimal returnCashAmt = null;

	/**
	 * Constructor de la clase
	 * @param invoiceID
	 * @param availableAmt
	 */
	public CreditNotePayment(int invoiceID, BigDecimal availableAmt,
			BigDecimal balanceAmt, boolean returnCash, BigDecimal returnCashAmt) {
		super();
		this.invoiceID = invoiceID;
		this.availableAmt = availableAmt;
		setBalanceAmt(balanceAmt);
		setReturnCash(returnCash);
		setReturnCashAmt(returnCashAmt);
	}

	/**
	 * @return the invoiceID
	 */
	public int getInvoiceID() {
		return invoiceID;
	}
	
	/**
	 * @param invoiceID the invoiceID to set
	 */
	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
	
	/**
	 * @return the availableAmt
	 */
	public BigDecimal getAvailableAmt() {
		return availableAmt;
	}
	
	/**
	 * @param availableAmt the availableAmt to set
	 */
	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}

	@Override
	public boolean isCreditNotePayment() {
		return true;
	}

	public void setBalanceAmt(BigDecimal balanceAmt) {
		this.balanceAmt = balanceAmt;
	}

	public BigDecimal getBalanceAmt() {
		return balanceAmt;
	}

	public void setReturnCash(boolean returnCash) {
		this.returnCash = returnCash;
	}

	public boolean isReturnCash() {
		return returnCash;
	}

	public void setReturnCashAmt(BigDecimal returnCashAmt) {
		this.returnCashAmt = returnCashAmt;
	}

	public BigDecimal getReturnCashAmt() {
		return returnCashAmt;
	}
	
}
