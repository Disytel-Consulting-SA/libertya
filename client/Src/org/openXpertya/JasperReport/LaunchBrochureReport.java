package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.BrochureReportDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBrochure;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MRefList;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class LaunchBrochureReport extends JasperReportLaunch {

	private static final String ORDERBY_REFERENCE_UID = "CORE-AD_Reference-1010253";
	private static final String ORDER_REFERENCE_UID = "CORE-AD_Reference-1010254";
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Organización
		if(!Util.isEmpty(getOrgID(), true)){
			MOrg org = MOrg.get(getCtx(), getOrgID());
			addReportParameter("ORG_VALUE", org.getValue());
			addReportParameter("ORG_NAME", org.getName());
		}
		// Folleto
		if(!Util.isEmpty(getBrochureID(), true)){
			MBrochure brochure = new MBrochure(getCtx(), getBrochureID(), null);
			addReportParameter("BROCHURE_DOCUMENTNO", brochure.getDocumentNo());
		}
		// Columna de orden
		addReportParameter("ORDERBY_NAME", MRefList.getListName(getCtx(),
				getOrderByReferenceID(), getOrderBy()));
		// Orden
		addReportParameter("ORDER_NAME", MRefList.getListName(getCtx(),
				getOrderReferenceID(), getOrder()));
		// Fechas
		addReportParameter("DateFrom", getDateFrom());
		addReportParameter("DateTo", getDateTo());
		// Transacción de ventas
		addReportParameter("IsSOTrx", getIsSOTrx());
	}

	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Integer getBrochureID(){
		return (Integer)getParameterValue("M_Brochure_ID");
	}
	
	protected String getOrderBy(){
		return (String)getParameterValue("OrderBy");
	}
	
	protected String getOrder(){
		return (String)getParameterValue("Order");
	}
	
	protected String getIsSOTrx(){
		return (String)getParameterValue("IsSOTrx");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("DateFrom");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateTo");
	}
	
	private String getReferenceIDQuery(){
		return "SELECT ad_reference_id FROM ad_reference WHERE ad_componentobjectuid = '";
	}
	
	private Integer getOrderByReferenceID(){
		return DB.getSQLValue(null, getReferenceIDQuery()+ORDERBY_REFERENCE_UID+"'");
	}

	private Integer getOrderReferenceID(){
		return DB.getSQLValue(null, getReferenceIDQuery()+ORDER_REFERENCE_UID+"'");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new BrochureReportDataSource(get_TrxName(), getOrgID(),
				getBrochureID(), getOrderBy(), getOrder(), getIsSOTrx(),
				getDateFrom(), getDateTo());
	}

}
