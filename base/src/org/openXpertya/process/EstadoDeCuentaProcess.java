package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.openXpertya.model.MClient;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MConversionRate;
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
	Timestamp dateToDays = null;
	boolean useDaysDue = false;
	int bPartnerID;
	int orgID;
	String accountType;
	String libroDeCaja = "Libro de Caja";
	int salesRepID = 0;
	String showDocuments = "O";
	Timestamp dateTrxFrom = null;
	Timestamp dateTrxTo = null;
	Timestamp dateConvert = null;
	int currencyID = 0;
	
	private Integer currencyClient;
	private HashMap<Integer, BigDecimal> saldosMultimoneda = new HashMap<Integer, BigDecimal>();
	private HashMap<Integer, BigDecimal> saldosGeneralMultimoneda = new HashMap<Integer, BigDecimal>();
	
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
                useDaysDue = true;
            } else if( name.equalsIgnoreCase( "DateToDays" )) {
            	dateToDays = (Timestamp)para[i].getParameter();
                // Si se ingreso un valor en el parámetro "Vencidas al" se calculan los días de inicio y fin.
                // Notar que no se filtra por la columna fecha sino que se reutiliza el filtro por días.
           		long diferenciaEn_ms = Env.getDate().getTime() - dateToDays.getTime();
           		long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
                daysfrom = (int) dias;
                daysto   = MAX_DUE_DAYS;            	
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
            } else if( name.equalsIgnoreCase( "C_Currency_ID" )) {
            	currencyID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
		}
	}
	
	private void validateParameters() throws Exception {
		if (useDaysDue && dateToDays != null) {
            log.log( Level.SEVERE,"Se produjo un error al obtener los filtros del reporte." );
            throw new Exception("@UseDaysDueAndDateToDaysError@");
        }
	}

	@Override
	protected String doIt() throws Exception {
		validateParameters();
		calculateDateConvert();
		deleteOldEntries();
		fillTable();
		
		return null;
	}

	private void calculateDateConvert() {
		/*
		1)	Si es Saldo Pendiente, la fecha de conversión debe ser al día de generación.
		2)	Si es Por Fechas, la fecha de conversión debe ser la ingresada en el parámetro Hasta. 
			Si no cargamos fecha hasta, entonces la fecha de conversión debe ser al día de generación.
		**/
		this.dateConvert = Env.getDate();
		if (this.isShowByDate() && this.dateTrxTo != null) {
			this.dateConvert = this.dateTrxTo;
		}
	}

	private void deleteOldEntries()
	{
		String	sql	= "DELETE FROM T_EstadoDeCuenta WHERE AD_Client_ID = "+ getAD_Client_ID()+" AND AD_PInstance_ID = " + getAD_PInstance_ID() + " OR CREATED < ('now'::text)::timestamp(6) - interval '3 days'";
        DB.executeUpdate(sql, get_TrxName());
	}
	
	private void fillTable() throws Exception
	{
		MClientInfo ci = MClient.get(getCtx()).getInfo();
		currencyClient = ci.getC_Currency_ID();
		StringBuffer query = new StringBuffer (
			"     SELECT dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner, i.issotrx, i.dateacct, i.dateinvoiced as datedoc, p.netdays, i.dateinvoiced + (p.netdays::text || ' days'::text)::interval AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + (p.discountdays::text || ' days'::text)::interval AS discountdate, round(i.grandtotal * p.discount * 0.01::numeric, 2) AS discountamt, " +
			//"     i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, " +
			"     i.grandtotal AS grandtotalmulticurrency, " +
			"     invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamtmulticurrency, " +
			"     invoiceopen(i.c_invoice_id, 0, i.c_currency_id,i.c_conversiontype_id) AS openamtmulticurrency, " +  
			"     currencybase(i.grandtotal,i.c_currency_id,i.dateinvoiced, i.ad_client_id, i.ad_org_id) AS grandtotal, " +
			"     invoicepaid(i.c_invoice_id, "+currencyClient+", 1) AS paidamt, " +
			"     invoiceopen(i.c_invoice_id, 0,"+currencyClient+",i.c_conversiontype_id) AS openamt, " + 		
			"     i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, -1::integer AS c_invoicepayschedule_id, i.c_paymentterm_id " +
			"	  FROM rv_c_invoice i " +
			"	  JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id " +
			"	  JOIN c_doctype dt on i.c_doctype_id = dt.c_doctype_id    " +
			"	  JOIN c_bpartner bp on i.c_bpartner_id = bp.c_bpartner_id    " +
			"	  WHERE i.ispayschedulevalid <> 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar " +
			"	  AND i.AD_Client_ID = " + getAD_Client_ID() +
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
			
			"	  SELECT dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner,  i.issotrx, i.dateacct, (CASE WHEN ips.duedate IS NOT NULL THEN ips.duedate ELSE i.dateinvoiced END) as datedoc, to_days(ips.duedate::timestamp without time zone) - to_days(i.dateinvoiced::timestamp without time zone) AS netdays, ips.duedate, to_days(now()::timestamp without time zone) - to_days(ips.duedate::timestamp without time zone) AS daysdue, ips.discountdate, ips.discountamt, " +
			//"     ips.dueamt * dt.signo_issotrx AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) * dt.signo_issotrx AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) * dt.signo_issotrx AS openamt, " +
			"     i.grandtotal AS grandtotalmulticurrency, " +
			"     invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamtmulticurrency, " +
			"     invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id ,i.c_currency_id,i.c_conversiontype_id) AS openamtmulticurrency, " +
			"     currencybase(i.grandtotal,i.c_currency_id,i.dateinvoiced, i.ad_client_id, i.ad_org_id) AS grandtotal, " +
			"     invoicepaid(i.c_invoice_id, "+currencyClient+", 1) AS paidamt, " +
			"     invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id,"+currencyClient+",i.c_conversiontype_id) AS openamt, " +
			"     i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id " +
			"	  FROM ( " + // Vista RV_C_Invoice original omitida 
//			 INLINE DE rv_c_invoice con modificaciones ad-hoc a fin de reducir tiempos de ejecución
			"	SELECT i.c_invoice_id, i.ad_client_id, i.ad_org_id, i.isactive, i.created, i.createdby, i.updated, i.updatedby, i.issotrx, i.documentno, i.docstatus, i.docaction, i.isprinted, i.isdiscountprinted, i.processing, i.processed, i.istransferred, i.ispaid, i.c_doctype_id, i.c_doctypetarget_id, i.c_order_id, i.description, i.isapproved, i.salesrep_id, i.dateinvoiced, i.dateprinted, i.dateacct, i.c_bpartner_id, i.c_bpartner_location_id, i.ad_user_id, i.poreference, i.dateordered, i.c_currency_id, i.c_conversiontype_id, i.paymentrule, i.c_paymentterm_id, i.m_pricelist_id, i.c_campaign_id, i.c_project_id, i.c_activity_id, i.ispayschedulevalid, " + // Columnas innecesarias 
			"	CASE " +
			"	    WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.chargeamt * (- 1::numeric) " +
			"	    ELSE i.chargeamt" +
			"	END AS chargeamt, " +
			"	CASE " +
			"	    WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.totallines * (- 1::numeric) " +
			"	    ELSE i.totallines " +
			"	END AS totallines,  " +
			"	CASE " +
			"	    WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN i.grandtotal * (- 1::numeric) " +
			"	    ELSE i.grandtotal " +
			"	END AS grandtotal, " +
			"	CASE " +
			"	    WHEN charat(d.docbasetype::character varying, 3)::text = 'C'::text THEN (-1) " +
			"	    ELSE 1 " +
			"	END AS multiplier " + 
//			"	, getinoutsdocumentsnofrominvoice(i.c_invoice_id)::character varying(30) AS documentno_inout " + // Comentada funcion postgres.  No era requerida y consumia tiempo considerable
			" FROM c_invoice i" +
			" JOIN c_doctype d ON i.c_doctype_id = d.c_doctype_id " +
			" WHERE i.docstatus = ANY (ARRAY['CO'::bpchar, 'CL'::bpchar]) " +	
			" AND i.ispayschedulevalid = 'Y'::bpchar  " + // DE LA PPAL 
			" AND i.AD_Client_ID = " + getAD_Client_ID() +	// DE LA PPAL
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") + 
			" ) i " + 
//			 FIN INLINE DE RV_C_INVOICE			
			"	  JOIN c_invoicepayschedule ips ON i.c_invoice_id = ips.c_invoice_id " +
			"	  JOIN c_doctype dt on i.c_doctype_id = dt.c_doctype_id " +
			"	  JOIN c_bpartner bp on i.c_bpartner_id = bp.c_bpartner_id " +
//			 Clausulas comentadas dado que se incorporan en el query interno de RV_C_Invoice modificado
			"	  WHERE ips.isvalid = 'Y'::bpchar " +	// i.ispayschedulevalid = 'Y'::bpchar AND i.docstatus <> 'DR'::bpchar AND  En la consulta interna
//			"	  AND i.AD_Client_ID = " + getAD_Client_ID() + 		// ya en la consulta interna. 
//			"	  AND i.docstatus IN ('CO', 'CL', 'RE', 'VO') " +	// ya en la consulta interna.  rv_c_invoice solo manejaba CO CL (omitia RE VO)		
			(isShowOpenBalance() ?
			"     AND to_days(now()::timestamp without time zone) - to_days(ips.duedate::timestamp without time zone) BETWEEN " + daysfrom + " AND " + daysto +
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
			
			"	  SELECT dt.signo_issotrx, dt.name as tipodoc, p.ad_org_id, p.ad_client_id, p.documentno, p.c_payment_id as doc_id, null as c_order_id, p.c_bpartner_id, bp.name as bpartner, p.isreceipt as issotrx, p.dateacct, p.datetrx as datedoc, null AS netdays, null as duedate, to_days(now()::timestamp without time zone) - to_days(p.datetrx::timestamp without time zone) AS daysdue, null as discountdate, null as discountamt, " +
			"     ABS(p.payamt) AS grandtotalmulticurrency, " +
			"     p.allocatedamt AS paidamtmulticurrency, " +
			"     availableamt AS openamtmulticurrency, " +
			"     currencyConvert(ABS(p.payamt), p.c_currency_id, "+currencyClient+", p.datetrx, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS grandtotal, " +
			"     currencyConvert(p.allocatedamt, p.c_currency_id, "+currencyClient+", p.datetrx, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS paidamt, " +
			"     currencyConvert(availableamt, p.c_currency_id, "+currencyClient+", p.datetrx, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS openamt, p.c_currency_id, p.c_conversiontype_id, null as ispayschedulevalid, null as c_invoicepayschedule_id, null as c_paymentterm_id " +
			"	  FROM rv_payment p " +
			"	  JOIN c_doctype dt on p.c_doctype_id = dt.c_doctype_id   " +
			"	  JOIN c_bpartner bp on p.c_bpartner_id = bp.c_bpartner_id " +  
			"	  WHERE 1 = 1  " +
			"	  AND p.AD_Client_ID = " + getAD_Client_ID() + 
			"	  AND p.docstatus IN ('CO', 'CL', 'RE', 'VO') " +
			(isShowOpenBalance() ?
			"     AND to_days(now()::timestamp without time zone) - to_days(p.datetrx::timestamp without time zone) BETWEEN " + daysfrom + " AND " + daysto +
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
			
			"	  SELECT d.signo_issotrx as signo_issotrx, '"+libroDeCaja+"' as tipodoc, d.ad_org_id, d.ad_client_id, d.documentno, d.document_id as doc_id, null as c_order_id, d.c_bpartner_id, d.name as bpartner, '' as issotrx, d.dateacct, d.datetrx as datedoc, null AS netdays, null as duedate, to_days(now()::timestamp without time zone) - to_days(d.datetrx::timestamp without time zone) AS daysdue, null as discountdate, null as discountamt, " +
			"     ABS(d.amount) AS grandtotalmulticurrency, " +
			"     (abs(d.amount) - abs(cashlineavailable(document_id))) AS paidamtmulticurrency, " +
			"     cashlineavailable(document_id) AS openamtmulticurrency, " +
			"     currencyConvert(d.amount, d.c_currency_id, "+currencyClient+", d.datetrx, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS grandtotal, " +
			"     currencyConvert((abs(d.amount) - abs(cashlineavailable(document_id))), d.c_currency_id, "+currencyClient+", d.datetrx, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS paidamt, " +
			"     currencyConvert(cashlineavailable(document_id), d.c_currency_id, "+currencyClient+", d.datetrx, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS openamt, d.c_currency_id, d.c_conversiontype_id, null as ispayschedulevalid, null as c_invoicepayschedule_id, null as c_paymentterm_id " +
			"	  FROM ( " +  // Vista V_DOCUMENTS original  omitida. 
//			 INLINE DE V_DOCUMENTS, omitiendo partes de la misma que no son necesarias en este caso.			
			"       SELECT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, " +  
			"        CASE  " +
			"           WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id " +
			"            ELSE i.c_bpartner_id " +
			"        END AS c_bpartner_id, dt.c_doctype_id, " + 
			"        CASE " +
			"            WHEN cl.amount < 0.0 THEN 1 " +
			"            ELSE (-1) " +
			"        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, " + 
			"        CASE " +
			"            WHEN cl.amount < 0.0 THEN 'N'::bpchar " +
			"            ELSE 'Y'::bpchar " +
			"        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, " + 
			"        NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, bp.name  " +
//			"        COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus " +
			"   FROM c_cashline cl " +
			"	JOIN c_cash c ON cl.c_cash_id = c.c_cash_id " +
			"	JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname FROM c_doctype d WHERE d.doctypekey::text = 'CMC'::text AND d.ad_client_id = " + getAD_Client_ID() + ") dt ON cl.ad_client_id = dt.ad_client_id " +
			"	LEFT JOIN c_bpartner bp ON " + (bPartnerID!=0?" cl.c_bpartner_id = " + bPartnerID + " AND ":"") + " cl.c_bpartner_id = bp.c_bpartner_id " +
			"	LEFT JOIN c_invoice i ON " + (bPartnerID!=0?" i.c_bpartner_id = " + bPartnerID + " AND ":"") + " cl.c_invoice_id = i.c_invoice_id " +
			" 	WHERE 1 = 1 " +
			(bPartnerID!=0? "   AND (cl.c_bpartner_id is not null AND cl.c_bpartner_id = " + bPartnerID + ") OR (i.c_bpartner_ID is not null and i.c_bpartner_id = " + bPartnerID + ") " : "") +
			(isShowOpenBalance() ?
			"     AND to_days(now()::timestamp without time zone) - to_days(c.statementdate::timestamp without time zone) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND cashlineavailable(cl.c_cashline_id) <> 0 " : "" ) +
			// } isShowOpenBalance			
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +			
//			"	LEFT JOIN c_bpartner bp2 ON i.c_bpartner_id = bp2.c_bpartner_id " +
			"	) as d " + 
//			FIN INLINE DE V_DOCUMENTS			
//			"	  JOIN c_bpartner bp on d.c_bpartner_id = bp.c_bpartner_id " +  
			"	  WHERE docstatus IN ('CO','CL') " +
			"	  AND d.AD_Client_ID = " + getAD_Client_ID() + 
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND d.datetrx::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND d.datetrx::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
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

		saldosMultimoneda.put(currencyClient, BigDecimal.ZERO);
		saldosGeneralMultimoneda.put(currencyClient, BigDecimal.ZERO);
		
		while (rs.next()) {
			bPartner = rs.getInt("c_bpartner_id");
			if (bPartner != bPartnerOld)
			{
				if (bPartnerOld != -1){ 
					insertTotalForBPartnerOld(bPartner, saldo);
					insertTotalForBPartnerChecks(bPartner);
				}
				saldogral=saldogral.add(saldo);
				bPartnerOld = bPartner;
				saldo = new BigDecimal(0);
				
				incrementarSaldosGeneralMultimoneda();
			}
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(rs.getInt("ad_org_id"));
			ec.setsigno_issotrx(rs.getInt("signo_issotrx"));
			ec.settipodoc(rs.getString("tipodoc"));
			ec.setDocumentNo(rs.getString("documentno"));
			ec.setdoc_id(rs.getInt("doc_id"));
			ec.setC_Order_ID(rs.getInt("c_order_id"));
			ec.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
			ec.setbpartner(rs.getString("bpartner"));
			ec.setIsSOTrx(rs.getString("issotrx").equalsIgnoreCase("Y"));
			ec.setDateAcct(rs.getTimestamp("dateacct"));
			ec.setNetDays(rs.getBigDecimal("netdays"));
			ec.setDaysDue(rs.getInt("daysdue"));
			ec.setDateDoc(rs.getTimestamp("datedoc"));
			ec.setDiscountDate(rs.getTimestamp("discountdate"));
			ec.setDiscountAmt(MCurrency.currencyConvert(rs.getBigDecimal("discountamt"), rs.getInt("c_currency_id"), currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx()));
			ec.setGrandTotalMulticurrency(rs.getBigDecimal("grandtotalmulticurrency"));
			ec.setPaidAmtMulticurrency(rs.getBigDecimal("paidamtmulticurrency"));
			ec.setOpenAmtMulticurrency(rs.getBigDecimal("openamtmulticurrency"));
			
			ec.setGrandTotal(rs.getBigDecimal("grandtotal"));
			ec.setPaidAmt(rs.getBigDecimal("paidamt"));
			ec.setOpenAmt(rs.getBigDecimal("openamt"));
			
			BigDecimal rate = MConversionRate.getRate(rs.getInt("c_currency_id"), currencyClient, this.dateConvert, rs.getInt("c_conversiontype_id"), getAD_Client_ID(), 0);
			this.incrementarSaldosMultimoneda(ec, rs.getInt("c_currency_id"));
			
			if (rate == null) {
				String fromISO = MCurrency.getISO_Code(getCtx(), rs.getInt("c_currency_id"));
				String toISO = MCurrency.getISO_Code(getCtx(), currencyClient);
				log.severe("No Currency Conversion from " + fromISO	+ " to " + toISO);
				throw new Exception("@NoCurrencyConversion@ (" + fromISO + "->" + toISO + ")");
			}
			
			ec.setOpenAmt(rs.getBigDecimal("openamtmulticurrency").multiply(rate));
			ec.setGrandTotal(ec.getPaidAmt().add(ec.getOpenAmt()));
			
			ec.setC_Currency_ID(rs.getInt("c_currency_id"));
			ec.setC_ConversionType_ID(rs.getInt("c_conversiontype_id"));
			ec.setConversionRate(MConversionRate.getRate(rs.getInt("c_currency_id"), currencyClient, this.dateConvert, rs.getInt("c_conversiontype_id"), getAD_Client_ID(), rs.getInt("c_order_id")));
			ec.setIsPayScheduleValid(rs.getString("ispayschedulevalid")!=null && rs.getString("ispayschedulevalid").equalsIgnoreCase("Y")); 
			ec.setC_InvoicePaySchedule_ID(rs.getInt("c_invoicepayschedule_id"));
			ec.setC_PaymentTerm_ID(rs.getInt("c_paymentterm_id"));
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.setDateToDays(dateToDays);
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
			insertTotalForBPartnerChecks(bPartner);
			
			incrementarSaldosGeneralMultimoneda();
			
			saldogral=saldogral.add(saldo);
			insertTotalGral(saldogral);
		}
		
	}
	
	private void incrementarSaldosMultimoneda(X_T_EstadoDeCuenta ec, Integer c_Currency_ID){
		if (!saldosMultimoneda.containsKey(c_Currency_ID)) {
			saldosMultimoneda.put(c_Currency_ID, BigDecimal.ZERO);
		}
		BigDecimal saldoMultimoneda = ec.getOpenAmtMulticurrency();
		if (!ec.gettipodoc().equalsIgnoreCase(libroDeCaja)) 
			saldoMultimoneda = saldoMultimoneda.multiply(new BigDecimal(ec.getsigno_issotrx()));
		else
			saldoMultimoneda = saldoMultimoneda.abs().multiply(new BigDecimal(ec.getsigno_issotrx()));					
		saldosMultimoneda.put(c_Currency_ID, saldosMultimoneda.get(c_Currency_ID).add(saldoMultimoneda));
	}
	
	private void incrementarSaldosGeneralMultimoneda(){
		Iterator it = saldosMultimoneda.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, BigDecimal> e = (Entry<Integer, BigDecimal>) it.next();
			
			if (!saldosGeneralMultimoneda.containsKey(e.getKey())) {
				saldosGeneralMultimoneda.put(e.getKey(), BigDecimal.ZERO);
			}
			saldosGeneralMultimoneda.put(e.getKey(), saldosGeneralMultimoneda.get(e.getKey()).add(e.getValue()));	
		}
		
		saldosMultimoneda = new HashMap<Integer, BigDecimal>();
		saldosMultimoneda.put(currencyClient, BigDecimal.ZERO);
	}
	
	private void insertTotalForBPartnerOld(int bPartner, BigDecimal saldo)	{
		Iterator it = saldosMultimoneda.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, BigDecimal> e = (Entry<Integer, BigDecimal>) it.next();
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BPartner_ID(bPartner);
			ec.setbpartner("             TOTAL " + MCurrency.getISO_Code(getCtx(), e.getKey()) + ":");
			ec.setOpenAmt(e.getValue());
			if (daysfrom != MAX_DUE_DAYS*-1 || daysto != MAX_DUE_DAYS) {
				ec.setDaysDue(daysfrom);
			}
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.setDateToDays(dateToDays);
			ec.setDocumentNo("");
			ec.setC_Currency_ID(e.getKey());
			if (dateTrxTo != null) {
				ec.setDateDoc(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
			} 
			ec.save();
		}

		// La fila totalizadora por EC, se visualiza sólo si el filtro Moneda está vacío.
		if (currencyID == 0) {
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BPartner_ID(bPartner);
			ec.setbpartner("             TOTAL:");
			ec.setOpenAmt(saldo);
			if (daysfrom != MAX_DUE_DAYS*-1 || daysto != MAX_DUE_DAYS) {
				ec.setDaysDue(daysfrom);
			}
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.setDateToDays(dateToDays);
			ec.setDocumentNo("");
			ec.setC_Currency_ID(currencyClient);	
			if (dateTrxTo != null) {
				ec.setDateDoc(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
			} 
			ec.save();
		}
	}
	
	private void insertTotalForBPartnerChecks(int bPartner)	{
		X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
		ec.setAD_PInstance_ID(getAD_PInstance_ID());
		ec.setAD_Org_ID(orgID);
		ec.setC_BPartner_ID(bPartner);
		ec.setbpartner("             TOTAL CHEQUES CARTERA:");
		
		String sql = "SELECT COALESCE(SUM(payamt),0) AS checkAmt " +
				     "FROM ((SELECT p.PayAmt, C_Bpartner_ID " +
				     "FROM C_Payment p " +
				     "INNER JOIN C_bankaccount ba ON (ba.C_BankAccount_ID = p.C_BankAccount_ID) " +
				     "WHERE (tendertype = 'K') AND (ba.ischequesencartera = 'Y') AND p.docstatus IN ('CO') AND (p.IsReceipt = 'Y'))	" +
				     "UNION	" +
				     "(SELECT pa.PayAmt, C_Bpartner_ID " +
				     "FROM C_Payment pa " +
				     "INNER JOIN C_bankaccount ba ON (ba.C_BankAccount_ID = pa.C_BankAccount_ID) " +
				     "WHERE (tendertype = 'K') AND (ba.ischequesencartera = 'N') AND pa.docstatus IN ('CO') AND (pa.IsReceipt = 'Y') AND (pa.IsReconciled = 'Y'))) AS subconsulta " +
				     "WHERE (C_Bpartner_ID = ?);";
		
		BigDecimal checksAmt = BigDecimal.ZERO.add(DB.getSQLValueBD(null,sql, bPartner));
		ec.setOpenAmt(checksAmt);
		if (daysfrom != MAX_DUE_DAYS*-1 || daysto != MAX_DUE_DAYS) {
			ec.setDaysDue(daysfrom);
		}
		ec.setAccountType(accountType);
		ec.setShowDocuments(showDocuments);
		ec.setSalesRep_ID(salesRepID);
		ec.setDateToDays(dateToDays);
		ec.setDocumentNo("");
		ec.setC_Currency_ID(currencyClient);
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
	
	private void insertTotalGral(BigDecimal saldo) {
		Iterator it = saldosGeneralMultimoneda.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, BigDecimal> e = (Entry<Integer, BigDecimal>) it.next();
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BPartner_ID(0);
			ec.setbpartner("             TOTAL GENERAL " + MCurrency.getISO_Code(getCtx(), e.getKey()) + ":");
			ec.setOpenAmt(e.getValue());
			if (daysfrom != MAX_DUE_DAYS*-1  || daysto != MAX_DUE_DAYS) {
				ec.setDaysDue(daysfrom);
			}
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.setDateToDays(dateToDays);
			ec.setDocumentNo("");
			ec.setC_Currency_ID(e.getKey());
			if (dateTrxTo != null) {
				ec.setDateDoc(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
			} 
			ec.save();
		}
		
		// La fila totalizadora para todas las EC, se visualiza sólo si el filtro Moneda está vacío.
		if (currencyID == 0) {
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BPartner_ID(0);
			ec.setbpartner("             TOTAL GENERAL:");
			ec.setOpenAmt(saldo);
			if (daysfrom != MAX_DUE_DAYS*-1  || daysto != MAX_DUE_DAYS) {
				ec.setDaysDue(daysfrom);
			}
			ec.setAccountType(accountType);
			ec.setShowDocuments(showDocuments);
			ec.setSalesRep_ID(salesRepID);
			ec.setDateToDays(dateToDays);
			ec.setDocumentNo("");
			ec.setC_Currency_ID(currencyClient);
			if (dateTrxTo != null) {
				ec.setDateDoc(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
			} 
			ec.save();
		}
	}
	
	protected boolean isShowOpenBalance() {
		return SHOW_DOCUMENTS_OPEN_BALANCE.equals(showDocuments);
	}
	
	protected boolean isShowByDate() {
		return SHOW_DOCUMENTS_BY_DATE.equals(showDocuments);
	}
	
}
