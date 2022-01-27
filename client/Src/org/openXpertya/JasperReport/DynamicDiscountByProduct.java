package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.util.Util;

public class DynamicDiscountByProduct extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal bpartnerID = (BigDecimal)params.get("C_BPartner_ID");
		if(!Util.isEmpty(bpartnerID, true)){
			MBPartner bPartner = new MBPartner(ctx, bpartnerID.intValue(), null);
			params.put("VendorValue", bPartner.getValue());
			params.put("VendorName", bPartner.getName());
		}		
	}

}
