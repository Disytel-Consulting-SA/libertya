package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * Incrementar stock, reponer mercadería, por lo tanto hay que tomar
 * los que se encuentran usados (ya utilizados) y decrementar la cantidad usada.
 */
public class IncrementStockClearanceProcessing extends ImportClearanceProcessing {

	public IncrementStockClearanceProcessing() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<MImportClearance> getImportClearances(Properties ctx, Integer productID, String trxName) {
		return MImportClearance.getUsedImportClearance(ctx, productID, trxName);
	}

	@Override
	public BigDecimal getQtyToCompare(MImportClearance mic) {
		return mic.getQtyUsed();
	}

	@Override
	public BigDecimal getNewQtyUsed(MImportClearance mic, BigDecimal qty) {
		return mic.getQtyUsed().subtract(qty);
	}

	@Override
	public BigDecimal getRealQty(BigDecimal qty) {
		return qty.negate();
	}

	
}
