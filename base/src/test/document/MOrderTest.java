package test.document;

import java.util.Properties;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPriceList;
import org.openXpertya.model.MProductPrice;
import org.openXpertya.model.PO;
import org.openXpertya.util.Env;

public class MOrderTest extends DocumentTest<MOrder>{

	/* Tarifa Standard */
	private final int M_PRICELIST_ID = 1010596;
	
	/* Articulo Standard*/
	private final int M_PRODUCT_ID = 1015400;
	
	
	
	@Override
	protected PO getTestingEntity() {
		
		setUpEnvironment();
		
		
		MOrder order = new MOrder(Env.getCtx(), 0, null);
		
		
		
		
		return order;
	}

	@Override
	protected MOrder createNewEntity(Properties ctx, int id, String trxName) {
		return new MOrder(Env.getCtx(), id, null);
	}

	@Override
	protected MOrder getModifiedEntity(MOrder currentEntity) {
		return null;
	}

	@Override
	protected boolean getUpdateAssertTrue(MOrder modifiedEntity) {
		return false;
	}

	
	
//	=====================================================
//	=====================  CUSTOM  ======================
//	=====================================================
	
	
	/* Establecer valores y entidades relacionadas a tarifas y articulos:
	 * Se debe contar con un articulo asociado a la tarifa actual para poder 
	 * persistir Order Lines */
	private void setUpEnvironment() {
		
		
		new MPriceList(Env.getCtx(), M_PRICELIST_ID, null);
		
		//debe existir un MProductPrice que asocie el articulo con la tarifa
		//para poder guardar la linea del pedido
		new MProductPrice(Env.getCtx(), M_PRICELIST_ID, M_PRODUCT_ID,null);
		
		
		
	}
	
	
	

}
