package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MWarehouse;
import org.openXpertya.util.Util;

public class DynamicStockValued extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal warehouseID = (BigDecimal)params.get("M_Warehouse_ID");
		if(!Util.isEmpty(warehouseID, true)){
			MWarehouse warehouse = MWarehouse.get(ctx, warehouseID.intValue());
			params.put("Warehouse_Value", warehouse.getValue());
			params.put("Warehouse_Name", warehouse.getName());
		}
	}

}
