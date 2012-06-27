package org.openXpertya.JasperReport;

public class LaunchPOSJournalInvoicesReceipt extends LaunchDeclaracionValores {

	@Override
	protected String getTitle(){
		return "Facturas y sus medios de cobro";
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
		return false;
	}
	
	@Override
	protected boolean isLoadValoresDataSource(){
		return false;
	}	
	
	@Override
	protected boolean isLoadVentasReceiptDataSource(){
		return true;
	}
}
