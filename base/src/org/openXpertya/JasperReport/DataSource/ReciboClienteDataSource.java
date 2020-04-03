package org.openXpertya.JasperReport.DataSource;


public class ReciboClienteDataSource extends OrdenPagoDataSource {

	public ReciboClienteDataSource(int allocationHdr_ID, String trxName) {
		super(allocationHdr_ID,trxName);
	}

	@Override
	public String getCashNameDescription()
	{
		return " p.documentNo ";
	}

	
}
