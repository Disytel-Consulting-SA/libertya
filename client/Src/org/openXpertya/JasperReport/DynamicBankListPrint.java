package org.openXpertya.JasperReport;

import java.util.Map;
import java.util.Properties;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.model.MBankList;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.X_C_BankAccount;

public class DynamicBankListPrint extends DynamicJasperReport {

	@Override
	public void addReportParameters(Properties ctx, Map<String, Object> params) {
		MBankList bl = new MBankList(ctx, (Integer) params.get("RECORD_ID"), null);
		MOrg org = MOrg.get(ctx, bl.getAD_Org_ID());
		MDocType dt = MDocType.get(ctx, bl.getC_DocType_ID());
		
		params.put("ORG_VALUE", org.getValue());
		params.put("ORG_NAME", org.getName());
		params.put("DOCTYPE_NAME", dt.getName());
		params.put("DOCUMENTNO", bl.getDocumentNo());
		params.put("DATETRX", bl.getDateTrx());
		params.put("DESCRIPTION", bl.getDescription());
		params.put("BANKACCOUNT", JasperReportsUtil.getPODisplayByIdentifiers(ctx, bl.getC_BankAccount_ID(),
				X_C_BankAccount.Table_ID, null));
		params.put("TOTAL", bl.getBankListTotal());
		params.put("DOCSTATUS", bl.getDocStatus());
	}

}
