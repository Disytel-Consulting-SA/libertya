package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.apache.tools.ant.taskdefs.condition.IsSet;
import org.openXpertya.util.DB;

public class MProductPriceInstance extends X_M_ProductPriceInstance {

	public MProductPriceInstance(Properties ctx, int M_ProductPriceInstance_ID, String trxName) {
		super(ctx, M_ProductPriceInstance_ID, trxName);
	}

	public MProductPriceInstance(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public static boolean isValidSetForPriceInstance(Properties ctx, int M_AttributeSet_ID, String trxName) {
		MAttributeSet as = new MAttributeSet(ctx, M_AttributeSet_ID, trxName); 
		
		if (as.isSerNo() || as.isSerNoMandatory())
			return false;
		
		if (as.isLot() || as.isLotMandatory())
			return false;
		
		if (as.isInstanceUniquePerUnit())
			return false;
		
		return true;
	}
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		int count = Integer.parseInt(DB.getSQLObject(get_TrxName(), "SELECT COUNT(*) FROM " + Table_Name + " WHERE m_pricelist_version_id = ? AND M_Product_ID = ? AND M_AttributeSetInstance_ID = ? ", new Object[]{getM_PriceList_Version_ID(), getM_Product_ID(), getM_AttributeSetInstance_ID()}).toString());

		if (newRecord && count > 0) {
			log.saveError("DuplicatedRecord", "");
			return false;
		}
		
		if (getPriceLimit() == null || getPriceLimit().signum() < 1) {
			log.saveError("PriceUnderZero", "");
			return false;
		}
		
		if (getPriceList() == null || getPriceList().signum() < 1) {
			log.saveError("PriceUnderZero", "");
			return false;
		}
		
		if (getPriceStd() == null || getPriceStd().signum() < 1) {
			log.saveError("PriceUnderZero", "");
			return false;
		}

		if (getM_AttributeSetInstance_ID() < 1) {
			log.saveError("InvalidAttributeSetInstance", "");
			return false;
		}
		
		MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(), get_TrxName());

		if (! isValidSetForPriceInstance( getCtx(), asi.getM_AttributeSet_ID(), get_TrxName() ) ) {
			log.saveError("InvalidAttributeSetInstance", "");
			return false;
		}
		
		return super.beforeSave(newRecord);
	}
	
	/**
	 * Constructor
	 */
	public MProductPriceInstance(Properties ctx, int M_PriceList_Version_ID, int M_Product_ID, int M_AttributeSetInstance_ID, String trxName) {
		this( ctx,0,trxName );
        setM_PriceList_Version_ID( M_PriceList_Version_ID );    // FK
        setM_Product_ID( M_Product_ID );                        // FK
        setM_AttributeSetInstance_ID(M_AttributeSetInstance_ID);// FK
	}
	
    public void changeOrg(int AD_Org_ID) {
    	set_ValueNoCheck("AD_Org_ID", AD_Org_ID);
    }
}
