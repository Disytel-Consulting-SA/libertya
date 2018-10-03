package org.openXpertya.process;

import org.openXpertya.model.X_C_Promotion_Code_Batch;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class VoidPromotionCodesFromBatch extends VoidPromotionCodes {

	/** Lote */
	private X_C_Promotion_Code_Batch batch;
	
	@Override
	public void preVoid() throws Exception {
		batch = new X_C_Promotion_Code_Batch(getCtx(), getPromotionCodeRelationedID(), get_TrxName());
		// Ya fue anulado
		if(!Util.isEmpty(batch.getVoidBatch(), true) && batch.getVoidBatch().equals("Y")){
			throw new Exception(Msg.getMsg(getCtx(), "PromotionalCodeBatchAlreadyVoided"));
		}
	}

	@Override
	public void postVoid() throws Exception {
		// Marcar como anulado el lote
		DB.executeUpdate("update " + X_C_Promotion_Code_Batch.Table_Name + " set voidbatch = 'Y' where "
				+ getPromotionCodeRelationedColumn() + " = " + getPromotionCodeRelationedID());
	}

	@Override
	public String getPromotionCodeRelationedColumn() {
		return "c_promotion_code_batch_id";
	}

	@Override
	public int getPromotionCodeRelationedID() {
		return getParamValueAsInt("C_PROMOTION_CODE_BATCH_ID") != null ? getParamValueAsInt("C_PROMOTION_CODE_BATCH_ID")
				: getRecord_ID();
	}

}
