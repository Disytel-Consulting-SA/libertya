package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.util.Util;

public class DynamicInvoicesByCreditCardOwner extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal bPartnerID = (BigDecimal)params.get("C_BPartner_ID"); 
		if(!Util.isEmpty(bPartnerID, true)){
			MBPartner bPartner = new MBPartner(ctx, bPartnerID.intValue(), null);
			params.put("BPartner_Name", bPartner.getName());
		}
	}

}
