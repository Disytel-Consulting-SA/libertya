package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_M_PriceList;
import org.openXpertya.model.X_M_PriceList_Version;

public class DynamicPriceChangeReport extends DynamicJasperReport {
	
	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		// Tarifa
		X_M_PriceList pl = new X_M_PriceList(ctx, ((BigDecimal)params.get("M_PriceList_ID")).intValue(), null);
		if(pl != null) {
			params.put("PriceList_Name", pl.getName());
		}
		// Versión de Lista 1
		X_M_PriceList_Version plv1 = new X_M_PriceList_Version(ctx, ((BigDecimal)params.get("M_PriceList_Version_From_ID")).intValue(), null);
		if(plv1 != null) {
			params.put("PriceList_Version_1_Name", plv1.getName());
		}
		// Versión de Lista 2
		X_M_PriceList_Version plv2 = new X_M_PriceList_Version(ctx, ((BigDecimal)params.get("M_PriceList_Version_To_ID")).intValue(), null);
		if(plv2 != null) {
			params.put("PriceList_Version_2_Name", plv2.getName());
		}
		// EC
		if (params.get("C_BPartner_ID") != null
				&& ((BigDecimal) params.get("C_BPartner_ID")).compareTo(BigDecimal.ZERO) > 0) {
			X_C_BPartner bp = new X_C_BPartner(ctx, ((BigDecimal)params.get("C_BPartner_ID")).intValue(), null);
			params.put("VendorValue", bp.getValue());
			params.put("VendorName", bp.getName());
		}
	}

}
