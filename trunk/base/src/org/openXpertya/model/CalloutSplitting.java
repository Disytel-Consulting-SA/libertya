package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public class CalloutSplitting extends CalloutEngine {

	/**
	 * Callout de Artículo a fraccionar
	 * @param ctx
	 * @param windowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String sourceProduct(Properties ctx, int windowNo, MTab mTab, MField mField, Object value) {
        Integer productID = (Integer)value;
        if (productID != null && productID > 0) {
        	MProduct product = MProduct.get(ctx, productID);
        	// Se asigna la UM del artículo
        	mTab.setValue("C_UOM_ID", product.getC_UOM_ID());
        	// Se asigna la UM de conversión por defecto si el artículo tiene UMs
        	Set<Integer> conversionUOMs = product.getUOMConversions().keySet();
        	if (!conversionUOMs.isEmpty()) {
        		mTab.setValue("C_Conversion_UOM_ID", conversionUOMs.iterator().next());
        	}
        }
        // Cuando el artículo cambia se resetean las cantidades
        mTab.setValue("ShrinkQty", BigDecimal.ZERO);
        mTab.setValue("SplitQty", BigDecimal.ZERO);
        mTab.setValue("ConvertedShrinkQty", BigDecimal.ZERO);
        mTab.setValue("ConvertedSplitQty", BigDecimal.ZERO);
        return "";
	}
	
	/**
	 * Callout del artículo destino de fraccionamiento (línea de fraccionamiento)
	 * @param ctx
	 * @param windowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String targetProduct(Properties ctx, int windowNo, MTab mTab, MField mField, Object value) {
		Integer productID = (Integer)value;
		if (productID != null && productID > 0) {
        	MProduct product = MProduct.get(ctx, productID);
        	// Se asigna la UM del artículo
        	mTab.setValue("C_UOM_ID", product.getC_UOM_ID());
        }
		return "";
	}

	
	/**
	 * Callout de la cantidad del artículo destino (línea de fraccionamiento)
	 * @param ctx
	 * @param windowNo
	 * @param mTab
	 * @param mField
	 * @param value
	 * @return
	 */
	public String productQty(Properties ctx, int windowNo, MTab mTab, MField mField, Object value) {
		Integer splittingID = (Integer) mTab.getValue("M_Splitting_ID");
		Integer productToID = (Integer) mTab.getValue("M_Product_To_ID");
		BigDecimal productQty = (BigDecimal) mTab.getValue("ProductQty");
		BigDecimal convertedQty = BigDecimal.ZERO;
		// Se calcula la cantidad convertida y se asigna al campo de la pestaña.
		if (splittingID != null && productToID != null && productToID > 0) {
			MSplitting splitting = new MSplitting(ctx, splittingID, null);
			convertedQty = splitting.convertToConversionUOM(productQty, productToID);
		}
		mTab.setValue("ConvertedQty", convertedQty);
		return "";
	}
	
	public String warehouse(Properties ctx, int windowNo, MTab mTab, MField mField, Object value) {
		Integer warehouseID = (Integer) mTab.getValue("M_Warehouse_ID");
		Integer locatorID = null;
		
		if (warehouseID != null && warehouseID > 0) {
			MWarehouse warehouse = MWarehouse.get(ctx, warehouseID);
			MLocator[] locators = warehouse.getLocators(false);
			if (locators.length == 1) {
				locatorID = locators[0].getM_Locator_ID();
			}
		}
		mTab.setValue("M_Locator_ID", locatorID);
		return "";
	}
}
