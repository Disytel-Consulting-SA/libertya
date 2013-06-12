package org.openXpertya.JasperReport;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.POSJournalChecksDataSource;

public class LaunchPOSJournalChecks extends LaunchDeclaracionValores {

	/**
	 * @return título del reporte
	 */
	protected String getTitle(){
		return "Informe de Cheques por Caja";
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new POSJournalChecksDataSource(getCtx(), getValoresDTO(), get_TrxName());
	}
	
	protected boolean isLoadVentasDataSource(){
		return false;
	}
	
	protected boolean isLoadCashDataSource(){
		return false;
	}
	
	protected boolean isLoadCheckDataSource(){
		return false;
	}
	
	protected boolean isLoadTransferDataSource(){
		return false;
	}
	
	protected boolean isLoadCuponDataSource(){
		return false;
	}
	
	protected boolean isLoadCreditNoteDataSource(){
		return false;
	}
	
	protected boolean isLoadCuentaCorrienteDataSource(){
		return false;
	}
	
	protected boolean isLoadValoresDataSource(){
		return false;
	}
	
	protected boolean isLoadVentasReceiptDataSource(){
		return false;
	}
	
	protected boolean isLoadVoidDocumentsDataSource(){
		return false;
	}
}
