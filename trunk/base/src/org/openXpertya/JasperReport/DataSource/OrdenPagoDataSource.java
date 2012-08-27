package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class OrdenPagoDataSource {

	protected int p_C_AllocationHdr_ID = 0;
	private MAllocationHdr paymentOrder;
	
	public OrdenPagoDataSource(int allocationHdr_ID) {
		super();
		p_C_AllocationHdr_ID = allocationHdr_ID;
		paymentOrder = new MAllocationHdr(Env.getCtx(), p_C_AllocationHdr_ID, null);
	}

	public JRDataSource getDocumentsDataSource() {
		DocumentsDataSource ds = new DocumentsDataSource();
		ds.loadData();
		return ds;
	}

	public JRDataSource getChecksDataSource() {
		ChecksDataSource ds = new ChecksDataSource();
		ds.loadData();
		return ds;
	}
	
	public JRDataSource getOtherPaymentsDataSource() {
		OtherPaymentsDataSource ds = new OtherPaymentsDataSource();
		ds.loadData();
		return ds;
	}
	
	/* *********************************************************** /
	 * OPDataSource: Clase que contiene la funcionalidad común
	 * a todos los DataSource de los subreportes del reporte.
	 * ************************************************************/
	abstract class OPDataSource implements JRDataSource {
		/** Lineas del informe		*/
		private Object[] m_reportLines;
		/** Registro Actual */
		private int m_currentRecord = -1; // -1 porque lo primero que se hace es un ++
		
		public boolean next() throws JRException {
			m_currentRecord++;
			if (m_currentRecord >= m_reportLines.length )
				return false;

			return true;
		}
		
		public Object getFieldValue(JRField jrf) throws JRException {
			return getFieldValue(jrf.getName(),m_reportLines[m_currentRecord]);
		}
		
		protected abstract Object getFieldValue(String name, Object record) throws JRException;
		
		protected abstract String getDataSQL();
		
		protected abstract Object createRecord(ResultSet rs) throws SQLException;
		
		protected abstract void setQueryParameters(PreparedStatement pstmt) throws SQLException;
		
		public void loadData() {

			// ArrayList donde se guardan los datos del informe.
			ArrayList<Object> list = new ArrayList<Object>();
			
			try {
				PreparedStatement pstmt = DB.prepareStatement(getDataSQL());
				setQueryParameters(pstmt);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next())	{
					Object line = createRecord(rs);
					list.add(line);
				}
						
			}
			catch (SQLException e)	{
				throw new RuntimeException("No se puede ejecutar la consulta para crear las lineas del informe.");
			}
			
			// Se guarda la lista de líneas en el arreglo de líneas del reporte.
			m_reportLines = new Object[list.size()];
			list.toArray(m_reportLines);
		}
		
	}

	/* *********************************************************** /
	 * DocumentsDataSource: Clase que contiene el DataSource para
	 * el subreporte de Comprobantes del reporte de Orden de Pago.
	 * ************************************************************/
	class DocumentsDataSource extends OPDataSource {

		@Override
		protected Object getFieldValue(String name, Object record) throws JRException {
			M_Document document = (M_Document)record;
			if (name.toUpperCase().equals("DOCUMENTNO"))	{
				return document.documentNo;
			} else if (name.toUpperCase().equals("CURRENCY"))	{
				return document.currency;
			} else if (name.toUpperCase().equals("GRANDTOTALAMT"))	{
				return document.grandTotalAmt;
			} else if (name.toUpperCase().equals("ALLOCATEDAMT"))	{
				return document.allocatedAmt;
			} else if (name.toUpperCase().equals("OPENAMT"))	{
				return document.openAmt;
			}
			return null;
		}
		
		@Override
		protected Object createRecord(ResultSet rs) throws SQLException {
			return new M_Document(rs);
		}

		protected String getDataSQL() {
			String sql = "" 
				 +"SELECT " 
				 +"	 CASE "
				 +"		WHEN dt.DocBaseType = 'API' THEN 'FAC ' || i.DocumentNo "
				 +"     ELSE i.DocumentNo " 
				 +"	 END AS DocumentNo, "
				 +"	 cu.iso_code as Currency, "
				 +"	 i.grandtotal AS GrandTotalAmt, "
				 //+"	 SUM(al.amount + al.discountamt + al.writeoffamt) AS AllocatedAmt, "
				 +"	 SUM(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, ah.datetrx::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id)) AS AllocatedAmt, "
			 	 +"	 invoiceopen(i.C_Invoice_ID,0) AS OpenAmt "		
				 +"FROM c_allocationhdr ah "
				 +"  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				 +"  JOIN c_invoice i ON i.C_Invoice_ID = al.C_Invoice_ID "	
				 +"  JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID "
				 +"  JOIN C_DocType dt ON dt.C_DocType_ID = i.C_DocType_ID "
				 +"WHERE ah.C_AllocationHdr_ID = ? "	
				 +"GROUP BY al.C_Invoice_ID, i.DocumentNo, dt.DocBaseType, GrandTotalAmt, OpenAmt, Currency ";
			return sql;
		}

		@Override
		protected void setQueryParameters(PreparedStatement pstmt) throws SQLException {
			pstmt.setInt(1, p_C_AllocationHdr_ID);
		}
		
		/**
		 * POJO de Cheques.
		 */
		private class M_Document {
			
			protected String documentNo;
			protected String currency;
			protected BigDecimal grandTotalAmt;
			protected BigDecimal allocatedAmt;
			protected BigDecimal openAmt;
		
			public M_Document(String documentNo, String currency, BigDecimal grandTotalAmt, BigDecimal allocatedAmt, BigDecimal openAmt) {
				super();
				this.documentNo = documentNo;
				this.currency = currency;
				this.grandTotalAmt = grandTotalAmt;
				this.allocatedAmt = allocatedAmt;
				this.openAmt = openAmt;
			}

			public M_Document(ResultSet rs) throws SQLException {
				this(rs.getString("DocumentNo"),
  					 rs.getString("Currency"),
				     rs.getBigDecimal("GrandTotalAmt"),
				     rs.getBigDecimal("AllocatedAmt"),
				     rs.getBigDecimal("OpenAmt")); 
			}
		}
	}
	
	/* *********************************************************** /
	 * ChecksDataSource: Clase que contiene el DataSource para
	 * el subreporte de Cheques del reporte de Orden de Pago.
	 * ************************************************************/
	class ChecksDataSource extends OPDataSource {

		@Override
		protected Object getFieldValue(String name, Object record) throws JRException {
			M_Check check = (M_Check)record;
			if (name.toUpperCase().equals("DOCUMENTCHECKNO"))	{
				return check.documentCheckNo;
			} else if (name.toUpperCase().equals("CURRENCY"))	{
				return check.currency;
			} else if (name.toUpperCase().equals("BANK"))	{
				return check.bank;
			} else if (name.toUpperCase().equals("DUEDATE"))	{
				return check.dueDate;
			} else if (name.toUpperCase().equals("AMOUNT"))	{
				return check.amount;
			}
			return null;
		}
		
		@Override
		protected Object createRecord(ResultSet rs) throws SQLException {
			return new M_Check(rs);
		}

		protected String getDataSQL() {
			String sql = "" +
			"SELECT DISTINCT p.C_Payment_ID, " +
			"        CASE " +
			"                WHEN p.CheckNo IS NOT NULL " +
			"                THEN p.checkNo " +
			"                ELSE p.DocumentNo::CHARACTER VARYING " +
			"        END AS DocumentCheckNo, " +
			"        cu.iso_code as Currency, " +
			"        CASE " +
	        "               WHEN P.A_BANK IS NOT NULL AND P.A_BANK <> '' " +  
	        "               THEN P.A_BANK  "     +         
			"				ELSE (SELECT b.Name " + 
			"               FROM C_Bank b INNER JOIN C_BankAccount ba " +
			"               ON (b.C_Bank_ID = ba.C_Bank_ID) " +
	        "               WHERE ba.C_BankAccount_ID = p.C_BankAccount_ID)" + 
	        "         END AS Bank,"  +
			"        p.DueDate, " +
		/*	"        currencyconvert(al.amount, p.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP " + 	
			"WITH TIME zone         , " +
			"        NULL::INTEGER  , " +
			"        ah.ad_client_id, " +
			"        ah.ad_org_id) AS Amount " +*/
			"        sum (currencyconvert(al.amount, ah.c_currency_id, p.c_currency_id, p.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount," +
			"        p.payamt as PayAmt " + 
			"FROM    c_allocationhdr ah " +
			"        JOIN c_allocationline al " +
			"        ON      ah.c_allocationhdr_id = al.c_allocationhdr_id " +
			"        JOIN c_payment p " +
			"        ON      al.c_payment_id = p.c_payment_id " +
			"  		 JOIN c_currency cu ON p.C_Currency_ID = cu.C_Currency_ID " +
			"WHERE   p.tenderType            = 'K' " +
			"    AND ah.C_AllocationHdr_ID   = ? " +
			"GROUP BY p.C_Payment_ID, DocumentCheckNo, Bank, p.DueDate, PayAmt, Currency  ";
			// "ORDER BY documentCheckNo ";
			return sql;
		}

		@Override
		protected void setQueryParameters(PreparedStatement pstmt) throws SQLException {
			pstmt.setInt(1, p_C_AllocationHdr_ID);
		}
		
		/**
		 * POJO de Cheques.
		 */
		private class M_Check {
			protected String documentCheckNo;
			protected String currency;
			protected String bank;
			protected Timestamp dueDate;
			protected BigDecimal amount;
			
			public M_Check(String documentCheckNo, String currency, String bank, Timestamp dueDate, BigDecimal amount) {
				super();
				this.documentCheckNo = documentCheckNo;
				this.currency = currency;
				this.bank = bank;
				this.dueDate = dueDate;
				this.amount = amount;
			}
			
			public M_Check(ResultSet rs) throws SQLException {
				this(rs.getString("DocumentCheckNo"),
					 rs.getString("Currency"),
					 rs.getString("Bank"),
					 rs.getTimestamp("DueDate"),
					 (getPaymentOrder().isAdvanced() ? 
								rs.getBigDecimal("PayAmt") : 
								rs.getBigDecimal("Amount")));
			}
		}
	}

	/* *********************************************************** /
	 * OtherPaymentsDataSource: Clase que contiene el DataSource para
	 * el subreporte de Otros Pagos del reporte de Orden de Pago.
	 * ************************************************************/
	class OtherPaymentsDataSource extends OPDataSource {

		public Object getFieldValue(String name, Object record) throws JRException {
			M_Payment payment = (M_Payment)record;
			if (name.toUpperCase().equals("PAYMENTTYPE"))	{
				return payment.paymentType;
			} else if (name.toUpperCase().equals("CASHNAME"))	{
				return payment.cashName;
			} else if (name.toUpperCase().equals("BANKACCOUNT"))	{
				return payment.bankAccount;
			} else if (name.toUpperCase().equals("ROUTINGNO"))	{
				return payment.routingNo;
			} else if (name.toUpperCase().equals("TRANSFERDATE"))	{
				return payment.transferDate;
			} else if (name.toUpperCase().equals("AMOUNT"))	{
				return payment.amount;
			} else if (name.toUpperCase().equals("CURRENCY"))	{
				return payment.currency;
			}
			return null;
		}

		@Override
		protected Object createRecord(ResultSet rs) throws SQLException {
			return new M_Payment(rs);
		}
		@Override
		protected String getDataSQL() {
			String sql = "" +
			"(SELECT DISTINCT p.C_Payment_ID                  , " +
			"        'TRANS'::CHARACTER VARYING AS PaymentType, " +
			"        " + getCashNameDescription() + "     AS CashName   , " + // redefinir
			"        (SELECT ba.cc " +
			"                || ' - ' " +
			"                || b.Name " +
			"        FROM    C_Bank b " +
			"                INNER JOIN C_BankAccount ba " +
			"                ON (b.C_Bank_ID     = ba.C_Bank_ID) " +
			"        WHERE   ba.C_BankAccount_ID = p.C_BankAccount_ID " +
			"        ) AS BankAccount          , " +
			"        p.RoutingNo               , " +
			"        p.DateAcct AS TransferDate, " +
			"        sum (currencyconvert(al.amount, ah.c_currency_id, p.c_currency_id, p.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount," +
			//"        sum(al.amount) AS Amount,   " +
			"        p.payamt AS PayAmt, " +
			"        cu.iso_code as Currency " +
			/*"        currencyconvert(al.amount, p.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP " + 
			"WITH TIME zone         , " +
			"        NULL::INTEGER  , " +
			"        ah.ad_client_id, " +
			"        ah.ad_org_id) AS Amount " +*/
			"FROM    c_allocationhdr ah " +
			"        JOIN c_allocationline al " +
			"        ON      ah.c_allocationhdr_id = al.c_allocationhdr_id " +
			"        JOIN c_payment p " +
			"        ON      al.c_payment_id = p.c_payment_id " +
			"  		 JOIN c_currency cu ON p.C_Currency_ID = cu.C_Currency_ID " +
			"WHERE   p.tenderType            = 'A' " +
			"    AND ah.C_AllocationHdr_ID   = ? " +
			"GROUP BY p.C_Payment_ID, PaymentType, CashName, BankAccount, p.RoutingNo, p.dateAcct, PayAmt, Currency " +
		//	"ORDER BY PaymentType, " +
		//	"        BankAccount " +
			") " +
			" " +
			"UNION ALL " +
			"        (SELECT DISTINCT ll.C_CashLine_ID                , " +
			"                'EFECT'::CHARACTER VARYING AS PaymentType, " +
			"                cb.name " +
			"                || ' - ' " +
			"                || c.name               AS CashName    , " +
			"                NULL::CHARACTER VARYING AS BankAccount , " +
			"                NULL::CHARACTER VARYING AS RoutingNo   , " +
			"                c.DateAcct              AS TransferDate, " +
/*			"                currencyconvert(ABS(ll.amount), cb.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP " +
			"WITH TIME zone         , " +
			"        NULL::INTEGER  , " +
			"        ah.ad_client_id, " +
			"        ah.ad_org_id) AS Amount " +*/
			"        sum (currencyconvert(al.amount, ah.c_currency_id, ll.c_currency_id, c.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount," +
			//"                SUM(al.amount) AS Amount, " +
			"                ABS(ll.amount) AS PayAmt,  " +
			"                cu.iso_code AS Currency  " +
			"        FROM    c_allocationhdr ah " +
			"                JOIN c_allocationline al " +
			"                ON      ah.c_allocationhdr_id = al.c_allocationhdr_id " +
			"                JOIN c_cashline ll " +
			"                ON      al.c_cashline_id = ll.c_cashline_id " +
			"                JOIN c_cash c " +
			"                ON      c.C_Cash_ID = ll.C_Cash_ID " +
			"                JOIN C_CashBook cb " +
			"                ON      cb.C_CashBook_ID = c.C_CashBook_ID " +
			"		  		 JOIN c_currency cu ON cb.C_Currency_ID = cu.C_Currency_ID " +
			"        WHERE   true " +
			"            AND ah.C_AllocationHdr_ID = ? " +
			"        GROUP BY ll.C_CashLine_ID, PaymentType, CashName, BankAccount, RoutingNo, TransferDate, PayAmt, Currency " +
			"        ORDER BY PaymentType, " +
			"                CashName " +
			"        ) " +
			"UNION ALL " +
			" (SELECT DISTINCT i.C_Invoice_ID, " +    
			"		 'CREDITO'::CHARACTER VARYING AS PaymentType, " +    
			//"		 (dt.PrintName || ' ' || i.DocumentNo) ::CHARACTER VARYING AS Description, " +    
			"  CASE " +
			"		WHEN ri.C_RetencionSchema_ID IS NOT NULL THEN rs.name " +
			"		ELSE (dt.PrintName || ' ' || i.DocumentNo)::CHARACTER VARYING " +
			"	END AS Description2, " +
			"		 NULL::CHARACTER VARYING AS BankAccount, " +    
			"		 NULL::CHARACTER VARYING AS RoutingNo, " +    
			"		 i.DateAcct AS TransferDate, " +    
			"		 sum(currencyconvert(al.amount, ah.c_currency_id, i.c_currency_id, i.DateAcct::TIMESTAMP WITH TIME zone, NULL::INTEGER, ah.ad_client_id, ah.ad_org_id)) AS Amount, " +    
			//"		 SUM(al.amount) AS Amount,   " +
			"        i.grandtotal AS PayAmt, " +		
			"        cu.iso_code AS Currency  " +
			" FROM    c_allocationhdr ah " +    
			"	 INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id " +     
			"	 INNER JOIN c_invoice i ON al.c_invoice_credit_id = i.c_invoice_id " +
			"  	 INNER JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID " +
			" 	 INNER JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id "+
			"    LEFT JOIN M_Retencion_Invoice ri ON i.c_invoice_id = ri.c_invoice_id " +
			"    LEFT JOIN C_RetencionSchema rs ON ri.c_retencionschema_ID = rs.c_retencionschema_id " +
			" WHERE  ah.C_AllocationHdr_ID   = ? " +
			// Filtro facturas que se hayan utilizado como crédito, pero que sean retenciones
			// dado que el reporte no debe mostrar el detalle de retenciones.
			// UPDATE 20090219: SE VISUALIZAN A FIN DE MOSTRAR EL DESGLOSE, POR LO CUAL SE COMENTA
	/*		"   AND  NOT EXISTS (SELECT c_invoice_retenc_id " + 
			"                    FROM M_Retencion_Invoice ri " + 
			"                    WHERE ri.C_AllocationHdr_ID = ah.C_AllocationHdr_ID " + 
			"                      AND ri.C_Invoice_ID = i.C_Invoice_ID) " +    */   
			" GROUP BY i.C_Invoice_ID, PaymentType, Description2, BankAccount, RoutingNo, TransferDate, PayAmt, Currency " +
			" ORDER BY Description2, i.DateAcct) ";
			return sql;
		}

		@Override
		protected void setQueryParameters(PreparedStatement pstmt) throws SQLException {
			int i = 1;
			pstmt.setInt(i++, p_C_AllocationHdr_ID);
			pstmt.setInt(i++, p_C_AllocationHdr_ID);
			pstmt.setInt(i++, p_C_AllocationHdr_ID);
		}
		
		/**
		 * POJO de Otros Pagos.
		 */
		private class M_Payment {
			protected String paymentType;
			protected String cashName;
			protected String bankAccount;
			protected String routingNo;
			protected Timestamp transferDate;
			protected BigDecimal amount;
			protected String currency;
			
			public M_Payment(String paymentType, String cashName, String bankAccount, String routingNo, Timestamp transferDate, BigDecimal amount, String currency) {
				super();
				this.paymentType = paymentType;
				this.cashName = cashName;
				this.bankAccount = bankAccount;
				this.routingNo = routingNo;
				this.transferDate = transferDate;
				this.amount = amount;
				this.currency = currency;
			}

			public M_Payment(ResultSet rs) throws SQLException {
				this(rs.getString("PaymentType"),
					 rs.getString("CashName"),
					 rs.getString("BankAccount"),
					 rs.getString("RoutingNo"),
					 rs.getTimestamp("TransferDate"),
					 (getPaymentOrder().isAdvanced() ? 
							rs.getBigDecimal("PayAmt") : 
							rs.getBigDecimal("Amount")),
							rs.getString("Currency"));
			}
		}
	}
	
	protected String getCashNameDescription()
	{
		return " NULL::CHARACTER VARYING ";
	}

	/**
	 * @return the paymentOrder
	 */
	public MAllocationHdr getPaymentOrder() {
		return paymentOrder;
	}

}
