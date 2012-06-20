package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.VencimientoDeFacturasDataSource;
import org.openXpertya.util.Util;

public class LaunchVencimientosDeFacturas extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		// Nombre de la organización
		addReportParameter("ad_org_name",getOrgName());
		// Nombre de la entidad comercial
		addReportParameter("bpartner",getBPartnerName());
		// Mostrar Subtotales
		addReportParameter("subtotales", getSubtotales());
		// Fecha Desde
		addReportParameter("fdesde", getDateFrom());
		// Fecha Hasta
		addReportParameter("fhasta", getDateTo());
		// Tipo de Transacción
		addReportParameter("TRX_TYPE_NAME", getTrxTypeInfo());
		// Filtro de fechas
		addReportParameter("DATE_FILTER_NAME", getDateFilterInfo());
	}
	
	/**
	 * @return Nombre de la organización o todos
	 */
	private String getOrgName(){
		Integer orgID = getOrgID();
		return Util.isEmpty(orgID, true) ? "Todos" : JasperReportsUtil
				.getOrgName(getCtx(), orgID);
	}
	
	/**
	 * @return Nombre de la entidad comercial o todos
	 */
	private String getBPartnerName(){
		Integer bPartnerID = getBPartnerID();
		return Util.isEmpty(bPartnerID, true) ? "Todos" : JasperReportsUtil
				.getBPartnerName(getCtx(), bPartnerID, get_TrxName());
	}
	
	/**
	 * @return fecha desde
	 */
	private Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("DateInvoiced");
	}
	
	/**
	 * @return fecha hasta
	 */
	private Timestamp getDateTo(){
		return (Timestamp)getParameterValue("DateInvoiced_TO");
	}
	
	/**
	 * @return id de la organización
	 */
	private Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	/**
	 * @return id de la entidad comercial
	 */
	private Integer getBPartnerID(){
		return (Integer)getParameterValue("C_BPartner_ID");
	}
	
	/**
	 * @return filtro de fechas
	 */
	private String getDateFilter(){
		return (String) getParameterValue("DateFilter");
	}
	
	/**
	 * @return tipo de transacción
	 */
	private String getTrxType(){
		return (String) getParameterValue("TrxType");
	}
	
	/**
	 * @return Info del tipo de transacción
	 */
	private String getTrxTypeInfo(){
		return getParameterInfo("TrxType");
	}

	/**
	 * @return Info del filtro de fechas
	 */
	private String getDateFilterInfo(){
		return getParameterInfo("DateFilter");
	}
	
	/**
	 * @return true si se deben agregar los subtotales por EC, false caso
	 *         contrario
	 */
	private Boolean getSubtotales(){
		return ((String)getParameterValue("subtotales")).equals("Y");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new VencimientoDeFacturasDataSource(getCtx(), getDateFrom(),
				getDateTo(), getOrgID(), getBPartnerID(), getDateFilter(),
				getTrxType(), get_TrxName());
	}

}
