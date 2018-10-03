package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.PromotionalCodesDataSource;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.X_C_Promotion_Code_Batch;

public class LaunchPromotionalCodes extends JasperReportLaunch {

	public Integer getPromotionalCodeBatchID(){
		return (Integer)(getParameterValue("C_PROMOTION_CODE_BATCH_ID") != null
				? getParameterValue("C_PROMOTION_CODE_BATCH_ID") : getRecord_ID());
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Lote
		X_C_Promotion_Code_Batch batch = new X_C_Promotion_Code_Batch(getCtx(), getPromotionalCodeBatchID(),
				get_TrxName());
		MOrg org = MOrg.get(getCtx(), batch.getAD_Org_ID());
		addReportParameter("ORGNAME", org.getName());
		
		// Logo
		MClient client = MClient.get(getCtx());
		if(client.getLogoImg() != null){
			InputStream logo = new ByteArrayInputStream(client.getLogoImg());
			addReportParameter("LOGO",(InputStream)logo);
		}
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		PromotionalCodesDataSource pcds = new PromotionalCodesDataSource(getCtx(), getPromotionalCodeBatchID(),
				get_TrxName());
		return pcds;
	}

}
