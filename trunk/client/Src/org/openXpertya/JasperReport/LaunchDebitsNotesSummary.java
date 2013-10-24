package org.openXpertya.JasperReport;

public class LaunchDebitsNotesSummary extends LaunchResumenVentas {

	@Override
	protected String getTitle(){
		return "Resumen de Notas de Débito";
	}
	
	@Override
	protected boolean isOnlyDN() {
		// TODO Auto-generated method stub
		return true;
	}

}
