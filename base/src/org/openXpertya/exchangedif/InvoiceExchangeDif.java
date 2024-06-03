package org.openXpertya.exchangedif;

import java.math.BigDecimal;

import org.openXpertya.model.MInvoice;
import org.openXpertya.util.Env;

public class InvoiceExchangeDif {
	
	private MInvoice invoice = null;
	private BigDecimal exchangeDiff = null;
	private int C_Invoice_ID = 0;

	public InvoiceExchangeDif(int invId, BigDecimal exchangeDif) {
		C_Invoice_ID = invId;
		invoice = new MInvoice(Env.getCtx(), invId, null);
		exchangeDiff = exchangeDif;
	}
	
	public InvoiceExchangeDif(MInvoice inv, BigDecimal exchangeDif) {
		invoice = inv;
		C_Invoice_ID = inv.getC_Invoice_ID();
		exchangeDiff = exchangeDif;
	}

	public int getC_Invoice_ID() {
		return C_Invoice_ID;
	}

	public void setC_Invoice_ID(int c_Invoice_ID) {
		C_Invoice_ID = c_Invoice_ID;
	}

	public MInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(MInvoice invoice) {
		this.invoice = invoice;
	}

	public BigDecimal getExchangeDiff() {
		return exchangeDiff;
	}

	public void setExchangeDiff(BigDecimal exchangeDiff) {
		this.exchangeDiff = exchangeDiff;
	}

}
