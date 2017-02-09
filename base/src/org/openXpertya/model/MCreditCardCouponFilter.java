package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MCreditCardCouponFilter extends X_C_CreditCardCouponFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6554561657943669442L;

	public MCreditCardCouponFilter(Properties ctx, int C_CreditCardCouponFilter_ID, String trxName) {
		super(ctx, C_CreditCardCouponFilter_ID, trxName);
		// TODO Auto-generated constructor stub
	}

	public MCreditCardCouponFilter(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	protected boolean afterDelete( boolean success ) {
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
		settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}

}
