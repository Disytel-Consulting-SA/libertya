package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MDiscountSchema;
import org.openXpertya.model.MUser;
import org.openXpertya.util.Util;

public class DynamicUpdatedDiscountSchemas extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		// Regla de Precios
		BigDecimal discountSchemaID = (BigDecimal)params.get("M_DiscountSchema_ID");
		if(!Util.isEmpty(discountSchemaID, true)){
			MDiscountSchema discountSchema = MDiscountSchema.get(ctx, discountSchemaID.intValue());
			params.put("DiscountSchema_Name", discountSchema.getName());
		}
		// Usuario
		BigDecimal userID = (BigDecimal)params.get("AD_User_ID");
		if(!Util.isEmpty(userID, true)){
			MUser user = MUser.get(ctx, userID.intValue());
			params.put("UpdatedBy_Name", user.getName());
			params.put("UpdatedBy_Description", user.getDescription());
		}

	}

}
