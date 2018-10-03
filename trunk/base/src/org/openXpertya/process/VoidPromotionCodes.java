package org.openXpertya.process;

import org.openXpertya.model.MPromotionCode;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public abstract class VoidPromotionCodes extends AbstractSvrProcess {
	
	@Override
	protected String doIt() throws Exception {
		// Previo a anular
		preVoid();
		// Anulación
		int voided = voidCodes();
		// Post anulación
		postVoid();
		
		return getMsg(voided);
	}

	/**
	 * Anula los códigos promocionales existentes
	 * 
	 * @return cantidad de códigos promocionales anulados
	 * @throws Exception
	 */
	public int voidCodes() throws Exception {
		String sql = "update " + MPromotionCode.Table_Name + " set isactive = 'N' where "
				+ getPromotionCodeRelationedColumn() + " = " + getPromotionCodeRelationedID();
		return DB.executeUpdate(sql, get_TrxName());
	}
	
	public String getMsg(int voidedCodes){
		return Msg.getMsg(getCtx(), "PromotionalCodesVoided")+" : "+voidedCodes;
	}
	
	public abstract void preVoid() throws Exception;
	
	public abstract void postVoid() throws Exception;
	
	public abstract String getPromotionCodeRelationedColumn();

	public abstract int getPromotionCodeRelationedID();
}
