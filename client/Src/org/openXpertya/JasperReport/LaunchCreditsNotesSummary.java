package org.openXpertya.JasperReport;


public class LaunchCreditsNotesSummary extends LaunchResumenVentas {

	@Override
	protected String getTitle(){
		return "Resumen de Notas de Crédito";
	}
	
	@Override
	protected boolean isOnlyCN() {
		// TODO Auto-generated method stub
		return true;
	}
}
