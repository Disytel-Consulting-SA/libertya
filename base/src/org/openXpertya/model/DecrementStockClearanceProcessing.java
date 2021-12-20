package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

public class DecrementStockClearanceProcessing extends ImportClearanceProcessing {

	public DecrementStockClearanceProcessing() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<MImportClearance> getImportClearances(Properties ctx, Integer productID, String trxName) {
		return MImportClearance.getAvailablesImportClearance(ctx, productID, trxName);
	}

	@Override
	public BigDecimal getQtyToCompare(MImportClearance mic) {
		return mic.getAvailableQty();
	}

	@Override
	public BigDecimal getNewQtyUsed(MImportClearance mic, BigDecimal qty) {
		return mic.getQtyUsed().add(qty);
	}

	@Override
	public BigDecimal getRealQty(BigDecimal qty) {
		return qty.abs();
	}
}
