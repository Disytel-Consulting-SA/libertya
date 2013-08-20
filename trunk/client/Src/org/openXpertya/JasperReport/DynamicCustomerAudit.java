package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MBPGroup;
import org.openXpertya.util.Util;

public class DynamicCustomerAudit extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		BigDecimal bpGroup = (BigDecimal)params.get("C_BP_Group_ID"); 
		if(!Util.isEmpty(bpGroup, true)){
			MBPGroup group = MBPGroup.get(ctx, bpGroup.intValue());
			params.put("Group_Value", group.getValue());
			params.put("Group_Name", group.getName());
		}

	}

}
