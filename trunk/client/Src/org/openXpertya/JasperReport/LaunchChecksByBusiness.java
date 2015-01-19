package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.ChecksByBusinessDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.Util;

public class LaunchChecksByBusiness extends LaunchChecksByAccount {


	@Override
	protected void loadReportParameters() throws Exception {
		super.loadReportParameters();
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// Entidad Comercial
		if(!Util.isEmpty(getBPartnerID())){
			MBPartner bPartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("BP_VALUE", bPartner.getValue());
			addReportParameter("BP_NAME", bPartner.getName());
		}
		// CUIT
		if(!Util.isEmpty(getTaxID(), true)){
			addReportParameter("CUIT", getTaxID());
		}
		// Sólo cuenta de cheques en cartera
		addReportParameter("ONLY_CHEQUE_CARTERA", getOnlyChequesEnCartera());
	}

	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Integer getBPartnerID(){
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	protected String getTaxID(){
		return (String)getParameterValue("TaxID");
	}
	
	protected String getOnlyChequesEnCartera(){
		return (String)getParameterValue("OnlyChequesEnCartera");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ChecksByBusinessDataSource(getCtx(), getOrgID(),
				getBPartnerID(), getTaxID(), getBankAccountID(), getDateFrom(),
				getDateTo(), getDateOrder(), getOnlyChequesEnCartera().equals("Y"),
				get_TrxName());
	}
	
	
}
