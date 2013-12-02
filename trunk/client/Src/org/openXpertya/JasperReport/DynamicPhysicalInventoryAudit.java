package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MInventory;
import org.openXpertya.model.MProductLines;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class DynamicPhysicalInventoryAudit extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		// Inventario
		MInventory inventory = new MInventory(ctx,
				((BigDecimal) params.get("M_Inventory_ID")).intValue(), null);
		params.put("INVENTORY_DOCUMENTNO", inventory.getDocumentNo());
		
		// Opción de visualización: Todos o Sólo con diferencia
		params.put("VISUAL_OPTION_DESCRIPTION", MRefList.getListName(ctx,
				getVisualOptionReferenceID(),
				(String) params.get("Visual_Option")));

		// Línea de artículo
		BigDecimal productLinesID = (BigDecimal) params.get("M_Product_Lines_ID");
		if(!Util.isEmpty(productLinesID, true)){
			MProductLines productLines = new MProductLines(ctx,
					productLinesID.intValue(), null);
			params.put("PRODUCT_LINES_VALUE", productLines.getValue());
			params.put("PRODUCT_LINES_NAME", productLines.getName());
		}
		
	}

	
	private Integer getVisualOptionReferenceID(){
		return DB
				.getSQLValue(
						null,
						"SELECT ad_reference_id FROM ad_reference WHERE ad_componentobjectuid = 'CORE-AD_Reference-1010236'");
	}
}
