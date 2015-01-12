package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.InvoicesWithoutInOutDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.Util;

public class LaunchInvoicesWithoutInOut extends JasperReportLaunch {

	public LaunchInvoicesWithoutInOut() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadReportParameters() throws Exception {
		// Organización
		if(getOrgID() > 0){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// Fechas
		if(getDateFrom() != null)
			addReportParameter("DATE_FROM", getDateFrom());
		if(getDateTo() != null)
			addReportParameter("DATE_TO", getDateTo());
		// EC
		if(!Util.isEmpty(getBPartnerID(), true)){
			MBPartner bPartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("BP_VALUE", bPartner.getValue());
			addReportParameter("BP_NAME", bPartner.getName());
		}
		// Factura
		if(!Util.isEmpty(getInvoiceID(), true)){
			MInvoice invoice = new MInvoice(getCtx(), getInvoiceID(), get_TrxName());
			addReportParameter("DOCUMENTNO", invoice.getDocumentNo());
		}
		// Transacción de ventas
		addReportParameter("ISSOTRX", getIsSOTrx().equals("Y"));
	}

	protected Integer getOrgID(){
		return (Integer) getParameterValue("AD_Org_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("Date_To");
	}
	
	protected Integer getBPartnerID(){
		return (Integer) getParameterValue("C_BPartner_ID");
	}
	
	protected Integer getInvoiceID(){
		return (Integer) getParameterValue("C_Invoice_ID");
	}
	
	protected String getIsSOTrx(){
		return (String) getParameterValue("IsSOTrx");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new InvoicesWithoutInOutDataSource(getCtx(), getOrgID(),
				getBPartnerID(), getInvoiceID(), getDateFrom(), getDateTo(),
				getIsSOTrx().equals("Y"), get_TrxName());
	}

}
