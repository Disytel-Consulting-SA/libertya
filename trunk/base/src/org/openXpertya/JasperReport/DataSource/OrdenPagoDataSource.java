package org.openXpertya.JasperReport.DataSource;

import net.sf.jasperreports.engine.JRDataSource;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MInvoice;
import org.openXpertya.util.Env;

public class OrdenPagoDataSource {

	protected int p_C_AllocationHdr_ID = 0;
	private MAllocationHdr paymentOrder;
	private String trx;

	public OrdenPagoDataSource(int allocationHdr_ID) {
		super();
		p_C_AllocationHdr_ID = allocationHdr_ID;
		paymentOrder = new MAllocationHdr(Env.getCtx(), p_C_AllocationHdr_ID,
				null);
	}
	
	public OrdenPagoDataSource (int allocationHdr_ID, String trxName )
	{
		super();
		p_C_AllocationHdr_ID = allocationHdr_ID;
		paymentOrder = new MAllocationHdr(Env.getCtx(), p_C_AllocationHdr_ID,
				null);
		trx = trxName;
	}

	public JRDataSource getDocumentsDataSource() {
		DocumentsDataSource ds = new DocumentsDataSource();
		ds.setOrdenPagoDataSource(this);
		ds.loadData();
		return ds;
	}

	public JRDataSource getChecksDataSource() {
		ChecksDataSource ds = new ChecksDataSource();
		ds.setOrdenPagoDataSource(this);
		ds.loadData();
		return ds;
	}

	public JRDataSource getOtherPaymentsDataSource() {
		OtherPaymentsDataSource ds = new OtherPaymentsDataSource();
		ds.setOrdenPagoDataSource(this);
		ds.loadData();
		return ds;
	}

	public JRDataSource getComprobanteRetenciones(int invoiceid) {
		MInvoice invoice = new MInvoice(Env.getCtx(), invoiceid, null);
		InvoiceDataSource ds = new InvoiceDataSource(Env.getCtx(), invoice);
		ds.loadData();
		return ds;
	}
	
	public JRDataSource getCreditNotesDataSource() {
		CreditNotesDataSource ds = new CreditNotesDataSource();
		ds.setOrdenPagoDataSource(this);
		ds.loadData();
		return ds;
	}

	/**
	 * Query para retornar la nómina de invoices usadas como crédito
	 * Utilizada también en el Launch para obtener el total de NC del Allocation! 
	 */
	public static String getCreditNotesQuery() {
		return 
			      " SELECT DISTINCT i.C_Invoice_ID, "
				+ "		   'Crédito'::CHARACTER VARYING AS PaymentType, "
				+ "		   dt.PrintName::CHARACTER VARYING AS Description, "
				+ "		   i.DocumentNo::CHARACTER VARYING AS DocumentNo, "
				+ "		   i.DateAcct AS DocumentDate, "
				+ "		   sum(currencyconvert(al.amount, ah.c_currency_id, i.c_currency_id, i.DateAcct::TIMESTAMP WITH TIME zone, NULL::INTEGER, ah.ad_client_id, ah.ad_org_id)) AS Amount, "
				+ "        i.grandtotal AS PayAmt, "
				+ "        cu.iso_code AS Currency  "
				+ " FROM    c_allocationhdr ah "
				+ "	INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "	INNER JOIN c_invoice i ON al.c_invoice_credit_id = i.c_invoice_id "
				+ " INNER JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID "
				+ " INNER JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id "
				+ " WHERE  ah.C_AllocationHdr_ID   = ? AND dt.doctypekey NOT IN ('RTI', 'RTR', 'RCR', 'RCI')"
				+ " GROUP BY i.C_Invoice_ID, PaymentType, dt.PrintName, i.DocumentNo, DocumentDate, PayAmt, Currency "
				+ " ORDER BY i.DateAcct";
	}

	protected String getCashNameDescription() {
		return " NULL::CHARACTER VARYING ";
	}

	/**
	 * @return the paymentOrder
	 */
	public MAllocationHdr getPaymentOrder() {
		return paymentOrder;
	}

	public String getTrx() {
		return trx;
	}

}
