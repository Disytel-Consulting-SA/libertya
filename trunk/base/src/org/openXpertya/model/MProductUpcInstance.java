package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;

public class MProductUpcInstance extends X_M_Product_Upc_Instance {

	public MProductUpcInstance(Properties ctx, int M_Product_Upc_Instance_ID, String trxName) {
		super(ctx, M_Product_Upc_Instance_ID, trxName);
	}
	
	public MProductUpcInstance(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		int count = Integer.parseInt(DB.getSQLObject(get_TrxName(), "SELECT COUNT(*) FROM " + Table_Name + " WHERE M_Product_ID = ? AND M_AttributeSetInstance_ID = ? ", new Object[]{getM_Product_ID(), getM_AttributeSetInstance_ID()}).toString());

		if (newRecord && count > 0) {
			log.saveError("DuplicatedRecord", "");
			return false;
		}
			
		if (getM_AttributeSetInstance_ID() < 1) {
			log.saveError("InvalidAttributeSetInstance", "");
			return false;
		}
		
		MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(), get_TrxName());

		if (! MProductPriceInstance.isValidSetForPriceInstance( getCtx(), asi.getM_AttributeSet_ID(), get_TrxName() ) ) {
			log.saveError("InvalidAttributeSetInstance", "");
			return false;
		}
		
		return super.beforeSave(newRecord);
	}
}

