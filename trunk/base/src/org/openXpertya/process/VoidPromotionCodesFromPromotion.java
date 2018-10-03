package org.openXpertya.process;

public class VoidPromotionCodesFromPromotion extends VoidPromotionCodes {

	@Override
	public void preVoid() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postVoid() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPromotionCodeRelationedColumn() {
		return "c_promotion_id";
	}

	@Override
	public int getPromotionCodeRelationedID() {
		return getParamValueAsInt("C_PROMOTION_ID") != null ? getParamValueAsInt("C_PROMOTION_ID")
				: getRecord_ID();
	}

}
