package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.util.Util;

public class DynamicSalesDiscounts extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal discountSchemaID = (BigDecimal)params.get("M_DiscountSchema_ID"); 
		if(!Util.isEmpty(discountSchemaID, true)){
			MDiscountSchema ds = MDiscountSchema.get(ctx, discountSchemaID.intValue());
			params.put("DiscountSchema_Name", ds.getName());
		}
	}

}
