package org.openXpertya.recovery;

import java.math.BigDecimal;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.AllocationGenerator.AllocationDocumentType;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.model.MCashLine;

public class RecoveryCashLine extends RecoveryType {

	public RecoveryCashLine(IRecoverySource rs) {
		super(rs);
	}

	@Override
	public Integer getRecoveryID() {
		return getRs().getCashLineRecoveryID();
	}

	@Override
	protected BigDecimal getAmt() {
		MCashLine cl = new MCashLine(getRs().getCtx(), getRecoveryID(), getRs().getTrxName());
		return cl.getAmount();
	}

	@Override
	public AllocationDocumentType getAllocationDocumentType() {
		return AllocationGenerator.AllocationDocumentType.CASH_LINE;
	}

	@Override
	public void addToAllocationLine(MAllocationLine allocationLine) {
		allocationLine.setC_CashLine_ID(getRecoveryID());		
	}

}
