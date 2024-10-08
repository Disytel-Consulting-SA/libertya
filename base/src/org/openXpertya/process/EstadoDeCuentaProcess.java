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
import org.openXpertya.util.Util;



public class EstadoDeCuentaProcess extends SvrProcess {

	protected static final int MAX_DUE_DAYS = 999999;
	protected static final String SHOW_DOCUMENTS_OPEN_BALANCE = "O";
	protected static final String SHOW_DOCUMENTS_BY_DATE = "D";
	protected static final String SHOW_DOCUMENTS_BY_DATE_ONLY_OPEN_DOCUMENTS = "X";
	
	
	int daysfrom = (-1) * MAX_DUE_DAYS;
	int daysto   = MAX_DUE_DAYS;
	Timestamp dateToDays = null;
	boolean useDaysDue = false;
	int bPartnerID;
	int bpGroupID;
	int orgID;
	String accountType;
	String libroDeCaja = "Libro de Caja";
	int salesRepID = 0;
	String showDocuments = "O";
	Timestamp dateTrxFrom = null;
	Timestamp dateTrxTo = null;
	Timestamp dateConvert = null;
	int currencyID = 0;
	String condition;
	String conditionWhereClause;
	
	private Integer currencyClient;
	private HashMap<Integer, BigDecimal> saldosMultimoneda = new HashMap<Integer, BigDecimal>();
	private HashMap<Integer, BigDecimal> saldosGeneralMultimoneda = new HashMap<Integer, BigDecimal>();
	
	PreparedStatement pstmt = null;
	
