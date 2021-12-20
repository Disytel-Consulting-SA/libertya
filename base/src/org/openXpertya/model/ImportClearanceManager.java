package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Env;

public class ImportClearanceManager {

	public static boolean isImportClearanceActive(Properties ctx) {
		return Env.getContext(ctx, "#ImportClearanceActive").equals("Y");
	}
	
	public static ImportClearanceProcessing getImportClearanceProcessingClass(boolean incrementStock) {
		return incrementStock?new IncrementStockClearanceProcessing():new DecrementStockClearanceProcessing();
	}

}
