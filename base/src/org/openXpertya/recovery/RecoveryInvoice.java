package org.openXpertya.recovery;

import java.math.BigDecimal;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.AllocationGenerator.AllocationDocumentType;
import org.openXpertya.model.MInvoice;

public class RecoveryInvoice extends RecoveryType {

	public RecoveryInvoice(IRecoverySource rs) {
		super(rs);
	}

	@Override
	public Integer getRecoveryID() {
		return getRs().getCreditRecoveryID();
	}

	@Override
	protected BigDecimal getAmt() {
		MInvoice invoice = new MInvoice(getRs().getCtx(), getRecoveryID(), getRs().getTrxName());
		return invoice.getGrandTotal();
	}

	@Override
	public AllocationDocumentType getAllocationDocumentType() {
		return AllocationGenerator.AllocationDocumentType.INVOICE;
	}

	@Override
	public void addToAllocationLine(MAllocationLine allocationLine) {
		allocationLine.setC_Invoice_Credit_ID(getRecoveryID());		
	}
}
