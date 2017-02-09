package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

/**
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MCouponsSettlements extends X_C_CouponsSettlements {
	private static final long serialVersionUID = 1L;

	public MCouponsSettlements(Properties ctx, int C_CouponsSettlements_ID, String trxName) {
		super(ctx, C_CouponsSettlements_ID, trxName);
	}

	public MCouponsSettlements(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	public boolean doAfterSave(boolean newRecord, boolean success) {
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
		settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}
	
	protected boolean afterDelete( boolean success ) {
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
		settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}

}
