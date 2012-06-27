package org.openXpertya.JasperReport;

import java.io.ByteArrayInputStream;

import org.openXpertya.JasperReport.DataSource.AuditoriaCashDataSource;
import org.openXpertya.JasperReport.DataSource.AuditoriaCuponDataSource;
import org.openXpertya.JasperReport.DataSource.AuditoriaDataSource;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MPreference;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class LaunchAuditoriaValores extends LaunchDeclaracionValores {

	/** Nombre de la Preferencia con la moneda de referencia del reporte */
	private static final String AUDIT_CURRENCY_PREFERENCE = "AuditReferenceCurrencyISOCode";
	
	/** Moneda legal */
	
	private MCurrency currency;
	
	/** Moneda de referencia */
	
	private MCurrency currencyReference;
	
	@Override
	protected String getTitle(){
		return "Auditoria de Valores";
	}
	
	@Override
	protected void loadReportParameters() throws Exception {
		// Símbolos de las monedas
		// Obtener la moneda de la preferencia para este reporte
		// Si no existe, por defecto tomamos dólares
		String currency_iso = MPreference.searchCustomPreferenceValue(
				AUDIT_CURRENCY_PREFERENCE, Env.getAD_Client_ID(getCtx()),
				Env.getAD_Org_ID(getCtx()), Env.getAD_User_ID(getCtx()), true);
		if(Util.isEmpty(currency_iso)){
			log.severe("Preference " + AUDIT_CURRENCY_PREFERENCE
					+ " Not Found - Using Default Currency ISO Code : USD");
			currency_iso = "USD";
		}
		log.info("Audit Report - Using Currency ISO Code : "+currency_iso);
		setCurrencyReference(MCurrency.get(getCtx(), currency_iso));
		setCurrency(MCurrency.get(getCtx(), Env.getContextAsInt(getCtx(), "$C_Currency_ID")));
		addReportParameter("CURRENCY_SYMBOL", getCurrency().getCurSymbol());
		addReportParameter("CURRENCY_REFERENCE_SYMBOL", getCurrencyReference().getCurSymbol());
		// Parámetros de la superclase
		super.loadReportParameters();
	}
	
	@Override
	protected void addSubreports() throws Exception{
		// Data Sources
		// Data Source de Efectivo
		AuditoriaDataSource cashDS = getAuditoriaCashDataSource();
		// Data Source de Cupon
		AuditoriaDataSource cuponDS = getAuditoriaCuponDataSource();
		// Se agregan los datasources de los subreportes
		addReportParameter("SUBREPORT_CASH_DATASOURCE", cashDS);
		addReportParameter("SUBREPORT_CUPON_DATASOURCE", cuponDS);
		// Subreporte compilado
		MJasperReport auditSubreport = getAuditoriaSubreport(); 
		// Se agrega el informe compilado como parámetro.
		addReportParameter("COMPILED_SUBREPORT_AUDITORIA", new ByteArrayInputStream(auditSubreport.getBinaryData()));
	}
	
	protected AuditoriaDataSource loadDSData(AuditoriaDataSource ds) throws Exception{
		ds.loadData();
		return ds;
	}
	
	protected AuditoriaDataSource getAuditoriaCashDataSource() throws Exception{
		AuditoriaDataSource cashDS = new AuditoriaCashDataSource(getCtx(),
				getValoresDTO(), getCurrency().getID(), getCurrencyReference()
						.getID(), get_TrxName());
		cashDS = (AuditoriaCashDataSource)loadDSData(cashDS);
		return cashDS;
	}
	
	protected AuditoriaDataSource getAuditoriaCuponDataSource() throws Exception{
		AuditoriaDataSource cuponDS = new AuditoriaCuponDataSource(getCtx(),
				getValoresDTO(), getCurrency().getID(), getCurrencyReference()
						.getID(), get_TrxName());
		cuponDS = (AuditoriaCuponDataSource)loadDSData(cuponDS);
		return cuponDS;
	}
	
	protected MJasperReport getAuditoriaSubreport() throws Exception{
		return getJasperReport(getCtx(), "AuditoriaValores-Subreport", get_TrxName());
	}

	private void setCurrency(MCurrency currency) {
		this.currency = currency;
	}

	private MCurrency getCurrency() {
		return currency;
	}

	private void setCurrencyReference(MCurrency currencyReference) {
		this.currencyReference = currencyReference;
	}

	private MCurrency getCurrencyReference() {
		return currencyReference;
	}
}
