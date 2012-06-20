package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.logging.Level;
import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.X_T_EstadoDeCuenta;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;



public class EstadoDeCuentaProcess extends SvrProcess {

	protected static final int MAX_DUE_DAYS = 999999;
	protected static final String SHOW_DOCUMENTS_OPEN_BALANCE = "O";
	protected static final String SHOW_DOCUMENTS_BY_DATE = "D";
	
	int daysfrom = (-1) * MAX_DUE_DAYS;
	int daysto   = MAX_DUE_DAYS;
	int bPartnerID;
	int orgID;
	String accountType;
	String libroDeCaja = "Libro de Caja";
	int salesRepID = 0;
	String showDocuments = "O";
	Timestamp dateTrxFrom = null;
	Timestamp dateTrxTo = null;
	
	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();
		 for( int i = 0;i < para.length;i++ ) {
	            log.fine( "prepare - " + para[ i ] );

	            String name = para[ i ].getParameterName();

	            if( para[ i ].getParameter() == null ) {
	                ;
	            } else if( name.equalsIgnoreCase( "daysdue" )) {
	                BigDecimal tmpDaysFrom = (BigDecimal)para[i].getParameter();
	                BigDecimal tmpDaysTo = (BigDecimal)para[i].getParameter_To();
	                // Si los días de vencimiento (inicio y fin) son null entonces
	                // se setea el máximo de días posible para que el filtro incluya todos
	                // los vencimientos. Esto amplía la flexibilidad del informe.
	                daysfrom = tmpDaysFrom == null ? (-1) * MAX_DUE_DAYS : tmpDaysFrom.intValue();
	                daysto   = tmpDaysTo == null ? MAX_DUE_DAYS : tmpDaysTo.intValue(); 
	            } else if( name.equalsIgnoreCase( "C_BPartner_ID" )) {
	            	bPartnerID = para[ i ].getParameterAsInt();
	            } else if( name.equalsIgnoreCase( "accountType" )) {
	            	accountType = (String)para[ i ].getParameter();
	            } else if( name.equalsIgnoreCase( "AD_Org_ID" )) {
	            	orgID = para[ i ].getParameterAsInt();
	            // Nuevo parámetro: Vendedor
	            } else if( name.equalsIgnoreCase( "SalesRep_ID" )) {
	            	salesRepID = para[ i ].getParameterAsInt();
	            // Nuevo parámetro: Mostrar Documentos
	            } else if( name.equalsIgnoreCase( "ShowDocuments" )) {
	            	showDocuments = (String)para[ i ].getParameter();
	            // Nuevo parámetro: Período de Fechas (ShowDocuments = By Date)
	            } else if( name.equalsIgnoreCase( "DateDoc" )) {
	            	dateTrxFrom = (Timestamp)para[i].getParameter();
	            	dateTrxTo = (Timestamp)para[i].getParameter_To();
	            } else {
	                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
	            }
	        }

	}
	
	
	@Override
	protected String doIt() throws Exception {

		deleteOldEntries();
		fillTable();
		
		return null;
	}

	
	private void deleteOldEntries()
	{
		String	sql	= "DELETE FROM T_EstadoDeCuenta WHERE AD_Client_ID = "+ getAD_Client_ID()+" AND AD_PInstance_ID = " + getAD_PInstance_ID() + " OR CREATED < ('now'::text)::timestamp(6) - interval '3 days'";
        DB.executeUpdate(sql, get_TrxName());
	}
	
	private void fillTable() throws Exception
	{
		StringBuffer query = new StringBuffer (
			"     SELECT dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner, i.issotrx, i.dateinvoiced as datedoc, p.netdays, i.dateinvoiced + (p.netdays::text || ' days'::text)::interval AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + (p.discountdays::text || ' days'::text)::interval AS discountdate, round(i.grandtotal * p.discount * 0.01::numeric, 2) AS discountamt, i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, -1::integer AS c_invoicepayschedule_id, i.c_paymentterm_id " +
			"	  FROM rv_c_invoice i " +
			"	  JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id " +
			"	  JOIN c_doctype dt on i.c_doctype_id = dt.c_doctype_id    " +
			"	  JOIN c_bpartner bp on i.c_bpartner_id = bp.c_bpartner_id    " +
			"	  WHERE i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar " +
			"	  AND i.AD_Client_ID = " + getAD_Client_ID() + 
			"	  AND dt.doctypekey not in ('RTR', 'RTI', 'RCR', 'RCI') " +
			"	  AND i.docstatus IN ('CO', 'CL', 'RE', 'VO') " +			
			(isShowOpenBalance() ?
			"     AND paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND invoiceopen(i.c_invoice_id, 0) <> 0::numeric ": "") +
			// } isShowOpenBalance
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND i.dateinvoiced::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND i.dateinvoiced::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND i.ad_org_id = " + orgID:"") + 
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") + 
			(salesRepID!=0?" AND i.salesrep_id = " + salesRepID : "") +
			"	UNION  " +
			"	  SELECT dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner,  i.issotrx, i.dateinvoiced as datedoc, to_days(ips.duedate) - to_days(i.dateinvoiced) AS netdays, ips.duedate, to_days(now()) - to_days(ips.duedate) AS daysdue, ips.discountdate, ips.discountamt, ips.dueamt AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) AS openamt, i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id " +
			"	  FROM rv_c_invoice i " +
			"	  JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id " +
			"	  JOIN c_doctype dt on i.c_doctype_id = dt.c_doctype_id " +
			"	  JOIN c_bpartner bp on i.c_bpartner_id = bp.c_bpartner_id " +
			"	  WHERE i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND ips.isvalid = 'Y'::bpchar " +
			"	  AND i.AD_Client_ID = " + getAD_Client_ID() + 
			"	  AND dt.doctypekey not in ('RTR', 'RTI', 'RCR', 'RCI') " +
			"	  AND i.docstatus IN ('CO', 'CL', 'RE', 'VO') " +			
			(isShowOpenBalance() ?
			"     AND to_days(now()) - to_days(ips.duedate) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND invoiceopen(i.c_invoice_id, 0) <> 0::numeric " +
			"	  AND invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) <> 0 " : "" ) +
			// } isShowOpenBalance
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND i.dateinvoiced::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND i.dateinvoiced::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND i.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") +
			(salesRepID!=0?" AND i.salesrep_id = " + salesRepID : "") +
			"	UNION " +
			"	  SELECT dt.signo_issotrx, dt.name as tipodoc, p.ad_org_id, p.ad_client_id, p.documentno, p.c_payment_id as doc_id, null as c_order_id, p.c_bpartner_id, bp.name as bpartner, p.isreceipt as issotrx, p.datetrx as datedoc, null AS netdays, null as duedate, to_days(now()) - to_days(p.datetrx) AS daysdue, null as discountdate, null as discountamt, ABS(p.payamt) AS grandtotal, p.allocatedamt AS paidamt, availableamt AS openamt, p.c_currency_id, p.c_conversiontype_id, null as ispayschedulevalid, null as c_invoicepayschedule_id, null as c_paymentterm_id " +
			"	  FROM rv_payment p " +
			"	  JOIN c_doctype dt on p.c_doctype_id = dt.c_doctype_id   " +
			"	  JOIN c_bpartner bp on p.c_bpartner_id = bp.c_bpartner_id " +  
			"	  WHERE 1 = 1  " +
			"	  AND p.AD_Client_ID = " + getAD_Client_ID() + 
			"	  AND p.docstatus IN ('CO', 'CL', 'RE', 'VO') " +
			(isShowOpenBalance() ?
			"     AND to_days(now()) - to_days(p.datetrx) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND availableamt > 0 " : "" ) +
			// } isShowOpenBalance 
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND p.datetrx::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND p.datetrx::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND p.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND p.c_bpartner_id = " + bPartnerID:"") +
			// Filtro por Vendedor:
			(salesRepID!=0? " AND (" + 
				// La EC pertenece al vendedor
				" COALESCE(bp.SalesRep_ID,0) = " + salesRepID + 
				// o existe al menos una factura del vendedor a la cual fue imputado el pago
				" OR EXISTS ( " +
					"SELECT al.C_Invoice_ID " +
					"FROM C_AllocationLine al " +
					"INNER JOIN C_Invoice i ON (al.C_Invoice_ID = i.C_Invoice_ID) " +
					"INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID) " +
					"WHERE ah.DocStatus IN ('CO','CL') AND al.IsActive = 'Y' " +
					  "AND al.C_Payment_ID = p.C_Payment_ID " +
					  "AND i.SalesRep_ID = " + salesRepID +
				")"	+  
			")":"") +
			"	UNION " +
			"	  SELECT d.signo_issotrx as signo_issotrx, '"+libroDeCaja+"' as tipodoc, d.ad_org_id, d.ad_client_id, d.documentno, d.document_id as doc_id, null as c_order_id, d.c_bpartner_id, bp.name as bpartner, '' as issotrx, d.datetrx as datedoc, null AS netdays, null as duedate, to_days(now()) - to_days(d.datetrx) AS daysdue, null as discountdate, null as discountamt, d.amount AS grandtotal, (abs(d.amount) - abs(cashlineavailable(document_id))) AS paidamt, cashlineavailable(document_id) AS openamt, d.c_currency_id, d.c_conversiontype_id, null as ispayschedulevalid, null as c_invoicepayschedule_id, null as c_paymentterm_id " +
			"	  FROM v_documents d " +
			"	  JOIN c_bpartner bp on d.c_bpartner_id = bp.c_bpartner_id " +  
			"	  WHERE d.DocumentTable = 'C_CashLine'  " +
			"	  AND d.AD_Client_ID = " + getAD_Client_ID() + 
			(isShowOpenBalance() ?
			"     AND to_days(now()) - to_days(d.datetrx) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND cashlineavailable(d.document_id) <> 0 " : "" ) +
			// } isShowOpenBalance
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND d.datetrx::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND d.datetrx::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND d.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND d.c_bpartner_id = " + bPartnerID:"") +
			(salesRepID!=0? " AND (" + 
				// La EC pertenece al vendedor
				" COALESCE(bp.SalesRep_ID,0) = " + salesRepID + 
				// o existe al menos una factura del vendedor a la cual fue imputada la línea de caja
				" OR EXISTS ( " +
					"SELECT al.C_CashLine_ID " +
					"FROM C_AllocationLine al " +
					"INNER JOIN C_Invoice i ON (al.C_Invoice_ID = i.C_Invoice_ID) " +
					"INNER JOIN C_AllocationHdr ah ON (al.C_AllocationHdr_ID = ah.C_AllocationHdr_ID) " +
					"WHERE ah.DocStatus IN ('CO','CL') AND al.IsActive = 'Y' " +
					  "AND al.C_CashLine_ID = d.Document_ID " +
					  "AND i.SalesRep_ID = " + salesRepID +
				")"	+  
			")":"") +
			"	ORDER BY bpartner, datedoc  ");
		
		PreparedStatement pstmt = DB.prepareStatement(query.toString());			
		ResultSet rs = pstmt.executeQuery();
		
		int bPartner = -1;
		int bPartnerOld = -1;
		BigDecimal saldo = new BigDecimal(0);
		BigDecimal subSaldo = new BigDecimal(0);
		BigDecimal saldogral = new BigDecimal(0);

		MClientInfo ci = MClient.get(getCtx()).getInfo();
		int currencyClient = ci.getC_Currency_ID();
		
		while (rs.next())
		{
			bPartner = rs.getInt("c_bpartner_id");
			if (bPartner != bPartnerOld)
			{
				if (bPartnerOld != -1) 
					insertTotalForBPartnerOld(bPartner, saldo);
				saldogral=saldogral.add(saldo);
				bPartnerOld = bPartner;
				saldo = new BigDecimal(0);
			}
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setsigno_issotrx(rs.getInt("signo_issotrx"));
			ec.settipodoc(rs.getString("tipodoc"));
			ec.setDocumentNo(rs.getString("documentno"));
			ec.setdoc_id(rs.getInt("doc_id"));
			ec.setC_Order_ID(rs.getInt("c_order_id"));
			ec.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
			ec.setbpartner(rs.getString("bpartner"));
			ec.setIsSOTrx(rs.getString("issotrx").equalsIgnoreCase("Y"));
			ec.setDateDoc(rs.getTimestamp("datedoc"));
			ec.setNetDays(rs.getBigDecimal("netdays"));
			ec.setDaysDue(rs.getInt("daysdue"));
			ec.setDateDoc(rs.getTimestamp("datedoc"));
			ec.setDiscountDate(rs.getTimestamp("discountdate"));
			ec.setDiscountAmt(MCurrency.currencyConvert(rs.getBigDecimal("discountamt"), rs.getInt("c_currency_id"), currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx()));
			ec.setGrandTotal(MCurrency.currencyConvert(rs.getBigDecimal("grandtotal"), rs.getInt("c_currency_id"), currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx()));
			ec.setPaidAmt(MCurrency.currencyConvert(rs.getBigDecimal("paidamt"), rs.getInt("c_currency_id"), currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx()));
			ec.setOpenAmt(MCurrency.currencyConvert(rs.getBigDecimal("openamt"), rs.getInt("c_currency_id"), currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx()));			
			ec.setC_Currency_ID(rs.getInt("c_currency_id"));
			ec.setC_ConversionType_ID(rs.getInt("c_conversiontype_id"));
			ec.setIsPayScheduleValid(rs.getString("ispayschedulevalid")!=null && rs.getString("ispayschedulevalid").equalsIgnoreCase("Y")); 
			ec.setC_InvoicePaySchedule_ID(rs.getInt("c_invoicepayschedule_id"));
			ec.setC_PaymentTerm_ID(rs.getInt("c_paymentterm_id"));
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.save();
			
			subSaldo = ec.getOpenAmt();
			if (!ec.gettipodoc().equalsIgnoreCase(libroDeCaja)) 
				subSaldo = subSaldo.multiply(new BigDecimal(ec.getsigno_issotrx()));
			else
				subSaldo = subSaldo.abs().multiply(new BigDecimal(ec.getsigno_issotrx()));					
			saldo = saldo.add(subSaldo);
		}
		if (bPartner != -1)
		{	insertTotalForBPartnerOld(bPartner, saldo);
			saldogral=saldogral.add(saldo);
			insertTotalGral(saldogral);
		}
		
	}
	
	private void insertTotalForBPartnerOld(int bPartner, BigDecimal saldo)
	{
		X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
		ec.setAD_PInstance_ID(getAD_PInstance_ID());
		ec.setC_BPartner_ID(bPartner);
		ec.setbpartner("             TOTAL:");
		ec.setOpenAmt(saldo);
		if (daysfrom != MAX_DUE_DAYS*-1 || daysto != MAX_DUE_DAYS) {
			ec.setDaysDue(daysfrom);
		}
		ec.setAccountType(accountType);
		ec.setShowDocuments(showDocuments);
		ec.setSalesRep_ID(salesRepID);
		ec.setDocumentNo("");
		if (dateTrxTo != null) {
			ec.setDateDoc(dateTrxTo);
		} else if (dateTrxFrom != null) {
			ec.setDateDoc(dateTrxFrom);
		} 
		ec.save();
	}
	
	private String getAccountTypeClause()
	{
		return accountType.equalsIgnoreCase("C")?"isCustomer":"isVendor";
	}
	
	private void insertTotalGral(BigDecimal saldo)
	{
		X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
		ec.setAD_PInstance_ID(getAD_PInstance_ID());
		ec.setC_BPartner_ID(0);
		ec.setbpartner("             TOTAL GENERAL:");
		ec.setOpenAmt(saldo);
		if (daysfrom != MAX_DUE_DAYS*-1  || daysto != MAX_DUE_DAYS) {
			ec.setDaysDue(daysfrom);
		}
		ec.setAccountType(accountType);
		ec.setShowDocuments(showDocuments);
		ec.setSalesRep_ID(salesRepID);
		ec.setDocumentNo("");
		if (dateTrxTo != null) {
			ec.setDateDoc(dateTrxTo);
		} else if (dateTrxFrom != null) {
			ec.setDateDoc(dateTrxFrom);
		} 
		ec.save();
	}
	
	protected boolean isShowOpenBalance() {
		return SHOW_DOCUMENTS_OPEN_BALANCE.equals(showDocuments);
	}
	
	protected boolean isShowByDate() {
		return SHOW_DOCUMENTS_BY_DATE.equals(showDocuments);
	}
	
}
