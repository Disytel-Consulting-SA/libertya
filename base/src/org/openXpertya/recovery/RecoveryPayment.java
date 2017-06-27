package org.openXpertya.recovery;

import java.math.BigDecimal;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.AllocationGenerator.AllocationDocumentType;
import org.openXpertya.model.MPayment;

public class RecoveryPayment extends RecoveryType {
	
	public RecoveryPayment(IRecoverySource rs) {
		super(rs);
	}

	@Override
	public Integer getRecoveryID() {
		return getRs().getPaymentRecoveryID();
	}

	@Override
	protected BigDecimal getAmt() {
		MPayment p = new MPayment(getRs().getCtx(), getRecoveryID(), getRs().getTrxName());
		return p.getPayAmt();
	}

	@Override
	public AllocationDocumentType getAllocationDocumentType() {
		return AllocationGenerator.AllocationDocumentType.PAYMENT;
	}

	@Override
	public void addToAllocationLine(MAllocationLine allocationLine) {
		allocationLine.setC_Payment_ID(getRecoveryID());		
	}
}
