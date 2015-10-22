package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Properties;

import org.openXpertya.util.DB;

public class InvoicePerceptionsDataSource extends QueryDataSource {

	/** Contexto */
	private Properties ctx;
	
	/** ID de Comprobante */
	private Integer invoiceID;
	
	public InvoicePerceptionsDataSource(String trxName) {
		super(trxName);
		// TODO Auto-generated constructor stub
	}
	
	public InvoicePerceptionsDataSource(Properties ctx, Integer invoiceID, String trxName) {
		this(trxName);
		setCtx(ctx);
		setInvoiceID(invoiceID);
	} 

	@Override
	protected String getQuery() {
		return "select c_tax_id, name, taxbaseamt, taxamt, taxamt/taxbaseamt * 100 as alicuota " +
			   "from (select t.c_tax_id, t.name, sum(it.taxbaseamt) as taxbaseamt, sum(it.taxamt) as taxamt " +
			   "		from c_invoicetax as it " +
			   "		inner join c_tax as t on t.c_tax_id = it.c_tax_id " +
			   "		where it.c_invoice_id = ? and t.ispercepcion = 'Y' " +
			   "		group by t.c_tax_id, t.name) as p " +
			   "order by name";
	}
	
	private String getTotalQuery(){
		return "select coalesce(sum(taxamt),0) as total from (" + getQuery() + ") as t";
	}
	
	public BigDecimal getTotalAmt() throws Exception{
		BigDecimal totalAmt = (BigDecimal) DB.getSQLObject(getTrxName(), getTotalQuery(), getParameters());
		return totalAmt.compareTo(BigDecimal.ZERO) > 0?totalAmt:BigDecimal.ZERO;
	}

	@Override
	protected Object[] getParameters() {
		return new Object[]{getInvoiceID()};
	}

	protected Integer getInvoiceID() {
		return invoiceID;
	}

	protected void setInvoiceID(Integer invoiceID) {
		this.invoiceID = invoiceID;
	}

	protected Properties getCtx() {
		return ctx;
	}

	protected void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
