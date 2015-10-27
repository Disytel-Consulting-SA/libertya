package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.cc.CurrentAccountQuery;
import org.openXpertya.model.MCurrency;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class CurrentAccountReport extends SvrProcess {

	/** Entidad Comercial de los comprobantes a consultar */
	private Integer p_C_BPartnerID;
	/** Fecha inicial del rango de fechas de la transacción */
	private Timestamp p_DateTrx_From;
	/** Fecha final del rango de fechas de la transacción */
	private Timestamp p_DateTrx_To;
	/** Organización de los comprobantes a consultar */
	private Integer p_AD_Org_ID;
	/** Tipo de documento de los comprobantes a consultar */
	private Integer p_C_DocType_ID;
	/** Tipo de Cuenta del Reporte: Cliente o Proveedor */
	private String p_AccountType;
	/** Incluir pedidos no facturados en el informe */
	private boolean p_includeOpenOrders;
	private String p_includeOpenOrders_char;
	/** Mostrar detalle de Cobros y Pagos en el informe */
	private boolean p_ShowDetailedReceiptsPayments;
	private String p_ShowDetailedReceiptsPayments_char;

	/** Signo de documentos que son débitos (depende de p_AccountType) */
	private int debit_signo_issotrx;
	/** Signo de documentos que son créditos (depende de p_AccountType) */
	private int credit_signo_isotrx;
	/** Moneda en la que trabaja la compañía */
	private int client_Currency_ID;

	/**
	 * Importe acumulado del Crédito hasta la fecha inicial parámetro
	 * p_DateTrx_From
	 */
	private BigDecimal acumCredit = BigDecimal.ZERO;
	/**
	 * Importe acumulado del Débito hasta la fecha inicial parámetro
	 * p_DateTrx_From
	 */
	private BigDecimal acumDebit = BigDecimal.ZERO;
	/**
	 * Importe acumulado del Saldo hasta la fecha inicial parámetro
	 * p_DateTrx_From
	 */
	private BigDecimal acumBalance = BigDecimal.ZERO;
	
	/** Generador de consultas de cuenta corriente */
	private CurrentAccountQuery currentAccountQuery;

	/** Sólo comprobantes en cuenta corriente */
	private boolean onlyCurrentAccountDocuments;
	
	/** Tipos de documento segun columna documenttable de v_documents */
	protected static final String DOC_INVOICE = "C_Invoice";
	protected static final String DOC_PAYMENT = "C_Payment";
	protected static final String DOC_CASHLINE = "C_CashLine";
	protected static final String DOC_ALLOCATIONHDR = "C_AllocationHdr";

	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null)
				;
			else if (name.equalsIgnoreCase("C_BPartner_ID")) {
				p_C_BPartnerID = ((BigDecimal) para[i].getParameter())
						.intValue();
			} else if (name.equalsIgnoreCase("IncludeOpenOrders")) {
				p_includeOpenOrders_char = (String) para[i].getParameter();
				p_includeOpenOrders = "Y".equals((String) para[i]
						.getParameter());
			} else if (name.equalsIgnoreCase("ShowDetailedReceiptsPayments")) {
				p_ShowDetailedReceiptsPayments_char = (String) para[i]
						.getParameter();
				p_ShowDetailedReceiptsPayments = "Y".equals((String) para[i]
						.getParameter());
			} else if (name.equalsIgnoreCase("datetrx")) {
				p_DateTrx_From = (Timestamp) para[i].getParameter();
				p_DateTrx_To = (Timestamp) para[i].getParameter_To();
			} else if (name.equalsIgnoreCase("accounttype")) {
				p_AccountType = (String) para[i].getParameter();
			} else if (name.equalsIgnoreCase("AD_Org_ID")) {
				BigDecimal tmp = (BigDecimal) para[i].getParameter();
				p_AD_Org_ID = tmp == null ? null : tmp.intValue();
			} else if (name.equalsIgnoreCase("C_DocType_ID")) {
				BigDecimal tmp = (BigDecimal) para[i].getParameter();
				p_C_DocType_ID = tmp == null ? null : tmp.intValue();
			} else if (name.equalsIgnoreCase("OnlyCurrentAccountDocuments")) {
				setOnlyCurrentAccountDocuments("Y".equals((String) para[i].getParameter()));
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}

		// Reporte de Cta Corriente de Cliente o Proveedor.
		// +-------------------------+--------------------------+
		// | Cta Corriente Cliente | Cta Corriente Proveedor |
		// +-------------------------+--------------------------+
		// | Debitos = Signo 1 | Debitos = Signo -1 |
		// | Créditos = Signo -1 | Créditos = Signo 1 |
		// +-------------------------+--------------------------+
		debit_signo_issotrx = p_AccountType.equalsIgnoreCase("C") ? 1 : -1;
		credit_signo_isotrx = p_AccountType.equalsIgnoreCase("C") ? -1 : 1;

		// Moneda de la compañía utilizada para conversión de montos de
		// documentos.
		client_Currency_ID = Env.getContextAsInt(getCtx(), "$C_Currency_ID");
		// Generador de consulta de cuenta corriente
		setCurrentAccountQuery(new CurrentAccountQuery(getCtx(), p_AD_Org_ID,
				p_C_DocType_ID, p_ShowDetailedReceiptsPayments, p_DateTrx_From,
				p_DateTrx_To, isOnlyCurrentAccountDocuments(), p_C_BPartnerID));
	}

	@Override
	protected String doIt() throws Exception {

		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_CUENTACORRIENTE WHERE DATECREATED < ('now'::text)::timestamp(6) - interval '7 days'");
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_CUENTACORRIENTE WHERE AD_PInstance_ID = "
				+ getAD_PInstance_ID());

		// Saldo acumulado, por defecto es 0.
		acumBalance = BigDecimal.ZERO;

		// Si se ingresó un filtro de fecha de inicio, entonces se calcula el
		// saldo acumulado
		// a partir de todos los comprobantes que tiene fecha de transacción
		// menor a la
		// fecha inicial seteada para el reporte.
		if (p_DateTrx_From != null) {
			calculateBalance();
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(getCurrentAccountQuery().getQuery(),
					get_TrxName(), true);
			int i = 1;
			// Parámetros de sqlDoc
			pstmt.setInt(i++, debit_signo_issotrx);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, credit_signo_isotrx);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, getAD_Client_ID());
			pstmt.setInt(i++, p_C_BPartnerID);
			if(isOnlyCurrentAccountDocuments()){
				pstmt.setInt(i++, debit_signo_issotrx);
			}
			i = pstmtSetParam(i, p_AD_Org_ID, pstmt);
			i = pstmtSetParam(i, p_C_DocType_ID, pstmt);
			// Parámetros para el filtro de fechas
			i = pstmtSetParam(i, p_DateTrx_From, pstmt);
			i = pstmtSetParam(i, p_DateTrx_To, pstmt);

			rs = pstmt.executeQuery();

			int subIndice = 0;
			int trx_Org_ID = p_AD_Org_ID != null ? p_AD_Org_ID : Env
					.getContextAsInt(getCtx(), "#AD_Org_ID");
			StringBuffer usql = new StringBuffer();

			if (p_DateTrx_From != null) {
				subIndice++;
				// insert first row: before query balance period
				// Field used for 'date field' in temporary table: DATETRX
				usql.append(" INSERT INTO T_CUENTACORRIENTE (SUBINDICE, IncludeOpenOrders, ShowDetailedReceiptsPayments, AD_CLIENT_ID, AD_ORG_ID, AD_PINSTANCE_ID, ISO_CODE, AMOUNT, DEBE, HABER, SALDO, NUMEROCOMPROBANTE, C_BPARTNER_ID, ACCOUNTTYPE, DATETRX, C_DOCTYPE_ID, OnlyCurrentAccountDocuments) "
						+ " VALUES ("
						+ subIndice
						+ ", '"
						+ p_includeOpenOrders_char
						+ "', '"
						+ p_ShowDetailedReceiptsPayments_char
						+ "', "
						+ getAD_Client_ID()
						+ ", "
						+ trx_Org_ID
						+ " , "
						+ getAD_PInstance_ID()
						+ ", '"
						+ "', "
						+ BigDecimal.ZERO
						+ ", "
						+ acumDebit
						+ ", "
						+ acumCredit
						+ ", "
						+ acumBalance
						+ ", '-', "
						+ p_C_BPartnerID
						+ ", '"
						+ p_AccountType
						+ "', '"
						+ p_DateTrx_From + "', NULL"
						+ ", "
						+ "'"+(isOnlyCurrentAccountDocuments()?"Y":"N")+"'"
						+ ");");
			}

			BigDecimal credit = null;
			BigDecimal debit = null;
			Map<String, Integer> documents = new HashMap<String, Integer>();
			String documentKey;
			// process rs & insert rows in table
			while (rs.next()) {
				documentKey = rs.getString("documenttable")
						+ "_"
						+ rs.getString("document_id")
						+ (Util.isEmpty(rs.getInt("c_invoicepayschedule_id"),
								true) ? "" : "_"
								+ rs.getInt("c_invoicepayschedule_id"));
				if(documents.get(documentKey) == null){
					Timestamp fechaVencimiento = null;
					if (!p_ShowDetailedReceiptsPayments) {
						// Obtengo la última fecha de vencimiento de la factura, si
						// es que tiene.
						String sqlFechaVen = "select MAX(duedate) from libertya.c_invoicepayschedule ipc inner join c_invoice i on (ipc.c_invoice_id = i.c_invoice_id) where (i.c_invoice_id = '"
								+ rs.getString("document_id")
								+ "') and ('"
								+ rs.getString("documenttable")
								+ "' = 'C_Invoice')";
						PreparedStatement pstmFechaVen = DB.prepareStatement(
								sqlFechaVen, get_TrxName(), true);
						ResultSet rsFechaVen = pstmFechaVen.executeQuery();
						if (rsFechaVen.next()) {
							fechaVencimiento = rsFechaVen.getTimestamp(1);
						}
					} else
						fechaVencimiento = rs.getTimestamp("duedate");
	
					subIndice++;
					credit = rs.getBigDecimal("Credit");
					debit = rs.getBigDecimal("Debit");
					int currencyID = rs.getInt("C_Currency_ID");
					// Validación de Conversión de monedas.
					// En caso de no existir una conversión entre la moneda del
					// documento y la moneda de
					// la compañía, entonces el monto Debit y Credit serán NULL.
					if ((credit == null || debit == null)
							&& currencyID != client_Currency_ID) {
						String fromISO = MCurrency
								.getISO_Code(getCtx(), currencyID);
						String toISO = MCurrency.getISO_Code(getCtx(),
								client_Currency_ID);
						log.severe("No Currency Conversion from " + fromISO
								+ " to " + toISO);
						throw new Exception("@NoCurrencyConversion@ (" + fromISO
								+ "->" + toISO + ")");
					}
					
					// ANTONIO: La cuenta es al reves acumBalance =
					// acumBalance.add(credit.subtract(debit));
					acumBalance = acumBalance.add(debit.subtract(credit));
					usql.append(" INSERT INTO T_CUENTACORRIENTE (SUBINDICE, IncludeOpenOrders, ShowDetailedReceiptsPayments, AD_CLIENT_ID, AD_ORG_ID, AD_PINSTANCE_ID, ISO_CODE, AMOUNT, DEBE, HABER, SALDO, NUMEROCOMPROBANTE, C_BPARTNER_ID, ACCOUNTTYPE, DATETRX, C_DOCTYPE_ID, C_INVOICE_ID, C_PAYMENT_ID, C_CASHLINE_ID, C_ALLOCATIONHDR_ID, duedate, OnlyCurrentAccountDocuments) "
							+ " VALUES ("
							+ subIndice
							+ ", '"
							+ p_includeOpenOrders_char
							+ "', '"
							+ p_ShowDetailedReceiptsPayments_char
							+ "', "
							+ getAD_Client_ID()
							+ ", "
							+ trx_Org_ID
							+ " , "
							+ getAD_PInstance_ID()
							+ " ,'"
							+ MCurrency.getISO_Code(getCtx(),
									rs.getInt("C_Currency_ID"))
							+ "', "
							+ rs.getBigDecimal("Amount")
							+ ", "
							+ rs.getBigDecimal("Debit")
							+ ", "
							+ rs.getBigDecimal("Credit")
							+ ", "
							+ acumBalance
							+ ", '"
							+ rs.getString("DocumentNo")
							+ "', "
							+ p_C_BPartnerID
							+ ", '"
							+ p_AccountType
							+ "', '"
							+ rs.getTimestamp("DateTrx")
							+ "', "
							+ rs.getInt("C_DocType_ID") 
							+ ", ");
	
					// La linea es de una factura?
					usql.append(
							DOC_INVOICE.equals(rs.getString("documenttable")) ? rs
									.getString("document_id") : "NULL")
							.append(", ");
					// La linea es de un pago/cheque?
					usql.append(
							DOC_PAYMENT.equals(rs.getString("documenttable")) ? rs
									.getString("document_id") : "NULL")
							.append(", ");
					// La linea es de una linea de caja?
					usql.append(
							DOC_CASHLINE.equals(rs.getString("documenttable")) ? rs
									.getString("document_id") : "NULL")
							.append(", ");
					// Es un recibo agrupado?
					usql.append(
							DOC_ALLOCATIONHDR.equals(rs.getString("documenttable")) ? rs
									.getString("document_id") : "NULL").append(
							" , ");
	
					// Si se tiene una fecha de vencimiento se inserta
					if (fechaVencimiento != null) {
						usql.append("'" + fechaVencimiento + "'");
					} else
						usql.append("NULL");
					
					usql.append(" , '"+(isOnlyCurrentAccountDocuments()?"Y":"N")+"'");
					usql.append(" ); ");
					documents.put(documentKey, trx_Org_ID);
				}
			}

			// incorporar los pedidos no facturados (parcial o total)
			if (p_includeOpenOrders)
				usql.append(appendOrdersNotInvoiced(subIndice, trx_Org_ID));

			if (usql.length() > 0)
				// Se insertan todas las líneas en la tabla.
				DB.executeUpdate(usql.toString(), get_TrxName());

		} catch (SQLException e) {
			log.log(Level.SEVERE, "Fill T_CuentaCorriente error", e);
			throw new Exception("Current Account Error", e);
		}
		return "";
	}

	private void calculateBalance() throws Exception {
		StringBuffer sqlBalance = new StringBuffer();
		sqlBalance
				.append(" SELECT COALESCE(SUM(t.Credit),0.0) AS Credit, COALESCE(SUM(t.Debit),0.0) AS Debit ");
		sqlBalance.append(" FROM ( ");
		sqlBalance.append(getCurrentAccountQuery().getAllDocumentsQuery(" AND d.DateTrx::date < ?::date "));
		sqlBalance.append(" ) t");

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sqlBalance.toString(), get_TrxName(), true);
			// Parámetros de sqlDoc
			int i = 1;
			pstmt.setInt(i++, debit_signo_issotrx);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, credit_signo_isotrx);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, getAD_Client_ID());
			pstmt.setInt(i++, p_C_BPartnerID);
			if(isOnlyCurrentAccountDocuments()){
				pstmt.setInt(i++, debit_signo_issotrx);
			}
			i = pstmtSetParam(i, p_AD_Org_ID, pstmt);
			i = pstmtSetParam(i, p_C_DocType_ID, pstmt);
			// Parámetros de sqlBalance
			pstmt.setTimestamp(i++, p_DateTrx_From);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				acumDebit = rs.getBigDecimal("Debit");
				acumCredit = rs.getBigDecimal("Credit");
				// ANTONIO: La cuenta es al reves acumBalance =
				// acumCredit.subtract(acumDebit);
				acumBalance = acumDebit.subtract(acumCredit);
			}

		} catch (SQLException e) {
			throw new Exception("Calculate previous balance error", e);
		}
	}

	private void sqlAppend(String clause, Object value, StringBuffer sql) {
		if (value != null)
			sql.append(clause);
	}

	private int pstmtSetParam(int index, Object value, PreparedStatement pstmt)
			throws SQLException {
		int i = index;
		if (value != null)
			pstmt.setObject(i++, value);
		return i;
	}

	/**
	 * Incorpora las sentencias sql correspondientes a los pedidos no
	 * facturados, ya sea de manera total o parcial, contemplando las
	 * referencias indicadas en las líneas de factura que apuntan hacia las
	 * lineas de pedido, de compra o de venta Para cada linea de pedido, ver
	 * cuantas lineas de factura las referencian (con sus cantidades facturadas
	 * y monto)
	 */
	protected StringBuffer appendOrdersNotInvoiced(int subIndice, int trx_Org_ID)
			throws Exception {
		StringBuffer query = new StringBuffer(
				" SELECT 	o.C_Order_ID, o.DocumentNo, o.DateAcct, o.C_DocType_ID, ")
				.append(" 			coalesce(currencyconvert(o.grandtotal - sum(matches.totalamtinvoiced), o.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), o.ad_client_id, o.ad_org_id),0) as pendingToInvoiceAmt, o.c_currency_id, o.grandtotal as Amount")
				.append(" FROM ")
				.append(" ( ")
				.append("				SELECT ol.c_orderline_id, ol.linetotalamt, ol.qtyordered, coalesce(sum(il.qtyinvoiced),0) as qtyinvoiced, coalesce(sum(il.linetotalamt),0) as totalamtinvoiced ")
				.append("				FROM C_OrderLine ol ")
				.append("				JOIN C_Order o ON ol.C_Order_ID = o.C_Order_ID ")
				.append("				LEFT JOIN C_InvoiceLine il ON ol.C_OrderLine_ID = il.C_OrderLine_ID ")
				.append("				LEFT JOIN C_Invoice i ON il.C_Invoice_ID = i.C_Invoice_ID ")
				.append("				WHERE ol.AD_Client_ID = ? ")
				.append("				AND o.DocStatus IN ('CO','CL', 'RE', 'VO') ")
				.append("				AND (i.C_Invoice_ID IS NULL OR i.DocStatus IN ('CO','CL', 'RE', 'VO')) ")
				.append(" 				AND o.C_BPartner_ID = ? ");
		sqlAppend("   	AND ? <= o.Dateacct ", p_DateTrx_From, query);
		sqlAppend("   	AND o.Dateacct <= ? ", p_DateTrx_To, query);
		sqlAppend("	AND o.AD_Org_ID = ? ", p_AD_Org_ID, query);
		query.append(
				"  GROUP BY ol.c_orderline_id, ol.linetotalamt, ol.qtyordered ")
				.append("				HAVING sum(il.qtyinvoiced) IS NULL OR ol.qtyordered - sum(il.qtyinvoiced) > 0 ")
				.append(" ) AS matches ")
				.append(" JOIN C_OrderLine ol ON ol.C_OrderLine_ID = matches.C_OrderLine_ID ")
				.append(" JOIN C_Order o ON o.C_Order_ID = ol.C_Order_ID ")
				.append(" GROUP BY o.C_Order_ID, o.DocumentNo, o.C_DocType_ID, o.DateAcct, o.C_Currency_ID, o.C_ConversionType_ID, o.AD_Client_ID, o.AD_Org_ID, o.GrandTotal ")
				.append(" ORDER BY o.C_Order_ID ASC ");

		int i = 1;
		PreparedStatement pstmt = DB.prepareStatement(query.toString());

		pstmt.setInt(i++, client_Currency_ID);
		pstmt.setInt(i++, getAD_Client_ID());
		pstmt.setInt(i++, p_C_BPartnerID);
		i = pstmtSetParam(i, p_DateTrx_From, pstmt);
		i = pstmtSetParam(i, p_DateTrx_To, pstmt);
		i = pstmtSetParam(i, p_AD_Org_ID, pstmt);

		ResultSet rs = pstmt.executeQuery();
		StringBuffer usql = new StringBuffer();
		while (rs.next()) {
			subIndice++;
			usql.append(" INSERT INTO T_CUENTACORRIENTE (SUBINDICE, IncludeOpenOrders, ShowDetailedReceiptsPayments, AD_CLIENT_ID, AD_ORG_ID, AD_PINSTANCE_ID, ISO_CODE, AMOUNT, DEBE, HABER, SALDO, NUMEROCOMPROBANTE, C_BPARTNER_ID, ACCOUNTTYPE, DATETRX, C_DOCTYPE_ID, C_INVOICE_ID, C_PAYMENT_ID, C_CASHLINE_ID, OnlyCurrentAccountDocuments) "
					+ " VALUES ("
					+ subIndice
					+ ", '"
					+ p_includeOpenOrders_char
					+ "', '"
					+ p_ShowDetailedReceiptsPayments_char
					+ "', "
					+ getAD_Client_ID()
					+ ", "
					+ trx_Org_ID
					+ " , "
					+ getAD_PInstance_ID()
					+ " ,'"
					+ MCurrency.getISO_Code(getCtx(),
							rs.getInt("C_Currency_ID"))
					+ "', "
					+ rs.getBigDecimal("Amount")
					+ ", "
					+ (p_AccountType.equalsIgnoreCase("C") ? BigDecimal.ZERO
							: rs.getBigDecimal("pendingToInvoiceAmt"))
					+ ", "
					+ (p_AccountType.equalsIgnoreCase("C") ? rs
							.getBigDecimal("pendingToInvoiceAmt")
							: BigDecimal.ZERO)
					+ ", "
					+ acumBalance
					+ ", '"
					+ rs.getString("DocumentNo")
					+ "', "
					+ p_C_BPartnerID
					+ ", '"
					+ p_AccountType
					+ "', '"
					+ rs.getTimestamp("DateAcct")
					+ "', "
					+ rs.getInt("C_DocType_ID") + ", " + " null, null, null"
					+ " , "
					+ "'"+(isOnlyCurrentAccountDocuments()?"Y":"N")+"'"
					+ "); ");
		}

		return usql;
	}

	protected CurrentAccountQuery getCurrentAccountQuery() {
		return currentAccountQuery;
	}

	protected void setCurrentAccountQuery(CurrentAccountQuery currentAccountQuery) {
		this.currentAccountQuery = currentAccountQuery;
	}

	protected boolean isOnlyCurrentAccountDocuments() {
		return onlyCurrentAccountDocuments;
	}

	protected void setOnlyCurrentAccountDocuments(
			boolean onlyCurrentAccountDocuments) {
		this.onlyCurrentAccountDocuments = onlyCurrentAccountDocuments;
	}

}
