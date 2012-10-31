package org.openXpertya.model;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class DocumentTax {

	private int taxID = 0;
	private BigDecimal taxAmt = BigDecimal.ZERO;
	private BigDecimal taxBaseAmt = BigDecimal.ZERO;
	private BigDecimal taxRate = BigDecimal.ZERO;
	
	public DocumentTax() {
		// TODO Auto-generated constructor stub
	}

	public void setTaxRate(){
		setTaxRate(getTaxAmt().multiply(Env.ONEHUNDRED).divide(getTaxBaseAmt(),
				2, BigDecimal.ROUND_HALF_UP));
	}
	
	public int getTaxID() {
		return taxID;
	}

	public void setTaxID(int taxID) {
		this.taxID = taxID;
	}

	public BigDecimal getTaxAmt() {
		return taxAmt;
	}

	public void setTaxAmt(BigDecimal taxAmt) {
		this.taxAmt = taxAmt;
	}

	public BigDecimal getTaxBaseAmt() {
		return taxBaseAmt;
	}

	public void setTaxBaseAmt(BigDecimal taxBaseAmt) {
		this.taxBaseAmt = taxBaseAmt;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}
}
