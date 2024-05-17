package test.model;

import org.openXpertya.model.PO;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trx;

import common.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Properties;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.LoggerFactory;

import test.util.StartupLY;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class GenericTest<T extends PO> {
	
	
	@BeforeAll
	static void setUpTestingEnvironment() throws Exception {
		StartupLY.getInstance().init();
	}
	
	@AfterAll
	static void finish() {
		//
	}
	
	//cada entidad implementa el metodo para obtener la entidad a crear (CRUD)
	protected abstract PO getTestingEntity();
	
	//creación especifica para clase T
	//No se puede implementar con genéricos
	//ej: return new MProduct(Env.getCtx(), entityID, testingTrxName);
	protected abstract T createNewEntity(Properties ctx, int id, String trxName);
	
	//modificación específica de entidad para testing de UPDATE
	protected abstract T getModifiedEntity(T currentEntity);
	
	//condición (true) a cumplir con la entidad modificada para que el test sea exitoso
	//ej: return modifiedEntity.getName().equals(nombreModificado)
	protected abstract boolean getUpdateAssertTrue(T modifiedEntity);
	
	//ID resultado de la operación CREATE
	int entityID = -1;
	
	
//	=====================================================
//	===================  CREATE  ========================
//	=====================================================
	
	@Test
	@Order(1)
	@DisplayName("Create Entity ok")
	void createEntityShouldReturnOK() {
        T entity = (T)getTestingEntity();
        entity.save();
        entityID = entity.getID();
        assertTrue(entityID > 0);
	}
	
//	=====================================================
//	====================  READ  =========================
//	=====================================================
	
	@Test
	@Order(2)
	@DisplayName("Retrieve created entity")
	void retrieveCreatedEntityShouldReturnOK() {
		T createdEntity = createNewEntity(Env.getCtx(), entityID, null);
		assertEquals(createdEntity.getID(), entityID);
	}
	
//	======================================================
//	====================  UPDATE  ========================
//	======================================================
	
	@Test
	@Order(3)
	@DisplayName("Modify created product")
	void modifyEntityShouldReturnOK() {
		
		T createdEntity = getModifiedEntity(createNewEntity(Env.getCtx(), entityID, null));
		createdEntity.save();
		
		T modifiedEntity = createNewEntity(Env.getCtx(), entityID, null);
		assertTrue(getUpdateAssertTrue(modifiedEntity));
	}
	
//	=====================================================
//	=====================  DELETE  ======================
//	=====================================================
	
	
	@Test
    @Order(5)
    void deleteEntityShouldReturnOK() {
		
		T createdEntity = createNewEntity(Env.getCtx(), entityID, null);
		createdEntity.delete(false);
		
		T deletedEntity = createNewEntity(Env.getCtx(), entityID, null);
        assertTrue(deletedEntity == null);
    }
	
}
