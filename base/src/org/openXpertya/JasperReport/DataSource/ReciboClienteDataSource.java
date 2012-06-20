package org.openXpertya.JasperReport.DataSource;

import net.sf.jasperreports.engine.JRDataSource;

public class ReciboClienteDataSource extends OrdenPagoDataSource {

	public ReciboClienteDataSource(int allocationHdr_ID) {
		super(allocationHdr_ID);
	}

	/* *********************************************************** /
	 * RCDocumentsDataSource: Clase que contiene el DataSource para
	 * el subreporte de Comprobantes del reporte de Recibo de 
	 * Clientes. Esta clase es un especialización del DataSource de
	 * Documentos de OP. Cualquier diferencia en los datos debe ser
	 * codificada en el método getDataSQL() de esta clase.
	 * ************************************************************/
	class RCDocumentsDataSource extends OrdenPagoDataSource.DocumentsDataSource {
		
	}

	/* *********************************************************** /
	 * RCDocumentsDataSource: Clase que contiene el DataSource para
	 * el subreporte de Cheques del reporte de Recibo de 
	 * Clientes. Esta clase es un especialización del DataSource de
	 * Cheques de OP. Cualquier diferencia en los datos debe ser
	 * codificada en el método getDataSQL() de esta clase.
	 * ************************************************************/
	class RCChecksDataSource extends OrdenPagoDataSource.ChecksDataSource {
		
	}

	/* *********************************************************** /
	 * RCDocumentsDataSource: Clase que contiene el DataSource para
	 * el subreporte de Otros Medios de Cobro del reporte de Recibo de 
	 * Clientes. Esta clase es un especialización del DataSource de
	 * Medios de Pago de OP. Cualquier diferencia en los datos debe ser
	 * codificada en el método getDataSQL() de esta clase.
	 * ************************************************************/
	class RCOtherPaymentsDataSource extends OrdenPagoDataSource.OtherPaymentsDataSource {
		
	}

	@Override
	public JRDataSource getChecksDataSource() {
		OPDataSource ds = new RCChecksDataSource();
		ds.loadData();
		return ds;
	}

	@Override
	public JRDataSource getDocumentsDataSource() {
		OPDataSource ds = new RCDocumentsDataSource();
		ds.loadData();
		return ds;
	}

	@Override
	public JRDataSource getOtherPaymentsDataSource() {
		OPDataSource ds = new RCOtherPaymentsDataSource();
		ds.loadData();
		return ds;
	}

	protected String getCashNameDescription()
	{
		return " p.documentNo ";
	}

	
}
