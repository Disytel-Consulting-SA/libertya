package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MOrg;

public class DynamicControlVentasXCierreZ extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		if (params.get("AD_Org_ID") != null && ((BigDecimal) params.get("AD_Org_ID")).compareTo(BigDecimal.ZERO) != 0) {
			MOrg org = MOrg.get(ctx, ((BigDecimal)params.get("AD_Org_ID")).intValue());		
			params.put("ORG_VALUE", org.getValue());
			params.put("ORG_NAME", org.getName());
		}
	}

}
