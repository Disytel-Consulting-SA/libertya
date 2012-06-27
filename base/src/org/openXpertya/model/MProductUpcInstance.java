package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MProductUpcInstance extends X_M_Product_Upc_Instance {

	public MProductUpcInstance(Properties ctx, int M_Product_Upc_Instance_ID, String trxName) {
		super(ctx, M_Product_Upc_Instance_ID, trxName);
	}
	
	public MProductUpcInstance(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
     * Constructor de la clase ...
     *
     *
     * @param impPI
     */

    public MProductUpcInstance( X_I_ProductInstance impPI ) {
        this( impPI.getCtx(),0,impPI.get_TrxName());
        setClientOrg( impPI );
        setUpdatedBy( impPI.getUpdatedBy());       

        //
        setM_Product_ID(impPI.getM_Product_ID());
        setM_AttributeSetInstance_ID(impPI.getM_AttributeSetInstance_ID());
        setUPC(impPI.getUPC());
        setName(impPI.getInstance_Description());
        
        
    }  
    
	@Override
	protected boolean beforeSave(boolean newRecord) {
		
		int count = Integer.parseInt(DB.getSQLObject(get_TrxName(), "SELECT COUNT(*) FROM " + Table_Name + " WHERE M_Product_ID = ? AND M_AttributeSetInstance_ID = ? ", new Object[]{getM_Product_ID(), getM_AttributeSetInstance_ID()}).toString());

		if (newRecord && count > 0) {
			log.saveError("DuplicatedRecord", "");
			return false;
		}
		
		if (!validateUniqueUPCInstance()) {
			return false;
		}
		
		if (!validateUniqueUPCInstanceProduct()) {
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
	
	private boolean validateUniqueUPCInstance() {
		String sql = 
			"SELECT M_Product_ID FROM M_Product_Upc_Instance " +
			"WHERE AD_Client_ID = ? AND UPC = ? AND M_Product_UPC_INSTANCE_ID <> ? ";
		Integer productID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
				new Object[] { getAD_Client_ID(), getUPC(), getM_Product_Upc_Instance_ID()});
		if (productID != null && productID > 0) {
			MProduct product = MProduct.get(getCtx(), productID);
			String productStr = "'" + product.getValue() + " " + product.getName() + "'";
			log.saveError("SaveError", 
					Msg.translate(getCtx(), "DuplicateUPCError") + " " + productStr);
		}
		return productID == null || productID == 0;
	}
	
	private boolean validateUniqueUPCInstanceProduct() {
		String sql = 
				"SELECT M_Product_ID FROM M_Product " +
				"WHERE AD_Client_ID = ? AND UPC = ? ";
		Integer	productID = (Integer)DB.getSQLObject(get_TrxName(), sql, 
					new Object[] { getAD_Client_ID(), getUPC()});
		if (productID != null && productID > 0) {
			MProduct product = MProduct.get(getCtx(), productID);
			String productStr = "'" + product.getValue() + " " + product.getName() + "'";
			log.saveError("SaveError", 
					Msg.translate(getCtx(), "DuplicateUPCError") + " " + productStr);
		}
		return productID == null || productID == 0;
	}
}

