package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MCreditCardClose;
import org.openXpertya.util.Util;

public class DynamicCreditCardCloseReport extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		Integer credit_id = (Integer) params.get("RECORD_ID"); 
		if(!Util.isEmpty(credit_id, true)){
			MCreditCardClose creditCardClose = new MCreditCardClose(ctx,credit_id,null);
			params.put("AD_Org_ID", new BigDecimal(creditCardClose.getAD_Org_ID()));
			params.put("FechaCierre", creditCardClose.getDateTrx());
		}
	}
}
