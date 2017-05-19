package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;
/**
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class MCouponsSettlements extends X_C_CouponsSettlements {
	private static final long serialVersionUID = 1L;
	private boolean reconciledFlag;

	public MCouponsSettlements(Properties ctx, int C_CouponsSettlements_ID, String trxName) {
		super(ctx, C_CouponsSettlements_ID, trxName);
	}

	public MCouponsSettlements(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public boolean isReconciledFlag() {
		return reconciledFlag;
	}

	public void setReconciledFlag(boolean reconciledFlag) {
		this.reconciledFlag = reconciledFlag;
	}

	@Override
	public boolean doAfterSave(boolean newRecord, boolean success) {
		MCreditCardSettlement settlement = new MCreditCardSettlement(getCtx(), getC_CreditCardSettlement_ID(), get_TrxName());
		if (!reconciledFlag)
			settlement.calculateSettlementCouponsTotalAmount(get_TrxName());
		return true;
	}
	
	@Override
	protected boolean beforeDelete() {
		// Si un cupón está conciliado no puede eliminarse
		if (isReconciled()) {
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

}
