package test.model;

import java.util.Properties;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_AD_User;
import org.openXpertya.util.Env;
import test.util.TestUtil;

public class AD_UserTest extends GenericTest<X_AD_User>{
	
	@Override
	protected PO getTestingEntity() {
		X_AD_User user = new X_AD_User(Env.getCtx(), 0, null);
        user.setName("Test User " + TestUtil.getFormattedDate());
        user.setPassword("TestPass");
        return user;
	}

	@Override
	protected X_AD_User createNewEntity(Properties ctx, int id, String trxName) {
		return new X_AD_User(ctx, id, trxName);
	}

	@Override
	protected X_AD_User getModifiedEntity(X_AD_User currentEntity) {
		currentEntity.setName("MODIFIED NAME");
		return currentEntity;
	}

	@Override
	protected boolean getUpdateAssertTrue(X_AD_User modifiedEntity) {
		return modifiedEntity.getName().equalsIgnoreCase("MODIFIED NAME");
	}
	
	@Test
	@Order(500)
	public void anotherTest() {
		//este es un test especifico
		System.out.println("Ejecutando test especifico para AD_User");
	}

}
