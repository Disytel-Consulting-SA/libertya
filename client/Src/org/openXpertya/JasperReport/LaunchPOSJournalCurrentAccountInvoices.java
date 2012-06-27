package org.openXpertya.JasperReport;


public class LaunchPOSJournalCurrentAccountInvoices extends LaunchDeclaracionValores{

	@Override
	protected String getTitle(){
		return "Facturas en Cuenta Corriente";
	}
	
	@Override
	protected boolean isLoadVentasDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadCashDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadCheckDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadTransferDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadCuponDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadCreditNoteDataSource(){
		return false;
	}
	
	@Override
	protected boolean isLoadCuentaCorrienteDataSource(){
		return true;
	}
	
	@Override
	protected boolean isLoadValoresDataSource(){
		return false;
	}	
}
