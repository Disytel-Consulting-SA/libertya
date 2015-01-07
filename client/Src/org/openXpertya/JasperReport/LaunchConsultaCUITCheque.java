package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.ConsultaCUITChequeDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MCheckCuitControl;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.Util;

public class LaunchConsultaCUITCheque extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// CUIT
		addReportParameter("CUIT", getCUIT());
		// Límit Global
		MCheckCuitControl cuitChequeGlobal = MCheckCuitControl.get(getCtx(), 0,
				getCUIT(), get_TrxName());
		if(cuitChequeGlobal != null){
			addReportParameter("GLOBAL_LIMIT", cuitChequeGlobal.getCheckLimit());	
		}
	}
	
	protected Integer getOrgID(){
		return (Integer) getParameterValue("AD_Org_ID");
	}
	
	protected String getCUIT(){
		return (String) getParameterValue("TaxID");
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ConsultaCUITChequeDataSource(getCtx(), getOrgID(),
				getCUIT(), get_TrxName());
	}

}
