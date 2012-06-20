package org.openXpertya.JasperReport;

import java.util.Date;

import org.openXpertya.JasperReport.DataSource.LibroIVADataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MOrder;

public class LaunchLibroIVA extends JasperReportLaunch {
	
	private LibroIVADataSource ds;
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return ds;
	}

	@Override
	protected void loadReportParameters() throws Exception {
		int orgID = getParameterValue("AD_Org_ID") == null ? 0 : (Integer)getParameterValue("AD_Org_ID");
		MOrder order = new MOrder(getCtx(), 0, null);
		ds = new LibroIVADataSource(this.get_TrxName(), getCtx(), (Date)getParameterValue("DateFrom"), 
									(Date)getParameterValue("DateTo"), (String)getParameterValue("TransactionType"), orgID);
		ds.calculateTotals();
		this.addReportParameter("DateFrom", (Date)getParameterValue("DateFrom"));
		this.addReportParameter("DateTo", (Date)getParameterValue("DateTo"));
		this.addReportParameter("Transactiontype", (String)getParameterValue("TransactionType"));
		this.addReportParameter("totalNeto", ds.getNeto());
		this.addReportParameter("totalFacturado", ds.getTotalFacturado());
		this.addReportParameter("Compania", (MClient.get(getCtx())).getName());
		this.addReportParameter("Localizacion", ds.getLocalizacion(order));
	}
	
}