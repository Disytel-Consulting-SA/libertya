package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;

public class MCardSettlementConcepts extends X_C_CardSettlementConcepts {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MCardSettlementConcepts(Properties ctx, int C_CardSettlementConcepts_ID, String trxName) {
		super(ctx, C_CardSettlementConcepts_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public MCardSettlementConcepts(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	/**
	 * Descripción de Método
	 * 
	 * 
	 * @param newRecord
	 * 
	 * @return
	 */
	protected boolean beforeSave(boolean newRecord) {
		// Chequeo del producto para determinar si está habilitado para comercializar
        if(getM_Product_ID() != 0) {
        	MProduct product = new MProduct(getCtx(), getM_Product_ID(),get_TrxName());
        	if(product.ismarketingblocked()) {
        		log.saveError("Error", product.getmarketingblockeddescr());
    			return false;
        	}
        }
		return true;
	}
	

}
