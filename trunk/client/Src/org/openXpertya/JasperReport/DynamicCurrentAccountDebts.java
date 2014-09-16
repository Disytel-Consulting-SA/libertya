package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPGroup;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.Util;

public class DynamicCurrentAccountDebts extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal bpGroup = (BigDecimal)params.get("C_BP_Group_ID"); 
		if(!Util.isEmpty(bpGroup, true)){
			MBPGroup group = MBPGroup.get(ctx, bpGroup.intValue());
			params.put("BP_GROUP_VALUE", group.getValue());
			params.put("BP_GROUCurrent Account Debts/** UID del Informe de Cambio de Reglas de Precios */
	protected final static String UPDATED_DISCOUNT_SCHEMAS_REPORT_UID = "CORE-AD_Process-1010392";
	protected final static String UPDATED_DISCOUNT_SCHEMAS_REPORT_FILENAME = "UpdatedDiscountSchemas.jrxml";CurrentAccountDebtsP_NAME", group.getName());
		}
		BigDecimal orgID = (BigDecimal)params.get("AD_Org_ID"); 
		if(!Util.isEmpty(orgID, true)){
			MOrg org = MOrg.get(ctx, orgID.intValue());
			params.put("ORG_VALUE", org.getValue());
			params.put("ORG_NAME", org.getName());
		}
	}

}
