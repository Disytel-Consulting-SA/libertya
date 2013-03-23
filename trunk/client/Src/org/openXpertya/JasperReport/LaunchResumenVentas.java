package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperEmptyDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasBPGroupDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasCategoriaIVADataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasCurrentAccountPaymentsDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasDocTypeDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasPaymentMediumDataSource;
import org.openXpertya.JasperReport.DataSource.ResumenVentasTenderTypeDataSource;
import org.openXpertya.model.MPOS;
import org.openXpertya.model.MUser;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class LaunchResumenVentas extends JasperReportLaunch {

	public LaunchResumenVentas() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadReportParameters() throws Exception {
		Integer orgID = getOrgID();
		Timestamp dateFrom = getDateFrom();
		Timestamp dateTo = getDateTo();
		addReportParameter("TITLE", getTitle());
		addReportParameter("DATE_FROM", dateFrom);
		addReportParameter("DATE_TO", dateTo);
		addReportParameter("ORG_NAME",
				JasperReportsUtil.getOrgName(getCtx(), orgID));
		addReportParameter("SHOW_CURRENTACCOUNTPAYMENTS",
				isShowCurrentAccountPayments());
		Integer posID = getPosID();
		if(!Util.isEmpty(posID, true)){
			MPOS pos = MPOS.get(getCtx(), posID);
			addReportParameter("POS_NAME", pos.getName());
		}
		Integer userID = getUserID();
		if(!Util.isEmpty(userID, true)){
			MUser user = MUser.get(getCtx(), userID);
			addReportParameter("USER_NAME",
					user.getName()
							+ (Util.isEmpty(user.getDescription(), true) ? ""
									: " - " + user.getDescription()));
		}
		// Obtener los subreportes junto con sus datasources
		// Datasources
		//////////////////////////////////////////////////////
		// Data Source de Total por condiciones de ventas
		ResumenVentasDataSource tenderTypeDS = getTenderTypeDataSource();
		// Data Source desagregado por condiciones de ventas
		ResumenVentasDataSource paymentMediumDS = getPaymentMediumDataSource();
		// Data Source de Totales por Categoría de IVA
		ResumenVentasDataSource categoriaIVADS = getCategoriaIVADataSource();
		// Data Source de Totales por Tipo de Comprobante
		ResumenVentasDataSource docTypeDS = getDocTypeDataSource();
		// Data Source de Totales por Repartición de cliente
		ResumenVentasDataSource bpGroupDS = getBPGroupDataSource();
		// Data Source con Cobros en cuenta corriente
		ResumenVentasDataSource currentAccountPaymentsDS = getCurrentAccountPaymentsDataSource();
		//////////////////////////////////////////////////////
		// Subreporte 
		MJasperReport subreport = getResumenVentasSubreport();
		// Se agrega el informe compilado como parámetro.
		addReportParameter("COMPILED_SUBREPORT", new ByteArrayInputStream(
				subreport.getBinaryData()));
		// Se agregan los datasources de los subreportes
		addReportParameter("SUBREPORT_TENDERTYPE", tenderTypeDS);
		addReportParameter("SUBREPORT_PAYMENTMEDIUM", paymentMediumDS);
		addReportParameter("SUBREPORT_CATEGORIAIVA", categoriaIVADS);
		addReportParameter("SUBREPORT_DOCTYPE", docTypeDS);
		addReportParameter("SUBREPORT_BPGROUP", bpGroupDS);
		addReportParameter("SUBREPORT_CURRENTACCOUNTPAYMENTS", currentAccountPaymentsDS);
	}
	
	protected String getTitle(){
		String adMessage = "SalesDailyReport";
		if (!Util.isEmpty(getPosID(), true) || !Util.isEmpty(getUserID(), true)) {
			adMessage = "SalesDailyReportPOSJournalUser";	
		}
		return Msg.getMsg(getCtx(), adMessage);
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("DateFrom");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateFrom_To");
	}
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Boolean isShowCurrentAccountPayments(){
		return (Boolean)getParameterValue("ShowCurrentAccountPayments").equals("Y");
	}
	
	protected Integer getPosID(){
		return (Integer)getParameterValue("C_POS_ID");
	}
	
	protected Integer getUserID(){
		return (Integer)getParameterValue("AD_User_ID");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new OXPJasperEmptyDataSource();
	}
	
	protected ResumenVentasDataSource loadDSData(ResumenVentasDataSource ds) throws Exception{
		ds.loadData();
		return ds;
	}
	
	protected ResumenVentasDataSource getTenderTypeDataSource() throws Exception{
		ResumenVentasTenderTypeDataSource tenderTypeDS = new ResumenVentasTenderTypeDataSource(
				get_TrxName(), getCtx(), getOrgID(), getDateFrom(), getDateTo(), getPosID(), getUserID());
		tenderTypeDS = (ResumenVentasTenderTypeDataSource)loadDSData(tenderTypeDS);
		return tenderTypeDS;
	}
	
	protected ResumenVentasDataSource getPaymentMediumDataSource() throws Exception{
		ResumenVentasPaymentMediumDataSource paymentMediumDS = new ResumenVentasPaymentMediumDataSource(
				get_TrxName(), getCtx(), getOrgID(), getDateFrom(), getDateTo(), getPosID(), getUserID());
		paymentMediumDS = (ResumenVentasPaymentMediumDataSource)loadDSData(paymentMediumDS);
		return paymentMediumDS;
	}
	
	protected ResumenVentasDataSource getCategoriaIVADataSource() throws Exception{
		ResumenVentasCategoriaIVADataSource categoriaIVADS = new ResumenVentasCategoriaIVADataSource(
				get_TrxName(), getCtx(), getOrgID(), getDateFrom(), getDateTo(), getPosID(), getUserID());
		categoriaIVADS = (ResumenVentasCategoriaIVADataSource)loadDSData(categoriaIVADS);
		return categoriaIVADS;
	}
	
	protected ResumenVentasDataSource getDocTypeDataSource() throws Exception{
		ResumenVentasDocTypeDataSource docTypeDS = new ResumenVentasDocTypeDataSource(
				get_TrxName(), getCtx(), getOrgID(), getDateFrom(), getDateTo(), getPosID(), getUserID());
		docTypeDS = (ResumenVentasDocTypeDataSource)loadDSData(docTypeDS);
		return docTypeDS;
	}
	
	protected ResumenVentasDataSource getBPGroupDataSource() throws Exception{
		ResumenVentasBPGroupDataSource bpGroupDS = new ResumenVentasBPGroupDataSource(
				get_TrxName(), getCtx(), getOrgID(), getDateFrom(), getDateTo(), getPosID(), getUserID());
		bpGroupDS = (ResumenVentasBPGroupDataSource)loadDSData(bpGroupDS);
		return bpGroupDS;
	}
	
	protected ResumenVentasDataSource getCurrentAccountPaymentsDataSource() throws Exception{
		ResumenVentasCurrentAccountPaymentsDataSource currentAccountPaymentsDS = null;
		if(isShowCurrentAccountPayments()){
			currentAccountPaymentsDS = new ResumenVentasCurrentAccountPaymentsDataSource(
					get_TrxName(), getCtx(), getOrgID(), getDateFrom(),
					getDateTo(), getPosID(), getUserID());
			currentAccountPaymentsDS = (ResumenVentasCurrentAccountPaymentsDataSource) loadDSData(currentAccountPaymentsDS);
		}
		return currentAccountPaymentsDS;
	}
	
	/**
	 * @return subreporte 
	 */
	protected MJasperReport getResumenVentasSubreport() throws Exception{
		return getJasperReport(getCtx(), "ResumenVentas-Subreport", get_TrxName());
	}

}
