package test.model;

import java.util.Properties;

import org.openXpertya.model.MProduct;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;

public class MProductTest extends GenericTest<MProduct>{
	
	@Override
	protected PO getTestingEntity() {
		
		MProduct product = new MProduct(Env.getCtx(), 0, null);
		product.setName("Test Product");
        return product;
	}

	@Override
	protected MProduct createNewEntity(Properties ctx, int id, String trxName) {
		return new MProduct(ctx, id, trxName);
	}


	@Override
	protected MProduct getModifiedEntity(MProduct currentEntity) {
		currentEntity.setName("MODIFIED PRODUCT");
		return currentEntity;
	}


	@Override
	protected boolean getUpdateAssertTrue(MProduct modifiedEntity) {
		return modifiedEntity.getName().equalsIgnoreCase("MODIFIED PRODUCT");
	}

}
