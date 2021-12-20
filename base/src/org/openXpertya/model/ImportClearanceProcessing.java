package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Util;

/**
 * Procesamiento de despachos de importación
 */
public abstract class ImportClearanceProcessing {

	public abstract List<MImportClearance> getImportClearances(Properties ctx, Integer productID, String trxName);
	public abstract BigDecimal getQtyToCompare(MImportClearance mic);
	public abstract BigDecimal getNewQtyUsed(MImportClearance mic, BigDecimal qty);
	public abstract BigDecimal getRealQty(BigDecimal qty);
	
	public void applyInventoryImportClearanceQty(Properties ctx, Integer inventoryLineID, Integer productID,
			BigDecimal qty, String trxName) throws Exception {
		List<MImportClearance> mics = getImportClearances(ctx, productID, trxName); 
    	// Itero y aplico
    	for (int i = 0; i < mics.size() && qty.compareTo(BigDecimal.ZERO) > 0; i++) {
			qty = doIt(qty, mics.get(i), inventoryLineID);
		}
	}
	
	public BigDecimal doIt(BigDecimal qty, MImportClearance mic, Integer inventoryLineID) throws Exception {
		BigDecimal newQty = getQtyToCompare(mic).compareTo(qty) <= 0 ? getQtyToCompare(mic) : qty;
		mic.setQtyUsed(getNewQtyUsed(mic, newQty));
		if(!mic.save()) {
			throw new Exception(CLogger.retrieveErrorAsString());
		}
		// Guardar la asociación entre la línea de inventario y el despacho
		saveImportClearanceInventory(mic, inventoryLineID, getRealQty(newQty));
		qty = qty.subtract(newQty);
		return qty;
	}
	
	/**
	 * Guardar la relación entre el despacho de importación y la línea de inventario
	 * 
	 * @param importClearance   despacho de importación
	 * @param inventoryLineID   id de línea de inventario, null o 0 si no se debe
	 *                          guardar relación
	 * @param qty               cantidad a registrar
	 */
	protected void saveImportClearanceInventory(MImportClearance importClearance, Integer inventoryLineID, BigDecimal qty) throws Exception {
		if(!Util.isEmpty(inventoryLineID, true)) {
			X_M_Import_Clearance_Inventory ici = new X_M_Import_Clearance_Inventory(importClearance.getCtx(), 0,
					importClearance.get_TrxName());
			ici.setM_Import_Clearance_ID(importClearance.getID());
			ici.setM_InventoryLine_ID(inventoryLineID);
			ici.setQty(qty);
			if(!ici.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}
	}
}
