package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

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
	
	@Override
	protected boolean beforeDelete() {
		// Si un cupón está conciliado no puede eliminarse
		if (haveCouponsReconciled()) {
			log.saveError("DeleteError", Msg.translate(getCtx(),
					"ReconcileCouponsDelete"));
			return false;
		}
		return true;
	}
	
	protected boolean afterDelete( boolean success ) {
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
		settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}
	
	private boolean haveCouponsReconciled() {
		int linesCount = DB.getSQLValue(get_TrxName(),
				"SELECT COUNT(*) FROM C_CouponsSettlements WHERE C_CreditCardCouponFilter_ID = ? AND IsReconciled = 'Y'",
				getID());
		return linesCount > 0;
	}

}
