package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperEmptyDataSource;
import org.openXpertya.cc.CurrentAccountManager;
import org.openXpertya.cc.CurrentAccountManagerFactory;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrg;
import org.openXpertya.reflection.CallResult;

public class LaunchCurrentAccountDocument extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		// Cargar los datos de la entidad comercial
		MBPartner bpartner = new MBPartner(getCtx(),
				(Integer) getParameterValue("C_BPartner_ID"), get_TrxName());
		MInvoice invoice = new MInvoice(getCtx(),
				(Integer) getParameterValue("C_Invoice_ID"), get_TrxName());
		/* Por ahora no se imprimen estos datos
		MOrg org = new MOrg(getCtx(), (Integer) getParameterValue("AD_Org_ID"),
				get_TrxName());
		CurrentAccountManager manager = CurrentAccountManagerFactory.getManager();
		// Obtengo el estado de crédito
		CallResult creditStatus = manager.setCurrentAccountStatus(getCtx(),
				bpartner, org, get_TrxName());
		// Obtengo el saldo de la entidad comercial
		CallResult balance = manager.updateBalance(getCtx(), org, bpartner,
				get_TrxName());
		// Obtengo el límite de la entidad comercial
		CallResult limit = manager.getCreditLimit(getCtx(), org, bpartner,
				(String) null, get_TrxName());
		*/
		// Agregar los parámetros
//		addReportParameter("GLOBAL_BALANCE", balance.getResult());
		addReportParameter("BPARTNER_NAME", bpartner.getValue()+" "+bpartner.getName());
//		addReportParameter("GLOBAL_LIMIT", limit.getResult());
//		addReportParameter("CREDIT_STATUS", JasperReportsUtil.getListName(
//				getCtx(), MBPartner.SOCREDITSTATUS_AD_Reference_ID,
//				(String) creditStatus.getResult()));
		
		// Se cambia por el nombre del esquema de vencimientos
//		addReportParameter("PAYMENTRULE_1", JasperReportsUtil.getListName(
//				getCtx(), MInvoice.PAYMENTRULE_AD_Reference_ID,
//				(String) getParameterValue("PaymentRule_1")));
		addReportParameter("PAYMENTRULE_1",(String) getParameterValue("PaymentRule_1"));
		addReportParameter("PAYMENTRULE_AMT_1", getParameterValue("PaymentRule_Amt_1"));
		addReportParameter("DOCUMENTNO",invoice.getDocumentNo());
		addReportParameter("DATE_INVOICED",invoice.getDateInvoiced());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new OXPJasperEmptyDataSource();
	}

}
