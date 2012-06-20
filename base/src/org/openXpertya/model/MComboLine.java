package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.Msg;

public class MComboLine extends X_C_ComboLine {

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param C_ComboLine_ID
	 * @param trxName
	 */
	public MComboLine(Properties ctx, int C_ComboLine_ID, String trxName) {
		super(ctx, C_ComboLine_ID, trxName);
	}

	/**
	 * Constructor de PO
	 * @param ctx
	 * @param rs
	 * @param trxName
	 */
	public MComboLine(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		// La cantidad debe ser mayor que cero
		if (getQty().compareTo(BigDecimal.ZERO) <= 0) {
			log.saveError("SaveError", Msg.getMsg(getCtx(), "ValueMustBeGreatherThanZero",
					new Object[] { Msg.translate(getCtx(), "Qty")}));
			return false;
		}
		
		return true;
	}
	
	@Override
	protected boolean beforeDelete() {
		// No se puede eliminar una línea cuando el combo está publicado
		MCombo combo = new MCombo(getCtx(), getC_Combo_ID(), get_TrxName());
		if(combo.isPublished()){
			log.saveError("DeleteComboLinePublished", "");
			return false;
		}
		return true;
	}
}