	private boolean filterInternalEC = false;
		
	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] para = getParameter();	
		for( int i = 0;i < para.length;i++ ) {
			log.fine( "prepare - " + para[ i ] );

			String name = para[ i ].getParameterName();

			if( name.equalsIgnoreCase( "daysdue" )) {
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
            } else if( name.equalsIgnoreCase( "DateAcct" )) {
            	dateTrxFrom = (Timestamp)para[i].getParameter();
            	dateTrxTo = (Timestamp)para[i].getParameter_To();
            } else if( name.equalsIgnoreCase( "C_Currency_ID" )) {
            	currencyID = para[ i ].getParameterAsInt();
            } else if( name.equalsIgnoreCase( "Condition" )) {
            	condition = (String)para[ i ].getParameter();
            } else if( name.equalsIgnoreCase( "C_BP_Group_ID" )) {
            	bpGroupID = para[ i ].getParameterAsInt();
            } else if( name.equalsIgnoreCase( "FilterInternalEC" )) {
            	filterInternalEC = ((String)para[ i ].getParameter()).equals("Y");
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
		}
		
		// Si tiene asociado una EC, entonces el grupo va 0
		if(!Util.isEmpty(bPartnerID, true)){
			bpGroupID = 0;
		}
	}
	
	private void validateParameters() throws Exception {
		if (useDaysDue && dateToDays != null) {
            log.log( Level.SEVERE,"Se produjo un error al obtener los filtros del reporte." );
            throw new Exception("@UseDaysDueAndDateToDaysError@");
        }
	}
	
	private void initialize(){
		if (currencyClient==null) {
			MClientInfo ci = MClient.get(getCtx()).getInfo();
			currencyClient = ci.getC_Currency_ID();
			
			saldosMultimoneda.put(currencyClient, BigDecimal.ZERO);
			saldosGeneralMultimoneda.put(currencyClient, BigDecimal.ZERO);
		}
	}

	@Override
	protected String doIt() throws Exception {
		validateParameters();
		initialize();
		calculateDateConvert();
		deleteOldEntries();
		
		// Performance: Consultar iterativamente por cada EC en lugar de una única query involucrando a todas las ECs 
		PreparedStatement pstmt = DB.prepareStatement(getBPartnersSQL(), get_TrxName());
		ResultSet rs = pstmt.executeQuery();
		int iter=0;
		while (rs.next())
		{
				iter++;
				bPartnerID = rs.getInt("c_bpartner_id");
				log.fine(Env.getDateTime("yyyy/MM/dd-HH:mm:ss.SSS") + " " + bPartnerID + " " + iter);
				fillTable();
		}
		insertTotalGral(saldogral);
		
		return null;
	}
	
	protected String getBPartnersSQL() {
		return 	" Select distinct c_bpartner_id " +
				" from c_bpartner " +
				" where isactive = 'Y' " +
				" and " + getAccountTypeClause() + " = 'Y' " +
				" and ad_client_id = " + getAD_Client_ID() +
				(bPartnerID!=0?" AND c_bpartner_id = " + bPartnerID:"") +
				(bpGroupID!=0?" AND c_bp_group_id = " + bpGroupID:"") +
				(bPartnerID!=0?"":getFilterInternalEC()) +
				" order by c_bpartner_id ";	
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
		String	sql	= "DELETE FROM T_EstadoDeCuenta	 WHERE AD_Client_ID = "+ getAD_Client_ID()+" AND AD_PInstance_ID = " + getAD_PInstance_ID() + " OR CREATED < ('now'::text)::timestamp(6) - interval '3 days'";
        DB.executeUpdate(sql, get_TrxName());
	}

	int bPartner = -1;
	
	BigDecimal saldo = new BigDecimal(0);
	BigDecimal subSaldo = new BigDecimal(0);
	BigDecimal saldogral = new BigDecimal(0);

	
	private void fillTable() throws Exception
	{
		StringBuffer query = new StringBuffer (
			"     SELECT distinct dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner, i.issotrx, i.dateacct::date as dateacct, i.dateinvoiced::date as datedoc, p.netdays, i.dateinvoiced + (p.netdays::text || ' days'::text)::interval AS duedate, paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) AS daysdue, i.dateinvoiced + (p.discountdays::text || ' days'::text)::interval AS discountdate, round(i.grandtotal * p.discount * 0.01::numeric, 2) AS discountamt, " +
			//"     i.grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) AS paidamt, invoiceopen(i.c_invoice_id, 0) AS openamt, " +
			"     i.grandtotal AS grandtotalmulticurrency, " +
			"     getallocatedamt(i.c_invoice_id, i.c_currency_id, i.c_conversiontype_id, 1, "+getDateToInlineQuery()+") AS paidamtmulticurrency, " +
			"     invoiceopen(i.c_invoice_id, 0, i.c_currency_id, i.c_conversiontype_id, "+getDateToInlineQuery()+") AS openamtmulticurrency, " +  
			"     currencybase(i.grandtotal, i.c_currency_id, i.dateacct, i.ad_client_id, i.ad_org_id) AS grandtotal, " +
			"     getallocatedamt(i.c_invoice_id, "+currencyClient+", i.c_conversiontype_id, 1, "+getDateToInlineQuery()+") AS paidamt, " +
			"     invoiceopen(i.c_invoice_id, 0,"+currencyClient+",i.c_conversiontype_id, "+getDateToInlineQuery()+") AS openamt, " + 		
			"     i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, -1::integer AS c_invoicepayschedule_id, i.c_paymentterm_id, bp.c_bp_group_id " +
			"	  FROM rv_c_invoice i " +
			"	  JOIN c_paymentterm p ON i.c_paymentterm_id = p.c_paymentterm_id " +
			"	  JOIN c_doctype dt on i.c_doctype_id = dt.c_doctype_id    " +
			"	  JOIN c_bpartner bp on i.c_bpartner_id = bp.c_bpartner_id    " +
			"	  WHERE i.ispayschedulevalid <> 'Y'::bpchar " + 
			"	  AND " +getInvoicesStatusWhereClause("i") +
			"	  AND i.AD_Client_ID = " + getAD_Client_ID() +
			(condition.equals(X_T_EstadoDeCuenta.CONDITION_All)?
					"":
					" AND i.paymentrule = '"+condition+"' ") +
			(isShowOpenBalance() ?
			"     AND paymenttermduedays(i.c_paymentterm_id, i.dateinvoiced::timestamp with time zone, now()) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND invoiceopen(i.c_invoice_id, 0, "+getDateToInlineQuery()+") <> 0::numeric ": "") +
			// } isShowOpenBalance
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND i.dateacct::date >= '" + dateTrxFrom + "'::date" : "" ) +
				(dateTrxTo != null ? " AND i.dateacct::date <= '" + dateTrxTo + "'::date" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND i.ad_org_id = " + orgID:"") + 
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") +
			(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
			(salesRepID!=0?" AND i.salesrep_id = " + salesRepID : "") +
			
			"	UNION  " +
			
			"	  SELECT distinct dt.signo_issotrx, dt.name as tipodoc, i.ad_org_id, i.ad_client_id, i.documentno, i.c_invoice_id as doc_id, i.c_order_id, i.c_bpartner_id, bp.name as bpartner,  i.issotrx, i.dateacct::date as dateacct, (CASE WHEN ips.duedate IS NOT NULL THEN ips.duedate ELSE i.dateacct END)::date as datedoc, to_days(ips.duedate::timestamp without time zone) - to_days(i.dateinvoiced::timestamp without time zone) AS netdays, ips.duedate, to_days(now()::timestamp without time zone) - to_days(ips.duedate::timestamp without time zone) AS daysdue, ips.discountdate, ips.discountamt, " +
			//"     ips.dueamt * dt.signo_issotrx AS grandtotal, invoicepaid(i.c_invoice_id, i.c_currency_id, 1) * dt.signo_issotrx AS paidamt, invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id) * dt.signo_issotrx AS openamt, " +
			"     i.grandtotal AS grandtotalmulticurrency, " +
			"     getallocatedamt(i.c_invoice_id, i.c_currency_id, i.c_conversiontype_id, 1, "+getDateToInlineQuery()+", ips.c_invoicepayschedule_id) AS paidamtmulticurrency, " +
			"     invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id ,i.c_currency_id,i.c_conversiontype_id, "+getDateToInlineQuery()+") AS openamtmulticurrency, " +
			"     currencybase(i.grandtotal,i.c_currency_id,i.dateacct, i.ad_client_id, i.ad_org_id) AS grandtotal, " +
			"     getallocatedamt(i.c_invoice_id, "+currencyClient+", i.c_conversiontype_id, 1, "+getDateToInlineQuery()+", ips.c_invoicepayschedule_id) AS paidamt, " +
			"     invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id,"+currencyClient+",i.c_conversiontype_id, "+getDateToInlineQuery()+") AS openamt, " +
			"     i.c_currency_id, i.c_conversiontype_id, i.ispayschedulevalid, ips.c_invoicepayschedule_id, i.c_paymentterm_id, bp.c_bp_group_id " +
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
			" JOIN c_bpartner bp ON bp.c_bpartner_id = i.c_bpartner_id " +
			" WHERE "+getInvoicesStatusWhereClause("i")+ 
			(condition.equals(X_T_EstadoDeCuenta.CONDITION_All)?
					"":
					" AND i.paymentrule = '"+condition+"' ") +
			" AND i.ispayschedulevalid = 'Y'::bpchar  " + // DE LA PPAL 
			" AND i.AD_Client_ID = " + getAD_Client_ID() +	// DE LA PPAL
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") +
			(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
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
			"     AND invoiceopen(i.c_invoice_id, 0, "+getDateToInlineQuery()+") <> 0::numeric " +
			"	  AND invoiceopen(i.c_invoice_id, ips.c_invoicepayschedule_id, "+getDateToInlineQuery()+") <> 0 " : "" ) +
			// } isShowOpenBalance
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND i.dateacct::date >= '" + dateTrxFrom + "'::date" : "" ) +
				(dateTrxTo != null ? " AND i.dateacct::date <= '" + dateTrxTo + "'::date" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND i.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") +
			(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
			(salesRepID!=0?" AND i.salesrep_id = " + salesRepID : "") +
			
			"	UNION " +
			
			"	  SELECT distinct dt.signo_issotrx, dt.name as tipodoc, p.ad_org_id, p.ad_client_id, p.documentno, p.c_payment_id as doc_id, null::integer as c_order_id, p.c_bpartner_id, bp.name as bpartner, p.isreceipt as issotrx, p.dateacct::date as dateacct, p.datetrx::date as datedoc, null::integer AS netdays, null::date as duedate, to_days(now()::timestamp without time zone) - to_days(p.datetrx::timestamp without time zone) AS daysdue, null::date as discountdate, null::numeric as discountamt, " +
			"     ABS(p.payamt) AS grandtotalmulticurrency, " +
			"     paymentallocated(p.c_payment_id, p.c_currency_id, "+getDateToInlineQuery()+") AS paidamtmulticurrency, " +
			"     paymentavailable(p.c_payment_id, "+getDateToInlineQuery()+") AS openamtmulticurrency, " +
			"     currencyConvert(ABS(p.payamt), p.c_currency_id, "+currencyClient+", p.dateacct, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS grandtotal, " +
			"     currencyConvert(paymentallocated(p.c_payment_id, p.c_currency_id, "+getDateToInlineQuery()+"), p.c_currency_id, "+currencyClient+", p.dateacct, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS paidamt, " +
			"     currencyConvert(paymentavailable(p.c_payment_id, "+getDateToInlineQuery()+"), p.c_currency_id, "+currencyClient+", p.dateacct, COALESCE(p.c_conversiontype_id,0), p.ad_client_id, p.ad_org_id ) AS openamt, p.c_currency_id, p.c_conversiontype_id, null::character(1) as ispayschedulevalid, null::integer as c_invoicepayschedule_id, null::integer as c_paymentterm_id, bp.c_bp_group_id " +
			"	  FROM c_payment p " +
			"	  JOIN c_doctype dt on p.c_doctype_id = dt.c_doctype_id   " +
			"	  JOIN c_bpartner bp on p.c_bpartner_id = bp.c_bpartner_id " + (bPartnerID!=0?" AND p.c_bpartner_id = " + bPartnerID:"") +
			(condition.equals(X_T_EstadoDeCuenta.CONDITION_All)? 
					"": 
					" LEFT JOIN (select i.c_invoice_id, al.c_payment_id, i.paymentrule "
					+ "				from c_allocationline as al "
					+ "				inner join c_allocationhdr as ah on ah.c_allocationhdr_id = al.c_allocationhdr_id "
					+ "				inner join c_invoice as i on i.c_invoice_id = al.c_invoice_id "
					+ "				inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id "
					+ "				where ah.isactive = 'Y' and ah.docstatus in ('CO','CL')" +
					
									(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") + // Esto está bien?						
									(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
					") as i on i.c_payment_id = p.c_payment_id ") +
			"	  WHERE 1 = 1  " +
			"	  AND p.AD_Client_ID = " + getAD_Client_ID() + 
			"	  AND " +getDocStatusWhereClause("p")+
			(condition.equals(X_T_EstadoDeCuenta.CONDITION_All)?
					"":
					" AND ("+ (condition.equals(X_T_EstadoDeCuenta.CONDITION_Cash)? 
							"" : 
							" i.c_invoice_id is null OR ") 
					+ " i.paymentrule = '"+condition+"') ") +
			(isShowOpenBalance() ?
			"     AND to_days(now()::timestamp without time zone) - to_days(p.datetrx::timestamp without time zone) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND paymentavailable(p.c_payment_id, "+getDateToInlineQuery()+") <> 0 " : "" ) +
			// } isShowOpenBalance 
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND p.dateacct::date >= '" + dateTrxFrom + "'::date" : "" ) +
				(dateTrxTo != null ? " AND p.dateacct::date <= '" + dateTrxTo + "'::date" : "" )
			:"") +
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +
			(orgID!=0?" AND p.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND p.c_bpartner_id = " + bPartnerID:"") +
			(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
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
			
			"	  SELECT distinct d.signo_issotrx as signo_issotrx, '"+libroDeCaja+"' as tipodoc, d.ad_org_id, d.ad_client_id, d.documentno, d.document_id as doc_id, null::integer as c_order_id, d.c_bpartner_id, d.name as bpartner, '' as issotrx, d.dateacct::date as dateacct, d.datetrx::date as datedoc, null::integer AS netdays, null::date as duedate, to_days(now()::timestamp without time zone) - to_days(d.datetrx::timestamp without time zone) AS daysdue, null::date as discountdate, null::numeric as discountamt, " +
			"     ABS(d.amount) AS grandtotalmulticurrency, " +
			"     (abs(d.amount) - abs(cashlineopen)) AS paidamtmulticurrency, " +
			"     cashlineopen AS openamtmulticurrency, " +
			"     currencyConvert(d.amount, d.c_currency_id, "+currencyClient+", d.dateacct, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS grandtotal, " +
			"     currencyConvert((abs(d.amount) - abs(cashlineopen)), d.c_currency_id, "+currencyClient+", d.dateacct, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS paidamt, " +
			"     currencyConvert(cashlineopen, d.c_currency_id, "+currencyClient+", d.dateacct, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ) AS openamt, d.c_currency_id, d.c_conversiontype_id, null::character(1) as ispayschedulevalid, null::integer as c_invoicepayschedule_id, null::integer as c_paymentterm_id, bp.c_bp_group_id " +
			"	  FROM ( " +  // Vista V_DOCUMENTS original  omitida. 
//			 INLINE DE V_DOCUMENTS, omitiendo partes de la misma que no son necesarias en este caso.			
			"       SELECT 'C_CashLine'::text AS documenttable, cl.c_cashline_id AS document_id, cl.ad_client_id, cl.ad_org_id, cl.isactive, cl.created, cl.createdby, cl.updated, cl.updatedby, " +  
			"        CASE  " +
			"           WHEN cl.c_bpartner_id IS NOT NULL THEN cl.c_bpartner_id " +
			"           WHEN ic.c_bpartner_id IS NOT NULL THEN ic.c_bpartner_id " +
			"            ELSE i.c_bpartner_id " +
			"        END AS c_bpartner_id, dt.c_doctype_id, " + 
			"        CASE " +
			"            WHEN cl.amount < 0.0 THEN 1 " +
			"            ELSE (-1) " +
			"        END AS signo_issotrx, dt.name AS doctypename, dt.printname AS doctypeprintname, '@line@'::text || cl.line::character varying::text AS documentno, " + 
			"        CASE " +
			"            WHEN cl.amount < 0.0 THEN 'N'::bpchar " +
			"            ELSE 'Y'::bpchar " +
			"        END AS issotrx, cl.docstatus, c.statementdate AS datetrx, c.dateacct, cl.c_currency_id, NULL::integer AS c_conversiontype_id, abs(cl.amount) AS amount, abs(cashlineavailable(cl.c_cashline_id, "+getDateToInlineQuery()+")) AS cashlineopen, " + 
			"        NULL::integer AS c_invoicepayschedule_id, NULL::timestamp without time zone AS duedate, c.statementdate AS truedatetrx, bp.name  " +
//			"        COALESCE(i.initialcurrentaccountamt, 0.00) AS initialcurrentaccountamt, COALESCE(bp.socreditstatus, bp2.socreditstatus) AS socreditstatus " +
			"   FROM c_cashline cl " +
			"	JOIN c_cash c ON cl.c_cash_id = c.c_cash_id " +
			"	JOIN ( SELECT d.ad_client_id, d.c_doctype_id, d.name, d.printname FROM c_doctype d WHERE d.doctypekey::text = 'CMC'::text AND d.ad_client_id = " + getAD_Client_ID() + ") dt ON cl.ad_client_id = dt.ad_client_id " +
			"	LEFT JOIN c_bpartner bp ON " + (bPartnerID!=0?" cl.c_bpartner_id = " + bPartnerID + " AND ":"") + " cl.c_bpartner_id = bp.c_bpartner_id " +
			"   LEFT JOIN (select i.c_invoice_id, al.c_cashline_id, i.paymentrule, i.c_bpartner_id, bp.c_bp_group_id " + 
			"				from c_allocationline as al " + 
			"				inner join c_allocationhdr as ah on ah.c_allocationhdr_id = al.c_allocationhdr_id " + 
			"				inner join c_invoice as i on i.c_invoice_id = al.c_invoice_id " +
			"				inner join c_bpartner as bp on bp.c_bpartner_id = i.c_bpartner_id " +
			"				where ah.isactive = 'Y' and ah.docstatus in ('CO','CL')" +

							(bPartnerID!=0?" AND i.c_bpartner_id = " + bPartnerID:"") + // Esto está bien?
							(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
			" 		) as i on i.c_cashline_id = cl.c_cashline_id " +
		    " LEFT JOIN c_invoice ic on ic.c_invoice_id = cl.c_invoice_id "+
		    " LEFT JOIN c_bpartner bic on bic.c_bpartner_id = ic.c_bpartner_id "+
			" 	WHERE 1 = 1 " +
			" AND " +getDocStatusWhereClause("cl")+
			(bPartnerID!=0? "   AND ((cl.c_bpartner_id is not null AND cl.c_bpartner_id = " + bPartnerID + ") OR (i.c_bpartner_ID is not null and i.c_bpartner_id = " + bPartnerID + ") OR (ic.c_bpartner_ID is not null and ic.c_bpartner_id = " + bPartnerID + ")) " : "") +
			(bpGroupID!=0? "   AND ((cl.c_bpartner_id is not null AND bp.c_bp_group_id = " + bpGroupID + ") OR (i.c_bp_group_id is not null and i.c_bp_group_id = " + bpGroupID + ") OR (bic.c_bp_group_id is not null and bic.c_bp_group_id = " + bpGroupID + ")) " : "") +
			(condition.equals(X_T_EstadoDeCuenta.CONDITION_All)?
					"":
					" AND ("+ (condition.equals(X_T_EstadoDeCuenta.CONDITION_Cash)? 
							"" : 
							" i.c_invoice_id is null OR ") 
					+ " i.paymentrule = '"+condition+"') ") +
			(isShowOpenBalance() ?
			"     AND to_days(now()::timestamp without time zone) - to_days(c.statementdate::timestamp without time zone) BETWEEN " + daysfrom + " AND " + daysto +
			"     AND cashlineavailable(cl.c_cashline_id, "+getDateToInlineQuery()+") <> 0 " : "" ) +
			// } isShowOpenBalance			
			"     AND bp." + getAccountTypeClause() + " = 'Y'" +			
//			"	LEFT JOIN c_bpartner bp2 ON i.c_bpartner_id = bp2.c_bpartner_id " +
			"	) as d " + 
//			FIN INLINE DE V_DOCUMENTS			
			"	  JOIN c_bpartner bp on d.c_bpartner_id = bp.c_bpartner_id " +  
			"	  WHERE docstatus IN ('CO','CL', 'WC') " +
			"	  AND d.AD_Client_ID = " + getAD_Client_ID() + 
			(isShowByDate() ?
				(dateTrxFrom != null ? " AND d.dateacct::timestamp with time zone >= '" + dateTrxFrom + "'" : "" ) +
				(dateTrxTo != null ? " AND d.dateacct::timestamp with time zone <= '" + dateTrxTo + "'" : "" )
			:"") +
			(orgID!=0?" AND d.ad_org_id = " + orgID:"") +
			(bPartnerID!=0?" AND d.c_bpartner_id = " + bPartnerID:"") +
			(bpGroupID!=0?" AND bp.c_bp_group_id = " + bpGroupID:"") +
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
			"	ORDER BY bpartner, dateacct  ");
		
		// En caso de haber indicado mostrar solo documentos con balance abierto, filtrar solo dichos documentos.
		if (isShowByDateOpenDocumentsOnly()) {
			query.insert(0, " SELECT * FROM ( ").append(" ) AS FINALFOO WHERE OPENAMT <> 0 ");
		}
		
		pstmt = DB.prepareStatement(query.toString(), get_TrxName(), true);
		//System.out.println(query.toString());
		ResultSet rs = pstmt.executeQuery();
		
//		saldosMultimoneda.put(currencyClient, BigDecimal.ZERO);
//		saldosGeneralMultimoneda.put(currencyClient, BigDecimal.ZERO);
		
		bPartner=-1;
		saldo = new BigDecimal(0);	
		BigDecimal realSign;
		while (rs.next()) {
			
			bPartner = rs.getInt("c_bpartner_id");
			realSign = getAccountTypeSign().multiply(new BigDecimal(rs.getInt("signo_issotrx")));
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(rs.getInt("ad_org_id"));
			ec.setsigno_issotrx(rs.getInt("signo_issotrx"));
			ec.settipodoc(rs.getString("tipodoc"));
			ec.setDocumentNo(rs.getString("documentno"));
			ec.setdoc_id(rs.getInt("doc_id"));
			ec.setC_Order_ID(rs.getInt("c_order_id"));
			ec.setC_BP_Group_ID(rs.getInt("c_bp_group_id"));
			ec.setC_BPartner_ID(rs.getInt("c_bpartner_id"));
			ec.setbpartner(rs.getString("bpartner"));
			ec.setIsSOTrx(rs.getString("issotrx").equalsIgnoreCase("Y"));
			ec.setDateAcct(rs.getTimestamp("dateacct"));
			ec.setNetDays(rs.getBigDecimal("netdays"));
			ec.setDaysDue(rs.getInt("daysdue"));
			ec.setDateDoc(rs.getTimestamp("datedoc"));
			ec.setDiscountDate(rs.getTimestamp("discountdate"));
			BigDecimal damt = MCurrency.currencyConvert(rs.getBigDecimal("discountamt"), rs.getInt("c_currency_id"),
					currencyClient, rs.getDate("datedoc"), Env.getAD_Org_ID(getCtx()), getCtx());
			ec.setDiscountAmt(damt != null?damt.multiply(realSign):BigDecimal.ZERO);
			ec.setGrandTotalMulticurrency(rs.getBigDecimal("grandtotalmulticurrency").multiply(realSign));
			ec.setPaidAmtMulticurrency(rs.getBigDecimal("paidamtmulticurrency").multiply(realSign));
			ec.setOpenAmtMulticurrency(rs.getBigDecimal("openamtmulticurrency").multiply(realSign));
			
			ec.setGrandTotal(rs.getBigDecimal("grandtotal").multiply(realSign));
			ec.setPaidAmt(rs.getBigDecimal("paidamt").multiply(realSign));
			ec.setOpenAmt(rs.getBigDecimal("openamt").multiply(realSign));
			
			BigDecimal rate = MConversionRate.getRate(rs.getInt("c_currency_id"), currencyClient, this.dateConvert, rs.getInt("c_conversiontype_id"), getAD_Client_ID(), 0);
			this.incrementarSaldosMultimoneda(ec, rs.getInt("c_currency_id"));
			
			if (rate == null) {
				String fromISO = MCurrency.getISO_Code(getCtx(), rs.getInt("c_currency_id"));
				String toISO = MCurrency.getISO_Code(getCtx(), currencyClient);
				log.severe("No Currency Conversion from " + fromISO	+ " to " + toISO);
				throw new Exception("@NoCurrencyConversion@ (" + fromISO + "->" + toISO + ")");
			}
			
			ec.setOpenAmt(rs.getBigDecimal("openamtmulticurrency").multiply(rate).multiply(realSign));
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
			ec.setCondition(condition);
			ec.setDirectInsert(true);
			ec.setFilterInternalEC(filterInternalEC);
			ec.save();
			
			subSaldo = ec.getOpenAmt();
			saldo = saldo.add(subSaldo);

		}
		if (bPartner>0) {
			insertTotalForBPartnerOld(bPartner, saldo);
			insertTotalForBPartnerChecks(bPartner);
				
			incrementarSaldosGeneralMultimoneda();
				
			saldogral=saldogral.add(saldo);
		}

	}
	
	private void incrementarSaldosMultimoneda(X_T_EstadoDeCuenta ec, Integer c_Currency_ID){
		if (!saldosMultimoneda.containsKey(c_Currency_ID)) {
			saldosMultimoneda.put(c_Currency_ID, BigDecimal.ZERO);
		}
		BigDecimal saldoMultimoneda = ec.getOpenAmtMulticurrency();					
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
			ec.setC_BP_Group_ID(bpGroupID);
			ec.setC_BPartner_ID(bPartner);
			ec.settipodoc("             TOTAL " + MCurrency.getISO_Code(getCtx(), e.getKey()) + ":");
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
				ec.setDateAcct(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
				ec.setDateAcct(dateTrxFrom);
			} 
			ec.setCondition(condition);
			ec.setFilterInternalEC(filterInternalEC);
			ec.setDirectInsert(true);
			ec.save();
		}

		// La fila totalizadora por EC, se visualiza sólo si el filtro Moneda está vacío.
		if (currencyID == 0) {
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BP_Group_ID(bpGroupID);
			ec.setC_BPartner_ID(bPartner);
			ec.settipodoc("             TOTAL:");
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
				ec.setDateAcct(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
				ec.setDateAcct(dateTrxFrom);
			} 
			ec.setCondition(condition);
			ec.setFilterInternalEC(filterInternalEC);
			ec.setDirectInsert(true);
			ec.save();
		}
	}
	
	private void insertTotalForBPartnerChecks(int bPartner)	{
		X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
		ec.setAD_PInstance_ID(getAD_PInstance_ID());
		ec.setAD_Org_ID(orgID);
		ec.setC_BP_Group_ID(bpGroupID);
		ec.setC_BPartner_ID(bPartner);
		ec.settipodoc("             TOTAL CHEQUES CARTERA:");
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
			ec.setDateAcct(dateTrxTo);
		} else if (dateTrxFrom != null) {
			ec.setDateDoc(dateTrxFrom);
			ec.setDateAcct(dateTrxFrom);
		} 
		ec.setCondition(condition);
		ec.setFilterInternalEC(filterInternalEC);
		ec.setDirectInsert(true);
		ec.save();
	}
	
	private String getAccountTypeClause()
	{
		return accountType.equalsIgnoreCase("C")?"isCustomer":"isVendor";
	}
	
	protected BigDecimal getAccountTypeSign(){
		return accountType.equalsIgnoreCase("C")?BigDecimal.ONE:BigDecimal.ONE.negate();
	}
	
	
	private void insertTotalGral(BigDecimal saldo) {
		Iterator it = saldosGeneralMultimoneda.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, BigDecimal> e = (Entry<Integer, BigDecimal>) it.next();
			
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BP_Group_ID(bpGroupID);
			ec.setC_BPartner_ID(0);
			ec.settipodoc("             TOTAL GENERAL " + MCurrency.getISO_Code(getCtx(), e.getKey()) + ":");
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
				ec.setDateAcct(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
				ec.setDateAcct(dateTrxFrom);
			} 
			ec.setCondition(condition);
			ec.setFilterInternalEC(filterInternalEC);
			ec.setDirectInsert(true);
			ec.save();
		}
		
		// La fila totalizadora para todas las EC, se visualiza sólo si el filtro Moneda está vacío.
		if (currencyID == 0) {
			X_T_EstadoDeCuenta ec = new X_T_EstadoDeCuenta(getCtx(), 0, get_TrxName());
			ec.setAD_PInstance_ID(getAD_PInstance_ID());
			ec.setAD_Org_ID(orgID);
			ec.setC_BP_Group_ID(bpGroupID);
			ec.setC_BPartner_ID(0);
			ec.settipodoc("             TOTAL GENERAL:");             
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
				ec.setDateAcct(dateTrxTo);
			} else if (dateTrxFrom != null) {
				ec.setDateDoc(dateTrxFrom);
				ec.setDateAcct(dateTrxFrom);
			} 
			ec.setCondition(condition);
			ec.setFilterInternalEC(filterInternalEC);
			ec.setDirectInsert(true);
			ec.save();
		}
	}
	
	protected boolean isShowOpenBalance() {
		return SHOW_DOCUMENTS_OPEN_BALANCE.equals(showDocuments);
	}
	
	protected boolean isShowByDate() {
		return SHOW_DOCUMENTS_BY_DATE.equals(showDocuments) || SHOW_DOCUMENTS_BY_DATE_ONLY_OPEN_DOCUMENTS.equals(showDocuments);
	}
	
	protected boolean isShowByDateOpenDocumentsOnly() {
		return SHOW_DOCUMENTS_BY_DATE_ONLY_OPEN_DOCUMENTS.equals(showDocuments);
	}
	
	@Override
	public boolean isCancelable() {
		return true;
	}
	
	@Override
	public void cancelProcess() {
		DB.cancelStatement(pstmt);
	}
	
	/**
	 * @return cláusula where para el estado de los documentos
	 */
	protected String getDocStatusWhereClause(String tableAlias){
		return tableAlias+".docstatus IN ('CO','CL','WC') ";
	}
	
	/**
	 * @return cláusula where para el estado de los documentos
	 */
	protected String getInvoicesStatusWhereClause(String tableAlias){
		return "(("+tableAlias+".issotrx = 'Y' AND "+tableAlias+".docstatus IN ('CO', 'CL', 'WC', 'VO')) OR ("+tableAlias+".issotrx = 'N' AND "+tableAlias+".docstatus IN ('CO', 'CL', 'WC')))";
	}
	
	/**
	 * @return fecha de corte sobre la fecha de fin del informe para las
	 *         funciones de la consulta principal
	 */
	public String getDateToInlineQuery(){
		return " ('"+ ((dateTrxTo != null) ? dateTrxTo + "'" : "now'::text") + ")::timestamp(6) without time zone ";
	}
	
	/**
	 * Filtro de entidades comerciales que sean de uso interno, es decir, para
	 * retenciones y entidades financieras
	 * 
	 * @return condición SQL para dichas EC
	 */
	protected String getFilterInternalEC() {
		String sql = "";
		if(filterInternalEC) {
			sql = " AND c_bpartner_id NOT IN (select distinct c_bpartner_id from m_entidadfinanciera where ad_client_id = "+Env.getAD_Client_ID(getCtx())+") ";
			sql += " AND c_bpartner_id NOT IN (select distinct c_bpartner_recaudador_id from c_retencionschema where ad_client_id = "+Env.getAD_Client_ID(getCtx())+") ";
		}
		return sql;
	}
}
