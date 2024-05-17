package test.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProductPrice;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_User;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import test.util.TestUtil;

public class MProductPriceTest extends GenericTest<MProductPrice>{
	
	protected final int PRODUCT_ID = 1015400;
    protected final int PRICE_LIST_VERSION_ID = 1010527;
	
	@Override
	protected PO getTestingEntity() {
		
		 // Eliminar el precio por las dudas de que ya existiera anteriormente
        DB.executeUpdate("DELETE FROM M_ProductPrice "
        				+ "WHERE M_Product_ID = " + PRODUCT_ID + " AND "
        				+ "M_PriceList_Version_ID = " + PRICE_LIST_VERSION_ID);
		
		MProductPrice pp = new MProductPrice(Env.getCtx(), 0, null);
		
		//obtener producto dinamicamente?
		//M_Table table = M_Table.get(Env.getCtx(), "M_Produt");
		//int[] ids = PO.getAllIDs("M_Produt", "isactive = 'Y' limit 1", null);
		//MProduct prod = new MProduct(Env.getCtx(), ids[0] ,null);
		
		pp.setM_Product_ID(PRODUCT_ID);
	    pp.setM_PriceList_Version_ID(PRICE_LIST_VERSION_ID);
		pp.setPriceLimit(new BigDecimal(500));
	    pp.setPriceList(new BigDecimal(500));
	    pp.setPriceStd(new BigDecimal(500));
		
        return pp;
	}

	@Override
	protected MProductPrice createNewEntity(Properties ctx, int id, String trxName) {
		return new MProductPrice(ctx, id, null);
	}

	@Override
	protected MProductPrice getModifiedEntity(MProductPrice currentEntity) {
		
		currentEntity.setPriceStd(new BigDecimal(704));
		return currentEntity;
	}

	@Override
	protected boolean getUpdateAssertTrue(MProductPrice modifiedEntity) {
		return (modifiedEntity.getPriceStd().compareTo(new BigDecimal(704)) == 0);
	}

}
