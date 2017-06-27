package org.openXpertya.recovery;

import java.math.BigDecimal;

import org.openXpertya.model.AllocationGenerator;
import org.openXpertya.model.MAllocationLine;
import org.openXpertya.util.Util;

public abstract class RecoveryType {

	/** Fuente de datos del recupero */
	private IRecoverySource rs;
	
	public RecoveryType(IRecoverySource rs) {
		setRs(rs);
	}

	public BigDecimal getRecoveryAmt(){
		BigDecimal amt = BigDecimal.ZERO;
		if(!Util.isEmpty(getRecoveryID(), true)){
			amt = getAmt();
		}
		return amt;
	}
	
	public abstract Integer getRecoveryID();
	protected abstract BigDecimal getAmt();
	public abstract AllocationGenerator.AllocationDocumentType getAllocationDocumentType();
	public abstract void addToAllocationLine(MAllocationLine allocationLine);
	
	protected IRecoverySource getRs() {
		return rs;
	}

	protected void setRs(IRecoverySource rs) {
		this.rs = rs;
	}
	
}
