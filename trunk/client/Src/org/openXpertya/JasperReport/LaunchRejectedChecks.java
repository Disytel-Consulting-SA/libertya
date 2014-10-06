package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.RejectedChecksDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchRejectedChecks extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// Entidad Comercial
		if(!Util.isEmpty(getBPartnerID(), true)){
			MBPartner bPartner = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("BPARTNER_VALUE", bPartner.getValue());
			addReportParameter("BPARTNER_NAME", bPartner.getName());
		}
		// Fechas
		if(getDateFrom() != null){
			addReportParameter("DATE_FROM", getDateFrom());
		}
		if(getDateTo() != null){
			addReportParameter("DATE_TO", getDateTo());
		}
		// Estado del documento
		if(!Util.isEmpty(getDocStatus(), true)){
			addReportParameter("DOCSTATUS_VALUE", getDocStatus());
			addReportParameter("DOCSTATUS_NAME", MRefList.getListName(getCtx(),
					MPayment.DOCSTATUS_AD_Reference_ID, getDocStatus()));
		}
		// Estado del cheque
		if(!Util.isEmpty(getCheckStatus(), true)){
			addReportParameter("CHECKSTATUS_VALUE", getCheckStatus());
			addReportParameter("CHECKSTATUS_NAME", MRefList.getListName(getCtx(),
					MPayment.CHECKSTATUS_AD_Reference_ID, getCheckStatus()));
		}
		// Transacción de ventas
		addReportParameter("ISRECEIPT", getIsReceipt());
		addReportParameter("RECEIPT_MSG", Msg.getMsg(getCtx(),
				(getIsReceipt().equals("Y") ? "SalesTransaction"
						: "PurchasesTransaction")));
	}
	
	protected Integer getOrgID() {
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}

	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("Date_TO");
	}
	
	protected String getDocStatus(){
		return (String)getParameterValue("DocStatus");
	}
	
	protected String getCheckStatus(){
		return (String)getParameterValue("CheckStatus");
	}
	
	protected String getIsReceipt(){
		return (String)getParameterValue("IsReceipt");
	}
	
	protected Integer getBPartnerID() {
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new RejectedChecksDataSource(getCtx(), getOrgID(),
				getBPartnerID(), getDateFrom(), getDateTo(), getDocStatus(),
				getCheckStatus(), getIsReceipt(), get_TrxName());
	}

}
