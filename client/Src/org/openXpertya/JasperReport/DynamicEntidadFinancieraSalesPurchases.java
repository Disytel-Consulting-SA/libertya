package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MEntidadFinanciera;
import org.openXpertya.util.Util;

public class DynamicEntidadFinancieraSalesPurchases extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		// EC relacionada a la EF
		BigDecimal bpartnerID = (BigDecimal)params.get("C_BPartner_ID");
		if(!Util.isEmpty(bpartnerID, true)){
			MBPartner bPartner = new MBPartner(ctx, bpartnerID.intValue(), null);
			params.put("EntidadFinancieraValue", bPartner.getValue());
			params.put("EntidadFinancieraName", bPartner.getName());
		}
		// EF
		BigDecimal entidadFinancieraID = (BigDecimal) params
				.get("M_EntidadFinanciera_ID");
		if(!Util.isEmpty(entidadFinancieraID, true)){
			MEntidadFinanciera entidadFinanciera = new MEntidadFinanciera(ctx,
					entidadFinancieraID.intValue(), null);
			params.put("EntidadFinancieraValue", entidadFinanciera.getValue());
			params.put("EntidadFinancieraName", entidadFinanciera.getName());
		}
	}

}
