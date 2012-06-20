package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.openXpertya.model.X_T_Allocation_Report;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class AllocationsReport extends SvrProcess {

	
	/*
	 *	Semántica de las columnas:
	 *		Debitos | Creditos | Imputado | Por imputar
	 *
	 * 	Debitos: Monto del documento de débito
	 *  Créditos: Monto del documento de crédito 
	 *  Imputado: Columna que lleva la sumatoria correspondiente para poder determinar en balance EN EL PERIODO ESPECIFICADO
	 *  Por imputar: Monto sin cancelar del documento
	 */
	 
	 	
	
	
	
	private static Map<Integer,String> docCategories;
	
	/** Tipos de documento segun columna documenttable de v_documents */
	protected static final String DOC_INVOICE = "C_Invoice";
	protected static final String DOC_PAYMENT = "C_Payment";
	protected static final String DOC_CASHLINE = "C_CashLine";
	
	static {
		docCategories = new HashMap<Integer,String>();
		docCategories.put(1, X_T_Allocation_Report.DOCALLOCATIONCATEGORY_TotalPaidDebits);
		docCategories.put(2, X_T_Allocation_Report.DOCALLOCATIONCATEGORY_PartialPaidDebits);
		docCategories.put(3, X_T_Allocation_Report.DOCALLOCATIONCATEGORY_UnpaidDebits);
		docCategories.put(4, X_T_Allocation_Report.DOCALLOCATIONCATEGORY_NotAllocatedCredits);
	}

	/** Descripción de línea de caja a Proveedor. Este texto es el que se almacena en la
	 * tabla del reporte para cada comprobante que sea un línea de caja a Proveedor. */
	private final String DOC_CASHLINE_VENDOR   = Msg.parseTranslation(Env.getCtx(),"@C_CashLine_ID@ (@IsVendor@)"); 
	/** Descripción de línea de caja a Cliente. Este texto es el que se almacena en la
	 * tabla del reporte para cada comprobante que sea un línea de caja a Cliente. */
	private final String DOC_CASHLINE_CUSTOMER = Msg.parseTranslation(Env.getCtx(),"@C_CashLine_ID@ (@IsCustomer@)");
	
	/** Entidad Comercial de los comprobantes a consultar */
	private Integer p_C_BPartnerID;
	/** Fecha inicial del rango de fechas de la transacción */
	private Timestamp  p_DateTrx_From;
	/** Fecha final del rango de fechas de la transacción */
	private Timestamp  p_DateTrx_To;
	/** Organización de los comprobantes a consultar */
	private Integer    p_AD_OrgTrx_ID;
	/** Tipo de documento de los comprobantes a consultar */
	private Integer    p_C_DocType_ID;
	/** Tipo de Cuenta del Reporte: Cliente o Proveedor */
	private String     p_AccountType;
	/** Bloque del reporte a mostrar: Débitos totalmente pagados, Débitos parcialmente
	 * pagados, Débitos sin pagar, Créditos sin aplicar. */
	private String     p_DocAllocationCategory; 

	/** Signo de documentos que son débitos (depende de p_AccountType) */
	private int debit_signo_issotrx;
	/** Signo de documentos que son créditos (depende de p_AccountType) */
	private int credit_signo_isotrx;
	/** Moneda en la que trabaja la compañía */
	private int client_Currency_ID;

	
	@Override
	protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) ;
            else if( name.equalsIgnoreCase( "C_BPartner_ID" )) {
            	p_C_BPartnerID = ((BigDecimal)para[ i ].getParameter()).intValue();
            } else if( name.equalsIgnoreCase( "DateTrx" )) {
            	p_DateTrx_From = ( Timestamp )para[ i ].getParameter();
            	p_DateTrx_To = ( Timestamp )para[ i ].getParameter_To();
            } else if( name.equalsIgnoreCase( "AccountType" )) {
        		p_AccountType = ( String )para[ i ].getParameter();
        	} else if( name.equalsIgnoreCase( "AD_OrgTrx_ID" )) {
        		BigDecimal tmp = ( BigDecimal )para[ i ].getParameter();
        		p_AD_OrgTrx_ID = tmp == null ? null : tmp.intValue();
        	} else if( name.equalsIgnoreCase( "C_DocType_ID" )) {
        		BigDecimal tmp = ( BigDecimal )para[ i ].getParameter(); 
        		p_C_DocType_ID = tmp == null ? null : tmp.intValue();
            } else if( name.equalsIgnoreCase( "DocAllocationCategory" )) {
        		p_DocAllocationCategory = ( String )para[ i ].getParameter();
            } else {	
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }
        
		// Reporte de Cta Corriente de Cliente o Proveedor.
		// +-------------------------+--------------------------+
		// |  Cta Corriente Cliente  |  Cta Corriente Proveedor |
		// +-------------------------+--------------------------+
		// |   Debitos  = Signo 1    |   Debitos  = Signo -1    |
		// |   Créditos = Signo -1   |   Créditos = Signo 1     |
		// +-------------------------+--------------------------+
		debit_signo_issotrx = p_AccountType.equalsIgnoreCase("C") ? 1 : -1;
		credit_signo_isotrx = p_AccountType.equalsIgnoreCase("C") ? -1 : 1;
		
		// Moneda de la compañía utilizada para conversión de montos de documentos.
		client_Currency_ID = Env.getContextAsInt(getCtx(), "$C_Currency_ID");
	}

	@Override
	protected String doIt() throws Exception {

		// delete all rows older than a week
		DB.executeUpdate("DELETE FROM T_Allocation_Report WHERE Created < ('now'::text)::timestamp(6) - interval '7 days'");		
		// delete all rows in table with the given ad_pinstance_id
		DB.executeUpdate("DELETE FROM T_Allocation_Report WHERE AD_PInstance_ID = " + getAD_PInstance_ID());

		// Consulta para obtener los datos de los comprobantes del reporte.
		// Esta consulta es solo una vista que se utiliza luego en la consulta
		// que categoriza cada comprobante en una sección del Reporte (1, 2, 3, o 4)
		// De esta forma, los comprobantes resultantes se encuentran desordenados, 
		// calculándose el monto convertido a la moneda de la compañía, el monto pendiente
		// y la determinación de si es un Débito o Crédito. 
		// Es aquí donde se filtran los comprobantes según los parámetros dado que las
		// operaciones de cálculo de monto pendiente y conversión de monedas son costosas,
		// y se debe tratar de aplicarlas en la menor cantidad de tuplas posibles.
		StringBuffer sqlView = new StringBuffer();
		sqlView.append(" SELECT "); 
		sqlView.append("	d.AD_Org_ID AS AD_OrgTrx_ID, ");
		sqlView.append("	d.C_BPartner_ID, ");
		sqlView.append("	d.DocumentTable, ");
		sqlView.append("	d.Document_ID, ");
		sqlView.append("	d.Signo_IsSOTrx, ");
		sqlView.append("    CASE WHEN d.Signo_IsSOTrx = ? THEN 'Y' ELSE 'N' END ::character(1) AS IsDebit, ");
	    sqlView.append("	d.C_DocType_ID, ");
	    sqlView.append("	d.DocTypeName, ");
	    sqlView.append("	d.DocumentNo, ");
	    sqlView.append("	d.Dateacct as DateTrx, ");
	    sqlView.append("	currencyconvert(d.amount, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) AS Amount, ");
	    sqlView.append("	CASE  ");
	    sqlView.append("		  WHEN d.DocumentTable = 'C_Invoice' THEN ");
	    sqlView.append("               currencyconvert(ABS(invoiceOpen(d.Document_ID,d.C_InvoicePaySchedule_ID)), d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) ");
	    sqlView.append("		  WHEN d.DocumentTable = 'C_Payment' THEN ");
	    sqlView.append("               currencyconvert(ABS(paymentAvailable(d.Document_ID)), d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) ");
	    sqlView.append("		  WHEN d.DocumentTable = 'C_CashLine' THEN ");
	    sqlView.append("               currencyconvert(ABS(cashlineAvailable(d.Document_ID)), d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) ");
	    sqlView.append("	END :: numeric(22,2) AS Balance, ");
	    sqlView.append("    d.C_InvoicePaySchedule_ID ");
	    sqlView.append(" FROM V_Documents d ");
	    sqlView.append(" WHERE d.DocStatus IN ('CO','CL') "); 
	    sqlView.append("   AND d.AD_Client_ID = ? ");
		sqlView.append("   AND d.C_Bpartner_ID = ? ");
	    sqlAppend     ("   AND d.AD_OrgTrx_ID = ? ", p_AD_OrgTrx_ID, sqlView);
	    sqlAppend     ("   AND d.C_DocType_ID = ? ", p_C_DocType_ID, sqlView);
		sqlAppend     ("   AND ? <= d.Dateacct ", p_DateTrx_From, sqlView);
		sqlAppend     ("      AND d.Dateacct <= ? ", p_DateTrx_To, sqlView);
	    
		// Consulta Principal. A partir de la vista definida anteriormente, determina
		// en que sección del reporte se incluirá el comprobante. Puede suceder el caso
		// de que un comprobante no cumpla con ninguna de las condiciones de los bloques
		// del reporte, entonces se filtran estos comprobantes quedando solo los que
		// se deben incluir en el reporte.
		// Finalmente aquí se efectua la ordenación de los datos, primero según la sección
		// del reporte y luego por fecha, tipo y número de documento.
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * ");
		sql.append(" FROM ( ");
		sql.append(" 	SELECT "); 
		sql.append("		CASE "); 
		sql.append("			WHEN v.IsDebit = 'Y' AND v.balance = 0 THEN 1 "); 
		sql.append("    		WHEN v.IsDebit = 'Y' AND v.balance > 0 AND v.balance <> v.amount THEN 2 ");
		sql.append("    		WHEN v.IsDebit = 'Y' AND v.balance = v.amount THEN 3 ");
		sql.append("    		WHEN v.IsDebit = 'N' AND v.balance = v.amount THEN 4 ");
		sql.append("    		ELSE null ");
		sql.append("    	END ::integer AS ReportSection, ");
		sql.append("    	v.* ");
		sql.append(" 	FROM (").append(sqlView).append(") v");
		sql.append(" ) x ");
		sql.append(" WHERE x.ReportSection IS NOT NULL " );
		sql.append(" ORDER BY x.ReportSection ASC, x.DateTrx ASC, x.DocTypeName, x.DocumentNo ASC ");   
	
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
		
	    try {
	    	
	    	pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			// Parámetros de sqlView
			pstmt.setInt(i++, debit_signo_issotrx);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, getAD_Client_ID());
			pstmt.setInt(i++, p_C_BPartnerID);
			i = pstmtSetParam(i, p_AD_OrgTrx_ID, pstmt);
			i = pstmtSetParam(i, p_C_DocType_ID, pstmt);
			i = pstmtSetParam(i, p_DateTrx_From, pstmt);
			i = pstmtSetParam(i, p_DateTrx_To, pstmt);

			rs = pstmt.executeQuery();
			
			X_T_Allocation_Report line   = null;
			boolean isDebit              = false;
			BigDecimal amount            = null;
			BigDecimal balance           = null;
			String document              = null;
			String docAllocationCategory = null;
			int signo_issotrx            = 0;
			DebitDocument debitDocument  = null;
			
			
			while(rs.next()) {
				
				isDebit = "Y".equals(rs.getString("IsDebit"));
				balance = rs.getBigDecimal("Balance");
				amount = rs.getBigDecimal("Amount");
				signo_issotrx = rs.getInt("Signo_IsSOTrx");
				// Traducción entre el nro de sección de reporte y la categoria de imputación
				// del documento (sección (numerico) -> categoria (string)).
				docAllocationCategory = docCategories.get(rs.getInt("ReportSection"));
				// Descripción del documento. Es la descripción que se visualiza en el reporte.
				document = getDocumentDescription(
						rs.getString("DocumentTable"),
						rs.getString("DocTypeName"),
						rs.getString("DocumentNo"),
						signo_issotrx);
	
				line = new X_T_Allocation_Report(getCtx(), 0, get_TrxName());
				line.setAD_PInstance_ID(getAD_PInstance_ID());
				line.setAD_OrgTrx_ID(rs.getInt("AD_OrgTrx_ID"));
				line.setC_BPartner_ID(rs.getInt("C_BPartner_ID"));
				line.setC_DocType_ID(rs.getInt("C_DocType_ID"));
				line.setDateTrx(rs.getTimestamp("DateTrx"));
				line.setAccountType(p_AccountType);
				line.setDocAllocationCategory(docAllocationCategory);

				// La linea es de una factura? un pago? una linea de caja?
				if (DOC_INVOICE.equals(rs.getString("documenttable")))
					line.setC_Invoice_ID(rs.getInt("document_id"));
				if (DOC_PAYMENT.equals(rs.getString("documenttable")))
					line.setC_Payment_ID(rs.getInt("document_id"));
				if (DOC_CASHLINE.equals(rs.getString("documenttable")))
					line.setC_CashLine_ID(rs.getInt("document_id"));
				
				line.setDebitDocument(document.trim());
				line.setDebitDocumentNo(rs.getString("DocumentNo"));
				
				// Asigno los datos si es un Débito.
				if (isDebit) {
					line.setDebit(amount);
					line.setBalance(amount);
					line.setCurrentOpenAmt(balance);
				// Para los créditos no se debe mostrar el Saldo.
				} else { //isCredit
					line.setCredit(amount);
					line.setBalance(new BigDecimal(-1).multiply(amount));
					line.setCurrentOpenAmt(balance);
				}
				
				if(!line.save()) {
					log.severe("T_Allocation_Report line save error");
					throw new Exception("@ProcessRunError@");
				}
				// Si el documento es un débito, entonces se calculan las líneas de crédito
				// asociadas al documento.
				if (isDebit)
					createCreditLines(new DebitDocument(rs, docAllocationCategory));
			}
	    	
	    } catch (SQLException e) {
			log.log(Level.SEVERE, "Fill T_CuentaCorriente error", e);
			throw new Exception("@ProcessRunError@",e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
		return "";
	}

	private void createCreditLines(DebitDocument debitDocument) throws Exception, SQLException {
		// Aquí se guardan las líneas de crédito a partir del documento de débito. 
		List<CreditLine> creditLines = new ArrayList<CreditLine>();
		
		// Consulta para recorrer las líneas de imputaciones completadas.
		// Luego cada método específico aplicará su filtro personalizado.
		StringBuffer mainSql = new StringBuffer();
		mainSql.append(" SELECT ");
		mainSql.append("   al.C_Invoice_ID, ");
		mainSql.append("   al.C_Invoice_Credit_ID, ");
		mainSql.append("   al.C_Payment_ID, ");
		mainSql.append("   al.C_CashLine_ID, ");
		mainSql.append("   currencyconvert((al.amount + al.discountamt + al.writeoffamt),ah.C_Currency_ID, ?, ('now'::text)::timestamp(6) with time zone, 0, al.ad_client_id, al.ad_org_id) AS TotalAmount, ");
		mainSql.append("   currencyconvert(al.amount, ah.C_Currency_ID, ?, ('now'::text)::timestamp(6) with time zone, 0, al.ad_client_id, al.ad_org_id) AS Amount ");
		mainSql.append(" FROM C_AllocationLine al ");
		mainSql.append(" INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID) ");
		mainSql.append(" WHERE ah.DocStatus IN ('CO','CL') ");
		
		// Si el débito es Factura (ND, NC), se buscan las líneas de imputación contra 
		// la factura y se crean en la lista de líneas de crédito. 
		if(debitDocument.documentTable.equals("C_Invoice"))
			createInvoiceCreditLines(mainSql, debitDocument, creditLines);
		
		// Si el débito es un Pago, se buscan las líneas de imputación contra el pago
		// y se crea la lista de líneas de créditos.
		else if(debitDocument.documentTable.equals("C_Payment"))
			createPaymentCreditLines(mainSql, debitDocument, creditLines);

		// Si el débito es un CashLine, se buscan las líneas de imputación contra el Cashline
		// y se crea la lista de líneas de créditos.
		else if(debitDocument.documentTable.equals("C_CashLine"))
			createCashLineCreditLines(mainSql, debitDocument, creditLines);
		
		// Se ordena la collección según el siguiente criterio.
		// 1. Fecha de Transacción.
		// 2. Descripción del documento.
		Collections.sort(creditLines, creditLinesComparator);
		
		// Se crean las líneas en la tabla temporal del reporte a partir de la colección
		// ordenada de créditos del débito actual.
		X_T_Allocation_Report line = null;
		for (CreditLine creditLine : creditLines) {
			line = new X_T_Allocation_Report(getCtx(), 0, get_TrxName());
			line.setAD_PInstance_ID(getAD_PInstance_ID());
			line.setAD_OrgTrx_ID(creditLine.orgTrx_ID);
			line.setC_BPartner_ID(creditLine.bPartner_ID);
			// El tipo de doc del crédito lo pongo igual al de su respectivo débito
			// de modo que al aplicarse el filtro del C_DocType en los parámetros
			// del reporte, solo se filtren débitos según el DocType seleccionado y 
			// se muestren los créditos asociados al débito.
			line.setC_DocType_ID(debitDocument.docTypeID);
			line.setDateTrx(creditLine.dateTrx);
			line.setAccountType(p_AccountType);
			line.setDocAllocationCategory(debitDocument.docAllocationCategory);
			// No es un débito
			line.setDebitDocument(null);
			line.setDebitDocumentNo(null);
			line.setDebit(null);
			// Datos del crédito
			line.setCreditDocument(creditLine.docDescription);
			line.setCreditDocumentNo(creditLine.documentNo);
			line.setCredit(creditLine.amount);
			line.setBalance(new BigDecimal(-1).multiply(creditLine.amount));
			
			// La linea es de una factura? un pago? una linea de caja?
			if (DOC_INVOICE.equals(creditLine.documentTable))
				line.setC_Invoice_ID(creditLine.documentID);
			if (DOC_PAYMENT.equals(creditLine.documentTable))
				line.setC_Payment_ID(creditLine.documentID);
			if (DOC_CASHLINE.equals(creditLine.documentTable))
				line.setC_CashLine_ID(creditLine.documentID);
			
			if(!line.save()) {
				log.severe("T_Allocation_Report credit line save error (" + line.getCreditDocument() + ")");
				throw new Exception("@ProcessRunError@");
			}
		}
	}
	
	private void createInvoiceCreditLines(StringBuffer sql, DebitDocument debitInvoice, List<CreditLine> creditLines) throws SQLException {
		
		// Agrego el where para filtrar las líneas de imputación de la factura.
		sql.append(" AND (al.C_Invoice_ID = ? OR al.C_Invoice_Credit_ID = ?) ");
		sql.append(" ORDER BY ah.DateTrx ASC, ah.Created ASC ");
		
		int debitInvoiceID = debitInvoice.documentID;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			// Parametro de mainSql
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			// Parámetros específicos de este método.
			pstmt.setInt(i++, debitInvoiceID);
			pstmt.setInt(i++, debitInvoiceID);
			
			rs = pstmt.executeQuery();
			Integer invoiceID;
			Integer invoiceCreditID;
			Integer paymentID;
			Integer cashLineID;
			CreditLine creditLine = null;
			
			while(rs.next()) {
				// Obtengo la factura utilizada como crédito. Puede estar en C_Invoice_ID,
				// C_Invoice_Credit_ID o no existir (cuando el crédito es un Pago o CashLine).
				invoiceID       = (Integer)rs.getObject("C_Invoice_ID");
				invoiceCreditID = (Integer)rs.getObject("C_Invoice_Credit_ID");
				invoiceID       = invoiceID == debitInvoiceID ? invoiceCreditID : invoiceID;
				// Obtengo el Pago y la CashLine.
				paymentID       = (Integer)rs.getObject("C_Payment_ID");
				cashLineID      = (Integer)rs.getObject("C_CashLine_ID");
				
				// La imputación se puede dar solo con uno de los 3 documentos previamente
				// obtenidos.
				// - Imputación: Factura -> Factura
				if (invoiceID != null) {
					creditLine = createCreditLineFromInvoice(invoiceID);
					creditLine.documentID = invoiceID;
					creditLine.documentTable = DOC_INVOICE;
				// - Imputación: Factura -> Pago 
				} else if (paymentID != null) {
					creditLine = createCreditLineFromPayment(paymentID);
					creditLine.documentID = paymentID;
					creditLine.documentTable = DOC_PAYMENT;
				// - Imputación: Factura -> CashLine
				} else if (cashLineID != null) {
					creditLine = createCreditLineFromCashLine(cashLineID);
					creditLine.documentID = cashLineID;
					creditLine.documentTable = DOC_CASHLINE;
				} else {
					log.warning("Invalid Allocation Line (C_Invoice_ID = NULL, C_Payment_ID = NULL, C_CashLine_ID = NULL");
					continue;
				}
				
				// Se asigna el importe de la imputación a la línea de crédito.
				creditLine.amount = rs.getBigDecimal("TotalAmount");
				// Se agrega la línea a la lista de líneas de crédito
				creditLines.add(creditLine);
			}

			// Si la factura débito tiene un esquema de pagos asociado (está dividida
			// en cuotas), entonces se recalculan los créditos para esta cuota de factura a partir
			// de los créditos que tiene TODA la factura.
			if (debitInvoice.invoicePayScheduleID != null)
				calculateInvoicePayScheduleAllocations(debitInvoice, creditLines);
		
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	private void createPaymentCreditLines(StringBuffer sql, DebitDocument debitPayment, List<CreditLine> creditLines) throws SQLException {
		// Agrego el where para filtrar las líneas de imputación de la factura.
		sql.append("AND (al.C_Payment_ID = ?)");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			// Parametro de mainSql
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			// Parámetros específicos de este método.
			pstmt.setInt(i++, debitPayment.documentID);
						
			rs = pstmt.executeQuery();
			Integer invoiceID;
			Integer invoiceCreditID;
			Integer cashLineID;
			CreditLine creditLine = null;
			
			while(rs.next()) {
				// Obtengo la factura utilizada como crédito. Puede estar en C_Invoice_ID,
				// C_Invoice_Credit_ID.
				invoiceID       = (Integer)rs.getObject("C_Invoice_ID");
				invoiceCreditID = (Integer)rs.getObject("C_Invoice_Credit_ID");
				invoiceID       = invoiceID != null ? invoiceID : invoiceCreditID;
				// Obtengo el la CashLine.
				cashLineID      = (Integer)rs.getObject("C_CashLine_ID");
				
				// La imputación se puede dar solo con uno de los 2 documentos previamente
				// obtenidos.
				// - Imputación: Pago -> Factura
				if (invoiceID != null) {
					creditLine = createCreditLineFromInvoice(invoiceID);
					creditLine.documentID = invoiceID;
					creditLine.documentTable = DOC_INVOICE;
				// - Imputación: Pago -> CashLine 
				} else if (cashLineID != null) {
					creditLine = createCreditLineFromCashLine(cashLineID);
					creditLine.documentID = cashLineID;
					creditLine.documentTable = DOC_CASHLINE;
				} else {
					log.warning("Invalid Allocation Line (C_Invoice_ID = NULL, C_Payment_ID = NULL, C_CashLine_ID = NULL");
					continue;
				}
				
				// Se asigna el importe de la imputación a la línea de crédito.
				creditLine.amount = rs.getBigDecimal("Amount");
				// Se agrega la línea a la lista de líneas de crédito
				creditLines.add(creditLine);
			}

		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	private void createCashLineCreditLines(StringBuffer sql, DebitDocument debitCashLine, List<CreditLine> creditLines) throws SQLException {
		// Agrego el where para filtrar las líneas de imputación de la factura.
		sql.append("AND (al.C_CashLine_ID = ?)");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			int i = 1;
			// Parametro de mainSql
			pstmt.setInt(i++, client_Currency_ID);
			pstmt.setInt(i++, client_Currency_ID);
			// Parámetros específicos de este método.
			pstmt.setInt(i++, debitCashLine.documentID);
						
			rs = pstmt.executeQuery();
			Integer invoiceID;
			Integer invoiceCreditID;
			Integer paymentID;
			CreditLine creditLine = null;
			
			while(rs.next()) {
				// Obtengo la factura utilizada como crédito. Puede estar en C_Invoice_ID,
				// C_Invoice_Credit_ID.
				invoiceID       = (Integer)rs.getObject("C_Invoice_ID");
				invoiceCreditID = (Integer)rs.getObject("C_Invoice_Credit_ID");
				invoiceID       = invoiceID != null ? invoiceID : invoiceCreditID;
				// Obtengo el Pago.
				paymentID       = (Integer)rs.getObject("C_Payment_ID");
				
				// La imputación se puede dar solo con uno de los 2 documentos previamente
				// obtenidos.
				// - Imputación: CashLine -> Factura
				if (invoiceID != null) {
					creditLine = createCreditLineFromInvoice(invoiceID);
					creditLine.documentID = invoiceID;
					creditLine.documentTable = DOC_INVOICE;
				// - Imputación: CashLine -> Pago 
				} else if (paymentID != null) {
					creditLine = createCreditLineFromPayment(paymentID);
					creditLine.documentID = paymentID;
					creditLine.documentTable = DOC_PAYMENT;
				} else {
					log.warning("Invalid Allocation Line (C_Invoice_ID = NULL, C_Payment_ID = NULL, C_CashLine_ID = NULL");
					continue;
				}
				
				// Se asigna el importe de la imputación a la línea de crédito.
				creditLine.amount = rs.getBigDecimal("Amount");
				// Se agrega la línea a la lista de líneas de crédito
				creditLines.add(creditLine);
			}

		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	
	private CreditLine createCreditLineFromInvoice(int invoiceID) throws SQLException {
		// Obtengo la información de la factura.
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append("   i.AD_Org_ID AS AD_OrgTrx_ID, ");
		sql.append("   i.C_BPartner_ID, ");
		sql.append("   i.C_DocType_ID, ");
		sql.append("   dt.Name AS DocTypeName, ");
		sql.append("   i.DocumentNo, ");
		sql.append("   i.DateInvoiced AS DateTrx, ");
		sql.append("   dt.signo_IsSOTrx, ");
		sql.append("   'C_Invoice' AS DocumentTable ");
		sql.append(" FROM C_Invoice i ");
		sql.append(" INNER JOIN C_DocType dt ON (i.C_DocType_ID = dt.C_DocType_ID) ");
		sql.append(" WHERE i.C_Invoice_ID = ? ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Línea de crédito a retornar.
		CreditLine creditLine = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, invoiceID);
			rs = pstmt.executeQuery();
			// Si no hay resultados retorna null
			if (rs.next()) {
				// Creo la línea a partir del resultset.
				creditLine = new CreditLine(rs);
			}
			return creditLine;
		
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	private CreditLine createCreditLineFromPayment(int paymentID) throws SQLException {
		// Obtengo la información del Pago.
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append("   p.AD_Org_ID AS AD_OrgTrx_ID, ");
		sql.append("   p.C_BPartner_ID, ");
		sql.append("   p.C_DocType_ID, ");
		sql.append("   dt.Name AS DocTypeName, ");
		sql.append("   p.DocumentNo, ");
		sql.append("   p.DateTrx AS DateTrx, ");
		sql.append("   dt.Signo_IsSOTrx, ");
		sql.append("   'C_Payment' AS DocumentTable ");
		sql.append(" FROM C_Payment p ");
		sql.append(" INNER JOIN C_DocType dt ON (p.C_DocType_ID = dt.C_DocType_ID) ");
		sql.append(" WHERE p.C_Payment_ID = ?");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Línea de crédito a retornar.
		CreditLine creditLine = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, paymentID);
			rs = pstmt.executeQuery();
			// Si no hay resultados retorna null
			if (rs.next()) {
				// Creo la línea a partir del resultset.
				creditLine = new CreditLine(rs);
			}
			return creditLine;
		
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}
	
	private CreditLine createCreditLineFromCashLine(int cashLineID) throws SQLException {
		// Obtengo la información del CashLine.
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT ");
		sql.append("   d.AD_Org_ID AS AD_OrgTrx_ID, ");
		sql.append("   d.C_BPartner_ID, ");
		sql.append("   d.C_DocType_ID, ");
		sql.append("   d.DocTypeName, ");
		sql.append("   d.DocumentNo, ");
		sql.append("   d.DateTrx AS DateTrx, ");
		sql.append("   d.signo_IsSOTrx, ");
		sql.append("   d.DocumentTable ");
		sql.append(" FROM V_Documents d ");
		sql.append(" WHERE d.DocumentTable = 'C_CashLine' ");  
		sql.append("   AND d.Document_ID = ? ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		// Línea de crédito a retornar.
		CreditLine creditLine = null;
		
		try {
			pstmt = DB.prepareStatement(sql.toString(), get_TrxName());
			pstmt.setInt(1, cashLineID);
			rs = pstmt.executeQuery();
			// Si no hay resultados retorna null
			if (rs.next()) {
				// Creo la línea a partir del resultset.
				creditLine = new CreditLine(rs);
			}
			return creditLine;
		
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}

	private void sqlAppend(String clause, Object value, StringBuffer sql) {
		if (value != null)
			sql.append(clause);
	}
	
	private int pstmtSetParam(int index, Object value, PreparedStatement pstmt) throws SQLException {
		int i = index;
		if (value != null)
			pstmt.setObject(i++, value);
		return i;
	}
	
	/**
	 * A partir de algunos datos del documento retorna la descripción que mostrará el reporte
	 * @param documentTable Tabla del documento.
	 * @param docTypeName Nombre del tipo de documento.
	 * @param documentNo Nro. del documento.
	 * @param signo_issotrx Signo del documento.
	 */
	private String getDocumentDescription(String documentTable, String docTypeName, String documentNo, int signo_issotrx) {
		String document = "";
		if (documentTable.equals("C_CashLine")) {
			document = signo_issotrx == 1 ? DOC_CASHLINE_VENDOR : DOC_CASHLINE_CUSTOMER; 
		} else
			document = docTypeName.trim() + " " + documentNo.trim();
		return document;
	}
	
	private void calculateInvoicePayScheduleAllocations(DebitDocument debitInvoice, List<CreditLine> creditLines) throws SQLException {
		// Lista temporal de créditos para luego borrar los créditos que quedan con
		// importe cero.
		List<CreditLine> auxCredits = new ArrayList<CreditLine>();
		auxCredits.addAll(creditLines);
		
		// Consulta para obtener la cuotas de la factura con sus montos, ordenadas por la fecha
		// de vencimiento.
		StringBuffer sqlPaySched = new StringBuffer();
		sqlPaySched.append(" SELECT d.DateTrx, d.C_InvoicePaySchedule_ID, d.DueDate, ");
		sqlPaySched.append("        ABS(currencyconvert(d.amount, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) AS Amount ");
		sqlPaySched.append(" FROM V_Documents d  ");
		sqlPaySched.append(" WHERE d.Document_ID = ? ");
		sqlPaySched.append(" ORDER BY d.DateTrx ASC, d.Created ASC");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean calculated = false;
		
		try {
			pstmt = DB.prepareStatement(sqlPaySched.toString(), get_TrxName());
			pstmt.setInt(1, client_Currency_ID);
			pstmt.setInt(2, debitInvoice.documentID);
			
			rs = pstmt.executeQuery();
			// Recorro las cuotas en orden hasta encontrar la cuota que necesito
			// calcular.
			while(rs.next() && !calculated) {
				int invoicePaySchedule_ID = rs.getInt("C_InvoicePaySchedule_ID");
				BigDecimal amount = rs.getBigDecimal("Amount");
				
				// La cuota actual no es la cuota de la que se quieren obtener los
				// créditos. Se reducen los montos de los créditos aplicándolos
				// a esta cuota.
				if (invoicePaySchedule_ID != debitInvoice.invoicePayScheduleID) {
					for (Iterator credits = auxCredits.iterator(); credits.hasNext() && amount.compareTo(BigDecimal.ZERO) != 0;) {
						CreditLine creditLine = (CreditLine) credits.next();
						// Se ignoran créditos que ya esten completamente asignados con una
						// cuota.
						if (creditLine.amount.compareTo(BigDecimal.ZERO) == 0) 
							continue;
						
						// El importe del crédito supera o iguala al importe de la cuota de la factura.
						if (creditLine.amount.compareTo(amount) >= 0) {
							creditLine.amount = creditLine.amount.subtract(amount); 
							amount = BigDecimal.ZERO;
						} else {
							amount = amount.subtract(creditLine.amount);
							creditLine.amount = BigDecimal.ZERO;
						}
					}
				
				// La cuota actual es la que queremos calcular.
				} else {
					for (Iterator credits = auxCredits.iterator(); credits.hasNext();) {
						CreditLine creditLine = (CreditLine) credits.next();
						// Se ignoran créditos que ya esten completamente asignados con una
						// cuota.
						if (creditLine.amount.compareTo(BigDecimal.ZERO) == 0) 
							continue;
						
						// Aún queda monto por aplicar
						if (amount.compareTo(BigDecimal.ZERO) > 0) {
							// Si el monto del crédito es mayor o igual a lo que resta imputar
							// a la cuota, entonces el crédito se reduce al monto a aplicar
							// y la cuota queda totalmente aplicada.
							if (creditLine.amount.compareTo(amount) >= 0) {
								creditLine.amount = amount;
								amount = BigDecimal.ZERO;
							// Si es menor, entonces se reduce el monto a aplicar de la cuota
							// y el crédito queda con el mismo monto.	
							} else {
								amount = amount.subtract(creditLine.amount);
							}
						// La totalidad de la cuota ya fué aplicada, entonces se anula el
						// importe de este crédito dado que no está asociado con la cuota
						} else {
							creditLine.amount = BigDecimal.ZERO;
						}
					}
					// Se indica que debe finalizar con la iteración de cuotas dado que la 
					// cuota últimamente calculada es la que se estaba buscando.
					calculated = true;
				}
			} // while(rs)
			
			// Elimino de la lista de créditos resultantes los créditos que tienen importe
			// en cero (dado que fueron reducidos con cuotas anteriores o estan de sobra
			// para esta cuota)
			for (CreditLine creditLine : auxCredits) {
				if (creditLine.amount.compareTo(BigDecimal.ZERO) == 0) {
					creditLines.remove(creditLine);
				}
			}	
			
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Tipo de dato interno para la creación en memoria de las líneas de crédito
	 * de un débito.
	 */
	private class CreditLine {
		protected Integer    orgTrx_ID;
		protected Integer    bPartner_ID;
		protected Integer    docType_ID;
		protected String     docTypeName;
		protected String     documentNo;
		protected String     docDescription;
		protected int        signo_IsSOTrx;
		protected Timestamp  dateTrx;
		protected BigDecimal amount;
		protected int 		 documentID;
		protected String	 documentTable;
		
		public CreditLine(ResultSet rs) throws SQLException {
			this.orgTrx_ID      = rs.getInt("AD_OrgTrx_ID");
			this.bPartner_ID    = rs.getInt("C_BPartner_ID");
			this.docType_ID     = rs.getInt("C_DocType_ID");
			this.docTypeName    = rs.getString("DocTypeName");
			this.documentNo     = rs.getString("DocumentNo");
			this.dateTrx        = rs.getTimestamp("DateTrx");
			this.signo_IsSOTrx  = rs.getInt("signo_IsSOTrx");
			this.docDescription = getDocumentDescription(
					rs.getString("DocumentTable"),
					rs.getString("DocTypeName"),
					rs.getString("DocumentNo"),
					rs.getInt("Signo_IsSOTrx"));
		}

		@Override
		public String toString() {
			return docDescription + "; AMT = " + amount;
		}
		
		
	}
	
	/**
	 * Tipo de Dato interno que representa una línea de documento de débito.
	 */
	private class DebitDocument {
		protected int documentID;
		protected String documentTable;
		protected BigDecimal amount;
		protected BigDecimal balance;
		protected Integer invoicePayScheduleID;
		protected String docAllocationCategory;
		protected Timestamp dateTrx;
		protected int docTypeID;
		
		public DebitDocument(ResultSet rs, String docAllocationCategory) throws SQLException {
			this.documentID            = rs.getInt("Document_ID");
			this.documentTable         = rs.getString("DocumentTable");
			this.amount                = rs.getBigDecimal("Amount");
			this.balance               = rs.getBigDecimal("Balance");
			this.invoicePayScheduleID  = (Integer)rs.getObject("C_InvoicePaySchedule_ID");
			this.dateTrx               = rs.getTimestamp("DateTrx");
			this.docAllocationCategory = docAllocationCategory;
			this.docTypeID            = rs.getInt("C_DocType_ID");
			
		}

		@Override
		public String toString() {
			return documentTable + "[ID = " + documentID + "; AMT = " + amount + "]"; 
		}
		
		
	}
	
	private Comparator<CreditLine> creditLinesComparator = new Comparator<CreditLine>() {

		public int compare(CreditLine cl1, CreditLine cl2) {
			int cmp = 0;
			cmp = cl1.dateTrx.compareTo(cl2.dateTrx);
			cmp = (cmp == 0 ? cl1.docDescription.compareTo(cl2.docDescription) : cmp);
			return cmp;
		}
	
	};
}
